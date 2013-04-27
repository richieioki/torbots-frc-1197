#include "TorShooter.h"

TorShooter::TorShooter(Joystick& theJoystick1, Joystick& theJoystick2, TorbotDrive& theDrive, TorFeeder& theFeeder, AnalogChannel& thePOT, DriverStationLCD& theDS)  // need feeder too
: m_joystick1(theJoystick1), m_joystick2(theJoystick2), m_drive(theDrive), m_feeder(theFeeder), m_shooterArmPOT(thePOT), ds(theDS)
{
  frontShootMotor = new Jaguar(Consts::SHOOTER_FRONT_MOD, Consts::SHOOTER_FRONT_JAG);     
  rearShootMotor = new Jaguar(Consts::SHOOTER_REAR_MOD, Consts::SHOOTER_REAR_JAG);    
  shootSolenoid = new Solenoid(Consts::SHOOTER_MAG_LOAD_SOLENOID);

  //shooterCounter1 = new SpeedCounter(5);                            // Hall effect sensor for mainMotor speed
  //shooterCounter2 = new SpeedCounter(6);                            // Hall effect sensor for feedMotor speed

  myTorTarget = new TorTargetAcquire();

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
        state = Shooting;
      
      // update current shooter state
      ManageState();
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
    break;


    // no target found yet, wait for driver to release targeting button (trigger)
  case Acquire:
    if (triggerButton)
    {
        //m_drive.SetSpeed(0.0);                            // stop robot before looking for target
        if (Target())                                       // find target and start moving towards it if found
            state = TargetSt;
    }
    else
        state = Init;
    
    break;

          
    case TargetSt:
    if (m_feeder.isDiskRight())
         ShooterArmSetPoint = ShooterArmYTheta;
    else
        ShooterArmSetPoint = ShooterArmYThetaAdjusted;
          
    if (triggerButton && m_feeder.isReadyToShoot())
        state = StartMove;
    else
        state = Init;
          
    break;

          
  case StartMove:
    state = Moving;          
    if (!triggerButton)
      state = Init;
    break;


    // target was found and Shooter Arm is moving to target; wait until it reaches its setpoint, then change state
  case Moving:

    ds.Printf(DriverStationLCD::kUser_Line4, 1, "M Pot=%d sPt=%d", m_shooterArmPOT.GetAverageValue(),(int)ShooterArmSetPoint);
    ds.UpdateLCD();

    if (abs(m_shooterArmPOT.GetAverageValue()) >= (int)fabs(ShooterArmSetPoint) ) 
      state = Stopped;

    if (!triggerButton)
      state = Init;

    break;

    // arm has stopped so its okay to start shooting
  case Stopped:
    if (!triggerButton)
      state = Init;
    else
      state = Shooting;

    break;

    // in the act of shooting
  case Shooting:
    if (triggerButton || manualTriggerButton)                                   // either trigger will allow shooting
    {
        if (Shoot())                                                            // Shoot returns true if it successfully shot,
          state = TargetSt;                                                       // verify target and try to shoot again
        else
          state = Init;                                                         // no more to shoot, return to Init state
    }
    else
      state = Init;
        
    break;

          
  default:
    if (!triggerButton)
      state = Init;
    break;


  } // end switch

} // end ManageState



// SHOOT!!
bool TorShooter::Shoot()
{

  // Start the shooter motor
  Start();

  // Check if feeder is ready to shoot
  // true paramter tells feeder to get ready to shoot if there are disks available
  if (this->isReadyToShoot())
    {
      // shoot 1 disk
      // pull piston to pull disk into shooter wheels
//      shootSolenoid->Set(false);
//      Wait(0.25);                                                                                  // piston fire time
//      shootSolenoid->Set(true);       // extend piston to retract mag loader
//      Wait(0.25);
  
      ds.Printf(DriverStationLCD::kUser_Line3, 1, "Shoot!!!");
      ds.UpdateLCD();
      
      // let feeder know the disk was shot so it can update its controls and move the next disk into position if exists
      m_feeder.ShotDisk();
      return true;
    }
  else
    return false;

}



