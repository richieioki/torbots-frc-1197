
package org.usfirst.frc.team1197.robot;


import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.CANTalon;

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
	
    RobotDrive myRobot;
    Joystick stick;
    Jaguar jag;
    AHRS ahrs;
    CANTalon talon, talon2;
    SerialPort serialport;
    Accelerometer accel;
    
    private I2C i2c;
    
    public byte[] distance;
    
    private final int lidar_addr = 0x62;
    private final int lidar_read = 0x00;

    public Robot() {
    	accel = new BuiltInAccelerometer();
        stick = new Joystick(0);
        //jag = new Jaguar(0);
        //i2c = new I2C(I2C.Port.kOnboard, 0x62);
        //talon = new CANTalon(1);
        //talon2 = new CANTalon(2);
        
        serialport = new SerialPort(9600, SerialPort.Port.kOnboard);
        serialport.enableTermination('-');
        
        try {
        	
        } catch (RuntimeException ex ) {
            //DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        	DriverStation.reportError(edu.wpi.first.wpilibj.hal.HALUtil.getHALstrerror(), false);
        }
    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() {
        myRobot.setSafetyEnabled(false);
        myRobot.drive(-0.5, 0.0);	// drive forwards half speed
        Timer.delay(2.0);		//    for 2 seconds
        myRobot.drive(0.0, 0.0);	// stop robot
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	int dist;
		String distance;
		
    	while(isEnabled()) {
    		
    		//check for double clicks
    		//also check data coming in
    		//wrap into seperate class for ease of use for kids.
    		//Right now just program get average function, eventually think of other calls.
    		if(stick.getRawButton(1)) {
    			serialport.writeString("r\n");
    			Timer.delay(0.05);
    			distance = serialport.readString();
    			SmartDashboard.putString("Distance", distance);
    		}
    		
    		
    		/*if(serialport.getBytesReceived() > 0) {
    			distance = serialport.readString();
    			SmartDashboard.putString("Distance String", distance);
    			dist = Integer.parseUnsignedInt(distance);
    			//SmartDashboard.putNumber("Distance", dist);
    		} else {
    			//DriverStation.reportError("Terminated", false);
    		}*/
    		
    		SmartDashboard.putNumber("accelX", accel.getX());
    		SmartDashboard.putNumber("accelY", accel.getY());
    		SmartDashboard.putNumber("accelZ", accel.getZ());
    		
    	}
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
