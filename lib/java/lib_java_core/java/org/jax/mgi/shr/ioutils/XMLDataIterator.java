package org.jax.mgi.shr.ioutils;



import org.jax.mgi.shr.dbutils.DataIterator;

/**
 * An iterator for iterating through elements of xml data
 * @has An XMLDataInterpreter for instantiating java data objects
 * based on the xml element records. If the interpreter is null then
 * the current xml element is returned as a XMLTagNavigator which the
 * user can use for parsing the element.
 * @does iterates through data as a collection of xml elements and returns
 * Java data objects based on these elements. The caller must set the
 * XMLDataInterpreter and cast the return objects to the expected type.
 * @company Jackson Laboratory
 * @author M. Walker
 */


public interface XMLDataIterator extends DataIterator {

  /**
   * Set the reference of a XMLDataInterpreter which will then be used on
   * subsequent calls to the next() method to create Java data objects
   * based on the contents of the record.
   * @assumes nothing
   * @effects will determine how xml elements are parsed
   * @param interpreter the XMLDataInterpreter object
   */

  public void setInterpreter(XMLDataInterpreter interpreter);

}
