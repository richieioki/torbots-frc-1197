package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TorCamera implements PIDSource{

	static double CENTER = 160.0;

	NetworkTable m_networkTable;

	public TorCamera(NetworkTable tb){
		m_networkTable = tb;
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// TODO Auto-generated method stub

	}

	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double pidGet() {
		double[] defaultValue = new double[0];

		double[] centerx = m_networkTable.getNumberArray("centerX", defaultValue);
		try{
			double deltaX = CENTER - centerx[0];
			return deltaX;
		}
		catch(Exception e){
			return 0;                          //If no value is found pretend found target (May want to change);
		}

	}

}
