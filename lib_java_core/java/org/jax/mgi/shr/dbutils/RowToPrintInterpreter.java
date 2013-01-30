package org.jax.mgi.shr.dbutils;

import java.util.*;
import java.sql.*;
import org.jax.mgi.shr.stringutil.Sprintf;

/**
 * A class for creating a formatted string from a RowReference
 * @has a designated delimiter string for delimiting column output and
 * a ResultSetMetaData object for obtaining RowReference data
 * @does creates a String from a given RowReference utilizing the given Sprint
 * formatting string
 */


public class RowToPrintInterpreter implements RowDataInterpreter
{
    RowToVectorInterpreter row2vec = new RowToVectorInterpreter();
    String pattern = null;

    /*
     * the following constant definitions are exceptions thrown by this class
     */
    private static String FormatNullErr =
        DBExceptionFactory.FormatNullErr;
    private static String RSMetaDataErr =
        DBExceptionFactory.RSMetaDataErr;


    /**
     * constructor
     * @param pattern the Sprintf pattern
     */
    public RowToPrintInterpreter(String pattern)
    {
        this.pattern = pattern;
    }
    public Object interpret(RowReference row) throws DBException {
        Vector v = (Vector)row2vec.interpret(row);
        // check for nulls and throw error if one found
        for (int i = 0; i < v.size(); i++)
        {
            if (v.get(i) == null)
            {
                DBExceptionFactory efactory = new DBExceptionFactory();
                DBException e = (DBException)
                    efactory.getException(FormatNullErr);
                ResultSetMetaData metadata = row.getMetaData();
                try
                {
                    e.bind(metadata.getColumnName(i + 1));
                }
                catch (SQLException sqlerr)
                {
                    DBException e2 = (DBException)
                        efactory.getException(RSMetaDataErr, e);
                    e.bind("UNKNOWN");
                    throw e2;
                }
                throw e;
            }
        }
        return Sprintf.sprintf(pattern, v);
    }
}
