package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

    /**
     * An MGIException thrown when a consensus snp allele summary is empty
     * @has an exception message
     * @does nothing
     * @company Jackson Laboratory
     * @author sc
     */

    public class SNPNoConsensusAlleleSummaryException extends MGIException {
        public SNPNoConsensusAlleleSummaryException() {
            super("No ConsensusAlleleSummary for RS ??", false);
        }
    }
