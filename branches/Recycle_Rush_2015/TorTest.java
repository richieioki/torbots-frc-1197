package Torbots;

import Torbots.TorJagDrive;
import Torbots.TorbotDrive;
import edu.wpi.first.wpilibj.*;
/**
 * Put all test code here
 * @author torbots
 *
 */
public class TorTest {

	/**
	 * Runs all tests and provides some feedback.
	 */
	Jaguar driveJag;
	Jaguar driveJag2;
	Jaguar driveJag3;
	Jaguar driveJag4;
	TorbotDrive myTorbotDrive;
	TorJagDrive myJagDrive;
	Joystick stick1;
	Joystick stick2;
	
	
	public void runAll() {
		driveJag = new Jaguar(1);
		driveJag2 = new Jaguar(2);
		driveJag3 = new Jaguar(3);
		driveJag4 = new Jaguar(4);
		
		
		stick1 = new Joystick(0);
		stick2 = new Joystick(1);
		myTorbotDrive.ArcadeDrive(true);
		
//	Press 3 on Joystick to test all Jaguars to ensure they are all receiving code
		if(stick1.getRawButton(3))
	{
		driveJag.set(0.5);
		driveJag2.set(0.5);
		driveJag3.set(0.5);
		driveJag4.set(0.5);	
	}
	else
	{
		driveJag.set(0.0);
		driveJag2.set(0.0);
		driveJag3.set(0.0);
		driveJag4.set(0.0);	
	}
	}
	
	public void testDrive() {
		
	}
	
	public void testPickup() {
		
	}
	
	public void testElevator() {
		
	}
		
}
