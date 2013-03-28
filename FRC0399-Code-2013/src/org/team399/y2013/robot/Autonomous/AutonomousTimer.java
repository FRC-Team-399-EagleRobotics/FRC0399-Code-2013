/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

/**
 * Threaded autonomous timer. Has various utilities to facilitate the writing of
 * autonomous.
 * @author Jeremy
 */
public class AutonomousTimer extends Thread{
    private long startTime = 0, timeElapsed = 0;
    private boolean running = false;
    private Object synch = new Object(); // Used for thread synchronization
    
    /**
     * Constructor
     */
    public AutonomousTimer() {
        
    }
    
    /**
     * Start the thread. Also resets the time elapsed counter
     */
    public synchronized void start() {
        running = true;
    }
    
    /**
     * Stops thread. resets counter
     */
    public synchronized void stop() {
        running = false;
        timeElapsed = 0;
    }
    
    /**
     * Resets the thread
     */
    public synchronized void reset() {
        start();
        stop();
    }
    
    private void set(long elapsed) {
        synchronized(synch)
        {
          timeElapsed =  elapsed;
        }
    }
    
    /**
     * Return the time elapsed since start
     * @return 
     */
    public long get() {
        long elapsed;
        synchronized(synch)
        {
            elapsed = timeElapsed;
        }
        return elapsed;
    }
    
    /**
     * Flag indicating that elapsed time since start is within two times
     * timeStart < timeElapsed < timeEnd
     * @param timeStart
     * @param timeEnd
     * @return 
     */
    public boolean isWithinTime(long timeStart, long timeEnd) {
        long elapsed = get();
        return elapsed >= timeStart && elapsed <= timeEnd;
    }
    
    /**
     * Thread run method. don't call this in user code
     */
    public void run() {
        startTime = System.currentTimeMillis();
        
        set(0);
        while(running) {
            set(System.currentTimeMillis() - startTime);
            try{Thread.sleep(10);}catch(Exception TIE){} // delay so thread doesn't consume all CPU power
        }
    }
    
}
