package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.Encoder;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

public class TorDrive
{	
	private Joystick stick;
	private Joystick overrideStick;
	private Encoder m_encoder;
	private float negOvershoot;
	
	private TorCAN m_jagDrive;
	private boolean isHighGear = true;
	private Solenoid m_solenoidshift;

	private double rightMotorSpeed;
	private double leftMotorSpeed;
	private TorJoystickProfiles profiles;
	private double targetSpeed;
	private double trackWidth = 0.5525; //meters, in inches 21.75
	private double halfTrackWidth = trackWidth / 2;
	private double steeringDeadBand = 0.1;
	private double centerRadius = 0.0;
	private double maxThrottle;
	private double approximateSensorSpeed;

	public TorDrive(Joystick stick, Joystick stick2, TorCAN cans, Encoder encoder, Solenoid shift, double approximateSensorSpeed)
	{
		profiles = new TorJoystickProfiles();
		stick = this.stick;
		overrideStick = stick2;
		m_jagDrive = cans;
		m_encoder = encoder;
		m_solenoidshift = shift;
		approximateSensorSpeed = this.approximateSensorSpeed;
		maxThrottle = (5.0/6.0) * (profiles.getMinTurnRadius() / (profiles.getMinTurnRadius() + halfTrackWidth));
	}
	
	public void driving(double throttleAxis, double arcadeSteerAxis, double carSteerAxis, boolean shiftButton){
		
		if(isHighGear){
			carDrive(throttleAxis, carSteerAxis);
			if(shiftButton){
				shiftToLowGear();
			}
		}
		else{
			ArcadeDrive(throttleAxis, arcadeSteerAxis);
			if(!shiftButton){
				shiftToHighGear();
			}
		}
	}
	
	public void shiftToHighGear(){
		m_solenoidshift.set(false);
		m_jagDrive.chooseVelocityControl();
		isHighGear = true;
	}
	
	public void shiftToLowGear(){
		m_solenoidshift.set(true);
		m_jagDrive.choosePercentVbus();
		isHighGear = false;
	}

	public void ArcadeDrive(double throttleAxis, double arcadeSteerAxis){
		throttleAxis = -throttleAxis;
		if (Math.abs(arcadeSteerAxis) <= 0.1D) {
			arcadeSteerAxis = 0.0D;
		}
		if (Math.abs(throttleAxis) <= 0.2D) {
			throttleAxis = 0.0D;
		}

		if (arcadeSteerAxis >= 0.0D) {
			arcadeSteerAxis *= arcadeSteerAxis;
		} else {
			arcadeSteerAxis = -(arcadeSteerAxis * arcadeSteerAxis);
		}
		if (throttleAxis >= 0.0D) {
			throttleAxis *= throttleAxis;
		} else {
			throttleAxis = -(throttleAxis * throttleAxis);
		}
		
		double rightMotorSpeed;
		double leftMotorSpeed;

		if (throttleAxis > 0.0D)
		{
			if (arcadeSteerAxis > 0.0D)
			{
				leftMotorSpeed = throttleAxis - arcadeSteerAxis;
				rightMotorSpeed = Math.max(throttleAxis, arcadeSteerAxis);
			}
			else
			{
				leftMotorSpeed = Math.max(throttleAxis, -arcadeSteerAxis);
				rightMotorSpeed = throttleAxis + arcadeSteerAxis;
			}
		}
		else
		{
			if (arcadeSteerAxis > 0.0D)
			{
				leftMotorSpeed = -Math.max(-throttleAxis, arcadeSteerAxis);
				rightMotorSpeed = throttleAxis + arcadeSteerAxis;
			}
			else
			{
				leftMotorSpeed = throttleAxis - arcadeSteerAxis;
				rightMotorSpeed = -Math.max(-throttleAxis, -arcadeSteerAxis);
			}
		}
		m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
	}

	
	public void carDrive(double throttleAxis, double carSteeringAxis){
		
		throttleAxis = -throttleAxis;
		carSteeringAxis = -carSteeringAxis;
		
		if (Math.abs(throttleAxis) < 0.1) {
			throttleAxis = 0.0;
		}

		targetSpeed = profiles.findSpeed(throttleAxis) * approximateSensorSpeed;
		
		targetSpeed *= maxThrottle;
		
		if (Math.abs(carSteeringAxis) > steeringDeadBand)
		{
			centerRadius = (Math.abs(throttleAxis) / throttleAxis) * profiles.findRadiusExponential(carSteeringAxis);
			rightMotorSpeed = targetSpeed * ((centerRadius - halfTrackWidth) / centerRadius);
			leftMotorSpeed = targetSpeed * ((centerRadius + halfTrackWidth) / centerRadius);
		}
		else {
			leftMotorSpeed = targetSpeed;
			rightMotorSpeed = targetSpeed;
		}

		m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
		
	}

	public void driveDistance(float distance, float speed, boolean forward)
	{
		m_encoder.reset();
		if (forward == true)
		{
			if (distance < 20.0F) {
				negOvershoot = 0.0F;
			} else {
				negOvershoot = 11.0F;
			}
			distance -= negOvershoot;
			do
			{
				if (m_encoder.getDistance() > distance) {
					break;
				}
				m_jagDrive.SetDrive(speed, -speed);
			} while (!overrideStick.getRawButton(10)); 
		}
		else
		{
			distance += negOvershoot;
			while (m_encoder.getDistance() >= distance)
			{
				m_jagDrive.SetDrive(-speed, speed);
				if (overrideStick.getRawButton(10)) {
					break;
				}
			}
		}
	}

	public void highGear()
	{
		m_jagDrive.highGear();
	}

	public void lowGear()
	{
		m_jagDrive.lowGear();
	}

	public void offGear()
	{
		m_jagDrive.offGear();
	}
}
