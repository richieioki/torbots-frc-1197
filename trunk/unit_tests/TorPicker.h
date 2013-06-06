#pragma once

#include "WPILib.h"
#include "TorFeeder.h"
#include "TorbotDrive.h"

class TorPicker
{
public:

  enum pickerState {
    Init, StartMove, Seeking, Picking
  };
  


  TorPicker(Joystick& theJoystick, TorbotDrive& drive, TorFeeder& torFeeder);

  void Run();
  void ManageState();
  bool isReadyToPickup(); 
  bool senseDisk(); 
  void grabDisk(); 
  void deployPicker(bool deploy); 
  bool isPickerDeployed();
  void setSuction(bool suctionOn);
  pickerState GetState();
  
private:
  Joystick& m_joystick;
  TorbotDrive& m_tordrive;
  TorFeeder& m_feeder;

  Solenoid *pickerSolenoid;
  Solenoid *suctionSolenoid;
  DigitalInput *diskSwitch;
  DigitalInput *retractSwitch;
  DigitalInput *diskSensor;
  
  pickerState state;
  int pickupButton;
  
};
