package Torbots;

import edu.wpi.first.wpilibj.*;

public class TorPickup {
	
	private Joystick tartarus;
	private Solenoid clamper;
	private Jaguar intake;
	private AnalogInput sonar;
	private DigitalInput pSwitch;
	private boolean isOverridden;
	private boolean isReadyRaise;
	public TorPickup(Joystick tartarus, Solenoid clamp, Jaguar intake, AnalogInput sonar, DigitalInput pressureSwitch){
		this.tartarus = tartarus;
		clamper = clamp;
		this.intake = intake;
		this.sonar = sonar;
		isOverridden = false;
		isReadyRaise = false;
	}
	public void run(){
		if(isOverridden){
			if(tartarus.getRawButton(6)){
				Clamp();
			}
			if(tartarus.getRawButton(7)){
				Retract();
			}
		}
		else{
			if(getSonarDistance() < 10.0){
				Clamp();
				while(!pSwitch.get()){
					
				}
				intake.set(0.0);
				isReadyRaise = true;
				
			}
		}
	}
	public void Clamp(){
		clamper.set(true);
		intake.set(0.6);
	}
	public void Retract(){
		clamper.set(false);
	}
	public double getSonarDistance(){
		return sonar.getVoltage()/0.009766;
	}
}
