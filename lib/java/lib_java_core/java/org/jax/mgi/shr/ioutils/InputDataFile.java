package org.jax.mgi.shr.ioutils;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channels;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.InputDataCfg;
import org.jax.mgi.shr.config.LogCfg;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.LoggerFactory;
import org.jax.mgi.shr.exception.MGIException;

/**
 * A class that represents an input file.
 * @has configurable parameters such as begin and end delimiters, buffersize and
 * character set.
 * @does provides the ability to iterate over objects from the input file
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public class InputDataFile
{

  // the record begin and end delimiters as strings. These will be interpreted
  // as a java regular expression unless the INFILE_USE_REGEX is set to false
  private String endDelimiter = null;
  private String beginDelimiter = null;

  // the size of the internal buffer
  private int bufferSize;

  // the indicator for whether to pattern match by regular expression or
  // direct byte sequence matching
  private boolean useRegex = true;

  // the indicator for whether to use stdin as the input data
  private String inputType = null;

  // the default filename from which to read records
  private String filename = null;

  // the charset to use when decoding input byte streams
  private String charset = null;

  // a Logger for logging debug messages
  private Logger logger = null;

  // the exception factory
  private IOUExceptionFactory exceptionFactory = new IOUExceptionFactory();

  // the following are the exceptions that are thrown
  private static final String DataReadEOF =
      IOUExceptionFactory.DataReadEOF;
  private static final String CloseErr =
      IOUExceptionFactory.CloseErr;
  private static final String NoRecordFound =
      IOUExceptionFactory.NoRecordFound;
  private static final String FormatErr =
      IOUExceptionFactory.FormatErr;
  private static final String InterpretErr =
      IOUExceptionFactory.InterpretErr;
  private static final String NullFilename =
      IOUExceptionFactory.NullFilename;
  private static final String FileNotFoundErr =
      IOUExceptionFactory.FileNotFoundErr;
  private static final String StdioErr =
    IOUExceptionFactory.StdioErr;

  /**
   * constructor which reads its values from the system configuration.
   * If parameters are not found then the defaults are used.
   * @throws IOUException thrown if an error occurs during configuration
   * @throws ConfigException thrown if there is an error reading the
   * configuration file
   */
  public InputDataFile() throws IOUException, ConfigException {
    configure(new InputDataCfg());
  }

  /**
   * constructor which allows overridding default values with a configuration
   * object. If parameters are not found then the defaults are used. The
   * default delimiters are such that each line is treated as a separate
   * record. The default buffer size is 512000. And the default character set
   * is ISO-8859-1
   * @param config the configuration class
   * @throws IOUException thrown if an error occurs during configuration
   * @throws ConfigException thrown if there is an error reading the
   * configuration file
   */
  public InputDataFile(InputDataCfg config) throws IOUException, ConfigException {
    configure(config);
  }

  /**
   * constructor which allows specifying the filename at runtime and
   * would override any specified filename value from the configuration.
   * @param filename the name of the file to process
   * @throws IOUException thown if file cannot be configured and opened
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   */
  public InputDataFile(String filename)
  throws IOUException, ConfigException {
    if (filename == null)
      throw (IOUException)exceptionFactory.getException(NullFilename);
    this.filename = filename;
    configure(new InputDataCfg());
  }

  /**
   * override the configured value for the buffer size
   * @assumes nothing
   * @effects will set the internal buffer size to the given value
   * @param bufferSize the buffer size
   */
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  /**
   * override the configured value for the end delimiter
   * @assumes nothing
   * @effects the record end delimiter will be set
   * @param endDelimiter the record end delimiter
   */
  public void setEndDelimiter(String endDelimiter) {
    this.endDelimiter = readMetaChars(endDelimiter);
  }

  /**
   * override the configured value for the end delimiter
   * @assumes nothing
   * @effects the record end delimiter will be set
   * @param beginDelimiter the record begin delimiter
   */
  public void setBeginDelimiter(String beginDelimiter) {
    this.beginDelimiter = readMetaChars(beginDelimiter);
  }


  /**
   * set whether or not to use regular expressions when performing
   * delimiter matching
   * @assumes nothing
   * @effects the regular expression indicator will be set
   * @param bool true to use regular expressions, false otherwise
   */
  public void setOkToUseRegex(boolean bool) {
    this.useRegex = bool;
  }

  public String getFilename()
  {
      return this.filename;
  }

  /**
   * return the end delimiter
   * @return the end delimiter
   */
  public String getEndDelimiter() {
    return this.endDelimiter;
  }

  /**
   * return the begin delimiter
   * @return the begin delimiter
   */
  public String getBeginDelimiter() {
    return this.beginDelimiter;
  }

  /**
   * return the buffer size
   * @return the buffer size
   */
  public int getBufferSize() {
    return this.bufferSize;
  }


  /**
   * Obtain an iterator for iterating through records from the file
   * @return the iterator
   * @throws IOUException thrown if an error occures opening the file
   */
  public RecordDataIterator getIterator() throws IOUException {
    RecordDataIterator iterator = null;
    if (this.filename.toUpperCase().equals("STDIN")) {
      try {
        iterator =
          new InputSourceIterator(Channels.newChannel(System.in));
      }
      catch (IOUException e) {
         IOUException e2 =
          (IOUException) exceptionFactory.getException(StdioErr, e);
         throw e2;
      }
    }
    else { // input type must be file
      try {
        iterator =
          new InputSourceIterator(new FileInputStream(filename).getChannel());
      }
      catch (FileNotFoundException e)
      {
        IOUException e2 =
            (IOUException) exceptionFactory.getException(FileNotFoundErr, e);
        e2.bind(filename);
        throw e2;
      }
    }
    return iterator;
  }

  /**
   * Obtain an iterator for iterating through records from the file
   * @param interpreter the setInterpreter method will be called on the
   * iterator with this class passed in as the parameter.
   * @return the iterator
   * @throws IOUException thrown if an error occures opening the file
   */
  public RecordDataIterator getIterator(RecordDataInterpreter interpreter)
  throws IOUException {
    RecordDataIterator iterator = getIterator();
    if (interpreter != null)
      iterator.setInterpreter(interpreter);
    return iterator;
  }

  /**
   * configure the instance variables
   * @param pConfig the configuration object from which to configure
   * this object
   * @throws IOUException
   * @throws ConfigException
   */
  private void configure(InputDataCfg pConfig)
      throws IOUException, ConfigException {
    this.endDelimiter = readMetaChars(pConfig.getEndDelimiter());
    this.beginDelimiter = readMetaChars(pConfig.getBeginDelimiter());
    this.useRegex = pConfig.getOkToUseRegex().booleanValue();
    if (this.beginDelimiter == null && this.endDelimiter == null)
    {
        // apply defaults so that each line is treated as a record
        this.endDelimiter = "\n";
        this.useRegex = false;
    }
    this.bufferSize = pConfig.getBufferSize().intValue();
    this.charset = pConfig.getCharset();
    LogCfg cfg = new LogCfg();
    LoggerFactory factory = cfg.getLoggerFactory();
    this.logger = factory.getLogger();
    if (this.filename == null)
        // filename may not have been defined through constructor
        // get from configuration
        this.filename = pConfig.getInputFileName();
    // if no filename was defined through constructor or configuration
    if (this.filename == null)
      throw (IOUException)exceptionFactory.getException(NullFilename);

  }

  /**
   * parse the string and return a new string with metacharacters properly
   * read.
   * @assumes nothing
   * @effects nothing
   * @param s the given string
   * @return the string with metacharacters correctly interpreted
   */
  private String readMetaChars(String s)
  {
    if (s != null) {
      s = s.replaceAll("\\\\n", "\n");
      s = s.replaceAll("\\\\t", "\t");
      s = s.replaceAll("\\\\r", "\r");
      //s = s.replaceAll("\\\\", "\\");
    }
    return s;
  }


  /**
   *  An object that iterates through records in a file
   * @has A reference to a RecordDataReader for reading records and a
   * RecordDataInterpreter for creating java objects from the records
   * @does Iterates through the records in a file and returns a java data
   * object for each record. If no RecordDataInterpreter has been defined then
   * the record is unparsed and returned as a String object.
   * @company Jackson Lab
   * @author MWalker
   */

  protected class InputSourceIterator implements RecordDataIterator {

    // the input channel to read from
    private ReadableByteChannel inputChannel = null;
    // the object used to create a java object from the current record.
    // it can remain null and the record is returned as a String
    private RecordDataInterpreter interpreter = null;
    // the object used to read records from a file
    private RecordDataReader recordDataReader= null;
    // the cache is used to store the current record
    private String cache = null;
    // cachedException is used to cache an Exception
    private IOUException cachedException = null;
    // indicator that exception has been cached
    private boolean exceptionState = false;
    // indicator the next record has been cached
    private boolean cachedState = false;



    /**
     * default constructor
     * @param channel the io channel
     * @throws IOUException thrown if file is not found or there is some
     * IO exception
     */
    protected InputSourceIterator(ReadableByteChannel channel)
    throws IOUException {
      this.inputChannel = channel;
      setupReader();
    }

    /**
     * sets the value of the RecordDataInterpreter
     * @param in the given RecordDataInterpreter
     */

    public void setInterpreter(RecordDataInterpreter in) {
      interpreter = in;
    }

    /**
     * returns a boolean value to designate whether there are more records left
     * to read in the current iteration
     * @return true if rows remain, otherwise return false.
     */

    // the hasNext() method calls the isValid() method of the
    // RecordDataInterpreter to see if the next record is valid and should
    // be returned. It will continue iterating through the file until
    // either a record is found which is valid or the end of file is
    // reached. If a record is found valid, then it is cached and the
    // method returns true. The record is returned to the caller when
    // the call to next() is made. If an exception is encountered while
    // reading from the file, then this exception is cached and thrown on the
    // call to next().

    public boolean hasNext() {
      // if the next record is cached then just return true.
      // the only way to get out of this cached state is to call next()
      if (cachedState)
        return true;
      // if no Interpreter is defined and no begin delimiter is being used,
      // then all records are considered valid
      if (interpreter == null && beginDelimiter == null)
        return recordDataReader.hasNext();

      // if an Interpreter is defined then find the next valid record
      boolean foundValid = false;
      String record = null;
      while (recordDataReader.hasNext()) {
        try {
          record = recordDataReader.next();
        }
        catch (IOException e) {
          // the interface does not define execptions thrown here.
          // this exception should be cached and thrown on the call to next()
          cachedException =
              (IOUException)exceptionFactory.getException(DataReadEOF, e);
          foundValid = true; // cannot throw exception here so return true
          exceptionState = true;
          break;
        }
        if (record == null)
            return false;
        if (interpreter == null || interpreter.isValid(record)) {
          // cache the record and return true
          cache = record;
          cachedState = true;
          foundValid = true;
          break;
        }
      }

      return foundValid;
    }

    /**
     * Creates a java data object based on the data obtained by
     * parsing the next record from the input. If no
     * RecordDataInterpreter has been defined, it returns a
     * String to caller to represent the record unparsed.
     * @return the object created based on the next record from the input
     * file.
     * @throws IOUException thrown if there is an IO exception or this
     * method is called after the end of the file has been reached.
     * @throws RecordFormatException thrown if there is an error parsing the
     * input record
     */

    public java.lang.Object next() throws IOUException, RecordFormatException {
      // If an exception was generated on the last call to
      // hasNext() then throw it now
      if (exceptionState) {
        // reset the state and throw the Exception
        exceptionState = false;
        throw cachedException;
      }
      String nextItem = null;
      // first check if the next item has been cached by a call to hasNext().
      // if so then return that item
      if (cachedState) {
        cachedState = false;
        if (interpreter == null)
          return cache;
        else {
          try {
            return interpreter.interpret(cache);
          }
          catch (MGIException e) {
            IOUException e2 =
                (IOUException) exceptionFactory.getException(InterpretErr, e);
            throw e2;
          }
        }
      }
      // find the next valid item or throw exception if end of file
      // is reached
      while (recordDataReader.hasNext()) {
        try {
          nextItem = recordDataReader.next();
        }
        catch (IOException e) {
          IOUException ke = (IOUException)
              exceptionFactory.getException(DataReadEOF, e);
          throw ke;
        }
        // if no interpreter has been defined then just return the item
        if (interpreter == null)
          return nextItem;
        // check if the record is valid through the interpreter and return
        // the interpreted item
        if (interpreter.isValid(nextItem)) {
          try {
            return interpreter.interpret(nextItem);
          }
          catch (MGIException e) {
            RecordFormatException e2 = new RecordFormatException();
            e2.bindRecord(nextItem);
            throw e2;
          }
        }
      }
      // never found a valid record, throw exception
      IOUException e = (IOUException)
          exceptionFactory.getException(NoRecordFound);
      throw e;

    } // ends inner class

    /**
     * close the input file
     * @throws IOUException
     */
    public void close() throws IOUException {
      try {
        recordDataReader.closeResources();
      }
      catch (IOException e) {
        IOUException ke = (IOUException)
            exceptionFactory.getException(CloseErr, e);
        throw ke;
      }
    }


    /**
     * creates an instance of a RecordDataReader
     * @assumes nothing
     * @effects a new RecordDataReader will be instantiated
     * @throws IOUException thrown if there
     */
    private void setupReader()
        throws IOUException
    {
        try
        {
            if (useRegex)
            {
                recordDataReader =
                    new RecordDataReader(inputChannel, beginDelimiter,
                                         endDelimiter, charset,
                                         bufferSize, logger);
            }
            else // delimiters are byte arrays
            {
                byte[] begin = null;
                byte[] end = null;
                if (beginDelimiter != null)
                    begin = beginDelimiter.getBytes();
                if (endDelimiter != null)
                    end = endDelimiter.getBytes();
                recordDataReader =
                    new RecordDataReader(inputChannel, begin, end,
                                         charset, bufferSize, logger);
            }
        }
        catch (IOException e)
        {
            IOUException ke = (IOUException)
                exceptionFactory.getException(DataReadEOF, e);
            throw ke;
        }
    }
  }

}
