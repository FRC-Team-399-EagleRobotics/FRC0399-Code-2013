/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import org.team399.y2013.Utilities.PulseTriggerBoolean;
import org.team399.y2013.robot.Constants;

/**
 *
 * @author Jeremy
 */
public class Arm {

    private CANJaguar arm = null;
    private double ARM_P = Constants.ARM_P, ARM_I = Constants.ARM_I, ARM_D = Constants.ARM_D;
    private double setpoint = Constants.ARM_STOW_UP;
    private boolean enabled = false;
    private int ARM_ID = Constants.ARM_ID;
    private static Arm instance = null;
    private DigitalInput zeroSwitch;
    private double current_position = 0.0;

    public static Arm getInstance() {
        if (instance == null) {
            instance = new Arm();
        }
        return instance;
    }

    private Arm() {
        int initCounter = 0;

        zeroSwitch = new DigitalInput(Constants.ZERO_SWITCH_SENSOR);

        while (initCounter <= 10 && arm == null) {
            arm = initializeArmJaguar(arm, ARM_ID);
            System.out.println("Arm initialized!");
            initCounter++;
        }

        if (initCounter >= 10) {
            System.out.println("Arm Jag init failed after 10 attempts");
        }
        setpoint = Constants.ARM_STOW_UP;
    }

    public double getSetpoint() {
        return setpoint;
    }

    public double getActual() {
        double angle = -1;
        try {
            angle = arm.getPosition();
            this.current_position = angle;
        } catch (Throwable t) {
            System.err.println("ARM CAN Error in getAngle");
            System.out.println(t);

            arm = initializeArmJaguar(arm, ARM_ID);
        }
        return angle;
    }

    /**
     * Returns a setpoint in rotations coverted from degrees from horizontal on
     * the arm Usage: setPointRotations(fromDegrees(angle))
     *
     * @param angle
     * @return
     */
    public double fromDegrees(double angle) {
        angle /= Constants.DEGREES_PER_TURN;
        angle = Constants.ARM_STOW_UP + angle;
        return angle;
    }

    public double toDegrees(double turns) {
        double answer;
        answer = turns;
        answer -= Constants.ARM_STOW_UP;
        answer *= Constants.DEGREES_PER_TURN;
        return answer;
    }
    PulseTriggerBoolean zSwitchWatcher = new PulseTriggerBoolean();

    public void autoZero() {
        zSwitchWatcher.set(getZeroSwitch());
        if (zSwitchWatcher.get()) {
//            System.out.println("Arm Zero Actuated!");
//            System.out.println("Old Upper Limit: " + Constants.ARM_LOWER_LIM);
//            System.out.println("Old UpStow: " + Constants.ARM_STOW_UP);
//            Constants.ARM_LOWER_LIM = getActual() - 1.65;
//            System.out.println("New Upper Limit: " + Constants.ARM_LOWER_LIM);
//            System.out.println("New UpStow: " + Constants.ARM_STOW_UP);

        }
    }

    /**
     * Sets the arm setpoint in terms of pot rotations
     *
     * @param setpoint setpoint in rotations
     */
    public void setPointRotations(double setpoint) {
        //System.out.println("Changing Arm Position. Old: " + this.setpoint + ". New: " + setpoint);
        if (setpoint < 0) {
            setpoint = 0;  //Clamp setpoint to 0-10.
        }
        if (setpoint > 10) {
            setpoint = 10;
        }
        if (setpoint < Constants.ARM_LOWER_LIM) {
            setpoint = Constants.ARM_LOWER_LIM;
        }
        if (setpoint > Constants.ARM_UPPER_LIM) {
            setpoint = Constants.ARM_UPPER_LIM;
        }
        //this.setpoint = 1 * setpoint;	//some scalar from angle to pot turns
//        if(!getZeroSwitch()) {
        this.setpoint = setpoint;
//        }
        try {
            //        arm.changeControlMode(CANJaguar.ControlMode.kPosition);
            if (this.current_position <= 1.0 || this.current_position >= 9.0) {
                faultCondition();
                System.out.println("arm pot fault. consider switching to open loop");
                //arm.enableControl();
                
                //consider reinitializing arm here?
                if(arm.getPowerCycled()) {
                    System.out.println("Arm browned out/power cycled. reinitizing...");
                    arm = initializeArmJaguar(arm, ARM_ID);
                }
            } else {
                arm.setX(this.setpoint);
            }
            
        } catch (Throwable t) {
            System.err.println("ARM CAN Error in setpoint change");
            System.out.println(t);

            arm = initializeArmJaguar(arm, ARM_ID);
        }
    }

