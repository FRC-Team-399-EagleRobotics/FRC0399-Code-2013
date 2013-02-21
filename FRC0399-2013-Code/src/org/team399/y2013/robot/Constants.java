/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot;

/**
 *
 * @author Jeremy
 */
public class Constants {
    
    public static final int DRIVER_LEFT_USB = 1;
    public static final int DRIVER_RIGHT_USB = 2;
    public static final int OPERATOR_USB = 3;
    
    public static final int COMPRESSOR_RELAY = 1;
    public static final int COMPRESSOR_SWITCH = 1;
    
    //Motor Controller ports
    public static final int DRIVE_LEFT_A  = 1;
    public static final int DRIVE_LEFT_B  = 2;
    public static final int DRIVE_RIGHT_A = 4;
    public static final int DRIVE_RIGHT_B = 3;
    
    public static final int WINCH_PORT = 5;
    
    public static final int FEEDER_MOTOR = 7;
    public static final int KICKER_PORT  = 1;
    
    public static final int INTAKE_MOTOR  = 6;
    public static final int INTAKE_SENSOR = 1;
    
    public static final double INTAKE_DISC_THRESH = 0.5;
    
    public static final int SHOOTER_A_ID = 2;
    public static final int SHOOTER_B_ID = 3;
    public static final int SHOOTER_C_ID = 4;
    public static final byte SHOOTER_SYNC_GROUP = 2;
    
    public static final double SHOOTER_KT = 1.3;
    public static final double SHOOTER_KO = 1.0;
    
    public static final double SHOOTER_KV         = 1608.0;
    public static final double SHOOTER_GEAR_RATIO = 0.44642857142;
    
    public static final int ARM_ID = 5;
    
    public static final double ARM_P = 600;
    public static final double ARM_I = .0010;
    public static final double ARM_D = 0.0;
    public static final double ARM_LOWER_LIM = 4.6;
    public static final double ARM_UPPER_LIM = 6.7;
    public static final double ARM_MANUAL_INPUT_SCALAR = .075;
    
    
    public static double STOW_UP     = 5.28;
    public static double INTAKE_LOAD = 0.0;
    public static double HUMAN_LOAD  = 5.9041900634765625;
    public static double HIGH_SHOT   = 5.7086944580078125;
    public static double MID_SHOT    = 0.0;
    public static double LOW_SHOT    = 0.0;
    public static double STOW_DOWN   = ARM_LOWER_LIM;
    
    
    
}
