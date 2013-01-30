// $Header$
// $Name$

package org.jax.mgi.shr.dbutils.bcp;

import java.util.Vector;

import org.jax.mgi.shr.dbutils.Table;

/**
 * An object which can translate it's data contents into a
 * Vector used for writing a line to a bcp file via the BCPWriter class.
 * @has a target Table class for which to create the Vector.
 * @does creates a Vector for the target Table which can be passed in
 * as a parameter to the write() method of the BCPWriter.
 * @company Jackson Laboratory
 * @author M. Walker
 */


public interface BCPTranslatable {

  /**
   * extract a Vector object which represents a row in the given database
   * table. The objects in the vector must correlate with the columns of
   * the database table in type and order.
   * @assumes mothing
   * @effects nothing
   * @param table the table object representing the target database table
   * @return a Vector of objects which represent the values for a database
   * @throws BCPException thrown if the given table is not supported
   * row.
   */
  public Vector getBCPVector(Table table) throws BCPException;

  /**
   * get a list of table names that this object supports bcp for.
   * @assumes that the return vector contains Strings
   * @effects nothing
   * @return a Vector of Strings which represent the table names
   */
  public Vector getBCPSupportedTables();
}

// $Log$
// Revision 1.2  2004/07/21 21:04:31  mbw
// javadocs edits only
//
// Revision 1.1  2003/12/30 16:50:48  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:49:20  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.2.1  2003/09/24 14:24:49  mbw
// moved to new package
//
// Revision 1.1.2.8  2003/08/26 15:19:47  mbw
// now throwing an exception in method getBCPVector(Table)
//
// Revision 1.1.2.7  2003/07/23 19:56:51  mbw
// added new method getBCPSupportedTables
//
// Revision 1.1.2.6  2003/06/04 18:29:52  mbw
// javadoc edits
//
// Revision 1.1.2.5  2003/05/22 15:56:17  mbw
// javadocs edits
//
// Revision 1.1.2.4  2003/05/08 01:56:09  mbw
// incorporated changes from code review
//
// Revision 1.1.2.3  2003/04/15 12:05:22  mbw
// javadocs edits
//
// Revision 1.1.2.2  2003/03/21 16:52:50  mbw
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
