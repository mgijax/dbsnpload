package org.jax.mgi.shr.dbutils;

import junit.framework.TestCase;

import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;


import org.jax.mgi.shr.dbutils.dao.DAO;
import org.jax.mgi.shr.dbutils.dao.Script_Stream;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.unitTest.*;

public class TestScriptWriter extends TestCase
{

    ScriptWriter writer = null;
    SQLDataManager sqlMgr = null;
    Script_Stream stream = null;
    TableCreator tc = null;
    FileUtility fileUtil = null;
    String sybaseOutFile = "test" + File.separator + "SybaseErrorFile.txt";
    String sybaseOutCompare = "test" + File.separator + "SybaseErrorFile.compare";


    /**
     * Constructor for TestScriptWriter.
     * @param arg0
     */
    public TestScriptWriter(String arg0)
    {
        super(arg0);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        sqlMgr = new SQLDataManager();
        tc = new TableCreator(sqlMgr.getUrl(), sqlMgr.getDatabase(),
                              sqlMgr.getUser(), sqlMgr.getPassword(),
                              sqlMgr.getConnectionManagerClass());
        tc.createDBsimple();
    }

    protected void tearDown() throws Exception
    {
        sqlMgr = null;
        writer = null;
        stream = null;
        fileUtil.delete("script.out");
        fileUtil.delete("script.sql");
        fileUtil.delete("script_1.out");
        fileUtil.delete("script_1.sql");
        tc.dropDBsimple();
        tc = null;
        super.tearDown();
    }

    public void testWrite() throws Exception
    {
        writer = new ScriptWriter(sqlMgr);
        writer.setOkToOverwrite(true);
        stream = new Script_Stream(writer);
        SimpleDAO dao = new SimpleDAO();
        stream.insert(dao);
        stream.close();
        BufferedReader in = new BufferedReader(new FileReader("script.sql"));
        String str;
        str = in.readLine();
        assertEquals("insert into Test_DBSimple values (1, 'one')", str);
        str = in.readLine();
        assertEquals("go", str);
        in.close();
        BufferedReader in2 = new BufferedReader(new FileReader("script.out"));
        str = in2.readLine();
        assertEquals("1> insert into Test_DBSimple values (1, 'one')", str);
        str = in2.readLine();
        assertEquals("(1 row affected)", str);
        in2.close();
    }

    public void testOkToOverwrite() throws Exception
    {
        writer = new ScriptWriter(sqlMgr);
        writer.setOkToOverwrite(true);
        stream = new Script_Stream(writer);
        ScriptWriter writer2 = new ScriptWriter(sqlMgr);
        writer2.setOkToOverwrite(false);
        Script_Stream stream2 = new Script_Stream(writer2);
        SimpleDAO dao = new SimpleDAO();
        stream.insert(dao);
        stream2.insert(dao);
        stream.close();
        stream2.close();
        assertTrue(fileUtil.doesExist("script_1.out"));
        assertTrue(fileUtil.doesExist("script_1.sql"));
    }

    public void testOkToOverwrite2() throws Exception
    {
        writer = new ScriptWriter(sqlMgr);
        writer.setOkToOverwrite(true);
        stream = new Script_Stream(writer);
        ScriptWriter writer2 = new ScriptWriter(sqlMgr);
        writer2.setOkToOverwrite(true);
        Script_Stream stream2 = new Script_Stream(writer2);
        SimpleDAO dao = new SimpleDAO();
        stream.insert(dao);
        stream2.insert(dao);
        stream.close();
        stream2.close();
        assertTrue(!fileUtil.doesExist("script_1.out"));
        assertTrue(!fileUtil.doesExist("script_1.sql"));
    }


    public void testErrorSummary() throws Exception
    {
        ScriptWriter.SybaseOutFile outFile =
            ScriptWriter.getSybaseOutFile(sybaseOutFile);
        String summary = (String)outFile.parseForErrors();
        System.out.println(summary);
        fileUtil.createFile("results", summary);
        assertTrue(fileUtil.compare("results", sybaseOutCompare));
        fileUtil.delete("results");
    }


    public class SimpleDAO extends DAO
    {

        public String getUpdateSQL()
        {
            return new String("update Test_DBSimple set columnB = 'uno' " +
                              "where columnA = 1");
        }

        public String getInsertSQL()
        {
            return new String("insert into Test_DBSimple values (1, 'one')");
        }

        public String getDeleteSQL()
        {
            return new String("delete TestDBSimple where columnA = 1");
        }

        public Vector getBCPVector(Table table)
        {
            throw MGIException.getUnsupportedMethodException();
        }

        public Vector getBCPSupportedTables()
        {
            throw MGIException.getUnsupportedMethodException();
        }

    }

}

