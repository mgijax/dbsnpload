package org.jax.mgi.shr.unix;

import java.io.*;
import java.util.*;

public class BufferedLargeFileWriter 
{
	/*Concept:
                IS: A buffered FileWriter that can write files
		       larger than the 2G limit which is imposed by
		       the FileWriter class of the Java1.2 API
               HAS: A buffered character-file output stream. 
              DOES: Writes itself to a file. The close() method, which handles
		      mandatory cleanup chores, must be called. Returns a File 
		      object's size or true if a File object exists
 
	 Notes: 
		This class is not thread-safe
	 Implementation:
	 1) Explanation of implementation:
	 	The Java1.2 API 'zoo' of writers cannot handle large files. This
	 class was created to accomodate applications that need to open and
	 write > 2G files. The write() method writes to the output file
	 passed to the constructor (the 'ultimateOutputFile') until the 2G
	 limit is reached. write() then creates temp files for each subsequent
	 2G chunk written.
	 	The output file passed to the constructor is placed in
	 a Vector at position 0. Any temp files created are added to the 
	 Vector. When done writing, the close method must be called to close  
	 the writer and catenate all the files in the Vector to the 
	 'ultimateOutputFile'at Vector[0]. 
	 Cleanup of temp file objects is automatic.
	
	 2) Definitions: 
              ultimateOutputFile - the output file path passed to the 
                constructor
              ultimateOutputFileObj -  a File object which is an abstract
                representation of 'ultimateOutputFile'. We do this to have  
                access to its convenient methods for getting info about a
                file. In addition, objects can be conveniently stored in a 
                container such as a Vector.
              currentFile - There is one writer which is initially created 
                for the 'ultimateOutputFile'. For each temp file created the 
                writer must be re-assigned to write to it. We keep track of
                the most current output file by assigning it to 'currentFile'	
	
	 3) Unfortunately some of the File methods don't work on large files.
		Because of this we have resorted to running UNIX shell commands
	      using the MGI RunCommand class.	
	
         4) This class is written as a wrapper around rather than an
               extension of the BufferedWriter class. The
               BufferedWriter class may be wrapped around any Writer
               object therefore the overhead of overriding
               constructors and methods for all Writer objects would
               be large. We simply need a buffered writer object to
               write large files.
        */

	//
	//constructors
        //

	public BufferedLargeFileWriter(
		String ultimateOutputFile, // abs or relative output file name
		boolean append,		   // true = append, false = overwrite
		int sz)			   // size of the buffer
		throws IOException, InterruptedException
	{
		// Purpose: create writer object that uses an output buffer
                //          of size 'sz', which will write to 
		//	    'ultimateOutputFile', in 'append' mode
                // Throws:  IOException with appropriate message if failed or 
		//	     interrupted IO operations
		//          InterruptedException if the sub process is 
		//           interrupted by another thread

		// create a file object for 'ultimateOutputFile'. 
                File ultimateOutputFileObj = new File(ultimateOutputFile);	
	
		// set the size of the buffer
		this.bufferSize = sz;
	
		// if ultimateOutputFileObj not absolute resolve against 
		// current user directory (UNIX)
                if(ultimateOutputFileObj.isAbsolute() == false)
                {
                        ultimateOutputFileObj = 
				ultimateOutputFileObj.getAbsoluteFile();
                }
		
		// use 'ultimateOutputFileObj' directory for temp files
		this.writeDir = ultimateOutputFileObj.getParentFile();

		// place 'ultimateOutputFileObj' at fileVector[0]
                this.fileVector.add(ultimateOutputFileObj);

		// Check existence of 'ultimateOutputFile'
		boolean exists = BufferedLargeFileWriter.fileExists(
			ultimateOutputFileObj);
		
		// initialize size of 'ultimateOutputFile' 
                Long initialSize = new Long(0);

		// if 'ultimateOutputFile' exists we need to check its size
		if(exists == true)
		{
			initialSize = BufferedLargeFileWriter.getFileSize(
				ultimateOutputFileObj);
		}
		
		// the current file to write to
		File currentFile = ultimateOutputFileObj;

		// if 'ultimateOutputFile' exists and is >= max 
		// allowable filesize and we aren't appending. We must delete
		// ultimateOutputFile, can't overwrite large files (a 'feature'
		// of Java1.2.2) 
		if((initialSize.longValue() >= (long)this.MAX_FILE_SIZE) &&
			append == false)
		{
			// delete it
			ultimateOutputFileObj.delete();
		
			// the file to write to
			currentFile = ultimateOutputFileObj;
			this.currentFileSize = 0;
		}

		// if the ultimateOutputFile is > MAX create a temp file to 
		// write to	
		else if ((initialSize.longValue() >= (long)this.MAX_FILE_SIZE) &&
                        append == true)
                {
			// create a temp file
			File temp = createTemp();
				
			// add it to the File Vector
			this.fileVector.add(temp);

			// assign as the file to write to
			currentFile = temp;
			this.currentFileSize = 0;
			
                }

		// all other cases. If exists == false then initialSize = 0, 
		// else initialSize = size of the file, now set currentFileSize 
		else 
		{
			// set 'currentFileSize' to initial size of 
			// 'ultimateOutputFile'
			this.currentFileSize = initialSize.intValue();
		}

		// create the writer object for the current file
		// Note: there is no FileWriter constructor that takes a file
		// object *and* boolean append so we pass a string representing
		// the abs path to the file
                this.currentWriter = new BufferedWriter(new FileWriter(
			currentFile.getPath(), append), this.bufferSize);
	}

