#include "TorAutonomous.h"

TorAutonomous::TorAutonomous(TorShooter& myShooter)
: shooter(myShooter)
{
  jagsRunning = false;
}
void TorAutonomous::runAutonomous()
{
  AutoMode2();
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
  shooter.SetJagSpeed(shooter.ShooterSpeed());
  jagsRunning = true;
}
void TorAutonomous::StopJags()
{
  shooter.SetJagSpeed(0.0);
  jagsRunning = false;
}
void TorAutonomous::AutoMode1() //everything works
{
  time_t start;
  time_t end;
  double timer; //number of seconds taken to travel
  time(&start); //start timer
  RunJags();
  //    myTorbotDrive->DriveStraight(0.6, 180.5f); //drive all the way to target
  time(&end);
  timer = difftime(end, start);
  if (timer > 5.0) //if (timer > 5)
    {
      AutoFire(); //fire
    }
  else
    {
      //take picture
      if (true) //if (goal is hot)
        {
          AutoFire(); //fire
        }
      else
        {
          Wait(6.0 - timer); //wait 5-timer plus a little extra
          AutoFire(); //fire
        }
    }


}
void TorAutonomous::AutoMode2()
{
  RunJags();
  //drive forward 180.5 inches to target
  AutoFire();
}
