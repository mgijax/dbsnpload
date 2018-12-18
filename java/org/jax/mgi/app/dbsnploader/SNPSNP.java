package org.jax.mgi.app.dbsnploader;

import java.util.*;

import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.snp.dao.*;

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
 *      <LI> a set of SNP_ConsensusSnp_StrainAlleleDAOs representing this
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
 * @does provides a sendToStream method which passes each DAO to the SQLStream
 * insert method
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
        // dbSNP Build 126 1 RefSnp id + 4  (1.09 subSnps per RefSnp each with 2 accessions)
        private Vector accessions = new Vector(5);

        // set of SNP_FlankDAO's
        // build 126 2.78 flanks (3' +  5') per Snp
        protected Vector flanks = new Vector(3);

        // the SubSNP DAO's
        // build 126 1.09 SubSnp per Snp
        protected Vector subSnps = new Vector(2);

        // the consensus and SubSNP strain allele DAO's
        // build 126 11 strain alleles per ConsensusSnp
        // keep initial capacity at default (10)
        protected Vector csStrainAlleles = new Vector();
        // build 126 11 strain alleles per SubSnp
        protected Vector ssStrainAlleles = new Vector();

        // The SNP_Coord_CacheDAOs for this SNP
        // protected so we can get count of coordinates
        // build 126 1.05 coordinates per Snp
        protected Vector coordCache = new Vector(2);

        // The DP_SNP_MarkerDAOs for this SNP
        // protected so we can get count of markers
        protected Vector dpMarker = new Vector(1);

        // new SNP_Strain objects representing strains we haven't yet
        // added to SNP_Strain
        private Vector strains = new Vector(1);

        /**
          * Constructs a SNPSNP object with just a stream
          * @param stream the stream which to pass the DAO objects to perform database
          *        inserts, updates, and deletes
          */
         public SNPSNP(SQLStream stream) {
             this.stream = stream;
         }

        /**
         * Constructs a SNPSNP object by creating SNP_ConsensusSnpDAO for 'state'
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
        /**
         * sets the SNP_ConsensusSnpDAO object created from 'state';
         * sets the SNP_ConsensusSnpKey object by extracting it from
         * SNP_ConsensusSnpDAO
         * @param state the state from which to create a DAO
         * @throws ConfigException
         * @throws DBException
         */
        public void setConsensusSnp(SNP_ConsensusSnpState state) throws
            ConfigException, DBException {
            cSnpDAO = new SNP_ConsensusSnpDAO(state);
            consensusSnpKey = cSnpDAO.getKey().getKey();
        }
        /**
         * Add a SNP_AccessionDAO object created from 'state'
         * @param state the state from which to create a DAO
         * @throws ConfigException
         * @throws DBException
         */
        public void addAccession(SNP_AccessionState state) throws
            ConfigException, DBException{
            accessions.add(new SNP_AccessionDAO(state));
        }
        /**
         * Add a SNP_FlankDAO object created from 'state'
         * @param state the state from which to create a DAO
         */
        public void addFlank(SNP_FlankState state) {
            flanks.add(new SNP_FlankDAO(state));
        }
        /**
         * Add a SNP_SubSnpDAO object created from 'state'
         * @param state the state from which to create a DAO
         * @return Integer _SubSnp_key
         * @throws ConfigException
         * @throws DBException
         */
        public Integer addSubSNP(SNP_SubSnpState state)
            throws ConfigException, DBException {
            SNP_SubSnpDAO dao = new SNP_SubSnpDAO(state);
            subSnps.add(dao);
            return dao.getKey().getKey();
        }
        /**
         * Add a SNP_StrainDAO object created from 'state'
         * @param state the state from which to create a DAO
         * @throws ConfigException
         * @throws DBException
         */
        public void addStrain(SNP_StrainState state)
            throws ConfigException, DBException{
            strains.add(new SNP_StrainDAO(state));
        }
        /**
         * Add a SNP_ConsensusSnp_StrainAlleleDAO object created from 'state'
         * @param state the state from which to create a DAO
         */
        public void addConsensusSnpStrainAllele(SNP_ConsensusSnp_StrainAlleleState state) {
            csStrainAlleles.add(new SNP_ConsensusSnp_StrainAlleleDAO(state));
        }
        /**
         * Add a SNP_SubSnp_StrainAlleleDAO object created from 'state'
         * @param state call stream.insert on each DAO
         */
        public void addSubSnpStrainAllele(SNP_SubSnp_StrainAlleleState state) {
            ssStrainAlleles.add(new SNP_SubSnp_StrainAlleleDAO(state));
        }
        /**
         * Add a SNP_Coord_CacheDAO object created from 'state'
         * @param state the state from which to create a DAO
         * @throws ConfigException
         * @throws DBException
         */
        public void addSnpCoordCache(SNP_Coord_CacheState state) throws
            ConfigException, DBException{
            coordCache.add(new SNP_Coord_CacheDAO(state));
        }
        /**
         * Add a DP_SNP_MarkerDAO object created from 'state'
         * @param state the state from which to create a DAO
         * @throws ConfigException
         * @throws DBException
         */
        public void addMarker(DP_SNP_MarkerState state) throws
            ConfigException, DBException {
            dpMarker.add(new DP_SNP_MarkerDAO(state));
        }
        /**
         *
         * get methods
         *
         */

        /**
         * get the _ConsensusSnp_key for this SNP
         * @return Integer _ConsensusSnp_key
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
            /**
             * call stream.insert on each DAO
             */

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
            Integer isMultiCoord = new Integer(0);
            if (coordCache.size() > 1) {
                isMultiCoord = 1;
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
            // we are adding in MGP strains ahead of time. We dod not want to truncate this 
            // table and reload because there are many dbsnp strains not used by MGP
            /*for (i = strains.iterator(); i.hasNext();) {
                stream.insert((SNP_StrainDAO)i.next());
            }*/ 
        }
    }
