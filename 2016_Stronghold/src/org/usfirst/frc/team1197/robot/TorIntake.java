package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Timer;

public class TorIntake {
	private CANTalon armTalon, elevatorTalon;
	private Joystick stick;
	private DigitalInput bottomBreakBeam, shooterBreakBeam;
	private TorSiege siege;
	private enum IntakeState{BOTH, ELEVATOR, IDLE};
	private IntakeState m_state;
	
	public TorIntake(Joystick stick, CANTalon cantalon, CANTalon cantalon2, DigitalInput breakBeam, DigitalInput breakBeam2, TorSiege siege){
		this.armTalon = cantalon; //arm talon		
		this.elevatorTalon = cantalon2; //elevator talon
		this.bottomBreakBeam = breakBeam; //bottom break beam
		this.shooterBreakBeam = breakBeam2; //shooter break beam
		this.siege = siege;
		this.stick = stick;
		m_state = IntakeState.IDLE;
	}
	
	public void intake(){
		//MANUAL CONTROL OF INTAKE
		if(stick.getRawButton(4)){
			m_state = IntakeState.IDLE;
			elevatorTalon.set(0.95);
			armTalon.set(0.95);
		} else if(stick.getRawButton(5)) {
			m_state = IntakeState.IDLE;
			armTalon.set(-0.95);
			elevatorTalon.set(-0.95);
		}
		
		//INTAKE using break beams
		else if(stick.getRawButton(6) && m_state == IntakeState.IDLE){
			m_state = IntakeState.BOTH; //if you are idle and the button is pressed go to both mode
			elevatorTalon.set(-0.95);
			armTalon.set(-0.95);
		} else if(m_state == IntakeState.BOTH) {
			if(bottomBreakBeam.get() == true) {
				armTalon.set(0);
				m_state = IntakeState.ELEVATOR;
			}
		} else if(m_state == IntakeState.ELEVATOR) {
			if(shooterBreakBeam.get() == true) {
				elevatorTalon.set(0);
				m_state = IntakeState.IDLE;
			}
		}/*
			if(breakBeam.get() == true && breakBeam2.get() == false){
				armIntakeStop(true);
				cantalon2.set(-0.95);
			}
			else if(breakBeam.get() == false && breakBeam2.get() == false){
				cantalon2.set(-0.95);
				cantalon.set(-0.95);
			}
			else if(breakBeam.get() == false && breakBeam2.get() == true){
				cantalon.set(0);
				cantalon2.set(-0.95);
			}*/
		else{
			if(m_state == IntakeState.IDLE) {
				elevatorTalon.set(0);
				armTalon.set(0);
			}
		}
	}
	
	public void autoLoad(){
		boolean setTrue;
//		while(stick.getRawButton(6)&&breakBeam2.get()==false){
//			cantalon.set(-0.95);
//			cantalon2.set(-0.95);
//		}
	}
	
	public boolean override(){
		if(stick.getRawButton(8)){
		return true;
		}
		else{
		return false;
		}
	}
	
	public void portcullis(){
		armTalon.set(0.95);
	}
	
	public void portStop(){
		armTalon.set(0);
	}
	
	public void portcullisTele(double val){
		armTalon.set(val);
	}
	
	public void armIntakeStop(boolean bool){
		if(bool == true){
			armTalon.set(0);
		}
	}
	
	public void intakeStop(boolean bool){
		if(bool == true){
			elevatorTalon.set(0);
		}
	}
}
