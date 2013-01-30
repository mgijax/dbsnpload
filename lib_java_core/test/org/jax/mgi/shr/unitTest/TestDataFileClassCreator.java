package org.jax.mgi.shr.unitTest;

import junit.framework.*;
import java.io.*;

public class TestDataFileClassCreator
    extends TestCase {

  private String inputData = "input.data";
  private String outputData = "output.data";
  private String javaFile = "enter_class_name_here.java";
  private String javaClass = "enter_class_name_here.class";
  private BufferedWriter writer = null;


  public TestDataFileClassCreator(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    writer = new BufferedWriter(new FileWriter(inputData));
    writer.write("line one\n");
    writer.write("line two\n");
    writer.write("line three\n");
    writer.close();
    File file = new File(javaFile);
    file.delete();

  }

  protected void tearDown() throws Exception {
    File file;
    file = new File(inputData);
    file.delete();
    file = new File(javaFile);
    file.delete();
    file = new File(javaClass);
    file.delete();
    file = new File(outputData);
    file.delete();
    super.tearDown();
  }

  public void testCreateClass() throws Exception {
      DataFileClassCreator.createClass(inputData, javaFile, null);
    String cmd = "javac " + javaFile;
    System.out.println(cmd);
    Process proc = Runtime.getRuntime().exec(cmd);
    proc.waitFor();
    /* write output to standard out */
    InputStream out = proc.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(out));
    String s = null;
    while ((s = reader.readLine()) != null) {
      System.out.println(s);
    }
    System.out.println("\n");
    reader.close();

    int rtnValue = proc.exitValue();
    if (rtnValue != 0) {
      InputStream err = proc.getErrorStream();
      reader = new BufferedReader(new InputStreamReader(err));
      while ((s = reader.readLine()) != null) {
        System.out.println(s);
      }
      reader.close();
      throw new Exception("Could not compile class");
    }
    Class c = Class.forName("enter_class_name_here");
    DataFileWriter dataWriter = (DataFileWriter)c.newInstance();
    dataWriter.createDataFile(outputData);
    assertTrue(FileUtility.compare(inputData, outputData));
  }

}
