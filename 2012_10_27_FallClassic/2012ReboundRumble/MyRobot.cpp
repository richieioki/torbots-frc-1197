#include "WPILib.h"
#include <math.h>
#include "SpeedCounter.h"

/* Hybrid class stores variables specific to Hybrid mode */
class Hybrid
{
public:
	//State 1
	static const int driveToFenderState = 1;
		static const float distToFender = 78; // inches
	//State 2
	static const int fireState = 2;
		static const float fire1StopTime = 1; // seconds after time start
		static const float fire2StartTime = 2;
		static const float fire2StopTime = 3;
	//State 3
	static const int driveToTurn1State = 3;
		static const float distToTurn1 = -192.4;
	//State 4
	static const int turn1State = 4;
		static const float turn1Angle = -58.11; // degrees
	//State 5
	static const int driveToTurn2State = 5;
		static const float distToTurn2 = -10; //-51.82;
	//State 6
	static const int turn2State = 6;
		static const float turn2Angle = 58.11;
	//State 7
	static const int driveToBridgeState = 7;
		static const float distToBridge = -50;
	//State 8
	static const int tiltBridgeState = 8;
	
	static const int autoLeft = 8;
	static const int autoMiddle = 6;
	static const int autoRight = 4;
	static const int autoDefault = 2;

	static const float moveValueFast = 0.4;
	static const float moveValueSlow = 0.4;
	static const float turnValue = 0.4;
};

/* Constants class stores constant values used in both Hybrid and Teleop */
class Constants
{
public:
	static const float stickDeadZone = 0.05; //if the joystick's value is less than this, it will be considered 0
	//								   Circ    count
	static const float encoderToDist = 25.13 / (440); // converts encoder ticks to distance (inches)
	static const float drivePGain = 0.02;
	static const float driveIGain = 0.0;
	static const float driveDGain = 0;
	static const float leftLowPassGain = 0.7;
	static const float rightLowPassGain = 0.7;
	static const float shootPGain = 1; // 1 // test: 1
	static const float shootIGain = 0.04; // 0.04 // test: 0.04
	static const float shootDGain = 0; // 0 // test: 1
	static const float maxFPS = 25; // Maximum drive speed (fps)
	
	static const float shooterCountsPerRev = 6; // Number of counts shooterCounter detects in 1 rev. Equals number of bolts on shooter
	static const float ballReleaseSpeedThresh = 35; // Maximum speed (rev/sec) the shooter may be running to detect that a ball has been released
	static const float shootSpeedTolerance = 0.04; // Tolerance (% of maxShootSpeed) within which to shoot
	static const float AUTOshootSpeedTolerance = 0.03;
	static const float spinUpTime = 1; // Amount of time after shooter begins running to feed shooter (only used in open loop shooting)
	static const float elevatorShootSpeed = -1.0; // Speed to run elevator to feed shooter
	static const float elevatorIntakeSpeed = -0.5; // Speed to run elevator after picking up a ball
	static const float elevatorReverseSpeed = 0.5; // Speed to run elevator when reverse button is pressed
	static const float elevatorIntakeTime = 0.55; // Time to run the elevator after intake (sec)
	static const float lowFrontShootSpeed = -0.92;
	static const float lowSideShootSpeed = -0.92;
	static const float highKeyShootSpeed = -1.15; //Default -1.0, -1.12 gives us corner key shot
	static const float highFrontShootSpeed = -0.92;
	static const float maxShootSpeed = 50; // TODO also contained in SpeedCounter.cpp
};

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 
class RobotDemo : public SimpleRobot
{
	Joystick stick;
	Jaguar *leftDriveJag;
	Jaguar *rightDriveJag;
	Jaguar *intakeJag;
	Jaguar *elevatorJag;
	Jaguar *shooterJag;
	Encoder *leftDriveEncoder;
	Encoder *rightDriveEncoder;
	Timer *timer;
	SpeedCounter *shooterCounter;
	Gyro *gyro;
	DigitalInput *intakeSensor;
	//PIDController *leftDrivePID;
	//PIDController *rightDrivePID;
	PIDController *shooterPID;
	Compressor *compressor;
	Solenoid *tiltSolenoid;
	Solenoid *shiftSolenoid;
	Solenoid *hoodSolenoid;
	SmartDashboard *smartDashboard;
	DriverStation *driverStation;
	int ballCount;
	float leftLowPassOut;
	float rightLowPassOut;

public:
	RobotDemo(void):
		stick(1)		// as they are declared above.
	{
		leftDriveJag = new Jaguar(10);
		rightDriveJag = new Jaguar(9);
		intakeJag = new Jaguar(4);
		elevatorJag = new Jaguar(3);
		shooterJag = new Jaguar(6);

		leftDriveEncoder = new Encoder(3,4,false, Encoder::k1X);
		rightDriveEncoder = new Encoder(5,6,false, Encoder::k1X);
		
		timer = new Timer();
		shooterCounter = new SpeedCounter(2);
		gyro = new Gyro(1, 1);
		
		intakeSensor = new DigitalInput(9);
		
		//leftDrivePID = new PIDController(Constants::drivePGain, Constants::driveIGain, Constants::driveDGain, leftDriveEncoder, leftDriveJag, 0.01);
		//rightDrivePID = new PIDController(Constants::drivePGain, Constants::driveIGain, Constants::driveDGain, rightDriveEncoder, rightDriveJag, 0.01);
		shooterPID = new PIDController(Constants::shootPGain, Constants::shootIGain, Constants::shootDGain, shooterCounter, shooterJag, 0.05);
		
		compressor = new Compressor(1,2);
		tiltSolenoid = new Solenoid(1);
		shiftSolenoid = new Solenoid(3);
		hoodSolenoid = new Solenoid(2);
		
		smartDashboard = SmartDashboard::GetInstance();
		driverStation = DriverStation::GetInstance();
		
		leftDriveEncoder->SetDistancePerPulse(Constants::encoderToDist);
		rightDriveEncoder->SetDistancePerPulse(Constants::encoderToDist);
		/*leftDriveEncoder->SetPIDSourceParameter(Encoder::kRate);
		rightDriveEncoder->SetPIDSourceParameter(Encoder::kRate);
		leftDrivePID->SetInputRange(Constants::maxFPS * -12, Constants::maxFPS * 12);
		rightDrivePID->SetInputRange(Constants::maxFPS * -12, Constants::maxFPS * 12);
		leftDrivePID->SetOutputRange(-1, 1);
		rightDrivePID->SetOutputRange(-1, 1);*/
		shooterPID->SetOutputRange(-1.0, 0.01);
		
		ballCount = 0;

		
	}
	
