#pragma once

#include "WPILib.h"
#include "TorShooter.h"
#include "TorJagDrive.h"
#include "TorShooter.h"

class TorClimber 
{
public:
	TorClimber(Joystick& theJosytick, TorbotDrive& drive, TorShooter& torShooter, DriverStationLCD& ds);		
	void Run(); //Check if climber button has been pressed and execute ManageState function
	bool ManageState(); //executes a while look that references the state machine
	bool checkLimits(int limit); //checks if we have hit a limit switch triggering an abort
	void Abort(); //stops all motors and puts the robot in an infinite loop
	void pullHands(); //Pulls the hands down
	void liftHands(float speed); //Lift the hands up
	void pullDown();

private:

	Jaguar *liftJag1, *liftJag2;

	enum State {
		Init, Pull, Lift, Shoot, Stop
	};
	
	
	
	void shootIntoGoal();
	
	//function that returns the state of the hands/feet sensors
	//also aborts if the two sensors differ.
	bool getHands(); 
	bool getFeet();
	
	//inputs
	Joystick& m_joystick;
	TorbotDrive& m_tordrive;
	TorShooter& m_shooter;
	DriverStationLCD& mds;
	
	//variables
	int level;  //starts at zero, but counts the levels we have climbed.
	float motorSpeed;
	float postInitSpeed;
	State state;
	bool climbButton;
	bool failed;
	
	//Sensors definitions below!
	//Reed switches to track if hands are depressed or up
	DigitalInput *leftHandSwitch, *rightHandSwitch, *leftFootSwitch, *rightFootSwitch;
	//Limit switch to hard top forklift movement
	DigitalInput *topForkLift, *bottomForkLift;
	//Limit switch that is our initial raise point.
	DigitalInput *initPosSwitch;
	Timer *timer;
};
