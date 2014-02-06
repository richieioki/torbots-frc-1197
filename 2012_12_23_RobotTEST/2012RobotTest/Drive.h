/*
 * Drive.h
 *
 *  Created on: Nov 2, 2012
 *      Author: TorBot
 */

/*
 * Description:
 */
#include "Joystick.h"
#include "RobotDrive.h"
#include "PIDController.h"

class myRobotDrive
{
public:
	
	//constructor version 1
	myRobotDrive(UINT32 joystickPort, UINT32 leftDriveJag, UINT32 rightDriveJag, bool usePID=FALSE);
	
	//constructor version 2
//	myRobotDrive(UINT32 joystickPort, UINT32 leftFrontDriveJag, UINT32 leftBackDriveJag, 
//			UINT32 rightFrontDriveJag, UINT32 rightBackDriveJag, bool usePID);
	
	void driveWithPID(float Pgain, float Igain, float Dgain);
	
	void ArcadeDrive();
	
	~myRobotDrive();
	
	
private:
	RobotDrive* myRobot;
	Joystick* stick;
	
	
	
	
};
