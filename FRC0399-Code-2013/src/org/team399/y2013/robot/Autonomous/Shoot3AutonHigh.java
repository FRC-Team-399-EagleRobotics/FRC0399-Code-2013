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
 *
 * @author Jackie
 */
public class Shoot3AutonHigh {

    static AutonomousTimer timer = new AutonomousTimer();
    private static long elapsedTime = 0, start = 0;
    
    private static long timeDelay = 0;
    private static double waitForArmDelay = 1.05;


    public static void start() {
        start = System.currentTimeMillis();
        Main.robot.shooter.start();
        Main.robot.shooter.setShooterSpeed(7900);
        Main.robot.arm.setPointRotations(Constants.ARM_MID_SHOT);
        Main.robot.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
    }

    public static void start(long delay) {
        timeDelay = delay;
        start = System.currentTimeMillis();
        Main.robot.shooter.start();
        Main.robot.shooter.setShooterSpeed(7900);
        Main.robot.arm.setPointRotations(Constants.ARM_MID_SHOT);
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
            Main.robot.arm.setPointRotations(Constants.ARM_AUTON_SHOT);
            Timer.delay(waitForArmDelay + (timeDelay / 1000));
            //Timer.delay(1.0);
            int vision_ctr = 0;
            while(vision_ctr <= 10) {
                SmartDashboard.putNumber("pitch", 90.0-Main.robot.arm.toDegrees(Main.robot.arm.getActual()));
                Main.robot.arm.setPointRotations(AutonCommon.autoPitch());
                Timer.delay(.05);
                vision_ctr++;
            }
            Timer.delay(.25);
            
            for(int i = 0; i < 5; i++) {
//                SmartDashboard.putNumber("pitch", 90.0-Main.robot.arm.toDegrees(Main.robot.arm.getActual()));
//                Main.robot.arm.setPointRotations(AutonCommon.autoPitch());
                AutonCommon.shootOneDisc();
            }
            Main.robot.shooter.setShooterSpeed(0);
            
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