	void RobotInit(void)
	{
		
	}
	
	void Disabled(void)
	{
		
	}

	void setDrivePID(float rotateValue, float moveValue, bool usePID) //move and rotate switched
	{
		//taken from RobotDrive::ArcadeDrive
		float leftMotorOutput;
		float rightMotorOutput;
		
		if (moveValue > 0.0)
		{
			if (rotateValue > 0.0)
			{
				leftMotorOutput = moveValue - rotateValue;
				rightMotorOutput = max(moveValue, rotateValue);
			}
			else
			{
				leftMotorOutput = max(moveValue, -rotateValue);
				rightMotorOutput = moveValue + rotateValue;
			}
		}
		else
		{
			if (rotateValue > 0.0)
			{
				leftMotorOutput = - max(-moveValue, rotateValue);
				rightMotorOutput = moveValue + rotateValue;
			}
			else
			{
				leftMotorOutput = moveValue - rotateValue;
				rightMotorOutput = - max(-moveValue, -rotateValue);
			}
		}
		
		leftMotorOutput = LowPassFilter(leftMotorOutput, leftLowPassOut, Constants::leftLowPassGain);
		rightMotorOutput = LowPassFilter(rightMotorOutput, rightLowPassOut, Constants::rightLowPassGain);
		
		/*if (usePID)
		{
			//PID
			leftDrivePID->SetSetpoint(leftMotorOutput * Constants::maxFPS * 12);
			rightDrivePID->SetSetpoint(rightMotorOutput * Constants::maxFPS * 12);
		}
		else
		{*/
			//Direct Drive
		//leftMotorOutput = (-stick.GetY() + stick.GetX()/2.0);
		//rightMotorOutput = (-stick.GetY() - stick.GetX()/2.0);
		//leftMotorOutput = (-stick.GetX() + stick.GetY()/2.0);
		//rightMotorOutput = (-stick.GetX() - stick.GetY()/2.0);
				
		leftMotorOutput = stick.GetY() - stick.GetX();
		rightMotorOutput = (-1 * stick.GetY()) - stick.GetX();
		
		if(leftMotorOutput > 1.0)
			leftMotorOutput = 1.0;
		if(rightMotorOutput > 1.0)
			rightMotorOutput = 1.0;
		if(leftMotorOutput < -1.0)
			leftMotorOutput = -1.0;
		if(rightMotorOutput < -1.0)
			rightMotorOutput = -1.0;
				
			leftDriveJag->Set(leftMotorOutput);
			rightDriveJag->Set(rightMotorOutput);
		//}
	}
	
	
	float LowPassFilter(float in, float lastOut, float gain)
	{
		return (lastOut + gain * (in - lastOut));
	}

