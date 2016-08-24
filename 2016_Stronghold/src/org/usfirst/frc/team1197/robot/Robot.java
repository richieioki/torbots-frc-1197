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
    StringBuilder _sb = new StringBuilder();
	private int _loops = 0;
	double hoodPosition;
	
//	int x = 5/0;

	public Robot()
	{
		gyro = new AHRS(SPI.Port.kMXP);
		stick = new Joystick(0);
		stick2 = new Joystick(1);
		cypress = new Joystick(2);
		stick3 = new Joystick(3);

		L1 = new CANTalon(1);
		L2 = new CANTalon(2);

		R1 = new CANTalon(5);
		R2 = new CANTalon(6);

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
		intakee = new TorIntake(stick, stick2, P1, P2, P3, P4, breakBeam, breakBeam2, siege, shoot); //stick2 -> stick
		drive = new TorDrive(stick2, stick, driveCANS, encoder, S1); //switch stick and stick22
		siege = new TorSiege(T1, stick2, pot, driveCANS, S1, stick, intakee, drive, encoder, gyro, camera);
		camera = new TorCamera(table, gyro, driveCANS, siege, intakee, stick2);
		shoot = new TorShooter(intakee, shooter1, shooter2, hood, P2, P1, stick2, gyro, driveCANS, camera);
		auto = new TorAuto(cypress, stick, stick2, gyro, encoder, driveCANS, S1, siege, intakee, drive, shoot, this, camera);
	}

	public void robotInit()
	{
	  drive.driving(0, 0, 0, true);
      /* first choose the sensor */
      R1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
      R1.reverseSensor(false);
      //R1.configEncoderCodesPerRev(XXX), // if using FeedbackDevice.QuadEncoder
      //R1.configPotentiometerTurns(XXX), // if using FeedbackDevice.AnalogEncoder or AnalogPot

      /* set the peak and nominal outputs, 12V means full */
      R1.configNominalOutputVoltage(+0.0f, -0.0f);
      R1.configPeakOutputVoltage(+12.0f, -12.0f);
      /* set closed loop gains in slot0 */
      R1.setProfile(0);
      R1.setF(0.263); 
      R1.setP(1); //1
      R1.setI(0); 
      R1.setD(10); //10
      
      /* first choose the sensor */
      L1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
      L1.reverseSensor(true);
      //L1.configEncoderCodesPerRev(XXX), // if using FeedbackDevice.QuadEncoder
      //L1.configPotentiometerTurns(XXX), // if using FeedbackDevice.AnalogEncoder or AnalogPot

      /* set the peak and nominal outputs, 12V means full */
      L1.configNominalOutputVoltage(+0.0f, -0.0f);
      L1.configPeakOutputVoltage(+12.0f, -12.0f);
      /* set closed loop gains in slot0 */
      L1.setProfile(0);
      L1.setF(0.263); 
      L1.setP(1); //1
      L1.setI(0); 
      L1.setD(10); //10

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

		driveCANS.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
		drive.highGear();
		
		while (isEnabled())
		{
//			double targetSpeed = getLeftY() * 0.417 * 4550;
//			double RmotorOutput = R1.getOutputVoltage() / R1.getBusVoltage();
//	    	double LmotorOutput = L1.getOutputVoltage() / L1.getBusVoltage();
	    	
//			_sb.append("\tRout:");
//			_sb.append(RmotorOutput);
//	        _sb.append("\tRspd:");
//	        _sb.append(R1.getSpeed());
//	        
//	        _sb.append("\tRerr:");
//            _sb.append(R1.getClosedLoopError());
//            _sb.append("\tRtrg:");
//            _sb.append(targetSpeed);
//	        
//	        _sb.append("\tLout:");
//			_sb.append(LmotorOutput);
//	        _sb.append("\tLspd:");
//	        _sb.append(L1.getSpeed() );
//            
//            _sb.append("\tLerr:");
//            _sb.append(L1.getClosedLoopError());
//            _sb.append("\tLtrg:");
//            _sb.append(targetSpeed);
            
//            if(++_loops >= 10) {
//	        	_loops = 0;
//	        	System.out.println(_sb.toString());
//	        }
//	        _sb.setLength(0);
            
//			System.out.println("LeftY: " + getLeftY());
			siege.SiegeArmUpdate();
			siege.intakeTele();
			intakee.intake();
			if (!siege.enabled) {
				drive.driving(getLeftY(), getLeftX(), getRightX(), getShiftButton());
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
//			System.out.println("POT: " + T1.getAnalogInRaw());
//			System.out.println("ENCODER: " + encoder.getDistance());
	    	/* get gamepad axis */
	    	double leftYstick = getLeftY();
	    	double RmotorOutput = R1.getOutputVoltage() / R1.getBusVoltage();
	    	double LmotorOutput = L1.getOutputVoltage() / L1.getBusVoltage();
	    	/* prepare line to print */
			_sb.append("\tRout:");
			_sb.append(RmotorOutput);
	        _sb.append("\tRspd:");
	        _sb.append(R1.getSpeed() );
	        
	        _sb.append("\tLout:");
			_sb.append(LmotorOutput);
	        _sb.append("\tLspd:");
	        _sb.append(L1.getSpeed() );
	        
	        if(stick2.getRawButton(3)){
	        	S1.set(true);
	        }
	        else{
	        	S1.set(false);
	        }
	        
	        
	        if(stick2.getRawButton(1)){
	        	/* Speed mode */
	        	double targetSpeed = leftYstick * 1150.0; /* 1500 RPM in either direction */
	        	R1.changeControlMode(TalonControlMode.Speed);
	        	R1.set(targetSpeed); /* 1500 RPM in either direction */
	        	
	        	L1.changeControlMode(TalonControlMode.Speed);
	        	L1.set(targetSpeed);
	
	        	/* append more signals to print when in speed mode. */
	            _sb.append("\tRerr:");
	            _sb.append(R1.getClosedLoopError());
	            _sb.append("\tRtrg:");
	            _sb.append(targetSpeed);
	            
	            _sb.append("\tLerr:");
	            _sb.append(L1.getClosedLoopError());
	            _sb.append("\tLtrg:");
	            _sb.append(targetSpeed);
	        } else {
	        	/* Percent voltage mode */
	        	R1.changeControlMode(TalonControlMode.PercentVbus);
	        	R1.set(leftYstick);
	        	
	        	L1.changeControlMode(TalonControlMode.PercentVbus);
	        	L1.set(leftYstick);
	        }
	
	        if(++_loops >= 10) {
	        	_loops = 0;
	        	System.out.println(_sb.toString());
	        }
	        _sb.setLength(0);
		}
	}
	public double getLeftX(){
		return stick2.getRawAxis(0);
	}
	public double getLeftY(){
		return stick2.getRawAxis(1);
	}
	public double getRightX(){
		return stick2.getRawAxis(4);
	}
	public double getRightY(){
		return stick2.getRawAxis(5);
	}
	public boolean getShiftButton(){
		return stick2.getRawButton(5);
	}
}


