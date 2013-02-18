// $Header$
// $Name$

package org.jax.mgi.shr.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Vector;
import org.jax.mgi.shr.stringutil.StringLib;

/* Author: jsb
*  Modification History:
*    07/01/2002: As a result of code review, standardized on use of Iterators
*		rather than Enumerations and modified a few comments.  Also,
*		hid the constructor and added a load() method to the class.
*    June 2002: created class for Look & Feel release (TR 3364)
*/

/* ======================================================================== */
/* ======================================================================== */

/** A Configuration object represents a configuration file as defined for a
* certain product.  It maps config option names to values.  Three file formats
* are supported: whitespace-delimited (tab), csh, and sh.  The default format
* is tab.  To override the default, the file should begin with an initial
* line like:
* <P>
* <BLOCKQUOTE>
*   <tt>#format: csh</tt>
* </BLOCKQUOTE>
* <P>
* For whitespace-delimited files, the first item on a line is the name of the
* configuration option, followed by whitespace.  The rest of the line is taken
* as the value for that option, as in:<BR>
* <BLOCKQUOTE>
*   <tt>optionName option's value here</tt>
* </BLOCKQUOTE>
* <P>
* For sh-format config files, the first item on the line is the name of the
* configuration option, followed by an equals sign.  The rest of the line is
* taken as the value for that option, as in:<BR>
* <BLOCKQUOTE>
*   <tt>optionName=option value here</tt>
* </BLOCKQUOTE>
* <P>
* For csh-format config files, you may use either <CODE>set</CODE> or
* <CODE>setenv</CODE> syntax.  The two are equivalent in this context.
* Thus, either of these examples is fine:<BR>
* <BLOCKQUOTE>
*   <tt>set optionName=option value here</tt><BR>
*   <tt>setenv optionName option value here</tt>
* </BLOCKQUOTE>
* <P>
* Quotes are not required around option values which include spaces.<BR>
* Leading and trailing whitespace is trimmed from the values.<P>
* For this sample configuration file...
* <PRE>
* DBSERVER	MGD_DEV
* DATABASE	mgd
* USERNAME	foo
* PASSWORD	moo
* </PRE>
* Here's how to get at a couple options from the file...<P>
* <PRE>
* Configuration config = new Configuration ("myFilename");
* String server = config.get("DBSERVER");
* String database = config.get("DATABASE");
* </PRE>
*/
public class Configuration
{
    //////////////////
    // public methods:
    //////////////////

    /* -------------------------------------------------------------------- */

    /** reads the given filename, using it to construct a Configuration
    *	object.  This method is private, to encourage use of the class's
    *	load() method.
    * @param filename path to the configuration file to read
    * @throws FileNotFoundException if the file cannot be found
    * @throws IOException if the file cannot be read and parsed properly
    */
    protected Configuration (String filename)
        throws IOException, FileNotFoundException
    {
        this.dirty = false;
        this.cache = new Hashtable();
        this.options = new Hashtable();
        this.processFile (new File (filename));
    }

    /* -------------------------------------------------------------------- */

    /** checks that each String in <tt>keys</tt> is defined as a configuration
    *		option in this object
    * @param keys Collection of Strings, each of which should be a
    *		configuration option defined in this object
    * @return void
    * @throws IllegalArgumentException with a message about which
    *		<tt>keys</tt> were not found in the object
    */
    public void checkKeys (Collection keys) throws IllegalArgumentException
    {
        String key = null;		// current key being examined
        Vector unknown = new Vector();	// collects unknown keys
        Iterator it = keys.iterator();	// steps through 'keys'

        while (it.hasNext())		// step through each key and check it
        {
            key = (String) it.next();
            if (!this.hasKey (key))
            {
                unknown.add (key);
            }
        }

        if (unknown.size() > 0)		// if any unknown ones, throw exc.
        {
            throw new IllegalArgumentException ("Unknown keys: " +
                                    StringLib.join (unknown, ", "));
        }
    }

