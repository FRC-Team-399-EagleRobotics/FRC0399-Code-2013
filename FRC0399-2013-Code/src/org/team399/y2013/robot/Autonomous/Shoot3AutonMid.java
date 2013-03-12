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
public class Shoot3AutonMid {
    static AutonomousTimerThread timer = new AutonomousTimerThread();
    
    private static long elapsedTime = 0, start = 0;
    public static void start() {
        start = System.currentTimeMillis();
        //timer.start();
        Main.shooter.start();
        Main.shooter.setShooterSpeed(7900);
        Main.arm.setPointAngle(Constants.MID_SHOT);
        Main.arm.setEnabled(true);
        System.out.println("Init'd auton");
        finished = false;
    }
    static boolean finished = false;
    public static void run() {
        System.out.println("Running auton, Timer: " + timer.get());
        elapsedTime = System.currentTimeMillis() - start;
//        if(elapsedTime > 2000 && elapsedTime < 2750) {
//            Main.feeder.setKicker(Constants.KICKER_OUT);
//            Main.feeder.setBelt(0);
//        } else if(elapsedTime > 2750 && elapsedTime < 4000) {
//            Main.feeder.setBelt(1.0);
//            Main.feeder.setKicker(Constants.KICKER_IN);
//        } else if(elapsedTime > 4000 && elapsedTime < 4750) {
//            Main.feeder.setKicker(Constants.KICKER_OUT);
//            Main.feeder.setBelt(0);
//        } else if(elapsedTime > 4750 && elapsedTime < 6000) {
//            Main.feeder.setBelt(1.0);
//            Main.feeder.setKicker(Constants.KICKER_IN);
//        } else if(elapsedTime > 6000 && elapsedTime < 6750) {
//            Main.feeder.setKicker(Constants.KICKER_OUT);
//            Main.feeder.setBelt(0);
//        } else if(elapsedTime > 6750 && elapsedTime < 8000) {
//            Main.feeder.setBelt(1.0);
//            Main.feeder.setKicker(Constants.KICKER_IN);
//        } else if(elapsedTime > 9000) {
//            Main.feeder.setBelt(0);
//            Main.feeder.setKicker(Constants.KICKER_OUT);
//            Main.shooter.setShooterSpeed(0);
//        }
        if(!finished) {
            Main.arm.setPointAngle(Constants.MID_SHOT+.1); //add .1 to make it mid
            Timer.delay(1.85);
            Main.feeder.setBelt(.5);
            Timer.delay(.25);
            Main.feeder.setKicker(Constants.KICKER_OUT);
            Main.feeder.setBelt(0);
            Timer.delay(.75);
            Main.feeder.setKicker(Constants.KICKER_IN);
            Timer.delay(.25);
            Main.feeder.setBelt(1.0);
            Timer.delay(1.25);
            Main.feeder.setKicker(Constants.KICKER_OUT);
            Main.feeder.setBelt(0);
            Timer.delay(.75);
            Main.feeder.setKicker(Constants.KICKER_IN);
            Timer.delay(.25);
            Main.feeder.setBelt(1.0);
            //Main.shooter.setShooterSpeed(8500);
            Timer.delay(1.25);
            Main.feeder.setKicker(Constants.KICKER_OUT);
            Main.feeder.setBelt(0);
            Timer.delay(.75);
            Main.feeder.setKicker(Constants.KICKER_IN);
            Timer.delay(.25);
            Main.feeder.setBelt(1.0);
            Timer.delay(1.25);
            Main.arm.setPointAngle(Constants.STOW_UP);
            Timer.delay(1.25);
//            for(int i = 0; i <= 100; i++) {
//                Main.drive.turnAngle(180);
//            }
//            Main.drive.tankDrive(0, 0);
//            Timer.delay(2.5);
//            Main.drive.tankDrive(-1, -1);
            finished = true;
        }
        
        
        Main.drive.tankDrive(0,0);
        Main.feeder.setBelt(0);
        Main.shooter.setShooterSpeed(0);
        
    }
    
}
