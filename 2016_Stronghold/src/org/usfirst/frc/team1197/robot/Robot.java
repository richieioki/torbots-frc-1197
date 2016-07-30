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
	double hoodPosition;

	public Robot()
	{
		gyro = new AHRS(SPI.Port.kMXP);
		stick = new Joystick(0);
		stick2 = new Joystick(1);
		cypress = new Joystick(2);
		stick3 = new Joystick(3);

		R1 = new CANTalon(1);
		R2 = new CANTalon(2);

		L1 = new CANTalon(5);
		L2 = new CANTalon(6);

		T1 = new CANTalon(7);

		

		double p = 40.0D;
		double i = 0.005D;
		double d = 10000.0D;

		double RampRate = 0.0D;
		int profile = 0;
		RampRate = 0.0D;
		profile = 0;
		
		T1.changeControlMode(CANTalon.TalonControlMode.Position);
		T1.setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot);
//		T1.reverseOutput(true);
		
		T1.setPID(20.0D, 0.0D, 0.0D, 0.0D, 0, RampRate, 1);
		T1.setPID(16.0D, 0.0D, 0.0D, 0.0D, 0, RampRate, 0);
		//profile 1 = 20 profile 0 = 16
		P1 = new CANTalon(8);
		P2 = new CANTalon(9);
	
		P3 = new CANTalon(4);
		P4 = new CANTalon(3);
		S1 = new Solenoid(0);

		shooter1 = new CANTalon(11);
		shooter2 = new CANTalon(12);
		

		hood = new CANTalon(10);

		breakBeam = new DigitalInput(4);
		breakBeam2 = new DigitalInput(5);
		gyro = new AHRS(SerialPort.Port.kMXP);
		pot = new AnalogPotentiometer(0);
		encoder = new Encoder(0, 1);
		encoder.setDistancePerPulse(0.017857142857142856D);
		driveCANS = new TorCAN(R1, R2, L1, L2);

		intakee = new TorIntake(stick, P1, P2, P3, P4, breakBeam, breakBeam2, siege, shoot); //stick2 -> stick

		drive = new TorDrive(stick2, stick, driveCANS, encoder, S1); //switch stick and stick22
		siege = new TorSiege(T1, stick2, pot, driveCANS, S1, stick, intakee, drive, encoder, gyro, camera);

		camera = new TorCamera(table, gyro, driveCANS, siege, intakee, stick2);

		shoot = new TorShooter(intakee, shooter1, shooter2, hood, P2, P1, stick2, gyro, driveCANS, camera);

		auto = new TorAuto(cypress, stick, stick2, gyro, encoder, driveCANS, S1, siege, intakee, drive, shoot, this, camera);
	}

	public void robotInit()
	{
		hood.set(hood.get());
	}

	public void autonomous()
	{
		shooter1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		shooter2.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		shooter1.set(1);
		shooter2.set(1);
		encoder.reset();
		siege.PID();
		driveCANS.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
		drive.lowGear();
		auto.ModeChooser();
		shooter1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		shooter2.changeControlMode(CANTalon.TalonControlMode.Voltage);
	}

	public void operatorControl()
	{
		shooter1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		shooter2.changeControlMode(CANTalon.TalonControlMode.Voltage);
		encoder.reset();
		gyro.reset();
		siege.PID();

		float rampRate = 15.0F;

		driveCANS.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
		drive.highGear();
		while (isEnabled())
		{
//			System.out.println("POT: " + T1.getAnalogInRaw());
			siege.SiegeArmUpdate();
			siege.intakeTele();
			intakee.intake();
			if (!siege.enabled) {
				drive.ArcadeDrive(true);
			}
			shoot.shooter();
		}
	}

	public void disabled()
	{
		T1.disableControl();
	}

	public void test()
	{
		compressor = new Compressor();
		compressor.start();
		
		gyro.reset();
		
		while (isEnabled())
		{
			drive.ArcadeDrive(true);
//			System.out.println("POT: " + T1.getAnalogInRaw());
//			System.out.println("ENCODER: " + encoder.getDistance());
		}
	}
}