///**
// * Example demonstrating the velocity closed-loop servo.
// * Tested with Logitech F350 USB Gamepad inserted into Driver Station]
// * 
// * Be sure to select the correct feedback sensor using SetFeedbackDevice() below.
// *
// * After deploying/debugging this to your RIO, first use the left Y-stick 
// * to throttle the Talon manually.  This will confirm your hardware setup.
// * Be sure to confirm that when the Talon is driving forward (green) the 
// * position sensor is moving in a positive direction.  If this is not the cause
// * flip the boolena input to the SetSensorDirection() call below.
// *
// * Once you've ensured your feedback device is in-phase with the motor,
// * use the button shortcuts to servo to target velocity.  
// *
// * Tweak the PID gains accordingly.
// */
//package org.usfirst.frc.team1197.robot;
//import edu.wpi.first.wpilibj.CANTalon;
//import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Joystick.AxisType;
//import edu.wpi.first.wpilibj.Solenoid;
//import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
//import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
//
//public class Robot extends IterativeRobot {
//  
//	CANTalon _talon = new CANTalon(1);	
//	Joystick _joy = new Joystick(0);	
//	Solenoid shift = new Solenoid(0);
//	StringBuilder _sb = new StringBuilder();
//	int _loops = 0;
//	
//	public void robotInit() {
//        /* first choose the sensor */
//        _talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
//        _talon.reverseSensor(true);
//        //_talon.configEncoderCodesPerRev(XXX), // if using FeedbackDevice.QuadEncoder
//        //_talon.configPotentiometerTurns(XXX), // if using FeedbackDevice.AnalogEncoder or AnalogPot
//
//        /* set the peak and nominal outputs, 12V means full */
//        _talon.configNominalOutputVoltage(+0.0f, -0.0f);
//        _talon.configPeakOutputVoltage(+12.0f, -12.0f);
//        /* set closed loop gains in slot0 */
//        _talon.setProfile(0);
//        _talon.setF(0.917);
//        _talon.setP(2.0);
//        _talon.setI(0); 
//        _talon.setD(0);
//	}
//    /**
//     * This function is called periodically during operator control
//     */
//    public void teleopPeriodic() {
//    	/* get gamepad axis */
//    	double leftYstick = _joy.getAxis(AxisType.kY);
//    	double motorOutput = _talon.getOutputVoltage() / _talon.getBusVoltage();
//    	/* prepare line to print */
//		_sb.append("\tout:");
//		_sb.append(motorOutput);
//        _sb.append("\tspd:");
//        _sb.append(_talon.getSpeed() );
//        
//        if(_joy.getRawButton(3)){
//        	shift.set(true);
//        }
//        else{
//        	shift.set(false);
//        }
//        
//        
//        if(_joy.getRawButton(1)){
//        	/* Speed mode */
//        	double targetSpeed = leftYstick * 1100.0; /* 1500 RPM in either direction */
//        	_talon.changeControlMode(TalonControlMode.Speed);
//        	_talon.set(targetSpeed); /* 1500 RPM in either direction */
//
//        	/* append more signals to print when in speed mode. */
//            _sb.append("\terr:");
//            _sb.append(_talon.getClosedLoopError());
//            _sb.append("\ttrg:");
//            _sb.append(targetSpeed);
//        } else {
//        	/* Percent voltage mode */
//        	_talon.changeControlMode(TalonControlMode.PercentVbus);
//        	_talon.set(leftYstick);
//        }
//
//        if(++_loops >= 10) {
//        	_loops = 0;
//        	System.out.println(_sb.toString());
//        }
//        _sb.setLength(0);
//    }
//}
