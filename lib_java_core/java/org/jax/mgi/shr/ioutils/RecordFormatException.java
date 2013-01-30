package org.jax.mgi.shr.ioutils;

import org.jax.mgi.shr.exception.MGIException;

/**
 *  An MGIException that represents an encounter with an unexpected
 * record format
 * @has An error message with a variable field for binding the
 * record string at runtime
 * @does Binds the given runtime record string to the error messsage
 * @author M Walker
 */

public class RecordFormatException extends MGIException {

  public RecordFormatException() {
    super("Unexpected record format for the following record: ??", true);
  }

  public void bindRecord(String s) {
    bind(s);
  }

}