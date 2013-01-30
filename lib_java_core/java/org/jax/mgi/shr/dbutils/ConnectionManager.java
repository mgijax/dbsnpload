package org.jax.mgi.shr.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An object which handles obtaining connections from the database
 * @has nothing
 * @does gets a database connection
 * @company The Jackson Laboratory
 * @author mbw
 */
public interface ConnectionManager
{
  /**
   * get a database connection
   * @assumes nothing
   * @effects a new connection is created in the database
   * @param database the database
   * @param user the user account to login with
   * @param password the password of the user account
   * @param url the database url
   * @return the database connection
   * @throws SQLException thrown if an exception is thrown from the database
   */
  public Connection getConnection(String database,
                                  String user,
                                  String password,
                                  String url) throws SQLException;

}
