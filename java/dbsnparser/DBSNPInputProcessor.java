// $Header
// $Name

package dbsnparser;
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

    // Hashmap of SS strain alleles by RS ( rsId:VectorOfGenotypeSS)
    private HashMap rsStrainAllelesBySS;

    // Compound object holding all the DAOs representing an RS
    private DBSNPNse dbSNPNse;

    // the current rsId we are processing
    String rsId;
    // current number of sequences added
    private int addCtr = 0;

    // > 1 dbsnp strainIds can map to the same mgd strain
    // so the consensusAllele calculation will be incorrect unless we resolve the
    // strain to a strain key here in the data provider side of the load
    private StrainKeyLookup strainKeyLookup;
    private AccessionLookup jaxRegistryLookup;
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
        rsStrainAllelesBySS = new HashMap();
        strainKeyLookup = new StrainKeyLookup();
        jaxRegistryLookup = new AccessionLookup(LogicalDBConstants.JAXREGISTRY,
        MGITypeConstants.STRAIN, AccessionLib.PREFERRED);

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

    public void processInput(DBSNPInput input) throws DBException,
        ConfigException, SNPNoStrainAlleleException, SNPNoBL6Exception, MGIException,
           SNPNoConsensusAlleleSummaryException {
        // RefSNP id of 'input'
        rsId = input.getRsId();

        // Create a map of rs ids to their  strain alleles by SS, if the
        // input object is for the genotype file
        if (input.getClass().getName().equals("dbsnparser.DBSNPGenotypeInput")){
            //rsId = ( (DBSNPGenotypeInput) input).getRsId();
            //logger.logdDebug(rsId);
            // ssAlleleMap looks like  ssId:HashMap(strain:Allele)
            HashMap ssAlleleMap = ( (DBSNPGenotypeInput) input).getAlleleMapForRs();
            rsStrainAllelesBySS.put(rsId, ssAlleleMap);
            //reportAllReverse(rsId, ssAlleleMap);
        }
        // Process this way if the input object is for the NSE file
         else if (input.getClass().getName().equals("dbsnparser.DBSNPNseInput")){

                 // get the set of strain alleles, for this rs
                 // looks like ssid:HashMap(strain:allele)
                 HashMap currentSSAlleleMap = (HashMap) rsStrainAllelesBySS.get(
                     rsId);
                // if currentSSAlleleMap is NULL (when no record in the genotype
                // file for this rs)
                // throw an exception so the loader
                // can decide what to do (load or not load that is the question)
                 if (currentSSAlleleMap == null) {
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
                 Integer consensusKey = createConsensus(rsVarClass);
                 // create coordinate and marker objects; do this first because
                 // some RS are rejected )we are only loading C57BL/6J
                 // coordinates)
                 // send rsId for reporting purpoases
                 createCoordinates(consensusKey, contigHits, rsId);
                 // create MGI accession object for rsid
                 createAccession(rsId, SNPLoaderConstants.LDB_CSNP, consensusKey,
                                 SNPLoaderConstants.OBJECTYPE_CSNP, Boolean.FALSE);
                 // create 1) subSNP objects, 2) accession objects for their ssId and submitter snp id
                 // and 3) strain allele objects for each of their strain alleles

                 // current number of ss that have strain alleles
                 int ssWithAllelesCt = 0;
                 for  ( Iterator i = subSNPs.iterator();i.hasNext(); ) {
                     DBSNPNseSS ss = (DBSNPNseSS) i.next();
                     HashMap alleleMap = (HashMap) currentSSAlleleMap.get(ss.getSSId());
                     // We don't want to load RS in the genotype file for which none of the
                     // SS have strain alleles. RS13476574 is an example
                     if(alleleMap.size() > 1) {
                         ssWithAllelesCt++;
                     }
                     createSS(consensusKey, ss, alleleMap);
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
                 // if none of the ss in the genotype file have any strain
                 // alleles, throw an exception
                 if (ssWithAllelesCt < 1) {
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
                 createFlank(consensusKey, flank5Prime, Boolean.TRUE);
                 // create flank objects for the 3' flanking sequence
                 createFlank(consensusKey, flank3Prime, Boolean.FALSE);
                 // create the consensus alleles for this RS
                 // send rsId just for debug
                 createConsensusAlleles(consensusKey, currentSSAlleleMap, rsId);
                 dbSNPNse.sendToStream();
                 addCtr++;
                 // now do the (temporary) mgd part

                 try {
                     snpProcessor.process(dbSNPNse, rsId);
                 } catch (SNPUnresolvedStrainException e) {
                     logger.logdInfo(e.getMessage(), false);
                 }
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
         report.add("Total SNPs created: " + addCtr);
        //logger.logcInfo("Total Mixed Class RefSNPS with SS varClass that disagree: " + mixedRS_SSConflictCtr, false );
        //logger.logcInfo("Total Mixed Class RefSNPS wtih SS varClass that agree: " + mixedRS_noSSConflictCtr, false );
        //logger.logcInfo("Total NM: " + nm, false);
        //logger.logcInfo("Total NR: " + nr, false);
        //logger.logcInfo("Total NP: " + np, false);
        //logger.logcInfo("Total XM: " + xm, false);
        //logger.logcInfo("Total XR: " + xr, false);
        //logger.logcInfo("Total XP: " + xp, false);
        //logger.logcInfo("Total other MRNA: " + otherMrna, false);
        //logger.logcInfo("Total other Protein: " + otherProt, false);

         return report;
     }

    // creates MGI_SNP_ConsensusSNP object
    private Integer createConsensus(String rsVarClass) throws DBException, ConfigException  {
        MGI_SNP_ConsensusSNPState state = new MGI_SNP_ConsensusSNPState();
        state.setVariationClass(rsVarClass);
        state.setJobStreamKey(jobStreamKey);
        dbSNPNse = new DBSNPNse(state, radarStream);
        return dbSNPNse.getConsensusKey();
    }

    // create MGI_SNP_StrainAlleles for the RS consensus strain alleles
    // ssAlleleMap looks like ssId:HashMap(strain:Allele)
    private void createConsensusAlleles(Integer consensusKey, HashMap ssAlleleMap,
                            String rsId) throws DBException, ConfigException,
        TranslationException, CacheException, SNPNoConsensusAlleleSummaryException {
        //System.out.println(rsId);
        // looks like strain:HashMap[allele:count]
        HashMap consensusAlleleMap = new HashMap();
        // true if 1 or more 'N' alleles are present in this RS
        boolean hasNAllele = false;
        // summary of the consensus alleles
        HashSet alleleSummarySet = new HashSet();
        int ssWithAllelesCt = 0; //DEBUG
        /**
         * Iterate thru each SS
         */
        for (Iterator i = ssAlleleMap.keySet().iterator(); i.hasNext(); ) {
            // get the ssid
            String currentSSId = (String) i.next();
            // get the set of strain alleles for this ssId
            HashMap alleleMap = (HashMap) ssAlleleMap.get(currentSSId);
            if (alleleMap.size() > 0 ) {
                ssWithAllelesCt++;
            }
            /**
             * Iterate thru the strain alleles of the current SS
             */

            for (Iterator j = alleleMap.keySet().iterator(); j.hasNext(); ) {
                String strain = (String) j.next();
                Integer strainKey = resolveStrain(strain);
                // if we can't resolve the strain, log it and continue
                if(strainKey == null) {
                    logger.logcInfo("BAD CS STRAIN " + strain + " RS" + rsId + " SS" + currentSSId, false);
                    //SNPUnresolvedStrainException e = new SNPUnresolvedStrainException();
                    //e.bind(strain);
                    continue;
                    //throw e;
                }
                ////////
                String allele = ( (Allele) alleleMap.get(strain)).getAllele();

                /**
                 * if in reverse orientation we need to flip 'allele' before storing
                 */
                if ( ( (Allele) alleleMap.get(strain)).getOrientation().equals(
                    SNPLoaderConstants.GENO_REVERSE_ORIENT)) {
                    StringBuffer convertedAllele = new StringBuffer();
                    char[] alArray = allele.toCharArray();
                    for (int k = 0; k < alArray.length; k++) {
                        switch(alArray[k]) {
                            case 'A': convertedAllele.append("T"); break;
                            case 'T': convertedAllele.append("A"); break;
                            case 'C': convertedAllele.append("G"); break;
                            case 'G': convertedAllele.append("C"); break;
                            case 'N': convertedAllele.append("N"); break;
                            case '-': convertedAllele.append("-"); break;
                            default: System.out.println("Bad input for ss " + currentSSId);
                        }
                    }
                    // set allele to its the flipped version
                    allele = convertedAllele.toString();
                }
                /**
                 * Add the allele to alleleSummary set (proper set, no repeats)
                 */
                if (!allele.equals("N")) {
                    alleleSummarySet.add(allele);
                }
                else { hasNAllele = true; }

                /**
                 * now map this allele to its strain; we'll process consensusAlleleMap
                 * later.
                 * consensusAlleleMap looks like strainKey:HashMap[allele:count]
                 * where ct is the number of instance of this allele for this
                 * strainKey. Remember we are using strain key because >1 dbsnp strains
                 * map to an mgd strain
                 */
                if (consensusAlleleMap.containsKey(strainKey)) {
                    // existing map looks like allele:count
                    HashMap existingMap = (HashMap)consensusAlleleMap.get(strainKey);
                    if (existingMap.containsKey(allele)) {
                        int ct = ((Integer)existingMap.get(allele)).intValue();
                        ct++;
                        existingMap.put(allele, new Integer(ct));
                    }
                    else {
                        existingMap.put(allele, new Integer(1));
                    }
                    consensusAlleleMap.put(strainKey, existingMap);
                }
                // if the strainKey is not in the map, add a new entry
                else {
                    HashMap newMap = new HashMap();
                    newMap.put(allele, new Integer(1));
                    consensusAlleleMap.put(strainKey, newMap);
                }
            }
            /**
             * Done iterating thru strain alleles for current SS
             */

        }
        /**
         * Done iterating thru all SS for this RS
         */

        /**
         * Process the alleleSummary
         */

        // add the delimiters
        StringBuffer summaryString = new StringBuffer();
        for (Iterator i = alleleSummarySet.iterator(); i.hasNext(); ) {
            summaryString.append((String)i.next() + "/");
        }
        // remove the trailing delimiter
        int len = summaryString.length();
        //System.out.println(rsId);
        //System.out.println("\t" + summaryString.toString());
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
        summaryString.deleteCharAt(len-1);
        dbSNPNse.addRSAlleleSummary(summaryString.toString());
        /**
         * now find the consensus allele
         * consensusAlleleMap looks like strainKey:HashMap[allele:count]
         */
         for (Iterator j = consensusAlleleMap.keySet().iterator(); j.hasNext();) {
             // the consensus allele determined thus far
             String conAllele = "";
             // the count of instances of conAllele
             int currentCt = 0;
             // true current comparison of allele counts are equal (we don't have
             // a consensus)
             boolean isEqual = false;
             // get the strain and the alleles
             Integer strainKey = (Integer)j.next();
             HashMap alleles = (HashMap)consensusAlleleMap.get(strainKey);
             // ANALYSIS
             if (alleles.size() > 2) {
                 logger.logdDebug("RS" + rsId + " has > 2 alleles for strainKey " + strainKey);
                 for (Iterator k = alleles.keySet().iterator(); k.hasNext();) {
                     String allele = (String)k.next();
                     logger.logdDebug("Allele: " + allele + " count " + alleles.get(allele)   );
                 }
             }
             // END ANALYSIS
             // iterate thru the alleles of this strain
             for (Iterator k = alleles.keySet().iterator(); k.hasNext();) {
                 // get an allele for this strain
                 String allele = (String)k.next();
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
                     conAllele = allele;
                     isEqual = false;
                 }
                // logger.logdDebug("\t\t" + " allele: " +
                  //                allele + " count " + count);
             }
             // if the equal flag is true  OR the consensusAllele is "N",
            // we don't have consensus
             if(isEqual == true || conAllele.equals("N")) {
                 conAllele = "?";
             }
             // now create the consensus allele
             MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
             state.setAllele(conAllele);
             state.setMgdStrainKey(strainKey);
             // no conflict if only 1 distinct allele for this strain that is NOT "?",
             // (which means the single allele was an "N")
             // otherwise we have a '?' or a simple majority which we flag
             if(alleles.size() == 1 && !conAllele.equals("?")) {
                 state.setIsConflict(Boolean.FALSE);
             }
             else {
                 state.setIsConflict(Boolean.TRUE);
             }
             state.setObjectKey(consensusKey);
             state.setObjectType(SNPLoaderConstants.OBJECTYPE_CSNP);
             state.setJobStreamKey(jobStreamKey);
             dbSNPNse.addStrainAllele(state);
             // ANALYSIS
             // if the set of alleles for a strain is > 2 we may have a problem with
             // computing majority; build 124 this case does not exist
             //if(alleles.keySet().size() > 2) {
             //logger.logcInfo("RS" + rsId + " strain " + strain + " has " + alleles.keySet().size() + "alleles " + alleles.keySet().toString(), false);
             //logger.logcInfo("\tconsensusAllele: " + conAllele, false);
             //}
             //END ANALYSIS
         }
    }
    private Integer resolveStrain(String strain) throws DBException, ConfigException,
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
        return strainKey;
    }
    private void createSS(Integer consensusKey, DBSNPNseSS ss, HashMap alleleMap)
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
        createAccession(ssId, SNPLoaderConstants.LDB_SSNP, ssKey,
                        SNPLoaderConstants.OBJECTYPE_SSNP, Boolean.FALSE);
        // create an accession object for the current submitter snp id
        createAccession(ss.getSubmitterSNPId(), SNPLoaderConstants.LDB_SUBMITTER,
                        ssKey, SNPLoaderConstants.OBJECTYPE_SSNP, Boolean.FALSE);
        if (alleleMap != null) {
            createSSStrainAlleles(ssKey, ssId, alleleMap);
        }

    }
    private void createAccession(String accid, String logicalDB,
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
    private void createSSStrainAlleles(Integer subSNPKey, String ssId, HashMap alleleMap)
        throws DBException, ConfigException, CacheException, TranslationException {
        // create an strain allele object for each strain assay for this ss
        for (Iterator i = alleleMap.keySet().iterator(); i.hasNext(); ) {
            String strain = (String)i.next();
            Integer strainKey = resolveStrain(strain);
            // if we still haven't found it write it to the curation log and throw an
             // exception
             if(strainKey == null) {
                 logger.logcInfo("BAD CS STRAIN " + strain + " RS" + rsId + " SS" + ssId, false);
                 //SNPUnresolvedStrainException e = new SNPUnresolvedStrainException();
                 //e.bind(strain);
                 continue;
                 //throw e;
             }
            MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
            state.setObjectKey(subSNPKey);
            state.setObjectType(SNPLoaderConstants.OBJECTYPE_SSNP);
            state.setJobStreamKey(jobStreamKey);
            state.setMgdStrainKey(strainKey);
            // get the allele string from the Allele object, set in state
            Allele allele = (Allele)alleleMap.get(strain);
            state.setAllele(allele.getAllele());
            state.setIsConflict(Boolean.FALSE);
            dbSNPNse.addStrainAllele(state);
        }
    }

    private void createFlank(Integer consensusKey, Vector flank, Boolean is5Prime)
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
            createFlankState(currentFlankChunk, new Integer(ctr), consensusKey, is5Prime);
        }
        // process the final chunk
        if (entireFlankStr.length() != 0 ) {
            ctr++;
            createFlankState(entireFlankStr, new Integer(ctr), consensusKey, is5Prime);
        }
    }
    private void createFlankState(String flankChunk, Integer sequenceNum,
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

    private void createCoordinates(Integer consensusKey, Vector contigHits, String rsId)
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
    // ANALYSIS methods
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
    private void analyzeVarClass(String rsId, String rsVarClass, Vector subSNPs) {
        HashSet ssVarClassSet = new HashSet();
        HashMap strAlleles = (HashMap)rsStrainAllelesBySS.get(rsId);
        for(Iterator i = subSNPs.iterator(); i.hasNext(); ) {
            DBSNPNseSS ss = (DBSNPNseSS) i.next();
            // don't count ss without strain alleles
            if (((HashMap)strAlleles.get(ss.getSSId())).size() < 1) {
                continue;
            }
            String varClass = ss.getSSVarClass();
            if (varClass.equals("in-del")) {
                ssVarClassSet.add(varClass);
                //indelCtr++;
            }
            else if (varClass.equals("mnp")) {
                ssVarClassSet.add(varClass);
                //mnpCtr++;
            }
            else if (varClass.equals("mixed")) {
                ssVarClassSet.add(varClass);
                //mixedCtr++;
            }
            else if (varClass.equals("snp")) {
                ssVarClassSet.add(varClass);
                //snpCtr++;
            }
            else if (varClass.equals("named")){
                ssVarClassSet.add(varClass);
                //namedCtr++;
            }
            else {
                System.out.println("Undocumented class: " + varClass);
            }
        }
        if (ssVarClassSet.size() > 1) {
            logger.logcInfo("RS" + rsId + "\t" + ssVarClassSet.toString(), false);
            mixedRS_SSConflictCtr++;
        }
        else {
            logger.logcInfo("RS" + rsId + " has no conflict " + ssVarClassSet.toString(), false);
            mixedRS_noSSConflictCtr++;
        }

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
