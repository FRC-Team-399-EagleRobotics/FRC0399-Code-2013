/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author Jeremy
 */
public class Feeder {
    private Solenoid kicker;
    private Victor beltMotor;
    
    public Feeder(int motor, int solenoid) {
        beltMotor = new Victor(motor);
        kicker = new Solenoid(solenoid);
    }
    
    public void setBelt(double in) {
        beltMotor.set(in);
    }
    
    public void setKicker(boolean state) {
        kicker.set(state);
    }
}
