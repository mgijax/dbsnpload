//  $Header
//  $Name

package org.jax.mgi.app.dbsnploader;

import java.util.*;

import org.jax.mgi.shr.Sets;
import org.jax.mgi.shr.dbutils.dao.SQLStream;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.cache.KeyNotFoundException;
import org.jax.mgi.shr.cache.CacheException;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.dbs.rdr.dao.*;
import org.jax.mgi.dbs.mgd.dao.*;
import org.jax.mgi.shr.dla.log.DLALoggingException;
import org.jax.mgi.dbs.mgd.MGITypeConstants;
import org.jax.mgi.dbs.mgd.MGD;
import org.jax.mgi.shr.dla.loader.seq.*;

/**
 *
 * is an object that represents a set of SNP DAOs for the RADAR database
 * @has
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class RADARSNP {

    // the stream used to accomplish the database inserts, updates, deletes
    private SQLStream stream;


    // the set of markers for this RS
    private Vector markerVector;

    /**
     * Constructs a RADARSNP object with just a stream
     * @param stream the stream which to pass the DAO objects to perform database
     *        inserts, updates, and deletes
     */
    public RADARSNP(SQLStream stream) {
        this.stream = stream;
        markerVector = new Vector();
    }
    /**
     * Constructs a RADARSNP object by creating MGI_SNP_MarkerDAO
     * for 'markerState'
     * @assumes state does not exist in the database
     * @effects queries a database for the next sequence key
     * @param rsState a SNP_ConsensusSnpState representing dbSNP RS data
     * @param stream the stream which to pass the DAO objects to perform database
     *        inserts, updates, and deletes
     * @throws ConfigException if error creating SNP_ConsensusSnpDAO
     * @throws DBException if error creating SNP_ConsensusSnpDAO
     */

    public RADARSNP( MGI_SNP_MarkerState markerState, SQLStream stream)
        throws ConfigException, DBException {
        this(stream);
        markerVector.add(new MGI_SNP_MarkerDAO(markerState));
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


    public Vector getMarkerDAOs() {
        // the set of markers for this RS
        return markerVector;
    }


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

        // insert marker associations
        i = markerVector.iterator();
        while (i.hasNext()) {
            stream.insert( (MGI_SNP_MarkerDAO) i.next());
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
