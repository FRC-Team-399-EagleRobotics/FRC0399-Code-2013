/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Jeremy
 */
public class Climber {
    private Talon winch;
    private DigitalInput limit;
    
    public Climber(int port, int limitSwitch) {
        winch = new Talon(port);
        limit = new DigitalInput(limitSwitch);
    }
    
    public void set(double value) {
        winch.set(value);
    }
    
    public boolean getSwitch() {
        return limit.get();
    }
}
