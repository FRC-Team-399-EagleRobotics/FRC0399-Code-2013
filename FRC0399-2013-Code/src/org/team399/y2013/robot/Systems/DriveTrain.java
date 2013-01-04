/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.*;
import org.team399.y2013.Utilities.EagleMath;

/**
 *
 * @author Jeremy
 */
public class DriveTrain {
    private SpeedController m_leftA, m_leftB, m_rightA, m_rightB;   //TODO: Change type to whatever speed controller we use
    
    /**
     * Constructor
     * @param leftA PWM ports for motor controllers
     * @param leftB
     * @param rightA
     * @param rightB 
     */
    public DriveTrain(int leftA, int leftB, int rightA, int rightB) {
        m_leftA = new Talon(leftA);
        m_leftB = new Talon(leftB);
        m_rightA = new Talon(rightA);
        m_rightB = new Talon(rightB);
    }
    
    /**
     * Tank drive method. send signal to motor controllers to individual side
     * @param leftPWM
     * @param rightPWM 
     */
    public void tankDrive(double leftPWM, double rightPWM) {
        leftPWM = (Math.abs(leftPWM) > 1.0) ? 1.0*EagleMath.signum(leftPWM) : leftPWM;      //Clamps inputs to +- 1.0
        leftPWM = (Math.abs(rightPWM) > 1.0) ? 1.0*EagleMath.signum(rightPWM) : rightPWM;
        m_leftA.set(leftPWM);
        m_leftB.set(leftPWM);
        m_rightA.set(rightPWM);
        m_rightB.set(rightPWM);
    }
    
    public void arcadeDrive(double throttle, double turn) {
        double leftOut = 0, rightOut = 0;
        
        double max = Math.abs(throttle);
        if(Math.abs(turn) > max) {
            max = Math.abs(turn);
        }
        double sum = throttle + turn;
        double dif = throttle - turn;
        
        if(throttle >= 0) {
            if(turn >= 0) {
                leftOut = max;
                rightOut = dif;
            } else {
                leftOut = sum;
                rightOut = max;
            }
        } else {
            if(turn >= 0) {
                leftOut = sum;
                rightOut = -max;
            } else {
                leftOut = -max;
                rightOut = dif;
            }
        }
        
        
        tankDrive(leftOut, rightOut);
    }
}
