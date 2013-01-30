// $Header$
// $Name$

package org.jax.mgi.shr.dbutils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.jax.mgi.shr.dbutils.bcp.RecordStampFactory;
import org.jax.mgi.shr.dbutils.types.TypeValidator;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;
import org.jax.mgi.shr.dbutils.bcp.RecordStamper;
import org.jax.mgi.shr.config.ConfigException;


/**
 * An object that represents a table from the database.
 * @has A table name and the table meta data. A SQLDataManager
 * for obtaining table meta data.
 * @does get the next incremental key value for the table and
 * validates a vector of data objects against the table meta data
 * in order to support record validation prior to running bcp. In validation
 * the following java types are supported: double, integer, String, boolean,
 * and DateTime. The following Sybase types are supported:
 * char, varchar, int, double, datetime, bit, text
 * @company Jackson Laboratory
 * @author M Walker
 */

public class Table {

  /*
   * the table name
   */
  private String tableName = null;
  /*
   * the vector of ColumnDefinition objects
   */
  private Vector columnDefinitions = new Vector();
  /*
   * the primary jeys for this table
   */
  private Vector pKeyDefinitions = new Vector();
  /*
   * the name of the single incremental key ... not all tables have one
   */
  private String incrementalKeyName = null;
  /**
   * the RecordFormat class to use for user/time stamping database records
   */
  private RecordStamper recordStamp = null;
  /*
   * the next key value for single valued incremental primary keys
   */
  private int cacheKey = 0;
  /*
   * an indicator of whether the primary key for this table is
   * single valued and incremental. That is can it be used in the
   * nextKey() method call.
   */
  private boolean isIncremental = true;
  /*
   * the SQLDataManager for obtaining the table meta data
   */
  private SQLDataManager dataManager = null;

  private static HashMap tablePool = new HashMap();
  /**
   * indicator for whether or not the metadata has been obtained
   */
  private boolean metadataRead = false;
  /*
   * the execption factory for storing and retrieving DataExceptions
   */
  private DataExceptionFactory dataExceptionFactory =
      new DataExceptionFactory();
  /*
   * the execption factory for storing and retrieving DBExceptions
   */
  private DBExceptionFactory dbExceptionFactory =
      new DBExceptionFactory();

  /**
   * exceptions thrown by this class
   */
  private static final String FieldCountErr =
      DataExceptionFactory.FieldCountErr;
  private static final String JDBCException =
      DBExceptionFactory.JDBCException;
  private static final String NoTableDefinitions =
      DBExceptionFactory.NoTableDefinitions;
  private static final String ValidationErr =
      DBExceptionFactory.ValidationErr;
  private static final String UnexpectedCondition =
      DBExceptionFactory.UnexpectedCondition;
  private static final String UnexpectedKeyCount =
      DBExceptionFactory.UnexpectedKeyCount;
  private static final String InterpreterInstanceErr =
      DBExceptionFactory.InterpreterInstanceErr;

  /**
 * get a Table object from the instance pool for the given table name
 * @assumes nothing
 * @effects a new Table instance will be created and placed in the pool
 * if one does not already exist.
 * @param tableName the name of the table
 * @return the Table instance
 * @throws DBException thrown if there is an error accessing the database
 * @throws ConfigException thrown if there is an error accessing the
 * configuration
 */
public static Table getInstance(String tableName)
throws DBException, ConfigException
{
    SQLDataManager sqlMgr = new SQLDataManager();
  String hashEntryName = sqlMgr.getDatabase() + tableName;
  Table table = (Table) tablePool.get(hashEntryName);
  if (table == null) {
    table = new Table(tableName, sqlMgr);
    tablePool.put(hashEntryName, table);
  }
  return table;
}


