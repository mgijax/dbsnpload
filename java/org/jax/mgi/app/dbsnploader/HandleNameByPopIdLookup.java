package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
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
 * @is An object that knows how to look up Handle Name given a Population Id
 * @has A query to get all handle name/population id pairs
 * @does Provides a method to look up a Handle Name given a Population Id
 * @company The Jackson Laboratory
 * @author sc
 */

public class HandleNameByPopIdLookup extends FullCachedLookup
{
    /**
     * Constructs a PopulationHandleLookup object.
     * @assumes Nothing
     * @effects May create a connection to a database
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public HandleNameByPopIdLookup ()
        throws CacheException, ConfigException, DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.SNP));
    }


    /**
     * Looks Population Id to find its Handle name.
     * @assumes Nothing
     * @effects queries a database if 'popId' not in the cache
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
        return new String("SELECT a.accid, p.subHandle " +
                          "FROM SNP_Accession a, SNP_Population p " +
                          "WHERE a._LogicalDB_key =  " + LogicalDBConstants.SNPPOPULATION +
                          " and a._MGITYpe_key =  " + MGITypeConstants.SNPPOPULATION +
                          " and a._Object_key = p._Population_key");
    }

    /**
     * Get a RowDataInterpreter for creating a KeyValue object from a database
     * used for creating a new cache entry.
     * @assumes nothing
     * @effects nothing
     * @return The RowDataInterpreter object.
     */
    public RowDataInterpreter getRowDataInterpreter()
    {
        class Interpreter implements RowDataInterpreter
        {
            public Object interpret (RowReference row)
                throws DBException
            {
                return new KeyValue(row.getString(1), row.getString(2));
            }
        }
        return new Interpreter();
    }
}
