// $Header:
// $Name:

package org.jax.mgi.shr.config;

import java.util.Vector;
import java.util.StringTokenizer;

/**
 * A class for configuring a BCPWriter.
 * @has a set of BCPWriter configuration parameters and a reference to
 * a ConfigurationManager
 * @does provides methods for getting configuration paramaters
 * for a BCPManager. It also provides default values for parameters that were
 * not configured.
 * @company Jackson Laboratory
 * @author M. Walker
 */
public class BCPWriterCfg
    extends Configurator {
    /**
     * default value. This value can be changed by the protected
     * method setDefaultOkToRecordStamp()
     */
    private boolean DEFAULT_OK_TO_RECORD_STAMP = false;
    /**
     * default value. This value can be changed by the protected
     * method setDefaultOkToTruncateTable()
     */
    private boolean DEFAULT_OK_TO_TRUNCATE_TABLE = false;
    /**
     * default value. This value can be changed by the protected
     * method setDefaultOkToDropIndexes()
     */
    private boolean DEFAULT_OK_TO_DROP_INDEXES = false;
    /**
     * default value. This value can be changed by the protected
     * method setDefaultOkToDropTriggers()
     */
    private boolean DEFAULT_OK_TO_DROP_TRIGGERS = false;
    /**
     * default value. This value can be changed by the protected
     * method setDefaultOkToAutoFlush()
     */
    private boolean DEFAULT_OK_TO_AUTO_FLUSH = false;
    /**
     * default constructor which obtains a reference to the
     * ConfigurationManager singleton class.
     * @throws ConfigException throws if there is a configuration error
     */
    public BCPWriterCfg() throws ConfigException {
        super();
    }

    /**
     * constructor which accepts a prefix string that will be prepended to
     * all lookup parameter names
     * @param pParameterPrefix the given prefix string
     * @throws ConfigException throws if there is a configuration error
     */
    public BCPWriterCfg(String pParameterPrefix) throws ConfigException {
        super();
        super.parameterPrefix = pParameterPrefix;
    }

    /**
     * get the value of the option which designates whether to automatically
     * drop indexes before running bcp. If this is set to true, then the
     * configuration variable DBSCHEMADIR should be set in order to
     * locate the directory where the schema product for this database is
     * located. The parameter name read from the configuration file for setting
     * this option is BCP_DROP_INDEXES and its default is false. This value
     * provides the initial parameter setting for a BCPWriter and can be
     * changed for any instance of a BCPWriter
     * @return true or false
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToDropIndexes() throws ConfigException {
        return getConfigBoolean("BCP_DROP_INDEXES",
                                new Boolean(DEFAULT_OK_TO_DROP_INDEXES));
    }

    /**
     * get the value of the option which designates whether to automatically
     * drop triggers before running bcp. If this is set to true, then the
     * configuration variable DBSCHEMADIR should be set in order to
     * locate the directory where the schema product for this database is
     * located. The parameter name read from the configuration file for setting
     * this option is BCP_DROP_TRIGGERS and its default is false. This value
     * provides the initial parameter setting for a BCPWriter and can be
     * changed for any instance of a BCPWriter
     * @return true or false
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToDropTriggers() throws ConfigException {
        return getConfigBoolean("BCP_DROP_TRIGGERS",
                                new Boolean(DEFAULT_OK_TO_DROP_TRIGGERS));
    }


    /**
     * get the value of the option which designates whether to automatically
     * truncate the table before doing a bcp. The parameter name read from the
     * configuration file for setting this option is BCP_TRUNCATE_TABLE and its
     * default is false. This value provides the initial parameter setting for
     * a BCPWriter and can be changed for any instance of a BCPWriter.
     * @return true or false
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToTruncateTable() throws ConfigException {
        return getConfigBoolean("BCP_TRUNCATE_TABLE",
                                new Boolean(DEFAULT_OK_TO_TRUNCATE_TABLE));
    }

    /**
     * get the string of sql statements to be executed prior to executing bcp.
     * This string consists of a series of sql statements seperated by a pipe
     * symbol. The parameter name read from the configuration file is
     * BCP_PRESQL and has no default vaule. This value provides the initial
     * parameter setting for a BCPWriter and can be changed for any instance
     * of a BCPWriter.
     * @return Vector of sql statements or empty if none were found
     */
    public Vector getPreSQL() {
        return buildSQLVector(getConfigStringNull("BCP_PRESQL"));
    }

    /**
     * get the string of sql statements to be executed after executing bcp.
     * This string consists of a series of sql statements seperated by a pipe
     * symbol. The parameter name read from the configuration file is
     * BCP_POSTSQL and has no default vaule. This value provides the initial
     * parameter setting for a BCPWriter and can be changed for any instance
     * of a BCPWriter.
     * @return Vector of sql statements or empty if none were found
     */
    public Vector getPostSQL() {
        return buildSQLVector(getConfigStringNull("BCP_POSTSQL"));
    }

    /**
     * get the value of the option which designates whether to automatically
     * add the record stamping fields when doing a bcp write. The
     * default value is taken from the corresponding setting in the parent
     * BCPManager object.
     * @return true if record stamping should be performed automatically
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToRecordStamp() throws ConfigException {
        return getConfigBoolean("BCP_RECORD_STAMPING",
                                new Boolean(DEFAULT_OK_TO_RECORD_STAMP));
    }

    /**
     * get the value of the option which designates whether to automatically
     * replace newline characters in text fields with spaces. The parameter
     * name read from the configuration file for setting
     * this option is BCP_OK_TO_REMOVE_NEWLINES and its default is false.
     * @return true if it is ok to remove newlines, false otherwise
     * @throws ConfigException thrown if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToRemoveNewlines() throws ConfigException {
        return getConfigBoolean("BCP_OK_TO_REMOVE_NEWLINES",
                                new Boolean(false));
    }

    /**
     * get the value of the option which designates whether to automatically
     * flush a buffer after each bcp write. The
     * default value is taken from the corresponding setting in the parent
     * BCPManager object.
     * @return true if buffer flushing should be performed automatically
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToAutoFlush() throws ConfigException {
        return getConfigBoolean("BCP_AUTO_FLUSH",
                                new Boolean(DEFAULT_OK_TO_AUTO_FLUSH));
    }

    /**
     * set the default value for the option which designates whether to
     * perform automatic record stamping. This method is primarily used by
     * the BCPManager for setting the default values of a new
     * BCPWriter based on the current BCPManager settings.
     * @param bool true if the default is to always apply record stamping,
     * false otherwise
     */
    public void setDefaultOkToRecordStamp(boolean bool) {
        DEFAULT_OK_TO_RECORD_STAMP = bool;
    }

    /**
     * set the default value for the option which designates whether to
     * automatically flush the buffer after each bcp write. This method is
     * primarily used by the BCPManager for setting the BCPWriter default
     * values based on the current BCPManager settings when creating a new
     * BCPWriter.
     * @param bool true if the default is to always flush the buffer,
     * false otherwise
     */
    public void setDefaultOkToAutoFlush(boolean bool) {
        DEFAULT_OK_TO_AUTO_FLUSH = bool;
    }

    /**
     * set the default value for the option which designates whether to
     * automatically truncate the table before executing bcp. This method is
     * primarily used by the BCPManager for setting the default values of a new
     * BCPWriter based on the current BCPManager settings.
     * @param bool true if the default is to always truncate the table,
     * false otherwise
     */
    public void setDefaultOkToTruncateTable(boolean bool) {
        DEFAULT_OK_TO_TRUNCATE_TABLE = bool;
    }

    /**
     * set the default value for the option which designates whether to
     * automatically drop indexes before executing bcp. This method is
     * primarily used by the BCPManager for setting the default values of a new
     * BCPWriter based on the current BCPManager settings.
     * @param bool true if the default is to always drop indexes,
     * false otherwise
     */
    public void setDefaultOkToDropIndexes(boolean bool) {
        DEFAULT_OK_TO_DROP_INDEXES = bool;
    }


    /**
     * set the default value for the option which designates whether to
     * automatically drop triggers before executing bcp. This method is
     * primarily used by the BCPManager for setting the default values of a new
     * BCPWriter based on the current BCPManager settings.
     * @param bool true if the default is to always drop triggers,
     * false otherwise
     */
    public void setDefaultOkToDropTriggers(boolean bool) {
        DEFAULT_OK_TO_DROP_TRIGGERS = bool;
    }


    /**
     * Build a vector of SQL statements from a "|" separated list.
     * @param sqlCmds A string of "|" separated SQL statements.
     * @return A vector of SQL statements or an empty vector if none
     * are found.
     */
    private Vector buildSQLVector(String sqlCmds) {
        Vector v = new Vector();
        if (sqlCmds != null && sqlCmds.length() > 0) {
            StringTokenizer tokens = new StringTokenizer(sqlCmds, "|");
            while (tokens.hasMoreTokens()) {
                v.add(tokens.nextToken());
            }
        }
        return v;
    }
}
// $Log:
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
 */
