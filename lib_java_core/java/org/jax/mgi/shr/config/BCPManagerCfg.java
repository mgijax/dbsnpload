// $Header$
// $Name$

package org.jax.mgi.shr.config;


/**
 * A class for configuring a BCPManager.
 * @has a set of BCPManager configuration parameters and a reference to
 * a ConfigurationManager
 * @does provides methods for getting configuration paramaters
 * for a BCPManager. It also provides default values for parameters that were
 * not configured.
 * @company Jackson Laboratory
 * @author M. Walker
 */

public class BCPManagerCfg extends Configurator {

  private String DEFAULT_PATH = ".";
  private String DEFAULT_DELIMITER = "\t";

  private String DEFAULT_CONNECTION_MANAGER =
      "org.jax.mgi.shr.dbutils.MGIDriverManager";


  /**
   * default constructor which obtains a reference to the
   * ConfigurationManager singleton class.
   * @throws ConfigException throws if there is a configuration error
   */
  public BCPManagerCfg() throws ConfigException {
    super();
  }

  /**
   * constructor which accepts a prefix string that will be prepended to
   * all lookup parameter names
   * @param pParameterPrefix the given prefix string
   * @throws ConfigException throws if there is a configuration error
   */
  public BCPManagerCfg(String pParameterPrefix) throws ConfigException {
    super();
    super.parameterPrefix = pParameterPrefix;
  }

  /**
   * get the path name where bcp files are created. The parameter name read
   * from the configuration file or system properties is BCP_PATH. The
   * default value is the current directory.
   * @return path name.
   */
  public String getPathname() {
    return getConfigString("BCP_PATH", DEFAULT_PATH);
  }

  /**
   * get the field delimiter for the BCP file. The parameter name read from
   * the configuration file or system properties is BCP_DELIMITER. The
   * default value is tab.
   * @return the field delimiter.
   */
  public String getDelimiter() {
    String delimiter =
        getConfigString("BCP_DELIMITER", DEFAULT_DELIMITER);
    return delimiter;
  }

  /**
   * get the value of the option which designates whether to prevent
   * executing the bcp command when called. The parameter name read from the
   * configuration file or system properties is BCP_PREVENT_EXECUTE. The
   * value can be yes, no, true or false and the case of the letters are
   * ignored. The default value is false.
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getPreventExecute() throws ConfigException {
    return getConfigBoolean("BCP_PREVENT_EXECUTE", new Boolean(false));
  }

  /**
   * get the value of the option which designates whether it is ok to
   * overwrite an existing bcp file. The parameter name read from the
   * configuration file or system properties is BCP_OK_TO_OVERWRITE. The
   * value can be yes, no, true or false and the case of the letters are
   * ignored. The default value is false.
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getOkToOverwrite() throws ConfigException {
    return getConfigBoolean("BCP_OK_TO_OVERWRITE", new Boolean(false));
  }

  /**
   * get the value of the option which designates whether to use temporary
   * files when creating bcp files. The parameter name read from the
   * configuration file or system properties is BCP_USE_TEMPFILE. The value
   * can be yes, no, true or false and the case of the letters are ignored.
   * The default value is false.
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getUseTempFile() throws ConfigException {
    return getConfigBoolean("BCP_USE_TEMPFILE", new Boolean(false));
  }

  /**
   * get the value of the option which designates whether to remove the bcp
   * files after executing. The parameter name read from the configuration
   * file or system properties is BCP_REMOVE_AFTER_EXECUTE. The value can be
   * yes,  no, true or false and the case of the letters are ignored. The
   * default value is false.
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getRemoveAfterExecute() throws ConfigException {
    return getConfigBoolean("BCP_REMOVE_AFTER_EXECUTE", new Boolean(false));
  }

  /**
   * get the value of the option which designates whether to automatically
   * add record stamping when doing a bcp write.
   * The parameter name read from the configuration file
   * for setting this option is BCP_RECORD_STAMPING and its default is false.
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getOkToRecordStamp() throws ConfigException {
    return getConfigBoolean("BCP_RECORD_STAMPING", new Boolean(false));
  }

  /**
   * get the value of the option which designates whether to automatically
   * flush a buffer after each bcp write.
   * The parameter name read from the configuration file
   * for setting this option is BCP_AUTO_FLUSH and its default is false.
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getOkToAutoFlush() throws ConfigException {
    return getConfigBoolean("BCP_AUTO_FLUSH", new Boolean(false));
  }


  /**
   * get the value of the option which designates whether to automatically
   * truncate the log after running bcp. The parameter name read from the
   * configuration file for setting this option is BCP_TRUNCATE_LOG and its
   * default is false.
   * @return true or false
   * @throws ConfigException throws if configuration value does not represent
   * a boolean
   */
  public Boolean getOkToTruncateLog() throws ConfigException {
    return getConfigBoolean("BCP_TRUNCATE_LOG", new Boolean(false));
  }

