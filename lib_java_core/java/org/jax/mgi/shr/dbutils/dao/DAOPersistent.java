package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.DBException;

/**
 * An interface for performing database inserts, updates and deletes of
 * DAOs (Data Access Objects)
 * @has nothing
 * @does nothing
 * @company The Jackson Laboratory
 * @author M Walker
 */
public interface DAOPersistent
{
    /**
     * delete the given DAO object from the database
     * @assumes nothing
     * @effects the dao object will be deleted
     * @param dao the object to delete
     * @throws DBException thrown if there is an error executing the delete
     */
    void delete(DAO dao)
        throws DBException;

    /**
     * update the given DAO object in the database
     * @assumes nothing
     * @effects the dao object will be updated
     * @param dao the object to update
     * @throws DBException thrown if there is an error executing the update
     */
    void update(DAO dao)
        throws DBException;

    /**
     * insert the given DAO object in the database
     * @assumes nothing
     * @effects the dao object will be inserted
     * @param dao the object to insert
     * @throws DBException thrown if there is an error executing the insert
     */
    void insert(DAO dao)
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
