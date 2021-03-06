/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems.Automation;

import edu.wpi.first.wpilibj.Timer;
import org.team399.y2013.Utilities.PulseTriggerBoolean;
import org.team399.y2013.robot.Constants;
import org.team399.y2013.robot.Systems.Feeder;
import org.team399.y2013.robot.Systems.Shooter;

/**
 *
 * @author Jeremy
 */
public class AutoShootController {

    private Shooter m_shooter = null;
    private Feeder m_feeder = null;
    double feedToRollerDelay = .25;
    double kickerResetDelay = .25;
    double feedToKickerDelay = .5;
    boolean isDiscStaged = false;
    boolean isKickerLoaded = false;
    boolean isReady = false;
    private long beginShotTime = 0, currentTime = 0;

    public AutoShootController(Shooter shooter_inst, Feeder feeder_inst) {
        this.m_shooter = shooter_inst;
        this.m_feeder = feeder_inst;
    }

    public void run(double shooterSpeed, boolean wantShoot) {
        currentTime = System.currentTimeMillis();
        
        //m_shooter.setShooterSpeed(shooterSpeed);    //Be careful with conflicting calls...
        if (wantShoot) {
            if (isDiscStaged && isKickerLoaded && isReady) {
                beginShotTime = System.currentTimeMillis();
                isDiscStaged = false;
                isKickerLoaded = false;
                isReady = false;
            }
            shootOneDisc();
        } else {
            isDiscStaged = false;
            isKickerLoaded = false;
            isReady = false;
        }

    }

    public void shootOneDisc() {
        if (currentTime - beginShotTime < feedToRollerDelay && !isDiscStaged) {
            m_feeder.setKicker(Constants.KICKER_OUT);
            m_feeder.setRoller(0);
            isDiscStaged = true;
        } else if (currentTime - beginShotTime < kickerResetDelay + feedToRollerDelay
                && currentTime - beginShotTime > feedToRollerDelay
                && !isKickerLoaded) {
            m_feeder.setKicker(Constants.KICKER_IN);
            isKickerLoaded = true;
        } else if (currentTime - beginShotTime < feedToKickerDelay + feedToRollerDelay + kickerResetDelay 
                && currentTime - beginShotTime > kickerResetDelay + feedToRollerDelay
                && !isReady) {
            m_feeder.setRoller(1.0);
            isReady = m_shooter.isAtTargetSpeed();
        }

    }
}
