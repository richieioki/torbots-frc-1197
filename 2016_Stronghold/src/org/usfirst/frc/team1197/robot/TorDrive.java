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

	double previousStick, stepValue, dec;
	int sign;

	public TorDrive(Joystick stick, TorCAN jagDrive)
	{
		stepValue = -1;
		dec = 0.02;
		previousStick = 0;


		this.m_stick = stick;

		this.m_jagDrive = jagDrive;

		this.table = NetworkTable.getTable("GRIP/myContoursReport");
		//    this.cam = new TorCamera(this.table);
		this.yawPID = new PIDController(0.001D, 0.0D, 0.0D, this.cam, this.m_jagDrive);

		this.yawPID.setContinuous(true);
		this.yawPID.setInputRange(-160.0D, 160.0D);
		this.yawPID.setOutputRange(-1.0D, 1.0D);
		this.yawPID.setPercentTolerance(5.0D);
		this.yawPID.setSetpoint(0.0D);
		this.yawPID.disable();
	}

	public TorDrive(Joystick stick, Joystick stick2, TorCAN cans, Encoder encoder, Solenoid shift)
	{
		this.m_stick = stick;
		this.overrideStick = stick2;
		this.m_jagDrive = cans;
		this.m_encoder = encoder;
		m_solenoidshift = shift;
	}

	public void ArcadeDrive(boolean squaredInputs)
	{
		double rightMotorSpeed;
		double leftMotorSpeed;

		boolean shiftButton = false;

		double stickX = this.m_stick.getX() * (0.66f);
		double stickY = this.m_stick.getY();

		stickX = -stickX;
		stickY = -stickY;

		//COAST CODE
		if(m_solenoidshift.get()) {
			if(previousStick == 0) {
				previousStick = stickY;
			}

			if(stepValue == -1) {
				if((Math.abs(previousStick) - Math.abs(stickY)) > 30) {
					stepValue = Math.abs(previousStick);
					sign = (int) (stickY/stepValue);
				} 
				previousStick = stickY;
			} else {
				if(Math.abs(stickY) < 0.2) {
					previousStick = stickY;
					stepValue -= dec;
					if(stepValue < 0) {
						stepValue = -1;
						previousStick = 0;
						stickY = 0;
					} else {
						stickY = sign * stepValue;
					}
				}
				else {
					stepValue = -1;
					previousStick = 0;
				}
			}
		}
		//END COAST CODE
		
		if (Math.abs(stickX) <= 0.1D) {
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
				stickY *= stickY;
			} else {
				stickY = -(stickY * stickY);
			}
		}

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

		if(m_solenoidshift.get()) {
			this.m_jagDrive.SetDrive(rightMotorSpeed * 0.65, -leftMotorSpeed * 0.65);
		} else {
			this.m_jagDrive.SetDrive(rightMotorSpeed, -leftMotorSpeed);
		}
	}

	public void ReverseArcadeDrive(boolean squaredInputs)
	{
		double rightMotorSpeed;
		double leftMotorSpeed;

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
}