	/**
	 * get the value of the option which designates whether to automatically
	 * drop indexes before running bcp. If this is set to true, then the
	 * configuration variable DBSCHEMA_INSTALLDIR should be set in order to
	 * locate the directory where the schema product for this database is
	 * located. The parameter name read from the configuration file for setting
	 * this option is BCP_DROP_INDEXES and its default is false. This value
	 * provides the initial parameter setting for a BCPWriter and can be
	 * overridden with a BCPWriterCfg object or within the BCPwriter instance.
	 * @return true or false
	 * @throws ConfigException throws if configuration value does not represent
	 * a boolean
	 */
	public Boolean getOkToDropIndexes() throws ConfigException {
		return getConfigBoolean("BCP_DROP_INDEXES", new Boolean(false));
	}

    /**
     * get the value of the option which designates whether to automatically
     * drop triggers before running bcp. If this is set to true, then the
     * configuration variable DBSCHEMA_INSTALLDIR should be set in order to
     * locate the directory where the schema product for this database is
     * located. The parameter name read from the configuration file for setting
     * this option is BCP_DROP_TRIGGERS and its default is false. This value
     * provides the initial parameter setting for a BCPWriter and can be
     * overridden with a BCPWriterCfg object or within the BCPwriter instance.
     * @return true or false
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToDropTriggers() throws ConfigException {
        return getConfigBoolean("BCP_DROP_TRIGGERS", new Boolean(false));
    }



	/**
	 * get the value of the option which designates whether to automatically
	 * truncate the table before doing a bcp. The parameter name read from the
	 * configuration file for setting this option is BCP_TRUNCATE_TABLE and its
	 * default is false. This value provides the initial parameter setting for
	 * a BCPWriter and can be overridden with a BCPWriterCfg object or within
	 * the BCPWriter instance.
	 * @return true or false
	 * @throws ConfigException throws if configuration value does not represent
	 * a boolean
	 */
	public Boolean getOkToTruncateTable() throws ConfigException {
		return getConfigBoolean("BCP_TRUNCATE_TABLE", new Boolean(false));
	}

    /**
     * get the name of the ConnectionManager class used for instantiating
     * a ConnectionManager instance which is used for obtaining a connection
     * to the database. The name of the configuration parameter is
     * DBCONNECTION_MANAGER. The default value if not set is
     * org.jax.mgi.shr.dbutils.MGIDriverManager
     * @return the name of the ConnectionManager class
     */
    public String getConnectionManagerClass() {
      return getConfigString("DBCONNECTION_MANAGER",
                             DEFAULT_CONNECTION_MANAGER);
    }


  /**
   * get the string which is used as the value of the created_by field
   * and modified_by field when writing records to the bcp file.
   * The parameter name read from the configuration file is JOBSTREAM.
   * The default value is the user.name java System property.
   * @return string to be used in the created_by database record field.
   */
  public String getJobStream()
  {
    setApplyPrefix(false); // do not apply any prefixes to this lookup
    String value =
        getConfigString("JOBSTREAM", System.getProperty("user.name"));
    setApplyPrefix(true);
    return value;
  }

