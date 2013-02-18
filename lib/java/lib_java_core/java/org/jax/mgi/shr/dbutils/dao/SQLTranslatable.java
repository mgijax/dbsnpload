package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.DBException;

/**
 *  An object which can translate it's internal contents into sql
 * statements for insert, update and delete.
 * @has nothing.
 * @does provides sql statements for insert, update and delete.
 * @company Jackson Laboratory
 * @author M. Walker
 */
public interface SQLTranslatable
{
    /**
     * get the sql for update
     * @assumes nothing
     * @effects nothing
     * @return the update sql string
     * @throws DBException thrown if there is an error accessing the database
     */
    String getUpdateSQL()
        throws DBException;

    /**
     * get the sql for delete
     * @assumes nothing
     * @effects nothing
     * @return the delete sql string
     * @throws DBException thrown if there is an error accessing the database
     */
    String getDeleteSQL()
        throws DBException;

    /**
     * get the sql for insert
     * @assumes nothing
     * @effects nothing
     * @return the insert sql string
     * @throws DBException thrown if there is an error accessing the database
     */
    String getInsertSQL()
        throws DBException;
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
