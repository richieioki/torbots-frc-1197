package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TorTeleop {
	
	private Solenoid shift;
	private Encoder m_encoder;
	private Joystick tele;
	private TorDrive m_drive;
	private TorCAN m_cans;
	public static final double GEAR_RATIO = 56.0f; //ticks per inch
	public double encoderDistance = 0;
	public static final double TARGET_DISTANCE = 45; //distance we need to travel
	public CANTalon R1, R2, R3, L1, L2, L3, T1;
	public int defense = 0;
	public int lane = 0;
	private AHRS gyro;
	private TorSiege siege;
	private Ultrasonic sonar;
	private Joystick tele2;
	private double potValue;
	private TorIntake intake;

	public TorTeleop(Joystick stick1, Joystick stick2, TorCAN m_cans, TorSiege siege){
		tele = stick1;
		tele2 = stick2;
		this.m_cans = m_cans;
		this.siege = siege;
	}
	
	public void DrawBridgeTeleop() {
		if (tele.getRawButton(5)) {
			if (siege.potGet() > 673) {
				siege.SiegeArmUp();
				m_cans.SetDrive(0.0, 0.0);
			}
			Timer.delay(0.5);
			m_cans.SetDrive(-0.5, 0.5);
			Timer.delay(1.7);
			if (siege.potGet() > 183) {
				siege.SiegeArmUp();
				m_cans.SetDrive(0.0, 0.0);
			}
			Timer.delay(0.5);
			m_cans.SetDrive(0.5, -0.5);
		}
	}
	public void ChevelTeleop(){
		if(tele.getRawButton(3)){
			if(siege.potGet() > 226){  	
				siege.SiegeArmUp();
				m_cans.SetDrive(0.0, 0.0);
		}
		Timer.delay(1.5);
		siege.stopArm();
		m_cans.SetDrive(-0.4, 0.4);
		Timer.delay(0.3);
		m_cans.SetDrive(0,0);

		m_cans.SetDrive(0.6, -0.6);
		Timer.delay(0.25);
		siege.SiegeArmDown();
		Timer.delay(2.6);
		siege.stopArm();
		m_cans.SetDrive(0, 0);
		}
	}
	public void SallyPortTeleop(){
		if(tele.getRawButton(6)){
			if(siege.potGet() > 527){
				siege.SiegeArmUp();
			}
			
			Timer.delay(0.75);
			siege.stopArm();
			m_cans.SetDrive(-0.5, 0.5);
			Timer.delay(2.15);
			m_cans.SetDrive(0,0);
			m_cans.SetDrive(0.5, 0.5);
			Timer.delay(0.25);
			m_cans.SetDrive(0,0);
			Timer.delay(0.2);
			m_cans.SetDrive(0.5, -0.5);
			Timer.delay(0.5);
			m_cans.SetDrive(-0.5,-0.5);
			Timer.delay(0.22);
			m_cans.SetDrive(0,0);
			m_cans.SetDrive(0.6,-0.6);
			Timer.delay(4);
			m_cans.SetDrive(0,0);
		}
	}
	public void PortcullisTeleop(){
		
		if(tele.getRawButton(4)){
			if(siege.potGet()>100){
				m_cans.SetDrive(0.4, -0.4);
				siege.SiegeArmUp();
			}
			Timer.delay(3);
			if(siege.potGet() < 700){
				intake.portcullis();
				Timer.delay(0.25);
				siege.SiegeArmDown();
			}
			Timer.delay(1.5);
			m_cans.SetDrive(0.5, -0.5);
			
		}
	}
	public void TeleTest(){
		if(tele.getRawButton(12)){
			m_cans.SetDrive(0.4, -0.4);
			Timer.delay(1);
			m_cans.SetDrive(0, 0);
			Timer.delay(0.5);
			m_cans.SetDrive(-0.4,0.4);
			Timer.delay(1);
			m_cans.SetDrive(0,0);
		}
	}
	public boolean override(){
		boolean bool=false;
		if(tele2.getRawButton(11)){
			bool = true;
		}
			return bool;
	}
	
}
