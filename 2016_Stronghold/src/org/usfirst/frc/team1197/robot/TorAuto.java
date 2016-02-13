package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.kauailabs.navx.frc.AHRS;

public class TorAuto {
 //
	private Solenoid shift;
	private Encoder m_encoder;
	private Joystick auto_input;
	private TorCAN m_cans;
	public static final double GEAR_RATIO = 56.0f; //ticks per inch
	public double encoderDistance = 0;
	public static final double TARGET_DISTANCE = 45; //distance we need to travel
	public CANTalon R1, R2, R3, L1, L2, L3, T1;
	public int defense = 0;
	public int lane = 0;
	private AHRS gyro;
	private TorSiege siege;
	private TorIntake intake;
	private Joystick stick;
	double armTop = siege.potGet(); //adjust pot value
	double drawbridgeTop = armTop - 151;
	double drawbridgeBot = armTop - 655;
	double sallyPort = armTop - 368;
	double chevelTop = armTop - 476;
	double portcullis = 0;
	
	//TODO some digital input read to get which auto we are running

	//NOTES Need to calculate distance that we need to drive and angles to turn.  
	//ALSO we need to start to estimate which sensors are being executed.  

	//AHRS m_ahrs;

	public TorAuto() {
//		m_encoder.setDistancePerPulse(1/TorAuto.GEAR_RATIO);

	}
	
	/**
	 * Temp constructor to test the features
	 */
	public TorAuto(Joystick cypress, Joystick stick, AHRS ahrs, Encoder encoder, TorCAN cans, Solenoid shift,TorSiege siege, 
			 TorIntake intake) {
		this.stick = stick;
		this.shift = shift;
		auto_input = cypress;
		auto_input = new Joystick(2);
		m_encoder = encoder;
		gyro = ahrs;
		m_cans = cans;
		this.siege = siege;
		this.intake = intake;
	}
	
//	boolean firstOne = false;
//	boolean secondOne = true;
//	public boolean switchIt(boolean first, boolean second){
//		firstOne = second;
//		secondOne = first;
//		return first;
//	}
	
	//temp function to test chooser
	public int[] initialize() {
		int laneDefense[] = new int[2];
		if(!auto_input.getRawButton(1) && auto_input.getRawButton(2)) {
			lane = 1;
		} else if(!auto_input.getRawButton(1) && !auto_input.getRawButton(2)) {
			lane = 3;
		} else if(!auto_input.getRawButton(2)) {
			lane = 2;
		} else if(!auto_input.getRawButton(3)) {
			lane = 4;
		} else {
			DriverStation.reportError("You have selected the wrong lane!", false);
		}
		
		if(!auto_input.getRawButton(5) && auto_input.getRawButton(6) &&
				auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 1; //rock wall
		} else if(auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 2; //cheveldefrise
		} else if(!auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 3; //rampart
		} else if(auto_input.getRawButton(5) && auto_input.getRawButton(6) && 
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 4; //moat
		} else if(!auto_input.getRawButton(5) && auto_input.getRawButton(6) &&
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
		    defense = 5; //rough terrain
		} else if(auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 6; //draw bridge
		} else if(!auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 7; //sally port
		} else if(auto_input.getRawButton(5) && auto_input.getRawButton(6) && 
				auto_input.getRawButton(7) && !auto_input.getRawButton(8)) {
			defense = 8; //portcullis
		} else {
			DriverStation.reportError("Incorrect Defense", false);
		}
		
		SmartDashboard.putNumber("Lane selected", laneDefense[0]);
		SmartDashboard.putNumber("Defense selected", laneDefense[1]);
		laneDefense[0] = lane;
		laneDefense[1] = defense;
		return laneDefense;
	}
	
	public void turnToTheta(double theta){
		double gyroAngle = gyro.getAngle();
		if(gyroAngle > theta){
			m_cans.SetDrive(-0.5, -0.5);
		}
		else if(gyroAngle < theta){
			m_cans.SetDrive(0.5, 0.5);
		}
	}
	
