package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.PIDOutput;

public class TorCAN

implements PIDOutput
{
	public DRIVE_STATE m_state;
	CANTalon m_Ltalon3;
	CANTalon m_Ltalon2;
	CANTalon m_Ltalon1;
	CANTalon m_Rtalon3;
	CANTalon m_Rtalon2;
	CANTalon m_Rtalon1;
	float lowgear = 20.0F;
	float highgear = 15.0F;
	int numOfJags;

	public static enum DRIVE_STATE
	{
		HIGHGEAR,  LOWGEAR,  OFF,  PIVOTING;

		private DRIVE_STATE() {}
	}

	public TorCAN(CANTalon R1, CANTalon R2, CANTalon L1, CANTalon L2)
	{
		this.numOfJags = 4;
		this.m_Rtalon1 = R1;
		this.m_Rtalon2 = R2;

		this.m_Ltalon1 = L1;
		this.m_Ltalon2 = L2;
	}

	public TorCAN(CANTalon R1, CANTalon L1)
	{
		this.numOfJags = 2;
		this.m_Rtalon1 = R1;
		this.m_Ltalon1 = L1;
	}

	public TorCAN(CANTalon R1, CANTalon R2, CANTalon R3, CANTalon L1, CANTalon L2, CANTalon L3)
	{
		this.numOfJags = 6;
		this.m_state = DRIVE_STATE.LOWGEAR;

		this.m_Rtalon1 = R1;
		this.m_Rtalon2 = R2;
		this.m_Rtalon3 = R3;

		this.m_Ltalon1 = L1;
		this.m_Ltalon2 = L2;
		this.m_Ltalon3 = L3;
	}

	public void SetDrive(double leftSpeed, double rightSpeed)
	{
		SetLeft(leftSpeed);
		SetRight(rightSpeed);
	}

	public void SetLeft(double speed)
	{
		if (this.numOfJags == 2)
		{
			this.m_Ltalon1.set(-speed);
		}
		else if (this.numOfJags == 4)
		{
			this.m_Ltalon1.set(-speed);
			this.m_Ltalon2.set(-speed);
		}
		else
		{
			this.m_Ltalon1.set(-speed);
			this.m_Ltalon2.set(-speed);
			this.m_Ltalon3.set(-speed);
		}
	}

	public void SetRight(double speed)
	{
		if (this.numOfJags == 2)
		{
			this.m_Rtalon1.set(speed);
		}
		else if (this.numOfJags == 4)
		{
			this.m_Rtalon1.set(speed);
			this.m_Rtalon2.set(speed);
		}
		else
		{
			this.m_Rtalon1.set(speed);
			this.m_Rtalon2.set(speed);
			this.m_Rtalon3.set(speed);
		}
	}

	public void pidWrite(double output)
	{
		SetRight(output);
		SetLeft(-output);
	}

	public void highGear()
	{
		this.m_Rtalon1.setVoltageRampRate(this.highgear);
		this.m_Rtalon2.setVoltageRampRate(this.highgear);
//		this.m_Rtalon3.setVoltageRampRate(this.highgear);
		this.m_Ltalon1.setVoltageRampRate(this.highgear);
		this.m_Ltalon2.setVoltageRampRate(this.highgear);
//		this.m_Ltalon3.setVoltageRampRate(this.highgear);
	}

	public void lowGear()
	{
		this.m_Rtalon1.setVoltageRampRate(this.lowgear);
		this.m_Rtalon2.setVoltageRampRate(this.lowgear);
//		this.m_Rtalon3.setVoltageRampRate(this.lowgear);
		this.m_Ltalon1.setVoltageRampRate(this.lowgear);
		this.m_Ltalon2.setVoltageRampRate(this.lowgear);
//		this.m_Ltalon3.setVoltageRampRate(this.lowgear);
	}

	public void offGear()
	{
		this.m_Rtalon1.setVoltageRampRate(1200.0D);
		this.m_Rtalon2.setVoltageRampRate(1200.0D);
//		this.m_Rtalon3.setVoltageRampRate(1200.0D);
		this.m_Ltalon1.setVoltageRampRate(1200.0D);
		this.m_Ltalon2.setVoltageRampRate(1200.0D);
//		this.m_Ltalon3.setVoltageRampRate(1200.0D);
	}

	public void pivot()
	{
		this.m_Rtalon1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		this.m_Rtalon2.changeControlMode(CANTalon.TalonControlMode.Voltage);
//		this.m_Rtalon3.changeControlMode(CANTalon.TalonControlMode.Voltage);
		this.m_Ltalon1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		this.m_Ltalon2.changeControlMode(CANTalon.TalonControlMode.Voltage);
//		this.m_Ltalon3.changeControlMode(CANTalon.TalonControlMode.Voltage);
	}

	public void unpivot()
	{
		this.m_Rtalon1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		this.m_Rtalon2.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//		this.m_Rtalon3.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		this.m_Ltalon1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		this.m_Ltalon2.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//		this.m_Ltalon3.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
	}
}
