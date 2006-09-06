package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when a SNP has no C57BL/6J coordinates
     * @has an exception message
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