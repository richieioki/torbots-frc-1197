#include "WPILib.h"
#include <math.h>

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
	Counter *shooterCounter;	//Gear tooth counter to control shooter speed
	SmartDashboard *smartDashboard; //Sends data from robot to laptop
	Timer *timer;					//Counts rev/sec
	Jaguar *armJag1;
	Jaguar *armJag2;

public:
	RobotDemo(void):
		myRobot(1, 2, 3, 4),	// these must be initialized in the same order
		stick(1)				// as they are declared above.
	{
		//myRobot.SetExpiration(0.1);
		gyro = new Gyro(1, 1);
		shooterCounter = new Counter(1);
		timer = new Timer();
		armJag1 = new Jaguar(6);
		armJag2 = new Jaguar(7);
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
		smartDashboard = SmartDashboard::GetInstance();
		//myRobot.SetSafetyEnabled(true);
		float robotAngleI;
		float robotAngleF;
		float drift = 0;
		float avgDrift;
		float angleAdjustment;
		gyro->Reset();
		shooterCounter->Start();
		timer->Start();
		float stickY;
		float jagOut;
		float speed;
		float error;
		float pGain = 8;
		float count;
		float timeCurr = 0;
		float timeLast = 0;
		float timeElap = 0;
		int cycles = 0;
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
			stickY = stick.GetY();
			if(stick.GetRawButton(1))
			{
				stickY = -0.7;
			}
			else if(stick.GetRawButton(2))
			{
				stickY = -0.7;
			}
			//armJag1->Set(stickY);
			//armJag2->Set(speed);
			//speed *= 36;
			timeCurr = timer->Get();
			timeElap = timeCurr - timeLast;
			if (timeElap >= 0.03)
			{
				count = shooterCounter->Get();
				if (timeElap != 0)
					speed = count / (6 * timeElap);
				else
					speed = 0;
				
				if (jagOut >= 0)
					error = max(min(stickY - speed/50, 1), -1);
				else
					error = max(min(stickY + speed/50, 1), -1);
				
				smartDashboard->PutDouble("shooter count", count);
				smartDashboard->PutDouble("rev/sec", speed);
				smartDashboard->PutDouble("error", error);
				smartDashboard->PutDouble("stickY", stickY);
				
				shooterCounter->Reset();
				timeLast = timeCurr;
				cycles = 0;
				
				printf("time=%f error=%f speed=%f\n", timeCurr, error, speed/50);
			}
			jagOut = max(min(error*pGain,1),-1);
			armJag1->Set(jagOut);
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

