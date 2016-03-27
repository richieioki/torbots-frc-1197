package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

public class TorTeleop
{
	private Solenoid shift;
	private Encoder m_encoder;
	private Joystick tele;
	private TorDrive m_drive;
	private TorCAN m_cans;
	public static final double GEAR_RATIO = 56.0D;
	public double encoderDistance = 0.0D;
	public static final double TARGET_DISTANCE = 45.0D;
	public CANTalon R1;
	public CANTalon R2;
	public CANTalon R3;
	public CANTalon L1;
	public CANTalon L2;
	public CANTalon L3;
	public CANTalon T1;
	public int defense = 0;
	public int lane = 0;
	private AHRS gyro;
	private TorSiege siege;
	private Ultrasonic sonar;
	private Joystick tele2;
	private double potValue;
	private TorIntake intake;

	public TorTeleop(Joystick stick1, Joystick stick2, TorCAN m_cans, TorSiege siege)
	{
		this.tele = stick1;
		this.tele2 = stick2;
		this.m_cans = m_cans;
		this.siege = siege;
	}

	public void DrawBridgeTeleop()
	{
		if (this.tele.getRawButton(5))
		{
			if (this.siege.potGet() > 673.0D)
			{
				this.siege.SiegeArmUp();
				this.m_cans.SetDrive(0.0D, 0.0D);
			}
			Timer.delay(0.5D);
			this.m_cans.SetDrive(-0.5D, 0.5D);
			Timer.delay(1.7D);
			if (this.siege.potGet() > 183.0D)
			{
				this.siege.SiegeArmUp();
				this.m_cans.SetDrive(0.0D, 0.0D);
			}
			Timer.delay(0.5D);
			this.m_cans.SetDrive(0.5D, -0.5D);
		}
	}

	public void ChevelTeleop()
	{
		if (this.tele.getRawButton(3))
		{
			if (this.siege.potGet() > 226.0D)
			{
				this.siege.SiegeArmUp();
				this.m_cans.SetDrive(0.0D, 0.0D);
			}
			Timer.delay(1.5D);
			this.siege.stopArm();
			this.m_cans.SetDrive(-0.4D, 0.4D);
			Timer.delay(0.3D);
			this.m_cans.SetDrive(0.0D, 0.0D);

			this.m_cans.SetDrive(0.6D, -0.6D);
			Timer.delay(0.25D);
			this.siege.SiegeArmDown();
			Timer.delay(2.6D);
			this.siege.stopArm();
			this.m_cans.SetDrive(0.0D, 0.0D);
		}
	}

	public void SallyPortTeleop()
	{
		if (this.tele.getRawButton(6))
		{
			if (this.siege.potGet() > 527.0D) {
				this.siege.SiegeArmUp();
			}
			Timer.delay(0.75D);
			this.siege.stopArm();
			this.m_cans.SetDrive(-0.5D, 0.5D);
			Timer.delay(2.15D);
			this.m_cans.SetDrive(0.0D, 0.0D);
			this.m_cans.SetDrive(0.5D, 0.5D);
			Timer.delay(0.25D);
			this.m_cans.SetDrive(0.0D, 0.0D);
			Timer.delay(0.2D);
			this.m_cans.SetDrive(0.5D, -0.5D);
			Timer.delay(0.5D);
			this.m_cans.SetDrive(-0.5D, -0.5D);
			Timer.delay(0.22D);
			this.m_cans.SetDrive(0.0D, 0.0D);
			this.m_cans.SetDrive(0.6D, -0.6D);
			Timer.delay(4.0D);
			this.m_cans.SetDrive(0.0D, 0.0D);
		}
	}

	public void PortcullisTeleop()
	{
		if (this.tele.getRawButton(4))
		{
			if (this.siege.potGet() > 100.0D)
			{
				this.m_cans.SetDrive(0.4D, -0.4D);
				this.siege.SiegeArmUp();
			}
			Timer.delay(3.0D);
			if (this.siege.potGet() < 700.0D)
			{
				this.intake.portcullis();
				Timer.delay(0.25D);
				this.siege.SiegeArmDown();
			}
			Timer.delay(1.5D);
			this.m_cans.SetDrive(0.5D, -0.5D);
		}
	}

	public void TeleTest()
	{
		if (this.tele.getRawButton(12))
		{
			this.m_cans.SetDrive(0.4D, -0.4D);
			Timer.delay(1.0D);
			this.m_cans.SetDrive(0.0D, 0.0D);
			Timer.delay(0.5D);
			this.m_cans.SetDrive(-0.4D, 0.4D);
			Timer.delay(1.0D);
			this.m_cans.SetDrive(0.0D, 0.0D);
		}
	}

	public boolean override()
	{
		boolean bool = false;
		if (this.tele2.getRawButton(11)) {
			bool = true;
		}
		return bool;
	}
}
