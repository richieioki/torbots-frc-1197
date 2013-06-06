#pragma once

#include "Consts.h"
#include "WPILib.h"
#include "TorbotDrive.h"
#include "DrivePIDOutput.h"
#include "TorShooter.h"
#include "Math.h"

class TorAuto
{
public:

  TorAuto(TorbotDrive& theDrive, Gyro& gyro, TorShooter& theShooter);
  void AutoSelect(int select);
  void Auto1();
  void Auto2();
  
  
  
//  void TorbotDrive::ArcadeDrive(bool squaredInputs);
//  void DriveToTheta(float theta, float motorSpeed, float distanceInches);
//  void DriveStraight(float motorSpeed, float distanceInches);
//  void TurnToTheta (float motorSpeed, float destTheta);
//  //Example of inline getter and setter
//  void SetSpeed(float value) { m_speed = value;}
//  float GetSpeed () {return m_speed;}
//  float getDistance();
//  void resetEncoder();
//  
private:
  TorbotDrive& m_drive;
  Gyro& m_gyro;
  TorShooter& m_shooter;

};
