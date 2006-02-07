// $Header
// $Name

package org.jax.mgi.app.dbsnploader;
/**
 * Debug stuff
 */
import org.jax.mgi.shr.timing.Stopwatch;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.rdr.dao.*;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedWriter;

/**
 * is an object that processes a DBSNPInput object. It resolves and/or
 * translates raw values into MGI values and creates radar and mgd
 * bcp files
 * @has Lookups to resolve attributes,
 *      SQLStreams to write to radar and mgd bcp files,
 *      A running list of RS ids already looked at, to avoid dups
 *      A writer to write out coordinate info in mgs format for processing
 *        by the coordload
 * @does resolves snp attributes writing them to bcp files and to a coordinate
 *       output file.
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPInputProcessor {
    // DEBUG
    private Stopwatch stopWatch;

    // for temporary load to mgd
    private SNPProcessor snpProcessor;

    // mgd stream; writes to bcp files
    private SQLStream loadStream;

    // radar stream; writes to bcp files
    private SQLStream radarStream;

    // logger for the load
    private DLALogger logger;

    // get a sequence load configurator
    private DBSNPLoaderCfg config;

    // jobstream key for the load
    private Integer jobStreamKey;

    // Hashmap of SS populations by RS
    // {rsId:{ssid:Vector of DBSNPGenotypePopulation}, ... }
    private HashMap rsPopulationsBySS;

    // Compound object holding all the DAOs representing an RS
    private DBSNPNse dbSNPNse;

    // Set of RS ids we have processed (so we don't load dups)
    private HashSet rsIdSet;

    // the current rsId we are processing
    private String rsId;

    // current number of radar snps added
    private int addCtr = 0;

     // current number of ss (for BL6 RS) w/o strain alleles)
    private int ssNoStAllele = 0;

     /** > 1 dbsnp strainIds can map to the same mgd strain
     * so the consensusAllele calculation will be incorrect unless we resolve
     * the strain to a strain key here in the data provider side of the load
     */
    // lookup mgd strain key, given a strain  name
    private StrainKeyLookup strainKeyLookup;

    // lookup a strain key, given a jax registry id
    private AccessionLookup jaxRegistryLookup;

    // we use the handle and population names to create a translatable
    // strain name for dbsnp strains with integer names
    //
    // lookup handle name given a population id
    private HandleNameByPopIdLookup handleNameLookup;

    // lookup population name given a population id
    private PopNameByPopIdLookup popNameLookup;

    // the mapping of strainIds to strain names
    private HashMap individualMap;


    /**
     * Constructs a DBSNPInputProcessor with a radar and mgd stream
     * and a BufferedWriter
     * @assumes Nothing
     * @effects Writes files to a filesystem
     * @param radarSqlStream stream for writing radar bcp files
     * @param loadSqlStream stream for writing mgd bcp files
     * @param coordWriter BufferedWriter for writing coordinate information
     * @throws ConfigException  if there are configuration errors.
     * @throws DBException if error creating DBSNPGenotype objects
     * @throws DLALoggingException if error creating a logger
     */

    public DBSNPInputProcessor(SQLStream radarSqlStream,
                               SQLStream loadSqlStream,
                               BufferedWriter coordWriter) throws
        CacheException,
        DBException, ConfigException, DLALoggingException, TranslationException,
        MGIException {
        /**
         * Debug stuff
         */
        stopWatch = new Stopwatch();

	// set the streams
        radarStream = radarSqlStream;
        loadStream = loadSqlStream;

	// get a logger
        logger = DLALogger.getInstance();

        // configurator
        config = new DBSNPLoaderCfg();

	// set the jobstream key
        jobStreamKey = new Integer(config.getJobstreamKey());

	// initialize HashMap for populations
        rsPopulationsBySS = new HashMap();

	// to lookup a strain key given a strain name=
        strainKeyLookup = new StrainKeyLookup();

	// to lookup a strain key given a JAX registry id
        jaxRegistryLookup = new AccessionLookup(LogicalDBConstants.JAXREGISTRY,
                                                MGITypeConstants.STRAIN,
                                                AccessionLib.PREFERRED);
	// to lookup a population name given a population id
        popNameLookup = new PopNameByPopIdLookup();

	// to lookup a submitter handle name given a population id
        handleNameLookup = new HandleNameByPopIdLookup();

	// resolves attributes and creates mgd bcp files and a coordinate file
        snpProcessor = new SNPProcessor(loadStream, coordWriter);

	// the set of rsIds we have already processed
        rsIdSet = new HashSet();

	// strain id to strain name map {strainId:strainName, ...}
        individualMap = new HashMap();
    }

    /**
     * reinitialize the individual and ss population maps
     */
    public void reinitializeProcessor() {
        individualMap = new HashMap();
        rsPopulationsBySS = new HashMap();
    }

    /**
     * Adds an individual mapping (strainId to strainName) to the individual map
     * @param input - a DBSNPGenotypeIndividualInput object
     */
    public void processGenoIndivInput(DBSNPGenotypeIndividualInput input) {
        individualMap.put(input.getStrainId(), input.getStrain());
    }

    /**
     * get the individual map
     * @returns - HashMap of strainId to strainName mappings
     */
    public HashMap getIndividualMap() {
        return individualMap;
    }


    /**
     * Adds a refSnp to ss population mapping to rsPopulationsBySS Map
     * @param input a DBSNPGenotypeRefSNPInput object
     * @throws MGIException
     */
    public void processGenoRefSNPInput(DBSNPGenotypeRefSNPInput input) throws
        MGIException {

        // get the rs id
        rsId = ( (DBSNPGenotypeRefSNPInput) input).getRsId();
        // get the ss population(s) for this rs
        HashMap ssPopulations = ( (DBSNPGenotypeRefSNPInput) input).
            getSSPopulationsForRs();
        // map the rsId to the ss population(s)
        rsPopulationsBySS.put(rsId, ssPopulations);
    }

    /**
     * Resolves and/or translates raw values into MGI values and creates
     * radar and mgd bcp files
     * @param input DBSNPNseInput object containing raw values to resolve in
     * order to create radar and mgd database objects
     * @effects - writes bcp files to a filesystem
     */
    public void processInput(DBSNPNseInput input) throws MGIException {

        DBSNPNseRS rs = ( (DBSNPNseInput) input).getRS();
        rsId = rs.getRsId();

        // don't load duplicate RefSNPs; build 125 multichr RefSnps are located
        // in each chromosome file on which they have a coordinate
        if (rsIdSet.contains(rsId)) {
            SNPRepeatException e = new SNPRepeatException();
            e.bind(rsId);
            throw e;
        }
        rsIdSet.add(rsId);

        // get the raw data objects from the DBSNPNseInput object
        Vector subSNPs = ( (DBSNPNseInput) input).getSubSNPs();
        Vector flank3Prime = ( (DBSNPNseInput) input).get3PrimeFlank();
        Vector flank5Prime = ( (DBSNPNseInput) input).get5PrimeFlank();
        Vector contigHits = ( (DBSNPNseInput) input).getContigHits();

        // get the set of strain alleles, for this rs
        // looks like {ssid:Vector of DBSNPGenotypePopulation objects, ...}
        HashMap currentSSPopulationMap = (HashMap) rsPopulationsBySS.get(
            rsId);
        // if currentSSPopulationMap is NULL (when no record in the genotype
        // file for this rs)
        // throw an exception so the loader
        // can decide what to do (load or not load that is the question)
        if (currentSSPopulationMap == null) {
            SNPNoStrainAlleleException e = new
                SNPNoStrainAlleleException();
            e.bind(rsId);
            throw e;
        }

        // create a radar Consensus SNP DAO; we do this first to get its key
        // in order to create other objects
        Integer consensusKey = processConsensusSnp(rs);

        // create radar coordinate and marker DAOs; do this first because
        // some RS will be rejected by this method; we are only loading C57BL/6J
        // coordinates)

        processCoordinates(consensusKey, contigHits, rsId);

        // create radar accession DAO for rs id
        processAccession(rsId, SNPLoaderConstants.LDB_CSNP, consensusKey,
                         SNPLoaderConstants.OBJECTYPE_CSNP, Boolean.FALSE);
        /**
         * for each SubSnp create:
         * 1) radar subSNP DAO
         * 2) radar accession DAOs for its ssId and submitter snp id
         * 3) radar population DAOs
         * 4) radar strain allele DAOs for each population's strain alleles
         */
        // removed below for build 125
        // current number of ss that have a population
        //int ssWithPopulationCt = 0;

        // for each SubSnp:
        for (Iterator i = subSNPs.iterator(); i.hasNext(); ) {
            DBSNPNseSS ss = (DBSNPNseSS) i.next();
            //logger.logdDebug("Getting populations for SS" + ss.getSSId());
	    // get the set of populations
	    Vector popsForSSVector = (Vector) currentSSPopulationMap.get(ss.
                getSSId());

            // Added 11/1 build 125 genotype file does not list ss
            // w/o strain/alleles
            if (popsForSSVector == null) {
                logger.logcInfo("No strain/alleles for RS" + rsId + " SS" +
                                ss.getSSId(), false);
                ssNoStAllele++;
                continue;
            }

            // removed this for build 125
            // We don't want to load RS in the genotype file for which none
            // of theSS have a population. RS13476574 is an example
            /*
            if (popsForSSVector.size() > 0) {
                ssWithPopulationCt++;
            } */

	    // create radar SubSnp DAO returning its key
            Integer ssKey = processSS(consensusKey, ss);

            // for each population
            for (Iterator j = popsForSSVector.iterator(); j.hasNext(); ) {
                // create radar StrainAllele DAOs
                processSSStrainAlleles(ssKey, ss.getSSId(),
                                       (DBSNPGenotypePopulation) j.next());
            }
            // ANALYSIS exemplars with no strain alleles
            /*
            if (ss.isExemplar.equals(Boolean.TRUE)) {
                String s = ss.getSSId();
                if( ((HashMap)currentSSAlleleMap.get(s)).size() == 0) {
                    logger.logcInfo("RS" + rsId + " SS " + s +
		    " is exemplar and has no alleles", false);
                }

	    }*/
            // end ANAYSIS exemplars with no strain alleles

        }
        // if none of the ss have a population, throw an exception
        // removed below for build 125
        /*
        if (ssWithPopulationCt < 1) {
            // throw an exception to be caught at the loader level
            // loader can decide (via configuration?) behaviour when there
            // are no strain alleles for an rs - log and go on to the next
            // snp or fatal error.
            logger.logcInfo("No alleles for RS" + rsId, false);
            SNPNoStrainAlleleException e = new
                SNPNoStrainAlleleException();
            e.bind(rsId);
            throw e;

        }*/

        // create radar flank DAOs for the 5' flanking sequence
        processFlank(consensusKey, flank5Prime, Boolean.TRUE);

        // create radar flank DAOs for the 3' flanking sequence
        processFlank(consensusKey, flank3Prime, Boolean.FALSE);

        // create the radar consensus allele DAOs for this RS
        // send rsId just for debug
        processConsensusAlleles(consensusKey, currentSSPopulationMap, rsId);

        // if we have gotten this far, we have a complete DBSNPNse object
        // send it to its radar stream
        dbSNPNse.sendToStream();

        // increment the counter
        addCtr++;

        // create mgd DAOs
        snpProcessor.process(dbSNPNse, rsId);
    }

    /**
     * Gets a Vector of Strings reporting various load statistics
     * @assumes nothing
     * @effects nothing
     * @return Vector of Strings reporting
     * <OL>
     * <LI>Total radar SNPs created
     * <LI>Total SS with no strain alleles
     * <LI>Statistics from the mgd SNPProcessor
     * </OL>
     */
    public Vector getProcessedReport() {
        Vector report = new Vector();
        report.add("Total RADAR SNPs created: " + addCtr);
        report.add("Total SS with no strain alleles: " + ssNoStAllele);
        for (Iterator i = snpProcessor.getProcessedReport().iterator();
             i.hasNext(); ) {
            report.add( (String) i.next());
        }
        return report;
    }

    /**
     * creates an MGI_SNP_ConsensusSNPDAO object
     * @param rs a DBSNPNseRS object
     * @return MGI_SNP_ConsensusSNP._ConsensusSNP_key
     * @throws DBException
     * @throws ConfigException
     */
    private Integer processConsensusSnp(DBSNPNseRS rs) throws DBException,
        ConfigException {
	// create an MGI_SNP_ConsensusSNPState
        MGI_SNP_ConsensusSNPState state = new MGI_SNP_ConsensusSNPState();

	// set its attributes
        state.setVariationClass(rs.getRsVarClass());
        state.setJobStreamKey(jobStreamKey);
        state.setBuildCreated(rs.getBuildCreated());
        state.setBuildUpdated(rs.getBuildUpdated());

	// create a new DBSNPNse passing the state and the radar stream
        dbSNPNse = new DBSNPNse(state, radarStream);

	// return the _ConsensusSNP_key
        return dbSNPNse.getConsensusKey();
    }

    /**
     * create MGI_SNP_StrainAllele objects for the RS consensus strain alleles
     * @param consensusKey
     * @param ssPopulationMap {ssId:Vector of Population objects, ...}
     * @param rsId
     * @assumes unresolvable strains are reported when processing
     *           SS strain alleles
     * @throws DBException
     * @throws ConfigException
     * @throws TranslationException
     * @throws CacheException
     * @throws SNPNoConsensusAlleleSummaryException if not able to create a
     *            ConsensusAllele summary
     */
    private void processConsensusAlleles(Integer consensusKey,
                                         HashMap ssPopulationMap,
                                         String rsId)
        throws DBException, ConfigException, TranslationException,
			      CacheException, MGIException,
			      SNPNoConsensusAlleleSummaryException {

        //  map strain to alleles and count of each allele
        // consensusAlleleMap looks like strain:HashMap[allele:count]
        HashMap consensusAlleleMap = new HashMap();

        // summary of the consensus alleles
        HashSet alleleSummarySet = new HashSet();

        // current number of ss that have a population
        int ssWithPopulationCt = 0; //DEBUG

        /**
         * Iterate thru each SS
         */
        for (Iterator i = ssPopulationMap.keySet().iterator(); i.hasNext(); ) {
            // get the ssid
            String currentSSId = (String) i.next();

            // get the set of populations for this SS
            Vector population = (Vector) ssPopulationMap.get(currentSSId);

	    // DEBUG
            if (population.size() > 0) {
                ssWithPopulationCt++;
            }
	    // END DEBUG
	    //
            /**
             * Iterate thru the populations of the current SS
             */

            for (Iterator j = population.iterator(); j.hasNext(); ) {
                DBSNPGenotypePopulation pop = (DBSNPGenotypePopulation) j.next();
                HashMap alleleMap = pop.getStrainAlleles();
                /**
                 * Iterate thru strains
                 */
                for (Iterator k = alleleMap.keySet().iterator(); k.hasNext(); ) {
                    String strain = (String) k.next();
                    Integer mgdStrainKey = resolveStrain(strain, pop.getPopId());
                    // if we can't resolve the strain, continue
                    if (mgdStrainKey == null) {
                        continue;
                    }
                    // get the allele string from the Allele object
                     Allele a = (Allele) alleleMap.get(strain);
                     String allele = a.getAllele();
                     // BUILD 125 - map " " allele to "N" for now.
                     if (allele.equals(" ")) {
                         allele = "N";
                     }
                    String orient = a.getOrientation();
                    /**
                     * if in reverse orientation we need to complement
		     * 'allele' before storing
                     */
                    if (orient.equals(
                        SNPLoaderConstants.GENO_REVERSE_ORIENT)) {
                        allele = complementAllele(allele, currentSSId);
                    }
                    /**
                     * Add the allele to alleleSummary set (proper set,
		     * no repeats)
                     */
                    if (!allele.equals("N")) {
                        alleleSummarySet.add(allele);
                    }
                    addToConsensusAlleleMap(mgdStrainKey, allele,
                                            consensusAlleleMap);
                }
                /**
                 * done iterating thru strains
                 */
            }
            /**
             * Done iterating thru populations
             */
        }
        /**
         * Done iterating thru ss
         */

        /**
         * Process the alleleSummary
         */

        // add the delimiters to the rs allele summary
        StringBuffer summaryString = new StringBuffer();
        for (Iterator i = alleleSummarySet.iterator(); i.hasNext(); ) {
            summaryString.append( (String) i.next() + "/");
        }
        int len = summaryString.length();
        /**
         * if we have 0 length summary allele it is because
         * 1) no strains resolve, therefore no alleles.
         * 2) the only allele is 'N'
         */
        if (len < 1) {
            logger.logcInfo("No ConsensusSnp Summary Allele for RS" + rsId, false);
            SNPNoConsensusAlleleSummaryException e = new
                SNPNoConsensusAlleleSummaryException();
            e.bind(rsId);
            throw e;
        }
        // remove the trailing '/'
        summaryString.deleteCharAt(len - 1);
        if (summaryString.length() > 100) {
            logger.logcInfo("ALLELE SUMMARY: for RS" + rsId + " is " + summaryString.length(), false);
        }
	// add the allele summary to the DBSNPNse object
        dbSNPNse.addRSAlleleSummary(summaryString.toString());
        /**
         * now find the consensus alleles for each strain
         */
        createConsensusAlleles(consensusKey, consensusAlleleMap);
    }

    /**
     * calculates the consensus allele for each strain
     * @param csKey the _ConsensusSNP_key
     * @param csAlleleMap strainKey:HashMap[allele:count]
     * @throws DBException
     * @throws ConfigException
     */
    private void createConsensusAlleles(Integer csKey, HashMap csAlleleMap)
	    throws DBException, ConfigException, MGIException {
        for (Iterator i = csAlleleMap.keySet().iterator(); i.hasNext(); ) {
            // the consensus allele determined thus far
            String currentConsensusAllele = "";

            // true if there is a conflict determining the consensus allele
            Boolean isConflict = null;

            // get the next strain for which to determine consensus allele
            Integer strainKey = (Integer) i.next();

            //get the set of alleles for this strain
            HashMap alleles = (HashMap) csAlleleMap.get(strainKey);

            /**
             * if we have only one allele there is no conflict determining the
             * consensus. However if the allele is "N" we set the consensusAllele
             * to '?'
             */
            if (alleles.size() == 1) {
                String allele = (String)alleles.keySet().iterator().next();
                if(allele.equals("N")) {
                    currentConsensusAllele = "?";
                }
                else {
                    currentConsensusAllele = allele;
                }
                // consensus allele determined without using a majority
                isConflict = Boolean.FALSE;
            }
            else {
                // remove 'N' allele (if it exists) from consideration
                alleles.remove("N");
                /**
                 * if we have only one allele now then we removed a 'N' allele
                 * and we have no conflict; we don't care about 'N' when determining
                 * conflict
                 */
                if (alleles.size() == 1) {
                    String allele = (String) alleles.keySet().iterator().next();
                    currentConsensusAllele = allele;
                    // consensus allele determined without using a majority
                    isConflict = Boolean.FALSE;
                }
                /**
                 * we have > 1 allele which means we have a conflict
                 * now determine whether we have a majority and if not set
                 * consensusAllele to '?'
                 */
                else {
                    // consensus allele deterimined by majority rule
                    isConflict = Boolean.TRUE;

                    // the count of instances of the current allele
                    int currentCt = 0;

                    // true  if current comparison of allele counts are not equal
                    Boolean isMajority = null;

                    // iterate thru the alleles of this strain
                    for (Iterator j = alleles.keySet().iterator(); j.hasNext(); ) {
                        // get an allele for this strain
                        String allele = (String) j.next();

                        // get number of instances of this allele
                        int count = ( (Integer) alleles.get(allele)).intValue();

                            /** if currentCt < count, the currentConsensusAllele was
                         * determined by majority
                             * if we have 2 alleles e.g. A, T that each have 1 instance
                         * we do not have a majority therefore no consensus
                         * if we have 2 alleles A=2, T=1 A is consensus allele
                         */
                        if (currentCt < count) {
                            currentCt = count;
                            currentConsensusAllele = allele;
                            isMajority = Boolean.TRUE;
                        }
                        else if (currentCt == count) {
                            isMajority = Boolean.FALSE;
                        }
                    }
                    if (isMajority == null) {

                        throw new MGIException(
                            "ERROR determining consensus allele for " +
                            "rs" + rsId + " isMajority == null");
                    }
                    // if there is no majority we assign a "?" to the consensus allele
                    if (isMajority.equals(Boolean.FALSE)) {
                        currentConsensusAllele = "?";
                    }
                }
            }
            if (isConflict == null) {
                    throw new MGIException("ERROR determining consensus allele for " +
                                     "rs" + rsId + " isConflict == null");
            }
            // now create the consensus allele
            MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
            state.setObjectKey(csKey);
            state.setObjectType(SNPLoaderConstants.OBJECTYPE_CSNP);
            state.setJobStreamKey(jobStreamKey);
            state.setMgdStrainKey(strainKey);
            state.setAllele(currentConsensusAllele);
            state.setIsConflict(isConflict);
            dbSNPNse.addStrainAllele(state);
        }

/* BEGIN OLD CODE
        // iterate thru the strainKeys
        for (Iterator i = csAlleleMap.keySet().iterator(); i.hasNext(); ) {
            // the consensus allele determined thus far
            String currentConsensusAllele = "";
            // the count of instances of currentConsensusAllele
            int currentCt = 0;
            // true  if current comparison of allele counts are equal
	    // (we don't have a consensus)
            boolean isEqual = false;

            // get the strain and the alleles
            Integer strainKey = (Integer) i.next();
            HashMap alleles = (HashMap) csAlleleMap.get(strainKey);

             //DEBUG
            if (alleles.size() > 2) {
                logger.logcInfo("RS" + rsId + " has > 2 alleles for strainKey " +
                                strainKey, false);
                for (Iterator k = alleles.keySet().iterator(); k.hasNext(); ) {
                    String allele = (String) k.next();
                    logger.logcInfo("Allele: " + allele + " count " +
                                    alleles.get(allele), false);
                }
            }
            // END DEBUG
	    //
            // iterate thru the alleles
            for (Iterator j = alleles.keySet().iterator(); j.hasNext(); ) {
                // get an allele for this strain
                String allele = (String) j.next();
                // exclude "N" from determining consensus
                if (allele.equals("N")) {
                    continue;
                }
                // get number of instances of this allele
                int count = ( (Integer) alleles.get(allele)).intValue();
                // if currentCt == count, we flag it as equal
                // if we have 2 alleles e.g. A, T that each have 1 instance
                // we do not have a majority therefore no consensus
                // if we have 2 alleles A=2, T=1 A is consensus allele
                if (currentCt == count) {
                    isEqual = true;
                }
                else if (currentCt < count) {
                    currentCt = count;
                    currentConsensusAllele = allele;
                    isEqual = false;
                }
            }
            // if the equal flag is true  OR the consensusAllele is "" (which
            // means the only allele was an 'N',
            // we don't have consensus
            if (isEqual == true || currentConsensusAllele.equals("")) {
                currentConsensusAllele = "?";
            }
            // now create the consensus allele
            MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
            state.setAllele(currentConsensusAllele);
            state.setMgdStrainKey(strainKey);
            // no conflict if only 1 distinct allele for this strain that is
	    // NOT "?" (? means the single allele was an "N")
            if (alleles.size() == 1 && !currentConsensusAllele.equals("?")) {
                state.setIsConflict(Boolean.FALSE);
            }
            // otherwise we have a '?' or a simple majority which is a conflict
            else {
                state.setIsConflict(Boolean.TRUE);
            }
	    // create the State object and set in the DBSNPNse object
            state.setObjectKey(csKey);
            state.setObjectType(SNPLoaderConstants.OBJECTYPE_CSNP);
            state.setJobStreamKey(jobStreamKey);
            dbSNPNse.addStrainAllele(state);
        }
        END OLD CODE */
    }

    /**
     * given a dbsnp strain name (or jax registry id) resolve it to an MGI
     * strain key
     * @param strain a dbsnp strain name of jax registry id
     * @param popId population id of the population from which 'strain' came
     * @throws DBException
     * @throws ConfigException
     * @throws TranslationException
     * @throws CacheException
     */
    private Integer resolveStrain(String strain, String popId) throws
        DBException, ConfigException,
        TranslationException, CacheException {

        Integer strainKey = null;

        // try looking up the strain in the strain vocab
        try {
            strainKey = strainKeyLookup.lookup(strain);
        }
        catch (KeyNotFoundException e) {
            strainKey = null;
        }
        // if not found, it may be a jax registry id
        if (strainKey == null) {
            try {
                strainKey = jaxRegistryLookup.lookup(strain);
            }
            catch (KeyNotFoundException e) {
                strainKey = null;
            }
        }
        // if still not found may be an integer strain id. Create strain name
	// as follows handle_population_strain and lookup again
        if (strainKey == null) {
            try {
		// lookup the handle name
                String handleName = handleNameLookup.lookup(popId);

		// lookup the population name
                String popName = popNameLookup.lookup(popId);

		// create the new strain name
                String qualifiedStrainName = handleName + "_" + popName + "_" +
                    strain.trim();
                //logger.logcInfo("Looking up strain: " + qualifiedStrainName, false);
                // lookup the new strain name
                strainKey = strainKeyLookup.lookup(qualifiedStrainName);
            }
            catch (KeyNotFoundException e) {
                strainKey = null;
            }
        }
        return strainKey;
    }

    /**
     * complements and allele string e.g. ATCG complement is TAGC
     * @param allele the allele string to complement
     * @param ssId the ssid from which this allele was found (for logging
     * of alleles not able to be complemented)
     * @return complemented allele string
     */
    private String complementAllele(String allele, String ssId) {
	// the complemented allele
        StringBuffer convertedAllele = new StringBuffer();

	// create a character array of the allele
        char[] alArray = allele.toCharArray();
	// iterate thru the characters of the allele
        for (int ctr = 0; ctr < alArray.length; ctr++) {
            switch (alArray[ctr]) {
                case 'A':
                    convertedAllele.append("T");
                    break;
                case 'T':
                    convertedAllele.append("A");
                    break;
                case 'C':
                    convertedAllele.append("G");
                    break;
                case 'G':
                    convertedAllele.append("C");
                    break;
                case 'N':
                    convertedAllele.append("N");
                    break;
                case '-':
                    convertedAllele.append("-");
                    break;
                default:
                    logger.logcInfo("Bad allele char for SS" +
                                    ssId + " allele " + alArray[ctr], false);
            }
        }
        return convertedAllele.toString();
    }

    /**
     * adds a strain allele to the Consensus Allele Map
     * @param mgdStrainKey the mgd strain key to which this allele belongs - we
     * have resolved to an mgd strain key because > 1 dbsnp strains map to the
     * same mgd strain key making this calculation incorrect unless we have
     * already resolved the strain.
     * @param allele the allele
     * @param csAlleleMap the map which to add the strain allele looks like
     *   { mgdStrainKey:{allele:count}, ... } where 'count' is the number of
     *   instances of 'allele'
     */
    private void addToConsensusAlleleMap(Integer mgdStrainKey, String allele,
                                         HashMap csAlleleMap) {
        // if mgdStrainKey in the map, add allele and/or count
        if (csAlleleMap.containsKey(mgdStrainKey)) {
            // existingMap looks like {allele:count, ...}
            HashMap existingMap = (HashMap) csAlleleMap.get(
                mgdStrainKey);
	    // if allele already in map, increment its counter
            if (existingMap.containsKey(allele)) {
                int ct = ( (Integer) existingMap.get(allele)).
                    intValue();
                ct++;
                existingMap.put(allele, new Integer(ct));
            }
	    // if allele not already in map add it and set its count to 1
            else {
                existingMap.put(allele, new Integer(1));
            }
            csAlleleMap.put(mgdStrainKey, existingMap);
        }
        // if the strainKey is not in the map, add a new entry
        else {
            HashMap newMap = new HashMap();
            newMap.put(allele, new Integer(1));
            csAlleleMap.put(mgdStrainKey, newMap);
        }
    }

   /**
    * create radar SS DAO objects
    * @param consensusKey radar _ConsensusSNP_key to which 'ss' belongs
    * @param ss DBSNPNseSS object
    * @throws DBException
    * @throws ConfigException
    * @throws CacheException
    * @throws TranslationException
    */
    private Integer processSS(Integer consensusKey, DBSNPNseSS ss) throws
        DBException, ConfigException, CacheException, TranslationException {
        // get the ssId, we will use it alot
        String ssId = ss.getSSId();

        // create a SS state object
        MGI_SNP_SubSNPState state = new MGI_SNP_SubSNPState();

        // add attributes to the SS state object
        state.setConsensusSNPKey(consensusKey);
        state.setExemplar(ss.getIsExemplar());
        state.setJobStreamKey(jobStreamKey);
        state.setOrientation(ss.getSSOrientToRS());
        state.setSubmitterHandle(ss.getSubmitterHandle());
        state.setVariationClass(ss.getSSVarClass());
        state.setObservedAlleles(ss.getObservedAlleles());

        // add the completed state object to the DBSNPNse object
        dbSNPNse.addSS(ssId, state);

        // get the ssKey so we can create other associated objects
        Integer ssKey = dbSNPNse.getSSKey(ssId);

        // create an accession object for the current ssId
        processAccession(ssId, SNPLoaderConstants.LDB_SSNP, ssKey,
                         SNPLoaderConstants.OBJECTYPE_SSNP, Boolean.FALSE);

        // create an accession object for the current submitter snp id
        processAccession(ss.getSubmitterSNPId(),
                         SNPLoaderConstants.LDB_SUBMITTER,
                         ssKey, SNPLoaderConstants.OBJECTYPE_SSNP,
                         Boolean.FALSE);
        return ssKey;
    }
    /**
     * create a radar accession DAO
     * @param accid the accession id
     * @param logicalDB the logical db of the accession id
     * @param objectKey the object key with which we are associating 'accid'
     * @param objectType the object type of 'object key'
     * @param isPrivate true if this association is private
     * @throws DBException
     * @throws ConfigException
     */
    private void processAccession(String accid, String logicalDB,
                                  Integer objectKey, String objectType,
                                  Boolean isPrivate) throws DBException,
        ConfigException {
	// create a stata object
        MGI_SNP_AccessionState state = new MGI_SNP_AccessionState();

	// set attributes
        state.setAccID(accid);
        state.setLogicalDB(logicalDB);
        state.setObjectKey(objectKey);
        state.setObjectType(objectType);
        state.setJobStreamKey(jobStreamKey);
        state.setPrivateVal(isPrivate);

	// set the state in the DBSNPNse object
        dbSNPNse.addAccession(state);
    }

    /**
     * create radar SubSNP strain allele DAOs for a population
     * @param subSNPKey SubSNP key for which we are creating strain alleles
     * @param ssId SubSNP id of the SS for which we are creating strain alleles
     * @param pop Population for which we are creating strain alleles
     * @throws DBException
     * @throws ConfigException
     * @throws CacheException
     * @throws TranslationException
     */
    private void processSSStrainAlleles(Integer subSNPKey, String ssId,
                                        DBSNPGenotypePopulation pop) throws
        DBException, ConfigException, CacheException, TranslationException {
        // create a strain allele DAO for each strain assay of this population
	// get the allele map of the population
        HashMap alleleMap = pop.getStrainAlleles();

	// get the population id
        String popId = pop.getPopId();

	// iterate thru the alleleMap
        for (Iterator i = alleleMap.keySet().iterator(); i.hasNext(); ) {
	    // get the strain
            String strain = (String) i.next();

	    // resolve the strain
            Integer strainKey = resolveStrain(strain, popId);

            // if we can't resolve strain, write it to the curation log
	    // and go on to the next
            if (strainKey == null) {
                logger.logcInfo("BAD STRAIN " + strain + " RS" + rsId + " SS" +
                                ssId + "PopId" + popId, false);
                continue;
            }
	    // create a state object
            MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();

	    // set the state attributes
            state.setObjectKey(subSNPKey);
            state.setObjectType(SNPLoaderConstants.OBJECTYPE_SSNP);
            state.setJobStreamKey(jobStreamKey);
            state.setMgdStrainKey(strainKey);
            state.setPopId(popId);

            // get the allele string from the Allele object, set in state
            Allele a = (Allele) alleleMap.get(strain);
            String allele = a.getAllele();
            // BUILD 125 - map it to "N" for now.
            if (allele.equals(" ")) {
                allele = "N";
            }
            state.setAllele(allele);
            state.setIsConflict(Boolean.FALSE);
            dbSNPNse.addStrainAllele(state);
        }
    }

    /**
     * create radar Flank DAOs
     * @param consensusKey radar _ConsensusKey to which 'flank' belongs
     * @param flank Vector of flanking sequence chunks as parsed
     *   from the input file
     * @param is5Prime true if 5' flank, false if 3' flank
     * @throws DBException
     * @throws ConfigException
     */

    private void processFlank(Integer consensusKey, Vector flank,
                              Boolean is5Prime) throws DBException,
        ConfigException {
        // need to get 255 char chunks of sequence in the flank; the input is
        // chunked, but in variable length chunks :-(
	//
        // get the entire flanking sequence
        StringBuffer entireFlank = new StringBuffer();
        for (Iterator i = flank.iterator(); i.hasNext(); ) {
            entireFlank.append( ( (DBSNPNseFlank) i.next()).getFlank());
        }
        // create a Flank DAO for each 255 char chunk
        int ctr = 0;
        String entireFlankStr = entireFlank.toString();
        String currentFlankChunk;
        while (entireFlankStr.length() > 255) {
            ctr++;
            // get the first 255 chars
            currentFlankChunk = entireFlank.substring(0, 255);
            // remove first 255 chars from entireFlankStr
            entireFlankStr = entireFlankStr.substring(255);
	    // create a Flank DAO for this chunk
            processFlankState(currentFlankChunk, new Integer(ctr), consensusKey,
                              is5Prime);
        }
        // process the final chunk
        if (entireFlankStr.length() != 0) {
            ctr++;
            processFlankState(entireFlankStr, new Integer(ctr), consensusKey,
                              is5Prime);
        }
    }

    /**
     * create flank DAOs
     * @param flankChunk sequence string
     * @param sequenceNum order of 'flankChunk' in a sequence of chunks
     * @param consensusKey radar _Consensus_key to which this flank belongs
     * @param is5Prime true if 5' flank, false if 3' flank
     * @throws DBException
     * @throws MGIException
     */
    private void processFlankState(String flankChunk, Integer sequenceNum,
                                   Integer consensusKey, Boolean is5Prime)
	    throws DBException, ConfigException {

        // create and MGI_FlankState for this chunk
        MGI_SNP_FlankState state = new MGI_SNP_FlankState();

	// set the attributes of the state
        state.setConsensusSNPKey(consensusKey);
        state.setFlank(flankChunk);
        state.setIs5prime(is5Prime);
        state.setSequenceNum(sequenceNum);
        state.setJobStreamKey(jobStreamKey);
        dbSNPNse.addFlank(state);
    }

    /**
     * create radar coordinate DAOs
     * @param consensusKey radar _Consensus_key to which 'contigHits' belong
     * @param contigHits Vector of contig hits for 'consensusKey'
     * @param rsId RefSNP id to which 'contigHits' belongs
     * @throws DBException
     * @throws ConfigException
     * @throws SNPNoBL6Exception
     * @throws SNPMultiBL6ChrException
     */
    private void processCoordinates(Integer consensusKey, Vector contigHits,
                                    String rsId) throws DBException,
        ConfigException, SNPNoBL6Exception, SNPMultiBL6ChrException {

        // true if this RefSNP has a BL6 MapLoc
        boolean bl6Flag = false;

        // true if this RefSNP has a  BL6 MapLoc with no coordinate value
        boolean bl6NoCoordFlag = false;

        // the set of chromosomes on BL6 assembly for this RS
        HashSet bl6ChrSet = new HashSet();

        // iterate over the contig hits
        for (Iterator i = contigHits.iterator(); i.hasNext(); ) {
            DBSNPNseContigHit cHit = (DBSNPNseContigHit) i.next();

	    // get the assembly of this contig hit
            String assembly = cHit.getAssembly();

            // skip it if not BL6
            if (!assembly.equals(SNPLoaderConstants.DBSNP_BL6)) {
                continue;
            }

	    // get the chromosome on which this contig hit is found
            String chromosome = cHit.getChromosome();

	    // add the chromosome to set of chr for this RefSNP
            bl6ChrSet.add(chromosome);

	    // get the Map locations of this Contig Hit
            Vector mapLoc = cHit.getMapLocations();

            // iterate over the Map Locations
            for (Iterator j = mapLoc.iterator(); j.hasNext(); ) {
                // get the map location object
                DBSNPNseMapLoc mloc = (DBSNPNseMapLoc) j.next();

                // get the start coordinate
                Double startCoord = mloc.getStartCoord();

                // when we get here we know we are looking at BL6
                // if there is a coordinate, flag it
                if (startCoord != null) {
                    bl6Flag = true;
                }
                // flag the fact therre is at least one BL6 MapLoc where coordinate
                // is null
                else {
                    bl6NoCoordFlag = true;
                    continue;
                }
                // END build 125 DEBUG
                // set the coordinate attributes
                MGI_SNP_CoordinateState cState = new MGI_SNP_CoordinateState();
                cState.setAssembly(assembly);
                cState.setChromosome(chromosome);
                cState.setConsensusSNPKey(consensusKey);
                cState.setJobStreamKey(jobStreamKey);
                // set the location attributes
                cState.setOrientation(mloc.getRSOrientToChr());
                cState.setStartCoord(startCoord);
                dbSNPNse.addCoordinate(cState);

                // now get the fxnSets and create the Marker objects
                Vector fxnSets = mloc.getFxnSets();

                // the distinct set of fxn classes. contains string composed of
                // chromosome + coord + locusId + fxnClass +
                // nucleotideId + proteinId
                HashSet fxnSetSet = new HashSet();

                // iterate over the FxnSets
                for (Iterator k = fxnSets.iterator(); k.hasNext(); ) {
                    DBSNPNseFxnSet fSet = (DBSNPNseFxnSet) k.next();
                    String fxnClass = fSet.getFxnClass();
                    String locusId = fSet.getLocusId();
                    String nucleotideId = fSet.getNucleotideId();
                    String proteinId = fSet.getProteinId();
     		    // create string to add to fxnSetSet
                    String join = chromosome + startCoord + locusId +
                        fxnClass + nucleotideId + proteinId;
                    if (fxnSetSet.contains(join)) {
                        continue;
                    }
                    fxnSetSet.add(join);

                    // create the state object
                    MGI_SNP_MarkerState mState = new MGI_SNP_MarkerState();
                    mState.setConsensusSNPKey(consensusKey);
                    mState.setEntrezGeneId(locusId);
                    mState.setFxnClass(fxnClass);
                    mState.setChromosome(chromosome);
                    mState.setStartCoord(startCoord);
                    mState.setRefseqNucleotide(fSet.getNucleotideId());
                    mState.setRefseqProtein(fSet.getProteinId());
                    mState.setContigAllele(fSet.getContigAllele());
                    mState.setResidue(fSet.getAAResidue());
                    mState.setAaPosition(fSet.getAAPostition());
                    mState.setReadingFrame(fSet.getReadingFrame());
                    mState.setJobStreamKey(jobStreamKey);
                    dbSNPNse.addMarker(mState);
                }
            }
        }

        // throw an exception if > 1 BL6 chromosome
        if (bl6ChrSet.size() > 1) {
           /* logger.logcInfo("RS" + rsId + " has " + bl6ChrSet.size() +
                            " chromosomes", false);
            for (Iterator j = bl6ChrSet.iterator(); j.hasNext(); ) {
                logger.logcInfo( (String) j.next(), false);
            }*/

            SNPMultiBL6ChrException e = new
                SNPMultiBL6ChrException();
            e.bind("rsId=" + rsId);
            throw e;
        }
        // log that there is a BL6 MapLoc w/o a coordinate value
        if(bl6NoCoordFlag == true) {
            logger.logcInfo("RS" + rsId +
                            " has at least one null BL6 startcoord ", false);
        }
        // throw an exception if no BL6 coordinates for this rs,
        // we don't want to load this rs
        if (bl6Flag == false) {
            SNPNoBL6Exception e = new
                SNPNoBL6Exception();
            e.bind("rsId=" + rsId);
            throw e;
        }

    }
}

// $Log
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
