package org.jax.mgi.shr.exception;

import junit.framework.*;

public class TestMGIException
    extends TestCase {
  private MGIException mgiException = null;

  public TestMGIException(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    mgiException = null;
  }

  protected void tearDown() throws Exception {
    mgiException = null;
    super.tearDown();
  }

  public void testChainedException() {
    Exception pParent = new BindableException("This is exception 1");
    boolean pDataRelated = false;
    String pMessage = "This is exception ??";
    String expectedReturn = "This is exception 2\nThis is exception 1";
    mgiException = new MGIException(pMessage, pDataRelated);
    mgiException.bind(2);
    mgiException.setParent(pParent);
    String actualReturn = mgiException.getMessage();
    assertEquals("return value", expectedReturn, actualReturn);
    assertTrue(!mgiException.isDataRelated());
  }

}
