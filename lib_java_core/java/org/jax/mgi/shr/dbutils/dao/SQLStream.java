package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * An abstract class for performing database inserts, updates and deletes
 * using strategy classes which can be plugged in to handle the particular
 * database operations
 * @has an InsertStrategy, UpdateStrategy and a DeleteStrategy for performing
 * the database operations
 * @does routes the DAO object to the correct strategy
 * @company The Jackson Laboratory
 * @author M Walker
 */

public abstract class SQLStream implements DAOPersistent
{

  protected InsertStrategy insertStrategy = null;
  protected UpdateStrategy updateStrategy = null;
  protected DeleteStrategy deleteStrategy = null;

  protected void setInsertStrategy(InsertStrategy insertStrategy)
  {
    this.insertStrategy = insertStrategy;
  }

  protected void setUpdateStrategy(UpdateStrategy updateStrategy)
  {
    this.updateStrategy = updateStrategy;
  }

  protected void setDeleteStrategy(DeleteStrategy deleteStrategy)
  {
    this.deleteStrategy = deleteStrategy;
  }

  public boolean isBCP()
  {
    if (this.insertStrategy instanceof BCPStrategy)
      return true;
    else
      return false;
  }

  /**
   * delete the given DAO object from the database
   * @assumes nothing
   * @effects the given DAO will be deleted from the database
   * or batched up to be deleted from the database, depending on the specific
   * implementation
   * @param dao the object to delete
   * @throws DBException thrown if there is an error executing the delete
   */
  public void delete(DAO dao) throws DBException
  {
      if (this.deleteStrategy != null)
          this.deleteStrategy.delete(dao);
      else
          throw MGIException.getUnsupportedMethodException();
  }

  /**
   * update the given DAO object in the database
   * @assumes nothing
   * @effects the given DAO will be updated in the database
   * or batched up to be updated in the database, depending on the specific
   * implementation
   * @param dao the object to update
   * @throws DBException thrown if there is an error accessing the database
   */
  public void update(DAO dao) throws DBException
  {
      if (this.updateStrategy != null)
          this.updateStrategy.update(dao);
      else
          throw MGIException.getUnsupportedMethodException();
  }

  /**
   * insert the given DAO object in the database
   * @assumes nothing
   * @effects the given DAO will be inserted into the database
   * or batched up to be inserted into the database, depending on the
   * specific implementation
   * @param dao the object to insert
   * @throws DBException thrown if there is an error executing the insert
   */
  public void insert(DAO dao) throws DBException
  {
      if (this.insertStrategy != null)
          this.insertStrategy.insert(dao);
      else // not supported by parent class
          throw MGIException.getUnsupportedMethodException();
  }

  /**
   * abstract method to be implemented by subclasses to handle the execution
   * of any batch SQL in an implementation specific manner
   * @assumes nothing
   * @effects any outstanding batch statements will be executed
   * @throws DBException thrown if there is an error trying to execute any
   * batch statements
   */
  public abstract void close() throws DBException;
}

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

