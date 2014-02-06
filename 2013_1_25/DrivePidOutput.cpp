#include "DrivePidOutput.h"

    DrivePIDOutput::DrivePIDOutput(TorJagDrive& theTorJagDrive, DriveModeType driveType)
    :m_torJagDrive (theTorJagDrive), m_driveMode (driveType)
    { 
    }
    
    void DrivePIDOutput::PIDWrite(float output)
    {
      switch (m_driveMode) {
        case DriveMode::Driving:
          //call m_torJagDrive method to set right or left drive to output
          //output is positive, send to left drive only
          //if negative send to right drive only.
          //if zero - send that zero to both sides (drive the minimum speed)
//          if (output <= 0)
//            m_torJagDrive.SetLeft(-output);
//          if (output >= 0)
//            m_torJagDrive.SetRight(output);
          m_torJagDrive.SetLeft(output);
          m_torJagDrive.SetRight(output);
                  
                   
          break;
          
        case DriveMode::Rotating:
          //call method that causes all m_torJagDrive to set all motors to same value
          //send output to the left and -output to the right 
          m_torJagDrive.SetLeft(-output);
          m_torJagDrive.SetRight(output);
                   
          break;
        default:
          //send out an error message or throw 
          //it's a programming error to ever get here.
          break;
      }

    }
