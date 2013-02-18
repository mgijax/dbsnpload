package org.jax.mgi.shr.types;

import org.jax.mgi.shr.exception.MGIException;

/**
 *  An MGIException which represents exceptions occuring during
 * data conversion or data type handling.
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 */

public class TypesException extends MGIException {
  public TypesException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}