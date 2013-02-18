// $Header$
// $Name$

package org.jax.mgi.shr.types;

import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;


/**
 *  A static object which handles java data type conversions for a known set
 * of java data types. The following types are supported:<br>
 * int, double, boolean, Integer, Double, Boolean, String and Timestamp
 * @has Static methods for doing java data type conversions.
 * @does Converts input between java types and strings and between
 * primitive types and the wrapper classes.<br>
 *
 * @company Jackson Laboratory
 * @author M Walker
 */


public class Converter {

  // the following constants are the exceptions used by this class
  private static final String UnhandledDataType =
      TypesExceptionFactory.UnhandledDataType;
  private static final String MalformedString =
      TypesExceptionFactory.MalformedString;

  // private constructor. use as static.
  private Converter() {
  }

  /**
   * create the wrapper class from the given primitive
   * @param input primitive
   * @return wrapper type
   */
  public static Integer wrap(int input) {
    return new Integer(input);
  }

  /**
   * create the wrapper class from the given primitive
   * @param input primitive
   * @return wrapper type
   */
  public static Double wrap(double input) {
    return new Double(input);
  }

  /**
   * create the wrapper class from the given primitive
   * @param input primitive
   * @return wrapper type
   */
  public static Boolean wrap(boolean input) {
    return new Boolean(input);
  }

  /**
   * convert data to String
   * @param input data for conversion
   * @return data converted to String
   */
  public static String toString(int input) {
    return wrap(input).toString();
  }

  /**
   * convert data to String
   * @param input data for conversion
   * @return data converted to String
   */
  public static String toString(double input) {
    return wrap(input).toString();
  }

  /**
   * convert data to String
   * @param input data for conversion
   * @return data converted to String whose value would be either "0" or "1"
   */
  public static String toString(boolean input) {
    if (input)
      return "1";
    else
      return "0";
  }

  /**
   * convert data to String
   * @param input data for conversion
   * @return data converted to String
   */
  public static String toString(Integer input) {
      if (input == null)
          return null;
    return input.toString();
  }

  /**
   * convert data to String
   * @param input data for conversion
   * @return data converted to String
   */
  public static String toString(Double input) {
      if (input == null)
          return null;
    return input.toString();
  }

  /**
   * convert data to String
   * @param input data for conversion
   * @return data converted to String whose value will be either "0" or "1"
   */
  public static String toString(Boolean input) {
      if (input == null)
          return null;
    if (input.booleanValue())
      return "1";
    else
      return "0";
  }

  /**
   * convert data to String
   * @param input data for conversion
   * @return data converted to String
   */
  public static String toString(Timestamp input) {
      if (input == null)
          return null;
    return input.toString();
  }

  /**
   * convert data from generic type object to String. The object type is
   * identified and if it is not of allowed type then a TypesException
   * is raised. See class definition for allowed types.
   * @param input data for conversion
   * @return data converted to String
   * @throws TypesException thrown if there is an error in conversion
   */
  public static String objectToString(Object input) throws TypesException {
      if (input == null)
          return null;
    if (input instanceof String)
      return (String)input;
    else if (input instanceof Integer)
      return toString((Integer)input);
    else if (input instanceof Double)
      return toString((Double)input);
    else if (input instanceof Timestamp)
      return toString((Timestamp)input);
    else if (input instanceof Boolean)
      return toString((Boolean)input);
    else {
      TypesExceptionFactory factory = new TypesExceptionFactory();
      TypesException e =
          (TypesException)factory.getException(UnhandledDataType);
      e.bind(input.getClass().getName());
      throw e;
    }
  }

  /**
   * return the string representing the properly formatted sql fragment
   * as expected by Sybase to represent the value field within the assignment
   * clause or where clause of a sql statement. This method is designed
   * to assist in dynamic sql generation.
   * @param input a given value to be formatted as a sql fragment
   * @return the formatted sql fragment with single quotes applied to the
   * given value
   */
  public static String toSQLString(String input) {
    input = input.replaceAll("'", "''");
    return "'" + input + "'";
  }

  /**
   * return the string representing the properly formatted sql fragment
   * as expected by Sybase to represent the value field within the assignment
   * clause or where clause of a sql statement. This method is designed
   * to assist in dynamic sql generation.
   * @param input a given value to be formatted as a sql fragment
   * @return a string representing zero or one
   */
  public static String toSQLString(Boolean input) {
    if (input.booleanValue())
      return "1";
    else
      return "0";
  }

