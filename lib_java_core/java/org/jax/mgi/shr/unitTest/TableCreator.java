package org.jax.mgi.shr.unitTest;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import org.jax.mgi.shr.dbutils.ConnectionManager;

/**
 * An object for creating database tables used in java unit
 * testing.
 * @has an database connection and methods for creating database tables.
 * A couple generic table definitions are provided. One is called DBTypes which
 * has a column for each data type expected to be encountered in an application.
 * The other is called TEST_DBgeneric and is meant to represent a generic table
 * encountered in applications which consist of the common fields createdBy,
 * modifiedBy, creation_date and modification_date. Additionally a method is
 * provided that will execute any drop table statement and create table
 * statement provided as parameters.
 * @does executes drop/create table commands for the purpose
 * of creating tables on the fly to support java unit testing programs.</p>
 * @company Jackson Laboratory
 * @author M Walker
 */
public class TableCreator {
  private Connection conn = null;
  private String DEFAULT_CONNECTION_MANAGER =
      "org.jax.mgi.shr.dbutils.MGIDriverManager";
  private String ORACLE = "org.jax.mgi.shr.dbutils.OrclConnection";

  private String connectionManager = null;
  /**
   * default constructor
   * @throws Exception
   */
  public TableCreator(String url, String database,
                      String user, String password) throws Exception {
      this.connectionManager = DEFAULT_CONNECTION_MANAGER;
    conn =
        getConnection(url, database, user, password, DEFAULT_CONNECTION_MANAGER);
  }

  /**
   * default constructor
   * @throws Exception
   */
  public TableCreator(String url, String database,
                      String user, String password,
                      String connectionManagerClass) throws Exception {
      this.connectionManager = connectionManagerClass;
    conn =
        getConnection(url, database, user, password, connectionManagerClass);
  }

  /**
   * close the connection used by this class
   * @assumes nothing
   * @effects the internal connection will be closed
   */
  public void close() throws SQLException
  {
      this.conn.close();
  }

