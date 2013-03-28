/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Main;

/**
 *
 * @author Jackie
 */
public class Shoot3AutonHigh {

    static AutonomousTimer timer = new AutonomousTimer();
    private static long elapsedTime = 0, start = 0;
    private static long timeDelay = 0;
    static double waitForArmDelay = .75;
    static double feedToRollerDelay = .5;
    static double kickerResetDelay = .25;
    static double feedToKickerDelay = 1.0;
    

    public static void start() {
        start = System.currentTimeMillis();
        Main.shooter.start();
        Main.shooter.setShooterSpeed(7900);
        Main.arm.setPointRotations(Constants.ARM_MID_SHOT);
        Main.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
    }

    public static void start(long delay) {
        timeDelay = delay;
        start = System.currentTimeMillis();
        Main.shooter.start();
        Main.shooter.setShooterSpeed(7900);
        Main.arm.setPointRotations(Constants.ARM_MID_SHOT);
        Main.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
        timer.start();
    }
    static boolean finished = false;

    public static void run() {


        System.out.println("Running auton, Timer: " + timer.get());
        elapsedTime = System.currentTimeMillis() - start;

        if (!finished) {
            Main.arm.setPointRotations(Constants.ARM_HIGH_SHOT);
            Timer.delay(waitForArmDelay + (timeDelay / 1000));

            for(int i = 0; i < 5; i++) {
                shootOneDisc();
            }
            
            Main.arm.setPointRotations(Constants.ARM_STOW_UP);
            Timer.delay(waitForArmDelay);
            
            Main.drive.setShifter(Constants.LOW_GEAR);
            Main.drive.tankDrive(-.5, -.5);

            finished = true;
        }

        Main.drive.tankDrive(0, 0);
        Main.feeder.setRoller(0);
        Main.shooter.setShooterSpeed(0);

    }

    public static void shootOneDisc() {
        Main.feeder.setKicker(Constants.KICKER_OUT);
        Main.feeder.setRoller(0);
        Timer.delay(feedToRollerDelay);

        Main.feeder.setKicker(Constants.KICKER_IN);
        Timer.delay(kickerResetDelay);

        Main.feeder.setRoller(1.0);
        Timer.delay(feedToKickerDelay);
    }
}
