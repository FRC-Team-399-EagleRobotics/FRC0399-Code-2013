/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems.Automation;

import org.team399.y2013.Utilities.BinarySignalFilter;
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
    
    private long lastKick = 0;
    
    BinarySignalFilter kickFilter = new BinarySignalFilter(10);
    
    public AutoShootController(Shooter shooter_inst, Feeder feeder_inst) {
        this.m_shooter = shooter_inst;
        this.m_feeder = feeder_inst;
    }
    
    public void run(double beltSpeed, double shooterSpeed) {
        m_feeder.setBelt(beltSpeed);
        m_shooter.setShooterSpeed(shooterSpeed);
        if(lastKick - System.currentTimeMillis() > Constants.AUTO_SHOOT_MIN_PERIOD && m_shooter.isAtTargetSpeed()) {
            kick(true);
        } else {
            kick(false);
        }
    }
    
    private void kick(boolean wantKick) {
        boolean filteredKick = kickFilter.calculate(wantKick);
        //Todo: modify kick to make it less erratic.
        m_feeder.setKicker(filteredKick);
        
        if(filteredKick) {
            lastKick = System.currentTimeMillis();
            filteredKick = false;
            
        }
    }
}
