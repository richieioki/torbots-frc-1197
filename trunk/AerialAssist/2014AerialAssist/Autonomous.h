#pragma once

#include "TorDrivetrain.h"
#include "TorShooter.h"

/*
 * Holds autonomous functions, also should have some sort of way to switch between autonomous function
 */


class Autonomous {
  
  public:
    
    //Autonomous(TorDriveTrain &drive, TorShooter &shooter);
    ~Autonomous();
    void runAutonomous();  //is called to run the various auto modes.  Function must access
  
  private:
    
    void AutoMode1(); //just drive forward
    void AutoMode2(); //target and fire, left.  Then drive forward
    void AutoMode3(); //target and fire right.  Then drive forward
    void AutoMode4(); //target and fire center.  Then drive forward
    
};
