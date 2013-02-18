package org.jax.mgi.shr.dbutils.dao;

import java.util.Vector;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;
import org.jax.mgi.shr.dbutils.bcp.BCPException;
import org.jax.mgi.shr.config.ConfigException;


/**
 * A SQLStream for doing inserts with bcp. Updates and deletes cannot be
 * performed and calling thoe methods will cause an exception to be raised
 * @has a BCPManager for managing the writing and executing of multiple
 * bcp files and a SQLDataManager for performing bcp related database
 * operations
 * @does inserts DAO objects into the database using bcp
 * @company The Jackson Laboratory
 * @author M Walker
 */
public class BCP_Stream
    extends SQLStream
{
    /**
     * the BCPManager class for performing database inserts with bcp
     */
    protected BCPManager bcpManager = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    protected static String SQLStreamCloseErr =
        DBExceptionFactory.SQLStreamCloseErr;

    /**
     * constructor
     * @assumes nothing
     * @effects nothing
     * @param sqlMgr the SQLDataManager to use
     * @param bcpMgr the BCPManager to use
     */
    public BCP_Stream(SQLDataManager sqlMgr, BCPManager bcpMgr)
    {
        super();
        this.bcpManager = bcpMgr;
        BCPStrategy bcpStrategy = new BCPStrategy(sqlMgr, bcpMgr);
        super.setInsertStrategy(bcpStrategy);
    }


    /**
     * initialize all the writers up front in order to assure order
     * when executing them and also to assure key values are properly
     * cached in the Table clases based on the BCPWriter configuration
     * parameter ok_to_truncate_table.
     * @assumes nothing
     * @effects the keys will be reset if the BCPWriters are configured to
     * truncate the tables and the order of executing the BCPWriters will
     * be effected
     * @param v a vector of Table classes in the order in which you want
     * them executed
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the database
     * @throws BCPException thrown if there is an error creating a new
     * BCPWriter
     */
    public void initBCPWriters(Vector v)
    throws ConfigException, DBException, BCPException
    {
        ((BCPStrategy)super.insertStrategy).initBCPWriters(v);
    }


    /**
     * execute the bcp commands
     * @assumes nothing
     * @effects the bcp command is executed
     * @throws DBException thrown if there is an error executing the bcp
     * command
     */
    public void close()
        throws DBException
    {
        try
        {
            bcpManager.executeBCP();
        }
        catch (MGIException e)
        {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)
                eFactory.getException(SQLStreamCloseErr, e);
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
