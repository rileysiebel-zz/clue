package solver;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/** An object in this class acts as a timer, indicating both how much
 * time has elapsed from the time the object was created, and how much
 * time remains until the end of a specified period.  The time
 * measured on most machines is CPU usage; when this is not supported,
 * elapsed clock time is used instead.
 */
public class Timer {

    /** This constructor begins a timer which will run for a period
     * specified by the <tt>max_time</tt> parameter.
     * @param max_time timer duration in milliseconds
     */
    public Timer(long max_time) {
	tb = ManagementFactory.getThreadMXBean();

	cpu_time = tb.isThreadCpuTimeSupported();

	start_time =  get_time();
	end_time = start_time + max_time;
    }

    /** Returns the time (in milliseconds) that has elapsed since the
     * creation of this object.
     */
    public long getTimeElapsed() {
	return (get_time() - start_time);
    }

    /** Returns the time (in milliseconds) that remains until the
     * timer object reaches the end of the period specified at its
     * creation.
     */
    public long getTimeRemaining() {
	return (end_time - get_time());
    }

    // private stuff

    private long start_time;
    private long end_time;
    private static final long milli_to_nano = 1000000;
    private ThreadMXBean tb;
    private boolean cpu_time;

    private long get_time() {
	if (cpu_time)
	    return tb.getCurrentThreadCpuTime() / milli_to_nano;
	else
	    return System.currentTimeMillis();
    }

}
