package org.jax.mgi.shr.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JDBCDriverManager implements ConnectionManager {
	
	private DataSource source;

	public Connection getConnection(String database) throws SQLException {
		Context ctx;
		try {
			ctx = new InitialContext();
			source = (DataSource) ctx.lookup(database);
			return source.getConnection();
		} catch (NamingException e) {
			throw new SQLException("Error finding DataSource: " + database);
		}
	}

	public Connection getConnection(String database, String user,
			String password, String url) throws SQLException {
		return getConnection(database);
	}

}
