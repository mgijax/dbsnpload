// $Header$
// $Name$

package org.jax.mgi.shr.dbutils.bcp;

import java.util.Vector;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jax.mgi.shr.config.BCPWriterCfg;
import org.jax.mgi.shr.config.ConfigException;
//import org.jax.mgi.shr.dbutils.bcp.*;
import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.shr.types.TypesException;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.dbutils.DataException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.Table;


/**
 *  An object that creates a bcp file and writes records to it.
 * @has <br>
 * <UL>
 * <LI> an instance of a Table object which represents the target table
 * for bcping and performs validation of data.
 * <LI> a bcp file
 * <LI> a SQLDataManager for passing to the Table object
 * <LI> a logger instance for logging informational and debug messages to
 * the log file
 * <LI> a set of configuration parameters
 * <LI> sql statements for executing before and after executing the bcp
 * command
 * </UL>
 * @does writes records to a bcp file for a given table. It
 * validates the fields in advance of writing them in order to catch
 * database errors in advance of executing the bcp command.
 * @company Jackson Laboratory
 * @author M Walker
 */

public class BCPWriter {

  /*
   * the parent BCPManager object
   */
  private BCPManager bcpmanager = null;
  /*
   * the SQLDataManager used for passing to the Table object
   */
  private SQLDataManager sqlmanager = null;
  /*
   * the Table object used for validating data
   */
  private Table table = null;
  /*
   * the bcp file
   */
  private File bcpFile = null;
  /*
   * the java file writer object used for writing to the bcp file
   */
  private FileWriter fileWriter = null;
  /*
   * a java buffered writer for buffering the writes
   */
  private BufferedWriter bufferedWriter = null;
  /*
   * a logger instance
   */
  private Logger logger = null;
  /*
   * option to auto-fill the user and date columns of the table
   */
  private boolean okToRecordStamp = false;
  /*
   * option to auto-flush the buffer after each bcp write
   */
  private boolean okToAutoFlush = false;
  /*
   * option to truncate table before executing bcp
   */
  private boolean okToTruncateTable = false;
  /*
   * option to drop indexes before executing bcp
   */
  private boolean okToDropIndexes = false;
  /*
   * option to drop triggers before executing bcp
   */
  private boolean okToDropTriggers = false;
  /*
   * option to replace newlines with spaces in text fields
   */
  private boolean okToRemoveNewlines = false;
  /*
   * a vector of sql statements to execute before running bcp
   */
  private Vector preSql = null;
  /*
   * a vector of sql statements to execute after running bcp
   */
  private Vector postSql = null;
  /*
   * an indicator of whether any records were ever writen to the bcp file
   */
  private boolean hasRecords = false;
  /*
   * an indicator controlled by the BCPManager of whetjer the bcp file
   * has been executed. Once executed the BCPWriter instance is made
   * invalid for further write calls
   */
  private boolean isValid = true;
  /*
   * the exception factory for storing and retrieving BCPExceptions
   */
  private BCPExceptionFactory eFactoryBCP = new BCPExceptionFactory();
  /*
   * the following are constants for exceptions thrown by this class
   */
  private static final String FileWriteErr =
      BCPExceptionFactory.FileWriteErr;
  private static final String TempFileCreateErr =
      BCPExceptionFactory.TempFileCreateErr;
  private static final String FileCloseErr =
      BCPExceptionFactory.FileCloseErr;
  private static final String FileCreateErr =
      BCPExceptionFactory.FileCreateErr;
  private static final String InvalidDelimiter =
      BCPExceptionFactory.InvalidDelimiter;
  private static final String StringConversionErr =
      BCPExceptionFactory.StringConversionErr;
  private static final String InvalidBCPWriter =
      BCPExceptionFactory.InvalidBCPWriter;
  private static final String InvalidBCPVector =
      BCPExceptionFactory.InvalidBCPVector;
  private static final String RecordStampErr =
      BCPExceptionFactory.RecordStampErr;

