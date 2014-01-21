#include "TorAutonomous.h"

TorAutonomous::TorAutonomous(TorShooter& myShooter)
: shooter(myShooter)
{
  jagsRunning = false;
}
void TorAutonomous::AutoFire()
{
  if (jagsRunning)
    {
      shooter.ManualFire();
    }
}
void TorAutonomous::RunJags()
{
  shooter.MoveCage(TorShooter::Shoot);
  shooter.SetJagSpeed(Consts::SHOOTER_FIRE_SPEED);
  jagsRunning = true;
}
void TorAutonomous::StopJags()
{
  shooter.SetJagSpeed(0.0);
  jagsRunning = false;
}
