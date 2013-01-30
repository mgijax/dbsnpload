package org.jax.mgi.shr.config;

/**
 * A class for configuring a ScriptWriter object
 * @has accessor methods for accessing configuration parameters
 * @does lookups configuration parameters from the ConfigurationManagement
 * object
 * @company The Jackson Laboratory
 * @author M Walker
 */
public class ScriptWriterCfg
    extends Configurator
{
    /**
     * default pathname for storing script files
     */
    private String DEFAULT_PATH = ".";

    /**
     * default script filename excluding the suffix
     */
    private String DEFAULT_FILENAME = "script";

    /**
     * default script suffix
     */
    private String DEFAULT_SUFFIX = "sql";

    /**
     * default output filename excluding the suffix
     */
    private String DEFAULT_OUTFILENAME = "script";

    /**
     * default output filename suffix
     */
    private String DEFAULT_OUTSUFFIX = "out";

    /**
     * default constructor which will use unprefixed parameters from the
     * configuration file for configuring. Use the alternative constructor
     * if you want to refer to parameters with a prefix.
     * @throws ConfigException thrown if the there is an error accessing the
     * configuration file
     */
    public ScriptWriterCfg()
        throws ConfigException
    {
        super();
    }

    /**
     * constructor which accepts a prefix string that will be prepended to
     * all configuration parameter on lookup
     * @param pParameterPrefix the given prefix string
     * @throws ConfigException throws if there is a configuration error
     */
    public ScriptWriterCfg(String pParameterPrefix)
        throws ConfigException
    {
        super();
        super.parameterPrefix = pParameterPrefix;
    }

    /**
     * get the path name where script file to created. The parameter name read
     * from the configuration file or system properties is SCP_PATH. The
     * default value is the current directory.
     * @return path name.
     */
    public String getPathname()
    {
        return getConfigString("SCP_PATH", DEFAULT_PATH);
    }

    /**
     * get the file name of the script to create. The parameter name read
     * from the configuration file or system properties is SCP_FILENAME and
     * the default is 'script'. This value should not include suffix file
     * extensions like '.txt', which is appended automatically using '.sql' or
     * whatever the SCP_SUFFIX parameter is set to be.
     * @return filename name without any suffixed file extensions.
     */
    public String getFilename()
    {
        return getConfigString("SCP_FILENAME", DEFAULT_FILENAME);
    }

    /**
     * get the file name suffix of the script to create which is appended to
     * the SCP_FILENAME as a file extension suffix. The parameter name read
     * from the configuration file or system properties is SCP_SUFFIX. The
     * default is 'sql' so that a suffix of '.sql' will be appended
     * @return suffix name.
     */
    public String getSuffix()
    {
        return getConfigString("SCP_SUFFIX", DEFAULT_SUFFIX);
    }

    /**
     * get the file name of the script output. The parameter name read
     * from the configuration file or system properties is SCP_OUTFILENAME. The
     * default is 'script'. This value should not include suffix file
     * extensions like '.txt', which is appended automatically using '.out' or
     * whatever the SCP_OUTSUFFIX parameter is set to be.
     * @return suffix name without any suffixed file extensions.
     */
    public String getOutFilename()
    {
        return getConfigString("SCP_OUTFILENAME", DEFAULT_OUTFILENAME);
    }

    /**
     * get the output file name suffix of the script which is appended to
     * the SCP_OUTFILENAME as a file extension suffix. The parameter name read
     * from the configuration file or system properties is SCP_OUTSUFFIX. The
     * default is 'out' so that a suffix of '.out' will be appended.
     * @return suffix name.
     */
    public String getOutSuffix()
    {
        return getConfigString("SCP_OUTSUFFIX", DEFAULT_OUTSUFFIX);
    }

    /**
     * get the value of the option which designates whether to prevent
     * executing the script when called. The parameter name read from the
     * configuration file or system properties is SCP_PREVENT_EXECUTE. The
     * value can be yes, no, true or false and the case of the letters are
     * ignored. The default value is false.
     * @return true if the script should not be executed or false otherwise
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getPreventExecute()
        throws ConfigException
    {
        return getConfigBoolean("SCP_PREVENT_EXECUTE", new Boolean(false));
    }

    /**
     * get the value of the option which designates whether it is ok to
     * overwrite an existing script file. The parameter name read from the
     * configuration file or system properties is SCP_OK_TO_OVERWRITE. The
     * value can be yes, no, true or false and the case of the letters are
     * ignored. The default value is false.
     * @return true if it is ok to overwrite an existing file or false
     * otherwise
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToOverwrite()
        throws ConfigException
    {
        return getConfigBoolean("SCP_OK_TO_OVERWRITE", new Boolean(false));
    }

    /**
     * get the value of the option which designates whether to use temporary
     * files when creating script files. The parameter name read from the
     * configuration file or system properties is SCP_USE_TEMPFILE. The value
     * can be yes, no, true or false and the case of the letters are ignored.
     * The default value is false.
     * @return true if temp files should be used or false otherwise
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getUseTempFile()
        throws ConfigException
    {
        return getConfigBoolean("SCP_USE_TEMPFILE", new Boolean(false));
    }

    /**
     * get the value of the option which designates whether to remove the script
     * file after executing. The parameter name read from the configuration
     * file or system properties is SCP_REMOVE_AFTER_EXECUTE. The value can be
     * yes,  no, true or false and the case of the letters are ignored. The
     * default value is false.
     * @return true if it is ok to remove the script after executing it or
     * false otherwise
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getRemoveAfterExecute()
        throws ConfigException
    {
        return getConfigBoolean("SCP_REMOVE_AFTER_EXECUTE", new Boolean(false));
    }

    /**
     * get the value of the option which designates whether to remove the output
     * file after executing. The parameter name read from the configuration
     * file or system properties is SCP_REMOVE_OUTFILE_AFTER_EXECUTE. The value
     * can be yes,  no, true or false and the case of the letters are ignored.
     * The default value is false.
     * @return true if it is ok to remove the output file after executing the
     * script or false otherwise
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getRemoveOutputAfterExecute()
        throws ConfigException
    {
        return getConfigBoolean("SCP_REMOVE_OUTFILE_AFTER_EXECUTE",
                                new Boolean(false));
    }


    /**
     * get the value of the option which designates whether to flush
     * io buffer on each write to the script file. The parameter name read
     * from the configuration file or system properties is
     * SCP_OK_TO_AUTOFLUSH. The value can be yes, no, true or false and the
     * case of the letters are ignored. The default value is false.
     * @return true if it is ok to flush io buffer on each write or
     * false otherwise
     * @throws ConfigException throws if configuration value does not represent
     * a boolean
     */
    public Boolean getOkToAutoFlush()
        throws ConfigException
    {
        return getConfigBoolean("SCP_OK_TO_AUTOFLUSH", new Boolean(false));
    }

}