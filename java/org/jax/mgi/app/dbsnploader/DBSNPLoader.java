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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;

public class DBSNPLoader extends DLALoader {
    // Gets SNP records from the genotype input


    // Gets SNP records from the NSE input


    // Gets SNP records from the genotype input

    private DBSNPInterpreter genoInterpreter;
    private DBSNPInterpreter nseInterpreter;


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

    // current number of rs with no allele summary (means no strain resolved or
    // all alleles are 'N'
    private int rsWithNoAlleleSummaryCtr;

    // rs with unresolvable vocabularies
    private int rsWithVocabResolverExceptionCtr;

    // list of chromosomes to parse
    private ArrayList chrList;

    // writer for CoordLoad input file
    private BufferedWriter coordWriter;

    // configurator
    private DBSNPLoaderCfg loadCfg;

    // SNP exception factory
    private SNPLoaderExceptionFactory snpEFactory;


    /**
     * Initializes instance variables
     * @effects instance variables will be instantiated
     * @throws MGIException if errors occur during initialization
     */
    protected void initialize() throws MGIException {
        // get configurators for each input file
        DBSNPLoaderCfg genoConfig = new DBSNPLoaderCfg("GENO");
        DBSNPLoaderCfg nseConfig = new DBSNPLoaderCfg("NSE");

        // create interpreters for each input file
        genoInterpreter = new DBSNPInterpreter(genoConfig);
        nseInterpreter = new DBSNPInterpreter(nseConfig);

        // The list of chromosomes for iterating thru Chr files
        chrList = new ArrayList();

        for (int i = 1; i < 20; i++) {
            chrList.add(String.valueOf(i));
        }
        chrList.add("X");
        chrList.add("Multi");

        //chrList.add("1");
        loadCfg = new DBSNPLoaderCfg();

       // for creating coordload input file
        try {
            coordWriter = new BufferedWriter(new FileWriter(
                loadCfg.getCoordFilename()));
        }
        catch (IOException e) {
            throw new MGIException(e.getMessage());
        }

        // rename the stream for clarity - the DLA thinks of the radar database as qc.
        radarStream = qcStream;
        dbsnpProcessor = new DBSNPInputProcessor(radarStream, loadStream, coordWriter);
        snpEFactory = new SNPLoaderExceptionFactory();

        snpCtr = 0;
        rsWithNoAllelesCtr = 0;
        rsWithNoBL6Ctr = 0;
        rsWithNoAlleleSummaryCtr = 0;
        rsWithVocabResolverExceptionCtr = 0;
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
        // create the genotype lookup
        for (Iterator i = chrList.iterator(); i.hasNext(); ) {
            String chr = ((String)i.next()).trim();
            logger.logdInfo("creating the genotype lookup for chr " + chr, false);
            genoInterpreter.loadChromosome(chr);
            DBSNPInput input = (DBSNPInput) genoInterpreter.interpret();
            while (input != null) {
                dbsnpProcessor.processInput(input);
                input = (DBSNPInput)genoInterpreter.interpret();
            }
        }
    }

    /**
     * Performs the DBSNP database load into the RADAR
     * @effects database records created within the RADAR
     * database. If stream is a BCP stream, creates bcp files which may be
     * temporary or persistent depending on configuration
     * @throws MGIException thrown if a fatal error occurs while performing the
     * load.
     */
    protected void run()  throws MGIException {
        // interpret/process chromosome files one at a time

        for (Iterator i = chrList.iterator(); i.hasNext(); ) {
            String chr = ((String)i.next()).trim();
            logger.logdInfo("Processing chr " + chr, true);
            nseInterpreter.loadChromosome(chr);
            DBSNPInput input = (DBSNPInput) nseInterpreter.interpret();
            //logger.logDebug(input.getRsId() + "\t" + ((DBSNPNseInput)input).getSubSNPs().toString());
            while (input != null) {
                try {
                    dbsnpProcessor.processInput(input);
                }
                catch (SNPNoStrainAlleleException e) {
                   // logger.logdInfo("No StrainAlleles: " + input.getRsId(), true);
                    rsWithNoAllelesCtr++;
                }
                catch (SNPNoBL6Exception e) {
                    rsWithNoBL6Ctr++;
                }
                catch (SNPNoConsensusAlleleSummaryException e) {
                    rsWithNoAlleleSummaryCtr++;
                }
                catch (SNPVocabResolverException e) {
                    rsWithVocabResolverExceptionCtr++;

                }
                input = (DBSNPInput) nseInterpreter.interpret();
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
        logger.logdInfo("Total RefSnps with no ConsensusSnpAlleleSummary: " +
                        rsWithNoAlleleSummaryCtr, false);
        logger.logdInfo("Total RefSnps with Vocab resolving errors: " +
                        rsWithVocabResolverExceptionCtr, false);
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