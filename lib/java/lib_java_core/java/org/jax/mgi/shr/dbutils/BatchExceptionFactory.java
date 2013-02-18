package org.jax.mgi.shr.dbutils;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for JDBC batch processing errors.
 * @has a hashmap of predefined BatchExceptions stored by a name key
 * @does looks up BCPExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class BatchExceptionFactory extends ExceptionFactory {

  /**
   * One of the batch commands failed
   */
  public static final String BatchCommandFailed =
      "org.jax.mgi.shr.batch.BatchCommandFailed";
  static {
    exceptionsMap.put(BatchCommandFailed, new BatchException(
        "One of the batch commands failed", false));
  }

  /**
   * The batch has been executed
   */
  public static final String BatchComplete =
      "org.jax.mgi.shr.batch.BatchComplete";
  static {
    exceptionsMap.put(BatchComplete, new BatchException(
        "No more operations allowed after executing the batch.", false));
  }

  /**
   * Could not add statements to batch.
   */
  public static final String AddBatchErr =
      "org.jax.mgi.shr.batch.AddBatchErr";
  static {
    exceptionsMap.put(AddBatchErr, new BatchException(
        "Could not add the following sql to batch: ??", false));
  }

  /**
   * Could not close BatchProcessor.
   */
  public static final String CloseErr =
      "org.jax.mgi.shr.batch.CloseErr";
  static {
    exceptionsMap.put(CloseErr, new BatchException(
        "Could not close BatchProcessor", false));
  }


  /**
   * This JDBC driver does not support batch operations.
   */
  public static final String BatchNotSupported =
      "org.jax.mgi.shr.batch.BatchNotSupported";
  static {
    exceptionsMap.put(BatchNotSupported, new BatchException(
        "This JDBC driver does not support batch operations", false));
  }

  /**
   * There were errors accessing the given file of sql commands.
   */
  public static final String FileIO =
      "org.jax.mgi.shr.batch.FileIO";
  static {
    exceptionsMap.put(FileIO, new BatchException(
        "There were errors accessing the file ??.", false));
  }





}
