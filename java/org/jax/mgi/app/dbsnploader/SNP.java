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
     * An object that manages a set of DAOs representing a sequence.
     * @has
     *   <UL>
     *   <LI>SEQ_SequenceDAO
     *   <LI>ACC_AccessionDAO's for its primary seqid and any 2ndary seqids
     *   <LI>MGI_ReferenceAssocDAO's for any references associated with the sequence
     *   <LI>SEQ_SourceAssocDAO's for sources associated with the sequence
     *   <LI>Knows if its SEQ_SequenceDAO exists in MGI or is a new sequence
     *   <LI>If the SEQ_SequenceDAO exists in MGI, knows if its state has changed
     *   </UL>
     * @does
     *   <UL>
     *   <LI>creates DAO objects for Sequence, primary and 2ndary seqids,
     *       reference association(s) and source association(s).
     *   <LI>Updates a sequence and adds new reference associations in a database
     *   <LI>Adds a sequence, its seqids, reference and source associations to
     *       a database
     *   <LI>Provides methods to get *copies only* of States for each of its DAO's
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class SNP {

        // the stream used to accomplish the database inserts, updates, deletes
        private SQLStream stream;

        // the ConsensusSNP DAO and its mgd database key
        private SNP_ConsensusSnpDAO cSnpDao;
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

        // The SNP_Strain_CacheDAOs for this SNP
        private Vector strainCache = new Vector();

        // new SNP Strain MGI_SetMemberDAOs representing strains we haven't yet
        // created a set member for in the load
        private Vector setMember = new Vector();

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

        public SNP(SNP_ConsensusSnpState state, SQLStream stream) throws
            ConfigException, DBException {
            this.stream = stream;
            cSnpDao = new SNP_ConsensusSnpDAO(state);
            consensusSnpKey = cSnpDao.getKey().getKey();
        }
        /**
         *
         * set methods
         *
         */
        public void setAccession(ACC_AccessionState state) throws
            ConfigException, DBException{
            accessions.add(new ACC_AccessionDAO(state));
        }
        public void setFlank(SNP_FlankState state) {
            flanks.add(new SNP_FlankDAO(state));
        }
        public Integer setSubSNP(SNP_SubSnpState state)
            throws ConfigException, DBException {
            SNP_SubSnpDAO dao = new SNP_SubSnpDAO(state);
            subSnps.add(dao);
            return dao.getKey().getKey();
        }

        public void setConsensusSnpStrainAllele(SNP_ConsensusSnp_StrainAlleleState state) {
            csStrainAlleles.add(new SNP_ConsensusSnp_StrainAlleleDAO(state));
        }
        public void setSubSnpStrainAllele(SNP_SubSnp_StrainAlleleState state) {
            ssStrainAlleles.add(new SNP_SubSnp_StrainAlleleDAO(state));
        }

        public void setStrainCache(SNP_Strain_CacheState state) {
            strainCache.add(new SNP_Strain_CacheDAO(state));
        }
        public void setSetMember(MGI_SetMemberState state)
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
         * writes to the appropriate SNP bcp files
         * @assumes Nothing
         * @effects Performs database Inserts
         * @throws DBException if error inserting into a database
         */

        public void sendToStream() throws DBException {
            // iterate thru all DAO structures calling stream.insert on each
            // insert the consensus snp
            stream.insert(cSnpDao);
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
            for( i = strainCache.iterator(); i.hasNext(); ) {
                stream.insert((SNP_Strain_CacheDAO)i.next());
            }
            for (i = setMember.iterator(); i.hasNext();) {
                stream.insert((MGI_SetMemberDAO)i.next());
            }

        }

    }