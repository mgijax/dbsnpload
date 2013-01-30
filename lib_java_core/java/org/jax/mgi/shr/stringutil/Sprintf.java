package org.jax.mgi.shr.stringutil;

import java.util.Vector;
import java.util.Iterator;
import java.text.DecimalFormat;
import java.lang.Math;

/* Author: jsb
*  Modification History:
*	07/01/2002: As a result of code review, reorganized a bit and added
*		design comments to the top of the file.
*	June 2002: created class for Look & Feel release (TR 3364)
*/

/* Design Notes:
*
* This file includes three classes (Sprintf, Token, and FormatToken) which
* cooperate to produce the functionality desired.
*
* The Sprintf class is the public class, providing static methods for doing
* the formatting.  The main logic is included in the method with the
* signature:
*	public static String sprintf (String pattern, Object[] items)
* This method uses the "tokenize()" method to break the pattern into tokens.
* Each of these tokens may be a standard Token or a FormatToken.  The
* sprintf() method then uses the FormatTokens to format the given items.
*
* The Token class represents one piece of the pattern string.  A standard
* Token cannot be used to format an object.  It can be thought of as just a
* string.
*
* The FormatToken class is a subclass of Token.  It represents a format
* specification from the pattern string.  A FormatToken contains the logic for
* formatting an object according to the particular format specification.
*/

/* ======================================================================== */
/* ======================================================================== */

