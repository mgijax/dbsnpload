// $Header$

// $Name$



package org.jax.mgi.shr.dbutils.bcp;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import org.jax.mgi.shr.unix.RunCommand;
import org.jax.mgi.shr.config.BCPManagerCfg;
import org.jax.mgi.shr.config.BCPWriterCfg;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBSchema;
import org.jax.mgi.shr.dbutils.DBSchemaException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.Table;

/**
 * An object for executing the Sybase bcp command against tables in the
 * database.
 * @has <br>
 * <UL>
 * <LI>a set of configuration parameters and a BCPManagerCfg object
 * for reading these parameters from the configuration files and system
 * properties.
 * <LI>A SQLDatabaseManager for handling DDL database calls such as 'drop
 * triggers', 'drop indexes', etc.
 * <LI>A BCPWriter class for each table it is to execute against.
 * <LI>A logger for logging informational and debug messages
 * </UL>
 * @does obtains BCPWriters for creating bcp files and execute the bcp
 * command for each file.
 * @company Jackson Laboratory
 * @author M. Walker
 */
public class BCPManager {
  /*
   * the SQLDataManager used for executing DDL commands before and after bcp
   */
  //private SQLDataManager sqlmanager = null;
  /*
   * the logger instance
   */
  private Logger logger = null;
  /*
   * the vector of all BCPWriters. Each Writer is associated with one table
   */
  private Vector allWriters = new Vector();
  /*
   * the path where the bcp files are stored
   */
  private String path = null;
  /*
   * the delimiter of the bcp file\
   */
  private String delimiter = null;
  /*
   * option to not run the bcp file on execute
   */
  private boolean preventExecute;
  /*
   * option to overwrite existing bcp files
   */
  private boolean okToOverwrite;
  /*
   * option to use temp file when creating bcp file
   */
  private boolean useTempFile;
  /*
   * option to remove the bcp file after executing
   */
  private boolean removeAfterExecute;
  /*
   * option to auto-fill the user and date columns of a table
   */
  private boolean okToRecordStamp;
  /*
   * option to auto-flush the buffer after each bcp write
   */
  private boolean okToAutoFlush;
  /*
   * option to truncate logs after executing bcp
   */
  private boolean okToTruncateLog;
  /*
   * option to drop indexes before executing bcp
   */
  private boolean okToDropIndexes;
  /*
   * option to drop triggers before executing bcp
   */
  private boolean okToDropTriggers;
  /*
   * option to truncate table before executing bcp
   */
  private boolean okToTruncateTable;
  /**
   * class for handling database connections ... indicator of system
   */
  private String connectionClass = null;
  /*
   * bcp exception factory used to store and obtain bcp exceptions
   */
  private BCPExceptionFactory exceptionFactory = new BCPExceptionFactory();
  /*
   * the following constant definitions are exceptions thrown by this class
   */
  private static String InterruptErr =
      BCPExceptionFactory.InterruptErr;
  private static String IOErr =
      BCPExceptionFactory.IOErr;
  private static String NonZeroErr =
      BCPExceptionFactory.NonZeroErr;
  private static String InvalidDelimiter =
      BCPExceptionFactory.InvalidDelimiter;
  /**
   * default constructor which reads the configuration from a
   * default configuration object
   * @throws ConfigException thrown if a configuration error occurs
   * @throws DBException thrown if an error occurs while trying to
   * access the database
   */
  public BCPManager() throws ConfigException, DBException {
    configure(new BCPManagerCfg());
  }

  /**
   * constructer which uses a given configuration object for obtaining
   * configuration parameters normally used for selecting a certain
   * set of configuration variables that have been prefixed
   * @param config the configuration object from which to configure the
   * BCPManager by
   * @throws ConfigException thrown if there is an error obtaining
   * configuration parameters
   * @throws DBException thrown if there is an error accessing the
   * database
   */
  public BCPManager(BCPManagerCfg config)
      throws ConfigException, DBException {
    configure(config);
  }

