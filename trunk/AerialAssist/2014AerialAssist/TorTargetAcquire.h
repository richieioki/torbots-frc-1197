#include "WPILib.h"
#include "Vision/AxisCamera.h"
#include "Vision/RGBImage.h"
#include "Vision/BinaryImage.h"
#include "Math.h"
#include "Consts.h"


class TorTargetAcquire
{
  struct Scores {
    double rectangularity;
    double aspectRatioVertical;
    double aspectRatioHorizontal;
  };

  struct TargetReport {
    int verticalIndex;
    int horizontalIndex;
    bool Hot;
    double totalScore;
    double leftScore;
    double rightScore;
    double tapeWidthScore;
    double verticalScore;
  };

  Scores *scores;
  DriverStationLCD *ds;
  
public:
  TorTargetAcquire();
  void visionTracking();
  //void checkHotTarget();

private:
  AxisCamera& m_camera;
  
  
  
  double computeDistance(BinaryImage *image, ParticleAnalysisReport *report);
  double scoreAspectRatio(BinaryImage *image, ParticleAnalysisReport *report, bool vertical);
  bool scoreCompare(Scores scores, bool vertical);
  double scoreRectangularity(ParticleAnalysisReport *report);
  double ratioToScore(double ratio);
  bool hotOrNot(TargetReport target);
  //void GetImage();
  
  
  
  
  
  

  
  
};
