package org.jax.mgi.shr.cache;

import java.util.Map;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.LoggerFactory;
import org.jax.mgi.shr.config.LogCfg;

/**
 * The base class behind different types of cache strategy classes,
 * which currently include the LazyCacheStrategy and FullCacheStrategy. These
 * classes provide the basic patterns for managing data between
 * a cache and the database
 * @has a SQLDataManager for querying the database and a RowDataCacheHandler
 * for obtaining sql statements to query the database with.
 * @does holds the reference to the RowDataCacheHandler class and the
 * SQLDataManager and provides accessors for them.
 * @abstract Subclasses will be responsible for implementing the
 * lookup(Object) method and the init(Map) method which will differ between
 * the LazyCacheStrategy and the FullCacheStrategy
 * @author MWalker
 */
abstract public class RowDataCacheStrategy
{
    /**
     * the SQLDataManager to use
     */
    protected SQLDataManager dataManager;
    /**
     * the strategy class for handling the cache lookups
     */
    protected RowDataCacheHandler cacheHandler;
    /**
     * indicator of whether or not the cache was initialized
     */
    protected boolean hasBeenInitialized = false;
    /**
     * the logger to use
     */
    protected Logger logger = null;
    /**
     * indicator of whether or not to log debug messages
     */
    protected boolean debug = false;

    /**
     * constructor
     * @param sqlDataManager the SQLDataManager for performing database
     * queries
     */
    public RowDataCacheStrategy(SQLDataManager sqlDataManager)
    {
        this.dataManager = sqlDataManager;
        this.logger = sqlDataManager.getLogger();
    }


    /**
     * lookup up a value in the cache with the given key
     * @assumes nothing
     * @effects nothing
     * @param key the key to lookup
     * @param cache the cache to look in
     * @return the value found
     * @throws CacheException thrown if there is an error interpreting
     * results from the database query
     * @throws DBException thrown if there is a database error when accessing
     * the database
     */
    public abstract Object lookup(Object key, Map cache)
        throws CacheException,
        DBException;

    /**
     * initialize the given cache with a query to the database
     * @assumes nothing
     * @effects places inital data into the cache
     * @param cache the cache to initialize
     * @throws CacheException thrown if there is an error interpreting the
     * results from the database query
     * @throws DBException thrown if there is an error while accessing the
     * database
     */
    public abstract void init(Map cache)
        throws CacheException, DBException;

    /**
     * set the logger for this instance. If not set then the logger is
     * obtained from the SQLDataManager which was given in the constructor
     * @effects log messages will be sent to this logger
     * @assumes nothing
     * @param logger the logger
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * get the logger for this instance
     * @assumes nothing
     * @effects nothing
     * @return the logger fro this instance
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * set whether to log debug messages to the logger. The Logger instance
     * will also have to be set with debug turned on. This is an extra level
     * of control of the caching logging messages since they are rarely ever
     * needed.
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * get the debug state of this instance
     * @assumes nothing
     * @effects nothing
     * @return the debug state of this instance
     */
    public boolean getDebug()
    {
        return this.debug;
    }

    /**
     * set the cache handler class reference
     * @assumes nothing
     * @effects the instance variable for the CacheHandler will be set
     * @param handler the given CacheHandler
     */
    protected void setCacheHandler(RowDataCacheHandler handler)
    {
        this.cacheHandler = handler;
    }
}
