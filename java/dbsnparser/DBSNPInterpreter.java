package dbsnparser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;

public class DBSNPInterpreter {

    // set of SNPs from all chromosomes
    private Vector snpVector;
    private Iterator snpIterator;
    private DBSNPParser parser;
    private String infileDir;
    private String infilePrefix;
    private String infileSuffix;
    // DEBUG
    DLALogger logger;

    public DBSNPInterpreter(DBSNPLoaderCfg config)  throws DLALoggingException, ConfigException {
        // The interpreter gets the parser prefixed with the prefix with
        // which the configurator was created
        parser = (DBSNPParser) config.getSnpParser();
        //snpVector = new Vector();
        // get file information from Configuration
        infileDir = config.getInfileDir();
        infilePrefix = config.getInfilePrefix();
        infileSuffix = config.getInfileSuffix();
        logger = DLALogger.getInstance();
    }

    // Iterate thru the snpVector of DBSNPInput Objects
    public DBSNPInput interpret() {
        if (snpIterator.hasNext()) {
            return (DBSNPInput)snpIterator.next();
        }
        else {
            return null;
        }
    }
    // parse a new chromosome file
    public void loadChromosome(String chr) throws MGIException {
        System.gc();
        // parse all the input files
        try {
            String infile = infileDir + "/" + infilePrefix + chr + infileSuffix;
            snpVector = parser.parse(infile);
            snpIterator = snpVector.iterator();
            logger.logdDebug("snpVector.size for chr " + chr + " = " + snpVector.size());
        }
        catch (IOException e) {
            throw new MGIException(e.getMessage());
        }
    }

}