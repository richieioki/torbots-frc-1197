package Torbots;

import edu.wpi.first.wpilibj.*;

/**
 * Class to hold all of our autonomous code
 * @author Reference 2015 assignment sheet
 *
 * Auto strategy as of now pick up recycle bin
 * and then push crate.
 */
//	We need to understand the distance in which we need to drive in order to pickup the recycle 
// bin, bring it in, load it, then drive to the crate, bring it in, load it and then release. 

public class TorAuto {

	Jaguar driveJag;
	Jaguar driveJag2;
	Jaguar driveJag3;
	Jaguar driveJag4;
	/**
	 * From the point of view from the driver station.
	 * 
	 * Far left
	 */
	public void location1() {
		driveJag = new Jaguar(1);
		driveJag2 = new Jaguar(2);
		driveJag3 = new Jaguar(3);
		driveJag4 = new Jaguar(4);
	}
	
	
	/**
	 * Center location
	 */
	public void location2() {
		
	}
	
	/**
	 * Far Right
	 */
	public void location3() {
		
	}
	
	
	/**
	 * Autonomous for just driving forward in case
	 * we have an awesome team mate that doesn't need
	 * us to do anything
	 */
	public void just_drive() {
		driveJag.set(0.5);
		driveJag2.set(0.5);
		driveJag3.set(0.5);
		driveJag4.set(0.5);
	}
	
}
