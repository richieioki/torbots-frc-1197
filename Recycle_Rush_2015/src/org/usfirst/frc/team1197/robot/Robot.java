
package org.usfirst.frc.team1197.robot;


import Torbots.TorAuto;
import Torbots.TorElevator;
import Torbots.TorJagDrive;
import Torbots.TorPickup;
import Torbots.TorbotDrive;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot {
	Joystick stick;
	Joystick tartarus;
	Joystick jon;
	
	Jaguar fLeftJag,fRightJag,bLeftJag,bRightJag;
	Encoder wheelEncoder;
	Gyro gyro;
	TorJagDrive jagDrive;
	TorbotDrive torDrive;
	
	Solenoid wheelSolenoid;
	Jaguar leftIntake;
	Jaguar rightIntake;
	AnalogInput armSonar;
	DigitalInput pSwitch;
	DigitalInput pSwitchLeft;
	TorPickup torPickup;
	
	Solenoid clamp;
	Solenoid canClamp;
	Jaguar elevatorJag;
	DigitalInput bSwitch;
	Encoder elevatorEncoder;
	TorElevator torElevator;
	PowerDistributionPanel PDP;
	
	SendableChooser autoChooser;
	
	boolean isDone;
	public boolean mode1;
	TorAuto auto;
	public int numTotes;
	boolean isDeploying;	

    public Robot() {
    	stick = new Joystick(0);
		tartarus = new Joystick(1);
		jon = new Joystick(2);
		numTotes = 2;
		isDeploying = false;
		
		fLeftJag = new Jaguar(2);
		bLeftJag = new Jaguar(3);
		fRightJag = new Jaguar(0);
		bRightJag = new Jaguar(1);
		jagDrive = new TorJagDrive(fLeftJag,bLeftJag,fRightJag,bRightJag);
		
		wheelEncoder = new Encoder(0,1,true,EncodingType.k4X);
		gyro = new Gyro(1);   
		torDrive = new TorbotDrive(stick,jagDrive,wheelEncoder,gyro);
		
		wheelSolenoid = new Solenoid(1);
		leftIntake = new Jaguar(4);
		rightIntake = new Jaguar(5);
		armSonar = new AnalogInput(0);
		pSwitch = new DigitalInput(2);
		pSwitchLeft = new DigitalInput(4);
		torPickup = new TorPickup(tartarus,wheelSolenoid, leftIntake, rightIntake, armSonar, pSwitch, jagDrive, jon, pSwitchLeft);
		
		clamp = new Solenoid(0);
		canClamp = new Solenoid(4);
		elevatorJag = new Jaguar(6);
		bSwitch = new DigitalInput(3);
		elevatorEncoder = new Encoder(6,7,false,EncodingType.k4X);
		torElevator = new TorElevator(tartarus, stick, torPickup, elevatorJag, clamp, canClamp, bSwitch, jagDrive, elevatorEncoder);
		
		PDP = new PowerDistributionPanel ();
		
		isDone = false;
		auto = new TorAuto(torDrive,torPickup, torElevator);
		
		mode1 = false;
		
		autoChooser = new SendableChooser();
		autoChooser.addObject("DriveStraight", new Integer(0));
		autoChooser.addObject("Left", new Integer(3));
		autoChooser.addObject("Middle", new Integer(2));
		autoChooser.addObject("Right", new Integer(1));
		autoChooser.addObject("Grab and Go", new Integer(4));
		autoChooser.addDefault("Ramp", new Integer(5));
		autoChooser.addDefault("RampL", new Integer(6));
		autoChooser.addDefault("RampR", new Integer(7));
		SmartDashboard.putData("Choose Auto", autoChooser);
    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() {
    	
    	
    	wheelSolenoid.set(true);
		clamp.set(true);
		gyro.reset();
		wheelEncoder.reset();
		if(!isDone){
			isDone = true;
			int x = (int)autoChooser.getSelected();
			auto.run(x);

		}
    }

    public void wait(double t){
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) / 1000.0 < t){

		}
	}
    
    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	wheelSolenoid.set(true);
		clamp.set(true);
        while (isOperatorControl() && isEnabled()) {
        	if(mode1){
    			if(!isDeploying){
    				torPickup.run();
    			}
    			torElevator.run();
    		}
    		else{
    			torPickup.run2();
    			torElevator.run2();
    		}
    		if(tartarus.getRawButton(4)){
    			mode1 = !mode1;
    			wait(0.1);
    		}
    		torDrive.ArcadeDrive(true);
    		if(tartarus.getRawButton(5)){
    			torPickup.Override(true);
    			torElevator.Override(true);
    		}
    		else{
    			torPickup.Override(false);
    			torElevator.Override(false);
    		}
    		if(tartarus.getRawButton(8)){
    			mode1 = false;
    			isDeploying = true;                                                                                                                                                                                                                                                            
    			torElevator.deploy(numTotes);
    			torPickup.deploy(numTotes);
    			numTotes = 0;
    			isDeploying = false;
    		}
    		
    		/*if(stick.getRawButton(1)) {
    			elevatorJag.set(0.5);
    		}
    		if(stick.getRawButton(2)) {
    			elevatorJag.set(0.0);
    		}*/
    		
    		SmartDashboard.putNumber("Elevator Location", elevatorEncoder.get());
    		SmartDashboard.putNumber("Sonar", armSonar.getVoltage()/0.009766);
    		SmartDashboard.putBoolean("Front Switch", pSwitch.get());
    		SmartDashboard.putBoolean("Left Front Switch", pSwitchLeft.get());
    		SmartDashboard.putBoolean("Bottom Switch", bSwitch.get());
    		SmartDashboard.putNumber("Angle", gyro.getAngle());
    		SmartDashboard.putNumber("Num Totes", numTotes);
    		SmartDashboard.putBoolean("Mode1", mode1);
    		SmartDashboard.putBoolean("Mode2", !mode1);
    		SmartDashboard.putNumber("Wheel Encoder", wheelEncoder.get());
            Timer.delay(0.005);		// wait for a motor update time
        }
    }

    /**
     * Runs during test mode
     */
    public void test() {
    	torElevator.test();
    }
}
