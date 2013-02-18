package org.jax.mgi.shr.dbutils.bcp;


/**
 *  A RecordStamper object for tables which contain no user or date stamps
 * @does creates a string containing the fields required for record which
 * in this case is an empty string
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class RecordStamper_None implements RecordStamper
{

  /**
   * return a empty string to represent no user or time stamps
   * @param delimiter the delimiter to use to sepearte the stamp fields
   * @return the record stamp string
   */
  public String getStamp(String delimiter)
  {
    return "";
  }

  /**
   * get the number of fields used for record stamping
   * @return the number of fields used for record stamping
   */
  public int getStampFieldCount()
  {
    return 0;
  }

}