	public void move(){
	
		m_encoder.setDistancePerPulse(1/GEAR_RATIO);
		encoderDistance = m_encoder.getDistance();
		while(encoderDistance < TARGET_DISTANCE){
			//drive
			shift.set(true);
			m_cans.SetDrive(0.2, -0.2);
			m_encoder.getDistance();
		}
		shift.set(false);
		m_cans.SetDrive(0.0, 0.0);
		m_encoder.reset();
	}
	
	public void Moat(){
		//m_encoder.setDistancePerPulse(1/GEAR_RATIO);
		encoderDistance = m_encoder.getDistance();
		shift.set(true);
		m_cans.SetDrive(0.6, -0.6);
		double time = 5.0; 
		Timer.delay(time);
		shift.set(false);
		//m_cans.SetDrive(-0.6, 0.6);
		m_cans.SetDrive(0.0, 0.0);
	
	}
	public void RoughTerrain(){
		encoderDistance = m_encoder.getDistance();
		shift.set(true);
		m_cans.SetDrive(0.6, -0.6);
		double time = 4.0; 
		Timer.delay(time);
		shift.set(false);
		//m_cans.SetDrive(-0.6, 0.6);
		m_cans.SetDrive(0.0, 0.0);
	}
	public void RockWall(){
		encoderDistance = m_encoder.getDistance();
		shift.set(true);
		m_cans.SetDrive(0.6, -0.6);
		double time = 4.0; 
		Timer.delay(time);
		shift.set(false);
		//m_cans.SetDrive(-0.6, 0.6);
		m_cans.SetDrive(0.0, 0.0);
	}
	public void Ramparts(){
		encoderDistance = m_encoder.getDistance();
		shift.set(true);
		m_cans.SetDrive(0.6, -0.6);
		double time = 4.0; 
		Timer.delay(time);
		shift.set(false);
		//m_cans.SetDrive(-0.6, 0.6);
		m_cans.SetDrive(0.0, 0.0);
		this.turnToTheta(0);
	}
	
	
	public void DrawBridge(){ //ALWAYS CHANGE POT VALUE
		m_cans.SetDrive(0.5,-0.5); //BEFORE TESTING FIX THE POT VALUE 
		Timer.delay(2.4);
		if(siege.potGet() > drawbridgeTop){
			siege.SiegeArmUp();
			m_cans.SetDrive(0.0,0.0);
		}
		Timer.delay(0.5);
		m_cans.SetDrive(-0.5, 0.5);
		Timer.delay(1.7);
		if(siege.potGet() > drawbridgeBot){
			siege.SiegeArmUp();
			m_cans.SetDrive(0.0,0.0);
		}
		Timer.delay(0.5);
		m_cans.SetDrive(0.5, -0.5);
		Timer.delay(3);
		m_cans.SetDrive(0,0);
		
		
//		m_cans.SetDrive(0.6, -0.6);
//		double time = 1.25; 
//		Timer.delay(time);
//		shift.set(false);
		//m_cans.SetDrive(-0.6, 0.6);
	
//		while(sonar.getRangeInches()>15){
//			m_cans.SetDrive(0.6, -0.6);
//			Timer.delay(0.2);
//			shift.set(true);
//			if(sonar.getRangeInches()<15){
//				siege.drawbridgeSiege();
//			}
//			if(sonar.getRangeInches()<16 && sonar.getRangeInches()>14){
//				break;
//			}
//		}
		
	
		
//		m_cans.SetDrive(0.5, -0.5);
//		Timer.delay(3);
//		m_cans.SetDrive(0, 0);
		
//		while(sonar.getRangeInches() < 20){
//			m_cans.SetDrive(-0.6, 0.6);
//			if (sonar.getRangeInches() == 20){
//				break;
//			}
//		}
		
//		while(siege.potGet() > 245){ // while pot value is greater than 167
//			if(siege.potGet() < 715){
//				m_cans.SetDrive(0.5, -0.5); //drives forwards
//				Timer.delay(2);
//				if(siege.potGet() < 233){
//					Timer.delay(2);
//					siege.SiegeArmDown(); //actually goes up
//					Timer.delay(2);
//					siege.stopArm(); //stops arm
//				}
//				Timer.delay(2);
//				m_cans.SetDrive(0, 0); //stops drive
//				break;
//			}
//			else{
//				siege.SiegeArmUp();
//				m_cans.SetDrive(-0.5, 0.5); //drives backwards
//			}
////			siege.SiegeArmUp(); //actaully goes down
//		}


		
//		while(sonar.getRangeInches() > 60){
//		m_cans.SetDrive(0.6, -0.6);
//		}
//		m_cans.SetDrive(0.0, 0.0);
//		
	}
	public void ChevelDeFrise(){ //ALWAYS CHECK THE POT VALUE
		m_cans.SetDrive(0.4, -0.4);
		Timer.delay(3); //adjust
		m_cans.SetDrive(0,0);
		if(siege.potGet() > chevelTop){  	
			siege.SiegeArmUp();
		}
		Timer.delay(1.5);
		siege.stopArm();
		m_cans.SetDrive(-0.4, 0.4);
		Timer.delay(0.3);
		m_cans.SetDrive(0,0);

		m_cans.SetDrive(0.5, -0.5);
		Timer.delay(0.25);
		siege.SiegeArmDown();
		Timer.delay(2.6);
		siege.stopArm();
		m_cans.SetDrive(0, 0);
	}
	
