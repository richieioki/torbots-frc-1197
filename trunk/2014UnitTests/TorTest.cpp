#include "WPILib.h"
#include "TorDrive.h"
#include "TorTest.h"

  
TorTest::TorTest(Joystick& theStick, DriverStationLCD *dsIn, TorDrive *DriveIn)
: m_stick(theStick), m_ds(dsIn), m_myTorDrive(DriveIn)
{
 
}

void TorTest::testDrive()
{
  m_myTorDrive->SetDrive(0.5,0.5);
}
void TorTest::testSensors()
{
  
}


