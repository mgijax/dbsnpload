package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.ExceptionFactory;
    /**
     * An ExceptionFactory for SNPLoaderExceptions
     * @has a hashmap of predefined SNPLoaderExceptions stored by a name key
     * @does looks up SNPLoaderExceptions by name
     * @company The Jackson Laboratory
     * @author sc
     */

public class SNPLoaderExceptionFactory
    extends ExceptionFactory {

    public SNPLoaderExceptionFactory() {
    }

     /**
     * Database Error deleting Accessions
     */
    public static final String SNPDeleteAccessionsErr =
        "org.jax.mgi.shr.dla.seqloader.SNPDeleteAccessionsErr";
    static {
        exceptionsMap.put(SNPDeleteAccessionsErr, new SNPLoaderException(
            "Database Error deleting SNP Accessions",
            false));
    }
}
