#include "TorShooter.h"

TorShooter::TorShooter(Joystick& theJoystick1, Joystick& theJoystick2, TorbotDrive& theDrive, TorFeeder& theFeeder, AnalogChannel& thePOT, DriverStationLCD& theDS)  // need feeder too
: m_joystick1(theJoystick1), m_joystick2(theJoystick2), m_drive(theDrive), m_feeder(theFeeder), m_shooterArmPOT(thePOT), ds(theDS)
{
  frontShootMotor = new Jaguar(Consts::SHOOTER_FRONT_MOD, Consts::SHOOTER_FRONT_JAG);     
  rearShootMotor = new Jaguar(Consts::SHOOTER_REAR_MOD, Consts::SHOOTER_REAR_JAG);    
  shootSolenoid = new Solenoid(Consts::SHOOTER_MAG_LOAD_SOLENOID);

  rearArmLimit = new DigitalInput(Consts::SHOOTER_REAR_STOP_MOD, Consts::SHOOTER_REAR_STOP);
  frontArmLimit = new DigitalInput(Consts::SHOOTER_FRONT_STOP_MOD, Consts::SHOOTER_FRONT_STOP);
  timer = new Timer();

  myTorTarget = new TorTargetAcquire();
  shooterCounter1 = new SpeedCounter(Consts::SHOOTER_HALL_EFFECT);

  // init trigger button variables to false
  manualTriggerButton = false;
  triggerButton = false;

  state = Init;
}


// Run called from outside. Checks joystick and current state and calls appropriate methods
void TorShooter::Run()
{
  
  do
    {
      // read joystick controls
      triggerButton = m_joystick1.GetRawButton(Consts::TRIGGER_BUTTON);
      manualTriggerButton = m_joystick2.GetRawButton(Consts::MANUAL_TRIGGER_BUTTON);
      // if manual trigger pressed, skip targeting and go straight to Shooting
      if (manualTriggerButton)
        {
          Start();
          state = Shooting;
        }
      // update current shooter state
      ManageState();
      
      // just in case, if driver has let go of trigger and state is set back to Init, stop the shooter motors
      if (state == Init)
        Stop();
      
    }
  while ((state != Init) && (state != StartMove));


  //  ds.Printf(DriverStationLCD::kUser_Line1, 1, "exiting RUN");
  //  ds.UpdateLCD();
}




void TorShooter::ManageState()
{
  //  ds.Printf(DriverStationLCD::kUser_Line2, 1, "Managing state");
  //  ds.UpdateLCD();

  switch (state)
  {
  case Init:
    if (triggerButton)                                          // trigger starts the target acquisition process from the Init state
      {
        if (m_feeder.isReadyToShoot())                          // check if feeder is ready to shoot, returns false if there are no disks
            state = Acquire;
        else
          state = Init;
      }
    //diagnostic test purposes
//    ds.Printf(DriverStationLCD::kUser_Line5, 1, "Init");
//    ds.UpdateLCD();
    break;


    // no target found yet, wait for driver to release targeting button (trigger)
  case Acquire:
    
    if (triggerButton)
    {
        //m_drive.SetSpeed(0.0);                            // stop robot before looking for target
        if (Target())                                       // find target and start moving towards it if found
          {
            Start();
            state = TargetSt;
          }
    }
    else
        state = Init;
//    ds.Printf(DriverStationLCD::kUser_Line5, 1, "Acq");
//    ds.UpdateLCD();
    break;

          
    case TargetSt:

      ShooterArmSetPoint = ShooterArmYTheta;

      if (triggerButton)
        state = StartMove;
      else
        state = Init;
//      ds.Printf(DriverStationLCD::kUser_Line5, 1, "Tgt");
//      ds.UpdateLCD();      
    break;

          
  case StartMove:
    state = Moving;          
    if (!triggerButton)
      state = Init;
//    ds.Printf(DriverStationLCD::kUser_Line5, 1, "StMov");
//    ds.UpdateLCD();
    break;


    // target was found and Shooter Arm is moving to target; wait until it reaches its setpoint, then change state
  case Moving:

//    ds.Printf(DriverStationLCD::kUser_Line4, 1, "Pot=%d sPt=%5.3f", m_shooterArmPOT.GetAverageValue(),ShooterArmSetPoint);
    ds.UpdateLCD();

    if ((abs(m_shooterArmPOT.GetAverageValue()) >= (int)(fabs(ShooterArmSetPoint)*0.99)) && (abs(m_shooterArmPOT.GetAverageValue()) <= (int)(fabs(ShooterArmSetPoint)*1.01)) ) 
      state = Stopped;
    
    if ((rearArmLimit->Get() == 0) || (frontArmLimit->Get() == 0))
      state = Init;

    if (!triggerButton)
      state = Init;

//    ds.Printf(DriverStationLCD::kUser_Line5, 1, "Mov");
//    ds.UpdateLCD();
    break;

    // arm has stopped so its okay to start shooting
  case Stopped:
    if (!triggerButton)
      state = Init;
    else
      state = Shooting;

//    ds.Printf(DriverStationLCD::kUser_Line5, 1, "Stpd");
//    ds.UpdateLCD();
    break;

    // in the act of shooting
  case Shooting:
//    ds.Printf(DriverStationLCD::kUser_Line5, 1, "ShootState");
//    ds.UpdateLCD();
    if (triggerButton || manualTriggerButton)                                   // either trigger will allow shooting
    {        
        Shoot();                                                            // Shoot returns true if it successfully shot,
    }
    else
      {
        Stop();
        state = Init;
      }
  
    break;

          
  default:
    if (!triggerButton)
      state = Init;
//    ds.Printf(DriverStationLCD::kUser_Line5, 1, "Def");
//    ds.UpdateLCD();
    break;


  } // end switch

} // end ManageState







