package org.jax.mgi.shr.config;

import java.util.Properties;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.lang.reflect.Array;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jax.mgi.shr.unix.RunCommand;


/**
 * An object that looks up configuration parameters in the java system
 * properties along with a set of configuration files. It implements the
 * Singleton pattern.
 * @has The collection of java system properties and a set of
 * Configuration classes (each of which represent a configuration file).
 * @does looks up parameters in the system properties. If they are not
 * found then it looks them up within the set of Configuration classes. The
 * system properties will override properties found in configuration files.
 * Configuration files are determined by the CONFIG system property. This
 * property is a comma separated list of configuration filenames ordered by
 * precedence.
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class ConfigurationManager {

  private Configuration config = null;
  // these properties are searched first in order to override configurations
  private Properties systemProperties = null;
  // the singleton instance of the ConfigurationManager. It is returned by
  // the getInstance() method
  private static ConfigurationManager instance = null;
  // the exception factory for throwing ConfigExceptions
  private ConfigExceptionFactory eFactory = new ConfigExceptionFactory();

  /**
   * get the singleton instance of the ConfigurationManager.
   * @assumes nothing
   * @effects the Singleton instance is created if it didnt already
   * exist and the configuration files and system properties are read into
   * memory
   * @return a reference to the ConfigurationManagement instance
   * @throws ConfigException thrown if an exception occurs while reading the
   * configuration file.
   */
  protected static ConfigurationManager getInstance()
  throws ConfigException {
    if (instance == null) {
      instance = new ConfigurationManager();
    }
    return instance;
  }

  /**
   * creates an array of strings of the form name=value which represents
   * the current environment which may be useful for running exec methods
   * or methods in the RunCommand class
   * @return the array of environment parameters
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   */

  public static String[] createEnvArray()
  throws ConfigException
  {
      ConfigurationManager cm = ConfigurationManager.getInstance();
      String[] props = cm.getKeys();
      for (int i = 0; i < props.length; i++)
      {
          String keyvalue = props[i] + "=" + cm.get(props[i]);
          props[i] = keyvalue;
      }
      return props;
  }

  /**
   * look up a value of a named parameter in the system properties
   * and if not found then lookup within the set of Configuration classes.
   *
   * @assumes nothing
   * @effects nothing
   * @param name the named parameter to search on
   * @return the value of the named parameter or null if not found
   */
  protected String get(String name) {
    String value;
    this.systemProperties = System.getProperties();
    value = (String)systemProperties.get(name);
    if (value == null && config != null)
      value = config.get(name);
    return value;
  }

  /**
   * get the key values for all the configuration parameters from the
   * configuration file(s) and the java system properties
   * @assumes nothing
   * @effects nothing
   * @return an array of configuration parameter names as Strings
   */
  protected String[] getKeys()
  {
      int counter = 0;
      ArrayList list = new ArrayList();
      this.systemProperties = System.getProperties();
      Enumeration e = this.systemProperties.propertyNames();
      while (e.hasMoreElements())
      {
          list.add(e.nextElement());
          counter++;
      }
      if (config != null)
      {
          Iterator it = this.config.keys();
          while (it.hasNext())
          {
              list.add(it.next());
              counter++;
          }
      }
      String[] keys = new String[counter];
      for (int i = 0; i < list.size(); i++)
      {
          keys[i] = (String)list.get(i);
      }
      return keys;
  }


  /**
   * reread system properties and configuration files
   * @assumes nothing
   * @effects the configuration files and system properties are
   * reread into memory obtaining any runtime changes
   * @throws ConfigException thrown if an exception occurs reading the
   * configuration files
   */
  protected void reinit() throws ConfigException {
    instance.init();
  }

  /**
   * private constructor
   * @assumes nothing
   * @effects the ConfigurationInstance will be created and the
   * configuration files and system properties are read into memory
   * @throws ConfigException thrown if an exception occures reading the
   * configuration file.
   */
  private ConfigurationManager() throws ConfigException {
    init();
  }

  /**
   * read configuration files and system properties
   * @assumes nothing
   * @effects the ConfigurationInstance will be created and the
   * configuration files and system properties are read into memory
   * @throws ConfigException thrown if there is an exception reading the
   * configuration files
   */
  private void init() throws ConfigException {
    systemProperties = System.getProperties();
    String value = (String)systemProperties.get("CONFIG");

    if (value != null)
    {
        String[] configfiles = value.split(",");
        int len = Array.getLength(configfiles);
        String filename = configfiles[0];
        try
        {
            // load first file and do not use previously cached file
            config = Configuration.load(filename, false);
            // include the remaining
            for (int i = 1; i < len; i++)
            {
                filename = configfiles[i];
                config.include(filename);
            }
        }
        catch (FileNotFoundException e) {
          ConfigException e2 = (ConfigException)eFactory.getException(
                   ConfigExceptionFactory.FileNotFound, e);
          e2.bind(filename);
          throw e2;
        }
        catch (IOException e) {
          ConfigException e2 = (ConfigException)eFactory.getException(
                   ConfigExceptionFactory.MgrLoadErr, e);
          e2.bind(filename);
          throw e2;
        }

    }
    else // null out the instance in case the reinit() method was called
        config = null;
  }


}