  /**
   * get the job stream key.
   * The parameter name read from the configuration file is JOBKEY.
   * @return string to be used in the created_by database record field.
   * @throws ConfigException thrown if there is an error accessing the
   * configuration or if the JOBKEY parameter is not found
   */
  public String getJobKey() throws ConfigException
  {
    setApplyPrefix(false); // do not apply any prefixes to this lookup
    String value = getConfigString("JOBKEY");
    setApplyPrefix(true);
    return value;
  }

  /**
   * get the string which is used as the value of the modified_by field when
   * writing records to the bcp file. The parameter name read from the
   * configuration file is MODIFIED_BY. The default value is the user.name
   * java System property.
   * @return string to be used in the modified_by database record field.
   */
  public String getModifiedBy() {
    setApplyPrefix(false); // do not apply any prefixes to this lookup
    String value =
        getConfigString("MODIFIED_BY", System.getProperty("user.name"));
    setApplyPrefix(true);
    return value;
  }

  /**
   * get the string which is used as the value of the created_by field when
   * writing records to the bcp file. The parameter name read from the
   * configuration file is CREATED_BY. The default value is the user.name
   * java System property.
   * @return string to be used in the created_by database record field.
   */
  public String getCreatedBy() {
    setApplyPrefix(false); // do not apply any prefixes to this lookup
    String value =
        getConfigString("CREATED_BY", System.getProperty("user.name"));
    setApplyPrefix(true);
    return value;
  }


}


// $Log$
// Revision 1.3  2005/08/15 14:58:42  mbw
// javadocs only
//
// Revision 1.2  2004/07/21 18:26:32  mbw
// added a getOkToDropTriggers method and made some javadocs edits
//
// Revision 1.1  2003/12/30 16:50:02  mbw
// imported into this product
//
// Revision 1.4  2003/12/09 22:48:38  mbw
// merged jsam branch onto the trunk
//
// Revision 1.3.2.18  2003/10/23 13:39:27  mbw
// added new parameters to BCPManager and new ways to control defaults in the BCPWriter for parameters okToDropIndexes and okToTruncateTable
//
// Revision 1.3.2.17  2003/10/01 18:32:45  dbm
// Add auto flush capability the bcp writers
//
// Revision 1.3.2.16  2003/09/10 21:37:30  mbw
// added new methods getJobStream and getJobKey
//
// Revision 1.3.2.15  2003/06/04 14:51:42  mbw
// javadoc edits
//
// Revision 1.3.2.14  2003/05/22 15:52:01  mbw
// javadocs edits
//
// Revision 1.3.2.13  2003/05/08 19:40:05  mbw
// moved methods into new BCPWriterCfg class and did javadoc edits
//
// Revision 1.3.2.12  2003/04/29 21:21:49  mbw
// now return Booleans instead of booleans. Also now using the setApplyPrefix() method in base class.
//
// Revision 1.3.2.11  2003/04/25 17:17:43  mbw
// changed to reflect new design of exception handling
//
// Revision 1.3.2.10  2003/04/18 01:53:51  mbw
// changed classes to extend Configurator
//
// Revision 1.3.2.9  2003/04/16 12:15:35  dbm
// Remove prefix qualifying from "CREATED_BY" and "MODIFIED_BY"
//
// Revision 1.3.2.8  2003/04/15 12:00:21  mbw
// javadoc edits
//
// Revision 1.3.2.7  2003/04/10 00:05:20  mbw
// fixed bug in constructor
//
// Revision 1.3.2.6  2003/04/09 21:18:20  mbw
// removed setter methods; replaced code in getter methods to use ConfigLookup class; added parameter name prefixing feature
//
// Revision 1.3.2.5  2003/04/04 00:06:50  mbw
// added clone method
// revised the configuration variables to suit offloading parameters to BCPWriter instead of configuring here
//
// Revision 1.3.2.4  2003/04/03 18:58:25  mbw
// javadocs
//
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
