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
		    this.T1.reverseOutput(true);

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
		this.hood.changeControlMode(CANTalon.TalonControlMode.Position);

		this.hood.reverseSensor(true);
		this.hood.enableForwardSoftLimit(true);
		this.hood.enableReverseSoftLimit(true);
		this.hood.setForwardSoftLimit(-1.81);
		this.hood.setReverseSoftLimit(-2.6);
		//this.hood.setPID(0.01D, 0.0D, 0.0D, 0.0D, 0, 0.0D, 0);
		this.hood.enable();
		
		hoodPosition = hood.getPulseWidthPosition();

		this.breakBeam = new DigitalInput(4);
		this.breakBeam2 = new DigitalInput(5);

		this.pot = new AnalogPotentiometer(0);
		this.encoder = new Encoder(0, 1);
		this.encoder.setDistancePerPulse(0.017857142857142856D);
		this.driveCANS = new TorCAN(this.R1, this.R2, this.R3, this.L1, this.L2, this.L3);

		this.intakee = new TorIntake(this.stick2, this.P1, this.P2, this.breakBeam, this.breakBeam2, this.siege);
		this.drive = new TorDrive(this.stick, this.stick2, this.driveCANS, this.encoder, S1);
		this.siege = new TorSiege(this.T1, this.stick2, this.pot, this.driveCANS, this.S1, this.stick, this.intakee, this.drive, this.encoder, this.gyro, stick3);

		this.auto = new TorAuto(this.cypress, this.stick, this.stick2, this.gyro, this.encoder, this.driveCANS, this.S1, this.siege, this.intakee, this.drive, shoot, this);

		this.shoot = new TorShooter(this.intakee, this.shooter1, this.shooter2, this.hood, this.P2, this.P1, this.stick3, this.stick2, this.gyro, this.driveCANS);

		this.gyro = new AHRS(SerialPort.Port.kMXP);
		camera = new TorCamera(table, gyro, driveCANS, siege, stick3, lidar);
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
		//    String autoSelected = (String) chooser.getSelected();
		//	System.out.println("Auto selected: " + autoSelected);
		//	switch(autoSelected) {
		//	case drawBridgeAuto:
		//		auto.DrawBridge();
		//		break;
		//	case sallyPortAuto:
		//		auto.Sallyport();
		//		break;
		//	case portcullisAuto:
		//		auto.Portcullis();
		//		break;
		//	case chevelAuto:
		//		auto.ChevelDeFrise();
		//		break;
		//	case rampartsAuto:
		//		auto.Ramparts();
		//		break;
		//	case rockWallAuto:
		//		auto.RockWall();
		//		break;
		//	case roughTerrainAuto:
		//		auto.RoughTerrain();
		//		break;
		//	case moatAuto:
		//		auto.Moat();
		//		break;
		//	}

		//    auto.autoThrottle();
		/*this.driveCANS.SetDrive(0.0D, 0.0D);
    while (isEnabled())
    {
      this.siege.SiegeArmUpdate();
      this.intakee.intake();
      System.out.println("Distance: " + this.encoder.getDistance());
    }*/
	}

	public void operatorControl()
	{
		boolean shootEnabled = false;
		this.encoder.reset();
		gyro.reset();
		this.siege.PID();
		siege.setDegrees(-42);
		
		while (isEnabled()) {
			//      System.out.println("Degrees: " + this.siege.potGet());
			//      System.out.println("Distance: " + this.encoder.getDistance());
			//      System.out.println("Hood Value" + hood.getPulseWidthPosition());
			//      System.out.println("Siege Enabled: " + this.siege.enabled);
			//    	if(shoot.shooterEnabled) {
			//    	lidar.getDistance();
			//        this.shoot.update();
//			this.siege.SiegeArmUpdate();
			//        this.shoot.adjustShooter();
			this.siege.intakeTele();
			//        siege.turnToReference();
			//        shoot.hoodSet();
			this.intakee.intake();
			//        this.shoot.hood();
			//        this.shoot.shoot();
		    camera.cameraUpdate();
//			this.shoot.hoodSet();
//			System.out.println("Hood Degrees" + shoot.hoodGetDegrees());
			this.drive.ArcadeDrive(true);
			
			/*if (!shootEnabled) {
				
			} else {
				if(stick3.getRawButton(1)) {
					double stickx = -stick3.getX();
					driveCANS.SetDrive(stickx * 0.5, stickx * 0.5);
				}
			}*/
			if (stick2.getRawButton(1))
			{
				S1.set(true);
				System.out.println("!!!!!Solenoid Enabled!!!!!!");
			}
			else if(stick2.getRawButton(2))
				S1.set(false);
			
			if(stick3.getRawButton(1)) {
				shooter1.set(0.8);
				shooter2.set(0.8);
				shootEnabled = true;
			} else if(shootEnabled) {
				shooter1.set(0.0);
				shooter2.set(0.0);
				shootEnabled = false;
			}
			
			//hood.set(stick3.getY());
			
//			hood.set(hoodPosition);
			double hoodStick = stick3.getY();
			if(Math.abs(hoodStick) > 0.05) {
				hood.set(hood.get() + (hoodStick *  0.1));
			}
//			System.out.println("HOOD GET: " + hood.get());
//			System.out.println("HOOD SETPOINT " + hood.getSetpoint());
			//System.out.println("HOOD PULSE VALUE: " + hood.getPulseWidthPosition());
			
		}
	}

	public void disabled()
	{
		this.T1.disableControl();
	}
	public void test()
	{

//		siege.PID();
//		compressor = new Compressor();
//		compressor.start();
		while (isEnabled()) {
			if(stick2.getRawButton(1)){
				S1.set(true);
			}
		}

	}
}

