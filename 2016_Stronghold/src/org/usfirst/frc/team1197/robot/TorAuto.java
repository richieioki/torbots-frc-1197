package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TorAuto {
 //
	private Encoder m_encoder;
	private Joystick auto_input;
	private TorDrive m_drive;
	private TorCAN m_cans;
	private Command autonomousCommand;
	private SendableChooser chooser;
	public static final double GEAR_RATIO = 56.0f; //ticks per inch
	public double encoderDistance = 0;
	public double targetDistance = 0; //distance we need to travel
	public CANTalon R1, R2, R3, L1, L2, L3;
	public int defense = 0;
	public int lane = 0;
	

	
	//TODO some digital input read to get which auto we are running

	//NOTES Need to calculate distance that we need to drive and angles to turn.  
	//ALSO we need to start to estimate which sensors are being executed.  

	//AHRS m_ahrs;

	public TorAuto(TorCAN cans) {
//		m_encoder.setDistancePerPulse(1/TorAuto.GEAR_RATIO);
		m_cans = cans;
		m_cans = new TorCAN(R1,R2,R3,L1,L2,L3);
	}
	
	/**
	 * Temp constructor to test the features
	 */
	public TorAuto(Joystick cypress) {
		auto_input = cypress;
		chooser = new SendableChooser();
	}
	
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
	
	public void move(){
		encoderDistance = m_encoder.getDistance();
		targetDistance=75;
		while(encoderDistance < targetDistance){
			//drive
			m_cans.SetDrive(0.5, 0.5);
			m_encoder.getDistance();
		}
		m_cans.SetDrive(0.0, 0.0);
		m_encoder.reset();
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
			move();
		}		
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 2){
			move();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 3){
			move();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 4){
			move();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 5){
			move();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 6){
			move();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 7){
			move();
		}
		else if(laneAndDefense[0] == 1 && laneAndDefense[1] == 8){
			move();
		}
		
		//position 2
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 1){
			move();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 2){
			move();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 3){
			move();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 4){
			move();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 5){
			move();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 6){
			move();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 7){
			move();
		}
		else if(laneAndDefense[0] == 2 && laneAndDefense[1] == 8){
			move();
		}
		
		//position 3
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 1){
			move();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 2){
			move();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 3){
			move();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 4){
			move();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 5){
			move();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 6){
			move();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 7){
			move();
		}
		else if(laneAndDefense[0] == 3 && laneAndDefense[1] == 8){
			move();
		}
		
		//position 4
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 1){
			move();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 2){
			move();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 3){
			move();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 4){
			move();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 5){
			move();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 6){
			move();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 7){
			move();
		}
		else if(laneAndDefense[0] == 4 && laneAndDefense[1] == 8){
			move();
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
	public double getDistance(){
	double distance=0;
	distance=(3.1416*8*m_encoder.getRaw())/GEAR_RATIO;
	
	return distance;
	}
}