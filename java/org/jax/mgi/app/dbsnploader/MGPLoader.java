package org.jax.mgi.app.dbsnploader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.shr.config.BCPManagerCfg;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dbutils.bcp.BCPManager;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.XMLDataIterator;



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

public class MGPLoader extends DLALoader {

    // An SQL data manager for providing database access to the snp database
    protected SQLDataManager snpDBMgr;

    // A bcp manager for controlling the bcp writers for the snp database
     protected BCPManager snpBCPMgr;
     
     // the SQLStream used for loading snp data
     private SQLStream snpStream;

    // Resolves DBSNP attributes to MGI values and writes
    // bcp files
    private MGPInputProcessor mgpProcessor;

    // writer to reports rsIds not loaded
    private BufferedWriter snpsNotLoadedWriter;

 
    // the number of VCF RefSnps processed
    private int vcfRsCtr;
    
    // current number of SubSnps for the RefSnps processed
    private int ssCtr;

    // current number of SNPs with no strain alleles in the genotype file
    private int rsWithNoAllelesCtr;

    // current number of SNPs with no BL6 coordinates
    private int rsWithNoBL6Ctr;

    // current number of SNPs with multiple BL6 chromosomes
    private int rsMultiBL6ChrCtr;

    // current number of SNPs with multiple BL6 coordinates on same chromosome
    private int rsMultiBL6ChrCoordCtr;

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

    // reasons not to load; most reasons handled by processor
    private static String SNP_NOTLOADED_NO_STRAINALLELE;
    private static final String NL = "\n";
    private static final String TAB = "\t";
 
    // lookup of rsIDs already in the database
    DBSNPRSLookup rsLookup;
    Vector v;
    
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
        //String[] snpTables = loadCfg.getTruncateSnpTables();

        // conf
        genoConfig = new DBSNPLoaderCfg("GENO");
        nseConfig = new DBSNPLoaderCfg("NSE");

        // get list of chromosomes for iterating thru Chr files
        chromosomes = loadCfg.getChromosomesToLoad();
        
        // create SQLStream for snp database
        snpDBMgr = SQLDataManagerFactory.getShared(SchemaConstants.SNP);
        snpDBMgr.setLogger(logger);
        snpBCPMgr = new BCPManager(new BCPManagerCfg("SNP"));
        snpBCPMgr.setLogger(logger);
        snpStream = createSQLStream(loadCfg.getSnpStreamName(),
                                   snpDBMgr, snpBCPMgr);
        // create writer for rs ids not loaded
        try {
           snpsNotLoadedWriter = new BufferedWriter(
		new FileWriter(loadCfg.getSnpNotLoadedFileName()));
        }
        catch (IOException e){
           throw new MGIException(e.getMessage());
        }

        // create snp processor
        try {
            mgpProcessor = new MGPInputProcessor(snpStream,
                snpsNotLoadedWriter);
        }
        catch (IOException e) {
            throw new MGIException(e.getMessage());
        }

        // close radar connection provided by super class, don't need it
        // Note we need the mgd connection to load the lookups
        qcDBMgr.closeResources();

        // create ExceptionFactory
        snpEFactory = new SNPLoaderExceptionFactory();
        
        /*try {
            logger.logdInfo("Truncating SNP_Strain table", true);
		
            if (snpTables != null) {
                DLALoaderHelper.truncateTables(snpTables,
                                               snpDBMgr.getDBSchema(), logger);
            }
        }
        catch (Exception e) {
            throw new MGIException(e.getMessage());
        }*/

        // initialize all counters
        vcfRsCtr = 0;
        ssCtr = 0;
        rsWithNoAllelesCtr = 0;
        rsWithNoBL6Ctr = 0;
        rsMultiBL6ChrCtr = 0;
        rsMultiBL6ChrCoordCtr = 0;
        rsWithNoAlleleSummaryCtr = 0;
        rsWithVocabResolverExceptionCtr = 0;
        rsRepeatExceptionCtr = 0;

