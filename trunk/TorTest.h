#pragma once

#include "WPILib.h"
#include "TorJagDrive.h"

class TorTest {
	
public:
	TorTest(DriverStationLCD *dsIn, TorJagDrive *JagDriveIn);
	void testDrive();
	void testCompressor();
	void testFire();
	void testGetShootArmPOT();
	void testShootArm();
	void testGetFeedPOT();
	void testRaiseFeedPOT();
	void testbreakBeam();
	void testDiskOrientation();
	void testTorClimber();
	void testPicker();
	void runElevator();
	void bottomTest();
	void gyroTest();
	void RunTests();
	
private:
	DriverStationLCD *ds;
	DriverStation *driverStation;
	TorJagDrive *myJagDrive;
	Timer *timer;
	float timeStart;

};
