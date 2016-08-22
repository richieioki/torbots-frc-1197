package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;


import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
		auto_input = cypress;
		auto_input = new Joystick(2);
		this.stick2 = stick2;
		m_encoder = encoder;
		gyro = ahrs;
		m_cans = cans;
		this.siege = siege;
		this.intake = intake;
		this.shoot = shoot;
		this.robot = robot;
		this.camera = camera;
	}

	public int throttle()
	{
		double val = (stick.getThrottle() + 1.0D) * 4.0D;
		return (int)val;
	}

	public int[] initialize()
	{
		int[] laneDefense = new int[2];
		if ((!auto_input.getRawButton(1)) && (auto_input.getRawButton(2))) {
			lane = 1;
		} else if ((!auto_input.getRawButton(1)) && (!auto_input.getRawButton(2))) {
			lane = 3;
		} else if (!auto_input.getRawButton(2)) {
			lane = 2;
		} else if (!auto_input.getRawButton(3)) {
			lane = 4;
		} else {
			DriverStation.reportError("You have selected the wrong lane!", false);
		}
		if ((!auto_input.getRawButton(5)) && (auto_input.getRawButton(6)) && 
				(auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 1;
		} else if ((auto_input.getRawButton(5)) && (!auto_input.getRawButton(6)) && 
				(auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 2;
		} else if ((!auto_input.getRawButton(5)) && (!auto_input.getRawButton(6)) && 
				(auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 3;
		} else if ((auto_input.getRawButton(5)) && (auto_input.getRawButton(6)) && 
				(!auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 4;
		} else if ((!auto_input.getRawButton(5)) && (auto_input.getRawButton(6)) && 
				(!auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 5;
		} else if ((auto_input.getRawButton(5)) && (!auto_input.getRawButton(6)) && 
				(!auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 6;
		} else if ((!auto_input.getRawButton(5)) && (!auto_input.getRawButton(6)) && 
				(!auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 7;
		} else if ((auto_input.getRawButton(5)) && (auto_input.getRawButton(6)) && 
				(auto_input.getRawButton(7)) && (!auto_input.getRawButton(8))) {
			defense = 8;
		} else if ((auto_input.getRawButton(5)) && (auto_input.getRawButton(6)) && 
				(auto_input.getRawButton(7)) && (auto_input.getRawButton(8))) {
			defense = 0;
		} else {
			DriverStation.reportError("Incorrect Defense", false);
		}
		SmartDashboard.putNumber("Lane selected", laneDefense[0]);
		SmartDashboard.putNumber("Defense selected", laneDefense[1]);
		laneDefense[0] = lane;
		laneDefense[1] = defense;
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
		intake.portcullis();
	}

	public void turnToTheta(double theta, double turnSpeed)
	{
		this.turnSpeed = turnSpeed;
		double currentAngle = gyro.getAngle();
		turnAngle = 0.0D;
		while (currentAngle != turnAngle) {
			m_cans.SetDrive(-turnSpeed, -turnSpeed);
		}
	}

	public void touchAuto()
	{
		m_encoder.reset();
		drive.driveDistance(57.0F, 0.5F, true);
		m_cans.SetDrive(0.0D, 0.0D);
	}

	public void Moat()
	{
		m_encoder.reset();
		siege.setDegrees(sallyPort);
//		siege.highGear();
		drive.driveDistance(15.0F, 0.5F, true);
		drive.driveDistance(45.0F, 1.0F, true);
		drive.driveDistance(90.0F, 0.5F, true);
		m_cans.SetDrive(0.0D, 0.0D);
		siege.setDegrees(0.0D);
	}

	public void RoughTerrain()
	{
		m_encoder.reset();
		siege.setDegrees(sallyPort);
		drive.driveDistance(130.0F, 0.5F, true);
		m_cans.SetDrive(0.0D, 0.0D);
	}

	public void RockWall()
	{
		m_encoder.reset();
		siege.setDegrees(sallyPort);
//		siege.highGear();
		drive.driveDistance(15.0F, 0.5F, true);
		drive.driveDistance(45.0F, 1.0F, true);
		drive.driveDistance(90.0F, 0.5F, true);
		m_cans.SetDrive(0.0D, 0.0D);
		siege.setDegrees(0.0D);
	}

	public void Ramparts()
	{
		m_encoder.reset();
		siege.setDegrees(sallyPort);
		drive.driveDistance(140.0F, 1.0F, true);
		m_cans.SetDrive(0.0D, 0.0D);
		siege.setDegrees(0.0D);
	}

	public void DrawBridge()
	{
		m_encoder.reset();
		drive.driveDistance(62.0F, 0.5F, true); //65
		m_cans.SetDrive(0.0D, 0.0D);
		siege.DrawBridge();
		while ((robot.isEnabled()) && (robot.isAutonomous())) {
			siege.SiegeArmUpdate();
			if(siege.m_states == TorSiege.DRAWBRIDGE.IDLE) {
				break;
			}
		}
	}

	public void ChevelDeFrise()
	{
		
		m_encoder.reset();
		drive.driveDistance(60.0F, 0.5F, true);
		m_cans.SetDrive(0.0D, 0.0D);
		siege.Cheve();
		while ((robot.isEnabled()) && (robot.isAutonomous())) {
			siege.SiegeArmUpdate();
			if(siege.m_chev == TorSiege.CHEVEL.IDLE) {
				break;
			}
		}
	}

	public void Sallyport()
	{
		
		m_encoder.reset();
		drive.driveDistance(60.0F, 0.5F, true);
		m_cans.SetDrive(0.0D, 0.0D);
		siege.SallyPort();
		while ((robot.isEnabled()) && (robot.isAutonomous())) {
			siege.SiegeArmUpdate();
			if(siege.m_sally == TorSiege.SALLYPORT.IDLE) {
				break;
			}
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
		double breakTime = System.currentTimeMillis()+1000;
		siege.setDegrees(siege.intakeVal1);
		while(siege.siegeOnTarget(2)){
			if(System.currentTimeMillis()<breakTime)
				break;
	}
		Timer.delay(1.0D);
		double value = camera.GetValue();
		gyro.reset();
		camera.AutonomousShoot(value);
	}

	public void AutoDriving()
	{
		siege.setDegrees(-50.0D);
		while (m_encoder.getDistance() < 120.0D) {
			m_cans.SetDrive(0.6D, -0.6D);
		}
		m_cans.SetDrive(0.0D, 0.0D);
	}

	public void turnLane1()
	{
		double timeout = System.currentTimeMillis() + 1000L;
		gyro.reset();
		while (!siege.turnToShoot(-15.0D)) {
			if (timeout < System.currentTimeMillis()) {
				break;
			}
		}
		m_cans.SetDrive(0.0D, 0.0D);
	}

	public void turnLane3()
	{
		double timeout = System.currentTimeMillis() + 1000L;
		gyro.reset();
		while (!siege.turnToShoot(10.0D)) {
			if (timeout < System.currentTimeMillis()) {
				break;
			}
		}
		m_cans.SetDrive(0.0D, 0.0D);
	}

	public void turnLane4()
	{
		double timeout = System.currentTimeMillis() + 1000L;
		gyro.reset();
		while (!siege.turnToShoot(30.0D)) {
			if (timeout < System.currentTimeMillis()) {
				break;
			}
		}
		m_cans.SetDrive(0.0D, 0.0D);
	}

	public void Portcullis()
	{
		m_encoder.reset();
		while (m_encoder.getDistance() < 44.0D)
		{
			m_cans.SetDrive(0.7D, -0.7D);
			siege.setDegrees(siege.portcullisBot + 14.0D);
		}
		while ((m_encoder.getDistance() >= 44.0D) && 
				(m_encoder.getDistance() < 60.0D))
		{
			m_cans.SetDrive(0.3D, -0.3D);
			siege.setDegrees(siege.portcullisBot);
		}
		m_cans.SetDrive(0.0D, 0.0D);
		siege.Portcullis();
		while ((robot.isEnabled()) && (robot.isAutonomous())) {
			siege.SiegeArmUpdate();
			if(siege.m_port == TorSiege.PORTCULLIS.IDLE) {
				break;
			}
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



		//position 1
		if(laneAndDefense[0] == 1 && laneAndDefense[1] == 1){
			touchAuto();
		}		
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 2){
			ChevelDeFrise();
			turnLane1();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 3){
			
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 4){
			
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 5){
			
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 6){
			DrawBridge();
			turnLane1();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 7){
			Sallyport();
			Timer.delay(0.5);
			turnLane1();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 8){
			Portcullis();
			turnLane1();
			ShootAuto();
		}

		//position 2
		//lane 2 does not require turning
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 1){
			
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 2){
			ChevelDeFrise();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 3){
			
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 4){
			
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 5){
			
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 6){
			DrawBridge();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 7){
			Sallyport();
			Timer.delay(0.5);
			ShootAuto();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 8){
			Portcullis();
			ShootAuto();
		}

		//position 3
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 0){
			DrawBridge();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 1){
			
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 2){
			ChevelDeFrise();
			turnLane3();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 3){
			
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 4){
			
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 5){
			
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 6){
			DrawBridge();
			turnLane3();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 7){
			Sallyport();
			Timer.delay(0.5);
			turnLane3();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 8){
			Portcullis();
			turnLane3();
			ShootAuto();
		}

		//position 4
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 1){
			
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 2){
			ChevelDeFrise();
			turnLane4();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 3){
			
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 4){
			
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 5){
			
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 6){
			DrawBridge();
			turnLane4();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 7){
			Sallyport();
			Timer.delay(0.5);
			turnLane4();
			ShootAuto();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 8){
			Portcullis();
			turnLane4();
			ShootAuto();
		}

		else {
			return;
		}

	}
}
