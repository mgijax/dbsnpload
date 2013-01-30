package org.jax.mgi.shr.dbutils;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An interface for iterating over abstract database
 * @has nothing
 * @does provides an iteration pattern
 * @company The Jackson Laboratory
 * @author M Walker
 */

public interface DataIterator
{
  /**
   * get the next object from the results
   * @return the next object from the query results
   * @throws MGIException thrown if there is an error accessing the source
   * data
   */
   public Object next() throws MGIException;

   /**
    * close the results
    * @throws MGIException thrown if there is an error accessing the source
    * data
    */
   public void close() throws MGIException;

   /**
    * see if there is another object to be retrieved from the results
    * @return true if there is another object to be retrieved from the
    * results, false otherwise
    */
   public boolean hasNext();

}