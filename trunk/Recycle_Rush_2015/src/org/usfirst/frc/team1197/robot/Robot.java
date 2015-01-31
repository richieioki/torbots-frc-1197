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
	Jaguar fLeftJag,fRightJag,bLeftJag,bRightJag;
	Joystick stick;
	Joystick stick2;
	int autoLoopCounter;
	RobotDrive rb;
	AnalogPotentiometer pot;
	Relay spike;
	Encoder e;
	Gyro gyro;
	AnalogInput sonar;
	PowerDistributionPanel PDP;
	DigitalInput limitswitch;
	TorJagDrive jagDrive;
	TorbotDrive torDrive;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		fLeftJag = new Jaguar(0);
		bLeftJag = new Jaguar(1);
		fRightJag = new Jaguar(2);
		bRightJag = new Jaguar(3);
		stick = new Joystick(0);
		stick2 = new Joystick(1);
		sonar = new AnalogInput(2);
		jagDrive = new TorJagDrive(fLeftJag,bLeftJag,fRightJag,bRightJag);
		torDrive = new TorbotDrive(jagDrive,stick,stick2);
		 
		//    	rb = new RobotDrive(0,1);
		
		pot = new AnalogPotentiometer(0);
		spike = new Relay(0);
		e = new Encoder(0,1,false,EncodingType.k4X);
		gyro = new Gyro(1);
		PDP = new PowerDistributionPanel ();
//		limitswitch = new DigitalInput (0);
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	public void autonomousInit() {
		autoLoopCounter = 0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		//    	if(autoLoopCounter < 100) //Check if we've completed 100 loops (approximately 2 seconds)
		//		{
		//			jag.set(0.5);
		SmartDashboard.putBoolean("On", autoLoopCounter == 0);
		//			rb.drive(1.0, 0.0);

		//		}
	}

	/**
	 * This function is called once each time the robot enters tele-operated mode
	 */
	public void teleopInit(){
//		e.reset();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		SmartDashboard.putDouble("Distance", sonar.getVoltage()/0.009766);
//		SmartDashboard.putDouble("Encoder Value", e.getRaw());
//		SmartDashboard.putDouble("Angle", gyro.getAngle());
//		SmartDashboard.putDouble("Voltage", PDP.getVoltage());
//		SmartDashboard.putDouble("Current Ch 14", PDP.getCurrent(14));
//		SmartDashboard.putDouble("Current Ch 15", PDP.getCurrent(15));
//		
//		torDrive.mechanum(true);
//		SmartDashboard.putDouble("FLeft", fLeftJag.get());
//		SmartDashboard.putDouble("FRight", fRightJag.get());
//		SmartDashboard.putDouble("BLeft", bLeftJag.get());
//		SmartDashboard.putDouble("BRight", bRightJag.get());
		

	}


	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}

}