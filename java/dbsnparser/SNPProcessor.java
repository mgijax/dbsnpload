package dbsnparser;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dbutils.SQLDataManagerFactory;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;
//import org.jax.mgi.dbs.SchemaConstants;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.dbs.rdr.dao.*;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.dbs.mgd.lookup.TranslationException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.dbs.mgd.lookup.MGITypeLookup;
import org.jax.mgi.dbs.mgd.lookup.LogicalDBLookup;
import org.jax.mgi.dbs.mgd.lookup.VocabKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.StrainKeyLookup;
import org.jax.mgi.dbs.mgd.lookup.AccessionLookup;
import org.jax.mgi.dbs.mgd.LogicalDBConstants;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.AccessionLib;
import org.jax.mgi.dbs.mgd.VocabularyTypeConstants;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.IOException;

public class SNPProcessor {
    private MGITypeLookup mgiTypeLookup;
    private LogicalDBLookup ldbLookup;
    private VocabKeyLookup varClassLookup;
    private VocabKeyLookup subHandleLookup;
    private StrainKeyLookup strainKeyLookup;
    private AccessionLookup jaxRegistryLookup;
    private SQLStream mgdStream;
    private SNP snp;
    private Integer mgdConsensusSnpKey;
    private String consensusSnpId;
    private BufferedWriter coordWriter;
    private DLALogger logger;
    private SNPLoaderExceptionFactory snpEFactory;
    private AlleleOrderer alleleOrderer;
    private IUPACResolver iupacResolver;

