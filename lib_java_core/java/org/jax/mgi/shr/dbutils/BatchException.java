package org.jax.mgi.shr.dbutils;

import java.sql.Statement;

import org.jax.mgi.shr.exception.MGIException;

/**
 * An MGIException which represents an error in jdbc batch processing
 * @has an exception message, a data related indicator and a parent
 * exception which can be null.
 * @does nothing
 * @author M Walker
 * @company The Jackson Laboratory
 */

public class BatchException extends MGIException {

  private int [] updateCounts;

  public static final int UNAVAILABLE = Statement.SUCCESS_NO_INFO;
  public static final int ERROR = Statement.EXECUTE_FAILED;

  public BatchException(String pMessage, boolean pDataRelated) {
    super(pMessage, pDataRelated);
  }

  public int [] getUpdateCounts()
  {
    return updateCounts;
  }

  public void setUpdateCounts(int [] counts)
  {
    updateCounts = counts;
  }
}
