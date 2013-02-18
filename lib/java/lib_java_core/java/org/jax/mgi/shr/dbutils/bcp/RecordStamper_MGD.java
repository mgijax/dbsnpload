package org.jax.mgi.shr.dbutils.bcp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.config.RecordStampCfg;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * A RecordStamper object for tables in the MGD database which
 * contain the fields _CreatedBy_key, _ModifiedBy_key, creation_date,
 * modification_date
 * @has a UserLookup class for resolving use names into ids
 * @does creates a string containing the fields required for record
 * stamping each seperated by a designated delimiter
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class RecordStamper_MGD implements RecordStamper
{

  /**
   * the User object used to obtain values for the fields
   * _CreatedBy_key and _ModifiedBy_key
   */
  private Integer userKey = null;

  /**
   * the following constant defintions are exceptions thrown by this
   * class
   */
  private static final String UserNotFound =
                        BCPExceptionFactory.UserNotFound;

  /**
   * return the string which represents the fields _CreatedBy_key,
   * _ModifiedBy_key, creation_date and modification_date seperated by the
   * given delimiter
   * @param delimiter the delimiter to use to sepearte the stamp fields
   * @return the record stamp string
   * @throws BCPException thrown if there is an error obtaining the user id
   */
  public String getStamp(String delimiter) throws BCPException {
    String name = null;
    if (userKey == null) { // the User has not been found yet
      try {
        RecordStampCfg cfg = new RecordStampCfg();
        name = cfg.getJobStreamName();
        userKey = cfg.getJobStreamKey();
      }
      catch (ConfigException e) {
        BCPExceptionFactory eFactory = new BCPExceptionFactory();
        BCPException e2 = (BCPException)
            eFactory.getException(UserNotFound, e);
        e.bind(name);
        throw e2;
      }
    }
    String timestamp =
        Converter.toString(new Timestamp(new Date().getTime()));
    String key = Converter.toString(userKey);
    //String key = null;
    String stamp = new String(delimiter + key + delimiter + key +
       delimiter + timestamp + delimiter + timestamp);
    return stamp;
  }

  /**
   * get the number of fields used for record stamping
   * @return the number of fields used for record stamping
   */
  public int getStampFieldCount()
  {
    return 4;
  }

  /**
   *  a RowDataCacheHandler class for looking up userids stored within
   * the MGI_USER table
   * @has a cache for storing database data in memory
   * @does gets data from the database and caches it into memory and provides
   * lookup methods for accessing the data
   * @author M Walker
   */

  public class UserLookup extends FullCachedLookup
  {
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
    public UserLookup() throws ConfigException, DBException, CacheException
    {
      super(SQLDataManagerFactory.getShared(SchemaConstants.MGD));
    }

    /**
     * look up a userid for the given user name from the MGI_USER table
     * @param name the name to lookup
     * @assumes nothing
     * @effects nothing
     * @return the userid
     * @throws CacheException thrown if there is an error accessing the cache
     * @throws DBException thrown if there is an error accessing the database
     * @throws KeyNotFoundException thrown if the key was not found
     */
    public Integer lookupByName(String name)
    throws CacheException, DBException, KeyNotFoundException
    {
      return (Integer)lookup(name);
    }

    /**
     * get the query to fully initialize the cache
     * @assumes nothing
     * @effects nothing
     * @return the query to fully initialize the cache
     */
    public String getFullInitQuery()
    {
      return new String("SELECT _User_key, login FROM MGI_USER");
    }

    /**
     * get a RowDataInterpreter for creating
     * @return the RowDataInterpreter
     */
    public RowDataInterpreter getRowDataInterpreter()
    {
      class Interpreter implements RowDataInterpreter {
        public Object interpret(RowReference row) throws DBException {
          return new KeyValue(row.getString(2), row.getInt(1));
        }
      }
      return new Interpreter();
    }

  }

}