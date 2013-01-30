package org.jax.mgi.shr.ioutils;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;

import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.LogCfg;
import org.jax.mgi.shr.config.OutputDataCfg;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.LoggerFactory;
import org.jax.mgi.shr.log.ConsoleLogger;
import org.jax.mgi.shr.unix.RunCommand;
import org.jax.mgi.shr.exception.MGIException;

/**
 * Is an object that represents an output data file. It is configurable using
 * the org.jax.mgi.shr.config package
 * @has OutputDataCfg object for configuration
 * @does Provides the ability to write raw data to an output file
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class OutputDataFile
{
    /**
     * a system constant for representing the tab character
     */
    public static final String TAB = "\t";
    /**
     * a system constant for representing the carriage return character
     */
    public static final String CRT = System.getProperty("line.separator");

    // The name of the output file to write to.
    //
    private String filename = null;

    // The name of the output file to write to.
    //
    //private BufferedWriter writer = null;
    private FileWriter writer = null;

    // A logger for logging messages.
    //
    private Logger logger = null;

    // option to auto-flush the buffer after each bcp write
    private boolean okToAutoFlush = false;

    // boolean indicator to determine if the file has been closed already
    private boolean isOpen = false;

    // the configuration object
    private OutputDataCfg config = null;

    // The following are the exceptions that are thrown.
    //
    private static final String NullOutFilename =
        IOUExceptionFactory.NullOutFilename;
    private static final String FileOpenErr =
        IOUExceptionFactory.FileOpenErr;
    private static final String FileWriteErr =
        IOUExceptionFactory.FileWriteErr;
    private static final String FileReadErr =
        IOUExceptionFactory.FileReadErr;
    private static final String FileCloseErr =
        IOUExceptionFactory.FileCloseErr;

    /**
     * default constructor
     * @throws IOUException thrown if there is a problem accessing the
     * database
     * @throws ConfigException thrown if there is a problem accessing the
     * configuration
     */
    public OutputDataFile()
    throws IOUException, ConfigException
    {
        configure(new OutputDataCfg());
    }

    /**
     * constructor which accepts a configuration object as input
     * @param cfg the configuration object from which to configure this
     * instance
     * @throws IOUException thrown if there is a problem creating the file
     * @throws ConfigException thrown if there is a problem accessing the
     * configuration
     */
    public OutputDataFile(OutputDataCfg cfg)
    throws IOUException, ConfigException
    {
        configure(cfg);
    }

    /**
     * constructor which allows specifying the filename at runtime and would
     * override any specified filename value from the configuration.
     * @param filename The name of the output file to write to.
     * @throws ConfigException if there is a configuration error.
     * @throws IOUException thrown if there is a problem creating the file.
     */
    public OutputDataFile(String filename)
        throws ConfigException, IOUException
    {
        this.filename = filename;
        configure(new OutputDataCfg());
    }

    /**
     * constructor which takes both a configuration object and a filename
     * @param filename The name of the output file to write to.
     * @param cfg The configuration object
     * @throws ConfigException if there is a configuration error.
     * @throws IOUException thrown if there is a problem creating the file.
     */
    public OutputDataFile(String filename, OutputDataCfg cfg)
        throws ConfigException, IOUException
    {
        this.filename = filename;
        configure(cfg);
    }

    /**
     * sets whether or not to flush the output buffer on each write
     * @assumes nothing
     * @effects alters the indicator value
     * @param bool true if the buffer should be flushed, false otherwise
     */
    public void setOKToAutoFlush(boolean bool)
    {
        this.okToAutoFlush = bool;
    }

    /**
     * set the Logger for this instance.
     * @assumes nothing
     * @effects alters the logger being used
     * @param logger the Logger object to use for logging
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Gets the name of the output file to write to.
     * @assumes Nothing
     * @effects Nothing
     * @return The name of the output file to write to.
     */
    public String getFilename()
    {
        return this.filename;
    }

    /**
     * gets the current configured value for whether or not to flush the
     * output buffer on every write
     * @assumes Nothing
     * @effects Nothing
     * @return true if the buffer is set to be flushed, false otherwise
     */
    public boolean getOKToAutoFlush()
    {
        return this.okToAutoFlush;
    }

    /**
     * get the instance of the current Logger
     * @assumes Nothing
     * @effects Nothing
     * @return the instance of the current Logger
     */
    public Logger getLogger()
    {
        return this.logger;
    }


    /**
     * write a string to the output file.
     * @assumes a newline is not wanted after the write
     * @effects new output will be written to the file
     * @param s the string to write
     * @throws IOUException if an error occurs finding or opening the file.
     */
    public void write(String s)
        throws IOUException
    {
        //if (!this.isOpen)
        //    this.openWriter();
        try
        {
            writer.write(s);
            if (this.okToAutoFlush)
                writer.flush();

        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory = new IOUExceptionFactory();
            IOUException ke = (IOUException)
                exceptionFactory.getException(FileWriteErr, e);
            ke.bind(filename);
            throw ke;
        }
    }



    /**
     * write a string to the output file followed by a newline character.
     * @assumes a newline is wanted after the write
     * @effects new output will be written to the file
     * @param s the string to write
     * @throws IOUException if an error occurs finding or opening the file.
     */
    public void writeln(String s)
        throws IOUException
    {
        write(s + this.CRT);
    }




    /**
     * close the output file
     * @assumes Nothing
     * @effects the raw data will be saved along with all formatted files
     * @throws IOUException if an error occurs finding or opening the file.
     * @throws ConfigException thrown if there is an error accessing the
     * configuration for formatting the output file
     */
    public void close()
        throws IOUException, ConfigException
    {
        try
        {
            if (this.isOpen)
                this.writer.close();
            else
            {
                // create empty file for a file that was never opened
                FileUtility.createFile(this.filename);
            }
            this.isOpen = false;
        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory = new IOUExceptionFactory();
            IOUException ke = (IOUException)
                exceptionFactory.getException(FileCloseErr, e);
            ke.bind(filename);
            throw ke;
        }
    }

    /**
     * flush the buffered output to the output file
     * @assumes nothing
     * @effects buffer content will be written to the file
     * @throws IOUException thrown if there is an error accessing the file
     * system
     */
    protected void flush()
    throws IOUException
    {
        try
        {
            this.writer.flush();
        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory =
                new IOUExceptionFactory();
            IOUException e2 = (IOUException)
                exceptionFactory.getException(FileWriteErr, e);
            e2.bind(this.filename);
            throw e2;
        }
    }

    private void configure(OutputDataCfg cfg)
    throws IOUException, ConfigException
    {
        this.config = cfg;
        if (this.filename == null)
            this.filename = cfg.getOutputFileName();
        if (this.filename == null)
        {
            IOUExceptionFactory exceptionFactory = new IOUExceptionFactory();
            throw
                (IOUException)exceptionFactory.getException(NullOutFilename);
        }
        this.logger = new ConsoleLogger();
        this.okToAutoFlush = cfg.getOkToAutoFlush().booleanValue();
	this.openWriter();
    }

    private void openWriter()
    throws IOUException
    {
        try
        {
            writer = new FileWriter(this.filename);
        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory = new IOUExceptionFactory();
            IOUException e2 = (IOUException)
                exceptionFactory.getException(FileOpenErr, e);
            e2.bind(this.filename);
            throw e2;
        }
        this.isOpen = true;

    }

}
