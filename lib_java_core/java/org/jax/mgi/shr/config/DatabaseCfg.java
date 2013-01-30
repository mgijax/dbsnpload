// $Header$
// $Name$

package org.jax.mgi.shr.config;


/**
 * A class for configuring a database connection.
 * @has a set of database connection parameters and a reference to
 * a ConfigurationManager
 * @does provides methods for getting configuration paramaters
 * for a database connection and for providing default values for those
 * parameters that are not configured
 * @company Jackson Laboratory
 * @author M. Walker
 */

public class DatabaseCfg
    extends Configurator implements DatabaseConfigurator {

  private String DEFAULT_SERVER = "DEV_MGI";
  private String DEFAULT_DATABASE = "mgd";
  private String DEFAULT_USER = "mgd_dbo";
  private String DEFAULT_URL = "rohan.informatics.jax.org:4100";
  private String DEFAULT_PWFILE =
      "/usr/local/mgi/live/dbutils/mgidbutilities/.mgd_dbo_dev_password";
  private String DEFAULT_DBSCHEMA_DIR =
      "/usr/local/mgi/live/dbutils/mgd/mgddbschema";
  private String DEFAULT_CONNECTION_MANAGER =
      "org.jax.mgi.shr.dbutils.MGIDriverManager";
  private int DEFAULT_MAX_INCLAUSE = 400;

  /**
   * default constructor which obtains a reference to the ConfigurationManager
   * singleton class.
   * @throws ConfigException
   */
  public DatabaseCfg() throws ConfigException {
    super();
  }

  /**
   * constructor which accepts a prefix string that will be prepended to
   * all lookup parameter names
   * @param pParameterPrefix the given prefix string
   * @throws ConfigException throws if there is a configuration error
   */
  public DatabaseCfg(String pParameterPrefix) throws ConfigException {
    super();
    super.parameterPrefix = pParameterPrefix;
  }


  /**
   * get the server name that hosts the database. The name of the
   * configuration parameter is DBSERVER. The default value is
   * DEV_MGI. If prefixing is used and the value is not found, then a
   * second lookup will be performed on the non-prefixed value. If the
   * second lookup fails then the default value is used.
   * @return server name
   */
  public String getServer() {
    return getConfigString("DBSERVER", DEFAULT_SERVER);
  }

  /**
   * get the name of the database. The name of the configuration
   * parameter is DBNAME. The default value if not set is mgd.
   * @return database name
   */
  public String getDatabase() {
    return getConfigString("DBNAME", DEFAULT_DATABASE);
  }

  /**
   * get the login user name for the database connection. The name of the
   * configuration parameter is DBUSER. The default value if not set is
   * mgd_dbo.
   * @return login user
   */
  public String getUser() {
    return getConfigString("DBUSER", DEFAULT_USER);
  }

  /**
   * get the login password for the database connection. The name of the
   * configuration parameter is DBPASSWORD. There is no default value for
   * this parameter. If it is not specified in the configuration file then
   * its value will be null. It is recommended to use the DBPASSWORDFILE
   * parameter instead to point to file which contains the passowrd
   * @return login password
   */
  public String getPassword() {
    return getConfigString("DBPASSWORD", null);
  }

  /**
   * get the database url. The name of the configuration parameter is DBURL.
   * The default value if not set is rohan.informatics.jax.org:4100.
   * @return database url
   */
  public String getUrl() {
    return getConfigString("DBURL", DEFAULT_URL);
  }

  /**
   * get the name of a file that holds the login password for the
   * database connection. The name of the configuration
   * parameter is DBPASSWORDFILE. The default value if not set is
   * /usr/local/mgi/dbutils/mgidbutilities/.mgd_dbo_password
   * @return password file name
   */
  public String getPasswordFile() {
    return getConfigString("DBPASSWORDFILE", DEFAULT_PWFILE);
  }

  /**
   * get the directory name for the installation of the database schema
   * product. The name of the configuration parameter is DBSCHEMADIR.
   * The default value if not set is
   * /usr/local/mgi/dbutils/mgidbutilities/mgd/mgddbschema.
   * @return database schema product installation directory
   */
  public String getDBSchemaDir() {
    return getConfigString("DBSCHEMADIR", DEFAULT_DBSCHEMA_DIR);
  }


  /**
   * get the name of the ConnectionManager class used for instantiating
   * a ConnectionManager instance which is used for obtaining a connection
   * to the database. The name of the configuration parameter is
   * DBCONNECTION_MANAGER. The default value if not set is
   * org.jax.mgi.shr.dbutils.MGIDriverManager
   * @return the name of the ConnectionManager class
   */
  public String getConnectionManagerClass() {
    return getConfigString("DBCONNECTION_MANAGER",
                           DEFAULT_CONNECTION_MANAGER);
  }

  /**
   * get whether or not to log debug messages
   * The parameter name read from the configuration file or system properties
   * is DBDEBUG. The default value is false.
   * @return true if debug messages are to be logged, otherwise false
   * @throws ConfigException thrown if the paramater value has some format
   * errors
   */
  public Boolean getDebug() throws ConfigException
  {
      return this.getConfigBoolean("DBDEBUG", new Boolean(false));
  }


  /**
   * get the maximum allowed number of objects to use for each sql statement
   * when composing a dynamic 'in clause'. There is a limit in Sybase for
   * this number. The default value is set to 400.
   * @return the maximum number of objects
   * @throws ConfigException
   */
  public Integer getMaxInClause() throws ConfigException
  {
      return this.getConfigInteger("DB_MAX_INCLAUSE",
                                   new Integer(DEFAULT_MAX_INCLAUSE));
  }




}
// $Log$
// Revision 1.7  2005/08/15 14:58:43  mbw
// javadocs only
//
// Revision 1.6  2005/08/05 16:28:00  mbw
// merged code from branch lib_java_core-tr6427-1
//
// Revision 1.5.4.2  2005/08/01 19:14:08  mbw
// javadocs only
//
// Revision 1.5.4.1  2005/06/02 19:43:25  mbw
// added getMaxInClause method
//
// Revision 1.5  2004/09/30 15:37:40  mbw
// added a getDebug() method
//
// Revision 1.4  2004/08/25 20:25:01  mbw
// changed test on default values to reflect use of new "live" directory
//
// Revision 1.3  2004/07/21 19:08:50  mbw
// removed the use of parameter prefixing from this class since it was recently moved to the base class
//
// Revision 1.2  2004/04/02 19:01:33  mbw
// changed default value of dbo password file to refect new file name
//
// Revision 1.1  2003/12/30 16:50:07  mbw
// imported into this product
//
// Revision 1.4  2003/12/09 22:48:39  mbw
// merged jsam branch onto the trunk
//
// Revision 1.3.2.19  2003/10/22 15:38:12  mbw
// no change
//
// Revision 1.3.2.18  2003/09/10 21:07:52  mbw
// now doing a second lookup if prefixing is being used for the non-prefixed term before applying the default
//
// Revision 1.3.2.17  2003/06/23 19:36:34  mbw
// added methods for obtaining a connection_manager and a logger
//
// Revision 1.3.2.16  2003/06/04 14:51:44  mbw
// javadoc edits
//
// Revision 1.3.2.15  2003/05/22 16:15:34  mbw
// now implements DatabaseConfigurator interface
//
// Revision 1.3.2.14  2003/05/22 15:52:02  mbw
// javadocs edits
//
// Revision 1.3.2.13  2003/05/16 15:11:15  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.3.2.12  2003/04/25 17:17:44  mbw
// changed to reflect new design of exception handling
//
// Revision 1.3.2.11  2003/04/18 01:53:51  mbw
// changed classes to extend Configurator
//
// Revision 1.3.2.10  2003/04/15 12:00:22  mbw
// javadoc edits
//
// Revision 1.3.2.9  2003/04/09 21:18:21  mbw
// removed setter methods; replaced code in getter methods to use ConfigLookup class; added parameter name prefixing feature
//
// Revision 1.3.2.8  2003/04/04 00:07:00  mbw
// added clone method
//
// Revision 1.3.2.7  2003/04/03 18:58:46  mbw
// javadocs
//
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
