
package org.usfirst.frc.team1197.robot;


import edu.wpi.first.wpilibj.*;

public class Robot extends SampleRobot {
	
	private TorCAN driveCANS;
	private TorDrive drive;
	
	private CANTalon R1, R2, R3, L1, L2, L3, T1;
	private Solenoid S1;
    
	private Encoder encoder;
	private Joystick stick;
	private TorSiege siege;
	
	private TorAuto autoSwitch;
	private Joystick cypress;
	
    public Robot() {
        stick = new Joystick(0);
        
        R1 = new CANTalon(1);
        R2 = new CANTalon(2);
        R3 = new CANTalon(3);
        
        L1 = new CANTalon(4);
        L2 = new CANTalon(5);
        L3 = new CANTalon(6);
        
        T1 = new CANTalon(7); //Need to change the ports for T1 and S1
        S1 = new Solenoid(0);
        
        autoSwitch = new TorAuto(cypress);
        
        siege = new TorSiege(T1, S1);
        driveCANS = new TorCAN(R1, R2, R3, L1, L2, L3);
        drive = new TorDrive(stick, driveCANS);
        
        /*try {
        	//ahrs = new AHRS(SPI.Port.kMXP);
        	
        	//distance = new TorDistance(ahrs);
        } catch (RuntimeException ex ) {
            //DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        	DriverStation.reportError(edu.wpi.first.wpilibj.hal.HALUtil.getHALstrerror(), false);
        }*/
        
    	
    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() {
    	autoSwitch.ModeChooser();
    }
    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
		
    	while(isEnabled()) {
//    		drive.turnToGoal();
    		
    		drive.ArcadeDrive(true);
    		
    		if(stick.getRawButton(3)){
    			siege.SiegeArmDown();
    		}
    		else if(stick.getRawButton(4)){
    			siege.SiegeArmUp();
    		}
    		else{
    			siege.stopArm();
    		}
    		
    		if(stick.getRawButton(5)){
    			siege.SallyPort();
    		}
    		else{
    			siege.stopSally();
    		}
    		
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
