package org.team399.y2013.robot.Systems;

import edu.wpi.first.wpilibj.*;
import org.team399.y2013.Utilities.EagleMath;
import org.team399.y2013.Utilities.FIRFilter;
import org.team399.y2013.Utilities.MovingAverage;
import org.team399.y2013.robot.Constants;

/**
 * Code to control the 3 motor shooter on Team 399's 2013 robot.
 * Uses Bang-Bang with Feed Forward to control the rotational velocity of the shooter wheel
 * Also uses logic to help with graceful degradation - 
 * @author Jeremy
 */
public class Shooter implements Runnable {

    final double maxSpeed = 1;	//Max shooter output value
    final double kV = Constants.SHOOTER_KV * Constants.SHOOTER_GEAR_RATIO;
    //kV by motor:
    //CIM      = 443 RPM/V
    //RS550    = 1608 RPM/V
    //Mini-CIM = 525 RPM/V
    double kT = Constants.SHOOTER_KT; 	//Tuning constant for closed loop velocity control
    double kO = Constants.SHOOTER_KO;  //Tuning constant for open loop failsafe mode control
    final byte SHOOTER_SYNC_GROUP = Constants.SHOOTER_SYNC_GROUP;
    final int SHOOTER_A_ID = Constants.SHOOTER_A_ID;
    final int SHOOTER_B_ID = Constants.SHOOTER_B_ID;
    final int SHOOTER_C_ID = Constants.SHOOTER_C_ID;
    private double shooter_setpoint;
    private CANJaguar shooterA = null;
    private CANJaguar shooterB = null;
    private CANJaguar shooterC = null;
    private Solenoid indicator = new Solenoid(Constants.SHOOTER_INDICATOR_PORT);
    private boolean running = false;
    private boolean isClosedLoop = true;
    private boolean initialized = false;
    private static Shooter singleInstance = null;
    private Thread thread = new Thread(this);
    private double velocity = 0.0;
    //private FIRFilter velocityFilter = new FIRFilter();
    private MovingAverage velFilt = new MovingAverage(10);
    private int stable_counter = 0;

    public Shooter() // make sure that only this class can make instances of Shooter
    {
        //LEAVE THIS BLANK FOR THREADED OPERATION
    }

    /** 
     * Only allow one instance of the class to be in memory at a time.
     * This is important, because the code would crash if 2 instances were made, 
     * due to the CANJaguars.
     * @returns an instance of the shooter object
     */
    public static Shooter getInstance() {
        if (singleInstance == null) {
            singleInstance = new Shooter();
        }
        return singleInstance;
    }

    /**
     * Stop running thread
     */
    public synchronized void stop() {
        running = false;
    }

    /**
     * Start running thread
     * @return flag indicating a successful start
     */
    public synchronized boolean start() {
        if (!running) {
            running = true;
            try {
                thread.start();
                System.out.println("Shooter thread started");
            } catch (Exception error) {
                System.out.println(error);
                running = false;
                return false;
            }
        }
        // Tell the calling code that the shooter thread started correctly
        return true;
    }

    /**
     * 
     * @return a flag indicating a successful initialization
     */
    public synchronized boolean isInitialized() {
        return initialized;
    }

    /**
     * initialize the shooter thread
     */
    private void init() {
        // Don't allow shooter code to run until all 3 motors in the shooter 
        // are properly configured
        while (shooterA == null || shooterB == null || shooterC == null) {
            initialized = false;
            shooterA = initializeJaguar(shooterA, SHOOTER_A_ID);
            shooterB = initializeJaguar(shooterB, SHOOTER_B_ID);
            shooterC = initializeJaguar(shooterC, SHOOTER_C_ID);
        }
        initialized = true;
        System.out.println("Shooter initialized!");
    }
    
    int errorThresh = 10;
    private int[] errorCnt = {0, 0, 0};
    
    /**
     * Returns the calculated velocity. Thread safe version
     * @return 
     */
    public synchronized double getVelocity() {
        return velocity;
    }

    private void incrementErrCount(int CAN_ID) {
        switch (CAN_ID) {
            case SHOOTER_A_ID:
                errorCnt[0]++;
                break;
            case SHOOTER_B_ID:
                errorCnt[1]++;
                break;
            case SHOOTER_C_ID:
                errorCnt[2]++;
                break;
            default:
                System.out.println("Non-valid Shooter CAN ID error");
        }
    }

    private int getErrorCount(int CAN_ID) {
        int count = 0;
        switch (CAN_ID) {
            case SHOOTER_A_ID:
                count = errorCnt[0];
                break;
            case SHOOTER_B_ID:
                count = errorCnt[1];
                break;
            case SHOOTER_C_ID:
                count = errorCnt[2];
                break;
            default:
                System.out.println("Non-valid Shooter CAN ID error");
                count = -1;
        }
        return count;
    }

