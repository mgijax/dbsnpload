package org.jax.mgi.shr.dbutils;

import org.jax.mgi.shr.exception.ExceptionFactory;

/**
 * An ExceptionFactory for errors related to the use of the DBSchema product.
 * @has a hashmap of predefined DBSchemaExceptions stored by a name key
 * @does looks up DBSchemaExceptions by name
 * @company The Jackson Laboratory
 * @author M Walker
 */

public class DBSchemaExceptionFactory extends ExceptionFactory {
  /**
   * Could not obtain ddl statements from dbSchema file
   */
  public static final String FileNotFoundErr =
      "org.jax.mgi.shr.dbschema.FileNotFoundErr";
  static {
    exceptionsMap.put(FileNotFoundErr, new DBSchemaException(
        "DBSchema file ?? does not exist", false));
  }
  /**
   * Could not obtain ddl statements from dbSchema file
   */
  public static final String FileReadErr =
      "org.jax.mgi.shr.dbschema.FileReadErr";
  static {
    exceptionsMap.put(FileReadErr, new DBSchemaException(
        "Error reading from file ??", false));
  }
  /**
   * Could not obtain ddl statements from dbSchema file
   */
  public static final String FileCloseErr =
      "org.jax.mgi.shr.dbschema.FileCloseErr";
  static {
    exceptionsMap.put(FileCloseErr, new DBSchemaException(
        "Error trying to close file ??", false));
  }
  /**
   * Could not obtain ddl statements from dbSchema file
   */
  public static final String NoRegexMatch =
      "org.jax.mgi.shr.dbschema.NoRegexMatch";
  static {
    exceptionsMap.put(NoRegexMatch, new DBSchemaException(
        "Could not locate regular expression ?? within " +
        "file ??", false));
  }
  /**
   * Could not obtain ddl statements from dbSchema file
   */
  public static final String UnexpectedString =
      "org.jax.mgi.shr.dbschema.UnexpectedString";
  static {
    exceptionsMap.put(UnexpectedString, new DBSchemaException(
        "File search returned unexpected string value " +
        "'??' from file ??. Could be an internal bug", false));
  }
  /**
   * Could not process line with nested quotation marks
   */
  public static final String NestedQuotesErr =
      "org.jax.mgi.shr.dbschema.NestedQuotesErr";
  static {
    exceptionsMap.put(NestedQuotesErr, new DBSchemaException(
        "An error occured while processing create ?? on table ??. " +
        "The following text could not be processed due to nested " +
        "quotation marks. To avoid this error, create nested quotations " +
        "using two single quotes instead of one and create encompassing " +
        "quotations using single quotes: \n??", false));
  }



}