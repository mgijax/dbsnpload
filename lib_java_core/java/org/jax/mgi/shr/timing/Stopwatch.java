package org.jax.mgi.shr.timing;

/** A Stopwatch object represents an actual real-life stopwatch.
* It can be used for timing the execution of blocks of code, and could
* be useful for debugging, logging, rough profiling, etc.
*/
public class Stopwatch {

	//////////////////
	// Public methods:
	//////////////////

	/** construct a Stopwatch object
	*/
	public Stopwatch () {
		this.reset();
	}

	/* ---------------------------------------------------------------- */

	/** start the stopwatch running, so we will be adding to the elapsed
	*	time.  If the stopwatch is currently running, invoking this
	*	method is a no-op.
	* @return void
	*/
	public void start () {
		if (!this.running) {
			this.running = true;
			this.lastStart = System.currentTimeMillis();
			}
	}

	/* ---------------------------------------------------------------- */

	/** stop the Stopwatch from running, ensuring that we are no longer
	*	adding to the elapsed time.  If the Stopwatch is not currently
	*	running, invoking this method is a no-op.
	* @return void
	*/
	public void stop () {
		if (this.running) {
			double now = System.currentTimeMillis();
			this.elapsed = this.elapsed + now - this.lastStart;
			this.lastStart = 0.0;
			this.running = false;
			}
	}

	/* ---------------------------------------------------------------- */

	/** get the amount of elapsed time (in seconds) measured by the
	*	Stopwatch to this point.  This is handled correctly whether
	*	the Stopwatch is currently running or not.
	* @return <tt>double</tt> - number of seconds which have elapsed
	*	while the Stopwatch was running
	*/
	public double time () {
		if (!this.running) {
			return this.elapsed / 1000.0;
			}
		else {
			double now = System.currentTimeMillis();
			return (this.elapsed + now - this.lastStart) / 1000.0;
			}
	}

	/* ---------------------------------------------------------------- */

	/** reset the Stopwatch to be stopped and to have no elapsed time
	* @return void
	*/
	public void reset () {
		this.elapsed = 0.0;
		this.lastStart = 0.0;
		this.running = false;
	}

	/* ---------------------------------------------------------------- */

	/** determine whether the Stopwatch is currently running
	* @return <tt>true</tt> if the Stopwatch is currently running
	*/
	public boolean isRunning() {
		return this.running;
		}

	/* ---------------------------------------------------------------- */

	//////////////////////
	// Instance Variables:
	//////////////////////

	private double elapsed = 0.0;		// total elapsed time so far
	private double lastStart = 0.0;		// time of last this.start()
	private boolean running = false;	// is stopwatch running now?
}
