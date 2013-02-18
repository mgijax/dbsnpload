package org.jax.mgi.shr.cache;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for cache exceptions.
 * @has a hashmap of predefined CacheExceptions stored by a name key
 * @does looks up CacheExceptions by name
 * @author M Walker
 */
public class CacheExceptionFactory
    extends ExceptionFactory
{
    /**
     * A given RowDataInterpreter did not create a KeyValue object
     */
    public static final String MissingKeyValue =
        "org.jax.mgi.shr.cache.MissingKeyValue";
    static
    {
        exceptionsMap.put(MissingKeyValue, new CacheException(
            "The RowDataInterpreter class, ??, did not create the " +
            "KeyValue object " +
            "required by the current CacheStrategy", false));
    }

    /**
     * This is an unknown cache strategy type
     */
    public static final String UnknownStrategy =
        "org.jax.mgi.shr.cache.UnknownStrategy";
    static
    {
        exceptionsMap.put(UnknownStrategy, new CacheException(
            "An unknown strategy type of ?? given to the constructor " +
            "of RowDataCacheHandler", false));
    }

    /**
     * Full cache was not initialized
     */
    public static final String InitializationErr =
        "org.jax.mgi.shr.cache.InitializationErr";
    static
    {
        exceptionsMap.put(InitializationErr, new CacheException(
            "A null initialization string was found for a full cache. " +
            "The method getFullInitQuery must be implemented with a " +
            "non-null value", false));
    }

    /**
     * Error accessing the configuration file
     */
    public static final String ConfigErr =
        "org.jax.mgi.shr.cache.ConfigErr";
    static
    {
        exceptionsMap.put(ConfigErr, new CacheException(
            "Could not access the configuration file", false));
    }

    /**
     * Error running sql during cache initialization
     */
    public static final String CacheInitErr =
        "org.jax.mgi.shr.cache.CacheInitErr";
    static
    {
        exceptionsMap.put(CacheInitErr, new CacheException(
            "Error executing cache initialization.", false));
    }

    /**
     * Error trying to print the cache to a stream
     */
    public static final String PrintErr =
        "org.jax.mgi.shr.cache.PrintErr";
    static
    {
        exceptionsMap.put(PrintErr, new CacheException(
            "Error trying to print cache to output stream.", false));
    }



}