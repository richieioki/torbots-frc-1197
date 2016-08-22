package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;


import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class TorSiege
{
	private CANTalon siegeTalon;
	private TorOnTarget target;
	private Joystick siegeStick;
	private Joystick stick;
	private AnalogPotentiometer pot;
	private TorCAN torcan;
	private TorDrive drive;
	private TorIntake intakeSiege;
	private Encoder encoder;
	private AHRS gyro;
	public boolean enabled;
	public static final double SONAR = 10.0D;
	public DRAWBRIDGE m_states;
	private HALT m_halt;
	private double[] errorHistory = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
	private TorCamera camera;

	public static enum HALT
	{
		IDLE,  NULL,  POS1,  POS2;

		private HALT() {}
	}

	public static enum DRAWBRIDGE
	{
		POS0,  POS1,  POS2,  POS3,  POS4,  POS5,  POS6,  POS7,  IDLE,  NULL,  POS2half;

		private DRAWBRIDGE() {}
	}

	public static enum SALLYPORT
	{
		POS0,  POS1,  POS2,  POS3,  POS4,  POS5,  POS6,  POS7,  IDLE,  NULL;

		private SALLYPORT() {}
	}

	public static enum PORTCULLIS
	{
		IDLE,  POS1,  POS2,  POS3,  POS$,  POS5,  NULL,  POS1B;

		private PORTCULLIS() {}
	}

	public static enum CHEVEL
	{
		POS1,  POS2,  POS3,  IDLE,  NULL;

		private CHEVEL() {}
	}

	public SALLYPORT m_sally = SALLYPORT.IDLE;
	public PORTCULLIS m_port = PORTCULLIS.IDLE;
	public CHEVEL m_chev = CHEVEL.IDLE;
	private Solenoid shift;
	double ratio = 0.6799999999999999D;
	double armTop = 0.0D;
	double drawbridgeTop = 0.0D;
	double drawbridgeBot = 0.0D;
	double sallyPort = 0.0D;
	double chevelTop = 0.0D;
	double portcullisTop = 0.0D;
	double portcullisBot = 0.0D;
	double potChecker = 0.0D;
	double intakeVal1 = 0.0D;
	double intakeVal2 = 0.0D;
	double degrees;
	double setDegreesSlope;
	double readDegreesSlope;
	double setDegreesInter;
	double readDegreesInter;
	double m_speed;
	double m_distance;
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
	double degreeCommand;

	public TorSiege(CANTalon T1, Joystick stick2, AnalogPotentiometer pot, 
			TorCAN torcan, Solenoid shift, Joystick stick, TorIntake intakee, TorDrive drive, 
			Encoder encoder, AHRS gyro, TorCamera camera)
	{
		siegeTalon = T1;
		siegeStick = stick2;
		this.pot = pot;
		this.torcan = torcan;
		this.shift = shift;
		this.stick = stick;
		intakeSiege = intakee;
		this.drive = drive;
		this.encoder = encoder;
		this.gyro = gyro;
//		stick3 = stick3;
		this.camera = camera;

		calc();

		enabled = false;
		target = new TorOnTarget(siegeTalon, 2);
		m_states = DRAWBRIDGE.NULL;
		m_port = PORTCULLIS.NULL;
		m_chev = CHEVEL.NULL;
		m_sally = SALLYPORT.NULL;
		m_halt = HALT.POS1;
	}

	public boolean siegeOnTarget(int tolerance)
	{
		double currentAngle = getDegrees();
		double setpointDegrees = readDegreesSlope * siegeTalon.getSetpoint() + readDegreesInter;
		if ((currentAngle > setpointDegrees - tolerance) && (currentAngle < setpointDegrees + tolerance)) {
			return true;
		}
		return false;
	}

	public boolean siegeOnTargetRaw(int tolerance)
	{
		int rawValue = siegeTalon.getAnalogInRaw();
		if ((rawValue > siegeTalon.getSetpoint() - tolerance) && 
				(rawValue < siegeTalon.getSetpoint() + tolerance)) {
			return true;
		}
		return false;
	}

	public void setDegrees(double degrees)
	{
		this.degrees = degrees;
		siegeTalon.set(setDegreesSlope * degrees + setDegreesInter);
	}

	public double getDegrees()
	{
		return readDegreesSlope * siegeTalon.getAnalogInRaw() + readDegreesInter;
	}

	public void calc()
	{
		turnP = 0.03D;
		degreesTop = 50.6D;
		degreesBot = -70.3D;
		drawbridgeBack = -39D;
		sallyPortInitBack = -6.0D;
		sallyPortBack = -50.0D;
		chevelBack = -8.0D;
		chevelArmUp = 13.0D;
		chevelDist = 80.0D;
		sallyPortDist = 116.0D;
		drawbridgeArmUp = 12.0D;
		drawbridgeDist = 122.0D;

		bottomArm = 497;//543 practice //470 competition
		int rest = 841;//315 practice //813 competition

		setDegreesSlope = ((bottomArm - rest) / (degreesBot - degreesTop));
		setDegreesInter = (rest - setDegreesSlope * degreesTop);
		readDegreesSlope = (1.0D / setDegreesSlope);
		readDegreesInter = (-setDegreesInter / setDegreesSlope);

		siegeTalon.setSetpoint(armTop);
		drawbridgeTop = 34.0D; 
		drawbridgeBot = -56.0D;
		sallyPort = 0.0D;
		chevelTop = -47.0D;
		portcullisTop = 5.0D;
		portcullisBot = -69.0D;
		intakeVal1 = -48.0D; //-52 comp //-45proto
		intakeVal2 = 54.0; //-42 comp //-35proto
		siegeTalon.setSetpoint(rest);
		drawbridgeConstant = ((drawbridgeTop - drawbridgeBot) / (-1.0D * drawbridgeBack));
		degreeCommand = 0.0;
	}

	public void PID()
	{
		siegeTalon.enable();
		int rest = siegeTalon.getAnalogInRaw();
		siegeTalon.set(rest);
		siegeTalon.setSetpoint(rest);
	}

	public double potGet()
	{
		double potValue = getDegrees();
		return potValue;
	}

	public void potTest()
	{
		if (siegeStick.getRawButton(8)) {
			setDegrees(drawbridgeTop);
		}
	}

	public void intakeTele()
	{
		if(stick.getRawButton(9)){
			setDegrees(intakeVal1);
		}
		if(stick.getRawButton(10)){
			setDegrees(intakeVal2);
		}
	}

	public long checkTime(int wait)
	{
		long time = System.currentTimeMillis();
		long endTime = System.currentTimeMillis() + wait;
		long difference = endTime - time;
		return difference;
	}

	public void SiegeArmUpdate()
	{
		if (!enabled)
		{
			if (stick.getRawButton(1))
			{
				DrawBridge();
			}
			else if (stick.getRawButton(2))
			{
				SallyPort();
			}
			else if (stick.getRawButton(3))
			{
				Cheve();
			}
			else if (stick.getRawButton(4))
			{
				Portcullis();
			}
			else if (stick.getY() < -0.025D) //siegeStick.getY() -> stick.getY()
			{
				siegeTalon.setProfile(1);
				siegeTalon.set(stick.getY() * 30.0D + siegeTalon
						.getAnalogInRaw());
			}
			else if (stick.getY() > 0.025D)
			{
				siegeTalon.setProfile(1);
				siegeTalon.set(stick.getY() * 30.0D + siegeTalon
						.getAnalogInRaw());
			}
			else
			{
				siegeTalon.setProfile(0);
			}
//			if ((siegeStick.getRawButton(5)) && (torcan.m_state != TorCAN.DRIVE_STATE.PIVOTING))
//			{
//				if ((torcan.m_state != TorCAN.DRIVE_STATE.LOWGEAR) && (torcan.m_state != TorCAN.DRIVE_STATE.OFF))
//				{
//					shift.set(false);
////					torcan.lowGear();
//					torcan.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
//				}
//			}
//			else if ((torcan.m_state != TorCAN.DRIVE_STATE.HIGHGEAR) && (torcan.m_state != TorCAN.DRIVE_STATE.OFF))
//			{
//				shift.set(true);
////				torcan.highGear();
//				torcan.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
//			}
		}
		else
		{
			update();
		}
	}

	public void Cheve()
	{
		if (!enabled)
		{
			drive.offGear();
			torcan.m_state = TorCAN.DRIVE_STATE.OFF;
			enabled = true;
			m_chev = CHEVEL.POS1;
		}
	}

//	public void highGear()
//	{
//		shift.set(true);
//	}
//
//	public void lowGear()
//	{
//		shift.set(false);
//	}

	public void Portcullis()
	{
		if (!enabled)
		{
			drive.offGear();
			torcan.m_state = TorCAN.DRIVE_STATE.OFF;
			enabled = true;
			m_port = PORTCULLIS.POS1B;
		}
	}

	public void SallyPort()
	{
		if (!enabled)
		{
			drive.offGear();
			torcan.m_state = TorCAN.DRIVE_STATE.OFF;
			enabled = true;
			m_sally = SALLYPORT.POS0;
		}
	}

	public void DrawBridge()
	{
		if (!enabled)
		{
			drive.offGear();
			torcan.m_state = TorCAN.DRIVE_STATE.OFF;
			enabled = true;
			m_states = DRAWBRIDGE.POS0;
		}
	}

	private void update()
	{
		if (stick.getRawButton(8))
		{
			m_states = DRAWBRIDGE.IDLE;
			m_sally = SALLYPORT.IDLE;
			m_chev = CHEVEL.IDLE;
			m_port = PORTCULLIS.IDLE;
			enabled = false;
			intakeSiege.stopArmTalon();
		}
		if ((m_port != PORTCULLIS.NULL) && (m_port != PORTCULLIS.IDLE)) {
			switch (m_port)
			{
			case NULL: 
				break;
			case IDLE: 
				torcan.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
				drive.highGear();
				enabled = false;
				m_port = PORTCULLIS.NULL;
				break;
			case POS1B: 
				torcan.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
				drive.lowGear();
				encoder.reset();
				m_port = PORTCULLIS.POS1;
				break;
			case POS1: 
				torcan.SetDrive(-0.2D, 0.2D);
				if (encoder.getDistance() < -3.0D)
				{
					torcan.SetDrive(0.0D, 0.0D);
					encoder.reset();
					m_port = PORTCULLIS.POS2;
				}
				break;
			case POS2: 
				intakeSiege.portcullis();
				haltDrive(0.5D);
				setDegrees(portcullisTop);
				if (siegeOnTarget(5))
				{
					intakeSiege.portStop();
					encoder.reset();
					m_port = PORTCULLIS.POS3;
				}
				break;
			case POS3: 
				torcan.SetDrive(0.6D, -0.6D);
				if (encoder.getDistance() > 16.0D) {
					setDegrees(portcullisTop + 40.0D);
				}
				if (encoder.getDistance() > 70.0D)
				{
//					setDegrees(sallyPort);
//					if(siegeOnTarget(10)){
					torcan.SetDrive(0.0D, 0.0D);
					enabled = false;
					m_port = PORTCULLIS.IDLE;
//					}
				}
				break;
			}
		} else if ((m_chev != CHEVEL.NULL) && (m_chev != CHEVEL.IDLE)) {
			switch (m_chev)
			{
			case NULL: 
				break;
			case IDLE: 
				torcan.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
				drive.highGear();
				enabled = false;
				m_chev = CHEVEL.NULL;
				break;
			case POS1: 
				torcan.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
				drive.lowGear();
				setDegrees(chevelTop);
				if (siegeOnTarget(4))
				{
					m_chev = CHEVEL.POS2;
					encoder.reset();
				}
				break;
			case POS2: 
				torcan.SetDrive(-0.35D, 0.35D); //ADJUST
				setDegrees(chevelTop - 4.0D);
				if (encoder.getDistance() < -5.0D)
				{
					haltDrive(0.5D);
					torcan.SetDrive(0.0D, 0.0D);
					m_chev = CHEVEL.POS3;
					encoder.reset();
				}
				break;
			case POS3: 
				torcan.SetDrive(0.5D, -0.5D);
				if (encoder.getDistance() > chevelArmUp) {
					setDegrees(sallyPort);
				}
				if (encoder.getDistance() > chevelDist)
				{
					torcan.SetDrive(0.0D, 0.0D);
					enabled = false;
					m_chev = CHEVEL.IDLE;
				}
				break;
			}
		} else if ((m_sally != SALLYPORT.IDLE) && (m_sally != SALLYPORT.NULL)) { 
			switch (m_sally)
			{
			case NULL: 
				break;
			case IDLE: 
				torcan.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
				drive.highGear();
				enabled = false;

				m_sally = SALLYPORT.NULL;
				break;
			case POS0: 
				enabled = true;
				torcan.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
				torcan.offGear();
				encoder.reset();
				gyro.reset();
				m_sally = SALLYPORT.POS1;
				break;
			case POS1: 
				torcan.offGear();
				enabled = true; //try in POS0?
				setDegrees(sallyPort);
				haltDrive(0.5D);
				if (siegeOnTarget(2))
				{
					torcan.SetDrive(0.0D, 0.0D);
					m_sally = SALLYPORT.POS3;
				}
				break;
			case POS3: 
				torcan.offGear();
				enabled = true;
				if (encoder.getDistance() > sallyPortBack + 10.0D)
				{
					torcan.SetDrive(-0.5D, 0.5D);
					setDegrees(-1.0D);
				}
				else
				{
					torcan.SetDrive(-0.2D, 0.2D);
				}
				if (encoder.getDistance() < sallyPortBack)
				{
					m_sally = SALLYPORT.POS4;
					encoder.reset();

					torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			case POS4: 
				enabled = true;

				m_sally = SALLYPORT.POS5;

				break;
			case POS5: 
				enabled = true;
				torcan.offGear();
				if (turnToTheta(-10.0))//turnToTheta(-20.0) //(gyro.getAngle() < 342.0D) && (gyro.getAngle() > 90.0D)
				{
					m_sally = SALLYPORT.POS6;
					torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			case POS6: 
				enabled = true;
				torcan.offGear();
				if (turnToTheta(0))//turnToTheta(0) //gyro.getAngle() > 353.0 || (gyro.getAngle() > 0 && gyro.getAngle() < 30)
				{
					torcan.SetDrive(0.0D, 0.0D);
					m_sally = SALLYPORT.POS7;
				}
				break;
			case POS7: 
				enabled = true;
				torcan.lowGear();
				torcan.SetDrive(1, -1);
				if (encoder.getDistance() > sallyPortDist)
				{
					m_sally = SALLYPORT.IDLE;
					encoder.reset();
					enabled = false;
					torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			}
		} else if ((m_states != DRAWBRIDGE.NULL) && (m_states != DRAWBRIDGE.IDLE)) {
			switch (m_states)
			{
			case NULL: 
				break;
			case IDLE: 
				torcan.m_state = TorCAN.DRIVE_STATE.HIGHGEAR;
				drive.highGear();
				enabled = false;
				m_states = DRAWBRIDGE.NULL;
				break;
			case POS0: 
				torcan.m_state = TorCAN.DRIVE_STATE.LOWGEAR;
				drive.lowGear();
				encoder.reset();
				intakeSiege.armtalon();
				m_states = DRAWBRIDGE.POS1;
			case POS1: 
				setDegrees(drawbridgeTop);
				haltDrive(0.5D);
				if (siegeOnTarget(4)) {
					m_states = DRAWBRIDGE.POS2;
				}
				break;
			case POS2: 
				if (encoder.getDistance() > drawbridgeBack + 10.0D)
				{
					torcan.SetDrive(-0.5D, 0.5D);
				}
				else
				{
					torcan.SetDrive(-0.2D, 0.2D);
				}
//				setDegrees(encoder.getDistance() * drawbridgeConstant + drawbridgeTop);
				degreeCommand = 20*Math.log(-((encoder.getDistance()/drawbridgeBack)-1.001))+36;
				if(degreeCommand < -56){
					degreeCommand = -56;
				}
				setDegrees(degreeCommand);
				if (encoder.getDistance() < drawbridgeBack)
				{
					intakeSiege.stopArmTalon();
					encoder.reset();
					haltDrive(0.5D);
					m_states = DRAWBRIDGE.POS3;
					torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			case POS3: 
				setDegrees(drawbridgeBot);
				haltDrive(0.5D);
				if (siegeOnTarget(2)) {
					m_states = DRAWBRIDGE.POS4;
				}
				break;
			case POS4: 
				encoder.reset();
				m_states = DRAWBRIDGE.POS5;
				break;
			case POS5: 
				torcan.SetDrive(0.5D, -0.5D);
				if (encoder.getDistance() > drawbridgeArmUp) {
					setDegrees(0.0D);
				}
				if (encoder.getDistance() > drawbridgeDist)
				{
					m_states = DRAWBRIDGE.IDLE;
					enabled = false;
					torcan.SetDrive(0.0D, -0.0D);
				}
				break;
			}
		}
	}

	public void haltDrive(double p)
	{
		m_distance = encoder.getDistance();
		m_speed = (-p * m_distance);
		torcan.SetDrive(m_speed, -1.0D * m_speed);
	}

	public void turnToReference() {}

	public boolean turnToTheta(double desiredAngle)
	{
		targetAngle = ((desiredAngle + 360.0D) % 360.0D);

		error = (gyro.getAngle() - targetAngle);
		if (Math.abs(error) > 180.0D) {
			if (error > 0.0D) {
				error -= 360.0D;
			} else {
				error += 360.0D;
			}
		}
//		System.out.println("Error " + error);
//		SmartDashboard.getNumber("Gyro", gyro.getAngle());
		
		if (error > 0.0D) {
			turnSpeed = Math.min(0.6D, error * 0.05);
		} else {
			turnSpeed = Math.max(error * 0.05, -0.6D);
		}
		torcan.SetDrive(turnSpeed, turnSpeed);
		if (Math.abs(error) > 1) {
			return false;
		}
		torcan.SetDrive(0, 0);
		return true;
	}

	public boolean turnToShoot(double desiredAngle)
	{
		targetAngle = ((desiredAngle + 360.0D) % 360.0D);

		error = (gyro.getAngle() - targetAngle);
		if (Math.abs(error) > 180.0D) {
			if (error > 0.0D) {
				error -= 360.0D;
			} else {
				error += 360.0D;
			}
		}
		
		for(int i=19; i>0; i--){
			errorHistory[i] = errorHistory[i - 1];
		}
		errorHistory[0] = error;
		
//		System.out.println("Error " + error);
		
		if (error > 0.0D) {
			turnSpeed = Math.min(4.0D, error * 0.04 * 12.0); //error * turnP * 12.0  - (stdev(errorHistory) * 1.0)
		} else {
			turnSpeed = Math.max(error * 0.04 * 12.0D, -4.0D); //originally -4.0 and 4.0  - (stdev(errorHistory) * 1.0)
		} 
		
		if (Math.abs(turnSpeed) < 1.7) { //originally 3.0
			turnSpeed = (1.7 * (turnSpeed / Math.abs(turnSpeed)));
		}
		
		
		
		if (Math.abs(error) < 0.75D ){
			turnSpeed = 0.0D;
		}
		
		torcan.SetDrive(turnSpeed, turnSpeed);
		
		if (Math.abs(error) > 0.75D || stdev(errorHistory) > (0.75/20)) {
			return false;
		}
		
		return true;
	}
	public double stdev(double[] data){
		double mean = 0;
		for(int i=0; i<data.length; i++){
			mean += data[i];
		}
		mean = mean / data.length;
	    
		double variance = 0;
	    for(int i=0; i<data.length; i++){
			variance += (data[i] - mean)*(data[i] - mean);
		}
	    variance = variance / data.length;
		
	    return Math.sqrt(variance);
	}

	public void SiegeArmDown()
	{
		siegeTalon.set(chevelTop);
	}

	public void SiegeArmUp()
	{
		siegeTalon.set(chevelTop - 100.0D);
	}

	public void drawbridgeTop()
	{
		siegeTalon.set(drawbridgeTop);
	}

	public void drawbridgeMid()
	{
		siegeTalon.set(drawbridgeTop + 130.0D);
	}

	public void drawbridgeBot()
	{
		siegeTalon.set(drawbridgeBot);
	}

	public void sally()
	{
		siegeTalon.set(sallyPort);
	}

	public void portBot()
	{
		siegeTalon.set(portcullisBot);
	}

	public void portTop()
	{
		siegeTalon.set(portcullisTop);
	}

	public void stopArm()
	{
		siegeTalon.set(0.0D);
	}

	public void reset()
	{
//		System.out.println("!!!!!!!!!!!RESET!!!!!!!!!!!!");
	}
}
