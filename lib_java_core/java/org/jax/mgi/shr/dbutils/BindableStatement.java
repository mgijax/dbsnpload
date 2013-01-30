// $Header$
// $Name$

package org.jax.mgi.shr.dbutils;

import java.util.Vector;
import java.util.Iterator;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.shr.timing.Stopwatch;


/**
 * A proxy class for the JDBC PreparedStatement which integrates with
 * other in-house packages for exception handling and logging.
 * @has PreparedStatement handle and a SQLDataManager and the sql string
 * @does binds and executes PreparedStatements. Data binding is limited
 * to the following data types:
 * Integer, Double, Timestamp, String and Boolean.
 * @company Jackson Laboratory
 * @author M. Walker
 */

public class BindableStatement {

  private PreparedStatement preparedStatement = null;
  private SQLDataManager dataManager = null;
  private String sql;
  private Vector bindVariables = null;
  private int bindCount = 0;
  private DBExceptionFactory dbExceptionFactory = new DBExceptionFactory();
  private Logger logger = null;
  private Stopwatch timer = new Stopwatch();

  // the following constant defintions are exceptions thrown by this class
  private static final String UnhandledDataType =
      DBExceptionFactory.UnhandledDataType;
  private static final String JDBCException =
      DBExceptionFactory.JDBCException;
  private static final String BindCountErr =
      DBExceptionFactory.BindCountErr;
  private static final String CloseErr =
      DBExceptionFactory.CloseErr;

  /**
   * constructor which sets the SQLDataManager, the PreparedStatement and
   * the sql string. This is protected. To get an instance of
   * BindableStatement, call the getBindableStatement methods in the
   * SQLDataManager class.
   * @param pManager the SQLDataManager
   * @param pPreparedStatement the PreparedStatment
   * @param pSQL the sql string
   */
  protected BindableStatement(SQLDataManager pManager,
                              PreparedStatement pPreparedStatement,
                              String pSQL) {
    dataManager = pManager;
    sql = pSQL;
    preparedStatement = pPreparedStatement;
    bindVariables = new Vector();
    int start = 0;
    while ((start = sql.indexOf("?", start + 1)) != -1)
    {
      bindVariables.add(null);
      bindCount++;
    }
    logger = pManager.getLogger();
  }

  /**
   * bind the data values provided in the given vector to the given
   * PreparedStatement. See the class description for a list of acceptable
   * data types. Any other type encountered will cause an
   * UnhandledTypeException  to be raised.
   * @assumes nothing
   * @effects parametric values within the SQL statement will set
   * @param pBindVariables a vector containing the bind variables ordered to
   * correspond to the PreparedStatement variables.
   * @throws DBException
   */
  public void bind(Vector pBindVariables) throws DBException {
      if (pBindVariables == null)
        return;
      if (pBindVariables.size() != bindCount)
      {
        DBException e = (DBException)
            dbExceptionFactory.getException(BindCountErr);
        e.bind(pBindVariables.size());
        e.bind(this.bindCount);
        throw e;
      }
      bindVariables = pBindVariables;
      Object currentObject;
      try {
        for (int i = 0; i < bindVariables.size(); i++) {
          currentObject = bindVariables.get(i);
          if (currentObject instanceof Integer) {
            preparedStatement.setInt(
                i + 1, ( (Integer) currentObject).intValue());
          }
          else if (currentObject instanceof Double) {
            preparedStatement.setDouble(i + 1,
                ( (Double) currentObject).doubleValue());
          }
          else if (currentObject instanceof Timestamp) {
            preparedStatement.setTimestamp(
                i + 1, ( (Timestamp) currentObject));
          }
          else if (currentObject instanceof String) {
            preparedStatement.setString(i + 1, ( (String) currentObject));
          }
          else if (currentObject instanceof Boolean) {
            preparedStatement.setBoolean(i + 1,
                ( (Boolean) currentObject).booleanValue());
          }
          else if (currentObject == null) {
            // using setNull() with VARCHAR works for all column types
            preparedStatement.setNull(i + 1, DBTypeConstants.DB_VARCHAR);
          }
          else {
            DBException e = (DBException)
                dbExceptionFactory.getException(UnhandledDataType);
            e.bind(currentObject.getClass().getName());
            throw e;
          }
        }
      }
      catch (SQLException e) {
        String errString =
            "bind the following values to sql string '" + sql + "':";
        for (Iterator it = bindVariables.iterator(); it.hasNext(); ) {
          Object o = it.next();
          errString = errString.concat(" " + o);
        }
        throw getJDBCException(errString, e);
      }
  }

