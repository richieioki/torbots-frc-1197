#pragma once

#include "Consts.h"
#include "WPILib.h"
#include "TorbotDrive.h"
#include "TorTargetAcquire.h"
#include "TorFeeder.h"
#include "SpeedCounter.h"
#include "Math.h"


class TorShooter 
{
public:
  enum shooterState {
    Init, Acquire, TargetSt, StartMove, Moving, Stopped, Shooting
  };
  
  enum ShooterArmPosition {
    Straight, Targeting, Loading, Climbing 
  };
  
  enum ShooterArmPOTPosition {
    StraightPOT = 220, TargetingPOT = 270, LoadingPOT = 0, ClimbingPOT = 500  
  };
  
  TorShooter(Joystick& theJoystick1, Joystick& theJoystick2, TorbotDrive& theDrive, TorFeeder& theFeeder, AnalogChannel& thePOT, DriverStationLCD& theDS);
  void Run();
  void Start();
  void Stop();
  bool Shoot();
  bool Target();
  void SetArmPosition(TorShooter::ShooterArmPosition position);
  int GetArmPosition();
//  void MoveArm(double theta);
  bool isMotorReady();
  bool isReadyToShoot();
  shooterState GetState();
  double GetArmSetPoint();
  double GetTurnSetPoint();
  

  
private:
  void ManageState();
  
  Joystick& m_joystick1;
  Joystick& m_joystick2;
  TorbotDrive& m_drive;
  TorFeeder& m_feeder;
  AnalogChannel& m_shooterArmPOT;

  Jaguar *frontShootMotor;
  Jaguar *rearShootMotor;
  Solenoid *shootSolenoid;
    
  shooterState state;
  double ShooterArmSetPoint;
  double ShooterTurnSetPoint;
  double ShooterArmYTheta;
  double ShooterArmYThetaAdjusted;
  bool isDiskRight;
    
  SpeedCounter *shooterCounter1;
  SpeedCounter *shooterCounter2;

  TorTargetAcquire *myTorTarget;
  
  DriverStationLCD& ds;

  bool manualTriggerButton;
  bool triggerButton;
  float targetSetPoint;
  
    float mainMotorSpeed;
    float feedMotorSpeed;
    
};
