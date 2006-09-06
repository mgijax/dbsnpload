package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.dao.DAO;
import org.jax.mgi.dbs.SchemaConstants;

import java.sql.Timestamp;
import java.util.Date;

/**
 * An object that updates the snp_data_version and modification_date of an
 *  MGI_dbinfoState object
 * @has
 *   <UL>
 *   <LI>MGI_dbinfoLookup to lookup the existing record in a database
 *   <LI>a database name
 *   <LI>date with which to update modification_date column
 *   <LI>SNP Build Number with which to update snp_data_version column
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

public class MGI_dbinfoUpdater {

    // gets the existing MGI_dbinfo record in a database
    private MGI_dbinfoLookup lookup;

    // database in which MGI_dbinfo will be updated
    private String database;

    // current time to update modification date
    private Timestamp modDate;

    /**
     * constructs a MGI_dbinfoUpdater for a given given database
     * @param database name of the database to update
     * @effects Queries a database
     * @throws DBException if error creating MGI_dbinfoLookup
     * @throws ConfigException if configuration error creating MGI_dbinfoLookup
     */

    public MGI_dbinfoUpdater(String database) throws
        DBException, ConfigException {
        this .database = database;
        lookup = new MGI_dbinfoLookup(database);
        Timestamp modDate = new Timestamp(new Date().getTime());
    }

    /**
     * Creates a MGI_dbinfoDAO from a database then updates its
     * snp_data_version and modification_date attributes
     * @param snpDataVersion new snp data version
     * @return updated MGI_dbinfoDAO object
     */
    public DAO update(String snpDataVersion) {

               DAO dao = lookup.lookup();
               if(database.equals(SchemaConstants.MGD)) {
                  org.jax.mgi.dbs.mgd.dao.MGI_dbinfoState state =
                      (( org.jax.mgi.dbs.mgd.dao.MGI_dbinfoDAO)dao).getState();
                  state.setSnpDataVersion(snpDataVersion);
                  state.setModificationDate(modDate);
               }
               else if (database.equals(SchemaConstants.SNPBE)) {
                   org.jax.mgi.dbs.snp.dao.MGI_dbinfoState state =
                       (( org.jax.mgi.dbs.snp.dao.MGI_dbinfoDAO)dao).getState();
                   state.setSnpDataVersion(snpDataVersion);
                   state.setModificationDate(modDate);
               }
               // unsupported database
               else {
                   // throw an exception
               }
               return dao;
    }
}
