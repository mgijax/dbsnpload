package org.jax.mgi.shr.cache;

import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.SQLDataManager;

/**
 * A FullCachedLookup implementation which allows the sql and
 * RowDataInterpreter to be defined at runtime and passed in by way of the
 * constructor
 * @has see javadocs for <a href="FullCachedLookup.html">FullCachedLookup</a>
 * @does see javadocs for <a href="FullCachedLookup.html">FullCachedLookup</a>
 * @company The Jackson Laboratory
 * @author MWalker
 */


public class FullCachedLookupImpl extends FullCachedLookup
{
    /**
     * the full initialization query provided through the constructor
     */
    protected String fullInitQuery = null;
    /**
     * the RowDataInterpreter provided through the constructor
     */
    protected RowDataInterpreter interpreter = null;


    public FullCachedLookupImpl(SQLDataManager sqlMgr,
                                String fullInitQuery,
                                RowDataInterpreter interpreter)
    throws CacheException
    {
        super(sqlMgr);
        this.interpreter = interpreter;
        this.fullInitQuery = fullInitQuery;
    }
    public String getFullInitQuery()
    {
        return this.fullInitQuery;
    }
    public RowDataInterpreter getRowDataInterpreter()
    {
        return this.interpreter;
    }
}