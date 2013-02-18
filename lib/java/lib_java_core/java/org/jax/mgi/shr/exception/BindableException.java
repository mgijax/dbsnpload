// $Header$
// $Name$

package org.jax.mgi.shr.exception;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object which extends the java Exception class to provided
 * additional functionality to bind runtime values to preset error messages.
 * @has An error message which can have embedded strings, namely the
 * charcters '??', used for substituting values in at runtime.
 * @does Provides bind methods for binding additional strings at runtime
 * to the error message
 * @company Jackson Laboratory
 * @author M Walker
 */

public class BindableException extends Exception implements Cloneable {

  // the binding characters to be replaced with runtime values
  private static String BIND_CHARACTER = "\\?\\?";
  // the compiled java regular expression of the bind characters
  private static Pattern pattern = Pattern.compile(BIND_CHARACTER);
  // the exception message
  private String message = null;
  // a matcher object for performing the runtime substitution
  private Matcher matcher = null;

  /**
   * constructor
   * @assumes nothing
   * @effects nothing
   * @param messageParm the message
   */
  public BindableException(String messageParm) {
    message = messageParm;
  }

  /**
   * Bind an integer to the exception message.
   * @assumes There is a "??" in the exception message that will be
   *             replaced with the given integer.
   * @effects Modifies the exception message by replacing the first
   * occurance of "??" with the given int value.
   * @param i The integer to bind to the exception message.
   */
  public void bind (int i)
  {
    String s = new Integer(i).toString();
    bind(s);
  }

  /**
   * Bind a string to the exception message.
   * @assumes There is a "??" in the exception message that will be
   *             replaced with the given string.
   * @effects Modifies the exception message by replacing the first
   * instance of "??" with the given string.
   * @param s The string to bind to the exception message.
   */
  public void bind (String s)
  {
        // metachars need to be dealt with...
    s = s.replace('$', '@');
    matcher = pattern.matcher(message);
    message = matcher.replaceFirst(s);
    matcher = pattern.matcher(message);
  }


  /**
   * Override the toString method of the Throwable class to
   *             return the exception message(s).
   * @assumes Nothing
   * @effects Nothing
   * @return The exception message(s).
   */
  public String toString() {
    return message;
  }

  /**
   * append to the exception message.
   * @assumes Nothing
   * @effects Nothing
   * @param message The exception message(s).
   */
  public void appendMessage(String message) {
    this.message = this.message + "\n" + message;
  }

  /**
   * Override the getMessage method of the Throwable class to
   *             return the exception message(s).
   * @assumes Nothing
   * @effects Nothing
   * @return The exception message(s).
   */
  public String getMessage() {
    return message;
  }


  /**
   * override the clone base class method to properly set instance variables
   * @return cloned object
   */
  public Object clone() {
    BindableException newException = null;
    try {
      newException = (BindableException)super.clone();
    }
    catch (CloneNotSupportedException e) {} // clone is supported so ignore
    newException.message = new String(this.message);
    newException.matcher = pattern.matcher(newException.message);
    return newException;
  }

  /**
   * replace the message with the given message
   * @assumes nothing
   * @effects the message in the MGIException will be replaced
   * @param message the given message
   */
  protected void replaceMessage(String message)
  {
    this.message = message;
  }

}
// $Log$
// Revision 1.1  2013/01/30 18:04:26  mgiadmin
// postgres convert
//
// Revision 1.3  2004/07/21 20:26:29  mbw
// javadocs edits only
//
// Revision 1.2  2004/01/05 18:28:27  mbw
// added replaceMessage() method
//
// Revision 1.1  2003/12/30 16:56:30  mbw
// imported into this product
//
// Revision 1.13  2003/10/03 16:43:45  mbw
// comments
//
// Revision 1.12  2003/10/01 19:20:58  mbw
// replacing char '$' on incoming string with '@'
//
// Revision 1.11  2003/06/04 17:07:05  mbw
// javadoc edits
//
// Revision 1.10  2003/06/04 17:05:24  mbw
// javadoc edits
//
// Revision 1.9  2003/05/22 15:49:50  mbw
// javadocs edits
//
// Revision 1.8  2003/05/16 15:09:57  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.7  2003/05/14 23:35:11  mbw
// removed dependence on types package
//
// Revision 1.6  2003/05/07 23:09:41  mbw
// implemented cloning of exceptions from the base class hashmap
//
// Revision 1.5  2003/04/22 00:05:05  mbw
// changed bindable characters to '??'
// and edited javadocs
//
// Revision 1.4  2003/03/21 14:48:34  mbw
// added standard header/footers
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
