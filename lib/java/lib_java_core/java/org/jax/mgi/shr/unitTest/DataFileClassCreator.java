package org.jax.mgi.shr.unitTest;

import java.io.File;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @is An application that can convert any data file into a file writer
 * class which contains a method to recreate the data file during unit testing.
 * The application is run as follows:
 * java org.jax.mgi.shr.unitTest.DataFileClassCreator <input filename> <output classname> [package name].
 * The input file is the data file to be used. The output file is the java source file
 * the application will create and the package is the name of the package the
 * compiled class will belong to. This newly created source file is compiled and then
 * the resulting class is subsequently used in a unit test program for recreating the
 * data file on the fly.
 * @has an input file
 * @does creates a java source file for compiling into a class that can
 * recreate the input file on demand.
 * @company Jackson Laboratory
 * @author M Walker
 */

public class DataFileClassCreator {

  public static void main(String args[]) throws IOException {
    if (args == null || args.length < 2) {
      usage();
      System.exit(1);
    }
    if (args.length > 2)
      createClass(args[0], args[1], args[2]);
    else
      createClass(args[0], args[1], null);
  }

  /**
   * default constructor
   */
  public DataFileClassCreator() {
  }

  /**
   * create a java source file which can be compiled into a java class which
   * can subsequently be used to recreate a known data file.
   * @param inParm the input file which is eventually recreated on the
   * call to the resultant class created by this method.
   * @param outParm the name of the source file which is to be created
   * @param pkgParm the name of the package that the resultant class should be
   * placed within.
   * @throws IOException
   */
  public static void createClass(String inParm, String outParm, String pkgParm)
  throws IOException
  {
    String className = outParm.replaceAll("\\.java", "");
    File inFile = new File(inParm);
    File outFile = new File(outParm);
    if (outFile.exists()) {
      System.out.println("A file named " + outParm + " already exists");
      System.exit(1);
    }
    if (!inFile.exists()) {
      System.out.println("A file named " + inParm + " does not exists");
      System.exit(1);
    }
    BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
    BufferedReader in = new BufferedReader(new FileReader(inFile));
    String s = null;
    writeHead(out, className, pkgParm);
    writeFile(out, in);
    writeTail(out);
    in.close();
    out.close();

  }

  /**
   * standard usage message
   */
  private static void usage() {
    System.out.println("java DataFileClassCreator <input filename> <output filename> [package name]");
  }

  /**
   * writes the head of the java source code which includes the package definition,
   * import statements and the beginning of the class definition.
   * @param writer a BufferedWriter to write to
   * @param className the name of the class that is being created
   * @param pkgName the name of the package which the class should belong to
   * @throws IOException
   */
  private static void writeHead(BufferedWriter writer, String className,
                                String pkgName) throws IOException {
    if (pkgName != null)
      writer.write("package " + pkgName + ";\n\n");
    writer.write("import java.io.BufferedWriter;\n");
    writer.write("import java.io.FileWriter;\n");
    writer.write("import java.io.IOException;\n");
    writer.write("import org.jax.mgi.shr.unitTest.DataFileWriter;\n\n");
    writer.write("public class " + className + " implements DataFileWriter \n");
    writer.write("{\n");
    writer.write("   private String name = null;\n\n");
    writer.write("   public " + className + "() {}\n\n");
    writer.write("   public void createDataFile(String name) throws IOException\n");
    writer.write("   {\n");
    writer.write("       BufferedWriter writer = \n");
    writer.write("         new BufferedWriter(new FileWriter(name));\n\n");
  }

  /**
   * writes the tail of the java source code.
   * @param writer a BufferedWriter to write to
   * @throws IOException
   */
  private static void writeTail(BufferedWriter writer) throws IOException {
    writer.write("   }\n");
    writer.write("}\n");
  }

  /**
   * write the part of the java source code which performs the writing of the
   * data file.
   * @param writer a BufferedWriter to write to
   * @param reader a BufferReader for reading the input file
   * @throws IOException
   */
  private static void writeFile(BufferedWriter writer, BufferedReader reader)
  throws IOException {
    String s1 = null;
    String s2 = null;
    while ((s1 = reader.readLine()) != null) {
      s2 = s1.replace('"', '\047');
      writer.write("      writer.write(\"" + s2 + "\\n\");\n");
    }
    writer.write("      writer.close();\n");
  }

}
