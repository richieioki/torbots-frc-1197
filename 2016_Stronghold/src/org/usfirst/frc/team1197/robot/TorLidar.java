package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Torbots Lidar class to use a Lidar to communicate through RS-232
 * to an Arduino which handles the Lidar.
 * 
 * Currently only consists of a constructor and a getDistance function.
 * Could expand future functionality to do more data checking and use as
 * example for serial communication.
 * 
 * @author Torbot
 */
public class TorLidar {

	SerialPort m_port;

	/**
	 * Takes in an intialized WPI SerialPort.
	 * Expected settings are, BAUD of 9600 and using the kOnboard port
	 */
	public TorLidar(SerialPort sp) {
		m_port = sp;		
	}

	/**
	 * Main function.  Currently asks Arduino to send average
	 * value back and then converts from string to int.
	 * @returns distance, zero if data is bad
	 */
	public int getDistance() {
		m_port.writeString("r\n");            //sends "r\n", r for request
		Timer.delay(0.05);						//delay to allow arduino to loop
		String distance = m_port.readString();  //Data is sent via string.

		//Right now data checking assumes that data is sent in 3 unit sections
		if(distance.trim().length() >= 4) {
			//we have a string that is too long, use the first 3 numbers
			distance = distance.substring(0, 3);
		} else if(distance.length() < 2) { //if the number too small return 0
			return 0;
		} else {
			distance = distance.trim();
		}

		//Conversion from string to int
		try {
			int dist = new Integer(distance);
			return dist;
		} catch(NumberFormatException nfe) {
			DriverStation.reportError("ERROR STRING " + distance + "  LENGITH " + distance.length(), false);
			return 0;
		}
	}
}
