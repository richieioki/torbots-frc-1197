#include "TorFeeder.h"

TorFeeder::TorFeeder() 
{
  diskSensor = new DigitalInput(10);						// break beam sensor in the bottom slot where the disk is loaded from the picker
  diskOrientionSensor = new DigitalInput(11);				// limit switch in feeder to determine if disk is upside down
  elevator = new Jaguar(1, 10);                             // motor to move feeder
  elevatorPOT = new AnalogChannel(3);						// POT to keep track of the feeder position

  // init feeder array
  for (int i=0; i < 4; i++)
    {
      feederDisks[i] = false;
      diskOrientation[i] = true;
    }
  NumDisks = 0;
  readyToFire = false;

}  //Constructor



// called by Picker object to check if there is room to load another disk
bool TorFeeder::isReadyToLoad ()							
{
  if (feederDisks[3])										// a disk in the loading slot means there is no more room
    return false;
  else
    return true;
}


// called by Shooter object to check if there is a disk ready to shoot
// if not, call PrepareToFire to load the next available disk to the ready position
bool TorFeeder::isReadyToShoot () 
{
  readyToFire = false;

  if(feederDisks[0])										// Disk in slot 1
    readyToFire = true; 
  else if (NumDisks > 0)
    {
      PrepareToFire();						// move the next available disk to the ready position

      readyToFire = true;
    }
// debug code
  readyToFire = true;
  
  return readyToFire;
}


// called from main operator loop to check the loader for new disks to load
void TorFeeder::checkDiskLoader ()
{
  //if break beam
  if ((diskSensor->Get() == 0) && !feederDisks[3])		// if beam is broken and we did not count this one already, indicates a new disk in the feeder load slot
    {
      NumDisks++;
      feederDisks[3] = true;
      diskOrientation[3] = diskOrientionSensor->Get() == 0; // if sensor==0, orientation is true/right-side up

      if(!feederDisks[0])				        // no disk in the top slot, we can raise the new disk up 1 slot
        {
          Raise1();						// raise the feeder up 1 slot to free up the loader slot
        }
    }

}


// called by shooter to determine if disk is right side up (true) or upside down (false)
bool TorFeeder::isDiskRight()
{
    return diskOrientation[0];
}

// raise disks up until the top slot is filled in preparation to shoot
void TorFeeder::PrepareToFire () 
{
  while (!feederDisks[0] && (NumDisks > 0))
    {
      Raise1();
    }
}


// raise the elevator 1 slot at a time
void TorFeeder::Raise1 () 
{
  // move the elevator motor up 1 slot
  elevator->Set(0.2);
  Wait(0.5);				// check POT rather than run a specific time. Hopefully this can be done without PID
  elevator->Set(0.0);

  // update feederDisks and diskOrientation arrays, moving each disk up one slot
  for (int i=0; i < 3; i++)
    {
      feederDisks[i] = feederDisks[1+1];
      diskOrientation[i] = diskOrientation[i+1];
    }
  feederDisks[3] = false;									// bottom slot is empty now that we moved everything up
  diskOrientation[3] = true;								// default to true=right side up

}


// called from Shooter when a disk is shot
void TorFeeder::ShotDisk() {
  //disk was shot so we have to decrement the number of disks
  //feederDisks[0] = false;
  NumDisks--;
  if (NumDisks < 0)
    NumDisks = 0;
  
  Raise1();
}

// called from autonomous main to initialize the feed data structures with the appropriate number of disks
void TorFeeder::InitalizeAuto(int numberDisks) 
{
  //Autonomous calls this to initialize the number of disks loaded manually
  for (int i=0; i < numberDisks; i++)
    {
      feederDisks[i] = true;
      diskOrientation[i] = true;							// default to true=right side up
    }
  NumDisks = numberDisks;

  readyToFire = true;
}


void TorFeeder::GetState ()
{

}
