/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems.Automation;

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
    
    public AutoShootController(Shooter shooter_inst, Feeder feeder_inst) {
        this.m_shooter = shooter_inst;
        this.m_feeder = feeder_inst;
    }
    
    public void run() {
        m_feeder.setBelt(1.0);
        kick((m_shooter.isAtTargetSpeed() && 
                (System.currentTimeMillis() % Constants.KICK_TIME_DELAY < 5)));

    }
    
    private void kick(boolean wantKick) {
        //Todo: modify kick to make it less erratic.
        m_feeder.setKicker(wantKick);
    }
}
