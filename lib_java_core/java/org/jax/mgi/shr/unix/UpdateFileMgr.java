package org.jax.mgi.shr.unix;

import java.io.*;
import java.util.*;

public class UpdateFileMgr
{
	/* Concept:
		IS: An object that invokes a bourne shell command to process 
		    files that it has determined to be unprocessed
		HAS: a command 
		     a list of all input files that have been processed - 
			a file containing 1 file (full path) per line
		     an input file directory 
		DOES: determines which files in the input directory have not 
                         been processed
		      Completes the command, inserting the files to process into 			 the command
	              Runs the command; all the files are processed by one
			 invocation of the command
		      Updates the list of processed files
		Implementation:
	*/
	//
	// constructor
	//
	
	public UpdateFileMgr (
		String cmd, 	   	   // a command to run
		String inputPlaceHolder,   // replace this string in 'cmd'
					   // with input file list
		File inputDataDir,	   // full path to dir where input files
					   // reside
		File processedFilesList)  // full path to file listing already
					   // processed files in 'inputDataDir'	
	{
		// Purpose: initialize instance variables
		// Effects: None
		// Throws: Nothing
		this.cmd = cmd;
		this.inputPlaceHolder = inputPlaceHolder;
		this.inputDataDir = inputDataDir;
		this.processedFilesList = processedFilesList;
	}
	
	// 
	// public methods
	// 

	public void go() throws IOException, InterruptedException
	{
		// Purpose: Determine the set of input files for 'this.cmd'
		//          Complete 'this.cmd' with those input files
		//   	    Run 'this.cmd'
		//	    Update the processed files list
                // Returns: nothing
                // Assumes: nothing
                // Effects: variable depending on the nature of 'this.cmd' -
		//          most cmds create files. 
                // Throws: IOException with appropriate message if failed or
                //          interrupted IO operations or RunCommand error 
                //         InterruptedException if RunCommand is
                //          interrupted by another thread

		// get the listing of files in 'this.inputDataDir'
		File[] currentFiles = this.getCurrentFiles();

		// get the listing of files (File objs representing full paths)
		// from 'this.processedFilesList' 
		Vector processedFiles = this.calcProcessedFiles();

		// get the list of files to process
		this.filesToProcess = this.getFilesToProcess(currentFiles, 
						processedFiles);
		if(this.filesToProcess.length() != 0)
		{	
			// complete the command with 'this.filesToProcess'
			this.completeCommand();

			// process the unprocessed input files
			this.processFiles();
			
			// if no errors update the list of processed files
			if(this.exitcode == 0)
			{
				this.updateProcessedFilesList();
			}
		}
		else
		{
			this.stdout = "UpdateFileMgr: No files to process";
		}
	}

	public int getExitCode()
		// Purpose: accessor for the exit code from running 'this.cmd'
		// Returns: int, the exit code from running 'this.cmd'
                // Assumes: nothing
                // Effects: nothing
                // Throws:  nothing
        {
                return this.exitcode;
        }

	public String getStdOut()
		// Purpose: accessor for stdout from running 'this.cmd'
		// Returns: String, stdout from 'this.cmd'
                // Assumes: nothing
                // Effects: nothing
                // Throws:  nothing
	{
		return this.stdout;
	}

	public String getStdErr()
		// Purpose: accessor for stderr  from running 'this.cmd'
		// Returns: String, stderr from 'this.cmd'
                // Assumes: nothing
                // Effects: nothing
                // Throws:  nothing
        {
                return this.stderr;
        }

	public String getCmd()
		// Purpose: accessor for 'this.cmd'
		// Returns: String, if called before go(), returns the command
                //           with the input placeholder, else returns the
                //           completed command with input files in place of
                //           the input placeholder
                // Assumes: nothing
                // Effects: nothing
                // Throws:  nothing

	{
		return this.cmd;
	}
	
	public String getProcessedFiles()
		// Purpose: get the list of files that were run thru 'this.cmd'
		//          after last invocation of go(). 
		// Returns: String of abs file paths space separated 
		//          or empty string 
		// Assumes: nothing
                // Effects: nothing
                // Throws:  nothing
        {
                return this.filesToProcess;
        }

	private void completeCommand()
	{
		// Purpose: Replace 'this.inputPlaceHolder' in 'this.cmd'  
		//         with 'this.filesToProcess'
		// Returns: nothing
		// Assumes: nothing
                // Effects: nothing
                // Throws:  nothing
 
                // There is no easy way to find and replace in Java

                // find the index of first occurence of 'this.inputPlaceHolder'
                int start = this.cmd.indexOf(this.inputPlaceHolder);

                // calculate the end index
                int end = start + this.inputPlaceHolder.length();

                // need to convert to StringBuffer to replace()
                StringBuffer cmd = new StringBuffer(this.cmd);
                cmd.replace(start, end, this.filesToProcess);
		
		// update 'this.cmd'
		this.cmd = cmd.toString();
	}

