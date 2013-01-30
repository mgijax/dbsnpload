package org.jax.mgi.shr.dbutils;

import junit.framework.*;

public class AllTests
    extends TestCase {

  public AllTests(String s) {
    super(s);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestBCPManager1.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestBCPWriter.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestBindableStatement.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestDBSchema.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestMultipleResults.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestSQLDataManager.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestTable.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestBatchProcessor.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestMultiRowIterator.class);
    suite.addTestSuite(org.jax.mgi.shr.dbutils.TestRecordStamp.class);
    return suite;
  }
}
