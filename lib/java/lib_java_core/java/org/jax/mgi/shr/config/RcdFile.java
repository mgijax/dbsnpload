package org.jax.mgi.shr.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.jax.mgi.shr.stringutil.StringLib;

/** A RcdFile object gives you mini-database capabilities by providing 
* read-only access to a specially-formatted file.  This file is analagous to
* a database table, and can include both named constants and individual
* Rcds.  Each Rcd (pronounce as "record") is an object which maps fieldnames
* to String values.  These String values may be defined explicitly or by
* including references to pre-defined constants, using the shell's typical
* <tt>${name}</tt> notation.
* <P>
* A RcdFile may contain many Rcds, each of which may contain different
* fieldnames and String values.  The only constraint is that each Rcd must
* contain a unique value for a certain fieldname (analogous to a database
* table's primary key) which is common to all Rcds in that RcdFile.
* <P>
* The <tt>#</tt> character is used to note that the rest of the line should
* be considered a comment and not parsed.  Comments may be included on lines
* by themselves or as the trailing part of other lines.
* <P>
* Leading and trailing whitespace is trimmed from names (fieldnames and names
* of constants) and values.  Quotes are not required around values which
* contain spaces.
* <P>
* Disregarding comments and blank lines, a line in an rcd-formatted file may
* contain:
* <OL>
* <LI> a <tt>[</tt> character to begin a Rcd, if one is not already open,
* <LI> a <tt>]</tt> character to end an Rcd, if one is already open, or
* <LI> a <tt>name = value</tt> pair, where <tt>value</tt> may be an empty
*	string.
* </OL>
* A <tt>name = value</tt> line defines a global constant if it appears outside
* a Rcd definition, or a fieldname and value of an Rcd if it is contained by
* one.  Values for global constants may be referenced when composing other
* values, using the shell's <tt>${name}</tt> notation.
* <P>
* A sample RcdFile is included here, to define three constants and two
* Rcds.  The <tt>PI</tt> field could be used as the key for this RcdFile:
* <P>
* <PRE>
* MGD = Mouse Genome Database
* GXD = Gene Expression Database
* INFORMATICS = @informatics.jax.org
*
* [
* PI = Janan Eppig
* Grant = ${MGD}
* Email = jte${INFORMATICS}
* ]
* 
* [
* PI = Martin Ringwald
* Grant = ${GXD}
* Email = ringwald${INFORMATICS}
* ]
* </PRE>
* To print the value of the Email field for each Rcd, you could use:
* <PRE>
* RcdFile config = new RcdFile ("myfilename", "PI");
* Iterator it = config.getRcds();
* while (it.hasNext())
* {
*	System.out.println ( ((Rcd) it.next()).getString("Email") );
* }
* </PRE>
*/
public class RcdFile
{
    //////////////////
    // public methods:
    //////////////////

    /* -------------------------------------------------------------------- */

    /** constructs a new RcdFile object by reading the given <tt>filename</tt>
    *	    and looking for the given <tt>keyname</tt> to uniquely identify
    *	    each record
    * @param filename path to the rcd-formatted file
    * @param keyname name of the field in each record which will uniquely
    *	    identify each one
    * @throws IOException when there is a problem reading and parsing the file
    * @throws FileNotFoundException when <tt>filename</tt> cannot be found
    */
    public RcdFile (String filename, String keyname)
	    throws IOException, FileNotFoundException
    {
	this.constants = new Hashtable();
	this.rcds = new Hashtable();
	this.filename = filename;
	this.keyname = keyname;
	this.processFile (new File(filename));
    }

    /* -------------------------------------------------------------------- */

    /** get the Rcd object corresponding to the given <tt>key</tt>.  If the
    *	given <tt>key</tt> does not correspond to an Rcd object, <tt>null</tt>
    *	is returned.
    * @param key value for the key field for this RcdFile, which identifies
    *	    individual records uniquely
    * @return Rcd object associated with <tt>key</tt>
    */
    public Rcd get (String key)
    {
	return (Rcd) this.rcds.get (key);
    }

    /* -------------------------------------------------------------------- */

    /** get a count of the number of Rcd objects in this RcdFile
    * @return number of Rcds in this RcdFile
    */
    public int size ()
    {
	return this.rcds.size();
    }

    /* -------------------------------------------------------------------- */

