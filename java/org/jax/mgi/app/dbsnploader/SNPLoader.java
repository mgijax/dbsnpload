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
import org.jax.mgi.dbs.mgd.AccessionLib;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;


public class SNPLoader extends DLALoader {
    /**
     * implements the DLALoader methods
     * 'initialize' and 'run' to accomplish DBSNP load initialization and processing.
     * @has a set of 'basic-needs' objects for doing DLA loads<br>
     * <UL>
     *   <LI>A Interpretor for parsing records from the input
     *   <LI>A Processor for adding dbSNP records to the database
     * </UL>
     * @does gets records from the input and gets them into the database
     * @author sc
     * @version 1.0
     */


    // list of chromsomes to parse
    private ArrayList chrList;

    /**
     * Initializes instance variables
     * @effects instance variables will be instantiated
     * @throws MGIException if errors occur during initialization
     */
    protected void initialize() throws MGIException {

        // The list of chromosomes f
        chrList = new ArrayList();

        for (int i = 1; i < 20; i++) {
            chrList.add(String.valueOf(i));
        }
        chrList.add("X");
        chrList.add("Multi");

        //chrList.add("1");

    }

    /**
     * to perform load pre processing
     * @effects any preprocessing will be performed
     * @throws MGIException if errors occur during preprocessing
     */
    protected void preprocess() throws MGIException {

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
            logger.logdDebug("Processing chr " + chr);
            /*nseInterpreter.loadChromosome(chr);
            DBSNPInput input = (DBSNPInput) nseInterpreter.interpret();
            //logger.logDebug(input.getRsId() + "\t" + ((DBSNPNseInput)input).getSubSNPs().toString());
            while (input != null) {
                try {
                    processor.processInput(input);
                }
                catch (SNPNoStrainAlleleException e) {
                   // logger.logdInfo("No StrainAlleles: " + input.getRsId(), true);
                    rsWithNoAllelesCtr++;
                }
                catch (SNPNoBL6Exception e) {
                    rsWithNoBL6Ctr++;
                }
                input = (DBSNPInput) nseInterpreter.interpret();
                snpCtr++;
            }
*/
        }
    }


    /**
     * closes the load stream. Reports load statistics.
     * @throws MGIException
     */
    protected void postprocess() throws MGIException
    {
    reportLoadStatistics();
    logger.logdInfo("SNPLoader complete", true);
    }

    /**
     * Reports load statistics; event counts, organism counts, valid sequence count
     * etc.
     * @assumes nothing
     * @effects nothing
     * @throws Nothing
     */
    private void reportLoadStatistics() {

    }
}
