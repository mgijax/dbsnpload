package org.jax.mgi.shr.stringutil;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.ArrayList;

/* Author: jsb
*  Modification History:
*    09/25/2002: Changed from Vector to Collection after Java WI code review.
*    07/01/2002: As a result of code review, we added methods:
*			join (Object[] v, String delim)
*			join (Object[] v)
*			join (Vector v)
*			justifyCenter (String s, int width)
*			justifyLeft (String s, int width)
*			justifyRight (String s, int width)
*		We also made the class constructor private.
*    June 2002: created class for Look & Feel release (TR 3364)
*/

/** StringLib includes static methods for manipulating Strings in certain ways
* which are not convenient in standard Java.
*/
public class StringLib {

    /** hide the default constructor so no StringLib objects can be created
    */
    private StringLib()
    {
    }

    /* -------------------------------------------------------------------- */

    /** return the first String contained in 'array', or null if 'array'
    *    contains no strings
    * @param array an array of Strings
    * @return String
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public static String getFirst (String[] array)
    {
	if ((array == null) || (array.length == 0))
	{
	    return null;
	}
	return array[0];
    }

    /* -------------------------------------------------------------------- */

    /** join the objects in <tt>v</tt> into one String
    * @param v an array of <tt>Object</tt>s which are to have their
    *	<tt>String</tt> representations joined together
    * @return <tt>String</tt> which is the concatenation of the string
    *	representations of the objects in <tt>v</tt>
    */
    public static String join (Object[] v)
    {
	return join (v, "");		// no delimiter between strings
    }

    /* -------------------------------------------------------------------- */

    /** join the objects in <tt>v</tt> into one String, using the given
    *	<tt>delim</tt> delimiter between them.
    * @param v an array of <tt>Object</tt>s which are to have their
    *	<tt>String</tt> representations joined together
    * @param delim the <tt>String</tt> to insert between the strings we are
    *	joining together
    * @return <tt>String</tt> which is the concatenation of the string
    *	representations of the objects in <tt>v</tt>, using <tt>delim</tt> as
    *	the delimiter between them
    */
    public static String join (Object[] v, String delim)
    {
	String s = null;			// steps through v
	boolean isFirst = true;			// first String in v?
	StringBuffer buf = new StringBuffer();	// what we're building

	if (v.length > 0)
	{
	    buf.append (v[0].toString());
	    for (int i = 1; i < v.length; i++)
	    {
		buf.append (delim);
		buf.append (v[i].toString());
	    }
	}
	return buf.toString();
    }

    /* -------------------------------------------------------------------- */

    /** join the objects in <tt>v</tt> into one String
    * @param v a <tt>Collection</tt> of <tt>Object</tt>s which are to have
    *	their <tt>String</tt> representations joined together
    * @return <tt>String</tt> which is the concatenation of the string
    *	representations of the objects in <tt>v</tt>
    */
    public static String join (Collection v)
    {
	return join (v, "");		// no delimiter between strings
    }

    /* -------------------------------------------------------------------- */

    /** join the objects in <tt>v</tt> into one String, using the given
    *	<tt>delim</tt> delimiter between them.
    * @param v a <tt>Collection</tt> of <tt>Object</tt>s which are to have
    *	their <tt>String</tt> representations joined together
    * @param delim the <tt>String</tt> to insert between the strings we are
    *	joining together
    * @return <tt>String</tt> which is the concatenation of the string
    *	representations of the objects in <tt>v</tt>, using <tt>delim</tt> as
    *	the delimiter between them
    */
    public static String join (Collection v, String delim)
    {
	String s = null;			// steps through v
	boolean isFirst = true;			// first String in v?
	StringBuffer buf = new StringBuffer();	// what we're building

	Iterator it = v.iterator();
	while (it.hasNext())
	{
	    s = it.next().toString();
	    if (isFirst)
	    {
		buf.append (s);
		isFirst = false;
	    }
	    else
	    {
		buf.append (delim);
		buf.append (s);
	    }
	}
	return buf.toString();
    }

    /* -------------------------------------------------------------------- */

    /** split the given <tt>String s</tt> into an <tt>ArrayList</tt> of
    *	<tt>String</tt>s, splitting whenever one of the delimiter
    *	characters in <tt>delims</tt> is found.
    * @param s the <tt>String</tt> to be split
    * @param delims contains the characters which are delimiters
    * @return <tt>ArrayList</tt> of <tt>String</tt>s, when <tt>s</tt> is
    *	split at each character in the set of delimiters
    */
    public static ArrayList split (String s, String delims)
    {
	ArrayList vec = new ArrayList();
	StringTokenizer tokenizer = new StringTokenizer (s, delims);

	while (tokenizer.hasMoreTokens())
	{
	    vec.add (tokenizer.nextToken());
	}
	return vec;
    }

    /* -------------------------------------------------------------------- */

    /** split the given <tt>String s</tt> into a <tt>ArrayList</tt> of
    *	<tt>String</tt>s, splitting using the standard set of
    *	delimiter characters -- space, tab, newline, carriage return,
    *	form feed
    * @return <tt>ArrayList</tt> of <tt>String</tt>s, when <tt>s</tt> is
    *	split at each whitespace
    */
    public static ArrayList split (String s)
    {
	return split (s, " \t\n\r\f");
    }

