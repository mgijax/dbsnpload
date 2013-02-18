package org.jax.mgi.shr.dbutils.dao;

/**
 * A SQLStream which can execute sql in a transactional way.
 * @has nothing.
 * @does provides a commit and rollback method and the sql statements for
 * insert, update and delete.
 * @company Jackson Laboratory
 * @author M. Walker
 */
public abstract class TransactionalSQLStream
    extends SQLStream
{
    /**
     * set whether or not to use transactions when executing sql.
     * @assumes nothing
     * @effects subsequent execution of sql will be either transactional or
     * not depending upon the given boolean value
     * @param bool true if the sql should be executing using transaction
     * management, false otherwise
     */
    public abstract void setTransactionOn(boolean bool);

    /**
     * commit the current transaction
     * @assumes nothing
     * @effects all outstanding transactional sql will be commited to the
     * database
     */
    public abstract void commit();

    /**
     * rollback the current transaction
     * @assumes nothing
     * @effects the current transaction will be roolbacked and a new
     * transaction
     * will be started if the class has transactions on.
     */
    public abstract void rollback();
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
