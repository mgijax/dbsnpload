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
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;


/**
 * @is An object that looks up a population name by its population id
 * @has A query to get all population name/population id pairs
 * @does Provides a method to look up a population name given a population id
 * @company The Jackson Laboratory
 * @author sc
 */

public class PopNameByPopIdLookup extends FullCachedLookup
{
    /**
     * Constructs a PopNameByPopIdLookup object.
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
        super(SQLDataManagerFactory.getShared(SchemaConstants.SNPBE));
    }


    /**
     * Looks up Population Id to find its Handle name.
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
                          "FROM SNP_Accession a, SNP_Population p " +
                          "WHERE a._LogicalDB_key =  " + LogicalDBConstants.SNPPOPULATION +
                          " and a._MGITYpe_key = " + MGITypeConstants.SNPPOPULATION +
                          " and a._Object_key = p._Population_key");
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
