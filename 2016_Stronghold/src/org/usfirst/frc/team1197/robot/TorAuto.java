package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.PrintStream;

public class TorAuto
{
	private Solenoid shift;
	private Encoder m_encoder;
	private Joystick auto_input;
	private Joystick stick;
	private TorCAN m_cans;
	private TorShooter shoot;
	private TorDrive drive;
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
	public CANTalon hood;
	public int defense = 0;
	public int lane = 0;
	private AHRS gyro;
	private TorIntake intake;
	private Joystick stick2;
	double ratio = 0.6799999999999999D;
	double armTop = 0.0D;
	double drawbridgeTop = 0.0D;
	double drawbridgeBot = 0.0D;
	double sallyPort = 0.0D;
	double chevelTop = 0.0D;
	double portcullis = 0.0D;
	private TorSiege siege;
	double turnSpeed;
	double turnAngle;
	private TorCamera camera;
	private Robot robot;

	public TorAuto() {}

	public TorAuto(Joystick cypress, Joystick stick, Joystick stick2, AHRS ahrs, Encoder encoder, TorCAN cans, Solenoid shift, TorSiege siege, TorIntake intake, TorDrive drive, TorShooter shoot, Robot robot, TorCamera camera)
	{
		this.stick = stick;
		this.drive = drive;
		this.shift = shift;
		this.auto_input = cypress;
		this.auto_input = new Joystick(2);
		this.stick2 = stick2;
		this.m_encoder = encoder;
		this.gyro = ahrs;
		this.m_cans = cans;
		this.siege = siege;
		this.intake = intake;
		this.shoot = shoot;
		this.robot = robot;
		this.camera = camera;
	}

	public int throttle()
	{
		double val = (this.stick.getThrottle() + 1.0D) * 4.0D;
		return (int)val;
	}

