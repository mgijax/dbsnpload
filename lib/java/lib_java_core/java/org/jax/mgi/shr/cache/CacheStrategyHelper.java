package org.jax.mgi.shr.cache;

import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.MultiRowInterpreter;
import org.jax.mgi.shr.dbutils.MultiRowIterator;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;

/**
 * A helper class for providing basic helper methods used by both
 * LazyCacheStrategy and FullCacheStrategy
 * @has nothing
 * @does provides static methods for doing tasks common to both the lazy
 * and full cache strategy classes.
 * @company The Jackson Laboratory
 */
public class CacheStrategyHelper
{
    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String MissingKeyValue =
        CacheExceptionFactory.MissingKeyValue;


    /**
     * Parses through the given ResultsNavigator and places data into the
     * cache. It determines if the given cacheHandler supports muliple rows
     * by checking if it's RowDataInterpreter is an instance of
     * MultiRowInterpreter. If so, then this method uses a
     * MultiResultsIterator for parsing the results.
     * See
     * <ul>
     * <li><a href="../dbutils/RowDataInterpreter.html">RowDataInterpreter</a></li>
     * <li><a href="../dbutils/MultiRowInterpreter.html">MultiRowInterpreter</a></li>
     * <li><a href="../dbutils/ResultsNavigator.html">ResultsNavigator</a></li>
     * <li><a href="../dbutils/MultiRowIterator.html">MultiRowIterator</a></li>
     * </ul>
     * @assumes nothing
     * @effects new entries are added to the cache
     * @param navigator the ResultsNavigator object from which objects
     * are obtained and placed in the cache
     * @param cache the cache to add the objects to
     * @param cacheHandler the RowDataCacheHandler class from which to obtain
     * the RowDataInterpreter classes for interpreting results of a database
     * query
     * @param logger the logger to use
     * @param debug indicator of whether or not to log debug messages
     * @throws CacheException if there is an error putting objects in the
     * cache
     * @throws DBException if there is an error accessing data from the
     * database
     */
    protected static void putResultsInMap(ResultsNavigator navigator,
                                          Map cache,
                                          RowDataCacheHandler cacheHandler,
                                          Logger logger,
                                          boolean debug)
        throws CacheException, DBException
    {
        Object resultObj = null;
        RowDataInterpreter interpreter = cacheHandler.getRowDataInterpreter();
        /**
         * see if the cacheHandler supports multiple rows per cache entry
         */
        if (interpreter instanceof MultiRowInterpreter)
        {
            /**
             * process multiple rows for each cache entry using a
             * MultiRowIterator
             */
            MultiRowInterpreter multiRowInterpreter =
                (MultiRowInterpreter) interpreter;
            MultiRowIterator iterator =
                new MultiRowIterator(navigator, multiRowInterpreter);
            while (iterator.hasNext())
            {
                resultObj = iterator.next();
                putObjectInCache(resultObj, cache, cacheHandler,
                                 logger, debug);
            }
            iterator.close();
        }
        else
        {
            /**
             * the cacheHandler is prepared to only handle one row per cache
             * entry
             */
            navigator.setInterpreter(interpreter);
            while (navigator.next())
            {
                putObjectInCache(navigator.getCurrent(),
                                 cache, cacheHandler, logger, debug);
            }
            navigator.close();
        }
    }

    /**
     * An overload to the putResultsInMap method above except this one takes
     * an ArrayList of ResultNavigators. See documentation for putResultsInMap
     * above.
     * @param navigators an ArrayList of ResultNavigators
     * @param cache the cache to add the objects to
     * @param cacheHandler the RowDataCacheHandler class from which to obtain
     * the RowDataInterpreter classes for interpreting results of a database
     * query
     * @param logger the logger to use
     * @param debug indicator of whether or not to log debug messages
     * @throws CacheException if there is an error putting objects in the
     * cache
     * @throws DBException if there is an error accessing data from the
     * database
     */
    protected static void putResultsInMap(ArrayList navigators,
                                          Map cache,
                                          RowDataCacheHandler cacheHandler,
                                          Logger logger,
                                          boolean debug)
    throws DBException, CacheException
    {
        for (Iterator it = navigators.iterator(); it.hasNext();)
        {
            ResultsNavigator nav = (ResultsNavigator)it.next();
            putResultsInMap(nav, cache, cacheHandler, logger, debug);
        }
    }


    /**
     * checks the given object to see if it is an instance of KeyValue and
     * throws a CacheException if it is not
     * @param o the given object to check
     * @param cacheHandler used for error reporting
     * @throws CacheException thrown if the given object is not an instance of
     * KeyValue
     */
    private static void checkForKeyValue(Object o,
                                         RowDataCacheHandler cacheHandler)
        throws CacheException
    {
        if (! (o instanceof KeyValue))
        {
            CacheExceptionFactory eFactory = new CacheExceptionFactory();
            CacheException e = (CacheException)
                eFactory.getException(MissingKeyValue);
            e.bind(cacheHandler.getRowDataInterpreter().getClass().getName());
            throw e;
        }
    }

    /**
     * puts the given object in the cache after assuring the the object
     * returned is a KeyValue type
     * @param o the given object
     * @param cache the cache to put the object into
     * @param cacheHandler is used for error reporting in that the name
     * of the handler class is obtained and reported on error
     * @param logger an implementation of the Logger interface
     * @param debug true if method should print debug statements to Logger,
     * false otherwise
     * @throws CacheException thrown if the given object is not of type
     * KeyValue
     */
    private static void putObjectInCache(Object o,
                                         Map cache,
                                         RowDataCacheHandler cacheHandler,
                                         Logger logger,
                                         boolean debug)
        throws CacheException
    {
        checkForKeyValue(o, cacheHandler); // assure the object's a KeyValue
        KeyValue keyValue = (KeyValue) o;
        if (debug)
            logger.logDebug("adding " + keyValue.getKey() + " to cache");
        Object key = keyValue.getKey();
        Object revisedKey = null;
        if (key instanceof String)
            revisedKey = ((String)key).toLowerCase();
        else
            revisedKey = key;

        cache.put(revisedKey, keyValue.getValue());
    }
}
