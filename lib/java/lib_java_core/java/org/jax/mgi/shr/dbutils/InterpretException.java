package org.jax.mgi.shr.dbutils;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which represents an error in data interpreting
 * which can arise when implementing the interpret method within the
 * RowDataInterpreter interface or the interpretKey method of the
 * MultiRowDataInterpreter interface. It is basically used as a way to wrap
 * all other MGIExceptions other than DBException which can arise during
 * the interpret process.
 * @has an exception message and a parent exception of type MGIException
 * @does nothing
 * @author M Walker
 */

public class InterpretException extends MGIException {
  public InterpretException(MGIException e) {
    super("An exception occurred during row interpretation", false);
    super.setParent(e);
  }

  public InterpretException(String message) {
      super(message, false);
  }

}
