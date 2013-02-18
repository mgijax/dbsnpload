package org.jax.mgi.shr.dbutils;

import java.util.Hashtable;
import org.jax.mgi.shr.config.DatabaseCfg;
import org.jax.mgi.shr.config.ConfigException;

/**
 * A class for accessing SLDataManager instances by name. The name
 * matches the prefix used for the SQLDataManager configuration parameters.
 * The references accessed can be shared or new.
 * @has a mapping of SQLDataManager references by name
 * @does provides a either a shared or new reference to a SQLDataManager
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class SQLDataManagerFactory {


  /**
   * the internal mapping of names and references
   */
  private static Hashtable refmap = new Hashtable();

  /**
   * get a new reference to a SQLDataManager by name
   * @param cfgPrefix the name of the reference which would match some
   * prefix used for a set of SQLDataManager configuration parameters
   * @return the SQLDataManager reference
   * @throws ConfigException thrown if there was an error accessing the
   * configuration file
   * @throws DBException thrown if a new SQLDataManager reference could not
   * be created
   */
  static public SQLDataManager getNew(String cfgPrefix)
      throws ConfigException, DBException
  {
    DatabaseCfg conf = new DatabaseCfg(cfgPrefix);
    SQLDataManager sqlmgr = new SQLDataManager(conf);
    return sqlmgr;
  }

  /**
   * get a shared reference to a SQLDataManager by name
   * @param cfgPrefix the name of the reference which would match some
   * prefix used for a set of SQLDataManager configuration parameters
   * @return the shared SQLDataManager reference
   * @throws ConfigException thrown if there was an error accessing the
   * configuration file
   * @throws DBException thrown if a new SQLDataManager reference could not
   * be created
   */
  static public SQLDataManager getShared(String cfgPrefix) throws
      ConfigException, DBException {
    SQLDataManager sqlmgr = (SQLDataManager) refmap.get(cfgPrefix);
    if (sqlmgr == null) {
      DatabaseCfg conf = new DatabaseCfg(cfgPrefix);
      sqlmgr = new SQLDataManager(conf);
      refmap.put(cfgPrefix, sqlmgr);
    }
    return sqlmgr;
  }

}





