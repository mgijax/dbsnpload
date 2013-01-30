package org.jax.mgi.shr.dbutils;

import java.sql.Statement;
import java.sql.SQLException;
import java.sql.BatchUpdateException;
import java.util.Vector;

import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.exception.MGIException;

/**
 * A class for handling jdbc batch execution
 * @has a Statement class
 * @does stores a batch of sql statements and executes them all with one
 * network call to the database
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class BatchProcessor
{

    /**
     * the Statement class which is used for performing batch
     */
    private Statement statement = null;
    /**
     * indicator of whether or not resources have been closed
     */
    private boolean isClosed = false;
    /**
     * the collection of sql statements being executed
     */
    private Vector history = new Vector();

    /**
     * the logger to use for error reporting
     */
    private Logger logger = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String BatchNotSupported =
        BatchExceptionFactory.BatchNotSupported;
    private static String BatchCommandFailed =
        BatchExceptionFactory.BatchCommandFailed;
    private static String BatchComplete =
        BatchExceptionFactory.BatchComplete;
    private static String AddBatchErr =
        BatchExceptionFactory.AddBatchErr;
    private static String CloseErr =
        BatchExceptionFactory.CloseErr;
    private static String FileIO =
        BatchExceptionFactory.FileIO;


    /**
     * constructor
     * @assumes nothing
     * @effects nothing
     * @param statement the JDBC Statement class used for handling batch sql
     */
    protected BatchProcessor(Statement statement)
    {
        this.statement = statement;
    }

    /**
     * override the logger to use for error reporting which is initialized to
     * the same logger from the SQLDataManager this instance was derived from
     * @assumes nothing
     * @effects all subsequent logging will use the given Logger
     * @param logger the Logger
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * add a sql string to the batch
     * @assumes nothing
     * @effects adds the given string the batch of sql statements
     * @param sql the sql string
     * @throws BatchException thrown if there is a database processing error
     */
    public void addBatch(String sql)
        throws BatchException
    {
        if (isClosed)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e = (BatchException)
                eFactory.getException(BatchComplete);
            throw e;
        }
        try
        {
            statement.addBatch(sql);
        }
        catch (SQLException e)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e2 = (BatchException)
                eFactory.getException(AddBatchErr, e);
            e2.bind(sql);
            throw e2;
        }
        history.add(sql);
    }

    /**
     * adds batch statements for the sql found in the given file
     * @param filename the file name
     * @throws BatchException thrown if there is an error accessing the file
     * or an error accessing the database
     *
     */
    public void addScriptBatch(String filename) throws BatchException
    {
        InputDataFile file = null;
        RecordDataIterator it = null;

        if (isClosed)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e = (BatchException)
                eFactory.getException(BatchComplete);
            throw e;
        }

        try
        {
            file = new InputDataFile(filename);
            file.setOkToUseRegex(true);
            file.setEndDelimiter("^/n");
            it = file.getIterator();
            while (it.hasNext())
            {
                String sql = (String)it.next();
                statement.addBatch(sql);
                history.add(sql);
            }
        }
        catch (MGIException e)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e2 = (BatchException)
                eFactory.getException(FileIO, e);
            e2.bind(filename);
            throw e2;
        }
        catch (SQLException e)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e2 = (BatchException)
                eFactory.getException(AddBatchErr, e);
            throw e2;

        }
    }

    /**
     * execute the batch of sql
     * @assumes nothing
     * @effects nothing
     * @return the array of counts for each update from the batch
     * @throws DBException thrown if there is an error in the database
     * @throws BatchException thrown if there is a batch exception
     */
    public int[] executeBatch()
        throws DBException, BatchException
    {
        if (isClosed)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e = (BatchException)
                eFactory.getException(BatchComplete);
            throw e;
        }
        try
        {
            int[] results = statement.executeBatch();
            if (logger != null)
            {
                logger.logInfo("The Batch Processor completed sucessfully");
                logger.logInfo(history.size() +
                               " sql statements were run\n0 failed");
            }
            return results;
        }
        catch (BatchUpdateException e)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e2 = (BatchException)
                eFactory.getException(BatchCommandFailed, e);
            int[] updateCounts = e.getUpdateCounts();
            e2.setUpdateCounts(updateCounts);
            int failedCount = 0;
            int totalCount = updateCounts.length;
            if (logger != null)
            {
                logger.logError(
                    "The following is a list of all sql statements that " +
                    "failed for this run");
            }
            for (int i = 0; i < totalCount; i++)
            {
                switch (updateCounts[i])
                {
                    case BatchException.ERROR:
                        if (logger != null)
                        {
                            logger.logError((String)history.get(i));
                        }
                        failedCount++;
                }
            }
            if (logger != null)
            {
                logger.logInfo("The Batch Processor completed sucessfully");
                logger.logError(totalCount + " sql statements were run\n" +
                                failedCount + " failed");
            }
            throw e2;
        }
        catch (SQLException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)
                eFactory.getException(BatchNotSupported, e);
            throw e2;
        }

    }

    /**
     * close resources for this processor
     * @assumes nothing
     * @effects the resources are freed and the processor will no longer
     * be available for operations
     * @throws BatchException thrown if there is a database processing error
     */
    public void close()
        throws BatchException
    {
        try
        {
            this.statement.close();
        }
        catch (SQLException e)
        {
            BatchExceptionFactory eFactory = new BatchExceptionFactory();
            BatchException e2 = (BatchException)
                eFactory.getException(CloseErr, e);
            throw e2;
        }
        history = null;
        isClosed = true;
    }

}