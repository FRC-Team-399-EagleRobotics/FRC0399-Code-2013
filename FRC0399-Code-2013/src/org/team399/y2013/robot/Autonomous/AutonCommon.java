/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Main;

/**
 * Class that contains common autonomous or automated functions use these only
 * in auton.
 *
 * @author Jeremy
 */
public class AutonCommon {

    private static long timeDelay = 0;
    private static double waitForArmDelay = .75;
    private static double feedToRollerDelay = .5;
    private static double kickerResetDelay = .25;
    private static double feedToKickerDelay = 1.0;

    /**
     * timed sequence for shooting one disc
     */
    public static void shootOneDisc() {
        Main.robot.feeder.setKicker(Constants.KICKER_OUT);
        Main.robot.feeder.setRoller(0);
        Timer.delay(feedToRollerDelay);

        Main.robot.feeder.setKicker(Constants.KICKER_IN);
        Timer.delay(kickerResetDelay);

        Main.robot.feeder.setRoller(1.0);
        Timer.delay(feedToKickerDelay);
    }
    
    /**
     * timed sequence for shooting one disc
     */
    public static void shootOneDiscFast() {
        Main.robot.feeder.setKicker(Constants.KICKER_OUT);
        Main.robot.feeder.setRoller(0);
        Timer.delay(.25);

        Main.robot.feeder.setKicker(Constants.KICKER_IN);
        Timer.delay(kickerResetDelay);

        Main.robot.feeder.setRoller(1.0);
        Timer.delay(.5);
    }
    

    /**
     * Open loop drive distance function. Use caution with lower speeds in high
     * gear, short distances in high gear.
     *
     * @param speed
     * @param distance
     * @param gear
     */
    public static void driveDistanceNaive(double speed, double distance, boolean gear) {
        if (gear == Constants.LOW_GEAR) {
            //    speed = EagleMath.cap(speed, -Constants.DRIVE_LOW_MAX_SPEED_FPS, 
            //                                  Constants.DRIVE_LOW_MAX_SPEED_FPS);
        }
        double delayTime = distance / speed;
        Main.robot.drive.setShifter(gear);
        Main.robot.drive.driveSpeed(speed, speed);
        Timer.delay(delayTime);
        Main.robot.drive.driveSpeed(-.2*EagleMath.signum(speed), //quick power reversal to brake
                -.2*EagleMath.signum(speed));
        Timer.delay(.1);
        Main.robot.drive.driveSpeed(0, 0);
    }

    public void waitForArm() {
        int ctr = 0;
        while ((Main.robot.arm.getSetpoint() - Main.robot.arm.getActual() > .075) || ctr >= 12) {
            System.out.println("Waiting for arm to reach position");
            Timer.delay(125);
            ctr++;
        }
    }

    public static void stop() {
        Main.robot.drive.setShifter(Constants.LOW_GEAR);
        Main.robot.drive.tankDrive(0, 0);
        Main.robot.feeder.setRoller(0);
        Main.robot.shooter.setShooterSpeed(0);
    }

    public static double autoPitch() {

        double altitude = SmartDashboard.getNumber("altitude", 0.0);
        double range = SmartDashboard.getNumber("TargetRange", 0.0);
        SmartDashboard.putNumber("pitch", 90.0 - Main.robot.arm.toDegrees(Main.robot.arm.getActual()));

        double offset = 0;


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
//            altitude += Constants.VISION_OFFSET_REAR_CTR;   //Offset for shots from scoring position
        altitude = Main.robot.arm.fromDegrees(altitude);     //Convert to pot rotations for the arm
        if (!SmartDashboard.getBoolean("found", false)) {
            //altitude = 30;
        }
        altitude = EagleMath.cap(altitude, 5, 75);
        return altitude;
    }
    
    public static double autoPitch(double offset) {

        double altitude = SmartDashboard.getNumber("altitude", 0.0);
        double range = SmartDashboard.getNumber("TargetRange", 0.0);
        SmartDashboard.putNumber("pitch", 90.0 - Main.robot.arm.toDegrees(Main.robot.arm.getActual()));

        
        altitude = -altitude + 90;      //Get te complement of the altitude angle because arm is referenced from vertical.
        altitude += offset;
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
//            altitude += Constants.VISION_OFFSET_REAR_CTR;   //Offset for shots from scoring position
        altitude = Main.robot.arm.fromDegrees(altitude);     //Convert to pot rotations for the arm
        if (!SmartDashboard.getBoolean("found", false)) {
            //altitude = 30;
        }
        altitude = EagleMath.cap(altitude, 5, 75);
        return altitude;
    }
}
