package org.jax.mgi.shr.dbutils.dao;

/**
 * static methods for various SQLStrategy classes
 * @has satic helper methods
 * @does nothing
 * @company The Jackson Laboratory
 * @author M Walker
 *
 */

public class SQLStrategyHelper
{
    /**
     * convert the JDBC syntax for a stored procedure call (using the "call"
     * command) to the isql syntax (using the "exec" command)
     * @param sql the sql to evaluate
     * @return the converted command
     */
    public static String convertToOtherProc(String sql)
    {
        if (sql.startsWith("{call "))
        {
            StringBuffer newSql = new StringBuffer("exec ");
            String[] tokens = sql.split("\\(");
            String[] subTokens = tokens[0].split(" ");
            String procName = subTokens[1];
            newSql.append(procName + " ");
            subTokens = tokens[1].split("\\)");
            newSql.append(subTokens[0]);
            return new String(newSql);
        }
        else
            return sql;
    }

    /**
     * convert the isql syntax for a stored procedure call (using the "call"
     * command) to the JDBC syntax (using the "exec" command)
     * @param sql the sql to evaluate
     * @return the converted command
     */
    public static String convertToJDBCProc(String sql)
    {
        if (sql.startsWith("exec "))
        {
            StringBuffer newSql = new StringBuffer("{call ");
            String[] tokens = sql.split(" ");
            newSql.append(tokens[1]);
            newSql.append("(");
            for (int i = 2; i < tokens.length; i++)
            {
                newSql.append(tokens[i] + " ");
            }
            // lop off last space
            newSql.deleteCharAt(newSql.length() - 1);
            newSql.append(")}");
            return new String(newSql);
        }
        else
            return sql;
    }

}

  /**************************************************************************
   *
   * Warranty Disclaimer and Copyright Notice
   *
   *  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
   *  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
   *  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
   *  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
   *  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
   *  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
   *
   *  This software and data are provided to enhance knowledge and encourage
   *  progress in the scientific community and are to be used only for research
   *  and educational purposes.  Any reproduction or use for commercial purpose
   *  is prohibited without the prior express written permission of The Jackson
   *  Laboratory.
   *
   * Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
   *
   * All Rights Reserved
   *
   **************************************************************************/