  /**
   * return the string representing the properly formatted sql fragment
   * as expected by Sybase to represent the value field within the assignment
   * clause or where clause of a sql statement. This method is designed
   * to assist in dynamic sql generation.
   * @param input a given value to be formatted as a sql fragment
   * @return the integer value as an unquoted string
   */
  public static String toSQLString(Integer input) {
    return input.toString();
  }

  /**
   * return the string representing the properly formatted sql fragment
   * as expected by Sybase to represent the value field within the assignment
   * clause or where clause of a sql statement. This method is designed
   * to assist in dynamic sql generation.
   * @param input a given value to be formatted as a sql fragment
   * @return the double value as an unquoted string
   */
  public static String toSQLString(Double input) {
    return input.toString();
  }

  /**
   * return the string representing the properly formatted sql fragment
   * as expected by Sybase to represent the value field within the assignment
   * clause or where clause of a sql statement. This method is designed
   * to assist in dynamic sql generation.
   * @param input a given value to be formatted as a sql fragment
   * @return the timestamp value with sinle quotes applied
   */
  public static String toSQLString(Timestamp input) {
    return "'" + input.toString() + "'";
  }


  /**
   *
   * convert data from String to primitive value. Strings representing
   * decimal numbers will cause a TypesException to be raised.
   * @param s data for conversion
   * @return data converted to primitive
   * @throws TypesException thrown if there is an error in conversion
   */
  public static int toPrimitiveInt(String s) throws TypesException {
    try {
      return (new Integer(s)).intValue();
    }
    catch (NumberFormatException e) {
      TypesExceptionFactory factory = new TypesExceptionFactory();
      TypesException e2 =
          (TypesException)factory.getException(MalformedString, e);
      e2.bind(s);
      e2.bind("int");
      throw e2;
    }
  }

  /**
   *
   * convert data from String to primitive value
   * @param s data for conversion
   * @return data converted to primitive
   * @throws TypesException thrown if there is an error in conversion
   */
  public static double toPrimitiveDouble(String s)
  throws TypesException
  {
    try {
      return (new Double(s)).doubleValue();
    }
    catch (NumberFormatException e) {
      TypesExceptionFactory factory = new TypesExceptionFactory();
      TypesException e2 =
          (TypesException)factory.getException(MalformedString);
      e2.bind(s);
      e2.bind("double");
      throw e2;
    }
  }

  /**
   *
   * convert data from String to primitive value. "true" yields
   * true; all else yields false. The function is case insensitive.
   * @param s data for conversion
   * @return data converted to primitive
   * @throws TypesException thrown if there is an error in conversion
   */
  public static boolean toPrimitiveBoolean(String s) throws TypesException {
    return convertToBoolean(s);
  }

  /**
   *
   * convert data from String to Integer. Strings representing decimal
   * numbers will cause a TypesException to be raised.
   * @param s data for conversion
   * @return new object converted
   * @throws TypesException thrown if there is an error in conversion
   */
  public static Integer toInteger(String s) throws TypesException {
    try {
      return new Integer(s);
    }
    catch (NumberFormatException e) {
      TypesExceptionFactory factory = new TypesExceptionFactory();
      TypesException e2 =
          (TypesException)factory.getException(MalformedString, e);
      e2.bind(s);
      e2.bind("Integer");
      throw e2;
    }
  }

  /**
   *
   * convert data from String to Double
   * @param s data for conversion
   * @return new object converted
   * @throws TypesException
   */
  public static Double toDouble(String s) throws TypesException {
    try {
      return new Double(s);
    }
    catch (NumberFormatException e) {
      TypesExceptionFactory factory = new TypesExceptionFactory();
      TypesException e2 =
          (TypesException)factory.getException(MalformedString, e);
      e2.bind(s);
      e2.bind("Double");
      throw e2;
    }
  }

  /**
   *
   * convert data from String to Boolean. "True" or "true" yields
   * true; all else yields false.
   * @param s data for conversion
   * @return new object converted
   * @throws TypesException thrown if there is an error in conversion
   */
  public static Boolean toBoolean(String s) throws TypesException {
    return new Boolean(convertToBoolean(s));
  }

