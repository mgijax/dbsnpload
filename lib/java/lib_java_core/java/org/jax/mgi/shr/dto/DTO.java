package org.jax.mgi.shr.dto;

/*
* $Header$
* $Name$
*/

/**
* @module DTO.java
* @author jsb
*/

import java.util.Collection;
import java.util.Set;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

/** a Data Transfer Object (DTO), useful for transferring data between layers
*    of a system.  For example, the WI uses classes that know how to retrieve
*    data from the database.  They then bundle that data into DTO objects and
*    pass it along to other classes (with no database knowledge) for
*    formatting.
*  a Data Transfer Object (DTO), to be used to transfer data among layers
*    of a system.
* @has a mapping between (String) fieldnames and (Object) values.
* @does To get a DTO to work with, there is no public constructor.  (We
*    maintain a pool of available DTO objects to minimize object creation
*    and increase efficiency.)  There are three public static methods relating
*    to this...
*    <OL>
*    <LI> getDTO() -- get an empty DTO to use
*    <LI> putDTO(DTO) -- give back a DTO with which we are finished
*    <LI> setPoolSize(int) -- set the size for the pool of free DTO objects;
*        the default size is 100.
*    </OL>
*    <P>
*    DTO now implements the java.util.Map interface.  In addition, each DTO
*    has the following public methods...
*    <OL>
*    <LI> set(String,Object) -- stores the given Object as the value
*        associated with the String fieldname.  for backward compatability.
*    <LI> getString(Object) -- return a String representation of the object
*        associated with the given key
*    <LI> getFields() -- return a String[] containing all fieldnames defined
*        in this DTO
*    <LI> reset() -- works like Map.clear().  for backward compatability.
*    <LI> toString() -- get a String representation of this DTO
*    </OL><P>
*/
public class DTO implements java.util.Map
{
    /* -------------------------------------------------------------------- */

    ///////////////
    // constructors
    ///////////////

    /** private constructor -- forces people to use 'getDTO()' method.
    * @purpose build a new DTO object.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    private DTO ()
    {
        this.data = new HashMap();
	return;
    }

    /* -------------------------------------------------------------------- */

    ///////////////////////////////////
    // public methods for Map interface
    ///////////////////////////////////

    /** see java.util.Map.clear()
    */
    public void clear()
    {
        this.data.clear();
    }

    /** see java.util.Map.clone()
    */
    public Object clone()
    {
	DTO myClone = DTO.getDTO();
	try
	{
	    myClone.putAll (this);
	}
	catch (NullPointerException exc)
	{
	    ; // will not happen, as 'this' is always defined
	}
        return myClone;
    }

    /** see java.util.Map.containsKey (Object key)
    */
    public boolean containsKey (Object key)
    {
        return this.data.containsKey(key);
    }

    /** see java.util.Map.containsValue (Object value)
    */
    public boolean containsValue (Object value)
    {
        return this.data.containsValue(value);
    }

    /** see java.util.Map.entrySet()
    */
    public Set entrySet()
    {
        return this.data.entrySet();
    }

    /** see java.util.Map.get (Object key)
    */
    public Object get (Object key)
    {
        return this.data.get(key);
    }

    /** see java.util.Map.isEmpty()
    */
    public boolean isEmpty()
    {
        return this.data.isEmpty();
    }

    /** see java.util.Map.keySet()
    */
    public Set keySet()
    {
        return this.data.keySet();
    }

    /** see java.util.Map.put (Object key, Object value)
    */
    public Object put (Object key, Object value)
    {
        return this.data.put (key, value);
    }

    /** see java.util.Map.putAll (Map t)
    */
    public void putAll (Map t) throws NullPointerException
    {
        this.data.putAll (t);
    }

    /** see java.util.Map.remove (Object key)
    */
    public Object remove (Object key)
    {
        return this.data.remove (key);
    }

    /** see java.util.Map.size()
    */
    public int size()
    {
        return this.data.size();
    }

    /** see java.util.Map.values()
    */
    public Collection values()
    {
        return this.data.values();
    }

    /////////////////
    // public methods
    /////////////////

