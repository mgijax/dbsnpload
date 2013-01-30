package org.jax.mgi.shr.dbutils;

/**
 *
 * A combination of query and the corresponding MultiRowInterpreter.
 * @has a query string and implements MultiRowInterpreter
 * @does provides a database query and an interpret method for the results.
 * @company The Jackson Laboratory
 * @author MWalker
 *
 */
public interface InterpretedMultiRowQuery
	extends MultiRowInterpreter
{
    /**
     * get the sql query string
     * @return the sql query string
     */
	public String getQuery();
}
