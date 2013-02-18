package org.jax.mgi.shr.ioutils;

/**
 * is a class which can parse xml data to create a Java data object and is
 * used by the XMLDataIterator when iterating over xml element data and
 * ctreating Java objects based on them
 * @has an XNLTagIterator to assist in navigating through the xml file
 * @does parses an xml element and creates a Java object
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public interface XMLDataInterpreter {

  /**
   * parses xml using a XMLTagNavigator and instantiates a java data object
   * based on the xml contents.
   * @param in the XMLTagNavigator
   * @return newly created data object based on the xml data
   * @throws InterpretException thrown if there is an error while parsing the
   * input
   */
  public java.lang.Object interpret(XMLTagIterator in)
      throws InterpretException;

}
