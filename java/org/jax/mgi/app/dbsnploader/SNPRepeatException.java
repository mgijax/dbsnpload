// $Header
// $Name

package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when there are repeated SNPs in the input
     * @has an exception message
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     */

    public class SNPRepeatException extends MGIException {
        public SNPRepeatException() {
            super("SNP Repeated in input ??", false);
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
