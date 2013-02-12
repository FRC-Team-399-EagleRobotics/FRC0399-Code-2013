/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team399.y2013.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
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
    Joystick driverJoy = new Joystick(Constants.DRIVER_USB);
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
    Shooter shooter = Shooter.getInstance();
    
    
    
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        shooter.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
    }
    
    public void operator() {
        double shooterSet = 0.0;
        arm.setPointAngle(arm.getSetpoint() + (operatorJoy.getRawAxis(2) * Constants.ARM_MANUAL_INPUT_SCALAR));
        if(operatorJoy.getRawButton(1)) {
            shooterSet = 4000.0;
        } else if(operatorJoy.getRawButton(2)) {
            shooterSet = 6000.0;
        } else {
            shooterSet = 0.0;
        }
        
        shooter.setShooterSpeed(shooterSet);
    }
    
}
