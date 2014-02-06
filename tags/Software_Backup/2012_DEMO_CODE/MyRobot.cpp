#include "WPILib.h"
#include <math.h>
#include "SpeedCounter.h"

/* Hybrid class stores variables specific to Hybrid mode */
class Hybrid
{
public:
	//State 1
	static const int driveToFenderState = 1;
		static const float distToFender = 105.5; // inches
	//State 2
	static const int fireState = 2;
		static Timer *timer;
		static const float fire1StopTime = 3; // seconds after time start
		static const float fire2StartTime = 4;
		static const float fire2StopTime = 5;
	//State 3
	static const int driveToTurn1State = 3;
		static const float distToTurn1 = -192.4;
	//State 4
	static const int turn1State = 4;
		static const float turn1Angle = -58.11; // degrees
	//State 5
	static const int driveToTurn2State = 5;
		static const float distToTurn2 = -51.82;
	//State 6
	static const int turn2State = 6;
		static const float turn2Angle = 58.11;
	//State 7
	static const int driveToBridgeState = 7;
		static const float distToBridge = -44;
	//State 8
	static const int tiltBridgeState = 8;
	
	static int state;
	static float driveSpeed;
	static float driveTurn;
	static float conveyorSpeed;

	static const float moveValueFast = 0.7;
	static const float moveValueSlow = 0.4;
	static const float turnValue = 0.5;
	
	static void Reset()
	{
		state = 0;
		driveSpeed = 0;
		driveTurn = 0;
		conveyorSpeed = 0;
		timer = new Timer();
	}
};

/* Constants class stores constant values used in both Hybrid and Teleop */
class Constants
{
public:
	static const float stickDeadZone = 0.05; //if the joystick's value is less than this, it will be considered 0
	//								   Circ    spkt count
	static const float encoderToDist = 25.13 / (2 * 440); // converts encoder ticks to distance (inches)
	static const float drivePGain = 0.09;
	static const float driveIGain = 0.02;
	static const float driveDGain = 0;
	static const float leftLowPassGain = 0.7;
	static const float rightLowPassGain = 0.7;
	static const float shootPGain = 1;
	static const float shootIGain = 0.04;
	static const float shootDGain = 0;
	static const float maxFPS = 25; // Maximum drive speed (fps)
	
	static const float shooterCountsPerRev = 6; // Number of counts shooterCounter detects in 1 rev. Equals number of bolts on shooter
	static const float ballReleaseSpeedThresh = 35; // Maximum speed (rev/sec) the shooter may be running to detect that a ball has been released
	static const float shootSpeedTolerance = 0.04; // Tolerance (% of maxShootSpeed) within which to shoot
	static const float spinUpTime = 1; // Amount of time after shooter begins running to feed shooter (only used in open loop shooting)
	static const float elevatorShootSpeed = -1.0; // Speed to run elevator to feed shooter
	static const float elevatorIntakeSpeed = -0.5; // Speed to run elevator after picking up a ball
	static const float elevatorReverseSpeed = 0.5; // Speed to run elevator when reverse button is pressed
	static const float elevatorIntakeTime = 0.4; // Time to run the elevator after intake (sec)
	static const float shootSpeed = -0.92;
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
	PIDController *leftDrivePID;
	PIDController *rightDrivePID;
	PIDController *shooterPID;
	Compressor *compressor;
	Solenoid *tiltSolenoid;
	Solenoid *shiftSolenoid;
	Solenoid *hoodSolenoid;
	SmartDashboard *smartDashboard;
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
		
		leftDrivePID = new PIDController(Constants::drivePGain, Constants::driveIGain, Constants::driveDGain, leftDriveEncoder, leftDriveJag, 0.01);
		rightDrivePID = new PIDController(Constants::drivePGain, Constants::driveIGain, Constants::driveDGain, rightDriveEncoder, rightDriveJag, 0.01);
		shooterPID = new PIDController(Constants::shootPGain, Constants::shootIGain, Constants::shootDGain, shooterCounter, shooterJag, 0.05);
		
		compressor = new Compressor(1,6);
		tiltSolenoid = new Solenoid(1);
		shiftSolenoid = new Solenoid(3);
		hoodSolenoid = new Solenoid(2);
		
		smartDashboard = SmartDashboard::GetInstance();
		
		leftDriveEncoder->SetDistancePerPulse(Constants::encoderToDist);
		rightDriveEncoder->SetDistancePerPulse(Constants::encoderToDist);
		leftDriveEncoder->SetPIDSourceParameter(Encoder::kRate);
		rightDriveEncoder->SetPIDSourceParameter(Encoder::kRate);
		leftDrivePID->SetInputRange(Constants::maxFPS * -12, Constants::maxFPS * 12);
		rightDrivePID->SetInputRange(Constants::maxFPS * -12, Constants::maxFPS * 12);
		leftDrivePID->SetOutputRange(-1, 1);
		rightDrivePID->SetOutputRange(-1, 1);
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
		
