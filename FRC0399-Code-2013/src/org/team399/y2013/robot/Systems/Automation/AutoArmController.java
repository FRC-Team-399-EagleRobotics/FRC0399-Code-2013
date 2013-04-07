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
    
    public AutoArmController(Arm arm, EagleEye eye) {
        this.arm = arm;
        this.eye = eye;
    }
    
    public void requestLock(boolean state) {
        wantLock = state;
    }
    
    public void start() {
        eye.start();
    }
    
    public double getArmSet() {
        return armSet;
    }
    
    public void run() {
        lockTriggerWatcher.set(wantLock);
        boolean isLock = lockTriggerWatcher.get();
        eye.requestNewImage(isLock);
        
        if(isLock) {
           // armSet = (eye.getHighestTarget().y-Constants.AUTO_AIM_ARM_Y_OFFSET) * Constants.AUTO_AIM_ARM_PXL_TO_ANGLE;
        } else {
            armSet = 0;
        }
    }
    
}
