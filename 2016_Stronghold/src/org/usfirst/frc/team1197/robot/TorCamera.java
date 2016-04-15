package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.io.PrintStream;

public class TorCamera
{
	static double CENTER = 63.0D;
	NetworkTable m_networkTable;
	AHRS ahrs;
	private double targetArea = 0.0D;
	private double targetX = 0.0D;
	private double deltaX = 0.0D;
	private double angleDeltaX = 0.0D;
	private double targetAngle = 0.0D;
	private double ahrsAngle = 0.0D;
	private Joystick stick3;
	public boolean empty = true;
	private TorLidar lidar;
	public TorCAN torcan;
	private TorIntake intake;
	private TorSiege siege;
	int counter;
	Joystick stick2;

	public static enum CAMERA
	{
		IDLE,  TURN;

		private CAMERA() {}
	}

	public CAMERA m_camera = CAMERA.IDLE;

	public TorCamera(NetworkTable tb, AHRS ahrs, TorCAN torcan, TorSiege siege, Joystick stick3, TorIntake intake, Joystick stick2)
	{
		this.stick2 = stick2;
		this.siege = siege;
		this.torcan = torcan;
		this.ahrs = ahrs;
		this.m_networkTable = NetworkTable.getTable("GRIP/myContoursReport");
		this.stick3 = stick3;
		this.intake = intake;

		this.counter = 0;
	}

	public void setPIDSourceType(PIDSourceType pidSource) {}

	public PIDSourceType getPIDSourceType()
	{
		return null;
	}

	public double GetValue()
	{
		double[] defaultValue = new double[0];
		double[] centerx = this.m_networkTable.getNumberArray("centerX", defaultValue);

		double[] width = this.m_networkTable.getNumberArray("width", defaultValue);

		int keeper = 0;
		double widthTemp = 0.0D;
		if (centerx.length > 0) {
			for (int i = 0; i < width.length; i++) {
				if (widthTemp == 0.0D)
				{
					keeper = i;
					widthTemp = width[i];
				}
				else if (widthTemp < width[i])
				{
					widthTemp = width[i];
					keeper = i;
				}
			}
		}
		if (centerx.length > 0)
		{
			double idealwidth = 23.0D;
			double widthRatio = width[keeper] / idealwidth;
			double value = centerx[keeper];
			if (value < CENTER)
			{
				this.deltaX = (value - CENTER);
				if(Math.abs(deltaX) > 28){
					deltaX = deltaX - 4;
					angleDeltaX = (this.deltaX * 0.342D);
					return angleDeltaX;
				}
				deltaX = deltaX - 1;
				this.angleDeltaX = (this.deltaX * 0.342D);
				return this.angleDeltaX;
			}
			if (value > CENTER)
			{
				this.deltaX = (value - CENTER);
				if(Math.abs(deltaX) > 50){
					deltaX = deltaX + 5;
					angleDeltaX = (this.deltaX * 0.342D);
					return angleDeltaX;
				}
				this.angleDeltaX = (this.deltaX * 0.342D);
				return this.angleDeltaX;
			}
		}
		System.out.println("ARRAY IS EMPTY!");
		return Integer.MAX_VALUE;
	}

	public double angleMapper()
	{
		this.ahrsAngle = this.ahrs.getAngle();
		if (this.ahrsAngle > 180.0D) {
			this.ahrsAngle = (360.0D - this.ahrsAngle);
		}
		return this.ahrsAngle;
	}

	public void AutoShoot(double deltaX)
	{
		double TargetAngle = deltaX;
		if (deltaX != Integer.MAX_VALUE)
		{
			System.out.println("CURRENT ANGLE " + this.ahrs.getAngle());

			double timeout = System.currentTimeMillis() + 2500L;
			this.torcan.pivot();
			while (!this.siege.turnToShoot(TargetAngle)) {
				if ((timeout < System.currentTimeMillis()) && (!this.stick3.getRawButton(2))) {
					break;
				}
			}
			this.torcan.unpivot();
			this.torcan.SetDrive(0.0D, 0.0D);

			this.counter += 1;
			while (this.stick3.getRawButton(2) || stick2.getRawButton(7)) {
				this.intake.fire();
			}
		}
	}

	public void AutonomousShoot(double deltaX)
	{
		double TargetAngle = deltaX;
		if (deltaX != Integer.MAX_VALUE)
		{
			System.out.println("CURRENT ANGLE " + this.ahrs.getAngle());
			boolean breakout = false;
			double timeout = System.currentTimeMillis() + 2500L;
			this.torcan.pivot();
			while (!this.siege.turnToShoot(TargetAngle)) {
				if (timeout < System.currentTimeMillis()) {
					breakout = true;
				}
			}
			this.torcan.unpivot();
			if (!breakout)
			{
				this.torcan.SetDrive(0.0D, 0.0D);
				this.intake.fire();
				System.out.println("AUTO FIRE");
				Timer.delay(0.6D);
				this.intake.stopElevator();
			}
		}
	}

	public double getCenterX()
	{
		double[] defaultValue = new double[0];
		double[] centerx = this.m_networkTable.getNumberArray("centerX", defaultValue);
		double[] width = this.m_networkTable.getNumberArray("width", defaultValue);
		int keeper = 0;
		double widthTemp = 0.0D;
		
		if (centerx.length > 0) {
			for (int i = 0; i < width.length; i++) {
				if (widthTemp == 0.0D)
				{
					keeper = i;
					widthTemp = width[i];
				}
				else if (widthTemp < width[i])
				{
					widthTemp = width[i];
					keeper = i;
				}
			}
		}
		
		return centerx[keeper];
		
	}
	
	public double centerX(){
		return getCenterX();
	}
}
