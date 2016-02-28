package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TorDrive {
	private Joystick m_stick, overrideStick;
	private Solenoid m_solenoidshift;
	private TorCAN m_jagDrive;
	private Encoder m_encoder;
	final static float ENC_CON = 11;
	float negOvershoot;

	private TorCamera cam;
	private NetworkTable table;
	private PIDController yawPID;
	double m_speed;
	double m_distance;
	
	public TorDrive(Joystick stick, TorCAN jagDrive) {
		m_stick = stick;
		//m_solenoidshift = solenoidshift;
		m_jagDrive = jagDrive;
		
		table = NetworkTable.getTable("GRIP/myContoursReport");
        cam = new TorCamera(table);
        yawPID = new PIDController(0.001, 0, 0, cam, m_jagDrive);
        
        yawPID.setContinuous(true);
        yawPID.setInputRange(-160.0, 160.0);
        yawPID.setOutputRange(-1.0, 1.0);
        yawPID.setPercentTolerance(5.0);
        yawPID.setSetpoint(0.0);
        yawPID.disable();
	}
	
	public TorDrive(Joystick stick, TorCAN cans, Encoder encoder) {
		m_stick = stick;
		m_jagDrive = cans;
		m_encoder = encoder;
	}

	public void ArcadeDrive(boolean squaredInputs) {

		boolean shiftButton = false; //Button 2

		double leftMotorSpeed;
		double rightMotorSpeed;

		// get negative of the stick controls. forward on stick gives negative value  
		double stickX = m_stick.getX();
		double stickY = m_stick.getY();

		stickX = -stickX;
		stickY = -stickY;

		// adjust joystick by dead zone
		if (Math.abs(stickX) <= .2) {
			stickX = 0.0;
		}
		if (Math.abs(stickY) <= .2) {
			stickY = 0.0;
		}

		// make sure X and Y don't go beyond the limits of -1 to 1
		if (stickX > 1.0) {
			stickX = 1.0;
		}
		if (stickX < -1.0) {
			stickX = -1.0;
		}

		if (stickY > 1.0) {
			stickY = 1.0;
		}
		if (stickY < -1.0) {
			stickY = -1.0;
		}

		// square the inputs to produce an exponential power curve
		// this allows finer control with joystick movement and full power as you approach joystick limits
		if (squaredInputs) {
			if (stickX >= 0.0) {
				stickX = (stickX * stickX);
			} else {
				stickX = -(stickX * stickX);
			}

			if (stickY >= 0.0) {
				stickY = (stickY * stickY);
			} else {
				stickY = -(stickY * stickY);
			}
		}

		if (stickY > 0.0) {
			if (stickX > 0.0) {
				leftMotorSpeed = stickY - stickX;
				rightMotorSpeed = Math.max(stickY, stickX);
			} else {
				leftMotorSpeed = Math.max(stickY, -stickX);
				rightMotorSpeed = stickY + stickX ;
			}
		} else {
			if (stickX > 0.0) {
				leftMotorSpeed = -Math.max(-stickY, stickX);
				rightMotorSpeed = stickY + stickX;
			} else {
				leftMotorSpeed = stickY - stickX; 
				rightMotorSpeed = -Math.max(-stickY, -stickX);
			}
		}

		m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
	}

	public void ReverseArcadeDrive(boolean squaredInputs) {



		boolean shiftButton = false; //Button 2

		double leftMotorSpeed;
		double rightMotorSpeed;

		// get negative of the stick controls. forward on stick gives negative value  
		double stickX = m_stick.getX();
		double stickY = m_stick.getY();

		stickX = -stickX;
		stickY = -stickY;
		shiftButton = m_stick.getRawButton(1);


		// adjust joystick by dead zone
		if (Math.abs(stickX) <= .2) {
			stickX = 0.0;
		}
		if (Math.abs(stickY) <= .2) {
			stickY = 0.0;
		}

		// make sure X and Y don't go beyond the limits of -1 to 1
		if (stickX > 1.0) {
			stickX = 1.0;
		}
		if (stickX < -1.0) {
			stickX = -1.0;
		}

		if (stickY > 1.0) {
			stickY = 1.0;
		}
		if (stickY < -1.0) {
			stickY = -1.0;
		}


		//  shift high/low drive gear
		if (shiftButton) {
			m_solenoidshift.set(true);
		} else {
			m_solenoidshift.set(false);
		}


		// square the inputs to produce an exponential power curve
		// this allows finer control with joystick movement and full power as you approach joystick limits
		if (squaredInputs) {
			if (stickX >= 0.0) {
				stickX = (stickX * stickX);
			} else {
				stickX = -(stickX * stickX);
			}

			if (stickY >= 0.0) {
				stickY = -(stickY * stickY);
			} else {
				stickY = (stickY * stickY);
			}
		}

		if (stickY > 0.0) {
			if (stickX > 0.0) {
				leftMotorSpeed = stickY - stickX;
				rightMotorSpeed = (Math.max(stickY, stickX)) * 20;
			} else {
				leftMotorSpeed = (Math.max(stickY, -stickX)) * 20;
				rightMotorSpeed = stickY + stickX;
			}
		} else {
			if (stickX > 0.0) {
				leftMotorSpeed = (-Math.max(-stickY, stickX)) * 20;
				rightMotorSpeed = stickY + stickX;
			} else {
				leftMotorSpeed = stickY - stickX;
				rightMotorSpeed = (-Math.max(-stickY, -stickX)) * 20;
			}
		}
		// set the motor speed
		//  m_driveJag.set(-leftMotorSpeed);
		//  m_driveJag2.set(-leftMotorSpeed);
		//  m_driveJag3.set(rightMotorSpeed);
		//  m_driveJag4.set(rightMotorSpeed);
		m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);

	}
//	public void turnToGoal(){
//		if(m_stick.getRawButton(1)){
//			yawPID.enable();
//		}
//		else{
//			yawPID.disable();
//		}
//	}
//	public void haltDrive(double p){
////		if(overrideStick.getRawButton(10)){
//		m_distance = m_encoder.getDistance();
//		m_speed = -p*m_distance;
//		m_jagDrive.SetDrive(m_speed, -1*m_speed);
////		}
//	}
	public void driveDistance(float distance, float speed, boolean forward){
		m_encoder.reset();
		if(forward==true){
			if(distance<20){
				negOvershoot = 0;
			}
			else{
				negOvershoot = ENC_CON;
			}
			distance = distance - negOvershoot;
			while(m_encoder.getDistance()<= distance){
				m_jagDrive.SetDrive(speed, -speed);
//				Timer.delay(0.02);
				if(overrideStick.getRawButton(10))
					break;
//				Timer.delay(0.02);
//				System.out.println("Distance: " + m_encoder.getDistance());
			}
		}
		else{
			distance = distance + negOvershoot;
			while(m_encoder.getDistance()>= distance){
				m_jagDrive.SetDrive(-speed, speed);
//				Timer.delay(0.02);
				if(overrideStick.getRawButton(10))
					break;
//				Timer.delay(0.02);
//				System.out.println("Distance: " + m_encoder.getDistance());
			}
			}
		}
	}
