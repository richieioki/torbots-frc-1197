#pragma once

#include "TorDrivetrain.h"
#include "TorShooter.h"

/*
 * Holds autonomous functions, also should have some sort of way to switch between autonomous function
 */


class Autonomous {
  
  public:
    
    Autonomous(TorDriveTrain &drive, TorShooter &shooter);
    ~Autonomous();
    void runAutonomous();  //is called to run the various auto modes.
  
  private:
    
    void AutoMode1();
    void AutoMode2();
    void AutoMode3();
    
};
