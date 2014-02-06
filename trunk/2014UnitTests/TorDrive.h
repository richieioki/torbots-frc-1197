#pragma once

#include "WPILib.h"


class TorDrive
{
public:
  TorDrive(Talon& left1, Talon& left2, Talon& right1, Talon& right2);
  TorDrive(Talon& left, Talon& right);
  void SetDrive(float leftSpeed, float rightSpeed);
  //The following two classes might be private if we always use the above Drive()
  void SetLeft(float speed);
  void SetRight(float speed);
 
  
private:
  Talon& m_left1;
  Talon& m_left2;
  Talon& m_right1;
  Talon& m_right2;
  int numOfJags;
};