  /**
   * see java.sql.PreparedStatement javadocs
   * @throws DBException if an error occurs with the database
   */

  public void setBoolean(int i, boolean x) throws DBException {
    try {
      preparedStatement.setBoolean(i, x);
    }
    catch (SQLException e) {
      throw getJDBCException("set boolean value " + x + " for column " + i +
              "in sql string '" + sql + "'", e);
    }
    bindVariables.set(i - 1, Converter.wrap(x));
  }

  /**
   * see java.sql.PreparedStatement javadocs
   * @throws DBException if an error occurs with the database
   */
  public void setDouble(int i, double x) throws DBException {
    try {
      preparedStatement.setDouble(i, x);
    }
    catch (SQLException e) {
      throw getJDBCException("set double value " + x + " for column " + i +
              "in sql string '" + sql + "'", e);
    }
    bindVariables.set(i - 1, Converter.wrap(x));
  }

  /**
   * see java.sql.PreparedStatement javadocs
   * @throws DBException if an error occurs with the database
   */
  public void setInt(int i, int x) throws DBException {
    try {
      preparedStatement.setInt(i, x);
    }
    catch (SQLException e) {
      throw getJDBCException("set int value " + x + " for column " + i +
              "in sql string '" + sql + "'", e);
    }
    bindVariables.set(i - 1, Converter.wrap(x));
  }

  /**
   * see java.sql.PreparedStatement javadocs
   * @throws DBException if an error occurs with the database
   */
  public void setNull(int i, int type) throws DBException {
    try {
      preparedStatement.setNull(i, type);
    }
    catch (SQLException e) {
      throw getJDBCException("set null on sql type " + type +
                             " for column " + i +
                             "in sql string '" + sql + "'", e);
    }
    bindVariables.set(i - 1, null);
  }

  /**
   * see java.sql.PreparedStatement javadocs
   * @throws DBException if an error occurs with the database
   */
  public void setString(int i, String x) throws DBException {
    try {
      preparedStatement.setString(i, x);
    }
    catch (SQLException e) {
      throw getJDBCException("set string value '" + x +
                             "' for column " + i +
                             "in sql string '" + sql + "'", e);
    }
    bindVariables.set(i - 1, x);
  }

  /**
   * see java.sql.PreparedStatement javadocs
   * @throws DBException if an error occurs with the database
   */
  public void setTimestamp(int i, Timestamp x) throws DBException {
    try {
      preparedStatement.setTimestamp(i, x);
    }
    catch (SQLException e) {
      throw getJDBCException("set timestamp value " + x +
                             " for column " + i +
                             "in sql string '" + sql + "'", e);
    }
    bindVariables.set(i - 1, x);
  }



  /**
   * Executes the PreparedStatement as an update, insert or delete.
   * @assumes nothing
   * @effects an sql statement will be executed in the database
   * @return the number of rows updated, deleted or inserted
   * @throws DBException
   */
  public int executeUpdate() throws DBException {
    int rtn;
    if (dataManager.isDebug())
    {
        timer.reset();
        timer.start();
        logger.logDebug(getSQLMessage());
    }
    try {
      rtn = preparedStatement.executeUpdate();
    }
    catch (SQLException e) {
      throw getJDBCException(getSQLMessage(), e);
    }
    if (dataManager.isDebug())
    {
        timer.stop();
        logger.logDebug("update took " + timer.time() + " seconds");
    }
    return rtn;
  }

  /**
   * First calls the bind methods on the PreparedStatement using the provided
   * vector and then executes the PreparedStatement as an update, insert or
   * delete. See the class description for a list of acceptable data types.
   * @assumes nothing
   * @effects an sql statement will executed in the database
   * @param v the vector storing data to be bound.
   * @return the number of rows updated, deleted or inserted
   * @throws DBException
   */
  public int executeUpdate(Vector v) throws DBException {
    bind(v);
    return executeUpdate();
  }

  /**
   * Executes the PreparedStatement as a query
   * @assumes nothing
   * @effects an sql statement will be executed in the database
   * @return a RowDataIterator of query results
   * @throws DBException
   */
  public ResultsNavigator executeQuery() throws DBException {
    ResultSet rs = null;
    if (dataManager.isDebug())
    {
        timer.reset();
        timer.start();
        logger.logDebug(getSQLMessage());
    }
    try {
      rs = preparedStatement.executeQuery();
    }
    catch (SQLException e) {
      throw getJDBCException(getSQLMessage(), e);
    }
    if (dataManager.isDebug())
    {
        timer.stop();
        logger.logDebug("query took " + timer.time() + " seconds");
    }
    return dataManager.getResultsNavigator(rs);
  }

