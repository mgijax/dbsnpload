package org.jax.mgi.shr.cache;

import junit.framework.*;
import java.io.ByteArrayOutputStream;

import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.RowDataInterpreter;
import org.jax.mgi.shr.dbutils.RowReference;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.BindableStatement;
import org.jax.mgi.shr.unitTest.TableCreator;

public class TestCacheHandler
    extends TestCase
{
  private CacheHandlerTestSubclass handler = null;
  private SQLDataManager sqlMgr = null;
  private TableCreator tblMgr = null;
  private String sql = "INSERT INTO TEST_DBsimple VALUES (?, ?)";
  private String partialInitQuery =
      "SELECT * FROM TEST_DBsimple WHERE ColumnA < 0";
  private String fullInitQuery = "SELECT * FROM TEST_DBsimple";
  private String addQuery = "SELECT * FROM TEST_DBsimple WHERE ColumnA = ";

  public TestCacheHandler(String name)
  {
    super(name);
  }
  protected void setUp()
      throws Exception
  {
    super.setUp();
    sqlMgr = new SQLDataManager();
    tblMgr = new TableCreator(sqlMgr.getUrl(), sqlMgr.getDatabase(),
                              sqlMgr.getUser(), sqlMgr.getPassword(),
                              sqlMgr.getConnectionManagerClass());
    tblMgr.createDBsimple();
    BindableStatement bs = sqlMgr.getBindableStatement(sql);
    bs.setInt(1, -1);
    bs.setString(2, "value -1");
    bs.executeUpdate();
    bs.setInt(1, 1);
    bs.setString(2, "value 1");
    bs.executeUpdate();
    bs.setInt(1, 2);
    bs.setString(2, "value 2");
    bs.executeUpdate();
    bs.setInt(1, 3);
    bs.setString(2, "value 3");
    bs.executeUpdate();
  }

  protected void tearDown()
      throws Exception
  {
    handler = null;
    tblMgr.dropDBsimple();
    tblMgr.close();
    sqlMgr.closeResources();
    sqlMgr = null;
    tblMgr = null;
    handler = null;
    super.tearDown();
  }

  public void testLazyInit() throws Exception
  {
    handler =
        new CacheHandlerTestSubclass(CacheConstants.LAZY_CACHE, sqlMgr);
    String expectedResult = "-1 value -1\n";
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    handler.printCache(baos);
    assertEquals(expectedResult, baos.toString());
  }

  public void testLazyLookup() throws Exception
  {
    handler =
        new CacheHandlerTestSubclass(CacheConstants.LAZY_CACHE, sqlMgr);
    String expectedResult = "2 value 2\n-1 value -1\n";
    String value = handler.lookup(2);
    assertEquals(value, "value 2");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    handler.printCache(baos);
    assertEquals(expectedResult, baos.toString());
  }

  public void testStaticCache() throws Exception
  {
    handler =
        new CacheHandlerTestSubclass(CacheConstants.LAZY_CACHE, sqlMgr);
    String expectedResult = "2 value 2\n-1 value -1\n";
    String value = handler.lookup(2);
    assertEquals(value, "value 2");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    handler.printCache(baos);
    assertEquals(expectedResult, baos.toString());
    // test to see if the cache is behaving in a static way
    handler =
        new CacheHandlerTestSubclass(CacheConstants.LAZY_CACHE, sqlMgr);
    assertEquals(expectedResult, baos.toString());
  }


  public void testFullInit() throws Exception
  {
    handler =
        new CacheHandlerTestSubclass(CacheConstants.FULL_CACHE, sqlMgr);
    String expectedResult = "2 value 2\n1 value 1\n-1 value -1\n3 value 3\n";
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    handler.printCache(baos);
    assertEquals(expectedResult, baos.toString());
  }

  public void testMissingValueForFull() throws Exception
  {
    handler =
        new CacheHandlerTestSubclass(CacheConstants.FULL_CACHE, sqlMgr);
    BindableStatement bs = sqlMgr.getBindableStatement(sql);
    // even though it exists in the database, the full cache should not
    // access it
    bs.setInt(1, 4);
    bs.setString(2, "value 4");
    String value = handler.lookup(4);
    assertNull(value);
  }

  public void testMissingValueForLazy() throws Exception
  {
    handler =
        new CacheHandlerTestSubclass(CacheConstants.FULL_CACHE, sqlMgr);
    String value = handler.lookup(4);
    assertNull(value);
  }

  public void testCaseSensitivity() throws Exception
  {
    FullCachedLookupImpl lookup = new FullCachedLookupImpl(sqlMgr,
        this.fullInitQuery, new innerInterpreter());

    Integer value = (Integer)lookup.lookup("VAluE 3");
    assertEquals(new Integer(3), value);

    CacheHandlerTestSubclass3 handler =
        new CacheHandlerTestSubclass3(CacheConstants.LAZY_CACHE, sqlMgr);
    value = handler.lookup("VAluE 3");
    assertEquals(new Integer(3), value);
  }

  public class innerInterpreter
      implements RowDataInterpreter
  {
      public Object interpret(RowReference ref)
          throws DBException
      {
          String key = ref.getString(2);
          Integer value = ref.getInt(1);
          KeyValue keyValue =
              new KeyValue(key, value);
          return keyValue;
      }
  }





}


