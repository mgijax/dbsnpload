package org.jax.mgi.shr.unitTest;

import java.util.Vector;
import java.util.HashMap;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.ResultsNavigator;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.Table;
import org.jax.mgi.shr.dbutils.DBSchema;
import org.jax.mgi.shr.dbutils.dao.BCP_Stream;
import org.jax.mgi.shr.dbutils.dao.Inline_Stream;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.dao.DAO;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;

/**
 * Is a class which handles tasks associated with setting up a test data,
 * including staging rows of data in the database, and controlling the runtime
 * configuration.
 * @has
 * @does
 * @company The Jackson Laboratory
 * @author not attributable
 *
 */

public class TestManager
{
    private SQLDataManager sqlMgr = null;
    private BCPManager bcpMgr = null;
    private SQLStream stageStream = null;
    private SQLStream cleanStream = null;
    private Vector stagedData = null;
    private DBSchema schema = null;
    private Properties properties = System.getProperties();
    private HashMap changedProperties = null;
    private HashMap newProperties = null;

    public TestManager() throws MGIException
    {
        sqlMgr = new SQLDataManager();
        bcpMgr = new BCPManager();
        this.schema = new DBSchema(sqlMgr);
        this.stageStream = new BCP_Stream(sqlMgr, bcpMgr);
        this.cleanStream = new Inline_Stream(sqlMgr);
        this.stagedData = new Vector();
        this.changedProperties = new HashMap();
        this.newProperties = new HashMap();
    }

    public void stageData(DAO dao) throws MGIException
    {
        stagedData.add(dao);
        stageStream.insert(dao);
    }

    public void commitStage() throws MGIException
    {
        this.stageStream.close();
    }

    public void cleanStage() throws MGIException
    {
        for (int i = 0; i < stagedData.size(); i++)
            this.cleanStream.delete((DAO)stagedData.get(i));
        this.cleanStream.close();
    }

    public void deleteObject(DAO dao) throws MGIException
    {
        this.cleanStream.delete(dao);
    }

    public void setConfig(String name, String value)
    {
        String oldValue = (String)this.properties.get(name);
        this.properties.put(name, value);
        if (oldValue != null)
            this.changedProperties.put(name, oldValue);
        else
            this.newProperties.put(name, null);
    }

    public void cleanConfig()
    {
        Iterator it = this.newProperties.keySet().iterator();
        while (it.hasNext())
            this.properties.remove(it.next());
        it = this.changedProperties.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            this.properties.put(entry.getKey(), entry.getValue());
        }
    }

    public void cleanAll() throws MGIException
    {
        this.cleanStage();
        this.cleanConfig();
    }

    public int countObjects(String tablename) throws MGIException
    {
        String sql = "select * from " + tablename;
        ResultsNavigator nav = sqlMgr.executeQuery(sql);
        int cnt = 0;
        while (nav.next())
        {
            RowReference row = nav.getRowReference();
            cnt++;
        }
        nav.close();
        return cnt;
    }

    public void resetKey(String tablename) throws MGIException
    {
        Table table = Table.getInstance(tablename);
        table.resetKey();
    }

    public void truncateTable(String tablename) throws MGIException
    {
        if (this.sqlMgr.getDatabase().indexOf("test") == -1)
            throw new MGIException("Database name must contain the string 'test' in order to perform table truncation");

        this.schema.truncateTable(tablename);
    }
}