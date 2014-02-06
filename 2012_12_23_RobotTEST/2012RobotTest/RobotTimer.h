//Enhanced Timer
#include "Timer.h"

class RobotTimer : public Timer
{
public:
	//constructor
	RobotTimer();

	//destructor
	~RobotTimer();
	
	void Start();
	
	void Reset();
	
	void Stop ();
	
	double Get();
	
	double TimeElap();
	
private:
	double timeCurr;
	double timeLast;
	double timeTemp;
	Timer *timer;
};
