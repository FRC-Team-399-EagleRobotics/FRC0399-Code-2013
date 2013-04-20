/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Main;
import org.team399.y2013.robot.Robot;

/**
 *
 * @author Jackie
 */
public class Shoot3AutonMid {

    static AutonomousTimer timer = new AutonomousTimer();
    private static long elapsedTime = 0, start = 0;
    private static long timeDelay = 0;
    static double waitForArmDelay = .75;
    static double feedToRollerDelay = .5;
    static double kickerResetDelay = .25;
    static double feedToKickerDelay = 1.0;
    

    public static void start() {
        start = System.currentTimeMillis();
        Main.robot.shooter.start();
        Main.robot.shooter.setShooterSpeed(7900);
        Main.robot.arm.setPointRotations(Constants.ARM_MID_SHOT -.01);
        Main.robot.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
    }

    public static void start(long delay) {
        timeDelay = delay;
        start = System.currentTimeMillis();
        Main.robot.shooter.start();
        Main.robot.shooter.setShooterSpeed(7900);
        Main.robot.arm.setPointRotations(Constants.ARM_MID_SHOT -.01);
        Main.robot.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
        timer.start();
    }
    static boolean finished = false;

    public static void run() {


        System.out.println("Running auton, Timer: " + timer.get());
        elapsedTime = System.currentTimeMillis() - start;

        if (!finished) {
            Main.robot.arm.setPointRotations(Constants.ARM_MID_SHOT -.01);
            Timer.delay(waitForArmDelay + (timeDelay / 1000));

            for(int i = 0; i < 5; i++) {
                AutonCommon.shootOneDisc();
            }
            
            Main.robot.arm.setPointRotations(Constants.ARM_STOW_UP);
            Timer.delay(waitForArmDelay);
            
            Main.robot.drive.setShifter(Constants.LOW_GEAR);
            Main.robot.drive.tankDrive(.5, .5);
            Timer.delay(3.0);

            finished = true;
        }
        AutonCommon.stop();

    }

}
