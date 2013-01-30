package org.jax.mgi.shr.dbutils.bcp;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for BCPExceptions.
 * @has a hashmap of predefined BCPExceptions stored by a name key
 * @does looks up BCPExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class BCPExceptionFactory extends ExceptionFactory {

  /**
   * An error occurred while trying to create a temp file for bcp
   */
  public static final String TempFileCreateErr =
      "org.jax.mgi.shr.dbutils.bcp.TempFileCreateErr";
  static {
    exceptionsMap.put(TempFileCreateErr, new BCPException(
        "Could not create a temp file for bcp", false));
  }
  /**
   * An interrupt signal was received during the execution of bcp
   */
  public static final String NonZeroErr =
      "org.jax.mgi.shr.dbutils.bcp.NonZeroErr";
  static {
    exceptionsMap.put(NonZeroErr, new BCPException(
        "BCP process returned non-zero status when " +
        "executing the following command: ??", false));
  }
  /**
   * An interrupt signal was received during the execution of bcp
   */
  public static final String InterruptErr =
      "org.jax.mgi.shr.dbutils.bcp.InterruptErr";
  static {
    exceptionsMap.put(InterruptErr, new BCPException(
        "BCP process was interrupted while running the " +
        "following command: ??", false));
  }
  /**
   * The delimiter for bcp is not valid
   */
  public static final String InvalidDelimiter =
      "org.jax.mgi.shr.dbutils.bcp.InvalidDelimiter";
  static {
    exceptionsMap.put(InvalidDelimiter, new BCPException(
        "The String '??' is an invalid delimiter " +
        "for BCPManager", false));
  }
  /**
   * The load data sql failed
   */
  public static final String LoadDataErr =
      "org.jax.mgi.shr.dbutils.bcp.LoadDataErr";
  static {
    exceptionsMap.put(LoadDataErr, new BCPException(
        "The Load Data sql command failed", false));
  }

  /**
   * The bcp file could not be closed
   */
  public static final String FileCloseErr =
      "org.jax.mgi.shr.dbutils.bcp.FileCloseErr";
  static {
    exceptionsMap.put(FileCloseErr, new BCPException(
        "Could not close file: ??", false));
  }
  /**
   * Error writing to a bcp file
   */
  public static final String FileWriteErr =
      "org.jax.mgi.shr.dbutils.bcp.FileWriteErr";
  static {
    exceptionsMap.put(FileWriteErr, new BCPException(
        "Error writing to file ??", false));
  }
  /**
   * Error creating or opening a bcp file
   */
  public static final String FileCreateErr =
      "org.jax.mgi.shr.dbutils.bcp.FileCreateErr";
  static {
    exceptionsMap.put(FileCreateErr, new BCPException(
        "Could not create or open file ??", false));
  }
  /**
   * Error creating or opening a bcp file
   */
  public static final String IOErr =
      "org.jax.mgi.shr.dbutils.bcp.IOErr";
  static {
    exceptionsMap.put(IOErr, new BCPException(
        "The RunCommand through an IO error occurred " +
        "while executing the following command: ??", false));
  }
  /**
   * Converting object to string when writing to the bcp file
   */
  public static final String StringConversionErr =
      "org.jax.mgi.shr.dbutils.bcp.StringConversionErr";
  static {
    exceptionsMap.put(StringConversionErr, new BCPException(
        "Could not convert object to String", true));
  }

  /**
   * the bcp writer has already been executed and the write() method
   * was called again
   */
  public static final String InvalidBCPWriter =
      "org.jax.mgi.shr.dbutils.bcp.InvalidBCPWriter";
  static {
    exceptionsMap.put(InvalidBCPWriter, new BCPException(
        "The BCPWriter has been executed. It is no longer valid to " +
        "call the write() method", false));
  }

  /**
   * there was a mixture of Vectors and scalars found within the BCP Vector
   */
  public static final String InvalidBCPVector =
      "org.jax.mgi.shr.dbutils.bcp.InvalidBCPVector";
  static {
    exceptionsMap.put(InvalidBCPVector, new BCPException(
        "The BCP Vector contained a mix of vectors and scalars. " +
        "Only one or the other is acceptable", false));
  }

  /**
   * the given table is not supported by this class
   */
  public static final String NonSupportedTable =
      "org.jax.mgi.shr.dbutils.bcp.NonSupportedTable";
  static {
    exceptionsMap.put(NonSupportedTable, new BCPException(
        "The table named ? is not supported by the class named ?", false));
  }

  /**
   * the user could not be found in the MGI_USER table
   */
  public static final String UserNotFound =
      "org.jax.mgi.shr.dbutils.bcp.UserNotFound";
  static {
    exceptionsMap.put(UserNotFound, new BCPException(
        "Could not complete automatic record stamping due" +
        "to unknown user ?? in the database", false));
  }

  /**
   * the job key could not be found in the configuration
   */
  public static final String JobKeyNotFound =
      "org.jax.mgi.shr.dbutils.bcp.JobKeyNotFound";
  static {
    exceptionsMap.put(JobKeyNotFound, new BCPException(
        "The JOBKEY configuration parameter could not be found", false));
  }

  /**
   * there was a failure when trying to auto-record-stamp
   */
  public static final String RecordStampErr =
      "org.jax.mgi.shr.dbutils.bcp.RecordStampErr";
  static {
    exceptionsMap.put(RecordStampErr, new BCPException(
        "There was a failure when trying to auto record stamp " +
        "for table ??", false));
  }

  /**
   * there was a failure when trying to create a BCP Vector
   */
  public static final String BCPVectorErr =
      "org.jax.mgi.shr.dbutils.bcp.BCPVectorErr";
  static {
    exceptionsMap.put(BCPVectorErr, new BCPException(
        "There was a failure when trying to create a bcp vector", false));
  }




}