    /* -------------------------------------------------------------------- */

    /** check that the configuration option <tt>name</tt> is known
    * @param name the configuration option we're investigating
    * @return <tt>true</tt> if the name is contained, <tt>false</tt> otherwise
    */
    public boolean containsKey (String name)
    {
        return this.options.containsKey (name);
    }

    /* -------------------------------------------------------------------- */

    /** retrieve the value of the configuration option specified by
    *		<tt>name</tt>, or <tt>null</tt> if not known
    * @param name the configuration option whose value we seek
    * @return value of option specified by <tt>name</tt>
    * @throws IllegalArgumentException if the value of <tt>name</tt> cannot be
    *		resolved because of a loop in parameters referencing each
    *		other
    */
    public String get (String name) throws IllegalArgumentException
    {
        // if there have been changes, then we need to reset the cache
        if (this.dirty)
        {
            this.cache = new Hashtable();
            this.dirty = false;
        }

        // if the value is already in the cache, the just retrieve it from
        // there
        if (this.cache.containsKey (name))
        {
            return (String) this.cache.get (name);
        }

        // if the option is not known, then return null.  otherwise,
        // compute and return the value.
        if (!this.options.containsKey (name))
        {
            return null;
        }
        return this.resolve (name, Configuration.MAX_RECURSION);
    }

    /* -------------------------------------------------------------------- */

    /** check that the configuration option <tt>name</tt> is known -- wrapper
    * 		over this.containsKey() to ease transition from Python
    * @param name the configuration option we're investigating
    * @return <tt>true</tt> if <tt>name</tt> is contained, <tt>false</tt> if
    *	    not
    */
    public boolean hasKey (String name)
    {
        return this.containsKey (name);
    }

    /* -------------------------------------------------------------------- */

    /** get an Iterator of all the (String) keys in this Configuration object
    * @return Iterator of Strings, each of which is a key
    */
    public Iterator keys ()
    {
        Vector myKeys = new Vector();
        Enumeration e = this.options.keys();

        while (e.hasMoreElements())
        {
            myKeys.add (e.nextElement());
        }
        return myKeys.iterator();
    }

    /* -------------------------------------------------------------------- */

    /** number of keys in this Configuration object, wrapper over
    *		the <tt>size()</tt> method to ease the conversion from Python
    * @return number of keys contained
    */
    public int length ()
    {
        return this.size();
    }

    /* -------------------------------------------------------------------- */

    /** add configuration option <tt>name</tt> to this Configuration object,
    *		with the given <tt>value</tt>
    * @param name the configuration option to be set
    * @param value the value of the configuration option
    * @return void
    */
    public void set (String name, String value)
    {
        this.setOption (name, value);
    }

    /* -------------------------------------------------------------------- */

    /** number of keys in this Configuration object
    * @return number of keys contained
    */
    public int size ()
    {
        return this.options.size();
    }

    /* -------------------------------------------------------------------- */

    /** converts this Configuration object to a String
    * @return String representation of this object
    */
    public String toString ()
    {
        return (String) this.options.toString();
    }

    ///////////////////
    // private methods:
    ///////////////////

    /* -------------------------------------------------------------------- */

