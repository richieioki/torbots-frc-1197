package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.Encoder;
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

	public void ArcadeDrive(boolean squaredInputs)
	{
		boolean shiftButton = false;

		double stickX = m_stick.getX();
		double stickY = m_stick.getY();

		stickX = -stickX;
		stickY = -stickY;
		if (Math.abs(stickX) <= 0.1D) {
			stickX = 0.0D;
		}
		if (Math.abs(stickY) <= 0.2D) {
			stickY = 0.0D;
		}
		if (!m_solenoidshift.get())
		{
			if (m_jagDrive.m_state == TorCAN.DRIVE_STATE.PIVOTING)
			{
				m_jagDrive.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
				m_jagDrive.lowGear();
			}
			stickX *= 0.9D;
		}
		if (stickX > 1.0D) {
			stickX = 1.0D;
		}
		if (stickX < -1.0D) {
			stickX = -1.0D;
		}
		if (stickY > 1.0D) {
			stickY = 1.0D;
		}
		if (stickY < -1.0D) {
			stickY = -1.0D;
		}
		if (squaredInputs)
		{
			if (stickX >= 0.0D) {
				stickX *= stickX;
			} else {
				stickX = -(stickX * stickX);
			}
			if (stickY >= 0.0D) {
				stickY *= stickY;
			} else {
				stickY = -(stickY * stickY);
			}
		}
		double rightMotorSpeed;
		double leftMotorSpeed;

		if (stickY > 0.0D)
		{
			if (stickX > 0.0D)
			{
				leftMotorSpeed = stickY - stickX;
				rightMotorSpeed = Math.max(stickY, stickX);
			}
			else
			{
				leftMotorSpeed = Math.max(stickY, -stickX);
				rightMotorSpeed = stickY + stickX;
			}
		}
		else
		{
			if (stickX > 0.0D)
			{
				leftMotorSpeed = -Math.max(-stickY, stickX);
				rightMotorSpeed = stickY + stickX;
			}
			else
			{
				leftMotorSpeed = stickY - stickX;
				rightMotorSpeed = -Math.max(-stickY, -stickX);
			}
		}
		if (m_solenoidshift.get()) {
			m_jagDrive.SetDrive(rightMotorSpeed * 0.65D, -leftMotorSpeed * 0.65D);
		} else {
			m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
		}
	}

	public void ReverseArcadeDrive(boolean squaredInputs)
	{
		boolean shiftButton = false;

		double stickX = m_stick.getX();
		double stickY = m_stick.getY();

		stickX = -stickX;
		stickY = -stickY;
		shiftButton = m_stick.getRawButton(1);
		if (Math.abs(stickX) <= 0.2D) {
			stickX = 0.0D;
		}
		if (Math.abs(stickY) <= 0.2D) {
			stickY = 0.0D;
		}
		if (stickX > 1.0D) {
			stickX = 1.0D;
		}
		if (stickX < -1.0D) {
			stickX = -1.0D;
		}
		if (stickY > 1.0D) {
			stickY = 1.0D;
		}
		if (stickY < -1.0D) {
			stickY = -1.0D;
		}
		if (squaredInputs)
		{
			if (stickX >= 0.0D) {
				stickX *= stickX;
			} else {
				stickX = -(stickX * stickX);
			}
			if (stickY >= 0.0D) {
				stickY = -(stickY * stickY);
			} else {
				stickY *= stickY;
			}
		}
		double rightMotorSpeed;
		double leftMotorSpeed;
		if (stickY > 0.0D)
		{
			if (stickX > 0.0D)
			{
				leftMotorSpeed = stickY - stickX;
				rightMotorSpeed = Math.max(stickY, stickX) * 20.0D;
			}
			else
			{
				leftMotorSpeed = Math.max(stickY, -stickX) * 20.0D;
				rightMotorSpeed = stickY + stickX;
			}
		}
		else
		{

			if (stickX > 0.0D)
			{
				leftMotorSpeed = -Math.max(-stickY, stickX) * 20.0D;
				rightMotorSpeed = stickY + stickX;
			}
			else
			{
				leftMotorSpeed = stickY - stickX;
				rightMotorSpeed = -Math.max(-stickY, -stickX) * 20.0D;
			}
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