    /**
     * Initialize the CAN Jaguars for the shooter in one simple function
     * @param toBeInitialized
     * @param CAN_ID
     * @return 
     */
    private CANJaguar initializeJaguar(CANJaguar toBeInitialized, int CAN_ID) {

        incrementErrCount(CAN_ID); // record how many times this jag has been reinitialized.
        // if the count is less than the threshold, try again.
        // otherwise, count this Jag out, and don't saturate the CAN bus trying 
        // to reconfigure it again and again
        if (getErrorCount(CAN_ID) < errorThresh) {
            try {
                if (toBeInitialized == null) {
                    toBeInitialized = new CANJaguar(CAN_ID, CANJaguar.ControlMode.kPercentVbus);
                }

                if (toBeInitialized.getPowerCycled()) // Should be true on first call; like if the bot was just turned on, or a brownout.
                {
                    toBeInitialized.configNeutralMode(CANJaguar.NeutralMode.kCoast);    //Coast to prevent shock loading
//                    // Change Jag to position mode, so that the encoder configuration can be stored in its RAM
                    toBeInitialized.changeControlMode(CANJaguar.ControlMode.kPosition); //Position mode to get encoder input
                    toBeInitialized.setPositionReference(CANJaguar.PositionReference.kQuadEncoder); //quad encoder config'd
                    toBeInitialized.configEncoderCodesPerRev(360);      //we use a 360 CPR encoder
                    toBeInitialized.changeControlMode(CANJaguar.ControlMode.kPercentVbus);  //back to percentVBus so we can use our own algorithm
                    toBeInitialized.setVoltageRampRate(0.0);    //VRamp configuration, maybe no ramp at all
                    toBeInitialized.configFaultTime(0.5); //0.5 second is min time.
                }
            } catch (Throwable e) {
                toBeInitialized = null; // If a jaguar fails to be initialized, then set it to null, and try initializing at a later time
                System.err.println("Jaguar Init CAN ERROR. ID: " + CAN_ID);
                System.out.println(e);
            }
        }

        return toBeInitialized;
    }

