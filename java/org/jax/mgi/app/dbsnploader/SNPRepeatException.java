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