package org.usfirst.frc.team1197.robot;


import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Encoder;
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
	private TorDrive drive;
	private TorIntake intakeSiege;
	private Encoder encoder;
	private AHRS gyro;

	public boolean enabled;


	public static final double SONAR = 10;
	private DRAWBRIDGE m_states;
	public TorTeleop tele;
	
	public enum DRAWBRIDGE{POS1, POS2, POS3, POS4, POS5, POS6, POS7, IDLE};
	public enum SALLYPORT{POS0, POS1, POS2, POS3, POS4, POS5, POS6, POS7, IDLE};
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
	double degrees;
	double setDegreesSlope;
	double readDegreesSlope;
	double setDegreesInter;
	double readDegreesInter;
	double m_speed;
	double m_distance;
	
	//VARIABLE TO CHANGE
	double degreesTop;
	double degreesBot;
	int bottomArm;
	double drawbridgeConstant;
	double drawbridgeBack;
	double sallyPortInitBack;
	double sallyPortBack;
	double chevelBack;
	double chevelArmUp;
	double chevelDist;
	double sallyPortDist;
	double drawbridgeArmUp;
	double drawbridgeDist;
	double turnAngle;
	double turnP;
	double turnSpeed;
	double error;
	double targetAngle;
	double sallyStartAngle;
	
	public TorSiege(CANTalon T1, Joystick stick2, AnalogPotentiometer pot, 
			Ultrasonic sonar, TorCAN torcan, Solenoid shift, Joystick stick, 
			TorIntake intakee, TorDrive drive, Encoder encoder, AHRS gyro){

		siegeTalon = T1;
		siegeStick = stick2;
		this.pot = pot;
		this.sonar = sonar;
		this.torcan = torcan;
		this.shift = shift;
		this.stick = stick;
		intakeSiege = intakee;
		this.drive = drive;
		this.encoder = encoder;
		this.gyro = gyro;

		calc();
		enabled = false;

		target = new TorOnTarget(siegeTalon, 2);
		m_states = DRAWBRIDGE.IDLE;

		//T1.setInverted(true);

	}
	public boolean siegeOnTarget(int tolerance) {
		double currentAngle = getDegrees();
		double setpointDegrees = readDegreesSlope*siegeTalon.getSetpoint()+readDegreesInter;
		if(currentAngle > (setpointDegrees - tolerance) 
		&& currentAngle < (setpointDegrees + tolerance)) {
			return true;
		} else {
			return false;
		}
	}
	public boolean siegeOnTargetRaw(int tolerance) {
		int rawValue = siegeTalon.getAnalogInRaw();
		if(rawValue > (siegeTalon.getSetpoint() - tolerance) 
		&& rawValue < (siegeTalon.getSetpoint() + tolerance)) {
			return true;
		} else {
			return false;
		}
	}
	public void setDegrees(double degrees){
		this.degrees = degrees;
		 siegeTalon.set(setDegreesSlope*degrees+setDegreesInter);
	}
	public double getDegrees(){
		return readDegreesSlope*siegeTalon.getAnalogInRaw()+readDegreesInter;
	}

	public void calc(){
		//VARIABLE TO CHANGE
		turnP = 0.05;
		degreesTop = 50.6;
		degreesBot = -70.3;
		bottomArm = 558;
		drawbridgeBack = -37;
		sallyPortInitBack = -6;
		sallyPortBack = -37;
		chevelBack = -8;
		chevelArmUp = 17;
		chevelDist = 80;
		sallyPortDist = 80;
		drawbridgeArmUp = 12;
		drawbridgeDist = 50;
//		ratio = 3.4/5;
		int rest = 320;
		setDegreesSlope = (bottomArm - rest)/(degreesBot - degreesTop);
		setDegreesInter = rest-setDegreesSlope*degreesTop;
		readDegreesSlope = 1/setDegreesSlope;
		readDegreesInter = -setDegreesInter/setDegreesSlope;
		
//		int rest = siegeTalon.getAnalogInRaw();
		//VARIABLE TO CHANGE
		siegeTalon.setSetpoint(armTop);
		drawbridgeTop = 29;
		drawbridgeBot = -62; 
		sallyPort = 0; //430
		chevelTop = -54; //530
		portcullisTop = armTop; //redo values later 370
		portcullisBot = armTop; //redo values later 530
		intakeVal = armTop + 708; //708
		siegeTalon.setSetpoint(320); //320
		drawbridgeConstant = (drawbridgeTop-drawbridgeBot)/(-1*drawbridgeBack);
//		armTop = (int)rest;
		//50.6 degrees right side top
		// -70.3 degrees right side bottom
	}


	public void PID(){
		siegeTalon.enable();
		int rest = siegeTalon.getAnalogInRaw();
		siegeTalon.set(rest);
		siegeTalon.setSetpoint(rest);
	}

	public double potGet(){
		double potValue = getDegrees();
//		System.out.println("Potentiometer value: " + (int)potValue);
		return potValue;
	} 
