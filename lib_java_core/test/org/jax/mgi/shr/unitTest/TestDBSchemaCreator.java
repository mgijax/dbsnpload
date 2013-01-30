package org.jax.mgi.shr.unitTest;

import junit.framework.*;

import java.io.File;

import org.jax.mgi.shr.dbutils.*;
import org.jax.mgi.dbs.SchemaConstants;

public class TestDBSchemaCreator
    extends TestCase {
    private DBSchemaCreator dBSchemaCreator = null;

    public TestDBSchemaCreator(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        dBSchemaCreator = null;
        super.tearDown();
    }

    public void testCreateTrigger() throws Exception {
        int testType = 1;
        SQLDataManager sqlMgr =
            SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        String dstFileCreate = sqlMgr.getDBSchemaDir() + File.separator +
            "trigger" + File.separator + "TEST_DBtrigger_create.object";
        String dstFileDrop = sqlMgr.getDBSchemaDir() + File.separator +
            "trigger" + File.separator + "TEST_DBtrigger_drop.object";
        File file1 = new File(dstFileCreate);
        File file2 = new File(dstFileDrop);
        assertTrue(!file1.exists());
        assertTrue(!file2.exists());
        DBSchemaCreator.createTestTrigger(testType);
        assertTrue(file1.exists());
        assertTrue(file2.exists());
        DBSchemaCreator.deleteTestTrigger();
        assertTrue(!file1.exists());
        assertTrue(!file2.exists());
    }

}
