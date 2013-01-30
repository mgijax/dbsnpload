package org.jax.mgi.shr.dbutils.bcp;

import java.sql.Timestamp;
import java.util.Date;

import org.jax.mgi.shr.config.BCPManagerCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.types.Converter;

/**
 *  A RecordStamper for tables in the MGD database which
 * contain the fields _JobStream_key, creation_date
 * @has a BCPManagerCfg for finding the JOBSTREAM parameter value
 * @does creates a string containing the fields required for record
 * stamping each seperated by a designated delimiter
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class RecordStamper_RADAR implements RecordStamper
{

  public String getStamp(String delimiter) throws BCPException
  {
    BCPManagerCfg cfgReader = null;
    String jobkey = null;
    try
    {
      cfgReader = new BCPManagerCfg();
      jobkey = cfgReader.getJobKey();
    }
    catch (ConfigException e)
    {
      BCPExceptionFactory eFactory = new BCPExceptionFactory();
      BCPException e2 = (BCPException)
          eFactory.getException(BCPExceptionFactory.JobKeyNotFound, e);
      throw e2;
    }
    String timestamp =
        Converter.toString(new Timestamp(new Date().getTime()));
    String stamp = new String(delimiter + jobkey + delimiter + timestamp);
    return stamp;
  }

  /**
   * get the number of fields used for record stamping
   * @return the number of fields used for record stamping
   */
  public int getStampFieldCount()
  {
    return 2;
  }


}