    /** determine if this RcdFile contains a Rcd with the given value for the
    *	    <tt>key</tt> field
    * @param key value for the key field; specifies which Rcd we seek
    * @return <tt>true</tt> if <tt>key</tt> identifies a Rcd in this RcdFile
    */
    public boolean containsKey (String key)
    {
	return this.rcds.containsKey (key);
    }

    /* -------------------------------------------------------------------- */

    /** get an Iterator of the String values for the key field in each Rcd of
    *	    this RcdFile
    * @return <tt>Iterator</tt> of <tt>String</tt>s, each of which is a
    *	    key identifying one Rcd in this RcdFile
    */
    public Iterator keys ()
    {
	Vector myKeys = new Vector();
	Enumeration e = this.rcds.keys();

	while (e.hasMoreElements())
	{
	    myKeys.add (e.nextElement());
	}
	return myKeys.iterator();
    }

    /* -------------------------------------------------------------------- */

    /** get an Iterator of the Rcds comprising this RcdFile
    * @return <tt>Iterator</tt> of <tt>Rcd</tt>s
    */
    public Iterator getRcds ()
    {
	Vector myRcds = new Vector();
	Enumeration e = this.rcds.keys();

	while (e.hasMoreElements())
	{
	    myRcds.add (this.rcds.get ((String) e.nextElement()) );
	}
	return myRcds.iterator();
    }

    /* -------------------------------------------------------------------- */

    /** get the path which was used to instantiate this RcdFile object
    * @return <tt>String</tt> which is the path to the rcd-formatted file
    */
    public String getFilename ()
    {
	return this.filename;
    }

    /* -------------------------------------------------------------------- */

    /** determine whether a constant value by the given <tt>name</tt> is
    *	    defined in this RcdFile
    * @param name name of the constant we seek
    * @return <tt>true</tt> if <tt>name</tt> identifies a constant in this
    *	    RcdFile
    */
    public boolean containsConstant (String name)
    {
	return this.constants.containsKey (name);
    }

    /* -------------------------------------------------------------------- */

    /** get an Iterator of the String names of each constant defined in this
    *	    RcdFile
    * @return <tt>Iterator</tt> of <tt>String</tt>s, each of which is the
    *	    name of a constant in this RcdFile
    */
    public Iterator constants ()
    {
	Vector myKeys = new Vector();
	Enumeration e = this.constants.keys();

	while (e.hasMoreElements())
	{
	    myKeys.add (e.nextElement());
	}
	return myKeys.iterator();
    }

    /* -------------------------------------------------------------------- */

    /** get the value of the constant with the given <tt>name</tt>
    * @param name name of the constant whose value we seek
    * @return <tt>String</tt> value associated with the given <tt>name</tt>
    */
    public String getConstant (String name)
    {
	return (String) this.constants.get (name);
    }

    /* -------------------------------------------------------------------- */

    /** get a <tt>String</tt> representation of this RcdFile
    * @return <tt>String</tt> representation of this RcdFile
    */
    public String toString ()
    {
	Rcd rcd = null;				// steps through Rcds
	StringBuffer s = new StringBuffer();	// string we're building
	Enumeration e = null;		// iterates through constants
						//    then Rcds
	String key = null;			// steps through constants

	// first, add the set of constants to the string 's'

	s.append ("Constants:\n----------\n");
	e = this.constants.keys();
	while (e.hasMoreElements())
	{
	    key = (String) e.nextElement();
	    s.append (key + ": " + (String) this.constants.get (key) + "\n");
	}

	// then add the set of Rcds to the string 's'

	s.append ("\n");
	s.append ("Rcds:\n-----\n");
	e = this.rcds.keys();
	while (e.hasMoreElements())
	{
	    rcd = (Rcd) this.rcds.get ((String) e.nextElement());
	    s.append (rcd.getString (this.keyname) + ": " + rcd.toString() +
		"\n");
	}
	return s.toString();
    }

    ///////////////////
    // private methods:
    ///////////////////

    /* -------------------------------------------------------------------- */

