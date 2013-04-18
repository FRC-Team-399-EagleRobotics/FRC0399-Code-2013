/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Main;

/**
 *
 * @author Jackie
 */
public class AutonCommon {

    private static long timeDelay = 0;
    private static double waitForArmDelay = .75;
    private static double feedToRollerDelay = .5;
    private static double kickerResetDelay = .25;
    private static double feedToKickerDelay = 1.0;

    public static void shootOneDisc() {
        Main.feeder.setKicker(Constants.KICKER_OUT);
        Main.feeder.setRoller(0);
        Timer.delay(feedToRollerDelay);

        Main.feeder.setKicker(Constants.KICKER_IN);
        Timer.delay(kickerResetDelay);

        Main.feeder.setRoller(1.0);
        Timer.delay(feedToKickerDelay);
    }

    public static void driveDistanceNaive(double speed, double distance, boolean gear) {
        if(gear == Constants.LOW_GEAR) {
            speed = EagleMath.cap(speed, -Constants.DRIVE_LOW_MAX_SPEED_FPS, 
                                          Constants.DRIVE_LOW_MAX_SPEED_FPS);
        }
        double delayTime = distance/speed;
        Main.drive.setShifter(gear);
        Main.drive.driveSpeed(speed, speed);
        Timer.delay(delayTime);
        Main.drive.driveSpeed(0, 0);
    }
    
}
