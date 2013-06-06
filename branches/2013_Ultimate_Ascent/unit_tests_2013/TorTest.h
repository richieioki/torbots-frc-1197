#pragma once

#include "WPILib.h"
#include "TorJagDrive.h"

class TorTest {
	
public:
	//TorTest(DriverStationLCD *dsIn, TorJagDrive *JagDriveIn);
	TorTest(Joystick& theStick, DriverStationLCD *dsIn, TorJagDrive *JagDriveIn);
	void testDrive();
	void testCompressor();
	void testSolenoids();
	void testFire();
	void testGetShootArmPOT();
	void testShootArm();
	void testGetFeedPOT();
	void testRaiseFeedPOT();
	void testbreakBeam();
	void testDiskOrientationSensor();
	void testFeederMagFullSensor();
	void testDiskOrientation();
	void testTorClimber();
	void testClimberLimitSwitch();
	void testSensors();
	void testPOTAccuracy();
	void testClimberMotors();
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
	Joystick& stick1;
	float timeStart;

};
