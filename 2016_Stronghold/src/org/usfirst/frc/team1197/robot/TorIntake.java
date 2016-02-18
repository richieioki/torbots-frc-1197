package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.CANTalon;

public class TorIntake {
	private CANTalon cantalon, cantalon2;
	private Joystick stick;
	private DigitalInput breakBeam;
	public TorIntake(Joystick stick, CANTalon cantalon, CANTalon cantalon2, DigitalInput breakbeam){
		this.cantalon = cantalon;
		this.stick = stick;
		this.cantalon2 = cantalon2;
		this.breakBeam = breakbeam;
	}
	public void intake(){
		if(stick.getRawButton(4)){
			cantalon2.set(0.95);
			cantalon.set(0.95);
		}
		else if(stick.getRawButton(5)){
			cantalon2.set(-0.95);
			cantalon.set(-0.95);
		}
		else{
			cantalon2.set(0);
			cantalon.set(0);
		}
	}
	
	public void portcullis(){
		cantalon.set(0.95);
	}
	public void portStop(){
		cantalon.set(0);
	}
	public void portcullisTele(double val){
		cantalon.set(val);
	}

}
