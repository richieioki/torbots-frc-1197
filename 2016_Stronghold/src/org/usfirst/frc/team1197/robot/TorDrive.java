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
		this.stepValue = -1.0D;
		this.dec = 0.02D;
		this.previousStick = 0.0D;

		this.m_stick = stick;

		this.m_jagDrive = jagDrive;

		this.table = NetworkTable.getTable("GRIP/myContoursReport");
	}

	public TorDrive(Joystick stick, Joystick stick2, TorCAN cans, Encoder encoder, Solenoid shift)
	{
		this.m_stick = stick;
		this.overrideStick = stick2;
		this.m_jagDrive = cans;
		this.m_encoder = encoder;
		this.m_solenoidshift = shift;
	}

	public void ArcadeDrive(boolean squaredInputs)
	{
		boolean shiftButton = false;

		double stickX = this.m_stick.getX();
		double stickY = this.m_stick.getY();

		stickX = -stickX;
		stickY = -stickY;
		if (Math.abs(stickX) <= 0.1D) {
			stickX = 0.0D;
		}
		if (Math.abs(stickY) <= 0.2D) {
			stickY = 0.0D;
		}
		if (!this.m_solenoidshift.get())
		{
			if ((this.m_jagDrive.m_state != TorCAN.DRIVE_STATE.PIVOTING) && (Math.abs(stickX) > 0.0D) && (Math.abs(stickY) == 0.0D))
			{
				this.m_jagDrive.m_state = TorCAN.DRIVE_STATE.PIVOTING;
				this.m_jagDrive.offGear();
			}
			else if (this.m_jagDrive.m_state == TorCAN.DRIVE_STATE.PIVOTING)
			{
				this.m_jagDrive.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
				this.m_jagDrive.lowGear();
			}
			stickX *= 0.75D;
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
		if (this.m_solenoidshift.get()) {
			this.m_jagDrive.SetDrive(rightMotorSpeed * 0.65D, -leftMotorSpeed * 0.65D);
		} else {
			this.m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
		}
	}

	public void ReverseArcadeDrive(boolean squaredInputs)
	{
		boolean shiftButton = false;

		double stickX = this.m_stick.getX();
		double stickY = this.m_stick.getY();

		stickX = -stickX;
		stickY = -stickY;
		shiftButton = this.m_stick.getRawButton(1);
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
		if (shiftButton) {
			this.m_solenoidshift.set(true);
		} else {
			this.m_solenoidshift.set(false);
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
		this.m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
	}

	public void driveDistance(float distance, float speed, boolean forward)
	{
		this.m_encoder.reset();
		if (forward == true)
		{
			if (distance < 20.0F) {
				this.negOvershoot = 0.0F;
			} else {
				this.negOvershoot = 11.0F;
			}
			distance -= this.negOvershoot;
			do
			{
				if (this.m_encoder.getDistance() > distance) {
					break;
				}
				this.m_jagDrive.SetDrive(speed, -speed);
			} while (!this.overrideStick.getRawButton(10));
		}
		else
		{
			distance += this.negOvershoot;
			while (this.m_encoder.getDistance() >= distance)
			{
				this.m_jagDrive.SetDrive(-speed, speed);
				if (this.overrideStick.getRawButton(10)) {
					break;
				}
			}
		}
	}

	public void highGear()
	{
		this.m_jagDrive.highGear();
	}

	public void lowGear()
	{
		this.m_jagDrive.lowGear();
	}

	public void offGear()
	{
		this.m_jagDrive.offGear();
	}
}
