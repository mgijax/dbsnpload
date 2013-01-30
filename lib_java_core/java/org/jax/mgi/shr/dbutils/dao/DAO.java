package org.jax.mgi.shr.dbutils.dao;

import java.util.Vector;
import org.jax.mgi.shr.dbutils.bcp.BCPTranslatable;
import org.jax.mgi.shr.dbutils.Table;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.bcp.BCPException;

/**
 * An abstract class which represents a record in the database. It
 * implements the BCPTranslatable and SQLTranslatable interfaces which
 * requires all subclasses to support inserts, updates and deletes through
 * both bcp and sql.
 * @has a couple of indicators to indicate whether or not the record exists
 * in the database and whether the record has been batched for update
 * @does provides accessor to the in-database and in-batch indicators
 * @abstract this abstract class implements the accessors for the in-database
 * and batched indicators. The subclass is responsible for implementing
 * the BCPTranslatable and SQLTranslatable interfaces.
 * @company The Jackson Laboratory
 * @author M Walker
 */
abstract public class DAO
    implements BCPTranslatable, SQLTranslatable
{
    /**
     * the flag which indicates whether or not this record exists in the
     * database
     */
    private boolean inDatabase;

    /**
     * the flag which indicates whether or not this record has been bathced
     * for update into the database
     */
    private boolean inBatch;

    /**
     * get the vector which represents column values for a row or a vector of
     * vectors for multiple rows which is called by a BCPWriter when writing
     * to a bcp file
     * @assumes nothing
     * @effects nothing
     * @param table the target Table object which is being bcped into
     * @return the vector of column values used for writing a line to a bcp
     * file or a vector of vectors if multiple lines are to be written to
     * the bcp file.
     * @throws BCPException if there is an error obtaining the bcp vector
     */
    public abstract Vector getBCPVector(Table table)
        throws BCPException;

    /**
     * get the list of supported tables when inserting via bcp. BCPStrategy
     * class will use this method within the insert(DAO) method to see which
     * tables need to be bcp into for this DAO instance.
     * @assumes nothing
     * @effects nothing
     * @return the list of supported tables for bcping
     */
    public abstract Vector getBCPSupportedTables();

    /**
     * get the sql statement for performing an update into the database
     * @assumes nothing
     * @effects nothing
     * @return the sql statement for performing an update into the database
     * @throws DBException thrown if there is an error accessing the database
     */
    public abstract String getUpdateSQL()
        throws DBException;

    /**
     * get the sql statement for performing a delete from the database
     * @assumes nothing
     * @effects nothing
     * @return the sql statement for performing a delete from the database
     * @throws DBException thrown if there is an error accessing the database
     */
    public abstract String getDeleteSQL()
        throws DBException;

    /**
     * get the sql string for performing an insert into the database
     * @assumes nothing
     * @effects nothing
     * @return the sql string for performing an insert into the database
     * @throws DBException thrown if there is an error accessing the database
     */
    public abstract String getInsertSQL()
        throws DBException;

    /**
     * return whether the record has been batched for insert into the
     * database
     * @assumes nothing
     * @effects nothing
     * @return true if the record has been batched for update in the database
     * or false otherwise
     */
    public boolean inBatch()
    {
        return inBatch;
    }

    /**
     * return whether the record exists or not in the database
     * @assumes nothing
     * @effects nothing
     * @return true if the record exists in the database or false otherwise
     */
    public boolean inDatabase()
    {
        return inDatabase;
    }

    /**
     * set whether or not this record has been batched for insert into the
     * database
     * @assumes nothing
     * @effects nothing
     * @param bool true if the record exists in the database, false otherwise
     */
    public void setInBatch(boolean bool)
    {
        inBatch = bool;
    }

    /**
     * set whether or not this record exists in the database
     * @assumes nothing
     * @effects nothing
     * @param bool true if this record has been batched for insert into the
     * database, false otherwise
     */
    public void setInDatabase(boolean bool)
    {
        inDatabase = bool;
    }
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