  /**
   * set the Logger
   * @assumes mothing
   * @effects the internal reference to a Logger will be set
   * @param pLogger the given Logger
   */
  public void setLogger(Logger pLogger) {
    logger = pLogger;
  }


  /**
   * set whether or not to truncate tables before executing the bcp command.
   * @assumes nothing
   * @effects the internal value of the okToTruncate flag will be set
   * @param boolValue true or false
   */
  public void setOkToTruncateTable(boolean boolValue) {
    okToTruncateTable = boolValue;
  }

  /**
   * set whether or not to drop indexes before executing the bcp command. If
   * this value is true, then indexes will be recreated afterwards.
   * @assumes nothing
   * @effects the internal value of the okToDropIndexes flag will be set
   * @param boolValue true or false
   */
  public void setOkToDropIndexes(boolean boolValue) {
    okToDropIndexes = boolValue;
  }

  /**
   * set whether or not to drop triggers before executing the bcp command. If
   * this value is true, then triggers will be recreated afterwards.
   * @assumes nothing
   * @effects the internal value of the okToDropTriggers flag will be set
   * @param boolValue true or false
   */
  public void setOkToDropTriggers(boolean boolValue) {
    okToDropIndexes = boolValue;
  }


  /**
   * set the option which designates whether to automatically
   * add the record stamping fields, overriding the value found
   * in the configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the okToRecordStamp flag will be set.
   * @param boolParam true or false
   */
  public void setOkToRecordStamp(boolean boolParam) {
    okToRecordStamp = boolParam;
  }

  /**
   * set the option which designates whether to automatically
   * flush the buffer after each bcp write, overriding the value found
   * in the configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the okToAutoFlush flag will be set.
   * @param boolParam true or false
   */
  public void setOkToAutoFlush(boolean boolParam) {
    okToAutoFlush = boolParam;
  }

  /**
   * set the option which designates whether to automatically
   * replace newlines with spaces in text fields, overriding the value found
   * in the configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the okToRemoveNewlines flag will be set.
   * @param boolParam true or false
   */
  public void setOkToRemoveNewlines(boolean boolParam) {
    okToRemoveNewlines = boolParam;
  }



  /**
   * provide a sql statement to execute before executing bcp
   * @assumes nothing
   * @effects the internal value of the preSQL field will be set
   * @param sql the sql statement
   */
  public void setPreSql(String sql) {
    Vector v = new Vector();
    v.add(sql);
    preSql = v;
  }

  /**
   * provide a Vector of sql statements to execute before executing bcp
   * @assumes nothing
   * @effects the internal value of the preSQL field will be set
   * @param sqlVector the Vector of sql statements
   */
  public void setPreSql(Vector sqlVector) {
    preSql = sqlVector;
  }

  /**
   * provide a sql statement to execute after executing bcp
   * @assumes nothing
   * @effects the internal value of the postSQL field will be set
   * @param sql the sql statement
   */
  public void setPostSql(String sql) {
    Vector v = new Vector();
    v.add(sql);
    postSql = v;
  }

  /**
   * provide a Vector of sql statements to execute after executing bcp
   * @assumes nothing
   * @effects the internal value of the postSQL field will be set
   * @param sqlVector the Vector of sql statements
   */
  public void setPostSql(Vector sqlVector) {
    postSql = sqlVector;
  }

  /**
   * get the name of the bcp file
   * @assumes nothing
   * @effects nothing
   * @return name of bcp file
   */
  public String getFilename() {
    return bcpFile.getPath();
  }


  /**
   * get the name of the target table name
   * @assumes nothing
   * @effects nothing
   * @return name of target table name
   */
  public String getTablename() {
    return table.getName();
  }

  /**
   * get the name of the target table as a Table object
   * @assumes nothing
   * @effects nothing
   * @return name of target table object
   */
  public Table getTable() {
    return table;
  }


  /**
   * get the sql statements defined for executing previous to running bcp
   * @assumes nothing
   * @effects nothing
   * @return a Vector of sql strings
   */
  public Vector getPreSql() {
    return preSql;
  }

