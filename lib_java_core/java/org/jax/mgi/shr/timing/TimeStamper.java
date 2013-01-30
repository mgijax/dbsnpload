package org.jax.mgi.shr.timing;

/*
* $Header$
* $Name$
*/

/**
* @module TimeStamper.java
* @author jsb
*/

import java.util.Vector;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import org.jax.mgi.shr.stringutil.Sprintf;

/** provides a mechanism for use in simplistic profiling of code.  "Simplistic
*   profiling" means that a programmer can use a TimeStamper to collect and
*   timestamp messages during the run of some code, then examine these
*   timestamps to see where the code may have bottlenecks.  A TimeStamper
*   collects timestamped messages in memory and does not write them out.  In
*   addition to a date/time for each message, we also keep a run-time (to the
*   millisecond level) for each entry, noting how long into the program's run
*   the message was recorded.
*  see description above
* @has a List of TimeStampedMessage objects
* @does provides public methods to...
*    <OL>
*    <LI> record a message
*    <LI> start the timer used to measure elapsed times
*    <LI> retrieve a <tt>List</tt> of <tt>TimeStampedMessage</tt> objects
*	collected so far (one per recorded message)
*    <LI> clear the TimeStamper to restart the timer and remove all messages
*	collected so far
*    <LI> return a String representation of the TimeStamper
*    </OL>
*/
public class TimeStamper
{
    /////////////////////
    // instance variables
    /////////////////////

    // the timer to be used to measure a running time when recording a message
    private Stopwatch timer = null;

    // used to format a date/time string for each message recorded
    private SimpleDateFormat dateFormatter = null;

    // collects TimeStampedMessage objects
    private Vector messages = null;

    // is recording of messages on?
    private boolean isOn = true;

    /* -------------------------------------------------------------------- */

    /** default constructor.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public TimeStamper()
    {
	// initialize all instance variables

	this.timer = new Stopwatch();
	this.dateFormatter =
	    (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
	this.dateFormatter.applyPattern ("MM/dd/yyyy HH:mm:ss");
	this.messages = new Vector();
	this.isOn = true;
	return;
    }

    /* -------------------------------------------------------------------- */

    /** constructor with optional "isOn" parameter
    * @param isOn boolean; true if we should do recording of messages, false
    *    if not
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public TimeStamper(boolean isOn)
    {
	// initialize all instance variables

	this.timer = new Stopwatch();
	this.dateFormatter =
	    (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
	this.dateFormatter.applyPattern ("MM/dd/yyyy HH:mm:ss");
	this.messages = new Vector();
	this.isOn = isOn;
	return;
    }

    /* -------------------------------------------------------------------- */

    /** time-stamp and record the given 'message'
    * @param message the String to be recorded
    * @return nothing
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public void record (String message)
    {
	this.messages.add (this.buildEntry (message));
	return;
    }

    /* -------------------------------------------------------------------- */

    /** start the internal timer that is used to measure elapsed times.  By
    *    default, the timer starts running when the first message is recorded.
    *    Using this method allows you to start the timer earlier than that.
    * @return nothing
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public void startTimer ()
    {
        this.timer.start();
	return;
    }

    /* -------------------------------------------------------------------- */

    /** retrieve the messages recorded so far
    * @return List of TimeStampedEntry objects, in order of ascending time.
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    * @notes This method returns the actual List, not a clone of it, so use
    *    it judiciously.
    */
    public List getMessages ()
    {
        return this.messages;
    }

    /* -------------------------------------------------------------------- */

    /** return this object to its initial state, discarding any collected
    *   messages and restarting the timer
    * @return nothing
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public void clear ()
    {
	this.timer.reset();
	this.messages.clear();
	return;
    }

    /* -------------------------------------------------------------------- */

    /** represent this object and its collected messages as a String.  This
    *   String will include embedded '\n' characters to delimit individual
    *   lines.
    * @return String see Purpose above
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    public String toString ()
    {
	// shows the number with 3 places left and 2 places right of the
	// decimal point
	DecimalFormat df = new DecimalFormat("000.00");

	// shows the number as a percentage (after multiplying by 100) with
	// one digit right of the decimal point
	DecimalFormat pf = new DecimalFormat("00.0%");

	// collects all output to be returned
        StringBuffer sb = new StringBuffer();

	// total number of messages to be formatted
	int messageCount = this.messages.size();

	// current message (as we iterate through them all)
	TimeStampedMessage msg = null;

	// time (in seconds) at which the current message was recorded
	double myTime = 0.0;

	// time (in seconds) at which the previous message was recorded
	double lastTime = 0.0;

	// time (in seconds) elapsed since the previous entry
	double elapsedTime = 0.0;

	// time (in seconds) at which the final message was recorded
	double totalTime = 0.0;
	if (messageCount > 0)
	{
	    totalTime = ((TimeStampedMessage) this.messages.get (
	        messageCount - 1)).getSeconds();
	}
	if (totalTime == 0.0)
	{
	    totalTime = 1.0;		// need to avoid divide-by-0 errors
	}

	String template = "%s %7.2f %6.2f %4.1f% %s\n";

	for (int i = 0; i < messageCount; i++)
	{
	    msg = (TimeStampedMessage) this.messages.get(i);
	    myTime = msg.getSeconds();
	    elapsedTime = myTime - lastTime;

	    sb.append (Sprintf.sprintf (template,
		msg.getDateTime(),
		new Double(myTime),
		new Double(elapsedTime),
		new Double(100.0 * elapsedTime / totalTime),
		msg.getMessage()
		) );

/*	    sb.append (msg.getDateTime());
	    sb.append (" ");
	    sb.append (df.format (myTime));
	    sb.append (" ");
	    sb.append (df.format (elapsedTime));
	    sb.append (" ");
	    sb.append (pf.format (elapsedTime / totalTime));
	    sb.append (" ");
	    sb.append (msg.getMessage());
	    sb.append ("\n");
*/
	    lastTime = myTime;
	}

	return sb.toString();
    }

    /* -------------------------------------------------------------------- */

    //////////////////
    // private methods
    //////////////////

    /** build and return a TimeStampedMessage for the given 'message'.
    * @return TimeStampedMessage containing the given 'message', with current
    *    timestamps
    * @assumes nothing
    * @effects nothing
    * @throws nothing
    */
    private TimeStampedMessage buildEntry (String message)
    {
	double elapsed = this.timer.time();
	if (!this.timer.isRunning())
	{
	    this.timer.start();
	}
	String dateTime = this.dateFormatter.format (new Date());

        return new TimeStampedMessage (message, dateTime, elapsed);
    }
}

/*
* $Log$
* Revision 1.4  2004/07/21 20:51:31  mbw
* javadocs edits only
*
* Revision 1.3  2004/02/25 20:21:09  mbw
* fixed to eliminate compiler warnings
*
* Revision 1.2  2004/02/10 16:11:36  jsb
* Updated formatting of output
*
* Revision 1.1  2003/12/30 16:56:50  mbw
* imported into this product
*
* Revision 1.1  2003/12/01 13:01:22  jsb
* Added as part of JSAM code review
*
* $Copyright$
*/
