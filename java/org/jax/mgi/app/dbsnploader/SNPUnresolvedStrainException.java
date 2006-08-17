package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when a strain cannot be resolved
     * @has an exception message
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     */

    public class SNPUnresolvedStrainException extends MGIException {
        public SNPUnresolvedStrainException() {
            super("Can't resolve strain ??", false);
        }
    }