/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import org.team399.y2013.robot.Systems.Arm;
import org.team399.y2013.robot.Systems.Automation.AutoShootController;
import org.team399.y2013.robot.Systems.Climber;
import org.team399.y2013.robot.Systems.DriveTrain;
import org.team399.y2013.robot.Systems.Feeder;
import org.team399.y2013.robot.Systems.Intake;
import org.team399.y2013.robot.Systems.Shooter;

/**
 *
 * @author Jeremy
 */
public class Robot {
    private static Robot instance = null;
    
    public Climber climber;
    public Intake intake;
    public DriveTrain drive;
    public Feeder feeder;
    public Shooter shooter;
    public Compressor comp;
    public AutoShootController autoshoot;
    public Solenoid ringLight;
    //public AxisCamera camera;
    
    
    public Arm arm;
    
    private Robot() {
        climber = new Climber(Constants.WINCH_PORT, Constants.LIMIT_SWITCH_PORT);
        intake = new Intake(Constants.INTAKE_MOTOR, Constants.INTAKE_SENSOR);
        drive = new DriveTrain(Constants.DRIVE_LEFT_A, Constants.DRIVE_LEFT_B, Constants.DRIVE_RIGHT_A, Constants.DRIVE_RIGHT_B);
        feeder = new Feeder(Constants.FEEDER_MOTOR, Constants.KICKER_PORT, Constants.FLAP_PORTA, Constants.FLAP_PORTB);
        shooter = Shooter.getInstance();
        comp = new Compressor(Constants.COMPRESSOR_SWITCH, Constants.COMPRESSOR_RELAY);        
        arm = Arm.getInstance();
        autoshoot = new AutoShootController(shooter, feeder);
        ringLight = new Solenoid(Constants.RING_PORT);
        shooter.start();
        comp.start();
        arm.setEnabled(true);
        ringLight.set(true);
        //camera = AxisCamera.getInstance();
        System.out.println("Robot is done initializing");
    }
    
    public static Robot getInstance() {
        if(instance == null) {
            instance = new Robot();
        }
        return instance;
    }
    
}
