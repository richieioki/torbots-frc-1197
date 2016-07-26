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
		numOfJags = 4;
		m_Rtalon1 = R1;
		m_Rtalon2 = R2;

		m_Ltalon1 = L1;
		m_Ltalon2 = L2;
	}

	public TorCAN(CANTalon R1, CANTalon L1)
	{
		numOfJags = 2;
		m_Rtalon1 = R1;
		m_Ltalon1 = L1;
	}

	public TorCAN(CANTalon R1, CANTalon R2, CANTalon R3, CANTalon L1, CANTalon L2, CANTalon L3)
	{
		numOfJags = 6;
		m_state = DRIVE_STATE.LOWGEAR;

		m_Rtalon1 = R1;
		m_Rtalon2 = R2;
		m_Rtalon3 = R3;

		m_Ltalon1 = L1;
		m_Ltalon2 = L2;
		m_Ltalon3 = L3;
	}

	public void SetDrive(double leftSpeed, double rightSpeed)
	{
		SetLeft(leftSpeed);
		SetRight(rightSpeed);
	}

	public void SetLeft(double speed)
	{
		if (numOfJags == 2)
		{
			m_Ltalon1.set(-speed);
		}
		else if (numOfJags == 4)
		{
			m_Ltalon1.set(-speed);
			m_Ltalon2.set(-speed);
		}
		else
		{
			m_Ltalon1.set(-speed);
			m_Ltalon2.set(-speed);
			m_Ltalon3.set(-speed);
		}
	}

	public void SetRight(double speed)
	{
		if (numOfJags == 2)
		{
			m_Rtalon1.set(speed);
		}
		else if (numOfJags == 4)
		{
			m_Rtalon1.set(speed);
			m_Rtalon2.set(speed);
		}
		else
		{
			m_Rtalon1.set(speed);
			m_Rtalon2.set(speed);
			m_Rtalon3.set(speed);
		}
	}

	public void pidWrite(double output)
	{
		SetRight(output);
		SetLeft(-output);
	}

	public void highGear()
	{
		m_Rtalon1.setVoltageRampRate(highgear);
		m_Rtalon2.setVoltageRampRate(highgear);
//		m_Rtalon3.setVoltageRampRate(highgear);
		m_Ltalon1.setVoltageRampRate(highgear);
		m_Ltalon2.setVoltageRampRate(highgear);
//		m_Ltalon3.setVoltageRampRate(highgear);
	}

	public void lowGear()
	{
		m_Rtalon1.setVoltageRampRate(lowgear);
		m_Rtalon2.setVoltageRampRate(lowgear);
//		m_Rtalon3.setVoltageRampRate(lowgear);
		m_Ltalon1.setVoltageRampRate(lowgear);
		m_Ltalon2.setVoltageRampRate(lowgear);
//		m_Ltalon3.setVoltageRampRate(lowgear);
	}

	public void offGear()
	{
		m_Rtalon1.setVoltageRampRate(1200.0D);
		m_Rtalon2.setVoltageRampRate(1200.0D);
//		m_Rtalon3.setVoltageRampRate(1200.0D);
		m_Ltalon1.setVoltageRampRate(1200.0D);
		m_Ltalon2.setVoltageRampRate(1200.0D);
//		m_Ltalon3.setVoltageRampRate(1200.0D);
	}

	public void pivot()
	{
		m_Rtalon1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		m_Rtalon2.changeControlMode(CANTalon.TalonControlMode.Voltage);
//		m_Rtalon3.changeControlMode(CANTalon.TalonControlMode.Voltage);
		m_Ltalon1.changeControlMode(CANTalon.TalonControlMode.Voltage);
		m_Ltalon2.changeControlMode(CANTalon.TalonControlMode.Voltage);
//		m_Ltalon3.changeControlMode(CANTalon.TalonControlMode.Voltage);
	}

	public void unpivot()
	{
		m_Rtalon1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		m_Rtalon2.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//		m_Rtalon3.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		m_Ltalon1.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		m_Ltalon2.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//		m_Ltalon3.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
	}
}
