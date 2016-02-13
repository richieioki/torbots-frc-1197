
package org.usfirst.frc.team1197.robot;



import edu.wpi.first.wpilibj.*;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
	
	
	private Compressor compressor;
	
	private TorCAN driveCANS;
	private TorDrive drive;
	
	private CANTalon R1, R2, R3, L1, L2, L3, T1, P1, P2, E1, shooter;
	private Solenoid S1;
    
	private Encoder encoder;
	private Joystick stick, stick2, stick3, cypress;
	private TorSiege siege;
	private TorIntake intake;
	private TorAuto auto;
	private AHRS gyro;
	private AnalogPotentiometer pot;
	private Ultrasonic sonar;
	public SmartDashboard sd;
	private DigitalInput breakBeam;
	private TorShooter shoot;
	
	
    public Robot() {
        stick = new Joystick(0);
        stick2 = new Joystick(1);
        cypress = new Joystick(2);
        stick3 = new Joystick(3);
        
        R1 = new CANTalon(1);
        R2 = new CANTalon(2);
        R3 = new CANTalon(3);
        
        L1 = new CANTalon(4);
        L2 = new CANTalon(5);
        L3 = new CANTalon(6);
        
        T1 = new CANTalon(7);
        
        P1 = new CANTalon(8);
        P2 = new CANTalon(9);
        E1 = new CANTalon(10); //unsure port
        shooter = new CANTalon(11);//unsure
        
        S1 = new Solenoid(0);
        
        sonar = new Ultrasonic(2, 3);
        sonar.setAutomaticMode(true);
        
        pot = new AnalogPotentiometer(0);
        breakBeam = new DigitalInput(0);
        encoder = new Encoder(0,1);
        encoder.setDistancePerPulse(1/TorAuto.GEAR_RATIO);
        driveCANS = new TorCAN(R1, R2, R3, L1, L2, L3);
        siege = new TorSiege(T1, stick2, pot, sonar, driveCANS, S1);
        intake = new TorIntake(stick2, P1, P2, E1, breakBeam);
        auto = new TorAuto(cypress, stick2, gyro, encoder, driveCANS, S1, siege, intake);
        shoot = new TorShooter(intake, shooter, stick3, gyro);
        
        
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
    	encoder.reset();
    	auto.ModeChooser();
    	
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
    	System.out.println("If the pot is good: " + siege.potChecker());
    	while(isEnabled()) {
    		
    		System.out.println(siege.potGet());
    		drive.ArcadeDrive(true);
    		siege.SiegeArmUpdate();
    		shooter.set(0.3);
    		intake.intake();
    		intake.autoLoad();
    		
    		//CHANGE BUTTONS
    		if(stick.getRawButton(5)){
    			siege.DrawBridgeStates();
    		}
    		if(stick.getRawButton(5)){
    			siege.PortcullisStates();
    		}
    		if(stick.getRawButton(5)){
    			siege.SallyPortStates();
    		}
    		if(stick.getRawButton(5)){
    			siege.ChevelStates();
    		}
    		


    		
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
    		
    		
//    		//list all defenses and movements for each class
//    		if(!tele.override()){
//    			
//				switch(m_state){
//    				
//    				
//    			case TEST1:
//    				//SiegeStates.A
//    				
//    			}
//    		}
    		
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
