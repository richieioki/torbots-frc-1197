package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

public class TorAuto {

	//TODO some digital input read to get which auto we are running

	//NOTES Need to calculate distance that we need to drive and angles to turn.  
	//ALSO we need to start to estimate which sensors are being executed.  

	AHRS m_ahrs;

	public TorAuto(AHRS ahrs) {
		m_ahrs = ahrs;
	}
	
	
	//starting positions
	//postion 0, 1 , 2 , 3

	//8 defences, but not all 8 need different autos

	public void AUTO_DrawBridge() {

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
	}
}