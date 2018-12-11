package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when a C57BL/6J SNP is on multiple chromosomes
     * @has an exception message
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     */

    public class SNPMultiBL6ChrException extends MGIException {
        public SNPMultiBL6ChrException() {
            super("Multiple BL6 chromosomes for ??", false);
        }
    }