package org.jax.mgi.shr.config;

import java.util.HashMap;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * A class for configuring a RecordStamp parameters.
 * @has a ConfigurationManager object
 * @does provides a method for getting the job stream name from the
 * configuration file or system properties and lookups up key values
 * in the MGI_User table for the configured JobStream name
 * @company Jackson Laboratory
 * @author M. Walker
 */
public class RecordStampCfg
    extends Configurator {
    /**
     * a cache of job stream names and keys that have been previously looked up
     */
    private static HashMap cache = new HashMap();
    /*
     * the following are constants for exceptions thrown by this class
     */
    private static String UserNotFound =
        ConfigExceptionFactory.UserNotFound;
    /**
         * default constructor which obtains a reference to the ConfigurationManager
     * singleton class.
     * @assumes nothing
     * @effects nothing
     * @throws ConfigException
     */
    public RecordStampCfg() throws ConfigException {
        super();
    }

    /**
     * get the string which is used as the value of the created_by field
     * and modified_by field when performing automatic record stamping either
     * through the java bcp frameworks or dao frameworks.
     * The parameter name read from the configuration file is JOBSTREAM.
     * The default value is the user.name java System property.
     * @assumes nothing
     * @effects nothing
     * @return string to be used in the created_by database record field.
     */
    public String getJobStreamName() {
        return getConfigString("JOBSTREAM", System.getProperty("user.name"));
    }

    /**
     * get the job stream key used for record stamping.
     * The parameter name read from the configuration file is JOBKEY.
     * @assumes nothing
     * @effects nothing
     * @return string to be used in the created_by database record field.
     * @throws ConfigException thrown if there is an error accessing the
     * configuration or if the JOBKEY parameter is not found
     */
    public Integer getJobKey() throws ConfigException {
        return getConfigInteger("JOBKEY");
    }

    /**
     * lookup the job stream key using the configured job stream name
     * @assumes nothing
     * @effects nothing
     * @return the job stream key
     * @throws ConfigException thrown if the name could not be found
     * or there was an error accessing the database or configuration file
     */
    public Integer getJobStreamKey() throws ConfigException {
        String name = getJobStreamName();
        UserLookup lookup = null;
        /**
         * first check the cache
         */
        Integer key = (Integer) cache.get(name);
        if (key == null) {
            try {
                lookup = new UserLookup();
                key = lookup.lookupByName(name);
            }
            catch (MGIException e) {
                ConfigExceptionFactory eFactory = new ConfigExceptionFactory();
                ConfigException e2 = (ConfigException)
                    eFactory.getException(UserNotFound, e);
                e2.bind(name);
                throw e2;
            }
            if (key == null) {
                ConfigExceptionFactory eFactory = new ConfigExceptionFactory();
                ConfigException e2 = (ConfigException)
                    eFactory.getException(UserNotFound);
                e2.bind(name);
                throw e2;
            }
        }
        /**
         * store results in cache and return the value
         */
        cache.put(name, key);
        return key;
    }

    /**
     * get the string which is used as the value of the modified_by field when
         * performing automatic record stamping with the bcp or dao java frameworks.
     * The parameter name read from the configuration file is MODIFIED_BY.
     * The default value is the user.name java System property.
     * @return string to be used in the modified_by database record field.
     */
    public String getModifiedBy() {
        String value =
            getConfigString("MODIFIED_BY", System.getProperty("user.name"));
        return value;
    }

    /**
     * get the string which is used as the value of the created_by field when
         * performing automatic record stamping with the bcp or dao java frameworks.
     * The parameter name read from the configuration file is CREATED_BY.
     * The default value is the user.name java System property.
     * @return string to be used in the created_by database record field.
     */
    public String getCreatedBy() {
        String value =
            getConfigString("CREATED_BY", System.getProperty("user.name"));
        return value;
    }

    /**
     * A class for looking up key values in the database for a given user of
     * the system
     * @has an internal cache of user keys to user names
     * @does maintains the internal cache and provoides lookup functiomality
     * @company The Jackson Laboratory
     * @author M Walker
     *
     */
    public class UserLookup
        extends FullCachedLookup {
        // internal cache
        private HashMap cache = new HashMap();
        /**
         * the default constructor
         * @assumes nothing
         * @effects nothing
         * @throws ConfigException thrown if there is an error accessing the
         * configuration file
             * @throws DBException thrown if there is an error accessing the database
             * @throws CacheException thrown if there is an error accessing the cache
         */
        public UserLookup() throws ConfigException, DBException, CacheException {
            super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
        }

        /**
         * look up a userid for the given user name from the MGI_USER table
         * @param name the name to lookup
         * @assumes nothing
         * @effects nothing
         * @return the userid
         * @throws DBException thrown if there is an error accessing the
         * database
         * @throws CacheException thrown if there is an error accessing the
         * cache
         * @throws KeyNotFoundException thrown if the lookup fails to find a
         * value
         */
        public Integer lookupByName(String name) throws DBException,
            CacheException, KeyNotFoundException {
            return (Integer) lookup(name);
        }

        /**
         * get the query to fully initialize the cache
         * @assumes nothing
         * @effects nothing
         * @return the query to fully initialize the cache
         */
        public String getFullInitQuery() {
            return new String("SELECT _User_key, login FROM MGI_USER");
        }

        /**
         * get a RowDataInterpreter for creating
         * @return the RowDataInterpreter
         */
        public RowDataInterpreter getRowDataInterpreter() {
            class Interpreter
                implements RowDataInterpreter {
                public Object interpret(RowReference row) throws DBException {
                    return new KeyValue(row.getString(2), row.getInt(1));
                }
            }

            return new Interpreter();
        }
    }
}
