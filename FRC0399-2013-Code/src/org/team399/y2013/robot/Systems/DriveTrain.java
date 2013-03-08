/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.*;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.Utilities.Integrator;
import org.team399.y2013.robot.Constants;

/**
 *
 * @author Jeremy
 */
public class DriveTrain {

    private Talon m_leftA, m_leftB, m_rightA, m_rightB;
    private Gyro yaw = new Gyro(2);
    private Gyro pitch = null;// new Gyro(3);
    private Solenoid shifter = new Solenoid(4);
    private final double WHEEL_DIA = Constants.WHEEL_DIAMETER;

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
        yaw.reset();
        //pitch.reset();
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

    
    /**
     * Drive input: throttle(Y translation) and turn(yaw translation) inputs
     * @param throttle y translation power
     * @param turn yaw translation power
     */
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

    /**
     * Drive algorithm with negative inertia(a la 971/254) on both the throttle and turning inputs
     * @param left
     * @param right 
     */
    public void filteredTankDrive(double left, double right) {
        double kThrot = 0.0075;
        double kTurn = 0.0075;

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

    /**
     * cheesy drive by team 254, ported to java by team 125
     * @param throttle
     * @param wheel
     * @param quickTurn 
     */
    public void cheesyDrive(double throttle, double wheel, boolean quickTurn) {

        double angular_power = 0.0;
        double overPower = 0.0;
        double sensitivity = 1.7;
        double rPower = 0.0;
        double lPower = 0.0;

        if (quickTurn) {
            overPower = 1.0;
            sensitivity = 1.0;
            angular_power = wheel;
        } else {
            overPower = 0.0;
            angular_power = Math.abs(throttle) * wheel * sensitivity;
        }

        rPower = lPower = throttle;
        lPower += angular_power;
        rPower -= angular_power;

        if (lPower > 1.0) {
            rPower -= overPower * (lPower - 1.0);
            lPower = 1.0;
        } else if (rPower > 1.0) {
            lPower -= overPower * (rPower - 1.0);
            rPower = 1.0;
        } else if (lPower < -1.0) {
            rPower += overPower * (-1.0 - lPower);
            lPower = -1.0;
        } else if (rPower < -1.0) {
            lPower += overPower * (-1.0 - rPower);
            rPower = -1.0;
        }

        tankDrive(lPower, rPower);
    }

    /**
     * Read the yaw gyro
     * @return 
     */
    public double getYaw() {
        return yaw.getAngle();
    }

    /**
     * read the pitch gyro
     * @return 
     */
    public double getPitch() {
        return 0;
    }

    /**
     * Set the shifter solenoids
     * @param state 
     */
    public void setShifter(boolean state) {
        shifter.set(state);
    }
    final double angleP = Constants.YAW_P,
            angleI = Constants.YAW_I,
            angleD = Constants.YAW_D;
    double angleInt = 0, prevAngleError = 0;

    /**
     * Closed loop turning by angle
     * @param angle
     * @return boolean indicating actual yaw is within tolerance
     */
    public boolean turnAngle(double angle) {
        double errorThresh = Constants.YAW_ERROR_THRESH;
        double angleError = yaw.getAngle() - angle;
        double turn = angleP * angleError
                + angleI * angleInt
                + angleD * (angleError - prevAngleError);
        if (Math.abs((angleInt + angleError) * angleI) <= 1)//Limit the integration
        {
            angleInt += angleError;
        }
        prevAngleError = angleError;

        tankDrive(turn, -turn);

        return Math.abs(angleError) < errorThresh;
    }

    //Todo: example usage for moveDist and turnAngle
    /**
     * Move drive a specific distance
     * Uses a logistic control loop to control drivetrain position
     * http://en.wikipedia.org/wiki/Logistic_function
     * Saturates the output till actual input is near target. acts as a 
     * smooth bang bang
     * @param dist distance in inches
     * @param actL actual encoder value for left drive
     * @param actR actual encoder value for right
     * @return true if drivetrain has reached target
     */
    public boolean moveDist(double dist, double speed, double actL, double actR) {
        dist /= WHEEL_DIA * Math.PI;  //convert distance from inches into encoder rotations
        double errorThresh = Constants.DISTANCE_ERROR_THRESH;
        double errorL = actL - dist;
        double errorR = actR - dist;

        errorL = (Math.abs(errorL) < errorThresh) ? 0 : errorL;
        errorR = (Math.abs(errorR) < errorThresh) ? 0 : errorR;


        tankDrive(distanceControl(errorL, speed), distanceControl(errorR, speed));

        System.out.println("Drive error L: " + errorL);
        System.out.println("Drive error R: " + errorR);
        return errorL == 0 && errorR == 0;
    }
    
    final double distAttenuation = Constants.DIST_KT;

    /**
     * Custom control loop for drive. Called Logistic control
     * http://en.wikipedia.org/wiki/Logistic_function
     * Saturates the output till actual input is near target. acts as a 
     * smooth bang bang
     * @param distance target position - actual position
     * @param maxSpeed Maximum output. Saturates output to this
     * @return calculated output speed
     */
    private double distanceControl(double distance, double maxSpeed) {
        return maxSpeed * (1 - MathUtils.pow(Math.E, -distAttenuation * distance));
    }
}