  /**
   * get a Table object from the instance pool for the given table name
   * @assumes nothing
   * @effects a new Table instance will be created and placed in the pool
   * if one does not already exist.
   * @param tableName the name of the table
   * @param sqlMgr the SQLDataManager
   * @return the Table instance
   * @throws DBException thrown if there is an error accessing the database
   */
  public static Table getInstance(String tableName, SQLDataManager sqlMgr)
  throws DBException
    {
    String hashEntryName = sqlMgr.getDatabase() + tableName;
    Table table = (Table) tablePool.get(hashEntryName);
    if (table == null) {
      table = new Table(tableName, sqlMgr);
      tablePool.put(hashEntryName, table);
    }
    else
    {
        // make sure connection is still open
        SQLDataManager mgr = table.getSQLDataManager();
        mgr.reconnect();
    }
    return table;
  }

  /**
   * the constructor which accepts the name of the associated database table
   * @param pName the table name
   * @param pSQLDataManager the SQLDataManager which is required for
   * obtaining metadata
   * @throws DBException if there is an error getting the table meta data
   */
  private Table(String pName, SQLDataManager pSQLDataManager) {
    tableName = pName;
    dataManager = pSQLDataManager;
  }

  /**
   * get the database name associated with this object
   * @assumes nothing
   * @effects nothing
   * @return the database name
   */
  public String getName() {
    return tableName;
  }

  /**
   * get the SQLDataManager for this class
   * @return the SQLDataManager
   */
  public SQLDataManager getSQLDataManager() {
    return dataManager;
  }

  /**
   * get the RecordStamp class for creating user/time stamps by the BCPManager
   * @return the RecordStamp object for this table
   * @throws DBException thrown if there is an error with the database
   */
  public RecordStamper getRecordStamp() throws DBException {
    // calculate the RecordStamp
    if (recordStamp == null)
      recordStamp =
          RecordStampFactory.getRecordStamp(this);
    return recordStamp;
  }

  /**
   * return whether or not this class has a single part incremental key
   * @assumes nothing
   * @effects nothing
   * @return true if class has a single part incremental key, false otherwise
   * @throws org.jax.mgi.shr.dbutils.DBException
   */
  public boolean hasIncrementalKey() throws DBException
  {
        if (!metadataRead) this.getTableDefinitions();
        return this.isIncremental;
  }

  /**
   * search through the column definitions and see if there is a column with
   * the given name
   * @param name the given column name to search on
   * @return true if there is a column with that name, false otherwise
   * @throws DBException thrown if there is an error with the database
   */
  public boolean hasColumnName(String name) throws DBException {
    if (!metadataRead)
      getTableDefinitions();
    Iterator i = this.columnDefinitions.iterator();
    while (i.hasNext()) {
      ColumnDef c = (ColumnDef) i.next();
      if (c.getName().toUpperCase().equals(name.toUpperCase()))
        return true;
    }
    return false;
  }

  /**
   * set the RecordStamp class used for creating user/time stamps by the
   * BCPManager.
   * @param stamp the given RecordStamp class
   * @throws DBException thrown if there is an error with the database
   */
  public void setRecordStamp(RecordStamper stamp) throws DBException {
    recordStamp = stamp;
  }

  /**
   * validate a given vector containing objects corresponding to a row for
   * this table. Metadata is queried from the database and each object is
   * compared to the column definitions corresponding to the order of the
   * vector item.
   * @assumes nothing
   * @effects nothing
   * @param v the vector of objects for validating
   * @param autoStamp indicator that the expected user and time fields will
   * be applied automatically by the BCPWriter when writing the record
   * @throws DataException thown if expected number of columns do not
   * match expected number or error getting table metadata.
   * @throws DBException thrown if there is an error getting the meta data
   */
  public void validateFields(Vector v, boolean autoStamp)
      throws DataException, DBException {
    // if auto-filling the user and date fields then additional
    // fields will be added after validation. But the field count
    // will have to be compensated for when validating.
    if (!metadataRead)
      getTableDefinitions();
    getRecordStamp();
    int additionalFields = 0;
    if (autoStamp)
      additionalFields = this.recordStamp.getStampFieldCount();
    if (columnDefinitions.size() != v.size() + additionalFields) {
      DataException e = (DataException)
          dataExceptionFactory.getException(FieldCountErr);
      e.bind(v.size() + additionalFields);
      e.bind(columnDefinitions.size());
      e.bind(tableName);
      //throw e;
    }
    for (int i = 0; i < v.size(); i++) {
      Object o = v.get(i);
      ColumnDef c = (ColumnDef) columnDefinitions.get(i);
      TypeValidator validator = c.getTypeValidator();
      try {
        validator.validate(o);
      }
      catch (DataException e) {
        DBException e2 =
            (DBException) dbExceptionFactory.getException(ValidationErr, e);
        e2.bind(tableName);
        throw e2;
      }
    }
  }


