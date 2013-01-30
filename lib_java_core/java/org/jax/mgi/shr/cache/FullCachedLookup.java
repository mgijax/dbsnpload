package org.jax.mgi.shr.cache;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.log.Logger;

/**
 * An abstract class intended to be extended by a particular lookup class
 * which uses a FullCacheStrategy. This class implements all the abstract base
 * class methods that are designed for lazy caching by throwing an exception.
 * Abstract methods from the base class that are designed for full caching
 * remain abstract and are intended to be implemented by the specific lookup
 * class.
 * @has nothing
 * @does throws runtime exceptions if methods for lazy cache strategies are
 * called on this object
 * @abstract the getPartialInitQuery() method and the getAddQuery() method
 * are both implemented by throwing a runtime exception to indicate that this
 * method is not supported for a full cache strategy. The subclass will be
 * required to implement getFullInitQuery() and getInterpreter(). The
 * getFullInitQuery can return a null which will indicate to the
 * FullCacheStrategy class that no initialization is to be performed.
 */
public abstract class FullCachedLookup
    extends CachedLookup
{
    /**
     * constructor
     * @assumes nothing
     * @effects nothing
     * @param sqlMgr the SQLDataManager class to use for performing database
     * queries.
     * @throws CacheException if there is an error accessing the cache
     */
    public FullCachedLookup(SQLDataManager sqlMgr)
        throws CacheException
    {
        super(CacheConstants.FULL_CACHE, sqlMgr);
    }


    /**
     * throws a RuntimeException indicating that this method is not supported
     * @return nothing. A RunTime Exception is thrown
     * @throws a RunTimeException always
     */
    public String getPartialInitQuery()
    {
        throw MGIException.getUnsupportedMethodException();
    }

    /**
     * throws a RuntimeException indicating that this method is not supported
     * @param addObject the taget object that needs to be found in the database
     * so that it can be added to the cache
     * @return nothing. A RunTime Exception is thrown.
     * @throws a RunTimeException always
     */
    public String getAddQuery(Object addObject)
    {
        throw MGIException.getUnsupportedMethodException();
    }
}