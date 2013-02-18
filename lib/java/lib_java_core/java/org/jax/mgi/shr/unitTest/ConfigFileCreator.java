package org.jax.mgi.shr.unitTest;


/**
 * @is A class used to create a database config file on the fly for use
 * in java unit testing programs which require a SQLDataManager or any type
 * of config file which uses the ConfigurationManager class. The name of
 * the config file is passed into the java runtime command by way of the CONFIG
 * parameter.
 * @has a server name, database name, user name, jdbc url base as expected
 * by the MGIDriverManager class, and a name of a password file which contains
 * the user password. By default these are as follows: DEV_MGI, mgd, mgd_dbo,
 * rohan.informatics.jax.org.4101, and the standard password filename.
 * @does provides methods for assigning database connection parameters and
 * creating any configuration setting which is subsequently written out to
 * a config file on the fly for use in java unit testing.
 * @Company Jackson Laboratory
 * @author M Walker
 */

public class ConfigFileCreator {
  String s = "#format: sh\n";

  /**
   * default constructor
   */
  public ConfigFileCreator() {
  }

  /**
   * add any additional parameter to the config file
   * @param parameterName name of the parameter
   * @param parameterValue the value of the parameter
   */
  public void addParameter(String parameterName, String parameterValue) {
    s = s + parameterName + "=" + parameterValue + "\n";
  }


  /**
   * create the config
   * @param name name of the config file
   * @throws KnownException
   */
  public void createConfig(String name) throws Exception {
    FileUtility.createFile(name, s);
  }

}