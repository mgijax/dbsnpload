package org.jax.mgi.shr.dbutils;

import junit.framework.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import org.jax.mgi.shr.unitTest.*;
import org.jax.mgi.shr.types.DataVector;
import org.jax.mgi.shr.dbutils.bcp.*;

public class TestBCPWriter
    extends TestCase {
  private BCPWriter bCPWriter = null;
  private BCPManager manager = null;
  private String tablename = "TEST_DBTypes";
  private DataVector v = null;
  private SQLDataManager sqlman = null;
  private TableCreator tableCreator = null;

  public TestBCPWriter(String name) {
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
    manager = new BCPManager();
    manager.setOkToOverwrite(true);
    manager.setRemoveAfterExecute(true);
    bCPWriter =
        manager.getBCPWriter(Table.getInstance(this.tablename,
                                       this.sqlman));
    v = new DataVector();
  }

  protected void tearDown() throws Exception {
    tableCreator.dropDBtypes();
    sqlman.closeResources();
    bCPWriter = null;
    manager = null;
    tablename = null;
    tableCreator = null;
    sqlman = null;
    v = null;
    super.tearDown();
  }

  public void testWrite() throws Exception  {
    DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    java.util.Date date = formatter.parse("2003.07.04.00.00.00");
    v.add(new String("hello there"));
    v.add(new Timestamp(date.getTime()));
    v.add(12345);
    v.add(new String("-"));
    v.add(new String("goodbye"));
    v.add((float)1.1);
    v.add(false);
    BCPTranslatable translator = new Data(v);
    bCPWriter.write(translator);
    manager.executeBCP();
    ResultsNavigator i = sqlman.executeQuery("select * from TEST_DBTypes");
    i.next();
    RowReference pointer = (RowReference) i.getCurrent();
    assertEquals(pointer.getString(1), "hello there");
    assertEquals(pointer.getTimestamp(2), new Timestamp(date.getTime()));
    assertEquals(pointer.getInt(3).intValue(), 12345);
    assertEquals(pointer.getString(4), "-");
    assertEquals(pointer.getString(5), "goodbye");
    assertEquals(pointer.getDouble(6).floatValue(), (float) 1.1, 0.0001);
    assertEquals(pointer.getBoolean(7).booleanValue(), false);
  }

  public void testWriteStrings() throws Exception  {
    DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    java.util.Date date = formatter.parse("2003.07.04.00.00.00");
    v.add(new String("varchar"));
    v.add(new String("2003/07/04"));
    v.add(new String("12"));
    v.add(new String("-"));
    v.add(new String("goodbye"));
    v.add(new String("1"));
    v.add(new String("0"));
    BCPTranslatable translator = new Data(v);
    bCPWriter.write(translator);
    manager.executeBCP();
    ResultsNavigator i = sqlman.executeQuery("select * from TEST_DBTypes");
    DataVector dv = new DataVector();
    RowReference pointer = null;
    if (i.next())
      pointer = (RowReference) i.getCurrent();
    assertEquals(pointer.getString(1), "varchar");
    assertEquals(pointer.getTimestamp(2), new Timestamp(date.getTime()));
    assertEquals(pointer.getInt(3).intValue(), 12);
    assertEquals(pointer.getString(4), "-");
    assertEquals(pointer.getString(5), "goodbye");
    assertEquals(pointer.getDouble(6).floatValue(), (float) 1.0, 0.0001);
    assertEquals(pointer.getBoolean(7).booleanValue(), false);
  }

  public void testDelimiter() throws Exception  {
    manager.setDelimiter("TAB");
    DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    java.util.Date date = formatter.parse("2003.07.04.00.00.00");
    v.add(new String("hello there"));
    v.add(new Timestamp(date.getTime()));
    v.add(12345);
    v.add(new String("-"));
    v.add(new String("goodbye"));
    v.add((float)1.1);
    v.add(false);
    BCPTranslatable translator = new Data(v);
    bCPWriter.write(translator);
    manager.executeBCP();
    ResultsNavigator i = sqlman.executeQuery("select * from TEST_DBTypes");
    i.next();
    RowReference pointer = (RowReference) i.getCurrent();
    assertEquals(pointer.getString(1), "hello there");
    assertEquals(pointer.getTimestamp(2), new Timestamp(date.getTime()));
    assertEquals(pointer.getInt(3).intValue(), 12345);
    assertEquals(pointer.getString(4), "-");
    assertEquals(pointer.getString(5), "goodbye");
    assertEquals(pointer.getDouble(6).floatValue(), (float) 1.1, 0.0001);
    assertEquals(pointer.getBoolean(7).booleanValue(), false);
  }


  private class Data implements BCPTranslatable {
    Vector v = null;

    public Data(Vector vParm) {
      v = vParm;
    }

    public Vector getBCPVector(Table table) {
      return v;
    }

    public Vector getBCPSupportedTables() {
      Vector v = new Vector();
      v.add("any");
      return v;
    }
  }


}
