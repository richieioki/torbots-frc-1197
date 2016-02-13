package org.usfirst.frc.team1197.robot;


import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Ultrasonic;

public class TorSiege{
	private CANTalon siegeTalon;

	private Joystick siegeStick;
	private AnalogPotentiometer pot;
	private Ultrasonic sonar;
	private TorCAN torcan;
	
	double armTop = this.potGet();
	double drawbridgeTop = armTop - 151;
	double drawbridgeBot = armTop - 655;
	double sallyPort = armTop - 368;
	double chevelTop = armTop - 476;
	double portcullis = 0;
	double potChecker = armTop - 655; //highest value
	
	public static final double SONAR = 10;
	private DRAWBRIDGE m_states;
	public TorTeleop tele;
	
	public enum DRAWBRIDGE{POS1, POS2, POS3, POS4, POS5};
	public enum SALLYPORT{POS1, POS2, POS3, POS4, POS5, POS6};
	public enum PORTCULLIS{POS1, POS2, POS3};
	public enum CHEVEL{POS1, POS2, POS3};
	
	public SALLYPORT m_sally;
	public PORTCULLIS m_port;
	public CHEVEL m_chev;
	private Solenoid shift;
	
	private double endTime;
	private double startTime = System.currentTimeMillis();
	
	
	public TorSiege(CANTalon T1, Joystick stick2, AnalogPotentiometer pot, 
			Ultrasonic sonar, TorCAN torcan, Solenoid shift){
		siegeTalon = T1;
		siegeStick = stick2;
		this.pot = pot;
		this.sonar = sonar;
		this.torcan = torcan;
		this.shift = shift;
	}
	public void DrawBridgeStates(){
		//list all defenses and movements for each class
		m_states = DRAWBRIDGE.POS1;
			if(this.override()!=true){
				
				switch(m_states){
					
					
					case POS1:
						endTime = System.currentTimeMillis() + 10;

						if(endTime != 0){
							if(sonar.getRangeInches() > 15){
								torcan.SetDrive(0.5, -0.5);
							}
						}
						if(startTime < endTime){ //meaning check if currenttime is >= endTime
							torcan.SetDrive(0, 0);
							m_states = DRAWBRIDGE.POS2;
							endTime = 0;
						}
					case POS2:
						while(this.checkTime(10) < 10){
							if(this.potGet() > drawbridgeTop){
								this.SiegeArmUp();
							}
							this.stopArm();
						}
						m_states = DRAWBRIDGE.POS3;
						
					
					case POS3:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							if(sonar.getRangeInches() < 10){
								torcan.SetDrive(-0.5, 0.5);
							}
						}
						if(startTime < endTime){ //meaning check if currenttime is >= endTime
							torcan.SetDrive(0, 0);
							endTime = 0;
							m_states = DRAWBRIDGE.POS4;
						}
						
					case POS4:
						while(this.checkTime(10) < 10){
							if(this.potGet() > drawbridgeBot){
								this.SiegeArmUp();
							}
							this.stopArm();
						}
						m_states = DRAWBRIDGE.POS5;
					
					case POS5:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							if(sonar.getRangeInches() > 10){
								torcan.SetDrive(0.5, -0.5);
							}
						}
						if(startTime < endTime){ //meaning check if currenttime is >= endTime
							torcan.SetDrive(0, 0);
							endTime = 0;
							break;
						}
				}
			}
	}
	public void SallyPortStates(){
		//list all defenses and movements for each class
		m_sally = SALLYPORT.POS1;
			if(this.override()!=true){
				
				switch(m_sally){
					
					
					case POS1:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							if(sonar.getRangeInches()>10){
								torcan.SetDrive(0.5, -0.5);
							}
						}
						if(startTime < endTime){
						torcan.SetDrive(0, 0);
						endTime = 0;
						m_sally = SALLYPORT.POS2;
						}
					
					case POS2:
						while(this.checkTime(10) < 10){
							if(this.potGet() > sallyPort){
								this.SiegeArmUp();
							}
							this.stopArm();
						}
						
						m_sally = SALLYPORT.POS3;
					
					case POS3:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							if(sonar.getRangeInches() < 10){
								torcan.SetDrive(-0.5, 0.5);
							}
						}
						if(startTime < endTime){
							torcan.SetDrive(0, 0);
							endTime = 0;
							m_sally = SALLYPORT.POS4;
						}
						
					case POS4:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							torcan.SetDrive(0.5, 0.5);
						}
						if(startTime < endTime){
							torcan.SetDrive(0, 0);
							endTime = 0;
							m_sally = SALLYPORT.POS5;
							}
					
					case POS5:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							torcan.SetDrive(-0.5, -0.5);
							//gyro
						}
						if(startTime < endTime){
							torcan.SetDrive(0, 0);
							endTime = 0;
							m_sally = SALLYPORT.POS6;
							}
					case POS6:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							if(sonar.getRangeInches() > 10){
								torcan.SetDrive(0.5, -0.5);
							}
						}
						if(startTime < endTime){
							torcan.SetDrive(0, 0);
							endTime = 0;
							break;
						}
				}
			}
	}
	public void PortcullisStates(){
		//list all defenses and movements for each class
		m_port = PORTCULLIS.POS1;
			if(this.override()!=true){
				
				switch(m_port){
					
					
					case POS1:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							if(sonar.getRangeInches() > 10){
								torcan.SetDrive(0.5, -0.5);
							}
						}
						if(startTime < endTime){
						torcan.SetDrive(0, 0);
						endTime = 0;
						m_port = PORTCULLIS.POS2;
						}
					
					case POS2:
						while(this.checkTime(10) < 10){
							if(this.potGet() > 100){
								this.SiegeArmUp();
							}
						}
						
						m_port = PORTCULLIS.POS3;
					
					case POS3:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							if(sonar.getRangeInches() < 10){
								torcan.SetDrive(-0.5, 0.5);
							}
						}
						if(startTime < endTime){
						torcan.SetDrive(0, 0);
						endTime = 0;
						}
				}
			}
	}
	public void ChevelStates(){
		//list all defenses and movements for each class
		m_chev = CHEVEL.POS1;
			if(this.override()!=true){
				
				switch(m_chev){
					
					
					case POS1:
						while(this.checkTime(10) < 10){
							if(this.potGet() > chevelTop){
								this.SiegeArmUp();
							}
						}
						m_chev = CHEVEL.POS2;
					
					case POS2:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							torcan.SetDrive(-0.5, 0.5);
						}
						if(startTime < endTime){
						torcan.SetDrive(0, 0);
						endTime = 0;
						m_chev = CHEVEL.POS3;
						}
					
					case POS3:
						endTime = System.currentTimeMillis() + 10;
						if(endTime != 0){
							torcan.SetDrive(0.5, -0.5);
						}
						if(startTime < endTime){
						torcan.SetDrive(0, 0);
						endTime = 0;
						break;
						}
				}
			}
	}
	public long checkTime(int wait){
		long time = System.currentTimeMillis();
		long endTime = System.currentTimeMillis() + wait; 
		long difference = endTime - time;
		return difference;
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
		else if(siegeStick.getRawButton(11)){
			shift.set(true);
		}
		else if(siegeStick.getRawButton(12)){
			shift.set(false);
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
	
	public double potGet(){
		double potValue = pot.get()*1024;
		System.out.println("Potentiometer value: " +(int)potValue);
		return (int)potValue;
	} 
	public boolean potChecker(){
		if(potChecker < 0){
			return false;
		}
		return true;
	}
	public boolean override(){
		boolean bool = false;
		if(siegeStick.getRawButton(11)){
			bool = true;
		}
			return bool;
	}
}

