package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;

public class TorSiege {
	private CANTalon siegeTalon;
	private Solenoid siegeSolenoid;
	public TorSiege(CANTalon T1, Solenoid S1){
		siegeTalon = T1;
		siegeSolenoid = S1;
	}
	
	public void SiegeArmDown(){
		siegeTalon.set(0.5);
	}
	public void SiegeArmUp(){
		siegeTalon.set(-0.5);
	}
	public void stopArm(){
		siegeTalon.set(0);
	}
	
	public void SallyPort(){
		siegeSolenoid.set(true);
	}
	public void stopSally(){
		siegeSolenoid.set(false);
	}
}