	void Autonomous(void)
	{		
		int series = 0;
		float delay = 0;
		int state = Hybrid::driveToFenderState;
		float driveSpeed = 0;
		float driveTurn = 0;
		float leftDriveSpeed = 0;
		float rightDriveSpeed = 0;
		float conveyorSpeed = 0;
		float fireSpeed = 0;
		float lastShootSpeed = 0;
		float shootTolerance = Constants::shootSpeedTolerance;
		
		float leftEncoderDist = 0;
		float rightEncoderDist = 0;
		float encoderDiff = 0;
		float gyroAngle = 0;
		float timeCurr = 0;
		float timeLast = 0;
		float timeElap = 0;

		/*for (int i = 2; i < 9; i += 2)
		{
			series += !driverStation->GetDigitalIn(i) ? i : 0;
		}
		if (series <= 0 || series >= 9)
		{
			series = Hybrid::autoDefault;
		}*/
		

		leftDriveEncoder->Reset();
		rightDriveEncoder->Reset();
		leftDriveEncoder->Start();
		rightDriveEncoder->Start();
		
		shooterCounter->Start();
		
		/*leftDrivePID->Enable();
		rightDrivePID->Enable();*/
		shooterPID->Enable();
		
		//compressor->Start(); CAUSES ALL SORTSA PROBLEMS
		
		gyro->Reset(); // need to acquire data for angle adjustment
		timer->Reset();
		timer->Start();
		
		while(IsAutonomous()){
			/*encoderDist = rightDriveEncoder->GetDistance();//GetRaw() * Constants::encoderToDist;
			gyroAngle = gyro->GetAngle();
			timeCurr = timer->Get();
			
			switch (series)
			{
			case Hybrid::autoLeft: //TODO Hybrid Left
				//fireSpeed = Constants::lowFrontShootSpeed;
	            switch (state)
	            {
	            case Hybrid::driveToFenderState:
	            	driveSpeed = Hybrid::moveValueFast;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist >= Hybrid::distToFender)
	            	{
	            		state = Hybrid::fireState;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		timer->Reset();
	            		timer->Start();
	            	}
	            	break;
	            case Hybrid::fireState:
	        		driveSpeed = 0;
	        		driveTurn = 0;
	        		//fireSpeed = Constants::lowFrontShootSpeed;
	            	if (timeCurr <= Hybrid::fire1StopTime)
	            	{
	            		// Fire 1st ball
	            		conveyorSpeed = 0;
	            	}
	            	else if (timeCurr <= Hybrid::fire2StartTime)
	            	{
	            		// Stop firing (after 1st ball)
	            		conveyorSpeed = 1;
	            	}
	            	else if (timeCurr <= Hybrid::fire2StopTime)
	            	{
	            		// Fire 2nd ball
	            		conveyorSpeed = 1;
	            	}
	            	else
	            	{
	            		// Stop firing (after 2nd ball)
	            		conveyorSpeed = 0;
	            		//fireSpeed = 0.0;
	            		//state = Hybrid::driveToTurn1State;
	                	driveSpeed = 0;//-1 * Hybrid::moveValueFast;
	                	driveTurn = 0;//-0.1 * gyroAngle;
	                	rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToTurn1State:
	            	driveSpeed = -1 * Hybrid::moveValueFast;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist <= Hybrid::distToTurn1)
	            	{
	            		state = Hybrid::turn1State;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		gyro->Reset();
	            	}
	            	break;
	            case Hybrid::turn1State:
	            	driveSpeed = 0;
	            	driveTurn = -1 * Hybrid::turnValue;
	            	if (gyroAngle <= Hybrid::turn1Angle)
	            	{
	            		state = Hybrid::driveToTurn2State;
	            		driveSpeed = -1 * Hybrid::moveValueSlow;
	            		driveTurn = 0;
	            		gyro->Reset();
	            		rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToTurn2State:
	            	driveSpeed = -1 * Hybrid::moveValueSlow;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist <= Hybrid::distToTurn2)
	            	{
	            		state = Hybrid::turn2State;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		gyro->Reset();
	            	}
	            	break;
	            case Hybrid::turn2State:
	            	driveSpeed = 0;
	            	driveTurn = Hybrid::turnValue;
	            	if (gyroAngle >= Hybrid::turn2Angle)
		        	{
		        		//state = Hybrid::driveToBridgeState;
		        		driveSpeed = 0;//-1 * Hybrid::moveValueSlow;
		        		driveTurn = 0;
	            		gyro->Reset();
	            		rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToBridgeState:
	            	driveSpeed = -1 * Hybrid::moveValueSlow;
	            	driveTurn = -0.1 * gyroAngle;
					tiltSolenoid->Set(true);
	            	if (encoderDist <= Hybrid::distToBridge)
	            	{
	            		state = Hybrid::tiltBridgeState;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		gyro->Reset();
	            	}
	            	break;
	            }
	            break;
	            
            case Hybrid::autoRight: //TODO Hybrid Right
            	//fireSpeed = Constants::lowFrontShootSpeed;
	            switch (state)
	            {
	            case Hybrid::driveToFenderState:
	            	driveSpeed = Hybrid::moveValueFast;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist >= Hybrid::distToFender)
	            	{
	            		state = Hybrid::fireState;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		timer->Reset();
	            		timer->Start();
	            	}
	            	break;
	            case Hybrid::fireState:
	        		driveSpeed = 0;
	        		driveTurn = 0;
	        		//fireSpeed = Constants::lowFrontShootSpeed;
	            	if (timeCurr <= Hybrid::fire1StopTime)
	            	{
	            		// Fire 1st ball
	            		conveyorSpeed = 0;
	            	}
	            	else if (timeCurr <= Hybrid::fire2StartTime)
	            	{
	            		// Stop firing (after 1st ball)
	            		conveyorSpeed = 1;
	            	}
	            	else if (timeCurr <= Hybrid::fire2StopTime)
	            	{
	            		// Fire 2nd ball
	            		conveyorSpeed = 1;
	            	}
	            	else
	            	{
	            		// Stop firing (after 2nd ball)
	            		conveyorSpeed = 0;
	            		//fireSpeed = 0.0;
	            		//state = Hybrid::driveToTurn1State;
	                	driveSpeed = 0; //-1 * Hybrid::moveValueFast;
	                	driveTurn = 0; //-0.1 * gyroAngle;
	                	rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToTurn1State:
	            	driveSpeed = -1 * Hybrid::moveValueFast;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist <= Hybrid::distToTurn1)
	            	{
	            		state = Hybrid::turn1State;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		gyro->Reset();
	            	}
	            	break;
	            case Hybrid::turn1State:
	            	driveSpeed = 0;
	            	driveTurn = Hybrid::turnValue;
	            	if (gyroAngle >= -1 * Hybrid::turn1Angle)
	            	{
	            		state = Hybrid::driveToTurn2State;
	            		driveSpeed = -1 * Hybrid::moveValueSlow;
	            		driveTurn = 0;
	            		gyro->Reset();
	            		rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToTurn2State:
	            	driveSpeed = -1 * Hybrid::moveValueSlow;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist <= Hybrid::distToTurn2)
	            	{
	            		state = Hybrid::turn2State;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		gyro->Reset();
	            	}
	            	break;
	            case Hybrid::turn2State:
	            	driveSpeed = 0;
	            	driveTurn = -1 * Hybrid::turnValue;
	            	if (gyroAngle <= -1 * Hybrid::turn2Angle)
		        	{
		        		//state = Hybrid::driveToBridgeState;
		        		driveSpeed = 0; //-1 * Hybrid::moveValueSlow;
		        		driveTurn = 0;
	            		gyro->Reset();
	            		rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToBridgeState:
	            	driveSpeed = -1 * Hybrid::moveValueSlow;
	            	driveTurn = -0.1 * gyroAngle;
					tiltSolenoid->Set(true);
	            	if (encoderDist <= Hybrid::distToBridge)
	            	{
	            		state = Hybrid::tiltBridgeState;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		gyro->Reset();
	            	}
	            	break;
	            }
	            break;
            	
            case Hybrid::autoMiddle: //TODO Hybrid Middle
            	//fireSpeed = Constants::highFrontShootSpeed;
	            switch (state)
	            {
	            case Hybrid::driveToFenderState:
	            	driveSpeed = Hybrid::moveValueFast;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist >= Hybrid::distToFender)
	            	{
	            		state = Hybrid::fireState;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		timer->Reset();
	            		timer->Start();
	            	}
	            	break;
	            case Hybrid::fireState:
	        		driveSpeed = 0;
	        		driveTurn = 0;
	        		//fireSpeed = Constants::highFrontShootSpeed;
	            	if (timeCurr <= Hybrid::fire1StopTime)
	            	{
	            		// Fire 1st ball
	            		conveyorSpeed = 0;
	            	}
	            	else if (timeCurr <= Hybrid::fire2StartTime)
	            	{
	            		// Stop firing (after 1st ball)
	            		conveyorSpeed = 1;
	            	}
	            	else if (timeCurr <= Hybrid::fire2StopTime)
	            	{
	            		// Fire 2nd ball
	            		conveyorSpeed = 1;
	            	}
	            	else
	            	{
	            		// Stop firing (after 2nd ball)
	            		conveyorSpeed = 0;
	            		//fireSpeed = 0.0;
	            		//state = Hybrid::driveToTurn1State;
	                	driveSpeed = 0;//-1 * Hybrid::moveValueFast;
	                	driveTurn = 0;//-0.1 * gyroAngle;
	                	rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToTurn1State:
	            	driveSpeed = -1 * Hybrid::moveValueFast;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist <= Hybrid::distToTurn1)
	            	{
	            		state = Hybrid::driveToTurn2State;
	                	driveSpeed = -1 * Hybrid::moveValueFast;
	                	driveTurn = -0.1 * gyroAngle;
	            		gyro->Reset();
	                	rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToTurn2State:
	            	driveSpeed = -1 * Hybrid::moveValueSlow;
	            	driveTurn = -0.1 * gyroAngle;
	            	if (encoderDist <= Hybrid::distToTurn2)
	            	{
	            		//state = Hybrid::driveToBridgeState;
		        		driveSpeed = 0; //-1 * Hybrid::moveValueSlow;
		        		driveTurn = 0;
	            		gyro->Reset();
	            		rightDriveEncoder->Reset();
	            	}
	            	break;
	            case Hybrid::driveToBridgeState:
	            	driveSpeed = -1 * Hybrid::moveValueSlow;
	            	driveTurn = -0.1 * gyroAngle;
					tiltSolenoid->Set(true);
	            	if (encoderDist <= Hybrid::distToBridge)
	            	{
	            		state = Hybrid::tiltBridgeState;
	            		driveSpeed = 0;
	            		driveTurn = 0;
	            		gyro->Reset();
	            	}
	            	break;
	            }
	            break;
            	
            default: //TODO Hybrid Default
            	//fireSpeed = Constants::highKeyShootSpeed;
        		driveSpeed = 0;
        		driveTurn = 0;
            	if (timeCurr <= Hybrid::fire1StopTime * 2)
            	{
            		// Fire 1st ball
            		conveyorSpeed = 0;
            	}
            	else if (timeCurr <= Hybrid::fire2StartTime * 2)
            	{
            		// Stop firing (after 1st ball)
            		conveyorSpeed = 1;
            	}
            	else if (timeCurr <= Hybrid::fire2StopTime * 2)
            	{
            		// Fire 2nd ball
            		conveyorSpeed = 1;
            	}
            	else
            	{
            		// Stop firing (after 2nd ball)
            		conveyorSpeed = 0;
            		//fireSpeed = 0.0;
            	}
            	break;
			}
			/*smartDashboard->PutDouble("speed", driveSpeed);
			smartDashboard->PutDouble("turn", driveTurn);
			smartDashboard->PutDouble("distRight", rightDriveEncoder->GetDistance());
			smartDashboard->PutInt("series", series);*/
			
			if (driverStation->GetDigitalIn(2) && driverStation->GetDigitalIn(4))
			{
				delay = 0;
				shootTolerance = Constants::shootSpeedTolerance;
	            // Drive all motors needed using set values			
				leftEncoderDist = -1 * leftDriveEncoder->GetDistance();
				rightEncoderDist = rightDriveEncoder->GetDistance();
				encoderDiff = leftEncoderDist - rightEncoderDist;
				smartDashboard->PutDouble("r_dist", rightEncoderDist);
				smartDashboard->PutDouble("l_dist", leftEncoderDist);
				if (leftEncoderDist < Hybrid::distToFender && rightEncoderDist < Hybrid::distToFender)
				{
					driveSpeed = Hybrid::moveValueFast;
					conveyorSpeed = 0;
					fireSpeed = Constants::highFrontShootSpeed;
				}
				else
				{
					driveSpeed = 0;
					conveyorSpeed = Constants::elevatorShootSpeed / 2;
					fireSpeed = Constants::highFrontShootSpeed;
				}
				
				leftDriveSpeed = driveSpeed - (encoderDiff / 5);
				rightDriveSpeed = driveSpeed + (encoderDiff / 5);
				
				if(leftDriveSpeed > 1.0)
					leftDriveSpeed = 1.0;
				if(rightDriveSpeed > 1.0)
					rightDriveSpeed = 1.0;
				if(leftDriveSpeed < -1.0)
					leftDriveSpeed = -1.0;
				if(rightDriveSpeed < -1.0)
					rightDriveSpeed = -1.0;
	
				leftDriveJag->Set(-1 * leftDriveSpeed);
				rightDriveJag->Set(rightDriveSpeed);
			}
			else
			{
				shootTolerance = Constants::AUTOshootSpeedTolerance;
				delay = 8.0;
				fireSpeed = Constants::highKeyShootSpeed;
				conveyorSpeed = Constants::elevatorShootSpeed;
			}
			shooterPID->SetSetpoint(fireSpeed);

			timeCurr = timer->Get();
			timeElap = timeCurr - timeLast;
			if (timeElap >= 0.05)
			{
				timeLast = timeCurr;
				float speed;
				int count = shooterCounter->Get();
				smartDashboard->PutInt("shooterCount", count);
				shooterCounter->Reset();
				if (timeElap != 0)
					speed = count / (Constants::shooterCountsPerRev * timeElap);
				else
					speed = 0;
				shooterCounter->SetSpeed(speed);
				
				/* If (the ball was above the threshold, is not currently,
				 * and the trigger is being held down)
				 * (We don't want to think we shot a ball when the wheel
				 * slows down naturally after the trigger is released)
				 * Then we just shot a ball
				 */
				/*if (lastShootSpeed > Constants::ballReleaseSpeedThresh && speed <= Constants::ballReleaseSpeedThresh && fireSpeed != 0)
				{
					ballCount--;
				}*/
				
				//printf("time=%f count=%d speed=%f\n", timeCurr, count, speed);
				lastShootSpeed = speed;
				smartDashboard->PutDouble("shooterSpeed", speed);
			}

			if (timeCurr >= delay && fabs(fabs(fireSpeed * Constants::maxShootSpeed) - lastShootSpeed) <= shootTolerance * Constants::maxShootSpeed) // Closed loop shooting
			{
				//Empty
			}
			else
			{
				conveyorSpeed = 0;
			}
			elevatorJag->Set(conveyorSpeed);
			
			/*if (series == Hybrid::autoLeft || series == Hybrid::autoRight)
			{
				fireSpeed = Constants::lowFrontShootSpeed;
			}
			else if (series == Hybrid::autoMiddle)
			{
				fireSpeed = Constants::highFrontShootSpeed;
			}
			else
			{
				fireSpeed = Constants::highKeyShootSpeed;
			}*/
			
			/*leftDriveJag->Set(-0.4);
			rightDriveJag->Set(0.4);
			
			Wait(10);
			fireSpeed = 0.92;
			
			elevatorJag->Set(-1.0);
			shooterPID->SetSetpoint(fireSpeed);
			
			/*
			timeCurr = timer->Get();
			timeElap = timeCurr - timeLast;
			if (timeElap >= 0.05)
			{
				timeLast = timeCurr;
				float speed;
				int count = shooterCounter->Get();
				//smartDashboard->PutInt("shooterCount", count);
				shooterCounter->Reset();
				if (timeElap != 0)
					speed = count / (Constants::shooterCountsPerRev * timeElap);
				else
					speed = 0;
				shooterCounter->SetSpeed(speed);
				smartDashboard->PutDouble("speed", speed);
				
				//printf("time=%f count=%d speed=%f\n", timeCurr, count, speed);
				lastShootSpeed = speed;
			}

			shooterPID->SetSetpoint(Constants::highKeyShootSpeed);
			
			//if the delay (set by radial switch) is over, and the shooter is spinning fast enough
			if (timeCurr >= delay && fabs(fabs(Constants::highKeyShootSpeed * Constants::maxShootSpeed) - shooterCounter->GetSpeed()) <= Constants::shootSpeedTolerance * Constants::maxShootSpeed) // Closed loop shooting
			{
				elevatorJag->Set(Constants::elevatorShootSpeed);
			}
			smartDashboard->PutDouble("delay", delay);
			smartDashboard->PutDouble("time", timeElap);*/
			//intakeJag->Set(-1.0);
		}
	}

