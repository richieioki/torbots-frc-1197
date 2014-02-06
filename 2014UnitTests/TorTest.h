#pragma once

#include "WPILib.h"
#include "TorDrive.h"


class TorTest {
        
public:

TorTest(Joystick& theStick, DriverStationLCD *dsIn, TorDrive *DriveIn);

void testDrive();
void testCompressor();
void testSolenoids();
void testFire();
void testSensors();
void testPOTAccuracy();
void RunTests();
void gyroTest();

private:
        DriverStationLCD *m_ds;
        DriverStation *m_driverStation;
        TorDrive *m_myTorDrive;
        Timer *timer;
        Joystick& m_stick;
        float timeStart;
};
