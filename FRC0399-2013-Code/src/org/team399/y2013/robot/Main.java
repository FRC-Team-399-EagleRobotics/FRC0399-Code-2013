/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.team399.y2013.robot;


import edu.wpi.first.wpilibj.*;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.robot.Systems.Arm;
import org.team399.y2013.robot.Systems.DriveTrain;
import org.team399.y2013.robot.Systems.Feeder;
import org.team399.y2013.robot.Systems.Intake;
import org.team399.y2013.robot.Systems.Shooter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team399.y2013.Utilities.GamePad;

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
    GamePad operatorJoy = new GamePad(Constants.OPERATOR_USB);
    
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
    Shooter shooter = Shooter.getInstance();
    
    
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        shooter.start();
        comp.start();
        arm.setEnabled(true);
        
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }
   
    public void disabledPeriodic() {
        arm.setPointAngle(5.18);
        System.out.println("Arm current: " + arm.getActual());
        SmartDashboard.putNumber("Shooter Actual Velocity", shooter.getVelocity());
        SmartDashboard.putNumber("Shooter Set Velocity", shooter.getShooterSetSpeed());
        SmartDashboard.putNumber("Arm Actual Position", arm.getActual());
        SmartDashboard.putNumber("Arm Set Position", arm.getSetpoint());
        SmartDashboard.putNumber("Drive Yaw", drive.getYaw());
        SmartDashboard.putNumber("Drive Pitch", drive.getPitch());
        
        shooter.setTuningConstants(SmartDashboard.getNumber("SHOOTER_KT", Constants.SHOOTER_KT), 
                                   SmartDashboard.getNumber("SHOOTER_KO", Constants.SHOOTER_KO));
        arm.setPIDConstants(SmartDashboard.getNumber("ARM_P", Constants.ARM_P), 
                            SmartDashboard.getNumber("ARM_I", Constants.ARM_I),
                            SmartDashboard.getNumber("ARM_D", Constants.ARM_D)); 
        
    }   

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        double multiplier = 1.0;
        SmartDashboard.putNumber("Shooter Actual Velocity", shooter.getVelocity());
        SmartDashboard.putNumber("Shooter Set Velocity", shooter.getShooterSetSpeed());
        SmartDashboard.putNumber("Arm Actual Position", arm.getActual());
        SmartDashboard.putNumber("Arm Set Position", arm.getSetpoint());
        SmartDashboard.putNumber("Drive Yaw", drive.getYaw());
        SmartDashboard.putNumber("Drive Pitch", drive.getPitch());
        
        shooter.setTuningConstants(SmartDashboard.getNumber("SHOOTER_KT", Constants.SHOOTER_KT), 
                                   SmartDashboard.getNumber("SHOOTER_KO", Constants.SHOOTER_KO));
        arm.setPIDConstants(SmartDashboard.getNumber("ARM_P", Constants.ARM_P), 
                            SmartDashboard.getNumber("ARM_I", Constants.ARM_I),
                            SmartDashboard.getNumber("ARM_D", Constants.ARM_D)); 
        
        
       
        if(leftJoy.getRawButton(6)) {
            winch.set(rightJoy.getRawAxis(2));
            System.out.println("Running Winch!");
        } else {
            winch.set(0);
            //drive.filteredTankDrive(leftJoy.getRawAxis(2)*multiplier, rightJoy.getRawAxis(2)*multiplier);
            drive.tankDrive(leftJoy.getRawAxis(2)*multiplier, rightJoy.getRawAxis(2)*multiplier);
            //drive.cheesyDrive(rightJoy.getRawAxis(2), leftJoy.getRawAxis(1), (Math.abs(leftJoy.getRawAxis(1)) > .5) && (Math.abs(rightJoy.getRawAxis(2)) < .25));
        }
        
        drive.setShifter(rightJoy.getRawButton(1));
        operator();
    }
    double armSet = 5.18;
    public void operator() {
        
        double manScalar = SmartDashboard.getNumber("ARM_MAN_SCAL", Constants.ARM_MANUAL_INPUT_SCALAR);
        
        
        if(operatorJoy.getButton(5)) {
            feeder.setBelt(1.0);
        } else if(operatorJoy.getButton(7)) {
            feeder.setBelt(-1.0);
        } else {
            feeder.setBelt(0);
        }
        if(operatorJoy.getButton(6)) {
            //feeder.setBelt(1.0);
            feeder.setKicker(false);
        } else {
            feeder.setKicker(true);
            
        }
        
        
        double shooterSet = 0.0;
        
        //armSet = operatorJoy.getRawAxis(4);
        System.out.println("Arm Actual: " + arm.getActual());
        System.out.println("Arm Set: " + arm.getSetpoint());
        if(operatorJoy.getButton(1)) {
            shooterSet = 4000.0;
            //shooter.setMotors(1.0);
        //    armSet = 5.18;
        } else if(operatorJoy.getButton(2)) {
            shooterSet = 6000.0;
            //shooter.setMotors(.75);
            //armSet = Constants.HUMAN_LOAD;
        } else if(operatorJoy.getButton(3)) {
            shooterSet = 8600;
            //shooter.setMotors(-.375);
        } else if(operatorJoy.getButton(8)) {
            shooterSet = -2500;
            //shooter.setMotors(-.375);
        } else {
            //shooter.setMotors(0);
            shooterSet = 0.0;
        }
        
        if(operatorJoy.getDPad(GamePad.DPadStates.RIGHT)) {
            armSet = Constants.HUMAN_LOAD;
        } else if(operatorJoy.getDPad(GamePad.DPadStates.DOWN)) {
            armSet = Constants.ARM_UPPER_LIM;
        } else if(operatorJoy.getDPad(GamePad.DPadStates.LEFT)){
            armSet = Constants.HIGH_SHOT;
        } else if(operatorJoy.getDPad(GamePad.DPadStates.UP)){
            armSet = Constants.STOW_UP;
        } else {
            //armSet =  arm.getSetpoint() + (operatorJoy.getRawAxis(2) * Constants.ARM_MANUAL_INPUT_SCALAR);
            if(Math.abs(operatorJoy.getLeftY()) > .5) {
                armSet = arm.getSetpoint() + Constants.ARM_MANUAL_INPUT_SCALAR*EagleMath.signum(operatorJoy.getLeftY());
            } else if(EagleMath.isInBand(Math.abs((float)operatorJoy.getLeftY()), (float).125, (float).499)) {
                armSet = arm.getSetpoint() + (Constants.ARM_MANUAL_INPUT_SCALAR*.5)*EagleMath.signum(operatorJoy.getLeftY());
            }
        }
        shooter.setShooterSpeed(shooterSet);
        arm.setPointAngle(armSet);
    }
}