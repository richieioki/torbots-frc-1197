
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
import edu.wpi.first.wpilibj.SPI;

public class Robot extends SampleRobot {
	
    RobotDrive myRobot;
    Joystick stick;
    Jaguar jag;
    AHRS ahrs;
    CANTalon talon, talon2;
    SerialPort serialport;
    Accelerometer accel;
    
    private I2C i2c;
    private TorLidar lidar;
    
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
        lidar = new TorLidar(serialport);
        
        try {
        	ahrs = new AHRS(SPI.Port.kMXP);
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
		boolean clicked = false;
		
    	while(isEnabled()) {
    		
    		//check for double clicks
    		//also check data coming in
    		//wrap into seperate class for ease of use for kids.
    		//Right now just program get average function, eventually think of other calls.
    		if(stick.getRawButton(1) && !clicked) {
    			dist = lidar.getDistance();
    			SmartDashboard.putNumber("Distance:", dist);
    			clicked = true;
    			Timer.delay(0.04);
    		} else if(!stick.getRawButton(1)) {
    			clicked = false;
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
