package org.jax.mgi.shr.dbutils.types;

import java.sql.Timestamp;

import org.jax.mgi.shr.dbutils.ColumnDef;
import org.jax.mgi.shr.dbutils.DataException;
import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.shr.types.TypesException;

/**
 *  A TypeValidator object for validating objects against a
 * column definition of type timestamp
 * @has a column definition for type timestamp and methods for validating
 * a given object against the represented column definition.
 * @does validates the given object
 * @company Jackson Laboratory
 * @author M Walker
 */
public class TimestampValidator extends TypeValidator
{

    /**
     * default constructor which accepts the ColumnDefinition object for the
     * represented column definition
     * @param cd the given ColumnDefinition
     */
    protected TimestampValidator(ColumnDef cd)
    {
        super(cd);
    }

    /**
     * validates a String object against the column definition for timestamp
     * @param s the given String
     * @throws DataException
     */
    public void validateString(String s) throws DataException
    {
        if (!columnDef.isNullable() && s.equals(""))
            failNullValue();
        try
        {
            Timestamp t = Converter.toTimestamp(s);
        }
        catch (TypesException e)
        {
            failTypeConversion(s, "timestamp");
        }
    }

    /**
     * validates a Integer object against the column definition for timestamp
     * @param i the given Integer
     * @throws DataException thrown if a validation error occurs
     */
    public void validateInteger(Integer i) throws DataException
    {
        failUnhandledConversion("integer", "timestamp");
    }

    /**
     * validates a Double object against the column definition for timestamp
     * @param d the given Double
     * @throws DataException thrown if a validation error occurs
     */
    public void validateDouble(Double d) throws DataException
    {
        failUnhandledConversion("double", "timestamp");
    }

    /**
     * validates a Boolean object against the column definition for timestamp
     * @param b the given Boolean
     * @throws DataException thrown if a validation error occurs
     */
    public void validateBoolean(Boolean b) throws DataException
    {
        failUnhandledConversion("boolean", "timestamp");
    }

    /**
     * validates a Timestamp object against the column definition for
     * timestamp
     * @param t the given Timestamp
     * @throws DataException thrown if a validation error occurs
     */
    public void validateTimestamp(Timestamp t) throws DataException
    {}

}