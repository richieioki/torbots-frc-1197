#include "WPILib.h"
#include <math.h>
#include "PIDCounter.h"

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 
class RobotDemo : public SimpleRobot
{
	RobotDrive myRobot; // robot drive system
	Joystick stick; // only joystick
	Gyro *gyro;				//Gyro to get heading of robot
	PIDCounter *shooterCounter;	//Gear tooth counter to control shooter speed
	SmartDashboard *smartDashboard; //Sends data from robot to laptop
	Timer *timer;					//Counts rev/sec
	Jaguar *armJag1;
	Jaguar *armJag2;
	//PIDController *shooterPID;

public:
	RobotDemo(void):
		myRobot(1, 2, 3, 4),	// these must be initialized in the same order
		stick(1)				// as they are declared above.
	{
		//myRobot.SetExpiration(0.1);
		gyro = new Gyro(1, 1);
		shooterCounter = new PIDCounter(2, 1);
		timer = new Timer();
		armJag1 = new Jaguar(6);
		armJag2 = new Jaguar(7);
		//shooterPID = new PIDController(0.1, 0, 0, shooterCounter, armJag2);
		smartDashboard = SmartDashboard::GetInstance();
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous(void)
	{
		myRobot.SetSafetyEnabled(false);
		myRobot.Drive(0.5, 0.0); 	// drive forwards half speed
		Wait(2.0); 				//    for 2 seconds
		myRobot.Drive(0.0, 0.0); 	// stop robot
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl(void)
	{
		//myRobot.SetSafetyEnabled(true);
		float robotAngleI;
		float robotAngleF;
		float drift = 0;
		float avgDrift;
		float angleAdjustment;
		gyro->Reset();
		shooterCounter->Start();
		timer->Start();
		float speed;
		float count;
		float timeCurr = 0;
		float timeLast = 0;
		float timeElap = 0;
		int cycles = 0;
		/*shooterPID->SetInputRange(-36.0, 36.0);
		shooterPID->SetOutputRange(-1.0, 1.0);
		shooterPID->Enable();*/
		while (IsOperatorControl())
		{
			
			/*robotAngleF = gyro->GetAngle();
			if (cycles <= 50000)
			{
				drift += robotAngleF - robotAngleI;
				robotAngleI = robotAngleF;
				avgDrift = drift / cycles;
				angleAdjustment = -drift;
			}
			else
			{
				angleAdjustment -= avgDrift;
				myRobot.ArcadeDrive(-0.5, -0.1 * (robotAngleF + angleAdjustment)); // drive with arcade style (use right stick)
			}
			smartDashboard->PutDouble("drift", drift);
			smartDashboard->PutDouble("cycles", cycles);
			smartDashboard->PutDouble("avg", avgDrift);
			smartDashboard->PutDouble("raw angle", robotAngleF);
			smartDashboard->PutDouble("adj angle", robotAngleF + angleAdjustment);*/
			//myRobot.ArcadeDrive(stick);
			/*if (stick.GetRawButton(6))
			{
				myRobot.ArcadeDrive(-0.5,0.0);
			}
			if (stick.GetRawButton(7))
			{
				myRobot.ArcadeDrive(-0.75, 0.0);
			}
			if (stick.GetRawButton(8))
			{
				myRobot.ArcadeDrive(-1.0,0.0);
			}
			if(stick.GetRawButton(10))
			{
				myRobot.ArcadeDrive(0.5,0.0);
			}
			if(stick.GetRawButton(11))
			{
				myRobot.ArcadeDrive(0.75,0.0);
			}
			if(stick.GetRawButton(9))
			{
				myRobot.ArcadeDrive(1.0,0.0);
			}*/

			cycles++;
			speed = stick.GetY();
			if(stick.GetRawButton(1))
			{
				speed = -0.7;
			}
			else if(stick.GetRawButton(2))
			{
				speed = -0.7;
			}
			//armJag1->Set(speed);
			//armJag2->Set(speed);
			//shooterPID->SetSetpoint(speed * 36.0);
			armJag1->Set(speed);
			if (cycles >= 100)
			{
				count = shooterCounter->Get();
				
				smartDashboard->PutDouble("shooter count", count);
				smartDashboard->PutDouble("rev/sec", shooterCounter->GetSpeed());
				/*smartDashboard->PutDouble("outSpeed", shooterPID->Get());
				smartDashboard->PutDouble("setpoint", shooterPID->GetSetpoint());*/
				smartDashboard->PutDouble("stickY", speed);
				
				cycles = 0;
			}
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

