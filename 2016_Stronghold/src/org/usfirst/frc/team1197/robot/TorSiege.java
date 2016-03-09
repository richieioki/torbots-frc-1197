package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Ultrasonic;
import java.io.PrintStream;

public class TorSiege
{
	private CANTalon siegeTalon;
	private TorOnTarget target;
	private Joystick siegeStick;
	private Joystick stick, stick3;
	private AnalogPotentiometer pot;
	private Ultrasonic sonar;
	private TorCAN torcan;
	private TorDrive drive;
	private TorIntake intakeSiege;
	private Encoder encoder;
	private AHRS gyro;
	public boolean enabled;
	public static final double SONAR = 10.0D;
	private DRAWBRIDGE m_states;
	private HALT m_halt;
	public TorTeleop tele;
	public static enum HALT{
		IDLE, NULL, POS1, POS2;
		
		private HALT() {}
	}
	public static enum DRAWBRIDGE
	{
		POS0,  POS1,  POS2,  POS3,  POS4,  POS5,  POS6,  POS7,  IDLE,  NULL;

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
	private double endTime = 0.0D;
	private double sallyTime;
	private double startTime;
	private double chevTime;
	private double portTime;
	double ratio = 0.6799999999999999D;
	double armTop = 0.0D;
	double drawbridgeTop = 0.0D;
	double drawbridgeBot = 0.0D;
	double sallyPort = 0.0D;
	double chevelTop = 0.0D;
	double portcullisTop = 0.0D;
	double portcullisBot = 0.0D;
	double potChecker = 0.0D;
	double intakeVal = 0.0D;
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

	public TorSiege(CANTalon T1, Joystick stick2, AnalogPotentiometer pot, 
			TorCAN torcan, Solenoid shift, Joystick stick, TorIntake intakee, TorDrive drive, 
			Encoder encoder, AHRS gyro, Joystick stick3)
	{
		this.siegeTalon = T1;
		this.siegeStick = stick2;
		this.pot = pot;
		this.torcan = torcan;
		this.shift = shift;
		this.stick = stick;
		this.intakeSiege = intakee;
		this.drive = drive;
		this.encoder = encoder;
		this.gyro = gyro;
		this.stick3 = stick3;

		calc();

		this.target = new TorOnTarget(this.siegeTalon, 2);
		this.m_states = DRAWBRIDGE.NULL;
		this.m_port = PORTCULLIS.NULL;
		this.m_chev = CHEVEL.NULL;
		this.m_sally = SALLYPORT.NULL;
		m_halt = HALT.POS1;
	}

	public boolean siegeOnTarget(int tolerance)
	{
		double currentAngle = getDegrees();
		double setpointDegrees = this.readDegreesSlope * this.siegeTalon.getSetpoint() + this.readDegreesInter;
		if ((currentAngle > setpointDegrees - tolerance) && (currentAngle < setpointDegrees + tolerance)) {
			return true;
		}
		return false;
	}

	public boolean siegeOnTargetRaw(int tolerance)
	{
		int rawValue = this.siegeTalon.getAnalogInRaw();
		if ((rawValue > this.siegeTalon.getSetpoint() - tolerance) && 
				(rawValue < this.siegeTalon.getSetpoint() + tolerance)) {
			return true;
		}
		return false;
	}

	public void setDegrees(double degrees)
	{
		this.degrees = degrees;
		this.siegeTalon.set(this.setDegreesSlope * degrees + this.setDegreesInter);
	}

	public double getDegrees()
	{
		return this.readDegreesSlope * this.siegeTalon.getAnalogInRaw() + this.readDegreesInter;
	}

	public void calc()
	{
		this.turnP = 0.05D;
		this.degreesTop = 50.6D;
		this.degreesBot = -70.3D;
		this.bottomArm = 439; //439
		this.drawbridgeBack = -34.0D;
		this.sallyPortInitBack = -6.0D;
		this.sallyPortBack = -50.0D;
		this.chevelBack = -8.0D;
		this.chevelArmUp = 13.0D;
		this.chevelDist = 80.0D;
		this.sallyPortDist = 116.0D;
		this.drawbridgeArmUp = 12.0D;
		this.drawbridgeDist = 110.0D;

		int rest = 797;  //797
		this.setDegreesSlope = ((this.bottomArm - rest) / (this.degreesBot - this.degreesTop));
		this.setDegreesInter = (rest - this.setDegreesSlope * this.degreesTop);
		this.readDegreesSlope = (1.0D / this.setDegreesSlope);
		this.readDegreesInter = (-this.setDegreesInter / this.setDegreesSlope);	

		this.siegeTalon.setSetpoint(this.armTop);
		this.drawbridgeTop = 29.0D;
		this.drawbridgeBot = -62.0D;
		this.sallyPort = 0.0D;
		this.chevelTop = -50.0D;
		this.portcullisTop = 5.0D;
		this.portcullisBot = -69.0D;
		this.intakeVal = -40.0D;
		this.siegeTalon.setSetpoint(rest);
		this.drawbridgeConstant = ((this.drawbridgeTop - this.drawbridgeBot) / (-1.0D * this.drawbridgeBack));
	}

