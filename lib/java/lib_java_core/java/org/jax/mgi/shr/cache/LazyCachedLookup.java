package org.jax.mgi.shr.cache;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.log.Logger;

/**
 * An abstract class intended to be extended by a particular lookup class
 * which uses a LazyCacheStrategy. This class implements all the abstract base
 * class methods that are designed for full caching by throwing an exception.
 * Abstract methods from the base class that are designed for lazy caching
 * remain abstract and are intended to be implemented by the specific lookup
 * class.
 * @has nothing
 * @does throws runtime exceptions if methods for full cache strategies are
 * called on this object
 * @abstract the getFullInitQuery method is implemented by throwing a runtime
 * exception to indicate that this method is not supported for a lazy cache
 * strategy. The subclass will be required to implement getPartialInitQuery(),
 * getAddQuery() and getInterpreter().
 *
 */
public abstract class LazyCachedLookup
    extends CachedLookup
{
    /**
     * constructor which accepts an SQLDataManager
     * @param sqlMgr the SQLDataMAnager
     * @throws CacheException if there is an error accessing the cache
     */
    public LazyCachedLookup(SQLDataManager sqlMgr)
        throws CacheException
    {
        super(CacheConstants.LAZY_CACHE, sqlMgr);
    }


    /**
     * implements this method as required by the abstract base class by
     * throwing a RuntimeException
     * @assumes nothing
     * @effects a runtime exception will be thron
     * @return nothing. a RuntimeException is thrown
     */
    public String getFullInitQuery()
    {
        throw MGIException.getUnsupportedMethodException();
    }
}