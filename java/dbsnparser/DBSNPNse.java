//  $Header
//  $Name

package dbsnparser;

import java.util.*;

import org.jax.mgi.shr.Sets;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.rdr.dao.*;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.shr.dla.loader.seq.*;

    /**
     * An object that manages a set of DAOs representing a dbSNP RS
     * input file.
     * @has
     *   <UL>
     *   <LI>
     *   <LI>
     *   </UL>
     * @does
     *   <UL>
     *   <LI>creates DAO objects for
     *   <LI>Adds a ----- to a database
     *   <LI>Provides methods to get *copies only* of States for each of its DAO's
     *   </UL>
     * @company The Jackson Laboratory
     * @author sc
     * @version 1.0
     */

public class DBSNPNse {

    // the stream used to accomplish the database inserts, updates, deletes
    private SQLStream stream;

    // The RS
    private MGI_SNP_ConsensusSNPDAO rsDAO;

    // the set of DP_DBSNP_SSDAO; all ss for this RS
    //private Vector ssVector;
    private HashMap ssMap;

    // the set of strain alleles for every SS making up this RS
    private Vector strAlleleVector;

    // the set of Accession ids for this RS and all its SS
    private Vector accVector;

    // the set of Flanking sequences for this RS
    private Vector flankVector;

    // the set of Coordinates for this RS
    private Vector coordVector;

    // the set of markers for this RS
    private Vector markerVector;

    /**
     * Constructs a DBSNPNse object with just a stream
     * @param stream the stream which to pass the DAO objects to perform database
     *        inserts, updates, and deletes
     */
    public DBSNPNse(SQLStream stream) {
        this.stream = stream;
        ssMap = new HashMap();
        strAlleleVector = new Vector();
        flankVector = new Vector();
        accVector = new Vector();
        coordVector = new Vector();
        markerVector = new Vector();
    }
    /**
     * Constructs a DBSPNse object by creating MGI_SNP_ConsensusSNPDAO
     * for 'rsState'
     * @assumes state does not exist in the database
     * @effects queries a database for the next sequence key
     * @param rsState a MGI_SNP_ConsensusSNPState representing dbSNP RS data
     * @param stream the stream which to pass the DAO objects to perform database
     *        inserts, updates, and deletes
     * @throws ConfigException if error creating MGI_SNP_ConsensusSNPDAO
     * @throws DBException if error creating MGI_SNP_ConsensusSNPDAO
     */

    public DBSNPNse(MGI_SNP_ConsensusSNPState rsState, SQLStream stream)
        throws ConfigException, DBException {
        this(stream);
        rsDAO = new MGI_SNP_ConsensusSNPDAO(rsState);


        /*
        for(Iterator i = rsStates.iterator(); i.hasNext();) {
            DP_DBSNP_GenotypeRSState rsState = (DP_DBSNP_GenotypeRSState)i.next();
            rsDAOVector.add(new DP_DBSNP_GenotypeRSDAO(rsState));
        }*/
    }

    /**
      * gets the consensus key from the MGI_SNP_ConsensusSNPDAO object
      */

    public Integer getConsensusKey() {
        return rsDAO.getKey().getKey();
    }

    /**
     * gets the subSNP key for 'ssId'
     * @param ssId
     * @return
     */
    public Integer getSSKey(String ssId) {
        MGI_SNP_SubSNPDAO dao = (MGI_SNP_SubSNPDAO)ssMap.get(ssId);
        return dao.getKey().getKey();
    }
    /**
     * add
     * @param state
     * @throws ConfigException
     * @throws DBException
     */
    /*
    public void addRS(MGI_SNP_ConsensusSNPState state) throws ConfigException,
        DBException {
        rsDAO = new MGI_SNP_ConsensusSNPDAO(state);
    }
    */

   /**
    * adds the allele summary to the RS DAO
    * @param summary the allele summary to add to the RS state
    */
    public void addRSAlleleSummary(String summary) {
        rsDAO.getState().setAlleleSummary(summary);
    }
    /**
     * Adds a MGI_SNP_SubSNPDAO to the set of DAO's representing the
     * SS of this RS
     * @assumes Nothing
     * @effects Queries a database for the next accession key
     * @param state MGI_SNP_SubSNP representing one SS member of this RS
     * @throws ConfigException if error creating the DAO object
     * @throws DBException if error creating the DAO object
     */

    public void addSS(String ssId, MGI_SNP_SubSNPState state)
        throws ConfigException, DBException {
        ssMap.put(ssId, new MGI_SNP_SubSNPDAO(state));
    }

