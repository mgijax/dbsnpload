package org.jax.mgi.shr.cache;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which represents an error in cache handling
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @author M Walker
 */
public class KeyNotFoundException
    extends MGIException
{
    /**
     * the object used in the lookup
     */
    private Object key;
    /**
     * the name of the lookup class which caught the exception
     */
    private String lookupClassName = null;
    public KeyNotFoundException(Object key, String lookupClassName)
    {
        super("The lookup key ?? was not found during lookup in class ??.",
              true);
        this.key = key;
        this.lookupClassName = lookupClassName;
        bind(key.toString());
        bind(lookupClassName);
    }

    /**
     * get the Object key not found
     * @return the Object key not found
     */
    public Object getKey()
    {
        return key;
    }

    /**
     * get the name of the lookup class which caught the exception
     * @return the lookup class name
     */
    public String getLookupClassName()
    {
        return lookupClassName;
    }
}