    /** set the given 'value' to be associated with the given 'fieldname'.
    * @param fieldname the fieldname for the given 'value'
    * @param value the value to be associated with the given 'fieldname'
    * @return nothing
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    * @notes If 'fieldname' already has a value associated with it, then the
    *    old association is discarded and this one replaces it.
    */
    public void set (String fieldname, Object value)
    {
        this.data.put(fieldname, value);
	return;
    }

    /* -------------------------------------------------------------------- */

    /** retrieve a String representation of the value associated with the
    *    given 'key'
    * @param key the key whose value we seek
    * @return String a String representation of the value associated with
    *    'key', or null if there is no value associated with it
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public String getString (Object key)
    {
	Object value = this.data.get(key);
	if (value == null)
	{
	    return null;
	}
        return value.toString();
    }

    /* -------------------------------------------------------------------- */

    /** get all the fieldnames defined in this DTO.
    * @return String[] an array of Strings, each of which is the name of a
    *    field defined in this DTO.  If the DTO has no fields defined, then
    *    a zero-length String[] is returned.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public String[] getFields ()
    {
        Set keySet = this.data.keySet();
	if (keySet.isEmpty())
	{
	    return new String[0];
	}
        return (String[]) keySet.toArray (STRING_ARRAY);
    }

    /* -------------------------------------------------------------------- */

    /** removes any fieldnames and values currently defined in this DTO.
    * @return nothing
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public void reset ()
    {
        this.data.clear();
	return;
    }

    /* -------------------------------------------------------------------- */

    /** merge the 'other' DTO into this one.  Any fields defined in 'this' and
    *    in 'other' will be set to the value from 'other'.
    * @param other the DTO to be merged into this one
    * @return nothing
    * @assumes nothing
    * @effects nothing -- 'other' will not be changed.
    * @throws nothing
    */
    public void merge (DTO other)
    {
        this.merge (other, true);
	return;
    }

    /* -------------------------------------------------------------------- */

    /** merge the 'other' DTO into this one.
    * @param other the DTO to be merged into this one
    * @param overwrite determines whether values from 'other' should replace
    *	values in this DTO for any shared fieldnames.  (true means replace
    *	them, false means to keep those currently in this DTO)
    * @return nothing
    * @assumes nothing
    * @effects nothing -- 'other' will not be changed.
    * @throws nothing
    */
    public void merge (DTO other, boolean overwrite)
    {
        String[] fields = other.getFields();
	String fieldname = null;

	// for the sake of efficiency, we only check 'overwrite' once, then
	// go to the appropriate loop (rather than having one loop and
	// checking 'overwrite' inside it):

        if (overwrite)
	{
	    // Since we are overwriting, we do not need to see if any of the
	    // fields are already defined in 'this' object; we just go ahead
	    // and copy all the definitions from 'other' to 'this'.

	    for (int i = 0; i < fields.length; i++)
	    {
	        fieldname = fields[i];
                this.data.put(fieldname, other.get(fieldname));
	    }
	}
	else
	{
	    for (int i = 0; i < fields.length; i++)
	    {
	        fieldname = fields[i];
	        if (!this.data.containsKey(fieldname))
		{
                    this.data.put(fieldname, other.get(fieldname));
		}
	    }
	}
	return;
    }

    /* -------------------------------------------------------------------- */

    /** get a String representation of this DTO.
    * @return String a representation of the data in this DTO, similar to:
    *    <P>
    *    {<BR>
    *    fieldname1 : object1.toString()<BR>
    *    fieldname2 : object2.toString()<BR>
    *    fieldname3 : --null--<BR>
    *    }
    *    <P>
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public String toString ()
    {
	// array of all fieldnames defined in this DTO
	String[] keys = this.getFields();

	// the output produced so far
        StringBuffer s =  new StringBuffer();

	// String representation of the object for the current fieldname
	String value = null;

	s.append ("{");			// opening brace
	s.append ("\n");

	// add a line for each field, being careful to report null values well

        for (int i = 0; i < keys.length; i++)
	{
	    value = this.getString(keys[i]);

	    s.append (keys[i]);
	    s.append (" : ");

	    if (value != null)
	    {
	        s.append (value);
	    }
	    else
	    {
	        s.append ("--null--");
	    }
	    s.append ("\n");
	}

	s.append ("}");			// closing brace
	s.append ("\n");

	return s.toString();
    }

    /* -------------------------------------------------------------------- */

    ////////////////////////
    // public static methods
    ////////////////////////