  /**
   * get a BCPWriter object for a given table
   * @assumes nothing
   * @effects a bcp file will be opened for writing
   * @param table the table object
   * @return the BCPWriter for the given table
   * @throws BCPException thrown if the bcp file cannot be created
   * @throws DBException thrown if database metadata cannot be obtained for
   * the table
   * @throws ConfigException thrown if there is an error during
   * configuration
   */
  public BCPWriter getBCPWriter(Table table)
      throws BCPException, DBException, ConfigException {
    BCPWriterCfg bcpCfg = new BCPWriterCfg();
    bcpCfg.setDefaultOkToRecordStamp(okToRecordStamp);
    bcpCfg.setDefaultOkToAutoFlush(okToAutoFlush);
    //if (bcpCfg.getOkToTruncateTable().booleanValue())
        //table.resetKey(); // reset table class to start key at 1
    return getBCPWriter(table, bcpCfg);
  }

  /**
   * get a BCPWriter object for a given table name
   * @assumes nothing
   * @effects a bcp file will be opened for writing
   * @param tableName the name of the table
   * @param sqlMgr the SQLDataManager to use
   * @return the BCPWriter for the given table
   * @throws BCPException thrown if the bcp file cannot be created
   * @throws DBException thrown if database metadata cannot be obtained for
   * the table
   * @throws ConfigException thrown if there is an error during
   * configuration
   */
  public BCPWriter getBCPWriter(String tableName, SQLDataManager sqlMgr)
      throws BCPException, DBException, ConfigException
  {
    Table table = Table.getInstance(tableName, sqlMgr);
    return getBCPWriter(table);
  }


  /**
   * get a BCPWriter object for a given table and apply the given
   * configurationto the instance.
   * @assumes nothing
   * @effects a bcp file will be opened for writing
   * @param table the table object to bcp against.
   * @param cfg the configuration object through which to configure the
   * BCPWriter
   * @return a BCPWriter for the given table
   * @throws BCPException thrown if the bcp file cannot be created
   * @throws DBException if database meta data cannot be obtained for
   * the table
   * @throws ConfigException thrown if there is an error when trying to
   * configure the BCPWriter object
   *
   */
  public BCPWriter getBCPWriter(Table table, BCPWriterCfg cfg) throws
      BCPException, DBException, ConfigException {
    cfg.setDefaultOkToRecordStamp(okToRecordStamp);
    cfg.setDefaultOkToAutoFlush(okToAutoFlush);
    cfg.setDefaultOkToDropIndexes(this.okToDropIndexes);
    cfg.setDefaultOkToTruncateTable(this.okToTruncateTable);
    //if (cfg.getOkToTruncateTable().booleanValue())
        //table.resetKey(); // reset table class to start key at 1
    BCPWriter writer = new BCPWriter(table, this, logger, cfg);
    allWriters.add(writer);
    return writer;
  }

  /**
   * get a BCPWriter object for a given table and apply the given
   * configuration to the instance.
   * @assumes nothing
   * @effects a bcp file will be opened for writing
   * @param tableName the table object to bcp against.
   * @param sqlMgr the SQLDataManager to use
   * @param cfg the configuration object through which to configure the
   * BCPWriter
   * @return a BCPWriter for the given table
   * @throws BCPException thrown if the bcp file cannot be created
   * @throws DBException if database meta data cannot be obtained for
   * the table
   * @throws ConfigException thrown if there is an error when trying to
   * configure the BCPWriter object
   *
   */
  public BCPWriter getBCPWriter(String tableName,
                                SQLDataManager sqlMgr,
                                BCPWriterCfg cfg)
      throws BCPException, DBException, ConfigException
  {
      Table table = Table.getInstance(tableName, sqlMgr);
      return getBCPWriter(table, cfg);
  }


  /**
   * set the Logger instance
   * @assumes nothing
   * @effects the internal reference to the Logger will be set.
   * @param pLogger the Logger
   */
  public void setLogger(Logger pLogger) {
    logger = pLogger;
  }


