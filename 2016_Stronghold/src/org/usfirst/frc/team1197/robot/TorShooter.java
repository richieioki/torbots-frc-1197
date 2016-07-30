package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public class TorShooter
{
	TorCAN cans;
	TorIntake intake;
	CANTalon shooter1;
	CANTalon shooter2;
	CANTalon hood;
	CANTalon elevate;
	CANTalon arm;
	Joystick stick2;
	AHRS gyro;
	public boolean shooterEnabled;
	private ShooterState m_state;
	float angleToTurn;
	double hoodTopLimit;
	double hoodBotLimit;
	double setHoodDegreesSlope;
	double readHoodDegreesSlope;
	double setHoodDegreesInter;
	double readHoodDegreesInter;
	double hoodDegreesBot;
	double hoodDegreesTop;
	private TorCamera camera;
	public boolean shootFlag;

	private static enum ShooterState
	{
		TURNING,  MANUAL;

		private ShooterState() {}
	}

	public TorShooter(TorIntake intake, CANTalon shooter1, CANTalon shooter2, 
			CANTalon hood, CANTalon elevate, CANTalon arm, 
			Joystick stick2, AHRS gyro, TorCAN can, TorCamera camera)
	{
		this.intake = intake;
		this.shooter1 = shooter1;
		this.shooter2 = shooter2;
		this.hood = hood;
		this.elevate = elevate;
		this.arm = arm;
		this.stick2 = stick2;
		this.gyro = gyro;
		cans = can;
		this.camera = camera;

		shootFlag = false;

		hoodCalc();

		shooterEnabled = false;
		m_state = ShooterState.MANUAL;
	}

	public void elevateShoot()
	{
		shooter1.set(0.75D);
		shooter2.set(0.75D);
		Timer.delay(1.0D);
		elevate.set(-0.95D);
		Timer.delay(1.0D);
		shooter1.set(0.0D);
		shooter2.set(0.0D);
		elevate.set(0.0D);
		shooterEnabled = false;
	}

	public void hoodCalc()
	{
		hoodTopLimit = -1.8099D;
		hoodBotLimit = -2.6365D;
		hoodDegreesTop = 59.0D;
		hoodDegreesBot = 17.0D;

		setHoodDegreesSlope = ((hoodBotLimit - hoodTopLimit) / (hoodDegreesBot - hoodDegreesTop));
		setHoodDegreesInter = (hoodTopLimit - setHoodDegreesSlope * hoodDegreesTop);
		readHoodDegreesSlope = (1.0D / setHoodDegreesSlope);
		readHoodDegreesInter = (-setHoodDegreesInter / setHoodDegreesSlope);
	}

	public void hoodSetDegrees(double degrees)
	{
		hood.set(setHoodDegreesSlope * degrees + setHoodDegreesInter);
	}

	public double hoodGetDegrees()
	{
		return readHoodDegreesSlope * hood.get() + readHoodDegreesInter;
	}


	public void setEnbled(boolean input)
	{
		shooterEnabled = input;
	}

	public void shooting() {}

	public void shooter()
	{
		if ((stick2.getRawButton(2))) // && (intake.shooterBreakBeam())
		{
			shooting();
			double value = camera.GetValue();
			cans.offGear();
			cans.m_state = TorCAN.DRIVE_STATE.PIVOTING;
			gyro.reset();
			camera.AutoShoot(value);
			Timer.delay(0.2);
			cans.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
			cans.lowGear();
		}
		else
		{
			shooter1.set(6.5); //10
			shooter2.set(6.5); //10
		}
	}
}
