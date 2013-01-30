package org.jax.mgi.shr.dbutils;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for storing ScriptExceptions.
 * @has a hashmap of predefined ScriptExceptions stored by a name key
 * @does looks up ScriptExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class ScriptExceptionFactory extends ExceptionFactory {

  /**
   * An error occurred while trying to create a temp file for scripting
   */
  public static final String TempFileCreateErr =
      "org.jax.mgi.shr.script.TempFileCreateErr";
  static {
    exceptionsMap.put(TempFileCreateErr, new ScriptException(
        "Could not create a temp file for the sql script", false));
  }

  /**
   * Error creating or opening a script file
   */
  public static final String FileCreateErr =
      "org.jax.mgi.shr.script.FileCreateErr";
  static {
    exceptionsMap.put(FileCreateErr, new ScriptException(
        "Could not create or open file ??", false));
  }

  /**
   * The script file could not be closed
   */
  public static final String FileCloseErr =
      "org.jax.mgi.shr.script.FileCloseErr";
  static {
    exceptionsMap.put(FileCloseErr, new ScriptException(
        "Could not close file: ??", false));
  }

  /**
   * Error writing to a script file
   */
  public static final String FileWriteErr =
      "org.jax.mgi.shr.script.FileWriteErr";
  static {
    exceptionsMap.put(FileWriteErr, new ScriptException(
        "Error writing to file ??", false));
  }

  /**
   * An interrupt signal was received during the execution of the script
   */
  public static final String InterruptErr =
      "org.jax.mgi.shr.script.InterruptErr";
  static {
    exceptionsMap.put(InterruptErr, new ScriptException(
        "The process was interrupted while running the " +
        "following command: ??", false));
  }

  /**
   * Error creating or opening a script file
   */
  public static final String ScriptIOErr =
      "org.jax.mgi.shr.script.ScriptIOErr";
  static {
    exceptionsMap.put(ScriptIOErr, new ScriptException(
        "The RunCommand generated an IO error " +
        "while executing the following command: ??", false));
  }

  /**
   * A nozero return value was received during the execution of the script
   */
  public static final String NonZeroErr =
      "org.jax.mgi.shr.script.NonZeroErr";
  static {
    exceptionsMap.put(NonZeroErr, new ScriptException(
        "Runcommand returned non-zero status when " +
        "executing the following command: ??", false));
  }

  /**
   * Errors found in script out file
   */
  public static final String ScriptErrorOutput =
      "org.jax.mgi.shr.script.ScriptErrorOutput";
  static {
    exceptionsMap.put(ScriptErrorOutput, new ScriptException(
        "Errors were found in script out file: ??", false));
  }


  /**
   * An error accessing the script out file
   */
  public static final String ScriptOutFileErr =
      "org.jax.mgi.shr.script.ScriptOutFileErr";
  static {
    exceptionsMap.put(ScriptOutFileErr, new ScriptException(
        "Could not access script out file: ??", false));
  }



}