  private static ArrayList<String> columnTranslations = new ArrayList<String>();
  static
  {
	  /*
		*	These column translations are needed for postgres, because it natively stores
		*	all column names in lowercase. 
	   */
	  columnTranslations.add("isMultiCoord");
	  columnTranslations.add("entrezGeneId");
	  columnTranslations.add("mutantCellLine");
	  columnTranslations.add("numberOfOrganisms");
	  columnTranslations.add("toSeqId");
	  columnTranslations.add("newRawName");
	  columnTranslations.add("newResolvedName");
	  columnTranslations.add("oldResolvedName");
	  columnTranslations.add("oldRawName");
	  columnTranslations.add("isCuratorEdited");
	  columnTranslations.add("goodHitCount");
	  columnTranslations.add("refAssocType");
	  columnTranslations.add("accID");
	  columnTranslations.add("libName");
	  columnTranslations.add("attrValue");
	  columnTranslations.add("mapType");
	  columnTranslations.add("toSeqId");
	  columnTranslations.add("sequenceProvider");
	  columnTranslations.add("columnName");
	  columnTranslations.add("newSource");
	  columnTranslations.add("newRawName");
	  columnTranslations.add("oldSource");
	  columnTranslations.add("foundMethod");
	  columnTranslations.add("logicalDB");
	  columnTranslations.add("MGIType");
	  columnTranslations.add("prefixPart");
	  columnTranslations.add("numericPart");
	  columnTranslations.add("modifiedBy");
	  columnTranslations.add("createdBy");
	  columnTranslations.add("privateVal");
	  columnTranslations.add("startCoordinate");
	  columnTranslations.add("endCoordinate");
	  columnTranslations.add("tagMethod");
	  columnTranslations.add("vectorEnd");
	  columnTranslations.add("reverseComp");
	  columnTranslations.add("pointCoordinate");
	  columnTranslations.add("segmentType");
	  columnTranslations.add("ageMin");
	  columnTranslations.add("ageMax");
	  columnTranslations.add("jobStream");
	  columnTranslations.add("isMutant");
	  columnTranslations.add("isWildType");
	  columnTranslations.add("isExtinct");
	  columnTranslations.add("isMixed");
	  columnTranslations.add("sequenceNum");
	  columnTranslations.add("rawOrganism");
	  columnTranslations.add("rawLibrary");
	  columnTranslations.add("incomingValue");
	  columnTranslations.add("attrName");
	  columnTranslations.add("fromSeqId");
	  columnTranslations.add("sequenceType");
	  columnTranslations.add("sequenceQuality");
	  columnTranslations.add("sequenceStatus");
	  columnTranslations.add("rawType");
	  columnTranslations.add("rawStrain");
	  columnTranslations.add("rawTissue");
	  columnTranslations.add("rawAge");
	  columnTranslations.add("rawSex");
	  columnTranslations.add("rawCellLine");
	  columnTranslations.add("cellLine");
	  columnTranslations.add("loadedBy");
	  columnTranslations.add("buildCreated");
	  columnTranslations.add("buildUpdated");
	  columnTranslations.add("alleleSummary");
	  columnTranslations.add("varClass");
	  columnTranslations.add("iupacCode");
	  columnTranslations.add("mgdStrain");
	  columnTranslations.add("consensusSnp");
	  columnTranslations.add("subSnp");
	  columnTranslations.add("isConflict");
	  columnTranslations.add("subHandle");
	  columnTranslations.add("isExemplar");
	  columnTranslations.add("is5Prime");
	  columnTranslations.add("startCoord");
	  columnTranslations.add("endCoord");
	  columnTranslations.add("refseqNucleotide");
	  columnTranslations.add("refseqProtein");
  }
  /**
   * get the ColumnDef objects for this table
   * @assumes nothing
   * @effects nothing
   * @return a vector of column definitions
   * @throws DBException thrown if there is an error with the database
   */
  public Vector getColumnDefinitions() throws DBException {
    if (!metadataRead)
      getTableDefinitions();
    Vector v = new Vector();
	ArrayList<String> uniqueColumns = new ArrayList<String>();
    for (int i = 0; i < columnDefinitions.size(); i++) {
	String colName = (String) ((ColumnDef) columnDefinitions.get(i)).getName();
	// perform any case-specific column name transformations for postgres
    	Iterator<String> iter = columnTranslations.iterator();
    	while(iter.hasNext())
    	{
    		String trans = iter.next();
    		if(colName.contains(trans.toLowerCase()))
    		{
    			colName = colName.replace(trans.toLowerCase(), trans);
			((ColumnDef) columnDefinitions.get(i)).setName(colName);
    		}
    	}
	// protect uniqueness of columns for postgres
	if(uniqueColumns.contains(colName)) continue;
	uniqueColumns.add(colName);

      v.add( ( (ColumnDef) columnDefinitions.get(i)).clone());
    }
    return v;
  }

