#pragma once

#include <WPILib.h>
#include "Vision/AxisCamera.h"
namespace TargetId
{
enum TargetId{High, Middle, Low};
}
typedef TargetId::TargetId TargetIdType;

struct TorTarget
{
  float xTheta;
  float yTheta;
  TargetIdType targetType;
  float distance;  
};

class TorTargetAcquire
{
  //AxisCamera &camera = AxisCamera::GetInstance();
public:
  TorTargetAcquire();
  void AcquireTargets();
  TorTarget GetTarget(TargetIdType value);//Create and then return a TorTarget of value type 
  TorTarget GetTarget();//return the best target.

private:
  //more private data, the vector of particle reports etc.
  AxisCamera& m_camera;
  TorTarget m_target;
 
  void GetImage();
  void GenerateTargets();
  //more to come
};
