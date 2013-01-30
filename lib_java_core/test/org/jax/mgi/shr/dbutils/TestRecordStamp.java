package org.jax.mgi.shr.dbutils;

import junit.framework.*;

import java.util.Properties;

import org.jax.mgi.shr.unitTest.TableCreator;
import org.jax.mgi.shr.types.DataVector;
import org.jax.mgi.shr.dbutils.bcp.*;

public class TestRecordStamp
    extends TestCase {
  private RecordStamper recordStamp = null;

  public TestRecordStamp(String name) {
    super(name);
  }

  private SQLDataManager sqlMgr = null;
  private BCPManager bcpMgr = null;
  private TableCreator tableCreator = null;

  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty("JOBKEY", "1");
    System.setProperty("JOBSTREAM", "testRun");
    sqlMgr = new SQLDataManager();
    bcpMgr = new BCPManager();
    bcpMgr.setOkToRecordStamp(true);
    bcpMgr.setRemoveAfterExecute(true);
    tableCreator =
      new TableCreator(sqlMgr.getUrl(), sqlMgr.getDatabase(),
                       sqlMgr.getUser(), sqlMgr.getPassword());
    String  sql = "create table MGI_User ( " +
       "_User_key                  int             not null, " +
       "_UserType_key              int             not null, " +
       "_UserStatus_key            int             not null, " +
       "login                      varchar(30)     not null, " +
       "name                       varchar(255)    not null, " +
       "_CreatedBy_key             int             not null, " +
       "_ModifiedBy_key            int             not null, " +
       "creation_date              datetime        not null, " +
       "modification_date          datetime        not null)";
    sqlMgr.executeUpdate("drop table MGI_USer");
    sqlMgr.executeUpdate(sql);
    sql = "INSERT into MGI_User values (1111122222, 222221111, 1, " +
                 "'testRun', 'testRun', 1, 1, '2003-07-04', '2003-07-04')";
    sqlMgr.executeUpdate(sql);
  }

  protected void tearDown() throws Exception {
      sqlMgr.closeResources();
    sqlMgr = null;
    bcpMgr = null;
    tableCreator = null;
    Properties p = System.getProperties();
    p.remove("JOBKEY");
    p.remove("JOBSTREAM");
    super.tearDown();
  }

  public void test_MGD() throws Exception {

    //RecordStamp stamp = new RecordStamp_MGD();
    RecordStamper stamp = new RecordStamper_MGD();
    tableCreator.createDBstamped_MGD();
    Table table = Table.getInstance("TEST_DBstamped_MGD", sqlMgr);
    BCPWriter writer = bcpMgr.getBCPWriter(table);
    DataVector v = new DataVector();
    v.add(1);
    v.add("test");
    writer.write(v);
    bcpMgr.executeBCP();
    String sql = "SELECT columnA, columnB FROM TEST_DBstamped_MGD";
    ResultsNavigator nav = sqlMgr.executeQuery(sql);
    nav.next();
    RowReference row = (RowReference)nav.getCurrent();
    assertEquals(1, row.getInt(1).intValue());
    assertEquals("test", row.getString(2));
  }

  public void test_MGDOrg() throws Exception {
    RecordStamper stamp = new RecordStamper_MGDOrg();
    tableCreator.createDBstamped_MGDOrg();
    Table table = Table.getInstance("TEST_DBstamped_MGDOrg", sqlMgr);
    BCPWriter writer = bcpMgr.getBCPWriter(table);
    DataVector v = new DataVector();
    v.add("test");
    v.add("2003-07-04 00:00:00.0");
    v.add(1);
    v.add((float)1.0);
    writer.write(v);
    bcpMgr.executeBCP();
    String sql = "SELECT columnA, columnC FROM TEST_DBstamped_MGDOrg";
    ResultsNavigator nav = sqlMgr.executeQuery(sql);
    nav.next();
    RowReference row = (RowReference)nav.getCurrent();
    assertEquals(1, row.getInt(2).intValue());
    assertEquals("test", row.getString(1));
  }

  public void test_RADAR() throws Exception {
    RecordStamper stamp = new RecordStamper_RADAR();
    tableCreator.createDBstamped_RADAR();
    Table table = Table.getInstance("TEST_DBstamped_RADAR", sqlMgr);
    BCPWriter writer = bcpMgr.getBCPWriter(table);
    DataVector v = new DataVector();
    v.add(1);
    v.add("test");
    writer.write(v);
    bcpMgr.executeBCP();
    String sql = "SELECT columnA, columnB FROM TEST_DBstamped_RADAR";
    ResultsNavigator nav = sqlMgr.executeQuery(sql);
    nav.next();
    RowReference row = (RowReference)nav.getCurrent();
    assertEquals(1, row.getInt(1).intValue());
    assertEquals("test", row.getString(2));
  }

  public void test_MGDDate() throws Exception {
    RecordStamper stamp = new RecordStamper_MGDDate();
    tableCreator.createDBstamped_MGDDate();
    Table table = Table.getInstance("TEST_DBstamped_MGDDate", sqlMgr);
    BCPWriter writer = bcpMgr.getBCPWriter(table);
    DataVector v = new DataVector();
    v.add(1);
    v.add("test");
    writer.write(v);
    bcpMgr.executeBCP();
    String sql = "SELECT columnA, columnB FROM TEST_DBstamped_MGDDate";
    ResultsNavigator nav = sqlMgr.executeQuery(sql);
    nav.next();
    RowReference row = (RowReference)nav.getCurrent();
    assertEquals(1, row.getInt(1).intValue());
    assertEquals("test", row.getString(2));
  }

  public void test_MGDRelease() throws Exception {
    RecordStamper stamp = new RecordStamper_MGDRelease();
    tableCreator.createDBstamped_MGDRelease();
    Table table = Table.getInstance("TEST_DBstamped_MGDRelease", sqlMgr);
    BCPWriter writer = bcpMgr.getBCPWriter(table);
    DataVector v = new DataVector();
    v.add(1);
    v.add("test");
    writer.write(v);
    bcpMgr.executeBCP();
    String sql = "SELECT columnA, columnB FROM TEST_DBstamped_MGDRelease";
    ResultsNavigator nav = sqlMgr.executeQuery(sql);
    nav.next();
    RowReference row = (RowReference)nav.getCurrent();
    assertEquals(1, row.getInt(1).intValue());
    assertEquals("test", row.getString(2));
  }

  public void test_None() throws Exception {
    RecordStamper stamp = new RecordStamper_None();
    tableCreator.createDBsimple();
    Table table = Table.getInstance("TEST_DBsimple", sqlMgr);
    BCPWriter writer = bcpMgr.getBCPWriter(table);
    DataVector v = new DataVector();
    v.add(1);
    v.add("test");
    writer.write(v);
    bcpMgr.executeBCP();
    String sql = "SELECT columnA, columnB FROM TEST_DBsimple";
    ResultsNavigator nav = sqlMgr.executeQuery(sql);
    nav.next();
    RowReference row = (RowReference)nav.getCurrent();
    assertEquals(1, row.getInt(1).intValue());
    assertEquals("test", row.getString(2));
  }






}