    /** read the contents of <tt>myFile</tt> and use them to initialize this
    *	    RcdFile object
    * @param myFile the rcd-formatted file to read
    * @throws FileNotFoundException if <tt>myFile</tt> does not refer to an
    *	    actual file
    * @throws IOException if there are problems reading and parsing the file
    * @return void
    */
    private void processFile (File myFile)
	    throws FileNotFoundException, IOException
    {
	boolean inRcd = false;		    // are we currently in a record?
	Rcd rcd = null;			    // the Rcd currently being built

	BufferedReader infile = new BufferedReader (new FileReader (myFile));

	// Since a line in a rcd-formatted file can end using a backslash as
	// a line-continuation character, we need to deal with "logical lines"
	// rather than actual ones in the file.

	this.nextLogicalLine (infile);

	while ((this.lineKey != null) && (this.lineKey.length() > 0))
	{
	    if (inRcd)
	    {
		// have we reached the end of this record?
		if (this.lineKey.equals ("]"))
		{
		    // error check - did this Rcd contain the required key?
		    if (!rcd.containsKey (this.keyname))
		    {
			throw new IOException ("Record missing key name '" +
					    this.keyname + "' around line " +
					    this.lineNum);
		    }

		    // error check - does another Rcd with this key exist?
		    if (this.containsKey (rcd.getString(this.keyname)))
		    {
			throw new IOException ("Records with duplicate " +
			    "value '" + rcd.getString(this.keyname) +
			    "' exist for key field '" + this.keyname + "'");
		    }

		    // otherwise, save this Rcd, and get ready to move on to
		    // the next
		    this.rcds.put (rcd.getString(this.keyname), rcd);
		    inRcd = false;
		    rcd = null;
		}

		// error check - cannot open one Rcd inside another
		else if (this.lineKey.equals ("["))
		{
		    throw new IOException ("Extra [ character at line " +
						this.lineNum +
						" in file " + this.filename);
		}

		// otherwise, we just need to store this name-value pair
		// in the current Rcd
		else
		{
		    rcd.add (this.lineKey, this.substitute(this.lineValue));
		}
	    }
	    else    // not inRcd
	    {
		// are we starting a new Rcd?
		if (this.lineKey.equals ("["))
		{
		    rcd = this.getNewRcd();
		    inRcd = true;
		}

		// error check - cannot close a Rcd if one is not open
		else if (this.lineKey.equals ("]"))
		{
		    throw new IOException ("Extra ] character at line " +
					    this.lineNum +
					    " in file " + this.filename);
		}

		// otherwise, we need to store this name-value pair as a
		// defined constant
		else
		{
		    this.constants.put (this.lineKey,
			    this.substitute(this.lineValue));
		}
	    }
	    this.nextLogicalLine (infile);
	}

	// error check - did we end the file with a Rcd still open?
	if (inRcd)
	{
	    throw new IOException ("Final record did not terminate in " +
				    this.filename);
	}
	infile.close();
    }

    /* -------------------------------------------------------------------- */

    /** read the next logical line from <tt>infile</tt>, handling line
    *	    continuation (\) characters appropriately, setting
    *	    this.lineKey and this.lineValue, and advancing this.lineNum
    * @param infile the rcd-formatted configuration file
    * @throws IOException if the next logical line is not formatted properly
    * @return void
    */
    private void nextLogicalLine (BufferedReader infile) throws IOException
    {
	String line = null;	// steps through actual lines from 'infile'
	int commentPos;		// position of # character in 'line'
	int eqPos;		// position of = character in 'line'

	line = infile.readLine();		// get next line from 'infile'
	this.lineNum = this.lineNum + 1;

	this.lineKey = null;			// nothing parsed yet
	this.lineValue = null;

	while (line != null)			// stop on end-of-file
	{
	    // strip off any comments, then trim off all leading and trailing
	    // whitespace

	    commentPos = line.indexOf ("#");
	    if (commentPos > -1)
	    {
		line = line.substring (0, commentPos);
	    }
	    line = line.trim();

	    // are we processing an additional line for one continued by a
	    // backslash character?  (in this case, we would have already
	    // filled in this.lineKey)

	    if (this.lineKey != null)
	    {
		// if this line is also to be continued, then add the contents
		// of this line (minus the backslash) and continue

		if (line.endsWith ("\\"))
		{
		    this.lineValue = this.lineValue + " " +
				line.substring (0, line.length() - 1).trim();
		}

		// otherwise, we can add this line to this.lineValue and exit

		else
		{
		    this.lineValue = this.lineValue + " " + line;
		    return;
		}
	    }

	    // otherwise, we are not working on a continued line

	    else
	    {
		// if we found a record delimiter, then set it as this.lineKey
		// and exit

		if (line.equals ("[") || line.equals ("]"))
		{
		    this.lineKey = line;
		    return;
		}

		// otherwise, look for an equals sign to separate the key
		// from the value

		eqPos = line.indexOf ("=");
		if (eqPos > -1)
		{
		    // collect the key and value, then bail out unless the
		    // line is to-be-continued

		    this.lineKey = line.substring (0, eqPos).trim();
		    this.lineValue = line.substring (eqPos + 1).trim();

		    if (!this.lineValue.endsWith ("\\"))
		    {
			return;
		    }
		    this.lineValue = this.lineValue.substring (0,
					this.lineValue.length() - 1).trim();
		}

		// error check - non-empty lines must have an equals sign

		else if (line.length() > 0)
		{
		    throw new IOException ("Missing = sign on line " + 
					    this.lineNum);
		}
	    }

	    // go back and get the next actual line from the file

	    line = infile.readLine();
	    this.lineNum = this.lineNum + 1;
	}
    }

