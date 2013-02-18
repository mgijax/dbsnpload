// $Header$
// $Name$

package org.jax.mgi.shr.dbutils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * An object that runs DDL commands from the dbschema
 * product, including create table, create index, drop index, etc.
 * @has A SQLDataManager object for running sql and obtaining the
 * dbschema installation directory.
 * @does Searches files from the dbshema product, locating the sql
 * commands for creating tables, creating and dropping indexes, etc and
 * executes them through JDBC.
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class DBSchema
{

    private SQLDataManager sqlmanager = null;
    /**
     * regular expression for locating drop index commands in the dbschema
     * files
     */
    private static final String REGEX_DROP =
        "^[dD][rR][oO][pP] .*";
    /**
     * regular expression for locating create index commands in the dbschema
     * files
     */
    private static final String REGEX_CREATE =
        "^[cC][rR][eE][aA][tT][eE] .*";
    /**
     * regex for locating alter table commands in the dbschema files
     */
    private static final String REGEX_ALTER = "^[aA][lL][tT][eE][rR] .*$";
    /**
     * regular expression for locating primary and foreign key commands
     */
    private static final String REGEX_KEY = "^sp_.*$";
    /**
     * regular expression for locating isql go commands in the dbschema
     * files
     */
    private static final String REGEX_GO_COMMAND = "^[gG][oO]$";
    /**
     * regular expression whcih locates lines with nested quotes
     */
    //private static final String REGEX_QUOTE_CONFLICT = "\"[.*'.*]\"";
    private static final String REGEX_QUOTE_CONFLICT = ".*\".*'.*\".*";


    /**
     * compiled regex pattern for locating drop index commands in the
     * dbschema files
     */
    private static final Pattern dropPattern =
        Pattern.compile(REGEX_DROP);
    /**
     * compiled regex pattern for locating create index commands in the
     * dbschema files
     */
    private static final Pattern createPattern =
        Pattern.compile(REGEX_CREATE);
    /**
     * compiled regex pattern for locating alter table commands in the
     * dbschema files
     */
    private static final Pattern alterPattern =
        Pattern.compile(REGEX_ALTER);
    /**
     * compiled regex pattern for locating create index commands in the
     * dbschema files
     */
    private static final Pattern keyPattern =
        Pattern.compile(REGEX_KEY);
    /**
     * compiled regex pattern for locating isql go commands in the dbschema
     * files used when locating the end of a create table command.
     */
    private static final Pattern goCommandPattern =
        Pattern.compile(REGEX_GO_COMMAND);
    /**
     * compiled regex pattern for locating lines with nested quotation marks.
     */
    private static final Pattern quoteConflictPattern =
        Pattern.compile(REGEX_QUOTE_CONFLICT);

    /*
     * the exception factory for DBScemaExceptions
     */
    private DBSchemaExceptionFactory exceptionFactory =
        new DBSchemaExceptionFactory();

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String FileNotFoundErr =
        DBSchemaExceptionFactory.FileNotFoundErr;
    private static String FileReadErr =
        DBSchemaExceptionFactory.FileReadErr;
    private static String FileCloseErr =
        DBSchemaExceptionFactory.FileCloseErr;
    private static String NoRegexMatch =
        DBSchemaExceptionFactory.NoRegexMatch;
    private static String UnexpectedString =
        DBSchemaExceptionFactory.UnexpectedString;
    private static String NestedQuotesErr =
        DBSchemaExceptionFactory.NestedQuotesErr;

    /**
     * constructor which sets the database configuartion
     * @param pSqlmanager the database manager object
     */
    public DBSchema(SQLDataManager pSqlmanager)
    {
        sqlmanager = pSqlmanager;
    }

    /**
     * get the SQLDataManager for this instance
     * @assumes nothing
     * @effects nothing
     * @return the SQLDataManager for this instance
     */
    public SQLDataManager getSQLDataManager()
    {
        return this.sqlmanager;
    }

    /**
     * locates the drop index commands from the dbschema product for the
     * given table and executes them in the database and if there are
     * partitions defined for the given table then they need to be dropped in
     * advance.
     * @assumes nothing
     * @effects indexes will be dropped on the given table
     * @param pTablename table name
     * @throws DBSchemaException thrown if there is an error accessing the
     * files within the dbschema product
     * @throws DBException thrown if there is an error with the database
     */
    public void dropIndexes(String pTablename)
        throws DBSchemaException, DBException
    {
        Vector sqlIndex = getDropIndexCommands(pTablename);
        String sqlPartition = null;
        try // if exception then assume no partitions are defined
        {
            sqlPartition = getDropPartitionCommand(pTablename);
        }
        catch (DBSchemaException e)
        {}
        if (sqlPartition != null)
            sqlmanager.executeUpdate(sqlPartition);
        executeSqlVector(sqlIndex);
    }

    /**
     * locates the create index commands from the dbschema product for the
     * given table and executes them in the db and if there are partitions
     * defined for the given table then they need to be created in advance.
     * @assumes nothing
     * @effects indexes will be created for the given table
     * @param pTablename table name
     * @throws DBSchemaException thrown if there is an error accessing the
     * files within the dbschema product
     * @throws DBException thrown if there is an error with the database
     */
    public void createIndexes(String pTablename)
        throws DBSchemaException, DBException
    {
        Vector sqlIndex = getCreateIndexCommands(pTablename);
        String sqlPartition = null;
        try // if exception then assume no partitions are defined
        {
            sqlPartition = getCreatePartitionCommand(pTablename);
        }
        catch (DBSchemaException e)
        {}
        if (sqlPartition != null)
            sqlmanager.executeUpdate(sqlPartition);
        executeSqlVector(sqlIndex);
    }

    /**
     * create the primary key(s) in the given table
     * @param pTablename name of the table
     * @assumes the primary key command in the dbschema product uses
     * sp_primarykey
     * @effects nothing
     * @throws DBSchemaException thrown if there is an error accessing the
     * dbschema product
     * @throws DBException thrown if there is an error accessing the database
     */
    public void createPKeys(String pTablename)
        throws DBSchemaException, DBException
    {
        Vector commands = getCreatePKeyCommands(pTablename);
        executeSqlVector(commands);
    }

    /**
     * create the foreign key(s) in the given table
     * @param pTablename name of the table
     * @assumes the primary key command in the dbschema product uses
     * sp_foreignkey
     * @effects nothing
     * @throws DBSchemaException thrown if there is an error accessing the
     * dbschema product
     * @throws DBException thrown if there is an error accessing the database
     */

    public void createFKeys(String pTablename)
        throws DBSchemaException, DBException
    {
        Vector commands = getCreateFKeyCommands(pTablename);
        executeSqlVector(commands);
    }


    /**
     * locates the create index command from the dbschema product for the
     * given table and executes it in the database.
     * @assumes nothing
     * @effects a new table will be created in the database
     * @param pTablename table name
     * @throws DBSchemaException thrown if there is an error accessing the
     * files within the dbschema product
     * @throws DBException thrown if there is an error with the database
     */
    public void createTable(String pTablename)
        throws DBSchemaException, DBException
    {
        String command = getCreateTableCommand(pTablename);
        if (sqlmanager.isOracle())
            command = this.convertToOracle(command);
        sqlmanager.executeUpdate(command);
    }

    /**
     * executes the drop table command for the given table name in the
     * database.
     * @assumes nothing
     * @effects a table will be dropped from the database
     * @param pTablename table name
     * @throws DBException thrown if there is an error with the database
     */
    public void dropTable(String pTablename)
        throws DBException
    {
        String command = "drop table " + pTablename;
        Vector v = new Vector();
        v.add(command);
        executeSqlVector(v);
    }

    /**
     * locates the create trigger commands from the dbschema product for the
     * given table and executes them in the database.
     * @assumes nothing
     * @effects new triggers will be created in the database
     * @param pTablename table name
     * @throws DBSchemaException thrown if there is an error accessing the
     * files within the dbschema product
     * @throws DBException thrown if there is an error with the database
     */
    public void createTriggers(String pTablename)
        throws DBSchemaException, DBException
    {
        Vector commands = getCreateTriggerCommands(pTablename);
        executeSqlVector(commands);
    }

    /**
     * executes the drop trigger commands for the given table name in the
     * database.
     * @assumes nothing
     * @effects triggers will be drpped from the database
     * @param pTablename table name
     * @throws DBException thrown if there is an error with the database
     * @throws DBSchemaException thrown if there is an error running the
     * DBSchema product
     */
    public void dropTriggers(String pTablename)
        throws DBSchemaException, DBException
    {
        Vector commands = getDropTriggerCommands(pTablename);
        executeSqlVector(commands);
    }



    /**
     * locates the create partition command from the dbschema product for the
     * given table and executes it in the database.
     * @assumes nothing
     * @effects partitions will be added to the table
     * @param pTablename table name
     * @throws DBSchemaException thrown if there is an error accessing the
     * files within the dbschema product
     * @throws DBException thrown if there is an error with the database
     */
    public void createPartition(String pTablename)
        throws DBSchemaException, DBException
    {
        String command = getCreatePartitionCommand(pTablename);
        sqlmanager.executeUpdate(command);
    }

    /**
     * locates the drop partition command from the dbschema product for the
     * given table and executes it in the database.
     * @assumes nothing
     * @effects partitions will be dropped from the table
     * @param pTablename table name
     * @throws DBSchemaException thrown if there is an error accessing the
     * files within the dbschema product
     * @throws DBException thrown if there is an error with the database
     */
    public void dropPartition(String pTablename)
        throws DBSchemaException, DBException
    {
        String command = getDropPartitionCommand(pTablename);
        sqlmanager.executeUpdate(command);
    }

    /**
     * truncates the transaction log for the configured database.
     * @assumes nothing
     * @effects a table will be truncated
     * @param pTablename table name
     * @throws DBException thrown if there is an error with the database
     */
    public void truncateTable(String pTablename)
        throws DBException
    {
	String command; 
	if(sqlmanager.isSybase())
	{
        	command = "truncate table " + pTablename;
	}
	else
	{
		// postgres syntax
        	command = "truncate table " + pTablename+" cascade ";
	}
        Vector v = new Vector();
        v.add(command);
        executeSqlVector(v);
    }

    /**
     * truncates the transaction log for the configured database.
     * @assumes nothing
     * @effects the database log will be truncated
     * @throws DBException thrown if an error occurs with the database
     */
    public void truncateLog()
        throws DBException
    {
        String dbname = sqlmanager.getDatabase();
        String command = "dump transaction " + dbname + " with truncate_only";
        Vector v = new Vector();
        v.add(command);
        executeSqlVector(v);
    }

    /**
     * parse the dbschema file and extract the sql commands for creating
     * indexes for the given table
     * @assumes nothing
     * @effects nothing
     * @param pTablename the given table name
     * @return a vector of sql commands from the dbschema file
     * @throws DBSchemaException thrown if the create index commands could
     * not be obtained fron the dbschema product
     */
    protected Vector getCreateIndexCommands(String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename("index", "create", pTablename);
        Vector v1 = getCommands(filename, createPattern);
        // do text substition for segment names
        Vector v2 = new Vector();
        String s = null;
        for (int i = 0; i < v1.size(); i++)
        {
            if (this.sqlmanager.isSybase())
            {
                s = ((String)v1.get(i)).replaceAll(
                    "\\$(\\{)?+DBCLUSTIDXSEG(\\})?+", "seg0");
                s = (s.replaceAll("\\$(\\{)?+DBNONCLUSTIDXSEG(\\})?+",
                                  "seg1"));
            }
            else
            {
                s = ((String)v1.get(i)).replaceAll(
                    "on \\$(\\{)?+DBCLUSTIDXSEG(\\})?+", "");
                s = (s.replaceAll("on \\$(\\{)?+DBNONCLUSTIDXSEG(\\})?+",
                                  ""));
                s = (s.replaceAll("on system", ""));
                s = (s.replaceAll("nonclustered index", "index"));
                s = (s.replaceAll("clustered index", "index"));
            }
            v2.add(s);
        }
        return v2;
    }

    /**
     * parse the dbschema file and extract the sql commands for creating
     * primary and foreign keys for the given table
     * @assumes the regular expression pattern sp_primarykey
     * to locate the command from the dbschema product
     * @effects nothing
     * @param pTablename the given table name
     * @return a vector of sql commands from the dbschema file
     * @throws DBSchemaException thrown if the create index commands could
     * not be obtained fron the dbschema product
     */
    protected Vector getCreatePKeyCommands(String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename("key", "create", pTablename);
        Vector v1 = getCommands(filename, keyPattern);
        if (this.sqlmanager.isSybase())
            return v1;
        else
        {
            // do text substition for non sybsase db to change sybase proc
            // calls with alter table command
            Vector v2 = new Vector();
            String s = null;
            for (int i = 0; i < v1.size(); i++)
            {
                s = ((String)v1.get(i));
                String[] tokens = s.split(",");
                String[] subtokens = tokens[0].split(" ");
                String tableName = subtokens[1];
                String command = subtokens[0];
                String columnName = tokens[1];
                for (int j = 2; j < tokens.length; j++)
                {
                    columnName = columnName.concat(", " + tokens[j]);
                }
                if (command.equals("sp_primarykey"))
                {
                    String alterTable =
                        "ALTER TABLE tableName_ ADD PRIMARY KEY " +
                        "( columnName_ )";
                    alterTable = alterTable.replaceFirst("tableName_",
                        tableName);
                    alterTable = alterTable.replaceFirst("columnName_",
                        columnName);
                    v2.add(alterTable);
                }
            }
            return v2;
        }
    }

    /**
     * parse the dbschema file and extract the sql commands for creating
     * foreign keys for the given table
     * @assumes the regular expression pattern sp_foreignkey is used
     * to locate the command from the dbschema product
     * @effects nothing
     * @param pTablename the given table name
     * @return a vector of sql commands from the dbschema file
     * @throws DBSchemaException thrown if the create index commands could
     * not be obtained fron the dbschema product
     */
    protected Vector getCreateFKeyCommands(String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename("key", "create", pTablename);
        Vector v1 = getCommands(filename, keyPattern);
        if (this.sqlmanager.isSybase())
            return v1;
        else
        {
            // do text substition for non sybsase db to change sybase proc
            // calls with alter table command
            Vector v2 = new Vector();
            String s = null;
            for (int i = 0; i < v1.size(); i++)
            {
                s = ((String)v1.get(i));
                String[] tokens = s.split(",");
                String[] subtokens = tokens[0].split(" ");
                String tableName = subtokens[1];
                String command = subtokens[0];
                String columnName = tokens[1];
                for (int j = 2; j < tokens.length; j++)
                {
                    columnName = columnName.concat(", " + tokens[j]);
                }
                if (command.equals("sp_foreignkey"))
                {
                    String alterTable =
                        "ALTER TABLE tableName_ ADD FOREIGN KEY " +
                        "( columnName_ )";
                    alterTable = alterTable.replaceFirst("tableName_",
                        tableName);
                    alterTable = alterTable.replaceFirst("columnName_",
                        columnName);
                    v2.add(alterTable);
                }
            }
            return v2;
        }
    }


    /**
     * searched the dbschema file and extracts the sql commands for dropping
     * indexes for the given table
     * @assumes nothing
     * @effects nothing
     * @param pTablename the given table name
     * @return a vector of sql commands from the dbschema file
     * @throws DBSchemaException thrown if the drop index commands could
     * not be obtained fron the dbschema product
     */
    protected Vector getDropIndexCommands(String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename("index", "drop", pTablename);
        return getCommands(filename, dropPattern);
    }

    /**
     * search the dbschema file and extracts the create table command from it
     * @assumes nothing
     * @effects nothing
     * @param pTablename the name of the table
     * @return the create table command
     * @throws DBSchemaException thrown if the create table command could
     * not be obtained fron the dbschema product.
     * @throws DBException thrown if there is an error with the database
     */
    protected String getCreateTableCommand(String pTablename)
        throws DBSchemaException
    {
        Vector commandsFound = getCreateCommands("table", pTablename);
        String s = (String)commandsFound.get(0);
        if (this.sqlmanager.isSybase())
            s = (s.replaceFirst("\\$(\\{)?+DBTABLESEGMENT(\\})?+", "seg0"));
        else
        {
            s = (s.replaceFirst("on \\$(\\{)?+DBTABLESEGMENT(\\})?+", ""));
            s = (s.replaceFirst("on seg\\d", ""));
        }
        return s;
    }

    /**
     * search the dbschema file and extracts the create trigger commands
     * from it
     * @assumes nothing
     * @effects nothing
     * @param pTablename the name of the table
     * @return the create trigger commands
     * @throws DBSchemaException thrown if the create trigger commands could
     * not be obtained fron the dbschema product.
     * @throws DBException thrown if there is an error with the database
     */
    protected Vector getCreateTriggerCommands(String pTablename)
        throws DBSchemaException
    {
        Vector v = new Vector();
        Vector commands = getCreateCommands("trigger", pTablename);
        for (int i = 0; i < commands.size(); i++)
        {
            String s = ((String)commands.get(i));
            s = s.replaceAll("\"", "'");
            v.add(s);
        }
        return v;
    }

    /**
     * search the dbschema file and extract the sql commands for dropping
     * triggers for the given table
     * @assumes nothing
     * @effects nothing
     * @param pTablename the given table name
     * @return a vector of sql commands from the dbschema file
     * @throws DBSchemaException thrown if the drop trigger commands could
     * not be obtained fron the dbschema product
     */
    protected Vector getDropTriggerCommands(String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename("trigger", "drop", pTablename);
        return getCommands(filename, dropPattern);
    }



    /**
     * search the given file and extract any create commands ('create table',
     * 'create trigger', 'create procedure') from it.
     * @assumes nothing
     * @effects nothing
     * @param targetObject the target object of the create command such as
     * 'table', 'trigger', etc.
     * @param pTablename the name of the table
     * @return the vector of create commands
     * @throws DBSchemaException thrown if the create commands could
     * not be obtained fron the dbschema product.
     * @throws DBException thrown if there is an error with the database
     */
    protected Vector getCreateCommands(String targetObject,
                                       String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename(targetObject, "create",
                                            pTablename);
        String line = null;
        Vector allCommands = new Vector();
        StringBuffer command = new StringBuffer();
        Pattern commandPattern = createPattern;
        Pattern goPattern = goCommandPattern;
        Matcher commandMatcher = null;
        Matcher goMatcher = null;
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException e)
        {
            DBSchemaException e2 = (DBSchemaException)
                exceptionFactory.getException(FileNotFoundErr, e);
            e2.bind(filename);
            throw e2;
        }
        try
        {
            while ((line = in.readLine()) != null)
            {
                line = line.trim();
                // search for the create table command
                commandMatcher = commandPattern.matcher(line);
                if (commandMatcher.matches())
                {
                    command.append(line);
                    boolean foundGoCommand = false;
                    // found create trigger command, now look for go command
                    while ((line = in.readLine()) != null)
                    {
                        line = line.trim();
                        goMatcher = goPattern.matcher(line);
                        if (!goMatcher.matches())
                        {
                          Matcher quoteConflictMatcher =
                              quoteConflictPattern.matcher(line);
                          if (quoteConflictMatcher.matches())
                          {
                              DBSchemaException e = (DBSchemaException)
                                  exceptionFactory.getException(NestedQuotesErr);
                              e.bind(targetObject);
                              e.bind(pTablename);
                              e.bind(line);
                              throw e;
                          }

                            command.append(" " + line);
                        }
                        else
                        {
                            foundGoCommand = true;
                            break;
                        }
                    }
                    if (!foundGoCommand)
                    {
                        DBSchemaException e2 = (DBSchemaException)
                            exceptionFactory.getException(UnexpectedString);
                        e2.bind(new String(command));
                        e2.bind(filename);
                        throw e2;
                    }
                    allCommands.add(new String(command));
                    command = new StringBuffer();
                    //break;
                }
            }
        }
        catch (IOException e)
        {
            DBSchemaException e2 = (DBSchemaException)
                exceptionFactory.getException(FileReadErr, e);
            e2.bind(filename);
            throw e2;
        }
        if (allCommands.size() == 0)
        {
            DBSchemaException e2 = (DBSchemaException)
                exceptionFactory.getException(NoRegexMatch);
            e2.bind(REGEX_CREATE);
            e2.bind(filename);
            throw e2;
        }
        try
        {
            in.close();
        }
        catch (IOException e)
        {
            DBSchemaException e2 = (DBSchemaException)
                exceptionFactory.getException(FileCloseErr, e);
            e2.bind(filename);
            throw e2;
        }
        return allCommands;
    }


    /**
     * parse the dbschema file and extract the sql command for creating
     * partitions for the given table
     * @assumes nothing
     * @effects nothing
     * @param pTablename the given table name
     * @return the sql string from the dbschema file
     * @throws DBSchemaException thrown if the create partition command could
     * not be obtained fron the dbschema product
     */
    protected String getCreatePartitionCommand(String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename("partition", "create",
                                            pTablename);
        Vector v = getCommands(filename, alterPattern);
        return (String)v.get(0);
    }

    /**
     * searches the dbschema file and extract the sql command for dropping
     * partitions for the given table
     * @assumes nothing
     * @effects nothing
     * @param pTablename the given table name
     * @return the sql string from the dbschema file
     * @throws DBSchemaException thrown if the drop partition command could
     * not be obtained fron the dbschema product
     */
    protected String getDropPartitionCommand(String pTablename)
        throws DBSchemaException
    {
        String filename = calculateFilename("partition", "drop",
                                            pTablename);
        Vector v = getCommands(filename, alterPattern);
        return (String)v.get(0);
    }

    /**
     * searches a file and extracts lines which match the given regular
     * expression
     * @assumes nothing
     * @effects nothing
     * @param pFilename the file to search
     * @param pRegex the regular expression to match on
     * @return the vector of lines which matched the regular expression
     * @throws DBSchemaException thrown if there is an error accessing the
     * dbschema files
     */
    private Vector getCommands(String pFilename, Pattern pRegex)
        throws DBSchemaException
    {
        String filename = pFilename;
        Vector sql = new Vector();
        String line = null;
        Pattern commandPattern = pRegex;
        Matcher commandMatcher = null;
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException e)
        {
            DBSchemaException e2 = (DBSchemaException)
                exceptionFactory.getException(FileNotFoundErr, e);
            e2.bind(filename);
            throw e2;
        }
        try
        {
            while ((line = in.readLine()) != null)
            {
                line = line.trim();
                commandMatcher = commandPattern.matcher(line);
                if (commandMatcher.matches())
                    sql.add(line);
            }
        }
        catch (IOException e)
        {
            DBSchemaException e2 = (DBSchemaException)
                exceptionFactory.getException(FileReadErr, e);
            e2.bind(filename);
            throw e2;
        }
        try
        {
            in.close();
        }
        catch (IOException e)
        {
            DBSchemaException e2 = (DBSchemaException)
                exceptionFactory.getException(FileCloseErr, e);
            e2.bind(filename);
            throw e2;
        }
        return sql;
    }

    /**
     * calculate the file name for a file in the dbschema product given the
     * command noun (like 'table', 'index', etc) and the command verb (like
     * 'create' or 'drop') and the name of the table. The configuration
     * parameter DBSCHEMA_INSTALLDIR is used as the directory name in which
     * the dbschema product is installed.
     * @assumes nothing
     * @effects nothing
     * @param pCommandNoun the sql command noun as in table or index
     * @param pCommandVerb the sql command verb as in create or delete
     * @param tablename the table
     * @return the name of the file from the dbschem product represented by
     * the given parameters
     */
    private String calculateFilename(String pCommandNoun,
                                     String pCommandVerb,
                                     String tablename)
    {
        String root = sqlmanager.getDBSchemaDir();
        String system = System.getProperties().getProperty("os.name");
        String delimiter = null;
        if (system.equals("Windows 2000"))
            delimiter = "\\";
        else
            delimiter = "/";
        String filename = sqlmanager.getDBSchemaDir() + delimiter +
            pCommandNoun + delimiter +
            tablename + "_" + pCommandVerb + ".object";
        return filename;
    }

    /**
     * exeutes the sql commands found within the given vector
     * @assumes nothing
     * @effects an sql statement will be executed within the database
     * @param pCommands a vector of sql commands
     * @throws DBException
     */
    protected void executeSqlVector(Vector pCommands)
        throws DBException
    {
        for (Iterator it = pCommands.iterator(); it.hasNext(); )
        {
            String command = (String)it.next();
            int rtn = sqlmanager.executeUpdate(command);
        }
    }

    /**
     * convert create database command to use Oracle datatypes
     * @assumes nothing
     * @effects nothing
     * @param sql the 'create table' command
     * @return revised 'create table' command using Oracle datatypes
     */
    private String convertToOracle(String sql)
    {
        String newsql = null;
        newsql = sql.replaceAll("varchar", "varchar2");
        newsql = newsql.replaceAll("datetime", "date");
        newsql = newsql.replaceAll("text", "long");
        newsql = newsql.replaceAll("bit", "smallint");
        return newsql;
    }


}
// $Log$
// Revision 1.1  2013/01/30 18:03:26  mgiadmin
// postgres convert
//
// Revision 1.11  2013/01/30 16:31:28  mgiadmin
//
// convert to postgres
//
// convert to postgres
//
// convert to postgres
//
// Revision 1.10  2004/09/03 17:52:54  mbw
// added a check for nested quotation marks within trigger
//
// Revision 1.9  2004/07/26 16:30:36  mbw
// formatting only
//
// Revision 1.8  2004/07/21 19:33:07  mbw
// added create trigger functionality along with create primary and foreign key functionality and also incorporated code to make more portable across different databases (namely MySQL and Oracle)
//
// Revision 1.7  2004/04/02 17:02:16  mbw
// bug fix: fixed regex strings
//
// Revision 1.6  2004/04/02 16:00:33  mbw
// bug fix: fixed regex string
//
// Revision 1.5  2004/03/29 19:49:46  mbw
// added compatibility with mysql databases
//
// Revision 1.4  2004/01/21 20:45:19  mbw
// added the dropping and creating of partition functionality
//
// Revision 1.3  2004/01/21 19:24:45  mbw
// consolidated the command search strings
//
// Revision 1.2  2004/01/14 17:59:49  sc
// changed getCreateIndexCommands to replace environ var reference with new segment names
//
// Revision 1.1  2003/12/30 16:50:24  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:48:55  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.4.9  2003/10/23 19:35:26  mbw
// bug fixed previous committed changes
//
// Revision 1.1.4.8  2003/10/23 18:51:36  mbw
// added use of segments for the create table commands
//
// Revision 1.1.4.7  2003/09/22 20:18:35  mbw
// made file seperator symbol more portable
//
// Revision 1.1.4.6  2003/08/26 15:24:57  mbw
// fixed bug so that proper DBSchemaException is thrown not DBException and added final to static constant definitions
//
// Revision 1.1.4.5  2003/06/17 20:26:48  mbw
// removed the use of this because of static context
//
// Revision 1.1.4.4  2003/06/04 18:29:55  mbw
// javadoc edits
//
// Revision 1.1.4.3  2003/06/03 19:50:50  mbw
// removed the use of table segments from the create table method
//
// Revision 1.1.4.2  2003/05/22 15:56:20  mbw
// javadocs edits
//
// Revision 1.1.4.1  2003/05/20 18:29:10  mbw
// merged from branch lib_java_shrdbutils-1-0-7-jsam
//
// Revision 1.1.2.11  2003/05/16 15:11:22  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.1.2.10  2003/05/13 18:22:24  mbw
// fixed bug in truncateLog which preventing execution of command
//
// Revision 1.1.2.9  2003/05/08 01:56:12  mbw
// incorporated changes from code review
//
// Revision 1.1.2.8  2003/04/28 21:13:48  mbw
// switched use from DBExceptionFactory to DBSchemaExceptionFactory
//
// Revision 1.1.2.7  2003/04/25 17:12:00  mbw
// updated to reflect changes in design of exception handling
//
// Revision 1.1.2.6  2003/04/15 12:07:09  mbw
// javadoc edits
//
// Revision 1.1.2.5  2003/04/09 21:25:18  mbw
// replaced attribute DatabaseCfg with SQLDataManager
//
// Revision 1.1.2.4  2003/03/21 16:52:55  mbw
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
