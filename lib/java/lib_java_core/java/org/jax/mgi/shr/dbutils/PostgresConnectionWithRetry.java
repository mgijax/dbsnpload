package org.jax.mgi.shr.dbutils;
import java.sql.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/** PostgresConnectionWithRetry is a customized ConnectionManager class that
 * 	can be used to get connections to a PostgreSQL database.  If there are
 * 	no available connections left on the server, this class will wait and
 * 	retry a few times before simply raising an exception.
*/
public class PostgresConnectionWithRetry implements ConnectionManager
{
    boolean logFailures = false;	// should we log failures to stdout?

   /** Opens and returns a database connection to the database specified in
    * 	the input parameters.  If the server has no available connections, we
    * 	wait and retry several times before raising an exception.
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

	 Connection conn = null;

	 int attempts = 0;	// number of connection attempts so far
	 int maxAttempts = 10;	// maximum number of attempts allowed
	 long delay = 100;	// number of milliseconds between attempts

	 while (conn == null) {
	    try {
		attempts++;
                conn = DriverManager.getConnection(connectionURL, props);
	    } catch (SQLException e) {
		String message = e.getMessage();
		String stringVal = e.toString();
		Throwable cause = e.getCause();

		// is it a bad username / password?  if so, fatal error
		if (message.indexOf("password authentication failed") >= 0) {
		    throw e; 

		// is it an unknown database?  if so, fatal error
		} else if ((message.indexOf("database") >= 0) && 
			(message.indexOf("does not exist") >= 0)) {
		    throw e;

		// is it an unknown host?  if so, fatal error
		} else if ((cause != null) &&
		    (cause.toString().indexOf("UnknownHostException") >= 0)) {
		    throw e;

		// have we already used up our retries?  if so, fatal error
		} else if (attempts >= maxAttempts) {
		    logFailure (hostAndPort, database, user, attempts, false);
		    throw new SQLException(
			"Failed to get a database connection after "
			+ attempts + " attempts.  Giving up.");
		}

		// If we are at this point, we know that the host, database,
		// user, and password are valid.  So, assume that the server
		// is too busy and wait a little while before trying again.
		
		logFailure (hostAndPort, database, user, attempts, true);
		try {
		    Thread.sleep(delay);
		} catch (InterruptedException ie) {}

		delay = delay * 2;	// bigger delay next time, if needed
	    }
	 }

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

      /** turn on logging of failures to stdout (useful for javawi2_pg)
       */
      public void logFailuresToStdout() {
	  this.logFailures = true;
	  return;
      }

      /** log connection failure to stdout, if such logging is turned on.
       */
      public void logFailure (String host, String database, String user,
		int attempt, boolean willRetry) {

	  if (!this.logFailures) { return; }

	  String s = "PostgresConnectionWithRetry: Failed to get connection"
	      + " for " + host + ":" + database
	      + " as " + user + "; ";

	  if (willRetry) {
	      s = s + "waiting to retry (attempt " + attempt + ")";
	  } else {
	      s = s + "giving up (attempt " + attempt + ")";
	  }
	  System.out.println (s);
      }
}
