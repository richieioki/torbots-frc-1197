#include "TorClimber.h"


TorClimber::TorClimber(Joystick& theJoystick, TorbotDrive& drive, TorShooter& torShooter) 
: m_joystick(theJoystick), m_tordrive(drive), m_shooter(torShooter)
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

    
  
  // set speeds here. one motor is inverted from the other. this ensures we maintain this inverted relationship
  motorSpeed = 0.5;

}


void TorClimber::Run() {
  //if button pressed
  do {
      climbButton = m_joystick.GetRawButton(Consts::CLIMB_BUTTON);

      ManageState();
  }
  while (climbButton && (state != Stop) && (state != Shoot));
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
void TorClimber::ManageState() {

  switch(state) {

  // lift hands to the starting position
  case Init:
    level = 0;                                                          // level counter

    liftHands();
    while (!checkLimits(2)){                                            // move until intitial position limit switch is hit
        Wait(0.01);
    }
    liftJag1->Set(0.0);
    liftJag2->Set(0.0);

    state = Pull;
    break;

    // grab bar with hands and pull down the feet up to the same bar
  case Pull:
    pullHands();
    while(getFeet() && !checkLimits(0)) {				// Feet are in the up position, pull hands down (if bottom limit is hit, stop)
        Wait(0.01);
    }
    while(!getFeet() && !checkLimits(0)) {				// Feet have made contact with the bar (down position), wait for feet to clear bar (if bottom limit is hit, stop)
        Wait(0.01);
    }
    liftJag1->Set(0.0);                                                 // stop hands
    liftJag2->Set(0.0);

    level++;                                                            // lift + pull = next level reached
    state = Lift;
    break;

    // lift hands up to the next bar while standing on the feet
  case Lift:
    liftHands();
    while(getHands() && checkLimits(1)) {                               // Hands are in the up position, lift hands (if top limit is hit, stop)
        Wait(0.01);
    }
    while(!getHands() && checkLimits(1)) {                              // Hands have made contact with the bar (down position), wait for hands to clear bar (if top limit is hit, stop)
        Wait(0.01);
    }
    liftJag1->Set(0.0);
    liftJag2->Set(0.0);
    state = Pull;
    break;

  case Shoot:                                                             // previous state was shoot, now just stop and return to main
    state = Stop;
    break;

    defualt: 
    break;
  } // end switch


  // if level 3 has been reached, set state to Shoot to try to put disks in pyramid goal
  if(level == 3)
    {
      state = Shoot;
      shootIntoGoal();
    }
  else
    {
      // set state to Stop
      state = Stop;
    }
}



// return true if the selected limit switch has been triggered
bool TorClimber::checkLimits(int limitType) {

  if (limitType==0)												
    return (bottomForkLift->Get() == 1);					// 0 = bottom limit
  else if (limitType == 1)
    return (topForkLift->Get() == 1);						// 1 = top limit
  else if (limitType == 2)
    return (initPosSwitch->Get() == 1);						// 2 = intitial position limit
  else	
    return false;
}



void TorClimber::Abort() {
  //Zero Motors
  liftJag1->Set(0.0);
  liftJag2->Set(0.0);
  while(true) {}
}


// returns true if both hands are in the up position
bool TorClimber::getHands() {

  //if both are on return true, if either are off return false
  if ((leftHandSwitch->Get() == 1) && (rightHandSwitch->Get() == 1))
    return true;
  else
    return false;
}


// return true if both feet are in the up position
bool TorClimber::getFeet() {

  //if both are on return true, if either are off return false
  if ((leftFootSwitch->Get() == 1) && (rightFootSwitch->Get() == 1))
    return true;
  else
    return false;
}


void TorClimber::liftHands() {
  liftJag1->Set(motorSpeed);
  liftJag2->Set(motorSpeed);
}

void TorClimber::pullHands() {
  liftJag1->Set(-motorSpeed);
  liftJag2->Set(-motorSpeed);
}

void TorClimber::shootIntoGoal() {
  //TODO write logic to shoot discs once at top

  Abort(); //execute abort method to keep the robot at the top.
}
