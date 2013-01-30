package org.jax.mgi.shr.unix;

import org.jax.mgi.shr.exception.MGIException;


/**
 * An MGIException which represents exceptions occuring during
 * the execution of external commands from the Java application.
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @company The Jackson Laboratory
 * @author M Walker
 */


public class CommandException extends MGIException {
  public CommandException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }

  public CommandException(String pMessage, Exception e)
  {
      super(pMessage, false);
      super.setParent(e);
  }

}
