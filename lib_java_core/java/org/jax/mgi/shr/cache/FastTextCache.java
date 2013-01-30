package org.jax.mgi.shr.cache;

/*
* $Header$
* $Name$
*/

/**
* @module FastTextCache.java
* @author jsb
*/

import java.util.HashMap;
import org.jax.mgi.shr.exception.MGIException;

/** Provides an enhanced implementation of the TextCache interface by starting
*    with a standard DiskTextCache and then augmenting it with a memory-based
*    cache as well for commonly requested items.  Since DiskTextCache is used,
*    the same restrictions apply:  nothing else should be stored in the
*    directory used for caching, and no slashes (/) should appear in the
*    textTypes.
*  a TextCache with better performance than a standard DiskTextCache
* @has a cache of items divided into various textTypes, each of which contains
*    entries identified by an ID unique within that textType
* @does standard operations specified by the TextCache interface
*/
public class FastTextCache extends AbstractTextCache
{
    //////////////////////
    // Instance variables:
    //////////////////////

    // primary storage for all cache entries
    private DiskTextCache diskCache = null;

    // stores commonly requested cache items for faster access.  keys are
    // textTypes which each reference another HashMap.  Those internal
    // HashMaps are each keyed by the unique IDs for the cached items.
    private HashMap memoryCache = null;

    // number of bytes which is half of the total memory, used to stop
    // adding to the memory cache when memory is half used.
    private long halfMemory = Runtime.getRuntime().totalMemory() / 2;

    /* -------------------------------------------------------------------- */

    /////////////////
    // public methods
    /////////////////

    /** constructs a FastTextCache object after performing some error
    *	checking.  If the given caching directory does not exist, this method
    *	attempts to create it.
    * @param directoryName file system path to the directory for use in
    *    caching
    * @assumes nothing
    * @effects nothing
    * @throws MGIException if the given 'directoryName' does not
    *	exist, is not a directory, or is not readable/writeable.
    */
    public FastTextCache (String directoryName) throws MGIException
    {
	this.diskCache = new DiskTextCache (directoryName);
	this.memoryCache = new HashMap();
	return;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public long age (String textType, String id) throws MGIException
    {
	// all entries will first be cached on disk, so we can just use their
	// age from the disk cache
        return this.diskCache.age (textType, id);
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void clear (String textType, String id) throws MGIException
    {
	// the portion of the memory cache for the specified 'textType'
	HashMap cacheForTextType = null;

        // must remove from disk and from memory...

	this.diskCache.clear (textType, id);

        if (this.memoryCache.containsKey (textType))
	{
	    cacheForTextType = (HashMap) this.memoryCache.get(textType);
	    cacheForTextType.remove(id);
	}
	return;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void clear (String textType) throws MGIException
    {
	this.diskCache.clear (textType);
	this.memoryCache.remove (textType);
        return;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void clear () throws MGIException
    {
        this.diskCache.clear();
	this.memoryCache = new HashMap();
        return;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>AbstractTextCache</tt> for comments.
    */
    public String primitiveGet (String textType, String id)
    	throws MGIException
    {
	// the portion of the memory cache for this 'textType'
        HashMap textTypeCache = (HashMap) this.memoryCache.get (textType);

	// the cached entry for this 'textType:id' pair
	String entry = null;

	// if the item is already in the memory cache, then just return it

	if (textTypeCache != null)
	{
	    entry = (String) textTypeCache.get (id);
	    if (entry != null)
	    {
	        return entry;
	    }
	}

	// Otherwise, check the disk cache.  If it is there, then we have our
	// first cache hit for this item, so put it in the memory cache, too.

	entry = this.diskCache.get (textType, id);
	if (entry != null)
	{
	    // if we have have more than half the total memory free, then
	    // add this item to the memory cache.

	    if (Runtime.getRuntime().freeMemory() > this.halfMemory)
	    {
	        if (textTypeCache == null)    // first hit for this textType?
	        {
	            textTypeCache = new HashMap();
		    this.memoryCache.put (textType, textTypeCache);
	        }
	        textTypeCache.put (id, entry);
	    }
	}
	return entry;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>AbstractTextCache</tt> for comments.
    */
    protected void primitivePut (String textType, String id, String contents)
	throws MGIException
    {
	// We always add entries initially to the disk cache.  Items only get
	// moved into the memory cache by the 'primitiveGet()' method.

        this.diskCache.put (textType, id, contents);
	return;
    }
}

/*
* $Log$
* Revision 1.2  2004/07/21 18:16:32  mbw
* javadocs only
*
* Revision 1.1  2003/12/30 16:49:51  mbw
* imported into this product
*
* Revision 1.2  2003/12/01 13:19:56  jsb
* Changes for JSAM code review
*
* Revision 1.1  2003/07/03 17:16:15  jsb
* Added to product for use by JSAM WI
*
* $Copyright$
*/
