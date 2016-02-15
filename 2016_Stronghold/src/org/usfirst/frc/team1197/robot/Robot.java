
package org.usfirst.frc.team1197.robot;



import edu.wpi.first.wpilibj.*;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
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
	private TorIntake intakee;
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
		
		T1.changeControlMode(TalonControlMode.Position);
		T1.setFeedbackDevice(FeedbackDevice.AnalogPot);
		T1.reverseOutput(true);
		
		double p = 8.0;
		double i = 0.0;
		double d = 0.0;
		double RampRate = 0.0;
		int profile = 1;
		RampRate = 0.0;
		profile = 0;
		T1.setPID(p, i, d, 0, 0, RampRate, profile);

		P1 = new CANTalon(8);
		P2 = new CANTalon(9);
		//        E1 = new CANTalon(10); //unsure port
		//        shooter = new CANTalon(11);//unsure

		S1 = new Solenoid(0);

		sonar = new Ultrasonic(2, 3);
		sonar.setAutomaticMode(true);

		pot = new AnalogPotentiometer(0);
		//        breakBeam = new DigitalInput(0);
		encoder = new Encoder(0,1);
		encoder.setDistancePerPulse(1/TorAuto.GEAR_RATIO);
		driveCANS = new TorCAN(R1, R2, R3, L1, L2, L3);
		intakee = new TorIntake(stick2, P1, P2, E1, breakBeam);
		siege = new TorSiege(T1, stick2, pot, sonar, driveCANS, S1, stick, intakee);
		auto = new TorAuto(cypress, stick2, gyro, encoder, driveCANS, S1, siege, intakee);
		shoot = new TorShooter(intakee, shooter, stick3, gyro);


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
		siege.PID();
		auto.ModeChooser();
//		siege.calc();
		double potVal;
		double range;

		while (isEnabled()){
//			potVal = siege.potGet();
//			range = sonar.getRangeInches();
//			System.out.println(potVal); 
			//    	System.out.println(range);
		}
	}
	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		sonar.setAutomaticMode(true);
		siege.PID();
		
		while(isEnabled()) {
			System.out.println("POT: " + siege.potGet());
			siege.SiegeArmUpdate();
			//    		shooter.set(0.3);
//			if(!stick.getRawButton(2)){
//				intakee.intake();
//			}
			//    		intake.autoLoad();
			
			if(!siege.enabled) {
				drive.ArcadeDrive(true);
			}
		}
	}
	public void disabled(){
		T1.disableControl();
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
