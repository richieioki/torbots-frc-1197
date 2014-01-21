#include "TorShooter.h"

TorShooter::TorShooter(Joystick& myJoystick)
: m_stick(myJoystick)
{
  topWheelJag = new Jaguar(Consts::TOP1_SHOOTER_JAG_MOD, Consts::TOP1_SHOOTER_JAG);
  topWheelJag1 = new Jaguar(Consts::TOP2_SHOOTER_JAG_MOD, Consts::TOP2_SHOOTER_JAG);
  bottomWheelJag = new Jaguar(Consts::BOTTOM1_SHOOTER_JAG_MOD, Consts::BOTTOM1_SHOOTER_JAG);
  bottomWheelJag1 = new Jaguar(Consts::BOTTOM2_SHOOTER_JAG_MOD, Consts::BOTTOM2_SHOOTER_JAG);
  
  loaderBarJag = new Jaguar(Consts::LOADER_BAR_JAG_MOD, Consts::LOADER_BAR_JAG);
  loadSolenoid = new Solenoid(Consts::LOAD_SOLENOID);
  fireSolenoid = new Solenoid(Consts::FIRE_SOLENOID);

  state = Init;
  runButton = false;
  fireButton = false;
  loadOverride = false;
  
  loaderDown = false;
  shooterDown = true;
  cage = Pass;
}
void TorShooter::Fire()
{
  fireButton = m_stick.GetRawButton(Consts::FIRE_BUTTON);
  if (state == Running && fireButton && (cage == Shoot || cage == Pass))
    {
      fireSolenoid->Set(!Consts::SHOOTER_PISTON_EXTENDED); //retract pistons
      Wait(0.3); //give the piston time to retract before extending
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
      if (cage == Pass)
        {
          loaderBarJag->Set(Consts::LOADER_BAR_SPEED);
        }
      ManageState();
    }
  else if ((state == Loading) && runButton)
    {
      SetJagSpeed(Consts::SHOOTER_LOAD_SPEED);
      loaderBarJag->Set(Consts::LOADER_BAR_SPEED);
      loadSolenoid->Set(Consts::LOADER_PISTON_EXTENDED); // extend loader pistons
      ManageState();
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
void TorShooter::MoveCage(cageState cg)
{
  if (cg == Load)
    {
      MoveShooterDown(true); 
      MoveLoaderDown(true);
    }
  else if (cg == Drive)
    {
      MoveLoaderDown(false);
    }
  else if (cg == Pass)
    {
      MoveShooterDown(true);
      MoveLoaderDown(true);
    }
  else if (cg == Shoot)
    {
      MoveShooterDown(false);
      MoveLoaderDown(true);
    }
  cage = cg;
}
void TorShooter::MoveShooterDown(bool downFlag)
{
  if (downFlag && !shooterDown)
    {
      //move shooter down
    }
  else if (!downFlag && shooterDown)
    {
      //move shooter up
    }
}
void TorShooter::MoveLoaderDown(bool downFlag)
{
  if (downFlag && !loaderDown)
    {
      loadSolenoid->Set(Consts::LOADER_PISTON_EXTENDED); //extend loader pistons
    }
  else if (!downFlag && loaderDown)
    {
      loadSolenoid->Set(!Consts::LOADER_PISTON_EXTENDED); //retract loader pistons
    }
}
TorShooter::cageState TorShooter::GetCageState()
{
  return cage;
}
TorShooter::shooterState TorShooter::GetShooterState()
{
  return state;
}
void TorShooter::SetJagSpeed(float speed)
{
  topWheelJag->Set(speed);
  topWheelJag1->Set(speed);
  bottomWheelJag->Set(speed);
  bottomWheelJag1->Set(speed);
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
          if (cage == Load || cage == Pass)
            {
              MoveCage(Shoot);
            }
        }
      else
        {
          state = Loading;
        }
      break;
    }
  case Loading:
    {
      if (IsLoaded() && !runButton)
        {
          state = Loaded;
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
      if(!IsLoaded())
        {
          state = Loading;
          MoveCage(Load);
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