    public SNPProcessor(SQLStream mgdSqlStream, BufferedWriter writer)
        throws CacheException, DBException, ConfigException,
            DLALoggingException, TranslationException {
        mgiTypeLookup = new MGITypeLookup();
        ldbLookup = new LogicalDBLookup();
        varClassLookup = new VocabKeyLookup(VocabularyTypeConstants.SNPVARCLASS);
        subHandleLookup = new VocabKeyLookup(VocabularyTypeConstants.SUBHANDLE);
        strainKeyLookup = new StrainKeyLookup();
        jaxRegistryLookup = new AccessionLookup(LogicalDBConstants.JAXREGISTRY,
                MGITypeConstants.STRAIN, AccessionLib.PREFERRED);
        mgdStream = mgdSqlStream;
        coordWriter = writer;
        logger = DLALogger.getInstance();
        snpEFactory = new SNPLoaderExceptionFactory();
        alleleOrderer = new AlleleOrderer();
        iupacResolver = new IUPACResolver();
    }
    public void process(DBSNPNse nse, String id) throws  DBException, CacheException,
       KeyNotFoundException, TranslationException, ConfigException,
           SNPUnresolvedStrainException, SNPLoaderException {
        consensusSnpId = id;

        // consensusAlleles and ssAlleles for this RS
        Vector radarStrAlleleDAOs = nse.getStrAlleleDAOs();
        //radarStrainAlleleMap = createStrainAlleleMap(strAlleles);
        // process the ConsensusSnp
        MGI_SNP_ConsensusSNPState csState = nse.getRSState();
        //create an mgd consensus snp passing the radar rsState and radar consensusSNPKey
        // needed to create it's strain alleles
        //System.out.println("Process ConsensusSnp");
        Vector radarAccessions = nse.getAccDAOs();
        processConsensusSnp(csState, nse.getConsensusKey(), radarAccessions, radarStrAlleleDAOs);
        //processAccessions(accVector);

        // process the set of Flanking sequences for this RS
        //System.out.println("Process Flanks");
        Vector flankVector = nse.getFlankDAOs();
        processFlanks(flankVector);

        // the set of Coordinates for this RS
        //System.out.println("Process Coordinates");
        Vector coordVector = nse.getCoordDAOs();
        processCoordinates(coordVector);

        // process the set of MGI_SNP_SubSNPDao's (all ss) for current snp
        // looks like ssId:ssDAO
        //System.out.println("Process SubSnps");
        HashMap ssMap = nse.getSSDAOs();
        processSS(ssMap, radarAccessions, radarStrAlleleDAOs);

        // don't need this, a separate process will load this table
        //Vector markerVector = nse.getMarkerDAOs();
        //System.out.println("Calling snp.sendToStream");
        snp.sendToStream();

    }
       // radarStrAlleleDAOs is the vector of radar strain allele daos for this consensusSNP
       // it includes cs and ss strain alleles
    private void processConsensusSnp(MGI_SNP_ConsensusSNPState radarState,
            Integer radarConsensusSnpKey, Vector radarAccessions, Vector radarStrAlleleDAOs)
                throws DBException, CacheException, KeyNotFoundException,
                   TranslationException, ConfigException, SNPUnresolvedStrainException {
        SNP_ConsensusSnpState mgdState = new SNP_ConsensusSnpState();
        // order allele summary
        //logger.logdDebug("raw allele summary: " + radarState.getAlleleSummary(), false);
        String orderedAlleleSummary = alleleOrderer.order(radarState.getAlleleSummary());
        //logger.logdDebug("ordered allele summary: " + orderedAlleleSummary, false);
        // resolve allele summary to iupac code
        String iupacCode = iupacResolver.resolve(orderedAlleleSummary);
        //logger.logdDebug("IUPAC code: " + iupacCode, false);
        mgdState.setAlleleSummary(orderedAlleleSummary);
        mgdState.setIupacCode(iupacCode);
        mgdState.setVarClassKey(varClassLookup.lookup(radarState.getVariationClass()));
        snp = new SNP(mgdState, mgdStream);
        // init this instance variable to be used by other methods
        mgdConsensusSnpKey = snp.getConsensusSnpKey();
        processConsensusSnpStrainAlleles(mgdConsensusSnpKey, radarStrAlleleDAOs);
        processConsensusSnpAccessions(mgdConsensusSnpKey, radarAccessions);
    }
    private void processConsensusSnpAccessions(Integer key, Vector radarAccessions) throws DBException,
       CacheException, KeyNotFoundException, TranslationException, ConfigException,
           SNPUnresolvedStrainException {
        for (Iterator i = radarAccessions.iterator(); i.hasNext(); ) {
            MGI_SNP_AccessionDAO radarDao = (MGI_SNP_AccessionDAO)i.next();
            MGI_SNP_AccessionState radarState = radarDao.getState();
            if(radarState.getObjectType().equals(SNPLoaderConstants.OBJECTYPE_CSNP)) {
                ACC_AccessionState mgdState = new ACC_AccessionState();
                mgdState.setObjectKey(mgdConsensusSnpKey);
                mgdState.setAccID(radarState.getAccID());
                mgdState.setLogicalDBKey(ldbLookup.lookup(radarState.
                    getLogicalDB()));
                mgdState.setMGITypeKey(mgiTypeLookup.lookup(radarState.
                    getObjectType()));
                mgdState.setPrivateVal(radarState.getPrivateVal());
                mgdState.setPreferred(Boolean.TRUE);
                snp.setAccession(mgdState);
            }
        }
    }
    private void processSubSnpAccession(Integer mgdSSKey,  Integer radarSSKey, Vector radarAccessions) throws DBException,
       CacheException, KeyNotFoundException, TranslationException, ConfigException,
           SNPUnresolvedStrainException {
        for (Iterator i = radarAccessions.iterator(); i.hasNext(); ) {
            MGI_SNP_AccessionDAO radarDao = (MGI_SNP_AccessionDAO)i.next();
            MGI_SNP_AccessionState radarState = radarDao.getState();
            // if the current radar object type is SS and the current radar object key
            // equals the passed in radarSSKey
            if(radarState.getObjectType().equals(SNPLoaderConstants.OBJECTYPE_SSNP)
               && radarSSKey.equals(radarState.getObjectKey())) {
                ACC_AccessionState mgdState = new ACC_AccessionState();
                mgdState.setObjectKey(mgdSSKey);
                mgdState.setAccID(radarState.getAccID());
                mgdState.setLogicalDBKey(ldbLookup.lookup(radarState.
                    getLogicalDB()));
                mgdState.setMGITypeKey(mgiTypeLookup.lookup(radarState.
                    getObjectType()));
                mgdState.setPrivateVal(radarState.getPrivateVal());
                mgdState.setPreferred(Boolean.TRUE);
                snp.setAccession(mgdState);
            }
        }
    }

