/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems.Automation;

import org.team399.y2013.Utilities.PulseTriggerBoolean;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Systems.Arm;
import org.team399.y2013.robot.Systems.Imaging.EagleEye;

/**
 *
 * @author Jeremy
 */
public class AutoArmController {
    private Arm arm;
    private EagleEye eye;
    
    private double armSet = 0.0;
    private boolean wantLock = false;
    
    private PulseTriggerBoolean lockTriggerWatcher = new PulseTriggerBoolean();
    
    public AutoArmController(Arm arm) {
        this.arm = arm;
    }
    
    public void setTargetData(boolean found, double x, double y, 
                              double azimuth, double altitude) {
        
    }
    
    public void requestLock(boolean state) {
    }
    
    public void start() {
    }
    
    public double getArmSet() {
        return 0;
    }
    
    public void run() {
        
    }
    
}