  /**
   * set the name of the path where bcp files should be created, overriding
   * the value found in the configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the pathname will be set.
   * @param pathParam the path name
   */
  public void setPathname(String pathParam) {
    path = pathParam;
  }

  /**
   * set the delimiter used as a field seperator in bcp files, overriding the
   * value found in the configuration file or system properties.
   * @assumes nothing
   * @effects the interval value of the delimiter will be set.
   * @param delimiterParam bcp field delimiter
   */
  public void setDelimiter(String delimiterParam) {
    delimiter = delimiterParam;
  }

  /**
   * set the option to prevent executing the bcp command when called,
   * overriding the value found in the configuration file or system
   * properties.
   * @assumes nothing
   * @effects the internal value of the preventExecute flag will be set.
   * @param boolParam true or false
   */
  public void setPreventExecute(boolean boolParam) {
    preventExecute = boolParam;
  }

  /**
   * set the option which allows existing files to be overwritten by new
   * bcp files with the same name, overriding the value found in the
   * configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the okToOverwrite flag will be set.
   * @param boolParam true or false
   */
  public void setOkToOverwrite(boolean boolParam) {
    okToOverwrite = boolParam;
  }

  /**
   * set the option which designates whether to use temporary files when
   * creating bcp files, overriding the value found in the configuration file
   * or system properties. The location of the temporary directory is
   * determined by the java system property java.io.tempdir.
   * @assumes nothing
   * @effects the internal value of the useTempFile flag will be set.
   * @param boolParam true or false
   */
  public void setUseTempFile(boolean boolParam) {
    useTempFile = boolParam;
  }

