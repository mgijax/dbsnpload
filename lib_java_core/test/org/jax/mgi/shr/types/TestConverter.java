package org.jax.mgi.shr.types;

import junit.framework.*;
import java.sql.*;
import java.text.*;

public class TestConverter
    extends TestCase {
  //private Converter converter = null;

  public TestConverter(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    //converter = new Converter();
  }

  protected void tearDown() throws Exception {
    //converter = null;
    super.tearDown();
  }

  public void testStringToBoolean() throws Exception {
    String s = "TRUE";
    assertTrue(Converter.toBoolean(s).booleanValue());
    s = "false";
    assertTrue(!Converter.toBoolean(s).booleanValue());
    s = "1";
    assertTrue(Converter.toBoolean(s).booleanValue());
    s = "0";
    assertTrue(!Converter.toBoolean(s).booleanValue());
    s = "anything but true";
    try {
      assertTrue(!Converter.toBoolean(s).booleanValue());
    }
    catch (Exception ke) {
      // this is what we expect
      assertTrue(true);
      return;
    }
    // we shouldnt get here
    assertTrue(false);
  }

  public void testStringToDouble() throws Exception {
    String s = "1.8767564";
    float expectedReturn = (float)1.8767564;
    Double actualReturn = Converter.toDouble(s);
    assertEquals(expectedReturn, actualReturn.floatValue(), (float)0.0001);
  }

  public void testStringToInteger() throws Exception {
    String s = "1";
    int expectedReturn = 1;
    Integer actualReturn = Converter.toInteger(s);
    assertEquals("return value", expectedReturn, actualReturn.intValue());
    s = "1.0235";
    try
    {
        actualReturn = Converter.toInteger(s);
        // should not get here
        assertTrue(false);
    }
    catch (TypesException e)
    {
        assertTrue(true);
    }
  }

  public void testStringToPrimitiveBoolean() throws Exception {
    String s = "true";
    boolean expectedReturn = true;
    boolean actualReturn = Converter.toPrimitiveBoolean(s);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testStringToPrimitiveDouble() throws Exception {
    String s = "1.098";
    double expectedReturn = (double)1.098;
    double actualReturn = Converter.toPrimitiveDouble(s);
    assertEquals(expectedReturn, actualReturn, (double)0.0001);
  }

  public void testStringToPrimitiveInt() throws Exception {
    String s = "100";
    int expectedReturn = 100;
    int actualReturn = Converter.toPrimitiveInt(s);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testStringToTimestamp() throws Exception, ParseException {
    DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    java.util.Date date = formatter.parse("2003.07.04.00.00.00");
    Timestamp expectedReturn = new Timestamp(date.getTime());
    String s = "2003-07-04 00:00:00.0";
    Timestamp actualReturn = Converter.toTimestamp(s);
    assertEquals("return value", expectedReturn, actualReturn);
    s = "2003/7/4";
    actualReturn = Converter.toTimestamp(s);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testToStringFromBoolean() {
    Boolean input = new Boolean(true);
    String expectedReturn = "1";
    String actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
    input = new Boolean(false);
    expectedReturn = "0";
    actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testToStringFromDouble() {
    Double input = new Double(1.0987865611111);
    String expectedReturn = "1.0987865611111";
    String actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testToStringFromInteger() {
    Integer input = new Integer(25);
    String expectedReturn = "25";
    String actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testToStringFromTimestamp() throws ParseException {
    DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
    java.util.Date date = formatter.parse("2003.07.04.00.00.00");
    Timestamp input = new Timestamp(date.getTime());
    String expectedReturn = "2003-07-04 00:00:00.0";
    String actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testToStringFromPrimitiveBoolean() {
    boolean input = false;
    String expectedReturn = "0";
    String actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testToStringFromPrimitiveDouble() {
    double input = (double)4.12341111111;
    String expectedReturn = "4.12341111111";
    String actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testToStringFromPrimitiveInt() {
    int input = -16;
    String expectedReturn = "-16";
    String actualReturn = Converter.toString(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testWrapBoolean() {
    boolean input = false;
    Boolean expectedReturn = new Boolean(false);
    Boolean actualReturn = Converter.wrap(input);
    assertEquals("return value", expectedReturn, actualReturn);
  }

  public void testWrapDouble() {
    double input = 0;
    Double expectedReturn = new Double(0);
    Double actualReturn = Converter.wrap(input);
    assertEquals("return value", expectedReturn.doubleValue(), actualReturn.doubleValue(), 0);
  }

  public void testWrapInt() {
    int input = 0;
    Integer expectedReturn = new Integer(0);
    Integer actualReturn = Converter.wrap(input);
    assertEquals("return value", expectedReturn.intValue(), actualReturn.intValue());
  }

}
