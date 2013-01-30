package org.jax.mgi.shr.exception;

/**
 *  A BindableException which can be chained to other Exceptions.
 * @has a parent Exception, message
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 */

public class ChainedException
    extends BindableException implements Cloneable {

  // the exception which this exception is based on
  Exception parentException = null;

  /**
   * constructor
   * @assumes nothing
   * @effects nothing
   * @param pMessage the exception message
   */
  public ChainedException(String pMessage) {
    super(pMessage);
  }


  /**
   * setter method for the parent exception
   * @assumes nothing
   * @effects nothing
   * @param pParent the parent exception
   */
  protected void setParent(Exception pParent) {
    parentException = pParent;
  }

  /**
   * getter method for the parent exception
   * @assumes nothing
   * @effects nothing
   * @return the parent exception
   */
  public Exception getParent() {
    return parentException;
  }

  /**
   * Return the message of this exception object concatenated
   * with the message of any parent exception object(s).
   * @assumes Nothing
   * @effects Nothing
   * @return The exception message.
   */
  public String toString ()
  {
      // If there is no parent exception, just return the message from
      // this exception object.  Otherwise, the message from the parent
      // exception object needs to be retrieved too.
      //
      if (parentException == null)
          return super.toString();
      else
          return super.toString() + "\n" + parentException.toString();
  }

  /**
   * override the clone base class method to properly set instance variables
   * @return cloned object
   */
  public Object clone() {
    ChainedException newException = (ChainedException)super.clone();
    newException.parentException = this.parentException;
    return newException;
  }

  /**
   * override the getMessage base class method to properly obtain chained
   * messages
   * @return chanined messages of all exceptions in hierarchy
   */
  public String getMessage() {
    return toString();
  }

}