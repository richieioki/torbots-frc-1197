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
  TorShooter(Joystick& myJoystick1, Joystick& myJoystick2);
  void Fire();
  void Run();
  
  bool IsLoaded();
  void MoveLoaderDown(bool downFlag);
  bool TorShooter::IsLoaderDown();
  void MoveShooter(float speed);
  
  void SetJagSpeed(float speed);
  float GetJagSpeed();
  
  float ShooterSpeed();
  
  void ManualFire();

private:  
  Joystick& m_stick;
  Joystick& tartarus;
  
  float currentJagSpeed;
  bool runButton;
  bool fireButton;
  bool loadOverride;
  
  bool loaderDown;
  bool shooterDown;
  
  float throttleValue;
  
  Jaguar *topWheelJag;
  Jaguar *topWheelJag1;
  Jaguar *bottomWheelJag;
  Jaguar *bottomWheelJag1;
  Jaguar *loaderBarJag;
  Jaguar *cageJag;
  Solenoid *loadSolenoid;
  Solenoid *fireSolenoid;
};
