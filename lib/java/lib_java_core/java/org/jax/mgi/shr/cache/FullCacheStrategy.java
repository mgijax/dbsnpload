package org.jax.mgi.shr.cache;

import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.QuerySeries;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;
import org.jax.mgi.shr.config.LogCfg;
import org.jax.mgi.shr.config.ConfigException;

/**
 * An extension of the RowDataCacheStrategy class that provides a full
 * cache strategy for lookups. That is it fully initializes a cache to
 * provide all the data required for subsequent lookups. It does not add new
 * entries after the initialization. If a value is not found on
 * lookup, no additional searches on the database are made.
 * @has see RowDataCacheStrategy as this is an extension of that class
 * @does executes a full init query and puts the results in the cache. Looks
 * up keys in the cache and returns null if not found.
 * @company The Jackson Laboratory
 * @author M Walker
 */
public class FullCacheStrategy
    extends RowDataCacheStrategy
{
    /*
     * the following are constants for exceptions thrown by this class
     */
    private static final String InitializationErr =
        CacheExceptionFactory.InitializationErr;
    private static final String ConfigErr =
        CacheExceptionFactory.ConfigErr;

    /**
     * constructor
     * @param dataManager the SQLDataManager
     */
    public FullCacheStrategy(SQLDataManager dataManager)
    {
        super(dataManager);
    }

    /**
     * constructor
     * @param dataManager the SQLDataManager
     * @param logger the Logger to use.
     */
    public FullCacheStrategy(SQLDataManager dataManager, Logger logger)
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
     * @throws DBException thorwn if there is an exception with the database
     */
    public void init(Map cache)
        throws CacheException, DBException
    {
        super.hasBeenInitialized = true;
        String sql = super.cacheHandler.getFullInitQuery();
        if (sql == null)
        {
            CacheExceptionFactory eFactory = new CacheExceptionFactory();
            CacheException e =
                (CacheException) eFactory.getException(InitializationErr);
            throw e;
        }
        if (super.debug)
            super.logger.logDebug("initializing cache with the following " +
                                  "sql:\n" + sql);
        if (super.cacheHandler.inClause != null)
        {
            QuerySeries series = null;
            try
            {
                series =
                    super.dataManager.buildInClauseQuery(sql,
                    super.cacheHandler.inClause.columnName,
                    super.cacheHandler.inClause.columnValues);
            }
            catch (ConfigException e)
            {
                CacheExceptionFactory eFactory = new CacheExceptionFactory();
                CacheException e2 =
                    (CacheException) eFactory.getException(ConfigErr, e);
                throw e2;
            }
            int count = 0;
            while (series.hasNext())
            {
                ResultsNavigator nav = series.executeNextQuery();
                /**
                 * The CacheStrategyHelper class is used to navigate through the
                 * query results and place objects in the cache
                 */
                CacheStrategyHelper.putResultsInMap(nav, cache,
                                                    this.cacheHandler,
                                                    super.logger, super.debug);
            }

        }
        else
        {
           ResultsNavigator nav = super.dataManager.executeQuery(sql);
           /**
            * The CacheStrategyHelper class is used to navigate through the
            * query results and place objects in the cache
            */
           CacheStrategyHelper.putResultsInMap(nav, cache,
                                               this.cacheHandler,
                                               super.logger, super.debug);
        }
    }

    /**
     * lookup a value in the cache for a given key
     * @assumes nothing
     * @effects nothing
     * @param key the key object to lookup
     * @param cache the cache to look in
     * @return the value found which could be null
     * @throws DBException thrown if there is an error accessing the database
     * @throws CacheException thrown if there is an error accessing the cache
     */
    public Object lookup(Object key, Map cache)
        throws DBException, CacheException
    {
        if (!super.hasBeenInitialized)
        {
            init(cache);
        }
        Object target = null;
        if (key instanceof String)
            // make lookup case insensitive
            target = ((String)key).toLowerCase();
        else
            target = key;

        Object o = cache.get(target);
        if (super.debug)
        {
            if (o != null)
            {
                super.logger.logDebug("key found in cache: " + key);
            }
            else
            {
                super.logger.logDebug("key not found in cache: " + key);
            }
        }
        return o;
    }
}
