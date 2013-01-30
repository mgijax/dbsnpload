package org.jax.mgi.shr.dbutils;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for storing DataExceptions.
 * @has a hashmap of predefined DataExceptions stored by a name key
 * @does looks up DataExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class DataExceptionFactory extends ExceptionFactory {

  /**
   * a string could not be converted to another datatype
   */
  public static final String ConversionErr =
      "org.jax.mgi.shr.data.ConversionErr";
  static {
    exceptionsMap.put(ConversionErr, new DataException(
      "Could not convert String value '??' to type ??", true));
  }
  /**
   * the object is not handled by this class
   */
  public static final String UnhandledDataType =
      "org.jax.mgi.shr.data.UnhandledDataType";
  static {
    exceptionsMap.put(UnhandledDataType, new DataException(
      "data type ?? is not handled by this class", true));
  }
  /**
   * the string is not of value "0" or "1"
   */
  public static final String BitValueErr =
      "org.jax.mgi.shr.data.BitValueErr";
  static {
    exceptionsMap.put(BitValueErr, new DataException(
      "Bit value must be 0 or 1.  Found: ??", true));
  }
  /**
   * a NULL was attemped as the value to not nullable database column
   */
  public static final String NotNullable =
      "org.jax.mgi.shr.data.NotNullable";
  static {
    exceptionsMap.put(NotNullable, new DataException(
      "Null value not allowed in column ??", true));
  }
  /**
   * a string was too large for the size of the database column
   */
  public static final String ColumnOverflow =
      "org.jax.mgi.shr.data.ColumnOverflow";
  static {
    exceptionsMap.put(ColumnOverflow, new DataException(
      "Value '??' is too large for column ??", true));
  }
  /**
   * the data type is not handled by this conversion routine
   */
  public static final String UnhandledConversion =
      "org.jax.mgi.shr.data.UnhandledConversion";
  static {
    exceptionsMap.put(UnhandledConversion, new DataException(
        "Conversion of datatype ?? to datatype ?? is " +
        "not supported", true));
  }
  /**
   * the count of data fields does not math the number of table columns
   */
  public static final String FieldCountErr =
      "org.jax.mgi.shr.data.FieldCountErr";
  static {
    exceptionsMap.put(FieldCountErr, new DataException(
        "Invalid number of fields.  Found: ?? " +
        "Expected: ?? for table ??", true));
  }



}