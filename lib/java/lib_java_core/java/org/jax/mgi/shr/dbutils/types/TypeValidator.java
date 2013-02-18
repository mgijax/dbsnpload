package org.jax.mgi.shr.dbutils.types;

import java.sql.Timestamp;

import org.jax.mgi.shr.dbutils.ColumnDef;
import org.jax.mgi.shr.dbutils.DataException;
import org.jax.mgi.shr.dbutils.DataExceptionFactory;
import org.jax.mgi.shr.dbutils.DBException;
import org.jax.mgi.shr.dbutils.DBExceptionFactory;
import org.jax.mgi.shr.dbutils.DBTypeConstants;

/**
 *  An abstract class which provides general base level methods for
 * validation of given objects against column meta data. The
 * base class provides the validateObject() method which checks the
 * type of object given and calls the appropriate validate method for
 * the given type. The base class also provides exception handling methods.
 * The sub class will have to implement the validate methods for the
 * different data types. The supported data types validated are String,
 * Integer, Double, Boolean and Timestamp. Each subclass represents a Sybase
 * column type. There is one for each allowed Sybase data type: Varchar,
 * Char, Bit, Double, Integer, Timestamp, Longvarchar.
 * @has column meta data.
 * @does validates the given java data type against the column meta data
 * to see if the data type is valid for use as a column value.
 * @company Jackson Laboratory
 * @author M Walker
 */
public abstract class TypeValidator
{

    /**
     * the columnDef object this instance was created to refer to
     */
    protected ColumnDef columnDef = null;
    /*
    * the execption factory for storing and retrieving DataExceptions
    */
    protected DataExceptionFactory dataExceptionFactory =
        new DataExceptionFactory();
    /*
    * the execption factory for storing and retrieving DBExceptions
    */
    protected DBExceptionFactory dbExceptionFactory = new DBExceptionFactory();

    /**
    * exceptions thrown by this class
    */
    protected static final String ColumnOverflow =
        DataExceptionFactory.ColumnOverflow;
    protected static final String NotNullable = DataExceptionFactory.NotNullable;
    protected static final String ConversionErr =
        DataExceptionFactory.ConversionErr;
    protected static final String BitValueErr = DataExceptionFactory.BitValueErr;
    protected static final String UnhandledDataType =
        DataExceptionFactory.UnhandledDataType;
    protected static final String UnhandledConversion =
        DataExceptionFactory.UnhandledConversion;
    protected static final String UnexpectedKeyType =
        DBExceptionFactory.UnexpectedKeyType;
    protected static final String UnhandledSybaseType =
        DBExceptionFactory.UnhandledSybaseType;

    /**
    * factor method
    * @param columnDef the given ColumnDefinition object
    * @return a new instance of a TypeValidator for the given column
    * definition
    */
    public static TypeValidator newInstance(ColumnDef columnDef)
    {
        TypeValidator validator = null;
        switch (columnDef.getType())
        {
            case DBTypeConstants.DB_CHAR :
                validator = new CharValidator(columnDef);
                break;
            case DBTypeConstants.DB_VARCHAR :
                validator = new VarcharValidator(columnDef);
                break;
            case DBTypeConstants.DB_INTEGER :
                validator = new IntegerValidator(columnDef);
                break;
            case DBTypeConstants.DB_DATETIME :
                validator = new TimestampValidator(columnDef);
                break;
            case DBTypeConstants.DB_TEXT :
                validator = new LongvarcharValidator(columnDef);
                break;
            case DBTypeConstants.DB_DOUBLE :
                validator = new DoubleValidator(columnDef);
                break;
            case DBTypeConstants.DB_BIT :
		case 5:
                validator = new BitValidator(columnDef);
                break;
            default :
                DBExceptionFactory dbExceptionFactory = new DBExceptionFactory();
                DBException e =
                    (DBException) dbExceptionFactory.getException(UnhandledSybaseType);
                e.bind(columnDef.getType());
        }
        return validator;
    }

