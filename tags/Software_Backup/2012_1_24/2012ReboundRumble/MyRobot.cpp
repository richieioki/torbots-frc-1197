#include "WPILib.h"
#include <math.h>

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
	static void Reset()
	{
		
	}
};


/* RobotDim class stores robot data for calculations used in both Hybrid and Telep */
class RobotCalc
{
public:
	//TODO include gear ratio in encoderDist
	//------------------------------ = pi	  *dia/ticks
	static const float encoderToDist = 3.1415 * 8 / 250; // converts encoder ticks to distance (inches)
};

/**
 * This code demonstrates the use of the KinectStick
 * class to drive your robot during the autonomous mode
 * of the match, making it a hybrid machine. The gestures
 * used to control the KinectStick class are described in the
 * "Getting Started with the Microsoft Kinect for FRC" document
 */ 
class RobotDemo : public SimpleRobot
{
	RobotDrive myRobot; // robot drive system
	KinectStick leftArm;	//The Left arm should be constructed as stick 1
	KinectStick rightArm; 	//The Right arm should be constructed as stick 2
	Joystick stick;			//Joystick for teleop control
	Encoder *encoder;	//Wheel encoder to get distance robot has driven
	Gyro *gyro;				//Gyro to get heading of robot
	SmartDashboard *smartDashboard; //Sends data from robot to laptop

public:
	RobotDemo(void):
		myRobot(1, 2),	// these must be initialized in the same order
		leftArm(1),		// as they are declared above.
		rightArm(2),
		stick(1)
	{
		myRobot.SetExpiration(0.1);
		//encoder = new Encoder();	// TODO construct based on robot
		gyro = new Gyro(1, 1);
		smartDashboard = SmartDashboard::GetInstance();
	}

	/* Gets the number of balls in the robot's possession */
	int getBallCount()
	{
		int count = 0;
		return count;
	}
	
	/* Tells the shooter mechanism whether or not to fire
	 * param fire: whether or not to fire
	 * returns: fire param */
	bool fire(bool fire)
	{
		return fire;
	}
	
	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous(void)
	{		
		Hybrid::Reset();

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
		}
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl(void)
	{
		Teleop::Reset();
		myRobot.SetSafetyEnabled(true);
		float stickX;
		float stickY;
		float gyroAngle;
		gyro->Reset();
		while (IsOperatorControl())
		{
			gyroAngle = gyro->GetAngle();
			stickX = stick.GetX();
			stickY = stick.GetY();
			// adjust joystick by dead zone
			if (fabs(stickX) <= Teleop::stickDeadZone)
				stickX = 0;
			if (fabs(stickY) <= Teleop::stickDeadZone)
				stickY = 0;
			
			// TODO remove sample Teleop code:
				myRobot.ArcadeDrive(stick); // drive with arcade style (use right stick)
				Wait(0.005);				// wait for a motor update time
			
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