/** The <tt>Sprintf</tt> class provides improved support for dynamic
*	generation of <tt>String</tt>s, analagous to C's <tt>sprintf</tt>
*	function and Python's percent (%) operator.
* <P>
* The first parameter to any call of a <tt>sprintf</tt> method is a
* <tt>pattern</tt> String.  This pattern string may include conversion
* specifications.  The i-th conversion specification will be replaced by
* the (specially formatted) value of the i-th object passed along with the
* pattern.  Up to five Objects may be passed in as parameters, or more than
* five may be passed in as an <tt>Object[]</tt> or as a <tt>Vector</tt> of
* Objects.  An <tt>IllegalArgumentException</tt> is raised if the pattern
* string expects fewer or more parameters than are supplied.
* <P>
* A conversion specification is made up of the following components:<BR>
* <BLOCKQUOTE>
*<TT>%[&lt;leader&gt;][&lt;width&gt;][.&lt;precision&gt;]&lt;type&gt;<BR></TT>
* </BLOCKQUOTE>
* The components of this specification are:
* <TABLE BORDER>
* <TR BGCOLOR=eeeeee><TH>component<TH>explanation
* <TR><TD> %
*	<TD> All conversion specifications begin with a '%' character.
* <TR><TD> leader
*	<TD> Optionally, you may specify a "leader" which instructs Sprintf
*		to use special formatting for this field.  Possible values
*		include:<BR>
*		<CENTER>
*		<TABLE BORDER=1>
*		<TR BGCOLOR=eeeeee><TH>leader</TH><TH>meaning</TH></TR>
*		<TR><TD>+</TD><TD> prepend a '+' sign for positive numbers
*			</TD></TR>
*		<TR><TD>#</TD><TD> prepend a '0x' for hex numbers or a '0'
*			for octal numbers</TD></TR>
*		<TR><TD>-</TD><TD> left-align the value (right-align is
*			default)</TD></TR>
*		<TR><TD>~</TD><TD> center the value</TD></TR>
*		</TABLE>
*		</CENTER>
* <TR><TD> width
*	<TD> Optionally, you may specify the minimum number of characters
*		for this field.  If the actual value takes fewer, it is padded
*		with spaces.  If it takes more, it will not be truncated. Note
*		that this may <I>not</I> be specified using a '*' as in C.
* <TR><TD> precision
*	<TD> Optionally, you may specify a precision after a decimal point.
*		The precision identifies how many places to the right of the
*		decimal point should be included (for numbers), or how many
*		characters of the string should be included (for strings, 
*		beginning at the left side of the string).
*		Note that this may <I>not</I> be specified using a '*' as in
*		C.
* <TR><TD> type
*	<TD> Specifies the type of format conversion to do.  Possible values
*		include:
*		<CENTER>
*		<TABLE BORDER=1 WIDTH="80%">
*		<TR BGCOLOR=eeeeee><TH>character</TH><TH>meaning</TH></TR>
*		<TR><TD>d, i</TD><TD>integer number; if formatting a floating
*			point number, then truncate to the integer
*			portion</TD></TR>
*		<TR><TD>o</TD><TD>octal number; if formatting a floating point
*			number, then truncate to the integer portion</TD></TR>
*		<TR><TD>x</TD><TD>hex number with lowercase letters; if
*			formatting a floating point number, then truncate to
*			the integer portion</TD></TR>
*		<TR><TD>X</TD><TD>hex number with uppercase letters; if
*			formatting a floating point number, then truncate to
*			the integer portion</TD></TR>
*		<TR><TD>s</TD><TD>string; converts any object with a
*			<tt>toString()</tt> method to a string, and formats
*			using that value</TD></TR>
*		<TR><TD>c</TD><TD>character; converts any object with a
*			<tt>toString()</tt> method to a string, and formats
*			using the first character of that value</TD></TR>
*		<TR><TD>f</TD><TD>floating point; default is a precision of
*			six decimal places</TD></TR>
*		<TR><TD>e, E</TD><TD>scientific notation; default is a
*			precision of six decimal places</TD></TR>
*		<TR><TD>g, G</TD><TD>general floating point; formats as 'f'
*			unless the exponent would be less than -4 or greater
*			than or equal to the precision (default 6), in which
*			case it would format as 'e'.  Note that this does
*			<I>not</I> omit trailing zeroes or decimal points, as
*			in C.</TD></TR>
*		<TR><TD>b</TD><TD>boolean; displays boolean values as the
*			strings 'true' and 'false'.  converts non-boolean
*			objects as follows:<BR>
*			<UL>
*			<LI> String: false if zero-length, true otherwise
*			<LI> Integer or Long: false if 0, true otherwise
*			<LI> Float or Double: false if 0.0, true otherwise
*			<LI> Character: always true
*			</UL></TD></TR>
*		</TABLE>
*		</CENTER>
* </TABLE>
* <P>
* Any "%" characters which are not part of valid conversion
* specification definition given above are treated as simply part of the
* string.  You may also use the traditional "%%" notation to include a single
* "%" in the string if needed.  (ie- in areas which would look like
* conversion specifications)
*/
public class Sprintf
{
    /** hide the default constructor so no Sprintf objects can be created
    */
    private Sprintf()
    {
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		objects contained in <tt>items</tt>.
    *	@param pattern a <tt>String</tt> containing text and conversion
    *	    specifications of any type appropriate to the type of Objects
    *	    according to the conversion rules for 'type' near the top of this
    *	    file.
    *	@param items an array of objects, with one per conversion
    *	    specification in <tt>pattern</tt>.
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specifications have been replaced by the values of
    *	    the objects in <tt>items</tt>
    *	@throws IllegalArgumentException if a given object in <tt>items</tt>
    *	    cannot be converted as specified by its corresponding conversion
    *	    specification in <tt>pattern</tt>
    */
    public static String sprintf (String pattern, Object[] items)
	throws IllegalArgumentException
    {
	Token tok = null;                   // steps through tokens in pattern
	StringBuffer s = new StringBuffer();        // what we're constructing

        Vector vec = Sprintf.tokenize (pattern);        // tokens in 'pattern'
	Iterator it = vec.iterator();                // iterates over tokens
	int i = 0;                              // counts formattable tokens

	while (it.hasNext())
	{
	    tok = (Token) it.next();
	    if (tok.canFormat())        // do we need to use an 'item' here?
	    {
		if (i >= items.length)
		{
		    throw new IllegalArgumentException (
			"Not enough arguments for the given pattern string");
		}
		s.append (((FormatToken) tok).format (items[i]));
		i = i + 1;
	    }
	    else                        // or just add the string as-is?
	    {
		s.append (tok.toString());
	    }
	}
	if (items.length > i)
	{
	    throw new IllegalArgumentException (
		"Too many arguments for the given pattern string");
		
	}
	return s.toString();
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		objects contained in <tt>items</tt>.
    *	@param pattern a <tt>String</tt> containing text and conversion
    *	    specifications
    *	@param items a <tt>Vector</tt> of objects, with one per conversion
    *	    specification in <tt>pattern</tt>.
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specifications have been replaced by the values of
    *	    the objects in <tt>items</tt>
    *	@throws IllegalArgumentException if a given object in <tt>items</tt>
    *	    cannot be converted as specified by its corresponding conversion
    *	    specification in <tt>pattern</tt>
    */
    public static String sprintf (String pattern, Vector items)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, items.toArray());
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of one of the following types:  b, s, c
    *	@param item a boolean variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt>
    */
    public static String sprintf (String pattern, boolean item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Boolean[] { new Boolean(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of any type
    *	@param item an integer variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt> (should
    *	    not happen)
    */
    public static String sprintf (String pattern, int item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Integer[] { new Integer(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of any type
    *	@param item a long variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt> (should
    *	    not happen)
    */
    public static String sprintf (String pattern, long item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Long[] { new Long(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of any type
    *	@param item a float variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt> (should
    *	    not happen)
    */
    public static String sprintf (String pattern, float item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Float[] { new Float(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of any type
    *	@param item a double variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt> (should
    *	    not happen)
    */
    public static String sprintf (String pattern, double item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Double[] { new Double(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of any type
    *	@param item a short variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt> (should
    *	    not happen)
    */
    public static String sprintf (String pattern, short item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Short[] { new Short(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of any type
    *	@param item a byte variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt> (should
    *	    not happen)
    */
    public static String sprintf (String pattern, byte item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Byte[] { new Byte(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of type:  c, b, or s
    *	@param item a char variable
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt>
    */
    public static String sprintf (String pattern, char item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Character[] {
						new Character(item) });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>.
    *	@param pattern a <tt>String</tt> containing text and a conversion
    *	    specification of any type appropriate to the type of Object
    *	    according to the conversion rules for 'type' near the top of this
    *	    file.
    *	@param item an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of
    *	    <tt>item</tt>
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt>
    */
    public static String sprintf (String pattern, Object item)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Object[] { item });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>s.
    *	@param pattern a <tt>String</tt> containing text and conversion
    *	    specifications of any type appropriate to the type of Objects
    *	    according to the conversion rules for 'type' near the top of this
    *	    file.
    *	@param item1 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item2 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of the
    *	    <tt>item</tt>s
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt>s
    */
    public static String sprintf (String pattern, Object item1, Object item2)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Object[] { item1, item2 });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>s.
    *	@param pattern a <tt>String</tt> containing text and conversion
    *	    specifications of any type appropriate to the type of Objects
    *	    according to the conversion rules for 'type' near the top of this
    *	    file.
    *	@param item1 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item2 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item3 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of the
    *	    <tt>item</tt>s
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt>s
    */
    public static String sprintf (String pattern, Object item1, Object item2,
            Object item3)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Object[] { item1, item2, 
                    item3 });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>s.
    *	@param pattern a <tt>String</tt> containing text and conversion
    *	    specifications of any type appropriate to the type of Objects
    *	    according to the conversion rules for 'type' near the top of this
    *	    file.
    *	@param item1 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item2 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item3 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item4 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of the
    *	    <tt>item</tt>s
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt>s
    */
    public static String sprintf (String pattern, Object item1, Object item2,
            Object item3, Object item4)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Object[] { item1, item2, 
                    item3, item4 });
    }

    /* -------------------------------------------------------------------- */

    /** replace the conversion specifications in <tt>pattern</tt> with the
    *		given <tt>item</tt>s.
    *	@param pattern a <tt>String</tt> containing text and conversion
    *	    specifications of any type appropriate to the type of Objects
    *	    according to the conversion rules for 'type' near the top of this
    *	    file.
    *	@param item1 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item2 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item3 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item4 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@param item5 an Object (can be String, Character, Float, Double,
    *	    Boolean, Integer, Long, or any type with a <tt>toString()</tt>
    *	    method)
    *	@return <tt>String</tt> which is <tt>pattern</tt> after the
    *	    conversion specification has been replaced by the value of the
    *	    <tt>item</tt>s
    *	@throws IllegalArgumentException if there is a mismatch between the
    *	    the conversion specification and the given <tt>item</tt>s
    */
    public static String sprintf (String pattern, Object item1, Object item2,
            Object item3, Object item4, Object item5)
	throws IllegalArgumentException
    {
	return Sprintf.sprintf (pattern, new Object[] { item1, item2, 
                    item3, item4, item5 });
    }

    /* -------------------------------------------------------------------- */

    /** split the 'pattern' String into a Vector of Tokens.  Each Token 
    *   represents either a simple String or a conversion specification
    *   to be filled in by an Object's value
    * @param pattern the <tt>String</tt> containing text and conversion
    *	    specifications
    * @return Vector of Tokens
    */
    private static Vector /* of Token */ tokenize (String pattern)
    {
	Vector tokens = new Vector();	// collects Tokens as we find them
	int pos = -1;			// index into 'pattern' of '%' char
	int lastPos = 0;		// processed 'pattern' up to where
	int tokenEnd = 0;		// ending index (into 'pattern') of
					// ... the current Token

	pos = pattern.indexOf ("%");
	while (pos != -1)
	{
	    // if there's a gap between where we've processed up to and where
	    // we found a new '%' character, then make that gap into a Token

	    if (pos != lastPos)
	    {
	        tokens.add (new Token (pattern.substring (lastPos, pos)));
	    }

	    // check the current 'pos' for a Token, adding it to 'tokens'
	    // if we find one.

	    tokenEnd = Sprintf.processToken (pattern, pos, tokens);
	    if (tokenEnd > pos)
	    {
	        lastPos = tokenEnd;	// now done through end of the Token
	    }
	    pos = pattern.indexOf ("%", tokenEnd + 1);
	}

	// add any remaining characters as a Token, too, to finish processing
	// the 'pattern'

	tokens.add (new Token (pattern.substring (lastPos)));
	return tokens;
    }

    /* -------------------------------------------------------------------- */

    /** examine 'pattern' starting at the given position ('pos') to find
    *	a conversion specification if possible.  If found, add a new
    *	FormatToken to 'tokens' and return the ending position.  Otherwise
    *	return 'pos' as-is.
    * @param pattern the <tt>String</tt> containing text and conversion
    *	    specifications
    * @param pos the index in 'pattern' where we found the next '%' character
    *	    to examine
    * @param tokens the Vector of Tokens found so far
    * @return integer index into 'pattern' of where we've processed up to...
    */
    private static int processToken (String pattern, int pos, Vector tokens)
    {
	FormatToken fToken = new FormatToken();	// what we're building
	int i = pos + 1;			// walks through 'pattern'
	char c = pattern.charAt (i);		// character at position 'i'
						// ... always the next char
						// ... to be processed
	StringBuffer s = new StringBuffer();	// collects characters for
						// ... width and precision

	// handle the case of a "%%" appearing together...

	if (c == '%')
	{
	    tokens.add (new Token ("%"));
	    return i + 1;
	}

	// handle a leader

	switch (c)
	{
	    case '+':
	    case '#':
	    case '-':
	    case '~': fToken.setLeader (c);
			i = i + 1;
			c = pattern.charAt (i);
			break;
	}

	// collect the width specified

	while ((c >= '0') && (c <= '9'))
	{
	    s.append (c);
	    i = i + 1;
	    c = pattern.charAt (i);
	}
	if (s.length() > 0)
	{
	    fToken.setMinWidth (s.toString());
	    s.delete (0, s.length());
	}

	// collect the precision specified (must start with a decimal point)

	if (c == '.')
	{
	    i = i + 1;
	    c = pattern.charAt (i);
	    while ((c >= '0') && (c <= '9'))
	    {
		s.append (c);
		i = i + 1;
		c = pattern.charAt (i);
	    }
	    if (s.length() > 0)
	    {
		fToken.setPrecision (s.toString());
	    }
	}

	// handle the format specifier

	switch (c)
	{
	    case 's':
	    case 'c':
	    case 'b':
	    case 'x':
	    case 'X':
	    case 'o':
	    case 'f':
	    case 'e':
	    case 'E':
	    case 'g':
	    case 'G':
	    case 'd' :
	    case 'i' : fToken.setSpecifier (c);
			break;
	    default: return pos;	// if no specifier, then this was not
					// ... a conversion specification.
					// ... just return 'pos' to say "no
					// ... formatToken here".
	}
	tokens.add (fToken);
	return i + 1;
    }
}

/* ======================================================================== */
/* ======================================================================== */

/** A Token is one piece of a pattern String.  This base Token class
*    represents a simple String, which is a substring of a pattern String.
*/
class Token
{
    /* -------------------------------------------------------------------- */

    /** default constructor - initializes a Token with an empty String
    */
    public Token ()
    {
    }

    /* -------------------------------------------------------------------- */

    /** constructor - initializes a Token containing the given String 's'
    * @param s the String to be contained in this Token
    */
    public Token (String s)
    {
	this.contents = s;
    }

    /* -------------------------------------------------------------------- */

    /** can this Token be used to format an Object?
    * @return boolean <tt>false</tt> because this base Token class just deals
    *    with Strings and not conversion specifications.
    */
    public boolean canFormat ()
    {
        return false;
    }

    /* -------------------------------------------------------------------- */

    /** use this Token to format the given 'item'
    * @throws IllegalArgumentException - since a standard Token cannot be used
    *	to format an 'item', we throw this exception.  (should not occur)
    */
    public String format (Object item) throws IllegalArgumentException
    {
        throw new IllegalArgumentException ("Token cannot format item");
    }

    /* -------------------------------------------------------------------- */

    /** retrieve the String used to construct this Token
    * @return String the String used to construct this Token
    */
    public String toString ()
    {
	return this.contents;
    }

    /* -------------------------------------------------------------------- */

    String contents = "";	// the String used to construct this Token
}

/* ======================================================================== */
/* ======================================================================== */

/** a FormatToken represents a conversion specification - a string which may
*	be used to provide special formatting for an object.  It is a subclass
*	of the base Token class.
*/
class FormatToken extends Token
{
    /* -------------------------------------------------------------------- */

    /** use this FormatToken to generate a String which formats 'item'
    * @param item the Object to be formatted
    * @return String the String which represents the specially formatted value
    *	of 'item'
    * @throws IllegalArgumentException if the format specifier is unknown or
    *	if there is another problem in doing the formatting
    */
    public String format (Object item) throws IllegalArgumentException
    {
	// different handling for different format specifiers...

	switch (this.specifier)
	{
	    case 'd':
	    case 'i': return this.intFormat (item);
	    case 's': return this.stringFormat (item);
	    case 'c': return this.charFormat (item);
	    case 'b': return this.booleanFormat (item);
	    case 'x':
	    case 'X': return this.hexFormat (item);
	    case 'o': return this.octalFormat (item);
	    case 'f': return this.floatFormat (item);
	    case 'e':
	    case 'E': return this.sciFormat (item);
	    case 'g':
	    case 'G': return this.generalFormat (item);
	}
	// should never get here:

	throw new IllegalArgumentException ("Unknown specifier: " +
			this.specifier);
    }

    /* -------------------------------------------------------------------- */

    /** can this Token be used to format an Object?
    * @return boolean <tt>true</tt> because this FormatToken class is designed
    *    just for that purpose
    */
    public boolean canFormat ()
    {
        return true;
    }

    /* -------------------------------------------------------------------- */

    /** sets the "leader" for this FormatToken, identifying special number
    *	formatting or string justification
    * @param c the character which is the "leader" for this conversion
    *	specification.  should be one of these: '-', '+', '#', '~'
    */
    public void setLeader (char c)
    {
        this.leader = c;
    }

    /* -------------------------------------------------------------------- */

    /** sets the minimum width for strings produced by this FormatToken.
    * @param s should be a String which can be parsed into an Integer
    */
    public void setMinWidth (String s)
    {
        this.width = Integer.parseInt (s);
    }

    /* -------------------------------------------------------------------- */

    /** sets the precision for strings produced by this FormatToken
    * @param s should be a String which can be parsed into an Integer
    */
    public void setPrecision (String s)
    {
        this.precision = Integer.parseInt (s);
    }

    /* -------------------------------------------------------------------- */

    /** sets the format specifier for this FormatToken.  should be one of the
    *	following letters: d, i, o, x, X, s, c, f, e, E, g, G, b
    * @param c format specifier
    */
    public void setSpecifier (char c)
    {
        this.specifier = c;
    }

    /* -------------------------------------------------------------------- */

    /** rebuilds and returns the conversion specification represented by this
    *	FormatToken object
    * @return String this object's conversion specification
    */
    public String toString ()
    {
	StringBuffer s = new StringBuffer();
	s.append ("%");
	if (this.width >= 0)
	{
	    s.append (Integer.toString (this.width));
	}
	if (this.precision >= 0)
	{
	    s.append (".");
	    s.append (Integer.toString (this.precision));
	}
	s.append (this.specifier);
	return s.toString();
    }

    /* -------------------------------------------------------------------- */

    ///////////////////
    // private methods:
    ///////////////////

    /** returns 'item' formatted according to a "%s" specification.
    *		converts 'item' to a String, then handles justification
    *		as needed.
    * @param item the object to be formatted as a String
    * @return <tt>String</tt> 'item' formatted according to this "%s"
    *		specification
    */
    private String stringFormat (Object item)
    {
	String c = item.toString();

	// if there's a precision specified, then use a substring from the
	// left side of the string

	if (this.precision >= 0)
	{
	    return justify (c.substring (0, this.precision));
	}
	return justify (c);
    }

    /* -------------------------------------------------------------------- */

    /** returns a String consisting of the first character of the String
    *		representation of the given 'item', justified according to
    *		this "%c" format specification
    * @param item the object to be formatted as a character
    * @return <tt>String</tt> 'item' formatted according to this "%c"
    *		specification
    */
    private String charFormat (Object item)
    {
	String itemStr = item.toString();

	if (itemStr.length() >= 1)
	{
	    return justify (itemStr.substring(0,1));
	}
	else
	{
	    return justify (" ");	// use a space for zero-length string
	}
    }

    /* -------------------------------------------------------------------- */

    /** handle displaying 'item' as a boolean "true" or "false", with these
    *	conversions:
    *		Boolean - as expected
    *		String - false if zero-length, true otherwise
    *		Long, Integer - false if zero, true otherwise
    *		Float, Double - false if zero, true otherwise
    *		Character - always true
    * @param item the object to be formatted as a boolean-encoding String
    */
    private String booleanFormat (Object item)
    {
	String objClass = item.getClass().getName();	// object's class name
	boolean value = true;				// default to return

	if (objClass.equals (BooleanClass))
	{
	    value = ((Boolean) item).booleanValue();
	}
	else if (objClass.equals (StringClass))
	{
	    value = (((String) item).length() >= 1);
	}
	else if (objClass.equals (IntegerClass))
	{
	    value = (((Integer) item).intValue() != 0);
	}
	else if (objClass.equals (LongClass))
	{
	    value = (((Long) item).longValue() != 0L);
	}
	else if (objClass.equals (FloatClass))
	{
	    value = (((Float) item).floatValue() != 0.0F);
	}
	else if (objClass.equals (DoubleClass))
	{
	    value = (((Double) item).doubleValue() != 0.0D);
	}

	if (value)
	{
	    return justify ("true");
	}
	return justify ("false");
    }

    /* -------------------------------------------------------------------- */

    /** return a String representing 'item' as an integer value.
    * @param item the object to be formatted as an integer-encoding String
    * @throws IllegalArgumentException if 'item' cannot be converted to an int
    */
    private String intFormat (Object item) throws IllegalArgumentException
    {
	String s = null;

	if (!this.isNumeric (item))
	{
	    throw new IllegalArgumentException (
			"Incompatible class for %i or %d: "
				+ item.getClass().getName());
	}

	// for Float and Double items, just truncate to the integer portion

	s = "" + ((Number) item).longValue();

	// for positive numbers, we may need to prepend a plus sign

	if (((Number) item).longValue() > 0)
	{
	    if (this.leader == '+')
	    {
		s = "+" + s;
	    }
	}
	return justify (s);
    }

    /* -------------------------------------------------------------------- */

    /** return a String representing 'item' as an integer value in hex
    *	notation.
    * @param item the object to be formatted as a String containing an integer
    *	in hex notation.
    * @throws IllegalArgumentException if 'item' cannot be converted to an int
    */
    private String hexFormat (Object item) throws IllegalArgumentException
    {
	String s = null;

	if (!this.isNumeric (item))
	{
	    throw new IllegalArgumentException (
			    "Incompatible class for %x or %X: "
					+ item.getClass().getName());
	}

	s = Long.toHexString (((Number) item).longValue());

	// convert to uppercase characters for hex if needed

	if (this.specifier == 'X')
	{
	    s = s.toUpperCase();
	}

	// we may need to prepend "0x" to denote hex notation

	if (this.leader == '#')
	{
	    s = "0x" + s;
	}
	return justify (s);
    }

    /* -------------------------------------------------------------------- */

    /** return a String representing 'item' as an integer value in octal
    *	notation.
    * @param item the object to be formatted as a String containing an integer
    *	in octal notation.
    * @throws IllegalArgumentException if 'item' cannot be converted to an int
    */
    private String octalFormat (Object item) throws IllegalArgumentException
    {
	String s = null;

	if (!this.isNumeric (item))
	{
	    throw new IllegalArgumentException (
			    "Incompatible class for %o: " + 
				item.getClass().getName());
	}

	s = Long.toOctalString (((Number) item).longValue());

	// we may need to prepend "0" to denote octal notation

	if (this.leader == '#')
	{
	    s = "0" + s;
	}
	return justify (s);
    }

    /* -------------------------------------------------------------------- */

    /** return a String representing 'item' as a floating point value
    * @param item the object to be formatted as a String containing a floating
    *	point number
    * @throws IllegalArgumentException if 'item' cannot be converted to a
    *	double
    */
    private String floatFormat (Object item) throws IllegalArgumentException
    {
	if (!this.isNumeric (item))
	{
	    throw new IllegalArgumentException (
			    "Incompatible class for %f: " + 
				item.getClass().getName());
	}

	checkFormat();			// ensure that this.decFormat exists

	// default is six places to the right of the decimal point

	if (this.precision < 0)
	{
	    this.decFormat.applyPattern (fixedFloat[6]);
	}

	// we have pre-defined constants for 1-10 decimal places

	else if (this.precision <= 10)
	{
	    this.decFormat.applyPattern (fixedFloat[this.precision]);
	}

	// for precision > 10,  we need to compose a format string...

	else
	{
	    StringBuffer s = new StringBuffer();
	    s.append ("0.");
	    for (int i = 0; i < this.precision; i++)
	    {
		s.append ("0");
	    }
	    this.decFormat.applyPattern (s.toString());
	}
	return this.justify (this.decFormat.format (
				((Number) item).doubleValue()));
    }

    /* -------------------------------------------------------------------- */

    /** return a String representing 'item' as a floating point value in
    *	scientific notation
    * @param item the object to be formatted as a String containing a floating
    *	point number in scientific notation
    * @throws IllegalArgumentException if 'item' cannot be converted to a
    *	double
    */
    private String sciFormat (Object item) throws IllegalArgumentException
    {
	if (!this.isNumeric (item))
	{
	    throw new IllegalArgumentException (
			    "Incompatible class for %e or %E: " + 
				item.getClass().getName());
	}

	checkFormat();			// ensure that this.decFormat exists

	// default is six places to the right of the decimal point

	if (this.precision < 0)
	{
	    this.decFormat.applyPattern (expoFixedFloat[6]);
	}

	// we have predefined constants for 1-10 decimal places

	else if (this.precision <= 10)
	{
	    this.decFormat.applyPattern (expoFixedFloat[this.precision]);
	}

	// for precision > 10, we need to build a format string manually

	else
	{
	    StringBuffer s = new StringBuffer();
	    s.append ("0.");
	    for (int i = 0; i < this.precision; i++)
	    {
		s.append ("0");
	    }
	    s.append ("E00");
	    this.decFormat.applyPattern (s.toString());
	}
	return this.justify (this.decFormat.format (
				((Number) item).doubleValue()));
    }

    /* -------------------------------------------------------------------- */

    /** return a String representing 'item' as a floating point value in
    *	either floating point (%f) or exponential (%e) notation.  By default,
    *	we use floating point notation.  If the exponent would be < -4 or
    *	>= the precision, then we use scientific notation.  Note that this
    *	method does not follow the K&R standard of omitting trailing zeroes
    *	and a trailing decimal point.
    * @param item the object to be formatted as a String containing a floating
    *	point number
    * @throws IllegalArgumentException if 'item' cannot be converted to a
    *	double
    */
    private String generalFormat (Object item) throws IllegalArgumentException
    {
	boolean useSci = false;		// should we use scientific notation?
	double val = 0.0;		// absolute value of 'item'
	double upper = 10.0;		// upper limit for using float-format

	if (!this.isNumeric (item))
	{
	    throw new IllegalArgumentException (
			    "Incompatible class for %e or %E: " + 
				item.getClass().getName());
	}
	checkFormat();			// ensure that this.decFormat exists

	val = Math.abs (((Number) item).doubleValue());

	// we need to use scientific notation for exponents < -4
	// or for exponents >= the precision

	if ((val > 0.0) && (val < 0.0001))	// exponent < -4
	{
	    useSci = true;
	}
	else
	{
	    for (int i = 0; i < this.precision; i++)
	    {
	        upper = upper * 10.0;
	    }
	    if (val >= upper)			// exponent >= precision
	    {
	        useSci = true;
	    }
	}

	if (useSci)
	{
	    return this.sciFormat (item);
	}
        return this.floatFormat (item);
    }

    /* -------------------------------------------------------------------- */

    /** ensure that the decFormat attribute has been initialized for this
    *	object.  (We don't want to instantitate this.decFormat unless we
    *	really need it.)
    */
    private void checkFormat ()
    {
	if (this.decFormat == null)
	{
	    this.decFormat = new DecimalFormat ();
	}
    }

    /* -------------------------------------------------------------------- */

    /** determine if 'item' is one of the following numeric classes:
    *		Integer, Long, Float, Double
    * @return boolean true if 'item' is of a numeric class, false if not
    */
    private boolean isNumeric (Object item)
    {
	String objClass = item.getClass().getName();
	return (objClass.equals (IntegerClass) ||
		objClass.equals (LongClass) ||
		objClass.equals (FloatClass) ||
		objClass.equals (DoubleClass));
    }

    /* -------------------------------------------------------------------- */

    /** do left, right, or centered justification for 'c' based on the
    *		width and leader attributes of this object
    * @return String containing 'c', justified according to this.leader
    *		and this.width
    */
    private String justify (String c)
    {
	if (this.width >= 0)
	{
	    switch (this.leader)
	    {
		case '-' : return StringLib.ljust (c, this.width);
		case '~' : return StringLib.center (c, this.width);
		default  : return StringLib.rjust (c, this.width);
	    }
	}
	return c;
    }

    /* -------------------------------------------------------------------- */

    //////////////////////
    // instance variables:
    //////////////////////

    private char leader    = 'n';	// 'n' means normal (internal use)
    private int  width     = -1;	// minimum field width
    private int  precision = -1;	// number of decimal places or...
					// characters to print from a string
    private char specifier = 's';	// one of: 'discobxXfeEgG'

    private DecimalFormat decFormat = null;	// used to format float and
						// double values.  We have
						// one (or zero) per object
						// to ensure thread-safety.
    ///////////////////
    // class variables:
    ///////////////////

    private static String CharClass    = "java.lang.Character";
    private static String StringClass  = "java.lang.String";
    private static String FloatClass   = "java.lang.Float";
    private static String DoubleClass  = "java.lang.Double";
    private static String IntegerClass = "java.lang.Integer";
    private static String LongClass    = "java.lang.Long";
    private static String BooleanClass = "java.lang.Boolean";

    // predefined format strings for formatting floating point numbers:

    private static String[] fixedFloat = new String[] {
						"0.",
						"0.0",
						"0.00",
						"0.000",
						"0.0000",
						"0.00000",
						"0.000000",
						"0.0000000",
						"0.00000000",
						"0.000000000",
						"0.0000000000"
						};

    // predefined format strings for formatting floating point numbers
    // in scientific notation:

    private static String[] expoFixedFloat = new String[] {
						"0.E00",
						"0.0E00",
						"0.00E00",
						"0.000E00",
						"0.0000E00",
						"0.00000E00",
						"0.000000E00",
						"0.0000000E00",
						"0.00000000E00",
						"0.000000000E00",
						"0.0000000000E00"
						};
}
