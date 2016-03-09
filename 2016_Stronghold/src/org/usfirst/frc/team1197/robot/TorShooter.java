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
	Joystick stick3;
	Joystick stick2;
	AHRS gyro;
	public boolean shooterEnabled;
	private ShooterState m_state;
	float angleToTurn;
	private double closeHood = 1690;
	private double midHood = 1150;
	private double farHood = 440;
	private double leftHood = 702;
	//1690 close, 440 far, 1150/1168 mid, 702 left
	
	double hoodTopLimit;
	double hoodBotLimit;
	double setHoodDegreesSlope;
	double readHoodDegreesSlope;
	double setHoodDegreesInter;
	double readHoodDegreesInter;
	double hoodDegreesBot;
	double hoodDegreesTop;

	private static enum ShooterState
	{
		TURNING,  MANUAL;

		private ShooterState() {}
	}

	public TorShooter(TorIntake intake, CANTalon shooter1, CANTalon shooter2, 
			CANTalon hood, CANTalon elevate, CANTalon arm, Joystick stick3, 
			Joystick stick2, AHRS gyro, TorCAN can)
	{
		this.intake = intake;
		this.shooter1 = shooter1;
		this.shooter2 = shooter2;
		this.hood = hood;
		this.elevate = elevate;
		this.arm = arm;
		this.stick3 = stick3;
		this.stick2 = stick2;
		this.gyro = gyro;
		this.cans = can;
		
		hoodCalc();
		
		this.shooterEnabled = false;
		this.m_state = ShooterState.MANUAL;
	}
	public void elevateShoot(){
		shooter1.set(0.75);
		shooter2.set(0.75);
		Timer.delay(1.0);
		elevate.set(-0.95);
		Timer.delay(1.0);
		shooter1.set(0);
		shooter2.set(0);
		elevate.set(0);
		shooterEnabled = false;
	}

	public void shoot()
	{
		if (this.stick3.getRawButton(1))
		{
			this.shooter1.set(0.75D);
			this.shooter2.set(0.75D);
		}
		else
		{
			this.shooter1.set(0.0D);
			this.shooter2.set(0.0D);
		}
	}
	public void hoodCalc(){
		hoodTopLimit = -1.8099;
		hoodBotLimit = -2.6365;
		hoodDegreesTop = 59.0;
		hoodDegreesBot = 17;
		
		this.setHoodDegreesSlope = ((hoodBotLimit - hoodTopLimit) / (this.hoodDegreesBot - this.hoodDegreesTop));
		this.setHoodDegreesInter = (hoodTopLimit - this.setHoodDegreesSlope * this.hoodDegreesTop);
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
	public void hoodSet(){
		if(stick3.getRawButton(3)){
			hoodSetDegrees(59);
		}
		if(stick3.getRawButton(4)){
			hoodSetDegrees(30);
		}
	}
	public void adjustShooter()
	{
		if (this.stick3.getRawButton(1))
		{
			shoot();
		}
//		else if (this.stick3.getRawButton(2))
//		{
//			this.m_state = ShooterState.TURNING;
//			this.gyro.reset();
//			this.angleToTurn = 25.0F;
//		}
//		else if (this.stick3.getRawButton(3))
//		{
//			this.m_state = ShooterState.TURNING;
//			this.gyro.reset();
//			this.angleToTurn = 15.0F;
//		}
//		else if (this.stick3.getRawButton(4))
//		{
//			this.m_state = ShooterState.TURNING;
//			this.gyro.reset();
//			this.angleToTurn = 5.0F;
//		}
		else if (this.stick3.getY() > 0.05D)
		{
			this.hood.set(-this.stick3.getY() * 50000.0D + this.hood.getPulseWidthPosition());
		}
//		else if (this.stick3.getRawButton(5))
//		{
//			this.m_state = ShooterState.TURNING;
//			this.gyro.reset();
//			this.angleToTurn = 335.0F;
//		}
//		else if (this.stick3.getRawButton(6))
//		{
//			this.m_state = ShooterState.TURNING;
//			this.gyro.reset();
//			this.angleToTurn = 345.0F;
//		}
//		else if (this.stick3.getRawButton(7))
//		{
//			this.m_state = ShooterState.TURNING;
//			this.gyro.reset();
//			this.angleToTurn = 355.0F;
//		}
		//    else if(stick3.getRawButton(8)){
			//    	hoodSet(closeHood);
			//    	Timer.delay(0.5);
			//    }
		//    else if(stick3.getRawButton(9)){
		//    	hoodSet(midHood);
		//    }
		//    else if(stick3.getRawButton(10)){
		//    	hoodSet(farHood);
		//    }
		else
		{
			this.hood.set(this.hood.getPulseWidthPosition());
		}
	}

	public void update()
	{
		if(this.stick3.getRawButton(1)) {
			this.elevateShoot();
		} 
		
		if(!this.stick3.getRawButton(2)) {
			shooterEnabled = false;
		}
	}

	private void shooterReset()
	{
		this.m_state = ShooterState.MANUAL;
		this.shooter1.set(0.0D);
		this.shooter2.set(0.0D);
	}

	public void setEnbled(boolean input) {
		shooterEnabled = input;
	}
}