	void OperatorControl(void)
	{
		float stickX = 0;
		float stickY = 0;
		float stickZ = 0;
		bool shiftButton = false; //Button 2
		bool tiltButton = false; //Button 7
		bool tiltButton2 = false; //Button 6
		bool hoodButton = false; //Button 3
		bool lowFrontShotButton = false; //DIO 5
		bool lowSideShotButton = false; //DIO 3
		bool highKeyShotButton = false; //DIO 1
		float shootSetupSpeed = 0; //the speed to set shootPID assuming trigger is pulled
		bool shootButton = false; //Button 1
		bool reverseElevatorButton = false; //Button 10
		bool ballCountReset1 = false; //Button 8
		bool ballCountReset2 = false; //Button 9
		
		float lastShootSpeed = 0;
		int intakeVal = 1; // 1 means the sensors can see each other (no ball in the way) 
		int lastIntakeVal = 1;
		
		float timeCurr = 0;
		float timeLast = 0;
		float timeElap = 0;
		float spinUpTime = 0;
		float intakeTime = 0;
		
		leftDriveEncoder->Reset();
		rightDriveEncoder->Reset();
		leftDriveEncoder->Start();
		rightDriveEncoder->Start();
		
		ballCount = 0;
		shooterCounter->Start();
		
		//leftDrivePID->Enable();
		//rightDrivePID->Enable();
		shooterPID->Enable();
		shooterPID->SetSetpoint(Constants::highFrontShootSpeed);
		
		compressor->Start();
		
		timer->Reset();
		timer->Start();
		while (IsOperatorControl())
		{
			stickX = stick.GetX();
			stickY = stick.GetY();
			stickZ = stick.GetZ();
			shiftButton = stick.GetRawButton(2);
			tiltButton = stick.GetRawButton(7);
			tiltButton2 = stick.GetRawButton(6);
			shootButton = stick.GetRawButton(1);
			hoodButton = stick.GetRawButton(3);
			lowFrontShotButton = !driverStation->GetDigitalIn(5);
			lowSideShotButton = !driverStation->GetDigitalIn(3);
			highKeyShotButton = !driverStation->GetDigitalIn(1);
			reverseElevatorButton = stick.GetRawButton(10);
			ballCountReset1 = stick.GetRawButton(8);
			ballCountReset2 = stick.GetRawButton(9);
			intakeVal = intakeSensor->Get();
			
			// adjust joystick by dead zone
			if (fabs(stickX) <= Constants::stickDeadZone)
				stickX = 0;
			if (fabs(stickY) <= Constants::stickDeadZone)
				stickY = 0;
			/*if ((stickX == 0 && stickY == 0) || stickZ >= 0)
			{
				leftDrivePID->Disable();
				rightDrivePID->Disable();
				leftDrivePID->Reset();
				rightDrivePID->Reset();
			}
			else
			{
				if (!leftDrivePID->IsEnabled() || !rightDrivePID->IsEnabled())
				{
					leftDrivePID->Enable();
					rightDrivePID->Enable();
				}
			}*/
			//call drive function, Z-axis controls whether or not to drive by PID
			setDrivePID(-stickY, -stickX, stickZ < 0);
			
			// shift high/low drive gear
			if (shiftButton)
			{
				shiftSolenoid->Set(true);
			}
			else
			{
				shiftSolenoid->Set(false);
			}
			
			// Activate bridge tilter
			if (tiltButton || tiltButton2)
			{
				tiltSolenoid->Set(true);
			}
			else
			{
				tiltSolenoid->Set(false);
			}
			
			//Check if we just picked up a ball
			if (lastIntakeVal == 1 && intakeVal == 0)
			{
				ballCount++;
				intakeTime = timeCurr;
			}
			lastIntakeVal = intakeVal;

			timeCurr = timer->Get();
			timeElap = timeCurr - timeLast;
			if (timeElap >= 0.05)
			{
				timeLast = timeCurr;
				float speed;
				int count = shooterCounter->Get();
				//smartDashboard->PutInt("shooterCount", count);
				shooterCounter->Reset();
				if (timeElap != 0)
					speed = count / (Constants::shooterCountsPerRev * timeElap);
				else
					speed = 0;
				shooterCounter->SetSpeed(speed);
				
				/* If (the ball was above the threshold, is not currently,
				 * and the trigger is being held down)
				 * (We don't want to think we shot a ball when the wheel
				 * slows down naturally after the trigger is released)
				 * Then we just shot a ball
				 */
				if (lastShootSpeed > Constants::ballReleaseSpeedThresh && speed <= Constants::ballReleaseSpeedThresh && shootButton)
				{
					ballCount--;
				}
				
				//printf("time=%f count=%d speed=%f\n", timeCurr, count, speed);
				lastShootSpeed = speed;
				smartDashboard->PutDouble("shooterSpeed", speed);
				smartDashboard->PutDouble("speed", speed / 50);
			}
			
			// In case we want to lower the balls
			if (reverseElevatorButton)
			{
				elevatorJag->Set(Constants::elevatorReverseSpeed);
			}
			// If we're trying to load the shooter and the shooter is spinning fast enough
			//else if (shootButton && timeCurr - spinUpTime >= Constants::spinUpTime) // Open loop shooting
			else if (shootButton && fabs(fabs(shootSetupSpeed * Constants::maxShootSpeed) - shooterCounter->GetSpeed()) <= Constants::shootSpeedTolerance * Constants::maxShootSpeed) // Closed loop shooting
			{
				elevatorJag->Set(Constants::elevatorShootSpeed);
			}
			// If we need to make room for another ball
			else if (timeCurr - intakeTime < Constants::elevatorIntakeTime)
			{
				elevatorJag->Set(Constants::elevatorIntakeSpeed);
			}
			else
			{
				elevatorJag->Set(0.0);				
			}
			
			// Raise aiming hood
			/*if (hoodButton)
			{
				hoodSolenoid->Set(true);
			}
			else
			{
				hoodSolenoid->Set(false);
			}*/
			if (hoodButton)//lowFrontShotButton)
			{
				hoodSolenoid->Set(true);
				shootSetupSpeed = Constants::lowFrontShootSpeed;
			}
			else if (lowSideShotButton)
			{
				hoodSolenoid->Set(true);
				shootSetupSpeed = Constants::lowSideShootSpeed;
			}
			else if (highKeyShotButton)
			{
				hoodSolenoid->Set(false);
				shootSetupSpeed = Constants::highKeyShootSpeed;
			}
			else if (shootButton)
			{
				hoodSolenoid->Set(false);
				shootSetupSpeed = Constants::highFrontShootSpeed;
			}
			else
			{
				hoodSolenoid->Set(false);
				shootSetupSpeed = Constants::highFrontShootSpeed;
			}
			
			if (shootButton)
			{
				//shooterJag->Set(Constants::shootSpeed);
				shooterPID->SetSetpoint(shootSetupSpeed);
			}
			else
			{
				spinUpTime = timeCurr;
				//shooterJag->Set(0.0);
				shooterPID->SetSetpoint(shootSetupSpeed);
			}
			
			//Reset ballCount if both reset buttons are pressed together
			if (ballCountReset1 && ballCountReset2)
			{
				ballCount = 0;
			}
			
			// run intake if we have less than 3 balls
			if (ballCount < 3)
			{
				intakeJag->Set(1.0);
			}
			/* run intake in reverse if we have at least 3 balls
			 * and there aren't any left in the intake
			 */
			else if (timeCurr - intakeTime >= Constants::elevatorIntakeTime)
			{
				intakeJag->Set(-1.0);
			}
			
			//printf("Time:%f Speed:%f\n", timer1->Get(), shooterCounter->GetSpeed());
			//timeCurr = timer1->Get();
			//timeElap = timeCurr - timeLast;
			
			//if (timeElap >= 0.5)
			//LowPassFilter(leftDriveEncoder->GetRate());
			
			smartDashboard->PutInt("ticksLeft", leftDriveEncoder->GetRaw());
			smartDashboard->PutDouble("distLeft", leftDriveEncoder->GetDistance());
			smartDashboard->PutInt("ticksRight", rightDriveEncoder->GetRaw());
			smartDashboard->PutDouble("distRight", rightDriveEncoder->GetDistance());
			
			/*smartDashboard->PutDouble("distLeft", leftDriveEncoder->GetDistance());
			smartDashboard->PutDouble("rateLeft", leftDriveEncoder->GetRate());
			smartDashboard->PutInt("ticksLeft", leftDriveEncoder->GetRaw());
			smartDashboard->PutDouble("leftOut", leftDrivePID->Get());
			smartDashboard->PutDouble("leftError", leftDrivePID->GetError());
			smartDashboard->PutDouble("leftSet", leftDrivePID->GetSetpoint());
			smartDashboard->PutDouble("distRight", rightDriveEncoder->GetDistance());
			smartDashboard->PutDouble("rateRight", rightDriveEncoder->GetRate());
			smartDashboard->PutInt("ticksRight", rightDriveEncoder->GetRaw());
			smartDashboard->PutDouble("rightOut", rightDrivePID->Get());
			smartDashboard->PutDouble("rightError", rightDrivePID->GetError());
			smartDashboard->PutDouble("rightSet", rightDrivePID->GetSetpoint());
			smartDashboard->PutDouble("stickY", stickY);
			smartDashboard->PutInt("ballCount", ballCount);
			smartDashboard->PutDouble("gyro", gyro->GetAngle());
			smartDashboard->PutInt("intake", intakeVal);*/
			//timeLast = timeCurr;
			//printf("rate: %f   error: %f   out: %f\n", leftDriveEncoder->GetRate(), leftDrivePID->GetError(), leftDrivePID->Get());
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

