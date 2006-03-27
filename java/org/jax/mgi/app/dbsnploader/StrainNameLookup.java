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

import java.lang.Integer;


/**
 * @is An object that looks up the strain name given a strain key
 * @has A query to _Strain_key/strain pairs
 * @does Provides a method to look up strain hame given a _Strain_Key
 * @company The Jackson Laboratory
 * @author sc
 */

public class StrainNameLookup extends FullCachedLookup
{
    /**
     * Constructs a StrainNameLookup object.
     * @assumes Nothing
     * @effects Nothing
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public StrainNameLookup ()
        throws CacheException, ConfigException, DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
    }


    /**
     * Looks up strain key to get its strain name
     * @assumes Nothing
     * @effects Nothing
     * @param popId Population Id.
     * @return A String object containing the strain name
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws DBException thrown if there is an error accessing the
     * database
     * @throws KeyNotFoundException thrown if the key is not found
     */
    public String lookup (Integer key)
        throws KeyNotFoundException, DBException, CacheException {
        return (String)super.lookup(key);
        //return new Integer((String)super.lookup(key));
    }

    /**
     * Get the query to fully initialize the cache.
     * @assumes Nothing
     * @effects Nothing
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery () {
        return new String("SELECT _Strain_key, strain " +
                          "FROM PRB_Strain");
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
                return new KeyValue(row.getInt(1), row.getString(2));
            }
        }
        return new Interpreter();
    }
}
