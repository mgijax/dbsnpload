// $Header
// $Name

package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.ExceptionFactory;
    /**
     * An ExceptionFactory for SNPLoaderExceptions
     * @has a hashmap of predefined SNPLoaderExceptions stored by a name key
     * @does looks up SNPLoaderExceptions by name
     * @company The Jackson Laboratory
     * @author sc
     */

public class SNPLoaderExceptionFactory
    extends ExceptionFactory {

    public SNPLoaderExceptionFactory() {
    }

    /**
     * Coordinate file IOException
     */
    public static final String CoordFileIOErr=
        "org.jax.mgi.shr.dla.seqloader.RepeatFileIOException";
    static {
        exceptionsMap.put(CoordFileIOErr, new SNPLoaderException(
            "Coordinate file IOException", false));
    }

    /**
     * could not add qc dao object to stream
     */
    public static final String QCErr =
        "org.jax.mgi.shr.dla.seqloader.QCErr";
    static {
        exceptionsMap.put(QCErr, new SNPLoaderException(
            "Could not add a new qc item to the qc reporting table named ??",
            false));
    }

    /**
     * Error creating a MGI_SNP_Accession object
     */
    public static final String CreateMGISNPAccessionErr =
        "org.jax.mgi.shr.dla.seqloader.CreateMGISNPAccessionErr";
    static {
        exceptionsMap.put(CreateMGISNPAccessionErr, new SNPLoaderException(
            "Error creating MGI_SNP_AccessionDAO objects for ??",
            false));
    }

    /**
     * Database Error sending DBSNPNse object to stream
     */
    public static final String DBSNPNseSendToStreamErr =
        "org.jax.mgi.shr.dla.seqloader.DBSNPNseSendToStreamErr";
    static {
        exceptionsMap.put(DBSNPNseSendToStreamErr, new SNPLoaderException(
            "Database Error sending DBSNPNse object to stream for ??",
            false));
    }

    /**
     * Database Error deleting Accessions
     */
    public static final String SNPDeleteAccessionsErr =
        "org.jax.mgi.shr.dla.seqloader.SNPDeleteAccessionsErr";
    static {
        exceptionsMap.put(SNPDeleteAccessionsErr, new SNPLoaderException(
            "Database Error deleting SNP Accessions",
            false));
    }


        /**
         * Database Error deleting dbsnp Strain Set
         */
        public static final String SNPDeleteStrainSetErr =
            "org.jax.mgi.shr.dla.seqloader.SNPDeleteStrainSetErr";
        static {
            exceptionsMap.put(SNPDeleteStrainSetErr, new SNPLoaderException(
                "Database Error deleting SNP Strain Set",
                false));
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