	public BufferedLargeFileWriter(
		String ultimateOutputFile, // abs or relative output file name
		boolean append) 	   // true = append false = overwrite
		throws IOException, InterruptedException
        {
                // Purpose: create writer object that uses the default buffer
                //          size for a BufferedWriter, and writes to 
                //          'ultimateOutputFile' in 'append' mode
		// Throws:  IOException with appropriate message if failed
		//	     or interrupted IO operations
		//          Interrupted Exception if sub process
                //           interrupted by another thread	
		
		// construct with first constructor passing default buffer size
		// for a BufferedWriter to first constructor
		this(ultimateOutputFile, append, 8192);
	}
	
	//
        //methods
        //

	public void write(String s) // The string to write
		throws IOException
	{
		// Purpose: Writes 's' to the current file. 
        	// Returns: nothing
        	// Assumes: currentWriter has been created and assigned to
		//           the current file
		//	    's' is a string < = 2^32 - 1 this is the size of 
		//            a Java1.2 int. String.substring()method params
		//	      must be ints
        	// Effects: writes to a file, possibly creates new File objects 
        	// Throws:  IOException with appropriate message if failed or 
		//           interrupted IO operations
		// create a new file if currentFileSize > than MAX allowed

		// Calculate how many bytes available in the current file
		int availBytes = this.MAX_FILE_SIZE - this.currentFileSize;

		// we can't write entire 's' to the current file so write
		// what we can, create new file(s) for the rest
		if (s.length() > availBytes)
		{
			// write to MAX_FILE_SIZE in current file 
			this.currentWriter.write(s.substring(0, availBytes));
			this.currentFileSize = this.currentFileSize + availBytes;

			//  remove what we just wrote from 's'
			s = s.substring(availBytes);

			// calc # MAX_FILE_SIZE temp files we need to write 's'
			int numNewFiles = s.length() / this.MAX_FILE_SIZE;

			// create and write 's' to numNewFiles number of files
			for (int i = 0; i < numNewFiles; i++)
			{	
				this.createWriterAndFile();
			
				this.currentWriter.write(s.substring(
				0, this.MAX_FILE_SIZE));

				// remove what we just wrote from 's'
				s = s.substring(this.MAX_FILE_SIZE); 

			}
			
			// currentFileSize is the max
			this.currentFileSize = this.MAX_FILE_SIZE;
		
			// write the remainder
			if (s.length() > 0)
			{
				this.createWriterAndFile();
				this.currentWriter.write(s);
				this.currentFileSize = s.length();
			}
			
			
		}
		// we can write entire 's' to current file
		else
		{
			this.currentWriter.write(s);
			this.currentFileSize = this.currentFileSize + s.length();
		}	

	}
	
	public void close() throws IOException, InterruptedException
	{
		// Purpose: close the BufferedLargeFileWriter object 
                // Returns: nothing
                // Assumes: BufferedLargeFileWriter object is open
                // Effects: temp files are auto removed on exit of the JVM - 
		//		see createTemp()
                // Throws: IOException with appropriate message if failed or
		//	    interrupted IO operations
		//         InterruptedException if the process is interrupted
		//          by another thread 
                // Notes: It is essential that every object of this class
		// 	  call this method because it catenates all temp files 
		//        to the 'ultimateOutputFile'
		
		// close the currentWriter
		this.currentWriter.close();

		// how many files do we have?
		int numFiles = this.fileVector.size();
		
		// if we have > 1 files catenate file 2 - n to ultimateOutput
		// File at fileVector[0]
		if(numFiles > 1)
		{
			// create a string of all the files to catenate
			StringBuffer fileString = new StringBuffer();
			
			for (int i = 1; i < numFiles; i++)
			{
				fileString.append(
					((File)this.fileVector.elementAt(i))
					.getPath() + " ");
			}
			// create command to cat the files
			String cmd = "/usr/bin/csh -c 'cat " +
				fileString.toString() + " >> " + 
				((File)this.fileVector.elementAt(0)).getPath() +
				"'";
			
			// run the command
			RunCommand runner = RunCommand.runCommand(cmd);
	
			// non-zero exit code throws exception
                        if(runner.getExitCode() != 0)
                                // throw exception with stderr
                                throw new IOException(runner.getStdErr());
			
		}
		
	}

