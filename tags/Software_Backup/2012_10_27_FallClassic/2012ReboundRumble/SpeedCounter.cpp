#include "SpeedCounter.h"
#include "PIDSource.h"

//constructor
SpeedCounter::SpeedCounter(UINT32 channel) : Counter(channel)
{
	
}

//destructor
SpeedCounter::~SpeedCounter()
{
	
}

//from PIDSource
double SpeedCounter::PIDGet()
{
	/*mTimeCurr = mTimer->Get();
	mTimeElap = mTimeCurr - mTimeLast;
	if (mTimeElap != 0)
		mSpeed = this->Get() / (6 * mTimeElap);
	else
		mSpeed = 0;
	mTimeLast = mTimeCurr;
	//this->Reset();
	mSpeed *= -1;*/
	return mSpeed/-50;
	
	
}

double SpeedCounter::GetSpeed()
{
	return mSpeed;
}

void SpeedCounter::SetSpeed(double speed)
{
	mSpeed = speed;
}
