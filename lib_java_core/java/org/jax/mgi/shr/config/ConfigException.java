package org.jax.mgi.shr.config;

import org.jax.mgi.shr.exception.MGIException;


/**
 * An MGIException which represents exceptions occuring during
 * the reading of configuration parameters.
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company The Jackson Laboratory
 * @author M Walker
 */


public class ConfigException extends MGIException {
  public ConfigException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }

  public ConfigException(String pMessage, Exception e) {
    super(pMessage, e);
  }



}