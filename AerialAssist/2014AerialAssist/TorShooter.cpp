#include "TorShooter.h"

TorShooter::TorShooter(Joystick& myJoystick)
: m_stick(myJoystick)
{
  topWheelJag = new Jaguar(Consts::TOP1_SHOOTER_JAG_MOD, Consts::TOP1_SHOOTER_JAG);
  topWheelJag1 = new Jaguar(Consts::TOP2_SHOOTER_JAG_MOD, Consts::TOP2_SHOOTER_JAG);
  bottomWheelJag = new Jaguar(Consts::BOTTOM1_SHOOTER_JAG_MOD, Consts::BOTTOM1_SHOOTER_JAG);
  bottomWheelJag1 = new Jaguar(Consts::BOTTOM2_SHOOTER_JAG_MOD, Consts::BOTTOM2_SHOOTER_JAG);
  
  loaderBarJag = new Jaguar(Consts::LOADER_BAR_JAG_MOD, Consts::LOADER_BAR_JAG);
  cageJag = new Jaguar(Consts::CAGE_JAG_MOD, Consts::CAGE_JAG);
  loadSolenoid = new Solenoid(Consts::LOAD_SOLENOID);
  fireSolenoid = new Solenoid(Consts::FIRE_SOLENOID);

  state = Init;
  ManageState();
  runButton = false;
  fireButton = false;
  loadOverride = false;
  
  MoveLoaderDown(false);
}
void TorShooter::Fire()
{
  fireButton = m_stick.GetRawButton(Consts::FIRE_BUTTON);
  if (state == Running && fireButton)
    {
      fireSolenoid->Set(!Consts::SHOOTER_PISTON_EXTENDED); //retract pistons
      Wait(1.0); //give the piston time to retract before extending
      fireSolenoid->Set(Consts::SHOOTER_PISTON_EXTENDED); //extend pistons
      loadOverride = false;
      ManageState();
    }
}
void TorShooter::Run()
{
  runButton = m_stick.GetRawButton(Consts::RUN_BUTTON);
  fireButton = m_stick.GetRawButton(Consts::FIRE_BUTTON);
  if ((state == Loaded || state == Running) && runButton)
    {
      SetJagSpeed(Consts::SHOOTER_FIRE_SPEED);
      if (loaderDown && IsLoaded())
        {
          loaderBarJag->Set(Consts::LOADER_BAR_SPEED);
        }
      ManageState();
    }
  else if ((state == Loading) && runButton)
    {
      SetJagSpeed(Consts::SHOOTER_LOAD_SPEED);
      loaderBarJag->Set(-1 * Consts::LOADER_BAR_SPEED);
      loadSolenoid->Set(Consts::LOADER_PISTON_EXTENDED); // extend loader pistons
      ManageState();
    }
  else
    {
      SetJagSpeed(0.0);
      if (IsLoaded())
        {
          state = Loaded;
        }
      else
        {
          state = Loading;
        }
    }
}
bool TorShooter::IsLoaded()
{
  //return true if ball is in cage
  bool isLoaded = false;
  if (m_stick.GetRawButton(Consts::LOAD_OVERRIDE_BUTTON))
    {
      loadOverride = true;
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
TorShooter::shooterState TorShooter::GetShooterState()
{
  return state;
}
bool TorShooter::IsLoaderDown()
{
  return loaderDown;
}
void TorShooter::SetJagSpeed(float speed)
{
  topWheelJag->Set(speed);
  topWheelJag1->Set(speed);
  bottomWheelJag->Set(speed);
  bottomWheelJag1->Set(speed);
  if (speed == 0)
    {
      loaderBarJag->Set(speed);
    }
  currentJagSpeed = speed;
}
float TorShooter::GetJagSpeed()
{
  return currentJagSpeed;
}
void TorShooter::ManualFire()
{
  fireSolenoid->Set(!Consts::SHOOTER_PISTON_EXTENDED); //retract pistons
  Wait(0.3); //give pistons time to retract before extending
  fireSolenoid->Set(Consts::SHOOTER_PISTON_EXTENDED); //extend pistons
}
void TorShooter::ManageState()
{
  switch(state)
  {
  case Init:
    {
      if (IsLoaded())
        {
          state = Loaded;
        }
      else
        {
          state = Loading;
        }
      break;
    }
  case Loading:
    {
      if (IsLoaded())
        {
          state = Loaded;
          while (!m_stick.GetRawButton(Consts::RUN_BUTTON))
            {
              Wait(0.1);
            }
          SetJagSpeed(0.0);
        }
      break;
    }
  case Loaded:
    {
      if (runButton)
        {
          state = Running;
        }
      break;
    }
  case Running:
    {
      runButton = m_stick.GetRawButton(Consts::RUN_BUTTON);
      if(!IsLoaded())
        {
          state = Loading;
        }
      else if (!runButton)
        {
          state = Loaded;
        }
      else if (fireButton)
        {
          Fire();
          break;
        }

      break;
    }
  default:
    {
      //this code should never be reached
      break;
    }
  }
}
