package org.jax.mgi.shr.dbutils;

import junit.framework.*;

import java.util.Vector;
import java.util.Iterator;

import org.jax.mgi.shr.unitTest.TableCreator;

public class TestMultiRowIterator
    extends TestCase {

  private MultiRowIterator multiRowIterator = null;
  private SQLDataManager sqlMgr = null;
  private TableCreator tblMgr = null;
  private String sql = "INSERT INTO TEST_DBsimple VALUES (?, ?)";
  private String query = "SELECT * FROM TEST_DBsimple ORDER BY columnA";


  public TestMultiRowIterator(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    sqlMgr = new SQLDataManager();
    tblMgr = new TableCreator(sqlMgr.getUrl(), sqlMgr.getDatabase(),
                              sqlMgr.getUser(), sqlMgr.getPassword());
    tblMgr.createDBsimple();
    BindableStatement bs = sqlMgr.getBindableStatement(sql);
    bs.setInt(1, 1);
    bs.setString(2, "one");
    bs.executeUpdate();
    bs.setInt(1, 1);
    bs.setString(2, "two");
    bs.executeUpdate();
    bs.setInt(1, 1);
    bs.setString(2, "three");
    bs.executeUpdate();
    bs.setInt(1, 2);
    bs.setString(2, "one");
    bs.executeUpdate();
    bs.setInt(1, 2);
    bs.setString(2, "two");
    bs.executeUpdate();
    ResultsNavigator nav = sqlMgr.executeQuery(query);
    nav.setInterpreter(new CompoundRowParser());
    multiRowIterator = new MultiRowIterator(nav, new CompoundRowParser());
  }

  protected void tearDown() throws Exception {
    multiRowIterator = null;
    sqlMgr.closeResources();
    sqlMgr = null;
    super.tearDown();
  }

  public void testHasNext() throws Exception {

    assertTrue(multiRowIterator.hasNext());
    assertTrue(multiRowIterator.hasNext());
    assertTrue(multiRowIterator.hasNext());
    Object o = multiRowIterator.next();
    assertTrue(multiRowIterator.hasNext());
    o = multiRowIterator.next();
    assertFalse(multiRowIterator.hasNext());
  }

  public void testNext() throws DBException {
    CompoundRow c = (CompoundRow)multiRowIterator.next();
    assertEquals(c.key, 1);
    Vector v = c.values;
    assertEquals((String)v.get(0), "one");
    assertEquals((String)v.get(1), "two");
    assertEquals((String)v.get(2), "three");
    c = (CompoundRow)multiRowIterator.next();
    assertEquals(c.key, 2);
    v = c.values;
    assertEquals((String)v.get(0), "one");
    assertEquals((String)v.get(1), "two");
  }


  public class CompoundRowParser implements MultiRowInterpreter
  {

    public java.lang.Object interpret(RowReference rowReference)
        throws DBException
    {
      SingleRow row = new SingleRow();
      row.columnA = rowReference.getInt(1).intValue();
      row.columnB = rowReference.getString(2);
      return row;
    }

    public Object interpretKey(RowReference row) throws DBException
    {
      return row.getInt(1);
    }

    public Object interpretRows(Vector v) throws InterpretException
    {
      CompoundRow compound = new CompoundRow();
      compound.key = ((SingleRow)v.get(0)).columnA;
      for (Iterator i = v.iterator(); i.hasNext(); )
      {
        compound.values.add(((SingleRow)i.next()).columnB);
      }
      return compound;
    }
  }

  public class SingleRow
  {
    public int columnA = 0;
    public String columnB = null;
  }

  public class CompoundRow
  {
    public int key = 0;
    public Vector values = null;
    public CompoundRow()
    {
      values = new Vector();
    }
  }


}
