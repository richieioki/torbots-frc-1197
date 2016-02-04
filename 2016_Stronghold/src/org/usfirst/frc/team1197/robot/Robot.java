
package org.usfirst.frc.team1197.robot;


import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CANTalon;

public class Robot extends SampleRobot {
	//
	
	private TorCAN driveCANS;
	private TorDrive drive;
	private AnalogGyro gyro;
	private TorAuto auto;
	private CANTalon R1, R2, R3, L1, L2, L3;
    
	private Encoder encoder;
	private Joystick stick;
	private Joystick cypress;
	
    public Robot() {
    	encoder = new Encoder(0, 1);
    	encoder.setDistancePerPulse(1/TorAuto.GEAR_RATIO);
        stick = new Joystick(0);
        cypress = new Joystick(2);
        R1 = new CANTalon(1);
        R2 = new CANTalon(2);
        R3 = new CANTalon(3);
        
        L1 = new CANTalon(4);
        L2 = new CANTalon(5);
        L3 = new CANTalon(6);
        
        driveCANS = new TorCAN(R1, R2, R3, L1, L2, L3);
        drive = new TorDrive(stick, driveCANS);
        
        auto = new TorAuto(cypress);
        
        gyro = new AnalogGyro(0);
        
        /*try {
        	//ahrs = new AHRS(SPI.Port.kMXP);
        	
        	//distance = new TorDistance(ahrs);
        } catch (RuntimeException ex ) {
            //DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        	DriverStation.reportError(edu.wpi.first.wpilibj.hal.HALUtil.getHALstrerror(), false);
        }*/
    }
    
    public void robotInit() {
    	auto.initialize();
    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() {
    	encoder.reset();
    	driveCANS.SetDrive(0.8, 0.8);
    	while(encoder.getDistance() > -(11*12)) {
    		SmartDashboard.putNumber("Encoder Value", encoder.getDistance());
    		//Timer.delay(0.02);
    		if(stick.getRawButton(1)) {
    			driveCANS.SetDrive(-0.2, -0.2);
    			Timer.delay(0.05);
    			driveCANS.SetDrive(0.0, 0.0);
    			return;
    		}
    	}
    	driveCANS.SetDrive(0.0, 0.0);
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
		encoder.reset();
    	while(isEnabled()) {
    		SmartDashboard.putNumber("Encoder Ticks", encoder.get());
    		drive.ArcadeDrive(true);
    		Timer.delay(0.02);
    		/*double stickY = stick.getY();
    		if(Math.abs(stickY) < 0.2) {
    			stickY = 0;
    		}
    		if(stickY > 0.8) {
    			stickY = 0.8;
    		} else if(stickY < -0.8) {
    			stickY = -0.8;
    		}
    		jag.set(stickY);*/
    		//talon.set(stickY);
    		//talon2.set(stickY);
    		//talon3.set(stickY);
    		
    		//check for double clicks
    		//also check data coming in
    		//wrap into seperate class for ease of use for kids.
    		//Right now just program get average function, eventually think of other calls.
    		/*if(stick.getRawButton(1) && !clicked) {
    			dist = lidar.getDistance();
    			SmartDashboard.putNumber("Distance:", dist);
    			clicked = true;
    			Timer.delay(0.04);
    		} else if(!stick.getRawButton(1)) {
    			clicked = false;
    		}*/
    		
    		
    		/*if(serialport.getBytesReceived() > 0) {
    			distance = serialport.readString();
    			SmartDashboard.putString("Distance String", distance);
    			dist = Integer.parseUnsignedInt(distance);
    			//SmartDashboard.putNumber("Distance", dist);
    		} else {
    			//DriverStation.reportError("Terminated", false);
    		}*/
    		
    		//SmartDashboard.putNumber("POT", pot.get());
    		//SmartDashboard.putNumber("Encoder Ticks", encoder.getRaw());
    		//ultra.ping();
    		
    		//Timer.delay(0.05);
    		
    		//SmartDashboard.putNumber("Range", ultra.getRangeInches());
    		
    		//SmartDashboard.putNumber("accelX", accel.getX());
    		//SmartDashboard.putNumber("accelY", accel.getY());
    		//SmartDashboard.putNumber("accelZ", accel.getZ());
    		
    	}
    }

    /**
     * Runs during test mode
     */
    public void test() {
    }
}
