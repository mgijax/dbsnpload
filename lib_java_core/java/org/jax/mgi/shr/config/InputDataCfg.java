package org.jax.mgi.shr.config;


/**
 * A class for configuring an InputDataFile object.
 * @has a set of InputDataFile configuration parameters and a
 * reference to a ConfigurationManager singleton object.
 * @does provides methods for getting configuration paramaters
 * for an InputDataFile object. It also provides default values for parameters
 * that were not configured. A prefix string can be provided to the
 * constructor which would cause a lookup of configuration parameter names
 * within the configuration files or system properties that are prefixed by
 * the given string.
 * @company Jackson Laboratory
 * @author M. Walker
 */

public class InputDataCfg extends Configurator {

  //default bufferSize
  private int DEFAULT_BUFFER_SIZE = 512000;
  //default charset
  private String DEFAULT_CHARSET = "ISO-8859-1";

  /**
   * default constructor
   * @throws ConfigException thrown if there is a configuration error
   */
  public InputDataCfg() throws ConfigException {
    super();
  }
  /**
   * constructor which accepts a prefix string that will be prepended to
   * all configuration parameter names on lookup. For example, if the prefix
   * value is the string 'SECONDARY', then a call to getInputFileName()
   * will lookup the parameter SECONDARY_INFILE_NAME instead of INFILE_NAME.
   * @param pParameterPrefix the given prefix string
   * @throws ConfigException throws if there is a configuration error
   */
  public InputDataCfg(String pParameterPrefix) throws ConfigException {
    super();
    super.parameterPrefix = pParameterPrefix;
  }


  /**
   * get the name of the input file. The parameter name read from the
   * configuration file or system properties is INFILE_NAME. If the
   * parameter is not found then null is returned.
   * @return the name of the input file
   */
  public String getInputFileName() {
    return getConfigStringNull("INFILE_NAME");
  }

  /**
   * get the begin delimeter for the input file. The delimiter is interpreted
   * as a java based regular expression or a sequence of bytes, depending on
   * the value of the INFILE_USE_REGEX parameter. The parameter name read from
   * the configuration file or system properties is INFILE_BEGIN_DELIMITER.
   * @return the value used as record delimiter
   */
  public String getBeginDelimiter() {
    return getConfigStringNull("INFILE_BEGIN_DELIMITER");
  }



  /**
   * get the end delimeter for the input file. The delimiter is interprete
   * as a java based regular expression or a sequence of bytes, depending on
   * the value of the INFILE_USE_REGEX parameter. The parameter name read from
   * the configuration file or system properties is INFILE_END_DELIMITER.
   * @return the value used as record delimiter
   */
  public String getEndDelimiter() {
    return getConfigStringNull("INFILE_END_DELIMITER");
  }

  /**
   * get the xml tag to iterate over. The parameter name read from
   * the configuration file or system properties is INFILE_XML_ITERATOR_TAG.
   * @return the value used as record delimiter
   * @throws an exception if the parameter is not set in the configuration
   */
  public String getXMLIteratorTag()
 throws ConfigException {
    return getConfigString("INFILE_XML_ITERATOR_TAG");
  }


  /**
   * get the size of the internal memory buffer. The parameter name read
   * from the configuration file or system properties is INFILE_BUFFERSIZE.
   * The default value = 512000.
   * @return the configured buffer size
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   */
  public Integer getBufferSize() throws ConfigException {
    return getConfigInteger("INFILE_BUFFERSIZE",
                            new Integer(this.DEFAULT_BUFFER_SIZE));
  }

  /**
   * get the value of the option which designates whether to use
   * regular expressions when doing delimiter matching.  The parameter
   * name read from the configuration file for setting this option is
   * INFILE_USE_REGEX and its default is true. This value provides the
   * initial parameter setting for an instance of the InputDataFile class
   * and can be overriden by setter methods within that class
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getOkToUseRegex() throws ConfigException {
    return getConfigBoolean("INFILE_USE_REGEX", new Boolean(false));
  }

  /**
   * get the charset to use when decoding bytes. The parameter name read
   * from the configuration file or system properties is INFILE_CHARSET.
   * The default value = "ISO-8859-1".
   * @return the charset to use
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   */
  public String getCharset() throws ConfigException {
    return getConfigString("INFILE_CHARSET", this.DEFAULT_CHARSET);
  }





}
