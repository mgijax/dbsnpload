package org.jax.mgi.shr.dbutils;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.jax.mgi.shr.config.ScriptWriterCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.unix.RunCommand;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.LoggerFactory;
import org.jax.mgi.shr.config.LogCfg;
import org.jax.mgi.shr.exception.MGIException;

/**
 * A class which creates and executes sql scripts within the Sybase
 * database
 * @has a configuration object for configuring
 * @does provides methods for creating and executing sql scripts
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class ScriptWriter
{

    /**
     * SQLDataManager to execute the script against
     */
    private SQLDataManager sqlMgr = null;
    /**
     * the name of the script filename
     */
    private String filename = null;
    /**
     * the suffix of the script filename
     */
    private String suffix = null;
    /**
     * the name of the script output filename
     */
    private String outfilename = null;
    /**
     * the suffix of the script output filename
     */
    private String outsuffix = null;
    /*
     * the path where the script files are stored
     */
    private String path = null;
    /*
     * option to not run the script file on execute
     */
    private boolean preventExecute;
    /*
     * option to overwrite existing script files
     */
    private boolean okToOverwrite;
    /*
     * option to overwrite existing script files
     */
    private boolean okToAutoFlush;
    /*
     * option to use temp file when creating the script file
     */
    private boolean useTempFile;
    /*
     * option to remove the script file after executing
     */
    private boolean removeAfterExecute;
    /*
     * option to remove the output file after executing
     */
    private boolean removeOutputAfterExecute;
    /**
     * a class that implements the Logger interface
     */
    private Logger logger = null;
    /**
     * the File object for the script
     */
    private File scriptFile = null;
    /*
     * the java file writer object used for writing to the script file
     */
    private FileWriter fileWriter = null;
    /*
     * a java buffered writer for buffering the writes
     */
    private BufferedWriter bufferedWriter = null;
    /**
     * indicator as to whether or not the script file has been closed
     */
    private boolean closed = false;
    /**
     * indicator that the script file has been created
     */
    private boolean fileCreated = false;
    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String TempFileCreateErr =
        ScriptExceptionFactory.TempFileCreateErr;
    private static String FileCreateErr =
        ScriptExceptionFactory.FileCreateErr;
    private static String FileCloseErr =
        ScriptExceptionFactory.FileCloseErr;
    private static String FileWriteErr =
        ScriptExceptionFactory.FileWriteErr;
    private static String InterruptErr =
        ScriptExceptionFactory.InterruptErr;
    private static String ScriptIOErr =
        ScriptExceptionFactory.ScriptIOErr;
    private static String NonZeroErr =
        ScriptExceptionFactory.NonZeroErr;
    private static String ScriptOutFileErr =
        ScriptExceptionFactory.ScriptOutFileErr;
    private static String ScriptErrorOutput =
        ScriptExceptionFactory.ScriptErrorOutput;

    /**
     * default constructor which reads the configuration from a
     * default configuration object
     * @param sqlMgr the SQLDataManager to execute the script against
     * @throws ConfigException thrown if a configuration error occurs
     * @throws ScriptException thrown if there was an error creating the
     * script file
     */
    public ScriptWriter(SQLDataManager sqlMgr) throws ConfigException,
        ScriptException
    {
        this.sqlMgr = sqlMgr;
        configure(new ScriptWriterCfg());
    }

    /**
     * constructer which uses a given configuration object for obtaining
     * configuration parameters which is normally used for designating a
     * parameter prefix which is prepended to all parameter names on lookup
     * @param config the configuration object from which to configure the
     * ScriptWriter
     * @param sqlMgr the SQLDataManager to execute the script against
     * @throws ConfigException thrown if there is an error obtaining
     * configuration parameters
     * @throws ScriptException thrown if there was an error creating the
     * script file
     */
    public ScriptWriter(ScriptWriterCfg config, SQLDataManager sqlMgr) throws
        ConfigException, ScriptException
    {
        this.sqlMgr = sqlMgr;
        configure(config);
    }

    /**
     * set the Logger instance
     * @assumes nothing
     * @effects the internal reference to a Logger will be set
     * @param logger the Logger
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * set the option which allows existing files to be overwritten by new
     * script files with the same name, overriding the value found in the
     * configuration file or system properties.
     * @assumes nothing
     * @effects existing script files will be overwritten
     * @param boolParam true or false
     */
    public void setOkToOverwrite(boolean boolParam) {
      okToOverwrite = boolParam;
    }

    /**
     * set the option which designates whether to use temporary files when
     * creating script files, overriding the value found in the configuration
     * file or system properties. The location of the temporary directory is
     * determined by the java system property java.io.tempdir.
     * @assumes nothing
     * @effects script files will be created in a temporay directory
     * @param boolParam true or false
     */
    public void setUseTempFile(boolean boolParam) {
      useTempFile = boolParam;
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
     * set the path name where script file is to be created.
     */
    public void setPathname(String path)
    {
        this.path = path;
    }

    /**
     * set the file name of the script to create not including the file
     * extension characters which is defined by the setSuffix() method
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * set the file name suffix (file extension characters) of the script file
     * name.
     */
    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    /**
     * set the file name of the script output file not including the file
     * extension characters which is defined by the setOutSuffix() method
     */
    public void setOutFilename(String outfilename)
    {
        this.outfilename = outfilename;
    }

    /**
     * set the file name suffix (file extension characters) of the output file
     */
    public void setOutSuffix(String outSuffix)
    {
        this.outsuffix = outSuffix;
    }

    /**
     * set the value of the option which designates whether to prevent
     * executing the script when called.
     */
    public void setPreventExecute(boolean bool)
    {
        this.preventExecute = bool;
    }

    /**
     * set the value of the option which designates whether to remove the script
     * file after executing.
     */
    public void setRemoveAfterExecute(boolean bool)
    {
        this.removeAfterExecute = bool;
    }

    /**
     * get the directory name for the script file.
     * @return the directory name for the script file.
     */
    public String getPathname()
    {
        return this.path;
    }

    /**
     * get the file name of the script
     * @return filename name without any suffixed file extensions.
     */
    public String getFilename()
    {
        return this.filename;
    }

    /**
     * get the suffixed file extension characters of the script file name
     * @return suffixed file extension characters of script file name.
     */
    public String getSuffix()
    {
        return this.suffix;
    }

    /**
     * get the file name of the script output file.
     * @return the file name of the script output file.
     */
    public String getOutFilename()
    {
        return this.outfilename;
    }

    /**
     * get the suffixed file extension characters of the output file.
     * @return the suffixed file extension characters of the output file.
     */
    public String getOutSuffix()
    {
        return this.outsuffix;
    }

    /**
     * get the value of the option which designates whether to prevent
     * executing the script when called.
     * @return true if the script should not be executed or false otherwise
     */
    public boolean getPreventExecute()
    {
        return this.preventExecute;
    }

    /**
     * get the value of the option which designates whether it is ok to
     * overwrite an existing script file.
     * @return true if it is ok to overwrite an existing file or false
     * otherwise
     */
    public boolean getOkToOverwrite()
    {
        return this.okToOverwrite;
    }

    /**
     * get the value of the option which designates whether to use temporary
     * files when creating script files.
     * @return true if temp files should be used or false otherwise
     */
    public boolean getUseTempFile()
    {
        return this.useTempFile;
    }

    /**
     * get the value of the option which designates whether to remove the script
     * file after executing.
     * @return true if it is ok to remove the script after executing it or
     * false otherwise
     */
    public boolean getRemoveAfterExecute()
    {
        return this.removeAfterExecute;
    }

    /**
     * get the value of the option which designates whether to remove the output
     * file after executing.
     * @return true if it is ok to remove the output file after executing the
     * script or false otherwise
     */
    public boolean getRemoveOutputAfterExecute()
    {
        return this.removeOutputAfterExecute;
    }


    /**
     * get the value of the option which designates whether to flush
     * io buffer on each write to the script file.
     * @return true if it is ok to flush io buffer on each write or
     * false otherwise
     */
    public boolean getOkToAutoFlush()
    {
        return this.okToAutoFlush;
    }



    /**
     * write a line to the script
     * @param s the line to write
     * @throws ScriptException thown if there is an error writing to the script
     * file
     */
    public void write(String s) throws ScriptException
    {
        if (!this.fileCreated)
            createScriptFile();
        try
        {
            bufferedWriter.write(s + "\n");
        }
        catch (IOException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(FileWriteErr, e);
            e2.bind(scriptFile.getName());
            throw e2;
        }
    }

    /**
     * write a line to the script and include the command termination string
     * ('go' in the case of the Sybase database)
     * @param s the line to write along with the command termination string
     * @throws ScriptException thown if there is an error writing to the script
     * file
     */
    public void writeGo(String s) throws ScriptException
    {
        if (!this.fileCreated)
            createScriptFile();
        try
        {
            bufferedWriter.write(s + "\n");
            go();
        }
        catch (IOException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(FileWriteErr, e);
            e2.bind(scriptFile.getName());
            throw e2;
        }
    }

    /**
     * write the command termination string to the script file ('go' in the
     * case of the Sybase database)
     * @throws ScriptException thrown if there is an error writing to the
     * script file
     */
    public void go() throws ScriptException
    {
        if (!this.fileCreated)
            createScriptFile();
        try
        {
            bufferedWriter.write("go\n");
        }
        catch (IOException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(FileWriteErr, e);
            e2.bind(scriptFile.getName());
            throw e2;
        }
    }

    /**
     * executes the script and closes resources
     * @assumes nothing
     * @effects the script file will be executed and closed
     * @throws ScriptException thrown if there is an error executing the script
     */
    public void execute() throws ScriptException
    {
        if (!this.fileCreated)
            return;
        String server = sqlMgr.getServer();
        String db = sqlMgr.getDatabase();
        String user = sqlMgr.getUser();
        String pwFile = sqlMgr.getPasswordFile();
        String inFile = path + File.separator + filename + "." + suffix;
        String outFile = path + File.separator + outfilename + "." + outsuffix;
        String cmd = "cat " + pwFile + " | isql -U" + user + " -S" +
            server + " -D" + db + " -i " + inFile + " -o " + outFile + " -e";
        this.logger.logInfo("executing script: " + cmd);
        try
        {
            close();
        }
        catch (IOException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(FileCloseErr, e);
            e2.bind(scriptFile.getName());
            throw e2;
        }
        // check attributes to see if script execution should not be performed
        if (preventExecute)
            return;
        if (scriptFile.length() == 0)
            return;
        RunCommand runner = new RunCommand();
        runner.setCommand(cmd);
        int exitCode = 0;
        try
        {
            exitCode = runner.run();
        }
        catch (InterruptedException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(InterruptErr, e);
            e2.bind(cmd);
            throw e2;
        }
        catch (IOException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(ScriptIOErr, e);
            e2.bind(cmd);
            throw e2;
        }
        // The RunCommand executed without exception although the exit code
        // may still indicate an error has occurred. Log the contents of
        // standard out and standard error
        String msgErr = null;
        String msgOut = null;
        if ( (msgErr = runner.getStdErr()) != null)
            logger.logInfo(msgErr);
        if ( (msgOut = runner.getStdOut()) != null)
            logger.logInfo(msgOut);
        // throw a ScriptException on non-zero exit code
        if (exitCode != 0)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e = (ScriptException)
                eFactory.getException(NonZeroErr);
            e.bind(cmd);
            throw e;
        }
        SybaseOutFile sybaseOut = null;
        try
        {
            sybaseOut = new SybaseOutFile(outFile);
        }
        catch (MGIException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(ScriptOutFileErr, e);
            e2.bind(outFile);
            throw e2;

        }
        String errorSummary = null;
        try
        {
            errorSummary = sybaseOut.parseForErrors();
        }
        catch (MGIException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(ScriptOutFileErr, e);
            e.bind(outFile);
            throw e2;
        }

        if (errorSummary != null)
        {
            if (this.logger != null)
            {
                logger.logError(errorSummary);
            }

            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e = (ScriptException)
                eFactory.getException(ScriptErrorOutput);
            e.bind(outFile);
            throw e;

        }

        if (this.removeAfterExecute)
            this.scriptFile.delete();
        if (this.removeOutputAfterExecute)
        {
            File out = new File(this.outfilename);
            if (out.exists())
                out.delete();
        }
    }

    /**
     * aborts the script and will close resources without executing the script
     * file
     * @assumes nothing
     * @effects the script file will be closed
     * @throws ScriptException thrown if there is an error executing the script
     */
    public void abort() throws ScriptException
    {
        if (!this.fileCreated)
            return;
        try
        {
            close();
        }
        catch (IOException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(FileCloseErr, e);
            e2.bind(scriptFile.getName());
            throw e2;
        }
        if (this.removeAfterExecute)
            scriptFile.delete();
    }

    /**
     * sets all the attributes from the configuration object
     * @assumes nothing
     * @effects all internal values will be set
     * @param config the configuration object
     * @throws ConfigException throws if there is a configuration error
     */
    private void configure(ScriptWriterCfg config) throws ConfigException
    {
        path = config.getPathname();
        filename = config.getFilename();
        suffix = config.getSuffix();
        outfilename = config.getOutFilename();
        outsuffix = config.getOutSuffix();
        preventExecute = config.getPreventExecute().booleanValue();
        okToOverwrite = config.getOkToOverwrite().booleanValue();
        useTempFile = config.getUseTempFile().booleanValue();
        removeAfterExecute = config.getRemoveAfterExecute().booleanValue();
        LogCfg cfg = new LogCfg();
        LoggerFactory logFactory = cfg.getLoggerFactory();
        this.logger = logFactory.getLogger();
    }

    /**
     * create the script file
     * @assumes nothing
     * @effects a new script file will be created
     * @throws ScriptException thrown if there is an error creating the
     * script file
     */
    private void createScriptFile() throws ScriptException
    {
        if (useTempFile == true)
        { // create a temp file
            try
            {
                scriptFile = File.createTempFile(filename, suffix);
                scriptFile.deleteOnExit();
            }
            catch (IOException e)
            {
                ScriptExceptionFactory eFactory = new
                    ScriptExceptionFactory();
                ScriptException e2 = (ScriptException)
                    eFactory.getException(TempFileCreateErr, e);
                e2.bind(scriptFile.getName());
                throw e2;
            }
        }
        else
        { // use named file
            scriptFile = new File(path + File.separator +
                                  filename + "." + suffix);
            if (!okToOverwrite)
            {
                String filenameOrg = filename;
                String outfilenameOrg = outfilename;
                int count = 0;
                while (scriptFile.exists())
                {
                    count = count + 1;
                    filename = filenameOrg + "_" + String.valueOf(count);
                    outfilename = outfilenameOrg + "_" + String.valueOf(count);
                    scriptFile = new File(path + File.separator +
                                          filename + "." + suffix);
                }
            }
        }
        try
        {
            fileWriter = new FileWriter(scriptFile);
            bufferedWriter = new BufferedWriter(fileWriter);
        }
        catch (IOException e)
        {
            ScriptExceptionFactory eFactory = new ScriptExceptionFactory();
            ScriptException e2 = (ScriptException)
                eFactory.getException(FileCreateErr, e);
            e2.bind(scriptFile.getName());
            throw e2;
        }
        this.fileCreated = true;
    }

    /**
     * close the script file
     * @assumes nothing
     * @effects the scriptFile will be closed
     * @throws IOException thrown if there is an error closing the script file
     */
    private void close() throws IOException
    {
        if (!this.fileCreated)
            return;
        if (!closed)
        {
            bufferedWriter.close();
            fileWriter.close();
            closed = true;
        }
    }

    /**
     * is a an object used to store error information for a failed sybase isql
     * error
     * @has an errno and an error message
     * @does nothing
     * @company Jackson Laboratory
     * @author M Walker
     *
     */
    public static class SybaseError
    {
        public Integer errno = null;
        public String message = null;
        public int count = 1;
        public SybaseError(Integer errno, String errMessage)
        {
            this.errno = errno;
            this.message = errMessage;
        }
        public boolean equals(Object o)
        {
            SybaseError err = (SybaseError)o;
            if (err.errno.equals(this.errno) &&
                err.message.equals(this.message))
            {
                return true;
            }
            else
                return false;
        }
    }


    /**
     * A file that stores output from an isql script file
     * @has
     * @does
     * @company Jackson Laboratory
     * @author M Walker
     *
     */
    public static class SybaseOutFile extends InputDataFile
    {
        /**
         * An object that represents one record of output in the output file
         * of the script
         * @has the sql command executed and a SybaseError object if an error
         * occurred
         * @does nothing
         * @company Jackson Laboratory
         * @author M Walker
         *
         */
        public class SybaseEntry
        {
            public String sql = null;
            public SybaseError error = null;
            public SybaseEntry(String sql, SybaseError error)
            {
                this.sql = sql;
                this.error = error;
            }
        }
        private Hashtable errs = new Hashtable();
        protected int totalCount = 0;

        /**
         * constructor
         * @param filename the name of the script out file
         * @throws IOUException thrown if there is an error accessing the
         * out file
         * @throws ConfigException thrown if there is an error accessing the
         * configuration
         */
        public SybaseOutFile(String filename)
            throws IOUException, ConfigException
        {
            super(filename);
            super.setBeginDelimiter("1>");
            super.setEndDelimiter(null);
            super.setOkToUseRegex(false);
        }

        /**
         * get a RecordDataIterator which iterates over the out file
         * and returns SybaseEntry objects for each record
         * @return a RecordDataIterator
         * @throws IOUException thrown if there is an error accessing ther file
         */
        public RecordDataIterator getEntryIterator() throws IOUException
        {
            RecordDataIterator it = getIterator(new Interpreter());
            return it;
        }

        /**
         * A RecordDataInterpreter for creating SybaseEntry objects
         * @has nothing
         * @does creates SybaseEntry objects from the given record data
         * @company Jackson Laboratory
         * @author M Walker
         *
         */
        public class Interpreter implements RecordDataInterpreter
        {

            /**
             * determine if the current out file record is valid
             * @param s the current record
             * @return true if the current record is valid and false otherwise
             */
            public boolean isValid(String s)
            {
                return true;
            }

            /**
             * interpret a record from the out file
             * @param s the current record of the out file
             * @return a SybaseEntry object for the current record
             */
            public Object interpret(String s)
            {
                String[] lines =
                    s.split("\n");
                if (lines[1].startsWith("Msg"))
                {
                    String[] errFields = lines[1].split(",");
                    String[] tokens = errFields[0].split(" ");
                    Integer errno = new Integer(tokens[1]);
                    String message = new String("");
                    for (int i = 1; i < lines.length; i++)
                    {
                        message = message + lines[i] + "\n";
                    }
                    return new SybaseEntry(lines[0].substring(2),
                                           new SybaseError(errno, message));
                }
                return new SybaseEntry(lines[0].substring(2), null);
            }
        }

        /**
         * parse the output file and create a summary of the errors
         * @return a summary of errors occurring the output file
         * @throws MGIException thrown if there is an error parsing the file
         */
        public String parseForErrors() throws MGIException
        {
            RecordDataIterator it = this.getEntryIterator();
            while (it.hasNext())
            {
                SybaseEntry entry = (SybaseEntry)it.next();
                totalCount++;
                if (entry.error != null)
                {
                    SybaseError savedError =
                        (SybaseError)errs.get(entry.error.errno);
                    if (savedError != null)
                        savedError.count++;
                    else
                        errs.put(entry.error.errno, entry.error);
                }
            }
            // create summary
            StringBuffer summary = null;
            for (Iterator it2=errs.entrySet().iterator(); it2.hasNext(); ) {
                Map.Entry entry = (Map.Entry)it2.next();
                SybaseError error = (SybaseError)entry.getValue();
                if (summary == null)
                    summary = new StringBuffer("");
                summary.append("The following error occurred " + error.count +
                               " out of " + totalCount + " entries" +
                               System.getProperty("line.separator"));
                summary.append(error.message);
                summary.append(System.getProperty("line.separator"));
            }
            if (summary != null)
                return new String(summary);
            else
                return null;

        }
    }

    /**
     * get an instance of a SybaseOutFile
     * @param filename the name of the file
     * @return an instance of a SybaseOutFile
     * @throws IOUException thrown if there is an error accessing the file
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     */
    public static SybaseOutFile getSybaseOutFile(String filename)
    throws IOUException, ConfigException
    {
        return new SybaseOutFile(filename);
    }



}