    /** factory method -- get an empty DTO for use.
    * @return an empty DTO
    * @assumes nothing
    * @effects removes a DTO from DTO.POOL if possible
    * @throws nothing
    */
    public static DTO getDTO ()
    {
	// since this could be used in a threaded environment, we must be
	// careful to synchronize our access to the class variable POOL

	synchronized (POOL)
	{
            if (!POOL.empty())
	    {
	        return (DTO) POOL.pop();
	    }
	}
	return new DTO();
    }

    /* -------------------------------------------------------------------- */

    /** make the given DTO available for re-use.
    * @param dto the DTO with which we have finished and which we want to
    *    make available for re-use.
    * @return nothing
    * @assumes nothing
    * @effects adds the given DTO to the DTO.POOL, if possible
    * @throws nothing
    * @notes We make sure that we do not add the DTO to the pool if there is
    *    already one reference to it.  (Ideally, this should not happen
    *    anyway, but we try to be extra-careful to prevent obscure bugs that
    *    might pop up later if DTOs ended up getting shared by mistake.)
    */
    public static void putDTO (DTO dto)
    {
	// true if we added 'dto' to the pool of available DTOs
	boolean added = false;

	// if the pool is already full, then don't add to it.  Note that we
	// do not synchronize this, so it is possible to have a pool that is
	// slightly larger than the expected pool size.  (We try to minimize
	// the amount of synchronized code, to increase efficiency.)

	if (POOL.size() < POOL_SIZE)
	{
	    synchronized (POOL)
	    {
	        if (POOL.search(dto) == -1)
	        {
	            POOL.push(dto);
		    added = true;
	        }
	    }

	    // if we added the DTO back to the pool, then we should reset it.
	    // otherwise, we just let the garbage collector do its work.

	    if (added)
	    {
	        dto.reset();
	    }
	}
	return;
    }

    /* -------------------------------------------------------------------- */

    /** set the size of the pool of available DTO objects.
    * @param size approximate maximum size of the pool of DTO objects.
    * @return nothing
    * @assumes nothing
    * @effects updated the class variable POOL_SIZE
    * @throws nothing
    * @notes Changing the pool size using this method does not immediately
    *    add items to the pool or remove items from it.  We just let the
    *    'putDTO()' method handle a gradual increase/decrease to the specified
    *    size over time.
    */
    public static void setPoolSize (int size)
    {
        POOL_SIZE = size;
	return;
    }

    /* -------------------------------------------------------------------- */

    ///////////////////////////
    // private member variables
    ///////////////////////////

    // the mapping of fieldnames to values in this DTO
    private HashMap data = null;

    /* -------------------------------------------------------------------- */

    //////////////////////////
    // private class variables
    //////////////////////////

    // a simple, one-item String array used to specify an array type for the
    // Set.toArray() method.  We define it once as a class variable to avoid
    // redefining it each time the getFields() method is called.
    private static String[] STRING_ARRAY = new String[1];

    // the pool of available DTO objects
    private static Stack POOL = new Stack();

    // approximate maximum count of items in the 'POOL', though it may vary
    // slighly above sometimes.
    private static int POOL_SIZE = 100;
}

/*
* $Log$
* Revision 1.1  2013/01/30 18:04:13  mgiadmin
* postgres convert
*
* Revision 1.4  2005/08/05 16:40:42  mbw
* merged code from branch lib_java_core-tr6046-2
*
* Revision 1.3  2005/08/05 16:29:18  mbw
* merged code from branch lib_java_core-tr6427-1
*
* Revision 1.2.6.2  2005/08/01 19:20:10  mbw
* javadocs only
*
* Revision 1.2.6.1  2005/08/01 19:14:12  mbw
* javadocs only
*
* Revision 1.2.14.1  2005/08/01 19:28:54  mbw
* javadocs only
*
* Revision 1.2  2004/07/21 20:25:12  mbw
* javadocs edits only
*
* Revision 1.1  2003/12/30 16:56:27  mbw
* imported into this product
*
* Revision 1.2  2003/12/01 13:10:05  jsb
* Updated to implement Map, other code review changes
*
* Revision 1.1  2003/07/03 17:29:54  jsb
* initial addition for use by JSAM WI
*
* $Copyright$
*/