	    // initialize reasons not to load (others determined in processor)
        SNP_NOTLOADED_NO_STRAINALLELE = loadCfg.getSnpNotLoadedNoStrainAllele();

        // initialize the statistics container
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
       // if(loadCfg.getOkToDeleteAccessions().equals(Boolean.TRUE)) {
       //     deleteAccessions();
        //}
    	// for MGP may be able to delete this method
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
            mgpProcessor.reinitializeProcessor();

            // encourage the garbage collector
            System.gc();

            // get the next chromosome and init the rsLookup for that chr
            String chr = chromosomes[i].trim();
            System.out.println("initializing rsLookup");
            rsLookup = new DBSNPRSLookup(chr);
            System.out.println("done initializing rsLookup");
            
            long startFreeMem = Runtime.getRuntime().freeMemory();
            stats.setChromosome(chr);
            stats.setStartFreeMem(startFreeMem);
            logger.logcInfo("Processing chr " + chr, true);
            logger.logdInfo("Processing chr " + chr, true);
            logger.logdInfo("Free memory: " + startFreeMem, false);

            /**
             * process genotype file for the Individual data
             */

            // create the genotype filename for this chromosome
            String genotypeFilename = genoConfig.getInfileDir() + 
            		PATH_SEPARATOR +
                    genoConfig.getInfilePrefix() + chr +
                    genoConfig.getInfileSuffix();
            logger.logdInfo("processing VCF file for Genotype data " +
			    genotypeFilename + ". Free memory: " +
			    Runtime.getRuntime().freeMemory(), true);

		    //System.out.println("filename="+genotypeFilename);
            System.out.println("MGPLoader calling getinputMap");
		    HashMap genoSNPMap = new MGPGenotypeRefSNPInputFile(
                 genotypeFilename).getInputMap();
		    System.out.println("MGPLoader returned from calling getinputMap");
		    vcfRsCtr = vcfRsCtr + genoSNPMap.size();
		    /**
             * create iterator over NSE file
             */
 
            String nseFilename = nseConfig.getInfileDir()  +
                PATH_SEPARATOR +
                nseConfig.getInfilePrefix() + chr +
                nseConfig.getInfileSuffix();

            XMLDataIterator it = new DBSNPNseInputFile(nseFilename).
                    getIterator();

            /**
             * report available memory
             */
            long afterLookupFreeMem = Runtime.getRuntime().freeMemory();
            stats.setFreeMemAfterGenoLookup(afterLookupFreeMem);

            System.out.println("processing NSE file " + nseFilename);
            logger.logdInfo("processing " + nseFilename + ". Free memory: " +
			    afterLookupFreeMem, true);

            /**
             * iterate through all snps on this chromosome
             */
            // total SNP in Geno File
            int totalGenoSnpOnChr = 0;
            // total ADD snps in geno file
            int totalGenoAddOnChr = 0;
            // total Update snps in geno file
            int totalGenoUpdateOnChr = 0;
            // rsId from the genotype file
            String genoRSId = null;

            // Create the two input objects
            MGPGenotypeRefSNPInput genoInput = null;
            DBSNPNseInput nseInput = null;

