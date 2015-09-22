package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.dao.DAO;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.dbutils.ResultsNavigator;

/**
 * @is An object that knows how to look up the MGI_dbinfo record for a given
 *     database.
 * @has Nothing
 * @does Provides a method to look up the MGI_dbinfo record.
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MGI_dbinfoLookup
{
    private SQLDataManager sqlMgr;
    private DAO dao;

    /**
     * Constructs a MGIdbinfoLookup object for a given database
     * @assumes Nothing
     * @effects Queries a database
     * @param database MGDDBConstants value for a database
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public MGI_dbinfoLookup (String database)
        throws ConfigException, DBException {
        System.out.println("MGI_dbinfoLookup database is " + database);
        sqlMgr = SQLDataManagerFactory.getShared(database);
        String query = "SELECT * FROM MGI_dbinfo";
        if (database.equals(SchemaConstants.MGD)) {
            org.jax.mgi.dbs.mgd.dao.MGI_dbinfoLookup lookup = new org.jax.mgi.
                   dbs.mgd.dao.MGI_dbinfoLookup(sqlMgr);
               ResultsNavigator nav = lookup.findAll();
               if (nav.next()) {
                   dao = (org.jax.mgi.dbs.mgd.dao.MGI_dbinfoDAO) nav.getCurrent();
               }
        }
        else if (database.equals(SchemaConstants.SNP)) {
            org.jax.mgi.dbs.snp.dao.MGI_dbinfoLookup lookup = new org.jax.mgi.
                dbs.snp.dao.MGI_dbinfoLookup(sqlMgr);
            ResultsNavigator nav = lookup.findAll();
            if (nav.next()) {
                dao = (org.jax.mgi.dbs.snp.dao.MGI_dbinfoDAO) nav.getCurrent();
            }
        }
        else {
            // throw exception
        }
    }

    /**
     * Looks up the MGI_dbinfoDAO object
     * @assumes Nothing
     * @effects Nothing
     * @return A MGI_dbinfoDAO object for a database
     */
    public DAO lookup (){
        return dao;
    }
}
