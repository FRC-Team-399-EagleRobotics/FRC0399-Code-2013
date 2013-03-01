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
public class AutonomousTimerThread extends Thread{
    private long startTime = 0, timeElapsed = 0;
    private boolean running = false;
    
    /**
     * Constructor
     */
    public AutonomousTimerThread() {
        
    }
    
    /**
     * Start the thread. Also resets the time elapsed counter
     */
    public synchronized void start() {
        running = true;
        startTime = System.currentTimeMillis();
        timeElapsed = 0;
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
    
    /**
     * Return the time elapsed since start
     * @return 
     */
    public synchronized long get() {
        return timeElapsed;
    }
    
    /**
     * Flag indicating that elapsed time since start is within two times
     * timeStart < timeElapsed < timeEnd
     * @param timeStart
     * @param timeEnd
     * @return 
     */
    public boolean isWithinTime(long timeStart, long timeEnd) {
        return timeElapsed >= timeStart && timeElapsed <= timeEnd;
    }
    
    /**
     * Thread run method. don't call this in user code
     */
    public void run() {
        startTime = System.currentTimeMillis();
        while(running) {
            timeElapsed = System.currentTimeMillis() - startTime;
        }
    }
    
}
