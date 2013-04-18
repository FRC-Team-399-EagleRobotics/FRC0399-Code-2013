/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

import edu.wpi.first.wpilibj.Timer;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Main;

/**
 * Shoots
 *
 * @author Jackie
 */
public class Shoot2CenterlineD {

    static AutonomousTimer timer = new AutonomousTimer();
    private static long elapsedTime = 0, start = 0;
    private static long timeDelay = 0;
    static double waitForArmDelay = .625;
    static double feedToRollerDelay = .375;
    static double kickerResetDelay = .2;
    static double feedToKickerDelay = .625;
    static double ARM_POSITION_OFFSET = 0.0;
    static double BACK_DIST = 8.0;  //8 feet to drive back
    static double TOP_SPEED = 12.0; //driving speed will be 12.0 ft/s

    public static void start() {
        start = System.currentTimeMillis();
        Main.shooter.start();
        Main.shooter.setShooterSpeed(7900);
        Main.arm.setPointRotations(Constants.ARM_MID_SHOT+ARM_POSITION_OFFSET);
        Main.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
    }

    public static void start(long delay) {
        timeDelay = delay;
        start();
        timer.start();
    }
    static boolean finished = false;

    public static void run() {


        System.out.println("Running auton, Timer: " + timer.get());
        elapsedTime = System.currentTimeMillis() - start;

        if (!finished) {
            //Set arm to shooting position
            Main.arm.setPointRotations(Constants.ARM_AUTON_SHOT+ARM_POSITION_OFFSET);
            //Timer delay
            Timer.delay(waitForArmDelay + (timeDelay / 1000));

            //Shoot discs. goes through the full cycle of 3 shots just in case
            for (int i = 0; i < 3; i++) {
                shootOneDisc();
            }
            Main.feeder.setRoller(0);//Stop roller when done

            //Stow arm down to drive out of pyramid
            Main.arm.setPointRotations(Constants.ARM_STOW_DOWN);
            Timer.delay(waitForArmDelay/2);

            Main.drive.setShifter(Constants.HIGH_GEAR);
            Main.drive.driveSpeed(TOP_SPEED, TOP_SPEED);
            Timer.delay(BACK_DIST/TOP_SPEED);
            Main.arm.setPointRotations(Constants.ARM_STOW_UP);
            Main.drive.driveSpeed(0, 0);

            finished = true;
        }
        Main.drive.setShifter(Constants.LOW_GEAR);
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
