package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;

public class TorShooter {
	TorCAN cans;
	TorIntake intake;
	CANTalon shooter, elevate;
	Joystick stick3, stick2;
	AHRS gyro;
	
	public TorShooter(TorIntake intake, CANTalon shooter, CANTalon elevate, Joystick stick3, Joystick stick2, AHRS gyro, TorCAN can){
		this.intake = intake;
		this.shooter = shooter;
		this.elevate = elevate;
		this.stick3 = stick3;
		this.stick2 = stick2;
		this.gyro = gyro;
		cans = can;
	}
	public void shoot(){
		if(stick2.getRawButton(1)){
		elevate.set(0.5);
		shooter.set(1.0);
		}
		elevate.set(0.0);
		shooter.set(0.3);
	}
	public void adjustShooter(){
		if(stick2.getRawButton(1)){
		if(stick3.getX()>.2){
			cans.SetDrive(0.5, 0.5);
		}
		if(stick3.getX()<-.2){
			cans.SetDrive(-0.5, -0.5);
		}
//		if(stick.getY()>.2){ //Comment out for use at a later date
//			shooter.set(0.5);
//		}
//		if(stick.getY()<.2){
//			shooter.set(-0.5);
//		}
		}
	}
}