    /**
     * validates an object by figuring the type of the object and calling the
     * appropriate validate method for the given object type.
     * @assumes nothing
     * @effects an exception will be raised if the given object is not
     * valid
     * @param object the given object to validate
     * @return true if object validates successful. Otherwise a
     * DataException will be raised.
     * @throws DataException
     */
    public boolean validate(Object object) throws DataException
    {
        if (!columnDef.isNullable() && object == null)
        {
            failNullValue();
        }
        if (object == null)
            return true;
        if (object instanceof String)
            validateString( (String) object);
        else
        if (object instanceof Integer)
            validateInteger( (Integer) object);
        else
        if (object instanceof Double)
            validateDouble( (Double) object);
        else
        if (object instanceof Boolean)
            validateBoolean( (Boolean) object);
        else
        if (object instanceof Timestamp)
            validateTimestamp( (Timestamp) object);
        else
        {
            DataException e =
                (DataException) dataExceptionFactory.getException(
                UnhandledDataType);
            e.bind(object.getClass().getName());
            throw e;
        }
        return true;
    }

    /**
     * abstract method for validating a String
     * @param s the given String
     * @throws DataException
     */
    public abstract void validateString(String s) throws DataException;

    /**
     * abstract method for validating a Integer
     * @param i the given Integer
     * @throws DataException thrown if a validation error occurs
     */
    public abstract void validateInteger(Integer i) throws DataException;

    /**
     * abstract method for validating a Double
     * @param d the given Double
     * @throws DataException thrown if a validation error occurs
     */
    public abstract void validateDouble(Double d) throws DataException;

    /**
     * abstract method for validating a Boolean
     * @param b the given Boolean
     * @throws DataException thrown if a validation error occurs
     */
    public abstract void validateBoolean(Boolean b) throws DataException;

    /**
     * abstract method for validating a Timestamp
     * @param t the given Timestamp
     * @throws DataException thrown if a validation error occurs
     */
    public abstract void validateTimestamp(Timestamp t) throws DataException;

    /**
     * exception handler method for handling the case where the given String
     * is too large for the column
     * @param s the given string object
     * @throws DataException
     */
    protected void failStringSize(String s) throws DataException
    {
        DataException e =
            (DataException) dataExceptionFactory.getException(ColumnOverflow);
        e.bind(s);
        e.bind(columnDef.getSize());
        throw e;
    }

    /**
     * exception handler method for handling the case where the conversion of
     * one data type to another is not supported.
     * @param from the name of the data type from which a conversion was
     * attempted
     * @param to the name of the data type to which a conversion was attempted
     * @throws DataException
     */
    protected void failUnhandledConversion(String from, String to)
        throws DataException
    {
        DataException e =
            (DataException) dataExceptionFactory.getException(UnhandledConversion);
        e.bind(from);
        e.bind(to);
        throw e;
    }

    /**
     * exception handler method for handling the case where a null was given
     * for a column that does not accept nulls.
     * @throws DataException
     */
    protected void failNullValue() throws DataException
    {
        DataException e =
            (DataException) dataExceptionFactory.getException(NotNullable);
        e.bind(columnDef.getName());
        throw e;
    }

    /**
     * exception handler method for handling the case where a conversion from
     * a String to another data type failed
     * @param s the given String on which a conversion was attempted
     * @param type the type to which the String conversion was attempted
     * @throws DataException
     */
    protected void failTypeConversion(String s, String type) throws DataException
    {
        DataException e =
            (DataException) dataExceptionFactory.getException(ConversionErr);
        e.bind(s);
        e.bind(type);
        throw e;
    }

    /**
    * default constructor which accepts the ColumnDefinition object for the
    * represented column definition
    * @param c the given ColumnDefinition
    */
    protected TypeValidator(ColumnDef c)
    {
        columnDef = c;
    }

}
