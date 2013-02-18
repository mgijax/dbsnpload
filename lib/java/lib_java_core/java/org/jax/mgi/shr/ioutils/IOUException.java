package org.jax.mgi.shr.ioutils;

import org.jax.mgi.shr.exception.MGIException;

/**
 *  A MGIException which represents an error occuring during io
 * processing
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 */

public class IOUException extends MGIException {
  public IOUException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }


}