    /**
         * Adds a MGI_SNP_StrainAllelDAO to the set of DAO's representing the
         * SS of this RS
         * @assumes Nothing
         * @effects Queries a database for the next accession key
         * @param state MGI_SNP_SubSNP representing one SS member of this RS
         * @throws ConfigException if error creating the DAO object
         * @throws DBException if error creating the DAO object
         */

        public void addStrainAllele(MGI_SNP_StrainAlleleState state)
            throws ConfigException, DBException {
            strAlleleVector.add(new MGI_SNP_StrainAlleleDAO(state));
        }

    /**
     * Adds a MGI_SNP_AccessionDAO to the set of DAO's representing the
     * accession ids for this RS
     * @assumes Nothing
     * @effects Queries a database for the next accession key
     * @param state MGI_SNP_AccessionState representing an accession id for this RS
     * @throws ConfigException if error creating the DAO object
     * @throws DBException if error creating the DAO object
     */


    public void addAccession(MGI_SNP_AccessionState state)
        throws ConfigException, DBException {
        accVector.add(new MGI_SNP_AccessionDAO(state));
    }

    /**
     * Adds a MGI_SNP_FlankDAO to the set of DAO's representing a chunk of
     * flanking sequence for this RS
     * @assumes Nothing
     * @effects Queries a database for the next accession key
     * @param state MGI_SNP_FlankState representing a chunk of flanking sequence
     * for this RS
     * @throws ConfigException if error creating the DAO object
     * @throws DBException if error creating the DAO object
     */

    public void addFlank(MGI_SNP_FlankState state)
        throws ConfigException, DBException {
        flankVector.add(new MGI_SNP_FlankDAO(state));
    }

    /**
     * Adds a MGI_SNP_MarkerDAO to the set of DAO's representing the markers
     * for this RS
     * @assumes Nothing
     * @effects Queries a database for the next accession key
     * @param state MGI_SNP_MarkerState representing the markers
     * for this RS
     * @throws ConfigException if error creating the DAO object
     * @throws DBException if error creating the DAO object
     */

    public void addMarker(MGI_SNP_MarkerState state)
        throws ConfigException, DBException {
        markerVector.add(new MGI_SNP_MarkerDAO(state));
    }

    /**
     * Adds a MGI_SNP_CoordinateDAO to the set of DAO's representing the
     * coordinates for this RS
     * @assumes Nothing
     * @effects Queries a database for the next accession key
     * @param state MGI_SNP_CoordinateState representing the coordinates
     * for this RS
     * @throws ConfigException if error creating the DAO object
     * @throws DBException if error creating the DAO object
     */

    public void addCoordinate(MGI_SNP_CoordinateState state)
        throws ConfigException, DBException {
        coordVector.add(new MGI_SNP_CoordinateDAO(state));
    }

    /**
     * gets a *copy* of the rs state
     * @assumes Nothing
     * @effects Nothing
     * @return state a *copy* of the sequence state
     */

    public MGI_SNP_ConsensusSNPState getRSState() {

        return rsDAO.getState();

    }
    /**
     *
     * create get methods for remaining attributes
     */


    /**
     * Determines the stream methods for and passes to those methods each of
     * its DAO objects.
     * Inserts or updates the sequence.
     * Inserts and deletes reference associations.
     * Inserts and deletes source associations.
     * Inserts primary seqid.
     * Inserts and deletes 2ndary seqids.
     * @assumes Nothing
     * @effects Performs database Inserts, updates, and deletes.
     * @throws DBException if error inserting, updating, or deleting in the database
     */

    public void sendToStream() throws DBException {
        Iterator i;
        // insert the RS
        stream.insert(rsDAO);
        // insert SS
        Set keys = ssMap.keySet();
        for (i = keys.iterator(); i.hasNext(); ) {
            stream.insert( (MGI_SNP_SubSNPDAO)ssMap.get(i.next()));
        }
        // insert ss strain alleles
        i = strAlleleVector.iterator();
        while (i.hasNext()) {
            stream.insert( (MGI_SNP_StrainAlleleDAO) i.next());
        }
        // insert accession ids
        i = accVector.iterator();
        while (i.hasNext()) {
            stream.insert( (MGI_SNP_AccessionDAO) i.next());
        }
        // insert flanking sequences
        i = flankVector.iterator();
        while (i.hasNext()) {
            stream.insert( (MGI_SNP_FlankDAO) i.next());
        }
        // insert marker associations
        i = markerVector.iterator();
        while (i.hasNext()) {
            stream.insert( (MGI_SNP_MarkerDAO) i.next());
        }
        // insert coordinates
        i = coordVector.iterator();
        while (i.hasNext()) {
            stream.insert( (MGI_SNP_CoordinateDAO) i.next());
        }
    }
}

//  $Log

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
