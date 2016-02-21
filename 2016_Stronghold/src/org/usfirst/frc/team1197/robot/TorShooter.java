package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;

public class TorShooter {
	TorCAN cans;
	TorIntake intake;
	CANTalon shooter1, shooter2, elevate;
	Joystick stick3, stick2;
	AHRS gyro;
	public boolean shooterEnabled;
	private enum ShooterState{TURNING, MANUAL};
	private ShooterState m_state;
	float angleToTurn;


	public TorShooter(TorIntake intake, CANTalon shooter1, CANTalon shooter2, CANTalon elevate, Joystick stick3, Joystick stick2, AHRS gyro, TorCAN can){
		this.intake = intake;
		this.shooter1 = shooter1;
		this.shooter2 = shooter2;
		this.elevate = elevate;
		this.stick3 = stick3;
		this.stick2 = stick2;
		this.gyro = gyro;
		cans = can;
		shooterEnabled = false;
		m_state = ShooterState.MANUAL;
		
		//NEED TO DETERMINE THE CORRECT SPEED 
		shooter1.set(0.4);
		shooter2.set(0.4);
	}

	public void shoot(){
		if(stick2.getRawButton(1)){
			elevate.set(0.5);
			shooter1.set(1.0);
		}
		elevate.set(0.0);
		shooter1.set(0.3);
	}

	public void adjustShooter(){
		if(stick3.getRawButton(1)) {
			shoot();
		} 
		//TURN LEFT X DEGREES
		else if(stick3.getRawButton(2)) {
			m_state = ShooterState.TURNING;
			gyro.reset();
			angleToTurn = 25;
		} else if(stick3.getRawButton(3)) {
			m_state = ShooterState.TURNING;
			gyro.reset();
			angleToTurn = 15;
		} else if(stick3.getRawButton(4)) {
			m_state = ShooterState.TURNING;
			gyro.reset();
			angleToTurn = 5;
		}
		//TURN RIGHT X DEGREES
		else if(stick3.getRawButton(5)) {
			m_state = ShooterState.TURNING;
			gyro.reset();
			angleToTurn = 360-25;
		} else if(stick3.getRawButton(6)) {
			m_state = ShooterState.TURNING;
			gyro.reset();
			angleToTurn = 360-15;
		} else if(stick3.getRawButton(7)) {
			m_state = ShooterState.TURNING;
			gyro.reset();
			angleToTurn = 360-5;
		}
		else if(m_state == ShooterState.MANUAL) {
			if(stick3.getX()>.2){
				cans.SetDrive(0.5, 0.5);
			}
			if(stick3.getX()<-.2){
				cans.SetDrive(-0.5, -0.5);
			}
		} else if(m_state == ShooterState.TURNING) {
			
		}
	}

	/**
	 * Main Update Loop
	 */
	public void update() {
		if(stick2.getRawButton(1)) { //NEED TO DEFINE A BUTTON
			shooterEnabled = true; //GIVES NICO CONTROL
		} else {
			shooterEnabled = false;
			shooterReset();
		}

		if(shooterEnabled) {
			adjustShooter();
		}
	}

	private void shooterReset() {
		m_state = ShooterState.MANUAL;
		shooter1.set(0.4);
		shooter2.set(0.4);
	}
}