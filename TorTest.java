package Torbots;

import Torbots.TorJagDrive;
import Torbots.TorbotDrive;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Put all test code here
 * @author torbots
 *
 */
public class TorTest {

	/**
	 * Runs all tests and provides some feedback.
	 */
	TorJagDrive jagDrive;
	TorbotDrive torDrive;
	Joystick stick;
	Encoder wheelEncoder;
	Gyro gyro;
	Jaguar fLeftJag;
	Jaguar bLeftJag;
	Jaguar fRightJag;
	Jaguar bRightJag;
	Timer timer;
	
	public void runAll() {
		testDrive();
		testPickup();
		testElevator();
	}
	
	public void testDrive() {
		jagDrive = new TorJagDrive(fLeftJag,bLeftJag,fRightJag,bRightJag);
		torDrive = new TorbotDrive(stick,jagDrive,wheelEncoder,gyro);
		fLeftJag = new Jaguar(0);
		bLeftJag = new Jaguar(1);
		fRightJag = new Jaguar(2);
		bRightJag = new Jaguar(3);
		wheelEncoder = new Encoder(0,1,false,EncodingType.k4X);
		gyro = new Gyro(1);
		stick = new Joystick(1);
		timer = new Timer();
		
		jagDrive.setLeft(0.5);
		jagDrive.setRight(0.5);
		timer.get();
		timer.start();
		while (timer.get() <= 20.0){
			SmartDashboard.putDouble("Wheel Encoder", wheelEncoder.get());
			SmartDashboard.putDouble("Angle", gyro.getAngle());
		}
		jagDrive.setLeft(0.0);
		jagDrive.setRight(0.0);
		timer.stop();
	}
	
	public void testPickup() {

	}
	
	public void testElevator() {
		
	}
		
}
