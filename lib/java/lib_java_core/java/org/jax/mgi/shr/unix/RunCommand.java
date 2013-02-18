package org.jax.mgi.shr.unix;

import java.io.*;

/**
 * Is a Unix shell command to be run in a Bourne shell (by default).
 * @has A shell command and an optional environment.
 * @does Runs the command; captures stdout, stderr, and exit code, and
 * provides accessors for them.
 * @notes <pre>This is not thread safe.
 *  Usage:
 *  There are basically three ways to use this class:
 *   1) To just quickly run a command:
 *      use the static class method 'runCommand' which takes a command
 *      and an optional environment as parameter(s), runs the command,
 *      and returns an object of the class with which one can access
 *      stdout, stderr, and exitcode with accessor methods. e.g.
 *      Example:
 *          RunCommand runner = RunCommand.runCommand(cmd);
 *          int ec = runner.getExitCode();
 *          if(ec != 0)
 *           System.err.println(runner.getStdErr());
 *          else
 *           System.out.println(runner.getStdOut());
 *
 *   2) To create an object with a command to run in the future:
 *      explicitly create a RunCommand object passing
 *      a command and an optional environment as parameter(s).
 *      Use the run() method to run the command and return the
 *      exit code. Use the accessors for stdout and stderr to get
 *      their values. e.g.
 *      Example:
 *      RunCommand runner = new RunCommand(cmd);
 *      int ec = runner.run();
 *      use accessors for stdout and stderr if desired
 *
 *   3) To create an object and both set and run the command at some time
 *       in the future:
 *      create a default RunCommand object. Use setCommand(cmd) and
 *      optionally setEnv(envp). Use the run() method to run the
 *      command and return the exit code. Use the accessors for
 *      stdout and stderr. Note: setCommand() always resets
 *      the state of the object - see it's method header.
 *      Example:
 *          RunCommand runner = new RunCommand();
 *          runner.setCommand(cmd);
 *          runner.setEnv(envp);
 *          int ec = runner.run();
 *          use accessors for stdout and stderr if desired
 *   Implementation:
 *   Explanation of command and environment parameters
 *   1) the command parameter is a String
 *      e.g. String cmd = "ls -al /home/sc/work";
 *      This class passes 'cmd' to the Bourne shell (sh) by default.
 *      It does this by passing a String array to Runtime.exec() with
 *      "/usr/bin/sh" in position 0, "-c" in position 1, and 'cmd' in
 *      position 2. See the Java1.2 API docs for the exec method in the
 *      Runtime class for more information.
 *      If you'd like  to use another shell specify it as part of the
 *      command
 *      e.g. String cmd = "/usr/bin/csh -c
 *        'cat /home/sc/work/lib/*.java > testRC.out'";
 *      This will open a Bourne shell, then invoke the c-shell within it,
 *      then have csh execute the command specified after the -c
 *      Important: Note the single quotes surrounding 'cmd' after the -c
 *   2) the environment parameter:
 *      This is a String[] of environment settings in the format:
 *      name=value. e.g. {"PRINTER=bentley", "USER=sc"}
 *
 * </pre>
 *
 */



public class RunCommand
{

//Public Interface

    /**
     * default constructor
     */
    public RunCommand()
    {
        // Purpose: Creates a default RunCommand object.
        //	    default objects must use setCommand() and run()
        // Throws: nothing

    }

    /**
     * constructor which takes the command string
     * @assumes nothing
     * @param cmdStr the command string to execute
     */
    public RunCommand(String cmdStr)	// a specified system command
    {
        // Purpose: creates a RunCommand object to run command 'cmdStr'
        // Throws: nothing

        this.setCommand(cmdStr);
    }

    /**
     * constructor which takes command string and environment array
     * @param cmdStr the command string to run
     * @param envp the environment array where each array item is a string
     * of the form "name=value" and represents an environment setting
     */
    public RunCommand(String cmdStr,  // a specified system command
            String[] envp)    // Environment in which to run cmdStr
        {
                // Purpose: creates a RunCommand object to run command 'cmdStr'
        //          with environment 'envp'
                // Throws: nothing

        this.setCommand(cmdStr);
        this.envp = envp;
        }

        //
        // methods
    //

    /**
     * sets the command string to execute
     * @assumes current value of envp
     * @effects will reinitialize all instance variables except the
     * environment
     * @param cmdStr the command string to execute
     */
    public void setCommand(String cmdStr)	// a specified system command
    {
        this.cmd = cmdStr;
        this.cmdSet = true;
        this.cmdRun = false;
        this.stdout = null;
        this.stderr = null;
        this.exitcode = 0;
    }

    /**
     * set the envirnoment array
     * @param envp the environment array where each array item is of the form
     * "name=value" and represents an environment setting
     */
    public void setEnv(String[] envp)  // environment in which to run a
                       // command
    {
        // Purpose: init the environment.
                // Returns: nothing
                // Assumes: nothing
                // Effects: will overwrite any existing value
                // Throws: nothing

        this.envp = envp;
    }

