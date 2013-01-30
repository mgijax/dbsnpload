package org.jax.mgi.shr.ioutils;

import junit.framework.*;
import java.io.*;
import org.jax.mgi.shr.unitTest.FileUtility;
import org.jax.mgi.shr.log.ConsoleLogger;

public class TestRecordDataReader
    extends TestCase {
  private RecordDataReader recordDataReader = null;
  private String inputFilename = "Mm.data";
  private String inputFilename2 = "MmShort.data";
  private String inputFilename3 = "fasta.data";
  private String compareFilename = "MmCompare.data";
  private String compareFilename1 = "MmCompare.begindel.data";
  private String compareFilename2 = "MmShortCompare.data";
  private String compareFilename4 = "MmShortCompare.bothdel.data";
  private String compareFilename5 = "MmShortCompare.bothdel.regex.data";
  private String compareFilename6 = "fasta.begindel.regex.data";
  private String outputFilename = "MmTestFile.data";
  private DataInput1 dataInput1 = null;
  private DataInput2 dataInput2 = null;
  private DataInput3 dataInput3 = null;
  private DataCompare1 dataCompare1 = null;
  private DataCompare1a dataCompare1a = null;
  private DataCompare2 dataCompare2 = null;
  private DataCompare2a dataCompare2a = null;
  private DataCompare2b dataCompare2b = null;
  private DataCompare2c dataCompare2c = null;
  private DataCompare3 dataCompare3 = null;


  public TestRecordDataReader(String name) {
    super(name);
  }

  public static void main (String[] args) {
    //junit.textui.TestRunner.run (new TestRecordDataReader("testSingleLineRecord"));
    //junit.textui.TestRunner.run (new TestRecordDataReader("testMultiLineRecord"));
  }


  protected void setUp() throws Exception {
    super.setUp();
    dataInput1 = new DataInput1(inputFilename);
    dataInput2 = new DataInput2(inputFilename2);
    dataInput3 = new DataInput3(inputFilename3);
    dataCompare1 = new DataCompare1(compareFilename);
    dataCompare1a = new DataCompare1a(compareFilename1);
    dataCompare2 = new DataCompare2(compareFilename4);
    dataCompare2a = new DataCompare2a(compareFilename5);
    dataCompare2b = new DataCompare2b(compareFilename2);
    dataCompare3 = new DataCompare3(compareFilename6);
    dataInput1.createFile();
    dataInput2.createFile();
    dataInput3.createFile();
    dataCompare1.createFile();
    dataCompare1a.createFile();
    dataCompare2.createFile();
    dataCompare2a.createFile();
    dataCompare2b.createFile();
    dataCompare3.createFile();
  }

  protected void tearDown() throws Exception {
    dataInput1 = null;
    dataCompare1 = null;
    dataCompare1a = null;
    dataInput2 = null;
    dataCompare2 = null;
    dataCompare2b = null;
    dataCompare2c = null;
    dataCompare3 = null;
    FileUtility.delete(inputFilename);
    FileUtility.delete(inputFilename2);
    FileUtility.delete(compareFilename);
    FileUtility.delete(compareFilename1);
    FileUtility.delete(compareFilename2);
    FileUtility.delete(compareFilename4);
    FileUtility.delete(compareFilename5);
    FileUtility.delete(compareFilename6);
    super.tearDown();
  }

  public void testMultiLineRecord() throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    FileInputStream in = new FileInputStream(inputFilename);
    RecordDataReader rdr = new RecordDataReader(in.getChannel(), null, "^\\|\\|", "US-ASCII", 512000, new ConsoleLogger());
    while (rdr.hasNext()) {
      String s = rdr.next();
      out.write(s);
      out.write("-----------------------------------------------\n");
    }
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename));
  }

  public void testMultiLineRecordBytes() throws IOException {
      String del = "||";
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    FileInputStream in = new FileInputStream(inputFilename);
    RecordDataReader rdr = new RecordDataReader(in.getChannel(), null, del.getBytes(), "US-ASCII", 512000, new ConsoleLogger());
    while (rdr.hasNext()) {
      String s = rdr.next();
      if (s.getBytes().length > 1)
      {
          out.write(s);
          out.write("\n-----------------------------------------------");
      }
    }
    out.write("\n");
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename));
  }



  public void testSingleLineRecord() throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    FileInputStream in = new FileInputStream(inputFilename2);
    RecordDataReader rdr = new RecordDataReader(in.getChannel(), null, "$", "US-ASCII", 512000, new ConsoleLogger());
    while (rdr.hasNext()) {
      String s = rdr.next();
      out.write(s);
      out.write("-----------------------------------------------\n");
    }
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename2));
  }

  public void testStartByteDelimiter() throws IOException {
      String beginDel = "ID  ";
      String endDel = "\nSEQUENCE";
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    FileInputStream in = new FileInputStream(inputFilename2);
    RecordDataReader rdr =
        new RecordDataReader(in.getChannel(), beginDel.getBytes(),
                             endDel.getBytes(), "US-ASCII", 2, new ConsoleLogger());
    while (rdr.hasNext()) {
      String s = rdr.next();
      if (s != null)
      {
          out.write(s);
          out.write("-----------------------------------------------\n");
      }
    }
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename4));
  }

  public void testStartRegexDelimiter() throws IOException {
      String beginDel = "^ID";
      String endDel = "^SEQUENCE";
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    FileInputStream in = new FileInputStream(inputFilename2);
    RecordDataReader rdr =
        new RecordDataReader(in.getChannel(), beginDel,
                             endDel, "US-ASCII", 2, new ConsoleLogger());
    while (rdr.hasNext()) {
      String s = rdr.next();
      if (s != null)
      {
          out.write(s);
          out.write("-----------------------------------------------\n");
      }
    }
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename5));
  }



  public void testSmallInternalBufferSize() throws IOException {
      String del = "\n";
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
    FileInputStream in = new FileInputStream(inputFilename2);
    RecordDataReader rdr =
        new RecordDataReader(in.getChannel(), null, del.getBytes(), "US-ASCII", 2, new ConsoleLogger());
    while (rdr.hasNext()) {
      String s = rdr.next();
      out.write(s);
      out.write("-----------------------------------------------\n");
    }
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename2));
  }

  public void testBeginByteDelimiterOnly() throws Exception
  {
      String del = "ID          M";
      BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
      FileInputStream in = new FileInputStream(inputFilename);
      RecordDataReader rdr =
        new RecordDataReader(in.getChannel(), del.getBytes(), null, "US-ASCII", 2, new ConsoleLogger());
    while (rdr.hasNext())
    {
        String s = rdr.next();
        out.write(s);
        out.write("-----------------------------------------------\n");
    }
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename1));
  }

  public void testBeginRegexDelimiterOnly() throws Exception
  {
      String del = "^>";
      BufferedWriter out = new BufferedWriter(new FileWriter(outputFilename));
      FileInputStream in = new FileInputStream(inputFilename3);
      RecordDataReader rdr =
        new RecordDataReader(in.getChannel(), del, null, "US-ASCII", 2, new ConsoleLogger());
    while (rdr.hasNext())
    {
        String s = rdr.next();
        out.write(s);
        out.write("-----------------------------------------------\n");
    }
    out.close();
    rdr.closeResources();
    in.close();
    assertTrue(FileUtility.compare(outputFilename, compareFilename6));
  }






}
