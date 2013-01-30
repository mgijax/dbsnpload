package org.jax.mgi.shr.log;

/**
 * A Logger instance factory
 * @has nothing
 * @does Creates an instance of a Logger
 * @author M Walker
 */

public interface LoggerFactory {

    /**
     * get an instance of Logger
     * @return a Logger
     */
  public Logger getLogger();

}