package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;

public class TorShooter {
	TorCAN cans;
	TorIntake intake;
	CANTalon shooter1, shooter2, hood, elevate, arm;
	Joystick stick3, stick2;
	AHRS gyro;
	public boolean shooterEnabled;
	private enum ShooterState{TURNING, MANUAL};
	private ShooterState m_state;
	float angleToTurn;


	public TorShooter(TorIntake intake, CANTalon shooter1, CANTalon shooter2,
			CANTalon hood, CANTalon elevate, CANTalon arm, Joystick stick3,
			Joystick stick2, AHRS gyro, TorCAN can) {
		this.intake = intake;
		this.shooter1 = shooter1;
		this.shooter2 = shooter2;
		this.hood = hood;
		this.elevate = elevate;
		this.arm = arm;
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

	public void shoot() {
		if (stick3.getRawButton(1)) {
			// elevate.set(0.5);
			// arm.set(0.5);
			shooter1.set(.65);
			shooter2.set(.65);
		} else {
			// elevate.set(0.0);
			// arm.set(0.0);
			// shooter.set(0.2);
			shooter1.set(0.0);
			shooter2.set(0.0);
		}
	}

	public void hood() {
		if(stick3.getY() < -.05){
			hood.set((-stick3.getY() * 50000) + hood.getPulseWidthPosition()); //siege arm down
		}
		else if(stick3.getY() > .05){
			hood.set((-stick3.getY() * 50000) + hood.getPulseWidthPosition());
		}
		else 
			hood.set(hood.getPulseWidthPosition());
//		else if(stick3.getRawButton(12))
//			hood.set(0.5);
//		else
//			hood.set(0.0);
	}

	public void adjustShooter() {
		if (stick3.getRawButton(1)) {
			if (stick3.getX() > .2) {
				cans.SetDrive(0.5, 0.5);
			}
			if (stick3.getX() < -.2) {
				cans.SetDrive(-0.5, -0.5);
			}
			// if(stick.getY()>.2){ //Comment out for use at a later date
			// shooter.set(0.5);
			// }
			// if(stick.getY()<.2){
			// shooter.set(-0.5);
			// }
		}
	}

	private void shooterReset() {
		m_state = ShooterState.MANUAL;
		shooter1.set(0.4);
		shooter2.set(0.4);
	}
}