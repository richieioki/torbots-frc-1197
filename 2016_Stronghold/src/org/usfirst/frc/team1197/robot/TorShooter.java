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
<<<<<<< HEAD
	private double closeHood = 1690.0D;
	private double midHood = 1150.0D;
	private double farHood = 440.0D;
	private double leftHood = 702.0D;
=======
	private double closeHood = 1690;
	private double midHood = 1150;
	private double farHood = 440;
	private double leftHood = 702;
	//1690 close, 440 far, 1150/1168 mid, 702 left
	
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
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

<<<<<<< HEAD
	public TorShooter(TorIntake intake, CANTalon shooter1, CANTalon shooter2, CANTalon hood, CANTalon elevate, CANTalon arm, Joystick stick3, Joystick stick2, AHRS gyro, TorCAN can, TorCamera camera)
=======
	public TorShooter(TorIntake intake, CANTalon shooter1, CANTalon shooter2, 
			CANTalon hood, CANTalon elevate, CANTalon arm, Joystick stick3, 
			Joystick stick2, AHRS gyro, TorCAN can, TorCamera camera)
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
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
		this.camera = camera;
<<<<<<< HEAD

		this.shootFlag = false;

		hoodCalc();

		this.shooterEnabled = false;
		this.m_state = ShooterState.MANUAL;
=======
		
		shootFlag = false;
		
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
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
	}

<<<<<<< HEAD
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

=======
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
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
<<<<<<< HEAD
=======
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
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
	}

<<<<<<< HEAD
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

	public void hoodSet()
	{
		if (this.stick3.getRawButton(5)) {
			hoodSetDegrees(59.0D);
		}
		if (this.stick3.getRawButton(6)) {
			hoodSetDegrees(30.0D);
		}
	}

	public void adjustShooter()
	{
		if (this.stick3.getRawButton(1)) {
=======
	public double hoodGetDegrees()
	{
		return this.readHoodDegreesSlope * this.hood.get() + this.readHoodDegreesInter;
	}
	
	public void hoodSet(){
		if(stick3.getRawButton(5)){
			hoodSetDegrees(59);
		}
		if(stick3.getRawButton(6)){
			hoodSetDegrees(30);
		}
	}
	public void adjustShooter()
	{
		if (this.stick3.getRawButton(1))
		{
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
			shoot();
<<<<<<< HEAD
		} else if (this.stick3.getY() > 0.05D) {
			this.hood.set(-this.stick3.getY() * 50000.0D + this.hood.getPulseWidthPosition());
		} else {
			this.hood.set(this.hood.getPulseWidthPosition());
=======
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
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
		}
	}

<<<<<<< HEAD
	public void update()
	{
		if (this.stick3.getRawButton(1)) {
			elevateShoot();
		}
		if (!this.stick3.getRawButton(2)) {
			this.shooterEnabled = false;
		}
=======
	private void shooterReset()
	{
		this.m_state = ShooterState.MANUAL;
		this.shooter1.set(0.0D);
		this.shooter2.set(0.0D);
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
	}

<<<<<<< HEAD
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
		if ((this.stick3.getRawButton(2)) && (this.intake.shooterBreakBeam()) && (this.intake.shooterMotor()))
		{
			shooting();
			double value = this.camera.GetValue();
			this.cans.offGear();
			this.cans.m_state = TorCAN.DRIVE_STATE.PIVOTING;
			this.gyro.reset();
			this.camera.AutoShoot(value);
			this.cans.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
			this.cans.lowGear();
		}
		else
		{
			this.shooter1.set(0.8D);
			this.shooter2.set(0.8D);
=======
	public void setEnbled(boolean input) {
		shooterEnabled = input;
	}
	
	public void shooting(){
		shooter1.set(0.8);
		shooter2.set(0.8);
	}
	
	public void shooter(){
		if(stick3.getRawButton(2) && !shootFlag){
			shooting();
//			double value = camera.GetValue();
			camera.AutoShoot(10);
			shootFlag = true;
		} else if(!stick3.getRawButton(2)) {
			intake.stopElevator();
			shooter1.set(0.3);
			shooter2.set(0.3);
//			intake.stopElevator();
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
		}
	}
}
