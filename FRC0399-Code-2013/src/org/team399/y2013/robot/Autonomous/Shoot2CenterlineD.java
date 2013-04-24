/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Autonomous;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Main;
import org.team399.y2013.robot.Robot;

/**
 * Shoots
 *
 * @author Jackie
 */
public class Shoot2CenterlineD {

    static AutonomousTimer timer = new AutonomousTimer();
    private static long elapsedTime = 0, start = 0;
    private static long timeDelay = 0;
    static double waitForArmDelay = .75;
    static double ARM_POSITION_OFFSET = 0.0;
    static double BACK_DIST = 20.0;  //8 feet to drive back
    static double TOP_SPEED = 16.0; //driving speed will be 12.0 ft/s

    public static void start() {
        start = System.currentTimeMillis();
        Main.robot.shooter.start();
        Main.robot.shooter.setShooterSpeed(8600);
        Main.robot.arm.setPointRotations(Constants.ARM_MID_SHOT + ARM_POSITION_OFFSET);
        Main.robot.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
    }

    public static void start(long delay) {
        timeDelay = delay;
        start();
        timer.start();
        System.out.println("Beginning centerline auton!");
    }
    static boolean finished = false;

    public static void run() {


        System.out.println("Running auton, Timer: " + timer.get());
        elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Centerline Auton");

        if (!finished) {
            //Set arm to shooting position
            Main.robot.arm.setPointRotations(Main.robot.arm.fromDegrees(60));
            //Timer delay
            Timer.delay(waitForArmDelay + (timeDelay / 1000));
            //Timer.delay(1.0);
            int vision_ctr = 0; //Vision counter to calcuate 
            while (vision_ctr <= 10) {
                System.out.println("Waiting For Vision");
                SmartDashboard.putNumber("pitch", 90.0 - Main.robot.arm.toDegrees(Main.robot.arm.getActual()));
                Main.robot.arm.setPointRotations(AutonCommon.autoPitch(0));
                Timer.delay(.1);
                vision_ctr++;
            }
            Timer.delay(.25);

            for (int i = 0; i < 3; i++) {
//                SmartDashboard.putNumber("pitch", 90.0-Main.robot.arm.toDegrees(Main.robot.arm.getActual()));
//                Main.robot.arm.setPointRotations(AutonCommon.autoPitch());
                AutonCommon.shootOneDisc();
            }

            Main.robot.arm.setPointRotations(Constants.ARM_STOW_DOWN);

            Main.robot.feeder.setRoller(0);//Stop roller when done

            //Stow arm down to drive out of pyramid
            System.out.println("Stowing arm...");
            Main.robot.arm.setPointRotations(10.0);
            Timer.delay(.5);
            int arm_ctr = 0;    //Safety caunter to prevent a faulty arm condition from blocking all user code from running
            while (Math.abs(Main.robot.arm.getActual()  - Main.robot.arm.getSetpoint())
                    > .1 || arm_ctr >= 8) {
                Main.robot.arm.setPointRotations(10.0);
                Timer.delay(.25);
                System.out.println("Waiting for arm to stow...");
                arm_ctr++;
            }

            if(arm_ctr < 8) {
                AutonCommon.driveDistanceNaive(TOP_SPEED, BACK_DIST, Constants.HIGH_GEAR);
            }

            finished = true;
        }

        AutonCommon.stop();
    }
}