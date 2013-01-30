// $Header$
// $Name$

package org.jax.mgi.shr.config;

import java.sql.Timestamp;
import java.util.Vector;

import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.shr.types.TypesException;

/**
 * A base class which provides protected access to the
 * ConfigurationManager singleton instance. It is intended to be
 * extended by so-called configurator classes which provide public
 * access lookup methods for parameters pertaining to their area of
 * interest.
 * @has A reference to the ConfigurationManager singleton instance. Also
 * it may have a parameter prefix string. If the prefix is not null then all
 * lookup parameter names are prefixed with this String before searching.
 * @does looks up parameters from the ConfigurationManager instance
 * and returns results as either Strings, ints or booleans.
 * @company The Jackson Laboratory
 * @author dbm, mbw
 */

public class Configurator
{
   // singleton instance of ConfigurationManager
   protected ConfigurationManager cm = null;

   // a prefix by which to prepend all lookup names
   protected String parameterPrefix = null;

   // an exception factory for creating ConfigExceptions
   protected ConfigExceptionFactory eFactory = new ConfigExceptionFactory();

   // indicates whether to apply prefixes to the parameter during lookup
   // this can be set on and off with the applyPrefix() method
   protected boolean applyPrefix = true;

   private static final String parameterNotFound =
       ConfigExceptionFactory.ParameterNotFound;
   private static final String parameterTypeError =
       ConfigExceptionFactory.ParameterTypeError;
   private static final String newInstanceFailed =
       ConfigExceptionFactory.NewInstanceFailed;
   private static final String patternMismatch =
       ConfigExceptionFactory.PatternMismatch;

   /**
    * protected constructor for members of this package.
    * @assumes nothing
    * @effects the singleton instance of a ConfigurationManager will
    * be created if it doesnt already exist.
    * @throws ConfigException thrown if an exception occures reading the
    * configuration files
    *
    */
    protected Configurator() throws ConfigException {
      cm = ConfigurationManager.getInstance();
    }

    /**
     * get the prefix being used by this instance when looking up
     * configuration parameters
     * @assumes nothing
     * @effects nothing
     * @return the configuration prefix string
     */
    public String getConfigPrefix()
    {
        return this.parameterPrefix;
    }

    /**
     * get the names of the configuration parameters
     * @assumes nothing
     * @effects nothing
     * @return the names of the configuration parameters
     */
    public String[] getConfigurationParameters()
    {
        return this.cm.getKeys();
    }

    /**
     * reread the configuration file.
     * @assumes Nothing
     * @effects Nothing
     * @throws ConfigException thrown if there is an exception reading the
     * configuration file
     */
    protected void reinit() throws ConfigException {
      cm.reinit();
    }

