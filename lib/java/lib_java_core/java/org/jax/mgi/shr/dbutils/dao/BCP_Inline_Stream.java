package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;

/**
 * A SQLStream for doing inserts with bcp and doing updates and deletes with
 * inline sql
 * @has an InlineSQLStrategy for performing updates and deletes and a
 * BCPStrategy for perfoming inserts
 * @does provides a set of update, insert and delete strategies for updating
 * a given DAO object in a database
 * @company The Jackson Laboratory
 * @author M Walker
 */
public class BCP_Inline_Stream
    extends BCP_Stream
{

    /**
     * constructor
     * @assumes nothing
     * @effects nothing
     * @param sqlMgr the SQLDataManager to use
     * @param bcpMgr the BCPManager to use
     */
    public BCP_Inline_Stream(SQLDataManager sqlMgr, BCPManager bcpMgr)
    {
        super(sqlMgr, bcpMgr);
        InlineStrategy inlineStrategy = new InlineStrategy(sqlMgr);
        super.setUpdateStrategy(inlineStrategy);
        super.setDeleteStrategy(inlineStrategy);
    }

    /**
     * execute the bcp commands
     * @assumes nothing
     * @effects the bcp command is executed for all BCPWriters created
     * through the BCPManager
     * @throws DBException thrown if there is an error executing the bcp
     * command
     */
    public void close()
        throws DBException
    {
        super.close();
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