    /**
     * create a RunCommand object with geven command string and run it
     * @param cmdStr the command string to run
     * @return  the newly created RunCommand
     * @throws IOException thrown if there is an error in IO
     * @throws InterruptedException thrown if there if the command was
     * interrupted from the command line
     */
    public static RunCommand runCommand(
                String cmdStr)	// specified system cmd
        throws IOException, InterruptedException
    {
        // Purpose: Create a RunCommand object with 'cmdStr' and run it
                // Returns: the object
                // Assumes: that there is no special environment
                // Effects: vary depending on the nature of  the command run
        // Throws: IOException with appropriate message if failed or
                //          interrupted IO operations
                //         InterruptedException if the new process is
                //          interrupted by another thread

        RunCommand runner = new RunCommand(cmdStr);
        runner.run();
        return runner;
    }

    /**
     * create a RunCommand object with given command string and
     * environment array and then run it
     * @param cmdStr the command string to run
     * @param envp the environment array where each item is of the form
     * "name=vale" and whcih represents an environment setting
     * @return  the newly created RunCommand
     * @throws IOException thrown if there is an error in IO
     * @throws InterruptedException thrown if there if the command was
     * interrupted from the command line
     */
    public static RunCommand runCommand(
                String cmdStr, // specified system cmd
                String[] envp) // environment in which to
                           // run 'cmdStr'
                throws IOException, InterruptedException
        {
        // Purpose: Create a RunCommand object with 'cmdStr' and run it
                // Returns: the object
                // Assumes: nothing
                // Effects: vary depending on the nature of  the command run
                // Throws: See runCommand(String cmdStr)

                RunCommand runner = new RunCommand(cmdStr, envp);
                runner.run();
                return runner;
        }

        /**
         * run the command
         * @return the command return code
         * @throws IOException thrown if there is an error in IO
         * @throws InterruptedException thrown if there if the command was
         * interrupted from the command line
         */
    public int run()
        throws IOException, InterruptedException
    {
        // Purpose: Create a new process, run this.cmd
        //          in new process and capture its output
                // Returns: an integer exit code
                // Assumes: if setEnv has not been called, this.envp = null
                // Effects: vary depending on the nature of  the command run
                // Throws: IOException with appropriate message if failed or
        //          interrupted IO operations or no command to run
        //	   InterruptedException if the new process is
        //          interrupted by another thread

        String line;
        BufferedReader in;

        // if cmd has been set - run it
        if(this.cmdSet == true)
        {
            // convert cmdStr to String array
                    String [] cmdArr = this.convertCmd();

                    // execute 'cmdArr' in a new process
                    Process proc = Runtime.getRuntime().exec(
                cmdArr, this.envp);

            // for reading stdout
            in = new BufferedReader(new InputStreamReader(
                // get the input stream connected to the std
                                // output stream of the subprocess 'proc'
                                proc.getInputStream() ));

            // read stdout from 'proc'
            while ((line = in.readLine()) != null)
                this.stdout = this.stdout + line + "\n";

            // reuse 'in' for reading stderr
            in = new BufferedReader(new InputStreamReader(
                // get the input stream connected to the error
                                // stream of the subprocess 'process'
                                proc.getErrorStream() ));

            //read stderr from 'proc'
             while ((line = in.readLine()) != null)
                        stderr = stderr + line + "\n";

                    // wait until that process has finished
                    proc.waitFor();

            // get the exit value of the subprocess 'proc'
                        this.exitcode = proc.exitValue();

                    this.cmdRun = true;

        }

        // command has not been set, raise an exception
        else
        {
            throw new IOException(
                "RunCommand Error: No command to run.");
        }

        return this.exitcode;
    }

    /**
     * get the standard ouput from the command
     * @assumes the command has been run
     * @effects none
     * @return the standard output from the command
     */
    public String getStdOut()
    {
        // Purpose: public interface to 'this.stdout'
        // Assumes: this.cmd has been run
        return this.stdout;
    }

    /**
     * get the standard error from the command
     * @assumes the command has been run
     * @effects none
     * @return the standard error from the command
     */
    public String getStdErr()
    {
        // Purpose: public interface to 'this.stderr'
        // Assumes: this.cmd has been run
        return this.stderr;
    }

    /**
     * get the exit code from the command
     * @assumes the command has been run
     * @effects none
     * @return the exit code
     */
    public int getExitCode()
    {
        // Purpose: public interface to 'this.exitcode'
        // Assumes: this.cmd has been run
        return this.exitcode;
    }

    /**
     * returns an indicator of whether or not the command has been run
     * @return true if the command has been run, false otherwise
     */
    public boolean hasRun()
        // Purpose: public interface to 'this.cmdRun'
    {
        return this.cmdRun;
    }

    // Private methods and instance variables
    private String[] convertCmd()
    {
        // Purpose: Create a cmd array to invoke 'cmd' in the
        //          Bourne shell
                // Returns: A String array representing 'cmd' to be invoked
        //	    in a Bourne shell
                // Assumes: nothing
                // Effects: nothing
                // Throws:

        return new String [] {"/usr/bin/sh", "-c", this.cmd};
    }

    //
        //instance variables
        //

    // Command to run
    private String cmd = null;

    // true if a command has been set
    private boolean cmdSet = false;

    // Array of name=value pairs representing environment variable settings
    private String[] envp = null;

    // true if command has been run
    private boolean cmdRun = false;

    // stdout from process running command
        private String stdout = null;

    // stderr from process running command
        private String stderr = null;

    // exit code from process running command
        private int exitcode = 0;
}

