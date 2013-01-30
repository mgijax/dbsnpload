package org.jax.mgi.shr.config;

import org.jax.mgi.shr.log.LoggerFactory;

/**
 * A class for accessing configuration information for logging
 * @has accessor methods for accessing configuration parameters
 * @does lookups configuration parameters from the ConfigurationManagement
 * object
 * @company The Jackson Laboratory
 * @author M Walker
 */



public class LogCfg extends Configurator {

  /**
   * default input directory containing report templates
   */
  private String DEFAULT_FACTORY = "org.jax.mgi.shr.log.ConsoleLoggerFactory";

  /**
   * default constructor which will use unprefixed parameters from the
   * configuration file for configuring
   * @throws ConfigException thrown if the there is an error accessing the
   * configuration file
   */

  public LogCfg() throws ConfigException {
    super();
  }


  /**
   * constructor which accepts a prefix string that will be prepended to
   * all configuration parameter on lookup
   * @param pParameterPrefix the given prefix string
   * @throws ConfigException throws if there is a configuration error
   */

  public LogCfg(String pParameterPrefix) throws ConfigException {
    super();
    super.parameterPrefix = pParameterPrefix;
  }


  /**
   * get LoggerFactory class for accessing an instance of Logger.
   * The parameter name read from the configuration file or system properties
   * is LOG_FACTORY. The default value is org.jax.mgi.shr.log.ConsoleLoggerFactory.
   * @return the LoggerFactory class.
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   */

  public LoggerFactory getLoggerFactory() throws ConfigException {
    return (LoggerFactory)getConfigObject("LOG_FACTORY", DEFAULT_FACTORY);
  }

  public Boolean getDebug() throws ConfigException
  {
      return getConfigBoolean("LOG_DEBUG", new Boolean(false));
  }




}
