/**
 * Title:        MGI Driver Manager
 * Description:  A convenience library to allow Java clients to easily use
 *               JNDI/LDAP or a direct database URL to connect to a database.
 * Limitations:  Currently Sybase-specific, but can probably be changed easily
 *               to support multiple database vendors.
 * Copyright:    Copyright (c) 2000 The Jackson Laboratory
 * Company:      Mouse Genome Informatics
 * @author       gld
 */

package org.jax.mgi.shr.dbutils;

// Standard packages needed for Sybase SQL

import java.util.*;
import java.sql.*;

// JNDI stuff
import javax.naming.*;

/**
* MGIDriverManager is a customized ConnectionManager class that MGIers use to
* get database connections. It is not a subclass of jdbc2.DriverManager because
* that class has a private constructor, and can thus not be derived from.
* @author gld
*/
public class MGIDriverManager implements ConnectionManager
{
	private static String CURRENT_DRIVER="sybase";
   /**
    * Returns a database connection to the server/database implied by
    *  the ldapurl_or_hostport. Note that "dataserver" is ignored if a non-LDAP
    *  URL is provided (since the name is not needed, and can not be used
    *  without consulting LDAP services).
    *  @param dataserver the database server ("DEV_MGI")
    *  @param database the database          ("mgd")
    *  @param user the database user
    *  @param password the user's password
    *  @param ldapurl_or_hostport the LDAP URL or a direct database URL
    *   ("ldap://<host>:<port>" or "host:port")
    *  @return a database Connection
    *  @throws SQLException (if connection cannot be created)
    */
   public static Connection getConnection(String dataserver,
                            String database, String user, String password,
                            String ldapurl_or_hostport)
      throws SQLException
       {
         Properties props = new Properties();
         String connectionURL;

         if (isldapURL(ldapurl_or_hostport)){
            String ldapServerURL = ldapurl_or_hostport;
            props.put(Context.INITIAL_CONTEXT_FACTORY,
                     "com.sun.jndi.ldap.LdapCtxFactory");

            // identify the LDAP server
            props.put(Context.PROVIDER_URL, ldapServerURL);

            // "ldap://mishka:2021" is an example of an LDAP server url.
            connectionURL = "jdbc:sybase:jndi:" + ldapServerURL +
                            "/database=" + dataserver + ".." +
                            database + ",ou=Databases,o=MGI";
         }
         else if(CURRENT_DRIVER.equals("postgres"))
	 {
            String directDatabaseURL = ldapurl_or_hostport;
            connectionURL = "jdbc:postgresql://" +
                             directDatabaseURL + "/" + database;
	 }
	 else
	 {
            String directDatabaseURL = ldapurl_or_hostport;
            connectionURL = "jdbc:sybase:Tds:" +
                             directDatabaseURL + "/" + database;
         }
	System.out.println("conn url: "+connectionURL);

         // initialize the properties for the connection
         props.put("user",user);
         props.put("password",password);
         return DriverManager.getConnection(connectionURL, props);
      }

      /**
       *  Loads the database driver. Must be called prior to using
       *  getConnection().
       *  requires: nothing
       *  modifies: nothing
       */
      public static void init()
      {
          String classpath = System.getProperty("java.class.path",".");
	System.out.println("classpath: "+classpath);
          int jconn3 = classpath.indexOf("jconn3");
          int jconn2 = classpath.indexOf("jconn2");
	  int postgres = classpath.indexOf("postgresql");
          String driver = "com.sybase.jdbc2.jdbc.SybDriver";
          if ((jconn3 > 0 && (jconn2 < 0 || jconn3 < jconn2)))
              driver = "com.sybase.jdbc3.jdbc.SybDriver";
	  if(postgres>0)
	  {
	    driver = "org.postgresql.Driver";
	    CURRENT_DRIVER="postgres";
	  }
	 
	System.out.println("db driver: "+driver);


        try {
          Driver d =
          (Driver) Class.forName(driver).newInstance();
           DriverManager.registerDriver(d);
        }
        catch (Exception ex)
        {
	   ex.printStackTrace();
           System.err.println("Error Message: " + ex.getMessage());
        }
      }

      /**
       * get a database connection
       * @param database the name of the database
       * @param user the name of the user for the login account
       * @param password the password for the login account
       * @param directDatabaseURL the url for the JDBC connection
       * @return the connection
       * @throws SQLException thrown if there is an error connecting to the
       * database
       */
      public Connection getConnection(String database,
                                      String user,
                                      String password,
                                      String directDatabaseURL)
          throws SQLException
      {
        init();
        return getConnection(null, database, user,
                             password, directDatabaseURL);
      }


      /**
       *  Identifies if "url" is an ldapURL or a direct database url.
       *  @param url a valid database connection url for JDBC
       *  @return true, if the URL starts with "ldap:", false otherwise.
       */
      private static boolean isldapURL(String url)
      {
         if (url.startsWith("ldap:"))
            return true;
         return false;
      }
}

