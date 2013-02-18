package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBException;

/**
 * A SQLStream for doing inserts, updates and deletes with inline sql
 * @has a InlineSQLStrategy for performing updates, inserts and deletes
 * @does inserts, updates and deletes DAO objects onto an SQLStream
 * @company The Jackson Laboratory
 * @author M Walker
 */
public class Inline_Stream
    extends SQLStream
{
    /**
     * constructor
     * @assumes nothing
     * @effects nothing
     * @param sqlMgr the SQLDataManager to use
     */
    public Inline_Stream(SQLDataManager sqlMgr)
    {
        super();
        InlineStrategy inlineStrategy = new InlineStrategy(sqlMgr);
        super.setInsertStrategy(inlineStrategy);
        super.setUpdateStrategy(inlineStrategy);
        super.setDeleteStrategy(inlineStrategy);
    }

    /**
     * does nothing since all sql is executied in realtime using inline sql
     * @assumes nothing
     * @effects nothing
     */
    public void close()
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
