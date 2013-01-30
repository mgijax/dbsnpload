package org.jax.mgi.shr.dbutils;

import java.io.PrintStream;

import org.jax.mgi.shr.dbutils.types.TypeValidator;

  /**
   * A column definition for a given database table
   * @has attributes associated with the definition of the column
   * @does provides accessors and modifiers for the attributes of a
   * column definition.
   * @company Jackson Laboratory
   * @author M Walker
   */

  public class ColumnDef implements Cloneable {
    String name = null;
    int type;
    String typeName = null;
    int size;
    int decimalSize;
    String nullable = null;
    String catalog = null;
    String schema = null;
    String table = null;
    TypeValidator validator = null;

    /**
     * clone method
     * @assumes nothing
     * @effects a new ColumnDef object will be created
     * @return the clone for this instance
     */
    public Object clone()
    {
        ColumnDef col = new ColumnDef();
        col.setType(this.getType());
        col.setCatalog(this.getCatalog());
        col.setDecimalSize(this.getDecimalSize());
        col.setName(this.getName());
        if (this.isNullable())
          col.setNullable("YES");
        else
          col.setNullable("NO");
        col.setSchema(this.getSchema());
        col.setSize(this.getSize());
        col.setTable(this.getTable());
        col.setTypeName(this.getTypeName());
        return col;
    }

    /**
     * set the value for the database catalog
     * @assumes nothing
     * @effects the internal catalog value will be set
     * @param s the name of the catalog
     */
    protected void setCatalog(String s) {
      catalog = s;
    }

    /**
     * set the value for the schema
     * @assumes nothing
     * @effects the internal schema value will be set
     * @param s the name of the schema
     */
    protected void setSchema(String s) {
     schema = s;
    }

    /**
     * set the value for the table
     * @assumes nothing
     * @effects the internal table value will be set
     * @param s the name of the table
     */
    protected void setTable(String s) {
      table = s;
    }

    /**
     * set the value for the column name
     * @assumes nothing
     * @effects the internal name value will be set
     * @param s the name of the column
     */
    protected void setName(String s) {
      name = s;
    }

    /**
     * set the value of the data type
     * @assumes nothing
     * @effects the internal typeName value will be set
     * @param s the name of th edata type
     */
    protected void setTypeName(String s) {
      typeName = s;
    }

                /**
                 * set the value of the data type
                 * @assumes nothing
                 * @effects the internal typeName value will be set
                 * @param i the name of the data type
                 */
                protected void setType(int i) {
                        type = i;
                }

    /**
     * set the value for the column size
     * @assumes nothing
     * @effects the internal size value will be set
     * @param i the column size
     */
    protected void setSize(int i) {
      size = i;
    }

    /**
     * set the value of the decimal size for the column
     * @assumes nothing
     * @effects the internal decimalSize value will be set
     * @param i the decimal size
     */
    protected void setDecimalSize(int i) {
      decimalSize = i;
    }

    /**
     * set whether the column allows null
     * @assumes nothing
     * @effects the internal isNullable value will be set
     * @param s the boolean indicator for whether the column accepts null.
     * In Sybase it is the string 'yes'
     */
    protected void setNullable(String s) {
      nullable = s;
    }

    /**
     * get the name of the column
     * @assumes nothing
     * @effects nothing
     * @return the column name
     */
    public String getName() {
      return name;
    }

    /**
     * get the sql data type from java.sql.Types
     * @assumes nothing
     * @effects nothing
     * @return the daya type as an integer
     */
    public int getType() {
      return type;
    }

                /**
                 * get the name of the type
                 * @assumes nothing
                 * @effects the internal type value will be set
                 * @return the daya type as an integer
                 */
                public String getTypeName() {
                        return typeName;
                }

    /**
     * get the size of the column
     * @assumes nothing
     * @effects nothing
     * @return the column size
     */
    public int getSize() {
      return size;
    }

    /**
     * get the decimal size of the column
     * @assumes nothing
     * @effects nothing
     * @return the column decimal size
     */
    public int getDecimalSize() {
      return decimalSize;
    }

    /**
     * get whether the column accepts null
     * @assumes nothing
     * @effects nothing
     * @return an indicator of whether the column accepts null
     */
    public boolean isNullable() {
      if (nullable.equals("YES"))
        return true;
      else
        return false;
    }

    /**
     * get the catalog name of the column
     * @assumes nothing
     * @effects nothing
     * @return the column catalog name
     */
    public String getCatalog() {
      return catalog;
    }

    /**
     * get the schema name
     * @assumes nothing
     * @effects nothing
     * @return the schema name
     */
    public String getSchema() {
      return schema;
    }

    /**
     * get the table name
     * @assumes nothing
     * @effects nothing
     * @return the table name
     */
    public String getTable() {
      return table;
    }

    /**
     * get the name of the java type by which this column is represented
     * @assumes the following mapping will be used:
     * <UL>
     *   <LI> DBTypeConstants.DB_CHAR maps to String
     *   <LI> DBTypeConstants.DB_VARCHAR maps to String
     *   <LI> DBTypeConstants.DB_TEXT maps to String
     *   <LI> DBTypeConstants.DB_INTEGER maps to Integer
     *   <LI> DBTypeConstants.DB_DATETIME maps to Timestamp
     *   <LI> DBTypeConstants.DB_DOUBLE maps to Double
     *   <LI> DBTypeConstants.DB_BIT maps to Boolean
     * </UL>
     * @return the java type name or null if the type is unknown
     */
    public String getJavaType() {
        String name = null;
        switch (type)
        {
                case DBTypeConstants.DB_CHAR:
                case DBTypeConstants.DB_VARCHAR:
                case DBTypeConstants.DB_TEXT:
                  name = "String";
                  break;
                case DBTypeConstants.DB_INTEGER:
                   name = "Integer";
                   break;
                case DBTypeConstants.DB_DATETIME:
                   name = "Timestamp";
                   break;
                case DBTypeConstants.DB_DOUBLE:
                   name = "Double";
                   break;
                case DBTypeConstants.DB_BIT:
		case 5:
                   name = "Boolean";
                   break;
                default:
                   name = null;
                   break;
        }
        return name;
    }

    /**
     * get the name of the given column in a format used during code
     * generation
     * @assumes nothing
     * @effects nothing
     * @return the column name
     */
    public String getCGName() {
      String[] parts = name.split("_");
      String cgName = null;
      boolean firstConcat = true; // first concat operation
      for (int i = 0; i < parts.length; i++) {
        String part = parts[i];
        String newPart = null;
        if (!part.equals("")) {
          if (!firstConcat) {
            // capitalize subsequent substrings during concat operation
            newPart =
                part.substring(0, 1).toUpperCase().concat(part.substring(1));
          }
          else {
            // dont capitalize the first substring
            newPart =
                part.substring(0, 1).toLowerCase().concat(part.substring(1));
          }
          if (cgName == null) {
            cgName = newPart;
            firstConcat = false;
          }
          else {
            cgName = cgName.concat(newPart);
          }
        }
      }
      if (cgName == null)
        cgName = this.name;
      if (cgName.equals("private") || cgName.equals("abstract") ||
          cgName.equals("class"))
        cgName = cgName + "Val";
      return cgName;
    }

    /**
     * get the name of the setter method for this column to be used for
     * the automated creation of java bean classes with common accessor
     * methods
     * @return the name of the setter method for this column
     */
    public String getSetterCGName() {
      String cgName = getCGName();
      return "set" +
          cgName.substring(0, 1).toUpperCase().concat(cgName.substring(1));
    }

    /**
     * get the name of the setter method for this column to be used for
     * the automated creation of java bean classes with common accessor
     * methods
     * @return the name of the setter method for this column
     */
    public String getGetterCGName() {
      String cgName = getCGName();
      return "get" +
          cgName.substring(0, 1).toUpperCase().concat(cgName.substring(1));
    }


    /**
     * get a TypeValidator for this column
     * @assumes nothing
     * @effects nothing
     * @return the TypeValidator class
     */
    public TypeValidator getTypeValidator() {
      return TypeValidator.newInstance(this);
    }

    /**
     * get the String representation of this instance
     * @return the String representation for this instance
     */
    public String toString() {
        String s = getName() + " : " + getTypeName();

        return s;
    }

    /**
     * dump the String representation of this instance to the given stream
     * @param stream the stream to dump on
     */
    public void dump(PrintStream stream) {
                        stream.println(this.toString());
    }


  }
