package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TorAuto {
 //
	private Encoder m_encoder;
	private Joystick auto_input;
	private TorDrive m_drive;
	private TorCAN m_cans;
	private SendableChooser chooser;
	public static final double GEAR_RATIO = 56.0f; //ticks per inch
	public double encoderDistance = 0;
	public double targetDistance = 0; //distance we need to travel
	public CANTalon R1, R2, R3, L1, L2, L3;
	
	
	
	private int lane, defense;
	
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
	public void initialize() {
		
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
			defense = 1;
		} else if(auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 2;
		} else if(!auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 3;
		} else if(auto_input.getRawButton(5) && auto_input.getRawButton(6) && 
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 4;
		} else if(!auto_input.getRawButton(5) && auto_input.getRawButton(6) &&
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 5;
		} else if(auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 6;
		} else if(!auto_input.getRawButton(5) && !auto_input.getRawButton(6) &&
				!auto_input.getRawButton(7) && auto_input.getRawButton(8)) {
			defense = 7;
		} else if(auto_input.getRawButton(5) && auto_input.getRawButton(6) && 
				auto_input.getRawButton(7) && !auto_input.getRawButton(8)) {
			defense = 8;
		} else {
			DriverStation.reportError("Incorrect Defense", false);
		}
		
		SmartDashboard.putNumber("Lane selected", lane);
		SmartDashboard.putNumber("Defense selected", defense);
	}
	public void move(){
		encoderDistance = m_encoder.getDistance();
		while(encoderDistance < targetDistance){
			//drive
			m_cans.SetDrive(0.5, 0.5);
			m_encoder.getDistance();
		}
		m_cans.SetDrive(0.0, 0.0);
	}
	
	public void RoughTerrain() {
		//hit at full power
		move();
		
	}
	
	public void Moat() {
		//use about 75 - 80 percent power, with light frame
	}
	
	public void Rampart() {
		
	}
	
	
	//starting positions
	//postion 0, 1 , 2 , 3

	//8 defences, but not all 8 need different autos

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
	distance=3.1416*8*2.54*m_encoder.getRaw();
	
	return distance;
	}
}