	private void processFiles()
		throws IOException, InterruptedException
	{
		// Purpose: Run 'this.cmd'
                // Returns: nothing
                // Assumes: nothing
                // Effects: variable depending on nature of 'this.cmd' -
		//          most commands create files
                // Throws: IOException with appropriate message if failed or
		//         interrupted IO operations or RunCommand error 
                //         InterruptedException if RunCommand is
                //          interrupted by another thread
		
		RunCommand runner = RunCommand.runCommand(this.cmd);
		this.exitcode = runner.getExitCode();
		this.stdout = runner.getStdOut();
		this.stderr = runner.getStdErr();
	}

	private File[] getCurrentFiles()
	{
		// Purpose: get a listing of all files in 'this.inputDataDir'
                // Returns: An array of File objects denoting the full path to
		//          files in 'this.inputDataDir'.
                // Assumes: nothing
                // Effects: nothing
                // Throws: nothing

		return this.inputDataDir.listFiles();
	}	

	private Vector calcProcessedFiles() throws IOException
	{

		// Purpose: read this.processedFilesList into a Vector -
		//           we don't know how many files we'll have
                // Returns: a Vector of file objects
                // Assumes: this.processedFilesList exists
                // Effects: nothing
                // Throws: IOException if failed or interrupted IO operations
		
		// a reader to read 'this.processedFilesList'
		BufferedReader in = new BufferedReader( 
			new FileReader(this.processedFilesList));

		// a Vector to hold all files in 'this.processedFilesList'
		Vector processedFiles = new Vector();

		// priming read 
		String line;
		line = in.readLine();
		
		// read until EOF, creating file objects and placing in Vector
		while (line != null)
		{
			line = line.trim();
			processedFiles.add(new File(line));
			line = in.readLine();
		}
		in.close();

		return processedFiles;
	}

	private String getFilesToProcess(
			File[] currentFiles,   // full paths to files in
					       //    'this.inputDataDir'
			Vector processedFiles) // full paths to files already
					       //    processed
			throws IOException
	{
		// Purpose: get the files to process (all files from 'current
		//          files' not in 'processedFiles') and make sure they
		//          are readable
                // Returns: String of space-delimited abs file paths
                // Assumes: nothing
                // Effects: nothing
                // Throws: IOException if failed or interrupted IO operations
		
		// create a StringBuffer denoting the set of files to process
		StringBuffer files = new StringBuffer();
		for (int i = 0; i < currentFiles.length; i++)
		{
			if( ! processedFiles.contains(currentFiles[i]))
			{
				// append full path to the file
				if(currentFiles[i].canRead())
				{
					files.append(" " + 
						currentFiles[i].toString());
				}
				else 
				{
					throw new IOException("UpdateFileMgr " +
					"Error: unreadable file: " + 
					currentFiles[i].toString());
				}
			}
		}
		return files.toString();
	}

	private void updateProcessedFilesList()
			throws IOException
	{
		// Purpose: Append 'this.processedFilesList' with each file in
		//          'this.filesToProcess'
                // Returns: nothing
                // Assumes: nothing
                // Effects: writes to this.processedFilesList
                // Throws: IOException if failed or interrupted IO operations
		
		// File object for filenames in 'this.filesToProcess
		File processedFile;

		// open the FilesProcessed file in append mode
		BufferedWriter out  = new BufferedWriter(
                        new FileWriter(
			this.processedFilesList.toString(), true));	
			
		// split 'this.filesToProcess' into indiv file tokens
		StringTokenizer fileNameTokens = new StringTokenizer(
				this.filesToProcess);

		// write the filename to the FilesProcessed file
		while(fileNameTokens.hasMoreElements())
                {
			out.write(fileNameTokens.nextToken() + this.NL);
		}

		out.close();

	}

	//
	// instance variables
	//		
	
	// cmd to process input files
	private String cmd;
	
	// full path to directory containing input files
	private File inputDataDir;

	// full path to file listing already processed files in 
        // 'this.inputDataDir'
	private File processedFilesList;

	// String in 'this.cmd' to replace with 'this.filesToProcess'
	private String inputPlaceHolder;
	
	// output from running this.cmd
	private int exitcode = 0;
	private String stdout = "";
	private String stderr = "";

	// Space delimited string of abs file names
	private String filesToProcess = "";

	// misc globals - should have a utility class for such things
	private String NL = "\n";
	private String TAB = "\t";
}
