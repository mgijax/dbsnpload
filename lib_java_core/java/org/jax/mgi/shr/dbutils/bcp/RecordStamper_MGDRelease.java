package org.jax.mgi.shr.dbutils.bcp;

import java.sql.Timestamp;
import java.util.Date;

import org.jax.mgi.shr.types.Converter;

/**
 *  A RecordStamper object for tables in the MGD database which
 * contain the fields creation_date, modification_date, release_date
 * @has nothing
 * @does creates a string containing the fields required for record
 * stamping each seperated by a designated delimiter
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class RecordStamper_MGDRelease implements RecordStamper
{

  /**
   * return the string which represents the fields creation_date,
   * modification_date and release_date seperated by the given delimiter
   * @param delimiter the delimiter to use to sepearte the stamp fields
   * @return the record stamp string
   */
  public String getStamp(String delimiter)
  {
    String timestamp =
        Converter.toString(new Timestamp(new Date().getTime()));
    return new String(delimiter + timestamp + delimiter + timestamp +
                      delimiter + timestamp);
  }

  /**
   * get the number of fields used for record stamping
   * @return the number of fields used for record stamping
   */
  public int getStampFieldCount()
  {
    return 3;
  }


}