		if (usePID)
		{
			//PID
			leftDrivePID->SetSetpoint(leftMotorOutput * Constants::maxFPS);
			rightDrivePID->SetSetpoint(rightMotorOutput * Constants::maxFPS);
		}
		else
		{
			//Direct Drive
			leftDriveJag->Set(leftMotorOutput);
			rightDriveJag->Set(rightMotorOutput);
		}
	}
	
	
	float LowPassFilter(float in, float lastOut, float gain)
	{
		return (lastOut + gain * (in - lastOut));
	}

	void Autonomous(void)
	{
		/*Hybrid::Reset();

		float encoderDist;
		float gyroAngle;
		float time;
		
		encoder->Reset();
		gyro->Reset(); // need to acquire data for angle adjustment
		
		while(IsAutonomous()){
			encoderDist = encoder->GetRaw() * RobotCalc::encoderToDist;
			gyroAngle = gyro->GetAngle();
			time = Hybrid::timer->Get();
			
            switch (Hybrid::state)
            {
            case Hybrid::driveToFenderState:
            	Hybrid::driveSpeed = Hybrid::moveValueFast;
            	Hybrid::driveTurn = -0.1 * gyroAngle;
            	if (encoderDist >= Hybrid::distToFender)
            	{
            		Hybrid::state = Hybrid::fireState;
            		Hybrid::driveSpeed = 0;
            		Hybrid::driveTurn = 0;
            		Hybrid::timer->Start();
            	}
            	break;
            case Hybrid::fireState:
        		Hybrid::driveSpeed = 0;
        		Hybrid::driveTurn = 0;
            	if (time <= Hybrid::fire1StopTime)
            	{
            		// Fire 1st ball
            		Hybrid::conveyorSpeed = 1;
            	}
            	else if (time <= Hybrid::fire2StartTime)
            	{
            		// Stop firing (after 1st ball)
            		Hybrid::conveyorSpeed = 0;
            	}
            	else if (time <= Hybrid::fire2StopTime)
            	{
            		// Fire 2nd ball
            		Hybrid::conveyorSpeed = 1;
            	}
            	else
            	{
            		// Stop firing (after 2nd ball)
            		Hybrid::conveyorSpeed = 0;
            		Hybrid::state = Hybrid::driveToTurn1State;
                	Hybrid::driveSpeed = -Hybrid::moveValueFast;
                	Hybrid::driveTurn = -0.1 * gyroAngle;
                	encoder->Reset();
            	}
            	break;
            case Hybrid::driveToTurn1State:
            	Hybrid::driveSpeed = -Hybrid::moveValueFast;
            	Hybrid::driveTurn = -0.1 * gyroAngle;
            	if (encoderDist <= Hybrid::distToTurn1)
            	{
            		Hybrid::state = Hybrid::turn1State;
            		Hybrid::driveSpeed = 0;
            		Hybrid::driveTurn = 0;
            		gyro->Reset();
            	}
            	break;
            case Hybrid::turn1State:
            	Hybrid::driveSpeed = 0;
            	Hybrid::driveTurn = -Hybrid::turnValue;
            	if (gyroAngle <= Hybrid::turn1Angle)
            	{
            		Hybrid::state = Hybrid::driveToTurn2State;
            		Hybrid::driveSpeed = -Hybrid::moveValueSlow;
            		Hybrid::driveTurn = 0;
            		gyro->Reset();
            		encoder->Reset();
            	}
            	break;
            case Hybrid::driveToTurn2State:
            	Hybrid::driveSpeed = -Hybrid::moveValueSlow;
            	Hybrid::driveTurn = -0.1 * gyroAngle;
            	if (encoderDist <= Hybrid::distToTurn2)
            	{
            		Hybrid::state = Hybrid::turn2State;
            		Hybrid::driveSpeed = 0;
            		Hybrid::driveTurn = 0;
            		gyro->Reset();
            	}
            	break;
            case Hybrid::turn2State:
            	Hybrid::driveSpeed = 0;
            	Hybrid::driveTurn = Hybrid::turnValue;
            	if (gyroAngle >= Hybrid::turn2Angle)
	        	{
	        		Hybrid::state = Hybrid::driveToBridgeState;
	        		Hybrid::driveSpeed = -Hybrid::moveValueSlow;
	        		Hybrid::driveTurn = 0;
            		gyro->Reset();
            		encoder->Reset();
            	}
            	break;
            case Hybrid::driveToBridgeState:
            	Hybrid::driveSpeed = -Hybrid::moveValueSlow;
            	Hybrid::driveTurn = -0.1 * gyroAngle;
            	if (encoderDist <= Hybrid::distToBridge)
            	{
            		Hybrid::state = Hybrid::tiltBridgeState;
            		Hybrid::driveSpeed = 0;
            		Hybrid::driveTurn = 0;
            		gyro->Reset();
            	}
            	break;
            case Hybrid::tiltBridgeState:
            	// TODO finish this state
            	break;
            }
            
            // Drive all motors needed using set values
            myRobot.ArcadeDrive(Hybrid::driveSpeed, Hybrid::driveTurn);
		}*/
		compressor->Start();
		setDrivePID(0.5, 0, false);
		Wait(2.0);
		shooterJag->Set(-0.65);
		Wait(1.0);
		elevatorJag->Set(-1.0);
		Wait(3.0);
		tiltSolenoid->Set(true);
		setDrivePID(-1.0, 0, false);
		Wait(2.0);
		elevatorJag->Set(0.0);
		shooterJag->Set(0.0);
	}

	void OperatorControl(void)
	{
		float stickX = 0;
		float stickY = 0;
		float stickZ = 0;
		bool shiftButton = false; //Button 2
		bool tiltButton = false; //Button 7
		bool hoodButton = false; //Button 3
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
		
		shooterCounter->Start();
		
		leftDrivePID->Enable();
		rightDrivePID->Enable();
		shooterPID->Enable();
		
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
			shootButton = stick.GetRawButton(1);
			hoodButton = stick.GetRawButton(3);
			reverseElevatorButton = stick.GetRawButton(10);
			ballCountReset1 = stick.GetRawButton(8);
			ballCountReset2 = stick.GetRawButton(9);
			intakeVal = intakeSensor->Get();
			
			// adjust joystick by dead zone
			if (fabs(stickX) <= Constants::stickDeadZone)
				stickX = 0;
			if (fabs(stickY) <= Constants::stickDeadZone)
				stickY = 0;
			if ((stickX == 0 && stickY == 0) || stickZ >= 0)
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
			}
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
			if (tiltButton)
			{
				tiltSolenoid->Set(true);
			}
			else
			{
				tiltSolenoid->Set(false);
			}
			
			// Raise aiming hood
			if (hoodButton)
			{
				hoodSolenoid->Set(true);
			}
			else
			{
				hoodSolenoid->Set(false);
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
			}
			
			// In case we want to lower the balls
			if (reverseElevatorButton)
			{
				elevatorJag->Set(Constants::elevatorReverseSpeed);
			}
			// If we're trying to load the shooter and the shooter is spinning fast enough
			//else if (shootButton && timeCurr - spinUpTime >= Constants::spinUpTime) // Open loop shooting
			else if (shootButton && fabs(fabs(Constants::shootSpeed * Constants::maxShootSpeed) - shooterCounter->GetSpeed()) <= Constants::shootSpeedTolerance * Constants::maxShootSpeed) // Closed loop shooting
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
			
			if (shootButton)
			{
				//shooterJag->Set(Constants::shootSpeed);
				shooterPID->SetSetpoint(Constants::shootSpeed);
				
			}
			else
			{
				spinUpTime = timeCurr;
				//shooterJag->Set(0.0);
				shooterPID->SetSetpoint(0.0);
			}
			
			//Reset ballCount if both reset buttons are pressed together
			if (ballCountReset1 && ballCountReset2)
			{
				ballCount = 0;
			}
			
			// run intake if we have less than 3 balls
			//if (ballCount < 3)
			//{
				intakeJag->Set(1.0);
			//}
			/* run intake in reverse if we have at least 3 balls
			 * and there aren't any left in the intake
			 */
			//else if (timeCurr - intakeTime >= Constants::elevatorIntakeTime)
			//{
				//intakeJag->Set(-1.0);
			//}
			
			//printf("Time:%f Speed:%f\n", timer1->Get(), shooterCounter->GetSpeed());
			//timeCurr = timer1->Get();
			//timeElap = timeCurr - timeLast;
			
			//if (timeElap >= 0.5)
			//LowPassFilter(leftDriveEncoder->GetRate());
			
			smartDashboard->PutDouble("distLeft", leftDriveEncoder->GetDistance());
			smartDashboard->PutDouble("rateLeft", leftDriveEncoder->GetRate());
			smartDashboard->PutInt("ticksLeft", leftDriveEncoder->GetRaw());
			/*smartDashboard->PutDouble("leftOut", leftDrivePID->Get());
			smartDashboard->PutDouble("leftError", leftDrivePID->GetError());
			smartDashboard->PutDouble("leftSet", leftDrivePID->GetSetpoint());*/
			smartDashboard->PutDouble("distRight", rightDriveEncoder->GetDistance());
			smartDashboard->PutDouble("rateRight", rightDriveEncoder->GetRate());
			smartDashboard->PutInt("ticksRight", rightDriveEncoder->GetRaw());
			smartDashboard->PutDouble("rightOut", rightDrivePID->Get());
			smartDashboard->PutDouble("rightError", rightDrivePID->GetError());
			smartDashboard->PutDouble("rightSet", rightDrivePID->GetSetpoint());
			smartDashboard->PutDouble("stickY", stickY);
			smartDashboard->PutInt("ballCount", ballCount);
			smartDashboard->PutDouble("gyro", gyro->GetAngle());
			smartDashboard->PutInt("intake", intakeVal);
			//timeLast = timeCurr;
			//printf("rate: %f   error: %f   out: %f\n", leftDriveEncoder->GetRate(), leftDrivePID->GetError(), leftDrivePID->Get());
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

