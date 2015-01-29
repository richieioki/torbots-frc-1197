package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import Torbots.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	Joystick stick;
	Joystick tartarus;
	
	Jaguar fLeftJag,fRightJag,bLeftJag,bRightJag;
	Encoder wheelEncoder;
	Gyro gyro;
	TorJagDrive jagDrive;
	TorbotDrive torDrive;
	
	Solenoid deployer;
	TorDeployer torDeploy;
	
	Solenoid clamp;
	Jaguar intake;
	AnalogInput armSonar;
	DigitalInput pSwitch;
	TorPickup torPickup;
	
	Encoder elevatorEncoder;

	PowerDistributionPanel PDP;
	
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		stick = new Joystick(0);
		tartarus = new Joystick(1);
		
		fLeftJag = new Jaguar(0);
		bLeftJag = new Jaguar(1);
		fRightJag = new Jaguar(2);
		bRightJag = new Jaguar(3);
		jagDrive = new TorJagDrive(fLeftJag,bLeftJag,fRightJag,bRightJag);
		
		wheelEncoder = new Encoder(0,1,false,EncodingType.k4X);
		gyro = new Gyro(1);
		torDrive = new TorbotDrive(stick,jagDrive,wheelEncoder,gyro);

		deployer = new Solenoid(0);
		torDeploy = new TorDeployer(stick,deployer);
		
		clamp = new Solenoid(1);
		intake = new Jaguar(4);
		armSonar = new AnalogInput(0);
		pSwitch = new DigitalInput(2);
		torPickup = new TorPickup(tartarus,clamp,intake, armSonar, pSwitch);
		
		elevatorEncoder = new Encoder(3,4,false,EncodingType.k4X);
		

		 
		//    	rb = new RobotDrive(0,1);
		
		
		PDP = new PowerDistributionPanel ();
		
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	public void autonomousInit() {
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		
	}

	/**
	 * This function is called once each time the robot enters tele-operated mode
	 */
	public void teleopInit(){
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
	}


	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

}