  /**
   * get the ColumnDef objects for the primary keys of this table
   * @assumes nothing
   * @effects nothing
   * @return a vector of primary key column definitions
   * @throws DBException thrown if there is an error with the database
   */
  public Vector getPrimaryKeyDefinitions() throws DBException {
    if (!metadataRead)
      getTableDefinitions();
    Vector v = new Vector();
    for (int i = 0; i < pKeyDefinitions.size(); i++) {
      v.add( ( (ColumnDef) pKeyDefinitions.get(i)).clone());
    }
    return v;
  }

  public String getIncrementalKeyName() throws DBException
  {
      if (!this.metadataRead) getTableDefinitions();
      return this.incrementalKeyName;
  }

    /**
     * get the next key value for the table from cache. This value is cached
     * and may not reflect the actual max key value in the table. This
     * method cannot be used in a mutithreaded nonconcurrent application.
     * @assumes that this method is not running concurrently in more than one
     * thread
     * @effects the cached key value will be incremented
     * @return the cached maximum key value + 1
     * @throws DBException thrown if there is a two part key or if the key
     * is not an integer
     */
    public Integer getNextKey() throws DBException {
      if (!metadataRead) getTableDefinitions();
      if (!isIncremental) {
        // only a single valued incremantal key is valid
        DBException e = (DBException)
            dbExceptionFactory.getException(UnexpectedKeyCount);
        e.bind(tableName);
        e.bind(dataManager.getDatabase());
        throw e;
      }
      return new Integer(++cacheKey);
    }

    /**
     * set the primary key to start at 1
     * @assumes nothing
     * @effects the call to getNextKey() will return 1
     */
    public void resetKey()
    {
        this.cacheKey = 0;
    }




