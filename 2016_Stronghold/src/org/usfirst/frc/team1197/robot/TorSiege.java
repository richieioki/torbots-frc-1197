package org.usfirst.frc.team1197.robot;


import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

public class TorSiege{
	private CANTalon siegeTalon;
	private TorOnTarget target;
	private Joystick siegeStick, stick;
	private AnalogPotentiometer pot;
	private Ultrasonic sonar;
	private TorCAN torcan;

	public boolean enabled;


	public static final double SONAR = 10;
	private DRAWBRIDGE m_states;
	public TorTeleop tele;
	
	public enum DRAWBRIDGE{POS1, POS2, POS3, POS4, POS5, POS6, POS7, IDLE};
	public enum SALLYPORT{POS1, POS2, POS3, POS4, POS5, POS6, IDLE};
	public enum PORTCULLIS{POS1, POS2, POS3};
	public enum CHEVEL{POS1, POS2, POS3, IDLE};

	public SALLYPORT m_sally = SALLYPORT.IDLE;
	public PORTCULLIS m_port;
	public CHEVEL m_chev = CHEVEL.IDLE;
	private Solenoid shift;

	private double endTime = 0;
	private double sallyTime;
	private double startTime;
	private double chevTime;
	double ratio = 3.4/5;
	double armTop = 0;
	double drawbridgeTop = 0;
	double drawbridgeBot = 0;
	double sallyPort = 0;
	double chevelTop = 0;
	double portcullis = 0;
	double potChecker = 0; //highest value


	public TorSiege(CANTalon T1, Joystick stick2, AnalogPotentiometer pot, 
			Ultrasonic sonar, TorCAN torcan, Solenoid shift, Joystick stick){

		siegeTalon = T1;
		siegeStick = stick2;
		this.pot = pot;
		this.sonar = sonar;
		this.torcan = torcan;
		this.shift = shift;
		this.stick = stick;

		calc();
		enabled = false;

		target = new TorOnTarget(siegeTalon, 13);
		m_states = DRAWBRIDGE.IDLE;

		//T1.setInverted(true);

	}

	public void calc(){
		ratio = 3.4/5;
		int rest = siegeTalon.getAnalogInRaw();
		drawbridgeTop = (int)170;
		drawbridgeBot = (int)582;
		sallyPort = (int)299;
		chevelTop = (int)552;
		armTop = rest;
	}

	public void PID(){
		siegeTalon.enable();
		siegeTalon.set(armTop);
	}

	public double potGet(){
		double potValue = siegeTalon.getAnalogInRaw();
		armTop = siegeTalon.getAnalogInRaw();
		System.out.println("Potentiometer value: " + potValue);
		return potValue;
	} 

	public void potTest(){
		if(siegeStick.getRawButton(8)){
			siegeTalon.set(drawbridgeTop);	
		}
	}
	
