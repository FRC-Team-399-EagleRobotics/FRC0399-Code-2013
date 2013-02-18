/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.*;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.Utilities.Integrator;

/**
 *
 * @author Jeremy
 */
public class DriveTrain {

    private Talon m_leftA, m_leftB, m_rightA, m_rightB;   //TODO: Change type to whatever speed controller we use

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
        throttleIntegrator.reset();
        turnIntegrator.reset();
    }

    /**
     * Tank drive method. send signal to motor controllers to individual side
     * @param leftPWM
     * @param rightPWM 
     */
    public void tankDrive(double leftPWM, double rightPWM) {
        if (Math.abs(leftPWM) < .15) {
            leftPWM = 0;
        }
        if (Math.abs(rightPWM) < .15) {
            rightPWM = 0;
        }
        m_leftA.set(-leftPWM);
        m_leftB.set(-leftPWM);
        m_rightA.set(rightPWM);
        m_rightB.set(rightPWM);
    }

    public void arcadeDrive(double throttle, double turn) {
        double leftOut = 0, rightOut = 0;

        double max = Math.abs(throttle);
        if (Math.abs(turn) > max) {
            max = Math.abs(turn);
        }
        double sum = throttle + turn;
        double dif = throttle - turn;

        if (throttle >= 0) {
            if (turn >= 0) {
                leftOut = max;
                rightOut = dif;
            } else {
                leftOut = sum;
                rightOut = max;
            }
        } else {
            if (turn >= 0) {
                leftOut = sum;
                rightOut = -max;
            } else {
                leftOut = -max;
                rightOut = dif;
            }
        }


        tankDrive(leftOut, rightOut);
    }
    Integrator throttleIntegrator = new Integrator(0);
    Integrator turnIntegrator = new Integrator(0);
    double throttle = 0, turn = 0,
            prevThrottle = 0, prevTurn = 0;

    public void filteredTankDrive(double left, double right) {
        double kThrot = 0.0075;
        double kTurn = 0.0075 ;

        prevThrottle = throttle;
        prevTurn = turn;
        throttle = twoStickToThrottle(left, right);
        turn = twoStickToTurning(left, right);

        throttleIntegrator.update(kThrot * (throttle - prevThrottle));
        turnIntegrator.update(kTurn * (turn - prevTurn));

        throttle += throttleIntegrator.get();
        turn += turnIntegrator.get();

        if (throttleIntegrator.get() > 1) {
            throttleIntegrator.add(-1);
        } else if (throttleIntegrator.get() < -1) {
            throttleIntegrator.add(1);
        } else {
            throttleIntegrator.reset();
        }

        if (turnIntegrator.get() > 1) {
            turnIntegrator.add(-1);
        } else if (turnIntegrator.get() < -1) {
            turnIntegrator.add(1);
        } else {
            turnIntegrator.reset();
        }

        tankDrive(throttle + turn, throttle - turn);

    }

    double twoStickToTurning(double left, double right) {
        return (left - right) / 2;
    }

    double twoStickToThrottle(double left, double right) {
        return (left + right) / 2;
    }
}
