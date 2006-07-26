package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.shr.ioutils.XMLDataIterator;
import org.jax.mgi.shr.dla.loader.DLALoaderHelper;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.config.DatabaseCfg;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;
import org.jax.mgi.shr.config.BCPManagerCfg;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;

import java.util.Iterator;
import java.util.Vector;

/**
 * is an object that parses DBSNP input files, resolves dbsnp attributes
 * to MGI values, and loads Consensus Snps into a database
 * @has
 * radar SQLStream - which we don't use(comes with DLALoader)
 * mgd SQLStream - for creating VocabLookups
 * snp SQLStream for creating bcp files and database records
 * DBSNPInputProcessor to resolve attributes and snp DAOs
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

    // Resolves DBSNP attributes to MGI values and writes
    // bcp files
    private DBSNPInputProcessor dbsnpProcessor;

    // current number of RefSNPs processed
    private int rsCtr;

    // current number of SubSnps for the RefSnps processed
    private int ssCtr;

    // current number of SNPs with no strain alleles
    private int rsWithNoAllelesCtr;

    // current number of SNPs with no BL6 coordinates
    private int rsWithNoBL6Ctr;

    // current number of SNPs with multiple BL6 chromosomes
    private int rsMultiBL6ChrCtr;

    // current number of SNPs with no allele summary
    // (means no strain resolved or all alleles are 'N' or ' ')
    private int rsWithNoAlleleSummaryCtr;

    // current number of SNPs with unresolvable vocabularies
    private int rsWithVocabResolverExceptionCtr;

    // current number of SNPs repeated in input
    private int rsRepeatExceptionCtr;

    // list of chromosomes for which we want to parse chromosome files
    private String[] chromosomes;

    // load configurator
    private DBSNPLoaderCfg loadCfg;

    // configurator for genotype file configuration
    private DBSNPLoaderCfg genoConfig;

    // configurator for nse file configuration
    private DBSNPLoaderCfg nseConfig;;

    // SNP exception factory
    private SNPLoaderExceptionFactory snpEFactory;

    // file path separator on this platform
    private static final String PATH_SEPARATOR = System.getProperty("file.separator");

    // holds one ChromosomeStats object per chromosome
    Vector chrStats;

    /**
     * Initializes instance variables
     * @effects instance variables will be instantiated
     * @throws MGIException if errors occur during initialization
     */
    protected void initialize() throws MGIException {
        // configurator for the load
        loadCfg = new DBSNPLoaderCfg();

        // the set of snp tables to truncate
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
        // Note we need the mgd connection to load the lookups
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
        rsCtr = 0;
        ssCtr = 0;
        rsWithNoAllelesCtr = 0;
        rsWithNoBL6Ctr = 0;
        rsMultiBL6ChrCtr = 0;
        rsWithNoAlleleSummaryCtr = 0;
        rsWithVocabResolverExceptionCtr = 0;
        rsRepeatExceptionCtr = 0;

        chrStats = new Vector();
    }

    /**
     * Performs load pre processing
     * @effects depending on configuration, SNP accessions are deleted
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
        for (int i = 0; i < chromosomes.length; i++ ) {
            // get the current time
            long startTime = System.currentTimeMillis();

            // create a statistics object for this chromosome
            ChromosomeStats stats = new ChromosomeStats();
            chrStats.add(stats);

            // reinitialize DBSNPProcessor structures for this chromosome
            dbsnpProcessor.reinitializeProcessor();

            // encourage the garbage collector
            System.gc();

            // get the next chromosome
            String chr = chromosomes[i].trim();
            long startFreeMem = Runtime.getRuntime().freeMemory();
            stats.setChromosome(chr);
            stats.setStartFreeMem(startFreeMem);
	    logger.logcInfo("Processing chr " + chr, true);
            logger.logdInfo("Processing chr " + chr, true);
            logger.logdInfo("Free memory: " + startFreeMem, false);
            /**
             * process the genotype file for the Individual data
             */

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
             * process the genotype file for SNP data
             */

            logger.logdInfo("processing genotype file for Snp data " +
			    genotypeFilename + ". Free memory: " +
			    Runtime.getRuntime().freeMemory(), true);
            XMLDataIterator genoSNPIterator =
		    new DBSNPGenotypeRefSNPInputFile(
                 genotypeFilename, dbsnpProcessor.getIndividualMap()).
		    getIterator();
            while (genoSNPIterator.hasNext()) {
                dbsnpProcessor.processGenoRefSNPInput((DBSNPGenotypeRefSNPInput)
				genoSNPIterator.next());
            }

            // create the nse filename for this chromosome
            String nseFilename = nseConfig.getInfileDir()  +
                PATH_SEPARATOR +
                nseConfig.getInfilePrefix() + chr +
                nseConfig.getInfileSuffix();

            /**
             * process the NSE file
             */
            long afterLookupFreeMem = Runtime.getRuntime().freeMemory();
            stats.setFreeMemAfterGenoLookup(afterLookupFreeMem);

            System.out.println("processing NSE file " + nseFilename);
            logger.logdInfo("processing " + nseFilename + ". Free memory: " +
			    afterLookupFreeMem, true);
            XMLDataIterator it = new DBSNPNseInputFile(nseFilename).
		    getIterator();
            int totalRsOnChr = 0;
            while (it.hasNext()) {
                totalRsOnChr++;
                DBSNPNseInput nseInput = (DBSNPNseInput)it.next();
                try {
                    dbsnpProcessor.processInput(nseInput);
                }
                catch (SNPNoStrainAlleleException e) {
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
                rsCtr++;
                ssCtr += nseInput.getSubSNPs().size();
            }
            stats.setTotalSnpsOnChr(totalRsOnChr);
            stats.setEndFreeMem(Runtime.getRuntime().freeMemory());
            // set elapsed time in minutes

            long endTime = System.currentTimeMillis();
            float elapsedTime = (endTime - startTime)/60000;
            System.out.println("Elapsed Time in minutes: " + elapsedTime);
            logger.logdInfo("Elapsed Time in minutes: " + elapsedTime, false);
            stats.setTimeToProcess(elapsedTime);
            int rsLoaded = dbsnpProcessor.getRsLoadedOnChrCtr();
            stats.setTotalRefSnpsLoaded(rsLoaded);
            int ssLoaded = dbsnpProcessor.getSsLoadedOnChrCtr();
            stats.setTotalSubSnpsLoaded(ssLoaded);
            logger.logdInfo("Total RS loaded on this chr: " + rsLoaded, false);
            logger.logdInfo("Total SS loaded on this chr: " + ssLoaded, false);
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
        if(loadCfg.getUpdateMGITables() != null) {
            updateMGITables();
        }

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
        logger.logdInfo("Total RefSnps Looked at: " + rsCtr, false);
        logger.logdInfo("Total SubSnps for the RefSnps Looked at: " + ssCtr, false);
        logger.logdInfo("Total RefSnp records repeated in the input " +
            "(can be multiple per RefSnp): " +
                        rsRepeatExceptionCtr, false);
        logger.logdInfo("Total RefSnps with no Strain Alleles: " +
                        rsWithNoAllelesCtr, false);
        logger.logdInfo("Total RefSnps with multi chromosome BL6 coordinates: " +
                        rsMultiBL6ChrCtr, false);
        logger.logdInfo("Total RefSnps with no BL6 coordinates: " +
                        rsWithNoBL6Ctr, false);
        logger.logdInfo("Total RefSnps with no ConsensusSnpAlleleSummary: " +
                        rsWithNoAlleleSummaryCtr, false);
        logger.logdInfo("Total RefSnps with Vocab resolving errors: " +
                        rsWithVocabResolverExceptionCtr, false);

        dbsnpProcessor.getProcessedReport();

        dbsnpProcessor.getDiscrepancyReport();

        String tab = "\t";
        String crt = "\n";
        logger.logdInfo("Chr" + tab + "time (min)" + tab + "FreeMemStart (bytes)" + tab +
                        "FreeMemAfterLookup" + tab + "FreeMemEnd" + tab +
                        "TotalSnpsOnChr" + tab + "TotalRefSnpsLoaded" + tab + "TotalSubSnpsLoaded" + crt, false);
        for (Iterator i = chrStats.iterator(); i.hasNext();) {
            ChromosomeStats s  = (ChromosomeStats)i.next();
            logger.logdInfo(s.getChromosome() + tab + s.getTimeToProcess() + tab +
                            s.getStartFreeMem() + tab +
                            s.getFreeMemAfterGenoLookup() + tab + s.getEndFreeMem() +
                            tab + s.getTotalRefSnpsOnChr() + tab + s.getTotalRefSnpsLoaded()  +
                            tab + s.getTotalSubSnpsLoaded() + crt, false);
        }
    }

    /**
      * writes updates to file for MGI_dbinfo table
      * @throws ConfigException
      * @throws DBException
      */
     private void updateMGIdbinfo() throws ConfigException, DBException {
         // the version of the dbsnp data, aka the Build Number
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