    /* -------------------------------------------------------------------- */

    /** use the constants we have parsed to fill in any references to
    *	    constants in 's'
    * @param s the String in which we want to expand constant references
    * @return <tt>String</tt> equivalent to <tt>s</tt>, but with the constant
    *	    references expanded
    * @throws IOException if <tt>s</tt> contains a reference to a constant
    *	    that has not yet been defined
    */
    private String substitute (String s) throws IOException
    {
	int pos;		// position of $ character in 's'
	int close;		// position of } character in 's'
	String key = null;	// name of constant being referenced
	int last = 0;		// last position +1 copied into 't'
	int nextToLast;		// index of the next to the last character

	StringBuffer t = new StringBuffer();	// new string we're building

	// if our source string has no $, then we can just return it as-is

	pos = s.indexOf ("$");
	if (pos == -1)
	{
	    return s;
	}

	nextToLast = s.length() - 2;

	// otherwise, we need to replace all constant references in 's',
	// remembering that we'll need to be able to look ahead one character

	while ((pos != -1) && (pos <= nextToLast))
	{
	    // if the next character after the $ is another $, then we should
	    // just convert them to a single $ character for 't'.

	    if (s.charAt(pos + 1) == '$')
	    {
		t.append (s.substring (last, pos));	// add to this point
		t.append ("$");
		last = pos + 2;				// we're past the $$
		pos = s.indexOf ("$", pos + 2);
		continue;				// goes back to top
	    }

	    // otherwise, if the next character is a {, then we have the
	    // start of a reference to a constant

	    else if (s.charAt(pos + 1) == '{')
	    {
		// see where the constant reference ends.  if it does not,
		// then we can just add the rest of the string to 't' and
		// exit

		close = s.indexOf ("}", pos + 2);
		if (close == -1)
		{
		    t.append (s.substring (last));
		    pos = -1;
		}

		// otherwise, we need to replace the constant reference with
		// its value

		else
		{
		    // add up to this point to 't'

		    t.append (s.substring (last, pos));

		    // look up and add the constant's value to 't'

		    key = s.substring (pos+2, close);
		    if (!this.constants.containsKey (key))
		    {
			throw new IOException ("Unknown constant: " + key);
		    }
		    t.append ((String) this.constants.get (key));

		    // get ready to look for the next constant

		    last = close + 1;
		    pos = s.indexOf ("$", last);
		}
	    }

	    // otherwise, this was just a $ character, so go on to look for
	    // the next one (which may start a constant)

	    else
	    {
		pos = s.indexOf ("$", pos + 1);
	    }
	}
	t.append (s.substring (last));		// add any leftover characters
	return t.toString();
    }

    /* -------------------------------------------------------------------- */

    /** get a new Rcd -- override in subclasses for different Rcd types
    * @return an empty Rcd
    */
    private Rcd getNewRcd ()
    {
	return new Rcd();
    }

    //////////////////////
    // instance variables:
    //////////////////////

    private Hashtable rcds = null;	// set of Rcds, indexed by key value
    private Hashtable constants = null;	// set of constants, indexed by name
    private String filename = null;	// name of the file we read
    private String keyname = null;	// name of the key field for Rcds

    private int lineNum = 0;		// count of actual lines read so far
    private String lineKey = null;	// fieldname on current logical line
    private String lineValue = null;	// field value on current logical line

    ///////////////
    // inner class:
    ///////////////

    /* ==================================================================== */

    /** A Rcd object represents one record from a rcd-formatted file.  It maps
    * a set of fieldnames to one or more values each.  See RcdFile.java for
    * more information.
    */
    public static class Rcd
    {
        //////////////////
        // public methods:
        //////////////////

	/* ---------------------------------------------------------------- */

        /** constructs an empty Rcd object
        */
        private Rcd ()
        {
    	    this.values = new Hashtable();
        }

	/* ---------------------------------------------------------------- */

