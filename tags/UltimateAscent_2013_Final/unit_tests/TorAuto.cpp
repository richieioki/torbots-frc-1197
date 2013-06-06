#include "TorAuto.h"

TorAuto::TorAuto(TorbotDrive& theDrive, Gyro& gyro, TorShooter& theShooter) 
: m_drive(theDrive), m_gyro(gyro), m_shooter(theShooter)
{

}

void TorAuto::AutoSelect(int select)
{
  switch (select)
  {
  case 1:
    Auto1();
    break;

  case 2:
    Auto2();
    break;

  } // end switch select

}// end AutoSelect



// Auto1
// start from behind pyramid with 3 disks. 
// Drive out to spot, acquire high target and shoot
void TorAuto::Auto1()
{
// start on back side of pyramid, facing away.

  // drive a bit off of pyramid so we can make a turn
  m_drive.DriveStraight(0.25, 12.0);
  
  // turn left 90 (-90)
  
  
  // drive straight a distance
  
  
  // turn left 110 (-110)
  
  
  // find high target
  
  
  // drive straight a distance
  
  
  // find high target (verify if needed)
  
  
  // fire 3 shots
  

  // reset to operator control starting position
  
  
  
} // end Auto1



// Auto1
// start from front side of pyramid with 2 disks. 
// don't move, acquire high target and shoot
void TorAuto::Auto2()
{
// start on front side of pyramid, facing target

  
  // find high target (verify if needed)
  
  
  // fire 2 shots
  

  // reset to operator control starting position
  
  
  
} // end Auto2


