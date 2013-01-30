package org.jax.mgi.shr.config;

/**
 * A configuration object for configuring a database connection
 * @has configuration variables and accessor methods
 * @does provides values for data connection parameters
 * @company Jackson Laboratory
 * @author M Walker
 */

public interface DatabaseConfigurator {

  /**
   * get the server name that hosts the database.
   * @return server name
   */
  public String getServer();

  /**
   * get the name of the database.
   * @return database name
   */
  public String getDatabase();

  /**
   * get the login user name for the database connection.
   * @return login user
   */
  public String getUser();

  /**
   * get the login password for the database connection.
   * @return login password
   */
  public String getPassword();

  /**
   * get the database url.
   * @return database url
   */
  public String getUrl();

  /**
   * get the name of a file that holds the login password for the
   * database connection.
   * @return password file name
   */
  public String getPasswordFile();

}

