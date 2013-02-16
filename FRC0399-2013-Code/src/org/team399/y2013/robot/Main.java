/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team399.y2013.robot;


import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import org.team399.y2013.robot.Systems.Arm;
import org.team399.y2013.robot.Systems.DriveTrain;
import org.team399.y2013.robot.Systems.Feeder;
import org.team399.y2013.robot.Systems.Intake;
import org.team399.y2013.robot.Systems.Shooter;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Main extends IterativeRobot {
    Joystick leftJoy = new Joystick(Constants.DRIVER_LEFT_USB);
    Joystick rightJoy = new Joystick(Constants.DRIVER_RIGHT_USB);
    Joystick operatorJoy = new Joystick(Constants.OPERATOR_USB);
    
    Arm arm = Arm.getInstance();
    DriveTrain drive = new DriveTrain(Constants.DRIVE_LEFT_A, 
                                      Constants.DRIVE_LEFT_B, 
                                      Constants.DRIVE_RIGHT_A, 
                                      Constants.DRIVE_RIGHT_B);
    Feeder feeder = new Feeder(Constants.FEEDER_MOTOR,
                               Constants.KICKER_PORT);
    Intake intake = new Intake(Constants.INTAKE_MOTOR, 
                               Constants.INTAKE_SENSOR);
    Talon winch = new Talon(Constants.WINCH_PORT);
    Compressor comp = new Compressor(Constants.COMPRESSOR_SWITCH, 
                                     Constants.COMPRESSOR_RELAY);
    Shooter shooter = new Shooter();
    
    
    
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        shooter.start();
        comp.start();
        shooter = new Shooter();
        arm.setEnabled(true);
        arm.setPointAngle(5.0);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }
   
    Solenoid solenoids[] = {new Solenoid(1), 
        new Solenoid(2), 
        new Solenoid(3), 
        new Solenoid(4)};
    
    public void testPeriodic() {
       for(int i = 0; i < solenoids.length; i++) {
           solenoids[i].set(true);
           Timer.delay(.125);
           solenoids[i].set(false);
           Timer.delay(.125);
           
       }
    }   

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        double multiplier = 1.0;
        
        
        if(leftJoy.getRawButton(6)) {
            winch.set(rightJoy.getRawAxis(2));
        } else {
            winch.set(0);
            drive.tankDrive(leftJoy.getRawAxis(2)*multiplier, rightJoy.getRawAxis(2)*multiplier);
        }
        feeder.setBelt(operatorJoy.getRawAxis(2));
        if(operatorJoy.getRawButton(6)) {
            //feeder.setBelt(1.0);
            feeder.setKicker(true);
        } else {
            feeder.setKicker(false);
            
        }
        operator();
    }
    
    public void operator() {
        double shooterSet = 0.0;
        double armSet = 0;
        
        armSet =  arm.getSetpoint() + (operatorJoy.getRawAxis(2) * Constants.ARM_MANUAL_INPUT_SCALAR);
        armSet = operatorJoy.getRawAxis(4);
        if(operatorJoy.getRawButton(1)) {
            shooterSet = 4000.0;
            //shooter.setMotors(1.0);
        } else if(operatorJoy.getRawButton(2)) {
            //shooterSet = 6000.0;
            //shooter.setMotors(.75);
        } else if(operatorJoy.getRawButton(5)) {
            //shooter.setMotors(-.375);
        } else {
            //shooter.setMotors(0);
            shooterSet = 0.0;
        }
        shooter.setShooterSpeed(shooterSet);
        arm.setPointAngle(armSet);
        arm.setPointAngle(armSet);
    }
}