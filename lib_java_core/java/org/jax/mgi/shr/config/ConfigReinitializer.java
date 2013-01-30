package org.jax.mgi.shr.config;

/**
 * A class for reinitializing the ConfigurationManager class and having it
 * re-read the configuration files
 * @author M Walker
 */

public class ConfigReinitializer {

  public static void reinit() throws ConfigException
  {
    ConfigurationManager cm = ConfigurationManager.getInstance();
    cm.reinit();
  }

}