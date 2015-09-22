//  $Header
//  $Name
package org.jax.mgi.dbs.snp.lookup;

import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.dbs.SchemaConstants;

//DEBUG
import org.jax.mgi.shr.dbutils.SQLDataManager;

/**
 * @is An object that knows how to look up an accession id in the 'snp'
 *      database for a given MGI Type
 *     and logicalDB value to get its _Object_key
 * @has A query to lookup an _Object_key for an accession id.<BR>
 *      A logicalDB and MGIType.
 * @does
 *   <UL>
 *   <LI> Provides a method to look up the _Object_key for an accession id
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class SNPAccessionLookup extends FullCachedLookup {
    private int logicalDBKey;
    private int mgiTypeKey;

    /**
     * Constructs an SNPAccessionLookup object.
     * @assumes Nothing
     * @effects Nothing
     * @param logicalDBKey the logical db of the accession id to look up
     * @param mgiTypeKey the MGI type of the object key to return
     * @throws CacheException
     * @throws ConfigException
     * @throws DBException
     */

    public SNPAccessionLookup(int logicalDBKey, int mgiTypeKey)
        throws CacheException, ConfigException, DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.SNP));
        //DEBUG
        SQLDataManager sqlDM = SQLDataManagerFactory.getShared(SchemaConstants.SNP);
        System.out.println("SNPAccessionLookup server: " + sqlDM.getServer());
        System.out.println("SNPAccessionLookup database: " + sqlDM.getDatabase());
        this.logicalDBKey = logicalDBKey;
        this.mgiTypeKey = mgiTypeKey;
    }

    /**
     * Get the query to fully initialize the cache.
     * @assumes Nothing
     * @effects Nothing
     * @return the query to fully initialize the cache
     * @throws Nothing
     */

    public String getFullInitQuery() {
        String query = "select accID, _Object_key " +
                        "from SNP_Accession " +
                        "where _LogicalDB_key = " + logicalDBKey +
                        " and " + "_MGIType_key = " + mgiTypeKey;
          return query;
    }

    /**
    * Get a RowDataInterpreter for creating a KeyValue object from a database
    * used for creating a new cache entry.
    * @assumes nothing
    * @effects nothing
    * @return A RowDataInterpreter object
    * @throws Nothing
    */

   public RowDataInterpreter getRowDataInterpreter() {
       class Interpreter implements RowDataInterpreter
       {
           public Object interpret (RowReference row)
               throws DBException {
               return new KeyValue(row.getString(1), row.getInt(2));
           }
       }
       return new Interpreter();
}

    /**
    * Looks up an snp accession id to find its object key.
    * @assumes Nothing
    * @effects Nothing
    * @param accid the snp accession id to look up.
    * @return The _object_key for that snp accession id
    * @throws DBException thrown if there is an error accessing the database
    * @throws CacheException thrown if there is an error accessing the cache
    * @throws KeyNotFoundException thrown if there is the key is not found
    */
    public Integer lookup (String accid)
        throws DBException, CacheException, KeyNotFoundException{
        return (Integer)super.lookupNullsOk(accid);
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