// TARGET: manage the targeting process
bool TorShooter::Target()
{

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
  if(myTorTarget->isTarget())
    {
      //          ShooterTurnSetPoint = m_drive.TurnToTheta(0.25, myTorTarget->GetXTheta(), false);   // turn 25% power, to XTheta angle, do not wait
      ShooterTurnSetPoint = myTorTarget->GetXTheta();
      ShooterArmYTheta = (double)m_shooterArmPOT.GetAverageValue() - (Consts::CAM_TO_POT*myTorTarget->GetYTheta());     // subtract from current position; to
      ShooterArmYThetaAdjusted = (double)m_shooterArmPOT.GetAverageValue() - (Consts::CAM_TO_POT*myTorTarget->GetYThetaAdjusted());
      ShooterArmSetPoint = ShooterArmYTheta; // initially set the setpoint to target center; Autonomous will use this, operator control will adjust as needed
    }
  ds.Printf(DriverStationLCD::kUser_Line5, 1, "%d xPt:%3.2f yPt:%3.2", (int)myTorTarget->GetDistance(), ShooterTurnSetPoint, ShooterArmSetPoint );
  ds.Printf(DriverStationLCD::kUser_Line6, 1, " xTh=%6.3f yTh=%6.3f", myTorTarget->GetXTheta(), myTorTarget->GetYTheta());
  if(myTorTarget->isTarget())
    ds.Printf(DriverStationLCD::kUser_Line6, 1, "*");

  ds.UpdateLCD();

  return myTorTarget->isTarget();

} // end Target




// start shooter motor
void TorShooter::Start()
{
  frontShootMotor->Set(1.0);
  rearShootMotor->Set(1.0);  
}

// stop shooter motor
void TorShooter::Stop()
{
  mainMotorSpeed = 0.0;
  feedMotorSpeed = 0.0;
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
  case Straight:                                                                // initial straight ahead
    ShooterArmSetPoint = StraightPOT;
    break;

  case Targeting:                                                               // look up at targets
    ShooterArmSetPoint = TargetingPOT;
    break;

  case Loading:                                                                 // load position (looking all the way down)
    ShooterArmSetPoint = LoadingPOT;
    break;

  case Climbing:                                                                // climb position (looking up and back behind)
    ShooterArmSetPoint = ClimbingPOT;
    break;

  }
} // end SetArmPosition


// get the current preset position
// not sure if this is needed. Maybe should return POT position.
// DISCUSS
int TorShooter::GetArmPosition()
{
  int position = m_shooterArmPOT.GetAverageValue();

  return position;
}


// Move the shooter arm a certain number of degrees (theta)
// typically used for targeting
// takes the YTheta value from the target object
//void TorShooter::MoveArm(double theta)
//{
//  double CamToPotConversion = 2.778;    // 360 degrees / 1000 pot pts constant
//  ShooterArmSetPoint = (double)m_shooterArmPOT.GetAverageValue() - (CamToPotConversion*theta);     // subtract from current position; to move up is negative theta
//
//  if (state != TorShooter::Moving)
//      state = TorShooter::Moving;
//          
//} // end Move

bool TorShooter::isMotorReady()
{
  // check speedcontroller speeds to see if they are up to the proper speeds

  return true;
}

// returns true if shooter is ready to shoot
// feeder must have a disk in the ready position
// and shooter motor must be up to speed
bool TorShooter::isReadyToShoot()
{

  // ask feeder if it is ready; pass true, telling feeder to get ready by moving existing disks to the ready position
  // check if motor is up to speed
  // when both are true, we're ready to shoot

  //ds.Printf(DriverStationLCD::kUser_Line5, 1, "%d xPt:%3.2f yPt:%3.2", (int)myTorTarget->GetDistance(), ShooterTurnSetPoint, ShooterArmSetPoint );
  return (m_feeder.isReadyToShoot() && this->isMotorReady());

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