    /** read the file specified by <tt>myFile</tt>, parse its contents, and
    *		use them to initialize this Configuration object
    * @param myFile the File to read in the file system
    * @return void
    * @throws FileNotFoundException if <tt>myFile</tt> cannot be found
    * @throws IOException if there are problems reading from <tt>myFile</tt>
    *		or processing some of its contents
    */
    protected void processFile (File myFile)
            throws FileNotFoundException, IOException
    {
        // steps through lines in the file as we read it
        String line = null;

        // default format is tab/space delimited
        int format = Configuration.TABBED;

        // open the file and check to see if the first line tells us what
        // format to expect
        BufferedReader infile = new BufferedReader (new FileReader (myFile));
        line = infile.readLine();
        if (line == null)		// if there's no line, it was an
	{                                                                    	// empty file, so bail out
            infile.close();
            return;
        }
        if (line.substring (0,8).equals("#format:"))
        {
            String formatString = line.substring (8).trim().toLowerCase();
            if (formatString.equals ("tab"))
            {
                format = Configuration.TABBED;
            }
            else if (formatString.equals ("sh"))
            {
                format = Configuration.BOURNE;
            }
            else if (formatString.equals ("csh"))
            {
                format = Configuration.CSH;
            }
            else
            {
                throw new IOException ("Unknown config file format: " +
                                                formatString);
            }
            line = infile.readLine();		// get next line (prime pump)
        }

        // step through the rest of the lines in the file
        while (line != null)
        {
            // trim any leading and trailing whitespace from the current line
            line = line.trim();

            // don't bother to parse blank lines and comments
            if (!(line.equals("") || line.substring(0,1).equals("#")))
            {
                switch (format)
                {
                    case Configuration.TABBED:
                        this.processTabLine (line);
                        break;
                    case Configuration.BOURNE:
                        this.processBourneLine (line);
                        break;
                    case Configuration.CSH:
                        this.processCshLine (line);
                        break;
                }
            }
            line = infile.readLine();
        }
        infile.close();
    }

    /* -------------------------------------------------------------------- */

    /** process <tt>line</tt>, expected to be in tab-delimited format
    * @param line the line from the configuration file we expect to process
    *		in a tab-delimited format
    * @return void
    */
    private void processTabLine (String line)
    {
        String name = null;
        String value = "";

        ArrayList parts = StringLib.split (line);
        if (parts.size() >= 1)
        {
            name = (String) parts.get(0);

            // if the file defines a value, then use it to replace the
            // empty String default
            if (parts.size() >= 2)
            {
                value = line.substring (name.length()).trim();
            }
            this.setOption (name, value);
        }
    }

    /* -------------------------------------------------------------------- */

    /** process <tt>line</tt>, expected to be in Bourne shell format
    * @param line the line from the configuration file we expect to process
    *		in a Bourne shell format
    * @return void
    */
    private void processBourneLine (String line)
    {
        String name = null;
        String value = "";
        int eqPos;
	
	// if line begins with "export", skip it

	if (line.startsWith("export"))
	{
	    return;
	}

	// if line does not contain an equal sign, skip it
	// note that this does not work for "if" statements that contain the equal sign!

	eqPos = line.indexOf("=");
	if (eqPos < 0)
	{
	    return;
	}

        ArrayList parts = StringLib.split (line, "=");
        if (parts.size() >= 1)
        {
            // ignore spaces between the name and the equals sign.  This is
            // a little more forgiving than true Bourne shell.
            name = ((String) parts.get(0)).trim();

            // if the user specified a value, then use it to replace the
            // empty String default
            if (parts.size() >= 2)
            {
                eqPos = line.indexOf ("=");
                value = line.substring (eqPos + 1).trim();
            }
            this.setOption (name, value);
        }
    }

    /* -------------------------------------------------------------------- */

