/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Main;
import org.team399.y2013.robot.Robot;

/**
 * Class that contains common autonomous or automated functions
 * use these only in auton.
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
     * Open loop drive distance function.
     * Use caution with lower speeds in high gear, short distances in high gear.
     * @param speed
     * @param distance
     * @param gear 
     */
    public static void driveDistanceNaive(double speed, double distance, boolean gear) {
        if(gear == Constants.LOW_GEAR) {
            speed = EagleMath.cap(speed, -Constants.DRIVE_LOW_MAX_SPEED_FPS, 
                                          Constants.DRIVE_LOW_MAX_SPEED_FPS);
        }
        double delayTime = distance/speed;
        Main.robot.drive.setShifter(gear);
        Main.robot.drive.driveSpeed(speed, speed);
        Timer.delay(delayTime);
        Main.robot.drive.driveSpeed(0, 0);
    }
    
    public static void stop() {
        Main.robot.drive.setShifter(Constants.LOW_GEAR);
        Main.robot.drive.tankDrive(0, 0);
        Main.robot.feeder.setRoller(0);
        Main.robot.shooter.setShooterSpeed(0);
    }
    
}
