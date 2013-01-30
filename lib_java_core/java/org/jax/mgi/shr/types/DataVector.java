// $Header$
// $Name$

package org.jax.mgi.shr.types;

import java.util.Vector;

/**
 * An extension of the java.util.vector class which include methods
 * for adding java primitives which automatically convert them to their
 * corresponding wrapper classes
 * @has nothing.
 * @does provides additional convienence methods that provide vector
 * functionality for adding, removing and locating primitive types int,
 * double and boolean. All values are promoted to their corresponding wrapper
 * types before Vector operations are performed.
 * @company Jackson Laboratory
 * @author M Walker
 */

public class DataVector extends Vector {

  /**
   * default constructor
   */
  public DataVector() {
    super();
  }

  /**
   * adds the given primitive
   * @param input the primitive to add
   * @return true (as per the general contract of Collection.add)
   */
  public boolean add(int input) {
    return super.add(Converter.wrap(input));
  }

  /**
   * inserts the given primitive at the specified position
   * @param position the position at which to insert primitive
   * @param input the primitive to add
   */
  public void add(int position, int input) {
    super.add(position, Converter.wrap(input));
  }


  /**
   * adds the given primitive
   * @param input the primitive to add
   * @return true (as per the general contract of Collection.add)
   */
  public boolean add(double input) {
    return super.add(Converter.wrap(input));
  }

  /**
   * inserts the given primitive at the specified position
   * @param position the position at which to insert primitive
   * @param input the primitive to add
   */
  public void add(int position, double input) {
    super.add(position, Converter.wrap(input));
  }


  /**
   * adds the given primitive
   * @param input the primitive to add
   * @return true (as per the general contract of Collection.add)
   */
  public boolean add(boolean input) {
    return super.add(Converter.wrap(input));
  }

  /**
   * inserts the given primitive at the specified position
   * @param position the position at which to insert primitive
   * @param input the primitive to add
   */
  public void add(int position, boolean input) {
    super.add(position, Converter.wrap(input));
  }

  /**
   * replaces the element at the given position with the given primitive
   * @param position the position at which to replace
   * @param input the primitive given
   * @return the associated wrapper class for the given primitive
   */
  public Object set(int position, int input) {
    return super.set(position, Converter.wrap(input));
  }

  /**
   * replaces the element at the given position with the given primitive
   * @param position the position at which to replace
   * @param input the primitive given
   * @return the associated wrapper class for the given primitive
   */
  public Object set(int position, double input) {
    return super.set(position, Converter.wrap(input));
  }

  /**
   * replaces the element at the given position with the given primitive
   * @param position the position at which to replace
   * @param input the primitive given
   * @return the associated wrapper class for the given primitive
   */
  public Object set(int position, boolean input) {
    return super.set(position, Converter.wrap(input));
  }

  /**
   * removes the first occurence of the specified primitive
   * @param input the specified primitive
   * @return true if the vector containe the specified primitive
   */
  public boolean removeValue(int input) {
    return super.remove(Converter.wrap(input));
  }

  /**
   * removes the first occurence of the specified primitive
   * @param input the specified primitive
   * @return true if the vector containe the specified primitive
   */
  public boolean removeValue(double input) {
    return super.remove(Converter.wrap(input));
  }

  /**
   * removes the first occurence of the specified primitive
   * @param input the specified primitive
   * @return true if the vector containe the specified primitive
   */
  public boolean removeValue(boolean input) {
    return super.remove(Converter.wrap(input));
  }

  /**
   * searches for the first occurence of the given primitive, testing for
   * equality using the equals method on the associtaed wrapper class
   * @param input the primitive to search for
   * @return the index of the first occurence or -1 if the primitive was not
   * found
   */
  public int indexOf(int input) {
    return super.indexOf(Converter.wrap(input));
  }

  /**
   * searches for the first occurence of the given primitive, testing for
   * equality using the equals method on the associtaed wrapper class
   * @param input the primitive to search for
   * @return the index of the first occurence or -1 if the primitive was not
   * found
   */
  public int indexOf(double input) {
    return super.indexOf(Converter.wrap(input));
  }

  /**
   * searches for the first occurence of the given primitive, testing for
   * equality using the equals method on the associtaed wrapper class
   * @param input the primitive to search for
   * @return the index of the first occurence or -1 if the primitive was not
   * found
   */
  public int indexOf(boolean input) {
    return super.indexOf(Converter.wrap(input));
  }

  /**
   * searches for the first occurence of the given primitive starting from
   * the given position, testing for equality using the equals method on the
   * associtaed wrapper class
   * @param input the primitive to search for
   * @param position the position in the vector from which to start the search
   * @return the index of the first occurence or -1 if the primitive was not
   * found
   */
  public int indexOf(int input, int position) {
    return super.indexOf(Converter.wrap(input), position);
  }

  /**
   * searches for the first occurence of the given primitive starting from
   * the given position, testing for equality using the equals method on the
   * associtaed wrapper class
   * @param input the primitive to search for
   * @param position the position in the vector from which to start the search
   * @return the index of the first occurence or -1 if the primitive was not
   * found
   */
  public int indexOf(double input, int position) {
    return super.indexOf(Converter.wrap(input), position);
  }

  /**
   * searches for the first occurence of the given primitive starting from
   * the given position, testing for equality using the equals method on the
   * associtaed wrapper class
   * @param input the primitive to search for
   * @param position the position in the vector from which to start the search
   * @return the index of the first occurence or -1 if the primitive was not
   * found
   */
  public int indexOf(boolean input, int position) {
    return super.indexOf(Converter.wrap(input), position);
  }

}

// $Log$
// Revision 1.3  2004/12/16 21:18:58  mbw
// merged assembly branch onto the trunk
//
// Revision 1.2.4.1  2004/12/02 19:27:11  mbw
// changed use of floats to doubles
//
// Revision 1.2  2004/07/21 20:52:15  mbw
// javadocs edits only
//
// Revision 1.1  2003/12/30 16:56:54  mbw
// imported into this product
//
// Revision 1.6  2003/06/04 17:10:10  mbw
// javadoc edits
//
// Revision 1.5  2003/05/22 15:50:53  mbw
// javadocs edits
//
// Revision 1.4  2003/05/16 15:11:49  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.3  2003/04/17 23:33:43  mbw
// javadoc edits
//
// Revision 1.2  2003/03/21 16:22:54  mbw
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
