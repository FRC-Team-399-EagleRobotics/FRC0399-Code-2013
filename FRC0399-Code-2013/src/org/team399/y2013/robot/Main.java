/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package org.team399.y2013.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import org.team399.y2013.Utilities.EagleMath;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team399.y2013.Utilities.GamePad;
import org.team399.y2013.Utilities.PulseTriggerBoolean;
import org.team399.y2013.robot.Autonomous.Shoot2CenterlineD;
import org.team399.y2013.robot.Autonomous.Shoot3AutonHigh;
import org.team399.y2013.robot.Autonomous.Shoot3AutonMid;
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
    GamePad operatorJoy = new GamePad(Constants.OPERATOR_USB);
    public static Robot robot = null;
    SendableChooser autonChooser = new SendableChooser();
    SendableChooser defArmPositionChooser = new SendableChooser();

    public Main() {
    }

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        robot = Robot.getInstance();
        autonChooser.addObject("HIGH", (new Integer(0)));
        autonChooser.addObject("MID", (new Integer(1)));
        autonChooser.addObject("ANTICENTERLINE", (new Integer(2)));
        autonChooser.addObject("DONOTHING", (new Integer(99)));
        SmartDashboard.putData("autonchooser", autonChooser);

        defArmPositionChooser.addObject("UP-STOW", (new Integer(0)));
        defArmPositionChooser.addObject("DOWN-STOW", (new Integer(0)));
        defArmPositionChooser.addObject("UNDER-PYR-STOW", (new Integer(0)));
        defArmPositionChooser.addObject("CURRENT-POS", (new Integer(0)));
        SmartDashboard.putData("arm_position_chooser", defArmPositionChooser);
        System.out.println("Robot Done Initializing...");
        System.out.println("System states at boot: ");
        System.out.println("Arm Position: " + robot.arm.getActual());
        System.out.println("Drive Yaw: " + robot.drive.getYaw());
        System.out.println("");
    }

    public void disabledInit() {
        robot.arm.setBrake(true);
        robot.arm.setPointRotations(Constants.ARM_STOW_UP);    //Set arm setpoint to stowed up when disabled
    }
    int auton = 0;

    public void disabledPeriodic() {
        robot.arm.autoZero();
        updateDashboard();                               //Update diagnostic dashboard
        //SmartDashboard.putData("autonchooser", autonChooser);

        if (leftJoy.getRawButton(1)) {
            auton = 0;
        } else if (rightJoy.getRawButton(1)) {
            auton = 1;
        } else if (leftJoy.getRawButton(2)) {
            auton = 2;
        } else {
            auton = ((Integer) autonChooser.getSelected()).intValue();
        }

        int defaultArmPositionIndex = ((Integer) autonChooser.getSelected()).intValue();
        double armSet = 0;
        if (defaultArmPositionIndex == 0) {          //Default to stow up once enabled
            armSet = Constants.ARM_STOW_UP;
        } else if (defaultArmPositionIndex == 1) {   //Default to stow down once enabled
            armSet = Constants.ARM_STOW_DOWN;
        } else if (defaultArmPositionIndex == 2) {   //default to angle stow once enabled
            armSet = Constants.ARM_STOW_UP + .2;    //tune this for an under pyramid stow for the centerline auton
        } else if (defaultArmPositionIndex == 3) {   //default to current arm position once enabled
            armSet = robot.arm.getActual();
        } else {
            armSet = Constants.ARM_STOW_UP;
        }

        robot.arm.setPointRotations(armSet);    //Set arm setpoint to stowed up when disabled

        String autonName = "";
        if (auton == 0) {                               //Displays selected auton
            autonName = "HIGH";
        } else if (auton == 1) {
            autonName = "MID";
        } else if (auton == 2) {
            autonName = "ANTICENTERLINE";
        } else if (auton == 99) {
            autonName = "DONOTHING";
        } else {
            autonName = "INVALID";
        }
        SmartDashboard.putString("Auton", autonName);
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
        robot.shooter.setAimLight(true);
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
        robot.shooter.setAimLight(false);
        
    }

    long teleopStart = 0;
    
    public void teleopInit() {
        robot.arm.setBrake(true);
        teleopStart = System.currentTimeMillis();
    }
    PulseTriggerBoolean autoAimWatcher = new PulseTriggerBoolean();
    boolean autoAimOut = false;

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Timer.delay(.001);
        updateDashboard();  //Update diagnostic dashboard
        SmartDashboard.putNumber("pitch", 90.0 - robot.arm.toDegrees(robot.arm.getActual()));

        double teleopElapsed = ((double) System.currentTimeMillis() - teleopStart)/1000;
        
        if((int)teleopElapsed % 10 == 0 && 
                DriverStation.getInstance().isFMSAttached()) {
//            System.out.println("Saving image from camera");
//            try {
//                robot.camera.getImage().write("//img" +System.currentTimeMillis() +".bmp");
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
            
        }
        autoAimWatcher.set(operatorJoy.getDPad(GamePad.DPadStates.LEFT));
        autoAimOut = autoAimWatcher.get();

     
        
            robot.shooter.setAimLight(leftJoy.getRawButton(1));
        //System.out.println("offset" + (arm.getActual() - Constants.ARM_LOWER_LIM));
        if (leftJoy.getRawButton(6)) {
            robot.climber.set(Constants.CLIMBER_UP_SPEED);
        } else if (leftJoy.getRawButton(7)) {
            robot.climber.set(Constants.CLIMBER_DOWN_SPEED);
        } else {
            double out = 0;
            if(Math.abs(operatorJoy.getRightY()) > .5) {
                out = -operatorJoy.getRightY();
            }
            robot.climber.set(out);
        }

        double leftAdjust = 0;
        double rightAdjust = 0;

        if (leftJoy.getRawButton(4)) {
            leftAdjust = .25;
            rightAdjust = -.25;
        }
        if (leftJoy.getRawButton(5)) {
            leftAdjust = -.25;
            rightAdjust = .25;
        }
        //       if(leftJoy.getRawButton(2)) {
        //       leftAdjust = .25;
        //     rightAdjust = .25;
        //     }
        if (leftJoy.getRawButton(3)) {
            leftAdjust = -.25;
            rightAdjust = -.25;
        }

        if (robot.drive.gear == Constants.LOW_GEAR) {
            leftAdjust *= .75;
            rightAdjust *= .75;
        }

        if (leftJoy.getRawButton(8)) {
            double aim = autoYaw();
            leftAdjust = -aim;
            rightAdjust = aim;
        }


        boolean shiftButton = rightJoy.getRawButton(1);
        robot.drive.setShifter(shiftButton);


        if (leftAdjust != 0 || rightAdjust != 0) {
            robot.drive.tankDrive(leftJoy.getRawAxis(2) + leftAdjust, rightJoy.getRawAxis(2) + rightAdjust);
        } else if (leftJoy.getRawButton(2)) {
            robot.drive.cheesyDrive(-leftJoy.getRawAxis(1),
                    rightJoy.getRawAxis(2));
        } else {
            robot.drive.cheesyDrive(robot.drive.twoStickToTurning(leftJoy.getRawAxis(2), rightJoy.getRawAxis(2)),
                    robot.drive.twoStickToThrottle(leftJoy.getRawAxis(2), rightJoy.getRawAxis(2)));
        }

        robot.arm.autoZero();

        operator();
    }

    double autoYaw() {
        double x = SmartDashboard.getNumber("TargetX", 0.0);
        x = EagleMath.cap(x, -.3, 3);
        if (Math.abs(x) < .1) {
            x = 0;
        }
        return x;
    }

    
    double autoPitch() {

        double altitude = SmartDashboard.getNumber("altitude", 0.0);
        double range = SmartDashboard.getNumber("TargetRange", 0.0);

        double offset = EagleMath.map((float)leftJoy.getRawAxis(3), 
                (float)-1, (float)1, (float)-10, (float)10);   //Offset for shots from scoring position
        System.out.println("Targetting Offset: " + offset);

        if (autoAimOut) {
            altitude = -altitude + 90;      //Get te complement of the altitude angle because arm is referenced from vertical.
            //Auto range logic. Use at your own risk.
            if (range < 300) {
                //altitude += Constants.VISION_OFFSET_FRNT_CTR;
                //System.out.println("Front Pyr Shot");
            } else if (range >= 300 && range <= 550) {
                //altitude += Constants.VISION_OFFSET_REAR_CTR;
                //System.out.println("Rear Pyr Shot");
            } else {
                //altitude += Constants.VISION_OFFSET_REAR_CNR;
                //System.out.println("Corner Pyr Shot");
            }
            altitude += offset;
            altitude = robot.arm.fromDegrees(altitude);     //Convert to pot rotations for the arm
            if (!SmartDashboard.getBoolean("found", false)) {
                //altitude = 30;
            }
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
            robot.feeder.setKickerTimed(Constants.KICKER_OUT);
        } else {
            robot.feeder.setKickerTimed(Constants.KICKER_IN);
        }

        boolean isShooting = false; //Flag to indicate operator is running the shooter

        double shooterSet = Constants.SHOOTER_STOP;

        if (operatorJoy.getButton(1)) {
            isShooting = true;
            shooterSet = 6000.0;
        } else if (operatorJoy.getButton(2)) {
            isShooting = true;
            shooterSet = 6000.0;
        } else if (operatorJoy.getButton(3)) {
            isShooting = true;
            shooterSet = Constants.SHOOTER_SHOT;
        } else {
            isShooting = false;
            shooterSet = Constants.SHOOTER_STOP;
        }

        //robot.autoshoot.run(shooterSet, wantShoot);

        if (operatorJoy.getButton(8) || leftJoy.getRawButton(1)) {
            robot.feeder.setFlapper(Constants.FLAP_OUT);
        } else {
            robot.feeder.setFlapper(Constants.FLAP_IN);
        }

        adjustUpButton.set(operatorJoy.getButton(9));
        adjustDnButton.set(operatorJoy.getButton(10));

        if (autoAimOut) {
            //armSet = Constants.ARM_HIGH_SHOT;
            armSet = autoPitch();
        } else if (operatorJoy.getDPad(GamePad.DPadStates.DOWN) || rightJoy.getRawButton(2)) {
            armSet = Constants.ARM_UPPER_LIM;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.RIGHT)) {
            armSet = Constants.ARM_HIGH_SHOT;
        } else if (operatorJoy.getDPad(GamePad.DPadStates.UP) || rightJoy.getRawButton(3)) {
            armSet = Constants.ARM_STOW_UP;
        } else if (autoAimOut) {
            armSet = autoPitch();
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
            if (isShooting) {
                fineAdjustInput = 0;
                //coarseAdjust = 0;;
                fineAdjust = 0;
            } else {
                fineAdjustInput = operatorJoy.getRightY();
            }

            if (adjustUpButton.get()) {
                fineAdjustInput = -1.0 / Constants.DEGREES_PER_TURN;
            } else if (adjustDnButton.get()) {
                fineAdjustInput = 1.0 / Constants.DEGREES_PER_TURN;
            } else {
                fineAdjustInput = 0;
            }

            fineAdjust *= EagleMath.deadband(fineAdjustInput, .005);
            

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
        SmartDashboard.putNumber("Arm Actual - Deg", robot.arm.toDegrees(robot.arm.getActual()));
        SmartDashboard.putNumber("Arm Set - Deg", robot.arm.toDegrees(robot.arm.getSetpoint()));

        SmartDashboard.putNumber("yaw", robot.drive.getYaw());
        //SmartDashboard.putBoolean("Arm Zero Switch", robot.arm.getZeroSwitch());
    }

    long testStart = 0;
    public void testInit() {
        testStart = System.currentTimeMillis();
    }

    public void testPeriodic() {
        
    }
}