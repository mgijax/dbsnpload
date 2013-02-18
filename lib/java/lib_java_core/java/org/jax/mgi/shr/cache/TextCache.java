package org.jax.mgi.shr.cache;

import java.util.Collection;
import org.jax.mgi.shr.exception.MGIException;

/** A <tt>TextCache</tt> represents a cache to be used for storing text
* strings.  A <tt>TextCache</tt> object can store Strings for multiple
* arbitrary textTypes.  A textType is simply a String that names a namespace.
* A given cached String is identified by an ID (String) that is unique within
* its associated textType.<P>
* The primary reason for developing this caching mechanism is
* to allow the web interface (WI) to store a web page after it has generated
* one (detail or summary page), in case the same page is requested again.  By
* bringing the basic functionality into this interface, we allow for many
* different implementations -- in memory, in the file system, and in a
* database, for examples.<P>
* <B>IS:</B> A <tt>TextCache</tt> is a cache for storing Strings, each of
*	which is identified by a textType and an ID.<BR>
* <B>HAS:</B> A <tt>TextCache</tt> has several bins -- one per textType --
*	into which we can put Strings identified by IDs.<BR>
* <B>DOES:</B> A <tt>TextCache</tt> provides for the storage, retrieval, and
*	removal of Strings from the cache.
*/
public interface TextCache
{
    /* -------------------------------------------------------------------- */

    /** return the age (in milliseconds) of the specified cache entry.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *	cache entry
    * @return long age (in milliseconds) of the specified cache entry, or zero
    *	if no cache entry exists for the given parameters
    * @throws MGIException in cases where the age is negative (shortly after
    *	a change for daylight savings time, for example, or when there is a
    *	problem with the system clock)
    */
    public long age (String textType, String id) throws MGIException;

    /* -------------------------------------------------------------------- */

    /** remove the specified cache entry from the cache.  If no entry exists
    *	for the specified parameters, then this call is a no-op.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *	cache entry
    * @throws MGIException if there are problems removing the entry
    */
    public void clear (String textType, String id) throws MGIException;

    /* -------------------------------------------------------------------- */

    /** remove all cache entries for the given <tt>textType</tt> from the
    *	cache.  If no entries exist for the specified <tt>textType</tt>, then
    *	this call is a no-op.
    * @param textType identifies the type of cache entry
    * @throws MGIException if there are problems removing an entry for the
    *	given <tt>textType</tt>
    */
    public void clear (String textType) throws MGIException;

    /* -------------------------------------------------------------------- */

    /** remove all cache entries from the cache.  If no entries exist in the
    *	cache, then this call is a no-op.
    * @throws MGIException if there are problems removing any entries.
    */
    public void clear () throws MGIException;

    /* -------------------------------------------------------------------- */

    /** retrieve the cached item corresponding to the given parameters, as a
    *	<tt>Collection</tt> of <tt>Strings</tt>.  Each <tt>String</tt>
    *	corresponds to one line (up to and including a newline character.)  If
    *   there is no corresponding cached item, null is returned.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *	cache entry
    * @throws MGIException if the entry exists, but there are problems
    *	retrieving it
    */
    public Collection /* of Strings */ getAsCollection (String textType,
    	String id) throws MGIException;

    /* -------------------------------------------------------------------- */

    /** retrieve the cached item corresponding to the given parameters, as a
    *	<tt>StringBuffer</tt>.  Newline characters may be included to delimit
    *   multiple lines.  If there is no corresponding cached item, null
    *	is returned.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *	cache entry
    * @throws MGIException if the entry exists, but there are problems
    *	retrieving it
    */
    public StringBuffer getAsStringBuffer (String textType, String id)
    	throws MGIException;

    /* -------------------------------------------------------------------- */

    /** retrieve the cached item corresponding to the given parameters, as a
    *	<tt>String</tt>.  Newline characters may be included to delimit
    *   multiple lines.  If there is no corresponding cached item, null is
    *	returned.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *	cache entry
    * @throws MGIException if the entry exists, but there are problems
    *	retrieving it
    */
    public String get (String textType, String id) throws MGIException;

    /* -------------------------------------------------------------------- */

    /** clears the hitRate data for the given <tt>textType</tt>
    * @param textType identifies the type of cache entry
    */
    public void clearHitRate (String textType);

    /* -------------------------------------------------------------------- */

    /** clears all hitRate data for all textTypes
    */
    public void clearHitRate ();

    /* -------------------------------------------------------------------- */

    /** get the percentage of calls to <tt>get</tt> methods which returned
    *	<tt>true</tt> for the given <tt>textType</tt>, since the cache was
    *	instantiated.
    * @param textType identifies the type of cache entry
    * @return double percentage of calls to <tt>get</tt> methods which
    *	returned <tt>true</tt> for the given <tt>textType</tt>
    */
    public double getHitRate (String textType);

    /* -------------------------------------------------------------------- */

    /** get the percentage of calls to <tt>get</tt> methods which returned
    *	<tt>true</tt>, since the cache was instantiated.
    * @return double percentage of calls to <tt>get</tt> methods which
    *	returned <tt>true</tt>
    */
    public double getHitRate ();

    /* -------------------------------------------------------------------- */

    /** get a count of cache "hits" -- calls to get(), getAsCollection(),
    *    and getAsStringBuffer() which return non-null values.
    * @param textType identifies the type of cache entry
    * @return long count of hits
    */
    public long getHits (String textType);

    /* -------------------------------------------------------------------- */

    /** get a count of cache "hits" -- calls to get(), getAsCollection(),
    *    and getAsStringBuffer() which return non-null values.  sums counts
    *    for all textTypes.
    * @return long count of hits
    */
    public long getHits ();

    /* -------------------------------------------------------------------- */

    /** get a count of cache "misses" -- calls to get(), getAsCollection(),
    *    and getAsStringBuffer() which return null values.
    * @param textType identifies the type of cache entry
    * @return long count of misses
    */
    public long getMisses (String textType);

    /* -------------------------------------------------------------------- */

    /** get a count of cache "misses" -- calls to get(), getAsCollection(),
    *    and getAsStringBuffer() which return null values.  sums counts
    *    for all textTypes.
    * @return long count of misses
    */
    public long getMisses ();

    /* -------------------------------------------------------------------- */

    /** store the given <tt>contents</tt> in the cache, associated with the
    *	given <tt>textType</tt> and <tt>id</tt>.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for the
    *	cache entry
    * @param contents text to store in the cache
    * @throws MGIException if problems occur when storing the item in cache
    */
    public void put (String textType, String id, String contents)
    	throws MGIException;

    /* -------------------------------------------------------------------- */

    /** store the given <tt>contents</tt> in the cache, associated with the
    *	given <tt>textType</tt> and <tt>id</tt>.  The Strings in the
    *	Collection are joined end-to-end with no separators added.  (So if you
    *	want line breaks between, you must add them.)
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for the
    *	cache entry
    * @param contents text to store in the cache
    * @throws MGIException if problems occur when storing the item in cache
    */
    public void put (String textType, String id,
		    Collection /* of String */ contents)
    	throws MGIException;

    /* -------------------------------------------------------------------- */

    /** store the given <tt>contents</tt> in the cache, associated with the
    *	given <tt>textType</tt> and <tt>id</tt>.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for the
    *	cache entry
    * @param contents text to store in the cache
    * @throws MGIException if problems occur when storing the item in cache
    */
    public void put (String textType, String id, StringBuffer contents)
    	throws MGIException;
}
