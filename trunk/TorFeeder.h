#pragma once

#include "WPILib.h"
#include "Consts.h"

class TorFeeder
{
public:
    TorFeeder(DriverStationLCD& theDS, DriverStation& theDSOut);
	bool isReadyToLoad (); 
	bool isReadyToShoot (bool override = false); 
	bool checkDiskLoader();
	bool isDiskRight();
	void Raise1 (); 
	void InitalizeAuto(int numberDisks);
	void ShotDisk();
	void PrepareToFire();
	int getNumDisks() {return NumDisks;}
	int getFeederSensor();
	void resetFeederToLoad();
	void resetDisks();
	void nudge();
	bool getFeeder(int position); //position in feeder, slots = 1-4
	void updateDisks();
	//what are the states that the feeder has?
	void GetState ();
	
private:

	//disc information
	int NumDisks;
	bool feederDisks[4];		//I assumed that 0 = top and 3 = last spot.
	bool diskOrientation[4];	// true = right side up, false = upside down
	bool readyToFire;
	//instead of an enum state status, just a bool.  Is ready to fire / is not ready to fire.
	
	DriverStationLCD& ds;
	DriverStation& dsOut;
	Timer *timer;
	
	//Sensors
	Jaguar *elevator;
	AnalogChannel *elevatorPOT;
	DigitalInput *diskSensor;
	DigitalInput *diskOrientionSensor;
	DigitalInput *feederReadySensor;
	//pot feederReadySensor
	//need to figure out how to setup the break beam
};