// 420
	//we will win 
	
	public void potTest(){
		if(siegeStick.getRawButton(8)){
			setDegrees(drawbridgeTop);	
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
		if(stick.getRawButton(2)){
			reset();
		}
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
			if(siegeStick.getY() < -.025){
				siegeTalon.setProfile(1);
				siegeTalon.set((-siegeStick.getY() * 30) + siegeTalon.getAnalogInRaw()); //siege arm down
			}
			else if(siegeStick.getY() > .025){
				siegeTalon.setProfile(1);
				siegeTalon.set((-siegeStick.getY() * 30) + siegeTalon.getAnalogInRaw());
			}
			else
				siegeTalon.setProfile(0);
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
			m_sally = SALLYPORT.POS0;
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
				Timer.delay(.2);
				siegeTalon.set(portcullisTop);
				Timer.delay(.2);
				intakeSiege.portcullis();
				Timer.delay(0.2);
				torcan.SetDrive(0.2,-0.2);
				intakeSiege.portcullis();
				Timer.delay(.2);
			} else if(portTime <= System.currentTimeMillis()) {
				m_port = PORTCULLIS.POS2;
//				intakeSiege.portStop();
				portTime = -1;
				torcan.SetDrive(0,0);
			}
			break;
		case POS2:
			if(portTime == -1) {
				intakeSiege.portcullis();
				Timer.delay(.2);
				portTime = System.currentTimeMillis() + 1700;
				torcan.SetDrive(0.5,-0.5);
			} else if(portTime <= System.currentTimeMillis()) {
				intakeSiege.portStop();
				m_port = PORTCULLIS.IDLE;
				portTime = -1;
				torcan.SetDrive(0,0);
				enabled = false;
			}
			break;
			
		}
		
		
		switch(m_chev) {
		case IDLE:
				enabled = false;
			break;
		
		case POS1:
			setDegrees(chevelTop);
			if(siegeOnTarget(1)){
			m_chev = CHEVEL.POS2;
			encoder.reset();
			}
			break;
		case POS2:
				torcan.SetDrive(-0.35, 0.35);
				setDegrees(chevelTop-2);
				if(encoder.getDistance()<-8){
					haltDrive(0.5);
					m_chev = CHEVEL.POS3;
					encoder.reset();
				}
			break;
		case POS3:
				torcan.SetDrive(0.5, -0.5);
				if(encoder.getDistance()>chevelArmUp){
					setDegrees(0);
				}
				if(encoder.getDistance()>chevelDist){
					torcan.SetDrive(0, 0);
					enabled = false;
					m_chev = CHEVEL.IDLE;
				}
			break;
			
		}
		
		switch(m_sally) {
		case IDLE:
				enabled = false;
			break;
		case POS0:
			encoder.reset();
			m_sally = SALLYPORT.POS1;
			break;
			
		case POS1:
				torcan.SetDrive(-0.5, 0.5);
				if(encoder.getDistance()<sallyPortInitBack){
					m_sally = SALLYPORT.POS2;
					encoder.reset();
					torcan.SetDrive(0,0);
			}
			break;
			
		case POS2:
				haltDrive(0.5);
				setDegrees(sallyPort);
				if(siegeOnTarget(2))
					m_sally = SALLYPORT.POS3;
			break;
		case POS3:
			if(siegeOnTarget(2)) {
				m_sally = SALLYPORT.POS4;
				encoder.reset();
			}
			break;
		
		case POS4:
				if(encoder.getDistance()>sallyPortBack-10){
					torcan.SetDrive(-0.5, 0.5);
					setDegrees(-5);
				}
				else
					torcan.SetDrive(-0.2, 0.2);
				if(encoder.getDistance()<sallyPortBack){
					m_sally = SALLYPORT.POS5;
					encoder.reset();
					haltDrive(0.5);
					torcan.SetDrive(0,0);
					gyro.reset();
				}
			break;
		case POS5:
			turnToTheta(-20);
			if(gyro.getAngle()<340&&gyro.getAngle()>90){
				m_sally = SALLYPORT.POS6;
				torcan.SetDrive(0,0);
			}
				break;
			
		case POS6:
			turnToTheta(350);
			if(gyro.getAngle()>348){
			torcan.SetDrive(0,0);
			m_sally = SALLYPORT.POS7;
			}
			break;
		case POS7:
			torcan.SetDrive(0.5,-0.5);
			if(encoder.getDistance()>sallyPortDist){
			m_sally = SALLYPORT.IDLE;
			encoder.reset();
			torcan.SetDrive(0,0);
			enabled=false;
			}
			break;
		}

		switch(m_states) {
		case IDLE:
				enabled = false;
			break;
		case POS1: //driving against and lower arm
			setDegrees(drawbridgeTop);
			if(siegeOnTarget(2)){
				m_states = DRAWBRIDGE.POS2;
				encoder.reset();
			}
			break;
		case POS2:
				torcan.SetDrive(-0.5,0.5);
				setDegrees(encoder.getDistance()*drawbridgeConstant+drawbridgeTop);
				if(encoder.getDistance()<drawbridgeBack){
					encoder.reset();
					haltDrive(0.5);
					m_states = DRAWBRIDGE.POS3;
					torcan.SetDrive(0, 0);
				}
				break;
			
		case POS3:
			setDegrees(drawbridgeBot);
			haltDrive(0.5);
			if(siegeOnTarget(2)){
			m_states = DRAWBRIDGE.POS4;
			}
			break;

		case POS4:
				encoder.reset();
				m_states = DRAWBRIDGE.POS5;
			break;

		case POS5:
				//YOU HAVE REACHED THE END
				torcan.SetDrive(0.5, -0.5);
				if(encoder.getDistance()>drawbridgeArmUp){
					setDegrees(0);
				}
				if(encoder.getDistance()>drawbridgeDist){
				m_states = DRAWBRIDGE.IDLE;
				torcan.SetDrive(0.0, -0.0);
				enabled = false;
				}
			break;
//			}
		}
	}
	public void haltDrive(double p){
		m_distance = encoder.getDistance();
		m_speed = -p*m_distance;
		torcan.SetDrive(m_speed, -1*m_speed);
	}
	public void turnToTheta(double desiredAngle){
		//TURNS TO AN ANGLE
		//(-speed,-speed) = Right
		//(+speed,+speed) = Left
		targetAngle = (desiredAngle+360)%360; // Modulus Sucks!!! -50%360=310!!!!
		error = gyro.getAngle() - targetAngle;
		if(Math.abs(error)>180){
			if(error>0){
				error-=180;
				error*=-1;
			}
			else{
				error+=180;
				error*=-1;
			}
		}
			if(error>0)
				turnSpeed=Math.min(0.6, error*turnP);
			else
				turnSpeed=Math.max(error*turnP, -0.6);
			torcan.SetDrive(turnSpeed, turnSpeed);
			System.out.println("Angle: "+gyro.getAngle());
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
		siegeTalon.set(drawbridgeTop+130);
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

