package org.jax.mgi.shr.dbutils.types;

import java.sql.Timestamp;

import org.jax.mgi.shr.dbutils.ColumnDef;
import org.jax.mgi.shr.dbutils.DataException;

/**
 *  A TypeValidator object for validating objects against a
 * column definition of type longvarchar
 * @has a column definition for type longvarchar and methods for
 * validating a given object against the represented column definition.
 * @does validates objects to be converted to longvarchar
 * if validation fails.
 * @company Jackson Laboratory
 * @author M Walker
 */
public class LongvarcharValidator extends TypeValidator
{

    /**
     * default constructor which accepts the ColumnDefinition object for the
     * represented column definition
     * @param cd the given ColumnDefinition
     */
    protected LongvarcharValidator(ColumnDef cd)
    {
        super(cd);
    }

    /**
     * validates a String object against the column definition for
     * longvarchar
     * @param s the given String
     * @throws DataException
     */
    public void validateString(String s) throws DataException
    {
        if (!columnDef.isNullable() && s.equals(""))
        {
            failNullValue();
        }
    }

    /**
     * validates a Integer object against the column definition for
     * longvarchar
     * @param i the given Integer
     * @throws DataException thrown if a validation error occurs
     */
    public void validateInteger(Integer i) throws DataException
    {}

    /**
     * validates a Double object against the column definition for
     * longvarchar
     * @param d the given Double
     * @throws DataException thrown if a validation error occurs
     */
    public void validateDouble(Double d) throws DataException
    {}

    /**
     * validates a Boolean object against the column definition for
     * longvarchar
     * @param b the given Boolean
     * @throws DataException thrown if a validation error occurs
     */
    public void validateBoolean(Boolean b) throws DataException
    {}

    /**
     * validates a Timestamp object against the column definition for
     * longvarchar
     * @param t the given Timestamp
     * @throws DataException thrown if a validation error occurs
     */
    public void validateTimestamp(Timestamp t) throws DataException
    {}

}
