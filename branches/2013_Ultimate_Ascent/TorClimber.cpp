#include "TorClimber.h"


TorClimber::TorClimber(Joystick& theJoystick, TorbotDrive& drive, TorShooter& torShooter, DriverStationLCD& ds) 
: m_joystick(theJoystick), m_tordrive(drive), m_shooter(torShooter), mds(ds)
{
  liftJag1 = new Jaguar(Consts::CLIMBER_LEFT_MOD, Consts::CLIMBER_LEFT_JAG);
  liftJag2 = new Jaguar(Consts::CLIMBER_RIGHT_MOD, Consts::CLIMBER_RIGHT_JAG);

  //intialize sensors here
  leftHandSwitch = new DigitalInput(Consts::LEFT_HAND_SENSOR_MOD, Consts::LEFT_HAND_SENSOR);  // left inside
  rightHandSwitch = new DigitalInput(Consts::RIGHT_HAND_SENSOR_MOD, Consts::RIGHT_HAND_SENSOR);                        // right inside
  leftFootSwitch = new DigitalInput(Consts::LEFT_FOOT_SENSOR_MOD, Consts::LEFT_FOOT_SENSOR);                        // left outside
  rightFootSwitch = new DigitalInput(Consts::RIGHT_FOOT_SENSOR_MOD, Consts::RIGHT_FOOT_SENSOR);                       // right outside

  bottomForkLift = new DigitalInput(Consts::BOTTOM_FORKLIFT_LIMIT_MOD, Consts::BOTTOM_FORKLIFT_LIMIT);                  // restracted/bottom limit switch
  topForkLift = new DigitalInput(Consts::TOP_FORKLIFT_LIMIT_MOD, Consts::TOP_FORKLIFT_LIMIT);                           // extended/top limit switch
  initPosSwitch  = new DigitalInput(Consts::INIT_FORKLIFT_LIMIT_MOD, Consts::INIT_FORKLIFT_LIMIT);                        // initial/starting position limit switch
  
  failed = true;
  
  state = Init;
  // set speeds here. one motor is inverted from the other. this ensures we maintain this inverted relationship
  motorSpeed = 0.5;
  postInitSpeed = 1.0;
  
  timer = new Timer();
}


void TorClimber::Run() {
  //if button pressed
  //climbButton = m_joystick.GetRawButton(1);
// STICK 1, BUTTON 11 (CONSTANT DEFINED) STARTS CLIMBING ACTION
  while (m_joystick.GetRawButton(Consts::CLIMB_BUTTON) && failed){
      mds.Printf(DriverStationLCD::kUser_Line2, 1, "Running Climber");
      mds.UpdateLCD();
      

      failed = ManageState();

      if(state == Stop) {
          
          mds.Printf(DriverStationLCD::kUser_Line4, 1, "Exiting loop, must have reached the top");
          mds.UpdateLCD();
          break;
      }
  }
  liftJag1->Set(0.0);
  liftJag2->Set(0.0);
}


/* Pull state
 * While feet are up -> pull
 * When feet are down -> pull (while feet are down)
 * When feet are up -> state = lift
 * Lift state
 * While hands are up -> lift
 * when hands are down -> lift
 * when hands are up -> state = pull
 */ 
