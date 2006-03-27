package org.jax.mgi.app.dbsnploader;

import java.util.*;

import org.jax.mgi.shr.Sets;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.snp.dao.*;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;

/**
 *
 * is an object that represents snp..SNP_ConsensusSnp database object and all its
 * associated objects
 * @has <UL>
 *      <LI>SNP_ConsensusSnpDAO
 *      <LI>Integer database key for the SNP_ConsensusSnpDAO for easy access
 *      <LI> a set of SNP_SubSnpDAOs
 *      <LI> a set of SNP_AccessionDAOs for all SNP accession ids
 *      <LI> a set of SNP_FlankDAOs
 *      <LI> as set of SNP_ConsensusSnp_StrainAlleleDAOs representing this
 *           consensusSnp's consensus alleles
 *      <LI> a set of SNP_SubSnp_StrainAlleleDAOs representing the strain alleles
 *           of each SNP_SubSnpDAO
 *      <LI> a set of SNP_Coord_CacheDAOs representing coordinates for this
 *           consensusSnp
 *      <LI> a set of DP_SNP_MarkerDAOs representing markers associated with this
 *           consensusSnp
 *      <LI> a set of SNP_StrainDAOs representing any dbsnp strains that have not
 *           yet been created
 *      </UL>
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class SNPSNP {

        // the stream used to accomplish the database inserts, updates, deletes
        private SQLStream stream;

        // the SNP_ConsensusSNPDAO and its snp database key
        private SNP_ConsensusSnpDAO cSnpDAO;
        private Integer consensusSnpKey;

        // the set of SNP_AccessionDAO's for a SNP_ConsensusSNP and its SNP_SubSNPs
        private Vector accessions = new Vector();

        // set of SNP_FlankDAO's
        private Vector flanks = new Vector();

        // the SubSNP DAO's
        private Vector subSnps = new Vector();

        // the consensus and SubSNP strain allele DAO's
        private Vector csStrainAlleles = new Vector();
        private Vector ssStrainAlleles = new Vector();

        // The SNP_Coord_CacheDAOs for this SNP
        private Vector coordCache = new Vector();

        // The DP_SNP_MarkerDAOs for this SNP
        private Vector dpMarker = new Vector();

        // new SNP_Strain objects representing strains we haven't yet
        // added to SNP_Strain
        private Vector strains = new Vector();

        /**
          * Constructs a SNPSNP object with just a stream
          * @param stream the stream which to pass the DAO objects to perform database
          *        inserts, updates, and deletes
          */
         public SNPSNP(SQLStream stream) {
             this.stream = stream;
         }

        /**
         * Constructs a SNP object by creating SNP_ConsensusSnpDAO for 'state'
         * @assumes state does not exist in the database
         * @effects queries a database for the next SNP_ConsensusSnp._ConsensusSnp_key
         * @param state a new SNP_ConsensusSnpState
         * @param stream the stream which to pass the DAO objects to perform database
         *        inserts, updates, and deletes
         * @throws ConfigException if error creating  SNP_ConsensusSnpDAO object
         * @throws DBException if error creating  SNP_ConsensusSnpDAO object
         */

        public SNPSNP(SNP_ConsensusSnpState state, SQLStream stream) throws
            ConfigException, DBException {
            this.stream = stream;
            cSnpDAO = new SNP_ConsensusSnpDAO(state);
            consensusSnpKey = cSnpDAO.getKey().getKey();
        }

        /**
         *
         * set methods
         *
         */
        public void setConsensusSnp(SNP_ConsensusSnpState state) throws
            ConfigException, DBException {
            cSnpDAO = new SNP_ConsensusSnpDAO(state);
            consensusSnpKey = cSnpDAO.getKey().getKey();
        }
        public void addAccession(SNP_AccessionState state) throws
            ConfigException, DBException{
            accessions.add(new SNP_AccessionDAO(state));
        }
        public void addFlank(SNP_FlankState state) {
            flanks.add(new SNP_FlankDAO(state));
        }
        public Integer addSubSNP(SNP_SubSnpState state)
            throws ConfigException, DBException {
            SNP_SubSnpDAO dao = new SNP_SubSnpDAO(state);
            subSnps.add(dao);
            return dao.getKey().getKey();
        }

        public void addStrain(SNP_StrainState state)
            throws ConfigException, DBException{
            strains.add(new SNP_StrainDAO(state));
        }

        public void addConsensusSnpStrainAllele(SNP_ConsensusSnp_StrainAlleleState state) {
            csStrainAlleles.add(new SNP_ConsensusSnp_StrainAlleleDAO(state));
        }
        public void addSubSnpStrainAllele(SNP_SubSnp_StrainAlleleState state) {
            ssStrainAlleles.add(new SNP_SubSnp_StrainAlleleDAO(state));
        }
        public void addSnpCoordCache(SNP_Coord_CacheState state) throws
            ConfigException, DBException{
            coordCache.add(new SNP_Coord_CacheDAO(state));
        }
        public void addMarker(DP_SNP_MarkerState state) throws
            ConfigException, DBException {
            dpMarker.add(new DP_SNP_MarkerDAO(state));
        }
        /**
         *
         * get methods
         *
         */
        public Integer getConsensusSnpKey() {
            return consensusSnpKey;
        }

        /**
          * gets the consensus snp DAO
          * @assumes Nothing
          * @effects Nothing
          * @return SNP_ConsensusSnpDAO
          */

         public SNP_ConsensusSnpDAO getConsensusSnpDao() {

             return cSnpDAO;

         }
         /**
          * get the set of SNP_Coord_Cache objects for this Consensus SNP
          * @return Vector of SNP_Coord_Cache objects
          */
         public Vector getCoordCacheDaos() {
             return coordCache;
         }

        /**
         * writes to the appropriate SNP* bcp files
         * @assumes Nothing
         * @effects Performs database Inserts
         * @throws DBException if error inserting into a database
         */

        public void sendToStream() throws DBException {
            //  call stream.insert on each DAO

            // insert the consensus snp
            stream.insert(cSnpDAO);
            // insert all flanking sequences
            Iterator i;
            for (i = flanks.iterator(); i.hasNext(); ) {
                stream.insert((SNP_FlankDAO)i.next());
            }
            // insert all subsnps
            for (i = subSnps.iterator(); i.hasNext(); ) {
                stream.insert((SNP_SubSnpDAO)i.next());
            }
            // insert all accessions
            for (i = accessions.iterator(); i.hasNext(); ) {
                stream.insert((SNP_AccessionDAO)i.next());
            }
            // insert all consensusSnp strain alleles
            for (i = csStrainAlleles.iterator(); i.hasNext(); ) {
                stream.insert((SNP_ConsensusSnp_StrainAlleleDAO)i.next());
            }
            // insert all subSnp strain alleles
            for (i = ssStrainAlleles.iterator(); i.hasNext(); ) {
                stream.insert((SNP_SubSnp_StrainAlleleDAO)i.next());
            }
            // First determine the isMultiCoord for the SNP_Coord_CacheDAO(s)
            Boolean isMultiCoord = Boolean.FALSE;
            if (coordCache.size() > 1) {
                isMultiCoord = Boolean.TRUE;
            }
            // insert snp coordinates setting isMultiCoord value
            for( i = coordCache.iterator(); i.hasNext(); ) {
                SNP_Coord_CacheDAO dao = (SNP_Coord_CacheDAO)i.next();
                dao.getState().setIsMultiCoord(isMultiCoord);
                stream.insert(dao);
            }
            // insert all snp markers
            for (i = dpMarker.iterator(); i.hasNext(); ) {
                stream.insert((DP_SNP_MarkerDAO)i.next());
            }
            // insert all new snp strains
            for (i = strains.iterator(); i.hasNext();) {
                stream.insert((SNP_StrainDAO)i.next());
            }
        }
    }