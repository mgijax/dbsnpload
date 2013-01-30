package org.jax.mgi.shr.ioutils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Iterator;

import org.jax.mgi.shr.config.OutputDataCfg;
import org.jax.mgi.shr.config.ConfigException;

/**
 * Is a class which reads application configuration properties to determine
 * a set of OutputDataFile classes to create and manage and provides static
 * methods for writing to them. The configuration parameters read should be
 * prefixed by an alias name to be later used when calling the static write
 * methods.
 * @has nothing
 * @does provides write access to a set of OutputDataFile instances configured
 * by the application configuration parameters
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public class OutputManager
{
    private static HashMap reports = new HashMap();

    /**
     * reads the configuration settings and initializes the OutputDataFile
     * instances
     * @assumes configuration settings have been set
     * @effects this instance will be initialized and ready for calling
     * write methods
     * @throws ConfigException thrown if there is an error accessing the
     * configuration file
     * @throws IOUException thrown if there is an error accessing the file
     * system
     */
    public static void initialize()
    throws ConfigException, IOUException
    {
        OutputDataCfg config = new OutputDataCfg();
        String[] parameters = config.getConfigurationParameters();
        for (int i = 0; i < parameters.length; i++)
        {
            String parm = parameters[i];
            int index = parm.indexOf("_OUTFILE_NAME");
            if (index > 0)
            {
                String alias = parm.substring(0, index);
                OutputDataFile file =
                    new OutputDataFile(new OutputDataCfg(alias));
                reports.put(alias, file);
            }
        }
    }

  /**
   * write text to the named OutputDataFile instance
   * @assumes nothing
   * @effects text will be written to the named OutputDataFile instance
   * @param alias the name of the OutputDataFile instance
   * @param text the text to write to the instance
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   * @throws IOUException thrown if there is an error accessing the file
   * system
   */
    public static void write(String alias, String text)
    throws ConfigException, IOUException
    {
        OutputDataFile file = (OutputDataFile)reports.get(alias);
        if (file == null)
        {
            file = new OutputDataFile(new OutputDataCfg(alias));
            reports.put(alias, file);
        }
        file.write(text);
    }

  /**
   * write text to the named OutputDataFile instance and include a newline
   * @assumes nothing
   * @effects text will be written to the named OutputDataFile instance
   * @param alias the name of the OutputDataFile instance
   * @param text the text to write to the instance
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   * @throws IOUException thrown if there is an error accessing the file
   * system
   */
    public static void writeln(String alias, String text)
    throws ConfigException, IOUException
    {
        write(alias, text + OutputDataFile.CRT);
    }


  /**
   * closes all the named OutputDataFile instances
   * @assumes nothing
   * @effects all OutputDataFile instances configured through the config file
   * will be closed
   * @throws ConfigException thrown if there is an error accessing the
   * configuration
   * @throws IOUException thrown if there is an error accessing the file
   * system
   */
    public static void close()
    throws ConfigException, IOUException
    {
        for (Iterator i = reports.entrySet().iterator(); i.hasNext();)
        {
            Entry entry = (Entry)i.next();
            OutputDataFile file = (OutputDataFile)entry.getValue();
            file.close();
        }
    }

}
