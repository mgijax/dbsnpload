// $Header$
// $Name$

package org.jax.mgi.shr.dbutils;

/**
 * An object that creates java data objects based on data from on a
 * RowReference.
 * This interface is used by a RowDataIterator in order to create objects
 * while iterating through a query result set.
 * @has nothing.
 * @does instantiates a java data object from a given RowReference.
 * @company Jackson Laboratory
 * @author M. Walker
 */

public interface RowDataInterpreter {

  /**
   * instantiates a java data object based on the given RowReference
   * @assumes nothing
   * @effects nothing
   * @param rowReference the RowReference used in creating the new data
   * object
   * @return newly created data object
   * @throws DBException thrown if there are errors while reading the
   * RowReference data.
   * @throws InterpretException if there is an any error occuring
   * during interpretation of the row
   */
  public java.lang.Object interpret(RowReference rowReference)
      throws DBException, InterpretException;

}
// $Log$
// Revision 1.3  2004/07/21 19:50:51  mbw
// javadocs edits only
//
// Revision 1.2  2004/01/29 21:21:36  mbw
// added new InterpretMethod to the Interpreter interfaces
//
// Revision 1.1  2003/12/30 16:50:35  mbw
// imported into this product
//
// Revision 1.3  2003/12/09 22:49:07  mbw
// merged jsam branch onto the trunk
//
// Revision 1.2.2.9  2003/06/04 18:29:56  mbw
// javadoc edits
//
// Revision 1.2.2.8  2003/05/22 15:56:24  mbw
// javadocs edits
//
// Revision 1.2.2.7  2003/05/08 01:56:13  mbw
// incorporated changes from code review
//
// Revision 1.2.2.6  2003/04/25 17:12:01  mbw
// updated to reflect changes in design of exception handling
//
// Revision 1.2.2.5  2003/04/15 12:05:20  mbw
// javadocs edits
//
// Revision 1.2.2.4  2003/04/10 17:23:26  mbw
// javadoc edit
//
// Revision 1.2.2.3  2003/03/21 16:52:59  mbw
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
