package org.jax.mgi.shr.dbutils.bcp;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which represents an error in bcp processing
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @author M Walker
 */

public class BCPException extends MGIException {
  public BCPException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}