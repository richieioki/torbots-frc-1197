#pragma once 
//#include "WPILib.h"
#include "TorJagDrive.h"

namespace DriveMode
{
  enum DriveMode{Rotating, Driving};
}
typedef DriveMode::DriveMode DriveModeType;

class DrivePIDOutput : public PIDOutput
  {
public:
    
  DrivePIDOutput(TorJagDrive& theTorJagDrive, DriveModeType driveType);
//    :m_right1 (right1), m_right2(right2), m_left1(left1), m_left2(left2)
//    { 
//    }
    
    void PIDWrite(float output);
 
private: 
    TorJagDrive& m_torJagDrive;
    DriveModeType m_driveMode;
//    Jaguar& m_right1;
//    Jaguar& m_right2;
//    Jaguar& m_left1;
//    Jaguar& m_left2;
    
  };
