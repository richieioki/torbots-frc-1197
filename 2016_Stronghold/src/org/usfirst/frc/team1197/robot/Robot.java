package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
//import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.PrintStream;

import edu.wpi.first.wpilibj.SerialPort.Port;

public class Robot
extends SampleRobot
{
	private Compressor compressor;
	private TorCAN driveCANS;
	private TorDrive drive;
	private TorLidar lidar;
	private CANTalon R1;
	private CANTalon R2;
	private CANTalon R3;
	private CANTalon L1;
	private CANTalon L2;
	private CANTalon L3;
	private CANTalon T1;
	private CANTalon P1;
	private CANTalon P2;
	private CANTalon P3;
	private CANTalon P4;
	private CANTalon E1;
	private CANTalon shooter1;
	private CANTalon shooter2;
	private CANTalon hood;
	private Solenoid S1;
	private Encoder encoder;
	private Joystick stick;
	private Joystick stick2;
	private Joystick stick3;
	private Joystick cypress;
	private TorSiege siege;
	private TorIntake intakee;
	private TorAuto auto;
	private AHRS gyro;
	private AnalogPotentiometer pot;
	public SmartDashboard sd;
	public SendableChooser chooser;
	Command autoCommand;
	final String drawBridgeAuto = "Draw Bridge";
	final String sallyPortAuto = "Sally Port";
	final String chevelAuto = "Chevel";
	final String portcullisAuto = "Portcullis";
	final String rampartsAuto = "Ramparts";
	final String rockWallAuto = "Rock Wall";
	final String roughTerrainAuto = "Rough Terrain";
	final String moatAuto = "Moat";
	private DigitalInput breakBeam;
	private DigitalInput breakBeam2;
	private TorShooter shoot;
	private TorCamera camera;
	private NetworkTable table;
	private double closeHood = 1690.0D;
	private double midHood = 1150.0D;
	private double farHood = 440.0D;
	private double leftHood = 702.0D;
	double hoodPosition;

	public Robot()
	{
		//this.lidar = new TorLidar(new SerialPort(38400, SerialPort.Port.kOnboard));

		gyro = new AHRS(SPI.Port.kMXP);
		stick = new Joystick(0);
		this.stick2 = new Joystick(1);
		this.cypress = new Joystick(2);
		this.stick3 = new Joystick(3);

		R1 = new CANTalon(1);
		R2 = new CANTalon(2);

		this.L1 = new CANTalon(5);
		this.L2 = new CANTalon(6);

		this.T1 = new CANTalon(7);

		

		double p = 40.0D;
		double i = 0.005D;
		double d = 10000.0D;

		double RampRate = 0.0D;
		int profile = 0;
		RampRate = 0.0D;
		profile = 0;
		
		this.T1.changeControlMode(CANTalon.TalonControlMode.Position);
		this.T1.setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot);
//		this.T1.reverseOutput(true);
		
		this.T1.setPID(20.0D, 0.0D, 0.0D, 0.0D, 0, RampRate, 1);
		this.T1.setPID(16.0D, 0.0D, 0.0D, 0.0D, 0, RampRate, 0);
		//profile 1 = 20 profile 0 = 16
		this.P1 = new CANTalon(8);
		this.P2 = new CANTalon(9);
	
		P3 = new CANTalon(4);
		P4 = new CANTalon(3);
		this.S1 = new Solenoid(0);

		this.shooter1 = new CANTalon(11);
		this.shooter2 = new CANTalon(12);
		

		this.hood = new CANTalon(10);
//		this.hood.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);

//		this.hoodPosition = this.hood.getPulseWidthPosition();

		this.breakBeam = new DigitalInput(4);
		this.breakBeam2 = new DigitalInput(5);
		this.gyro = new AHRS(SerialPort.Port.kMXP);
		this.pot = new AnalogPotentiometer(0);
		this.encoder = new Encoder(0, 1);
		this.encoder.setDistancePerPulse(0.017857142857142856D);
		this.driveCANS = new TorCAN(this.R1, this.R2, this.L1, this.L2);

		this.intakee = new TorIntake(this.stick2, this.P1, this.P2, P3, P4, this.breakBeam, this.breakBeam2, this.siege, this.shoot);

		this.drive = new TorDrive(this.stick, this.stick2, this.driveCANS, this.encoder, this.S1);

		this.siege = new TorSiege(this.T1, this.stick2, this.pot, this.driveCANS, this.S1, this.stick, this.intakee, this.drive, this.encoder, this.gyro, camera);

		this.camera = new TorCamera(this.table, this.gyro, this.driveCANS, this.siege, this.intakee, stick2);

		this.shoot = new TorShooter(this.intakee, this.shooter1, this.shooter2, this.hood, this.P2, this.P1, this.stick2, this.gyro, this.driveCANS, this.camera);

		this.auto = new TorAuto(this.cypress, this.stick, this.stick2, this.gyro, this.encoder, this.driveCANS, this.S1, this.siege, this.intakee, this.drive, this.shoot, this, this.camera);
	}

	public void robotInit()
	{
		this.hood.set(this.hood.get());
	}

	public void autonomous()
	{
		shooter1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		shooter2.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		this.shooter1.set(1);
		this.shooter2.set(1);
		this.encoder.reset();
		this.siege.PID();
		this.driveCANS.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
		drive.lowGear();
		this.auto.ModeChooser();
		shooter1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		shooter2.changeControlMode(CANTalon.TalonControlMode.Voltage);
	}

	public void operatorControl()
	{
		shooter1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		shooter2.changeControlMode(CANTalon.TalonControlMode.Voltage);
		boolean shootEnabled = false;
		this.encoder.reset();
		this.gyro.reset();
		this.siege.PID();

		float rampRate = 15.0F;

		this.driveCANS.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
		this.drive.highGear();
		while (isEnabled())
		{
			this.siege.SiegeArmUpdate();
			this.siege.intakeTele();
			this.intakee.intake();
			if (!this.siege.enabled) {
				this.drive.ArcadeDrive(true);
			}
			this.shoot.shooter();
		}
	}

	public void disabled()
	{
		this.T1.disableControl();
	}

	public void test()
	{
		this.compressor = new Compressor();
		this.compressor.start();
		
		gyro.reset();
		
		while (isEnabled())
		{
			drive.ArcadeDrive(true);
//			System.out.println("POT: " + T1.getAnalogInRaw());
//			System.out.println("ENCODER: " + encoder.getDistance());
		}
	}
}