	public void PID()
	{
		this.siegeTalon.enable();
		int rest = this.siegeTalon.getAnalogInRaw();
		this.siegeTalon.set(rest);
		this.siegeTalon.setSetpoint(rest);
	}

	public double potGet()
	{
		//    double potValue = getDegrees();
		double potValue = getDegrees();
		return potValue;
	}

	public void potTest()
	{
		if (this.siegeStick.getRawButton(8)) {
			setDegrees(this.drawbridgeTop);
		}
	}
	public void intakeTele()
	{
		if (this.stick.getRawButton(1))
		{
			this.setDegrees(intakeVal);
			//      this.siegeTalon.setSetpoint(this.intakeVal);
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
		if(!this.enabled) {
			if ((this.stick.getRawButton(3))) {
				DrawBridge();
			}
			else if ((this.stick.getRawButton(4))) {
				SallyPort();
			}
			else if ((this.stick.getRawButton(5))) {
				Cheve();
			}
			else if ((this.stick.getRawButton(6))) {
				Portcullis();
			}
			else {
				if (this.siegeStick.getY() < -0.025D)
				{
					this.siegeTalon.setProfile(1);
					this.siegeTalon.set(this.siegeStick.getY() * 30.0D + this.siegeTalon
							.getAnalogInRaw());
				}
				else if (this.siegeStick.getY() > 0.025D)
				{
					this.siegeTalon.setProfile(1);
					this.siegeTalon.set(this.siegeStick.getY() * 30.0D + this.siegeTalon
							.getAnalogInRaw());
				}
				else if (siegeStick.getRawButton(8)){
					setDegrees(-45.0);
				}
				else
				{
					this.siegeTalon.setProfile(0);
				}
			}

			if (this.siegeStick.getRawButton(1))
			{
				this.shift.set(true);
				System.out.println("!!!!!Solenoid Enabled!!!!!!");
			}
			else
				shift.set(false);
		} else {
			update();
		}
	}

	public void Cheve()
	{
		if (!this.enabled)
		{
			this.enabled = true;
			this.m_chev = CHEVEL.POS1;
		}
	}

	public void highGear()
	{
		this.shift.set(true);
	}

	public void lowGear()
	{
		this.shift.set(false);
	}

	public void Portcullis()
	{
		if (!this.enabled)
		{
			this.enabled = true;
			this.m_port = PORTCULLIS.POS1B;
		}
	}

	public void SallyPort()
	{
		if (!this.enabled)
		{
			this.enabled = true;
			this.m_sally = SALLYPORT.POS0;
		}
	}

	public void DrawBridge()
	{
		if (!this.enabled)
		{
			this.enabled = true;
			this.m_states = DRAWBRIDGE.POS0;
		}
		System.out.println("Enabled: " + this.enabled);
	}

	private void update()
	{
		if (this.stick.getRawButton(2))
		{
			this.m_states = DRAWBRIDGE.IDLE;
			this.m_sally = SALLYPORT.IDLE;
			this.m_chev = CHEVEL.IDLE;
			this.m_port = PORTCULLIS.IDLE;
			enabled = false;
		}

		if(m_port != PORTCULLIS.NULL && m_port != PORTCULLIS.IDLE) {

			switch (this.m_port)
			{
			case NULL: 
				break;
			case IDLE: 
				this.enabled = false;
				m_port = PORTCULLIS.NULL;
				break;
			case POS1B: 
				this.encoder.reset();
				this.m_port = PORTCULLIS.POS1;
				break;
			case POS1: 
				this.torcan.SetDrive(-0.2D, 0.2D);
				if (this.encoder.getDistance() < -3.0D)
				{
					this.torcan.SetDrive(0.0D, 0.0D);
					this.encoder.reset();
					this.m_port = PORTCULLIS.POS2;
				}
				break;
			case POS2: 
				this.intakeSiege.portcullis();
				haltDrive(0.5D);
				setDegrees(this.portcullisTop);
				if (siegeOnTarget(5))
				{
					this.intakeSiege.portStop();
					this.encoder.reset();
					this.m_port = PORTCULLIS.POS3;
				}
				break;
			case POS3: 
				this.torcan.SetDrive(0.6D, -0.6D);
				if (this.encoder.getDistance() > 16.0D) {
					setDegrees(this.portcullisTop + 40.0D);
				}
				if (this.encoder.getDistance() > 70.0D) {
					torcan.SetDrive(0,0);
					enabled = false;
					this.m_port = PORTCULLIS.IDLE;
				}
				break;
			}
		} else if(m_chev != CHEVEL.NULL && m_chev != CHEVEL.IDLE) {

			switch (this.m_chev)
			{
			case NULL: 
				break;
			case IDLE: 
				this.enabled = false;
				this.m_chev = CHEVEL.NULL;
				break;
			case POS1: 
				setDegrees(this.chevelTop);
				if (siegeOnTarget(2))
				{
					this.m_chev = CHEVEL.POS2;
					this.encoder.reset();
				}
				break;
			case POS2: 
				this.torcan.SetDrive(-0.35D, 0.35D);
				setDegrees(this.chevelTop - 4.0D);
				if (this.encoder.getDistance() < -8.0D)
				{
					haltDrive(0.5D);
					this.torcan.SetDrive(0.0D, 0.0D);
					this.m_chev = CHEVEL.POS3;
					this.encoder.reset();
				}
				break;
			case POS3: 
				this.torcan.SetDrive(0.5D, -0.5D);
				if (this.encoder.getDistance() > this.chevelArmUp) {
					setDegrees(45.0D);
				}
				if (this.encoder.getDistance() > this.chevelDist)
				{
					this.torcan.SetDrive(0.0D, 0.0D);
					enabled = false;
					this.m_chev = CHEVEL.IDLE;
				}
				break;
			}
		} else if(m_sally != SALLYPORT.IDLE && m_sally != SALLYPORT.NULL) {

			switch (m_sally)
			{
			case NULL: 
				break;
			case IDLE: 
				this.enabled = false;
				System.out.println("SallyEnabledFalse: " + this.enabled);
				this.m_sally = SALLYPORT.NULL;
				break;
			case POS0: 
				this.encoder.reset();
				this.gyro.reset();
				this.m_sally = SALLYPORT.POS1;
				break;
			case POS1: 
				this.enabled = true;
				setDegrees(this.sallyPort);
				haltDrive(0.5D);
				if (siegeOnTarget(2))
				{
					this.torcan.SetDrive(0.0D, 0.0D);
					this.m_sally = SALLYPORT.POS3;
				}
				break;
			case POS3: 
				this.enabled = true;
				if (this.encoder.getDistance() > this.sallyPortBack + 10.0D)
				{
					this.torcan.SetDrive(-0.5D, 0.5D);
					setDegrees(-5.0D);
				}
				else
				{
					this.torcan.SetDrive(-0.2D, 0.2D);
				}
				if (this.encoder.getDistance() < this.sallyPortBack)
				{
					this.m_sally = SALLYPORT.POS4;
					this.encoder.reset();

					this.torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			case POS4: 
				this.enabled = true;

				this.m_sally = SALLYPORT.POS5;

				break;
			case POS5: 
				this.enabled = true;
				turnToTheta(-20.0D);
				if ((this.gyro.getAngle() < 342.0D) && (this.gyro.getAngle() > 90.0D))
				{
					this.m_sally = SALLYPORT.POS6;
					this.torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			case POS6: 
				this.enabled = true;
				turnToTheta(353.0D);
				if (this.gyro.getAngle() > 355.0D)
				{
					this.torcan.SetDrive(0.0D, 0.0D);
					this.m_sally = SALLYPORT.POS7;
				}
				break;
			case POS7: 
				this.enabled = true;
				this.torcan.SetDrive(0.5D, -0.5D);
				if (this.encoder.getDistance() > this.sallyPortDist)
				{
					this.m_sally = SALLYPORT.IDLE;
					this.encoder.reset();
					enabled = false;
					this.torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			}
		} else if(m_states != DRAWBRIDGE.NULL && m_states != DRAWBRIDGE.IDLE) {

			switch (this.m_states)
			{
			case NULL: 
				break;
			case IDLE: 
				this.enabled = false;
				System.out.println("DrawbridgeEnabledFalse: " + this.enabled);
				this.m_states = DRAWBRIDGE.NULL;
				break;
			case POS0: 
				this.encoder.reset();
				this.m_states = DRAWBRIDGE.POS1;
			case POS1: 
				setDegrees(this.drawbridgeTop);
				haltDrive(0.5D);
				if (siegeOnTarget(3)) {
					this.m_states = DRAWBRIDGE.POS2;
				}
				break;
			case POS2: 
				this.torcan.SetDrive(-0.5D, 0.5D);
				setDegrees(this.encoder.getDistance() * this.drawbridgeConstant + this.drawbridgeTop);
				if (this.encoder.getDistance() < this.drawbridgeBack)
				{
					this.encoder.reset();
					haltDrive(0.5D);
					this.m_states = DRAWBRIDGE.POS3;
					this.torcan.SetDrive(0.0D, 0.0D);
				}
				break;
			case POS3: 
				setDegrees(this.drawbridgeBot);
				haltDrive(0.5D);
				if (siegeOnTarget(2)) {
					this.m_states = DRAWBRIDGE.POS4;
				}
				break;
			case POS4: 
				this.encoder.reset();
				this.m_states = DRAWBRIDGE.POS5;
				break;
			case POS5: 
				this.torcan.SetDrive(0.5D, -0.5D);
				if (this.encoder.getDistance() > this.drawbridgeArmUp) {
					setDegrees(0.0D);
				}
				if (this.encoder.getDistance() > this.drawbridgeDist)
				{
					this.m_states = DRAWBRIDGE.IDLE;
					enabled = false;
					this.torcan.SetDrive(0.0D, -0.0D);
				}
				break;
			}
		}
	}

	public void haltDrive(double p)
	{
		this.m_distance = this.encoder.getDistance();
		this.m_speed = (-p * this.m_distance);
		this.torcan.SetDrive(this.m_speed, -1.0D * this.m_speed);
	}
	
	public void haltButton(double p){
		if(stick.getRawButton(12)){
			switch(m_halt){
		case NULL: 
			break;
		case IDLE:
			this.m_halt = HALT.NULL;
			break;
		case POS1:
			encoder.reset();
			this.m_halt = HALT.POS2;
			break;
		case POS2:
			haltDrive(p);
			this.m_halt = HALT.IDLE;
			break;
			}
		}
	}
	
	public void turnToReference(){
		//	  if(stick3.getRawButton(9)){
		//		  turnToTheta(5);
		//	  }
		//	  if(stick3.getRawButton(10)){
		//		  turnToTheta(-5);
		//	  }
		//	  if(stick3.getRawButton(11)){
		//		  turnToTheta(10);
		//	  }
		//	  if(stick3.getRawButton(12)){
		//		  turnToTheta(-10);
		//	  }
	}
	public void turnToTheta(double desiredAngle)
	{
		this.targetAngle = ((desiredAngle + 360.0D) % 360.0D);

		this.error = (this.gyro.getAngle() - this.targetAngle);
		if (Math.abs(this.error) > 180.0D) {
			if (this.error > 0.0D)
			{
				this.error -= 180.0D;
				this.error *= -1.0D;
			}
			else
			{
				this.error += 180.0D;
				this.error *= -1.0D;
			}
		}
		if (this.error > 0.0D) {
			this.turnSpeed = Math.min(0.6D, this.error * this.turnP);
		} else {
			this.turnSpeed = Math.max(this.error * this.turnP, -0.6D);
		}
		this.torcan.SetDrive(this.turnSpeed, this.turnSpeed);
		System.out.println("Angle: " + this.gyro.getAngle());
	}

	public void SiegeArmDown()
	{
		this.siegeTalon.set(this.chevelTop);
	}

	public void SiegeArmUp()
	{
		this.siegeTalon.set(this.chevelTop - 100.0D);
	}

	public void drawbridgeTop()
	{
		this.siegeTalon.set(this.drawbridgeTop);
	}

	public void drawbridgeMid()
	{
		this.siegeTalon.set(this.drawbridgeTop + 130.0D);
	}

	public void drawbridgeBot()
	{
		this.siegeTalon.set(this.drawbridgeBot);
	}

	public void sally()
	{
		this.siegeTalon.set(this.sallyPort);
	}

	public void portBot()
	{
		this.siegeTalon.set(this.portcullisBot);
	}

	public void portTop()
	{
		this.siegeTalon.set(this.portcullisTop);
	}

	public void stopArm()
	{
		this.siegeTalon.set(0.0D);
	}

	public void reset()
	{
		System.out.println("!!!!!!!!!!!RESET!!!!!!!!!!!!");
	}
}
