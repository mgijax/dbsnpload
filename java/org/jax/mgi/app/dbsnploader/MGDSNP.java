package org.jax.mgi.app.dbsnploader;

import java.util.*;

import org.jax.mgi.shr.Sets;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGD;;

/**
 *
 * is an object that represents a set of SNP DAOs for the MGD database
 * @has
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class MGDSNP {

        // the stream used to accomplish the database inserts, updates, deletes
        private SQLStream stream;

        // the ConsensusSNP DAO and its mgd database key
        private SNP_ConsensusSnpDAO cSnpDAO;
        private Integer consensusSnpKey;

        // the set accession id DAO's for a ConsensusSNP and its SubSNPs
        private Vector accessions = new Vector();

        // flanking sequence DAO's
        private Vector flanks = new Vector();

        // the SubSNP DAO's
        private Vector subSnps = new Vector();

        // the consensus and SubSNP strain allele DAO's
        private Vector csStrainAlleles = new Vector();
        private Vector ssStrainAlleles = new Vector();

        // The SNP_Coord_CacheDAOs for this SNP
        private Vector coordCache = new Vector();

        // new SNP Strain MGI_SetMemberDAOs representing strains we haven't yet
        // created a set member for in the load
        private Vector setMember = new Vector();

        /**
          * Constructs a MGDSNP object with just a stream
          * @param stream the stream which to pass the DAO objects to perform database
          *        inserts, updates, and deletes
          */
         public MGDSNP(SQLStream stream) {
             this.stream = stream;
         }

        /**
         * Constructs a SNP object by creating SNP_ConsensusSnpDAO for 'state'
         * @assumes state does not exist in the database
         * @effects queries a database for the next sequence key
         * @param state a new SNP_ConsensusSnpState
         * @param stream the stream which to pass the DAO objects to perform database
         *        inserts, updates, and deletes
         * @throws ConfigException if error creating  SEQ_SequenceSeqloaderDAO
         * @throws DBException if error creating  SEQ_SequenceSeqloaderDAO
         */

        public MGDSNP(SNP_ConsensusSnpState state, SQLStream stream) throws
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
        public void addAccession(ACC_AccessionState state) throws
            ConfigException, DBException{
            accessions.add(new ACC_AccessionDAO(state));
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
        public void addSetMember(MGI_SetMemberState state)
            throws ConfigException, DBException{
            setMember.add(new MGI_SetMemberDAO(state));
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
         * writes to the appropriate SNP bcp files
         * @assumes Nothing
         * @effects Performs database Inserts
         * @throws DBException if error inserting into a database
         */

        public void sendToStream() throws DBException {
            // iterate thru all DAO structures calling stream.insert on each
            // insert the consensus snp
            stream.insert(cSnpDAO);
            // insert the flanking sequences
            Iterator i;
            for (i = flanks.iterator(); i.hasNext(); ) {
                stream.insert((SNP_FlankDAO)i.next());
            }
            for (i = subSnps.iterator(); i.hasNext(); ) {
                stream.insert((SNP_SubSnpDAO)i.next());
            }
            for (i = accessions.iterator(); i.hasNext(); ) {
                stream.insert((ACC_AccessionDAO)i.next());
            }
            for (i = csStrainAlleles.iterator(); i.hasNext(); ) {
                stream.insert((SNP_ConsensusSnp_StrainAlleleDAO)i.next());
            }
            for (i = ssStrainAlleles.iterator(); i.hasNext(); ) {
                stream.insert((SNP_SubSnp_StrainAlleleDAO)i.next());
            }
            // First determine the isMultiCoord for the SNP_Coord_CacheDAO(s)
            Boolean isMultiCoord = Boolean.FALSE;
            if (coordCache.size() > 1) {
                isMultiCoord = Boolean.TRUE;
            }
            // send to stream, setting isMultiCoord value
            for( i = coordCache.iterator(); i.hasNext(); ) {
                SNP_Coord_CacheDAO dao = (SNP_Coord_CacheDAO)i.next();
                dao.getState().setIsMultiCoord(isMultiCoord);
                stream.insert(dao);
            }
            for (i = setMember.iterator(); i.hasNext();) {
                stream.insert((MGI_SetMemberDAO)i.next());
            }

        }

    }