	private void createWriterAndFile() throws IOException
	{
		// Purpose: Create a temp file, reassign currentWriter to it 
                // Returns: nothing
                // Assumes: fileVector has been initialized
                // Effects: Creates a new file
                // Throws: IOException with appropriate message if failed or 
		//	    interrupted IP operations

		// create a temp file
		File temp = this.createTemp();

		// add the temp file to the File Vector
		this.fileVector.add(temp);
		
		// close the currentWriter to flush buffer 
                this.currentWriter.close();

		// reassign currentWriter
		this.currentWriter = new BufferedWriter(
			new FileWriter(temp), this.bufferSize);

		// reset currentFileSize
		this.currentFileSize = 0;
	}

	private File createTemp() throws IOException 
	{
		// Purpose: create a temp file that will automatically be 
		//	    deleted upon exit of the JVM
                // Returns: a 'temporary' File object
                // Assumes: nothing
                // Effects: Creates a new file
                // Throws: IOException with appropriate message if failed or
		//          interrupted IO operations
                
		// create a temp file 
                File tempFile = File.createTempFile(
	                "temp.", null, this.writeDir);

                // auto delete tempfile on termination of VM
                tempFile.deleteOnExit();
		return tempFile;

	}
	public static boolean fileExists(File file) // abs file to check
	{
		// Purpose: determine if 'file' exists
                // Returns: boolean true if exists else false
                // Assumes: nothing
                // Effects: nothing
                // Throws: nothing
		// Note: The File.exists() method does not work on large files
		
		// get listing of directory where 'file' resides/will reside
		String []dirList = file.getParentFile().list();
		
		// check all filenames in the listing for the relative name
		// of 'filename
		for(int i = 0; i < dirList.length; i++)
                {
                	if(file.getName().equals(dirList[i]))
			return true;
                }
		// if we get here we didn't find it
		return false;
	}

	public static Long getFileSize(File file) // abs file to check
		throws IOException, InterruptedException
	{
		// Purpose: determine size of 'file' 
                // Returns: Integer - size of 'file'
                // Assumes: 'file' exists
                // Effects: nothing
                // Throws: IOException - if RunCommand has non-zero exit code
		// InterruptedException - if the RunCommand process is  
		//	interrupted by another thread

		// Unix command to obtain long listing of a file
		String cmd = "/usr/bin/ls -al " +  file.getPath();
		
		RunCommand runner = RunCommand.runCommand(cmd);
			
		// non-zero exit code throws exception
		if(runner.getExitCode() != 0)	
			// throw exception with stderr 
			throw new IOException(runner.getStdErr());

		// tokenize  stdout to find the length
		StringTokenizer tokenizedStdout = new StringTokenizer(
			runner.getStdOut() );

		// To count how many dummy tokens to collect
		int ctr = 1;

		// To collect all tokens in stdout we don't need
		String dummy;

		// collect all tokens prior to the filesize token. Sorry but
		// StringTokenizer class doesn't have method to grab nth token
		while( ctr < 5)
		{
		dummy = tokenizedStdout.nextToken();
		ctr++;
		}

		// the next token is the filesize (a string) convert it Integer 
		// because we can't convert it to an int (primitive) 
		return new Long(tokenizedStdout.nextToken());
	}

	//
        //instance variables
        //
	
	// writer for current output file
	private BufferedWriter currentWriter;

	// container for all output File objects
	private Vector fileVector = new Vector();
	
	// where ultimateOutputFile resides and temp files will be created 
	private File writeDir;

	// maximum output file size of 2G
	private final int MAX_FILE_SIZE = (int)(2*(Math.pow(2, 30)));

	// file size of the current output file. 
	private int currentFileSize = 0;
	
	// buffer size of currentWriter
	private int bufferSize;
}
