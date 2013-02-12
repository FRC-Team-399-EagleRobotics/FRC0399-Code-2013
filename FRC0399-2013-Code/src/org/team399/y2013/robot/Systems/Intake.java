/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Victor;
import org.team399.y2013.Utilities.EagleMath;

/**
 *
 * @author Jeremy
 */
public class Intake {
    private AnalogChannel discSensor;
    private Victor intake_motor;
    
    private final double discThresh = 0.5;
    
    public Intake(int motor_port, int sensor_port) {
        discSensor = new AnalogChannel(sensor_port);
        intake_motor = new Victor(motor_port);
    }
    
    public void set(double in) {
        in = (Math.abs(in) > 1.0) ? 1.0*EagleMath.signum(in) : in;
        intake_motor.set(in);
    }
    
    public boolean isDiscPresent() {
        return discSensor.getAverageVoltage() > discThresh;
    }
    
    
            
}
