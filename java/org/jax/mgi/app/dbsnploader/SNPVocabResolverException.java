package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when cannot resolve a vocabulary term for a SNP
     * @has an exception message
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     */

    public class SNPVocabResolverException extends MGIException {
        public SNPVocabResolverException(String vocab) {
            super("Cannot resolve vocab " + vocab + " term ??", false);
        }
    }