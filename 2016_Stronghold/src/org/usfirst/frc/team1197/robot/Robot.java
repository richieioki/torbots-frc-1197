
package org.usfirst.frc.team1197.robot;


import edu.wpi.first.wpilibj.*;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
	
	
	private Compressor compressor;
	
	private TorCAN driveCANS;
	private TorDrive drive;
	
	private CANTalon R1, R2, R3, L1, L2, L3, T1, P1, P2;
	private Solenoid S1;
    
	private Encoder encoder;
	private Joystick stick;
	private Joystick stick2;
	private TorSiege siege;
	private TorIntake intake;
	
	private TorAuto autoSwitch;
	private Joystick cypress;
	private AHRS gyro;
	private AnalogPotentiometer pot;
	private Ultrasonic sonar;
	public SmartDashboard sd;
	
    public Robot() {
        stick = new Joystick(0);
        stick2 = new Joystick(1);
        
        R1 = new CANTalon(1);
        R2 = new CANTalon(2);
        R3 = new CANTalon(3);
        
        L1 = new CANTalon(4);
        L2 = new CANTalon(5);
        L3 = new CANTalon(6);
        
        T1 = new CANTalon(7);
        
        P1 = new CANTalon(8);
        P2 = new CANTalon(9);
        
        S1 = new Solenoid(0);
        
        sonar = new Ultrasonic(2, 3);
        sonar.setAutomaticMode(true);
        
        pot = new AnalogPotentiometer(0);
        encoder = new Encoder(0,1);
        encoder.setDistancePerPulse(1/TorAuto.GEAR_RATIO);
        driveCANS = new TorCAN(R1, R2, R3, L1, L2, L3);
        siege = new TorSiege(T1, S1, stick2, pot, sonar, driveCANS);
        intake = new TorIntake(stick2, P1, P2);
        Solenoid shift = new Solenoid(1);
        autoSwitch = new TorAuto(cypress, stick2, gyro, encoder, driveCANS, shift, siege, sonar, intake);
        
        drive = new TorDrive(stick, driveCANS);
        gyro = new AHRS(SPI.Port.kMXP);
        /*try {
        	ahrs = new AHRS(SPI.Port.kMXP);
        	
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
//    	compressor = new Compressor();
//    	compressor.start();
    	encoder.reset();
    	autoSwitch.ModeChooser();
    	
    	double potVal = siege.potGet();
    	double range = sonar.getRangeInches();
    	
    	while (isEnabled()){
    	potVal = siege.potGet();
    	range = sonar.getRangeInches();
    	System.out.println(potVal); 
//    	System.out.println(range);
   	}
    }
    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	sonar.setAutomaticMode(true);
//    	double range = sonar.getRangeInches();
    	while(isEnabled()) {
//    		drive.turnToGoal();
//    		siege.potGet();
//			System.out.println(sonar.getRangeInches()); 
//    		double angle = gyro.getAngle();
    		System.out.println(siege.potGet());
    		System.out.println(sonar.getRangeInches());
    		drive.ArcadeDrive(true);
    		autoSwitch.shift();
    		siege.SiegeArmUpdate();
    		
    		intake.intake(); 
//    		if(stick2.getRawButton(3)){
//    			siege.SiegeArmDown();
//    		}
//    		else if(stick2.getRawButton(4)){
//    			siege.SiegeArmUp();
//    		}
//    		else{
//    			siege.stopArm();
//    		}
//    		
//    		if(stick2.getRawButton(5)){
//    			siege.SallyPort();
//    		}
//    		else if(stick2.getRawButton(2)){
//    			siege.stopSally();
//    		}
//    		else{
//    			
//    		}
    		
//    		Timer.delay(0.02);
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
    	
    	compressor = new Compressor();
    	compressor.start();
    	//Drive arm down to lowest position and turn sprocket to 0.1 pot value
    	double range = sonar.getRangeInches();
    	while(isEnabled()){
    			
    			range=sonar.getRangeInches();
    			System.out.println("Sonar: " + range); 
    			System.out.println("POT: " + siege.potGet());
//    			System.out.println("HI, IM MATTeotimotimo");
    		
    	}
    	
    }
}
