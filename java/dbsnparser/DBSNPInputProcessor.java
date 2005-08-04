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

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    private Stopwatch stopWatch;

    // a stream for handling RADAR DAO objects
    private SQLStream radarStream;

    // logger for the load
    private DLALogger logger;

    // get a sequence load configurator
    private DBSNPLoaderCfg config;

    // jobstream key for the load
    private Integer jobStreamKey;

    // Hashmap of SS strain alleles by RS ( rsId:VectorOfGenotypeSS)
    private HashMap rsStrainAllelesBySS;

    // Compound object holding all the DAOs representing an RS
    private DBSNPNse dbSNPNse;

    // current number of sequences added
    private int addCtr = 0;

    // analysis of RS vs SS var class; each is current count of the SS class
    // when the RS class is mixed
    private int mixedRS_SSConflictCtr = 0;
    private int mixedRS_noSSConflictCtr = 0;
    private int indelCtr = 0;
    private int mnpCtr = 0;
    private int mixedCtr = 0;
    private int snpCtr = 0;
    private int namedCtr = 0;
    /**
     * Constructs a
     * @assumes Nothing
     * @effects Nothing
     * @param radarSqlStream stream for adding QC information to a RADAR database
     * @throws ConfigException  if there are configuration errors.
     * @throws DBException if error creating DBSNPGenotype objects
     * @throws DLALoggingException if error creating a logger
     */

    public DBSNPInputProcessor(SQLStream radarSqlStream) throws CacheException,
        DBException, ConfigException, DLALoggingException {
        /**
         * Debug stuff
         */
        stopWatch = new Stopwatch();

        radarStream = radarSqlStream;
        logger = DLALogger.getInstance();
        // configurator to lookup logicalDB
        config = new DBSNPLoaderCfg();
        jobStreamKey = new Integer(config.getJobstreamKey());
        rsStrainAllelesBySS = new HashMap();
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
        ConfigException, SNPNoStrainAlleleException, SNPNoBL6Exception {
        // RefSNP id of 'input'
        String rsId;

        // Create a map of rs ids to their  strain alleles by SS, if the
        // input object is for the genotype file
        if (input.getClass().getName().equals("dbsnparser.DBSNPGenotypeInput")){
            rsId = ( (DBSNPGenotypeInput) input).getRsId();
            //logger.logdDebug(rsId);
            // ssAlleleMap looks like  ssId:HashMap(strain:Allele)
            HashMap ssAlleleMap = ( (DBSNPGenotypeInput) input).getAlleleMapForRs();
            rsStrainAllelesBySS.put(rsId, ssAlleleMap);
            reportAllReverse(rsId, ssAlleleMap);
        }
        // Process this way if the input object is for the NSE file
         else if (input.getClass().getName().equals("dbsnparser.DBSNPNseInput")){

                 //logger.logdDebug("rsStrainAllelesBySS.size = " + rsStrainAllelesBySS.size());
                 rsId = ( (DBSNPNseInput) input).getRsId();
                 // get the set of strain alleles, for this rs, each SSId is mapped to
                 // its set of strain alleles e.g. ssid:HashMap(strain:allele)

               //  if (isValid(contigHits)) {
                 // CHECK TO MAKE SURE currentSSAlleleMap is not NULL
                 HashMap currentSSAlleleMap = (HashMap) rsStrainAllelesBySS.get(
                     rsId);

                 if (currentSSAlleleMap == null) {
                     // throw an exception to be caught at the loader level
                     // loader can decide (via configuration?) behaviour when there
                     // are no strain alleles for an rs - log and go on to the next
                     // snp or fatal error.
                     //System.out.println("No alleles for RS" + rsId);
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
                 // analyze varClass where RS is mixed and SS don't have a varClass
                 // consensus
                 /*
                 if (rsVarClass.equals("mixed") && subSNPs.size() > 1) {
                     analyzeVarClass(rsId, rsVarClass, subSNPs);
                 }*/

                 // create the MGI consensus object
                 Integer consensusKey = createConsensus(rsVarClass);
                 // create coordinate and marker objects; do this first because
                 // some RS are rejected )we are only loading C57BL/6J
                 // coordinates)
                 // send rsId for reporting
                 createCoordinates(consensusKey, contigHits, rsId);
                 // create MGI accession object for rsid
                 createAccession(rsId, SNPLoaderConstants.LDB_CSNP, consensusKey,
                                 SNPLoaderConstants.OBJECTYPE_CSNP);
                 // create subSNP objects, accession objects for their ssId and submitter snp id
                 // and strain allele objects for each of their strain alleles
                 for (Iterator i = subSNPs.iterator(); i.hasNext(); ) {
                     DBSNPNseSS ss = (DBSNPNseSS) i.next();
                     createSS(consensusKey, ss,
                              (HashMap) currentSSAlleleMap.get(ss.getSSId()));
                     // analyze exemplars with no strain alleles
                     /*
                     if (ss.isExemplar.equals(Boolean.TRUE)) {
                         String s = ss.getSSId();
                         if( ((HashMap)currentSSAlleleMap.get(s)).size() == 0) {
                             logger.logcInfo("RS" + rsId + " SS " + s + " is exemplar and has no alleles", false);
                         }
                     }*/
                     // end analyze exemplars with no strain alleles
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
        logger.logcInfo("Total Mixed Class RefSNPS with SS varClass that disagree: " + mixedRS_SSConflictCtr, false );
        logger.logcInfo("Total Mixed Class RefSNPS wtih SS varClass that agree: " + mixedRS_noSSConflictCtr, false );
         return report;
     }

    // creates MGI_SNP_ConsensusSNP object
    private Integer createConsensus(String rsVarClass) throws DBException, ConfigException {
        MGI_SNP_ConsensusSNPState state = new MGI_SNP_ConsensusSNPState();
        state.setVariationClass(rsVarClass);
        state.setJobStreamKey(jobStreamKey);
        dbSNPNse = new DBSNPNse(state, radarStream);
        return dbSNPNse.getConsensusKey();
    }
    // create MGI_SNP_StrainAlleles for the RS consensus strain alleles
    // ssAlleleMap looks like ssId:HashMap(strain:Allele)
    private void createConsensusAlleles(Integer consensusKey, HashMap ssAlleleMap,
                            String rsId) throws DBException, ConfigException {

        // looks like strain:HashMap[allele:count]
        HashMap consensusAlleleMap = new HashMap();
        // summary of the consensus alleles
        HashSet alleleSummary = new HashSet();
        // get alleles for each ss into newMap
        int ssWithAllelesCt = 0; //DEBUG
        for (Iterator i = ssAlleleMap.keySet().iterator(); i.hasNext(); ) {
            // get the ssid
            String ssId = (String) i.next();
            // get the set of strain alleles for this ssId
            HashMap alleleMap = (HashMap) ssAlleleMap.get(ssId);
            if (alleleMap.size() > 0 ) {
                ssWithAllelesCt++;
            }
            // iterate thru the strain alleles
            for (Iterator j = alleleMap.keySet().iterator(); j.hasNext(); ) {
                String strain = (String) j.next();
                String allele = ( (Allele) alleleMap.get(strain)).getAllele();

                // if in reverse orientation we need to flip 'allele' before storing
                if ( ( (Allele) alleleMap.get(strain)).getOrientation().equals(
                    SNPLoaderConstants.REVERSE_ORIENT)) {
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
                            default: System.out.println("Bad input for ss " + ssId);
                        }
                    }
                    // set a to the flipped versio of the allele
                    allele = convertedAllele.toString();
                    // add allele to the alleleSummary set
                    alleleSummary.add(allele);
                }

                // now put the allele in the consensus allele map

                // if the strain is already in the consensus allele map,
                // increment ctr for the allele or add anew allele with ct=1 is necessary
                // consensusAlleleMap looks like strain:HashMap[allele:count]
                if (consensusAlleleMap.containsKey(strain)) {
                    // existing map looks like allele:count
                    HashMap existingMap = (HashMap)consensusAlleleMap.get(strain);
                    if (existingMap.containsKey(allele)) {
                        int ct = ((Integer)existingMap.get(allele)).intValue();
                        ct++;
                        existingMap.put(allele, new Integer(ct));
                    }
                    else {
                        existingMap.put(allele, new Integer(1));
                    }
                    consensusAlleleMap.put(strain, existingMap);
                }
                // if the strain is not in the map, add the new strain  with
                // and add the strain and allele the the consensus allele map
                else {
                    HashMap newMap = new HashMap();
                    newMap.put(allele, new Integer(1));
                    consensusAlleleMap.put(strain, newMap);
                }
            }
            /*if(ssWithAllelesCt > 2) {
                logger.logcInfo("RS" + rsId + " has > 2 ss with alleles ", false);
            }*/
        }
        // add allele summary to the rs
        String alleleString = alleleSummary.toString();
        dbSNPNse.addRSAlleleSummary(alleleString.substring(1,alleleString.length()-1));
        // now find the consensus allele
         //logger.logdDebug("RS" + rsId);
         // consensusAlleleMap looks like strain:HashMap[allele:count]
         for (Iterator j = consensusAlleleMap.keySet().iterator(); j.hasNext();) {
             // the current consensus allele found
             String conAllele = "";
             // the count of instances of conAllele
             int currentCt = 0;
             // true if the currentCt is equal to count
             boolean isEqual = false;
             // get the strain and the alleles
             String strain = (String)j.next();
             HashMap alleles = (HashMap)consensusAlleleMap.get(strain);
             //logger.logdDebug("\tstrain " + strain );
             // iterate thru the alleles of sthi strain
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
                 logger.logdDebug("\t\t" + " allele: " +
                                  allele + " count " + count);
             }
             // if the equal flag is true we don't have consensus
             if(isEqual == true) {
                 conAllele = "?";
             }
             // now create the consensus allele
             MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
             state.setAllele(conAllele);
             state.setStrain(strain);
             state.setObjectKey(consensusKey);
             state.setObjectType(SNPLoaderConstants.OBJECTYPE_CSNP);
             state.setJobStreamKey(jobStreamKey);
             dbSNPNse.addStrainAllele(state);
             // if the set of alleles for a strain is > 2 we may have a problem with
             // computing majority; build 124 this case does not exist
             //if(alleles.keySet().size() > 2) {
             //logger.logcInfo("RS" + rsId + " strain " + strain + " has " + alleles.keySet().size() + "alleles " + alleles.keySet().toString(), false);
             //logger.logcInfo("\tconsensusAllele: " + conAllele, false);
             //}
         }
    }

    private void createSS(Integer consensusKey, DBSNPNseSS ss, HashMap alleleMap)
            throws DBException, ConfigException {
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

        // add the completed state object to the DBSNPNse object
        dbSNPNse.addSS(ssId, state);
        // get the ssKey so we can create other associated objects
        Integer ssKey = dbSNPNse.getSSKey(ssId);
        // create an accession object for the current ssId
        createAccession(ssId, SNPLoaderConstants.LDB_SSNP, ssKey,
                        SNPLoaderConstants.OBJECTYPE_SSNP);
        // create an accession object for the current submitter snp id
        createAccession(ss.getSubmitterSNPId(), SNPLoaderConstants.LDB_SUBMITTER,
                        ssKey, SNPLoaderConstants.OBJECTYPE_SSNP);
        if (alleleMap != null) {
            createStrainAlleles(ssKey, alleleMap);
        }

    }
    private void createAccession(String accid, String logicalDB,
            Integer objectKey, String objectType)
                throws DBException, ConfigException {
        MGI_SNP_AccessionState state = new MGI_SNP_AccessionState();
        state.setAccID(accid);
        state.setLogicalDB(logicalDB);
        state.setObjectKey(objectKey);
        state.setObjectType(objectType);
        state.setPrivateVal(Boolean.FALSE);
        state.setJobStreamKey(jobStreamKey);
        dbSNPNse.addAccession(state);
    }
    private void createStrainAlleles(Integer subSNPKey, HashMap alleleMap)
        throws DBException, ConfigException {
        // create an strain allele object for each strain assay for this ss
        for (Iterator i = alleleMap.keySet().iterator(); i.hasNext(); ) {
            String strain = (String)i.next();
            MGI_SNP_StrainAlleleState state = new MGI_SNP_StrainAlleleState();
            state.setObjectKey(subSNPKey);
            state.setObjectType(SNPLoaderConstants.OBJECTYPE_SSNP);
            state.setJobStreamKey(jobStreamKey);
            state.setStrain(strain);
            // get the allele string from the Allele object, set in state
            Allele allele = (Allele)alleleMap.get(strain);
            state.setAllele(allele.getAllele());
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
                cState.setStartCoord(mloc.getStartCoord());
                dbSNPNse.addCoordinate(cState);

                // now get the fxnSet and create the Marker objects
                Vector fxnSets = mloc.getFxnSets();
                // thes set of locusId fxnClass pairs; we don't want to load
                // dups
                //analyzeFxnSets(fxnSets, rsId);
                HashSet fxnSetSet = new HashSet();
                // iterate over the FxnSets

                for (Iterator k = fxnSets.iterator(); k.hasNext(); ) {
                    DBSNPNseFxnSet fSet = (DBSNPNseFxnSet)k.next();
                    String fxnClass = fSet.getFxnClass();
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
                    fxnSetSet.add(locusId + fxnClass);

                    // create the state object
                    MGI_SNP_MarkerState mState = new MGI_SNP_MarkerState();
                    mState.setConsensusSNPKey(consensusKey);
                    mState.setJobStreamKey(jobStreamKey);
                    mState.setEntrezGeneId(locusId);
                    mState.setFxnClass(fxnClass);
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
    // for statistics
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
        }
        if (nonsynonFlag == true && synonFlag == true) {
            logger.logcInfo("RS" + rsId + " has " + referenceCtr + " reference class instances", false);
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
