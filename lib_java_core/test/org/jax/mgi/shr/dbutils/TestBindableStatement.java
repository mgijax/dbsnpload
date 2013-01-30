package org.jax.mgi.shr.dbutils;

import junit.framework.*;
import java.util.Vector;

import org.jax.mgi.shr.unitTest.*;

public class TestBindableStatement
    extends TestCase {
  private BindableStatement bindableStatement = null;
  private TableCreator tableCreator = null;
  private SQLDataManager manager = null;
  private TestManager testMgr = null;

  public TestBindableStatement(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    /**@todo verify the constructors*/
    bindableStatement = null;
    manager = new SQLDataManager();
    testMgr = new TestManager();
    //testMgr.setConfig("DBDEBUG", "true");
    //testMgr.setConfig("LOG_DEBUG", "true");
    String sql =
        "insert into test_dbtypes values (" +
        "?, ?, ?, ?, ?, ?, ?)";
    bindableStatement = manager.getBindableStatement(sql);
    tableCreator = new TableCreator(manager.getUrl(),
                                    manager.getDatabase(),
                                    manager.getUser(),
                                    manager.getPassword());
    tableCreator.createDBtypes();
  }

  protected void tearDown() throws Exception {
      bindableStatement.close();
      manager.closeResources();
    bindableStatement = null;
    tableCreator.dropDBtypes();
    tableCreator = null;
    testMgr = null;
    super.tearDown();
  }

  public void testBind() throws DBException {
    Vector vars = new Vector();
    vars.add(null);
    vars.add(null);
    vars.add(null);
    vars.add(null);
    vars.add(null);
    vars.add(null);
    vars.add(new Boolean(true));
    bindableStatement.bind(vars);
    bindableStatement.setString(1, "HELLO");
    bindableStatement.executeUpdate();
    bindableStatement.setString(1, "GOODBYE");
    bindableStatement.executeUpdate();
    ResultsNavigator rn = manager.executeQuery("select * from test_dbtypes");
    rn.next();
    RowReference r = (RowReference)rn.getCurrent();
    assertEquals("HELLO", r.getString(1));
    assertNull(r.getObject(2));
    assertNull(r.getObject(3));
    assertNull(r.getObject(4));
    assertNull(r.getObject(5));
    assertNull(r.getObject(6));
    assertEquals(true, r.getBoolean(7).booleanValue());
    rn.next();
    assertEquals("GOODBYE", r.getString(1));
    assertNull(r.getObject(2));
    assertNull(r.getObject(3));
    assertNull(r.getObject(4));
    assertNull(r.getObject(5));
    assertNull(r.getObject(6));
    assertEquals(true, r.getBoolean(7).booleanValue());
  }

  public void testExecute() throws Exception
  {
      Vector vars = new Vector();
      vars.add("HELLO");
      vars.add(null);
      vars.add(null);
      vars.add(null);
      vars.add(null);
      vars.add(null);
      vars.add(new Boolean(true));
      bindableStatement.executeUpdate(vars);
      ResultsNavigator rn = manager.executeQuery("select * from test_dbtypes");
      rn.next();
      RowReference r = (RowReference)rn.getCurrent();
      assertEquals("HELLO", r.getString(1));
      assertNull(r.getObject(2));
      assertNull(r.getObject(3));
      assertNull(r.getObject(4));
      assertNull(r.getObject(5));
      assertNull(r.getObject(6));
      assertEquals(true, r.getBoolean(7).booleanValue());
  }

}
