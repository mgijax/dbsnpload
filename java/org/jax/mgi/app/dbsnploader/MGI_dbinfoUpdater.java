// $Header
// $Name

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
 *   <LI>an instance of itself
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
               else if (database.equals(SchemaConstants.SNP)) {
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
// $Log
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
