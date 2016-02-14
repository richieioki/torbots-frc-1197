package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class TorOnTarget {
	
	private CANTalon pidTalon;
	private int tolerance;
	/**
	 * Constructor takes in the PID talon and a tolerance value
	 * @param talon
	 * @param tolerance Tolerance in units, IE 10 = 10 tick tolerance
	 */
	public TorOnTarget(CANTalon talon, int tolerance) {
		pidTalon = talon;
		this.tolerance = tolerance;
	}
	
	/**
	 * Tells the user if they are on target or not
	 * @param location INT location that you want to reach
	 * @return if you are within your target, true if you are within threshold, false if not
	 */
	public boolean OnTarget(int location) {
		int rawValue = pidTalon.getAnalogInRaw();
		if(rawValue > (location - tolerance) && rawValue < (location + tolerance)) {
			return true;
		} else {
			return false;
		}
	}
}