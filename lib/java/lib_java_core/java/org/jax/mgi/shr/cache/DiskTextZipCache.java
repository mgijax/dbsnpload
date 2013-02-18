package org.jax.mgi.shr.cache;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jax.mgi.shr.exception.MGIException;

/** The <tt>DiskTextCache</tt> class uses a directory in the file system to
* provide an implementation of the <tt>TextCache</tt> interface.  <I>Nothing
* else should be stored in the directory used for caching.</I> Since this
* cache is implemented in the file system, there should be no slashes (/) in
* a <tt>textType</tt>.
*/
public class DiskTextZipCache extends AbstractTextCache
{
    //////////////////////
    // Instance variables:
    //////////////////////

    /* Within this directory are subdirectories, one for text textType.  Each
    * of those directories is named by the textType and contains a file for
    * each cache entry for that textType.  Each cache entry is named by the
    * item's unique ID.
    */
    private File directory = null;	// directory to use for caching

    // factory used to generate exceptions for this class
    private TextCacheExceptionFactory excFactory = null;

    /////////////////
    // public methods
    /////////////////

    /* -------------------------------------------------------------------- */

    /** constructs a DiskTextCache object after performing some error
    *	checking.  If the given caching directory does not exist, this method
    *	attempts to create it.
    * @throws MGIException if the given <tt>directoryName</tt> does not
    *	exist, is not a directory, or is not readable/writeable.
    */
    public DiskTextZipCache (String directoryName) throws MGIException
    {
	File dir = new File (directoryName);

	// get a reference to the exception factory if we don't have one

	if (excFactory == null)
	{
	    this.excFactory = TextCacheExceptionFactory.getFactory();
	}

	/* if the directory does not exist, then we try to create it and any
	*  missing parent directories as well.
	*/

	if (!dir.exists())
	{
	    boolean succeeded;	// can we create the needed directories?
	    try
	    {
		succeeded = dir.mkdirs();
	    }
	    catch (SecurityException e)
	    {
		succeeded = false;
	    }
	    if (!succeeded)
	    {
	        throw this.excFactory.getException (
		    TextCacheExceptionFactory.FAILED_CACHE_CREATION_EXC);
	    }
	}

	// error checking...

	if (!dir.isDirectory())
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.NOT_DIR_EXC);
	}
	if (!dir.canWrite())
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.NOT_WRITEABLE_EXC);
	}
	if (!dir.canRead())
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.NOT_READABLE_EXC);
	}

	this.directory = dir;	    // passed sanity checks, so dir is okay
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public long age (String textType, String id) throws MGIException
    {

	long elapsed;		// elapsed time since file created

	File myFile = this.getFile (textType, id);
	if (!myFile.exists())
	{
	    return 0L;		// no entry in cache --> no age in cache
	}

	// this can be negative in the event of a system clock problem or
	// during the hour switchover to/from daylight savings time

	elapsed = System.currentTimeMillis() - myFile.lastModified();
	if (elapsed < 0L)
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.NEGATIVE_AGE_EXC);
	}
	return elapsed;
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void clear (String textType, String id) throws MGIException
    {
	File entry = this.getFile (textType, id);
	try
	{
	    entry.delete();
	}
	catch (SecurityException e)
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.FAILED_REMOVAL_EXC);
	}
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>TextCache</tt> for comments.
    */
    public void clear (String textType) throws MGIException
    {
        /* In pure Java, this is likely to be slow.  If performance becomes a
	*  problem, we may want to rewrite this with a system call to 'rm'.
	*/
	File dir = this.getFile (textType);
	File[] entries = dir.listFiles();
	File entry = null;

	if (entries != null)				// remove files
	{
	    for (int i = 0; i < entries.length; i++)
	    {
	    	this.clear (textType, entries[i].getName());
	    }
	}

	try						// remove directory
	{
	    dir.delete();
	}
	catch (SecurityException e)
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.FAILED_REMOVAL_EXC);
	}
    }

    /* -------------------------------------------------------------------- */

    /** remove all cache entries from the cache.  If no entries exist in the
    *	cache, then this call is a no-op.
    */
    public void clear () throws MGIException
    {
        /* In pure Java, this is likely to be slow.  If performance becomes a
	*  problem, we may want to rewrite this with a system call to 'rm'.
	*/
	File[] textTypes = this.directory.listFiles();

	if (textTypes != null)
	{
	    for (int i = 0; i < textTypes.length; i++)
	    {
		this.clear (textTypes[i].getName());
	    }
	}
    }

    /* -------------------------------------------------------------------- */

    /** retrieve the String which was stored in the cache for the given
    *	<tt>textType</tt> and <tt>id</tt>.  return <tt>null</tt> if there is
    *	no cache entry for those parameters.
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *	cache entry
    * @return String the cached entry, or <tt>null</tt> if none existed
    * @throws MGIException if the entry exists, but we have problems reading
    *	it back in
    */
    public String primitiveGet (String textType, String id)
    	throws MGIException
    {
	File entry = this.getFile (textType, id);
	String line = null;
	StringBuffer contents = new StringBuffer();

	if (!entry.exists())
	{
	    return null;
	}

	try
	{		
		BufferedReader inFile =  new BufferedReader( new InputStreamReader(
				(new GZIPInputStream(new FileInputStream(entry)))));

	    while ((line = inFile.readLine()) != null)
	    {
	        contents.append (line + "\n");
	    }
	    inFile.close();
	}
	catch (IOException e)
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.FAILED_RETRIEVAL_EXC);
	}
	return contents.toString();
    }

    /* -------------------------------------------------------------------- */

    /** see <tt>AbstractTextCache</tt> for comments.
    */
    protected void primitivePut (String textType, String id, String contents)
	throws MGIException
    {
	File outDir = this.getFile (textType);
	if (!outDir.exists())
	{
	    if (!outDir.mkdir())
	    {
		return;			// can't create directory -- bail out
	    }
	}
	

	File outFile = this.getFile (textType, id);
	try
	{
        BufferedReader in = new BufferedReader(
                new StringReader(contents));
        
		BufferedOutputStream out = new BufferedOutputStream (
	    		new GZIPOutputStream(new FileOutputStream (outFile)));
		
        int c;
        while ((c = in.read()) != -1){
        	out.write(c);
        }
        
        in.close();
	    out.close();
	}
	catch (IOException e)
	{
	    throw this.excFactory.getException (
	        TextCacheExceptionFactory.FAILED_PUT_EXC);
	}
    }

    /* -------------------------------------------------------------------- */

    ///////////////////
    // Private methods:
    ///////////////////

    /* -------------------------------------------------------------------- */

    /** return the file object for the location of cached entries for the
    * given <tt>textType</tt>.
    * @param textType identifies the type of cache entry
    */
    private File getFile (String textType)
    {
	return new File (this.directory, textType);
    }

    /** return a <tt>File</tt> object corresponding to the given parameters
    * @param textType identifies the type of cache entry
    * @param id unique identifier (within a given <tt>textType</tt>) for a
    *	cache entry
    * @return File abstract filename corresponding to an entry for the given
    *	parameters
    */
    private File getFile (String textType, String id)
    {
	return new File (this.getFile (textType), id);
    }
}
