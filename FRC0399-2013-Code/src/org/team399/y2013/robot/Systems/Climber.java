/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.Talon;
import org.team399.y2013.robot.Constants;

/**
 *
 * @author Jeremy
 */
public class Climber {
    private Talon winch;
    
    public Climber(int port) {
        winch = new Talon(port);
    }
    
    public void set(double value) {
        winch.set(value);
    }
}