    private void processFlanks(Vector radarFlanks) throws DBException, CacheException,
        KeyNotFoundException, TranslationException, ConfigException,
            SNPUnresolvedStrainException {
       for (Iterator i = radarFlanks.iterator(); i.hasNext(); ) {
           MGI_SNP_FlankDAO radarDao = (MGI_SNP_FlankDAO)i.next();
           MGI_SNP_FlankState radarState = radarDao.getState();
           SNP_FlankState mgdState = new SNP_FlankState();
           mgdState.setConsensusSnpKey(mgdConsensusSnpKey);
           mgdState.setFlank(radarState.getFlank());
           mgdState.setSequenceNum(radarState.getSequenceNum());
           mgdState.setIs5Prime(radarState.getIs5prime());
           snp.setFlank(mgdState);
       }
   }
   // throws IOException as MGIException
   // here we are creating a MGS format file to be loaded by the coordload
   // note that we have an empty last column.
   private void processCoordinates(Vector radarCoordinates) throws DBException,
       CacheException, KeyNotFoundException, TranslationException, ConfigException,
           SNPUnresolvedStrainException, SNPLoaderException {
       StringBuffer coord = new StringBuffer();
       for (Iterator i = radarCoordinates.iterator(); i.hasNext(); ) {
           MGI_SNP_CoordinateDAO radarDao = (MGI_SNP_CoordinateDAO)i.next();
           MGI_SNP_CoordinateState radarState = radarDao.getState();
           coord.append(consensusSnpId);
           coord.append("\t");
           coord.append(radarState.getChromosome());
           coord.append("\t");
           coord.append(radarState.getStartCoord());
           coord.append("\t");
           coord.append(radarState.getStartCoord());
           coord.append("\t");
           String orient = radarState.getOrientation();
           String translatedOrient = "";
           if(orient.equals(SNPLoaderConstants.NSE_FORWARD)){
              translatedOrient = "f";
           }
           else if (orient.equals(SNPLoaderConstants.NSE_RS_REVERSE)){
               translatedOrient = "r";
           }
           else {
               logger.logdDebug("Unhandled RS orientation " + orient + " for RS" + consensusSnpId);
               throw new TranslationException("Unhandled RS orient " + orient + " for RS" + consensusSnpId, true);
           }
           coord.append(translatedOrient);
           coord.append("\t\n");
       }
       try {
           coordWriter.write(coord.toString());
       } catch (IOException e) {
           SNPLoaderException e1 =
                 (SNPLoaderException) snpEFactory.getException(
                 SNPLoaderExceptionFactory.CoordFileIOErr, e);
             throw e1;
       }
   }
   private void processSS(HashMap radarSSMap, Vector radarAccessions, Vector radarStrAlleleDAOs) throws DBException, CacheException,
       KeyNotFoundException, TranslationException, ConfigException,
           SNPUnresolvedStrainException {
       for (Iterator i = radarSSMap.keySet().iterator(); i.hasNext(); ) {
           MGI_SNP_SubSNPDAO radarDao = (MGI_SNP_SubSNPDAO)radarSSMap.get(i.next());
           MGI_SNP_SubSNPState radarState = radarDao.getState();
           SNP_SubSnpState mgdState = new SNP_SubSnpState();
           mgdState.setConsensusSnpKey(mgdConsensusSnpKey);
           mgdState.setSubHandleKey(subHandleLookup.lookup(radarState.getSubmitterHandle()));
           mgdState.setVarClassKey(varClassLookup.lookup(radarState.getVariationClass()));
          // resolve orientation
           String orient = radarState.getOrientation();
           String translatedOrient = "";
           if(orient.equals(SNPLoaderConstants.NSE_FORWARD)){
               translatedOrient = "f";
           }
           else if (orient.equals(SNPLoaderConstants.NSE_SS_REVERSE)){
               translatedOrient = "r";
           }
           else {
               logger.logdDebug("Unhandled SS orientation " + orient + " for RS" + consensusSnpId);
               throw new TranslationException("Unhandled SS orient " + orient + " for RS" + consensusSnpId, true);
           }
           mgdState.setOrientation(translatedOrient);
           mgdState.setIsExemplar(radarState.getExemplar());
           mgdState.setAlleleSummary(radarState.getObservedAlleles());
           // setting the state in the SNP object returns the ssKey for use
           // creating the SS strain alleles
           Integer mgdSSKey = snp.setSubSNP(mgdState);
           // need this to process Accessions
           Integer radarSSKey = radarDao.getKey().getKey();
           processSubSnpAccession(mgdSSKey, radarSSKey, radarAccessions);
           processSubSnpStrainAllele(mgdSSKey, radarSSKey, radarStrAlleleDAOs);
       }
   }
   // radarStrAlleleDAOs is the vector of radar strain allele daos for this consensusSNP
   // it includes cs and ss strain alleles
   private void processConsensusSnpStrainAlleles(Integer mgdCSKey,
       Vector radarStrAlleleDAOs) throws DBException, CacheException,
           KeyNotFoundException, TranslationException, ConfigException,
               SNPUnresolvedStrainException {
        HashMap strainAlleles = new HashMap();
        for (Iterator i = radarStrAlleleDAOs.iterator(); i.hasNext();) {
            MGI_SNP_StrainAlleleDAO radarDao = (MGI_SNP_StrainAlleleDAO)i.next();
            MGI_SNP_StrainAlleleState radarState = (MGI_SNP_StrainAlleleState)radarDao.getState();
            if (radarState.getObjectType().equals(SNPLoaderConstants.OBJECTYPE_CSNP)) {
                SNP_ConsensusSnp_StrainAlleleState mgdState = new
                    SNP_ConsensusSnp_StrainAlleleState();
                mgdState.setConsensusSnpKey(mgdCSKey);
                Integer strainKey = radarState.getMgdStrainKey();
                mgdState.setStrainKey(strainKey);
                String allele = radarState.getAllele();
                mgdState.setAllele(allele);
                mgdState.setIsConflict(radarState.getIsConflict());
                snp.setConsensusSnpStrainAllele(mgdState);
                strainAlleles.put(strainKey, allele);
            }
        }
        if (strainAlleles.size() > 0) {
            processStrainCache(mgdCSKey, strainAlleles);
        }
    }
    private void processStrainCache(Integer mgdCSKey, HashMap strainAlleles) {

        HashMap compareTo = new HashMap(strainAlleles);
        //logger.logcInfo("strainAlleles.size() " + strainAlleles.size(), false);
        for (Iterator i = strainAlleles.keySet().iterator(); i.hasNext(); ) {
            Integer strainKey1 = (Integer)i.next();
            String  allele1 = (String)strainAlleles.get(strainKey1);
            //logger.logcInfo("Str1: " + strainKey1 + " allelel: " + allele1, false);
            compareTo.remove(strainKey1);
            for(Iterator j = compareTo.keySet().iterator(); j.hasNext();) {
                SNP_Strain_CacheState state = new SNP_Strain_CacheState();
                Boolean isSame = Boolean.FALSE;
                Integer strainKey2 = (Integer)j.next();
                String allele2 = (String)strainAlleles.get(strainKey2);
                //System.out.println("Str2: " + strainKey2 + " allelel2: " + allele2);
                if(allele1.equals("?") || allele2.equals("?")) {
                    isSame = Boolean.FALSE;
                }
                else if(allele1.equals(allele2)) {
                    isSame = Boolean.TRUE;
                }
                //System.out.println("isSame: " + isSame);
                state.setConsensusSnpKey(mgdCSKey);
                state.setIsSame(isSame);
                state.setStrainKey1(strainKey1);
                state.setStrainKey2(strainKey2);
                snp.setStrainCache(state);
            }
        }
    }
   // radarStrAlleleDAOs is the vector of radar strain allele daos for this consensusSNP
   // it includes cs and ss strain alleles
  private void processSubSnpStrainAllele(Integer mgdSSKey, Integer radarSSKey,
          Vector radarStrAlleleDAOs) throws DBException, CacheException,
              KeyNotFoundException, TranslationException, ConfigException, SNPUnresolvedStrainException {
              for (Iterator i = radarStrAlleleDAOs.iterator(); i.hasNext(); ) {
                  MGI_SNP_StrainAlleleDAO radarDao = (MGI_SNP_StrainAlleleDAO) i.next();
                  MGI_SNP_StrainAlleleState radarState = (MGI_SNP_StrainAlleleState)
                      radarDao.getState();
                  Integer currentRadarObjectKey = radarDao.getKey().getKey();
                  if (radarState.getObjectType().equals(SNPLoaderConstants.
                          OBJECTYPE_SSNP) && radarSSKey.equals(radarState.getObjectKey())) {
                      SNP_SubSnp_StrainAlleleState mgdState = new
                          SNP_SubSnp_StrainAlleleState();
                      mgdState.setSubSnpKey(mgdSSKey);
                      mgdState.setStrainKey(radarState.getMgdStrainKey());
                      mgdState.setAllele(radarState.getAllele());
                      snp.setSubSnpStrainAllele(mgdState);
                  }
              }
          }
}