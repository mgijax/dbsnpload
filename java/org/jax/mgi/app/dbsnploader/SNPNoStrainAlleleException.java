package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when there are no strain alleles
     * file for this SNP
     * @has an exception message
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     */

    public class SNPNoStrainAlleleException extends MGIException {
        public SNPNoStrainAlleleException() {
            super("No Strain Alleles for RS ??", false);
        }
    }