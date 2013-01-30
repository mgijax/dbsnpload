package org.jax.mgi.shr.dbutils.bcp;

import java.io.IOException;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBSchema;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.unix.RunCommand;
import org.jax.mgi.shr.log.Logger;

/**
 * This class imports data from a file into a Sybase database
 * @has nothing
 * @does imports data from a file into a Sybase database
 * @company The Jackson Laboratory
 * @author MWalker
 *
 */


public class FileImporterSybase
    implements FileImporter
{

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String InterruptErr = BCPExceptionFactory.InterruptErr;
    private static String IOErr = BCPExceptionFactory.IOErr;
    private static String NonZeroErr = BCPExceptionFactory.NonZeroErr;

    /**
     * import the file into the database
     * @param filename the name of the file
     * @param tablename the name of the table
     * @param delimiter the delimieter to use
     * @param sqlMgr the SQLDataManager
     * @param logger the Logger
     * @throws BCPException thrown if there is an error importing
     */

    public void importFile(String filename,
                           String tablename,
                           String delimiter,
                           SQLDataManager sqlMgr,
                           Logger logger)
        throws BCPException
    {
        String server = sqlMgr.getServer();
        String db = sqlMgr.getDatabase();
        String user = sqlMgr.getUser();
        String pwFile = sqlMgr.getPasswordFile();
        DBSchema dbSchema = new DBSchema(sqlMgr);
        String cmd = "cat " + pwFile + " | bcp " + db + ".." + tablename +
            " in " + filename + " -c -S " + server + " -U " + user +
            " -t " + delimiter;
        int exitCode = 0;
        RunCommand runner = new RunCommand();
        try
        {
            if (logger != null)
                logger.logInfo(tablename + ": Execute the bcp command: " +
                               cmd);
            runner.setCommand(cmd);
            exitCode = runner.run();
        }
        catch (InterruptedException e)
        {
            BCPExceptionFactory exceptionFactory = new BCPExceptionFactory();
            BCPException e2 = (BCPException)
                exceptionFactory.getException(InterruptErr, e);
            e2.bind(cmd);
            throw e2;
        }
        catch (IOException e)
        {
            BCPExceptionFactory exceptionFactory =
                new BCPExceptionFactory();
            BCPException e2 = (BCPException)
                exceptionFactory.getException(IOErr, e);
            e2.bind(cmd);
            throw e2;
        }
        // The RunCommand executed without exception although the exit code
        // may still indicate an error has occurred. Log the contents of
        // standard out and standard error
        String msgErr = null;
        String msgOut = null;
        if ((msgErr = runner.getStdErr()) != null)
            logger.logInfo(msgErr);
        if ((msgOut = runner.getStdOut()) != null)
        {
            if (logger != null)
                logger.logInfo(msgOut);
        }
        // exit code of non-zero indicates an error occurred while running
        // bcp.
        if (exitCode != 0)
        {
            BCPExceptionFactory exceptionFactory = new BCPExceptionFactory();
            BCPException e2 = (BCPException)
                exceptionFactory.getException(NonZeroErr);
            e2.bind(cmd);
            throw e2;
        }
    }
}