	public int[] initialize()
	{
		int[] laneDefense = new int[2];
		if ((!this.auto_input.getRawButton(1)) && (this.auto_input.getRawButton(2))) {
			this.lane = 1;
		} else if ((!this.auto_input.getRawButton(1)) && (!this.auto_input.getRawButton(2))) {
			this.lane = 3;
		} else if (!this.auto_input.getRawButton(2)) {
			this.lane = 2;
		} else if (!this.auto_input.getRawButton(3)) {
			this.lane = 4;
		} else {
			DriverStation.reportError("You have selected the wrong lane!", false);
		}
		if ((!this.auto_input.getRawButton(5)) && (this.auto_input.getRawButton(6)) && 
				(this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 1;
		} else if ((this.auto_input.getRawButton(5)) && (!this.auto_input.getRawButton(6)) && 
				(this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 2;
		} else if ((!this.auto_input.getRawButton(5)) && (!this.auto_input.getRawButton(6)) && 
				(this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 3;
		} else if ((this.auto_input.getRawButton(5)) && (this.auto_input.getRawButton(6)) && 
				(!this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 4;
		} else if ((!this.auto_input.getRawButton(5)) && (this.auto_input.getRawButton(6)) && 
				(!this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 5;
		} else if ((this.auto_input.getRawButton(5)) && (!this.auto_input.getRawButton(6)) && 
				(!this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 6;
		} else if ((!this.auto_input.getRawButton(5)) && (!this.auto_input.getRawButton(6)) && 
				(!this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 7;
		} else if ((this.auto_input.getRawButton(5)) && (this.auto_input.getRawButton(6)) && 
				(this.auto_input.getRawButton(7)) && (!this.auto_input.getRawButton(8))) {
			this.defense = 8;
		} else if ((this.auto_input.getRawButton(5)) && (this.auto_input.getRawButton(6)) && 
				(this.auto_input.getRawButton(7)) && (this.auto_input.getRawButton(8))) {
			this.defense = 0;
		} else {
			DriverStation.reportError("Incorrect Defense", false);
		}
		SmartDashboard.putNumber("Lane selected", laneDefense[0]);
		SmartDashboard.putNumber("Defense selected", laneDefense[1]);
		laneDefense[0] = this.lane;
		laneDefense[1] = this.defense;
		return laneDefense;
	}

	public void autoThrottle()
	{
		if (throttle() != 0) {
			if (throttle() == 1) {
				DrawBridge();
			} else if (throttle() == 2) {
				Sallyport();
			} else if (throttle() == 3) {
				ChevelDeFrise();
			} else if (throttle() == 4) {
				Portcullis();
			} else if (throttle() == 5) {
				Ramparts();
			} else if (throttle() == 6) {
				RoughTerrain();
			} else if (throttle() == 7) {
				Moat();
			} else if (throttle() == 8) {
				RockWall();
			}
		}
	}

	public void telePortcullis()
	{
		this.intake.portcullis();
	}

	public void turnToTheta(double theta, double turnSpeed)
	{
		this.turnSpeed = turnSpeed;
		double currentAngle = this.gyro.getAngle();
		this.turnAngle = 0.0D;
		while (currentAngle != this.turnAngle) {
			this.m_cans.SetDrive(-this.turnSpeed, -this.turnSpeed);
		}
	}

	public void touchAuto()
	{
		this.m_encoder.reset();
		this.drive.driveDistance(57.0F, 0.5F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
	}

	public void Moat()
	{
		this.m_encoder.reset();
		this.siege.setDegrees(this.sallyPort);
		this.siege.highGear();
		this.drive.driveDistance(15.0F, 0.5F, true);
		this.drive.driveDistance(45.0F, 1.0F, true);
		this.drive.driveDistance(90.0F, 0.5F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
		this.siege.setDegrees(0.0D);
	}

	public void RoughTerrain()
	{
		this.m_encoder.reset();
		this.siege.setDegrees(this.sallyPort);
		this.drive.driveDistance(130.0F, 0.5F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
	}

	public void RockWall()
	{
		this.m_encoder.reset();
		this.siege.setDegrees(this.sallyPort);
		this.siege.highGear();
		this.drive.driveDistance(15.0F, 0.5F, true);
		this.drive.driveDistance(45.0F, 1.0F, true);
		this.drive.driveDistance(90.0F, 0.5F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
		this.siege.setDegrees(0.0D);
	}

	public void Ramparts()
	{
		this.m_encoder.reset();
		this.siege.setDegrees(this.sallyPort);
		this.drive.driveDistance(140.0F, 1.0F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
		this.siege.setDegrees(0.0D);
	}

	public void DrawBridge()
	{
		this.m_encoder.reset();
		this.drive.driveDistance(62.0F, 0.5F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
		this.siege.DrawBridge();
		while ((this.robot.isEnabled()) && (this.robot.isAutonomous())) {
			this.siege.SiegeArmUpdate();
		}
	}

	public void ChevelDeFrise()
	{
		this.m_encoder.reset();
		this.drive.driveDistance(60.0F, 0.5F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
		this.siege.Cheve();
		while ((this.robot.isEnabled()) && (this.robot.isAutonomous())) {
			this.siege.SiegeArmUpdate();
		}
	}

	public void Sallyport()
	{
		this.m_encoder.reset();
		this.drive.driveDistance(60.0F, 0.5F, true);
		this.m_cans.SetDrive(0.0D, 0.0D);
		this.siege.SallyPort();
		while ((this.robot.isEnabled()) && (this.robot.isAutonomous())) {
			this.siege.SiegeArmUpdate();
		}
	}

	public void testShoot3()
	{
		AutoDriving();
		turnLane3();
		ShootAuto();
	}

	public void testShoot2()
	{
		AutoDriving();
		ShootAuto();
	}

	public void testShoot4()
	{
		AutoDriving();
		turnLane4();
		ShootAuto();
	}

	public void testShoot1()
	{
		AutoDriving();
		turnLane1();
		ShootAuto();
	}

	public void ShootAuto()
	{
		System.out.println("AUTO SHOOTING");
		Timer.delay(1.0D);
		double value = this.camera.GetValue();
		this.gyro.reset();
		this.camera.AutonomousShoot(value);
	}

	public void AutoDriving()
	{
		this.siege.setDegrees(-50.0D);
		while (this.m_encoder.getDistance() < 120.0D) {
			this.m_cans.SetDrive(0.6D, -0.6D);
		}
		this.m_cans.SetDrive(0.0D, 0.0D);
	}

	public void turnLane1()
	{
		double timeout = System.currentTimeMillis() + 1000L;
		this.gyro.reset();
		System.out.println("GYRO: " + this.gyro.getAngle());
		this.m_cans.pivot();
		while (!this.siege.turnToShoot(-15.0D)) {
			if (timeout < System.currentTimeMillis()) {
				break;
			}
		}
		this.m_cans.unpivot();
		this.m_cans.SetDrive(0.0D, 0.0D);
	}

	public void turnLane3()
	{
		double timeout = System.currentTimeMillis() + 1000L;
		this.gyro.reset();
		System.out.println("GYRO: " + this.gyro.getAngle());
		this.m_cans.pivot();
		while (!this.siege.turnToShoot(10.0D)) {
			if (timeout < System.currentTimeMillis()) {
				break;
			}
		}
		this.m_cans.unpivot();
		this.m_cans.SetDrive(0.0D, 0.0D);
	}

	public void turnLane4()
	{
		double timeout = System.currentTimeMillis() + 1000L;
		this.gyro.reset();
		System.out.println("GYRO: " + this.gyro.getAngle());
		this.m_cans.pivot();
		while (!this.siege.turnToShoot(30.0D)) {
			if (timeout < System.currentTimeMillis()) {
				break;
			}
		}
		this.m_cans.unpivot();
		this.m_cans.SetDrive(0.0D, 0.0D);
	}

	public void Portcullis()
	{
		this.m_encoder.reset();
		while (this.m_encoder.getDistance() < 44.0D)
		{
			this.m_cans.SetDrive(0.7D, -0.7D);
			this.siege.setDegrees(this.siege.portcullisBot + 14.0D);
		}
		while ((this.m_encoder.getDistance() >= 44.0D) && 
				(this.m_encoder.getDistance() < 60.0D))
		{
			this.m_cans.SetDrive(0.3D, -0.3D);
			this.siege.setDegrees(this.siege.portcullisBot);
		}
		this.m_cans.SetDrive(0.0D, 0.0D);
		this.siege.Portcullis();
		while ((this.robot.isEnabled()) && (this.robot.isAutonomous())) {
			this.siege.SiegeArmUpdate();
		}
	}

	public void ModeChooser(){

		/* 1. rock wall
		 * 2. chevel de frise
		 * 3. rampart
		 * 4. moat
		 * 5. rough terrain
		 * 6. draw bridge
		 * 7. sally port
		 * 8. portcullis
		 */

		int laneAndDefense[] = initialize();

		System.out.println("lane: " + laneAndDefense[0]);
		System.out.println("defense: " + laneAndDefense[1]);

		//position 1
		if(laneAndDefense[0] == 1 && laneAndDefense[1] == 1){
			RockWall();
		}		
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 2){
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 3){
			Ramparts();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 4){
			Moat();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 5){
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 6){
			DrawBridge();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 7){
			Sallyport();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 8){
			Portcullis();
		}

		//position 2
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 1){
			System.out.println("Pos 2 Rock Wall");
			RockWall();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 2){
			System.out.println("Pos 2 Chevel De Frise");
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 3){
			System.out.println("Pos 2 Ramparts");
			Ramparts();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 4){
			System.out.println("Pos 2 Moat");
			Moat();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 5){
			System.out.println("Pos 2 Rough Terrain");
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 6){
			System.out.println("Pos 2 drawbridge");
			DrawBridge(); //adjust potentiometer
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 7){
			System.out.println("Pos 2 sallyport");
			Sallyport();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 8){
			System.out.println("Pos 2 portcullis");
			Portcullis();
		}

		//position 3
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 1){
			RockWall();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 2){
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 3){
			Ramparts();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 4){
			Moat();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 5){
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 6){
			DrawBridge();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 7){
			Sallyport();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 8){
			Portcullis();
		}

		//position 4
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 1){
			RockWall();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 2){
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 3){
			Ramparts();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 4){
			Moat();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 5){
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 6){
			DrawBridge();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 7){
			Sallyport();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 8){
			Portcullis();
		}

		else {
			return;
		}

	}
}
