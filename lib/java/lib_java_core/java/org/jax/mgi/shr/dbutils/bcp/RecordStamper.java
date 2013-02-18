package org.jax.mgi.shr.dbutils.bcp;

/**
 *  A class that provides the user-time stamp field values to a BCPWriter
 * in aid of composing a line for a bcp file
 * @has nothing
 * @does calculates the user-time fields for the implemented format and
 * creates a String composed of the fields seperated by a designated delimiter
 */
public interface RecordStamper
{
  /**
   * get the string which lists the record stamping fields seperated by
   * the given delimiter
   * @param delimiter the delimiter to use
   * @return the string of record stamping fields seperated by the given
   * delimiter
   * @throws BCPException thrown if there is an excption trying to
   * determine the values for the record stamp
   */
  public String getStamp(String delimiter) throws BCPException;

  /**
   * get the number of fields used for record stamping
   * @return the number of fields used for record stamping
   */
  public int getStampFieldCount();
}
