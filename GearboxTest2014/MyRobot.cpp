#include "WPILib.h"

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 
class RobotDemo : public SimpleRobot
{
	//RobotDrive myRobot; // robot drive system
	Joystick stick; // only joystick
	Jaguar *jag1; //Jaguar used to control the speed of the motor
	Jaguar *jag2;
	Jaguar *jag3;
	Jaguar *jag4;
	DriverStationLCD *ds;
public:
	RobotDemo():
		//myRobot(3,4),	// these must be initialized in the same order
		stick(1)		// as they are declared above
	{
	  //myRobot.
	            jag1 = new Jaguar(1);
	            jag2 = new Jaguar(2);
	            jag3 = new Jaguar(3);
	            jag4 = new Jaguar(4);
	            ds = DriverStationLCD::GetInstance();
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous()
	{
		//myRobot.SetSafetyEnabled(false);
		//myRobot.Drive(-0.5, 0.0); 	// drive forwards half speed
		Wait(2.0); 				//    for 2 seconds
		//myRobot.Drive(0.0, 0.0); 	// stop robot
		/*
		myRobot.ArcadeDrive(0.5, 0);
		Wait(1.0);
		myRobot.StopMotor();
		Wait(1.0);
		myRobot.ArcadeDrive(-0.5, 0);
		Wait(1.0);
		myRobot.StopMotor();
		Wait(1.0);
		myRobot.ArcadeDrive(0.1, 0.8);
		Wait(1.0);
		myRobot.StopMotor();
		*/
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl()
	{

	  ds->Clear();
	  ds->UpdateLCD();
	  //ds->Printf(DriverStationLCD::kUser_Line1, 1, "Motor Test");
	  //ds->UpdateLCD();
	  //myRobot.SetSafetyEnabled(true);
	  while (IsEnabled())
	    { 
	      //myRobot.ArcadeDrive (stick);

	      //jag1->Set(0.3);
	      //jag2->Set(0.3);
	      //jag3->Set(0.3);
	      //jag4->Set(0.3);




	      //myRobot.ArcadeDrive(stick);

	      if (stick.GetRawButton(1))
	        {
	          jag1->Set(-0.8);
	          jag2->Set(-0.8);
	          jag3->Set(-0.8);
	          jag4->Set(-0.8);

	        }
	      else if (stick.GetRawButton(3))
	        {
	          jag1->Set(0.8);
	          jag2->Set(0.8);
	          jag3->Set(0.8);
	          jag4->Set(0.8);
	        }
	      else
	        {
	          jag1->Set(0.0);
	          jag2->Set(0.0);
	          jag3->Set(0.0);
	          jag4->Set(0.0);
	        }		    
	    }
	}

	/**
	 * Runs during test mode
	 */
	void Test() {

	}

	
};

START_ROBOT_CLASS(RobotDemo);

