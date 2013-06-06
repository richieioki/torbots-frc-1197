#pragma once

#include "WPILib.h"

class TorFeeder
{
public:
    TorFeeder();
	bool isReadyToLoad (); 
	bool isReadyToShoot (); 
	void checkDiskLoader();
	bool isDiskRight();
	void Raise1 (); 
	void InitalizeAuto(int numberDisks);
	void ShotDisk();
	void PrepareToFire();

	//what are the states that the feeder has?
	void GetState ();
	
private:

	//disc information
	int NumDisks;
	bool feederDisks[4];		//I assumed that 0 = top and 3 = last spot.
	bool diskOrientation[4];	// true = right side up, false = upside down
	bool readyToFire;			//instead of an enum state status, just a bool.  Is ready to fire / is not ready to fire.
	
	//Sensors
	Jaguar *elevator;
	AnalogChannel *elevatorPOT;
	DigitalInput *diskSensor;
	DigitalInput *diskOrientionSensor;

	//pot 
	//need to figure out how to setup the break beam
};
