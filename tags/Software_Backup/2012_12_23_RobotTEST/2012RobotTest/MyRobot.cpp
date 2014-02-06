#include "WPILib.h"
#include "Drive.h"

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 
class RobotDemo : public SimpleRobot
{
//	RobotDrive myRobot; // robot drive system
//	Joystick stick; // only joystick
	myRobotDrive *robotDrive;
	Compressor *compressor;
	Solenoid *hood; //da hood.
	Solenoid *shifter; //Super Shifter Gear Change
	Solenoid *tilter; //Bridge Tilter
	Jaguar *intake;
	Jaguar *elevator;
	Jaguar *shooter;
	DigitalInput *intakeSensor;

public:
	RobotDemo(void)//:
//		myRobot(10, 9),	// these must be initialized in the same order
//		stick(1)		// as they are declared above.
	{
		robotDrive = new myRobotDrive(1,10,9);
		compressor = new Compressor(1,2);
		tilter = new Solenoid(1);
		hood = new Solenoid(2);
		shifter = new Solenoid(3);
		intake = new Jaguar(4);
		intakeSensor = new DigitalInput(9);
		elevator = new Jaguar(3);
		shooter = new Jaguar(6);
//		myRobot.SetExpiration(0.1);
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous(void)
	{
//		myRobot.SetSafetyEnabled(false);
//		myRobot.Drive(0.5, 0.0); 	// drive forwards half speed
//		Wait(2.0); 				//    for 2 seconds
//		myRobot.Drive(0.0, 0.0); 	// stop robot
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl(void)
	{
//		myRobot.SetSafetyEnabled(true);
		compressor->Start();
		int ballcounter = 0;
		while (IsOperatorControl())
		{
//			myRobot.ArcadeDrive(stick); // drive with arcade style (use right stick)
			robotDrive->ArcadeDrive();
			intake->Set(1.0); //clockwise from the back
			/*if (intakeSensor->Get() == 0)
			{
				intake->Set(-1.0);
			}*/
			//Button Controls
//			if (stick.GetRawButton(3))
//			{
//				hood->Set(true);
//			}
//			else
//			{
//				hood->Set(false);
//			}
//			if (stick.GetRawButton(2))
//			{
//				shifter->Set(true);
//			}
//			else
//			{
//				shifter->Set(false);
//			}
//			if (stick.GetRawButton(7) && stick.GetRawButton(8))
//			{				
//				tilter->Set(true);
//			}
//			else
//			{
//				tilter->Set(false);
//			}
//			if (stick.GetRawButton(1))
//			{
//				Wait(2.0);
//				elevator->Set(-1.0);
//			}
//			else
//			{
//				elevator->Set(0.0);
//			}
//			shooter->Set(-0.8);
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

