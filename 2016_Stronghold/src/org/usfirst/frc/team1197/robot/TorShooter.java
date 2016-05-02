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
//	Joystick stick3;
	Joystick stick2;
	AHRS gyro;
	public boolean shooterEnabled;
	private ShooterState m_state;
	float angleToTurn;
	private double closeHood = 1690.0D;
	private double midHood = 1150.0D;
	private double farHood = 440.0D;
	private double leftHood = 702.0D;
	//1690 close, 440 far, 1150/1168 mid, 702 left
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
//		this.stick3 = stick3;
		this.stick2 = stick2;
		this.gyro = gyro;
		this.cans = can;
		this.camera = camera;

		this.shootFlag = false;

		hoodCalc();

		this.shooterEnabled = false;
		this.m_state = ShooterState.MANUAL;
	}

	public void elevateShoot()
	{
		this.shooter1.set(0.75D);
		this.shooter2.set(0.75D);
		Timer.delay(1.0D);
		this.elevate.set(-0.95D);
		Timer.delay(1.0D);
		this.shooter1.set(0.0D);
		this.shooter2.set(0.0D);
		this.elevate.set(0.0D);
		this.shooterEnabled = false;
	}

//	public void shoot()
//	{
//		if (this.stick3.getRawButton(1))
//		{
//			this.shooter1.set(0.75D);
//			this.shooter2.set(0.75D);
//		}
//		else
//		{
//			this.shooter1.set(0.0D);
//			this.shooter2.set(0.0D);
//		}
//	}

	public void hoodCalc()
	{
		this.hoodTopLimit = -1.8099D;
		this.hoodBotLimit = -2.6365D;
		this.hoodDegreesTop = 59.0D;
		this.hoodDegreesBot = 17.0D;

		this.setHoodDegreesSlope = ((this.hoodBotLimit - this.hoodTopLimit) / (this.hoodDegreesBot - this.hoodDegreesTop));
		this.setHoodDegreesInter = (this.hoodTopLimit - this.setHoodDegreesSlope * this.hoodDegreesTop);
		this.readHoodDegreesSlope = (1.0D / this.setHoodDegreesSlope);
		this.readHoodDegreesInter = (-this.setHoodDegreesInter / this.setHoodDegreesSlope);
	}

	public void hoodSetDegrees(double degrees)
	{
		this.hood.set(this.setHoodDegreesSlope * degrees + this.setHoodDegreesInter);
	}

	public double hoodGetDegrees()
	{
		return this.readHoodDegreesSlope * this.hood.get() + this.readHoodDegreesInter;
	}

//	public void hoodSet()
//	{
//		if (this.stick3.getRawButton(5)) {
//			hoodSetDegrees(59.0D);
//		}
//		if (this.stick3.getRawButton(6)) {
//			hoodSetDegrees(30.0D);
//		}
//	}

//	public void adjustShooter()
//	{
//		if (this.stick3.getRawButton(1)) {
//			shoot();
//		} else if (this.stick3.getY() > 0.05D) {
//			this.hood.set(-this.stick3.getY() * 50000.0D + this.hood.getPulseWidthPosition());
//		} else {
//			this.hood.set(this.hood.getPulseWidthPosition());
//		}
//	}

//	public void update()
//	{
//		if (this.stick3.getRawButton(1)) {
//			elevateShoot();
//		}
//		if (!this.stick3.getRawButton(2)) {
//			this.shooterEnabled = false;
//		}
//	}

	private void shooterReset()
	{
		this.m_state = ShooterState.MANUAL;
		this.shooter1.set(0.0D);
		this.shooter2.set(0.0D);
	}

	public void setEnbled(boolean input)
	{
		this.shooterEnabled = input;
	}

	public void shooting() {}

	public void shooter()
	{
		if ((stick2.getRawButton(2))) // && (this.intake.shooterBreakBeam())
		{
			shooting();
			double value = this.camera.GetValue();
			this.cans.offGear();
			this.cans.m_state = TorCAN.DRIVE_STATE.PIVOTING;
			this.gyro.reset();
			this.camera.AutoShoot(value);
			Timer.delay(0.2);
			this.cans.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
			this.cans.lowGear();
		}
		else
		{
			shooter1.set(10.0);
			shooter2.set(10.0);
		}
	}
}
