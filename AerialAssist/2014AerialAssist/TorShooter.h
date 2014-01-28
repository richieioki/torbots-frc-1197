#pragma once

#include "Consts.h"
#include "WPILib.h"
#include "TorbotDrive.h"
/*
 * Container for jags required to run the shooter.  
 * Getters and setters functions to read and set the shooter jags.
 * Run state machine based on sensors : Ready, Not Ready, and Loading?
 */

class TorShooter {
public:
  enum shooterState {
    Init, Loading, Loaded, Running
  };
  TorShooter(Joystick& myJoystick);
  void Fire();
  void Run();
  
  bool IsLoaded();
  shooterState GetShooterState();
  void MoveLoaderDown(bool downFlag);
  bool TorShooter::IsLoaderDown();
  void MoveShooter(float speed);
  
  void SetJagSpeed(float speed);
  float GetJagSpeed();
  
  void ManualFire();

private:
  void ManageState();
  
  Joystick& m_stick;
  
  float currentJagSpeed;
  bool runButton;
  bool fireButton;
  bool loadOverride;
  
  bool loaderDown;
  bool shooterDown;
  
  shooterState state;
  Jaguar *topWheelJag;
  Jaguar *topWheelJag1;
  Jaguar *bottomWheelJag;
  Jaguar *bottomWheelJag1;
  Jaguar *loaderBarJag;
  Jaguar *cageJag;
  Solenoid *loadSolenoid;
  Solenoid *fireSolenoid;
};