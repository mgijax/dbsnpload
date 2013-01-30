package org.jax.mgi.shr.dbutils;

import org.jax.mgi.shr.exception.MGIException;

/**
 * A MGIException which represents an error occuring during a
 * process involving a database resource
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class DBException extends MGIException {
  public DBException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}