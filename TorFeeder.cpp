#include "TorFeeder.h"

TorFeeder::TorFeeder(DriverStationLCD& theDS) : ds(theDS)
{
  diskSensor = new DigitalInput(Consts::FEEDER_DISK_SENSOR_MOD, Consts::FEEDER_DISK_SENSOR);		// break beam sensor in the bottom slot where the disk is loaded from the picker
  diskOrientionSensor = new DigitalInput(Consts::FEEDER_DISK_ORIENT_MOD, Consts::FEEDER_DISK_ORIENT);	// limit switch in feeder to determine if disk is upside down
  elevator = new Jaguar(Consts::FEEDER_ELEVATOR_MOD, Consts::FEEDER_ELEVATOR_JAG);                      // motor to move feeder
  elevatorPOT = new AnalogChannel(Consts::FEEDER_POT_MOD, Consts::FEEDER_POT);			        // POT to keep track of the feeder position
  feederReadySensor = new DigitalInput(Consts::FEEDER_READY_SENSOR_MOD, Consts::FEEDER_READY_SENSOR);

  
  // init feeder array
  for (int i=0; i < 4; i++)
    {
      feederDisks[i] = false;
      diskOrientation[i] = true;
    }
  NumDisks = 0;
  readyToFire = false;

  timer = new Timer();
}  //Constructor



// called by Picker object to check if there is room to load another disk
bool TorFeeder::isReadyToLoad ()							
{
  if (feederDisks[3])										// a disk in the loading slot means there is no more room
    return false;
  else
    return true;
}

//RESETS DISK COUNT TO 0
void TorFeeder::resetDisks()
{
  for (int i=0; i < 4; i++)
      {
        feederDisks[i] = false;
        diskOrientation[i] = true;
      }
  NumDisks = 0;
  resetFeederToLoad();
}




// called by Shooter object to check if there is a disk ready to shoot
// if not, call PrepareToFire to load the next available disk to the ready position
bool TorFeeder::isReadyToShoot (bool override) 
{
  readyToFire = false;
  if (NumDisks > 0 || override)
    {
      if(feederReadySensor->Get() == 1) {
          PrepareToFire();						// move the next available disk to the ready position
      }

      readyToFire = true;
    }

  return readyToFire;
}


// called from main operator loop to check the loader for new disks to load
bool TorFeeder::checkDiskLoader ()
{

  ds.Printf(DriverStationLCD::kUser_Line3, 1, "Pot: %d", elevatorPOT->GetAverageValue());
  ds.UpdateLCD();

  // if break beam
  // sensor gives 0 when broken
  if ((diskSensor->Get() == 0) && NumDisks < 4) 		// if beam is broken and we did not count this one already, indicates a new disk in the feeder load slot
    {
      Raise1();
      NumDisks++;
      feederDisks[3] = true;
      diskOrientation[3] = diskOrientionSensor->Get() == 0;     // if sensor==0, orientation is true/right-side up

      return true;
    }
  else
    return false;

}


// called by shooter to determine if disk is right side up (true) or upside down (false)
bool TorFeeder::isDiskRight()
{
//    return diskOrientation[0];
    return true;

}

// raise disks up until the top slot is filled in preparation to shoot
void TorFeeder::PrepareToFire () 
{
  timer->Stop();
  timer->Reset();
  timer->Start();
//  if(feederReadySensor->Get() == 0) {
//      return;
//  }
  while ((feederReadySensor->Get() == 1) && (timer->Get() < 2.0))
    {
      elevator->Set(0.3);
//      Wait(0.05);
    }
  elevator->Set(0.0);
  timer->Stop();  
}


