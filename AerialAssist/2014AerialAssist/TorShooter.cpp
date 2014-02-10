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
  cageJag = new Talon(Consts::CAGE_JAG);
  loadSolenoid = new Solenoid(Consts::LOAD_SOLENOID);
  fireSolenoid = new Solenoid(Consts::FIRE_SOLENOID);

  //shooterArmPOT = new AnalogChannel(Consts::SHOOTER_ARM_POT); //TODO: Uncomment this

  runButton = false;
  fireButton = false;
  loadOverride = false;
  isShooterInit = false;

  MoveLoaderDown(false);
}
void TorShooter::Fire()
{
  fireButton = m_stick.GetRawButton(Consts::FIRE_BUTTON);
  m_stick.GetThrottle();
  if (currentJagSpeed != 0.0 && IsLoaded() && fireButton)
    {
      fireSolenoid->Set(!Consts::SHOOTER_PISTON_EXTENDED); //retract pistons
      Wait(1.0); //give the piston time to retract before extending
      fireSolenoid->Set(Consts::SHOOTER_PISTON_EXTENDED); //extend pistons
      loadOverride = false;
    }
}
void TorShooter::Run()
{
  runButton = m_stick.GetRawButton(Consts::RUN_BUTTON);
  fireButton = m_stick.GetRawButton(Consts::FIRE_BUTTON);
  if (IsLoaded() && runButton)
    {
      SetJagSpeed(ShooterSpeed());
      if (loaderDown && IsLoaded())
        {
          loaderBarJag->Set(Consts::LOADER_BAR_SPEED);
        }

      if (fireButton)
        {
          Fire();
        }
    }
  else if ((!IsLoaded()) && runButton)
    {
      SetJagSpeed(Consts::SHOOTER_LOAD_SPEED);
      loaderBarJag->Set(-1 * Consts::LOADER_BAR_SPEED);
      loadSolenoid->Set(Consts::LOADER_PISTON_EXTENDED); // extend loader pistons
    }
  else
    {
      SetJagSpeed(0.0);
    }
}
bool TorShooter::IsLoaded()
{
  //return true if ball is in cage
  bool isLoaded = false;
  if (m_stick.GetRawButton(Consts::S_LOAD_OVERRIDE_BUTTON) || tartarus.GetRawButton(Consts::LOAD_OVERRIDE_BUTTON))
    {
      loadOverride = true;
    }
  else if (m_stick.GetRawButton(Consts::S_UNLOAD_OVERRIDE_BUTTON) || tartarus.GetRawButton(Consts::UNLOAD_OVERRIDE_BUTTON))
    {
      loadOverride = false;
    }

  return isLoaded || loadOverride;
}
void TorShooter::MoveLoaderDown(bool downFlag)
{
  loaderDown = downFlag;
  loadSolenoid->Set(!downFlag);
}
void TorShooter::MoveShooter(float speed)
{
  cageJag->Set(speed);
}
void TorShooter::SetCagePos(bool raiseFlag)
{
  shooterDown = !raiseFlag;
  isShooterInit = true;
}
void TorShooter::SetCagePos()
{
  if (isShooterInit)//wont run unless shooterdown has been initialized
    {
      int goal;
      if (!shooterDown)
        {
          goal = Consts::SHOOTER_ARM_UP;
        }
      else
        {
          goal = Consts::SHOOTER_ARM_DOWN;
        }
      if (shooterArmPOT->GetAverageValue() - goal < Consts::POT_THRESHOLD && shooterArmPOT->GetAverageValue() - goal > -Consts::POT_THRESHOLD)
        {
          MoveShooter(0);
        }
      else if (shooterArmPOT->GetAverageValue() > goal && shooterDown)
        {
          MoveShooter(-Consts::CAGE_MOVE_SPEED);
        }
      else if (shooterArmPOT->GetAverageValue() < goal && !shooterDown)
        {
          MoveShooter(Consts::CAGE_MOVE_SPEED);
        }
      else
        {
          MoveShooter(0);
        }
    }
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
float TorShooter::GetJagSpeed()
{
  return currentJagSpeed;
}
float TorShooter::ShooterSpeed()
{
  throttleValue = m_stick.GetTwist(); //1 is down, -1 is up
  return (((1.0 - throttleValue) / 2.0) * (0.9 - Consts::BASE_SHOOTER_FIRE_SPEED)) + Consts::BASE_SHOOTER_FIRE_SPEED;
}
void TorShooter::ManualFire()
{
  fireSolenoid->Set(!Consts::SHOOTER_PISTON_EXTENDED); //retract pistons
  Wait(0.3); //give pistons time to retract before extending
  fireSolenoid->Set(Consts::SHOOTER_PISTON_EXTENDED); //extend pistons
}
