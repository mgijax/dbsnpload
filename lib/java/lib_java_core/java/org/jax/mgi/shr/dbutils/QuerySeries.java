package org.jax.mgi.shr.dbutils;

import java.util.*;

/**
 * Is an object which stores multiple queries
 * @has a series of sql statements
 * @does provides an iterator over the execution of the series
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class QuerySeries
{

    private ArrayList sqlStatements = null;
    private Iterator i = null;
    private SQLDataManager sqlMgr = null;
    private ResultsNavigator closeNav = null;


    /**
     * constructor
     * @assumes nothing
     * @effects new object created
     * @param sqlStatements a collection of sql statements as strings
     * @param sqlMgr the SQLDataManager to use for querying
     */

    protected QuerySeries(ArrayList sqlStatements,
                          SQLDataManager sqlMgr)
    {
        this.sqlStatements = sqlStatements;
        this.i = sqlStatements.iterator();
        this.sqlMgr = sqlMgr;
    }

    /**
     * gets the count of statements
     * @assumes nothing
     * @effects nothing
     * @return count of statements
     */
    public int queryCount()
    {
        return sqlStatements.size();
    }

    /**
     * get whether or not there are more statements to execute
     * @assumes nothing
     * @effects nothing
     * @return true if there are more statements to execute, false otherwise
     */
    public boolean hasNext()
    {
        return i.hasNext();
    }

    /**
     * execute the next query in the series
     * @assumes nothing
     * @effects nothing
     * @return a ResultsNavigator for the results from the next query
     */
    public ResultsNavigator executeNextQuery()
    throws DBException
    {
        if (closeNav != null)
        {
            try
            {
                closeNav.close();
            }
            catch (Exception e) {} // do nothing if already closed
        }
        String sql = (String)i.next();
        closeNav = sqlMgr.executeQuery(sql);
        return closeNav;
    }

}
