// $Header$
// $Name$

package org.jax.mgi.shr.ioutils;

import org.jax.mgi.shr.dbutils.DataIterator;

/**
 * An iterator for iterating through records of data
 * @has A data file. A RecordDataReader for parsing the data file into
 * records. A RecordDataInterpreter for instantiating java data objects
 * based on the current record. If the interpreter is null then the current
 * record is returned as a String. The interpreter also defines whether a
 * current record is considered to be valid or not by returning true or false
 * to the isValid(RowReference) method. If an interpreter is
 * defined then only "valid" records (those that return true on the call to
 * isValid(RowReference)) are returned during iteration.
 * @does iterates through data as a collection of records and returns java
 * data objects. The caller must set the RecordDataInterpreter and cast the
 * return objects to the expected type.
 * @company Jackson Laboratory
 * @author M. Walker
 */


public interface RecordDataIterator extends DataIterator {

  /**
   * Set the reference of a RecordDataInterpreter which will then be used on
   * subsequent calls to the next() method to create java data objects
   * based on the contents of the record.
   * @param interpreter the RecordDataInterpreter object
   */

  public void setInterpreter(RecordDataInterpreter interpreter);

}
// $Log$
// Revision 1.1  2013/01/30 18:04:45  mgiadmin
// postgres convert
//
// Revision 1.2  2004/07/07 20:37:47  mbw
// now extends DataIterator from the dbutils package
//
// Revision 1.1  2003/12/30 16:56:37  mbw
// imported into this product
//
// Revision 1.4  2003/06/04 15:06:34  mbw
// javadoc edits
//
// Revision 1.3  2003/05/20 15:22:43  mbw
// merged code from branch lib_java_ioutils-1-0-5-jsam
//
// Revision 1.2.2.9  2003/05/16 15:10:38  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.2.2.8  2003/05/07 23:37:02  mbw
// javadoc edits
//
// Revision 1.2.2.7  2003/04/29 17:26:01  mbw
// added changes from code reviews
//
// Revision 1.2.2.6  2003/04/28 16:24:29  mbw
// changed due to impact of new exception handling model
//
// Revision 1.2.2.5  2003/04/15 11:58:09  mbw
// javadoc edits
//
// Revision 1.2.2.4  2003/04/10 17:18:01  mbw
// added new method to interface
//
// Revision 1.2.2.3  2003/03/21 16:12:29  mbw
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
