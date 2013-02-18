package org.jax.mgi.shr.dbutils;

import java.util.Vector;

import java.sql.SQLException;
import java.sql.ResultSetMetaData;

/**
 * A class for creating a DTO object from a RowReference
 * @has a ResultSetMetaData object for obtaining row meta data
 * @does creates a DTO from a given RowReference
 */
public class RowToVectorInterpreter
    implements RowDataInterpreter {
  private ResultSetMetaData metadata = null;
  private String JDBCException = DBExceptionFactory.JDBCException;

  public RowToVectorInterpreter() {}

  public RowToVectorInterpreter(ResultSetMetaData pMetadata) {
    metadata = pMetadata;
  }

  public Object interpret(RowReference rowReference) throws DBException {
      if (metadata == null)
          metadata = rowReference.getMetaData();
    Vector v = new Vector();
    if (metadata == null)
      throw new DBException("ResultSet metadata was null", false);
    try {
      int numColumns = metadata.getColumnCount();
      for (int i = 1; i < numColumns + 1; i++)
          v.add(rowReference.getObject(i));
    }
    catch (SQLException e) {
      DBExceptionFactory eFactory = new DBExceptionFactory();
      DBException e2 = (DBException)
          eFactory.getException(JDBCException, e);
      e2.bind("access RowReference columns for DTO creation");
      return e2;
    }
    return v;
  }
}
