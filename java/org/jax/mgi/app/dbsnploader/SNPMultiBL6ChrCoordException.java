package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown a snp has too many chromosome coordinates
     * @has an exception message
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     * @version 1.0
     */

    public class SNPMultiBL6ChrCoordException extends MGIException {
        public SNPMultiBL6ChrCoordException() {
            super("Too many chromosome coordinates for  ??", false);
        }
    }