	public void Portcullis(){
		if(siege.potGet()>100){
			m_cans.SetDrive(0.4, -0.4);
			siege.SiegeArmUp();
		}
		Timer.delay(3);
		if(siege.potGet() < 700){
			intake.portcullis();
			Timer.delay(0.25);
			siege.SiegeArmDown();
		}
		Timer.delay(1.5);
		m_cans.SetDrive(0.5, -0.5);
	}
	
	public void Sallyport(){
		m_cans.SetDrive(0.5,-0.5);
		Timer.delay(2);
		m_cans.SetDrive(0,0);
		if(siege.potGet() > sallyPort){
			siege.SiegeArmUp();
		}
		Timer.delay(0.75);
		siege.stopArm();
		m_cans.SetDrive(-0.4, 0.4);
		Timer.delay(2.15);
		m_cans.SetDrive(0,0);
		m_cans.SetDrive(0.5, 0.5);
		Timer.delay(0.25);
		m_cans.SetDrive(0,0);
		Timer.delay(0.2);
		m_cans.SetDrive(0.5, -0.5);
		Timer.delay(0.5);
		m_cans.SetDrive(-0.5,-0.5);
		Timer.delay(0.22);
		m_cans.SetDrive(0,0);
		m_cans.SetDrive(0.5,-0.5);
		Timer.delay(4);
		m_cans.SetDrive(0,0);
	}
	
	public void ModeChooser(){
		
		/* 1. rock wall
		 * 2. chevel de frise
		 * 3. rampart
		 * 4. moat
		 * 5. rough terrain
		 * 6. draw bridge
		 * 7. sally port
		 * 8. portcullis
		 */
		
		int laneAndDefense[] = initialize();
		
		System.out.println("lane: " + laneAndDefense[0]);
		System.out.println("defense: " + laneAndDefense[1]);
		
		//position 1
		if(laneAndDefense[0] == 1 && laneAndDefense[1] == 1){
			RockWall();
		}		
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 2){
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 3){
			Ramparts();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 4){
			Moat();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 5){
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 6){
			DrawBridge();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 7){
			Sallyport();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 8){
			Portcullis();
		}
		
