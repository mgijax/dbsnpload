package org.jax.mgi.shr.unitTest;

import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * @is A class for providing on the fly test file creation and comparison
 * methods used in java unit testing.
 * @has static methods for general purpose file utilities to support
 * java unit testing.
 * @does provides methods for managing the lifecycle of test files used in
 * java unit testing programs along with providing general file utility methods
 * like file comparison.
 * @company Jackson Laboratory
 * @author M Walker
 */

public class FileUtility {

  /**
   * static method for creating a file with the given contents and name
   * @param name name of the file to create
   * @param contents the contents of the file in the form of a String
   * @throws IOException
   */
  public static void createFile(String name, String contents)
  throws IOException
  {
    BufferedWriter writer = null;
    writer = new BufferedWriter(new FileWriter(name));
    writer.write(contents);
    writer.close();
  }

  /**
   * deletes the given file
   * @param filename the filename to delete
   */
  public static void delete(String filename) {
    File file = new File(filename);
    file.delete();
  }

  /**
   * compares the contents of two files
   * @param filename1 the name of a file
   * @param filename2 the name of a second file
   * @return true only if the files exists and are exact
   * @throws IOException
   */
  public static boolean compare(String filename1, String filename2)
      throws IOException {
    File file1 = new File(filename1);
    File file2 = new File(filename2);
    BufferedReader r1 = new BufferedReader(new FileReader(file1));
    BufferedReader r2 = new BufferedReader(new FileReader(file2));
    String str1, str2;
    boolean compare = true;
    while ((str1 = r1.readLine()) != null) {
      str2 = r2.readLine();
      if (!str1.equals(str2)) {
        compare = false;
        break;
      }
    }
    r1.close();
    r2.close();
    return compare;
  }

  public static void copy(String srcFilename, String destFilename)
  throws FileNotFoundException, IOException
  {
      // create channel on the source
      FileChannel srcChannel =
          new FileInputStream(srcFilename).getChannel();
      FileChannel destChannel =
          new FileOutputStream(destFilename).getChannel();
      destChannel.transferFrom(srcChannel, 0, srcChannel.size());
      // close the channels
      srcChannel.close();
      destChannel.close();
  }

  /**
   * prints the contents of a file to stdout
   * @param filename the name of a file to print
   * @throws IOException
   */
  public static void print(String filename)
      throws IOException {
    File file = new File(filename);
    BufferedReader r = new BufferedReader(new FileReader(file));
    String str;
    while ((str = r.readLine()) != null) {
      System.out.println(str);
    }
    r.close();
  }


  /**
   * return whether the file exists
   * @param filename the name of the file to be tested for
   * @return true if the file exists and false otherwise
   */
  public static boolean doesExist(String filename) {
    File file = new File(filename);
    return file.exists();
  }

}
