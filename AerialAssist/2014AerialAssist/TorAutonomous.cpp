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
  shooter.MoveShooter(0.45);
  Wait(0.8); //replace once we have pot values
  shooter.MoveShooter(0);
  shooter.SetJagSpeed(Consts::SHOOTER_FIRE_SPEED);
  jagsRunning = true;
}
void TorAutonomous::StopJags()
{
  shooter.SetJagSpeed(0.0);
  jagsRunning = false;
}
void TorAutonomous::AutoMode1()
{
  time_t start;
  time_t end;
  double timer; //number of seconds taken to travel
  time(&start); //start timer
  //myAutonomous->RunJags();
  //    myTorbotDrive->DriveStraight(0.6, 180.5f); //drive all the way to target
  time(&end);
  timer = difftime(end, start);
  if (timer < 5.0) //if (timer > 5)
    {
      //myAutonomous->AutoFire(); //fire
    }
  else
    {
      //take picture
      if (true) //if (goal is hot)
        {
          //myAutonomous->AutoFire(); //fire
        }
      else
        {
          Wait(6.0 - timer); //wait 5-timer plus a little extra
          //myAutonomous->AutoFire(); //fire
        }
    }


}
