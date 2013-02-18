package org.jax.mgi.shr.ioutils;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * An object which provides file handling functions such as create,
 * delete and copy
 * @has nothing.
 * @does provides static file handling methods
 * @company Jackson Laboratory
 * @author M. Walker
 */


public class FileUtility {

    // the following are the exceptions that are thrown
    private static final String FileOpenErr =
        IOUExceptionFactory.FileOpenErr;
    private static final String FileReadErr =
        IOUExceptionFactory.FileReadErr;
    private static final String FileWriteErr =
        IOUExceptionFactory.FileWriteErr;
    private static final String FileCloseErr =
        IOUExceptionFactory.FileCloseErr;
    private static final String TransferErr =
        IOUExceptionFactory.TransferErr;
    private static final String FileCreateErr =
        IOUExceptionFactory.FileCreateErr;


  /**
   * deletes a file by the given name
   * @assumes nothing
   * @effects the named file will be removed from the system
   * @param filename the name of the file to delete
   */
    public static void deleteFile(String filename)
    {
        File file = new File(filename);
        file.delete();
    }

  /**
   * create a new empty file for the given filename
   * @assumes nothing
   * @effects  a new file will be created on the system
   * @param filename the name of the file to create
   * @throws IOUException thrown if there are error in io
   */

    public static void createFile(String filename)
    throws IOUException
    {
        File file = new File(filename);
        try
        {
            file.createNewFile();
        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory = new
                IOUExceptionFactory();
            IOUException e2 = (IOUException)
                exceptionFactory.getException(FileCreateErr, e);
            e2.bind(filename);
            throw e2;
        }
    }


  /**
   * copies file contents from one named file to another
   * @assumes nothing
   * @effects the contents of the named file will be copied from one file
   * to another
   * @param infilename the name of the file to copy contents from
   * object
   * @param outfilename the name of the file to copy contents to
   * @throws IOUException thrown if there are error with io
   */
    public static void copyFile(String infilename, String outfilename)
    throws IOUException
    {

        File infile = new File(infilename);
        File outfile = new File(outfilename);
        FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;
        try
        {
            sourceChannel = new FileInputStream(infile).getChannel();
        }
        catch (FileNotFoundException e)
        {
            IOUExceptionFactory exceptionFactory = new
                IOUExceptionFactory();
            IOUException e2 = (IOUException)
                exceptionFactory.getException(FileOpenErr, e);
            e2.bind(infilename);
            throw e2;
        }
        try
        {
            destinationChannel =
                new FileOutputStream(outfile).getChannel();
        }
        catch (FileNotFoundException e)
        {
           IOUExceptionFactory exceptionFactory = new
               IOUExceptionFactory();
           IOUException e2 = (IOUException)
               exceptionFactory.getException(FileOpenErr, e);
           e2.bind(outfilename);
           throw e2;
        }
        try
        {
            destinationChannel.transferFrom(sourceChannel, 0,
                                            sourceChannel.size());
        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory = new
                IOUExceptionFactory();
            IOUException e2 = (IOUException)
                exceptionFactory.getException(TransferErr, e);
            e2.bind(infilename);
            e2.bind(outfilename);
            throw e2;
        }
        try
        {
            sourceChannel.close();
        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory = new
                IOUExceptionFactory();
            IOUException e2 = (IOUException)
                exceptionFactory.getException(FileCloseErr, e);
            e2.bind(infilename);
            throw e2;
        }
        try
        {
            destinationChannel.close();
        }
        catch (IOException e)
        {
            IOUExceptionFactory exceptionFactory = new
                IOUExceptionFactory();
            IOUException e2 = (IOUException)
                exceptionFactory.getException(FileCloseErr, e);
            e2.bind(outfilename);
            throw e2;
        }

    }

}
