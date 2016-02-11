package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Ultrasonic;

public class TorSiege implements PIDSource{
	private CANTalon siegeTalon;
	private Solenoid siegeSolenoid;
	private Joystick siegeStick;
	private AnalogPotentiometer pot;
	private PIDController siegePID;
	private Ultrasonic sonar;
	private TorCAN torcan;
	public static final int DRAWBRIDGE_TOP = 648;
	public static final int DRAWBRIDGE_BOT = 130;
	public static final int SALLY_PORT = 487; 
	public static final int CHEVEL_BOT = 183;
	public static final int CHEVEL_TOP = 309;
	public static final int ARM_TOP = 785;
	public static final int ARM_BOT = 119;
	public static final double SONAR = 10;
	
	
	public TorSiege(CANTalon T1, Solenoid S1, Joystick stick2, AnalogPotentiometer pot, Ultrasonic sonar, TorCAN torcan){
		siegeTalon = T1;
		siegeSolenoid = S1;
		siegeStick = stick2;
		this.pot = pot;
		this.sonar = sonar;
		this.torcan = torcan;
	}
	
	public void SiegeArmUpdate(){
		if(siegeStick.getY() > .2){
		siegeTalon.set(siegeStick.getY()); //siege arm down
		}
		else if(siegeStick.getY() < -.2){
		siegeTalon.set(siegeStick.getY());
		}
		else if(siegeStick.getY() > .2){
			siegeTalon.set(0.5); // siege arm up
		}
		else if(siegeStick.getRawButton(5)){
			siegeSolenoid.set(true); // piston fire
		}
		else if(siegeStick.getRawButton(4)){
			siegeSolenoid.set(false); // piston retract
		}
		else {
			siegeTalon.set(0);
		}
		
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

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double pidGet() {
		// TODO Auto-generated method stub
		double potenValue = pot.get();
		System.out.println("Potentiometer value: " + potenValue);
		return potenValue;
	}
	public double potGet(){
		double potValue = pot.get()*1024;
		double potValue2 = potValue;
		potValue2 = 800.0;
		System.out.println("Potentiometer value: " +(int)potValue);
		return (int)potValue;
	}  
	public void drawbridgeSiege(){
//		double range=sonar.getRangeInches();
//		System.out.println(range);
		
//		while(this.potGet()<DRAWBRIDGE_TOP){
//			siegeTalon.set(0.25);
//		
//		}
		while(this.potGet()>DRAWBRIDGE_TOP){
				siegeTalon.set(-0.5);
			}
			siegeTalon.set(0.0);
	}
	public void sallyPortSiege(){
		while(this.potGet()<SALLY_PORT){ //need to change sally value
			siegeTalon.set(0.25);
		}
			siegeTalon.set(0.0);
	}
	public void chevelSiege(){
		while(this.potGet()<CHEVEL_TOP&&this.potGet()>CHEVEL_BOT){
			siegeTalon.set(0.25);
		}
			siegeTalon.set(0.0);
	}
}
