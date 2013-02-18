package org.jax.mgi.shr.cache;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.shr.exception.MGIException;

/** The <tt>AbstractTextCache</tt> class represents an implementation of the
* <tt>TextCache</tt> interface.  It is an abstract class, providing
* implementations for six of the interface's methods and leaving another
* seven to be implemented in each subclass.<P>
* See <tt>TextCache</tt> for more information about this class's purpose.<P>
* This class provides implementations for many methods for the
* <tt>TextCache</tt> interface.  We leave for definition in subclasses those
* which are truly implementation-dependent:  one age method, three clear
* methods, and primitive get and put methods.
*/
public abstract class AbstractTextCache implements TextCache
{
    ///////////////////
    // abstract methods (to be defined in subclasses)
    ///////////////////

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.  left abstract as it is
    *	implementation-dependent.
    */
    public abstract long age (String textType, String id) throws MGIException;

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.  left abstract as it is
    *	implementation-dependent.
    */
    public abstract void clear (String textType, String id)
    	throws MGIException;

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.  left abstract as it is
    *	implementation-dependent.
    */
    public abstract void clear (String textType) throws MGIException;

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.  left abstract as it is
    *	implementation-dependent.
    */
    public abstract void clear () throws MGIException;

    /* -------------------------------------------------------------------- */

    /** retrieves the entry specified by <tt>textType</tt> and <tt>id</tt>
    *	from the cache, or returns <tt>null</tt> if it is not in the cache.
    *	The other get* methods are implemented in terms of this one.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *   cache entry
    * @return String the cache entry specified, or <tt>null</tt> if it does
    *	not exist
    * @throws MGIException if the entry exists, but we have problems
    *	retrieving it
    */
    protected abstract String primitiveGet (String textType, String id)
    	throws MGIException;

    /* -------------------------------------------------------------------- */

    /** stores the given <tt>contents</tt> in the cache, identified by the
    *	given <tt>textType</tt> and <tt>id</tt>.
    *	The other put* methods are implemented in terms of this one.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *   cache entry
    * @param contents the item to store in the cache
    * @throws MGIException if we encounter problems storing the item in
    *	the cache
    */
    protected abstract void primitivePut (String textType, String id,
    	String contents)
    	    throws MGIException;

    /* -------------------------------------------------------------------- */

    ///////////////////////
    // non-abstract methods (to leave alone)
    ///////////////////////

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public String get (String textType, String id) throws MGIException
    {
        long count = 0;
        String s = this.primitiveGet (textType, id);

	if (s != null)
	{
	    if (this.hits.containsKey (textType))
	    {
	        count = ((Long) this.hits.get (textType)).longValue();
	    }
	    this.hits.put (textType, new Long (count + 1));
	}
	else
	{
	    if (this.misses.containsKey (textType))
	    {
	        count = ((Long) this.misses.get (textType)).longValue();
	    }
	    this.misses.put (textType, new Long (count + 1));
	}
	return s;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.  uses this.get() so that the
    * hitRate is handled appropriately.
    */
    public Collection /* of Strings */ getAsCollection (
    	String textType, String id) throws MGIException
    {
        String s = this.get (textType, id);		// retrieve from cache
	Collection c = StringLib.split (s, "\n");	// break into lines
	Collection cLf = new ArrayList();		// with newlines

	Iterator it = c.iterator();
	while (it.hasNext())
	{
	    cLf.add ( ((String) it.next()) + "\n");
	}
	return cLf;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.  uses this.get() so that the
    * hitRate is handled appropriately.
    */
    public StringBuffer getAsStringBuffer (String textType, String id)
        throws MGIException
    {
        return new StringBuffer (this.get (textType, id));
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public double getHitRate (String textType)
    {
	double hits = 0;
	double misses = 0;

	if (!this.hits.containsKey (textType))
	{
	    return 0.0D;
	}
	if (!this.misses.containsKey (textType))
	{
	    return 100.0D;
	}
	hits = ((Long) this.hits.get(textType)).doubleValue();
	misses = ((Long) this.misses.get(textType)).doubleValue();

	return hits / (hits + misses) * 100;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public double getHitRate ()
    {
        double hits = 0;
	double misses = 0;
	Iterator it = null;
	String textType = null;

	it = this.hits.keySet().iterator();
	while (it.hasNext())
	{
	    textType = (String) it.next();
	    hits = hits + ((Long) this.hits.get(textType)).doubleValue();
	}

	it = this.misses.keySet().iterator();
	while (it.hasNext())
	{
	    textType = (String) it.next();
	    misses = misses +
	        ((Long) this.misses.get(textType)).doubleValue();
	}

	if (hits + misses == 0)
	{
	    return 100.0D;
	}
	return hits / (hits + misses) * 100;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public long getHits (String textType)
    {
	if (!this.hits.containsKey (textType))
	{
	    return 0L;
	}
	return ((Long) this.hits.get(textType)).longValue();
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public long getHits ()
    {
        long hits = 0;
	Iterator it = null;
	String textType = null;

	it = this.hits.keySet().iterator();
	while (it.hasNext())
	{
	    textType = (String) it.next();
	    hits = hits + ((Long) this.hits.get(textType)).longValue();
	}
	return hits;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public long getMisses (String textType)
    {
	if (!this.misses.containsKey (textType))
	{
	    return 0L;
	}
	return ((Long) this.misses.get(textType)).longValue();
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public long getMisses ()
    {
        long misses = 0;
	Iterator it = null;
	String textType = null;

	it = this.misses.keySet().iterator();
	while (it.hasNext())
	{
	    textType = (String) it.next();
	    misses = misses + ((Long) this.misses.get(textType)).longValue();
	}
	return misses;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void put (String textType, String id, String contents)
        throws MGIException
    {
        this.primitivePut (textType, id, contents);
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void put (String textType, String id,
		    Collection /* of String */ contents)
        throws MGIException
    {
        this.primitivePut (textType, id, StringLib.join (contents, ""));
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void put (String textType, String id, StringBuffer contents)
        throws MGIException
    {
        this.primitivePut (textType, id, contents.toString());
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void clearHitRate (String textType)
    {
       this.hits.remove(textType);
       this.misses.remove(textType);
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void clearHitRate ()
    {
        this.hits = new HashMap();
        this.misses = new HashMap();
    }


    //////////////////////
    // instance variables:
    //////////////////////

    /* is keyed by textType; each key's value is the number of "hits" for that
    * textType.  (a successful 'get()' from the cache)
    */
    private HashMap hits = new HashMap();

    /* is keyed by textType; each key's value is the number of "misses" for
    * that textType.  (an unsuccessful 'get()' from the cache)
    */
    private HashMap misses = new HashMap();
}
