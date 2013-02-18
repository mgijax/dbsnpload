
package org.jax.mgi.shr.cache;

import java.util.Map;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;

/**
 * An extension of the RowDataCacheStrategy class that provides
 * a lazy cache strategy for lookups. That is it partially initializes a
 * cache and adds new entries to the cache from the database on lookup if
 * they do not already exist in the cache. This class is used by a
 * LazyCacheLookup object.
 * @has see RowDataCacheStrategy as this extends it to implement a lazy
 * strategy
 * @does executes a partial init query and puts the results in the cache.
 * Looks up keys in the cache and if not found then gets the value from the
 * database and adds it to the cache.
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class LazyCacheStrategy
    extends RowDataCacheStrategy
{

    /**
     * constructor
     * @param dataManager the SQLDataManager
     */
    public LazyCacheStrategy(SQLDataManager dataManager)
    {
        super(dataManager);
    }


    /**
     * constructor
     * @param dataManager the SQLDataManager
     * @param logger the Logger to use.
     */
    public LazyCacheStrategy(SQLDataManager dataManager, Logger logger)
    {
        super(dataManager);
        super.setLogger(logger);
    }


    /**
     * initializes the cache
     * @assumes nothing
     * @effects puts initial objects in the cache
     * @param cache the cache to initialize
     * @throws CacheException thrown if the RowDataCacheHandler for this
     * class does not create the proper KeyValue object required for
     * inserting into the cache
     * @throws DBException thrown if there is an exception with the database
     */
    public void init(Map cache)
        throws CacheException, DBException
    {
        super.hasBeenInitialized = true;
        String sql = super.cacheHandler.getPartialInitQuery();
        if (sql == null)
            return;
        if (super.debug)
            super.logger.logDebug("initializing cache with the following " +
                                  "sql:\n" + sql);
        ResultsNavigator nav = super.dataManager.executeQuery(sql);
        /**
         * The CacheStrategyHelper class is used to navigate through the
         * query results and place objects in the cache
         */
        CacheStrategyHelper.putResultsInMap(nav, cache,
                                            this.cacheHandler,
                                            this.logger, debug);
    }

    /**
     * This method will lookup a value in the given cache for the given key
     * and if it is not found it will look it up in the database and add it
     * to the cache or return null if not found in the database or cache.
     * @assumes nothing
     * @effects nothing
     * @param key the target key to look up in the cache
     * @param cache the cache to look up the value in
     * @return the value found for the given key
     * @throws CacheException thrown if the RowDataCacheHandler for this
     * class does not create the proper KeyValue object required for
     * inserting into the cache
     * @throws DBException thrown if there is an exception with the database
     */
    public Object lookup(Object key, Map cache)
        throws CacheException, DBException
    {
        if (!super.hasBeenInitialized)
        {
            init(cache);
        }
        Object revisedKey = null;
        if (key instanceof String)
            revisedKey = ((String)key).toLowerCase();
        else
            revisedKey = key;
        Object value = cache.get(revisedKey);
        if (value != null)
        {
            if (super.debug)
                super.logger.logDebug("key found in cache: " + key);
            return value;
        }
        // value was not found, see if it is in the database
        String sql = this.cacheHandler.getAddQuery(key);
        if (super.debug)
        {
            super.logger.logDebug("key not found in cache: " + key);
            super.logger.logDebug(
                "looking up key in database with the following sql:\n" +
                sql);
        }
        ResultsNavigator nav = dataManager.executeQuery(sql);
        /**
         * The CacheStrategyHelper class is used to navigate through the
         * query results and place objects in the cache
         */
        CacheStrategyHelper.putResultsInMap(nav, cache,
                                            this.cacheHandler,
                                            this.logger, debug);
        // now retrieve object from cache which can be null if it was not
        // found in the query results
        value = cache.get(revisedKey);
        if (super.debug)
        {
            if (value != null)
            {
                super.logger.logDebug("key found: " + key);
            }
            else
            {
                super.logger.logDebug("key not found: " + key);
            }
        }

        return value;
    }
  }