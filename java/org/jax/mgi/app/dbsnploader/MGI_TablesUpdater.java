package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dbutils.dao.DAO;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.lookup.MGIUserKeyLookup;

import java.sql.Timestamp;
import java.util.Date;

/**
 * An object that updates modification_date of an
 *  MGI_dbinfoState object
 * @has
 *   <UL>
 *   <LI>Name of the process, 'loaded_by', using this Updater
 *   <LI>Name of the database we are updating
 *   <LI>date with which to update MGI_Tables modification_date and loaded_date
 *   <LI>user (loaded_by) with which to update MGI_Tables loaded_by
 *   <LI>MGI_TablesLookup to lookup the existing record in a database
 *   <LI>MGIUserKeyLookup to resolve 'loaded_by'
 *   </UL>
 * @does
 *   <UL>
 *   <LI>>Creates and Updates  the snp_data_version and modification_date of an
 *        MGI_dbinfoState object
 *   <UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class MGI_TablesUpdater {

    // Looks up an existing MGI_Table object
    private MGI_TablesLookup tableDAOLookup;

    // Looks up an MGI_User login to get its key
    MGIUserKeyLookup userKeyLookup;

    // _loadedBy_key with which to update MGI_Tables records
    Integer loadedByKey;

    // database in which MGI_Table objects will be updated
    private String database;

    // current time to update modification date
    private Timestamp currentDate;

    /**
     * constructs a MGI_TablesUpdater for a given given database
     * @param database the db in which to do the update
     * @param loadedBy the MGI_User name of the process doing the update
     * @effects Queries a database
     * @throws DBException if error creating MGI_TablesLookup
     * @throws ConfigException if configuration error creating MGI_TablesLookup or MGIUserLookup
     * @throws CacheException
     * @throws KeyNotFoundException
     */

    public MGI_TablesUpdater(String database, String loadedBy) throws
        DBException, ConfigException, CacheException, KeyNotFoundException {
        this .database = database;
        tableDAOLookup = new MGI_TablesLookup(database);
        userKeyLookup= new MGIUserKeyLookup();
        loadedByKey = userKeyLookup.lookup(loadedBy);
        currentDate = new Timestamp(new Date().getTime());
    }

    /**
     * Creates MGI_TablesDAO for 'tableName' from the current database
     * then updates _LoadedBy_key,  modification_date, and loaded_date attributes
     * @param tableName name of the table for which to update its MGI_Tables record
     * @return updated MGI_TablesDAO object
     */
    public DAO update(String tableName) {

        DAO dao = tableDAOLookup.lookup(tableName);

        if(database.equals(SchemaConstants.MGD)) {
            org.jax.mgi.dbs.mgd.dao.MGI_TablesState state =
                (( org.jax.mgi.dbs.mgd.dao.MGI_TablesDAO)dao).getState();
            state.setLoadedByKey(loadedByKey);
            state.setModificationDate(currentDate);
            state.setLoadedDate(currentDate);
        }
        else if (database.equals(SchemaConstants.SNPBE)) {
            org.jax.mgi.dbs.snp.dao.MGI_TablesState state =
                (( org.jax.mgi.dbs.snp.dao.MGI_TablesDAO)dao).getState();
            state.setLoadedByKey(loadedByKey);
            state.setModificationDate(currentDate);
            state.setLoadedDate(currentDate);
        }
        // unsupported database
        else {
            // throw an exception
        }
        return dao;
    }
}
