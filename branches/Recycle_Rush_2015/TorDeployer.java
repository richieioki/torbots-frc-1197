package Torbots;

import edu.wpi.first.wpilibj.*;

public class TorDeployer {
	private Joystick stick;
	private Solenoid deployer;
	private boolean isFired;
	public TorDeployer(Joystick stick, Solenoid deploy){
		this.stick = stick;
		deployer = deploy;
		isFired = false;
	}
	public void run(){
		if(stick.getRawButton(1)){
			if(isFired){
				Retract();
			}
			else{
				Deploy();
			}
		}
	}
	public void Deploy(){
		deployer.set(true);
		isFired = true;
	}
	public void Retract(){
		deployer.set(false);
		isFired = false;
	}
	public boolean isFired(){
		return isFired;
	}
	
}
