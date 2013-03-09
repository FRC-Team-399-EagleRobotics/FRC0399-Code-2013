/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot;

/**
 * Constants file containing all of the seldom changed values such as ports,
 * tuning constants, and time constants.
 * @author Jeremy
 */
public class Constants {
    
    //Driver station constants:
    public static final int DRIVER_LEFT_USB = 1;
    public static final int DRIVER_RIGHT_USB = 2;
    public static final int OPERATOR_USB = 3;
    
    //Compressor constants:
    public static final int COMPRESSOR_RELAY = 1;
    public static final int COMPRESSOR_SWITCH = 1;
    
    //Drivetrain constants
    public static final int DRIVE_LEFT_A  = 1;
    public static final int DRIVE_LEFT_B  = 2;
    public static final int DRIVE_RIGHT_A = 4;
    public static final int DRIVE_RIGHT_B = 3;
    public static final double WHEEL_DIAMETER = 4.0;
    public static final double DISTANCE_ERROR_THRESH = .039887;
    public static final double DIST_KT = .8;
    public static final double YAW_ERROR_THRESH = 1.5;
    public static final double YAW_P = 4.0;
    public static final double YAW_I = 0;
    public static final double YAW_D = 0;
    
    //Climber Constants:
    public static final int WINCH_PORT = 5;
    public static final double CLIMBER_UP_SPEED = 1.0;
    public static final double CLIMBER_DOWN_SPEED = -1.0;
    
    //Feeder constants:
    public static final int FEEDER_MOTOR = 6;//pre spring hook port 7;
    public static final int KICKER_PORT  = 1;
    public static final long KICK_TIME_DELAY = 500; //Time delay between allowed kicks to prevent erratic kicking
    public static final boolean KICKER_OUT = true;
    public static final boolean KICKER_IN = false;
    
    //Intake Constants:
    public static final int INTAKE_MOTOR  = 7;// pre spring hook port 6;
    public static final int INTAKE_SENSOR = 1;
    public static final double INTAKE_DISC_THRESH = 0.5;    //Intake sensor value when disc present
    
    //Shooter Constnats:
    public static final int SHOOTER_A_ID = 2;   //CAN IDs
    public static final int SHOOTER_B_ID = 3;
    public static final int SHOOTER_C_ID = 4;
    public static final byte SHOOTER_SYNC_GROUP = 2;
    public static final double SHOOTER_KT = 1.125;    //Closed loop tuning constant
    public static final double SHOOTER_KO = 1.0;    //Open loop tuning constant
    public static final double SHOOTER_KV         = 1608.0; //Motor rpm/v constant
    public static final double SHOOTER_GEAR_RATIO = 0.44642857142;  //Shooter gear ratio
    
    //Arm constants
    public static final int ARM_ID = 5;     //CAN ID
    public static final double ARM_P = 700; //PID constants
    public static final double ARM_I = .00010;
    public static final double ARM_D = 0.0;
    
    
    public static final double ARM_LOWER_LIM = 4.78;//5.185 <-pre spring hook limit //is actually vertical most limit
    public static final double ARM_UPPER_LIM = ARM_LOWER_LIM+1.315;
    public static final double ARM_MANUAL_INPUT_SCALAR = .015;   
    //These setpoints are relative to the arm's topmost limit
    //Arm setpoints
    public static double STOW_UP     = ARM_UPPER_LIM-1.42;
    public static double INTAKE_LOAD = STOW_UP;
    public static double HUMAN_LOAD  = 5.728;
    public static double HIGH_SHOT   = 5.973;
    public static double MID_SHOT    = 6.1;
    public static double STOW_DOWN   = ARM_UPPER_LIM;
    public static double LOW_SHOT    = STOW_DOWN-0.5;
    
    //todo: organize these
}