		//position 2
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 1){
			System.out.println("Pos 2 Rock Wall");
			RockWall();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 2){
			System.out.println("Pos 2 Chevel De Frise");
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 3){
			System.out.println("Pos 2 Ramparts");
			Ramparts();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 4){
			System.out.println("Pos 2 Moat");
			Moat();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 5){
			System.out.println("Pos 2 Rough Terrain");
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 6){
			System.out.println("Pos 2 drawbridge");
			DrawBridge(); //adjust potentiometer
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 7){
			System.out.println("Pos 2 sallyport");
			Sallyport();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 8){
			System.out.println("Pos 2 portcullis");
			Portcullis();
		}
		
		//position 3
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 1){
			RockWall();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 2){
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 3){
			Ramparts();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 4){
			Moat();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 5){
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 6){
			DrawBridge();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 7){
			Sallyport();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 8){
			Portcullis();
		}
		
		//position 4
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 1){
			RockWall();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 2){
			ChevelDeFrise();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 3){
			Ramparts();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 4){
			Moat();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 5){
			RoughTerrain();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 6){
			DrawBridge();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 7){
			Sallyport();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 8){
			Portcullis();
		}
		
		else {
			return;
		}

	}
	
	
	//starting positions
	//postion 0, 1 , 2 , 3

	//8 defenses, but not all 8 need different autos

	/*public void AUTO_DrawBridge() {

		//Drawbridge	
		start position 1
		drive forward to defense ( X FEET)
		use seige arm to pull draw bridge down
		drive forward to shooting spot( Y FEET)

		Shoot()

		Turn around

		Start position 2 || 0
		drive forward to defense ( X FEET)
		use seige arm to pull down drawbridge
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around

		Start position 3
		drive forward to defense ( X FEET)
		use seige arm to pull down drawbridge
		drive forward to shooting spot ( Y FEET)

		Turn to angle (q degrees)

		Shoot ()

		Turn around
	}

	public void AUTO_DRIVE() {
		//Moat, Rough Terrain, Ramparts, Rock Wall
		Start position 2 || 0
		drive forward to defense ( X FEET)
		run over moat, rough terrain, ramparts, rock wall
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around

		//Moat, Rough Terrain, Ramparts, Rock Wall
		Start position 3
		drive forward to defense ( X FEET)
		run over moat, rough terrain, ramparts, rock wall
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around 

		Start position 1
		drive forward to defense ( X FEET)
		run over moat, rough terrain, ramparts, rock wall
		drive forward to shooting spot ( Y FEET)

		Shoot ()

		Turn around

	}

	public void AUTO_PORTCULLIS() {

		//Portcullis
		Start position 1
		drive forward to defense ( X FEET)
		use seige arm to pull up portcullis
		drive forward to shooting spot ( Y FEET)

		Shoot ()

		Turn around

		Start position 2 || 0
		drive forward to defense ( X FEET)
		use seige arm to pull up portcullis
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around

		Start position 3
		drive forward to defense ( X FEET)
		use seige arm to pull up portcullis
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around
	}

	public void AUTO_ChevalDeFrise() {
		//Cheval de Frise
		Start position 1
		drive forward to defense ( X FEET)
		use seige arm to pull down cheval de frise
		drive forward to shooting spot ( Y FEET)

		Shoot ()

		Turn around

		Start position 2 || 0
		drive forward to defense ( X FEET)
		use seige arm to pull down cheval de frise
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around		
	}



	public void AUTO_SALLYPORT() {		

		Start position 1
		drive forward to defense ( X FEET)
		use seige arm to pull to the side sally port
		drive forward to shooting spot ( Y FEET)

		Shoot ()

		Turn around

		Start positon 2 || 0
		drive forward to defense ( X FEET)
		use seige arm to pull to the side sally port
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around	

		//Sally Port
		Start position 3
		drive forward to defense ( X FEET)
		use seige arm to pull to the side sally port
		drive forward to shooting spot ( Y FEET)

		Turn to angle (z degrees)

		Shoot ()

		Turn around
	}*/
//	public double getDistance(){
//	double distance=0;
//	distance=(3.1416*8*m_encoder.getRaw())/GEAR_RATIO;
//	
//	return distance;
//	}
}