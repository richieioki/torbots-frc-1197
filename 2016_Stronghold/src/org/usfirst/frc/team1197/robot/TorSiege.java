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
	private TorIntake intakeSiege;

	public boolean enabled;


	public static final double SONAR = 10;
	private DRAWBRIDGE m_states;
	public TorTeleop tele;
	
	public enum DRAWBRIDGE{POS1, POS2, POS3, POS4, POS5, POS6, POS7, IDLE};
	public enum SALLYPORT{POS1, POS2, POS3, POS4, POS5, POS6, IDLE};
	public enum PORTCULLIS{IDLE, POS1, POS2, POS3};
	public enum CHEVEL{POS1, POS2, POS3, IDLE};

	public SALLYPORT m_sally = SALLYPORT.IDLE;
	public PORTCULLIS m_port = PORTCULLIS.IDLE;
	public CHEVEL m_chev = CHEVEL.IDLE;
	private Solenoid shift;

	private double endTime = 0;
	private double sallyTime;
	private double startTime;
	private double chevTime;
	private double portTime;
	double ratio = 3.4/5;
	double armTop = 0;
	double drawbridgeTop = 0;
	double drawbridgeBot = 0;
	double sallyPort = 0;
	double chevelTop = 0;
	double portcullisTop = 0;
	double portcullisBot = 0;
	double potChecker = 0; //highest value
	double intakeVal = 0;


	public TorSiege(CANTalon T1, Joystick stick2, AnalogPotentiometer pot, 
			Ultrasonic sonar, TorCAN torcan, Solenoid shift, Joystick stick, TorIntake intakee){

		siegeTalon = T1;
		siegeStick = stick2;
		this.pot = pot;
		this.sonar = sonar;
		this.torcan = torcan;
		this.shift = shift;
		this.stick = stick;
		intakeSiege = intakee;

		calc();
		enabled = false;

		target = new TorOnTarget(siegeTalon, 13);
		m_states = DRAWBRIDGE.IDLE;

		//T1.setInverted(true);

	}

	public void calc(){
		ratio = 3.4/5;
		int rest = siegeTalon.getAnalogInRaw();
		siegeTalon.setSetpoint(armTop);
		drawbridgeTop = armTop + 160;
		drawbridgeBot = armTop + 540; //550 for protobot, 800 for final bot
		sallyPort = armTop + 250; //260 for protobot, 400 for final bot
		chevelTop = armTop + 520; //550 for protobot, 800 for final bot
		portcullisTop = armTop + 90;
		portcullisBot = armTop + 520; //530 for protobot, 825 for final bot			
		intakeVal = armTop + 708;
		armTop = (int)rest;
	}


	public void PID(){
		siegeTalon.enable();
		int rest = siegeTalon.getAnalogInRaw();
		siegeTalon.set(rest);
		siegeTalon.setSetpoint(rest);
	}

	public int potGet(){
		double potValue = siegeTalon.getAnalogInRaw();
//		System.out.println("Potentiometer value: " + (int)potValue);
		return (int)potValue;
	} 
