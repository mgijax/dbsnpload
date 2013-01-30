package org.jax.mgi.shr.cache;

/*
* $Header$
* $Name$
*/

import org.jax.mgi.shr.exception.ExceptionFactory;
import org.jax.mgi.shr.exception.MGIException;

/**
* @module TextCacheExceptionFactory.java
* @author jsb
*/

/** handles generation of exceptions for the TextCache classes
*  an ExceptionFactory which has been customized to generate exceptions for
*    the TextCache classes
* @has a set of pre-defined named exceptions
* @does Retrieves an exception corresponding to a given name.  Provides two
*    means for getting a TextCacheExceptionFactory:
*    <OL>
*    <LI> standard constructor -- builds a new TextCacheExceptionFactory
*        instance
*    <LI> getFactory() -- returns a reference to a shared instance
*        maintained within the class
*    </OL>
*/
public class TextCacheExceptionFactory extends ExceptionFactory
{
    ///////////////////////////
    // standard exception names
    ///////////////////////////

    // if a negative age is found for a cache entry
    public static final String NEGATIVE_AGE_EXC = "NegativeAgeException";

    // problems removing a cache entry
    public static final String FAILED_REMOVAL_EXC = "FailedRemovalException";

    // problems retrieving a cache entry
    public static final String FAILED_RETRIEVAL_EXC =
        "FailedRetrievalException";

    // problems storing a cache entry
    public static final String FAILED_PUT_EXC = "FailedPutException";

    // problems creating a cache directory
    public static final String FAILED_CACHE_CREATION_EXC =
        "FailedCacheCreationException";

    // cache directory specified is not really a directory
    public static final String NOT_DIR_EXC = "NotDirectoryException";

    // cache directory is not writeable
    public static final String NOT_WRITEABLE_EXC = "NotWriteableException";

    // cache directory is not readable
    public static final String NOT_READABLE_EXC = "NotReadableException";

    //////////////////////////
    // private class variables
    //////////////////////////

    // have we yet initialized the HashMap of exceptions?  (true == yes)
    private static boolean INITIALIZED = false;

    // one instance available to be shared when a new instance is not needed
    private static TextCacheExceptionFactory FACTORY = null;

    /////////////////
    // public methods
    /////////////////

    /** constructor
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public TextCacheExceptionFactory ()
    {
	this.init();
        return;
    }

    /** get a reference to the shared TextCacheExceptionFactory (does not
    *    create a new instance).  This is useful for code which does not need
    *    an entirely new TextCacheExceptionFactory of its own.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public static TextCacheExceptionFactory getFactory ()
    {
        if (FACTORY == null)
	{
	    FACTORY = new TextCacheExceptionFactory();
	}
	return FACTORY;
    }

    //////////////////
    // private methods
    //////////////////

    /** initialize the class's and instance's internal data structures.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    private void init ()
    {
	// if we have not already added the WI's exceptions to the class's
	// HashMap, then do so now.

        if (!INITIALIZED)
	{
	    exceptionsMap.put (NEGATIVE_AGE_EXC,
	        new MGIException ("Cache entry is newer than current time",
		    false));
	    exceptionsMap.put (FAILED_REMOVAL_EXC,
	        new MGIException ("Failed to remove cache entry", false));
	    exceptionsMap.put (FAILED_RETRIEVAL_EXC,
	        new MGIException ("Failed to retrieve cache entry", false));
	    exceptionsMap.put (FAILED_PUT_EXC,
	        new MGIException ("Failed to add cache entry", false));
	    exceptionsMap.put (FAILED_CACHE_CREATION_EXC,
	        new MGIException ("Failed to create cache directory", false));
	    exceptionsMap.put (NOT_DIR_EXC,
	        new MGIException ("Cache dir is not a directory", false));
	    exceptionsMap.put (NOT_WRITEABLE_EXC,
	        new MGIException ("Cache dir is not writeable", false));
	    exceptionsMap.put (NOT_READABLE_EXC,
	        new MGIException ("Cache dir is not readable", false));

	    INITIALIZED = true;
	}
	return;
    }
}

/*
* $Log$
* Revision 1.3  2004/07/21 18:24:47  mbw
* javadocs only
*
* Revision 1.2  2004/02/25 20:21:07  mbw
* fixed to eliminate compiler warnings
*
* Revision 1.1  2003/12/30 16:49:58  mbw
* imported into this product
*
* Revision 1.1  2003/12/01 13:16:48  jsb
* Added per JSAM code review
*
* $Copyright$
*/
