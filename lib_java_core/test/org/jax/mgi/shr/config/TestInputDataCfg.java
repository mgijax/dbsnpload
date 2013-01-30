package org.jax.mgi.shr.config;

import java.util.*;
import junit.framework.*;
import org.jax.mgi.shr.unitTest.*;

public class TestInputDataCfg
    extends TestCase {
  private InputDataCfg fileCfg = null;
  //private FileUtility fileUtility = new FileUtility();
  private String config = "config";
  private String originalConfigValue = null;

  public static void main(String args[]) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestInputDataCfg.class);
    return suite;
  }

  public TestInputDataCfg(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    String s =
        "#format: sh\n" +
        "INFILE_NAME=testfile\n" +
        "INFILE_BEGIN_DELIMITER=^//\n" +
        "INFILE_BUFFERSIZE=2222\n" +
        "SECONDARY_INFILE_NAME=testfile2\n" +
        "SECONDARY_INFILE_END_DELIMITER=\\|\n" +
        "SECONDARY_INFILE_BUFFERSIZE=333\n";

    FileUtility.createFile(config, s);
    String configParm = System.getProperty("CONFIG");
    if (configParm != null) {
        originalConfigValue = configParm;
        config = configParm + "," + config;
    }
    System.setProperty("CONFIG", config);
    ConfigReinitializer.reinit();


  }

  protected void tearDown() throws Exception {
      Properties p = System.getProperties();
      if (originalConfigValue != null)
          System.setProperty("CONFIG", originalConfigValue);
      else
          p.remove("CONFIG");
      ConfigReinitializer.reinit();
      fileCfg = null;
      FileUtility.delete(config);
      super.tearDown();
  }

  public void testGets() throws Exception {
    System.setProperty("CONFIG", config);
    fileCfg = new InputDataCfg();
    assertEquals(new Integer(2222), fileCfg.getBufferSize());
    assertEquals("^//", fileCfg.getBeginDelimiter());
    assertEquals("return value", "testfile", fileCfg.getInputFileName());
  }

  public void testPrefixing() throws Exception {
    System.setProperty("CONFIG", config);
    fileCfg = new InputDataCfg("SECONDARY");
    assertEquals(new Integer(333), fileCfg.getBufferSize());
    assertEquals("\\|", fileCfg.getEndDelimiter());
    assertEquals("return value", "testfile2", fileCfg.getInputFileName());

  }

}