  /**
   * First calls the bind method on the PreparedStatement with the provided
   * vector data and then executes the PreparedStatement as a query
   * @assumes nothing
   * @effects an sql ststement will be executed in the database
   * @param v the vector storing data to be bound. See the class description
   * for a list of acceptable data types.
   * @return a RowDataIterator of query results
   * @throws DBException
   */
  public ResultsNavigator executeQuery(Vector v) throws DBException {
    bind(v);
    return executeQuery();
  }

  /**
   * free up JDBC resources
   * @assumes nothing
   * @effects JDBC resources will be freed
   * @throws DBException thrown if there is an error with the database
   */
  public void close() throws DBException
  {
      try
      {
          if (preparedStatement != null)
              preparedStatement.close();
      }
      catch (SQLException e)
      {
          DBExceptionFactory eFactory = new DBExceptionFactory();
          DBException e2 = (DBException)
              eFactory.getException(CloseErr, e);
          throw e2;
      }
  }

  /**
   * get a DBException with message defined by ther name
   * ExceptionFactory.JDBCException
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

  /**
   * compose a message based on the sql string for this instance and the
   * bind variables and log the message to the logger obtained from the
   * SQLDataManager if it is not null
   * @assumes nothing
   * @effects a message will be logged to the log file
   */
  private void logDebugMessage() {
    logger.logDebug(getSQLMessage());
  }

  /**
   * compose a message based on the SQL statement and the bind variables for
   * this instance
   * @assumes nothing
   * @effects nothing
   * @return the composed string
   */
  private String getSQLMessage() {
    StringBuffer message = new StringBuffer();
    message.append("execute the following BindableStatement:\n");
    message.append(sql);
    int size = bindVariables.size();
    if (size > 0) {
      message.append("\nwith the following bind variables:\n");
      Object o = null;
      for (int i = 0; i < size; i++) {
        o = bindVariables.get(i);
        if (o == null)
          message.append("**NULL\n");
        else
          message.append(o.toString()+"\n");
      }
    }
    return message.toString();
  }



}
// $Log$
// Revision 1.7  2004/12/16 21:18:53  mbw
// merged assembly branch onto the trunk
//
// Revision 1.6.2.1  2004/12/02 19:27:00  mbw
// changed use of floats to doubles
//
// Revision 1.6  2004/10/22 18:54:53  mbw
// now checks DBDEBUG parameter to see if sql should be logged
//
// Revision 1.5  2004/07/26 16:38:49  mbw
// formatting only
//
// Revision 1.4  2004/07/21 19:19:30  mbw
// javadocs only
//
// Revision 1.3  2004/02/11 21:13:24  mbw
// added time stats to debug logging
//
// Revision 1.2  2004/02/10 14:54:39  mbw
// added close method
//
// Revision 1.1  2003/12/30 16:50:21  mbw
// imported into this product
//
// Revision 1.4  2003/12/09 22:48:51  mbw
// merged jsam branch onto the trunk
//
// Revision 1.3.2.14  2003/08/26 15:21:41  mbw
// added internal vector of bind variables with default initialization code
//
// Revision 1.3.2.13  2003/06/17 20:20:55  mbw
// cleaned out unused imports
//
// Revision 1.3.2.12  2003/06/04 18:29:54  mbw
// javadoc edits
//
// Revision 1.3.2.11  2003/06/04 14:51:48  mbw
// javadoc edits
//
// Revision 1.3.2.10  2003/06/03 21:58:05  mbw
// bug fix: added newline charactes to exception messages
//
// Revision 1.3.2.9  2003/06/02 16:26:01  mbw
// bug fix: no longer iterating through an empty vector of bind variables when composing error messages
//
// Revision 1.3.2.8  2003/05/22 15:56:19  mbw
// javadocs edits
//
// Revision 1.3.2.7  2003/05/16 15:11:21  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.3.2.6  2003/05/08 01:56:11  mbw
// incorporated changes from code review
//
// Revision 1.3.2.5  2003/04/25 17:11:59  mbw
// updated to reflect changes in design of exception handling
//
// Revision 1.3.2.4  2003/04/15 12:03:55  mbw
// added extended error messages and edits to javadocs
//
// Revision 1.3.2.3  2003/03/21 16:52:54  mbw
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
