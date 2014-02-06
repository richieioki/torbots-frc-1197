/*
 * RobotTimer.cpp
 *
 *  Created on: Nov 2, 2012
 *      Author: TorBot
 */

#include "RobotTimer.h"
#include "Timer.h"
/*
 * Default constructor
 */
RobotTimer::RobotTimer()
{
	timer = new Timer();
}

/*
 * Default destructor
 */
RobotTimer::~RobotTimer()
{
	
}
void RobotTimer::Start()
{
	timer->Start();
}

void RobotTimer::Reset()
{
	timer->Reset();
}

void RobotTimer::Stop()
{
	timer->Stop();
}

double RobotTimer::Get()
{
	return timer->Get();
}

double RobotTimer::TimeElap()
{
	if (!timeLast > 0)
	{
		timeLast = timer->Get();
	}
	timeCurr = timer->Get();
	timeTemp = timeLast;
	timeLast = timeCurr;
	return timeCurr - timeTemp;
}
