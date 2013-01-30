package org.jax.mgi.shr.exception;

import junit.framework.*;

public class TestBindableException
    extends TestCase {
  private BindableException bindableException = null;

  public TestBindableException(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    bindableException = null;
    super.tearDown();
  }

  public void testToString() {
    bindableException = new BindableException("This is a string");
    String expectedReturn = "This is a string";
    String actualReturn = bindableException.toString();
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testGetMessage() {
    bindableException = new BindableException("This is a string");
    String expectedReturn = "This is a string";
    String actualReturn = bindableException.getMessage();
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testBind() {
    int i = 0;
    bindableException = new BindableException("The integer is ??");
    String expectedReturn = "The integer is 0";
    bindableException.bind(i);
    String actualReturn = bindableException.getMessage();
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testBind1() {
    String s1 = "first";
    String s2 = "second";
    bindableException = new BindableException("This is the ?? and this is the ??");
    String expectedReturn = "This is the first and this is the second";
    bindableException.bind(s1);
    bindableException.bind(s2);
    String actualReturn = bindableException.getMessage();
    assertEquals("return value", expectedReturn, actualReturn);
  }

  // the following is a visual test only

  //public void testPrintStackTrace() {
    //bindableException = new BindableException("The exception");
    //bindableException.printStackTrace();
  //}

}
