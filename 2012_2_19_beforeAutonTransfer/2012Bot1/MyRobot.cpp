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

/* Teleop class stores variables specific to Teleoperated mode */
class Teleop
{
public:
	static const float stickDeadZone = 0.05; //if the joystick's value is less than this, it will be considered 0
	static const float ballReleaseSpeedThresh = 35;
	static const float minShootSpeed = 48;
	static void Reset()
	{

	}
};


/* Constants class stores constant values used in both Hybrid and Telep */
class Constants
{
public:
	//								   Circ    spkt count
	static const float encoderToDist = 25.13 / (2 * 1000); // converts encoder ticks to distance (inches)
	static const float drivePGain = 0.06;
	static const float driveIGain = 0.002;
	static const float driveDGain = 0;
	static const float maxFPS = 25;
};

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 
class RobotDemo : public SimpleRobot
{
	Joystick stick; // only joystick
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
	Relay *tiltRelay;
	Solenoid *shiftRelay;
	Relay *hoodRelay;
	SmartDashboard *smartDashboard;
	//static int counter;
	int ballCount;

public:
	RobotDemo(void):
		stick(1)		// as they are declared above.
	{
		leftDriveJag = new Jaguar(1);
		rightDriveJag = new Jaguar(2);
		intakeJag = new Jaguar(4);
		elevatorJag = new Jaguar(3);
		shooterJag = new Jaguar(5);

		leftDriveEncoder = new Encoder(3,4,true, Encoder::k1X);
		rightDriveEncoder = new Encoder(5,6,false, Encoder::k1X);
		
		timer = new Timer();
		shooterCounter = new SpeedCounter(2);
		gyro = new Gyro(1, 1);
		
		intakeSensor = new DigitalInput(7);
		
		leftDrivePID = new PIDController(Constants::drivePGain, Constants::driveIGain, Constants::driveDGain, leftDriveEncoder, leftDriveJag, 0.01);
		rightDrivePID = new PIDController(Constants::drivePGain, Constants::driveIGain, Constants::driveDGain, rightDriveEncoder, rightDriveJag, 0.01);
		shooterPID = new PIDController(6, 0.5, 0, shooterCounter, shooterJag, 0.05);
		
		compressor = new Compressor(1,6);
		tiltRelay = new Relay(3);
		shiftRelay = new Solenoid(1);
		hoodRelay = new Relay(5);
		
		smartDashboard = SmartDashboard::GetInstance();
		
		leftDriveEncoder->SetReverseDirection(false);
		rightDriveEncoder->SetReverseDirection(false);
		leftDriveEncoder->SetDistancePerPulse(Constants::encoderToDist);
		rightDriveEncoder->SetDistancePerPulse(Constants::encoderToDist);
		leftDriveEncoder->SetPIDSourceParameter(Encoder::kRate);
		rightDriveEncoder->SetPIDSourceParameter(Encoder::kRate);
		leftDrivePID->SetInputRange(Constants::maxFPS * -12, Constants::maxFPS * 12);
		rightDrivePID->SetInputRange(Constants::maxFPS * -12, Constants::maxFPS * 12);
		leftDrivePID->SetOutputRange(-1, 1);
		rightDrivePID->SetOutputRange(-1, 1);
		shooterPID->SetOutputRange(-0.01, 1);
		
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
	
	
	/*void LowPassFilter(double leftRate)
	{
		static int counter = 0;
		double sum;
		float samples [9]; 
		samples[counter] = leftRate;
		smartDashboard->PutDouble("samples test", samples[counter]);
		counter++;
		double avg;
		if (samples[9] > 0)
		{
			for (int i = 0; i <= 9; i++)
			{
				sum = sum + samples[i];
			} 
			avg = sum/10;
			sum = 0;
		}
		if (counter > 9)
		{
			counter = 0;
			for (int i = 0; i<=9;i++)
			{
				samples[i] = 0;
			}
		}
		
		smartDashboard->PutDouble("Low pass", avg);
		
	}*/

	void Autonomous(void)
	{
		
	}

	void OperatorControl(void)
	{
		float stickX = 0;
		float stickY = 0;
		float stickZ = 0;
		bool shiftButton = false; //Button 2
		bool tiltButton = false; //Button 7
		bool tiltBool = true; //relay will only update when bool != button
		bool hoodButton = false; //Button 3
		bool hoodBool = true; //relay will only update when bool != button
		bool shootButton = false; //Button 1
		bool ballCountReset1 = false;
		bool ballCountReset2 = false;
		
		float lastShootSpeed = 0;
		int intakeVal = 0; // 0 means the sensors can see each other (no ball in the way) 
		int lastIntakeVal = 0;
		
		float timeCurr = 0;
		float timeLast = 0;
		float timeElap = 0;
		
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
			ballCountReset1 = stick.GetRawButton(8);
			ballCountReset2 = stick.GetRawButton(9);
			intakeVal = intakeSensor->Get();
			
			// adjust joystick by dead zone
			if (fabs(stickX) <= Teleop::stickDeadZone)
				stickX = 0;
			if (fabs(stickY) <= Teleop::stickDeadZone)
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
				shiftRelay->Set(true);
			}
			else
			{
				shiftRelay->Set(false);
			}
			
			/* This part is set up to only call Set when the direction is changed
			 * because spikes hold their set values.
			 * TODO This will need to be changed on the final robot
			 * because the solenoid breakout does not hold set valus.
			 */
			if (tiltButton && !tiltBool)
			{
				tiltRelay->Set(Relay::kForward);
				tiltBool = true;
			}
			else if (!tiltButton && tiltBool)
			{
				tiltRelay->Set(Relay::kReverse);
				tiltBool = false;
			}
			
			if (hoodButton && !hoodBool)
			{
				hoodRelay->Set(Relay::kForward);
				hoodBool = true;
			}
			else if (!hoodButton && hoodBool)
			{
				hoodRelay->Set(Relay::kReverse);
				hoodBool = false;
			}
			
			//Check if we just picked up a ball
			if (lastIntakeVal == 0 && intakeVal == 1)
			{
				ballCount++;
			}
			lastIntakeVal = intakeVal;

			timeCurr = timer->Get();
			timeElap = timeCurr - timeLast;
			if (timeElap >= 0.05)
			{
				timeLast = timeCurr;
				float speed;
				int count = shooterCounter->Get();
				shooterCounter->Reset();
				if (timeElap != 0)
					speed = count / (6 * timeElap);
				else
					speed = 0;
				shooterCounter->SetSpeed(speed);
				
				/* If (the ball was above the threshold, is not currently,
				 * and the trigger is being held down)
				 * (We don't want to think we shot a ball when the wheel
				 * slows down naturally after the trigger is released)
				 * Then we just shot a ball
				 */
				if (lastShootSpeed > Teleop::ballReleaseSpeedThresh && speed <= Teleop::ballReleaseSpeedThresh && shootButton)
				{
					ballCount--;
				}
				
				//printf("time=%f count=%d speed=%f\n", timeCurr, count, speed);
				lastShootSpeed = speed;
			}
			
			// If we're trying to load the shooter and the shooter is spinning fast enough
			if (shootButton && shooterCounter->GetSpeed() >= Teleop::minShootSpeed)
			{
				elevatorJag->Set(1.0);
			}
			// If we need to make room for another ball
			else if (intakeVal == 1)
			{
				elevatorJag->Set(0.5);
			}
			else
			{
				elevatorJag->Set(0.0);				
			}
			
			if (shootButton)
			{
				//shooterJag->Set(1.0);
				shooterPID->SetSetpoint(1.0);
			}
			else
			{
				//shooterJag->Set(0.0);
				shooterPID->SetSetpoint(0.0);
			}
			
			//Reset ballCount if both reset buttons are pressed together
			if (ballCountReset1 && ballCountReset2)
			{
				ballCount = 0;
			}
			
			// run intake if we have less than 3 balls
			if (ballCount < 3)
			{
				intakeJag->Set(-1.0);
			}
			/* run intake in reverse if we have at least 3 balls
			 * and there aren't any left in the intake
			 */
			else if (intakeVal == 0)
			{
				intakeJag->Set(1.0);
			}
			
			//printf("Time:%f Speed:%f\n", timer1->Get(), shooterCounter->GetSpeed());
			//timeCurr = timer1->Get();
			//timeElap = timeCurr - timeLast;
			
			//if (timeElap >= 0.5)
			//LowPassFilter(leftDriveEncoder->GetRate());
			
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
			smartDashboard->PutDouble("stickY", stickY);*/
			smartDashboard->PutInt("ballCount", ballCount);
			//smartDashboard->PutDouble("gyro", gyro->GetAngle());
			//timeLast = timeCurr;
			//printf("rate: %f   error: %f   out: %f\n", leftDriveEncoder->GetRate(), leftDrivePID->GetError(), leftDrivePID->Get());
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

