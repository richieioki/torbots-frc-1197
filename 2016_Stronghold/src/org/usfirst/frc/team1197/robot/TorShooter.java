package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;

public class TorShooter {
	TorIntake intake;
	CANTalon shooter, elevate;
	Joystick stick;
	AHRS gyro;
	public TorShooter(TorIntake intake, CANTalon shooter, Joystick stick, AHRS gyro){
		this.intake = intake;
		this.shooter = shooter;
		this.stick = stick;
		this.gyro = gyro;
	}
	public void shoot(){
		if(stick.getRawButton(9)){
		elevate.set(0.5);
		shooter.set(1.0);
		}
		elevate.set(0.0);
		shooter.set(0.3);
	}
}