    /**
     * Thread run method. 
     */
    public void run() {
        init();
        while (running && initialized) {
            long startTime = System.currentTimeMillis();
            velocityControl(shooter_setpoint);
            long endTime = System.currentTimeMillis();
            
            long dT = endTime - startTime;
            // Try to keep execution rate at a constant 100Hz.
            // If a thread execution takes longer, it starts the next iteration sooner
            // or if it takes shorter, it starts later.
            try {
                Thread.sleep(Math.abs(Math.min(0, 10 - dT)));//1 / (10 mS) = 100 Hz; code iterates 100 times per second
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Drivetrain encoders are plugged into the shooter jaguars for wiring simplicity
     * @return the left drivetrain encoder position
     */
    public synchronized double getLeftDriveEncoder() {
        double answer = -1;
        try {
            answer = shooterB.getPosition();
        } catch(Throwable t) {
            shooterB = this.initializeJaguar(shooterB, SHOOTER_B_ID);
        }
        return answer;
    }
    
    /**
     * Drivetrain encoders are plugged into the shooter jaguars for wiring simplicity
     * @return the right drivetrain encoder position
     */
    public synchronized double getRightDriveEncoder() {
        double answer = -1;
        try {
            answer = shooterC.getPosition();
        } catch(Throwable t) {
            shooterC = this.initializeJaguar(shooterC, SHOOTER_C_ID);
        }
        return answer;
    }

    /**
     * Sets the target velocity
     * @param newSetpoint target vel in RPM
     */
    public synchronized void setShooterSpeed(double newSetpoint) {
        shooter_setpoint = newSetpoint;
    }

    /**
     * 
     * @return the set velocity
     */
    public synchronized double getShooterSetSpeed() {
        return shooter_setpoint;
    }
    private double vel = 0;
    private final double a = 0.0;
    private double prevT = System.currentTimeMillis();
    private double pos = 0, prevPos = 0;
    /**
     * Gets the rotational velocity of the encoder
     * @return 
     */
    private double getEncoderRate() {
        try {
            prevPos = pos;
            pos = shooterA.getPosition(); // is it ShooterA's jag that has the encoder?
            //System.out.println("ShooterEncoderPosition: " + pos);
            //Probably. Will change if different
            double time = System.currentTimeMillis();
            double newVel = (pos - prevPos) / (((time - prevT) * (.0000166666666))); //Velocity is change in position divided by change in unit time, converted to minutes
            prevT = time;

            //vel = velocityFilter.filter(newVel);
            vel = velFilt.calculate(newVel);
            //vel = newVel;

            //vel /= 2;			//Testing showed that output was approx 2x of actual
            if (Math.abs(vel) < 50) {	//zero out any unusually tiny outputs
                vel = 0;
            }
            vel *= -2;
            velocity = vel;
            return vel;
        } catch (Throwable e) {
            shooterA = initializeJaguar(shooterA, SHOOTER_A_ID);
            System.err.println("Error in Velocity calculations");
            System.out.println(e);
        }

        return 0.0; //Returns 0 if there was a fault in above code
    }
    private double error = 0;

    /**
     * Sets new tuning constants
     * @param kT Closed loop tuning constant
     * @param kO Open loop tuning constant
     */
    public synchronized void setTuningConstants(double kT, double kO) {
        this.kT = kT;
        this.kO = kO;
    }
    
    /**
     * Velocity control loop
     * @param setpoint target velocity
     */
    private void velocityControl(double setpoint) {
        double rate = getEncoderRate();
        velocity = rate;
        error = rate-setpoint;	//Calculate error
        double output = 0.0;				//initialize output
        

        double feedFwd;
        feedFwd = (Math.abs(setpoint) / kV);
        feedFwd = fromVolts(feedFwd);
        //feedFwd *= kT;

        //If the shooter is spinning slower than the setpoint, then apply full
        // power. Else, go with the feed forward amount.
        double speedScalar = 1;
        if(Math.abs(setpoint) < 2000) {
            speedScalar = .3;
        }
        
        if(EagleMath.signum(setpoint) > 0) {
            output = ((error < 0) ? maxSpeed*speedScalar : feedFwd * kT);
        } else {
            output = ((error > 0) ? -maxSpeed*speedScalar : -feedFwd * kT);
        }
        indicator.set(isAtTargetSpeed());
        
        

        
        if (rate == 0 || !isClosedLoop) {
          //  System.out.println("Shooter in open loop/feed fwd mode");
            //maybe scale it a bit differently once we are relying on it for speed control
            output = feedFwd * kO*EagleMath.signum(setpoint);
            if (!isClosedLoop) // if we are running in open loop mode, don't print that we are in failsafe, as the operator should be 
            // aware of the malfunction, or has decided that they like open loop control.
            {
                System.err.println("Shooter velocity control in failsafe mode");

                // This is a failsafe mechanism.
                // If the encoder has failed, and is not returning any rate, this code will run.
                // It sets the shooter speed to the feed forward value, so that the operators will still retain some measure of control
                // Without this line, the shooter would always stay at the maxSpeed, regardless of the operator input.

                // This will be called once or twice per robot execution: auton enabled and/or teleop enabled.
                // This is because the shooter may have stopped spinning, this is fine.
                // by the second call of this method, the  error case should be cleared.
            }
        }
        
        if(Math.abs(setpoint) < 50) {
            output = 0;
        }
        
        setMotors(output*1);//EagleMath.signum(setpoint));
    }

    /**
     * 
     * @return a flag indicating shooter is at target speed
     */
    public synchronized boolean isAtTargetSpeed() {
        boolean flag = (Math.abs(error) < 700) || !isClosedLoop;
        if(flag) {
            this.stable_counter++;
        } else {
            this.stable_counter = 0;
        }
        return flag && this.stable_counter >= 25;
    }

    /**
     * enables or disables closed loop control
     * @param flag 
     */
    public synchronized void setIsClosedLoop(boolean flag) {
        isClosedLoop = flag;
    }

    /**
     * Scales a number from a voltage range
     * @param input
     * @return 
     */
    private double fromVolts(double input) {
        return input / 12.0;
    }
    
    public double getCurrent(int motor) {
        double answer = 0;
        CANJaguar thisJag = null;
        if(motor == 0) {
            thisJag = shooterA;
        } else if(motor == 1) {
            thisJag = shooterB;
        } else if(motor == 2) {
            thisJag = shooterC;
        }
        try {
            answer = thisJag.getOutputCurrent();
        } catch (Throwable e) {
                System.err.println("CAN ERROR");
                System.out.println(e);
        }
        return answer;
    }

    /**
     * output signal to the shooter motors
     * @param output 
     */
    public void setMotors(double output) {
        output *=-1;
        if (getErrorCount(SHOOTER_A_ID) < errorThresh) {
            try {
                shooterA.setX(output, (byte) SHOOTER_SYNC_GROUP);
            } catch (Throwable e) {
                shooterA = initializeJaguar(shooterA, SHOOTER_A_ID);
                System.err.println("Shooter motor A CAN ERROR");
                System.out.println(e);
            }
        }
        if (getErrorCount(SHOOTER_B_ID) < errorThresh) {
            try {
                shooterB.setX(output, (byte) SHOOTER_SYNC_GROUP);
            } catch (Throwable e) {
                shooterB = initializeJaguar(shooterB, SHOOTER_B_ID);
                System.err.println("Shooter motor B CAN ERROR");
                System.out.println(e);
            }
        }
        if (getErrorCount(SHOOTER_C_ID) < errorThresh) {
            try {
                shooterC.setX(output, (byte) SHOOTER_SYNC_GROUP);
            } catch (Throwable e) {
                shooterC = initializeJaguar(shooterC, SHOOTER_C_ID);
                System.err.println("Shooter motor C CAN ERROR");
                System.out.println(e);
            }
        }

        try {
            // Only update the shooter values if one (or more) of the shooter motors are active.
            if (getErrorCount(SHOOTER_A_ID) < errorThresh
                    || getErrorCount(SHOOTER_B_ID) < errorThresh
                    || getErrorCount(SHOOTER_C_ID) < errorThresh) {
                CANJaguar.updateSyncGroup((byte) SHOOTER_SYNC_GROUP);
            }
        } catch (Throwable e) {
            System.err.println("Shooter Sync group CAN ERROR. Take note if this prints...");
            System.out.println(e);
        }
    }
}