package org.jax.mgi.shr.log;

/**
 *  A basic logger interface
 * @has nothing
 * @does logs messages of three types: info, error and debug
 * @company The Jackson Labatory
 * @author M Walker
 */

public interface Logger {

  /**
   * log an informational message to the logger
   * @assumes nothing
   * @effects a message get logged
   * @param message the message to log
   */
  public void logInfo(String message);

  /**
   * log an error message to the logger
   * @assumes nothing
   * @effects a message get logged
   * @param message the message to log
   */
  public void logError(String message);


  /**
   * log a debug message to the logger
   * @assumes nothing
   * @effects a message get logged
   * @param message the message to log
   */
  public void logDebug(String message);


  /**
   * turn debug logging on or off
   * @assumes nothing
   * @effects debug messages will either be allowed or prevented
   * @param bool true if debug should be set to on, false otherwise
   */
  public void setDebug(boolean bool);

  /**
   * get whether debug logging is on or off
   * @assumes nothing
   * @effects nothing
   * @return boolean indicator
   */
  public boolean isDebug();

  /**
   * closes any file handlers
   * @assumes nothing
   * @effects file handlers would be closed
   */
  public void close();

}