//  $Header
package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.dao.DAO;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.dbs.SchemaConstants;
import java.util.HashMap;

/**
 * @is An object that knows how to look up the MGI_Tables record for a given
 *     database.
 * @has A database name, a Mapping of the table name to a MGI_TablesDAO for
 * each table in MGI_Tables for this database
 * @does Provides a method to look up the MGI_Tables record.
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MGI_TablesLookup {

    // key=tablename, value = MGI_TableDAO object for the database used by this
    // Lookup
    private HashMap daos = new HashMap();

    /**
     * Constructs a MGI_TablesLookup object for a given database
     * @assumes Nothing
     * @effects Queries a database
     * @param database MGDDBConstants value for a database
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public MGI_TablesLookup (String database)
        throws ConfigException, DBException {
        ResultsNavigator nav;
        SQLDataManager sqlMgr = SQLDataManagerFactory.getShared(database);

        if (database.equals(SchemaConstants.MGD)) {
            nav = new org.jax.mgi.dbs.mgd.dao.MGI_TablesLookup(
                sqlMgr).findAll();
            while(nav.next()) {
                org.jax.mgi.dbs.mgd.dao.MGI_TablesDAO mgdDao =
                    (org.jax.mgi.dbs.mgd.dao.MGI_TablesDAO ) nav.getCurrent();
                daos.put(mgdDao.getState().getTableName(), mgdDao);
            }
        }
        else if (database.equals(SchemaConstants.SNP)) {
            nav = new org.jax.mgi.dbs.snp.dao.MGI_TablesLookup(
                sqlMgr).findAll();
            while(nav.next()) {
                org.jax.mgi.dbs.snp.dao.MGI_TablesDAO snpDao =
                    (org.jax.mgi.dbs.snp.dao.MGI_TablesDAO ) nav.getCurrent();
                daos.put(snpDao.getState().getTableName(), snpDao);
            }
        }
        else {
            // throw exception
        }
    }

    /**
     *
     * @param tableName name of table for which to get an MGI_TablesDAO object
     * @return DAO(MGI_TablesDAO) object from the database
     * used by this Lookup for 'tableName'
     */
    public DAO lookup(String tableName) {
        return (DAO)daos.get(tableName);
    }
}


//  $Log
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
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
