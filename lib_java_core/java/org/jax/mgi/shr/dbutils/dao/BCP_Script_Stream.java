package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.shr.dbutils.ScriptException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;

/**
 * A SQLStream for doing inserts with bcp and doing updates and deletes with
 * scripting
 * @has an ScriptWriter for performing updates and deletes and a
 * BCPStrategy for perfoming inserts
 * @does provides a set of update, insert and delete strategies for updating
 * a given DAO object in a database
 * @company The Jackson Laboratory
 * @author M Walker
 */
public class BCP_Script_Stream
    extends BCP_Stream
{
    /**
     * the ScriptWriter to use
     */
    protected ScriptWriter writer = null;

    // the following constant defintions are exceptions thrown by this class
    protected static final String ExecuteScriptErr =
        DBExceptionFactory.ExecuteScriptErr;

    /**
     * constructor
     * @param writer the ScriptWriter to use
     * @param bcpMgr the BCPManager to use
     * @throws DBException thrown if there is an error accessing the database
     */
    public BCP_Script_Stream(ScriptWriter writer, SQLDataManager sqlMgr,
                             BCPManager bcpMgr)
        throws
        DBException
    {
        super(sqlMgr, bcpMgr);
        this.writer = writer;
        ScriptStrategy scriptStrategy = new ScriptStrategy(writer);
        super.setUpdateStrategy(scriptStrategy);
        super.setDeleteStrategy(scriptStrategy);
    }

    /**
     * execute the bcp commands followed by the batch statements
     * @assumes nothing
     * @effects data will be updated in bulk withing the database
     * @throws DBException thrown if there is an error accessing the database
     */
    public void close()
        throws DBException
    {
        super.close();
        try
        {
            writer.execute();
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
