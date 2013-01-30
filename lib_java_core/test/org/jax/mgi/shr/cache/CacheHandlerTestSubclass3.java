package org.jax.mgi.shr.cache;

import java.util.HashMap;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.SQLDataManager;

/**
 * @is this class is used for testing purposes only. It is an example implementation
 * of the RowDataCacheHandler and is called by the junit test cases.
 * @has its own static cache which it uses in place of cache provided by the
 * base class.
 * @does provides a full implementation for a RowDataCacheHandler for testing
 * purposes
 * @company The Jackson Labatory
 * @author M Walker
 * @version 1.0
 */
public class CacheHandlerTestSubclass3
    extends RowDataCacheHandler
{
    private static HashMap cache = new HashMap();
    public CacheHandlerTestSubclass3(int cacheType, SQLDataManager sqlMgr)
        throws CacheException, DBException
    {
        super(cacheType, sqlMgr);
        initCache(cache);
    }

    public String getPartialInitQuery()
    {
        return "SELECT * FROM TEST_DBsimple WHERE ColumnA < 0";
    }

    public String getFullInitQuery()
    {
        return "SELECT * FROM TEST_DBsimple";
    }

    public RowDataInterpreter getRowDataInterpreter()
    {
        ThisInterpreter interpreter = new ThisInterpreter();
        return interpreter;
    }

    public String getAddQuery(Object addObject)
    {
        String sql = "SELECT * FROM TEST_DBsimple WHERE ColumnB = '" +
            addObject + "'";
        return sql;
    }

    public Integer lookup(String key)
        throws DBException, CacheException
    {
        Integer value = (Integer)
            super.cacheStrategy.lookup(key, cache);
        return value;
    }

    public class ThisInterpreter
        implements RowDataInterpreter
    {
        public Object interpret(RowReference ref)
            throws DBException
        {
            String key = ref.getString(2);
            Integer value = ref.getInt(1);
            KeyValue keyValue =
                new KeyValue(key, value);
            return keyValue;
        }
    }
}