// SHOOT!!
bool TorShooter::Shoot()
{
  // shoot 1 disk

  if (isMotorReady() || (triggerButton && manualTriggerButton))           //isReadyToShoot())
    if (isReadyToShoot())
      {        
        // pull piston to pull disk into shooter wheels
        shootSolenoid->Set(true);
        Wait(0.4);                                                                                  // piston fire time
        shootSolenoid->Set(false);       // extend piston to retract mag loader
        Wait(0.4);

        //      ds.Printf(DriverStationLCD::kUser_Line5, 1, "Shoot!!!");
        //      ds.UpdateLCD();

        // let feeder know the disk was shot so it can update its controls and move the next disk into position if exists
        m_feeder.ShotDisk();
      }


  //  ds.Printf(DriverStationLCD::kUser_Line5, 1, "Shoot!!!");
  //  ds.UpdateLCD();
  return true;
}



// TARGET: manage the targeting process
bool TorShooter::Target()
{
  float yOffSet, xOffSet;
  
  //      ds.Printf(DriverStationLCD::kUser_Line5, 1, "Trying to acquire target");
  //      ds.UpdateLCD();
  //      state = TorShooter::Acquire; // make sure we don't run this code every time through while the button is pressed; do it just once
  // take picture and create target particals
  myTorTarget->AcquireTargets();

  // get best target available (closest to center and recognized as a valid target)
  myTorTarget->GetTarget();

  // look for High target when trigger pressed
  //      if (triggerButton)
  //        myTorTarget->GetTarget(TargetId::High);

  // look for Middle target when track button pressed
  //      if (trackButton)
  //        myTorTarget->GetTarget(TargetId::Middle);


  // set setpoints to turn robot to face target and lift shooter to aim
  //y theta goes from -1 (top) to +1 (bottom)
   
  
  if(myTorTarget->isTarget())
    {
      // calc offsets based on distance range
      //yOffSet = myTorTarget->GetDistance() * Consts::ARM_THETA_OFFSET * Consts::CAM_TO_POT;
      if ((int)myTorTarget->GetDistance() == 1)
        {
          yOffSet = 5.0 * Consts::CAM_TO_POT;            // in closest range, cut y offset in half to aim a bit lower
          xOffSet = Consts::DRIVE_THETA_OFFSET*0.5;
        }
      else if((int)myTorTarget->GetDistance() == 2)
        {
          yOffSet = Consts::ARM_THETA_OFFSET * Consts::CAM_TO_POT;
          xOffSet = Consts::DRIVE_THETA_OFFSET;
        }
      else if((int)myTorTarget->GetDistance() == 3)
        {
          yOffSet = Consts::ARM_THETA_OFFSET*1.5 * Consts::CAM_TO_POT;
          xOffSet = Consts::DRIVE_THETA_OFFSET*1.5;
        }
      else      // assume it must be the pyramid so use the same offsets as the middle shot
        {
          yOffSet = Consts::ARM_THETA_OFFSET * Consts::CAM_TO_POT;
          xOffSet = Consts::DRIVE_THETA_OFFSET;
        }

      //ShooterTurnSetPoint = m_drive.TurnToTheta(0.25, myTorTarget->GetXTheta(), false);   // turn 25% power, to XTheta angle, do not wait
      ShooterTurnSetPoint = myTorTarget->GetXTheta() - xOffSet; // subtract xOffSet to compensate for camera position off to left side of shooter
      ShooterArmYTheta = m_shooterArmPOT.GetAverageValue() - (Consts::CAM_TO_POT*myTorTarget->GetYTheta()) + yOffSet;     // subtract from current position and add offset to compensate for camera position
      ShooterArmYThetaAdjusted = (double)m_shooterArmPOT.GetAverageValue() - (Consts::CAM_TO_POT*myTorTarget->GetYThetaAdjusted());
      ShooterArmSetPoint = ShooterArmYTheta; // initially set the setpoint to target center; Autonomous will use this, operator control will adjust as needed
    
      if ((ShooterArmYTheta <= Consts::SHOOTER_ARM_LOADING) || (ShooterArmYTheta >= Consts::SHOOTER_ARM_CLIMBING))
        {
          ShooterArmYTheta = Consts::SHOOTER_ARM_TARGETING;
          ShooterArmYThetaAdjusted = Consts::SHOOTER_ARM_TARGETING;
//          ds.Printf(DriverStationLCD::kUser_Line6, 1, "ABORT!!");
//          ds.UpdateLCD();

          return false;
        }
    
    }
//  ds.Printf(DriverStationLCD::kUser_Line5, 1, "%d xPt:%3.2f yPt:%3.2f", (int)myTorTarget->GetDistance(), ShooterTurnSetPoint, ShooterArmSetPoint );
//  ds.Printf(DriverStationLCD::kUser_Line6, 1, " xTh=%6.3f yTh=%6.3f", myTorTarget->GetXTheta(), myTorTarget->GetYTheta());
  if(myTorTarget->isTarget())
    ds.Printf(DriverStationLCD::kUser_Line6, 1, "* t: %d dIdx:=%3.1f", myTorTarget->GetType(), myTorTarget->GetDistance());
  else
    ds.Printf(DriverStationLCD::kUser_Line6, 1, "  t: %d dIdx:=%3.1f", myTorTarget->GetType(), myTorTarget->GetDistance());
    
  ds.UpdateLCD();

  return myTorTarget->isTarget();

} // end Target




