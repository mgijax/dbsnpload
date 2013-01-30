package org.jax.mgi.shr.timing;

/*
* $Header$
* $Name$
*/

/**
* @module TimeStampedMessage
* @author jsb
*/

/** encapsulates one entry from a TimeStamper
*  one message with associated timestamps
* @has a message String, a date/time String, and a double representing the
*    number of seconds which had elapsed by the time this message was
*    recorded.
* @does provides accessor methods for the three pieces mentioned above.
*/
public class TimeStampedMessage
{
    /////////////////////
    // instance variables
    /////////////////////

    // message that was recorded
    private String message = null;

    // date/time at which the message was recorded
    private String dateTime = null;

    // elapsed time (during the program run) at which the message was recorded
    private double totalSec = 0.0D;

    /* -------------------------------------------------------------------- */

    /////////////////
    // public methods
    /////////////////

    /** constructor.
    * @param message the message that was recorded
    * @param dateTime the date/time at which the message was recorded
    * @param totalSec the total number of seconds that had elapsed during the
    *    program's run by the time the message was recorded
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public TimeStampedMessage (String message, String dateTime,
        double totalSec)
    {
        this.message = message;
	this.dateTime = dateTime;
	this.totalSec = totalSec;
	return;
    }

    /* -------------------------------------------------------------------- */

    /** accessor method for the recorded message.
    * @return String the message that was recorded
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public String getMessage ()
    {
        return this.message;
    }

    /* -------------------------------------------------------------------- */

    /** accessor method for the date/time at which the message was recorded.
    * @return String contains the date/time at which the message was recorded
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public String getDateTime ()
    {
        return this.dateTime;
    }

    /* -------------------------------------------------------------------- */

    /** accessor method for the runtime at which the message was recorded.
    * @return double the total number of seconds (in this run of the program)
    *    that had elapsed by the time the message was recorded.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public double getSeconds ()
    {
        return this.totalSec;
    }
}

/*
* $Log$
* Revision 1.2  2004/07/21 20:51:14  mbw
* javadocs edits only
*
* Revision 1.1  2003/12/30 16:56:50  mbw
* imported into this product
*
* Revision 1.1  2003/12/01 13:01:21  jsb
* Added as part of JSAM code review
*
* $Copyright$
*/
