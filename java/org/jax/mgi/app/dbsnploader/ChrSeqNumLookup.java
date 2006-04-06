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

import java.lang.Integer;

/**
 * @is An object that looks up  the sequenceNum of a chromosome
 * given a _Chromosome_Key
 * @has A query to get _Chromosome_Key/sequenceNum pairs
 * @does Provides a method to look up sequenceNum given a _Chromosome_Key
 * @company The Jackson Laboratory
 * @author sc
 */

public class ChrSeqNumLookup extends FullCachedLookup
{
    /**
     * Constructs a ChromosomeSeqNumLookup object.
     * @assumes Nothing
     * @effects Nothing
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws ConfigException thrown if there is an error accessing the
     * configuration
     * @throws DBException thrown if there is an error accessing the
     * database
     */
    public ChrSeqNumLookup ()
        throws CacheException, ConfigException, DBException {
        super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
    }


    /**
     * Looks up a _Chromosome_key to get its sequenceNum.
     * @assumes Nothing
     * @effects Nothing
     * @param key _Chromosome_key to lookup
     * @return An Integer, the sequenceNum for the given _Chromosome_key
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws DBException thrown if there is an error accessing the database
     * @throws KeyNotFoundException thrown if the key is not found
     */
    public Integer lookup (Integer key)
        throws KeyNotFoundException, DBException, CacheException {
        return (Integer)super.lookup(key);
    }

    /**
     * Get the query to fully initialize the cache.
     * @assumes Nothing
     * @effects Nothing
     * @return The query to fully initialize the cache.
     */
    public String getFullInitQuery () {
        return new String("SELECT _Chromosome_Key, sequenceNum " +
                          "FROM MRK_Chromosome");
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
                return new KeyValue(row.getInt(1), row.getInt(2));
            }
        }
        return new Interpreter();
    }
}
