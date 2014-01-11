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
	DriverStationLCD *ds;
public:
	RobotDemo():
		//myRobot(3,4,1, 2),	// these must be initialized in the same order
		stick(1)		// as they are declared above.
	        
	{
	            jag1 = new Jaguar(1);
	            jag2 = new Jaguar(2);
	            //myRobot.SetExpiration(0.1);
	            
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
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl()
	{
		
	        ds->Printf(DriverStationLCD::kUser_Line1, 1, "Motor Test");
	        ds->UpdateLCD();
	        //myRobot.SetSafetyEnabled(true);
		while (IsOperatorControl())
		{ 
		    //myRobot.ArcadeDrive(stick);
		    
		    if (stick.GetRawButton(1))
		      {
		        jag1->Set(0.3);
		        jag2->Set(0.3);
		        
		      }
		    else if (stick.GetRawButton(3))
		      {
		        jag1->Set(-0.3);
		        jag2->Set(-0.3);
		      }
		    else
		      {
		        jag1->Set(0.0);
		        jag2->Set(0.0);
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

