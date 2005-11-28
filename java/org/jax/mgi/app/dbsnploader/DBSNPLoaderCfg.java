package org.jax.mgi.app.dbsnploader;

import java.sql.Timestamp;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 * @is an object that retrieves Configuration pararmeters for DBSNPLoader
 * @has Nothing
 *   <UL>
 *   <LI> a configuration manager
 *   </UL>
 * @does
 *   <UL>
 *   <LI> provides methods to retrieve Configuration parameters that are
 *        specific the DBSNPLoader
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DBSNPLoaderCfg extends Configurator {

    /**
     * Constructs a DBSNPLoader configurator
     * @assumes Nothing
     * @effects Nothing
     * @throws ConfigException if a configuration manager cannot be obtained
     */

    public DBSNPLoaderCfg() throws ConfigException {
    }

    public DBSNPLoaderCfg(String prefix) throws ConfigException{
        //System.out.println(prefix);
         super.parameterPrefix = prefix;
    }
    /**
     * Gets the  parser object
     * @assumes Nothing
     * @effects Nothing
     * @return The parser object
     * @throws ConfigException if "SNP_PARSER" not found by the Configurator
     */
    public Object getSnpParser() throws ConfigException {
        //System.out.println("ins getSNParser: " + super.getConfigPrefix());
        return getConfigObject("SNP_PARSER");
    }


     /**
     * Gets the  input file directory
     * @assumes Nothing
     * @effects Nothing
     * @return The input file directory
     * @throws ConfigException if "SNP_INFILEDIR" not found by the Configurator
     */
    public String getInfileDir() throws ConfigException {
        return getConfigString("SNP_INFILEDIR");
    }

     /**
     * Gets the input file prefix
     * @assumes Nothing
     * @effects Nothing
     * @return The input file prefix
     * @throws ConfigException if "DBSNP_INFILEPREFIX" not found by the Configurator
     */

    public String getInfilePrefix() throws ConfigException {
        return getConfigString("SNP_INFILEPREFIX");
    }

     /**
      * Gets the input file suffix
      * @assumes Nothing
      * @effects Nothing
      * @return The input file suffix
      * @throws ConfigException if "DBSNP_INFILESUFFIX" not found by the Configurator
      */

     public String getInfileSuffix() throws ConfigException {
         return getConfigString("SNP_INFILESUFFIX");
     }

    /**
     * Gets the Jobstream name
     * @return the Jobstream name
     * @throws ConfigException if "JOBSTREAM" not found by the Configurator
     */
    public String getJobstreamName() throws ConfigException {
        return getConfigString("JOBSTREAM");
    }
    /**
     * Gets the Jobstream key
     * @return the Jobstream name
     * @throws ConfigException if "JOBKEY" not found by the Configurator
     */
    public String getJobstreamKey() throws ConfigException {
        return getConfigString("JOBKEY");
    }
    /**
     * Gets the dbsnpload coordload filename (output of dbsnpload, input for
     *   coordload)
     * @return the coordload filename
     * @throws ConfigException if "COORD_OUTPUT_FILE" not found by the Configurator
     */
    public String getCoordFilename() throws ConfigException {
        return getConfigString("COORD_OUTPUT_FILE");
    }
    /**
     * If true, deleted all SNP accession Ids.
     * @return whether to deleted SNP accession ids
     * @throws ConfigException if "SNP_OK_TO_DELETE_ACCESSIONS" not found by the Configurator
     */

    public Boolean getOkToDeleteAccessions() throws ConfigException {
        return getConfigBoolean("SNP_OK_TO_DELETE_ACCESSIONS", Boolean.FALSE);
    }
    /**
     * get list of chromomes to load.
     * @return list of chromosomes
     * @throws ConfigException if "SNP_CHROMOSOMES_TOLOAD" not found by the Configurator
     */

    public String getChromosomesToLoad() throws ConfigException {
        return getConfigString("SNP_CHROMOSOMES_TOLOAD");
    }

}