            while (it.hasNext()) {
                nseInput = (DBSNPNseInput)it.next();
                String nseRSId = nseInput.getRS().getRsId();
                //System.out.println("XML rsID: " + nseRSId);
                //logger.logcInfo("Next RS" + nseRSId, false);
                int intNseRSId = new Integer(nseRSId).intValue();

                if (genoSNPMap.containsKey(intNseRSId)) {
                	totalGenoSnpOnChr++;
                	genoInput = 
                			(MGPGenotypeRefSNPInput)genoSNPMap.get(intNseRSId);
                	//System.out.println("genoInput.rsID " + genoInput.getRsId());
                	genoRSId = Integer.toString(genoInput.getRsId()); 
                	//System.out.println("geno rsID: " + genoRSId);
                	//if (rsLookup.lookup("rs" + genoRSId) != null) {
                	if (rsLookup.lookup(genoRSId) != null) {
                    	//logger.logcInfo("Geno RS is already in MGI rs" + genoRSId, false);
                    	totalGenoUpdateOnChr++;
                    	/*
                    	 * mgpProcessor.processUpdate(nseInput, genoInput, "update");
                    	 */
                    	continue;
                    }
                	else {
                		//logger.logcInfo("Geno RS not in MGI rs" + genoRSId, false);
                		mgpProcessor.processInput(nseInput, genoInput); // add action param "add" 
                		
                		totalGenoAddOnChr++;
                	}
                		
                }
              
                
                //ssCtr += nseInput.getSubSNPs().size();
            }
            stats.setTotalSnpsOnChr(totalGenoSnpOnChr);
            stats.setTotalGenoAddOnChr(totalGenoAddOnChr);
            stats.setTotalGenoUpdateOnChr(totalGenoUpdateOnChr); 
            stats.setEndFreeMem(Runtime.getRuntime().freeMemory());
            // set elapsed time in minutes
            long endTime = System.currentTimeMillis();
            float elapsedTime = (endTime - startTime)/60000;
            //System.out.println("Elapsed Time in minutes: " + elapsedTime);
            logger.logdInfo("Elapsed Time in minutes: " + elapsedTime, false);
            stats.setTimeToProcess(elapsedTime);
            int rsLoaded = mgpProcessor.getRsLoadedOnChrCtr();
            int ssLoaded = mgpProcessor.getSsLoadedOnChrCtr();
            stats.setTotalRefSnpsLoaded(rsLoaded);
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


        // close snp and mgd streams
        logger.logdInfo("Closing snp stream", false);
        this.snpStream.close();

        //logger.logdInfo("Closing mgd stream", false);
        this.loadStream.close();

        // report load statistics
        reportLoadStatistics();
        try {
            snpsNotLoadedWriter.close();
        } catch (IOException e) {
            throw new MGIException(e.getMessage());
        }
        logger.logdInfo("DBSNPLoader complete", true);
    }

    /**
     * Reports load statistics
     */
    private void reportLoadStatistics() throws ConfigException {
        logger.logdInfo("Total VCF RefSnps In VCF Files: " + vcfRsCtr, false);
        
        logger.logdInfo("Total RefSnp records repeated in the input " +
            "(can be multiple per RefSnp): " +
                        rsRepeatExceptionCtr, false);
        
        logger.logdInfo("Total RefSnps mapped to > " + loadCfg.getMaxChrCoordCt()
                         + " coordinates on the same chromosome: " + rsMultiBL6ChrCoordCtr, false);
        
        logger.logdInfo("Total RefSnps with Vocab resolving errors: " +
                        rsWithVocabResolverExceptionCtr, false);

        mgpProcessor.getProcessedReport();

        mgpProcessor.getDiscrepancyReport();

        String tab = "\t";
        String crt = "\n";
        logger.logdInfo("Chr" + tab + "time (min)" + tab + "FreeMemStart (bytes)" + tab +
                        "FreeMemAfterLookup" + tab + "FreeMemEnd" + tab +
                        "TotalMgpSnpsOnChr" + tab + "TotalMgpRefSnpsLoaded" + tab + "TotalMgpSubSnpsLoaded" + tab + "TotalMgpSubSnpsNotInDatabase" + tab + "totalMgpSubSnpsInDatabase" + crt, false);
        for (Iterator i = chrStats.iterator(); i.hasNext();) {
            ChromosomeStats s  = (ChromosomeStats)i.next();
            logger.logdInfo(s.getChromosome() + tab + s.getTimeToProcess() + tab +
                            s.getStartFreeMem() + tab +
                            s.getFreeMemAfterGenoLookup() + tab + s.getEndFreeMem() +
                            tab + s.getTotalRefSnpsOnChr() + tab + s.getTotalRefSnpsLoaded()  +
                            tab + s.getTotalSubSnpsLoaded() + tab + s.getTotalGenoAddOnChr() + tab + s.getTotalGenoUpdateOnChr() + crt, false);
        }
    }
}
