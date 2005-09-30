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
//import org.jax.mgi.dbs.SchemaConstants;
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
 * an object that
 * @has
 *   <UL>
 *   <LI>a logger
 *   <LI>
 *   </UL>
 * @does
 *   <UL>
 *   <LI>
 *   <LI>
 *   <LI>
 *   </UL>
 * @company The Jackson Laboratory
 * @author sc
 * @version 1.0
 */

public class DBSNPInputProcessor {
    // DEBUG
    private Stopwatch stopWatch;

    // for temporary load to mgd
    private SNPProcessor snpProcessor;
    // stream for temporarily loading mgd
    private SQLStream loadStream;

    // a stream for handling RADAR DAO objects
    private SQLStream radarStream;

    // logger for the load
    private DLALogger logger;

    // get a sequence load configurator
    private DBSNPLoaderCfg config;

    // summaryAllele orderer

    // jobstream key for the load
    private Integer jobStreamKey;

    // Hashmap of SS populations by RS ( rsId:[ssid:Vector of DBSNPGenotypePopulation] )
    private HashMap rsPopulationsBySS;

    // Compound object holding all the DAOs representing an RS
    private DBSNPNse dbSNPNse;

    // the current rsId we are processing
    String rsId;
    // current number of radar snps added
    private int addCtr = 0;

    // > 1 dbsnp strainIds can map to the same mgd strain
    // so the consensusAllele calculation will be incorrect unless we resolve the
    // strain to a strain key here in the data provider side of the load
    private StrainKeyLookup strainKeyLookup;
    private AccessionLookup jaxRegistryLookup;
    // these lookups to create a translatable strain name for dbsnp strains
    // with integer names
    private HandleNameByPopIdLookup handleNameLookup;
    private PopNameByPopIdLookup popNameLookup;

    // analysis of RS vs SS var class; each is current count of the SS class
    // when the RS class is mixed
    private int mixedRS_SSConflictCtr = 0;
    private int mixedRS_noSSConflictCtr = 0;
    private int indelCtr = 0;
    private int mnpCtr = 0;
    private int mixedCtr = 0;
    private int snpCtr = 0;
    private int namedCtr = 0;
    // analysis of RefSeq proteins and mrnas
    private int nm = 0;
    private int nr = 0;
    private int np = 0;
    private int xm = 0;
    private int xr = 0;
    private int xp = 0;
    private int otherProt = 0;
    private int otherMrna = 0;
    /**
     * Constructs a
     * @assumes Nothing
     * @effects Nothing
     * @param radarSqlStream stream for adding QC information to a RADAR database
     * @throws ConfigException  if there are configuration errors.
     * @throws DBException if error creating DBSNPGenotype objects
     * @throws DLALoggingException if error creating a logger
     */

    public DBSNPInputProcessor(SQLStream radarSqlStream, SQLStream loadSqlStream, BufferedWriter coordWriter) throws CacheException,
        DBException, ConfigException, DLALoggingException, TranslationException, MGIException {
        /**
         * Debug stuff
         */
        stopWatch = new Stopwatch();

        radarStream = radarSqlStream;
        loadStream = loadSqlStream;
        logger = DLALogger.getInstance();
        // configurator to lookup logicalDB
        config = new DBSNPLoaderCfg();
        jobStreamKey = new Integer(config.getJobstreamKey());
        rsPopulationsBySS = new HashMap();
        strainKeyLookup = new StrainKeyLookup();
        jaxRegistryLookup = new AccessionLookup(LogicalDBConstants.JAXREGISTRY,
                MGITypeConstants.STRAIN, AccessionLib.PREFERRED);
        popNameLookup = new PopNameByPopIdLookup();
        handleNameLookup = new HandleNameByPopIdLookup();
        snpProcessor = new SNPProcessor(loadStream, coordWriter);
    }

    /**
     * Adds a SNP to the database
     * @assumes Nothing
     * @effects inserts into a database
     * @param input a DBSNPInput object - a set of SNP attributes to
     *       add to the database
     * @throws ConfigException  if there are configuration errors.
     * @throws DBException if error creating DBSNPGenotype objects
     */

