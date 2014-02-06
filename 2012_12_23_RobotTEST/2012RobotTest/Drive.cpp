/*
 * Drive.cpp
 *
 *  Created on: Nov 2, 2012
 *      Author: TorBot
 */

#include "Drive.h"

/*
 * Default constructor
 */
myRobotDrive::myRobotDrive(UINT32 joystickPort, UINT32 leftDriveJag, UINT32 rightDriveJag, bool usePID)
:myRobot(new RobotDrive(leftDriveJag,rightDriveJag)), stick(new Joystick(joystickPort))
{

//	if (usePID)
//	{
//		driveWithPID(0.0,0.0,0.0);
//	}
}

//myRobotDrive::myRobotDrive(UINT32 joystickPort, UINT32 leftFrontDriveJag, UINT32 leftBackDriveJag, UINT32 rightFrontDriveJag, UINT32 rightBackDriveJag, bool usePID)
//{
//	if (usePID)
//	{
//		driveWithPID(0.0,0.0,0.0);
//	}
//}

void myRobotDrive::driveWithPID(float Pgain, float Igain, float Dgain)
{
	//empty for now
	//shooterPID = new PIDController(Constants::shootPGain, Constants::shootIGain, Constants::shootDGain, shooterCounter, shooterJag, 0.05);
}

void myRobotDrive::ArcadeDrive()
{
	myRobot->ArcadeDrive(stick);
}

/*
 * Default destructor
 */
myRobotDrive::~myRobotDrive()
{
	
}
