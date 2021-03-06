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
    public static final boolean HIGH_GEAR = true;
    public static final boolean LOW_GEAR = true;
    public static final int SHIFTER_PORT = 8;
    public static final double DRIVE_HIGH_MAX_SPEED_FPS = 16.5;
    public static final double DRIVE_LOW_MAX_SPEED_FPS  = 6.0;
    public static final boolean YAW_GYRO_ENABLED = true;       //Change this to true on comp bot
    
    
    //Drive algorithm constants
    public static final double CD_SENS_HIGH         = 0.875;
    public static final double CD_SENS_LOW          = 1.111;
    public static final double CD_WHEEL_NONLIN_HIGH = 1.0;
    public static final double CD_WHEEL_NONLIN_LOW  = 0.8;
    public static final double CD_NEG_INERTIA       = 3.0;
    
    
    //Climber Constants:
    public static final int WINCH_PORT = 5;
    public static final int LIMIT_SWITCH_PORT = 5;
    public static final double CLIMBER_UP_SPEED = 1.0; //Comp Bot == 1.0;
    public static final double CLIMBER_DOWN_SPEED = -1.0;
    
    //Feeder constants:
    public static final int FEEDER_MOTOR = 6;//pre spring hook port 7;
    public static final int KICKER_PORT  = 1;
    public static final long KICK_TIME_DELAY = 500; //Time delay between allowed kicks to prevent erratic kicking
    public static final boolean KICKER_OUT = true;
    public static final boolean KICKER_IN = false;
    
    public static final boolean FLAP_OUT = false;
    public static final boolean FLAP_IN = true;
    public static final int FLAP_PORTA = 4;
    public static final int FLAP_PORTB = 3;
    
    //Intake Constants:
    public static final int INTAKE_MOTOR  = 7;// pre spring hook port 6;
    public static final int INTAKE_SENSOR = 4;
    public static final double INTAKE_DISC_THRESH = 0.5;    //Intake sensor value when disc present
    
    //Shooter Constnats:
    public static final int SHOOTER_A_ID = 2;   //CAN IDs
    public static final int SHOOTER_B_ID = 3;
    public static final int SHOOTER_C_ID = 4;
    public static final byte SHOOTER_SYNC_GROUP = 2;
    public static final double SHOOTER_KT = 1.125;    //Closed loop tuning constant
    public static final double SHOOTER_KO = 1.5;    //Open loop tuning constant
    public static final double SHOOTER_KV         = 1608.0; //Motor rpm/v constant
    public static final double SHOOTER_GEAR_RATIO = 0.44642857142;  //Shooter gear ratio
    public static final int SHOOTER_INDICATOR_PORT = 5;
    
    public static final double SHOOTER_SHOT      = 8600;
    public static final double SHOOTER_INTAKE    =-3000;
    public static final double SHOOTER_STOP      = 0;
    public static final double SHOOTER_AUTON_HIGH=7900;
    
    //Arm constants
    public static final int ARM_ID = 5;     //CAN ID
    public static final double ARM_P = 850; //PID constants
    public static final double ARM_I = .00010;
    public static final double ARM_D = 0.0;
    public static final int ZERO_SWITCH_SENSOR = 14;
    public static final double DEGREES_PER_TURN = 61.7142857143;
    
    public static double ARM_LOWER_LIM = 4.65;//4.79 <-SD limit5.185 <-pre spring hook limit //is actually vertical most limit
    public static double ARM_UPPER_LIM = ARM_LOWER_LIM+1.65;
    public static double ARM_MANUAL_INPUT_SCALAR = .0225;   //In theory .02 - .025 should be the optimal range for the new 550
    
    //These setpoints are relative to the arm's topmost limit
    //Arm setpoints
    public static double ARM_STOW_UP     = ARM_LOWER_LIM + .37;
    public static double ARM_INTAKE_LOAD = ARM_LOWER_LIM + 1.327;
    public static double ARM_HUMAN_LOAD  = ARM_LOWER_LIM + 1.327;
    public static double ARM_HIGH_SHOT   = ARM_LOWER_LIM + 1.305;
    public static double ARM_HIGH_SHOT_FRNT   = ARM_LOWER_LIM + 1.235;
    public static double ARM_MID_SHOT    = ARM_LOWER_LIM + 1.42;
    public static double ARM_STOW_DOWN   = ARM_HUMAN_LOAD;
    public static double ARM_LOW_SHOT    = ARM_STOW_DOWN-0.5;
    public static double ARM_CENTER_OFFSET = .0;
    public static double ARM_AUTON_SHOT    = ARM_LOWER_LIM + 1.323;
    public static double ARM_AUTON_STOW_ANGLE = ARM_LOWER_LIM + 0.765;
    
    public static long AUTO_SHOOT_MIN_PERIOD = 375;
    
    public static double AUTO_AIM_ARM_PXL_TO_ANGLE = 0.002;   //Scalar to convert from pixels to arm angle
    public static double VISION_OFFSET_REAR_CTR = -15.0;        //pbot is -15
    public static double VISION_OFFSET_FRNT_CTR = -8.0;
    public static double VISION_OFFSET_REAR_CNR = -12.0;

    public static final int RING_PORT = 6;
    //todo: organize these
}
