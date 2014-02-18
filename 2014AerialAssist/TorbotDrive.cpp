#include "TorbotDrive.h"


TorbotDrive::TorbotDrive(Joystick& theJoystick, TorJagDrive& theTorJagDrive, Gyro& gyro, Encoder& encoder, DriverStationLCD& ds) 
: m_joystick(theJoystick), m_jagDrive(theTorJagDrive), m_gyro(gyro), m_encoder(encoder), m_ds(ds)
{
  m_encoder.Reset();
  m_encoder.Start();
  
  shiftSolenoid = new Solenoid(Consts::SHIFT_SOLENOID);

  isLowGear = false;
}
/*
TorbotDrive::TorbotDrive(Joystick& theJoystick, TorJagDrive& theTorJagDrive)
: m_joystick(theJoystick), m_jagDrive(theTorJagDrive)
{
  isLowGear = false;
  shiftSolenoid = new Solenoid(Consts::SHIFT_SOLENOID);
}
*/

void TorbotDrive::ArcadeDrive(bool squaredInputs)
{
  //m_encoder.Reset();
  float stickX = 0.0;
  float stickY = 0.0;
  //bool shiftButton = false; //Button 2

  float leftMotorSpeed;
  float rightMotorSpeed;

  // get negative of the stick controls. forward on stick gives negative value  
  stickX = -m_joystick.GetX();
  stickY = -m_joystick.GetY();
  //shiftButton = m_joystick.GetRawButton(Consts::SHIFT_BUTTON);

  // adjust joystick by dead zone
  if (fabs(stickX) <= Consts::stickDeadZone)
    stickX = 0.0;
  if (fabs(stickY) <= Consts::stickDeadZone)
    stickY = 0.0;

  // make sure X and Y don't go beyond the limits of -1 to 1
  if (stickX > 1.0)
    stickX = 1.0;
  if (stickX < -1.0)
    stickX = -1.0;

  if (stickY > 1.0)
    stickY = 1.0;
  if (stickY < -1.0)
    stickY = -1.0;


  // shift high/low drive gear
  //  if (isLowGear)
  //    {
  //      shiftSolenoid->Set(true);
  //    }
  //  else
  //    {
  //      shiftSolenoid->Set(false);
  //    }


  // square the inputs to produce an exponential power curve
  // this allows finer control with joystick movement and full power as you approach joystick limits
  if (squaredInputs)
    {
      if (stickX >= 0.0)
        stickX = (stickX*stickX);
      else
        stickX = -(stickX*stickX);

      if (stickY >= 0.0)
        stickY = (stickY*stickY);
      else
        stickY = -(stickY*stickY);
    }


  // old code
  //  leftMotorSpeed = -1*(stickY - stickX)/2;
  //  rightMotorSpeed = -1*(stickY + stickX)/2;


  if (stickY > 0.0)
    {
      if (stickX > 0.0)
        {
          leftMotorSpeed = stickY - stickX;
          rightMotorSpeed = max(stickY, stickX);
        }
      else
        {
          leftMotorSpeed = max(stickY, -stickX);
          rightMotorSpeed = stickY + stickX;
        }
    }
  else
    {
      if (stickX > 0.0)
        {
          leftMotorSpeed = -max(-stickY, stickX);
          rightMotorSpeed = stickY + stickX;
        }
      else
        {
          leftMotorSpeed = stickY - stickX;
          rightMotorSpeed = -max(-stickY, -stickX);
        }
    }
  // set the motor speed
  m_jagDrive.SetDrive(leftMotorSpeed, rightMotorSpeed);
} // end arcadeDrive


void TorbotDrive::setShifters(bool shiftToggle)
{
  shiftSolenoid->Set(shiftToggle);
}

void TorbotDrive::DriveToTheta(float theta, float motorSpeed, float distanceInches)
{
  float angleError = 0.0;
  float angleTarget;
  float motorAdjust;
  Timer *timer;
  float timeCurr = 0.0;

  timer = new Timer();
  timer->Reset();
  timer->Start();
 
  //m_gyro.Reset(); //Richie added this as a "fix" but it ended up breaking the code so i removed it
  angleTarget = theta;          // adjusted target angle; add current ange to desired angle. resetting may not set to 0.0 exactly

//  ds->Printf(DriverStationLCD::kUser_Line1, 1, "gyro: %2.2f AE: %4.2f", m_gyro.GetAngle(), angleError);
//  ds->UpdateLCD();

  // drive the desired distance
  resetEncoder();

  timeCurr = timer->Get();
  
  // Arbitrary adjustment made from empirical data 9/15/13
  // float distanceAdjustment = (distanceInches / 8.0) + (motorSpeed - 0.1) * 12.5;
  m_ds.Clear();
    
  while (fabs(getDistance()) < fabs(distanceInches))
    {
        
      angleError = m_gyro.GetAngle()-angleTarget;       // error off of adjusted target heading
      motorAdjust = angleError/10.0; // percent of error range (5degrees) 

      m_ds.Printf(DriverStationLCD::kUser_Line1, 1, "gyro: %2.2f AE: %4.2f", m_gyro.GetAngle(), angleError);
      m_ds.Printf(DriverStationLCD::kUser_Line2, 1, "distance: %f", getDistance());
      m_ds.UpdateLCD();

      //use these lines to use corrections
      m_jagDrive.SetLeft(motorSpeed*(1.0-motorAdjust));
      m_jagDrive.SetRight(motorSpeed*(1.0+motorAdjust));
      
      // test drive constant speed, no adjust, to unit test the motor speed
//      motorSpeed = 1.0;
//      m_jagDrive.SetLeft(motorSpeed);
//      m_jagDrive.SetRight(motorSpeed);
            
      Wait(0.05); // wait so we don't update continously. update 20 times per second
      
      // if this run takes more than 3 seconds, we probably ran into something. Stop and backup.
      if ((timer->Get() - timeCurr) >= 10.0)                                                     // should not take more than 3 seconds
        {
          // stop, back up a bit and exit this loop
          m_jagDrive.SetDrive(0.0,0.0);                                                         // stop
          m_jagDrive.SetDrive(-motorSpeed, -motorSpeed);                                        // backup
          Wait(0.25);
          m_jagDrive.SetDrive(0.0,0.0);                                                         // stop
                              
          break;                                                                                // exit      
        }// end time-out check
    } // end while

  // Stop motors
  m_jagDrive.SetDrive(0.0, 0.0);

   
} // end driveToTheta


void TorbotDrive::DriveStraight(float motorSpeed, float distanceInches)
{
  float currentAngle = 0.0;
  
  // get the current heading and use this as the direction to drive
  currentAngle = m_gyro.GetAngle();
  DriveToTheta(currentAngle, motorSpeed, distanceInches); // maintain currentAngle
  
}




float TorbotDrive::getDistance() {
  float encoderDistance;
  
  encoderDistance = ((float)m_encoder.GetRaw()*Consts::wheelCircumference*Consts::wheelGearRatio_High)/(250*4);  //(Consts::encoderTicks*4);
  
  return encoderDistance;
}

void TorbotDrive::resetEncoder() {
  m_encoder.Reset();
}



void TorbotDrive::shiftGear(bool lowGearFlag)
{
      shiftSolenoid->Set(lowGearFlag);  
      isLowGear = lowGearFlag;
}

float TorbotDrive::getRawTicks() {
  return (float)m_encoder.GetRaw();
}

