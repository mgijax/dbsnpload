package org.jax.mgi.shr.unix;

import java.io.IOException;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.ConfigExceptionFactory;
import org.jax.mgi.shr.config.ConfigurationManager;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.ConsoleLogger;

/**
 * Is an abstract class representing an external command which can be run
 * from the Java application and which handles setting up the environment,
 * handling exceptions and logging the output of the command into the
 * application logs.
 * @has Logger for logging
 * parameters
 * @does executes an external command using the RunCommand class and
 * handles configuration, exception handling and logging. The current
 * configuration settings for the Java application are passed over to the
 * environment under which the command is executed.
 * @abstract the getCommandLine() must be implemented. It is called during
 * the run() method to obtain the command line string from the base class.
 * Additionally, configure(CommndCfg) must be implemented which is called
 * during the run() method so the base class can access configuration
 * settings to support creating the command line string. If no configuration
 * settings are used, then this method should be created as an empty method
 * call.
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public abstract class AbstractCommand {

    private Logger logger = null;

    /*
     * the following are constants for exceptions thrown by this class
     */
    private static final String InterruptErr =
        CommandExceptionFactory.InterruptErr;
    private static final String IOErr =
        CommandExceptionFactory.IOErr;
    private static final String NonZeroStatus =
        CommandExceptionFactory.NonZeroStatus;

    /**
     * default constructor which will use default values for any configuration
     * settings
     * @throws ConfigException thrown if there is an error accessing the
     * configuration file
     */
    public AbstractCommand()
    {
        this.logger = new ConsoleLogger();
    }


    /**
     * get the command line for this instance
     * @return the command line string to execute
     */
    protected abstract String getCommandLine() throws ConfigException;

    /**
     * set the logger used by this instance
     * @param logger the Logger instance to use
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * get the logger for this instance
     * @return the logger for this instance
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * execute the command
     * @assumes nothing
     * @effects an external command will be executed
     * @throws CommandException thrown if there is an error executing the
     * command
     * @throws ConfigException thrown if there is an error accessing the
     * configuration file
     */
    public void run()
    throws CommandException, ConfigException
    {
        prerun();
        String cmd = getCommandLine();
        String[] envp = ConfigurationManager.createEnvArray();
        RunCommand runner = new RunCommand(cmd, envp);
        int exitCode;
        try
        {
            exitCode = runner.run();
        }
        catch (InterruptedException e)
        {
            CommandExceptionFactory exceptionFactory =
                new CommandExceptionFactory();
            CommandException e2 = (CommandException)
                exceptionFactory.getException(InterruptErr, e);
            e2.bind(cmd);
            throw e2;
        }
        catch (IOException e)
        {
            CommandExceptionFactory exceptionFactory =
                new CommandExceptionFactory();
            CommandException e2 = (CommandException)
                exceptionFactory.getException(IOErr, e);
            e2.bind(cmd);
            throw e2;
        }
        // The RunCommand executed without exception although the exit code
        // may still indicate an error has occurred. Log the contents of
        // standard out and standard error
        String msgErr = null;
        String msgOut = null;
        if ((msgErr = runner.getStdErr()) != null)
            logger.logInfo(msgErr);
        if ((msgOut = runner.getStdOut()) != null)
        {
            if (logger != null)
                logger.logInfo(msgOut);
        }
        // exit code of non-zero indicates an error occurred
        if (exitCode != 0)
        {
            CommandExceptionFactory exceptionFactory =
                new CommandExceptionFactory();
            CommandException e2 = (CommandException)
                exceptionFactory.getException(NonZeroStatus);
            e2.bind(cmd);
            throw e2;
        }
        postrun();
    }

    protected void prerun() throws CommandException {}

    protected void postrun() throws CommandException {}

}
