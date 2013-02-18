package org.jax.mgi.shr.ioutils;

import javax.xml.stream.*;

import org.jax.mgi.shr.config.InputDataCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.LogCfg;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.log.LoggerFactory;
import org.jax.mgi.shr.exception.MGIException;

/**
 * A class that represents an input file in xml format.
 * @has an xml file to parse and an optional InputDataCfg for configuring
 * an instance using configuration parameter prefixes
 * @does manages the xml file resource and provides an iterator for
 * iterating over the xml elements of the file
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public class InputXMLDataFile
{

  // the default filename from which to read records
  private String filename = null;

  // a Logger for logging debug messages
  private Logger logger = null;

  // Stax class for reading an xml file
  private XMLStreamReader streamReader = null;

  // the exception factory
  private IOUExceptionFactory exceptionFactory = new IOUExceptionFactory();

  // the following are the exceptions that are thrown
  private static final String NullFilename =
      IOUExceptionFactory.NullFilename;
  private static final String FileNotFoundErr =
      IOUExceptionFactory.FileNotFoundErr;
  private static final String XMLStreamOpenErr =
      IOUExceptionFactory.XMLStreamOpenErr;


  /**
   * default constructor
   * @throws IOUException thrown thown if file name is not configured or
   * it cannot be opened
   * @throws ConfigException thrown if there is an error reading the
   * configuration file
   */
  public InputXMLDataFile()
  throws ConfigException, IOUException
  {
    configure(new InputDataCfg());
  }

  /**
   * constructor which allows overridding default values with a configuration
   * object.
   * @param config the configuration object
   * @throws IOUException thrown thown if file name is not configured or
   * it cannot be opened
   * @throws ConfigException thrown if there is an error reading the
   * configuration file
   */
  public InputXMLDataFile(InputDataCfg config)
  throws ConfigException, IOUException
  {
    configure(config);
  }

  /**
   * constructor which uses default configuration values and allows specifying
   * the filename
   * @param filename the name of the file to process
   * @throws IOUException thown if file cannot be opened
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   */
  public InputXMLDataFile(String filename)
  throws IOUException, ConfigException
  {
    if (filename == null)
      throw (IOUException)exceptionFactory.getException(NullFilename);
    this.filename = filename;
    configure(new InputDataCfg());
  }

  /**
   * constructor which uses configuration values specified by the
   * given configuration object and allows specifying
   * the filename at runtime
   * @param filename the name of the file to process
   * @param cfg the configuration object for specifying a subset of
   * configuration values using parameter name prefixing
   * @throws IOUException thown if file cannot be opened
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   */
  public InputXMLDataFile(String filename, InputDataCfg cfg)
  throws IOUException, ConfigException
  {
      configure(cfg);
      this.filename = filename;
      if (filename == null)
          throw (IOUException) exceptionFactory.getException(
              NullFilename);
  }



  /**
   * Obtain an iterator for iterating through xml elements from the file
   * @param tagname the tag name to iterate over
   * @assumes nothing
   * @effects the returned iterator will return un parsed xml elements via
   * the XMLTagIterator class
   * @return the iterator
   */
  public XMLDataIterator getIterator(String tagname)
  {
      Iterator it = new Iterator(tagname);
      return it;
  }

  /**
   * Obtain an iterator for iterating through xml elements from the file
   * @param interpreter the given XMLDataInterpreter to be used for
   * creating Java objects based on the xml elements from the file
   * @param tagname the name of the tags to iterate over
   * @assumes nothing
   * @effects the returned iterator will return pre-parsed xml as
   * Java objects
   * @return the iterator
   * @throws IOUException thrown if an error occures opening the file
   */
  public XMLDataIterator getIterator(String tagname,
                                     XMLDataInterpreter interpreter)
  {
      Iterator it = new Iterator(tagname);
      it.setInterpreter(interpreter);
      return it;
  }

  /**
   * get the name of the xml file
   * @return the name of the xml file
   */
  public String getFilename()
  {
      return this.filename;
  }


  /**
   * configure the instance variables
   * @param inputcfg the configuration object from which to configure
   * this object
   * @throws IOUException
   * @throws ConfigException
   */
  private void configure(InputDataCfg inputcfg)
  throws IOUException, ConfigException
  {

      LogCfg logcfg = new LogCfg();
      LoggerFactory logFactory = logcfg.getLoggerFactory();
      this.logger = logFactory.getLogger();
      if (this.filename == null)

          // filename may not have been defined through constructor
          // get from configuration
          this.filename = inputcfg.getInputFileName();
      // if no filename was defined through constructor or configuration
      if (this.filename == null)
          throw (IOUException)exceptionFactory.getException(NullFilename);
      try
      {
          XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
          if (this.filename.toUpperCase().equals("STDIN"))
          {
              this.streamReader =
                  xmlFactory.createXMLStreamReader(System.in);
          }
          else
          {
              this.streamReader =
                  xmlFactory.createXMLStreamReader(
                  new java.io.FileInputStream(this.filename));
          }
      }
      catch (java.io.FileNotFoundException e)
      {
          IOUException e2 =
              (IOUException)exceptionFactory.getException(FileNotFoundErr);
          e2.bind(this.filename);
          throw e2;
      }
      catch (XMLStreamException e)
      {
          IOUException e2 =
              (IOUException)exceptionFactory.getException(XMLStreamOpenErr);
          e2.bind(this.filename);
          throw e2;
      }

  }

  private class Iterator implements XMLDataIterator
  {

      private XMLDataInterpreter interpreter = null;

      private Object readForwardCache = null;

      private MGIException cacheException = null;


      private XMLTagIterator xml = null;

      public Iterator(String iteratorTag)
      {
          this.xml = new XMLTagIterator(iteratorTag, streamReader);
      }


      /**
       * Set the reference of a XMLDataInterpreter which will then be used on
       * subsequent calls to the next() method to create java data objects
       * based on the contents of the record.
       * @param interpreter the XMLDataInterpreter object
       */

      public void setInterpreter(XMLDataInterpreter interpreter)
      {
          this.interpreter = interpreter;
      }

      /**
       * get the next object from the results
       * @return the next object from the query results
       * @throws MGIException thrown if there is an error accessing the source
       * data
       */
       public Object next()
       throws MGIException
       {
           if (this.cacheException != null)
               throw this.cacheException;
           if (this.readForwardCache != null)
           {
               Object o = this.readForwardCache;
               this.readForwardCache = null;
               return o;
           }
           try
           {
               xml.nextTag();
           }
           catch (Exception e)
           {
               throw new MGIException("Error parsing xml", e);
           }
           if (xml.getState() == xml.TAG_EOF)
               return null;
           else
           {
               if (this.interpreter != null)
               {
                   return interpreter.interpret(xml);
               }
               else
                   return xml;
           }
       }

       /**
        * close the results
        * @throws MGIException thrown if there is an error accessing the source
        * data
        */
       public void close()
       throws MGIException
       {
           try
           {
               xml.close();
           }
           catch (Exception e)
           {
               throw new MGIException("Error closing xml", e);
           }
       }

       /**
        * see if there is another object to be retrieved from the results
        * @return true if there is another object to be retrieved from the
        * results, false otherwise
        */
       public boolean hasNext()
       {
           if (this.readForwardCache != null)
               return true;
           // indicator of whether interpreter returned non-null object
           boolean foundValid = false;
           while (!foundValid)
           {
               try
               {
                   xml.nextTargetTag();
               }
               catch (Exception e)
               {
                   // cache the exception so that it is thrown on call to next()
                   this.cacheException =
                       new MGIException("Error reading from xml", e);
                   return true;
               }
               if (xml.getState() == xml.TAG_EOF)
                   return false;

               if (this.interpreter != null)
               {
                   Object o = null;
                   try
                   {
                       o = interpreter.interpret(xml);
                   }
                   catch (InterpretException e)
                   {
                       // cache the exception so that it is thrown on call
                       // to next()
                       this.cacheException = e;
                       return true;
                   }
                   if (o != null)
                   {
                       // cache the object so that it can be returned on call
                       // to next()
                       this.readForwardCache = o;
                       foundValid = true;
                   }
               }
               else
                   foundValid = true;
           }
           return true;

       }
  }

}
