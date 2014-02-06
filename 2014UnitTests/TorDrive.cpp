#include "WPILib.h"
#include "TorDrive.h"

  TorDrive::TorDrive(Talon& left1,Talon& left2, Talon& right1, Talon& right2 )
  : m_left1 (left1), m_left2(left2), m_right1(right1), m_right2(right2)
  {numOfJags = 4;}

  TorDrive::TorDrive(Talon& left, Talon& right) : m_left1(left), m_left2(left), m_right1(right), m_right2(right)
  { numOfJags = 2;}

  void TorDrive::SetDrive(float leftSpeed, float rightSpeed)
  {
    SetLeft(leftSpeed);
    SetRight(rightSpeed);
  }

  void TorDrive::SetLeft(float speed)
  {
    // left motors take positive speed to move forward
          if(numOfJags == 2) {
                  m_left1.Set(-speed);
          } else {
                  m_left1.Set(-speed);
                  m_left2.Set(-speed);
          }
    
    //m_left2.Set(-speed);
  }

  void TorDrive::SetRight(float speed)
  {
    // right motors are inverted. Change sign for driving right side
          if(numOfJags == 2) {
                  m_right1.Set(speed);
          } else {
                  m_right1.Set(speed);
                  m_right2.Set(speed);
          }
    //m_right2.Set(speed);	
  }


