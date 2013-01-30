package org.jax.mgi.shr.ioutils;

import junit.framework.*;
import org.jax.mgi.shr.config.*;
import java.io.*;
import java.util.Properties;
import org.jax.mgi.shr.unitTest.FileUtility;

public class TestInputDataFile
    extends TestCase {

  private InputDataFile inputFile = null;
  private String del = System.getProperty("file.separator");
  private String inFile1 = "test" + del + "MmShort.data";
  private String inFile2 = "test" + del + "Mm.data";
  private String inFile3 = "test" + del + "MmAllCaps.data";
  private String inFile4 = "test" + del + "fasta.data";
  private String compareFile1 = "test" + del + "MmShortCompare.data";
  private String compareFile2 = "test" + del + "MmCompare.data";
  private String compareFile3 = "test" + del + "MmAllCapsCompare.data";
  private String compareFile4 = "test" + del + "fastaCompare.data";
  private String compareFile5 = "test" + del + "MmShortCompare2.data";
  private String outputFilename = "MmTestFile.data";

  public TestInputDataFile(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    inputFile = null;
    super.tearDown();
  }

  public void testNoFilename() throws Exception {
    try {
      InputDataFile inputFile = new InputDataFile();
    }
    catch (IOUException e) {
      // this is what we expect with null filename
      assertTrue(true);
      return;
    }
    // we dont expect to get here
    assertTrue(false);
  }

  public void testNoDelimiters() throws Exception {
    System.setProperty("INFILE_NAME", inFile1);
    inputFile = new InputDataFile();
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    RecordDataIterator it = inputFile.getIterator();
    while (it.hasNext()) {
      String s = (String)it.next();
      out.write(s);
      out.write("-----------------------------------------------\n");
    }
    out.close();
    it.close();
    FileUtility util = new FileUtility();
    assertTrue(FileUtility.compare(outputFilename, compareFile1));
  }

  public void testEndDelimiterOnly() throws Exception {
    System.setProperty("INFILE_END_DELIMITER", "^\\|\\|");
    System.setProperty("INFILE_NAME", inFile2);
    System.setProperty("INFILE_USE_REGEX", "true");
    ConfigReinitializer.reinit();
    inputFile = new InputDataFile();
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    RecordDataIterator i = inputFile.getIterator();
    while (i.hasNext()) {
      String s = (String)i.next();
      out.write(s);
      out.write("-----------------------------------------------\n");
    }
    out.close();
    i.close();
    Properties p = System.getProperties();
    p.remove("INFILE_END_DELIMITER");
    p.remove("INFILE_USE_REGEX");
    p.remove("INFILE_NAME");
    ConfigReinitializer.reinit();
    assertTrue(FileUtility.compare(outputFilename, compareFile2));
  }

  public void testBeginEndDelimiters() throws Exception {
    System.setProperty("INFILE_BEGIN_DELIMITER", "ID          ");
    System.setProperty("INFILE_END_DELIMITER", "SEQUENCE");
    //System.setProperty("INFILE_USE_REGEX", "true");
    System.setProperty("INFILE_NAME", inFile1);
    ConfigReinitializer.reinit();
    inputFile = new InputDataFile();
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    RecordDataIterator i = inputFile.getIterator();
    while (i.hasNext()) {
      String s = (String)i.next();
      out.write(s);
      out.write("-----------------------------------------------\n");
    }
    out.close();
    i.close();
    Properties p = System.getProperties();
    p.remove("INFILE_BEGIN_DELIMITER");
    p.remove("INFILE_END_DELIMITER");
    //p.remove("INFILE_USE_REGEX");
    p.remove("INFILE_NAME");
    ConfigReinitializer.reinit();
    assertTrue(FileUtility.compare(outputFilename, compareFile5));
  }


  public void testNoDelimiters2() throws Exception {
    inputFile =
        new InputDataFile(inFile3);
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    Interpreter interp = new Interpreter();
    RecordDataIterator i = inputFile.getIterator(interp);
    while (i.hasNext()) {
      String s = (String)i.next();
      out.write(s);
      out.write("-----------------------------------------------\n");
    }
    out.close();
    i.close();
    assertTrue(FileUtility.compare(outputFilename, compareFile3));
  }

  public void testBeginDelimiterOnly() throws Exception {
      System.setProperty("INFILE_BEGIN_DELIMITER", "^>");
      System.setProperty("INFILE_USE_REGEX", "true");
      inputFile =
          new InputDataFile(inFile4);
      BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
      RecordDataIterator i = inputFile.getIterator();
      while (i.hasNext()) {
        String s = (String)i.next();
        out.write(s);
        out.write("-----------------------------------------------\n");
      }
      out.close();
      i.close();
      Properties p = System.getProperties();
      p.remove("INFILE_BEGIN_DELIMITER");
      p.remove("INFILE_USE_REGEX");
      ConfigReinitializer.reinit();
      assertTrue(FileUtility.compare(outputFilename, compareFile4));
  }


  public void testIterator1() throws Exception {
      FileUtility.createFile("testInput",
                             "this is line 1\nthis is line 2\nthis is line 3");
    inputFile =
        new InputDataFile("testInput");
    inputFile.setEndDelimiter("$");
    inputFile.setOkToUseRegex(true);
    RecordDataIterator i = inputFile.getIterator();
    // call to hasNext() should have no effect on call to next()
    i.hasNext();
    i.hasNext();
    i.hasNext();
    String s = (String)i.next();
    assertEquals(s, "this is line 1\n");
    s = (String)i.next();
    assertEquals(s, "this is line 2\n");
    s = (String)i.next();
    FileUtility.delete("testInput");
    assertEquals(s, "this is line 3");
  }

  public void testIterator2() throws Exception {
      FileUtility.createFile("testInput",
                             "this is line 1\nthis is line 2\nthis is line 3");
    inputFile =
        new InputDataFile("testInput");
    inputFile.setEndDelimiter("$");
    inputFile.setOkToUseRegex(true);
    RecordDataIterator i = inputFile.getIterator();
    int count = 0;
    while (i.hasNext()) {
      count++;
      i.next();
    }
    FileUtility.delete("testInput");
    assertEquals(count, 3);
  }

  public void testIterator3() throws Exception {
      FileUtility.createFile("testInput",
                             "this is line 1\nthis is line 2\nthis is line 3");
    inputFile =
        new InputDataFile("testInput");
    inputFile.setEndDelimiter("$");
    inputFile.setOkToUseRegex(true);
    RecordDataIterator i = inputFile.getIterator();
    int count = 0;
    while (i.hasNext()) {
      count++;
      i.next();
    }
    // at end of file, a call to next() should cause exception
    try {
      i.next();
    }
    catch (Exception e) {
      // this is what we expect
      assertTrue(true);
      return;
    }
    // we dont expect to make it here
    assertTrue(false);
    assertEquals(count, 3);
  }




  private class Interpreter implements RecordDataInterpreter {
    public Object interpret(String s) {
      return new String(s.toUpperCase());
    }
    public boolean isValid(String s) {
      return true;
    }
  }


}