	public void SallyPortStates(){
		//list all defenses and movements for each class
		calc();
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
					if(siegeTalon.getAnalogInRaw() > sallyPort){
						siegeTalon.set(sallyPort);
					}
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
		calc();
		ratio = 3.4/5;
		portcullis = 0;
		m_port = PORTCULLIS.POS1;
		if(this.override()!=true){

			switch(m_port){


			case POS1:
				endTime = System.currentTimeMillis() + 1000;
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
					if(siegeTalon.getAnalogInRaw() > portcullis){
						siegeTalon.set(portcullis);
					}
				}

				m_port = PORTCULLIS.POS3;

			case POS3:
				endTime = System.currentTimeMillis() + 10;
				if(endTime != 0){

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
		calc();
		m_chev = CHEVEL.POS1;
		if(this.override()!=true){

			switch(m_chev){


			case POS1:
				while(this.checkTime(10) < 10){
					if(siegeTalon.getAnalogInRaw() > chevelTop){
						siegeTalon.set(chevelTop);
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
		if(stick.getRawButton(3) && !enabled) {
			DrawBridge();
		}
		
		if(stick.getRawButton(4) && !enabled) {
			SallyPort();
		}
		
		if(stick.getRawButton(5) && !enabled) {
			Cheve();
		}

		if(!enabled) {
			if(siegeStick.getY() > -.2){
				siegeTalon.set((siegeStick.getY() * 100) + siegeTalon.getAnalogInRaw()); //siege arm down
			}
			else if(siegeStick.getY() < .2){
				siegeTalon.set((siegeStick.getY() * 100) + siegeTalon.getAnalogInRaw());
			}
		}

		else if(siegeStick.getRawButton(10)){
			shift.set(true);
		}
		else if(siegeStick.getRawButton(11)){
			shift.set(false);
		}
		else {
			//	siegeTalon.set(siegeTalon.getAnalogInRaw());
		}

		update();

	}

	private void Cheve() {
		if(!enabled) {
			enabled = true;
			m_chev = CHEVEL.POS1;
		}
		
	}

	private void SallyPort() {
		if(!enabled) {
			enabled = true;
			m_sally = SALLYPORT.POS1;
		}
	}

	private void update() {

		//OVER RIDE 
		if(stick.getRawButton(2)) {
			m_states = DRAWBRIDGE.IDLE;
			m_sally = SALLYPORT.IDLE;
			m_chev = CHEVEL.IDLE;
		}
		
		switch(m_chev) {
		case IDLE:
			if(chevTime != -1) {
				chevTime = -1;
			}
			break;
		
		case POS1:
			siegeTalon.set(chevelTop);
			m_chev = CHEVEL.POS2;
			break;
			
		case POS2:
			if(target.OnTarget((int)chevelTop)) {
				torcan.SetDrive(-0.5, 0.5);
				Timer.delay(0.2);
				torcan.SetDrive(0, 0);
				m_chev = CHEVEL.POS3;
			}
			break;
		case POS3:
			if(chevTime == -1) {
				chevTime = System.currentTimeMillis() + 2500;
				torcan.SetDrive(0.5, -0.5);
				siegeTalon.set(chevelTop - 100);
			} else if(chevTime <= System.currentTimeMillis()) {
				enabled = false;
				m_chev = CHEVEL.IDLE;
			}
			break;
		}
		
		switch(m_sally) {
		case IDLE:
			if(sallyTime != -1) {
				sallyTime = -1;
			}
			break;
			
			
		case POS1:
			//torcan.SetDrive(-0.5, 0.5);
			m_sally = SALLYPORT.POS2;
			//if(sallyTime == -1) {
			//	sallyTime = System.currentTimeMillis() + 180;
			//} 
			break;
			
		case POS2:
			//if(sallyTime <= System.currentTimeMillis()) {
				m_sally = SALLYPORT.POS3;
				torcan.SetDrive(0, 0);
				siegeTalon.set(sallyPort + 25);
			//}
			break;
		case POS3:
			if(target.OnTarget((int)sallyPort)) {
				m_sally = SALLYPORT.POS4;				
			}
			break;
		
		case POS4:
			if(sallyTime == -1) {
				torcan.SetDrive(-0.5, 0.5);
				sallyTime = System.currentTimeMillis() + 2200;
			} else if(sallyTime <= System.currentTimeMillis()) {
				m_sally = SALLYPORT.POS5;
				torcan.SetDrive(0, 0);
				sallyTime = -1;
			}
			break;
		case POS5:
//			Timer.delay(0.2);
			torcan.SetDrive(0.5, 0.5);
			Timer.delay(0.25);
			torcan.SetDrive(0,0);
			Timer.delay(0.2);
			torcan.SetDrive(-0.5,-0.5);
			Timer.delay(0.2);
			torcan.SetDrive(0,0);
			torcan.SetDrive(0.5,-0.5);
			m_sally = SALLYPORT.POS6;
			break;
			
		case POS6:
			if(sallyTime == -1) {
				sallyTime = System.currentTimeMillis() + 3000;
			} else if(sallyTime <= System.currentTimeMillis()) {
				enabled = false;
				m_sally = SALLYPORT.IDLE;
			}
			break;
		}

		switch(m_states) {
		case IDLE:
			//do something if idle
			if(endTime != -1) {
				endTime = -1;
			}

			break;
		case POS1: //driving against and lower arm
			siegeTalon.set(drawbridgeTop + 20);
			m_states = DRAWBRIDGE.POS2;
			break;
		case POS2:
			System.out.println("DRAVE BRIDGE TOP " + siegeTalon.getAnalogInRaw());
			if(target.OnTarget((int)drawbridgeTop)) {
				m_states = DRAWBRIDGE.POS3;
				endTime = -1;
			}
			break;
		case POS3:
			if(endTime == -1) {
				endTime = System.currentTimeMillis() + 1400;
				siegeTalon.set(drawbridgeTop + 140);
				torcan.SetDrive(-0.5, 0.5);
			} else {
				double startTime = System.currentTimeMillis();
				if(startTime > endTime) {
					m_states = DRAWBRIDGE.POS4;
					torcan.SetDrive(0, 0);
				}
			}
			break;
		case POS4:
			siegeTalon.set(drawbridgeBot);
			m_states = DRAWBRIDGE.POS5;
			endTime = -1;
			break;

		case POS5:
			if(target.OnTarget((int)drawbridgeBot)) {
				torcan.SetDrive(0.5, -0.5);
				m_states = DRAWBRIDGE.POS6;
			}
			break;

		case POS6:
			if(endTime == -1) {
				endTime = System.currentTimeMillis() + 2000;
			} else if(endTime <= System.currentTimeMillis()) {
				//YOU HAVE REACHED THE END
				m_states = DRAWBRIDGE.IDLE;
				enabled = false;
			}
			break;
		}
	}

	public void DrawBridge() {
		if(!enabled) {
			enabled = true;
			m_states = DRAWBRIDGE.POS1;
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
	public boolean override(){
		boolean bool = false;
		if(stick.getRawButton(2)){
			bool = true;
		}
		return bool;
	}

	public void reset() {
		System.out.println("!!!!!!!!!!!RESET!!!!!!!!!!!!");
		m_states = DRAWBRIDGE.IDLE;
		//siegeTalon.set(siegeTalon.getAnalogInRaw());
		//siegeTalon.setProfile(0);
	}
}

