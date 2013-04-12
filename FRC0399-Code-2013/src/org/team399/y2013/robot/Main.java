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
import org.team399.y2013.Utilities.PulseTriggerBoolean;
import org.team399.y2013.robot.Autonomous.Shoot3AutonHigh;
import org.team399.y2013.robot.Autonomous.Shoot3AutonMid;
import org.team399.y2013.robot.Systems.Automation.AutoShootController;
import org.team399.y2013.robot.Systems.Climber;
import org.team399.y2013.robot.Systems.Imaging.EagleEye;

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
    public static Arm arm = null;
    public static Shooter shooter = Shooter.getInstance();
    public static DriveTrain drive = new DriveTrain(Constants.DRIVE_LEFT_A,
            Constants.DRIVE_LEFT_B,
            Constants.DRIVE_RIGHT_A,
            Constants.DRIVE_RIGHT_B);
    public static Feeder feeder = new Feeder(Constants.FEEDER_MOTOR,
            Constants.KICKER_PORT,
            Constants.FLAP_PORTA,
            Constants.FLAP_PORTB
            );
    public static Intake intake = new Intake(Constants.INTAKE_MOTOR,
            Constants.INTAKE_SENSOR);
    public static Climber climber = new Climber(Constants.WINCH_PORT, Constants.LIMIT_SWITCH_PORT);
    public static Compressor comp = new Compressor(Constants.COMPRESSOR_SWITCH,
            Constants.COMPRESSOR_RELAY);
    public static EagleEye eye = new EagleEye();
    public static AutoShootController autoshoot = new AutoShootController(shooter, feeder);
    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        shooter.start();
        arm = Arm.getInstance();
        comp.start();
        arm.setEnabled(true);
        eye.start();
        eye.requestNewImage(false);
    }
    
    public void disabledInit() {
        arm.setBrake(true);
        System.out.println("Robot is done initializing");
    }

    public void autonomousInit() {
        arm.setBrake(true);
        if (auton == 0) {
            Shoot3AutonHigh.start();
        } else if (auton == 1) {
            Shoot3AutonMid.start();
        }
        arm.setPointRotations(Constants.ARM_MID_SHOT);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        arm.autoZero();
        if (auton == 0) {
            Shoot3AutonHigh.run();
        } else if (auton == 1) {
            Shoot3AutonMid.run();
        }

    }
    int auton = 0;

    public void disabledPeriodic() {
        arm.autoZero();
        //System.out.println("offset" + (arm.getActual() - Constants.ARM_LOWER_LIM));
        arm.setPointRotations(Constants.ARM_STOW_UP);    //Set arm setpoint to stowed up when disabled
        updateDashboard();                               //Update diagnostic dashboard
        
        if (leftJoy.getRawButton(1)) {
            auton = 0;
        } else if (rightJoy.getRawButton(1)) {
            auton = 1;
        }

        if (auton == 0) {                               //Displays selected auton
            SmartDashboard.putString("Auton", "HIGH");
        } else if (auton == 1) {
            SmartDashboard.putString("Auton", "MID");
        } else {
            SmartDashboard.putString("Auton", "INVALID!");
        }
    }
    
    public void teleopInit() {
        arm.setBrake(true);
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
        eye.requestNewImage(camButtonOut);
        
        //System.out.println("offset" + (arm.getActual() - Constants.ARM_LOWER_LIM));
        if (leftJoy.getRawButton(6)) {
            climber.set(Constants.CLIMBER_UP_SPEED);
        } else if (leftJoy.getRawButton(7)) {
            climber.set(Constants.CLIMBER_DOWN_SPEED);
        } else {
            climber.set(0);
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
        
        if(drive.gear == Constants.LOW_GEAR) {
            leftAdjust *= .75;
            rightAdjust*= .75;
        }
        
        boolean shiftButton = rightJoy.getRawButton(1);
        drive.setShifter(shiftButton);
        
        if(leftAdjust != 0 || rightAdjust != 0) {
            drive.tankDrive(leftJoy.getRawAxis(2)+leftAdjust, rightJoy.getRawAxis(2)+rightAdjust);
        } else {
            drive.cheesyDrive
                    (drive.twoStickToTurning(leftJoy.getRawAxis(2), rightJoy.getRawAxis(2)), 
                     drive.twoStickToThrottle(leftJoy.getRawAxis(2), rightJoy.getRawAxis(2)));
        }
        
        arm.autoZero();
        
        operator();
    }
    double armSet = Constants.ARM_STOW_UP;
    PulseTriggerBoolean adjustUpButton = new PulseTriggerBoolean();
    PulseTriggerBoolean adjustDnButton = new PulseTriggerBoolean();
    public void operator() {

        double manScalar = SmartDashboard.getNumber("ARM_MAN_SCAL", Constants.ARM_MANUAL_INPUT_SCALAR);

        if (operatorJoy.getButton(5)) {
            feeder.setRoller(1.0);
        } else if (operatorJoy.getButton(7)) {
            feeder.setRoller(-1.0);
        } else {
            feeder.setRoller(0);
        }
        boolean wantShoot = operatorJoy.getButton(6);
        
        if (wantShoot) {
            //comment out feeder kicker lines for autoshoot
            
            feeder.setKicker(Constants.KICKER_OUT);
        } else {
            feeder.setKicker(Constants.KICKER_IN);
        }

        boolean isShooting = false; //Flag to indicate operator is running the shooter
        
        double shooterSet = Constants.SHOOTER_STOP;
        
        if (operatorJoy.getButton(1)) {
            isShooting = true;
            shooterSet = 6000.0;
        } else if (operatorJoy.getButton(2)) {
            isShooting = true;
            shooterSet = Constants.SHOOTER_SHOT;
        } else if (operatorJoy.getButton(3)) {
            isShooting = true;
            shooterSet = Constants.SHOOTER_SHOT;
        } else {
            isShooting = false;
            shooterSet = Constants.SHOOTER_STOP;
        }
        
        //autoshoot.run(shooterSet, wantShoot);
        
        if (operatorJoy.getButton(8) || leftJoy.getRawButton(1)) {
            feeder.setFlapper(Constants.FLAP_OUT);
        } else {
            feeder.setFlapper(Constants.FLAP_IN);
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
        } else if (camButtonOut && eye.getTargets()!=null){
            System.out.println("AutoAim");
            double visionOut = (40-(240-eye.getTargets()[0].y))*Constants.AUTO_AIM_ARM_PXL_TO_ANGLE;
            System.out.println("Autoaim output = " + visionOut);
            visionOut = EagleMath.cap(visionOut, -15/Constants.DEGREES_PER_TURN, 15/Constants.DEGREES_PER_TURN);
           armSet += visionOut;
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
                fineAdjustInput = 2.5/Constants.DEGREES_PER_TURN;
            } else {
                fineAdjustInput = 0;
            }
            fineAdjust *= EagleMath.deadband(fineAdjustInput, .05);
            
            armSet = arm.getSetpoint() + coarseAdjust + fineAdjust;
        }
        
        
        shooter.setShooterSpeed(shooterSet);
        arm.setPointRotations(armSet);
    }

    public void updateDashboard() {
        SmartDashboard.putNumber("Shooter Actual Velocity", shooter.getVelocity());     //shooter current vel
        SmartDashboard.putNumber("Shooter Set Velocity", shooter.getShooterSetSpeed()); //Shooter set vel
        SmartDashboard.putNumber("Shooter A current", shooter.getCurrent(0));
        SmartDashboard.putNumber("Shooter B current", shooter.getCurrent(1));
        SmartDashboard.putNumber("Shooter C current", shooter.getCurrent(2));
        SmartDashboard.putBoolean("Shooter IsAtTarget", shooter.isAtTargetSpeed());
        
        SmartDashboard.putNumber("Arm Actual Position", arm.getActual());               //arm actual pos
        SmartDashboard.putNumber("Arm Set Position", arm.getSetpoint());                //arm set pos
        SmartDashboard.putNumber("Arm offset", arm.getActual() - Constants.ARM_LOWER_LIM);//Arm offset from vertical most limt
        SmartDashboard.putNumber("Arm current", arm.getCurrentOutput());
        SmartDashboard.putBoolean("Arm CAN fault", arm.getCurrentOutput() == -1);
        SmartDashboard.putNumber("Arm Actual - Deg", arm.toDegrees(arm.getActual()));
        SmartDashboard.putNumber("Arm Set - Deg", arm.toDegrees(arm.getSetpoint()));
        
        SmartDashboard.putNumber("Left Drive Output", drive.leftOutput);
        SmartDashboard.putNumber("Right Drive Output", drive.rightOutput);
        
        SmartDashboard.putBoolean("Climber upper limit", climber.getSwitch());
        SmartDashboard.putBoolean("Arm Zero Switch", arm.getZeroSwitch());
       
        if(eye.getTargets() != null) {
            SmartDashboard.putNumber("LargestTarget Size", eye.getTargets()[0].area);
            SmartDashboard.putNumber("LargestTarget X", eye.getTargets()[0].x);
            SmartDashboard.putNumber("LargestTarget Y", eye.getTargets()[0].y);
        }
    }
}