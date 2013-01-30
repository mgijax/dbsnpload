package org.jax.mgi.app.dbsnploader;

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
     * @param prefix prepend this prefix on all parameters
     * @throws ConfigException if a configuration manager cannot be obtained
     */
    public DBSNPLoaderCfg(String prefix) throws ConfigException {
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
     * If true, deleted all SNP accession Ids.
     * @return whether to deleted SNP accession ids
     * @throws ConfigException if "SNP_OK_TO_DELETE_ACCESSIONS" not
     * found by the Configurator
     */

    public Boolean getOkToDeleteAccessions() throws ConfigException {
        return getConfigBoolean("SNP_OK_TO_DELETE_ACCESSIONS", Boolean.FALSE);
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
     public String[] getTruncateSnpTables() {
         return getConfigStringArrayNull("DLA_TRUNCATE_SNP_TABLES");
     }

     /**
      * get list of tables for which to update SNP..MGI_Tables
      * @return list of table name for which to update SNP..MGI_Tables
      */
     public String[] getUpdateMGITables() {
	String[] tableNames = getConfigStringArrayNull("SNP_UPDATE_MGITABLES");
	/*
    	 String[] tableNamesLower = new String[tableNames.length];
    	 // fix casing for postgres
    	 for(int i=0;i<tableNames.length;i++)
    	 {
    		 tableNamesLower[i] = tableNames[i].toLowerCase();
		System.out.println("tablename = "+tableNames[i].toLowerCase());
    	 }
	*/
         return tableNames;

     }
     /**
      * get the name of the SQLStream for snp data (the snp stream)
      * @return the name of the SQLStream
      */
     public String getSnpStreamName() {
         return getConfigString("SNP_STREAM",
                                "org.jax.mgi.shr.dbutils.dao.Bcp_Stream");
     }
     /**
     * get the current Mouse Genome Build number
     * @return the current Mouse Genome Build number
     */
    public String getGenomeBuildNum() throws ConfigException {
        return getConfigString("GENOME_BUILD");
    }

    /**
     * get the max allowable coordinates for a snp on a single chr
     * @return max allowable coordinates
     */
    public Integer getMaxChrCoordCt() throws ConfigException {
        return getConfigInteger("MAX_CHR_COORD_CT");
    }
    /**
    * get the name of the file to report snps not loaded
    * @return the name of the file to report snps not loaded
    */
   public String getSnpNotLoadedFileName() throws ConfigException {
       return getConfigString("SNP_NOTLOADED");
   }
   /**
   * get the text to report snps not loaded because multi chromosomes
   * @return the text for snps with multi chromosomes that are not loaded
   */
   public String getSnpNotLoadedMultChr() throws ConfigException {
       return getConfigString("SNP_NOTLOADED_MULTICHR");
   }

   /**
    * get the text to report snps not loaded because multi coords on same chromosome
    * @return the text to report snps not loaded because multi coords on same chromosome
    */
   public String getSnpNotLoadedMultiChrCoord() throws ConfigException {
       return getConfigString("SNP_NOTLOADED_MULTICHR_COORD");
   }
   /**
    * get the text to report snps not loaded because no BL6 coordinates
    * @returnthe text to report snps not loaded because no BL6 coordinates
    */
   public String getSnpNotLoadedNoBL6() throws ConfigException {
       return getConfigString("SNP_NOTLOADED_NO_BL6");
   }
   /**
    * get the text to report snps not loaded because no strain/alleles
    * @return the text to report snps not loaded because no strain/alleles
    */
   public String getSnpNotLoadedNoStrainAllele() throws ConfigException {
       return getConfigString("SNP_NOTLOADED_NO_STRAINALLELE");
   }
}
