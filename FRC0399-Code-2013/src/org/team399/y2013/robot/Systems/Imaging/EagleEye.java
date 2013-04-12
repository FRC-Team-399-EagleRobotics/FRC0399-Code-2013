/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems.Imaging;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Relay;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.Utilities.PrintStream;
import org.team399.y2013.robot.Systems.Imaging.ImageProcessor.Target;

/**
 * Eagle Eye class. Encapsulates camera, ImageTracker, and the RGB light ring into one class.
 * @author Jeremy Germita, Justin S. Jackie P.
 * 
 */
public class EagleEye extends Thread {

    public LightRing ring;
    public Camera cam;
    private Target[] targets;
    private PrintStream m_ps = new PrintStream("[EAGLE-EYE] ");
    boolean m_run = true;   //Flag to wantRefresh/disable execution in the thread
    //Color Thresholds
    Color[] colors = {
        Color.fromRGB(0, 1, 0), //Green color
        Color.fromRGB(1, 0, 0), //Red color
        Color.fromRGB(0, 0, 1) //blue color
    };
    HSLThreshold[] thresholds = {
        new HSLThreshold(100, 130, 0, 255, 20, 255), //Green threshold
        new HSLThreshold(0, 45, 80, 255, 120, 255), //Red threshold
        new HSLThreshold(200, 240, 80, 255, 120, 255) //Blue threshold
    };

    /**
     * Constructor
     */
    public EagleEye() {
        //TODO: light ring code
        System.out.println("[EAGLE-EYE] Eagle Eye initialized");
    }
    boolean targetsFound = false;
    int colorIndex = 0;
    long timeLastTarget = 0;
    boolean wantRefresh = true;
    boolean idle = false;
    public double framerate = 0;

    public void init() {
        cam = new Camera(); //Init camera
        m_ps.println("Vision thread started...");

    }

    /**
     * Main run method. Call this for functionality.
     */
    public void run() {
        init();
        while (m_run) {
            
            if (cam.freshImage() && wantRefresh) {
                m_ps.println("Processing image from camera");
                long procStartTime = System.currentTimeMillis();
                targets = ImageProcessor.processImage(cam.getImage(), thresholds[0]);    //Process image from camera using given thresholds

                long timeElapsed = (System.currentTimeMillis() - procStartTime);
                framerate = EagleMath.truncate((1000.0 / (double) timeElapsed), 2);
                m_ps.println("Processing Images at " + framerate + " fps");
                
                if (targets != null && targets.length > 0) {    //If targets are detected
                    targetsFound = true;                        //Set flag to true
                    timeLastTarget = System.currentTimeMillis();
                } else {
                    targetsFound = false;
                }
                
                if(targetsFound) {
                    System.out.println("Targets Found");
                    for(int i = 0; i < targets.length; i++) {
                        System.out.println("Target " + i);
                        System.out.println(targets[i].toString());
                    }
                }
                wantRefresh = false;    //This is for oneshot logic
                
            } else {
                framerate = 0;
            }
            
            try {
                Thread.sleep(10);
            } catch (Exception e) { }


        }
    }

    public synchronized void requestNewImage(boolean en) {
        wantRefresh = en;
    }

    public synchronized int getNumberOfTargets() {
        try {
            return getTargets().length;
        } catch (Exception e) {
            return 0;
        }
    }

    public synchronized void setIdle(boolean id) {
        if (!id && idle) {
            m_ps.println("Setting to idle...");
        } else if (id && !idle) {
            m_ps.println("Setting to active!");
        }
        this.idle = id;
    }

    /**
     * Return whether or not any target is found.
     * @return 
     */
    public boolean foundTarget() {
        return targetsFound;
    }

    /**
     * Get the entire array for targets
     * @return 
     */
    public Target[] getTargets() {
        return targets;
        
    }

    /**
     * Returns the target with the highest Y value
     * @return 
     */
    public Target getHighestTarget() {
        //this might be redundant
        if (targets == null) {
            return null;// new Target(0,0,0);
        }
        Target highest = targets[0];
        for (int i = 1; i < targets.length; i++) {
            if (targets[i].y < highest.y) {
                highest = targets[i];
            }
        }
        return highest;
    }
}