  /**
   * get the next key value for the table and cache it. This value is cached
   * and may not reflect the actual max key value in the table. That is if
   * you call this method multiple times before doing
   * @assumes nothing
   * @effects the cached key value will be set
   * @throws DBException thrown if there is a two part key or if the key
   * is not an integer
   */
  public void synchronizeKey() throws DBException {
    int nextKey = 0;
    // if there is a multivalued key then caching the next key value
    // is not appropriate.
    if (pKeyDefinitions.size() != 1) {
      isIncremental = false;
      return;
    }
    // get the name of the primary key stored in the primaryKeys variable
    ColumnDef c = (ColumnDef)pKeyDefinitions.get(0);
    String keyName = c.getName();
    if (c == null) {
      // this should not happen so throw an unexpected condition error
      DBException e = (DBException)
          dbExceptionFactory.getException(UnexpectedCondition);
      e.bind("get the next primary key value for table " + tableName);
      throw e;
    }
    // check to see that the primary key is an integer. Only integers
    // can be incremented so caching the next key is not appropriate
    if (c.getType() != DBTypeConstants.DB_INTEGER) {
      isIncremental = false;
      return;
    }
    // all tests have shown that it is a single value incremental key
    // get the current key value
    isIncremental = true;
    this.incrementalKeyName = keyName;
    String sql = "SELECT MAX(" + keyName + ") FROM " + tableName;
    ResultsNavigator it = dataManager.executeQuery(sql);
    if (it.next()) {
        RowReference row = (RowReference) it.getCurrent();
        Integer colData = row.getInt(1);
        if (colData != null)
            cacheKey = colData.intValue();
        if (cacheKey < 0)
            cacheKey = 0;
    }
  }

  /**
   * query the database to get the metadata associated with this table.
   * Note: Sybase does not return results with getPrimaryKeys() if the
   * the keys were created with sp_primarykey. Therefore, to get primary
   * key information, this code uses the sp_helpkey procedure.
   * @assumes nothing
   * @effects the internal column definitions will be set
   * @throws org.jax.mgi.shr.dbutils.DBException
   */
  private void getTableDefinitions() throws DBException {
    try {
      DatabaseMetaData dbmd = dataManager.getMetaData();
      // obtain the column definitions for the table
      ResultSet rs = dbmd.getColumns(null, null, tableName.toLowerCase(), "%");
      while (rs.next()) {
        ColumnDef c = new ColumnDef();
        c.setType(rs.getInt("DATA_TYPE"));
        c.setCatalog(rs.getString("TABLE_CAT"));
        c.setSchema(rs.getString("TABLE_SCHEM"));
        c.setTable(rs.getString("TABLE_NAME"));
        c.setName(rs.getString("COLUMN_NAME"));
        c.setTypeName(rs.getString("TYPE_NAME"));
        c.setSize(rs.getInt("COLUMN_SIZE"));
        c.setDecimalSize(rs.getInt("DECIMAL_DIGITS"));
        c.setNullable(rs.getString("IS_NULLABLE"));
        columnDefinitions.add(c);
      }
      // throw error no column definitions were obtained
      if (columnDefinitions.isEmpty()) {
        DBException e = (DBException)
            dbExceptionFactory.getException(NoTableDefinitions);
        e.bind(tableName);
        e.bind(dataManager.getDatabase());
        throw e;
      }
      /**
       * Sybase key declaration is non standard in that it uses a Sybase
       * stored procedure to define primary keys. A special case was required
       * to obtain primary key metadata from JDBC
       */
      if (dataManager.isSybase())
	{
        getSybasePrimaryKeys();
	}
      else
	{
          getPrimaryKeys(dbmd);
	}
      // cache the next key value if there is a single valued incremental
      // primary key for this table. This call will set the isIncremental
      // class variable to true or false and if true, then cache the next
      // key value.
      synchronizeKey();
      // indicate that metadata has been read
      metadataRead = true;
    }
    catch (SQLException e) {
      DBException e2 = (DBException)
          dbExceptionFactory.getException(JDBCException, e);
      e2.bind("access metadata for table " + tableName);
      throw e2;
    }
  }

  /**
   * query the database metadata for the given table and assign the results
   * to the instance variable pKeyDefinitions
   * @param metadata the database metadata
   * @throws SQLException throwsn if there is an error accessing the metadata
   */
  private void getPrimaryKeys(DatabaseMetaData metadata) throws SQLException
  {
      boolean foundKeys = false;
      ResultSet rs = metadata.getPrimaryKeys(null, null, tableName.toLowerCase());
      while (rs.next())
      {
          String key = rs.getString("COLUMN_NAME");
          ColumnDef col = getColumnByName(key);
          pKeyDefinitions.add(col);
      }

  }

