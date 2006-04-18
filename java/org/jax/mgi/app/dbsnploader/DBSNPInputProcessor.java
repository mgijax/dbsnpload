// $Header
// $Name

package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.snp.dao.*;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.ChromosomeKeyLookup;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;
import org.jax.mgi.shr.cache.CacheConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.snp.lookup.SNPAccessionLookup;
import org.jax.mgi.dbs.SchemaConstants;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * is an object that processes a DBSNPInput object. It resolves and/or
 * translates raw values into MGI values and creates bcp files
 * @has Lookups to resolve attributes,
 *      SQLStream to write to snp bcp files
 *      A running list of RS ids already looked at, to avoid dups
 * @does resolves snp attributes writing them to bcp files a
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPInputProcessor {

    // snp stream; writes to bcp files
    private SQLStream snpStream;

    // logger for the load
    private DLALogger logger;

    // get a sequence load configurator
    private DBSNPLoaderCfg loadCfg;

    // Compound object holding all the DAOs representing an RS
    private SNPSNP snpSnp;

    // Set of RS ids we have processed (so we don't load dups)
    private HashSet rsIdSet;

    /**
     * Counters for logging statistics
     */
    // the current rsId we are processing
    private String rsId;

    // current number of RefSNPs processed
    private int rsCtr = 0;

    // current number of SubSNPs processed
    private int ssCtr = 0;

    // current number of radar snp/marker relationships process
    private int rdrMkrCtr = 0;

    // current number of snp coordinates processed
    private int snpCoordCtr = 0;

     // current number of ss (for BL6 RS) w/o strain alleles)
    private int ssNoStAllele = 0;

    /**
     * Lookups
     */
    // the mapping of strainIds to strain names
    private HashMap individualMap;

    // SS populations by RS
    // {rsId:{ssid:Vector of DBSNPGenotypePopulation}, ... }
    private HashMap rsPopulationsBySS;

    // lookup mgd db strain key, given a strain  name
    private StrainKeyLookup strainKeyLookup;

    // lookup mgd db strain name given a strain key
    private StrainNameLookup strainNameLookup;

    // uniq set of snp strain keys to create SNP_Strain
    private HashSet strainKeySet;

    // lookup mgd db strain key, given a jax registry id
    private AccessionLookup jaxRegistryLookup;

    // lookup snp db handle name given a population id
    private HandleNameByPopIdLookup handleNameLookupByPopId;

    // lookup population key given population id
    private SNPAccessionLookup populationKeyLookupByPopId;

    // lookup population name given a population id
    private PopNameByPopIdLookup popNameLookupByPopId;

    // lookup handle key given handle name
    private VocabKeyLookup subHandleKeyLookup;

    // lookup variation class key given variation class
    private VocabKeyLookup varClassKeyLookup;

    // lookup fxn class key given a fxn class
    // we use this for verification only; fxnClassName
    // is loaded to DP_SNP_Marker. snpcacheload then
    // resolves the fxn class to a key.
    private VocabKeyLookup fxnClassKeyLookup;

    // lookup MRK_Chromosome._Chromosome_key by name
    private ChromosomeKeyLookup chrLookupByName;

    // get a chromosome sequenceNum given a _Chromosome_key
    private ChrSeqNumLookup chrSeqNumLookupByKey;

    /**
     * Helper classes
     */
    // orders a set of alleles
    private AlleleOrderer alleleOrderer;

    // resolves an allele string to an iupac code
    private IUPACResolver iupacResolver;

    // Exception Factory
    private SNPLoaderExceptionFactory snpEFactory;

    /**
     * Constructs a DBSNPInputProcessor with a snp stream
     * @assumes Nothing
     * @param snpSqlStream stream for writing snp bcp files
     * @throws CacheException - if lookup caching erro
     * @throws KeyNotFoundException - if error creating ChromosomeKeyLookup
     * @throws TranslationException - if error creating any VocabKeyLookup
     *      or ChromsomeKeyLookup
     * @throws ConfigException - if error accessing configuration
     * @throws DBException if error creating DBSNPGenotype objects
     * @throws DLALoggingException if error creating a logger
     */

      public DBSNPInputProcessor(SQLStream snpSqlStream) throws
        CacheException, KeyNotFoundException, TranslationException,
        DBException, ConfigException, DLALoggingException {

        // set the streams
        snpStream = snpSqlStream;

        // get a logger
        logger = DLALogger.getInstance();

        // configurator
        loadCfg = new DBSNPLoaderCfg();

        // initialize HashMap for populations
        rsPopulationsBySS = new HashMap();

        // to lookup a strain key in mgd given a strain name and
        // initialize now, so we can close the mgd connection
        strainKeyLookup = new StrainKeyLookup();
        strainKeyLookup.initCache();

	    // lookup strain name by strain key
        strainNameLookup = new StrainNameLookup();
        strainNameLookup.initCache();

        // unique set of strain keys
        strainKeySet = new HashSet();

        // to lookup a strain key in mgd given a JAX registry id and
        // initialize now, so we can close the mgd connection
        jaxRegistryLookup = new AccessionLookup(LogicalDBConstants.JAXREGISTRY,
                                                MGITypeConstants.STRAIN,
                                                AccessionLib.PREFERRED);
        jaxRegistryLookup.initCache();

        // to lookup a submitter handle name in snp given a population id and
        handleNameLookupByPopId = new HandleNameByPopIdLookup();

        // to lookup a population key in snp given a population id
        populationKeyLookupByPopId = new SNPAccessionLookup(LogicalDBConstants.SNPPOPULATION,
                MGITypeConstants.SNPPOPULATION);

        // to lookup a population name in snp given a population id
        popNameLookupByPopId = new PopNameByPopIdLookup();

        // to lookup term key in mgd given a submitter handle and
        // initialize now, so we can close the mgd connection
        subHandleKeyLookup = new VocabKeyLookup(VocabularyTypeConstants.SUBHANDLE,
            CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
        subHandleKeyLookup.initCache();

        // to lookup term key in mgd given a variation class and
        // initialize now, so we can close the mgd connection
        varClassKeyLookup = new VocabKeyLookup(VocabularyTypeConstants.SNPVARCLASS,
                CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
        varClassKeyLookup.initCache();

        // to lookup term key in mgd given a function class and
        // initialize now, so we can close the mgd connection
        fxnClassKeyLookup = new VocabKeyLookup(VocabularyTypeConstants.SNPFUNCTION,
                CacheConstants.FULL_CACHE, CacheConstants.FULL_CACHE);
        fxnClassKeyLookup.initCache();

        // to lookup a chromosome sequence number of a given a mouse chromosome
        // in mgd. First get the key then get the sequenceNum
        // initialize now, so we can close the mgd connection
        chrLookupByName = new ChromosomeKeyLookup(SNPLoaderConstants.MGI_MOUSE);
        chrLookupByName.initCache();
        chrSeqNumLookupByKey = new ChrSeqNumLookup();
        chrSeqNumLookupByKey.initCache();

        // create set to avoid loading dups
        rsIdSet = new HashSet();

        // strain id to strain name map {strainId:strainName, ...}
        individualMap = new HashMap();

        // orders a set of alleles
        alleleOrderer = new AlleleOrderer();

        // resolves an allele string to an iupac code
        iupacResolver = new IUPACResolver();

        // close the mgd connection provided by superclass now that we have
        // created the lookups
        //SQLDataManagerFactory.getShared(SchemaConstants.MGD).closeResources();
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
     * Adds a refSnp-to-ss population mapping to rsPopulationsBySS Map
     * @param input a DBSNPGenotypeRefSNPInput object
     */
    public void processGenoRefSNPInput(DBSNPGenotypeRefSNPInput input) {

        // get the rs id
        rsId = ( (DBSNPGenotypeRefSNPInput) input).getRsId();
        // get the ss population(s) for this rs
        HashMap ssPopulations = ( (DBSNPGenotypeRefSNPInput) input).
            getSSPopulationsForRs();
        // map the rsId to the ss population(s)
        rsPopulationsBySS.put(rsId, ssPopulations);
    }

    /**
     * Resolves and/or translates raw values to MGI values and creates bcp files
     * @param input DBSNPNseInput object containing objects representing raw values
     * from the input the NSE Input file
     * @effects - writes bcp files to a filesystem
     * @throws MGIException if
     * <OL>
     * <LI>can't resolve a strain
     * <LI>a RefSNP is repeated
     * <LI>no BL6 coordinates
     * <LI>BL6 coordinates on > 1 chromosome
     * <LI>if error using lookups
     * <LI>if error accessing configuration
     * <LI>if error resolving a) orientation b)
     * </OL
     */
    public void processInput(DBSNPNseInput input) throws MGIException {
        // The compound snp object we are building
        snpSnp = new SNPSNP(snpStream);

        // get the rs object and its rsId from 'input'
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

        // W know we don't have a repeat; get the rest of the data objects from 'input'
        Vector subSNPs = ( (DBSNPNseInput) input).getSubSNPs();
        Vector flank3Prime = ( (DBSNPNseInput) input).get3PrimeFlank();
        Vector flank5Prime = ( (DBSNPNseInput) input).get5PrimeFlank();
        Vector contigHits = ( (DBSNPNseInput) input).getContigHits();

        // get the set of strain alleles, for this rs
        // looks like {ssid:Vector of DBSNPGenotypePopulation objects, ...}
        HashMap currentSSPopulationMap = (HashMap) rsPopulationsBySS.get(
            rsId);

        // if currentSSPopulationMap is NULL (when no record in the genotype
        // file for this rs) throw an exception so the loader
        // can decide what to do (load or not load that is the question)
        if (currentSSPopulationMap == null) {
            SNPNoStrainAlleleException e = new
                SNPNoStrainAlleleException();
            e.bind(rsId);
            throw e;
        }

        // create the consensus object
        Integer consensusKey = processConsensusSnp(rs);

        // process coordinates first; RS with no C57BL/6J coordinates are
        // rejected by this method
        processCoordinates(consensusKey, contigHits, rsId);

        // create ACC_AccesssionDAO for the rs id

        processAccession(SNPLoaderConstants.PREFIX_CSNP + rsId, LogicalDBConstants.REFSNP, consensusKey,
                         MGITypeConstants.CONSENSUSSNP);
        /**
         * for each SubSnp create:
         * 1)  subSNP DAO
         * 2)  accession DAOs for its ssId and submitter snp id
         * 3)  strain allele DAOs for each population's strain alleles
         */
        for (Iterator i = subSNPs.iterator(); i.hasNext(); ) {
            DBSNPNseSS ss = (DBSNPNseSS) i.next();

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

            // create all DAO's for SubSnps (SNP_SubSnpDAOs, SNP_AccessionDAOs
            // SNP_SubSnp_StrainAlleleDAOs, SNP_StrainDAOs)
            processSS(consensusKey, ss, popsForSSVector);
        }

        // create flank DAOs for the 5' flanking sequence
        processFlank(consensusKey, flank5Prime, Boolean.TRUE);

        // create flank DAOs for the 3' flanking sequence
        processFlank(consensusKey, flank3Prime, Boolean.FALSE);

        // create the consensus allele DAOs for this RS
        // send rsId just for debug
        String orderedAlleleSummary = processConsensusAlleles(consensusKey,
            currentSSPopulationMap, rsId);

        // resolve the remaining SNP_ConsensusSnpState attributes
        finishConsensusSnp(orderedAlleleSummary, rs.getRsVarClass());

        // if we have gotten this far, we have complete snp and mgd SNP objects
        // send them to their streams.
        snpSnp.sendToStream();

        // incr ctr, we've added another snp
        rsCtr++;
    }

    /**
     * Gets a Vector of Strings reporting various load statistics
     * @assumes nothing
     * @effects nothing
     * @return Vector of Strings reporting
     * <OL>
     * <LI>Total SNPs created
     * <LI>Total SS with no strain alleles
     * <LI>Statistics from the mgd SNPProcessor
     * </OL>
     */
    public Vector getProcessedReport() {
        Vector report = new Vector();
        report.add("Total RefSNPs created: " + rsCtr);
        report.add("Total SubSNPs created: " + ssCtr);
        report.add("Total SS with no strain alleles: " + ssNoStAllele);
        report.add("Total snp/marker relationships created: " + rdrMkrCtr);
        report.add("Total snp coordinates created: " + snpCoordCtr);
        /*
        for (Iterator i = snpProcessor.getProcessedReport().iterator();
             i.hasNext(); ) {
            report.add( (String) i.next());
        }
*/
        return report;
    }

    /**
     * creates an SNP_ConsensusSNPDAO object and sets in the SNPSNP object
     * @param rs a DBSNPNseRS object
     * @return SNP_ConsensusSNP._ConsensusSNP_key
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     */
    private Integer processConsensusSnp(DBSNPNseRS rs) throws DBException,
        ConfigException {
        // create the state and set in the SNP object, we'll be adding
        // alleleSummary, iupacCode, and variation class key later after
        // we process consensus alleles
        SNP_ConsensusSnpState state = new SNP_ConsensusSnpState();
        state.setBuildCreated(rs.getBuildCreated());
        state.setBuildUpdated(rs.getBuildUpdated());
        snpSnp.setConsensusSnp(state);
        return snpSnp.getConsensusSnpKey();
    }


    /**
     * Adds alleleSummary, iupac code, _VarClass_key to the current SNP_ConsensusSnpState object
     * Adds _VarClass_key to SNP_Coord_CacheState object
     * These three attributes can be determined only after all ConsensusSnp alleles
     * have been processed.
     * @param orderedAlleleSummary - allele summary string
     * @param dbsnpVarClass - the raw dbSNP variation class
     * @throws SNPVocabResolverException
     * @throws ConfigException - if error accessing configuration
     * @throws TranslationException
     * @throws CacheException - if lookup caching error
     * @throws DBException
     */
    private void finishConsensusSnp(String orderedAlleleSummary, String dbsnpVarClass)
        throws SNPVocabResolverException, ConfigException, TranslationException,
        CacheException, DBException {
        // resolve allele summary to iupac code
        String iupacCode = iupacResolver.resolve(orderedAlleleSummary);

        // resolve the variation class
        Integer varClassKey = resolveCSVarClass(orderedAlleleSummary, dbsnpVarClass);

        // get the SNP_ConsensusSnpState from the MGDSNP and add alleleSummary
        // variation class key and iupac code
        SNP_ConsensusSnpState csState = snpSnp.getConsensusSnpDao().getState();
        csState.setAlleleSummary(orderedAlleleSummary);
        csState.setVarClassKey(varClassKey);
        csState.setIupacCode(iupacCode);

        // get the set of SNP_Coord_CacheDAOs from the MGDSNP so we can add
        // alleleSummary, variation class key, and iupac code
        Vector v = snpSnp.getCoordCacheDaos();
        for(Iterator i = v.iterator(); i.hasNext();) {
            SNP_Coord_CacheState ccState = ((SNP_Coord_CacheDAO)i.next()).getState();
            ccState.setAlleleSummary(orderedAlleleSummary);
            ccState.setVarClassKey(varClassKey);
            ccState.setIupacCode(iupacCode);
            snpCoordCtr++;
        }
    }

    /**
     * determines the consensus snp variation class based on the allele summary
     * @param orderedAlleleSummary - allele summary string
     * @param dbsnpVarClass - the raw dbSNP variation class
     * @return variation class key
     * @throws DBException
     * @throws CacheException - if lookup caching error
     * @throws TranslationException
     * @throws ConfigException - if error accessing configuration
     * @throws SNPVocabResolverException
     * @note
     * See the requirements doc for Richard's algorithm. This method could
     * be written with fewer if statements, but I felt it important that the
     * algorithm be readily apparent in the code.
     * See the requirements/design doc at
     * /mgi/all/wts_projects/1500/1560/Data_Req_Design/DataRequirements.pdf
     */
    private Integer resolveCSVarClass( String orderedAlleleSummary, String dbsnpVarClass)
        throws DBException, CacheException, TranslationException,
            ConfigException, SNPVocabResolverException {
        String varClass = null;
        // for testing
        Integer varClassKey = null;

        // mapping of length of allele to alleles with that length
        HashMap map = new HashMap();

        // break the alleleSummary into tokens
        StringTokenizer alleleTokenizer = new StringTokenizer(orderedAlleleSummary, "/");

        // the number of alleles in the allele summary
        int numAlleles = alleleTokenizer.countTokens();

        // load the map
        while (alleleTokenizer.hasMoreTokens()) {
            String allele = alleleTokenizer.nextToken();
            if(!allele.equals("-")) {

               Integer len = new Integer(allele.length());
               if(map.keySet().contains(len)) {
                   // add allele to the map for key 'len'
                   ((Vector)map.get(len)).add(allele);
               }
               else {
                   // add new key 'len' with value 'v'
                   Vector v = new Vector();
                   v.add(allele);
                   map.put(len, v);
               }
            }
        }
        // the set of alleles sizes for the current alleleSummary
        Set alleleSizes = map.keySet();
        // if dbsnp varClass is 'named', we call it 'named'
        if(dbsnpVarClass.equals(SNPLoaderConstants.VARCLASS_NAMED)) {
           varClass = dbsnpVarClass;
        }
        // if there is a deletion ('-')
        else if(orderedAlleleSummary.startsWith("-")) {
            // if '-' is the only allele, or if there are only 2 alleles:
            if(orderedAlleleSummary.equals("-") ||  numAlleles == 2){
               varClass = SNPLoaderConstants.VARCLASS_INDEL;
            }
            // if there are >2 alleles and all alleles, excluding '-',
            // are of different sizes (numAlleles - 1 because we have excluded
            // the '-' allele from the map)
            else if ( (numAlleles > 2) && (alleleSizes.size() == numAlleles - 1)) {
                varClass = SNPLoaderConstants.VARCLASS_INDEL;
            }
            // if there are >2 alleles and >1 of the same size
            else if ( (numAlleles > 2)) {
                for (Iterator i = alleleSizes.iterator(); i.hasNext();) {
                    if ( ( (Vector) map.get( ( (Integer) i.next()))).size() > 1) {
                        varClass = SNPLoaderConstants.VARCLASS_MIXED;
                    }
                }
            }
            // log uncovered cases
            else {
                logger.logdDebug("CSVarClass Uncovered Case for RS" + rsId +
                                 " alleleSummary: " + orderedAlleleSummary);
            }

        }
        // if there is NOT a deletion
        else {
            // if all alleles are singletons (same size and that size is 1)
            if (alleleSizes.size() == 1 && alleleSizes.contains(new Integer(1))) {
                varClass = SNPLoaderConstants.VARCLASS_SNP;
            }
            // if all alleles are not singletons and are of the same size
            else if (alleleSizes.size() == 1 ) {
                varClass = SNPLoaderConstants.VARCLASS_MNP;
            }
            // if all alleles are of different sizes
            else if (alleleSizes.size() == numAlleles) {
                varClass = SNPLoaderConstants.VARCLASS_INDEL;
            }
            // if >2 alleles and > 1 of the same size
            else if((numAlleles > 2)) {
                for (Iterator i = alleleSizes.iterator(); i.hasNext();) {
                    if ( ( (Vector) map.get( ( (Integer) i.next()))).size() > 1) {
                        varClass = SNPLoaderConstants.VARCLASS_MIXED;
                    }
                }
            }
            else {
                logger.logdDebug("CSVarClass Uncovered Case for RS" + rsId +
                                 " alleleSummary: " + orderedAlleleSummary);
            }
        }
        // now resolve if not null
        if (varClass == null) {
            // case not covered; log it
            logger.logcInfo("resolveCSVarClass case not covered. RS" +
                            rsId + " dbsnpVarClass: " +
                            dbsnpVarClass + " alleleSummary " + orderedAlleleSummary, false);
        }
        else {
            try {
                varClassKey = varClassKeyLookup.lookup(varClass);
            }
            catch (KeyNotFoundException e) {
                logger.logcInfo("UNRESOLVED CS VARCLASS " + varClass +
                                " RS" + rsId, false);
                SNPVocabResolverException e1 = new
                    SNPVocabResolverException("VarClass");
                e1.bind("RS" + rsId + " varClass " + varClass);
                throw e1;
            }
        }
        return varClassKey;
    }

    /**
     * create consensus snp allele objects
     * @param consensusKey _ConsensusSnp_key for the current Consensus Snp
     * @param ssPopulationMap {ssId:Vector of Population objects, ...}
     * @param rsId for the current Consensus Snp
     * @assumes unresolvable strains are reported when processing
     *           SS strain alleles
     * @return allele summary string
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     * @throws TranslationException
     * @throws CacheException - if lookup caching error
     * @throws KeyNotFoundException if can't resolve strain
     * @throws SNPNoConsensusAlleleSummaryException if not able to create a
     *            ConsensusAllele summary
     * @throws MGIException if error determining consensus allele
     */
    private String processConsensusAlleles(Integer consensusKey,
                                         HashMap ssPopulationMap,
                                         String rsId)
        throws DBException, ConfigException, TranslationException,
			      CacheException, KeyNotFoundException,
			      SNPNoConsensusAlleleSummaryException, MGIException {

        //  map strain to alleles and count of each allele
        // consensusAlleleMap looks like strain:HashMap[allele:count]
        HashMap consensusAlleleMap = new HashMap();

        // summary of the consensus alleles
        HashSet alleleSummarySet = new HashSet();

        /**
         * Iterate thru each SS
         */
        for (Iterator i = ssPopulationMap.keySet().iterator(); i.hasNext(); ) {
            // get the ssid
            String currentSSId = (String) i.next();

            // get the set of populations for this SS
            Vector population = (Vector) ssPopulationMap.get(currentSSId);

            /**
             * Iterate thru the populations of the current SS and create the
             * allele summary. Also create a mapping of strain to allele and its
             * instance count { strainKey:{allele:count}, ... } for determining
             * the consensusAllele by majority
             */

            for (Iterator j = population.iterator(); j.hasNext(); ) {
                DBSNPGenotypePopulation pop = (DBSNPGenotypePopulation) j.next();
                HashMap alleleMap = pop.getStrainAlleles();
                /**
                 * Iterate thru strains
                 */
                for (Iterator k = alleleMap.keySet().iterator(); k.hasNext(); ) {
                    String strain = ((String) k.next()).trim()  ;
                    Integer strainKey = resolveStrain(strain, pop.getPopId()).getMgdStrainKey();
                    // if we can't resolve the strain, continue
                    if (strainKey == null) {
                        continue;
                    }
                    // get the allele string from the Allele object
                     Allele a = (Allele) alleleMap.get(strain);
                     String allele = a.getAllele();
                     // BUILD 125 - map " " allele to "N" for now.
                     if (allele.equals(" ")) {
                         allele = "N";
                     }
                     else if (allele.equals("--")) {
                         allele = "-";
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
                    addToConsensusAlleleMap(strainKey, allele,
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
            logger.logcInfo("ALLELE SUMMARY: for RS" + rsId + " is " +
                summaryString.length(), false);
        }
        // order the allele summary
        String orderedAlleleSummary = alleleOrderer.order(summaryString.toString());


        /**
         * now find the consensus alleles for each strain
         */
        createConsensusAlleles(consensusKey, consensusAlleleMap);
        return orderedAlleleSummary;
    }

    /**
     * calculates the consensus allele for each strain
     * @param csKey the _ConsensusSNP_key
     * @param csAlleleMap strainKey:HashMap[allele:count]
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     * @throws MGIException if error determining consensus allele
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
                 * if we have only one allele now that we removed a 'N' allele,
                 * we have no conflict; we don't care about 'N' when determining
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
                 * now determine whether we have a majority; if not set
                 * consensusAllele to '?'
                 */
                else {
                    // consensus allele deterimined by majority rule has a conflict
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
            SNP_ConsensusSnp_StrainAlleleState state = new SNP_ConsensusSnp_StrainAlleleState();
            state.setConsensusSnpKey(csKey);
            state.setMgdStrainKey(strainKey);
            state.setAllele(currentConsensusAllele);
            state.setIsConflict(isConflict);
            snpSnp.addConsensusSnpStrainAllele(state);
        }
    }

    /**
     * given a dbsnp strain name (or jax registry id) resolve it to an MGI
     * strain key
     * @param strain a dbsnp strain name of jax registry id
     * @param popId population id of the population from which 'strain' came
     * @return SNP_StrainState object, which may be null
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     * @throws TranslationException
     * @throws CacheException - if lookup caching error
     * @throws KeyNotFoundException - if unable to resolve strain
     */
    private SNP_StrainState resolveStrain(String strain, String popId) throws
        DBException, ConfigException,
        TranslationException, CacheException, KeyNotFoundException {

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
                String handleName = handleNameLookupByPopId.lookup(popId);

                // lookup the population name
                String popName = popNameLookupByPopId.lookup(popId);

                // create the new strain name
                String qualifiedStrainName = handleName + "_" + popName + "_" +
                    strain;
                // lookup the new strain name
                strainKey = strainKeyLookup.lookup(qualifiedStrainName);
            }
            catch (KeyNotFoundException e) {
                strainKey = null;
            }
        }
        SNP_StrainState state = null;
        if (strainKey != null) {
            String name = strainNameLookup.lookup(strainKey);
            state = new SNP_StrainState();
            state.setMgdStrainKey(strainKey);
            state.setStrain(name);
            // another process sets sequenceNum
            state.setSequenceNum(new Integer(1));
        }
        return state;
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
    * creates SNP_SubSnpDAO(s), SNP_AccessionDAOs for submitter snp id and SubSnp id,
    * SNP_SubSnp_StrainAlleleDAO(s), and MGI_SetMemberDAO(s) for SNP Strain set
    * @param consensusKey _ConsensusSNP_key to which 'ss' belongs
    * @param ss DBSNPNseSS (raw dbsnp ss data) object
    * @param populations the set of populations for 'ss'
    * @throws DBException
    * @throws ConfigException - if error accessing configuration
    * @throws CacheException - if lookup caching error
    * @throws TranslationException
    * @throws KeyNotFoundException
    * @throws SNPVocabResolverException
    */
    private void processSS(Integer consensusKey, DBSNPNseSS ss, Vector populations) throws
        DBException, ConfigException, CacheException, TranslationException,
        KeyNotFoundException, SNPVocabResolverException {
        // get the ssId, we will use it alot
        String ssId = ss.getSSId();

        // create a SS state object
        SNP_SubSnpState state = new SNP_SubSnpState();

        /**
         * set attributes to the SNP_SubSnpState object
         */

        // add _ConsensusSnp_key
        state.setConsensusSnpKey(consensusKey);

        // resolve and set _SubmitterHandle_Key
        try{
               state.setSubHandleKey(subHandleKeyLookup.lookup(ss.
                   getSubmitterHandle()));
        } catch (KeyNotFoundException e) {
            String h = ss.getSubmitterHandle();
            logger.logcInfo("UNRESOLVED SUBMITTERHANDLE " + h +
                    " RS" + rsId, false);
            SNPVocabResolverException e1 = new
                SNPVocabResolverException("SubHandle");
            e1.bind("RS" + rsId + " SubHandle" + h);
            throw e1;
        }
        // resolve and set _VariationClass_key
        try {
            state.setVarClassKey(varClassKeyLookup.lookup(ss.getSSVarClass()));
        } catch (KeyNotFoundException e) {
            String v = ss.getSSVarClass();
            logger.logcInfo("UNRESOLVED SS VARCLASS " + v +
                " RS" + rsId, false);
            SNPVocabResolverException e1 = new
                SNPVocabResolverException("VarClass");
            e1.bind("RS" + rsId + " varClass " + v);
            throw e1;
        }
        // resolve and set orientation
          String orient = ss.getSSOrientToRS();
          String translatedOrient = "";
          if(orient.equals(SNPLoaderConstants.NSE_FORWARD)){
              translatedOrient = "f";
          }
          else if (orient.equals(SNPLoaderConstants.NSE_REVERSE)){
              translatedOrient = "r";
          }
          else {
              logger.logdDebug("Unhandled SS orientation " + orient +
                  " for RS" + rsId);
              throw new TranslationException("Unhandled SS orient " +
                  orient + " for RS" + rsId, true);
          }
          state.setOrientation(translatedOrient);
          // set isExemplar bit
          state.setIsExemplar(ss.getIsExemplar());

          // set observedAlleles
          state.setAlleleSummary(ss.getObservedAlleles());

          // set ss state object in the snp object, this returns the ssKey
          // for use creating accession and strain allele objects
          Integer ssKey = snpSnp.addSubSNP(state);
          // count of the total ss loaded
          ssCtr++;

          // create an accession object for the ssId
          processAccession(SNPLoaderConstants.PREFIX_SSNP + ssId, LogicalDBConstants.SUBSNP,
                           ssKey, MGITypeConstants.SUBSNP);

          // create an accession object for the current submitter snp id
          processAccession(ss.getSubmitterSNPId(),
              LogicalDBConstants.SUBMITTERSNP, ssKey, MGITypeConstants.SUBSNP);

          // process the strain alleles
          for (Iterator j = populations.iterator(); j.hasNext(); ) {
            processSSStrainAlleles(ssKey, ssId,
                (DBSNPGenotypePopulation) j.next());
         }

    }
    /**
     * create a SNP_AccessionState and set in the SNPSNP object
     * @param accid an accession id
     * @param logicalDBKey  _LogicalDB_key of the accession id
     * @param objectKey  _Object_key with which we are associating 'accid'
     * @param mgiTypeKey  _MGIType_key for the object type of 'object key'
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     */
    private void processAccession(String accid, int logicalDBKey,
                                  Integer objectKey, int mgiTypeKey)
            throws DBException, ConfigException {

        // create a state object
        SNP_AccessionState state = new SNP_AccessionState();

        state.setAccID(accid);

        // split 'accid' into prefixPart and numericPart and set in state
        Vector splitAccession = AccessionLib.splitAccID(accid);
        state.setPrefixPart((String)splitAccession.get(0));
        state.setNumericPart((Integer)splitAccession.get(1));

        // set logicalDB in state
        state.setLogicalDBKey(new Integer(logicalDBKey));

        // set object key in state
        state.setObjectKey(objectKey);

        // set mgi type
        state.setMGITypeKey(new Integer(mgiTypeKey));

        // set the state in the SNPSNP object
        snpSnp.addAccession(state);
    }

    /**
     * create SubSNP strain allele DAOs for a population
     * @param ssKey SubSNP key for which we are creating strain alleles
     * @param ssId SubSNP id of the SS for which we are creating strain alleles
     * @param pop Population for which we are creating strain alleles
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     * @throws CacheException - if lookup caching error
     * @throws TranslationException if error translating strain
     * @throws KeyNotFoundException if fail to resolve strain or population id to its name
     */
    private void processSSStrainAlleles(Integer ssKey, String ssId,
                                        DBSNPGenotypePopulation pop) throws
        DBException, ConfigException, CacheException, TranslationException,
        KeyNotFoundException {
        // create a strain allele DAO for each strain assay of this population
        // get the allele map of the population
        HashMap alleleMap = pop.getStrainAlleles();

        // get the population id
        String popId = pop.getPopId();

        // iterate thru the alleleMap
        for (Iterator i = alleleMap.keySet().iterator(); i.hasNext(); ) {
            // get the strain
            String strain = ((String) i.next()).trim();
            // get the allele, translate blank alleles to "N"
            Allele a = (Allele) alleleMap.get(strain);
            String allele = a.getAllele();
            // BUILD 125 - map it to "N" for now.
            if (allele.equals(" ")) {
                allele = "N";
            }
            if (allele.equals("--")) {
                allele = "-";
            }
            // resolve the strain
            SNP_StrainState strainState= resolveStrain(strain, popId);

            // if we can't resolve strain, write it to the curation log
            // and go on to the next
            if (strainState == null) {
                logger.logcInfo("BAD STRAIN " + strain + " RS" + rsId + " SS" +
                                ssId + "PopId" + popId, false);
                continue;
            }
            // get the strainKey attribute from strainState
            Integer strainKey = strainState.getMgdStrainKey();

            // create SNP_StrainDAO for this strain, if we haven't already
            createStrain(strainState);

            // create a SNP_SubSnp_StrainAlleleState object and set it's attributes
            SNP_SubSnp_StrainAlleleState state = new SNP_SubSnp_StrainAlleleState();
            state.setSubSnpKey(ssKey);

            // resolve and set the population key
            // allow this to throw KeyNotFoundException, precondition is that
            // populations are in place
            state.setPopulationKey(populationKeyLookupByPopId.lookup(popId));

            // set the strain key
            state.setMgdStrainKey(strainKey);

            // set the allele
            state.setAllele(allele);

            // set the state object in the SNPSNP
            snpSnp.addSubSnpStrainAllele(state);
        }
    }
    /**
    * Add a SNP_StrainState to the SNPSNP object for this strain if we haven't already
    * @param state SNP_StrainState from which to create DAO
    * @throws DBException
    * @throws CacheException - if lookup caching error
    * @throws ConfigException - if error accessing configuration
    */
    private void createStrain(SNP_StrainState state)
             throws DBException, CacheException, ConfigException {
         Integer key = state.getMgdStrainKey();
         // if strainSet contains 'key' we have already created a SNP_Strain object
         if (!strainKeySet.contains(key)) {
             strainKeySet.add(key);
             snpSnp.addStrain(state);
         }
     }

    /**
     * create Flank DAOs
     * @param consensusKey  _ConsensusKey to which 'flank' belongs
     * @param flank Vector of flanking sequence chunks as parsed
     *   from the input file
     * @param is5Prime true if 5' flank, false if 3' flank
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     */

    private void processFlank(Integer consensusKey, Vector flank,
        Boolean is5Prime) throws DBException, ConfigException {
        // need to get 255 char chunks of sequence in the flank; the input is
        // chunked, but in variable length chunks :-(

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
     * @param consensusKey  _Consensus_key to which this flank belongs
     * @param is5Prime true if 5' flank, false if 3' flank
     * @throws DBException
     * @throws ConfigException - if error accessing configuration
     */
    private void processFlankState(String flankChunk, Integer sequenceNum,
                                   Integer consensusKey, Boolean is5Prime)
	    throws DBException, ConfigException {

        // create and MGI_FlankState for this chunk
        SNP_FlankState state = new SNP_FlankState();

        // set the attributes of the state
        state.setConsensusSnpKey(consensusKey);
        state.setFlank(flankChunk);
        state.setIs5Prime(is5Prime);
        state.setSequenceNum(sequenceNum);
        snpSnp.addFlank(state);
    }

    /**
     * creates SNP_Coord_Cache and DP_SNP_Marker DAOs
     * @param consensusKey _Consensus_key to which 'contigHits' belong
     * @param contigHits Vector of contig hits for 'consensusKey'
     * @param rsId RefSNP id to which 'contigHits' belongs
     * @throws DBException - if database error in a lookup or when creating DAOs
     * @throws ConfigException - if error accessing configuration
     * @throws CacheException - if lookup caching error
     * @throws KeyNotFoundException - if can't resolve chromosome, chr sequenceNum
     *         or function class
     * @throws SNPNoBL6Exception - if no BL6 coordinates
     * @throws SNPMultiBL6ChrException - if BL6 coordinates on multiple chr
     * @throws TranslationException - if can't resolve orientation
     */
    private void processCoordinates(Integer consensusKey, Vector contigHits,
                                    String rsId) throws DBException,
        ConfigException, CacheException, KeyNotFoundException,
        SNPNoBL6Exception, SNPMultiBL6ChrException, TranslationException{

        // We're going to need some SNP_ConsensusSnpState attributes in this processing
        SNP_ConsensusSnpState csState = (SNP_ConsensusSnpState)snpSnp.getConsensusSnpDao().getState();

        // true if this SNP has a BL6 coordinate
        boolean bl6Flag = false;

        // true if this SNP has a  BL6 MapLoc with no coordinate value
        boolean bl6NoCoordFlag = false;

        // the set of chromosomes on BL6 assembly for this RS
        HashSet bl6ChrSet = new HashSet();
        //StringBuffer coord = new StringBuffer();

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

            // add the chromosome to set of chr for this SNP
            bl6ChrSet.add(chromosome);

            // get the Map locations of this Contig Hit
            Vector mapLoc = cHit.getMapLocations();

            // iterate over the Map Locations creatng SNP_Coord_Cache objects
            for (Iterator j = mapLoc.iterator(); j.hasNext(); ) {
                // get the map location object
                DBSNPNseMapLoc mloc = (DBSNPNseMapLoc) j.next();

                // get the start coordinate
                Double startCoord = mloc.getStartCoord();

               // when we get here we know we are looking at BL6
                // and have at least one coordinate
                if (startCoord != null) {
                    bl6Flag = true;
                }

                // flag that there is at least one BL6 MapLoc where coordinate
                // is null
                else {
                    bl6NoCoordFlag = true;
                    continue;
                }
                // create the state object
                SNP_Coord_CacheState coordCacheState = new SNP_Coord_CacheState();

                /**
                 * We have BL6 coords on a sgl chr, build the SNP_Coord_Cache object
                 */
                coordCacheState.setConsensusSnpKey(consensusKey);
                coordCacheState.setChromosome(chromosome);

                //resolve the chromosome sequence number; set in the state
                Integer chrKey = chrLookupByName.lookup(chromosome);
                coordCacheState.setSequenceNum( chrSeqNumLookupByKey.lookup(chrKey) );

                // dbSNP xml files now 0 based - need to add 1
                // Note: isMultiCoord is set in snpSnp
                startCoord = new Double (startCoord.intValue() + 1);
                coordCacheState.setStartCoordinate(startCoord);

                /**
                 * get the RS orientation to the chromosome and translate it,
                 * if necessary. Set in SNP_Coord_CacheState object
                 */
                String orient = mloc.getRSOrientToChr();
                String translatedOrient = "";

                if(orient.equals(SNPLoaderConstants.NSE_FORWARD)){
                    translatedOrient = "f";
                }
                else if (orient.equals(SNPLoaderConstants.NSE_REVERSE)){
                    translatedOrient = "r";
                }
                else {
                    logger.logdDebug("Unhandled RS orientation " + orient +
                                     " for RS" + rsId);
                    throw new TranslationException("Unhandled RS orient " + orient
                        + " for RS" + rsId, true);
                }
                coordCacheState.setStrand(translatedOrient);
                // Note: _VarClass_key, alleleSummary, and iupac code not set till
                // later - See finishConsensusSnp() method
                snpSnp.addSnpCoordCache(coordCacheState);
                /**
                 * now get the fxnSets and create MGI_SNP_MarkerState objects
                 */
                Vector fxnSets = mloc.getFxnSets();

                // the distinct set of fxn classes. contains string composed of
                // chromosome + coord + locusId + fxnClass + nucleotideId + proteinId
                // uset this so we don't load dup fxn classes
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
                    Integer fxnKey = fxnClassKeyLookup.lookup(fxnClass);
                    if (fxnKey == null) {
                        logger.logcInfo("UNRESOLVED FXNCLASS " + fxnClass, false);
                        continue;
                    }

                    // create the DP_SNP_MarkerState object
                    DP_SNP_MarkerState mState = new DP_SNP_MarkerState();
                    mState.setAccID(rsId);
                    mState.setEntrezGeneId(locusId);
                    mState.setFxnKey(fxnKey);
                    mState.setChromosome(chromosome);
                    mState.setStartCoord(startCoord);
                    mState.setRefseqNucleotide(fSet.getNucleotideId());
                    mState.setRefseqProtein(fSet.getProteinId());
                    mState.setContigAllele(fSet.getContigAllele());
                    mState.setResidue(fSet.getAAResidue());
                    mState.setAaPosition(fSet.getAAPostition());
                    mState.setReadingFrame(fSet.getReadingFrame());
                    snpSnp.addMarker(mState);

                    rdrMkrCtr++;
                }
            }
        }
           // throw an exception if > 1 BL6 chromosome
           if (bl6ChrSet.size() > 1) {
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
        // throw an exception if no BL6
        if (bl6Flag != true) {
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
