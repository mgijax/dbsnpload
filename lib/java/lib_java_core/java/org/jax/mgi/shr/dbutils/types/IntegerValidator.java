package org.jax.mgi.shr.dbutils.types;

import java.sql.Timestamp;

import org.jax.mgi.shr.dbutils.ColumnDef;
import org.jax.mgi.shr.dbutils.DataException;
import org.jax.mgi.shr.types.Converter;
import org.jax.mgi.shr.types.TypesException;

/**
*  A TypeValidator object for validating objects against a
* column definition of type integer
* @has a column definition for type integer and methods for validating
* a given object against the represented column definition.
* @does validates objects to be converted to integers
* if validation fails.
* @company Jackson Laboratory
* @author M Walker
*/
public class IntegerValidator extends TypeValidator
{

    /**
    * default constructor which accepts the ColumnDefinition object for the
    * represented column definition
    * @param cd the given ColumnDefinition
    */
    protected IntegerValidator(ColumnDef cd)
    {
        super(cd);
    }

    /**
    * validates a String object against the column definition for integer
    * @param s the given String
    * @throws DataException thrown if a validation error occurs
    */
    public void validateString(String s) throws DataException
    {
        if (!columnDef.isNullable() && s.equals(""))
            failNullValue();
        try
        {
            Integer i = Converter.toInteger(s);
        }
        catch (TypesException e)
        {
            failTypeConversion(s, "Integer");
        }
    }

    /**
    * validates an Integer object against the column definition for varchar
    * @param i the given Integer
    * @throws DataException thrown if a validation error occurs
    */
    public void validateInteger(Integer i) throws DataException
    {}

    /**
     * validates a Double object against the column definition for varchar
     * @param d the given Double
     * @throws DataException thrown if a validation error occurs
     */
    public void validateDouble(Double d) throws DataException
    {
        failUnhandledConversion("double", "integer");
    }

    /**
     * validates a Boolean object against the column definition for varchar
     * @param b the given Boolean
     * @throws DataException thrown if a validation error occurs
     */
    public void validateBoolean(Boolean b) throws DataException
    {
        failUnhandledConversion("boolean", "integer");
    }

    /**
     * validates a Timestamp object against the column definition for varchar
     * @param t the given Timestamp
     * @throws DataException thrown if a validation error occurs
     */
    public void validateTimestamp(Timestamp t) throws DataException
    {
        failUnhandledConversion("timestamp", "integer");
    }
}