bool TorClimber::ManageState() {

  switch(state) {

  // lift hands to the starting position
  case Init:
    mds.Printf(DriverStationLCD::kUser_Line4, 1, "State = init");
    mds.UpdateLCD();
    level = 0;                                                          // level counter

    liftHands(motorSpeed);
    while (!checkLimits(2)){                                            // move until intitial position limit switch is hit
        Wait(0.01);
        if (!m_joystick.GetRawButton(Consts::CLIMB_BUTTON))
          {
            break;
          }
        if(checkLimits(1)) {
            break;
        }
    }
    liftJag1->Set(0.0);
    liftJag2->Set(0.0);
    state = Pull;
    mds.Printf(DriverStationLCD::kUser_Line4, 1, "State = Pull");
    mds.UpdateLCD();
    break;

    // grab bar with hands and pull down the feet up to the same bar
  case Pull:
    
    mds.Printf(DriverStationLCD::kUser_Line4, 1, "State = Pull");
          mds.UpdateLCD();
    pullHands();
    while(!checkLimits(0)) {				// Feet are in the up position, pull hands down (if bottom limit is hit, stop)
        Wait(0.01);
        if (!m_joystick.GetRawButton(Consts::CLIMB_BUTTON))
          {
            break;
          }
    }
//    while(checkLimits(0)) {				// Feet have made contact with the bar (down position), wait for feet to clear bar (if bottom limit is hit, stop)
//        Wait(0.01);
//    }
    liftJag1->Set(0.0);                                                 // stop hands
    liftJag2->Set(0.0);

    level++;                                                            // lift + pull = next level reached
    state = Lift;
    mds.Printf(DriverStationLCD::kUser_Line4, 1, "State = lift");
    mds.UpdateLCD();
    break;

    // lift hands up to the next bar while standing on the feet
  case Lift:
    liftHands(postInitSpeed);
    while(!checkLimits(1)) {                               // Hands are in the up position, lift hands (if top limit is hit, stop)
        Wait(0.01);
        if (!m_joystick.GetRawButton(Consts::CLIMB_BUTTON))
          {
            break;
          }
    }
    liftJag1->Set(0.0);
    liftJag2->Set(0.0);
    state = Pull;
    mds.Printf(DriverStationLCD::kUser_Line4, 1, "State = Pull");
    mds.UpdateLCD();
    break;

//  case Shoot:                                                             // previous state was shoot, now just stop and return to main
//    state = Stop;
//    break;

  default: 
    mds.Printf(DriverStationLCD::kUser_Line3, 1, "No state");
    mds.UpdateLCD();
    break;
  } // end switch


  // if level 3 has been reached, set state to Shoot to try to put disks in pyramid goal
  if(level == 3)
    {
      mds.Printf(DriverStationLCD::kUser_Line4, 1, "Reached level 3");
            mds.UpdateLCD();
      state = Stop;
      //shootIntoGoal();
    }
  return true;
}

void TorClimber::pullDown() {
  liftJag1->Set(-0.60);
  liftJag2->Set(0.60);
  
  while(checkLimits(0)) {
      Wait(0.01);
  }
  
  liftJag1->Set(-0.0);
  liftJag2->Set(0.0);
}



// return true if the selected limit switch has been triggered
bool TorClimber::checkLimits(int limitType) {

  if (limitType==0)												
    return (bottomForkLift->Get() == 0);					// 0 = bottom limit
  else if (limitType == 1)
    return (topForkLift->Get() == 0);						// 1 = top limit
  else if (limitType == 2)
    return (initPosSwitch->Get() == 0);						// 2 = intitial position limit
  else	if(m_joystick.GetRawButton(Consts::CLIMB_BUTTON)) 
    return true;
  else
    return false;
}



void TorClimber::Abort() {
  //Zero Motors
  liftJag1->Set(0.0);
  liftJag2->Set(0.0);
  while(true) {} //TODO
}


// returns true if both hands are in the down position (engaged with bar)
bool TorClimber::getHands() {

  //if both are on return true, if either are off return false
  if ((leftHandSwitch->Get() == 1) || (rightHandSwitch->Get() == 1))
    return false;
  else
    return true;
}


// returns true if both feet are in the down position (engaged with bar)
bool TorClimber::getFeet() {

  //if either are on return true, if both are off return false
  if ((leftFootSwitch->Get() == 1) || (rightFootSwitch->Get() == 1))
    return false;
  else
    return true;
}


void TorClimber::liftHands(float speed) {
  liftJag1->Set(speed);
  liftJag2->Set(-speed);
}

void TorClimber::pullHands(bool override) {
  if (!override)
    {
      liftJag1->Set(-postInitSpeed);
      liftJag2->Set(postInitSpeed);
    }
  else //manually pull them down
    {
      liftJag1->Set(-0.25);
      liftJag2->Set(0.25);
    }
}

void TorClimber::shootIntoGoal() {
  //TODO write logic to shoot discs once at top

  Abort(); //execute abort method to keep the robot at the top.
}

void TorClimber::reset() {
  level = 0;
  pullHands();
  
  timer->Stop();
  timer->Reset();
  timer->Start();
  while(!checkLimits(0) && timer->Get() < 3.0) {
      Wait(0.1);
  }
  liftJag1->Set(-0.0);
  liftJag2->Set(0.0);
  timer->Stop();
  timer->Reset();  
}
