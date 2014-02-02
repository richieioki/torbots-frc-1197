#pragma once

#include "TorShooter.h"

/*
 * Holds autonomous functions, also should have some sort of way to switch between autonomous function
 */


class TorAutonomous {
  
  public:
    
    TorAutonomous(TorShooter &shooter);
    ~TorAutonomous();
    void runAutonomous();  //is called to run the various auto modes.  Function must access
    
    void AutoFire();
    void RunJags();
    void StopJags();
  
  private:
    
    void AutoMode1(); //everything works
    void AutoMode2(); //camera doesn't work
    void AutoMode3(); 
    void AutoMode4(); 
    
    TorShooter shooter;
    bool jagsRunning;
    
};