// start shooter motor
void TorShooter::Start()
{
  frontShootMotor->Set(-1.0);
  rearShootMotor->Set(-1.0);
// reset the wheel hall effect sensor to 0
  shooterCounter1->Reset();
  shooterCounter1->Start();
// reset the timer
  timer->Stop();
  timer->Reset();
  timer->Start();
}

// stop shooter motor
void TorShooter::Stop()
{
  frontShootMotor->Set(0.0);
  rearShootMotor->Set(0.0);  
  
  // stop timer and counter used to monitor motors
  timer->Stop();
  shooterCounter1->Stop();
  
}


// set the shooter arm position
// value is a preset value
// 0=initial straight ahead
// 1=look up at targets
// 2=load position
// 3=climb position
//
void TorShooter::SetArmPosition(TorShooter::ShooterArmPosition position)
{
  switch (position)
  {
  case Loading:                                                                 // load position (looking all the way down)
    ShooterArmSetPoint = Consts::SHOOTER_ARM_LOADING;
    break;

  case Straight:                                                                // initial straight ahead
    ShooterArmSetPoint = Consts::SHOOTER_ARM_STRAIGHT;
    break;

  case Targeting:                                                               // look up at targets
    ShooterArmSetPoint = Consts::SHOOTER_ARM_TARGETING;
    break;

  case Climbing:                                                                // climb position (looking up and back behind)
    ShooterArmSetPoint = Consts::SHOOTER_ARM_CLIMBING;
    break;

  }
} // end SetArmPosition


// get the current preset position
// not sure if this is needed. Maybe should return POT position.
// DISCUSS
double TorShooter::GetArmPosition()
{
  double position = (double)m_shooterArmPOT.GetAverageValue();

  return position;
}



bool TorShooter::isMotorReady()
{
  bool isReady = false;
  double maxSpeed = 85.0;
  
  // check speedcontroller speeds to see if they are up to the proper speeds
  // timer and counter are reset in the Start() method
  if (timer->Get() > 0.1)                       // don't check until 0.1 seconds have gone by since the reset
    {
      speed = shooterCounter1->GetSpeed(timer->Get());
      if (speed >= (0.98*maxSpeed))
        {
          // speed in range, set flag and return
          isReady = true;
          // reset timer and counter for next shot
        }
      timer->Reset();
      shooterCounter1->Reset();

      ds.Printf(DriverStationLCD::kUser_Line1, 1,"speed = %5.1f %d pct", speed, (int)(speed/maxSpeed));
      ds.UpdateLCD();

    } // end if min time elapsed

  return (isReady);

}


// returns true if shooter is ready to shoot
// feeder must have a disk in the ready position
// and shooter motor must be up to speed
bool TorShooter::isReadyToShoot()
{
  // ask feeder if it is ready; pass true, telling feeder to get ready by moving existing disks to the ready position

  //ds.Printf(DriverStationLCD::kUser_Line5, 1, "%d xPt:%3.2f yPt:%3.2", (int)myTorTarget->GetDistance(), ShooterTurnSetPoint, ShooterArmSetPoint );
  return (m_feeder.isReadyToShoot(manualTriggerButton));         // && this->isMotorReady());

}


// get the current state of the shooter process
TorShooter::shooterState TorShooter::GetState()
{
  return state;  
}


double TorShooter::GetArmSetPoint()
{
      return ShooterArmSetPoint;
}


double TorShooter::GetTurnSetPoint()
{
  return ShooterTurnSetPoint;
}

            
