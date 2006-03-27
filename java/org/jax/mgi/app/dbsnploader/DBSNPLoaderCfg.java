package org.jax.mgi.app.dbsnploader;

import java.sql.Timestamp;

import org.jax.mgi.shr.config.Configurator;
import org.jax.mgi.shr.config.ConfigException;

/**
 *
 * is an object that gets DBSNP load Configuration values
 * @has a configuration manager
 * @does provides methods to retrieve Configuration parameters that are
 *        specific the DBSNPLoader
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPLoaderCfg extends Configurator {

    /**
     * Default constructor
     * @throws ConfigException if a configuration manager cannot be obtained
     */

    public DBSNPLoaderCfg() throws ConfigException {
    }

     /**
     * Constructs a DBSNPLoader configurator to get Values for parameters
     * prefixed with 'prefix'
     * @throws ConfigException if a configuration manager cannot be obtained
     */
    public DBSNPLoaderCfg(String prefix) throws ConfigException{
        //System.out.println(prefix);
         super.parameterPrefix = prefix;
    }

     /**
     * Gets the  input file directory
     * @return The input file directory
     * @throws ConfigException if "SNP_INFILEDIR" not found by the Configurator
     */
    public String getInfileDir() throws ConfigException {
        return getConfigString("SNP_INFILEDIR");
    }

     /**
     * Gets the input file prefix
     * @return The input file prefix
     * @throws ConfigException if "SNP_INFILEPREFIX" not found by the
     * Configurator
     */

    public String getInfilePrefix() throws ConfigException {
        return getConfigString("SNP_INFILEPREFIX");
    }

     /**
      * Gets the input file suffix
      * @return The input file suffix
      * @throws ConfigException if "SNP_INFILESUFFIX" not found by the
      * Configurator
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
     * Gets the dbsnpload coordload filename (output of dbsnpload, input for
     *   coordload)
     * @return the coordload filename
     * @throws ConfigException if "COORD_OUTPUT_FILE" not found by the
     * Configurator
     */
    public String getCoordFilename() throws ConfigException {
        return getConfigString("COORD_OUTPUT_FILE");
    }
    /**
     * If true, deleted all SNP accession Ids.
     * @return whether to deleted SNP accession ids
     * @throws ConfigException if "SNP_OK_TO_DELETE_ACCESSIONS" not
     * found by the Configurator
     */

    public Boolean getOkToDeleteAccessions() throws ConfigException {
        return getConfigBoolean("SNP_OK_TO_DELETE_ACCESSIONS", Boolean.FALSE);
    }

    /**
     * If true, delete the dbsnp strain set.
     * @return whether to deleted SNP accession ids
     * @throws ConfigException if "SNP_OK_TO_DELETE_STRAINSET" not found by the
     * Configurator
     */

    public Boolean getOkToDeleteStrainSet() throws ConfigException {
        return getConfigBoolean("SNP_OK_TO_DELETE_STRAINSET", Boolean.FALSE);
    }

    /**
     * get list of chromomes to load.
     * @return list of chromosomes
     * @throws ConfigException if "SNP_CHROMOSOMES_TOLOAD" not found by the
     * Configurator
     */

    public String[] getChromosomesToLoad() {

        return getConfigStringArrayNull("SNP_CHROMOSOMES_TOLOAD");
    }
    /**
     * get dbSNP data version (build number)
     * @return dbSNP data version (build number)
     * @throws ConfigException if "SNP_DATAVERSIONk" not found by the
     * Configurator
     */

    public String getSnpDataVersion() throws ConfigException {
        return getConfigString("SNP_DATAVERSION");
    }

    /**
      * get the list of table names to truncate for the snp stream
      * @return the list of table names to truncate for the snp stream
      */
     public String[] getTruncateSnpTables()
     {
         return getConfigStringArrayNull("DLA_TRUNCATE_SNP_TABLES");
     }

     /**
      * get list of tables for which to update SNP..MGI_Tables
      * @return list of table name for which to update SNP..MGI_Tables
      */
     public String[] getUpdateMGITables() {
         return getConfigStringArrayNull("SNP_UPDATE_MGITABLES");

     }
     /**
      * get the name of the SQLStream for snp data (the snp stream)
      * @return the name of the SQLStream
      */
     public String getSnpStreamName()
     {
         return getConfigString("SNP_STREAM",
                                "org.jax.mgi.shr.dbutils.dao.Bcp_Stream");
     }
}
