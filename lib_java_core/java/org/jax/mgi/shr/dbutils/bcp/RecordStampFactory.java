package org.jax.mgi.shr.dbutils.bcp;

import java.util.Iterator;
import java.util.Vector;

import org.jax.mgi.shr.dbutils.Table;
import org.jax.mgi.shr.dbutils.ColumnDef;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.bcp.RecordStamper_RADAR;
import org.jax.mgi.shr.dbutils.bcp.RecordStamper_None;

/**
 *
 * A factory class for obtaining a RecordStamp class for a given table name.
 * @has the knowledge of all RecordStamp classes and their associations
 * to the various time stamping formats found throught the database
 * @does provides the appropriate RecordStamp class for a given table name.
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */
public class RecordStampFactory {
  private static RecordStamper mgd = new RecordStamper_MGD();
  private static RecordStamper mgdDate = new RecordStamper_MGDDate();
  private static RecordStamper mgdOrg = new RecordStamper_MGDOrg();
  private static RecordStamper mgdRel = new RecordStamper_MGDRelease();
  private static RecordStamper mgdRadar = new RecordStamper_RADAR();
  private static RecordStamper none = new RecordStamper_None();

  /**
   * look at the column definitions for this table and calculate which
   * RecordStamp class to use
   * @assumes nothing
   * @effects nothing
   * @param table the table to use for obtaining a RecordStamper
   * @return the RecordStamp class
   * @throws DBException thrown if there is an error accessing the database
   */
  public static RecordStamper getRecordStamp(Table table)
      throws DBException {
    RecordStamper stamp = null;
    /**
     * see if there is a column named _CreatedBy_key.
     * if so, use RecordStamp_MGD
     */
    if (hasColumnName(table, "_CreatedBy_Key"))
      return mgd;
    /**
     * see if there is a column named _JobStream_key.
     * if so, use RecordStamp_RADAR
     */
    if (hasColumnName(table, "_JobStream_key"))
      return mgdRadar;
    /**
     * see if there is a column named _JobStream_key.
     * if so, use RecordStamp_MGDOrg
     */
    if (hasColumnName(table, "createdBy"))
      return mgdOrg;
    /**
     * see if there is a column named _JobStream_key.
     * if so, use RecordStamp_MGDRelease
     */
    if (hasColumnName(table, "release_date"))
      return mgdRel;
    /**
     * see if there is a column named _JobStream_key.
     * if so, use RecordStamp_MGDDate
     */
    if (hasColumnName(table, "creation_date"))
      return mgdDate;
    /**
     * no RecordStamp found return RecordStamp_None
     */
    return new RecordStamper_None();
  }

  /**
   * search through the column definitions and see if there is a column with
   * the given name
   * @param table the table to use
   * @param name the given column name to search on
   * @return true if there is a column with that name, false otherwise
   * @throws DBException thrown if there is an error accessing the database
   */
  private static boolean hasColumnName(Table table, String name) throws
      DBException {
    Iterator i = ( (Vector) (table.getColumnDefinitions())).iterator();
    while (i.hasNext()) {
      ColumnDef c = (ColumnDef) i.next();
      if (c.getName().toUpperCase().equals(name.toUpperCase()))
        return true;
    }
    return false;
  }
}