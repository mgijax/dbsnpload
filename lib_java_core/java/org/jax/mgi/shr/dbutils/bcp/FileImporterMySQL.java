package org.jax.mgi.shr.dbutils.bcp;

import java.io.File;
import java.io.IOException;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBSchema;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.unix.RunCommand;
import org.jax.mgi.shr.log.Logger;

/**
 * This class imports data from a file into a MySQL database
 * @has nothing
 * @does imports data from a file into a MySQL database
 * @company The Jackson Laboratory
 * @author MWalker
 *
 */

public class FileImporterMySQL
    implements FileImporter
{

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String LoadDataErr = BCPExceptionFactory.LoadDataErr;

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
        File file = new File(filename);
        String fullpath = file.getAbsolutePath();
        fullpath = fullpath.replaceAll("\\\\", "\\\\\\\\");
        String load =
            "load data infile '" + fullpath + "' into table " + tablename +
            " FIELDS TERMINATED BY " + delimiter;
        try
        {
            sqlMgr.executeUpdate(load);
        }
        catch (DBException e)
        {
            BCPExceptionFactory exceptionFactory = new BCPExceptionFactory();
            BCPException e2 = (BCPException)
                exceptionFactory.getException(LoadDataErr, e);
            throw e2;
        }
    }

}