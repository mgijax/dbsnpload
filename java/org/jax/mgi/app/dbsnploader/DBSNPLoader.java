package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.timing.Stopwatch;
//import org.jax.mgi.shr.config.SequenceLoadCfg;
import org.jax.mgi.shr.ioutils.RecordDataIterator;
import org.jax.mgi.shr.dbutils.ScriptWriter;
import org.jax.mgi.shr.dbutils.DataIterator;
import org.jax.mgi.shr.config.ScriptWriterCfg;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.RecordFormatException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.MSException;
import org.jax.mgi.dbs.mgd.loads.SeqSrc.UnresolvedAttributeException;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.ioutils.XMLDataIterator;
import org.jax.mgi.shr.dla.loader.DLALoaderHelper;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.config.DatabaseCfg;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;
import org.jax.mgi.shr.config.BCPManagerCfg;
import org.jax.mgi.shr.dla.loader.DLALoaderException;
import org.jax.mgi.shr.dla.loader.DLALoaderExceptionFactory;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * is an object that parses DBSNP input files, resolves dbsnp values
 * to MGI values, and loads DBSNPs into a database
 * @has
 * radar and mgd streams to create bcp files and database records
 * DBSNPProcessor to resolve attributes and create radar and mgd DAOs
 * BufferedWriter to write coordinate feature information to a file
 * A set of chromosomes to determine which files to process
 * @does parses DBSNP input files, resolves DBSNP attributes to MGI values
 * and creates radar and mgd database objects
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPLoader extends DLALoader {

    // An SQL data manager for providing database access to the snp database
    protected SQLDataManager snpDBMgr;

    // A bcp manager for controlling the bcp writers for the snp database
     protected BCPManager snpBCPMgr;

     // the SQLStream used for loading snp data
     private SQLStream snpStream;

    // Resolves DBSNP attributes to MGI values and writes radar and mgd
    // bcp files
    private DBSNPInputProcessor dbsnpProcessor;

    // current number of RefSNPs processed
    private int snpCtr;

    // current number of RefSNPs with no strain alleles
    private int rsWithNoAllelesCtr;

    // current number of RefSNPs with no BL6 coordinates
    private int rsWithNoBL6Ctr;

    // current number of RefSNPs with multiple BL6 chromosomes
    private int rsMultiBL6ChrCtr;

    // current number of RefSNPs with no allele summary
    // (means no strain resolved or all alleles are 'N' or ' ')
    private int rsWithNoAlleleSummaryCtr;

    // current number of RefSNPs with unresolvable vocabularies
    private int rsWithVocabResolverExceptionCtr;

    // current number of RefSNPs repeated in input
    private int rsRepeatExceptionCtr;

    // list of chromosomes for which we want to parse chromosome files
    //private ArrayList chrList;
    private String[] chromosomes;

    // writer for CoordLoad input file
    //private BufferedWriter coordWriter;

    // load configurator
    private DBSNPLoaderCfg loadCfg;

    // configurator for genotype file configuration
    private DBSNPLoaderCfg genoConfig;

    // configurator for nse file configuration
    private DBSNPLoaderCfg nseConfig;;

    // SNP exception factory
    private SNPLoaderExceptionFactory snpEFactory;

    // file path separator
    private static final String PATH_SEPARATOR = System.getProperty("file.separator");

    /**
     * Initializes instance variables
     * @effects instance variables will be instantiated
     * @throws MGIException if errors occur during initialization
     */
    protected void initialize() throws MGIException {
        // configurator for the load
        loadCfg = new DBSNPLoaderCfg();
        String[] snpTables = loadCfg.getTruncateSnpTables();

        // configurators for each input file
        genoConfig = new DBSNPLoaderCfg("GENO");
        nseConfig = new DBSNPLoaderCfg("NSE");

        // get list of chromosomes for iterating thru Chr files
        chromosomes = loadCfg.getChromosomesToLoad();

        // create SQLStream for snp database
        snpDBMgr = new SQLDataManager(new DatabaseCfg("SNP"));
        snpDBMgr.setLogger(logger);
        snpBCPMgr = new BCPManager(new BCPManagerCfg("SNP"));
        snpBCPMgr.setLogger(logger);
        snpStream = createSQLStream(loadCfg.getSnpStreamName(),
                                   snpDBMgr, snpBCPMgr);
       // create snp processor
        dbsnpProcessor = new DBSNPInputProcessor(snpStream);

        // close radar connection provided by super class, don't need it
        qcDBMgr.closeResources();

        // create ExceptionFactory
        snpEFactory = new SNPLoaderExceptionFactory();
        try {
            logger.logdInfo("Truncating SNP tables", true);
            if (snpTables != null) {
                DLALoaderHelper.truncateTables(snpTables,
                                               snpDBMgr.getDBSchema(), logger);
            }
        }
        catch (Exception e) {
            throw new MGIException(e.getMessage());
        }

        // initialize all counters
        snpCtr = 0;
        rsWithNoAllelesCtr = 0;
        rsWithNoBL6Ctr = 0;
        rsMultiBL6ChrCtr = 0;
        rsWithNoAlleleSummaryCtr = 0;
        rsWithVocabResolverExceptionCtr = 0;
        rsRepeatExceptionCtr = 0;
    }

    /**
     * Performs load pre processing
     * @effects depending on configuration, SNP accessions and/or
     * the snp strain set is deleted
     * @throws MGIException if errors occur during preprocessing
     */
    protected void preprocess() throws MGIException {

        // delete accession records, note that truncating SNP tables is done at
        // the dla level via Configuration
        if(loadCfg.getOkToDeleteAccessions().equals(Boolean.TRUE)) {
            deleteAccessions();
        }
    }

    /**
     * Performs the DBSNP database load
     * @effects database records created
     * If stream is a BCP stream, creates bcp files which may be
     * temporary or persistent depending on configuration
     * @throws MGIException thrown if a fatal error occurs while performing the
     * load.
     */
    protected void run()  throws MGIException {
        /**
         * process chromosome files one at a time
         */
        //for (Iterator i = chrList.iterator(); i.hasNext(); ) {
        for (int i = 0; i < chromosomes.length; i++ ) {
            // reinitialize DBSNPProcessor structures for this chromosome
            dbsnpProcessor.reinitializeProcessor();

            // encourage the garbage collector
            System.gc();

            // get the next chromosome
            String chr = chromosomes[i].trim();
            logger.logdInfo("Processing chr " + chr, true);
            logger.logdInfo("Free memory: " + Runtime.getRuntime().freeMemory(), false);
            /**
             * process the genotype file for the Individual data
             */
            logger.logdDebug("Processing chr " + chr + ". Free memory: " +  Runtime.getRuntime().freeMemory());

            // create the genotype filename for this chromosome
            String genotypeFilename = genoConfig.getInfileDir()  +
                PATH_SEPARATOR +
                genoConfig.getInfilePrefix() + chr +
                genoConfig.getInfileSuffix();
            /**
             * process the genotype file for Individual data
             */
            logger.logdInfo("processing genotype file for Individual data " +
			    genotypeFilename + ". Free memory: " +
			    Runtime.getRuntime().freeMemory(), true);
            XMLDataIterator indivIterator =
                new DBSNPGenotypeIndividualInputFile(
				genotypeFilename).getIterator();
            while(indivIterator.hasNext() ) {
                dbsnpProcessor.processGenoIndivInput(
				(DBSNPGenotypeIndividualInput)indivIterator.
				next());
            }
            /**
             * process the genotype file for RefSNP data
             */

            logger.logdInfo("processing genotype file for RefSnp data " +
			    genotypeFilename + ". Free memory: " +
			    Runtime.getRuntime().freeMemory(), true);
            XMLDataIterator genoRefSNPIterator =
		    new DBSNPGenotypeRefSNPInputFile(
                 genotypeFilename, dbsnpProcessor.getIndividualMap()).
		    getIterator();
            while (genoRefSNPIterator.hasNext()) {
                dbsnpProcessor.processGenoRefSNPInput((DBSNPGenotypeRefSNPInput)
				genoRefSNPIterator.next());
            }

            // create the nse filename for this chromosome
            String nseFilename = nseConfig.getInfileDir()  +
                PATH_SEPARATOR +
                nseConfig.getInfilePrefix() + chr +
                nseConfig.getInfileSuffix();

            /**
             * process the NSE file
             */
            System.out.println("processing NSE file " + nseFilename);
            logger.logdInfo("processing " + nseFilename + ". Free memory: " +
			    Runtime.getRuntime().freeMemory(), true);
            XMLDataIterator it = new DBSNPNseInputFile(nseFilename).
		    getIterator();
            while (it.hasNext()) {
                DBSNPNseInput nseInput = (DBSNPNseInput)it.next();
                //logger.logdDebug("Processing " + nseInput.getRS().getRsId() +
                //". Free memory: " +  Runtime.getRuntime().freeMemory(), true);

                try {
                    dbsnpProcessor.processInput(nseInput);
                }
                catch (SNPNoStrainAlleleException e) {
                   // logger.logdInfo("No StrainAlleles: " +
		   // input.getRsId(), true);
                    rsWithNoAllelesCtr++;
                }
                catch (SNPNoBL6Exception e) {
                    rsWithNoBL6Ctr++;
                }
                catch (SNPMultiBL6ChrException e) {
                    rsMultiBL6ChrCtr++;
                }
                catch (SNPNoConsensusAlleleSummaryException e) {
                    rsWithNoAlleleSummaryCtr++;
                }
                catch (SNPVocabResolverException e) {
                    rsWithVocabResolverExceptionCtr++;
                }
                catch (SNPRepeatException e) {
                    rsRepeatExceptionCtr++;
                }
                snpCtr++;
            }
        }
    }


    /**
     * closes the load stream and reports load statistics.
     * @throws MGIException
     */
    protected void postprocess() throws MGIException
    {
        // do updates on MGI_dbinfo table in the 'snp' database
        updateMGIdbinfo();

        // do updates on MGI_Tables in the 'snp' database
        updateMGITables();

        // close snp and mgd streams
        logger.logdInfo("Closing snp stream", false);
        this.snpStream.close();

        // report load statistics
        reportLoadStatistics();
        logger.logdInfo("DBSNPLoader complete", true);
    }

    /**
     * deletes snp..SNP_Accession for  RefSNP, SubSNP and SubmitterSnp ids
     * @throws MGIException
     */
    private void deleteAccessions() throws MGIException {
        logger.logdInfo("Deleting Accessions", true);

        try {
            snpDBMgr.executeUpdate(
                    "select a._Accession_key " +
                    "into #todelete " +
                    "from SNP_Accession a " +
                    "where a._MGIType_key =  " + MGITypeConstants.CONSENSUSSNP +
                    " and a._LogicalDB_key = " + LogicalDBConstants.REFSNP +
                    " UNION " +
                    "select a._Accession_key " +
                    "from SNP_Accession a " +
                    "where a._MGIType_key =  " + MGITypeConstants.SUBSNP +
                    " and a._LogicalDB_key = " + LogicalDBConstants.SUBSNP +
                    " UNION " +
                    "select a._Accession_key " +
                    "from SNP_Accession a " +
                    "where a._MGIType_key =  " + MGITypeConstants.SUBSNP +
                    " and a._LogicalDB_key = " +
            LogicalDBConstants.SUBMITTERSNP);

            snpDBMgr.executeUpdate(
                "create index idx1 on #todelete(_Accession_key)");

            snpDBMgr.executeUpdate("delete SNP_Accession " +
                           "from #todelete d, SNP_Accession a " +
                           "where d._Accession_key = a._Accession_key");
       }
       catch (MGIException e) {
           SNPLoaderException e1 =
                   (SNPLoaderException) snpEFactory.getException(
                   SNPLoaderExceptionFactory.SNPDeleteAccessionsErr, e);
               throw e1;
       }
    }

    /**
     * Reports load statistics
     */
    private void reportLoadStatistics() {
        logger.logdInfo("Total RefSnps Looked at: " + snpCtr, false);
        logger.logdInfo("Total RefSnps with no Strain Alleles: " +
                        rsWithNoAllelesCtr, false);
        logger.logdInfo("Total RefSnps with no BL6 coordinates: " +
                        rsWithNoBL6Ctr, false);
        logger.logdInfo("Total RefSnps with multi chromosome BL6 coordinates: " +
                        rsMultiBL6ChrCtr, false);
        logger.logdInfo("Total RefSnps with no ConsensusSnpAlleleSummary: " +
                        rsWithNoAlleleSummaryCtr, false);
        logger.logdInfo("Total RefSnps with Vocab resolving errors: " +
                        rsWithVocabResolverExceptionCtr, false);
        logger.logdInfo("Total RefSnp records repeated in the input " +
			"(can be multiple per RefSnp): " +
                        rsRepeatExceptionCtr, false);
        for (Iterator i = dbsnpProcessor.getProcessedReport().iterator();
			i.hasNext(); ) {
            logger.logdInfo((String)i.next(), false);
        }
    }

    /**
      * writes updates to file for MGI_dbinfo table
      * @throws ConfigException
      * @throws DBException
      */
     private void updateMGIdbinfo() throws ConfigException, DBException {

         String dataVersion = loadCfg.getSnpDataVersion();

         // create updater for the snp database
         MGI_dbinfoUpdater snpUpdater = new MGI_dbinfoUpdater(SchemaConstants.SNP);

         // writes update out to script file
         snpStream.update((org.jax.mgi.dbs.snp.dao.MGI_dbinfoDAO)snpUpdater.
                 update(dataVersion));
     }

    /**
     * updates MGI_Tables reloaded by this load
     * @throws ConfigException
     * @throws DBException
     * @throws KeyNotFoundException
     * @throws CacheException
     */
    private void updateMGITables() throws ConfigException, DBException,
        KeyNotFoundException, CacheException{

        // get an updater for the 'snp' database
        MGI_TablesUpdater snpUpdater =
            new MGI_TablesUpdater(SchemaConstants.SNP, loadCfg.getJobstreamName());

        // get the set of SNP database tables to update
        String[] tables = loadCfg.getUpdateMGITables();

        // update each table
        for (int i = 0; i < tables.length; i++) {
            snpStream.update((org.jax.mgi.dbs.snp.dao.MGI_TablesDAO)snpUpdater.update(tables[i].trim()));
        }
    }
}
