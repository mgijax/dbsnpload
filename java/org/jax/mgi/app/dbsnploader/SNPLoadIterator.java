package org.jax.mgi.app.dbsnploader;

import java.util.Vector;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.MultiRowInterpreter;
import org.jax.mgi.shr.dbutils.MultiRowIterator;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.RowReference;

import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dbutils.InterpretException;
import org.jax.mgi.shr.exception.MGIException;

/**
 * @is An object that knows how to query the MGI_SNP tables and
 *     interpret the results set to build SNP objects.
 * @has
 *   <UL>
 *   <LI> A RowDataIterator that will get the next IMAGEAssociation object each
 *        time the next() method is called.
 *   <LI> An interpreter object (inner class) that knows how to build
 *        IMAGEAssociation objects from the results set.
 *   </UL>
 * @does
 *   <UL>
 *   <LI> Provides a method to determine if there is another IMAGEAssociation
 *        object available.
 *   <LI> Provides a method to get the next available IMAGEAssociation object.
 *   </UL>
 * @company The Jackson Laboratory
 * @author dbm
 */

public class SNPLoadIterator{
    private DLALogger logger;
    private MultiRowIterator iterator;
    private String query;
    private Interpreter interpreter;

    public SNPLoadIterator (String chr) throws MGIException {
        // get an instance of a logger
        logger = DLALogger.getInstance();
        query = "";
        // Get an SQLDataManager for the RADAR database
        SQLDataManager sqlMgr =
            SQLDataManagerFactory.getShared(SchemaConstants.RADAR);
        ResultsNavigator resultsNav = sqlMgr.executeQuery(query);
        iterator = new MultiRowIterator(resultsNav, interpreter);
    }

    private class Interpreter implements MultiRowInterpreter {
        // a row of data
        private RowData rowData;

        /**
         * Create a RowData object from the given RowReference
         * @assumes Nothing
         * @effects Nothing
         * @param row the current RowReference
         * @return the Object we have interpreted from 'row'
         * @throws DBException if error getting columns for a row reference
         */

        public Object interpret(RowReference row) throws DBException {
            return new RowData(row);
        }

        /**
         * gets the object representing the key to a set of row references
         * @assumes Nothing
         * @effects Nothing
         * @param row the current RowReference
         * @return the key to the given RowReference
         * @throws DBException if error getting columns for a row reference
         */

        public Object interpretKey(RowReference row) throws DBException {
            // MGI_ConsensusSNP._ConsensusSNP_key
            return row.getInt(0);
        }

        /**
         * Build a Sequence object from a Vector of RowData objects
         * @assumes Nothing
         * @effects Nothing
         * @param v a Vector of RowData objects
         * @return a Sequence object, null if v is empty
         * @throws InterpretException if error creating the Sequence object
         */

        public Object interpretRows(Vector v) throws InterpretException {
            return new Object();
        }
    }
    /**
     * @is an object that represents a row of data from the query we are
     * interpreting
     * @has
     *   <UL>
     *   <LI> attributes representing each column selected in the query
     *   </UL>
     * @does
     *   <UL>
     *   <LI> assigns its attributes from a RowReference object
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

    private class RowData {
        /**
         * Constructs a RowData object from a RowReference
         * @assumes Nothing
         * @effects Nothing
         * @param row a RowReference
         * @throws DBException if error accessing RowReference methods
         */

        public RowData(RowReference row) throws DBException {

        }

    }

}