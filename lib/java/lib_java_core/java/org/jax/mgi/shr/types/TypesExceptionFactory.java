package org.jax.mgi.shr.types;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 *  An ExceptionFactory which returns TypesExceptions.
 * @has a hashmap of Exceptions
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 */

public class TypesExceptionFactory extends ExceptionFactory {

  /**
   * a string could not be converted to another datatype
   */
  public static final String MalformedString =
      "org.jax.mgi.shr.types.MalformedString";
  static {
    exceptionsMap.put(MalformedString, new TypesException(
        "Could not convert String value '??' to type ??", true));
  }

  /**
   * the object is not handled by this call
   */
  public static final String UnhandledDataType =
      "org.jax.mgi.shr.types.UnhandledDataType";
  static {
    exceptionsMap.put(UnhandledDataType, new TypesException(
        "Unhandled datatype: ??", true));
  }

}

// $Log$
// Revision 1.1  2013/01/30 18:05:39  mgiadmin
// postgres convert
//
// Revision 1.2  2004/07/21 20:52:49  mbw
// javadocs edits only
//
// Revision 1.1  2003/12/30 16:56:55  mbw
// imported into this product
//
// Revision 1.3  2003/05/22 15:50:54  mbw
// javadocs edits
//
// Revision 1.2  2003/04/22 22:20:23  mbw
// removed getException methods. They are now in the base class
//
// Revision 1.1  2003/04/22 00:09:37  mbw
// initial version
//
// Revision 1.10  2003/04/18 12:41:20  mbw
// added new exception
//
// Revision 1.9  2003/04/15 11:59:06  mbw
// added more exceptions
//
// Revision 1.8  2003/04/10 00:44:49  mbw
// removed exception for filename not specified in config file
//
// Revision 1.7  2003/04/09 21:28:51  mbw
// removed an existing bcp execption no longer used
//
// Revision 1.6  2003/04/01 21:53:35  mbw
// resolved impact of class name change
//
// Revision 1.5  2003/03/21 15:51:33  mbw
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