  /**
   * get the sql statements defined for executing after running bcp
   * @assumes nothing
   * @effects nothing
   * @return a Vector of sql strings
   */
  public Vector getPostSql() {
    return postSql;
  }


  /**
   * get the value of the attribute which designates whether to automatically
   * drop indexes before running bcp.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToDropIndexes() {
    return okToDropIndexes;
  }

  /**
   * get the value of the attribute which designates whether to automatically
   * drop triggers before running bcp.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToDropTriggers() {
    return okToDropTriggers;
  }



  /**
   * get the value of the attribute which designates whether to automatically
   * truncate the table before doing a bcp.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToTruncateTable() {
    return okToTruncateTable;
  }

  /**
   * get the value of the attribute which designates whether to automatically
   * stamp the bcp write record with the user/time stamp.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToRecordStamp() {
    return okToRecordStamp;
  }

  /**
   * get the value of the attribute which designates whether to automatically
   * flush the bcp writes instead of buffering them.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToAutoFlush() {
    return okToAutoFlush;
  }

  /**
   * get the value of the attribute which designates whether to automatically
   * replace newline characters with spaces in text fields.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToRemoveNewlines() {
    return this.okToRemoveNewlines;
  }





  /**
   * write a line to the bcp file using the given Vector, the contents of
   * which correlate to the columns of the target database table. Each item
   * will get validated against the column definitions for the given target
   * table.
   * @assumes nothing
   * @effects nothing
   * @param fields the vector of objects to be written to the bcp file
   * @throws BCPException thrown if the BCPWriter has already been executed
   * or if the vector was determined to be invalid
   * @throws DBException thrown if there is a database execption
   * @throws DataException thrown if there is a data validation exception
   */

  public void write(Vector fields)
      throws BCPException, DataException, DBException {
    if (fields == null)
      return;
    if (!isValid) {
      throw (BCPException) eFactoryBCP.getException(InvalidBCPWriter);
    }
    /*
     * This method allows a vector of vectors to be passed in which
     * indicates that more than one line is to be written to the bcp file.
     * There cannot be a mixture of vectors and scalars though. A check
     * is necessary here to see if a vector of vectors is being passed in
     * and if so, that no scalars are mixed in.
     */
    if ((fields.size() > 0) && fields.get(0) instanceof Vector) {
      // first element is a vector.
      // make sure no scalars are present
      // and recursively call write for each vector
      for (Iterator it = fields.iterator(); it.hasNext(); ) {
        Object current = it.next();
        if (!(current instanceof Vector)) {
          BCPException e = (BCPException)
              eFactoryBCP.getException(InvalidBCPVector);
          throw e;
        }
        table.validateFields((Vector)current, okToRecordStamp);
        writeToBcpFile((Vector)current);
      }
    }
    else { // the first element was a scalar. assure no vectors exist
      for (Iterator it = fields.iterator(); it.hasNext(); ) {
        Object current = it.next();
        if (current instanceof Vector) {
          BCPException e = (BCPException)
              eFactoryBCP.getException(InvalidBCPVector);
          throw e;
        }
      }
      // validation will throw a DataException on a validation error
      if (fields.size() > 0) {
        table.validateFields(fields, okToRecordStamp);
        writeToBcpFile(fields);
      }
    }
  }

  /**
   * This method will extract data from the given object and format and write
   * the data as a line to a bcp file. Additionally, it will validate the
   * data against the column definitions for the target table in order to
   * ensure correctness prior to writing to the bcp file.
   * @assumes nothing
   * @effects nothing
   * @param translator this is an object that implements the BCPTranslatable
   * interface. This interface requires a translate method which this method
   * will call to extract the data in the form of a vector, the contents
   * of which correlate to the columns of the target database table.
   * @throws BCPException thrown if the BCPWriter has already been executed
   * or if the BCPTranslatable object was determined to be invalid
   * @throws DBException thrown if there is a database execption
   * @throws DataException thrown if there is a data validation exception
   */

