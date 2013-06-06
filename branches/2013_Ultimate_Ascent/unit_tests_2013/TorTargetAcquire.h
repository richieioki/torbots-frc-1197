#pragma once

#include "WPILib.h" //<> or "" ?
#include "Vision/AxisCamera.h"
#include "Vision/RGBImage.h"
#include "Vision/BinaryImage.h"
#include "Math.h"
#include "Consts.h"

namespace TargetId
{
enum TargetId{High, Middle, Low};
}
typedef TargetId::TargetId TargetIdType;


class TorTargetAcquire
{
  //AxisCamera &camera = AxisCamera::GetInstance();
  
  struct Scores 
  {
    double rectangularity;
    double aspectRatioInner;
    double aspectRatioOuter;
    double xEdge;
    double yEdge;
    double xNorm;
    double yNorm;
    double yTop;

  };
  
  struct TorTarget
  {
    float xTheta;
    float yTheta;
    float yThetaAdjusted;
    TargetIdType targetType;
    float distance;  
    bool isValid;
  };
  
  Scores *scores;
 
  TorTarget *m_target;
  BinaryImage *image;
  vector<ParticleAnalysisReport> *particles;
  
  
public:
  TorTargetAcquire();
  void AcquireTargets(); //just grab the image and grab the particles
  void GetTarget(TargetIdType value);//Create and then return a TorTarget of value type 
  void GetTarget();//return the best target.
  float GetXTheta();
  float GetYTheta();
  float GetYThetaAdjusted();
  float GetDistance();
  TargetIdType GetType();
  bool isTarget();
  
private:
  //more private data, the vector of particle reports etc.
  AxisCamera& m_camera;
  
  bool scoreCompare (Scores scores, bool outer); //compares each particle to a necessary score
  double scoreRectangularity(ParticleAnalysisReport *report);
  double scoreAspectRatio(ParticleAnalysisReport *report, bool outer);

  void GetImage(); //Gets cleaned image
  void GenerateTargets(vector<ParticleAnalysisReport> *particles); //scores each particle
  //more to come
};
