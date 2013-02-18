package org.jax.mgi.shr.log;

/**
 * This is an implementation of the LoggerFactory interface and provides
 * a way to obtain an ConsoleLogger.
 * @has nothing
 * @does creates a new Logger class
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class ConsoleLoggerFactory implements LoggerFactory
{

    /**
     * get an instance of a Logger
     * @assumes nothing
     * @effects nothing
     * @return a Logger instance
     */
    public Logger getLogger()
    {
        return new ConsoleLogger();
    }

}