    /** process <tt>line</tt>, expected to be in csh format
    * @param line the line from the configuration file we expect to process
    *		in a C shell format
    * @return void
    * @throws IOException if the given <tt>line</tt> is not parseable
    */
    private void processCshLine (String line) throws IOException
    {
        String cmd = null;		// "set" or "setenv"
        String name = null;		// name of the option to set
        String value = "";		// value of the option to set
        String original = null;		// value of "line" as passed in

        original = line;	// remember this, for exception generation

        ArrayList parts = StringLib.split (line);
        if (parts.size() == 0)
        {
            return;
        }

        // grab the command type, then cut it off the beginning of 'line'
        cmd = (String) parts.get(0);
        line = line.substring (line.indexOf(cmd) + cmd.length()).trim();

        if (cmd.equals ("setenv"))
        {
            // everything after the 'name' is the 'value', with leading and
            // trailing whitespace stripped
            name = (String) parts.get(1);
            value = line.substring (line.indexOf(name) + name.length()).trim();
            this.setOption (name, value);
        }
        else if (cmd.equals ("set"))
        {
            ArrayList subParts = StringLib.split (line, "=");

            // if the line has an equals sign, then everything after it is
            // the value, with leading and trailing whitespace stripped
            if (subParts.size() > 1)
            {
                name = (String) subParts.get(0);
                value = line.substring (line.indexOf ("=") + 1).trim();
            }
            // otherwise, we rely on the default empty String value
            else
            {
                name = (String) subParts.get(0);
            }
            this.setOption (name, value);
        }
        else
        {
                throw new IOException ("Unknown config file line format: \n"
                        + original);
        }
    }

    /* -------------------------------------------------------------------- */

    /** retrieve the value for <tt>name</tt>, filling in any references to
    *		other configuration options
    * @param name the name of the option we want to look up
    * @param steps maximum number of recursive steps we have left to go thru
    *		to generate the value for <tt>name</tt>, to prevent endless
    *		loops
    * @return String, which is <tt>name</tt> with any references resolved
    * @throws IllegalArgumentException if the value of <tt>name</tt> cannot be
    *		resolved within the given number of 'steps' (because of a loop
    *		in parameters referencing each other)
    */
    private String resolve (String name, int steps)
                throws IllegalArgumentException
    {
        String value = null;	// the value associated with 'name'
        int start = -1;		// where we find start of cited name
        int stop = -1;		// where we find end of cited name

        // if we've already computed this and stored it in the cache, then
        // just retrieve and return it from there
        if (this.cache.containsKey (name))
        {
            return (String) this.cache.get (name);
        }

        // if we've recursed through all our allowed steps, then give up
        if (steps == 0)
        {
            throw new IllegalArgumentException("Cannot resolve: " + name);
        }

        // retrieve the raw value associated with 'name', as read from the
        // configuration file and stored in this.options
        value = (String) this.options.get(name);
	System.out.println("config name="+name+", value="+value);
        if (value == null)
        {
            throw new IllegalArgumentException("Cannot resolve: " + name);
        }
        start = value.indexOf ("${");

        // we need to process all instances of ${...} citations, not just
        // the first one
        while (start >= 0)
        {
            stop = value.indexOf ("}");
            if (stop >= 0)
            {
                // replace the ${...} citation with the option's value
                value = value.substring (0, start)
                        + this.resolve (
                                value.substring (start+2,stop), steps-1)
                        + value.substring (stop+1);

                // and move on to find the next citation
                start = value.indexOf ("${", start);
            }
            else
            {
                start = value.indexOf ("${", start + 2);
            }
        }
        // since we've finished the computation for name's value, go ahead
        // and store it in the cache for next time
        this.cache.put (name, value);
        return value;
    }

    /* -------------------------------------------------------------------- */

    /** store the given <tt>value</tt> for <tt>name</tt> in this Configuration
    *		object
    * @param name the configuration option to be set
    * @param value the value of the configuration option
    * @return void
    */
    private void setOption (String name, String value)
    {
        String strippedValue = null;
        strippedValue = value;

        // we take care to strip off enclosing quotes, to just leave the
        // raw value.  (either single or double quotes)

        if (value.length() > 1)
        {
            char first = ' ';		// first character of 'value'
            char last = ' ';		// last character of 'value'

            first = value.charAt(0);
            last = value.charAt(value.length()-1);

            if ((first == last) && (first == '\"' || first == '\''))
            {
                strippedValue = value.substring (1, value.length() - 1);
            }
        }

        // now that we've set a new value for an option, we need to remember
        // that our cached values are out of date.  (the cache is dirty)
        this.dirty = true;

        // finally, remember the new value for 'name'
        this.options.put (name, strippedValue);
    }

