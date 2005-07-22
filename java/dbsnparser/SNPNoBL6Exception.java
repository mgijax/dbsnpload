// $Header
// $Name

package dbsnparser;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when a sequences raw organism in the database
     *     does not match the incoming raw organism
     * @has an exception message, a data related indicator and a parent
     * exception which can be null.
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     * @version 1.0
     */

    public class SNPNoBL6Exception extends MGIException {
        public SNPNoBL6Exception() {
            super("No C57BL/6J coordinates for ??", false);
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
