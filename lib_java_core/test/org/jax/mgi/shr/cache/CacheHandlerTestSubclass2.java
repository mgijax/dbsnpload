package org.jax.mgi.shr.cache;

import java.util.Vector;

import org.jax.mgi.shr.dbutils.MultiRowInterpreter;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManager;


/**
 * @is a class used for testing purposes only. It is an example
 * implementation of a RowDataCacheHandler and is called by the junit test
 * TestRowDataCacheHandler.
 * @has its own static cache which it uses in place of cache provided by the
 * base class and a RowDataInterpreter for interpreting results from a
 * query.
 * @does provides a full implementation for a RowDataCacheHandler for testing
 * purposes
 * @company The Jackson Labatory
 * @author M Walker
 * @version 1.0
 */
public class CacheHandlerTestSubclass2
    extends FullCachedLookup
{
    public CacheHandlerTestSubclass2(int cacheType, SQLDataManager sqlMgr)
        throws CacheException, DBException
    {
        super(sqlMgr);
    }

    public String getFullInitQuery()
    {
        return "SELECT * FROM TEST_DBsimple order by columnA";
    }

    public RowDataInterpreter getRowDataInterpreter()
    {
        ThisInterpreter interpreter = new ThisInterpreter();
        return interpreter;
    }

    public String getAddQuery(Object addObject)
    {
        String sql = "SELECT * FROM TEST_DBsimple WHERE ColumnA = " +
            addObject;
        return sql;
    }

    public String lookup(int key)
        throws DBException, CacheException
    {
        String value = (String)
            super.cacheStrategy.lookup(new Integer(key), cache);
        return value;
    }

    public class ThisInterpreter
        implements MultiRowInterpreter
    {
        public Object interpret(RowReference ref)
            throws DBException
        {
            Integer key = ref.getInt(1);
            String value = ref.getString(2);
            KeyValue keyValue =
                new KeyValue(key, value);
            return keyValue;
        }
        public Object interpretRows(Vector v)
        {
            KeyValue keyValue = (KeyValue)v.get(0);
            Integer key = (Integer)keyValue.getKey();
            String value = (String)keyValue.getValue();
            for (int i = 1; i < v.size(); i++)
            {
                keyValue = (KeyValue)v.get(i);
                value = value.concat(":" + keyValue.getValue());
            }
            return new KeyValue(key, value);
        }
        public Object interpretKey(RowReference row) throws DBException
        {
            return row.getInt(1);
        }
    }
}