    /* -------------------------------------------------------------------- */

    /////////////////
    // class methods:
    /////////////////

    /** gets the Configuration object associated with the file at the given
    *	filename.  (returns it from a cache in memory if available, or after
    *	reading and parsing the file from the file system)
    * @param filename path to the configuration file to read
    * @throws FileNotFoundException if the file cannot be found
    * @throws IOException if the file cannot be read and parsed properly
    */
    public static Configuration load (String filename)
        throws IOException, FileNotFoundException
    {
    return Configuration.load (filename, true);
    }

    /** gets the Configuration object associated with the file at the given
    *	filename.  'fromCache' allows you to specify whether you want a cached
    *   copy of the file if one is available.
    * @param filename path to the configuration file to read
    * @param fromCache true if we can return the file from a memory cache, or
    *   false to force a reload (even if already cached)
    * @throws FileNotFoundException if the file cannot be found
    * @throws IOException if the file cannot be read and parsed properly
    */
    public static Configuration load (String filename, boolean fromCache)
        throws IOException, FileNotFoundException
    {
        if (fromCache && Configuration.loaded.containsKey (filename))
        {
            return (Configuration) Configuration.loaded.get (filename);
        }
        Configuration newOne = new Configuration (filename);
        Configuration.loaded.put (filename, newOne);
        return newOne;
    }

  /**
    * adds values from the file with the given filename
    * @param filename path to the configuration file to read
    * @throws FileNotFoundException if the file cannot be found
    * @throws IOException if the file cannot be read and parsed properly
    */

    public void include(String filename) throws IOException
    {
        this.processFile (new File (filename));
    }


    //////////////////////
    // instance variables:
    //////////////////////

    // This could be implemented as a HashMap, but I opted for synchronization
    // of the Hashtable over the ability to use nulls in HashMap.

    protected Hashtable options = null;		// set of config options
    protected Hashtable cache = null;		// set of resolved options
    protected boolean dirty = false;		// is cache clean or dirty?

    ///////////////////
    // class variables:
    ///////////////////

    // constants which indicate the type of configuration file we're reading

    private static final int TABBED = 0;
    private static final int BOURNE = 1;
    private static final int CSH = 2;

    // maximum recursive depth when trying to resolve symbolic references

    private static final int MAX_RECURSION = 100;

    // hash table of loaded configuration objects (to save re-parsing if we
    // are asked for the same file twice)

    protected static Hashtable loaded = new Hashtable();
}
// $Log$
// Revision 1.1  2013/01/30 18:01:28  mgiadmin
// postgres convert
//
// Revision 1.8  2013/01/30 16:31:21  mgiadmin
//
// convert to postgres
//
// convert to postgres
//
// Revision 1.7  2011/08/26 13:43:40  sc
// lib_java_core-4-4-1-1
//
// Revision 1.6  2007/12/24 15:38:16  pf
// merging tr8087 branch
//
// Revision 1.5.4.1  2007/09/10 16:43:10  marka
// changed some methods and variables to protected to aaccomodate the new WebAppCfg class
//
// Revision 1.5  2006/12/20 16:59:45  lec
// fix bourne parser
//
// Revision 1.4  2004/12/16 21:18:51  mbw
// merged assembly branch onto the trunk
//
// Revision 1.3.8.1  2004/12/02 18:14:56  mbw
// bug fix: now throws exception if a referenced paramater cannot be resolved
//
// Revision 1.3  2004/04/14 20:18:08  mbw
// now reads multiple config files and handles parameters set in one to be refered by another
//
// Revision 1.2  2004/02/17 19:03:22  jsb
// Added additional load() method to bypass cache.  is backward-compatible
//
// Revision 1.1  2003/12/30 16:50:05  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:47:23  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.2.2  2003/03/21 14:25:29  mbw
// added standard header/footer
//
// Revision 1.1.2.1  2003/03/21 14:16:01  mbw
// added standard header/footer
//
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
