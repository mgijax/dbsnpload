package org.jax.mgi.shr.dbutils;
import java.sql.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/** PostgresConnection is a customized ConnectionManager class that can be
 *	used to get connections to a PostgreSQL database.
*/
public class PostgresConnection implements ConnectionManager
{
   /** Opens and returns a database connection to the database specified in
    * 	the input parameters.
    *  @param database database name
    *  @param user the database username
    *  @param password the user's password
    *  @param hostAndPort the host and port for the Postgres server
    * ("rohan.informatics.jax.org:5432")
    *  @return a database Connection
    *  @throws SQLException (if connection cannot be created)
    */
   public Connection getConnection(String database,
                                   String user,
                                   String password,
                                   String hostAndPort)
      throws SQLException
       {
         this.init();
         Properties props = new Properties();
         String connectionURL;

         connectionURL = "jdbc:postgresql://" + hostAndPort + "/" + database;

         // initialize the properties for the connection
         props.put("user",user);
         props.put("password",password);
         Connection conn = DriverManager.getConnection(connectionURL, props);
	 return conn;
      }

      /**  Loads the database driver. Must be called prior to using
       *  getConnection().
       *  requires: nothing
       *  modifies: nothing
       */
      public void init()
      {
        try {
          Driver d =
          (Driver) Class.forName("org.postgresql.Driver").newInstance();
           DriverManager.registerDriver(d);
        }
        catch (Exception ex)
        {
           System.err.println("Error Message: " + ex.getMessage());
        }
      }
}
