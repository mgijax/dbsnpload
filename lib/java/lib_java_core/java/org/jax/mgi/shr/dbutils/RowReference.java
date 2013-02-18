// $Header$
// $Name$

package org.jax.mgi.shr.dbutils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Calendar;
import java.io.PrintStream;

import org.jax.mgi.shr.log.Logger;

/**
 * An object representing a reference to the current row
 * of a JDBC ResultSet. It is returned as the default object from
 * the call to the getCurrent() method on a ResultsNavigator.
 * @has The JDBC ResultSet object and a pointer to the current row
 * @does provides controlled access to the current row of data of a
 * ResultSet. Only direct access to the following data types are provided:
 * Integer, Double, String, Timestamp and Boolean. All other types can be
 * accessed indirectly through the getObject methods.
 * @company Jackson Laboratory
 * @author M. Walker
 */


public class RowReference
{
  private ResultSet rs = null;
  private ResultSetMetaData meta = null;

  // the following constant definitions are exceptions thrown by this class
  private static final String JDBCException =
      DBExceptionFactory.JDBCException;

  /**
   * protected constructor.
   * @param rsParm the ResultSet object
   */
  protected RowReference(ResultSet rsParm) {
    rs = rsParm;
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Boolean getBoolean(int columnIndex) throws DBException {
    try {
      boolean b = rs.getBoolean(columnIndex);
      if (rs.wasNull())
        return null;
      else
        return new Boolean(b);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get boolean value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Boolean getBoolean(String columnName) throws DBException {
    try {
      boolean b = rs.getBoolean(columnName);
      if (rs.wasNull())
        return null;
      else
        return new Boolean(b);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get boolean value from column name " + columnName, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Double getDouble(int columnIndex) throws DBException {
    try {
      double f = rs.getDouble(columnIndex);
      if (rs.wasNull())
        return null;
      else
        return new Double(f);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get double value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Double getDouble(String columnName) throws DBException {
    try {
      double f = rs.getDouble(columnName);
      if (rs.wasNull())
        return null;
      else
        return new Double(f);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get double value from column name " + columnName, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Integer getInt(int columnIndex) throws DBException {
    try {
      int i = rs.getInt(columnIndex);
      if (rs.wasNull())
        return null;
      else
        return new Integer(i);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get int value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Integer getInt(String columnName) throws DBException {
    try {
      int i = rs.getInt(columnName);
      if (rs.wasNull())
        return null;
      else
        return new Integer(i);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get int value from column name " + columnName, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Object getObject(int columnIndex) throws DBException {
    try {
      return rs.getObject(columnIndex);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get object value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Object getObject(int columnIndex, Map map) throws DBException {
    try {
      return rs.getObject(columnIndex, map);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get object value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Object getObject(String columnName) throws DBException {
    try {
      return rs.getObject(columnName);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get object value from column name " + columnName, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Object getObject(String columnName, Map map) throws DBException {
    try {
      return rs.getObject(columnName, map);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get object value from column name " + columnName, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public String getString(int columnIndex) throws DBException {
    try {
      return rs.getString(columnIndex);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get string value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public String getString(String columnName) throws DBException {
    try {
      return rs.getString(columnName);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get string value from column name " + columnName, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Timestamp getTimestamp(int columnIndex) throws DBException {
    try {
      return rs.getTimestamp(columnIndex);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get timestamp value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Timestamp getTimestamp(int columnIndex, Calendar cal)
      throws DBException {
    try {
      return rs.getTimestamp(columnIndex, cal);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get timestamp value from column index " + columnIndex, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Timestamp getTimestamp(String columnName) throws DBException {
    try {
      return rs.getTimestamp(columnName);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get timestamp value from column name " + columnName, e);
    }
  }

  /**
   * see java.sql.ResultSet javadocs
   * @throws DBException thrown if there is an error with the database
   */
  public Timestamp getTimestamp(String columnName, Calendar cal)
      throws DBException {
    try {
      return rs.getTimestamp(columnName, cal);
    }
    catch (SQLException e) {
      throw getJDBCException(
          "get timestamp value from column name " + columnName, e);
    }
  }

  /**
   * get the row metadata
   * @assumes nothing
   * @effects nothing
   * @return the metadata
   * @throws DBException thrown if there is an error from the database
   */
  public ResultSetMetaData getMetaData() throws DBException {
    if (meta == null)
      setMetaData();
    return meta;
  }

  /**
   * create a string representing the current row with a format
   * consisting of columnName = columnValue
   * @assumes nothing
   * @effects nothing
   * @return a string representing the current row
   * @throws DBException throw if meta data cannot be accessed or if the
   * column values cannot be obtained
   */
  public String createStringFromRow() throws DBException {
    StringBuffer s = new StringBuffer();
    if (meta == null)
      setMetaData();
    try {
      int numColumns = meta.getColumnCount();
      for (int i = 1; i < numColumns + 1; i++) {
        String columnName = meta.getColumnName(i);
        if (columnName.equals(""))
          columnName = "NO COLUMN NAME";
        Object object = rs.getObject(i);
        if (object == null)
          s.append(columnName + " = null\n");
        else
          s.append(columnName + " = " + object.toString() + "\n");
      }
    }
    catch (SQLException e) {
      throw getJDBCException("access RowReference columns " +
                             "for printing or logging", e);
    }
    return new String(s);
  }


  /**
   * Prints the column values of the row to the given print stream.
   * @assumes nothing
   * @effects the given print stream has been sent a string
   * @param stream the given print stream.
   * @throws DBException thrown if the meta data cannot be accessed
   * or the column values cannot be obtained
   */
  public void dump(PrintStream stream) throws DBException {
    stream.println(createStringFromRow());
  }

  /**
   * Prints the column values of the row to the given print stream.
   * @assumes nothing
   * @effects the given logger has been sent a log message
   * @param logger the logger instance to print to.
   * @throws DBException thrown if the meta data cannot be accessed
   * or the column values cannot be obtained
   */
  public void dump(Logger logger) throws DBException {
    logger.logInfo(createStringFromRow());
  }


  /**
   * set the internal reference for the row metadata
   * @assumes nothing
   * @effects the internal reference for the row metadata will be set
   * @throws DBException
   */
  private void setMetaData() throws DBException {
    try {
      meta = rs.getMetaData();
    }
    catch (SQLException e) {
      throw getJDBCException("get metadata from ResultSet", e);
    }
  }

  /**
   * get a DBException with message defined by ExceptionFactory.JDBCException
   * @assumes nothing
   * @effects nothing
   * @param pBind string to concatenate to exception message describing
   * what was being attempted when the JDBC Exception was thrown
   * @param pException the JDBC exception that was thrown
   * @return DBException the exception with a completed message
   */
  private DBException getJDBCException(String pBind, Exception pException) {
    DBExceptionFactory eFactory = new DBExceptionFactory();
    DBException e = (DBException)
        eFactory.getException(JDBCException, pException);
    e.bind(pBind);
    return e;
  }

}


// $Log$
// Revision 1.1  2013/01/30 18:03:26  mgiadmin
// postgres convert
//
// Revision 1.3  2004/12/16 21:18:53  mbw
// merged assembly branch onto the trunk
//
// Revision 1.2.4.1  2004/12/02 19:27:01  mbw
// changed use of floats to doubles
//
// Revision 1.2  2004/07/21 19:52:12  mbw
// javadocs edits only
//
// Revision 1.1  2003/12/30 16:50:36  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:49:08  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.4.9  2003/09/30 17:57:06  dbm
// Support conversion of int to Integer for all row reference values
//
// Revision 1.1.4.8  2003/06/17 20:23:13  mbw
// cleaned out unused imports
//
// Revision 1.1.4.7  2003/06/04 18:29:57  mbw
// javadoc edits
//
// Revision 1.1.4.6  2003/06/03 20:41:31  mbw
// added new method createStringFromRow() and use it within the dump() methods
//
// Revision 1.1.4.5  2003/06/03 20:15:16  mbw
// bug fix: incorporated previous bug fixes for dump(PrintStream) into dump(Logger)
//
// Revision 1.1.4.4  2003/06/03 16:00:08  mbw
// bug fix: if executing dump() with an unspecified column name then substitute the name "NO COLUMN NAME"
//
// Revision 1.1.4.3  2003/06/02 15:50:01  mbw
// bug fixed: handling null columns in the dump() method
//
// Revision 1.1.4.2  2003/05/22 15:56:25  mbw
// javadocs edits
//
// Revision 1.1.4.1  2003/05/20 18:30:19  mbw
// merged from branch lib_java_shrdbutils-1-0-7-jsam
//
// Revision 1.1.2.10  2003/05/16 15:11:23  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.1.2.9  2003/05/09 14:24:24  mbw
// added a method for getting the resultset meta data
//
// Revision 1.1.2.8  2003/05/08 01:56:14  mbw
// incorporated changes from code review
//
// Revision 1.1.2.7  2003/04/25 17:12:02  mbw
// updated to reflect changes in design of exception handling
//
// Revision 1.1.2.6  2003/04/15 12:06:51  mbw
// added extended error messages
//
// Revision 1.1.2.5  2003/04/04 00:09:06  mbw
// removed extraneous EOF character
//
// Revision 1.1.2.4  2003/03/21 16:53:02  mbw
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

