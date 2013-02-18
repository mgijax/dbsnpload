// $Header$
// $Name$

package org.jax.mgi.shr.dbutils;

import java.sql.Types;

/**
 * A class that stores constants which represent the various Sybase data types
 * that are "supported" by the framework.
 * @has a set of static constants with symbolic names.
 * @does provides a list of symbolic names used to identify Sybase
 * data types and assigns to them the corresponding constant values from
 * java.sql.Types that Sybase maps to.
 * <p>Note: the following Sybase types have not been represented:
 * money, timestamp, smalldatetime, unichar, univarchar, nchar, binary,
 * varbinary, image</p>
 * @company Jackson Laboratory
 * @author M Walker
 * @version 1.0
 */

public class DBTypeConstants {
  /**
   * the Sybase char type assigned the value of java.sql.Types.CHAR
   */
  public static final int DB_CHAR = Types.CHAR;
  /**
   * the Sybase varchar type assigned the value of java.sql.Types.VARCHAR
   */
  public static final int DB_VARCHAR = Types.VARCHAR;
  /**
   * the Sybase integer type assigned the value of java.sql.Types.INTEGER
   */
  public static final int DB_INTEGER = Types.INTEGER;
  /**
   * the Sybase datetime type assigned the value of java.sql.Types.TIMESTAMP
   */
  public static final int DB_DATETIME = Types.TIMESTAMP;
  /**
   * the Sybase text type assigned the value of java.sql.Types.LONGVARCHAR
   */
  public static final int DB_TEXT = Types.LONGVARCHAR;
  /**
   * the Sybase double type assigned the value of java.sql.Types.DOUBLE
   */
  public static final int DB_DOUBLE = Types.DOUBLE;
  /**
   * the Sybase bit type assigned the value of java.sql.Types.BIT
   */
  public static final int DB_BIT = Types.BIT;
}

// $Log$
// Revision 1.1  2013/01/30 18:03:26  mgiadmin
// postgres convert
//
// Revision 1.5  2004/12/16 21:18:53  mbw
// merged assembly branch onto the trunk
//
// Revision 1.4.2.1  2004/12/02 19:27:00  mbw
// changed use of floats to doubles
//
// Revision 1.4  2004/09/22 17:06:36  mbw
// javadocs only
//
// Revision 1.3  2004/07/21 19:37:52  mbw
// reverted back to values for Sybase database
//
// Revision 1.2  2004/07/21 19:34:31  mbw
// javadocs only
//
// Revision 1.1.2.4  2003/06/04 14:51:49  mbw
// javadoc edits
//
// Revision 1.1.2.3  2003/05/22 15:56:22  mbw
// javadocs edits
//
// Revision 1.1.2.2  2003/03/21 16:52:57  mbw
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
