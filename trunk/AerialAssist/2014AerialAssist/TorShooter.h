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
  enum cageState {
    Load, Drive, Pass, Shoot
  };
  TorShooter(Joystick& myJoystick);
  void Fire();
  void Run();
  
  bool IsLoaded();
  void MoveCage(cageState cg);
  cageState GetCageState();
  shooterState GetShooterState();
  
  void SetJagSpeed(float speed);
  float GetJagSpeed();
  
  void ManualFire();

private:
  void ManageState();
  void MoveShooterDown(bool downFlag);
  void MoveLoaderDown(bool downFlag);
  
  Joystick& m_stick;
  
  float currentJagSpeed;
  bool runButton;
  bool fireButton;
  bool loadOverride;
  
  bool loaderDown;
  bool shooterDown;
  
  shooterState state;
  cageState cage;
  Jaguar *topWheelJag;
  Jaguar *topWheelJag1;
  Jaguar *bottomWheelJag;
  Jaguar *bottomWheelJag1;
  Jaguar *loaderBarJag;
  Solenoid *loadSolenoid;
  Solenoid *fireSolenoid;
};
