package org.jax.mgi.shr.unitTest;

import junit.framework.*;
import java.io.*;

public class TestFileUtility
    extends TestCase {
  private String file1 = "file1";
  private String file2 = "file2";
  private String testFile = "testFile";

  public TestFileUtility(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    String s = "input line 1\n" +
               "input line 2\n" +
               "input line 3\n";
    BufferedWriter writer = new BufferedWriter(new FileWriter(file1));
    writer.write(s);
    writer.close();
    writer = new BufferedWriter(new FileWriter(file2));
    writer.write(s);
    writer.close();
  }

  protected void tearDown() throws Exception {
    File file = new File(file1);
    file.delete();
    file = new File(file2);
    file.delete();
    file = new File(testFile);
    file.delete();
    super.tearDown();
  }

  public void testCompare() throws FileNotFoundException, IOException {
    assertTrue(FileUtility.compare(file1, file2));
  }

  public void testDelete() {
      FileUtility.delete(file1);
      FileUtility.delete(file2);
    File test1 = new File(file1);
    File test2 = new File(file2);
    assertTrue(!test1.exists());
    assertTrue(!test2.exists());
  }

  public void testCreate() throws Exception {
      FileUtility.createFile(testFile, "this is line 1\nthis is line 2");
    File test = new File(testFile);
    assertTrue(test.exists());
    BufferedReader reader = new BufferedReader(new FileReader(testFile));
    assertEquals(reader.readLine(), "this is line 1");
    assertEquals(reader.readLine(), "this is line 2");
  }

}
