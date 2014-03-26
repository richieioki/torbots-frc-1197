#include "TorShooter.h"
#include <cmath>

TorShooter::TorShooter(Joystick& myJoystick1, Joystick& myJoystick2)
: m_stick(myJoystick1), tartarus(myJoystick2)
{
  topWheelJag = new Talon(Consts::TOP1_SHOOTER_JAG);
  topWheelJag1 = new Talon(Consts::TOP2_SHOOTER_JAG);
  bottomWheelJag = new Talon(Consts::BOTTOM1_SHOOTER_JAG);
  bottomWheelJag1 = new Talon(Consts::BOTTOM2_SHOOTER_JAG);

  loaderBarJag = new Talon(Consts::LOADER_BAR_JAG);
  loadSolenoid = new Solenoid(Consts::LOAD_SOLENOID);
  fireSolenoid = new Solenoid(Consts::FIRE_SOLENOID);

  runButton = false;
  loaderDown = false;
  passButton = false;
  catchButton = false;

}
void TorShooter::Fire()
{
  fireSolenoid->Set(Consts::SHOOTER_PISTON_EXTENDED); //retract pistons
  Wait(1.0); //give the piston time to retract before extending
  fireSolenoid->Set(!Consts::SHOOTER_PISTON_EXTENDED); //extend pistons
}
void TorShooter::Run()
{
  runButton = m_stick.GetRawButton(Consts::RUN_BUTTON);
  passButton = m_stick.GetRawButton(Consts::PASS_BUTTON);
  catchButton = m_stick.GetRawButton(Consts::CATCH_BUTTON);
  if (runButton)
    {
      if (loaderDown)
        {
          SetJagSpeed(-ShooterSpeed());
          loaderBarJag->Set(Consts::LOADER_BAR_SPEED);
        } 
      else {
          Fire(); 
      }
    } 
  else if (passButton) 
    {

      SetJagSpeed(Consts::MAX_SHOOTER_FIRE_SPEED);

      loaderBarJag->Set(-Consts::LOADER_BAR_SPEED);
      Fire();
    }
  else if (catchButton)
    {
      SetJagSpeed(-ShooterSpeed());
    }
  else if (loaderDown) 
    {

      SetJagSpeed(0.0);
    }
}

void TorShooter::MoveLoaderDown(bool downFlag)
{
  loaderDown = downFlag;
  loadSolenoid->Set(downFlag);
}
bool TorShooter::IsLoaderDown()
{
  return loaderDown;
}
void TorShooter::SetJagSpeed(float speed)
{
  topWheelJag->Set(speed);
  topWheelJag1->Set(-speed);
  bottomWheelJag->Set(speed);
  bottomWheelJag1->Set(-speed);
  if (speed == 0.0)
    {
      loaderBarJag->Set(speed);
    }
  currentJagSpeed = speed;
}
void TorShooter::SetLoaderBarSpeed (float speed)
{
  loaderBarJag->Set(speed);
}

float TorShooter::GetJagSpeed()
{
  return currentJagSpeed;
}
float TorShooter::ShooterSpeed()
{

  throttleValue = m_stick.GetTwist(); //1 is down, -1 is up
  return (((1.0 - throttleValue) / 2.0) * (Consts::MAX_SHOOTER_FIRE_SPEED - Consts::BASE_SHOOTER_FIRE_SPEED)) + Consts::BASE_SHOOTER_FIRE_SPEED;
}
void TorShooter::LoaderDownOverride(bool ld)
{
  if(ld)
    {
      loadSolenoid->Set(false);           //Move Loader Up
    }
  else
    {
      loadSolenoid->Set(loaderDown);      //Put in previous position

    }

}
