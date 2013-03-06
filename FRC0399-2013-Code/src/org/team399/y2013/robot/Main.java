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
import org.team399.y2013.robot.Systems.Climber;

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
    
    Arm arm = null;
    Shooter shooter = Shooter.getInstance();
    DriveTrain drive = new DriveTrain(Constants.DRIVE_LEFT_A,
            Constants.DRIVE_LEFT_B,
            Constants.DRIVE_RIGHT_A,
            Constants.DRIVE_RIGHT_B);
    Feeder feeder = new Feeder(Constants.FEEDER_MOTOR,
            Constants.KICKER_PORT);
    Intake intake = new Intake(Constants.INTAKE_MOTOR,
            Constants.INTAKE_SENSOR);
    Climber climber = new Climber(Constants.WINCH_PORT);
    Compressor comp = new Compressor(Constants.COMPRESSOR_SWITCH,
            Constants.COMPRESSOR_RELAY);

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        shooter.start();
        arm =  Arm.getInstance();
        comp.start();
        arm.setEnabled(true);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    }

    public void disabledPeriodic() {
        arm.setPointAngle(5.18);    //Set arm setpoint to stowed up when disabled
        updateDashboard();          //Update diagnostic dashboard

       // For safety, require a disable to change shooter and arm tuning constants using SDB
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
        Timer.delay(.001);
        updateDashboard();  //Update diagnostic dashboard

        if(leftJoy.getRawButton(6)) {
            climber.set(Constants.CLIMBER_UP_SPEED);
        } else if(leftJoy.getRawButton(7)) {
            climber.set(Constants.CLIMBER_DOWN_SPEED);
        } else {
            climber.set(0);
        }

        drive.tankDrive(leftJoy.getRawAxis(2), rightJoy.getRawAxis(2));
        //drive.cheesyDrive(rightJoy.getRawAxis(2), leftJoy.getRawAxis(1), (Math.abs(leftJoy.getRawAxis(1)) > .5) && (Math.abs(rightJoy.getRawAxis(2)) < .25));

        drive.setShifter(rightJoy.getRawButton(1));
     //   operator();
    }
    double armSet = 5.18;

    public void operator() {

        double manScalar = SmartDashboard.getNumber("ARM_MAN_SCAL", Constants.ARM_MANUAL_INPUT_SCALAR);

        if (operatorJoy.getButton(5)) {
            feeder.setBelt(1.0);
        } else if (operatorJoy.getButton(7)) {
            feeder.setBelt(-1.0);
        } else {
            feeder.setBelt(0);
        }
        if (operatorJoy.getButton(6)) {
            //feeder.setBelt(1.0);
            feeder.setKicker(false);
        } else {
            feeder.setKicker(true);

        }


        double shooterSet = 0.0;
        if (operatorJoy.getButton(1)) {
            shooterSet = 4000.0;
        } else if (operatorJoy.getButton(2)) {
            shooterSet = 6000.0;
        } else if (operatorJoy.getButton(3)) {
            shooterSet = 8600;
        } else if (operatorJoy.getButton(8)) {
            shooterSet = -2500;
        } else {
            shooterSet = 0.0;
        }

        if (operatorJoy.getDPad(GamePad.DPadStates.LEFT)) {
            armSet = Constants.HUMAN_LOAD;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.DOWN)) {
            armSet = Constants.ARM_UPPER_LIM;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.RIGHT)) {
            armSet = Constants.HIGH_SHOT;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.UP)) {
            armSet = Constants.STOW_UP;
        } else {
            if (Math.abs(operatorJoy.getLeftY()) > .5) {
                armSet = arm.getSetpoint() + manScalar * EagleMath.signum(operatorJoy.getLeftY());
            } else if (EagleMath.isInBand(Math.abs((float) operatorJoy.getLeftY()), (float) .125, (float) .499)) {
                armSet = arm.getSetpoint() + (manScalar * .5) * EagleMath.signum(operatorJoy.getLeftY());
            }
        }
        shooter.setShooterSpeed(shooterSet);
        arm.setPointAngle(armSet);
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("Shooter Actual Velocity", shooter.getVelocity());     //shooter current vel
        SmartDashboard.putNumber("Shooter Set Velocity", shooter.getShooterSetSpeed()); //Shooter set vel
        SmartDashboard.putNumber("Arm Actual Position", arm.getActual());               //arm actual pos
        SmartDashboard.putNumber("Arm Set Position", arm.getSetpoint());                //arm set pos
        SmartDashboard.putNumber("Arm offset", arm.getActual()-Constants.ARM_LOWER_LIM);//Arm offset from vertical most limt
        SmartDashboard.putNumber("Drive Yaw", drive.getYaw());                          //Drivetrain yaw angle
        //SmartDashboard.putNumber("Drive Pitch", drive.getPitch());                    //Drivetrain pitch angle - zeroed out
       
    }
}