  public void write(BCPTranslatable translator)
      throws BCPException, DataException, DBException {
    if (!isValid) {
      throw (BCPException) eFactoryBCP.getException(InvalidBCPWriter);
    }
    Vector fields = translator.getBCPVector(table);
    write(fields);
  }

  /**
   * the constructor which accepts the Table and BCPManager objects
   * @assumes nothing
   * @effects nothing
   * @param pTable the Table object representing the target database
   * table.
   * @param pBcpmanager the BCPManager object which performs the bcp
   * @param pLogger the DataLoadLogger instance
   * @param pCfg the configuration through which to configure the BCPWriter
   * @throws BCPException thrown if the bcp file cannot be opened
   * @throws DBException if database meta data cannot be obtained for
   * the table
   * @throws ConfigException thrown if there is an error when trying to
   * configure the BCPWriter
   */
  protected BCPWriter(Table pTable, BCPManager pBcpmanager,
                      Logger pLogger, BCPWriterCfg pCfg)
      throws BCPException, DBException, ConfigException {
    table = pTable;
    logger = pLogger;
    bcpmanager = pBcpmanager;
    sqlmanager = table.getSQLDataManager();
    okToDropIndexes = pCfg.getOkToDropIndexes().booleanValue();
    okToTruncateTable = pCfg.getOkToTruncateTable().booleanValue();
    //if (okToTruncateTable)
    //{
        // reset table class to start key at 1
        //table.resetKey();
    //}
    okToRecordStamp = pCfg.getOkToRecordStamp().booleanValue();
    okToAutoFlush = pCfg.getOkToAutoFlush().booleanValue();
    okToRemoveNewlines = pCfg.getOkToRemoveNewlines().booleanValue();
    preSql = pCfg.getPreSQL();
    postSql = pCfg.getPostSQL();
    createBCPFile(table.getName());
  }

  /**
   * used by the BCPManager to invalidate the BCPWriter after executing
   * @assumes nothing
   * @effects the internal value of the isValid field will be set
   * @param pIsValid false if the BCPWriter should no longer be used after
   * the BCPManager runs executeBCP
   */
  protected void setIsValid(boolean pIsValid) {
    isValid = pIsValid;
  }

  /**
   * close the file writers
   * @assumes nothing
   * @effects the bcp file will be closed
   * @throws BCPException throws if error occurs on close
   */
  protected void close() throws BCPException {
    try {
      bufferedWriter.close();
      fileWriter.close();
    }
    catch (IOException e) {
      BCPExceptionFactory eFactory = new BCPExceptionFactory();
      BCPException e2 = (BCPException)
          eFactory.getException(FileCloseErr, e);
      e2.bind(bcpFile.getName());
      throw e2;
    }
  }

