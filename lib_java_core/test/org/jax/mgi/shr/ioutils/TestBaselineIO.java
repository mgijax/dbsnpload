
package org.jax.mgi.shr.ioutils;

import junit.framework.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

import org.jax.mgi.shr.timing.Stopwatch;

public class TestBaselineIO extends TestCase {

  DataInput1 fileCreator = null;
  DataCompare1 dataCompare = null;

  String inputFilename = "MmShort.data";
  String outputFilename = "MmShort1.data";
  String compareFilename = "MmShortCompare.data";
  int bufferSize = 512000;
  int cnt = 0;

  public static void main (String[] args) {
    //junit.textui.TestRunner.run (new TestBaselineIO("fileIO"));
    //junit.textui.TestRunner.run (new TestBaselineIO("fileNIO"));
    //junit.textui.TestRunner.run (new TestBaselineIO("frameworks1"));
    junit.textui.TestRunner.run (new TestBaselineIO("frameworks2"));
  }

  public TestBaselineIO(String s) {
    super(s);
  }

  protected void setUp() throws IOException {
    /*
    fileCreator = new DataInput1(inputFilename);
    dataCompare = new DataCompare1(compareFilename);
    fileCreator.createFile();
    dataCompare.createFile();
    */
  }

  protected void tearDown() {
    /*
    File file = new File(inputFilename);
    file.delete();
    file = new File(outputFilename);
    file.delete();
    */
  }

  public void fileIO()
  throws FileNotFoundException, IOException {

        BufferedReader in = new BufferedReader(new FileReader("Mm.data"));
        BufferedWriter out = new BufferedWriter(new FileWriter("Mm1.data"));
        String str;
        long startTime = System.currentTimeMillis();
        long endTime;
        long cnt = 0;
        while ((str = in.readLine()) != null) {
            out.write(str);
            out.newLine();
            endTime = System.currentTimeMillis();
            //System.out.println(endTime - startTime);
            startTime = System.currentTimeMillis();
            cnt++;
        }
        in.close();
        out.close();
        System.out.println("\ntotal cnt is " + cnt);
  }

  public void fileNIO()
  throws FileNotFoundException, IOException {
    FileInputStream fin = new FileInputStream("Mm.data");
    FileOutputStream fout = new FileOutputStream("Mm2.data");

    FileChannel in = fin.getChannel();
    FileChannel outc = fout.getChannel();

    ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
    while (true) {
      int ret = in.read( buffer );
      if (ret == -1)
        break;
      buffer.flip();
      outc.write(buffer);
      buffer.clear();
    }
  }

  public void frameworks1() throws Exception {
    BufferedWriter log = new BufferedWriter(new FileWriter("outfilename"));
    Stopwatch stopwatch = new Stopwatch();
    BufferedWriter out = new BufferedWriter(new FileWriter("Mm3.data"));
    InputDataFile in = new InputDataFile("Mm.data");
    RecordDataIterator it = in.getIterator();
    long startTime = System.currentTimeMillis();
    long endTime;
    long cnt = 0;
    stopwatch.start();
    while (it.hasNext()) {
      String s = (String)it.next();
      stopwatch.stop();
      log.write(cnt++ + " " + stopwatch.time());
      log.newLine();
      stopwatch.start();
      out.write(s);
      endTime = System.currentTimeMillis();
      //System.out.println(endTime - startTime);
      startTime = System.currentTimeMillis();
      cnt++;
    }
    it.close();
    out.close();
    System.out.println("total count is " + cnt);
  }

  public void frameworks2() throws Exception {
    BufferedWriter log = new BufferedWriter(new FileWriter("outfilename"));
    Stopwatch stopwatch = new Stopwatch();
    BufferedWriter out = new BufferedWriter(new FileWriter("Mm4.data"));
    InputDataFile in = new InputDataFile("Mm.data");
    byte[] matchSeq = {10,47,47};
    //in.setDelimiter(matchSeq);
    RecordDataIterator it = in.getIterator();
    long startTime = System.currentTimeMillis();
    long endTime;
    long cnt = 0;
    stopwatch.start();
    while (it.hasNext()) {
      String s = (String)it.next();
      stopwatch.stop();
      out.write(s);
      log.write(cnt++ + " " + stopwatch.time());
      log.newLine();
      stopwatch.start();
      endTime = System.currentTimeMillis();
      //System.out.println(endTime - startTime);
      startTime = System.currentTimeMillis();
      cnt++;
    }
    it.close();
    out.close();
    System.out.println("total count is " + cnt);
  }


}
