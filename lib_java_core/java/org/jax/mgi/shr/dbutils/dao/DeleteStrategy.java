package org.jax.mgi.shr.dbutils.dao;

import org.jax.mgi.shr.dbutils.DBException;

/**
 * An interface which defines a method to delete the given DAO
 * object from the database. This could be implemented in a number of
 * different ways, including inline sql, batch JDBC or scripting
 * @has nothing
 * @does provides an interface for deleting dao objects
 * @author M Walker
 */
public interface DeleteStrategy
{
    /**
     * delete the given DAO object from the database
     * @assumes nothing
     * @effects the given DAO will be deleted from the database or
     * batched up to be deleted from the database, depending on the
     * specific implementation.
     * @param dao the object to delete
     * @throws DBException thrown if an exception occurs when executing
     * the delete
     */
    void delete(DAO dao)
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
