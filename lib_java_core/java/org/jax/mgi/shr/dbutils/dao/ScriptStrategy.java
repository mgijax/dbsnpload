package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.shr.dbutils.ScriptException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;

/**
 * A class which implements the InsertStrategy, UpdateStrategy,
 * and DeleteStrategy using a scripting strategy
 * @has a ScriptWriter.
 * @does casts the given DAO to a SQLTranslatable from which to
 * obtain the sql and executes the sql for either insert, update or
 * delete by using a database script.
 * @author M Walker
 */


public class ScriptStrategy
    implements DeleteStrategy, InsertStrategy, UpdateStrategy
{

    private ScriptWriter writer = null;

    // the following constant defintions are exceptions thrown by this class
    private static final String ExecuteScriptErr =
        DBExceptionFactory.ExecuteScriptErr;

    /**
     * constructor
     * @param writer the ScriptWriter to use
     */
    public ScriptStrategy(ScriptWriter writer)
    {
        this.writer = writer;
    }

    /**
     * update the given DAO object
     * @assumes nothing
     * @effects the given DAO will be batched up for update into the
     * database
     * @param dao the object to update
     * @throws DBException thrown if there is an error accessing the database
     */
    public void update(DAO dao)
        throws DBException
    {
        String sql = ( (SQLTranslatable) dao).getUpdateSQL();
        sql = SQLStrategyHelper.convertToOtherProc(sql);
        try
        {
            writer.write(sql);
            writer.go();
        }
        catch (ScriptException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)
                eFactory.getException(ExecuteScriptErr, e);
            throw e2;
        }
    }

    /**
     * delete the given DAO object
     * @assumes nothing
     * @effects the given DAO will be batched up for delete from the
     * database
     * @param dao the object to delete
     * @throws DBException thrown if there is an error accessing the database
     */
    public void delete(DAO dao)
        throws DBException
    {
        String sql = ( (SQLTranslatable) dao).getDeleteSQL();
        sql = SQLStrategyHelper.convertToOtherProc(sql);
        try
        {
            writer.write(sql);
            writer.go();
        }
        catch (ScriptException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)
                eFactory.getException(ExecuteScriptErr, e);
            throw e2;
        }
    }

    /**
     * insert the given DAO object
     * @assumes nothing
     * @effects the given DAO will be batched up for insert into the
     * database
     * @param dao the object to insert
     * @throws DBException thrown if there is an error accessing the database
     */
    public void insert(DAO dao)
        throws DBException
    {
        String sql = ( (SQLTranslatable) dao).getInsertSQL();
        sql = SQLStrategyHelper.convertToOtherProc(sql);
        try
        {
            writer.write(sql);
            writer.go();
        }
        catch (ScriptException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)
                eFactory.getException(ExecuteScriptErr, e);
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