  /**
   * return whether any records have been written to the bcp file
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  protected boolean hasRecords() {
    return hasRecords;
  }

  /**
   * Writes a line to a bcp file.
   * @assumes nothing
   * @effects a bcp line will be written to the database
   * @param v the vector of strings which will be written to the bcp file as
   * a representation of one row of data
   * @throws BCPException throws if file write error
   */
  private void writeToBcpFile(Vector v)
      throws BCPException {
    // convert the delimiter to a string suitable for writing to the
    // bcp file. This will convert special strings such as TAB and
    // SPACE to their corresponding delimiters
    String delimiter = convertDelimiter(bcpmanager.getDelimiter());
    StringBuffer bcpLine = null;
    Object currentObject = null;
    String element = null;
    for (Iterator it = v.iterator(); it.hasNext(); ) {
      currentObject = it.next();
      if (currentObject == null)
        element = "";
      else
      {
          if (this.okToRemoveNewlines && currentObject instanceof String)
          {
              currentObject = ((String)currentObject).replaceAll("\\n", " ");
          }
          try
          {
              element = Converter.objectToString(currentObject);
          }
          catch (TypesException e)
          {
              BCPExceptionFactory eFactory = new BCPExceptionFactory();
              BCPException e2 = (BCPException)
                  eFactory.getException(StringConversionErr, e);
              throw e2;
          }
      }
      if (bcpLine == null) { // it has not yet been written to
        bcpLine = new StringBuffer();
        bcpLine.append(element);
      }
      else { // bcp has already been written to, so just append to it
        bcpLine.append(delimiter + element);
      }
    }
    // check if the bcp file had ever been written to
    if (bcpLine == null) {
      bcpLine = new StringBuffer(); // create an empty line
    }
    else {
      if (okToRecordStamp) { // add user and time fields
        // use the RecordStampFormatter object
        try {
          bcpLine =
              bcpLine.append(table.getRecordStamp().getStamp(delimiter));
        }
        catch (DBException e) {
          BCPExceptionFactory eFactory = new BCPExceptionFactory();
          BCPException e2 = (BCPException)
              eFactory.getException(RecordStampErr, e);
          e2.bind(table.getName());
          throw e2;
        }
      }
    }
    bcpLine.append('\n');
    // write the line to the bcp file
    try {
      bufferedWriter.write(new String(bcpLine));
      if (okToAutoFlush)
        bufferedWriter.flush();
      hasRecords = true;
    }
    catch (IOException e) {
      BCPExceptionFactory eFactory = new BCPExceptionFactory();
      BCPException e2 = (BCPException)
          eFactory.getException(FileWriteErr, e);
      e2.bind(bcpFile.getName());
      throw e2;
    }
  }

  /**
   * create the bcp file
   * @assumes nothing
   * @effects a new bcp file will be created
   * @param table the table name to which the bcp file will write to
   * @throws BCPException if there are any io errors
   */
  private void createBCPFile(String table) throws BCPException {
    String path = bcpmanager.getPathname();
    int count = 0;
    boolean useTemp = bcpmanager.getUseTempFile();
    if (useTemp == true) { // create a temp file
      try {
        String prefix = table;
        bcpFile = File.createTempFile(prefix, null);
        bcpFile.deleteOnExit();
      }
      catch (IOException e) {
      BCPExceptionFactory eFactory = new BCPExceptionFactory();
        BCPException e2 = (BCPException)
            eFactory.getException(TempFileCreateErr, e);
        throw e2;
      }
    }
    else { // use named file
      bcpFile = new File(path + File.separator + table + ".bcp");
      boolean okToOverwrite = bcpmanager.getOkToOverwrite();
      if (!okToOverwrite) {
        while (bcpFile.exists()) {
          count = count + 1;
          bcpFile = new File(path + File.separator + table + "_" +
                             String.valueOf(count) + ".bcp");
        }
      }

    }
    try {
      fileWriter = new FileWriter(bcpFile);
      bufferedWriter = new BufferedWriter(fileWriter);
    }
    catch (IOException e) {
      BCPExceptionFactory eFactory = new BCPExceptionFactory();
      BCPException e2 = (BCPException)
          eFactory.getException(FileCreateErr, e);
      e2.bind(bcpFile.getName());
      throw e2;
    }
  }


  /**
   * converts a string representing the delimiter character to a string
   * suitable for writing to a file
   * @assumes nothing
   * @effects nothing
   * @param s the string to convert
   * @return the converted value
   * @throws BCPException
   */
  private String convertDelimiter(String s) throws BCPException {
    if (s == null  || s.equals("")) {
      BCPExceptionFactory eFactory = new BCPExceptionFactory();
      BCPException e = (BCPException)
          eFactory.getException(InvalidDelimiter);
      e.bind("null");
      throw e;
    }
    if (s.toUpperCase().equals("SPACE") || s.equals(" "))
      return new String(" ");
    else if (s.toUpperCase().equals("TAB") || s.equals("\t"))
      return Character.toString('\t');
    else {
      return s;
    }
  }


}

