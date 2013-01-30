package org.jax.mgi.shr.dbutils;

import junit.framework.*;

public class TestMultipleResults
    extends TestCase {
  private MultipleResults multipleResults = null;
  private SQLDataManager mgr = null;

  public TestMultipleResults(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    //mgr = new SQLDataManager(null, "tempdb", null, null, null);
    mgr = new SQLDataManager();
    try {
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
    mgr.executeUpdate(createTable);
    mgr.executeUpdate(insert1);
    mgr.executeUpdate(insert2);
    multipleResults = null;
  }

  protected void tearDown() throws Exception {
    mgr.executeUpdate("drop table temp_table");
    mgr.closeResources();
    multipleResults = null;
    mgr = null;
    super.tearDown();
  }

  public void testGetNextResults() throws Exception {
    String sql =
        "select * from temp_table\n" +
        "update temp_table set columnB = 'uno' where columnA = 1\n" +
        "update temp_table set columnB = 'no specified'";
    multipleResults = mgr.execute(sql);
    StringBuffer s = new StringBuffer();
    Object o = null;
    while ((o = multipleResults.getNextResults()) != null) {
      if (o instanceof ResultsNavigator) {
        ResultsNavigator nav = (ResultsNavigator)o;
        while (nav.next()) {
          RowReference r = (RowReference)nav.getCurrent();
          String row = r.createStringFromRow();
          s.append(row);
        }
      }
      else if (o instanceof Integer) {
        s.append("update count: " + o.toString() + "\n");
      }
      else {
        s.append("Unknown results\n");
      }
    }
    String expectedString = "columnA = 1\ncolumnB = one\ncolumnA = 2\n" +
        "columnB = two\nupdate count: 1\nupdate count: 2\n";
    assertEquals(expectedString, new String(s));
  }

}
