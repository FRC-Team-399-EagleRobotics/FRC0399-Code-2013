/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.team399.y2013.robot;

import edu.wpi.first.wpilibj.*;
import org.team399.y2013.Utilities.EagleMath;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team399.y2013.Utilities.GamePad;
import org.team399.y2013.Utilities.PulseTriggerBoolean;
import org.team399.y2013.robot.Autonomous.Shoot2CenterlineD;
import org.team399.y2013.robot.Autonomous.Shoot3AutonHigh;
import org.team399.y2013.robot.Autonomous.Shoot3AutonMid;


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
    
    public static Robot robot = null;

    public Main() {
    }
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        robot = Robot.getInstance();
        System.out.println("Robot Done Initializing...");
        System.out.println("System states at boot: ");
        System.out.println("Arm Position: " + robot.arm.getActual());
    }
    
    public void disabledInit() {
        
        robot.arm.setBrake(true);
    }

    public void autonomousInit() {
        robot.arm.setBrake(true);
        if (auton == 0) {
            Shoot3AutonHigh.start();
        } else if (auton == 1) {
            Shoot3AutonMid.start();
        } else if (auton == 2) {
            Shoot2CenterlineD.start();
        }
        robot.arm.setPointRotations(Constants.ARM_MID_SHOT);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        robot.arm.autoZero();
        if (auton == 0) {
            Shoot3AutonHigh.run();
        } else if (auton == 1) {
            Shoot3AutonMid.run();
        } else if (auton == 2) {
            Shoot2CenterlineD.run();
        }

    }
    int auton = 0;

    public void disabledPeriodic() {
        robot.arm.autoZero();
        
        robot.arm.setPointRotations(Constants.ARM_STOW_UP);    //Set arm setpoint to stowed up when disabled
        updateDashboard();                               //Update diagnostic dashboard
        
        if (leftJoy.getRawButton(1)) {
            auton = 0;
        } else if (rightJoy.getRawButton(1)) {
            auton = 1;
        } else if(leftJoy.getRawButton(2)) {
            auton = 2;
        } else {
            //auton = ((Integer)autonChooser.getSelected()).intValue();
        }
        
        
        if (auton == 0) {                               //Displays selected auton
            SmartDashboard.putString("Auton", "HIGH");
        } else if (auton == 1) {
            SmartDashboard.putString("Auton", "MID");
        } else if (auton == 2) {
            SmartDashboard.putString("Auton", "ANTICENTERLINE");
        } else {
            SmartDashboard.putString("Auton", "INVALID!");
        }
    }
    
    public void teleopInit() {
        robot.arm.setBrake(true);
    }

    PulseTriggerBoolean cameraButton = new PulseTriggerBoolean();
    boolean camButtonOut = false;
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Timer.delay(.001);
        updateDashboard();  //Update diagnostic dashboard
        
        cameraButton.set(rightJoy.getRawButton(8));
        camButtonOut = cameraButton.get();
        
        //System.out.println("offset" + (arm.getActual() - Constants.ARM_LOWER_LIM));
        if (leftJoy.getRawButton(6)) {
            robot.climber.set(Constants.CLIMBER_UP_SPEED);
        } else if (leftJoy.getRawButton(7)) {
            robot.climber.set(Constants.CLIMBER_DOWN_SPEED);
        } else {
            robot.climber.set(0);
        }
        
        double leftAdjust = 0;
        double rightAdjust = 0;
        
        if(leftJoy.getRawButton(4)) {
            leftAdjust = .25;
            rightAdjust = -.25;
        }
        if(leftJoy.getRawButton(5)) {
            leftAdjust = -.25;
            rightAdjust = .25;
        }
        if(leftJoy.getRawButton(2)) {
            leftAdjust = .25;
            rightAdjust = .25;
        }
        if(leftJoy.getRawButton(3)) {
            leftAdjust = -.25;
            rightAdjust = -.25;
        }
        
        if(robot.drive.gear == Constants.LOW_GEAR) {
            leftAdjust *= .75;
            rightAdjust*= .75;
        }
        
        if(leftJoy.getRawButton(8)) {
            double aim = autoYaw();
            leftAdjust = -aim;
            rightAdjust = aim;
        }
        
        
        boolean shiftButton = rightJoy.getRawButton(1);
        robot.drive.setShifter(shiftButton);
        
        if(leftAdjust != 0 || rightAdjust != 0) {
            robot.drive.tankDrive(leftJoy.getRawAxis(2)+leftAdjust, rightJoy.getRawAxis(2)+rightAdjust);
        } else {
            robot.drive.cheesyDrive
                    (robot.drive.twoStickToTurning(leftJoy.getRawAxis(2), rightJoy.getRawAxis(2)), 
                     robot.drive.twoStickToThrottle(leftJoy.getRawAxis(2), rightJoy.getRawAxis(2)));
        }
        
        robot.arm.autoZero();
        
        operator();
    }
    
    double autoYaw() {
        double x = SmartDashboard.getNumber("TargetX", 0.0);
        x = EagleMath.cap(x, -.3, 3);
        if(Math.abs(x) < .1) {
            x = 0;
        }
        return x;
    }
    
    double autoPitch() {
        SmartDashboard.putNumber("pitch", 90.0-robot.arm.toDegrees(robot.arm.getActual()));
        double altitude = SmartDashboard.getNumber("altitude", 0.0);
        if(cameraButton.get()) {
            altitude = -altitude + 90;
            altitude = robot.arm.fromDegrees(altitude);
            return altitude;
        } else {
            return 0;
        }
    }
    
    double armSet = Constants.ARM_STOW_UP;
    PulseTriggerBoolean adjustUpButton = new PulseTriggerBoolean();
    PulseTriggerBoolean adjustDnButton = new PulseTriggerBoolean();
    public void operator() {

        double manScalar = Constants.ARM_MANUAL_INPUT_SCALAR;

        if (operatorJoy.getButton(5)) {
            robot.feeder.setRoller(1.0);
        } else if (operatorJoy.getButton(7)) {
            robot.feeder.setRoller(-1.0);
        } else {
            robot.feeder.setRoller(0);
        }
        boolean wantShoot = operatorJoy.getButton(6);
        
        if (wantShoot) {
            robot.feeder.setKicker(Constants.KICKER_OUT);
        } else {
            robot.feeder.setKicker(Constants.KICKER_IN);
        }

        boolean isShooting = false; //Flag to indicate operator is running the shooter
        
        double shooterSet = Constants.SHOOTER_STOP;
        
        if (operatorJoy.getButton(1)) {
            isShooting = true;
            shooterSet = 1800.0;
        } else if (operatorJoy.getButton(2)) {
            isShooting = true;
            shooterSet = 2500.0;
        } else if (operatorJoy.getButton(3)) {
            isShooting = true;
            shooterSet = Constants.SHOOTER_SHOT;
        }
        else {
            isShooting = false;
            shooterSet = Constants.SHOOTER_STOP;
        }
        
        //autoshoot.run(shooterSet, wantShoot);
        
        if (operatorJoy.getButton(8) || leftJoy.getRawButton(1)) {
            robot.feeder.setFlapper(Constants.FLAP_OUT);
        } else {
            robot.feeder.setFlapper(Constants.FLAP_IN);
        }

        adjustUpButton.set(operatorJoy.getButton(9));
        adjustDnButton.set(operatorJoy.getButton(10));
        
        if (operatorJoy.getDPad(GamePad.DPadStates.LEFT)) {
            armSet = Constants.ARM_HIGH_SHOT;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.DOWN) || rightJoy.getRawButton(2)) {
            armSet = Constants.ARM_UPPER_LIM;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.RIGHT)) {
            armSet = Constants.ARM_MID_SHOT;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.UP)) {
            armSet = Constants.ARM_STOW_UP;
        } else {
            double fineAdjust = 1;//(Constants.ARM_MANUAL_INPUT_SCALAR);
//            if(Math.abs(operatorJoy.getRightY()) > .2) {
//                fineAdjust *= operatorJoy.getRightY();
//            } else {
//                fineAdjust = 0;
//            }
//            
            double fineAdjustInput = 0;
            double coarseAdjust = manScalar * EagleMath.signum(operatorJoy.getLeftY());
            if(isShooting) {
                fineAdjustInput = 0;
                //coarseAdjust = 0;;
                fineAdjust = 0;
            } else {
                fineAdjustInput = operatorJoy.getRightY();
            }
            
            if(adjustUpButton.get()) {
                fineAdjustInput = -2.5/Constants.DEGREES_PER_TURN;
            } else if(adjustDnButton.get()) {
                fineAdjustInput =  2.5/Constants.DEGREES_PER_TURN;
            } else {
                fineAdjustInput =  0;
            }
            
            if(leftJoy.getRawButton(9)) {
                fineAdjustInput = -autoPitch();
            }
            fineAdjust *= EagleMath.deadband(fineAdjustInput, .05);
            
            armSet = robot.arm.getSetpoint() + coarseAdjust + fineAdjust;
        }
        
        
        robot.shooter.setShooterSpeed(shooterSet);
        robot.arm.setPointRotations(armSet);
    }
    
    

    public void updateDashboard() {
        SmartDashboard.putNumber("Shooter Actual Velocity", robot.shooter.getVelocity());     //shooter current vel
        SmartDashboard.putNumber("Shooter Set Velocity", robot.shooter.getShooterSetSpeed()); //Shooter set vel
        SmartDashboard.putNumber("Shooter A current", robot.shooter.getCurrent(0));
        SmartDashboard.putNumber("Shooter B current", robot.shooter.getCurrent(1));
        SmartDashboard.putNumber("Shooter C current", robot.shooter.getCurrent(2));
        SmartDashboard.putBoolean("Shooter IsAtTarget", robot.shooter.isAtTargetSpeed());
        
        SmartDashboard.putNumber("Arm Actual Position", robot.arm.getActual());               //arm actual pos
        SmartDashboard.putNumber("Arm Set Position", robot.arm.getSetpoint());                //arm set pos
        SmartDashboard.putNumber("Arm offset", robot.arm.getActual() - Constants.ARM_LOWER_LIM);//Arm offset from vertical most limt
        SmartDashboard.putNumber("Arm current", robot.arm.getCurrentOutput());
        SmartDashboard.putBoolean("Arm CAN fault", robot.arm.getCurrentOutput() == -1);
        SmartDashboard.putNumber("Arm Actual - Deg", robot.arm.toDegrees(robot.arm.getActual()));
        SmartDashboard.putNumber("Arm Set - Deg", robot.arm.toDegrees(robot.arm.getSetpoint()));
        
        //SmartDashboard.putNumber("")
        
        SmartDashboard.putNumber("Left Drive Output", robot.drive.leftOutput);
        SmartDashboard.putNumber("Right Drive Output", robot.drive.rightOutput);
        
        SmartDashboard.putBoolean("Climber upper limit", robot.climber.getSwitch());
        SmartDashboard.putBoolean("Arm Zero Switch", robot.arm.getZeroSwitch());
    }
}