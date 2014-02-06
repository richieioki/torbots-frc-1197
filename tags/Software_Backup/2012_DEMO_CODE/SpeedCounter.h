#include "Counter.h"
#include "PIDSource.h"

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
	double GetSpeed();
	
	void SetSpeed(double speed);
	
private:
	double mSpeed;
};
