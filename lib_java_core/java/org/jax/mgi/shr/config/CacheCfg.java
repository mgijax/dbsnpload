package org.jax.mgi.shr.config;

import org.jax.mgi.shr.log.LoggerFactory;

/**
 * A class for accessing configuration information for the cache package
 * @has accessor methods for accessing configuration parameters
 * @does lookups configuration parameters from the ConfigurationManagement
 * object
 * @company The Jackson Laboratory
 * @author M Walker
 */



public class CacheCfg extends Configurator {

  /**
   * default constructor which will use unprefixed parameters from the
   * configuration file for configuring
   * @throws ConfigException thrown if the there is an error accessing the
   * configuration file
   */

  public CacheCfg() throws ConfigException {
    super();
  }

  /**
   * get whether or not to log debug messages
   * The parameter name read from the configuration file or system properties
   * is CACHE_DEBUG. The default value is false.
   * @return true if debug messages are to be logged, otherwise false
   * @throws ConfigException
   */
  public Boolean getDebug() throws ConfigException
  {
      return getConfigBoolean("CACHE_DEBUG", new Boolean(false));
  }




}