    /* -------------------------------------------------------------------- */

    /** return a String with the contents of <tt>s</tt> right-justified in
    *		<tt>width</tt> characters.  If <tt>s.length() >= width</tt>,
    *		then it just returns <tt>s</tt> as-is.  This function is a
    *		wrapper over <tt>rjust()</tt>.
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String justifyRight (String s, int width)
    {
	return rjust (s, width);
    }

    /* -------------------------------------------------------------------- */

    /** return a String with the contents of <tt>s</tt> left-justified in
    *		<tt>width</tt> characters.  If <tt>s.length() >= width</tt>,
    *		then it just returns <tt>s</tt> as-is.  This function is a
    *		wrapper over <tt>ljust()</tt>.
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String justifyLeft (String s, int width)
    {
	return ljust (s, width);
    }

    /* -------------------------------------------------------------------- */

    /** return a String with the contents of <tt>s</tt> centered in
    *		<tt>width</tt> characters.  If <tt>s.length() >= width</tt>,
    *		then it just returns <tt>s</tt> as-is.  This function is a
    *		wrapper over <tt>center()</tt>.
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String justifyCenter (String s, int width)
    {
	return center (s, width);
    }

    /* -------------------------------------------------------------------- */

    /** build and return a String with the contents of <tt>s</tt> at the
    /** return a String with the contents of <tt>s</tt> right-justified in
    *		<tt>width</tt> characters.  If <tt>s.length() >= width</tt>,
    *		then it just returns <tt>s</tt> as-is.  This function is named
    *		for its equivalent function in Python's string module.
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String rjust (String s, int width)
    {
	return padLeft (s, width, ' ');
    }

    /* -------------------------------------------------------------------- */

    /** return a String with the contents of <tt>s</tt> left-justified in
    *		<tt>width</tt> characters.  If <tt>s.length() >= width</tt>,
    *		then it just returns <tt>s</tt> as-is.  This function is named
    *		for its equivalent function in Python's string module.
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String ljust (String s, int width)
    {
	return padRight (s, width, ' ');
    }

    /* -------------------------------------------------------------------- */

    /** return a String with the contents of <tt>s</tt> centered in
    *		<tt>width</tt> characters.  If <tt>s.length() >= width</tt>,
    *		then it just returns <tt>s</tt> as-is.  This function is named
    *		for its equivalent function in Python's string module.
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String center (String s, int width)
    {
	return padCenter (s, width, ' ');
    }

    /* -------------------------------------------------------------------- */

    /** build and return a String with the contents of <tt>s</tt> at the
    *		right, padded out to the given <tt>width</tt> by adding
    *		copies of the <tt>pad</tt> character to the <I>left</I> side
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String padLeft (String s, int width, char pad)
    {
	int padSize = width - s.length();
	if (padSize <= 0)
	{
	    return s;
	}

	StringBuffer sb = new StringBuffer (width);
	for (int i = 0; i < padSize; i++)
	{
	    sb.append (pad);
	}
	sb.append (s);
	return sb.toString();
    }

    /* -------------------------------------------------------------------- */

    /** build and return a String with the contents of <tt>s</tt> at the
    *		left, padded out to the given <tt>width</tt> by adding
    *		copies of the <tt>pad</tt> character to the <I>right</I> side
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String padRight (String s, int width, char pad)
    {
	int padSize = width - s.length();
	if (padSize <= 0)
	{
	    return s;
	}

	StringBuffer sb = new StringBuffer (width);
	sb.append (s);
	for (int i = 0; i < padSize; i++)
	{
	    sb.append (pad);
	}
	return sb.toString();
    }

    /* -------------------------------------------------------------------- */

    /** return a String with the contents of <tt>s</tt> centered in
    *		<tt>width</tt> characters.  To do the centering, copies of
    *		the <tt>pad</tt> character are added to the left and right
    *		sides as needed.  If <tt>s.length() >= width</tt>,
    *		then it just returns <tt>s</tt> as-is.<P>
    *	Note that for a <tt>width</tt> of odd size, the extra <tt>pad</tt>
    *	goes to the right.
    * @return <tt>String</tt> with length >= <tt>width</tt>
    */
    public static String padCenter (String s, int width, char pad)
    {
	int padSize = width - s.length();
	if (padSize <= 0)
	{
	    return s;
	}

	int halfPad = padSize / 2;
	StringBuffer sb = new StringBuffer (width);
	for (int i = 0; i < halfPad; i++)
	{
	    sb.append (pad);
	}
	sb.append (s);
	for (int i = halfPad; i < padSize; i++)
	{
	    sb.append (pad);
	}
	return sb.toString();
    }

    /** compare 's' and 't' to see if they are equal, handling nulls correctly
    * @param s first String to compare
    * @param t second String to compare
    * @return boolean true if both are null or if both are non-null equal
    *   Strings, false otherwise.
    * @assumes nothing
    * @effects nothing
    */
    public static boolean equals (String s, String t)
    {
        if (s == null)
	{
	    if (t == null)
	    {
	        return true;
	    }
	    return false;
	}
	else
	{
	    if (t == null)
	    {
	        return false;
	    }
	}
        return s.equals(t);
    }
}
