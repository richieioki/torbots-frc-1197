#pragma once

#include "Consts.h"
#include "WPILib.h"
#include "TorJagDrive.h"
#include "DrivePIDOutput.h"
#include "Math.h"

class TorbotDrive
{
public:

  TorbotDrive(Joystick& theJosytick, TorJagDrive& jagDrive, Gyro& gyro, Encoder& encoder);
  void ArcadeDrive(bool squaredInputs);
  void DriveToTheta(float theta, float motorSpeed, float distanceInches);
  void DriveStraight(float motorSpeed, float distanceInches);
  float TurnToTheta (float motorSpeed, float destTheta, bool waitFinish);
  //Example of inline getter and setter
  void SetSpeed(float value) { m_speed = value;}
  float GetSpeed () {return m_speed;}
  void disableTurnPID();
  float getDistance();
  void resetEncoder();
  void shiftGear(bool lowGearFlag);

private:
  Joystick& m_joystick;
  TorJagDrive& m_jagDrive;
  Gyro& m_gyro;
  float m_speed;
//  PIDController *drivePID;
//  PIDController *turnPID;
  Encoder& m_encoder;
  Solenoid *shiftSolenoid;
  Solenoid *shiftSolenoid2;
};