  private void getSybasePrimaryKeys() throws DBException, SQLException
  {
      /*
       * get primary keys from the table. Since the keys were created
       * using sp_primarykey then we cannot use the getPrimaryKeys()
       * method on the database meta data object. Instead we need to
       * call sp_keyhelp
       */
      boolean foundKeys = false;
      ResultSet rs = null;
      Connection c = dataManager.getConnection();
      CallableStatement cs =
          c.prepareCall("sp_helpkey " + tableName,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
      try {
        // an exception is thrown on the call to executeQuery
        // if there are no keys defined for table
        rs = cs.executeQuery();
        foundKeys = true; // made it here implies keys were found
      }
      catch (SQLException e) {
        // no primary key defined for table
        foundKeys = false;
      }
      // store key column names into the primaryKeys vector
      while (foundKeys && rs.next()) {
        String keyType = rs.getString("keytype");
        if (keyType.equals("primary")) {
          String objectKeys = rs.getString("object_keys");
          // parse the string 'key1, key2, *, *, *, *, *'
          StringTokenizer tokens = new StringTokenizer(objectKeys, ",");
          while (tokens.hasMoreTokens()) {
            String key = tokens.nextToken().trim();
            if (!key.equals("*")) {
              ColumnDef col = getColumnByName(key);
              pKeyDefinitions.add(col);
            }
          }
        }
      }
  }


  /**
   * get the ColumnDefinition object for this table which has the given name
   * @assumes nothing
   * @effects nothing
   * @param name the name to match on
   * @return the ColumnDefinition or null if not found
   */
  private ColumnDef getColumnByName(String name) {
    for (Iterator it = columnDefinitions.iterator(); it.hasNext(); ) {
      ColumnDef c = (ColumnDef)it.next();
      if (c.getName().equals(name))
        return c;
    }
    // must not have been found
    return null;
  }
}

// $Log$
// Revision 1.8  2013/01/30 16:31:28  mgiadmin
//
// convert to postgres
//
// convert to postgres
//
// convert to postgres
//
// Revision 1.7  2005/08/05 16:05:06  mbw
// merged code from lib_java_core-tr6427-1
//
// Revision 1.6.2.2  2005/06/02 19:39:50  mbw
// javadocs only
//
// Revision 1.6.2.1  2005/06/02 14:40:47  mbw
// added method getIncrementalKeyName
//
// Revision 1.6  2004/12/16 21:18:53  mbw
// merged assembly branch onto the trunk
//
// Revision 1.5.2.1  2004/12/02 19:27:01  mbw
// changed use of floats to doubles
//
// Revision 1.5  2004/09/03 17:59:17  mbw
// checks the cached Table instances and assures that all the connections are still good and now sets the next key to 0 even if the max key is less than zero
//
// Revision 1.4  2004/07/26 16:44:43  mbw
// formatting only
//
// Revision 1.3  2004/07/21 20:24:31  mbw
// - fixed bug causing key value to start at 2
// - added methods resetKey() and synchronizeKey()
// - made some javadocs edits
//
// Revision 1.2  2004/03/29 19:54:28  mbw
// made more compatible with non-sybase dbs
//
// Revision 1.1  2003/12/30 16:50:41  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:49:12  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.2.40  2003/11/18 22:40:57  mbw
// fixed cosmetic spaces
//
// Revision 1.1.2.39  2003/10/24 16:07:06  mbw
// bug fix : was not putting ColumnDef objects into the primaryKeyDefinitions vector but instead was putting the names of the keys
//
// Revision 1.1.2.38  2003/10/23 17:22:08  mbw
// bug fix : obtaining ColumnDef by name vs doing a new() when creating the primaryKey vector content
//
// Revision 1.1.2.37  2003/10/23 16:47:05  mbw
// bug fix: uncommented out the code setting isIncremental to false
//
// Revision 1.1.2.36  2003/10/22 20:00:52  mbw
// bug fix: was parsing primaryKeyDefinitions vector as Strings instead of Columndef objects
//
// Revision 1.1.2.35  2003/10/22 15:36:03  mbw
// changed method from getPrimaryKeyNames to getPrimaryKeyDefinitions and now returns a vector of ColumnDef objects
//
// Revision 1.1.2.34  2003/10/21 14:45:13  mbw
// changed column definition structire from iterator to vector
//
// Revision 1.1.2.33  2003/10/17 13:39:18  mbw
// fixed bug : had to obtain the record stamp in the validatFields method
//
// Revision 1.1.2.32  2003/10/16 13:45:29  mbw
// no longer getting recordStamp when obtaining metadata
//
// Revision 1.1.2.31  2003/10/06 19:00:12  mbw
// added if logic to protect possible null reference exception
//
// Revision 1.1.2.30  2003/10/02 19:01:00  mbw
// coding format changes
//
// Revision 1.1.2.29  2003/10/02 15:17:53  mbw
// changed constructor to private and added a new static getInstance method and a new pool of instances
//
// Revision 1.1.2.28  2003/09/30 20:10:26  mbw
// added new exception for a failure when doing auto record stamping
//
// Revision 1.1.2.27  2003/09/30 19:33:28  mbw
// now storing the key value as the current instead of the next value
//
// Revision 1.1.2.26  2003/09/30 17:57:06  dbm
// Support conversion of int to Integer for all row reference values
//
// Revision 1.1.2.25  2003/09/29 20:34:52  mbw
// fixed bug to avoid recursion when calculating the RecordStamp
//
// Revision 1.1.2.24  2003/09/25 22:03:35  mbw
// removed line excluding windows2000 from using the sp_helpkey call
//
// Revision 1.1.2.23  2003/09/25 21:03:04  mbw
// fixed bug
//
// Revision 1.1.2.22  2003/09/24 14:12:19  mbw
// fixed import
//
// Revision 1.1.2.21  2003/09/23 19:24:31  mbw
// removed the inner ColumnDef class and TypeValidator class
//
// Revision 1.1.2.20  2003/09/18 19:27:30  mbw
// added new method to calculate the RecordStamp class to use
//
// Revision 1.1.2.19  2003/09/10 21:27:50  mbw
// now the Table class has a RecordStamp object for automatically applying user/time stamps
//
// Revision 1.1.2.18  2003/06/17 20:27:25  mbw
// cleaned up unused import statements
//
// Revision 1.1.2.17  2003/06/04 18:29:58  mbw
// javadoc edits
//
// Revision 1.1.2.16  2003/05/22 15:56:26  mbw
// javadocs edits
//
// Revision 1.1.2.15  2003/05/16 15:11:25  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.1.2.14  2003/05/14 00:17:40  mbw
// fixed nextKey to return 1 if no rows were found in table
//
// Revision 1.1.2.13  2003/05/12 19:13:30  mbw
// now caching the next key value for the table
//
// Revision 1.1.2.12  2003/05/09 14:25:02  mbw
// fixed getNextKey method to use sp_helpkey stored procedure
//
// Revision 1.1.2.11  2003/05/08 01:56:15  mbw
// incorporated changes from code review
//
// Revision 1.1.2.10  2003/04/25 17:12:03  mbw
// updated to reflect changes in design of exception handling
//
// Revision 1.1.2.9  2003/04/15 12:01:53  mbw
// added getNextKey method and edits to javadocs
//
// Revision 1.1.2.8  2003/04/04 00:09:06  mbw
// removed extraneous EOF character
//
// Revision 1.1.2.7  2003/03/21 16:53:04  mbw
// added standard header/footer
//
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
