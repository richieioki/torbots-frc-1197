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
	private double closeHood = 1690;
	private double midHood = 1150;
	private double farHood = 440;
	private double leftHood = 702;
	
	double hoodPosition;
	
	public Robot()
	{
		this.gyro = new AHRS(SPI.Port.kMXP);
		this.stick = new Joystick(0);
		this.stick2 = new Joystick(1);
		this.cypress = new Joystick(2);
		this.stick3 = new Joystick(3);

		this.R1 = new CANTalon(1);
		this.R2 = new CANTalon(2);
		this.R3 = new CANTalon(3);

		this.L1 = new CANTalon(4);
		this.L2 = new CANTalon(5);
		this.L3 = new CANTalon(6);

		this.T1 = new CANTalon(7);

		this.T1.changeControlMode(CANTalon.TalonControlMode.Position);
		this.T1.setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot);
//		    this.T1.reverseOutput(true);

		double p = 40.0D;
		double i = 0.005D;
		double d = 10000.0D;

		double RampRate = 0.0D;
		int profile = 0;
		RampRate = 0.0D;
		profile = 0;
		this.T1.setPID(20.0D, 0.001D, 0.0D, 0.0D, 0, RampRate, 1);//20 for proto joystick y
		this.T1.setPID(16.0D, 0.0D, 0.0D, 0.0D, 0, RampRate, 0);//32 for proto setpoints

		this.P1 = new CANTalon(8);
		this.P2 = new CANTalon(9);

		this.S1 = new Solenoid(0);

		this.shooter1 = new CANTalon(11);
		this.shooter2 = new CANTalon(12);
		this.hood = new CANTalon(10);
		this.hood.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
//		this.hood.changeControlMode(CANTalon.TalonControlMode.Position);
//
//		this.hood.reverseSensor(true);
//		this.hood.enableForwardSoftLimit(true);
//		this.hood.enableReverseSoftLimit(true);
//		this.hood.setForwardSoftLimit(-1.81);
//		this.hood.setReverseSoftLimit(-2.6);
//		//this.hood.setPID(0.01D, 0.0D, 0.0D, 0.0D, 0, 0.0D, 0);
//		this.hood.enable();
		
		hoodPosition = hood.getPulseWidthPosition();

		this.breakBeam = new DigitalInput(4);
		this.breakBeam2 = new DigitalInput(5);

		this.pot = new AnalogPotentiometer(0);
		this.encoder = new Encoder(0, 1);
		this.encoder.setDistancePerPulse(0.017857142857142856D);
		this.driveCANS = new TorCAN(this.R1, this.R2, this.R3, this.L1, this.L2, this.L3);

		this.intakee = new TorIntake(this.stick2, this.P1, this.P2, 
				this.breakBeam, this.breakBeam2, this.siege, shoot, stick3);
		
		this.drive = new TorDrive(this.stick, this.stick2, this.driveCANS, this.encoder, S1);
		
		this.siege = new TorSiege(this.T1, this.stick2, this.pot, this.driveCANS, this.S1, this.stick, 
				this.intakee, this.drive, this.encoder, this.gyro, stick3);

		this.auto = new TorAuto(this.cypress, this.stick, this.stick2, this.gyro, this.encoder, 
				this.driveCANS, this.S1, this.siege, this.intakee, this.drive, shoot, this);

		this.shoot = new TorShooter(this.intakee, this.shooter1, this.shooter2, this.hood, this.P2, 
				this.P1, this.stick3, this.stick2, this.gyro, this.driveCANS, camera);

		this.gyro = new AHRS(SerialPort.Port.kMXP);
		camera = new TorCamera(table, gyro, driveCANS, siege, stick3, lidar, intakee);
	}

	public void robotInit(){
		//hood.setSetpoint(hood.getPulseWidthPosition());
		hood.set(hood.get());
	}

	public void autonomous()
	{
		this.encoder.reset();
		this.siege.PID();
		this.auto.ModeChooser();
	}

	public void operatorControl()
	{
		boolean shootEnabled = false;
		this.encoder.reset();
		gyro.reset();
		this.siege.PID();
		
		
		while (isEnabled()) {
			this.siege.SiegeArmUpdate();
			this.siege.intakeTele();
			this.intakee.intake();
			this.drive.ArcadeDrive(true);

			if (stick2.getRawButton(1))
			{
				S1.set(true);
			}
			else {
				S1.set(false);
			}
			
//			shoot.shooter();
			if(stick3.getRawButton(1)){
				shooter1.set(0.8);
				shooter2.set(0.8);
			}
			else{
				shooter1.set(0);
				shooter2.set(0);
			}
		}
	}

	public void disabled()
	{
		this.T1.disableControl();
	}
	public void test()
	{

//		siege.PID();
		compressor = new Compressor();
		compressor.start();
		while (isEnabled()) {
//			System.out.println("HOOD: " + hood.getPulseWidthPosition());
//			hood.set(stick3.getY());
//			camera.cameraUpdate();
		}

	}
}

