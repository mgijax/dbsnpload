package org.jax.mgi.shr.dbutils;

import junit.framework.TestCase;
import org.jax.mgi.shr.unitTest.*;
import org.jax.mgi.shr.log.*;

public class TestBatchProcessor
    extends TestCase
{
    private SQLDataManager sqlMgr = null;

    private TableCreator tc = null;

    public TestBatchProcessor(String arg0)
    {
        super(arg0);
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        sqlMgr = new SQLDataManager();
        tc = new TableCreator(sqlMgr.getUrl(), sqlMgr.getDatabase(),
                              sqlMgr.getUser(), sqlMgr.getPassword(),
                              sqlMgr.getConnectionManagerClass());
        tc.createDBsimple();
    }

    protected void tearDown()
        throws Exception
    {
        sqlMgr.closeResources();
        sqlMgr = null;
        tc.dropDBsimple();
        tc = null;
        super.tearDown();
    }

    public void testExecuteBatch()
        throws Exception
    {
        BatchProcessor bp = sqlMgr.getBatchProcessor();
        //bp.setLogger(new ConsoleLogger());
        bp.addBatch("insert into TEST_DBsimple values (1, 'uno')");
        bp.executeBatch();
        ResultsNavigator nav = sqlMgr.executeQuery(
            "select * from TEST_DBsimple");
        nav.next();
        RowReference row = (RowReference) nav.getCurrent();
        assertEquals(row.getInt(1).intValue(), 1);
        assertEquals(row.getString(2), "uno");
    }

    public void testRollback()
        throws Exception
    {
        BatchProcessor bp = sqlMgr.getBatchProcessor();
        //bp.setLogger(new ConsoleLogger());
        bp.addBatch("insert into TEST_DBsimple values (1, 'uno')");
        // force error
        bp.addBatch("bad sql");
        bp.addBatch("insert into TEST_DBsimple values (2, 'deuce')");
        bp.addBatch("update TEST_DBsimple set columnB = 'wildcard'");
        try
        {
            bp.executeBatch();
        }
        catch (BatchException e)
        {
            int[] results = e.getUpdateCounts();
            assertTrue(true);
            assertEquals(new Integer(results.length), new Integer(0));
        }
        // sybase does not support a partial commit ... i.e., all is rolledback
        // check to assure the first statement was not commited
        ResultsNavigator nav = sqlMgr.executeQuery(
            "select count(*) from TEST_DBsimple");
        nav.next();
        RowReference row = nav.getRowReference();
        assertEquals(row.getInt(1).intValue(), 0);
    }

    public void testClose()
        throws Exception
    {
        BatchProcessor bp = sqlMgr.getBatchProcessor();
        bp.close();
        try
        {
            bp.addBatch("");
            //expect exception block to take over
            assertTrue(false);
        }
        catch (BatchException e)
        {
            // this is expected
            assertTrue(true);
        }
    }
}