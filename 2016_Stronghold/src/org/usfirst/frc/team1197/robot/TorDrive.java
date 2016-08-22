package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.Encoder;

import java.math.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TorDrive
{
	private Joystick m_stick;
	private Joystick overrideStick;
	private Solenoid m_solenoidshift;
	private TorCAN m_jagDrive;
	private Encoder m_encoder;
	static final float ENC_CON = 11.0F;
	float negOvershoot;
	private TorCamera cam;
	private NetworkTable table;
	private PIDController yawPID;
	double m_speed;
	double m_distance;
	double previousStick;
	double stepValue;
	double dec;
	int sign;
	private double rightMotorSpeed;
	private double leftMotorSpeed;
	
	private double trackWidth = 0.5525; //meters, in inches 21.75
	private double halfTrackWidth = trackWidth / 2;
	private double steeringDeadBand = 0.02;
	private double minTurnRadius = halfTrackWidth;
	private double maxTurnRadius = 20.0;
	private double steeringConstant = (maxTurnRadius - minTurnRadius) / 
			Math.tan((Math.PI / 2) * (1 - steeringDeadBand));
	private double maxThrottle = 0.417;
	private double rightRadius;
	private double leftRadius;
	private double centerRadius = 0.0;

	public TorDrive(Joystick stick, TorCAN jagDrive)
	{
		stepValue = -1.0D;
		dec = 0.02D;
		previousStick = 0.0D;

		m_stick = stick;

		m_jagDrive = jagDrive;

		table = NetworkTable.getTable("GRIP/myContoursReport");
	}

	public TorDrive(Joystick stick, Joystick stick2, TorCAN cans, Encoder encoder, Solenoid shift)
	{
		m_stick = stick;
		overrideStick = stick2;
		m_jagDrive = cans;
		m_encoder = encoder;
		m_solenoidshift = shift;
	}
	public void driving(double throttleAxis, double arcadeSteerAxis, double carSteerAxis){
		if(m_solenoidshift.get() == false){
			ArcadeDrive(throttleAxis, arcadeSteerAxis);
		}
		else{
			carDrive(throttleAxis, carSteerAxis);
		}
	}

	public void ArcadeDrive(double throttleAxis, double arcadeSteerAxis)
	{
		arcadeSteerAxis = -arcadeSteerAxis;
		throttleAxis = -throttleAxis;
		if (Math.abs(arcadeSteerAxis) <= 0.1D) {
			arcadeSteerAxis = 0.0D;
		}
		if (Math.abs(throttleAxis) <= 0.2D) {
			throttleAxis = 0.0D;
		}
//		if (!m_solenoidshift.get()){
//			if (m_jagDrive.m_state == TorCAN.DRIVE_STATE.PIVOTING)
//			{
//				m_jagDrive.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
//				m_jagDrive.lowGear();
//			}
//			arcadeSteerAxis *= 0.9D;
//		}
//		if (arcadeSteerAxis > 1.0D) {
//			arcadeSteerAxis = 1.0D;
//		}
//		if (arcadeSteerAxis < -1.0D) {
//			arcadeSteerAxis = -1.0D;
//		}
//		if (throttleAxis > 1.0D) {
//			throttleAxis = 1.0D;
//		}
//		if (throttleAxis < -1.0D) {
//			throttleAxis = -1.0D;
//		}

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
//		if (m_solenoidshift.get()) {
//			m_jagDrive.SetDrive(rightMotorSpeed * 0.65D, -leftMotorSpeed * 0.65D);
//		} else {
		m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
//		}
	}

	
	public void carDrive(double throttleAxis, double carSteeringAxis){
		
		throttleAxis = -throttleAxis;
		
		if (Math.abs(throttleAxis) <= 0.02) {
			throttleAxis = 0.0D;
		}
		
		throttleAxis *= maxThrottle;

		if (Math.abs(carSteeringAxis) > steeringDeadBand)
		{
			centerRadius = findRadius(carSteeringAxis);
			rightMotorSpeed = throttleAxis * ((centerRadius - halfTrackWidth) / centerRadius);
			leftMotorSpeed = throttleAxis * ((centerRadius + halfTrackWidth) / centerRadius);
		}
		else {
			leftMotorSpeed = throttleAxis;
			rightMotorSpeed = throttleAxis;
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
	public double findRadius(double carSteeringAxis){
		if(carSteeringAxis == 0.0){
			return 0.0;
		}
		else{
			return steeringConstant * (Math.tan((Math.PI / 2) * (1 - carSteeringAxis))) + 
					(minTurnRadius * (Math.abs(carSteeringAxis) / carSteeringAxis));
		}
	}
}
