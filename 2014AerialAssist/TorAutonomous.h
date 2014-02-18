#pragma once

#include "TorShooter.h"
#include "Consts.h"

/*
 * Holds autonomous functions, also should have some sort of way to switch between autonomous function
 */


class TorAutonomous {
  
  public:
    
    TorAutonomous(TorShooter& myShooter, TorbotDrive& myTorbotDrive);
    ~TorAutonomous();
    void runAutonomous();  //is called to run the various auto modes.  Function must access
    
    void AutoFire();
    void RunShooter();
    void StopShooter();
  
  private:
    
    void AutoMode1(); //everything works
    void AutoMode2(); //camera doesn't work
    void AutoMode3(); 
    void AutoMode4(); 
    
    TorShooter& shooter;
    TorbotDrive& torbotDrive; 
    bool jagsRunning;
    
};
