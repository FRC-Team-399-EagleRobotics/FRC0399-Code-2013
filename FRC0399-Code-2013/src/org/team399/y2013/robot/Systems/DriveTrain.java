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
    private Solenoid shifter = new Solenoid(Constants.SHIFTER_PORT);
    private final double WHEEL_DIA = Constants.WHEEL_DIAMETER;
    boolean gear = true;
    
    public double leftOutput = 0;
    public double rightOutput = 0;

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
        yaw.reset();
        //pitch.reset();
    }

    /**
     * Tank drive method. send signal to motor controllers to individual side
     * @param leftPWM
     * @param rightPWM 
     */
    public void tankDrive(double leftPWM, double rightPWM) {
        this.leftOutput = leftPWM;
        this.rightOutput = rightPWM;
        if (Math.abs(leftPWM) < .1) {
            leftPWM = 0;
        }
        if (Math.abs(rightPWM) < .1) {
            rightPWM = 0;
        }
        m_leftA.set(rightPWM);
        m_leftB.set(rightPWM);
        m_rightA.set(-leftPWM);
        m_rightB.set(-leftPWM);
    }
    private double old_wheel = 0.0;
    private double neg_inertia_accumulator = 0.0;

    public void cheesyDrive(double wheel, double throttle) {
        double left_pwm, right_pwm, overPower;
        double sensitivity = 1.2;
        double angular_power;
        double linear_power;
        double wheelNonLinearity;
        boolean quickTurn = Math.abs(throttle) < .05;//Math.abs(wheel) > .375 &&
        
        double neg_inertia = wheel - old_wheel;
        old_wheel = wheel;

        if (gear == Constants.HIGH_GEAR) {
            wheelNonLinearity = 1.0;        //Used to be .9 higher is less sensitive
            // Apply a sin function that's scaled to make it feel bette
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        } else {
            wheelNonLinearity = 0.8;
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        }

        double neg_inertia_scalar;
        if (gear == Constants.HIGH_GEAR) {
            neg_inertia_scalar = 3;
            sensitivity = 1.15;
        } else {
            if (wheel * neg_inertia > 0) {
                neg_inertia_scalar = 5;
            } else {
                if (Math.abs(wheel) > 0.65) {
                    neg_inertia_scalar = 10;
                } else {
                    neg_inertia_scalar = 3;
                }
            }
            sensitivity = 1.11; //lower is less sensitive

            if (Math.abs(throttle) > 0.1) {
                sensitivity = .9 - (.9 - sensitivity) / Math.abs(throttle);
            }
        }
        neg_inertia_scalar *= .4;
        double neg_inertia_power = neg_inertia * neg_inertia_scalar;
        if (Math.abs(throttle) >= 0.05 || quickTurn) {
            neg_inertia_accumulator += neg_inertia_power;
        }
        wheel = wheel + neg_inertia_accumulator;
        if (neg_inertia_accumulator > 1) {
            neg_inertia_accumulator -= 1;
        } else if (neg_inertia_accumulator < -1) {
            neg_inertia_accumulator += 1;
        } else {
            neg_inertia_accumulator = 0;
        }

        linear_power = throttle;

        if ((!EagleMath.isInBand(throttle, -0.2, 0.2) || !(EagleMath.isInBand(wheel, -0.65, 0.65))) && quickTurn) {
            overPower = 1.0;
            if (gear == Constants.HIGH_GEAR) {
                sensitivity = 1.0;
            } else {
                sensitivity = 1.0;
            }
            sensitivity = 1.0;
            angular_power = wheel;
        } else {
            overPower = 0.0;
            angular_power = Math.abs(throttle) * wheel * sensitivity;
        }

        if(quickTurn) {
            angular_power = EagleMath.signedSquare(angular_power, 1);   //make turning less sensitive under quickturn
        }
        
        right_pwm = left_pwm = linear_power;
        left_pwm += angular_power;
        right_pwm -= angular_power;

        if (left_pwm > 1.0) {
            right_pwm -= overPower * (left_pwm - 1.0);
            left_pwm = 1.0;
        } else if (right_pwm > 1.0) {
            left_pwm -= overPower * (right_pwm - 1.0);
            right_pwm = 1.0;
        } else if (left_pwm < -1.0) {
            right_pwm += overPower * (-1.0 - left_pwm);
            left_pwm = -1.0;
        } else if (right_pwm < -1.0) {
            left_pwm += overPower * (-1.0 - right_pwm);
            right_pwm = -1.0;
        }
        tankDrive((left_pwm), (right_pwm));
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

        tankDrive(sum, dif);
    }

    public double twoStickToTurning(double left, double right) {
        return (left - right) / 2;
    }

    public double twoStickToThrottle(double left, double right) {
        return (left + right) / 2;
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
        gear = state;
        shifter.set(gear);
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
