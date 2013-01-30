package org.jax.mgi.shr.unitTest;

import junit.framework.*;

public class TestConfigFileCreator
    extends TestCase {
  private ConfigFileCreator configFileCreator = null;
  private String testFile = "testFile";
  private String configFile = "configFile";

  public TestConfigFileCreator(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    configFileCreator = new ConfigFileCreator();
  }

  protected void tearDown() throws Exception {
    configFileCreator = null;
    super.tearDown();
    FileUtility.delete(testFile);
    FileUtility.delete(configFile);
  }

  public void testAddParameter() throws Exception {
    String testContents =
          "#format: sh\n"
        + "param1=value1\n"
        + "param2=value2\n"
        + "DB_SERVER=DEV_MGI\n"
        + "DB_DATABASE=mgd\n"
        + "DB_USER=mgd_dbo\n"
        + "DB_URL=rohan.informatics.jax.org:4101\n"
        + "DB_PASSWORDFILE=/usr/local/mgi/dbutilities/.mgd_dbo_password\n";
    FileUtility.createFile(testFile, testContents);
    configFileCreator.addParameter("param1", "value1");
    configFileCreator.addParameter("param2", "value2");
    configFileCreator.createConfig(configFile);
    assertTrue(FileUtility.compare(configFile, testFile));
  }

}
