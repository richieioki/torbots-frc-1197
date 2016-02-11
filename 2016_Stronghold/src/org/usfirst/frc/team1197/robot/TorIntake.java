package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.CANTalon;

public class TorIntake {
	private CANTalon cantalon, cantalon2;
	private Joystick stick;
	public TorIntake(Joystick stick, CANTalon cantalon, CANTalon cantalon2){
		this.cantalon = cantalon;
		this.stick = stick;
		this.cantalon2 = cantalon2;
	}
	
	public void intake(){
		if(stick.getRawButton(10)){
			cantalon.set(0.75);
		}
		else if(stick.getRawButton(11)){
			cantalon.set(-0.75);
		}
		else{
			cantalon.set(0);
		}
		
		if(stick.getRawButton(4)){
			cantalon2.set(0.75);
		}
		else if(stick.getRawButton(5)){
			cantalon2.set(-0.75);
		}
		else{
			cantalon2.set(0);
		}
	}
	
	public void portcullis(){
		cantalon.set(-0.75);
	}

}
