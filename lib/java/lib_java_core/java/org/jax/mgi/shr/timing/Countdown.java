package org.jax.mgi.shr.timing;

/*
* $Header$
* $Name$
*/

/**
* @module Countdown.java
* @author jsb
*/

/** counts down from a given number of seconds to zero, allowing one to check
*   to see if the time has elapsed yet or not.
*  a timer which counts down a certain number of seconds, stopping when it
*   reaches zero.
* @has a <tt>double</tt> number of seconds to count down (the "timeout"), a
*    <tt>double</tt> number of seconds remaining, and an indicator for whether
*    the time has completely elapsed or not.
* @does Public methods include:
*    <OL>
*    <LI> reset() -- starts the countdown again using its most recent timeout
*        value
*    <LI> elapsed() -- determines whether the time has fully elapsed or not
*    <LI> getTimeOut() -- find what the current timeout setting is
*    <LI> setTimeOut(double) -- change the current timeout setting, but let
*        the current timer keep running (does not reset it)
*    </OL>
*/
public class Countdown
{
    /* -------------------------------------------------------------------- */

    ///////////////
    // constructors
    ///////////////

   /**
    * standard constructor which instantiates a <tt>Countdown</tt> with the
    * given 'timeOut'.
    * @param timeOut specifies the number of seconds from which to count down
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public Countdown (double timeOut)
    {
	this.startTime = getCurrentTime();
	this.setTimeOut (timeOut);
	return;
    }

    /* -------------------------------------------------------------------- */

    /////////////////
    // public methods
    /////////////////

    /** restarts the countdown timer.
     * uses the most recently set timeout value
    * @return nothing
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public void reset ()
    {
        this.startTime = getCurrentTime();
	this.endTime = this.startTime + this.timeOut_msec;
	return;
    }

    /* -------------------------------------------------------------------- */

    /** determine if the current timeout value has fully elapsed.
    * @return boolean <tt>true</tt> if it has elapsed, <tt>false</tt> if not
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public boolean elapsed ()
    {
        return (this.endTime <= getCurrentTime());
    }

    /* -------------------------------------------------------------------- */

    /** determine the current timeout value.
    * @return double the current value of the timeout
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public double getTimeOut ()
    {
        return this.timeOut;
    }

    /* -------------------------------------------------------------------- */

    /** set a new timeout value.  keeps the current timer running and does not
    *    reset it.  (If the previous timeout was 20 seconds, and 15 had
    *    elapsed when we change the timeout value to 40, then there would be
    *    20 more seconds remaining.)
    * @param timeOut number of seconds to count down.
    * @return nothing
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public void setTimeOut (double timeOut)
    {
        this.timeOut = timeOut;
	this.timeOut_msec = timeOut * 1000.0;
	this.endTime = this.startTime + this.timeOut_msec;
	return;
    }

    /* -------------------------------------------------------------------- */

    //////////////////
    // private methods
    //////////////////

    /** private -- get the current system time, expressed in milliseconds.
    * @return double the current system time in milliseconds.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    private static double getCurrentTime ()
    {
        return System.currentTimeMillis();
    }

    /* -------------------------------------------------------------------- */

    ///////////////////////////
    // private member variables
    ///////////////////////////

    // the timeout value, expressed in seconds
    private double timeOut = 0.0D;

    // the timeout value, converted to milliseconds (We keep both so that we
    // can still return an accurate timeout value to the user, in case of
    // rounding errors.)
    private double timeOut_msec = 0.0D;

    // system time at which the countdown timer was most recently started,
    // in milliseconds.  (at instantiation or the most recent 'reset()')
    private double startTime = 0.0D;

    // system time at which the countdown timer will elapse
    private double endTime = 0.0D;
}

/*
* $Log$
* Revision 1.1  2013/01/30 18:05:26  mgiadmin
* postgres convert
*
* Revision 1.2  2004/07/21 20:50:55  mbw
* javadocs edits only
*
* Revision 1.1  2003/12/30 16:56:48  mbw
* imported into this product
*
* Revision 1.2  2003/12/01 13:02:11  jsb
* Minor efficiency tweak as part of JSAM code review
*
* Revision 1.1  2003/07/03 17:33:37  jsb
* initial addition for use by JSAM WI
*
* $Copyright$
*/
