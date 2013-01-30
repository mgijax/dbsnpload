package org.jax.mgi.shr.dbutils;

import junit.framework.*;
import java.util.*;
import java.sql.Timestamp;
import org.jax.mgi.shr.unitTest.*;
import org.jax.mgi.shr.types.*;
import org.jax.mgi.shr.exception.*;

public class TestTable
    extends TestCase {
  private Table table = null;
  private DataVector v;
  private TableCreator tableCreator = null;
  private SQLDataManager sqlman = null;

  public TestTable(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    sqlman = new SQLDataManager();
    tableCreator = new TableCreator(sqlman.getUrl(),
                                    sqlman.getDatabase(),
                                    sqlman.getUser(),
                                    sqlman.getPassword(),
                                    sqlman.getConnectionManagerClass());
    tableCreator.createDBtypes();
    table = Table.getInstance("TEST_DBTypes", sqlman);
    v = new DataVector();
    v.add("column a");
    v.add(new Timestamp(new Date().getTime()));
    v.add(1);
    v.add("x");
    v.add("some text");
    v.add((float)1.0);
    v.add(true);
  }

  protected void tearDown() throws Exception {
    table = null;
    tableCreator.dropDBtypes();
    sqlman.closeResources();
    sqlman = null;
    tableCreator.close();
    tableCreator = null;
    v = null;
    super.tearDown();
  }

  public void testValidateFields() {
    checkValidation();
  }

  public void testIntegerToChar() {
    v.set(3, 1);
    checkValidationException();
  }

  public void testFloatToChar() {
    v.set(3, (float)1.0);
    checkValidationException();
  }

  public void testBooleanToChar() {
    v.set(3, false);
    checkValidationException();
  }

  public void testTimestampToChar() {
    v.set(3, new Timestamp(new Date().getTime()));
    checkValidationException();
  }

  public void testIntegerToVarchar() {
    v.set(0, 123);
    checkValidation();
  }

  public void testFloatToVarchar() {
    v.set(0, (float)1.0);
    checkValidation();
  }

  public void testBooleanToVarchar() {
    v.set(0, false);
    checkValidation();
  }

  public void testTimestampToVarchar() {
    v.set(0, false);
    checkValidation();
  }

  public void testIntegerToFloat() {
    v.set(5, new Integer(1));
    checkValidation();
  }

  public void testBooleanToFloat() throws Exception {
    v.set(5, false);
    checkValidationException();
  }

  public void testTimestampToFloat() throws Exception {
    v.set(5, new Timestamp(0));
    checkValidationException();
  }

  public void testStringToFloat() {
    v.set(5, "1.78E13");
    checkValidation();
  }

  public void testFloatToInteger() throws Exception {
    v.set(2, (float)6.2345);
    checkValidationException();
  }

  public void testStringToInteger() throws Exception {
    v.set(2, "6.234");
    checkValidationException();
    v.set(2, "6");
    checkValidation();
  }

  public void testBooleanToInteger() throws Exception {
    v.set(2, true);
    checkValidationException();
  }

  public void testTimestampToInteger() throws Exception {
    v.set(2, new Timestamp(0));
    checkValidationException();
  }

  public void testFloatToTimestamp() throws Exception {
    v.set(1, (float)1.0);
    checkValidationException();
  }

  public void testIntegerToTimestamp() throws Exception {
    v.set(1, 1);
    checkValidationException();
  }

  public void testBooleanToTimestamp() throws Exception {
    v.set(1, true);
    checkValidationException();
  }

  public void testStringToTimestamp() throws Exception {
    v.set(1, "bad timestamp");
    checkValidationException();
  }

  public void testStringToBit() throws Exception {
    v.set(6, "true");
    checkValidationException();
    v.set(6, "false");
    checkValidationException();
    v.set(6, "1");
    checkValidation();
    v.set(6, "0");
    checkValidation();
    v.set(6, "false boolean string");
    checkValidationException();
  }

  public void testFloatToBit() throws Exception {
    v.set(6, (float)1.0);
    checkValidationException();
  }

  public void testIntegerToBit() throws Exception {
    v.set(6, 1);
    checkValidationException();
  }

  public void testTimestampToBit() throws Exception {
    v.set(6, new Timestamp(0));
    checkValidationException();
  }

  public void testNull() throws Exception {
    v.set(3, null);
    v.set(4, null);
    v.set(5, null);
    checkValidation();
  }

  public void testNullNotAllowed() throws Exception {
    v.set(6, null);
    checkValidationException();
  }


  public void testOverrun() throws Exception {
    v.set(3, "abcd");
    checkValidationException();
    v.set(3, "a");
    v.set(0, "this line should be too long for the column definition");
    checkValidationException();
  }


  public void testExtraFields() throws Exception {
    v.add(3, 1);
    checkValidationException();
  }

  public void testLessFields()  {
    v.remove(1);
    v.remove(2);
    checkValidationException();
  }

  public void testGetNextKey() throws Exception {
    SQLDataManager manager = new SQLDataManager();
    tableCreator.createDBkeyed();
    tableCreator.createDBmultikeyed();
    manager.executeUpdate("insert into TEST_DBkeyed values (1, '1')");
    manager.executeUpdate("insert into TEST_DBkeyed values (2, '2')");
    manager.executeUpdate("insert into TEST_DBmultikeyed values (1, '1')");
    String sql = "select max(columnA) from TEST_DBkeyed";
    ResultsNavigator rn = manager.executeQuery(sql);
    rn.next();
    RowReference r = (RowReference)rn.getCurrent();
    Integer maxkey = r.getInt(1);
    maxkey = new Integer(maxkey.intValue() + 1);
    table = Table.getInstance("TEST_DBkeyed", manager);
    Integer keyval = table.getNextKey();
    assertEquals(keyval, maxkey);
    table.resetKey();
    try {
      table = Table.getInstance("TEST_DBmultikeyed", manager);
      // this should fail because of multikeyed table
      keyval = table.getNextKey();
      assertTrue(false);
    }
    catch (Exception e)
    {
      assertTrue(true);
    }
    table.resetKey();
    tableCreator.dropDBkeyed();
    tableCreator.dropDBmultikeyed();
  }

  public void testGetNextKey2() throws Exception {
    SQLDataManager manager = new SQLDataManager();
    tableCreator.createDBkeyed();
    table = Table.getInstance("TEST_DBkeyed", manager);
    Integer keyval = table.getNextKey();
    assertEquals(new Integer(1), keyval);
    table.resetKey();
    tableCreator.dropDBkeyed();
  }

  public void testSynchronizeKey() throws Exception
  {
      tableCreator.createDBkeyed();
      SQLDataManager sqlMgr = new SQLDataManager();
      Table table = Table.getInstance("Test_DBKeyed", sqlMgr);
      assertEquals(new Integer("1"), table.getNextKey());
      sqlMgr.executeUpdate("insert into TEST_DBKeyed values (1, '1')");
      sqlMgr.executeUpdate("insert into TEST_DBKeyed values (2, '2')");
      assertEquals(new Integer("2"), table.getNextKey());
      table.synchronizeKey();
      assertEquals(new Integer("3"), table.getNextKey());
  }



  private void checkValidationException() {
    try {
      table.validateFields(v, false);
    }
    catch (MGIException e) {
      assertTrue(true);
      return;
    }
    assertTrue(false);
  }

  private void checkValidation() {
    try {
      table.validateFields(v, false);
    }
    catch (MGIException e) {
      assertTrue(false);
      return;
    }
    assertTrue(true);
  }
}
