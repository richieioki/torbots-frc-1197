#include "WPILib.h"
#include "Autonomous.h"
#include "TorbotDrive.h"
#include "TorJagDrive.h"
#include "Consts.h"

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
	TorbotDrive *myTorbotDrive;
	TorJagDrive *myJagDrive;
	Encoder *wheelEncoderLeft;
	Encoder *wheelEncoderRight;
	Jaguar *leftDriveJag;
	Jaguar *rightDriveJag;
	Gyro *gyro;
	Solenoid *hangSolenoid;
	Compressor *compressor;
	//Autonomous *autonomous; //Needs to be initialized.

public:
	RobotDemo():
		myRobot(1, 2),	// these must be initialized in the same order
		stick(1)		// as they are declared above.
	{
		myRobot.SetExpiration(0.1);
		compressor = new Compressor(1, 1); //change ports for actual bot
		compressor->Start();
		hangSolenoid = new Solenoid (Consts::PICKUP_ARM_SOLENOID);
		leftDriveJag = new Jaguar(1, 4);
		rightDriveJag = new Jaguar(1, 3);
		wheelEncoderLeft = new Encoder(2, 5, 2, 6); //a channel, b channel
		wheelEncoderRight = new Encoder(1, 3, 1, 4); //a channel, b channel
		gyro = new Gyro(1, 1);
		gyro->Reset();
		myJagDrive = new TorJagDrive(*leftDriveJag, *rightDriveJag);
		myTorbotDrive = new TorbotDrive(stick, *myJagDrive, *gyro, *wheelEncoderRight);
		
		//intialize varibles
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous()
	{
	  //autonomous->runAutonomous();
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl()
	{
	  /*
	   * Order of while loop
	   * 1: check if states should have changed
	   * 2: evaluate and execute inputs
	   * 3: execute states
	   */
	  myTorbotDrive->resetEncoder();
	  gyro->Reset();
	  compressor->Start();
	  bool shiftToggle = false;
	  while (IsOperatorControl() && IsEnabled())
	    {
	      myTorbotDrive->ArcadeDrive(true);
	      if (stick.GetRawButton(3))
	        {
	          shiftToggle = true;
	        }
	      else if (stick.GetRawButton(5))
	        {
	          shiftToggle = false;
	        }
	      myTorbotDrive->shiftGear(shiftToggle);
	      
	    }
	  
	}
	
	/**
	 * Runs during test mode
	 */
	void Test() {

	}
};

START_ROBOT_CLASS(RobotDemo);
