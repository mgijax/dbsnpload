package org.jax.mgi.shr.dbutils;

import java.sql.SQLException;
import java.sql.ResultSetMetaData;

/**
 * A class for creating a string from a RowReference
 * @has a designated delimiter string for delimiting column output and
 * a ResultSetMetaData object for obtaining RowReference data
 * @does creates a String from a given RowReference
 */
public class RowToStringInterpreter
    implements RowDataInterpreter {
    private String delimiter = null;
    private String JDBCException = DBExceptionFactory.JDBCException;
    public RowToStringInterpreter() {
        delimiter = "\t";
    }
    public RowToStringInterpreter(String pDelimiter) {
        delimiter = pDelimiter;
    }

    public Object interpret(RowReference rowReference) throws DBException {
        ResultSetMetaData metadata = rowReference.getMetaData();
        StringBuffer s = new StringBuffer();
        if (delimiter == null)
            delimiter = "\t";
        if (metadata == null)
            throw new DBException("ResultSet metadata was null", false);
        try {
            int numColumns = metadata.getColumnCount();
            if (numColumns == 0)
                return new String(s);
            Object object = rowReference.getObject(1);
            s.append(object == null ? "null" : object.toString());
            for (int i = 2; i < numColumns + 1; i++) {
                s.append(delimiter);
                object = rowReference.getObject(i);
                s.append(object == null ? "null" : object.toString());
            }
        }
        catch (SQLException e) {
            DBExceptionFactory eFactory = new DBExceptionFactory();
            DBException e2 = (DBException)
                eFactory.getException(JDBCException, e);
            e2.bind("access RowReference columns for String creation");
            return e2;
        }
        return new String(s);
    }
}