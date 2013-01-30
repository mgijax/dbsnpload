package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.BatchProcessor;
import org.jax.mgi.shr.dbutils.BatchException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;

/**
 * A class which implements the InsertStrategy, UpdateStrategy,
 * and DeleteStrategy using JDBC batch
 * @has a BatchProcessor.
 * @does extracts SQL from a DAO object and executes the SQL in batch.
 * @author M Walker
 */
public class BatchStrategy
    implements DeleteStrategy, InsertStrategy, UpdateStrategy
{

    /**
     * the BatchProcessor used for executing batch sql
     */
    private BatchProcessor batch = null;

    private static final String DAOErr =
      DBExceptionFactory.DAOErr;


    /**
     * constructor
     * @param batch the BatchProcessor to use
     */
    public BatchStrategy(BatchProcessor batch)
    {
        this.batch = batch;
    }

    /**
     * update the given DAO object
     * @assumes nothing
     * @effects the given DAO will be batched up for update into the
     * database
     * @param dao the dao object to update in the database
     * @throws DBException thrown if there is an error with the database
     */
    public void update(DAO dao)
        throws DBException
    {
        String sql = ( (SQLTranslatable) dao).getUpdateSQL();
        try
        {
            batch.addBatch(SQLStrategyHelper.convertToJDBCProc(sql));
        }
        catch (BatchException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)eFactory.getException(DAOErr, e);
            e2.bind(this.getClass().getName());
            throw e2;
        }
    }

    /**
     * delete the given DAO object
     * @assumes nothing
     * @effects the given DAO will be batched up for a delete from the
     * database
     * @param dao the dao object to delete
     * @throws DBException thrown if there is an error accessing the database
     */
    public void delete(DAO dao)
        throws DBException
    {
        String sql = ( (SQLTranslatable) dao).getDeleteSQL();
        try
        {
            batch.addBatch(SQLStrategyHelper.convertToJDBCProc(sql));
        }
        catch (BatchException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)eFactory.getException(DAOErr, e);
            e2.bind(this.getClass().getName());
            throw e2;
        }
    }

    /**
     * insert the given DAO object
     * @assumes nothing
     * @effects the given DAO will be batched up for insert into the
     * database
     * @param dao the dao object to insert
     * @throws DBException thrown if there is an error accessing the database
     */
    public void insert(DAO dao)
        throws DBException
    {
        String sql = ( (SQLTranslatable) dao).getDeleteSQL();
        try
        {
            batch.addBatch(SQLStrategyHelper.convertToJDBCProc(sql));
        }
        catch (BatchException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)eFactory.getException(DAOErr, e);
            e2.bind(this.getClass().getName());
            throw e2;
        }

    }
}

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
