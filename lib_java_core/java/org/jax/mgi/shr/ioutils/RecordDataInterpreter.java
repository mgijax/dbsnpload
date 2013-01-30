// $Header$
// $Name$

package org.jax.mgi.shr.ioutils;

import org.jax.mgi.shr.exception.MGIException;


/**
 * An object which evaluates records from input files to determine whether or
 * not they are valid for processing and creates java data objects based on the
 * input record.
 * @has nothing
 * @does evaluates a given record from a file and indicates whether the
 * record should be considered valid and also instantiates java objects based
 * on the contents a given record.
 * @company Jackson Laboratory
 * @author M. Walker
 */


public interface RecordDataInterpreter {

  /**
   * parses a record string and instantiates a java data object based
   * on the record contents.
   * @param in the record string
   * @return newly created data object
   * @throws MGIException thrown if the record format is unexpected
   */
  public java.lang.Object interpret(String in) throws MGIException;


  /**
   * checks the contents of the given String and decides whether it should
   * be processed
   * @param in the input String
   * @return true or false
   */
  public boolean isValid(String in);
}
// $Log$
// Revision 1.2  2004/07/21 20:44:37  mbw
// javadocs edits only
//
// Revision 1.1  2003/12/30 16:56:37  mbw
// imported into this product
//
// Revision 1.7  2003/11/04 15:24:09  mbw
// no change
//
// Revision 1.6  2003/10/27 17:36:49  mbw
// changed the signature of interpret method in RecordDataInterpreter to throw MGIException instead of RecordFormtException
//
// Revision 1.5  2003/06/04 15:06:33  mbw
// javadoc edits
//
// Revision 1.4  2003/05/20 15:23:33  mbw
// merged code from branch lib_java_ioutils-1-0-5-jsam
//
// Revision 1.3.2.9  2003/05/16 15:10:37  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.3.2.8  2003/04/29 21:18:34  mbw
// javadoc edit
//
// Revision 1.3.2.7  2003/04/25 16:56:15  mbw
// changed interface to throw changes to exception design
//
// Revision 1.3.2.6  2003/04/15 11:58:08  mbw
// javadoc edits
//
// Revision 1.3.2.5  2003/04/10 17:16:04  mbw
// added new method to interface
//
// Revision 1.3.2.4  2003/03/21 16:12:28  mbw
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
