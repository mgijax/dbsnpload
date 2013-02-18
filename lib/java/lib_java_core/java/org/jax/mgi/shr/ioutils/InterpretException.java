package org.jax.mgi.shr.ioutils;

/**
 * <p>@is </p>
 * <p>@has </p>
 * <p>@does </p>
 * <p>@company The Jackson Laboratory</p>
 * @author not attributable
 *
 */

import org.jax.mgi.shr.exception.MGIException;


/**
 * Represents exceptions occuring during the interpretation of any of a
 * number of various input types, including files and JDBC results
 * @has an exception message and possibly a parent MGIException
 * exception which can be null.
 * @does nothing
 * @company The Jackson Laboratory
 * @author M Walker
 */


public class InterpretException extends MGIException {
  public InterpretException(String pMessage) {
    super(pMessage, false);
  }
  public InterpretException(String pMessage, Exception e) {
      super(pMessage, false);
      super.setParent(e);
  }
  }

