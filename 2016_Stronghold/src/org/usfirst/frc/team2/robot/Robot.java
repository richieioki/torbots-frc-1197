
package org.usfirst.frc.team2.robot;


import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.I2C;

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
	SerialPort port;
    RobotDrive myRobot;
    Joystick stick;
    Jaguar jag;
    AHRS ahrs;
    
    private I2C i2c;
    
    public byte[] distance;
    
    private final int lidar_addr = 0x62;
    private final int lidar_read = 0x00;

    public Robot() {
        //myRobot = new RobotDrive(0, 1);
        //myRobot.setExpiration(0.1);
        stick = new Joystick(0);
        //jag = new Jaguar(0);
        //i2c = new I2C(I2C.Port.kOnboard, lidar_addr);
        
        distance = new byte[2];
        try {
            /* Communicate w/navX MXP via the MXP SPI Bus.                                     */
            /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
            /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
            ahrs = new AHRS(SerialPort.Port.kMXP);
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
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
    	
    	ahrs.reset();
    	
    	while(isEnabled()) {
    		    		
	        double stick_val = -stick.getY();
	        
	        if(stick_val > 0.8) {
	        	stick_val = 0.8;
	        } else if(stick_val < -0.8) {
	        	stick_val = -0.8;
	        } 
	        
	        if(stick_val < 0.2 && stick_val > -0.2) {
	        	stick_val = 0;
	        }
	        
	        //jag.set(stick_val);
	        
	        if(stick.getRawButton(1)) {
	        	ahrs.reset();
	        }
	        
	        //i2c.write(0x00, 0x04);
	        //Timer.delay(0.1);
	        //i2c.read(0x8f, 2, distance);
	        //Timer.delay(0.005);
	        
	        //SmartDashboard.putNumber("FIRST VALUE" , distance[0]);
	        
	        //SmartDashboard.putNumber("LIDAR DISTANCE", Integer.toUnsignedLong(distance[0] << 8) + Byte.toUnsignedInt(distance[1]));
	        
	        /* Display 6-axis Processed Angle Data                                      */
            SmartDashboard.putBoolean(  "IMU_Connected",        ahrs.isConnected());
            SmartDashboard.putBoolean(  "IMU_IsCalibrating",    ahrs.isCalibrating());
            SmartDashboard.putNumber(   "IMU_Yaw",              ahrs.getYaw());
            SmartDashboard.putNumber(   "IMU_Pitch",            ahrs.getPitch());
            SmartDashboard.putNumber(   "IMU_Roll",             ahrs.getRoll());
            
            SmartDashboard.putNumber(   "IMU_Accel_X",          ahrs.getWorldLinearAccelX());
            SmartDashboard.putNumber(   "IMU_Accel_Y",          ahrs.getWorldLinearAccelY());
            SmartDashboard.putBoolean(  "IMU_IsMoving",         ahrs.isMoving());
            SmartDashboard.putBoolean(  "IMU_IsRotating",       ahrs.isRotating());
            
            SmartDashboard.putNumber(   "Velocity_X",           ahrs.getVelocityX());
            SmartDashboard.putNumber(   "Velocity_Y",           ahrs.getVelocityY());
            SmartDashboard.putNumber(   "Displacement_X",       ahrs.getDisplacementX());
            SmartDashboard.putNumber(   "Displacement_Y",       ahrs.getDisplacementY());
            
            SmartDashboard.putNumber(   "RawGyro_X",            ahrs.getRawGyroX());
            SmartDashboard.putNumber(   "RawGyro_Y",            ahrs.getRawGyroY());
            SmartDashboard.putNumber(   "RawGyro_Z",            ahrs.getRawGyroZ());
            
            SmartDashboard.putNumber(   "ANGLE",            ahrs.getAngle());
    	}
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