    public void processInput(DBSNPInput input) throws MGIException {
        //throws DBException,
        //ConfigException, SNPNoStrainAlleleException, SNPNoBL6Exception, MGIException,
        //SNPNoConsensusAlleleSummaryException {
        // RefSNP id of 'input'
        rsId = input.getRsId();

        // Create a map of rs ids to their  strain alleles by SS, if the
        // input object is for the genotype file
        if (input.getClass().getName().equals("org.jax.mgi.app.dbsnploader.DBSNPGenotypeInput")){
            //rsId = ( (DBSNPGenotypeInput) input).getRsId();
            //logger.logdDebug(rsId);
            // ssAlleleMap looks like  ssId:HashMap(strain:Allele)
            //HashMap ssAlleleMap = ( (DBSNPGenotypeInput) input).getAlleleMapForRs();
            //rsStrainAllelesBySS.put(rsId, ssAlleleMap);
            HashMap ssPopulations = ((DBSNPGenotypeInput) input).getSSPopulationsForRs();
            rsPopulationsBySS.put(rsId, ssPopulations);
            //reportAllReverse(rsId, ssAlleleMap);
        }
        // Process this way if the input object is for the NSE file
         else if (input.getClass().getName().equals("org.jax.mgi.app.dbsnploader.DBSNPNseInput")){

                 // get the set of strain alleles, for this rs
                 // looks like ssid:Vector of DBSNPGenotypePopulation objects
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
                 String rsVarClass = ( (DBSNPNseInput) input).getRSVarClass();
                 Vector subSNPs = ( (DBSNPNseInput) input).getSubSNPs();
                 Vector flank3Prime = ( (DBSNPNseInput) input).get3PrimeFlank();
                 Vector flank5Prime = ( (DBSNPNseInput) input).get5PrimeFlank();
                 Vector contigHits = ( (DBSNPNseInput) input).getContigHits();
                 //ANALYSIS
                 // analyze varClass where RS is mixed and SS don't have a varClass
                 // consensus
                 /*
                 if (rsVarClass.equals("mixed") && subSNPs.size() > 1) {
                     analyzeVarClass(rsId, rsVarClass, subSNPs);
                 }*/
                 // END ANALYSIS
                 // create the MGI consensus object
                 Integer consensusKey = processConsensusSnp(rsVarClass);
                 // create coordinate and marker objects; do this first because
                 // some RS are rejected )we are only loading C57BL/6J
                 // coordinates)
                 // send rsId for reporting purpoases
                 processCoordinates(consensusKey, contigHits, rsId);
                 // create MGI accession object for rsid
                 processAccession(rsId, SNPLoaderConstants.LDB_CSNP, consensusKey,
                                 SNPLoaderConstants.OBJECTYPE_CSNP, Boolean.FALSE);
                 /**
                  * create:
                  * 1) subSNP objects
                  * 2) accession objects for their ssId and submitter snp id
                  * 3) population objects
                  * 4) strain allele objects for each population's strain alleles
                  */

                 // current number of ss that have a population
                 int ssWithPopulationCt = 0;
                 for  ( Iterator i = subSNPs.iterator();i.hasNext(); ) {
                     DBSNPNseSS ss = (DBSNPNseSS) i.next();
                     Vector popsForSSVector = (Vector) currentSSPopulationMap.get(ss.getSSId());
                     // We don't want to load RS in the genotype file for which none of the
                     // SS have a population. RS13476574 is an example
                     if(popsForSSVector.size() > 0) {
                         ssWithPopulationCt++;
                     }

                     Integer ssKey = processSS(consensusKey, ss);
                     for (Iterator j = popsForSSVector.iterator(); j.hasNext(); ) {
                         processSSStrainAlleles(ssKey, ss.getSSId(),
                             (DBSNPGenotypePopulation)j.next());
                     }
                     // ANALYSIS exemplars with no strain alleles
                     /*
                     if (ss.isExemplar.equals(Boolean.TRUE)) {
                         String s = ss.getSSId();
                         if( ((HashMap)currentSSAlleleMap.get(s)).size() == 0) {
                             logger.logcInfo("RS" + rsId + " SS " + s + " is exemplar and has no alleles", false);
                         }
                     }*/
                     // end ANAYSIS exemplars with no strain alleles


                 }
                 // if none of the ss have a population, throw an exception
                 if (ssWithPopulationCt < 1) {
                     // throw an exception to be caught at the loader level
                     // loader can decide (via configuration?) behaviour when there
                     // are no strain alleles for an rs - log and go on to the next
                     // snp or fatal error.
                     logger.logdDebug("No alleles for RS" + rsId);
                     SNPNoStrainAlleleException e = new
                         SNPNoStrainAlleleException();
                     e.bind(rsId);
                     throw e;

                 }
                 // create flank objects for the 5' flanking sequence
                 processFlank(consensusKey, flank5Prime, Boolean.TRUE);
                 // create flank objects for the 3' flanking sequence
                 processFlank(consensusKey, flank3Prime, Boolean.FALSE);
                 // create the consensus alleles for this RS
                 // send rsId just for debug
                 processConsensusAlleles(consensusKey, currentSSPopulationMap, rsId);
                 dbSNPNse.sendToStream();
                 addCtr++;
                 // now do the (temporary) mgd part
                 snpProcessor.process(dbSNPNse, rsId);
            }
             else {
                 // throw exception and log here
                 System.out.println("Unrecognized Input class");
             }
    }

