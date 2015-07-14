package Torbots;

import edu.wpi.first.wpilibj.*;

public class TorPickup {

	private Joystick tartarus;
	private Joystick jon;

	private Solenoid wheelSolenoid;
	private Jaguar leftIntake;
	private Jaguar rightIntake;

	private AnalogInput sonar;
	private DigitalInput pSwitch;
	private DigitalInput lSwitch;

	private TorJagDrive m_jagDrive;

	private boolean isOverridden;
	private boolean elevatorRunning;
	private boolean isReadyRaise;
	
	private boolean CLOSED;
	private boolean OPEN;

	public TorPickup(Joystick tartarus, Solenoid wheelSolenoid, 
			Jaguar intake, Jaguar intake2, AnalogInput sonar, DigitalInput pressureSwitch,
			TorJagDrive jagDrive, Joystick j, DigitalInput leftSwitch){
		this.tartarus = tartarus;

		this.wheelSolenoid = wheelSolenoid;
		leftIntake = intake;
		rightIntake = intake2;

		this.sonar = sonar;
		pSwitch = pressureSwitch;
		lSwitch = leftSwitch;

		m_jagDrive = jagDrive;

		isOverridden = false;
		isReadyRaise = false;
		
		CLOSED = false;
		OPEN = true;
		
		jon = j;
	}
	public void wait(double t){
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) / 1000.0 < t){

		}
	}
	public void run(){
		if(isOverridden){
			if(tartarus.getRawButton(1)){
				if(!wheelSolenoid.get()){
					leftIntake.set(0.0);                                                                                  
					rightIntake.set(0.0);
				}
				else{
					leftIntake.set(-0.3);
					rightIntake.set(0.3);
				}
				wheelSolenoid.set(!wheelSolenoid.get());
				wait(0.1);
				
			}
		}
		else{
			if(getSonarDistance() < 15.0 && !pSwitch.get() && !elevatorRunning){
				m_jagDrive.setJagSpeed(0.0, 0.0);
				closeWheels();
				while(!(pSwitch.get()&&lSwitch.get()) && !jon.getRawButton(5)){
					if(tartarus.getRawButton(5)){
						break;
					}
					if(tartarus.getRawButton(4)){
						break;
					}
					if(pSwitch.get() && !lSwitch.get()){
						rightIntake.set(0.5);
						leftIntake.set(-0.15);
					}
					else if(!pSwitch.get() && lSwitch.get()){
						rightIntake.set(0.15);
						leftIntake.set(-0.5);
					}
					else{
						leftIntake.set(-0.3);
						rightIntake.set(0.3);			
						}
				}
				wait(0.1);
				retractWheels();
				isReadyRaise = true;
			}
		}
	}
	public void run2(){
		leftIntake.set(0.0);                                                                                  
		rightIntake.set(0.0);
		if(tartarus.getRawButton(1)){
			wheelSolenoid.set(!wheelSolenoid.get());
			wait(0.1);
		}
	}
	
	public void closeWheels(){
		wheelSolenoid.set(CLOSED); 
		leftIntake.set(-0.3);
		rightIntake.set(0.3);
	}
	public void retractWheels(){
		leftIntake.set(0.0);                                                                                  
		rightIntake.set(0.0);
		wheelSolenoid.set(OPEN);
	}
	public void close(){
		leftIntake.set(0.0);
		rightIntake.set(0.0);
		wheelSolenoid.set(CLOSED);
	}
	public void shoot(){
		leftIntake.set(1.0);
		rightIntake.set(-1.0);
		wait(1.0);
		leftIntake.set(0.0);
		rightIntake.set(0.0);
	}
	public void deploy(int x){
		wait(1.0);
		wheelSolenoid.set(CLOSED);
		leftIntake.set(((30.0*x)/4.0)+(70.0/4.0));
		rightIntake.set(-((30.0*x)/4.0)+(70.0/4.0));
		wait(0.5);
		leftIntake.set(0.0);
		rightIntake.set(0.0);
		wheelSolenoid.set(OPEN);
		m_jagDrive.setJagSpeed(-0.5, 0.5);
		wait(0.5);
		wheelSolenoid.set(OPEN);
		Override(false);
		wheelSolenoid.set(OPEN);

	}
	public double getSonarDistance(){
		return sonar.getAverageVoltage()/0.009766;
	}
	public boolean isReadyRaise(){
		return pSwitch.get() || lSwitch.get();
	}
	public void setRaise(boolean a){
		isReadyRaise = a;
	}
	public void Override(boolean a){
		isOverridden = a;
	}
	public void elevatorRunning(boolean a){
		elevatorRunning = a;
	}

}
