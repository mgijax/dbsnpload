package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.dla.loader.DLALoader;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.config.SequenceLoadCfg;
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
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.shr.ioutils.XMLDataIterator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DBSNPLoader extends DLALoader {

    // the SQLStream used for loading data
    private SQLStream radarStream;

    // Puts SNPs into the database
    private DBSNPInputProcessor dbsnpProcessor;

    // current number of SNPs processed
    private int snpCtr;

    // current number of rs with alleles loaded into memory
    private int rsWithNoAllelesCtr;

    // current number of rs with no BL6 coordinates
    private int rsWithNoBL6Ctr;

    // current number of rs with multiple BL6 chromosomes
    private int rsMultiBL6ChrCtr;

    // current number of rs with no allele summary (means no strain resolved or
    // all alleles are 'N'
    private int rsWithNoAlleleSummaryCtr;

    // current number of rs with unresolvable vocabularies
    private int rsWithVocabResolverExceptionCtr;

    // current number of rs repeated in input
    private int rsRepeatExceptionCtr;

    // list of chromosomes to parse
    private ArrayList chrList;

    // writer for CoordLoad input file
    private BufferedWriter coordWriter;

    //load configurator
    private DBSNPLoaderCfg loadCfg;
    // configurator for genotype file configuration
    private DBSNPLoaderCfg genoConfig;
    // configurator for nse file configuration
    private DBSNPLoaderCfg nseConfig;;
    // SNP exception factory
    private SNPLoaderExceptionFactory snpEFactory;

    // file path separator
    private static final String PATH_SEPARATOR = System.getProperty("file.separator");;
    /**
     * Initializes instance variables
     * @effects instance variables will be instantiated
     * @throws MGIException if errors occur during initialization
     */
    protected void initialize() throws MGIException {
        // configurator for the load
        loadCfg = new DBSNPLoaderCfg();
        // configurators for each input file
        genoConfig = new DBSNPLoaderCfg("GENO");
        nseConfig = new DBSNPLoaderCfg("NSE");

        // create list of chromosomes for iterating thru Chr files
        String chromosomes = loadCfg.getChromosomesToLoad();
        StringTokenizer chrTokenizer = new StringTokenizer(chromosomes, ",");
        chrList = new ArrayList();
        while (chrTokenizer.hasMoreTokens()) {
            chrList.add( ( (String) chrTokenizer.nextToken()).trim());
        }

        // writer for creating coordload input file
        try {
            coordWriter = new BufferedWriter(new FileWriter(
                loadCfg.getCoordFilename()));
        }
        catch (IOException e) {
            throw new MGIException(e.getMessage());
        }

        // rename the stream for clarity - the DLA thinks of the radar database as qc.
        radarStream = qcStream;
        dbsnpProcessor = new DBSNPInputProcessor(radarStream, loadStream,
                                                 coordWriter);
        snpEFactory = new SNPLoaderExceptionFactory();



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
     * to perform load pre processing
     * @effects any preprocessing will be performed
     * @throws MGIException if errors occur during preprocessing
     */
    protected void preprocess() throws MGIException {
        // delete accession records, note that truncating tables is done at the
        // dla level via Configuration
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
        for (Iterator i = chrList.iterator(); i.hasNext(); ) {
            // encourage the garbage collector
            System.gc();

            String chr = ((String)i.next()).trim();
            logger.logdInfo("Processing chr " + chr, true);
            /**
             * process the genotype file for the Individual data
             */
            String genotypeFilename = genoConfig.getInfileDir()  +
                PATH_SEPARATOR +
                genoConfig.getInfilePrefix() + chr +
                genoConfig.getInfileSuffix();
            XMLDataIterator indivIterator =
                new DBSNPGenotypeIndividualInputFile(genotypeFilename).getIterator();
            while(indivIterator.hasNext() ) {
                //DBSNPInput indivInput = (DBSNPInput)indivIterator.next();
                dbsnpProcessor.processGenoIndivInput((DBSNPGenotypeIndividualInput)indivIterator.next());
            }
            /**
             * process the genotype file for RefSNP data
             */
            System.out.println("processing genotype file " + genotypeFilename);
            XMLDataIterator genoRefSNPIterator = new DBSNPGenotypeRefSNPInputFile(
                 genotypeFilename, dbsnpProcessor.getIndividualMap()).getIterator();
            while (genoRefSNPIterator.hasNext()) {
                dbsnpProcessor.processGenoRefSNPInput((DBSNPGenotypeRefSNPInput) genoRefSNPIterator.next());
            }

            /**
             * process the NSE file
             */
            String nseFilename = nseConfig.getInfileDir()  +
                PATH_SEPARATOR +
                nseConfig.getInfilePrefix() + chr +
                nseConfig.getInfileSuffix();
            System.out.println("processing NSE file " + nseFilename);
            XMLDataIterator it = new DBSNPNseInputFile(nseFilename).getIterator();
            while (it.hasNext()) {
                DBSNPNseInput nseInput = (DBSNPNseInput)it.next();
                try {
                    logger.logdDebug("Processing " + nseInput.getRS().getRsId());
                    dbsnpProcessor.processInput(nseInput);
                }
                catch (SNPNoStrainAlleleException e) {
                   // logger.logdInfo("No StrainAlleles: " + input.getRsId(), true);
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
     * closes the load stream. Reports load statistics.
     * @throws MGIException
     */
    protected void postprocess() throws MGIException
    {

        try {
            coordWriter.close();
        } catch (IOException e) {
            throw new MGIException(e.getMessage());
        }
        logger.logdInfo("Closing load stream", false);
        this.radarStream.close();
        this.loadStream.close();
        reportLoadStatistics();
        logger.logdInfo("DBSNPLoader complete", true);
    }

    /**
     * Reports load statistics; event counts, organism counts, valid sequence count
     * etc.
     * @assumes nothing
     * @effects nothing
     * @throws Nothing
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
        logger.logdInfo("Total RefSnp records repeated in the input (can be multiple per RefSnp): " +
                        rsRepeatExceptionCtr, false);
        for (Iterator i = dbsnpProcessor.getProcessedReport().iterator(); i.hasNext(); ) {
            logger.logdInfo((String)i.next(), false);
        }
    }
    private void deleteAccessions() throws MGIException {
        logger.logdInfo("Deleting Accessions", true);

        try {
            loadDBMgr.executeUpdate(
                    "select a._Accession_key " +
                    "into #todelete " +
                    "from ACC_Accession a " +
                    "where a._MGIType_key =  " + MGITypeConstants.CONSENSUSSNP +
                    " and a._LogicalDB_key = " + LogicalDBConstants.REFSNP +
                    " UNION " +
                    "select a._Accession_key " +
                    "from ACC_Accession a " +
                    "where a._MGIType_key =  " + MGITypeConstants.SUBSNP +
                    " and a._LogicalDB_key = " + LogicalDBConstants.SUBSNP +
                    " UNION " +
                    "select a._Accession_key " +
                    "from ACC_Accession a " +
                    "where a._MGIType_key =  " + MGITypeConstants.SUBSNP +
                    " and a._LogicalDB_key = " + LogicalDBConstants.SUBMITTERSNP);

            loadDBMgr.executeUpdate("create index idx1 on #todelete(_Accession_key)");

            loadDBMgr.executeUpdate("delete ACC_Accession " +
                           "from #todelete d, ACC_Accession a " +
                           "where d._Accession_key = a._Accession_key");
       }
       catch (MGIException e) {
           SNPLoaderException e1 =
                   (SNPLoaderException) snpEFactory.getException(
                   SNPLoaderExceptionFactory.SNPDeleteAccessionsErr, e);
               throw e1;

       }
    }
}