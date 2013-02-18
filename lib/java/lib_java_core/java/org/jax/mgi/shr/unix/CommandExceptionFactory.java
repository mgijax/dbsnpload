package org.jax.mgi.shr.unix;

import org.jax.mgi.shr.exception.ExceptionFactory;


/**
 * An ExceptionFactory for command line execution related errors.
 * @has a hashmap of predefined CommandExceptions stored by a name key
 * @does looks up CommandExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class CommandExceptionFactory extends ExceptionFactory {

  /**
   * A requested parameter was not found
   */
  public static final String NonZeroStatus =
      "org.jax.mgi.shr.dla.cmd.NonZeroStatus";
  static {
    exceptionsMap.put(NonZeroStatus, new CommandException(
        "The following command returned a non zero status: ??", false));
  }

  /**
   * An interrupt signal was received during the execution of command
   */
  public static final String InterruptErr =
      "org.jax.mgi.shr.dla.cmd.InterruptErr";
  static {
    exceptionsMap.put(InterruptErr, new CommandException(
        "Interrupt signal received while running the " +
        "following command: ??", false));
  }

  /**
   * Error creating or opening a bcp file
   */
  public static final String IOErr =
      "org.jax.mgi.shr.dla.cmd.IOErr";
  static {
    exceptionsMap.put(IOErr, new CommandException(
        "The RunCommand threw an IO error " +
        "while executing the following command: ??", false));
  }
}