// $Log$
// Revision 1.4  2004/08/04 14:21:48  mbw
// removed call to resetKeys on the Table class
//
// Revision 1.3  2004/07/26 16:58:30  mbw
// formatting only
//
// Revision 1.2  2004/07/21 21:08:13  mbw
// - removed constructor which accepts SQLDataManager
// - added dropping trigger functionality
// - added remove newlines functionality
//
// Revision 1.1  2003/12/30 16:50:49  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:49:21  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.2.8  2003/10/22 15:33:20  mbw
// modified to suit changes in the Converter class
//
// Revision 1.1.2.7  2003/10/02 16:13:51  mbw
// added new get methods for the flags okToRecordStamp and okToAutoFlush
//
// Revision 1.1.2.6  2003/10/02 15:26:15  mbw
// fixed impact of changed constructor for the Table class
//
// Revision 1.1.2.5  2003/10/01 18:33:39  dbm
// Add auto flush capability the bcp writers
//
// Revision 1.1.2.4  2003/09/30 21:28:38  mbw
// removed println
//
// Revision 1.1.2.3  2003/09/30 21:24:59  mbw
// *** empty log message ***
//
// Revision 1.1.2.2  2003/09/30 20:10:27  mbw
// added new exception for a failure when doing auto record stamping
//
// Revision 1.1.2.1  2003/09/24 14:24:50  mbw
// moved to new package
//
// Revision 1.1.2.27  2003/09/18 19:24:40  mbw
// now has two constructors: one which takes a Table object and another which takes the table name
//
// Revision 1.1.2.26  2003/09/10 21:25:00  mbw
// now getting its SQLDataManager from the Table class
//
// Revision 1.1.2.25  2003/09/08 15:23:38  mbw
// initial version
//
// Revision 1.1.2.24  2003/06/17 20:20:20  mbw
// cleaned out unused imports
//
// Revision 1.1.2.23  2003/06/04 18:29:53  mbw
// javadoc edits
//
// Revision 1.1.2.22  2003/05/22 15:56:18  mbw
// javadocs edits
//
// Revision 1.1.2.21  2003/05/16 15:11:20  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.1.2.20  2003/05/12 19:12:51  mbw
// removed empty statement
//
// Revision 1.1.2.19  2003/05/12 18:40:59  mbw
// fixed bug in handling the writing of a vector of vectors
//
// Revision 1.1.2.18  2003/05/12 17:45:19  mbw
// allows now a vector of vector to the write method in order to support writng multiple bcp lines per call
//
// Revision 1.1.2.17  2003/05/08 19:40:54  mbw
// changed to suit addition of new class BCPWriterCfg
//
// Revision 1.1.2.16  2003/05/08 01:56:10  mbw
// incorporated changes from code review
//
// Revision 1.1.2.15  2003/04/25 17:11:58  mbw
// updated to reflect changes in design of exception handling
//
// Revision 1.1.2.14  2003/04/15 17:58:25  mbw
// added getTable method
//
// Revision 1.1.2.13  2003/04/15 12:06:16  mbw
// changed the way the SQLDataManager is getting set and did javadoc edits
//
// Revision 1.1.2.12  2003/04/11 15:32:56  mbw
// now setting the SQLDataManager class for the Table class after instantiating
//
// Revision 1.1.2.11  2003/04/11 15:27:14  mbw
// added code to execute pre sql and post sql when running bcp
//
// Revision 1.1.2.10  2003/04/10 17:23:25  mbw
// javadoc edit
//
// Revision 1.1.2.9  2003/04/09 21:24:18  mbw
// moved executeBCP code to BCPManager; added new attributes and setter methods
//
// Revision 1.1.2.8  2003/04/04 00:14:37  mbw
// moved bcp implementation from BCPManager to this class (see revision revision 1.1.2.7 for actual work performed)
//
// Revision 1.1.2.7  2003/04/04 00:09:04  mbw
// removed extraneous EOF character
//
// Revision 1.1.2.6  2003/03/21 16:52:52  mbw
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
