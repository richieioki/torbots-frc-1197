#include "SpeedCounter.h"
#include "PIDSource.h"
#include "Consts.h"
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
	return mSpeed;/// -50; //50 is from 2012. no idea why it's there. might be to normalize vals
	
	
}

double SpeedCounter::GetSpeed(double timeElap)
{
        mCount = (float)Get();
        mSpeed = mCount/ (Consts::shooterCountsPerRev * timeElap);
	return mSpeed;
}

void SpeedCounter::SetSpeed(double speed)
{
        
	mSpeed = speed;
	
}
