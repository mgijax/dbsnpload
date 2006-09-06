package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which represents exceptions occuring while processing
 * snps
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author sc
 */

public class SNPLoaderException extends MGIException {
  public SNPLoaderException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }
}
