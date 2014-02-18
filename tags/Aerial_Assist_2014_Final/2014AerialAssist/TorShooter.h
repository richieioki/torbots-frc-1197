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
  bool IsLoaderDown();
  
  void SetJagSpeed(float speed);
  void SetLoaderBarSpeed(float speed);
  float GetJagSpeed();
  
  float ShooterSpeed();

private:  
  Joystick& m_stick;
  Joystick& tartarus;
  
  float currentJagSpeed;
  bool runButton;
  bool passButton;
  bool catchButton;
  
  bool loaderDown;
  bool shooterDown;
  bool isShooterInit;
  
  float throttleValue;
  
  Talon *topWheelJag;
  Talon *topWheelJag1;
  Talon *bottomWheelJag;
  Talon *bottomWheelJag1;
  Talon *loaderBarJag;
  Solenoid *loadSolenoid;
  Solenoid *fireSolenoid;
};