  /**
   * set the option which designates that the bcp files will be removed
   * from the system after they are executed, overriding the value found
   * in the configuration file or system properties. This option will be
   * ignored if the
   * preventExecute option is set to true.
   * @assumes nothing
   * @effects the internal value of the removeAfterExecute flag will be
   * set.
   * @param boolParam true or false
   */
  public void setRemoveAfterExecute(boolean boolParam) {
    removeAfterExecute = boolParam;
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
   * flush a buffer after each bcp write, overriding the value found
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
   * truncate the log after doing a bcp, overriding the value found in the
   * configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the okToTruncateLog flag will be set.
   * @param boolParam true or false
   */
  public void setOkToTruncateLog(boolean boolParam) {
    okToTruncateLog = boolParam;
  }

  /**
   * set the option which designates whether to automatically
   * drop indexes before doing a bcp, overriding the value found in the
   * configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the okToDropIndexes flag will be set.
   * @param boolParam true or false
   */
  public void setOkToDropIndexes(boolean boolParam) {
    okToDropIndexes = boolParam;
  }

  /**
   * set the option which designates whether to automatically
   * drop triggers before doing a bcp, overriding the value found in the
   * configuration file or system properties.
   * @assumes nothing
   * @effects the internal value of the okToDropTriggers flag will be set.
   * @param boolParam true or false
   */
  public void setOkToDropTriggers(boolean boolParam) {
    okToDropTriggers = boolParam;
  }

  /**
   * get the field delimiter for the BCP file.
   * @assumes nothing
   * @effects nothing
   * @return the field delimiter.
   */
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * get the path name where bcp files are created.
   * @assumes nothing
   * @effects nothing
   * @return path name.
   */
  public String getPathname() {
    return path;
  }

  /**
   * get the value of the attribute which designates whether to use temporary
   * files when creating bcp files.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getUseTempFile() {
    return useTempFile;
  }

  /**
   * get the value of the attribute which designates whether it is ok to
   * overwrite an existing bcp file.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToOverwrite() {
    return okToOverwrite;
  }

  /**
   * get the value of the option which designates whether to prevent
   * executing the bcp command when called.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getPreventExecute() {
    return preventExecute;
  }

  /**
   * get the value of the option which designates whether to remove the bcp
   * files after executing.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getRemoveAfterExecute() {
    return removeAfterExecute;
  }

  /**
   * get the value of the option which designates whether to automatically
   * truncate the log after running bcp.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToTruncateLog() {
    return okToTruncateLog;
  }

  /**
   * get the value of the option which designates whether to automatically
   * drop indexes before running bcp.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToDropIndexes() {
    return okToDropIndexes;
  }

  /**
   * get the value of the option which designates whether to automatically
   * drop triggers before running bcp.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToDropTriggers() {
    return okToDropTriggers;
  }



  /**
   * get the value of the option which designates whether to automatically
   * add the fields createdBy, modifiedBy and the the fields
   * modification_date and creation_date when doing a bcp write.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToRecordStamp() {
    return okToRecordStamp;
  }

  /**
   * get the value of the option which designates whether to automatically
   * flush a buffer after each bcp write.
   * @assumes nothing
   * @effects nothing
   * @return true or false
   */
  public boolean getOkToAutoFlush() {
    return okToAutoFlush;
  }


  /**
   * get a BCPWriter object for a given table
   * @assumes nothing
   * @effects a bcp file will be opened for writing
   * @param pTable the table name
   * @return the BCPWriter for the given table
   * @throws BCPException thrown if the bcp file cannot be created
   * @throws DBException thrown if database metadata cannot be obtained for
   * the table
   * @throws ConfigException thrown if there is an error during
   * configuration
   */
  //public BCPWriter getBCPWriter(String pTable, SQLDataManager sqlMgr)
      //throws BCPException, DBException,
      //ConfigException {
    //BCPWriterCfg bcpCfg = new BCPWriterCfg();
    //bcpCfg.setDefaultOkToRecordStamp(okToRecordStamp);
    //bcpCfg.setDefaultOkToAutoFlush(okToAutoFlush);
    //return getBCPWriter(pTable, sqlMgr, bcpCfg);
  //}

  /**
   * get a BCPWriter object for a given table and apply the given
   * configuration to the instance.
   * @assumes nothing
   * @effects a bcp file will be opened for writing
   * @param pTable the table name to bcp against.
   * @param sqlMgr the SQLDataManager to use
   * @param pCfg the configuration object through which to configure the
   * BCPWriter
   * @return a BCPWriter for the given table
   * @throws BCPException thrown if the bcp file cannot be created
   * @throws DBException if database meta data cannot be obtained for
   * the table
   * @throws ConfigException thrown if there is an error when trying to
   * configure the BCPWriter object
   *
   */
  //public BCPWriter getBCPWriter(String pTable,
                                //SQLDataManager sqlMgr,
                                //BCPWriterCfg pCfg)
      //throws
      //BCPException, DBException, ConfigException {
    //pCfg.setDefaultOkToRecordStamp(this.okToRecordStamp);
    //pCfg.setDefaultOkToAutoFlush(this.okToAutoFlush);
    //pCfg.setDefaultOkToDropIndexes(this.okToDropIndexes);
    //pCfg.setDefaultOkToTruncateTable(this.okToTruncateTable);
    //BCPWriter writer = new BCPWriter(pTable, this, sqlMgr, logger, pCfg);
    //allWriters.add(writer);
    //return writer;
  //}

  /**
   * execute all the existing bcp files
   * @throws BCPException thrown if there is some error handling the bcp
   * file or executing the bcp command
   * @throws DBException thrown if there is an error with the database
   * @throws DBSchemaException thrown if there is an error obtainning DDL
   * commands, for example commands for dropping and creating indexes
   */
  public void executeBCP()
      throws BCPException, DBException, DBSchemaException {
    BCPWriter writer = null;
    for (Iterator it = allWriters.iterator(); it.hasNext(); ) {
      writer = (BCPWriter) it.next();
      writer.close();
    }
    for (Iterator it = allWriters.iterator(); it.hasNext(); ) {
      writer = (BCPWriter) it.next();
      executeBCP(writer);
      // invalidate the writer
      writer.setIsValid(false);
    }
    // clear all writers
    allWriters = new Vector();
  }

  /**
   * sets all the attributes from the configuration object
   * @assumes nothing
   * @effects all internal values will be set
   * @param pConfig the configuration object
   * @throws ConfigException throws if there is a configuration error
   */
  private void configure(BCPManagerCfg pConfig) throws ConfigException {
    this.path = pConfig.getPathname();
    this.delimiter = pConfig.getDelimiter();
    this.preventExecute = pConfig.getPreventExecute().booleanValue();
    this.okToOverwrite = pConfig.getOkToOverwrite().booleanValue();
    this.useTempFile = pConfig.getUseTempFile().booleanValue();
    this.removeAfterExecute = pConfig.getRemoveAfterExecute().booleanValue();
    this.okToRecordStamp = pConfig.getOkToRecordStamp().booleanValue();
    this.okToAutoFlush = pConfig.getOkToAutoFlush().booleanValue();
    this.okToTruncateLog = pConfig.getOkToTruncateLog().booleanValue();
    this.okToDropIndexes = pConfig.getOkToDropIndexes().booleanValue();
    this.okToTruncateTable = pConfig.getOkToTruncateTable().booleanValue();
    this.connectionClass = pConfig.getConnectionManagerClass();
  }

  /**
   * executes the associated bcp file for the given BCPWriter.
   * @assumes nothing
   * @effects data will be loaded into the database
   * @param bcpWriter the BCPWiter object
   * @throws BCPException throws if BCPWriters cannot be closed or if bcp
   * command fails or if an sql exception is thrown while running pre sql or
   * post sql.
   * @throws DBException thrown if there is a failure when trying to run
   * sql commands
   * @throws DBSchemaException thrown if there is an error running DDL
   * commands using the DBSchema product
   */
  private void executeBCP(BCPWriter bcpWriter) throws BCPException,
      DBException, DBSchemaException {
      // check attributes to see if the bcp command should not be performed
      if (preventExecute)
        return;
    SQLDataManager sqlmanager = bcpWriter.getTable().getSQLDataManager();
    DBSchema dbSchema = new DBSchema(sqlmanager);
    String file = bcpWriter.getFilename();
    String table = bcpWriter.getTablename();
    // execute any sql pre bcp as designated in the bcpWriter
    if (logger != null)
      logger.logInfo(table + ": Execute any configured pre-SQL statements");
    executeSql(bcpWriter.getPreSql(), sqlmanager);
    if (bcpWriter.getOkToTruncateTable()) {
      if (logger != null)
        logger.logInfo(table + ": Truncate table");
      dbSchema.truncateTable(table);
    }
    // Only drop indexes, run bcp and re-create indexes if the writer has
    // records to load.
    if (bcpWriter.hasRecords()) {
      // drop indexes on table if configured to do so
      if (bcpWriter.getOkToDropIndexes()) {
        if (logger != null)
          logger.logInfo(table + ": Drop indexes on table");
        dbSchema.dropIndexes(table);
      }
      if (bcpWriter.getOkToDropTriggers()) {
        if (logger != null)
          logger.logInfo(table + ": Drop triggers on table");
        dbSchema.dropTriggers(table);
      }
      try {
          FileImporter fileImporter = chooseFileImporter();
          fileImporter.importFile(file, table,
                                  convertDelimiter(delimiter),
                                  sqlmanager, logger);
      }
      // if execeptions are caught, try and recreate any indexes or triggers
      // that were dropped and throw the Exception
      catch (BCPException e) {
        // need to recreate indexes if they were dropped
        if (bcpWriter.getOkToDropIndexes()) {
          if (logger != null)
            logger.logInfo(table + ": Create indexes on table");
          dbSchema.createIndexes(table);
        }
        if (bcpWriter.getOkToDropTriggers()) {
          if (logger != null)
            logger.logInfo(table + ": Create triggers on table");
          dbSchema.createTriggers(table);
        }
        throw e;
      }
      // All is OK...The import ran without exception.
      // recreate indexes and triggers if they were dropped in advance
      if (bcpWriter.getOkToDropIndexes()) {
        if (logger != null)
          logger.logInfo(table + ": Create indexes on table");
        dbSchema.createIndexes(table);
      }
      if (bcpWriter.getOkToDropTriggers()) {
        if (logger != null)
          logger.logInfo(table + ": Create triggers on table");
        dbSchema.createTriggers(table);
      }

    }
    // execute any sql post bcp as designated in the bcpWriter
    if (logger != null)
      logger.logInfo(table + ": Execute any configured post-SQL statements");
    executeSql(bcpWriter.getPostSql(),
               bcpWriter.getTable().getSQLDataManager());
    // truncate the log if configured to do so
    if (okToTruncateLog) {
      if (logger != null)
        logger.logInfo("Truncate transaction log");
      dbSchema.truncateLog();
    }
    // see if bcp file should be removed
    if (removeAfterExecute) {
      File f = new File(file);
      f.delete();
    }
  }

  /**
   * executes a vector of sql strings
   * @assumes nothing
   * @effects an sql statment will executed in the database
   * @param v the vector of sql strings
   * @param sqlmanager the SQLDataManager to use
   * @throws DBException thrown if there is an sql exception
   */
  private void executeSql(Vector v, SQLDataManager sqlmanager) throws
      DBException {
    if (v != null) {
      for (Iterator it = v.iterator(); it.hasNext(); ) {
        String sql = (String) it.next();
        sqlmanager.executeUpdate(sql);
      }
    }
  }

  public FileImporter chooseFileImporter()
  {
      if (this.connectionClass.equals(
        "org.jax.mgi.shr.dbutils.MGIDriverManager"))
      {
          return new FileImporterSybase();
      }
      else
          return new FileImporterMySQL();
  }

  /**
   * converts a string representing the delimiter to a string suitable
   * for writing to the command line
   * @assumes mothing
   * @effects nothing
   * @param s the string to convert
   * @return the converted value
   * @throws BCPException
   */
  private String convertDelimiter(String s) throws BCPException {
    if (s == null || s.equals("")) {
      BCPExceptionFactory eFactory = new BCPExceptionFactory();
      BCPException e = (BCPException)
          eFactory.getException(InvalidDelimiter);
      e.bind("null");
      throw e;
    }
    if (s.toUpperCase().equals("SPACE") || s.equals(" "))
      return new String("' '");
    else if (s.toUpperCase().equals("TAB") || s.equals("\t"))
      return new String("'\t'");
    else {
      return new String("'" + s + "'");
    }
  }
}
// $Log$
// Revision 1.1  2013/01/30 18:03:59  mgiadmin
// postgres convert
//
// Revision 1.6  2004/08/04 14:20:11  mbw
// removed call to resetKeys on the Table class
//
// Revision 1.5  2004/07/28 18:15:35  mbw
// javadocs only
//
// Revision 1.4  2004/07/26 16:58:29  mbw
// formatting only
//
// Revision 1.3  2004/07/21 21:03:56  mbw
// - modified signatures for the getBCPWriter methods
// - removed the getSQLDataManager and the setSQLDataManager methods
// - added drop trigger functionality
// - made compatible with nonSybase databases
//
// Revision 1.2  2004/01/16 17:26:48  mbw
// formatting code only
//
// Revision 1.1  2003/12/30 16:50:46  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:49:18  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.2.10  2003/11/26 11:22:05  dbm
// Write the bcp command to the diagnostic log
//
// Revision 1.1.2.9  2003/11/19 14:43:51  dbm
// Only skip indexes and bcp if there are no records to load
//
// Revision 1.1.2.8  2003/10/23 17:43:14  dbm
// Set defaults for drop index and truncate table in getWriter(Table,BCPWriterCfg)
//
// Revision 1.1.2.7  2003/10/23 13:38:25  mbw
// added new parameters settings for okTODropIndexes and okToTruncateTable
//
// Revision 1.1.2.6  2003/10/14 13:40:40  dbm
// Close all bcp files before attempting to execute them.
//
// Revision 1.1.2.5  2003/10/02 19:04:13  mbw
// added new calls to the setDefault methods on the incoming BCPWriterCfg in the getBCPWriter method
//
// Revision 1.1.2.4  2003/10/02 16:25:19  mbw
// *** empty log message ***
//
// Revision 1.1.2.3  2003/10/02 16:23:35  mbw
// bug fix: now calling setDefault methods on BCPWriterCfg in getWriter() method
//
// Revision 1.1.2.2  2003/10/01 18:33:38  dbm
// Add auto flush capability the bcp writers
//
// Revision 1.1.2.1  2003/09/24 14:24:47  mbw
// moved to new package
//
// Revision 1.5.2.33  2003/09/18 19:22:50  mbw
// added a SQLDataManager reference as a new instance variable
// and now have dual methods for getBCPWriter(): one which takes a String and another which takes a Table object
//
// Revision 1.5.2.32  2003/09/10 21:23:52  mbw
// now using a Table class directly instead of the name of the table and it no longer needs a SQLDataManager since it can get one from the Table class
//
// Revision 1.5.2.31  2003/09/08 15:23:37  mbw
// initial version
//
// Revision 1.5.2.30  2003/06/04 18:29:51  mbw
// javadoc edits
//
// Revision 1.5.2.29  2003/06/04 14:51:47  mbw
// javadoc edits
//
// Revision 1.5.2.28  2003/05/22 15:56:16  mbw
// javadocs edits
//
// Revision 1.5.2.27  2003/05/16 15:11:19  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.5.2.26  2003/05/15 00:50:07  mbw
// impacted from changes to unitTest package
//
// Revision 1.5.2.25  2003/05/14 15:10:12  dbm
// Include the table name on bcp log messages
//
// Revision 1.5.2.24  2003/05/13 18:21:15  mbw
// modified logging messages
//
// Revision 1.5.2.23  2003/05/08 19:40:53  mbw
// changed to suit addition of new class BCPWriterCfg
//
// Revision 1.5.2.22  2003/05/08 01:56:09  mbw
// incorporated changes from code review
//
// Revision 1.5.2.21  2003/04/29 21:22:47  mbw
// changed to reflect changes made to the BCPManagerCfg class
//
// Revision 1.5.2.20  2003/04/28 21:18:32  mbw
// creating an instance member for the BCPExceptionFactory
//
// Revision 1.5.2.19  2003/04/25 17:11:57  mbw
// updated to reflect changes in design of exception handling
//
// Revision 1.5.2.18  2003/04/15 17:39:37  dbm
// Log any pre/post bcp SQL statements
//
// Revision 1.5.2.17  2003/04/15 17:07:40  dbm
// Check for null vector of SQL commands before trying to execute
//
// Revision 1.5.2.16  2003/04/15 12:02:29  mbw
// javadoc edits
//
// Revision 1.5.2.15  2003/04/11 15:27:13  mbw
// added code to execute pre sql and post sql when running bcp
//
// Revision 1.5.2.14  2003/04/10 17:22:50  mbw
// getBCPWriter now takes string as a parameter vs Table
//
// Revision 1.5.2.13  2003/04/09 21:20:36  mbw
// added setter methods (no longer uses configuration objects for storing attribute values); moved executeBCP code from BCPWriter to this class;
//
// Revision 1.5.2.12  2003/04/04 00:13:31  mbw
// moved bcp implementation to BCPWriter (see rev revision 1.5.2.11)
//
// Revision 1.5.2.11  2003/04/04 00:09:03  mbw
// removed extraneous EOF character
//
// Revision 1.5.2.10  2003/03/21 16:52:48  mbw
// added standard header/footer