// 420
	//we will win 
	
	public void potTest(){
		if(siegeStick.getRawButton(8)){
			siegeTalon.set(drawbridgeTop);	
		}
	}
	public void intakeTele(){
		if(siegeStick.getRawButton(3)){
			siegeTalon.set(intakeVal);
			siegeTalon.setSetpoint(intakeVal);
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
		if(stick.getRawButton(6) && !enabled) {
			Portcullis();
		}

		
		if(!enabled) {
			if(siegeStick.getY() > -.2){
				siegeTalon.set((-siegeStick.getY() * 100) + siegeTalon.getAnalogInRaw()); //siege arm down
			}
			else if(siegeStick.getY() < .2){
				siegeTalon.set((-siegeStick.getY() * 100) + siegeTalon.getAnalogInRaw());
			}
		}

		if(siegeStick.getRawButton(7)){
			shift.set(true);
			System.out.println("!!!!!Solenoid Enabled!!!!!!");
		}
		if(siegeStick.getRawButton(9)){
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
	
	private void Portcullis() {
		if(!enabled) {
			enabled = true;
			m_port = PORTCULLIS.POS1;
		}
		
	}

	private void SallyPort() {
		if(!enabled) {
			enabled = true;
			m_sally = SALLYPORT.POS1;
		}
	}
	public void DrawBridge() {
		if(!enabled) {
			enabled = true;
			m_states = DRAWBRIDGE.POS1;
		}
	}

	private void update() {

		//OVER RIDE 
		if(stick.getRawButton(2)) {
			m_states = DRAWBRIDGE.IDLE;
			m_sally = SALLYPORT.IDLE;
			m_chev = CHEVEL.IDLE;
			m_port = PORTCULLIS.IDLE;
		}
		
		
		switch(m_port) {
		case IDLE:
			if(portTime != -1){
				portTime = -1;
				torcan.SetDrive(0, 0);
				enabled = false;
			}
			break;
		
		case POS1:
			if(portTime == -1) {
				portTime = System.currentTimeMillis() + 750;
				intakeSiege.portcullis();
				siegeTalon.set(portcullisTop);
				Timer.delay(0.2);
				torcan.SetDrive(0.2,-0.2);
			} else if(portTime <= System.currentTimeMillis()) {
				m_port = PORTCULLIS.POS2;
				intakeSiege.portStop();
				portTime = -1;
				torcan.SetDrive(0,0);
			}
			break;
		case POS2:
			if(portTime == -1) {
				portTime = System.currentTimeMillis() + 1700;
				torcan.SetDrive(0.5,-0.5);
			} else if(portTime <= System.currentTimeMillis()) {
				m_port = PORTCULLIS.IDLE;
				portTime = -1;
				torcan.SetDrive(0,0);
				enabled = false;
			}
			break;
			
		}
		
		
		switch(m_chev) {
		case IDLE:
			if(chevTime != -1) {
				chevTime = -1;
				torcan.SetDrive(0, 0);
				enabled = false;
			}
			break;
		
		case POS1:
			siegeTalon.set(chevelTop);
			Timer.delay(0.7);
			m_chev = CHEVEL.POS2;
			break;
			
		case POS2:
			if(chevTime == -1) {
				chevTime = System.currentTimeMillis() + 250;
				torcan.SetDrive(-0.5, 0.5);
			} else if(chevTime <= System.currentTimeMillis()) {
				torcan.SetDrive(0, 0);
				chevTime = -1;
				m_chev = CHEVEL.POS3;
			}
			break;
		case POS3:
			if(chevTime == -1) {
				chevTime = System.currentTimeMillis() + 2500;
				torcan.SetDrive(0.5, -0.5);
				Timer.delay(0.3);
				siegeTalon.set(chevelTop - 250);
			} else if(chevTime <= System.currentTimeMillis()) {
				torcan.SetDrive(0, 0);
				enabled = false;
				m_chev = CHEVEL.IDLE;
			}
			break;
			
		}
		
		switch(m_sally) {
		case IDLE:
			if(sallyTime != -1) {
				sallyTime = -1;
				torcan.SetDrive(0, 0);
				enabled = false;
			}
			break;
			
			
		case POS1:
			if(sallyTime == -1) {
				torcan.SetDrive(-0.5, 0.5);
				sallyTime = System.currentTimeMillis() + 100;
			} 
			else if(sallyTime <= System.currentTimeMillis()){
				m_sally = SALLYPORT.POS2;
				torcan.SetDrive(0,0);
				sallyTime = -1;
			}
			break;
			
		case POS2:
			//if(sallyTime <= System.currentTimeMillis()) {
				m_sally = SALLYPORT.POS3;
				torcan.SetDrive(0, 0);
				siegeTalon.set(sallyPort);
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
				sallyTime = System.currentTimeMillis() + 1500;//originally 2200
			} else if(sallyTime <= System.currentTimeMillis()) {
				m_sally = SALLYPORT.POS5;
				torcan.SetDrive(0, 0);
				sallyTime = -1;
			}
			break;
		case POS5:
			Timer.delay(0.7);
			torcan.SetDrive(0.6, 0.6);
			Timer.delay(0.25);
			torcan.SetDrive(0,0);
			Timer.delay(0.2);
			torcan.SetDrive(-0.6,-0.6);
			Timer.delay(0.2);
			torcan.SetDrive(0,0);
			torcan.SetDrive(0.5,-0.5);
			m_sally = SALLYPORT.POS6;
			break;
			
		case POS6:
			if(sallyTime == -1) {
				sallyTime = System.currentTimeMillis() + 1000;
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
				torcan.SetDrive(0, 0);
				enabled = false;
			}

			break;
		case POS1: //driving against and lower arm
			siegeTalon.set(drawbridgeTop);
			Timer.delay(0.5);
			m_states = DRAWBRIDGE.POS2;
			break;
		case POS2:
			if(endTime == -1) {
				endTime = System.currentTimeMillis() + 1250;
				siegeTalon.set(drawbridgeTop + 140);
				torcan.SetDrive(-0.5, 0.5);
			} else {
				double startTime = System.currentTimeMillis();
				if(startTime > endTime) {
					m_states = DRAWBRIDGE.POS3;
					torcan.SetDrive(0, 0);
				}
			}
			break;
		case POS3:
			siegeTalon.set(drawbridgeBot);
			m_states = DRAWBRIDGE.POS4;
			endTime = -1;
			break;

		case POS4:
			if(target.OnTarget((int)drawbridgeBot)) {
				torcan.SetDrive(0.5, -0.5);
				m_states = DRAWBRIDGE.POS5;
			}
			break;

		case POS5:
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

	public void SiegeArmDown(){
		siegeTalon.set(chevelTop);
	}
	public void SiegeArmUp(){
		siegeTalon.set(chevelTop-100);
	}
	public void drawbridgeTop(){
		siegeTalon.set(drawbridgeTop);
	}
	public void drawbridgeMid(){
		siegeTalon.set(drawbridgeTop+150);
	}
	public void drawbridgeBot(){
		siegeTalon.set(drawbridgeBot);
	}
	public void sally(){
		siegeTalon.set(sallyPort);
	}
	public void portBot(){
		siegeTalon.set(portcullisBot);
	}
	public void portTop(){
		siegeTalon.set(portcullisTop);
	}
	public void stopArm(){
		siegeTalon.set(0);
	}
	public void reset() {
		System.out.println("!!!!!!!!!!!RESET!!!!!!!!!!!!");
		m_states = DRAWBRIDGE.IDLE;
		//siegeTalon.set(siegeTalon.getAnalogInRaw());
		//siegeTalon.setProfile(0);
	}
}

