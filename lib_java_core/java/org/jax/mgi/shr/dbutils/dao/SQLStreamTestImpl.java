package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.BatchProcessor;
import org.jax.mgi.shr.dbutils.ScriptWriter;

/**
 * This class is a testing implementation of the SQLStream class which is used
 * for testing SQLStream functionality
 * @has
 * @does
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public class SQLStreamTestImpl extends SQLStream
{

    private SQLDataManager sqlMgr = null;
    private BatchProcessor batch = null;

    /**
     * constructor
     * @param sqlMgr SQLDataManager instance
     * @throws DBException thrown if there is an error with the database
     */
    public SQLStreamTestImpl(SQLDataManager sqlMgr,
                             ScriptWriter scriptWriter) throws DBException
    {
        super();
        this.batch = sqlMgr.getBatchProcessor();
        BatchStrategy batchStrategy = new BatchStrategy(batch);
        InlineStrategy inlineStrategy = new InlineStrategy(sqlMgr);
        ScriptStrategy script = new ScriptStrategy(scriptWriter);
        super.setUpdateStrategy(batchStrategy);
        super.setInsertStrategy(inlineStrategy);
        super.setDeleteStrategy(script);
    }

    /**
     * close the SQLStream
     * @throws DBException thrown if there is an error with the database
     */
    public void close() throws DBException
    {

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
