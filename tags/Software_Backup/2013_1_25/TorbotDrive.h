#pragma once

#include "Consts.h"
#include "WPILib.h"
#include "TorJagDrive.h"

class TorbotDrive
{
public:

  TorbotDrive(Joystick& theJosytick, TorJagDrive& jagDrive);
  void DriveToTheta(float destTheta);
  void TurnToTheta (float destTheta);
  //Example of inline getter and setter
  void SetSpeed(float value) { m_speed = value;}
  float GetSpeed () {return m_speed;}
  
private:
  Joystick& m_joystick;
  TorJagDrive& m_jagDrive;
  float m_speed;
};
