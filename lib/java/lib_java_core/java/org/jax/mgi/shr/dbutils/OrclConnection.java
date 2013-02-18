package org.jax.mgi.shr.dbutils;

// Standard packages needed for SQL

import java.sql.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * OrclConnection is a customized ConnectionManager class that can be used to
 * get connections to a Oracle database.
 * @has nothing
 * @does gets a mysql connection
 * @author MWalker
 * @company The Jackson Laboratory
*/
public class OrclConnection implements ConnectionManager
{
   /**
    * Returns a database connection to the server/database implied by url.
    *  @param database the database
    *  @param user the database user
    *  @param password the user's password
    *  @param directDatabaseURL the direct database URL
    *  @return a database Connection
    *  @throws SQLException (if connection cannot be created)
    */
   public Connection getConnection(String database,
                                   String user,
                                   String password,
                                   String directDatabaseURL)
      throws SQLException
       {
         init();
         Properties props = new Properties();
         String connectionURL;

         connectionURL = "jdbc:oracle:thin:@" +
                          directDatabaseURL + "/" + database;

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
      public void init()
      {
        try {
          Driver d =
          (Driver) Class.forName("oracle.jdbc.OracleDriver").newInstance();
           DriverManager.registerDriver(d);
        }
        catch (Exception ex)
        {
           System.err.println("Error Message: " + ex.getMessage());
        }
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

/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
