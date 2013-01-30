package org.jax.mgi.shr.config;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * @author marka
 *
 */
public class WebAppCfg extends Configuration {

    // this is the name of the parameter in the local config file 
    // that gives the location of the global config file.
    private static final String GLOBAL_CONFIG = "GLOBAL_CONFIG";

    /**
     * 
     * @param filename
     * @throws IOException
     * @throws FileNotFoundException
     */
    protected WebAppCfg(String filename) 
            throws IOException, FileNotFoundException {
        super(filename);
    }

    /** gets the Configuration object associated with the file at the given
     *  filename.  'fromCache' allows you to specify whether you want a cached
     *   copy of the file if one is available.
     * @param filename path to the configuration file to read
     * @param fromCache true if we can return the file from a memory cache, or
     *   false to force a reload (even if already cached)
     * @throws FileNotFoundException if the file cannot be found
     * @throws IOException if the file cannot be read and parsed properly
     */
     public static Configuration load (String filename, boolean fromCache)
         throws IOException, FileNotFoundException {
         
         // if the config file was already loaded, return previous read
         if (fromCache && WebAppCfg.loaded.containsKey (filename)) {
             return (Configuration) Configuration.loaded.get (filename);
         }
         
         // hold the configuration parameters being read in
         Configuration newOne = null;
         
         // initial read of local config file to get location of global file
         Configuration temp = new WebAppCfg(filename);
         
         // load global config, do not throw exception if it fails,
         // log error instead
         if (temp.hasKey(GLOBAL_CONFIG)){
             String globalConfig = temp.get(GLOBAL_CONFIG);            
             try {
                 // read global config into newOne
                 newOne = new WebAppCfg(globalConfig);
             } catch (Exception ex){
                 // log error
                 System.out.println("Error loading GLOBAL_CONFIG " + 
                         globalConfig);
             }
         }
         
         // if newOne is null, we need to keep the initial read, otherwise
         // use include() to add local config parameters to newOne.  
         if (newOne == null){
             // we were unable to load the global, keep initial local read
             newOne = temp;
         } else {
             // read local config options into newOne, local overwrites any
             // duplicate config parameters
             newOne.include(filename);
         }

         // save this read of config files
         WebAppCfg.loaded.put (filename, newOne);
         // return Configuration object
         return newOne;
     }
}
