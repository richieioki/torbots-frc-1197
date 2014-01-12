#include "WPILib.h"
#include "Autonomous.h"

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
	Autonomous *autonomous; //Needs to be initialized.

public:
	RobotDemo():
		myRobot(1, 2),	// these must be initialized in the same order
		stick(1)		// as they are declared above.
	{
		myRobot.SetExpiration(0.1);
		
		//intialize varibles
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous()
	{
	  autonomous->runAutonomous();
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl()
	{
	  /*
	   * Order of while loop
	   * 1: check if states should have changed
	   * 2: evaluate inputs
	   * 3: execute states
	   */
	  
	  
	}
	
	/**
	 * Runs during test mode
	 */
	void Test() {

	}
};

START_ROBOT_CLASS(RobotDemo);
