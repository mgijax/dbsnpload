package org.jax.mgi.app.dbsnploader;

import java.util.HashMap;
import java.util.HashSet;

import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.cache.FullCachedLookup;
import org.jax.mgi.shr.cache.KeyValue;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;

/**
 * @is a FullCachedLookup for caching RS IDs in the database 
 * @has a RowDataCacheStrategy of type FULL_CACHE used for creating the
 * cache and performing the cache lookup
 * @does provides a lookup method for getting the set of RS IDs in the database
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */
public class DBSNPRSLookup extends FullCachedLookup
{
  // provide a static cache so that all instances share one cache
  private static HashMap cache = new HashMap();

  // indicator of whether or not the cache has been initialized
  private static boolean hasBeenInitialized = false;


   /**
   * constructor
   * @throws CacheException thrown if there is an error with the cache
   * @throws DBException thrown if there is an error accessing the db
   * @throws ConfigException thrown if there is an error accessing the
   * configuration file
   */
  public DBSNPRSLookup()
      throws CacheException, DBException, ConfigException {
    super(SQLDataManagerFactory.getShared(SchemaConstants.SNP));
    if (!hasBeenInitialized) {
        initCache(cache);
      }
      hasBeenInitialized = true;
  }

  /**
   * look up the set of mutations terms for an allele
   * @param alleleKey the allele key to look up
   * @return Set of mutation terms (Strings)
   * @throws CacheException thrown if there is an error accessing the cache
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   * @throws DBException thrown if there is an error accessing the
   * database
   * @throws TranslationException thrown if there is an error accessing the
   * translation tables
   */
  public String lookup(String rsID) throws CacheException,
		DBException {
      return (String)super.lookupNullsOk(rsID);
  }

  /**
   * get the full initialization query which is called by the CacheStrategy
   * class when performing cache initialization
   * @return the full initialization query
   */
 public String getFullInitQuery()
  {
    String s = "SELECT accid " +
	    "FROM SNP_Accession " + 
	    "WHERE _LogicalDB_key = 73 ";
    return s;
  }


  /**
   * get the RowDataInterpreter which is required by the CacheStrategy to
   * read the results of a database query.
   * @return the partial initialization query
   */
  public RowDataInterpreter getRowDataInterpreter() {
    class Interpreter implements RowDataInterpreter {
		String rsID;
    	public Object interpret(RowReference row) throws DBException {
			//return new KeyValue(row.getString(1), row.getInt(2));
			rsID = row.getString(1);
			return new KeyValue(rsID, rsID );
		}
		
    }
    return new Interpreter();
  }
  /**
   * initialize the cache if not already initialized
   * @throws CacheException thrown if there is an error with the cache
   * @throws DBException thrown if there is an error accessing the db
   * @throws ConfigException thrown if there is an error accessing the
   * configuration file
   */
  private void initialize()
      throws CacheException, DBException, ConfigException {
    // since cache is static make sure you do not reinit
    if (!hasBeenInitialized)
		initCache(cache);
    hasBeenInitialized = true;

  }
   /**
     * Simple data object representing a row of data from the query
     */
    class RowData {
    	protected String rsID;
        public RowData (RowReference row) throws DBException {
            rsID = row.getString(1);
            
        }
    }
}