// raise the elevator 1 slot at a time
void TorFeeder::Raise1 () 
{
  float startPOTVal = elevatorPOT->GetAverageValue();
  float TargetPOT = startPOTVal+Consts::FEEDER_INCREMENT_VAL; 
  float currPOT;
  
  int margin = 10;                                              // margin above and below the target POT value to check to stop feeder
  
  // check if target is beyond the max. reset the target value to be above 0 and below feeder_pot_max
  if(TargetPOT > Consts::FEEDER_POT_MAX) 
    {
      TargetPOT = TargetPOT - Consts::FEEDER_POT_MAX + 1;

      if ((TargetPOT - margin) <= 0.0)                                // don't want our bottom range limit to be negative
        TargetPOT = margin;

    }

  
  // move the elevator motor up 1 slot
  
  timer->Stop();
  timer->Reset();
  timer->Start();
    
  while (feederReadySensor->Get() == 1)     //Raises Feeder as long as we don't hit the top sensor
    {
      elevator->Set(0.3);
      // if feeder ready sensor is tripped, a disk is at the top so we must stop
      if (feederReadySensor->Get() == 0) {
          elevator->Set(0.0);
          break;
      }
          
      currPOT = elevatorPOT->GetAverageValue();
      // if current pot value is within 'margin' points either way of the target, break out and stop the elevator
      if ( (currPOT <= (TargetPOT+margin)) && (currPOT >= (TargetPOT-margin)) ) {
          elevator->Set(0.0);
          break;
      }
      // break out if timer reaches max
      if (timer->Get() > 1.0)
        {
          elevator->Set(0.0);
          break;
        }     
    } // end while
 

   
  elevator->Set(0.0);
  timer->Stop();
  timer->Reset();
  
  
  // update feederDisks and diskOrientation arrays, moving each disk up one slot
  for (int i=0; i < 3; i++)
    {
      feederDisks[i] = feederDisks[i+1];
      diskOrientation[i] = diskOrientation[i+1];
    }
  feederDisks[3] = false;									// bottom slot is empty now that we moved everything up
  diskOrientation[3] = true;								// default to true=right side up

}


void TorFeeder::resetFeederToLoad()
{
  int TargetPOT = 810;
  int margin = 5;

  timer->Stop();
  timer->Reset();
  timer->Start();

  while (feederReadySensor->Get() == 1)     //Raises Feeder as long as we don't hit the top sensor
    {
      elevator->Set(0.3);
      // if feeder ready sensor is tripped, a disk is at the top so we must stop
      if (feederReadySensor->Get() == 0) {
          elevator->Set(0.0);
          break;
      }
      // if current pot value is within 'margin' points either way of the target, break out and stop the elevator
      if ( (elevatorPOT->GetAverageValue() <= (TargetPOT+margin)) && (elevatorPOT->GetAverageValue() >= (TargetPOT-margin)) ) {
          elevator->Set(0.0);
          break;
      }
      // break out if timer reaches max
      if (timer->Get() > 3.0)
        {
          elevator->Set(0.0);
          break;
        }     
    } // end while
 
  elevator->Set(0.0);
  timer->Stop();
  timer->Reset();
  
}


// called from Shooter when a disk is shot
void TorFeeder::ShotDisk() {
  //disk was shot so we have to decrement the number of disks
  //feederDisks[0] = false;
  NumDisks--;
  if (NumDisks < 0)
    NumDisks = 0;
  
  if (NumDisks > 0)
    Raise1();
  else
    resetFeederToLoad();

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
int TorFeeder::getFeederSensor()
{
  return feederReadySensor->Get();
}


void TorFeeder::GetState ()
{

}

void TorFeeder::nudge()
{
  Timer locTimer;
  locTimer.Reset();
  float position;
  position = elevatorPOT->GetAverageValue();

  // nudge down
  elevator->Set(-0.3);
  Wait(0.2);
  elevator->Set(0.0);
  Wait(0.2);
  
  // nudge up
  elevator->Set(0.3);
  locTimer.Start();
  while (locTimer.Get() < 0.4)
    {
      if (feederReadySensor->Get() == 0)
        break;
    }
  elevator->Set(0.0);
  Wait(0.2);

  // nudge back to start
  elevator->Set(-0.3);  
  while (elevatorPOT->GetAverageValue() >= position)
    {
      if (locTimer.Get() >= 0.2)
        break;
    }
  elevator->Set(0.0);

}

