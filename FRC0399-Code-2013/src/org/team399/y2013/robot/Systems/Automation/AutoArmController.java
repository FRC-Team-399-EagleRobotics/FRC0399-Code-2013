/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems.Automation;

import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.robot.Systems.Arm;
import org.team399.y2013.robot.Systems.Intake;
import org.team399.y2013.robot.Systems.Shooter;

/**
 *
 * @author Jeremy
 */
public class AutoArmController {
    private Arm arm;
    private Intake intake;
    private Shooter shooter;
    
    private double intakeSet = 0.0;
    private double armSet = 0.0;
    
    public AutoArmController(Arm arm, Intake intake, Shooter shooter) {
        this.arm = arm;
        this.intake = intake;
        this.shooter = shooter;
    }
    
    public void run() {
//        if(intake.isDiscPresent() && !EagleMath.isInBand(arm.getActual(), 
//                                     Arm.ArmSetpoints.INTAKE_LOAD - .05, 
//                                     Arm.ArmSetpoints.INTAKE_LOAD + .05)) { 
//            intakeSet = 0.0;
//        } else {
//            
//        }
//        
//        intake.set(intakeSet);
    }
     
    public void setIntake(double in) {
        this.intakeSet = in;
    }
    
    
}