  /**
   *
   * Convert data from String to Timestamp.
   * The string format accepted is any of the following patterns, with or
   * without the time segments:<br>
   * yyyy-mm-dd hh:mm:ss.fffffffff<br>
   * yyyy/mm/dd hh:mm:ss.fffffffff
   * @param s data for conversion
   * @return new object converted
   * @throws TypesException thrown if there is an error in conversion
   */
  public static Timestamp toTimestamp(String s)
  throws TypesException
  {
    SimpleDateFormat format = null;
    Date date = null;
    format = new SimpleDateFormat("yyyy-MM-dd");
    try {
      date = format.parse(s);
      return (new Timestamp(date.getTime()));
    }
    catch (ParseException e) {}
    format = new SimpleDateFormat("yyyy/MM/dd");
    try {
      date = format.parse(s);
      return (new Timestamp(date.getTime()));
    }
    catch (ParseException e) {}

    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
    try {
      date = format.parse(s);
      return (new Timestamp(date.getTime()));
    }
    catch (ParseException e) {}
    format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSSSSSSS");
    try {
      date = format.parse(s);
      return new Timestamp(date.getTime());
    }
    catch (ParseException e) {
      TypesExceptionFactory factory = new TypesExceptionFactory();
      TypesException e2 =
          (TypesException)factory.getException(MalformedString, e);
      e2.bind(s);
      e2.bind("Timestamp");
      throw e2;
    }
  }

  /**
   *
   * Convert a string to a boolean value.  "TRUE", "YES" or "1" are
   * equivalent to true.  "FALSE", "NO" or "0" are equivalent to false.
   * Anything else yields false.
   * @param s data for conversion
   * @return true or false
   * @throws TypesException thrown if there is an error in conversion
   */
  private static boolean convertToBoolean(String s) throws TypesException {
    if (s.toUpperCase().equals("TRUE") ||
      s.toUpperCase().equals("YES") ||
      s.equals("1"))
      return true;
    else if (s.toUpperCase().equals("FALSE") ||
      s.toUpperCase().equals("NO") ||
      s.equals("0"))
      return false;
    else {
      TypesExceptionFactory factory = new TypesExceptionFactory();
      TypesException e2 =
          (TypesException)factory.getException(MalformedString);
      e2.bind(s);
      e2.bind("boolean");
      throw e2;
    }
  }
}

// $Log$
// Revision 1.1  2013/01/30 18:05:39  mgiadmin
// postgres convert
//
// Revision 1.6  2008/10/08 12:59:36  sc
// lib_java_core-4-1-2-2
//
// Revision 1.5.52.1  2008/09/03 18:23:07  sc
// lib_java_core-tr9231-1
//
// Revision 1.5  2004/12/16 21:18:58  mbw
// merged assembly branch onto the trunk
//
// Revision 1.4.4.1  2004/12/02 19:27:11  mbw
// changed use of floats to doubles
//
// Revision 1.4  2004/07/21 20:51:58  mbw
// javadocs edits only
//
// Revision 1.3  2004/05/05 16:47:49  mbw
// now returning "null" in all toString methods if the object being converted is null
//
// Revision 1.2  2003/12/31 18:26:36  mbw
// added new toSQLString methods
//
// Revision 1.1  2003/12/30 16:56:53  mbw
// imported into this product
//
// Revision 1.16  2003/10/22 15:32:28  mbw
// changed name of toString(Object) method to objectToString(Object)
//
// Revision 1.15  2003/05/22 15:50:52  mbw
// javadocs edits
//
// Revision 1.14  2003/05/16 15:11:49  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.13  2003/04/22 22:19:55  mbw
// added casting of MGIException to TypesException
//
// Revision 1.12  2003/04/22 00:10:09  mbw
// no longer throwing type KnownException
//
// Revision 1.11  2003/04/17 23:32:26  mbw
// made constructor private
//
// Revision 1.10  2003/04/08 22:44:29  mbw
// now throwing exception if string value dows not represent a boolean
//
// Revision 1.9  2003/04/03 01:10:27  mbw
// method has been changed for private use only
//
// Revision 1.8  2003/03/21 16:22:54  mbw
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
