package org.jax.mgi.shr.dbutils.bcp;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.log.Logger;

/**
 * This interface is used for importing data from a file into a database and
 * is implemented for various database products including MySQL and Sybase
 * @has nothing
 * @does imports data from a file into a database
 * @company The Jackson Laboratory
 * @author MWalker
 *
 */


public interface FileImporter
{
    /**
     * import the file into the database
     * @param filename the name of the file
     * @param tablename the name of the table
     * @param delimiter the delimieter to use
     * @param sqlMgr the SQLDataManager
     * @param logger the Logger
     * @throws BCPException thrown if there is an error importing
     */
    public void importFile(String filename,
                           String tablename,
                           String delimiter,
                           SQLDataManager sqlMgr,
                           Logger logger) throws BCPException;
}