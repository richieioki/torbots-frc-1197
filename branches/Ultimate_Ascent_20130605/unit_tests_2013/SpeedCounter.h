#pragma once

#include "Counter.h"
#include "PIDSource.h"
#include "Consts.h"

class SpeedCounter : public Counter, public PIDSource
{
public:
	//constructor
	SpeedCounter(UINT32 channel);
	
	//destructor
	~SpeedCounter();
	
	//from PIDSource
	double PIDGet();
	
	//return mSpeed
	double GetSpeed(double timeElap);
	
	void SetSpeed(double speed);
	
private:
	double mSpeed;
	double mCount;
};