        /** adds the given <tt>value</tt> to the given field <tt>name</tt>
	*   within this object (multiple values per field are allowed)
        * @param name the field for which we want to add a value
        * @param value the value to be added
        * @return void
        */
        private void add (String name, String value)
        {
            if (this.values.containsKey (name))
	    {
	        ((Vector) this.values.get (name)).add (value);
	    }
	    else
	    {
	        this.set (name, value);
	    }
        }

	/* ---------------------------------------------------------------- */

        /** determine if the given field <tt>name</tt> is defined in this Rcd
        * @param name the field name to look for
        * @return <tt>true</tt> if <tt>name</tt> is contained, <tt>false</tt>
	*    if not
        */
        public boolean containsKey (String name)
        {
	    return this.values.containsKey (name);
        }

	/* ---------------------------------------------------------------- */

        /** delete all values for the given field <tt>name</tt> in this Rcd
        * @param name the field name for which values should be removed
        * @return void
        */
        private void delete (String name)
        {
    	    this.values.remove (name);
        }

	/* ---------------------------------------------------------------- */

        /** get a <tt>String</tt> representation of all values of the given
	*    field <tt>name</tt>, separating multiple values with a comma and
	*    a space
        * @param name the name of the field to be retrieved
        * @return <tt>String</tt> representation of the values for the given
	*    field <tt>name</tt>, using a comma and a space as the separator
	*    between multiple values
        */
        public String getString (String name)
        {
	    return this.getString (name, ", ");
        }

	/* ---------------------------------------------------------------- */

        /** get a <tt>String</tt> representation of all values of the given
	*    field <tt>name</tt>, separating multiple values with the given
        *    <tt>separator</tt>
        * @param name the name of the field to be retrieved
        * @param separator String to use to join multiple values together
        * @return <tt>String</tt> representation of the values for the given
	*    field <tt>name</tt>, using the given <tt>separator</tt> between
        *    multiple values
        */
        public String getString (String name, String separator)
        {
	    return StringLib.join ((Vector) this.values.get (name),
					separator);
        }

	/* ---------------------------------------------------------------- */

        /** get an <tt>Iterator</tt> which will step through all the values
        *    for the given field <tt>name</tt>.  Each value will be a
        *    <tt>String</tt>.
        * @param name the name of the field to be retrieved
        * @return an Iterator which walks through the <tt>String</tt> values
        *	    associated with <tt>name</tt>
        */
        public Iterator getIterator (String name)
        {
	    return ((Vector) this.values.get (name)).iterator();
        }

	/* ---------------------------------------------------------------- */

        /** get all the <tt>String</tt> values associated with the given field
        *    <tt>name</tt> in a <tt>Vector</tt>
        * @param name the name of the field to be retrieved
        * @return <tt>Vector</tt> of <tt>String</tt> values associated with
        *    <tt>name</tt>
        */
        public Vector getVector (String name)
        {
	    return (Vector) ((Vector) this.values.get (name)).clone();
        }

	/* ---------------------------------------------------------------- */

        /** get an <tt>Iterator</tt> of all the <tt>String</tt> field names
        *    defined in this Rcd
        * @return <tt>Iterator</tt> of <tt>String</tt>s, each of which is
	*    a key
        */
        public Iterator keys ()
        {
	    Vector myKeys = new Vector();
	    Enumeration e = this.values.keys();

	    while (e.hasMoreElements())
	    {
		myKeys.add (e.nextElement());
	    }
	    return myKeys.iterator();
        }

	/* ---------------------------------------------------------------- */

        /** set the given <tt>value</tt> to be the value associated with the
	*    given field <tt>name</tt>, replacing any previously defined
	*    value(s)
        * @param name the name of the field for which we want to set the value
        * @param value the value to be set for that field name
        * @return void
        */
        private void set (String name, String value)
        {
	    Vector temp = new Vector();
	    temp.add (value);
	    this.values.put (name, temp);
        }

	/* ---------------------------------------------------------------- */

        /** get the number of fields defined for this Rcd
        * @return number of fields in this Rcd
        */
        public int size ()
        {
	    return this.values.size();
        }

	/* ---------------------------------------------------------------- */

        /** get a <tt>String</tt> representation of this Rcd
        * @return <tt>String</tt> representation of this Rcd
        */
        public String toString ()
        {
	    return this.values.toString();
        }

        //////////////////////
        // instance variables:
        //////////////////////

        private Hashtable values = null;	// fields & their values

    } // end class Rcd

} // end class RcdFile