    /**
     * Gets a Vector containing a String reporting count of Sequences added
     * @assumes nothing
     * @effects nothing
     * @return Vector containing single string with count of Sequences added
     */
     public Vector getProcessedReport() {
         Vector report = new Vector();
         report.add("Total RADAR SNPs created: " + addCtr);
         for(Iterator i = snpProcessor.getProcessedReport().iterator(); i.hasNext(); ) {
             report.add((String)i.next());
         }
         return report;
     }

    // creates MGI_SNP_ConsensusSNP object
    private Integer processConsensusSnp(String rsVarClass) throws DBException, ConfigException  {
        MGI_SNP_ConsensusSNPState state = new MGI_SNP_ConsensusSNPState();
        state.setVariationClass(rsVarClass);
        state.setJobStreamKey(jobStreamKey);
        dbSNPNse = new DBSNPNse(state, radarStream);
        return dbSNPNse.getConsensusKey();
    }

    // create MGI_SNP_StrainAlleles for the RS consensus strain alleles
    // ssAlleleMap looks like ssId:Vector of Population objects
    private void processConsensusAlleles(Integer consensusKey, HashMap ssPopulationMap,
                            String rsId) throws DBException, ConfigException,
        TranslationException, CacheException, SNPNoConsensusAlleleSummaryException {
        //System.out.println(rsId);
        //  map strain to alleles and count of each allele
        // consensusAlleleMap looks like strain:HashMap[allele:count]
        HashMap consensusAlleleMap = new HashMap();
        // true if 1 or more 'N' alleles are present in this RS
        boolean hasNAllele = false;
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
            if (population.size() > 0 ) {
                ssWithPopulationCt++;
            }
            /**
             * Iterate thru the populations of the current SS
             */

            for (Iterator j = population.iterator(); j.hasNext(); ) {
                DBSNPGenotypePopulation pop = (DBSNPGenotypePopulation) j.next();
                HashMap strAlleleMap = pop.getStrainAlleles();
                /**
                 * Iterate thru strains
                 */
                for (Iterator k = strAlleleMap.keySet().iterator(); k.hasNext(); ) {
                    String strain = (String) k.next();
                    Integer mgdStrainKey = resolveStrain(strain, pop.getPopId());
                    // if we can't resolve the strain, continue
                    if (mgdStrainKey == null) {
                        //logger.logcInfo("BAD STRAIN " + strain + " RS" + rsId + " SS" + currentSSId, false);
                        //SNPUnresolvedStrainException e = new SNPUnresolvedStrainException();
                        //e.bind(strain);
                        continue;
                        //throw e;

                    }
                    Allele a = (Allele)strAlleleMap.get(strain);
                    String allele = a.getAllele();
                    String orient = a.getOrientation();
                    /**
                     * if in reverse orientation we need to complement 'allele' before storing
                     */
                    if (orient.equals(
                        SNPLoaderConstants.GENO_REVERSE_ORIENT)) {
                        allele = complementAllele(allele, currentSSId);
                    }
                    /**
                     * Add the allele to alleleSummary set (proper set, no repeats)
                     */
                    if (!allele.equals("N")) {
                        alleleSummarySet.add(allele);
                    }
                    else {
                        hasNAllele = true;
                    }
                    addToConsensusAlleleMap(mgdStrainKey, allele, consensusAlleleMap);
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
            summaryString.append((String)i.next() + "/");
        }
        int len = summaryString.length();
        /**
         * if we have 0 length summary allele it is because
         * 1) no strains resolve, therefore no alleles.
         * 2) the only allele is 'N'
         */
        if(len < 1) {
            logger.logdDebug("No ConsensusSnp Summary Allele for RS" + rsId);
            SNPNoConsensusAlleleSummaryException e = new
                SNPNoConsensusAlleleSummaryException();
            e.bind(rsId);
            throw e;
        }
        // remove the trailing '/'
        summaryString.deleteCharAt(len-1);
        dbSNPNse.addRSAlleleSummary(summaryString.toString());
        /**
         * now find the consensus allele
         */
        createConsensusAlleles(consensusKey, consensusAlleleMap);
    }

    private void createConsensusAlleles(Integer csKey, HashMap csAlleleMap)
          throws DBException, ConfigException {

        //consensusAlleleMap looks like strainKey:HashMap[allele:count]
        // iterate thru the strainKeys
        for (Iterator i = csAlleleMap.keySet().iterator(); i.hasNext();) {
            // the consensus allele determined thus far
            String currentConsensusAllele = "";
            // the count of instances of currentConsensusAllele
            int currentCt = 0;
            // true  if current comparison of allele counts are equal (we don't have
            // a consensus)
            boolean isEqual = false;
            // get the strain and the alleles
            Integer strainKey = (Integer)i.next();
            HashMap alleles = (HashMap)csAlleleMap.get(strainKey);
            // ANALYSIS
            if (alleles.size() > 2) {
                logger.logdDebug("RS" + rsId + " has > 2 alleles for strainKey " + strainKey);
                for (Iterator k = alleles.keySet().iterator(); k.hasNext();) {
                    String allele = (String)k.next();
                    logger.logdDebug("Allele: " + allele + " count " + alleles.get(allele)   );
                }
            }
            // END ANALYSIS
            // iterate thru the alleles
            for (Iterator j = alleles.keySet().iterator(); j.hasNext();) {
                // get an allele for this strain
                String allele = (String)j.next();
                // exclude "N" from determining consensus
                if(allele.equals("N")) {
                   continue;
                }
                // get number of instances of this allele
                int count = ((Integer)alleles.get(allele)).intValue();
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
               // logger.logdDebug("\t\t" + " allele: " +
               //                allele + " count " + count);
            }
            // if the equal flag is true  OR the consensusAllele is "" (which
            // means the only allele was an 'N',
            // we don't have consensus
            if(isEqual == true || currentConsensusAllele.equals("")) {
                currentConsensusAllele = "?";
            }
            // now create the consensus allele
            MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
            state.setAllele(currentConsensusAllele);
            state.setMgdStrainKey(strainKey);
            // no conflict if only 1 distinct allele for this strain that is NOT "?",
            // (which means the single allele was an "N")
            // otherwise we have a '?' or a simple majority which we flag
            if(alleles.size() == 1 && !currentConsensusAllele.equals("?")) {
                state.setIsConflict(Boolean.FALSE);
            }
            else {
                state.setIsConflict(Boolean.TRUE);
            }
            state.setObjectKey(csKey);
            state.setObjectType(SNPLoaderConstants.OBJECTYPE_CSNP);
            state.setJobStreamKey(jobStreamKey);
            dbSNPNse.addStrainAllele(state);
        }
    }
    private Integer resolveStrain(String strain, String popId) throws DBException, ConfigException,
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
        // if still not found may be an integer strain id. IN this case the
        // translation has been qualified as follows handle_population_strain
        if (strainKey == null) {
            try {
                String handleName = handleNameLookup.lookup(popId);
                String popName = popNameLookup.lookup(popId);
                String qualifiedStrainName = handleName + "_" + popName + "_" + strain;
                strainKey = strainKeyLookup.lookup(qualifiedStrainName);
            }
            catch (KeyNotFoundException e) {
                strainKey = null;
            }
        }
        return strainKey;
    }
    private String complementAllele(String allele, String ssId) {
        StringBuffer convertedAllele = new StringBuffer();
        char[] alArray = allele.toCharArray();
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
                    System.out.println("Bad input for ss " +
                                       ssId);
            }
        }
        return convertedAllele.toString();

    }
    private void addToConsensusAlleleMap(Integer mgdStrainKey, String allele, HashMap csAlleleMap) {
        /**
         * map 'allele' to its mgdStrainKey
         * csAlleleMap looks like mgdStrainKey:HashMap[allele:count]
         * where count is the number of instance of this allele for this
         * mgdStrainKey. Remember we are using mgd strain key because >1 dbsnp strains
         * map to an mgd strain
         */
        // if mgdStrainKey in the map, add allele and/or count
        if (csAlleleMap.containsKey(mgdStrainKey)) {
            // existing map looks like allele:count
            HashMap existingMap = (HashMap) csAlleleMap.get(
                mgdStrainKey);
            if (existingMap.containsKey(allele)) {
                int ct = ( (Integer) existingMap.get(allele)).
                    intValue();
                ct++;
                existingMap.put(allele, new Integer(ct));
            }
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
    //private void processSS(Integer consensusKey, DBSNPNseSS ss, HashMap alleleMap)
      private Integer processSS(Integer consensusKey, DBSNPNseSS ss)
            throws DBException, ConfigException, CacheException, TranslationException{
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
        processAccession(ss.getSubmitterSNPId(), SNPLoaderConstants.LDB_SUBMITTER,
                        ssKey, SNPLoaderConstants.OBJECTYPE_SSNP, Boolean.FALSE);
        //if (alleleMap != null) {
        //    processSSStrainAlleles(ssKey, ssId, alleleMap);
        //}
        return ssKey;
    }
    private void processAccession(String accid, String logicalDB,
            Integer objectKey, String objectType,  Boolean isPrivate)
                throws DBException, ConfigException {
        MGI_SNP_AccessionState state = new MGI_SNP_AccessionState();
        //System.out.println(accid);
        state.setAccID(accid);
        state.setLogicalDB(logicalDB);
        state.setObjectKey(objectKey);
        state.setObjectType(objectType);
        state.setJobStreamKey(jobStreamKey);
        state.setPrivateVal(isPrivate);
        dbSNPNse.addAccession(state);
    }
    private void processSSStrainAlleles(Integer subSNPKey, String ssId,
           DBSNPGenotypePopulation pop)
        throws DBException, ConfigException, CacheException, TranslationException {
        // create an strain allele object for each strain assay for this population
        HashMap alleleMap = pop.getStrainAlleles();
        String popId = pop.getPopId();
        for (Iterator i = alleleMap.keySet().iterator(); i.hasNext(); ) {
            String strain = (String)i.next();
            Integer strainKey = resolveStrain(strain, popId);
            // if we still haven't found it write it to the curation log continue
             if(strainKey == null) {
                 logger.logcInfo("BAD STRAIN " + strain + " RS" + rsId + " SS" + ssId + "PopId" + popId, false);
                 continue;
             }
            MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
            state.setObjectKey(subSNPKey);
            state.setObjectType(SNPLoaderConstants.OBJECTYPE_SSNP);
            state.setJobStreamKey(jobStreamKey);
            state.setMgdStrainKey(strainKey);
            state.setPopId(popId);
            // get the allele string from the Allele object, set in state
            Allele allele = (Allele)alleleMap.get(strain);
            state.setAllele(allele.getAllele());
            state.setIsConflict(Boolean.FALSE);
            dbSNPNse.addStrainAllele(state);
        }
    }

    private void processFlank(Integer consensusKey, Vector flank, Boolean is5Prime)
        throws DBException, ConfigException {
        // need to get 255 char chunks of sequence in the flank; the input is
        // chunked, but in variable chunks :-(

        // get the entire flanking sequence
        StringBuffer entireFlank = new StringBuffer();
        for (Iterator i = flank.iterator(); i.hasNext(); ) {
            entireFlank.append(((DBSNPNseFlank)i.next()).getFlank());
        }
        //
        int ctr = 0;
        String entireFlankStr = entireFlank.toString();
        String currentFlankChunk;
        while(entireFlankStr.length() > 255) {
            ctr++;
            // get the first 255 chars
            currentFlankChunk = entireFlank.substring(0,255);
            // remove first 255 chars from entireFlankStr
            entireFlankStr = entireFlankStr.substring(255);
            processFlankState(currentFlankChunk, new Integer(ctr), consensusKey, is5Prime);
        }
        // process the final chunk
        if (entireFlankStr.length() != 0 ) {
            ctr++;
            processFlankState(entireFlankStr, new Integer(ctr), consensusKey, is5Prime);
        }
    }
    private void processFlankState(String flankChunk, Integer sequenceNum,
                                  Integer consensusKey, Boolean is5Prime)
            throws DBException, ConfigException {
        // create and MGI_FlankState for this chunk of 255
        MGI_SNP_FlankState state = new MGI_SNP_FlankState();
        state.setConsensusSNPKey(consensusKey);
        state.setFlank(flankChunk);
        state.setIs5prime(is5Prime);
        state.setSequenceNum(sequenceNum);
        state.setJobStreamKey(jobStreamKey);
        dbSNPNse.addFlank(state);
    }

    private void processCoordinates(Integer consensusKey, Vector contigHits, String rsId)
        throws DBException, ConfigException, SNPNoBL6Exception {
        boolean bl6Flag = false;
        // iterate over the contig hits
        for (Iterator i = contigHits.iterator(); i.hasNext(); ) {
            DBSNPNseContigHit cHit = (DBSNPNseContigHit) i.next();
            String assembly = cHit.getAssembly();
            // skip it if not BL6
            if (! assembly.equals(SNPLoaderConstants.DBSNP_BL6)) {
                continue;
            }
            // we've got at least one BL6, flag it.
            bl6Flag = true;
            String chromosome = cHit.getChromosome();
            Vector mapLoc = cHit.getMapLocations();

            // iterate over the Map Locations
            for (Iterator j = mapLoc.iterator(); j.hasNext(); ) {
                // set the coordinate attributes
                MGI_SNP_CoordinateState cState = new MGI_SNP_CoordinateState();
                cState.setAssembly(assembly);
                cState.setChromosome(chromosome);
                cState.setConsensusSNPKey(consensusKey);
                cState.setJobStreamKey(jobStreamKey);
                // get the map location object
                DBSNPNseMapLoc mloc = (DBSNPNseMapLoc) j.next();
                // set the location attributes
                cState.setOrientation(mloc.getRSOrientToChr());
                // need to use startCoord in the marker too
                Double startCoord = mloc.getStartCoord();
                cState.setStartCoord(startCoord);
                dbSNPNse.addCoordinate(cState);

                // now get the fxnSet and create the Marker objects
                Vector fxnSets = mloc.getFxnSets();
                // the set of locusId fxnClass pairs; we don't want to load
                // dups. also analyzes refseq accessions
                //analyzeFxnSets(fxnSets, rsId);
                // Contains String locusId + fxnClass to avoid fxn class/locus id dups
                HashSet fxnSetSet = new HashSet();
                // iterate over the FxnSets

                for (Iterator k = fxnSets.iterator(); k.hasNext(); ) {
                    DBSNPNseFxnSet fSet = (DBSNPNseFxnSet)k.next();
                    String fxnClass = fSet.getFxnClass();
                    // we dont want 'reference' fxn class
                    // 7/25 - we do want reference fxn class
                    /*if(fxnClass.equals(SNPLoaderConstants.REFERENCE)) {
                        continue;
                    }*/

                    String locusId = fSet.getLocusId();
                    String nucleotideId = fSet.getNucleotideId();
                    String proteinId = fSet.getProteinId();
                    // we don't want duplicate chromosome/coord/locusId/fxnClass/nucleotide/protein
                    // records
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
        // throw an exception if no BL6
        if (bl6Flag != true) {
            // we have no coordinates for BL6, reject this RS
            SNPNoBL6Exception e = new
                        SNPNoBL6Exception();
                    e.bind("_Consensus_key=" + consensusKey);
                    throw e;

        }
    }
    /**
     * ANALYSIS methods
     */

    private void reportAllReverse(String rsId, HashMap ssAlleleMap) {
        // the set of orientations for ss that have alleles
        HashSet orientSet = new HashSet();
        //ssAlleleMap looks like ssId:HashMap(strain:Allele)
        for(Iterator i = ssAlleleMap.keySet().iterator(); i.hasNext(); ) {
            String ssId = (String)i.next();
            // map looks like strain:Allele
            HashMap map = (HashMap)ssAlleleMap.get(ssId);
            for (Iterator j = map.keySet().iterator(); j.hasNext();) {
                String strain = (String)j.next();
                Allele a = (Allele)map.get(strain);
                orientSet.add(a.getOrientation());
            }
        }
       // if (orientSet.size() == 1 && orientSet.contains(SNPLoaderConstants.REVERSE_ORIENT)) {
         //   logger.logcInfo("RS" + rsId, false);
       // }
    }

    private void analyzeFxnSets(Vector fxnSets, String rsId) {
        HashSet fxnSetSet = new HashSet();
        boolean nonsynonFlag = false;
        boolean synonFlag = false;
        int referenceCtr = 0;
        for (Iterator k = fxnSets.iterator(); k.hasNext(); ) {
            DBSNPNseFxnSet fSet = (DBSNPNseFxnSet) k.next();
            String fxnClass = fSet.getFxnClass();
            String mrna = fSet.getNucleotideId();
            String prot = fSet.getProteinId();

            // we dont want 'reference' fxn classes
            // 7/25 - maybe we do want reference
            /*if(fxnClass.equals(SNPLoaderConstants.REFERENCE)) {
                continue;
                                 }*/
            // we don't want dup locusId/fxnclass pairs
            String locusId = fSet.getLocusId();
            String join = locusId + fxnClass;
            if (fxnSetSet.contains(join)) {
                continue;
            }

            if(mrna != null) {
                //System.out.println(mrna.substring(0,2));
                if ((mrna.substring(0, 2)).equals("NM")) {
                    nm++;
                    //logger.logcInfo(rsId + "\t" + mrna, false);
                }
                else if ((mrna.substring(0, 2)).equals("NR")) {
                    nr++;
                }
                else if((mrna.substring(0, 2)).equals("XM")) {
                    xm++;
                }
                else if((mrna.substring(0, 2)).equals("XR")) {
                    xr++;
                }
                else {
                    logger.logcInfo("Other MRNA RefSeq: " + mrna, false);
                    otherMrna++;
                }
            }
            if(prot != null) {
                //System.out.println(prot.substring(0,2));
                if ((prot.substring(0, 2)).equals("NP")) {
                    np++;
                    //logger.logcInfo(rsId + "\t" + prot, false);
                }
                else if ((mrna.substring(0, 2)).equals("XP")) {
                    xp++;
                }
                else {
                    //logger.logcInfo("Other Prot RefSeq: " + prot, false);
                    otherProt++;
                }
            }
            fxnSetSet.add(locusId + fxnClass);
            if (fxnClass.equals("coding-nonsynon")) {
                nonsynonFlag = true;
            }
            else if (fxnClass.equals("coding-synon")) {
                synonFlag = true;
            }
            else if (fxnClass.equals("reference")) {
                referenceCtr++;
            }
        }/*
        if (nonsynonFlag == true && synonFlag == true) {
            logger.logcInfo("RS" + rsId + " has " + referenceCtr + " reference class instances", false);
        }*/
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
