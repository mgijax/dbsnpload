package org.jax.mgi.shr.ioutils;

import org.jax.mgi.shr.exception.MGIException;

/**
 * is an MGIException specific to errors occurring during file formatting
 * @has nothing
 * @does nothing
 * @company The Jackson Laboratory
 * @author not attributable
 *
 */

public class FormatException extends MGIException
{
    /**
     * constructor
     * @param pMessage the error message
     */
    public FormatException(String pMessage) {
      super(pMessage);
    }

    /**
     * constructor
     * @param pMessage the error message
     * @param e the parent exception
     */
    public FormatException(String pMessage, Exception e) {
      super(pMessage);
      super.setParent(e);
    }

}