package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;

/**
 * @is An object that knows how to look up a MGI Type.
 * @has Nothing
 * @does
 *   <UL>
 *   <LI> Provides a method to look up a MGI Type.
 *   </UL>
 * @company The Jackson Laboratory
 * @author dbm
 * @version 1.0
 */

public class PopNameByPopIdLookup extends FullCachedLookup
{
    /**
     * Constructs a PopulationHandleLookup object.
     * @assumes Nothing
     * @effects Nothing
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public PopNameByPopIdLookup ()
        throws CacheException, ConfigException, DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
    }


    /**
     * Looks Population Id to find its Handle name.
     * @assumes Nothing
     * @effects Nothing
     * @param popId Population Id.
     * @return A String object containing the Handle name for popId
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws DBException thrown if there is an error accessing the
     * database
     * @throws KeyNotFoundException thrown if the key is not found
     */
    public String lookup (String popId)
        throws KeyNotFoundException, DBException, CacheException {
        return (String)super.lookup(popId);
    }

    /**
     * Get the query to fully initialize the cache.
     * @assumes Nothing
     * @effects Nothing
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery () {
        return new String("SELECT a.accid, p.name " +
                          "FROM ACC_Accession a, SNP_Population p " +
                          "WHERE a._LogicalDB_key = 76 " +
                          "and a._MGITYpe_key = 33 " +
                          "and a._Object_key = p._Population_key");
    }

    /**
     * Get a RowDataInterpreter for creating a KeyValue object from a database
     * used for creating a new cache entry.
     * @assumes nothing
     * @effects nothing
     * @return The RowDataInterpreter object.
     */
    public RowDataInterpreter getRowDataInterpreter() {
        class Interpreter implements RowDataInterpreter {
            public Object interpret (RowReference row)
                throws DBException {
                return new KeyValue(row.getString(1), row.getString(2));
            }
        }
        return new Interpreter();
    }
}
