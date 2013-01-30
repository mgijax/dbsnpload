package org.jax.mgi.shr.unitTest;

import java.io.File;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.dbs.SchemaConstants;

/**
 * A class for creating named files within the DBSchema product for
 * supporting unit testing of the DBSchema and other related classes
 * @has static methods which create and remove files test files with the DBSchema
 * product
 * @does provides static methods
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public class DBSchemaCreator {

    public static final int PROBLEM_QUOTE = 1;
    public static final int CLEAN = 2;

    public DBSchemaCreator() {
    }

    /**
     * creates a trigger which contains faulty text to use for testing error
     * handling. This will create a file within the trigger subdirectory of
     * the DBSchema product designated by the config parameter DBSCHEMADIR.
     * This method also creates a dummy table called TEST_DBtrigger which is
     * refered to within the trigger code. The table is deleted on the call to
     * deleteTestTrigger. Only one test trigger can exist at one
     * time and there are a number of different test types you can create.
     * Test types are determined by the testType argument. Currently the test
     * types  include DBSchemaCreator.PROBLEM_QUOTE for creating a trigger with
     * problem quotation marks. The other type is DBSchemaCreator.CLEAN
     * which should execute cleanly through JDBC. Each test type is created
     * using the same trigger name, so they created all at once but should
     * be created, tested, and removed for each test case. When performing
     * muliple test cases, avoid calling this method during setup() unless
     * it is the only test trigger you plan on creating for all test cases.
     * @param testType the test type to create for this test trigger
     * @throws Exception
     */
    public static void createTestTrigger(int testType)
    throws Exception
    {
        SQLDataManager sqlMgr =
            SQLDataManagerFactory.getShared(SchemaConstants.MGD);
        TableCreator tc = new TableCreator(sqlMgr.getUrl(),
                                           sqlMgr.getDatabase(),
                                           sqlMgr.getUser(),
                                           sqlMgr.getPassword(),
                                           sqlMgr.getConnectionManagerClass());
        tc.createTriggerCompanion();
        String srcFileDrop =
            new String("test" + File.separator +
                           "TEST_DBtrigger_drop.object");
        String srcFileCreate = null;
        if (testType == DBSchemaCreator.PROBLEM_QUOTE)
        {
            srcFileCreate =
                new String("test" + File.separator +
                           "TEST_DBtrigger_create.object.nestedQuote");
        }
        else if (testType == DBSchemaCreator.CLEAN)
        {
            srcFileCreate =
                new String("test" + File.separator +
                           "TEST_DBtrigger_create.object.clean");
        }
        else
        {
            throw new MGIException("unknown fault type for argument to the " +
                                   "createFaultyTrigger() method");
        }
        String dstFileCreate = sqlMgr.getDBSchemaDir() + File.separator +
            "trigger" + File.separator + "TEST_DBtrigger_create.object";
        String dstFileDrop = sqlMgr.getDBSchemaDir() + File.separator +
            "trigger" + File.separator + "TEST_DBtrigger_drop.object";
        FileUtility.copy(srcFileCreate, dstFileCreate);
        FileUtility.copy(srcFileDrop, dstFileDrop);
    }



    public static void deleteTestTrigger()
        throws Exception
    {
        SQLDataManager sqlMgr = new SQLDataManager();
        String dstFileCreate = sqlMgr.getDBSchemaDir() + File.separator +
            "trigger" + File.separator + "TEST_DBtrigger_create.object";
        String dstFileDrop = sqlMgr.getDBSchemaDir() + File.separator +
            "trigger" + File.separator + "TEST_DBtrigger_drop.object";
        FileUtility.delete(dstFileCreate);
        FileUtility.delete(dstFileDrop);
    }

}