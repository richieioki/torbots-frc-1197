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
	static double CENTER = 67.0D; //54.0  //63.0 4/30 987 practice goal am
	NetworkTable m_networkTable;
	AHRS ahrs;
	private double targetArea = 0.0D;
	private double targetX = 0.0D;
	private double deltaX = 0.0D;
	private double angleDeltaX = 0.0D;
	private double targetAngle = 0.0D;
	private double ahrsAngle = 0.0D;
//	private Joystick stick3;
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

	public TorCamera(NetworkTable tb, AHRS ahrs, TorCAN torcan, TorSiege siege, TorIntake intake, Joystick stick2)
	{
		this.stick2 = stick2;
		this.siege = siege;
		this.torcan = torcan;
		this.ahrs = ahrs;
		m_networkTable = NetworkTable.getTable("GRIP/myContoursReport");
//		stick3 = stick3;
		this.intake = intake;

		counter = 0;
	}

	public void setPIDSourceType(PIDSourceType pidSource) {}

	public PIDSourceType getPIDSourceType()
	{
		return null;
	}

	public double GetValue()
	{
		double[] defaultValue = new double[0];
		double[] centerx = m_networkTable.getNumberArray("centerX", defaultValue);

		double[] width = m_networkTable.getNumberArray("width", defaultValue);

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
				deltaX = (value - CENTER);
				if(Math.abs(deltaX) > 28){
					angleDeltaX = (deltaX * 0.368D);
					return angleDeltaX;
				}
				angleDeltaX = (deltaX * 0.368D);
				return angleDeltaX;
			}
			if (value > CENTER)
			{
				deltaX = (value - CENTER);
				if(Math.abs(deltaX) > 50){
					angleDeltaX = (deltaX * 0.342D);
					return angleDeltaX;
				}
				angleDeltaX = (deltaX * 0.342D);
				return angleDeltaX;
			}
		}
		return Integer.MAX_VALUE;
	}

	public double angleMapper()
	{
		ahrsAngle = ahrs.getAngle();
		if (ahrsAngle > 180.0D) {
			ahrsAngle = (360.0D - ahrsAngle);
		}
		return ahrsAngle;
	}

	public void AutoShoot(double deltaX)
	{
		double TargetAngle = deltaX;
		if (deltaX != Integer.MAX_VALUE)
		{
//			System.out.println("CURRENT ANGLE " + ahrs.getAngle());

			double timeout = System.currentTimeMillis() + 5000L;
			torcan.pivot();
			while (!siege.turnToShoot(TargetAngle)) { //check - 4/29 hotel pm
				if (timeout < System.currentTimeMillis() || !stick2.getRawButton(2)) {
					break;
				}
			}
			torcan.unpivot();
			torcan.SetDrive(0.0D, 0.0D);

			counter += 1;
			while (stick2.getRawButton(2)) {
				intake.fire();
			}
		}
	}

	public void AutonomousShoot(double deltaX)
	{
		double TargetAngle = deltaX;
		if (deltaX != Integer.MAX_VALUE)
		{
//			System.out.println("CURRENT ANGLE " + ahrs.getAngle());
			boolean breakout = false;
			double timeout = System.currentTimeMillis() + 5000L;
			torcan.pivot();
			while (!siege.turnToShoot(TargetAngle)) {
				if (timeout < System.currentTimeMillis()) {
					breakout = true;
				}
			}
			torcan.unpivot();
			if (!breakout)
			{
				torcan.SetDrive(0.0D, 0.0D);
				intake.fire();
//				System.out.println("AUTO FIRE");
				Timer.delay(0.6D);
				intake.stopElevator();
			}
		}
	}

	public double getCenterX()
	{
		double[] defaultValue = new double[0];
		double[] centerx = m_networkTable.getNumberArray("centerX", defaultValue);
		double[] width = m_networkTable.getNumberArray("width", defaultValue);
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
