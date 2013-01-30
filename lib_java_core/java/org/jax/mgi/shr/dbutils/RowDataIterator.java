package org.jax.mgi.shr.dbutils;

import java.util.Vector;

/**
 * An object which iterates in a forward direction through a given set
 * of query results and returns java objects based on the data from each row
 * @has a ResultsNavigator for parsing through the query results and a
 * RowDataInterpreter for creating objects based on multiple rows
 * @does it acts as a proxy to the ResultsNavigator class to allow the
 * ResultsNavigator to be used through the DataIterator interface.
 * @author M Walker
 */

public class RowDataIterator implements DataIterator
{
  /**
   * the ResultsNavigator used for iterating through the query results
   */
  private ResultsNavigator nav = null;

  /**
   * a flag to indicate whether or not the ResultsNavigator has reached
   * the end of its results
   */
  private boolean done = false;
  /**
   * the cached object
   */
  private Object cacheObject = null;

  /*
   * the following constant definitions are exceptions thrown by this class
   */
  private static String PastEndOfResultSet =
      DBExceptionFactory.PastEndOfResultSet;


  /**
   * the constructor
   * @assumes nothing
   * @effects nothing
   * @param nav the query results through which to iterate
   * @param interpreter the MultiRowDataInterpreter used for creating java
   * data objects based on the rows of query results
   * @throws DBException thrown if there is an error within the database
   */
  public RowDataIterator(ResultsNavigator nav,
                         RowDataInterpreter interpreter)
  throws DBException
  {
    this.nav = nav;
    nav.setInterpreter(interpreter);
    if (!nav.next())
      done = true;
  }

  /**
   * return an indicator of whether or not there are any more results to
   * process
   * @assumes nothing
   * @effects nothing
   * @return true if thjere are more results to process, false otherwise
   */
  public boolean hasNext()
  {
      return !done;
  }

  /**
   * close the ResultsNavigator
   * @assumes nothing
   * @effects the ResultsNavigator will be closed
   * @throws DBException
   */
  public void close() throws DBException
  {
    nav.close();
  }

  /**
   * returns a java data object interpreted from the next row
   * @assumes nothing
   * @effects nothing
   * @return the created java data object
   * @throws DBException thrown if there is an error within the database
   */
  public Object next() throws DBException
  {
      /**
       * the object that will get created based on the rows
       */
      Object nextObject = null;

      if (done) // there are no more rows left in the ResultsNavigator
      {
          DBExceptionFactory factory = new DBExceptionFactory();
          DBException e =
              (DBException)factory.getException(PastEndOfResultSet);
          throw e;
      }
      nextObject = nav.getCurrent();
      if (!nav.next())
          done = true;

      return nextObject;
  }
}
