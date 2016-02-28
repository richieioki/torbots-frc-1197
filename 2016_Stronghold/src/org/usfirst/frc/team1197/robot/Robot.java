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

	private CANTalon R1, R2, R3, L1, L2, L3, T1, P1, P2, E1, shooter1,
			shooter2, hood;
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
		gyro = new AHRS(SPI.Port.kMXP);
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

		double p = 40.0;
		double i = 0.005;
		double d = 10000.0; //100,000
		double RampRate = 0.0;
		int profile = 0;
		RampRate = 0.0;
		profile = 0;
		T1.setPID(28.0, 0, 0, 0, 0, RampRate, 1);
		T1.setPID(50.0, 0, 0, 0, 0, RampRate, 0);
//		T1.setProfile(1);

		P1 = new CANTalon(8);
		P2 = new CANTalon(9);
		// shooter = new CANTalon(10);//unsure
		
		S1 = new Solenoid(0);

		shooter1 = new CANTalon(11);
		shooter2 = new CANTalon(12);
		hood = new CANTalon(10);
		hood.changeControlMode(TalonControlMode.Position);
		//hood.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		hood.reverseOutput(false);
		hood.setPID(.01,0,0,0,0,0,0);
		hood.enable();

		// 12-11 shooter
		// 10 window motor

		sonar = new Ultrasonic(2, 3);
		sonar.setAutomaticMode(true);

		pot = new AnalogPotentiometer(0);
		// breakBeam = new DigitalInput(0);
		encoder = new Encoder(0, 1);
		encoder.setDistancePerPulse(1 / TorAuto.GEAR_RATIO);
		driveCANS = new TorCAN(R1, R2, R3, L1, L2, L3);
		intakee = new TorIntake(stick2, P1, P2, breakBeam);
		siege = new TorSiege(T1, stick2, pot, sonar, driveCANS, S1, stick,
				intakee, drive, encoder, gyro);
		auto = new TorAuto(cypress, stick2, gyro, encoder, driveCANS, S1,
				siege, intakee);
		shoot = new TorShooter(intakee, shooter1, shooter2, hood, P2, P1,
				stick3, stick2, gyro, driveCANS);

		drive = new TorDrive(stick, driveCANS);
		gyro = new AHRS(SerialPort.Port.kMXP);
		/*
		 * try { ahrs = new AHRS(SPI.Port.kMXP);
		 * 
		 * //distance = new TorDistance(ahrs); } catch (RuntimeException ex ) {
		 * //DriverStation.reportError("Error instantiating navX MXP:  " +
		 * ex.getMessage(), true);
		 * DriverStation.reportError(edu.wpi.first.wpilibj
		 * .hal.HALUtil.getHALstrerror(), false); }
		 */

	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	public void autonomous() {
		encoder.reset();
		siege.PID();
		auto.ModeChooser();
		// siege.calc();
		double potVal;
		double range;

		while (isEnabled()) {
			// potVal = siege.potGet();
			// range = sonar.getRangeInches();
			// System.out.println(potVal);
			// System.out.println(range);
		}
	}

	/**
	 * Runs the motors with arcade steering.
	 */
	public void operatorControl() {
		sonar.setAutomaticMode(true);
		siege.PID();
		encoder.reset();
		
		while (isEnabled()) {
//			System.out.println("Degrees: " + siege.potGet());
			System.out.println("Distance: " + encoder.getDistance());
//			System.out.println("Angle: " + gyro.getAngle());
//			System.out.println("Hood: " + hood.getPulseWidthPosition());
//			System.out.println("Hood SetPoint " + hood.getSetpoint());
			if(stick3.getRawButton(11)){
				siege.turnToTheta(-20);
////				siege.setDegrees(0.0);
//				siege.haltDrive(0.5);
////				Timer.delay(1);
			}
//			else{
//				encoder.reset();
//			}
			siege.SiegeArmUpdate();
			siege.intakeTele();
			intakee.intake();
			shoot.hood();
			shoot.shoot();
			if (!siege.enabled) {
				drive.ArcadeDrive(true);
			}
		}
	}

	public void disabled() {
		T1.disableControl();
	}

	/**
	 * Runs during test mode
	 */
	public void test() {

		siege.PID();
		compressor = new Compressor();
		compressor.start();
		// siege.portBot();
		// Timer.delay(1);
		// siege.portTop();

		while (isEnabled()) {
			System.out.println("POT: " + siege.potGet());
		}

	}
}