  /**
   * creates a predefined table that is defined with one of each of all
   * expected data types.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBtypes
   * columnA     varchar(30)   not null
   * columnB     datetime      not null
   * columnC     int           not null
   * columnD     char(1)       null
   * columnE     text          null
   * columnF     double(8)     null
   * columnG     bit           not null
   * </pre>
   *
   * @throws Exception
   */
  public void createDBtypes() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBtypes";
    String sqlCreate =
        "CREATE TABLE TEST_DBtypes ("
        + "columnA     varchar(30)   null,"
        + "columnB     datetime      null,"
        + "columnC     int           null,"
        + "columnD     char(1)       null,"
        + "columnE     text          null,"
        + "columnF     double(8)     null,"
        + "columnG     bit           not null)";
    if (this.connectionManager.equals(ORACLE))
    {
        sqlCreate = convertToOracle(sqlCreate);
    }
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBtypes
   * @throws Exception
   */
  public void dropDBtypes() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBTypes";
    dropTable(sqlDrop);
  }

  /**
   * creates a predefined table to be used for unit testing classes
   * designed for database functionality.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBsimple
   * columnA           int         not null
   * columnB           varchar(30) null
   * </pre>
   * @throws Exception
   */
  public void createDBsimple() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBsimple";
    String sqlCreate =
        "CREATE TABLE TEST_DBsimple ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBsimple
   * @throws Exception
   */
  public void dropDBsimple() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBsimple";
    dropTable(sqlDrop);
  }

  /**
   * creates a table with a primary key column of type int to be used for
   * unit testing methods that are designed with database functionality.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBkeyedInt
   * columnA           int         not null primary key
   * columnB           varchar(30) null
   * <br>
   * </pre>
   * @throws Exception
   */
  public void createDBkeyedInt() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBkeyedInt";
    String sqlCreate =
        "CREATE TABLE TEST_DBkeyedInt ("
        + "columnA           int         primary key,"
        + "columnB           varchar(30) null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBKeyedInt
   * @throws Exception
   */
  public void dropDBkeyedInt() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBkeyedInt";
    dropTable(sqlDrop);
  }

  /**
   * creates a predefined table which models original tables in MGD that have
   * record stamping fields createdBy, modifiedBy, creation_date
   * and modification_date.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBstamped_MGDOrg
   * columnA           varchar(30) not null
   * columnB           datetime    not null
   * columnC           int         not null
   * columnD           double      null
   * createdBy         varchar(30) not null
   * modifiedBy        varchar(30) not null
   * creation_date     datetime    not null
   * modification_date datetime    not null
   * </pre>
   * @throws Exception
   */
  public void createDBstamped_MGDOrg() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGDOrg";
    String sqlCreate =
        "CREATE TABLE TEST_DBstamped_MGDOrg ("
        + "columnA           varchar(30) not null,"
        + "columnB           datetime    not null,"
        + "columnC           int         not null,"
        + "columnD           double      null,"
        + "createdBy         varchar(30) not null,"
        + "modifiedBy        varchar(30) not null,"
        + "creation_date     datetime    not null,"
        + "modification_date datetime not null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBstamped_MGDOrg
   * @throws Exception
   */
  public void dropDBstamped_MGDOrg() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGDOrg";
    dropTable(sqlDrop);
  }

  /**
   * creates a predefined table which models tables in MGD that have record
   * stamping fields _createdBy_key, _modifiedBy_ky, creation_date
   * and modification_date.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBstamped_MGD
   * columnA           int         not null
   * columnB           String      not null
   * _createdBy_key    int         not null
   * _modifiedBy_key   int         not null
   * creation_date     datetime    not null
   * modification_date datetime    not null
   * </pre>
   * @throws Exception
   */
  public void createDBstamped_MGD() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGD";
    String sqlCreate =
        "CREATE TABLE TEST_DBstamped_MGD ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) not null,"
        + "_createdBy_key    int         not null,"
        + "_modifiedBy_key   int         not null,"
        + "creation_date     datetime    not null,"
        + "modification_date datetime not null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBstamped_MGD
   * @throws Exception
   */
  public void dropDBstamped_MGD() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGD";
    dropTable(sqlDrop);
  }

  /**
   * creates a table which models RADAR tables that have record
   * stamping fields _jobstream_key and creation_date.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBstamped_RADAR
   * columnA           int         not null
   * columnB           String      not null
   * _jobstream_key    int         not null
   * creation_date     datetime    not null
   * </pre>
   * @throws Exception
   */
  public void createDBstamped_RADAR() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_RADAR";
    String sqlCreate =
        "CREATE TABLE TEST_DBstamped_RADAR ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) not null,"
        + "_jobstream_key    int         not null,"
        + "creation_date     datetime    not null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBstamped_RADAR
   * @throws Exception
   */
  public void dropDBstamped_RADAR() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_RADAR";
    dropTable(sqlDrop);
  }

  /**
   * creates a predefined table which models tables in MGD that have record
   * stamping fields creation_date and modification_date.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBstamped_MGDDate
   * columnA           int         not null
   * columnB           String      not null
   * creation_date     datetime    not null
   * modification_date datetime    not null
   * </pre>
   */
  public void createDBstamped_MGDDate() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGDDate";
    String sqlCreate =
        "CREATE TABLE TEST_DBstamped_MGDDate ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) not null,"
        + "creation_date     datetime    not null,"
        + "modification_date datetime    not null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBstamped_MGDDate
   * @throws Exception
   */
  public void dropDBstamped_MGDDate() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGDDate";
    dropTable(sqlDrop);
  }

  /**
   * creates a predefined table which models tables in MGD that have record
   * stamping fields creation_date, modification_date and release_date.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBstamped_MGDRelease
   * columnA           int         not null
   * columnB           String      not null
   * creation_date     datetime    not null
   * modification_date datetime    not null
   * release_date      datetime    not null
   * </pre>
   */
  public void createDBstamped_MGDRelease() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGDRelease";
    String sqlCreate =
        "CREATE TABLE TEST_DBstamped_MGDRelease ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) not null,"
        + "creation_date     datetime    not null,"
        + "modification_date datetime    not null,"
        + "release_date      datetime    not null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBstamped_MGDRelease
   * @throws Exception
   */
  public void dropDBstamped_MGDRelease() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBstamped_MGDRelease";
    dropTable(sqlDrop);
  }

  /**
   * creates a predefined table to be used for unit testing classes
   * designed for database functionality.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBkeyed
   * columnA           int         not null
   * columnB           varchar(30) null
   * </pre>
   * and applies a primary key definition to columnA
   * @throws Exception
   */
  public void createDBkeyed() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBkeyed";
    String sqlCreate =
        "CREATE TABLE TEST_DBkeyed ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) null)";
    createTable(sqlDrop, sqlCreate);
    Statement statement = conn.createStatement();
    if (this.connectionManager == this.DEFAULT_CONNECTION_MANAGER)
        statement.execute("sp_primarykey TEST_DBkeyed, columnA");
    else
    {
        statement.execute("ALTER TABLE TEST_DBkeyed " +
                          "ADD PRIMARY KEY ( columnA )");
    }
  }

  /**
   * drop the table TEST_DBkeyed
   * @throws Exception
   */
  public void dropDBkeyed() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBkeyed";
    dropTable(sqlDrop);
  }

  /**
   * creates a table to be used for unit testing methods that are designed
   * for database functionality.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBmultikeyed
   * columnA           int         not null
   * columnB           varchar(30) null
   * </pre>
   * add applies a primary key definition to columnA
   * @throws Exception
   */
  public void createDBmultikeyed() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBmultikeyed";
    String sqlCreate =
        "CREATE TABLE TEST_DBmultikeyed ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) null)";
    createTable(sqlDrop, sqlCreate);
    Statement statement = conn.createStatement();
    if (this.connectionManager == this.DEFAULT_CONNECTION_MANAGER)
        statement.execute("sp_primarykey TEST_DBmultikeyed, columnA, columnB");
    else
    {
        statement.execute("ALTER TABLE TEST_DBmultikeyed " +
                          "ADD PRIMARY KEY ( columnA,  columnB)");
    }

  }

  /**
   * drop the table TEST_DBmultikeyed
   * @throws Exception
   */
  public void dropDBmultikeyed() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBmultikeyed";
    dropTable(sqlDrop);
  }

  /**
   * creates a predefined table to be used for unit testing classes
   * designed for database functionality.
   * The table has the following definition:
   * <br>
   * <pre>
   * CREATE TABLE TEST_DBtrigger
   * columnA           int         not null
   * columnB           varchar(30) null
   * </pre>
   * @throws Exception
   */
  public void createTriggerCompanion() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBtrigger";
    String sqlCreate =
        "CREATE TABLE TEST_DBtrigger ("
        + "columnA           int         not null,"
        + "columnB           varchar(30) null)";
    createTable(sqlDrop, sqlCreate);
  }

  /**
   * drop the table TEST_DBsimple
   * @throws Exception
   */
  public void dropTriggerCompanion() throws Exception {
    String sqlDrop = "DROP TABLE TEST_DBtrigger";
    dropTable(sqlDrop);
  }


  /**
       * execute the given drop table and create table commands. The drop command is
   * run in advance of create to compensate for the scenario where the table
   * already exists in the database.
   * @param drop drop table command
   * @param create create table command
   * @throws Exception
   */
  private void createTable(String drop, String create) throws Exception {
    try {
      Statement statement = conn.createStatement();
      statement.executeUpdate(drop);
    }
    catch (Exception e) {
      // ignore this since probably triggered from the table not existing
    }
    Statement statement = conn.createStatement();
    statement.executeUpdate(create);
  }

  /**
       * will execute the given drop table command to be used when cleaning up at the
   * completion of a unit test run.
   * @param drop the drop table command
   * @throws Exception
   */
  private void dropTable(String drop) throws Exception {
    Statement statement = conn.createStatement();
    statement.executeUpdate(drop);
  }

  private Connection getConnection(String url, String database,
                                   String user, String password,
                                   String connectionManagerClass) throws
      SQLException {
    ConnectionManager connectionManager = null;
    Connection conn = null;
    try {
      Class c = Class.forName(connectionManagerClass);
      connectionManager = (ConnectionManager) c.newInstance();
      conn = connectionManager.getConnection(database, user, password, url);
    }
    catch (ClassNotFoundException e) {
      throw new SQLException(e.getMessage());
    }
    catch (IllegalAccessException e) {
      throw new SQLException(e.getMessage());
    }
    catch (InstantiationException e) {
      throw new SQLException(e.getMessage());
    }
    return conn;
  }

  private String convertToOracle(String sql)
  {
      String newsql = null;
      newsql = sql.replaceAll("varchar", "varchar2");
      newsql = newsql.replaceAll("datetime", "date");
      newsql = newsql.replaceAll("text", "long");
      newsql = newsql.replaceAll("bit", "smallint");
      return newsql;
  }
}