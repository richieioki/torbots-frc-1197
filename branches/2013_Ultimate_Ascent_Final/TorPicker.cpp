#include "TorPicker.h"

TorPicker::TorPicker(Joystick& theJoystick, TorbotDrive& drive, TorFeeder& torFeeder) 
: m_joystick(theJoystick), m_tordrive(drive), m_feeder(torFeeder)
{
  pickerSolenoid = new Solenoid(Consts::PICKUP_ARM_SOLENOID);
  suctionSolenoid = new Solenoid(Consts::PICKUP_SUCTION_SOLENOID);
  diskSwitch = new DigitalInput(12);
  retractSwitch = new DigitalInput(13);
  diskSensor = new DigitalInput(9);
  
}  //Constructor 



// Run called from outside. Checks joystick and current state and calls appropriate methods
void TorPicker::Run()
{

  do
    {
      // read joystick controls
        pickupButton = m_joystick.GetRawButton(Consts::PICKUP_BUTTON);


      // update current shooter state
      ManageState();
//      ds.Printf(DriverStationLCD::kUser_Line4, 1, "State: %d Tr: %d", state, triggerButton);
//      ds.UpdateLCD();

    }
  while ((state != TorPicker::Init) && (state != TorPicker::Seeking) && (state != TorPicker::StartMove));


//  ds.Printf(DriverStationLCD::kUser_Line1, 1, "exiting RUN");
//  ds.UpdateLCD();
}

void TorPicker::ManageState()
{
//  ds.Printf(DriverStationLCD::kUser_Line2, 1, "Managing state");
//  ds.UpdateLCD();

  switch (state)
  {
  case TorPicker::Init:
    if (pickupButton && isReadyToPickup())                  // picker button starts the search for disk process from the Init state
        state = TorPicker::StartMove;                       // initiate the shooter arm to the pickup position
    break;

  case TorPicker::StartMove:
    state = TorPicker::Seeking;
    if (!pickupButton)
      state = TorPicker::Init;
    break;
       
    // feeder has been moved into position, start checking the disk sensor
  case TorPicker::Seeking:
  if (!pickupButton)
    state = TorPicker::Init;
  else
    // sensed a disk, grab disk and start picking it up
    if (senseDisk())
      {
        grabDisk();
        state = TorPicker::Picking;
      }
    break;


  case TorPicker::Picking:
    if (!pickupButton)
      state = TorPicker::Init;
    else                                        // picking up the disk, when arm position reaches the retracted state, turn off the suction
      if (!isPickerDeployed())
        {
          setSuction(false);
          state = TorPicker::Init;              // and we are done
        }
    break;


  } // end switch
} // end ManageState


bool TorPicker::isReadyToPickup () 
{
  if (m_feeder.isReadyToLoad())
    {
      // Set state to move Shooter Arm to Pickup Ready position
      state = TorPicker::StartMove;
      return true;
    }
  else
    return false;
}


// check digital break beam sensor to see if a disk is in the "V"
bool TorPicker::senseDisk () 
{
  if (diskSensor->Get() == 0)
    return true;
  else
    return false;
}

// sequential process to deploy, grab and retrieve a disk from the floor
void TorPicker::grabDisk ()
{
  float timeStart = 0;
  Timer *timer = new Timer();
  timer->Reset();
          
  // stop driving
  m_tordrive.SetSpeed(0.0);
  
  // deploy picker
  deployPicker(true);

  // sense picker limit switch indicating we hit a disk
  timeStart = timer->Get();
  while (diskSwitch->Get() == 0)
    {
      Wait(0.01);
      // shouldn't take more than a second to do this
      // break out of loop if it's taking too long. Don't want to get stuck
      if ((timer->Get() - timeStart) >= 1.0)
        break;
    }

  // turn on suction if diskSwitch has been triggered
  if (diskSwitch->Get() == 1)
      setSuction(true);

  // retract picker
  deployPicker(false);

} // end grabDisk


void TorPicker::deployPicker (bool deploy)
{
    pickerSolenoid->Set(deploy);
    // Wait(0.1);
}

// return true if picker is deployed
bool TorPicker::isPickerDeployed()
{
  return pickerSolenoid->Get();
}


void TorPicker::setSuction (bool suctionOn)
{
  pickerSolenoid->Set(suctionOn);
  // Wait(0.1);

}

