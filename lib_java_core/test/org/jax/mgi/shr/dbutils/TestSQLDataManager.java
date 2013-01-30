package org.jax.mgi.shr.dbutils;

import java.util.ArrayList;
import java.util.Iterator;
import junit.framework.*;
import org.jax.mgi.shr.unitTest.*;

public class TestSQLDataManager
    extends TestCase {
  private SQLDataManager sqlman = null;
  private TableCreator tableCreator = null;
  private FileUtility fileUtility = new FileUtility();
  private String originalConfigValue = null;
  private String query = "SELECT * FROM TEST_DBtypes";
  private String insert = "INSERT INTO TEST_DBtypes " +
               "VALUES ('column', '2003/07/04', 1, null, 'some text', " +
               "1.111, 0)";
  private TestManager testMgr = null;


  public TestSQLDataManager(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    sqlman = new SQLDataManager();
    testMgr = new TestManager();
    //testMgr.setConfig("DBDEBUG", "true");
    //testMgr.setConfig("LOG_DEBUG", "true");
    tableCreator = new TableCreator(sqlman.getUrl(),
                                    sqlman.getDatabase(),
                                    sqlman.getUser(),
                                    sqlman.getPassword(),
                                    sqlman.getConnectionManagerClass());

    tableCreator.createDBtypes();
  }

  protected void tearDown() throws Exception {
      sqlman.closeResources();
    sqlman = null;
    tableCreator.dropDBtypes();
    tableCreator = null;
    testMgr = null;
    super.tearDown();
  }


  public void testRowDataIterator1() throws Exception {
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference o = i.getRowReference();
    // read to end of iterator
    while (i.next()) {
      //Object o = i.getCurrent();
    }
    // now try and read past end of iterator
    try {
      //RowReference o = (RowReference)i.getCurrent();
      String s = o.getString(1);
    }
    catch (Exception e) {
      // this is what we would expect
      assertTrue(true);
      return;
    }
    // if we get here then the test has failed, assert false
    assertTrue(false);
  }


  public void testRowDataIterator2() throws Exception {
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference row = i.getRowReference();
    // read to end of iterator
    while (i.next()) {
      //Object o = i.getCurrent();
    }
    // now check if we are past end
    assertTrue(!i.next());
  }

  public void testRowDataIterator3() throws Exception {
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference o = i.getRowReference();
    //RowReference o = (RowReference)i.getCurrent();
    String s = null;
    try {
      s = o.getString(1); // should fail
    }
    catch (Exception e) {
      assertTrue(true);
      return;
    }
    // we should not get here
    assertTrue(false);
  }

  public void testRowDataIterator4() throws Exception {
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    String query = "SELECT * FROM TEST_DBtypes WHERE COLUMNA = 'NO MATCH'";
    // should return no records
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference o = i.getRowReference();
    assertTrue(!i.next());
    // next try and access row
    try { // this should fail
      //RowReference o = (RowReference)i.getCurrent();
      String s = o.getString(1); // should fail
      if (s == null)
      {
          assertTrue(true);
          return;
      }
    }
    catch (Exception e) {
      assertTrue(true);
      return;
    }
    // should not get here
    assertTrue(false);
  }

  public void testRowDataIterator5() throws Exception {
    sqlman.executeUpdate(insert);
    String query = "SELECT * FROM TEST_DBtypes";
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference rp = i.getRowReference();
    i.next();
    //RowReference rp = (RowReference)i.getCurrent();
    try {
      String s = (String)rp.getString(1);
    }
    catch (Exception e) {
      // failure unexpected
      assertTrue(false);
      return;
    }
    // expect to get here
    assertTrue(true);
  }

  public void testRowDataIterator6() throws Exception {
    sqlman.executeUpdate(insert);
    String query = "SELECT * FROM TEST_DBtypes";
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference rp = i.getRowReference();
    i.next();
    //RowReference rp = (RowReference)i.getCurrent();
    boolean multipleRows = i.next();
    try {
      String s = (String)rp.getString(1);
    }
    catch (Exception e) {
      // failure expected
      assertTrue(true);
      return;
    }
    // expect not to get here
    assertTrue(false);
  }

  public void testRowDataIterator7() throws Exception {
    sqlman.setScrollable(true);
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference o = i.getRowReference();
    // read to end of iterator
    while (i.next()) {
      //Object o = i.getCurrent();
    }
    i.previous();
    i.previous();
    // now check if we are past end
    assertTrue(i.next());
  }

  public void testRowDataIterator8() throws Exception {
    sqlman.setScrollable(false);
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    sqlman.executeUpdate(insert);
    ResultsNavigator i = sqlman.executeQuery(query);
    RowReference o = i.getRowReference();
    // read to end of iterator
    while (i.next()) {
      //Object o = i.getCurrent();
    }
    try {
      i.previous(); // should fail since results are not scrollable
    }
    catch (Exception e) {
      assertTrue(true);
      return;
    }
    // should not get here
    assertTrue(false);
  }



  public void testExecuteSimpleProc() throws Exception {
    SQLDataManager mgr =
        new SQLDataManager(null, "tempdb", null, null, null, null);
    try {
      mgr.executeUpdate("drop procedure temp_proc");
      mgr.executeUpdate("drop table temp_table");
    }
    catch (Exception e) {} // probably due to objects not existing; ignore
    String createTable =
        "CREATE TABLE temp_table (" +
        "columnA int not null, columnB varchar(30) not null)";
    String insert1 =
        "INSERT INTO temp_table VALUES (1, 'one')";
    String insert2 =
        "INSERT INTO temp_table VALUES (2, 'two')";
    String createProc =
        "CREATE PROCEDURE temp_proc AS\n" +
        "SELECT * FROM temp_table\n" +
        "return 5";
    mgr.executeUpdate(createTable);
    mgr.executeUpdate(insert1);
    mgr.executeUpdate(insert2);
    mgr.executeUpdate(createProc);
    int rtn = mgr.executeSimpleProc("temp_proc");
    mgr.executeUpdate("drop procedure temp_proc");
    mgr.executeUpdate("drop table temp_table");
    mgr.closeResources();
    assertEquals(rtn, 5);
  }


  public void testRowRef() throws Exception
  {
    sqlman.executeUpdate(
        "INSERT INTO TEST_DBtypes " +
        "VALUES ('A', '2003/07/04', 1, null, 'some text', " +
        "1.111, 0)");
    sqlman.executeUpdate(
        "INSERT INTO TEST_DBtypes " +
        "VALUES ('B', '2003/07/04', 0, null, 'some text', " +
        "1.111, 0)");
    sqlman.setScrollable(true);
    ResultsNavigator nav = sqlman.executeQuery("select * from test_dbtypes");
    RowReference row = nav.getRowReference();
    while (nav.next()) {}
    nav.previous();
    String s = row.getString(1);
    assertEquals(s, "B");
  }

  public void testInClauseQuery() throws Exception
  {
      sqlman.executeUpdate(
          "INSERT INTO TEST_DBtypes " +
          "VALUES ('A', '2003/07/04', 1, null, 'text a', " +
          "1.111, 0)");
      sqlman.executeUpdate(
          "INSERT INTO TEST_DBtypes " +
          "VALUES ('B', '2003/07/04', 0, null, 'text b', " +
          "1.111, 0)");
      sqlman.executeUpdate(
          "INSERT INTO TEST_DBtypes " +
          "VALUES ('C', '2003/07/04', 1, null, 'text c', " +
          "1.111, 0)");
      sqlman.executeUpdate(
          "INSERT INTO TEST_DBtypes " +
          "VALUES ('D', '2003/07/04', 0, null, 'text d', " +
          "1.111, 0)");
      String sql = "select * from TEST_DBtypes order by columnA";
      ArrayList values = new ArrayList();
      values.add("A");
      values.add("B");
      values.add("C");
      sqlman.setMaxInClauseCount(2);
      QuerySeries series = sqlman.buildInClauseQuery(sql, "columnA", values);

      assertEquals(new Integer(2), new Integer(series.queryCount()));
      ArrayList actual = new ArrayList();
      ArrayList expected = new ArrayList();
      ResultsNavigator nav1 = series.executeNextQuery();
      nav1.next();
      RowReference row = nav1.getRowReference();
      assertEquals("text a", row.getString(5));
      nav1.next();
      row = nav1.getRowReference();
      assertEquals("text b", row.getString(5));
      ResultsNavigator nav2 = series.executeNextQuery();
      nav2.next();
      row = nav2.getRowReference();
      assertEquals("text c", row.getString(5));
  }
}
