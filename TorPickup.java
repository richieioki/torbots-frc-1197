package Torbots;

import edu.wpi.first.wpilibj.*;

public class TorPickup {
	
	private Joystick tartarus;
	private Solenoid wheelSolenoid;
	private Jaguar leftIntake;
	private Jaguar rightIntake;
	private AnalogInput sonar;
	private DigitalInput pSwitch;
	private TorJagDrive m_jagDrive;
	private boolean isOverridden;
	private boolean isReadyRaise;
	
	public TorPickup(Joystick tartarus, Solenoid wheelSolenoid, 
			Jaguar intake, Jaguar intake2, AnalogInput sonar, DigitalInput pressureSwitch,
			TorJagDrive jagDrive){
		this.tartarus = tartarus;
		this.wheelSolenoid = wheelSolenoid;
		leftIntake = intake;
		rightIntake = intake2;
		this.sonar = sonar;
		pSwitch = pressureSwitch;
		m_jagDrive = jagDrive;
		isOverridden = false;
		isReadyRaise = false;
	}
	public void run(){
		if(isOverridden){
			if(tartarus.getRawButton(1)){
				closeWheels();
			}
			if(tartarus.getRawButton(2)){
				retractWheels();
			}
		}
		else{
			if(getSonarDistance() < 10.0 && !pSwitch.get()){
				m_jagDrive.setJagSpeed(0.0, 0.0);
				closeWheels();
				while(!pSwitch.get()){
					if(tartarus.getRawButton(5)){
						break;
					}
				}
				retractWheels();
				isReadyRaise = true;
			}
		}
	}
	public void closeWheels(){
		wheelSolenoid.set(false);
		leftIntake.set(-0.2);
		rightIntake.set(0.2);
	}
	public void retractWheels(){
		leftIntake.set(0.0);                                                                                  
		rightIntake.set(0.0);
		wheelSolenoid.set(true);
	}
	public double getSonarDistance(){
		return sonar.getVoltage()/0.009766;
	}
	public boolean isReadyRaise(){
		
		return pSwitch.get();
	}
	public void setRaise(boolean a){
		isReadyRaise = a;
	}
	public void Override(boolean a){
		isOverridden = a;
	}
}
