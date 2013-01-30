package org.jax.mgi.shr.dbutils.types;

import java.sql.Timestamp;

import org.jax.mgi.shr.dbutils.ColumnDef;
import org.jax.mgi.shr.dbutils.DataException;


/**
 *  A TypeValidator object for validating objects against a
 * column definition of type bit
 * @has a column definition for type bit and methods for validating
 * a given object against the represented column definition.
 * @does validates objects to be converted to bit
 * @company Jackson Laboratory
 * @author M Walker
 */

public class BitValidator extends TypeValidator
{

    /**
     * default constructor which accepts the ColumnDefinition object for the
     * represented column definition
     * @param cd the given ColumnDefinition
     */
    protected BitValidator(ColumnDef cd)
    {
        super(cd);
    }

    /**
     * validates a String object against the column definition for bit
     * @param s the given String
     * @throws DataException
     */
    public void validateString(String s) throws DataException
    {
        if (!columnDef.isNullable() && s.equals(""))
            failNullValue();
        if (!(s.equals("1") || s.equals("0")))
        {
            DataException e =
                (DataException) dataExceptionFactory.getException(BitValueErr);
            e.bind(s);
            throw e;
        }
    }

    /**
     * validates a Integer object against the column definition for bit
     * @param i the given Integer
     * @throws DataException thrown if a validation error occurs
     */
    public void validateInteger(Integer i) throws DataException
    {
        failUnhandledConversion("integer", "bit");
    }

    /**
     * validates a Double object against the column definition for bit
     * @param d the given Double
     * @throws DataException thrown if a validation error occurs
     */
    public void validateDouble(Double d) throws DataException
    {
        failUnhandledConversion("double", "bit");
    }

    /**
     * validates a Boolean object against the column definition for bit
     * @param b the given Boolean
     * @throws DataException thrown if a validation error occurs
     */
    public void validateBoolean(Boolean b) throws DataException
    {}

    /**
     * validates a Timestamp object against the column definition for bit
     * @param t the given Timestamp
     * @throws DataException thrown if a validation error occurs
     */
    public void validateTimestamp(Timestamp t) throws DataException
    {
        failUnhandledConversion("timestamp", "bit");
    }
}