    private void faultCondition() {
        try {
            System.out.println("Arm Position Fault");
            //arm.disableControl();
        } catch (Throwable t) {
            System.err.println("ARM CAN Error in setpoint change");
            System.out.println(t);

            arm = initializeArmJaguar(arm, ARM_ID);
        }
    }

    public void setBrake(boolean wantBrake) {

        try {
            if (wantBrake) {
                arm.configNeutralMode(CANJaguar.NeutralMode.kBrake);
            } else {
                arm.configNeutralMode(CANJaguar.NeutralMode.kCoast);
            }
        } catch (Throwable t) {
            System.err.println("ARM CAN Error in brake config");
            System.out.println(t);

            arm = initializeArmJaguar(arm, ARM_ID);
        }
    }

    public boolean getZeroSwitch() {
        return !zeroSwitch.get();
    }

    public double getCurrentOutput() {
        double answer = -1;
        try {
            answer = arm.getOutputCurrent();
        } catch (Throwable t) {
            System.err.println("ARM CAN Error in brake config");
            System.out.println(t);
            arm = initializeArmJaguar(arm, ARM_ID);
        }

        return answer;
    }

    public void setPIDConstants(double P, double I, double D) {
        ARM_P = P;
        ARM_I = I;
        ARM_D = D;

        //Only send the updated constants to the Jag if they are sufficiently 
        //different, to conserve CAN bandwidth.
        if (Math.abs(P - ARM_P) <= 0.000001
                || Math.abs(I - ARM_I) <= 0.000001
                || Math.abs(D - ARM_D) <= 0.000001) {
            setEnabled(enabled);
        }

    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled; // the this is needed because they have the same name
        try {
            if (enabled) {
                arm.setPID(ARM_P, ARM_I, ARM_D);    //not sure if right syntax
                //arm.enableControl();
            } else {
                //arm.disableControl();
            }
        } catch (Throwable t) {
            System.err.println("ARM CAN Error in Enabling");
            System.out.println(t);// remember the driver station error message box is small

            //reconfigure the jag, as in the event of a brown out, it loses configuration
            arm = initializeArmJaguar(arm, ARM_ID);
        }

    }

    private CANJaguar initializeArmJaguar(CANJaguar armJag, int CAN_ID) {

        try {
            if (armJag == null) {
                armJag = new CANJaguar(CAN_ID, CANJaguar.ControlMode.kPosition);
            }

            if (armJag.getPowerCycled()) // Should be true on first call; like if the bot was just turned on, or a brownout.
            {
                // Change Jag to position mode, so that the encoder configuration can be stored in its RAM
                armJag.changeControlMode(CANJaguar.ControlMode.kPosition);
                //armJag.enableControl();

                armJag.setPositionReference(CANJaguar.PositionReference.kPotentiometer);
                armJag.configPotentiometerTurns(10);

                armJag.setPID(ARM_P, ARM_I, ARM_D);
                //armJag.configSoftPositionLimits(Constants.ARM_UPPER_LIM, Constants.ARM_LOWER_LIM);
                //armJag.disableControl();
                //armJag.configMaxOutputVoltage(12.0);
                armJag.setVoltageRampRate(0.0);	//Might want to play with this during testing
                armJag.configFaultTime(0.5); //0.5 second is min time.
                armJag.enableControl();
            }
        } catch (Throwable e) {
            armJag = null; // If a jaguar fails to be initialized, then set it to null, and try initializing at a later time
            System.err.println("ARM Init CAN ERROR. ID: " + CAN_ID);
            System.out.println(e);
        }

        return armJag;
    }
}
