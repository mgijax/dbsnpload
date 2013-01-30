package org.jax.mgi.shr.dbutils;

import junit.framework.*;
import org.jax.mgi.shr.config.*;
import org.jax.mgi.shr.unitTest.*;
import org.jax.mgi.shr.types.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.*;
import org.jax.mgi.shr.dbutils.bcp.*;


public class TestBCPManager1
    extends TestCase {
  private BCPManager bcpManager = null;
  private SQLDataManager sqlManager = null;
  private String bcpFileTab = "bcpFileTab";
  private String bcpFilePipe = "bcpFilePipe";
  private String contentsTab = "some string\t2003-07-04 00:00:00.0\t1\t1.0\tloader\tloader\t2003-07-04 00:00:00.0\t2003-07-04 00:00:00.0\n" +
                               "another string\t2003-07-04 00:00:00.0\t2\t2.0\tloader\tloader\t2003-07-04 00:00:00.0\t2003-07-04 00:00:00.0\n";
  private String contentsPipe = "some string|2003-07-04 00:00:00.0|1|1.0|loader|loader|2003-07-04 00:00:00.0|2003-07-04 00:00:00.0\n" +
                                "another string|2003-07-04 00:00:00.0|2|2.0|loader|loader|2003-07-04 00:00:00.0|2003-07-04 00:00:00.0\n";
  private String table = "TEST_DBstamped_MGDOrg";
  private DataVector v = null;
  private TableCreator tableCreator = null;
  private String config = "config_test";
  private FileUtility fileUtility = new FileUtility();
  private String originalConfigValue = null;

  public TestBCPManager1(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    String s =
        "#format: sh\n" +
        "BCP_DELIMITER=tab\n" +
        "BCP_PREVENT_EXECUTE=no\n" +
        "BCP_OK_TO_OVERWRITE=NO\n" +
        "BCP_USE_TEMPFILE=false\n" +
        "BCP_REMOVE_AFTER_EXECUTE=FALSE\n" +
        "BCP_RECORD_STAMPING=false\n" +
        "BCP_TRUNCATE_TABLE=true\n" +
        "BCP_TRUNCATE_LOG=0\n" +
        "BCP_DROP_INDEXES=false\n" +
        "CREATED_BY=some loader\n" +
        "MODIFIED_BY=some loader";

    FileUtility.createFile(config, s);

    FileUtility.createFile(bcpFileTab, contentsTab);
    FileUtility.createFile(bcpFilePipe, contentsPipe);

    String configParm = System.getProperty("CONFIG");
    if (configParm != null) {
      originalConfigValue = configParm;
      config = configParm + "," + config;
    }
    System.setProperty("CONFIG", config);
    ConfigReinitializer.reinit();

    bcpManager = new BCPManager();
    sqlManager = new SQLDataManager();
    tableCreator = new TableCreator(sqlManager.getUrl(),
                                    sqlManager.getDatabase(),
                                    sqlManager.getUser(),
                                    sqlManager.getPassword(),
                                    sqlManager.getConnectionManagerClass());
    tableCreator.createDBstamped_MGDOrg();
    tableCreator.createDBtypes();
    boolean test = bcpRecordsExist();
    v = new DataVector();
    deleteBCPFiles();
  }

  protected void tearDown() throws Exception {
    tableCreator.dropDBstamped_MGDOrg();
    tableCreator.dropDBtypes();
    sqlManager.closeResources();
    bcpManager = null;
    sqlManager = null;
    FileUtility.delete(bcpFileTab);
    FileUtility.delete(bcpFilePipe);
    FileUtility.delete(config);
    FileUtility.delete(table + ".bcp");
    v = null;
    tableCreator.close();
    tableCreator = null;
    Properties p = System.getProperties();
    if (originalConfigValue != null)
      System.setProperty("CONFIG", originalConfigValue);
    else
      p.remove("CONFIG");
    ConfigReinitializer.reinit();
    super.tearDown();
  }

  public void testSets() throws Exception {
    bcpManager = new BCPManager();
    bcpManager.setPathname("another");
    bcpManager.setDelimiter("|");
    bcpManager.setPreventExecute(false);
    bcpManager.setOkToOverwrite(true);
    bcpManager.setUseTempFile(false);
    bcpManager.setRemoveAfterExecute(true);
    bcpManager.setOkToRecordStamp(false);
    bcpManager.setOkToTruncateLog(false);
    assertEquals("another", bcpManager.getPathname());
    assertEquals("|", bcpManager.getDelimiter());
    assertEquals(false, bcpManager.getPreventExecute());
    assertEquals(true, bcpManager.getOkToOverwrite());
    assertEquals(false, bcpManager.getUseTempFile());
    assertEquals(true, bcpManager.getRemoveAfterExecute());
    assertEquals(false, bcpManager.getOkToTruncateLog());
    assertEquals(false, bcpManager.getOkToRecordStamp());
  }

  public void testWriteLn() throws Exception {
    bcpManager.setPreventExecute(true);
    setDataVector();
    BCPWriter writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.write(v);
    v.set(0, "another string");
    v.set(2, 2);
    v.set(3, (float)2.0);
    writer.write(v);
    bcpManager.executeBCP();
    assertTrue(FileUtility.compare(table + ".bcp", bcpFileTab));
  }

  public void testOkToOverwrite() throws Exception {
    setDataVector();
    bcpManager.setPreventExecute(true);
    bcpManager.setOkToOverwrite(true);
    BCPWriter writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.write(v);
    writer.write(v);
    bcpManager.executeBCP();
    assertTrue(FileUtility.doesExist(table + ".bcp"));
    assertTrue(!FileUtility.doesExist(table + "_1.bcp"));
    bcpManager.setOkToOverwrite(false);
    writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.write(v);
    bcpManager.executeBCP();
    assertTrue(FileUtility.doesExist(table + "_1.bcp"));
    FileUtility.delete(table + ".bcp");
    FileUtility.delete(table + "_1.bcp");
  }

  public void testUseTemp() throws Exception {
    setDataVector();
    bcpManager.setUseTempFile(true);
    BCPWriter writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.write(v);
    bcpManager.executeBCP();
    assertTrue(!FileUtility.doesExist(table + ".bcp"));
    assertTrue(bcpRecordsExist());
  }

  public void testRemoveAfterExecute() throws Exception {
    setDataVector();
    BCPWriter writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.write(v);
    bcpManager.executeBCP();
    assertTrue(FileUtility.doesExist(table + ".bcp"));
    FileUtility.delete(table + ".bcp");
    bcpManager.setRemoveAfterExecute(true);
    writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.write(v);
    bcpManager.executeBCP();
    assertTrue(!FileUtility.doesExist(table + ".bcp"));
  }

  public void testSetDelimiter() throws Exception {
    setDataVector();
    bcpManager.setPreventExecute(true);
    bcpManager.setDelimiter("|");
    BCPWriter writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.write(v);
    v.set(0, "another string");
    v.set(2, 2);
    v.set(3, (float)2.0);
    writer.write(v);
    bcpManager.executeBCP();
    assertTrue(FileUtility.compare(bcpFilePipe, table + ".bcp"));
  }

  public void testDelimiter() throws Exception  {
    DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    java.util.Date date = formatter.parse("2003.07.04.00.00.00");
    v.add(new String("varchar"));
    v.add(new String("2003/07/04"));
    v.add(new String("12"));
    v.add(new String("-"));
    v.add(new String("goodbye"));
    v.add(new String("1"));
    v.add(new String("0"));
    bcpManager.setDelimiter("*****");
    bcpManager.setRemoveAfterExecute(true);
    BCPWriter writer =
        bcpManager.getBCPWriter(Table.getInstance("TEST_DBTypes",
                                          this.sqlManager));
    writer.write(v);
    bcpManager.executeBCP();
    ResultsNavigator i = sqlManager.executeQuery("select * from TEST_DBTypes");
    RowReference pointer = null;
    if (i.next())
      pointer = (RowReference) i.getCurrent();
    assertEquals(pointer.getString(1), "varchar");
    assertEquals(pointer.getTimestamp(2), new Timestamp(date.getTime()));
    assertEquals(pointer.getInt(3), new Integer(12));
    assertEquals(pointer.getString(4), "-");
    assertEquals(pointer.getString(5), "goodbye");
    assertEquals(pointer.getDouble(6).floatValue(), (float) 1.0, 0.0001);
    assertEquals(pointer.getBoolean(7).booleanValue(), false);
  }


  public void testRecordStamping() throws Exception {
    setDataVectorNoStamp();
    bcpManager.setOkToRecordStamp(true);
    bcpManager.setPreventExecute(true);
    BCPWriter writer =
        bcpManager.getBCPWriter(Table.getInstance(this.table,
                                          this.sqlManager));
    writer.setOkToRecordStamp(true);
    writer.write(v);
    bcpManager.executeBCP();
    //FileUtility.print(table + ".bcp");
    BufferedReader in = new BufferedReader(new FileReader(table + ".bcp"));
    String s = in.readLine();
    String timeString = s.substring(64, 85);
    //System.out.println(timeString);
    Timestamp currentTime = new Timestamp(new java.util.Date().getTime());
    Timestamp testTime = Timestamp.valueOf(timeString);
    //System.out.println(currentTime);
    long diff = currentTime.getTime() - testTime.getTime();
    assertTrue(diff < 500);
  }

  private void setDataVector() {
    v.add("some string");
    v.add("2003-07-04 00:00:00.0");
    v.add(1);
    v.add((float)1.0);
    v.add("loader");
    v.add("loader");
    v.add("2003-07-04 00:00:00.0");
    v.add("2003-07-04 00:00:00.0");
  }

  private void setDataVectorNoStamp() {
    v.add("some string");
    v.add("2003-07-04 00:00:00.0");
    v.add(1);
    v.add((float)1.0);
  }


  private class BCPFile implements FilenameFilter {
    public boolean accept(File dir, String name) {
      if (name.startsWith(table))
        return true;
      else
        return false;
    }
  }

  private void deleteBCPFiles() {
    File testDirectory = new File(System.getProperty("user.dir"));
    BCPFile bcpFile = new BCPFile();
    File files[] = testDirectory.listFiles(bcpFile);
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      file.delete();
    }
  }

  private boolean bcpRecordsExist() throws Exception {
    String sql = "SELECT * FROM " + table;
    ResultsNavigator it = sqlManager.executeQuery(sql);
    if (it.next())
      return true;
    else
      return false;
  }

  private boolean bcpRecordsExistSecondary() throws Exception {
    String sql = "SELECT * FROM " + table;
    DatabaseCfg dbConfig = new DatabaseCfg("SECONDARY");
    sqlManager = new SQLDataManager(dbConfig);
    ResultsNavigator it = sqlManager.executeQuery(sql);
    if (it.next())
      return true;
    else
      return false;
  }


}