    /**
     * Gets a configuration value for a given name and throws
     * an exception if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A string representing the configuration value.
     * @exception ConfigException Thrown if the given name is not found by the
     * configuration manager.
     */
    protected String getConfigString(String name)
        throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, throw an
        // exception.
        //
        if (str != null)
            return str;
        else
        {
            ConfigException e =
                (ConfigException)eFactory.getException(parameterNotFound);
            e.bind(name);
            throw e;
        }
    }

    /**
     * Gets a configuration value for a given name and returns
     * the given default value if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @param defaultValue The value to be returned if the configuration
     * parameter is not found.
     * @return A string representing the configuration value.
     */
    protected String getConfigString(String name, String defaultValue)
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, return the
        // default value.
        //
        if (str != null)
            return str;
        else
            return defaultValue;
    }

    /**
     * Gets a configuration value for a given name and returns a
     * NULL if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A string representing the configuration value or NULL if the
     * given name is not found.
     */
    protected String getConfigStringNull(String name)
    {
        // Return the value from the configuration manager.  It may be NULL.
        //
        return this.lookup(name);
    }

    /**
     * Gets a list of configuration values for a given name and returns a
     * NULL if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return An array of strings representing a set of configuration valuse
     * or NULL if the given name is not found.
     */
    protected String[] getConfigStringArrayNull(String name)
    {
        // Return the value from the configuration manager.  It may be NULL.
        //
        String stringList = this.lookup(name);
        if (stringList == null)
            return null;
        return stringList.split(",");
    }


    /**
     * Gets a configuration value for a given name and throws
     * an exception if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A string representing the configuration value.
     * @exception ConfigException Thrown if the given name is not found by the
     * configuration manager.
     */
    protected String getConfigRegex(String name, String regex)
        throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, throw an
        // exception.
        //
        if (str != null)
            return str;
        else
        {
            ConfigException e =
                (ConfigException)eFactory.getException(parameterNotFound);
            e.bind(name);
            throw e;
        }
    }

    /**
     * Gets a configuration value for a given name and returns
     * the given default value if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @param defaultValue The value to be returned if the configuration
     * parameter is not found.
     * @return A string representing the configuration value.
     */
    protected String getConfigRegex(String name,
                                     String defaultValue,
                                     String regex)
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, return the
        // default value.
        //
        if (str != null)
            return str;
        else
            return defaultValue;
    }

    /**
     * Gets a configuration value for a given name and returns a
     * NULL if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A string representing the configuration value or NULL if the
     * given name is not found.
     */
    protected String getConfigRegexNull(String name, String regex)
    {
        // Return the value from the configuration manager.  It may be NULL.
        //
        return this.lookup(name);
    }

    /**
     * Gets a configuration value for a given name and throws
     * an exception if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return An integer representing the configuration value.
     * @exception ConfigException Thrown if the given name is not found by the
     * configuration manager or a conversion error occurs.
     */
    protected Integer getConfigInteger(String name)
        throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, convert the value to an integer and return
        // it.  Otherwise, throw an exception.  Note that the conversion can
        // also throw an exception.
        //
        if (str != null) {
          try {
            return Converter.toInteger(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("int");
            e2.bind(str);
            throw e2;
          }
        }
        else
        {
            ConfigException e =
                (ConfigException)eFactory.getException(parameterNotFound);
            e.bind(name);
            throw e;
        }
    }

    /**
     * Gets a configuration value for a given name and returns
     * the given default value if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @param defaultValue The value to be returned if the configuration
     * parameter is not found.
     * @return An integer representing the configuration value.
     * @exception ConfigException Thrown if a conversion error occurs.
     */
    protected Integer getConfigInteger(String name, Integer defaultValue)
        throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, convert the value to an integer and return
        // it.  Otherwise, return the default value.  Note that the conversion
        // can throw an exception.
        //
        if (str != null) {
          try {
            return Converter.toInteger(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("Integer");
            e2.bind(str);
            throw e2;
          }
        }
        else
            return defaultValue;
    }

    /**
     * Gets a configuration value for a given name and returns
     * NULL if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return An integer representing the configuration value or null if
     * not found
     * @exception ConfigException thrown if the value cannot be converted to
     * an Integer
     */
    protected Integer getConfigIntegerNull(String name)
        throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, convert the value to an integer and return
        // it.  Otherwise, throw an exception.  Note that the conversion can
        // also throw an exception.
        //
        if (str != null) {
          try {
            return Converter.toInteger(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("int");
            e2.bind(str);
            throw e2;
          }
        }
        else
            return null;
    }


        /**
         * Gets a configuration value for a given name and throws
         * an exception if the value is not found.
         * @assumes Nothing.
         * @effects Nothing
         * @param name The name of the configuration parameter to search on.
         * @return A Double representing the configuration value.
         * @exception ConfigException Thrown if the given name is not found by the
         * configuration manager or a conversion error occurs.
         */
        protected Double getConfigDouble(String name)
            throws ConfigException
        {
            // Have the configuration manager find the value for the name.
            //
            String str = lookup(name);

            // If the name was found, convert the value to an integer and return
            // it.  Otherwise, throw an exception.  Note that the conversion can
            // also throw an exception.
            //
            if (str != null) {
              try {
                return Converter.toDouble(str);
              }
              catch (TypesException e) {
                ConfigException e2 = (ConfigException)
                    eFactory.getException(parameterTypeError, e);
                e2.bind(name);
                e2.bind("int");
                e2.bind(str);
                throw e2;
              }
            }
            else
            {
                ConfigException e =
                    (ConfigException)eFactory.getException(parameterNotFound);
                e.bind(name);
                throw e;
            }
        }

        /**
         * Gets a configuration value for a given name and returns
         * the given default value if the value is not found.
         * @assumes Nothing.
         * @effects Nothing
         * @param name The name of the configuration parameter to search on.
         * @param defaultValue The value to be returned if the configuration
         * parameter is not found.
         * @return A Double representing the configuration value.
         * @exception ConfigException Thrown if a conversion error occurs.
         */
        protected Double getConfigDouble(String name, Double defaultValue)
            throws ConfigException
        {
            // Have the configuration manager find the value for the name.
            //
            String str = lookup(name);

            // If the name was found, convert the value to an integer and return
            // it.  Otherwise, return the default value.  Note that the conversion
            // can throw an exception.
            //
            if (str != null) {
              try {
                return Converter.toDouble(str);
              }
              catch (TypesException e) {
                ConfigException e2 = (ConfigException)
                    eFactory.getException(parameterTypeError, e);
                e2.bind(name);
                e2.bind("Integer");
                e2.bind(str);
                throw e2;
              }
            }
            else
                return defaultValue;
        }

        /**
         * Gets a configuration value for a given name and returns
         * NULL if the value is not found.
         * @assumes Nothing.
         * @effects Nothing
         * @param name The name of the configuration parameter to search on.
         * @return A Double representing the configuration value or null if
         * not found
         * @exception ConfigException thrown if the value cannot be converted to
         * an Integer
         */
        protected Double getConfigDoubleNull(String name)
            throws ConfigException
        {
            // Have the configuration manager find the value for the name.
            //
            String str = lookup(name);

            // If the name was found, convert the value to an integer and return
            // it.  Otherwise, throw an exception.  Note that the conversion can
            // also throw an exception.
            //
            if (str != null) {
              try {
                return Converter.toDouble(str);
              }
              catch (TypesException e) {
                ConfigException e2 = (ConfigException)
                    eFactory.getException(parameterTypeError, e);
                e2.bind(name);
                e2.bind("int");
                e2.bind(str);
                throw e2;
              }
            }
            else
                return null;
        }



    /**
     * Gets a configuration value for a given name and throws
     * an exception if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A boolean representing the configuration value.
     * @exception ConfigException Thrown if the given name is not found by the
     * configuration manager or if value cannot be converted to a boolean.
     */
    protected Boolean getConfigBoolean(String name) throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, throw an
        // exception.
        //
        if (str != null)
        {
          try {
            return Converter.toBoolean(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("Boolean");
            e2.bind(str);
            throw e2;
          }
        }
        else
        {
            ConfigException e =
                (ConfigException)eFactory.getException(parameterNotFound);
            e.bind(name);
            throw e;
        }
    }

    /**
     * Gets a configuration value for a given name and returns
     * the given default value if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @param defaultValue The value to be returned if the configuration
     * parameter is not found.
     * @return A boolean representing the configuration value.
     * @exception ConfigException Thrown if cannot convert value to boolean
     */
    protected Boolean getConfigBoolean(String name, Boolean defaultValue)
    throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, return the
        // default value.
        //
        if (str != null) {
          try {
            return Converter.toBoolean(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("Boolean");
            e2.bind(str);
            throw e2;
          }
        }
        else
            return defaultValue;
    }

    /**
     * Gets a configuration value for a given name and returns
     * null if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A boolean representing the configuration value or null if
     * the parameter was not found.
     * @exception ConfigException if value cannot be converted to a boolean.
     */
    protected Boolean getConfigBooleanNull(String name)
    throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, return the
        // default value.
        //
        if (str != null) {
          try {
            return Converter.toBoolean(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("Boolean");
            e2.bind(str);
            throw e2;
          }
        }
        else
            return null;
    }

    /**
     * Gets a configuration value for a given name and throws
     * an exception if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A boolean representing the configuration value.
     * @exception ConfigException Thrown if the given name is not found by the
     * configuration manager or if value cannot be converted to a boolean.
     */
    protected Timestamp getConfigDate(String name) throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, throw an
        // exception.
        //
        if (str != null)
        {
          try {
            return Converter.toTimestamp(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("Timestamp");
            e2.bind(str);
            throw e2;
          }
        }
        else
        {
            ConfigException e =
                (ConfigException)eFactory.getException(parameterNotFound);
            e.bind(name);
            throw e;
        }
    }

    /**
     * Gets a configuration value for a given name and returns
     * the given default value if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @param defaultValue The value to be returned if the configuration
     * parameter is not found.
     * @return A boolean representing the configuration value.
     * @exception ConfigException Thrown if cannot convert value to boolean
     */
    protected Timestamp getConfigDate(String name, Timestamp defaultValue)
    throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, return the
        // default value.
        //
        if (str != null) {
          try {
            return Converter.toTimestamp(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("Timestamp");
            e2.bind(str);
            throw e2;
          }
        }
        else
            return defaultValue;
    }

    /**
     * Gets a configuration value for a given name and returns
     * null if the value is not found.
     * @assumes Nothing.
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return A boolean representing the configuration value or null if
     * the parameter was not found.
     * @exception ConfigException if value cannot be converted to a boolean.
     */
    protected Timestamp getConfigDateNull(String name)
    throws ConfigException
    {
        // Have the configuration manager find the value for the name.
        //
        String str = lookup(name);

        // If the name was found, return the value.  Otherwise, return the
        // default value.
        //
        if (str != null) {
          try {
            return Converter.toTimestamp(str);
          }
          catch (TypesException e) {
            ConfigException e2 = (ConfigException)
                eFactory.getException(parameterTypeError, e);
            e2.bind(name);
            e2.bind("Timestamp");
            e2.bind(str);
            throw e2;
          }
        }
        else
            return null;
    }


    /**
     * Gets a configuration value for a given name and throws
     * an exception if the value is not found.
     * @assumes Nothing.
     * @effects a new object will be created
     * @param name The name of the configuration parameter to search on.
     * @return A java object representing the configuration value.
     * @exception ConfigException Thrown if the given name is not found by the
     * configuration manager or if value cannot be converted to a boolean.
     */
    protected Object getConfigObject(String name) throws ConfigException
    {
        String str = lookup(name);

        if (str == null)
        {
            ConfigException e =
                (ConfigException)eFactory.getException(parameterNotFound);
            e.bind(name);
            throw e;
        }
        return createObjectFromName(str);
    }

    /**
     * Gets a configuration value for a given name and returns
     * the given default value if the value is not found.
     * @assumes Nothing.
     * @effects a new object may be created
     * @param name The name of the configuration parameter to search on.
     * @param defaultObj The name of the object to instantiate if the
     * configuration parameter is not found.
     * @return A java object representing the configuration value.
     * @exception ConfigException thrown if object cannot be instantiated.
     */
    protected Object getConfigObject(String name, String defaultObj)
        throws ConfigException
    {
        String str = lookup(name);

        if (str == null)
        {
            return createObjectFromName(defaultObj);
        }
        return createObjectFromName(str);
    }

    /**
     * Gets a configuration value for a given name and returns
     * null if the value is not found.
     * @assumes Nothing.
     * @effects a new object may be created
     * @param name The name of the configuration parameter to search on.
     * @return A java object representing the configuration value.
     * @exception ConfigException thrown if object cannot be instantiated.
     */
    protected Object getConfigObjectNull(String name)
        throws ConfigException
    {
        String str = lookup(name);

        if (str == null)
        {
            return null;
        }
        return createObjectFromName(str);
    }

    /**
     * Gets an array of objects listed in the given configuration parameter
     * or NULL if the value is not found. An object is instantiated for each
     * one listed in the configuration setting
     * @assumes an object can be instantiated with no arguments
     * @effects Nothing
     * @param name The name of the configuration parameter to search on.
     * @return An array of objects as listed in the configuration setting
     * or NULL if the given name is not found.
     */
    protected Object[] getConfigObjectArrayNull(String name)
    throws ConfigException
    {
        // Return the value from the configuration manager.  It may be NULL.
        //
        String[] objectNames = this.getConfigStringArrayNull(name);
        if (objectNames == null)
            return null;
        Vector objects = new Vector();
        for (int i = 0; i < objectNames.length; i++)
        {
            String objectName = objectNames[i];
            Object object = this.createObjectFromName(objectName);
            objects.add(object);
        }
        return objects.toArray();
    }




    /**
     * set whether to apply prefixes to parameter names before lookup
     * @param b true if prefix should be applied
     */
    protected void setApplyPrefix(boolean b) {
      applyPrefix = b;
    }

    /**
     * looks up a value within the ConfigurationManager while applying prefixing
     * rules.
     * @param name the name to lookup
     * @return the value found which can be null
     */
    private String lookup(String name)
    {
        String s = null;
        if (applyPrefix && parameterPrefix != null)
        {
            s = cm.get(parameterPrefix + "_" + name);
            if (s == null)
                s = cm.get(name);
        }
        else
            s = cm.get(name);
        if (s != null && s.equals(""))
            s = null;
        return s;
    }

        /**
         * does translation of allowable configuration values for a file
         * delimiter into a java string suitable for using as the file delimiter
         * @param s the string to convert
         * @return the converted value
         * @throws ConfigException thrown if the given string is not a valid
         * delimiter representation. Allowable values are space and tab
         * (without any regard for case) and the strings "' '" or "\t".
         */
        private String convertDelimiter(String s) throws ConfigException {
                if (s == null  || s.equals("")) {
                        ConfigExceptionFactory eFactory = new ConfigExceptionFactory();
                        ConfigException e = (ConfigException)
                                eFactory.getException(ConfigExceptionFactory.InvalidDelimiter);
                        e.bind(s);
                        throw e;
                }
                if (s.toUpperCase().equals("SPACE") || s.equals("' '"))
                        return new String(" ");
                else if (s.toUpperCase().equals("TAB") || s.equals("\t"))
                        return Character.toString('\t');
                else {
                        return s;
                }
        }

    /**
     * create a new object for the given class
     * @assumes nothing
     * @effects a new object will created
     * @param className the name of the class
     * @return a new instance of the class
     * @throws ConfigException thrown if there is an error accessing the
     * configuration or if there is an exception during object creation
     */
    private Object createObjectFromName(String className) throws ConfigException
    {
        Object o = null;
        try
        {
            Class cls = Class.forName(className);
            o = cls.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            ConfigException e2 =
                (ConfigException)eFactory.getException(newInstanceFailed, e);
            e2.bind(className);
            throw e2;
        }
        catch (IllegalAccessException e)
        {
            ConfigException e2 =
               (ConfigException)eFactory.getException(newInstanceFailed, e);
           e2.bind(className);
           throw e2;
        }
        catch (InstantiationException e)
        {
            ConfigException e2 =
               (ConfigException)eFactory.getException(newInstanceFailed, e);
           e2.bind(className);
           throw e2;
        }
        return o;

    }

    private void throwBadPatternMatch(String key, String value, String pattern)
    throws ConfigException
    {
        ConfigExceptionFactory factory = new ConfigExceptionFactory();
        ConfigException e =
            (ConfigException)factory.getException(this.patternMismatch);
        e.bind(value);
        e.bind(key);
        e.bind(pattern);
        throw e;
    }

}
// $Log$
// Revision 1.10  2006/12/20 16:59:45  lec
// fix bourne parser
//
// Revision 1.9  2005/08/05 16:28:00  mbw
// merged code from branch lib_java_core-tr6427-1
//
// Revision 1.8.2.2  2005/08/03 17:28:38  mbw
// javadocs only
//
// Revision 1.8.2.1  2005/08/01 18:35:10  mbw
// added getConfigObjectArrayNull method
//
// Revision 1.8  2004/12/16 21:18:51  mbw
// merged assembly branch onto the trunk
//
// Revision 1.7.2.1  2004/12/02 19:26:52  mbw
// changed use of floats to doubles
//
// Revision 1.7  2004/10/11 15:49:22  mbw
// added methods for obtainning floats from the configuration
//
// Revision 1.6  2004/09/30 15:36:54  mbw
// changed so that any empty string read in as a parameter value will be interpreted as a null.
//
// Revision 1.5  2004/08/04 14:18:08  mbw
// added new method for getting a string array from the config file
//
// Revision 1.4  2004/07/21 18:38:31  mbw
// implemented config prefixing in this class so that all base classes inherit this functionality
// added getConfigRegex family of methods
// made some javadocs edits
//
// Revision 1.3  2004/04/14 16:41:08  mbw
// bug fix: fixed parameter passing error in call to createNewObject() method
//
// Revision 1.2  2004/03/29 19:39:25  mbw
// added accessor methods for datatypes of data and object
//
// Revision 1.1  2003/12/30 16:50:06  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:47:24  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.4.5  2003/06/17 20:12:15  mbw
// now reads delimiter strings as another possible parameter from the config file and throws new exception if string cannot be converted to a valid delimiter
//
// Revision 1.1.4.4  2003/06/05 00:10:19  mbw
// bug fix: removed quotes around a reference to static ConfigExceptionFactory instance variables
//
// Revision 1.1.4.3  2003/06/04 18:28:35  mbw
// javadoc edits
//
// Revision 1.1.4.2  2003/05/22 15:48:47  mbw
// javadocs edits
//
// Revision 1.1.4.1  2003/05/20 18:42:22  mbw
// merged from branch lib_java_shrdbutils-1-0-7-jsam
//
// Revision 1.1.2.9  2003/05/01 15:40:56  dbm
// Return null from getConfigIntegerNull() when no value is found.
//
// Revision 1.1.2.8  2003/04/29 21:14:12  mbw
// changed return values from int and boolean to Integer and Boolean and added new lookup methods which lookup and convert Intgers and Booleans but do not throw exception if parameters are not found. Also added a parameter for turning the prefix mechanism on and off.
//
// Revision 1.1.2.7  2003/04/22 22:16:43  mbw
// added casting of MGIException to ConfigException
//
// Revision 1.1.2.6  2003/04/22 00:11:22  mbw
// no longer throwing KnownException type
//
// Revision 1.1.2.5  2003/04/18 14:20:13  mbw
// javadoc edit
//
// Revision 1.1.2.4  2003/04/18 14:17:25  mbw
// javadoc edit
//
// Revision 1.1.2.3  2003/04/18 01:57:12  mbw
// exempted the Strings MODIFIED_BY and CREATED_BY from prefixing
//
// Revision 1.1.2.2  2003/04/18 01:32:42  mbw
// fixed constructor so not to return void
//
// Revision 1.1.2.1  2003/04/18 01:03:10  mbw
// renamed ConfigLookup to Configurator
//
// Revision 1.1.2.12  2003/04/11 15:23:48  dbm
// Made getConfigString(name,cm) method static
//
// Revision 1.1.2.11  2003/04/09 21:27:00  mbw
// changed to throw exception if the found string does not represent a boolean value
//
// Revision 1.1.2.10  2003/04/08 22:20:09  mbw
// ConfigReader is no longer static
//
// Revision 1.1.2.9  2003/03/21 14:25:25  mbw
// added standard header/footer
//
// Revision 1.1.2.8  2003/03/21 14:13:10  mbw
// added standard header/footer
//
// Revision 1.1.2.7  2003/03/21 14:11:28  mbw
// added standard header/footer
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
