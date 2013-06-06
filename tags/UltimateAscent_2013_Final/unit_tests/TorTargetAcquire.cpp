#include "TorTargetAcquire.h"
#include "Consts.h"

#define AREA_MINIMUM 500
#define RECTANGULARITY_LIMIT 60
#define ASPECT_RATIO_LIMIT 75
#define X_EDGE_LIMIT 40
#define Y_EDGE_LIMIT 60
#define GEAR_RATIO 12.0/26.0

 
 
TorTargetAcquire::TorTargetAcquire()
: m_camera ( AxisCamera::GetInstance())
{

  m_camera.WriteResolution(AxisCamera::kResolution_320x240);
  m_camera.WriteBrightness(50);
  m_camera.WriteCompression(20);
  
  m_target = new TorTarget;
}

/****************************
 * PUBLIC FUNCTIONS
 ****************************/

void TorTargetAcquire::AcquireTargets()
{
  //target particles vector
  
  GetImage();
  particles = image->GetOrderedParticleAnalysisReports();
}


void TorTargetAcquire::GetTarget() //TODO
//RETURNS BEST TARGET IN VIEW
{
  // GenerateTargets

  m_target->isValid = false;

  if (!particles->empty())
    {
                                        
      GenerateTargets(particles); //score each particle
      
      // first target in the list is the one closest to the center of the image; ie Best
      // use array[0] as best target
      m_target->xTheta = scores[0].xNorm * Consts::CAMERA_FOV;
      m_target->isValid = true;
      m_target->yTheta = scores[0].yNorm * Consts::CAMERA_V_FOV;
      m_target->yThetaAdjusted = scores[0].yTop * Consts::CAMERA_V_FOV;
      // check if we have a valid target and set the type that was found
      if (scoreCompare(scores[0], true)) // true = outer/Middle target
        {
          m_target->targetType = TargetId::Middle;
          m_target->isValid = true;
        }
      else if (scoreCompare(scores[0], false)) // false = innter/High target
        {
          m_target->targetType = TargetId::High;
          m_target->isValid = true;
        }

      m_target->distance = particles->size(); // debug code until we have a reason to return Distance
      
    } //end !(particles empty)

} // end GetTarget



void TorTargetAcquire::GetTarget(TargetIdType type) //TODO
//RETURNS TARGET OF TYPE GIVEN
{
//  // GenerateTargets

  m_target->isValid = false;

  if (!particles->empty())
    {
                                        
      GenerateTargets(particles); //score each particle

      for (unsigned int i=0; i < particles->size(); i++)
        {
          if (scoreCompare(scores[i], type == TargetId::Middle)) // outer flag is true if we are tracking the Middle/Outer target
            {
              m_target->xTheta = scores[i].xNorm * Consts::CAMERA_FOV;
              
              m_target->yTheta = scores[i].yNorm * Consts::CAMERA_V_FOV;
              
              m_target->targetType = type;
              m_target->isValid = true;
              
            }//grab reports
        }
      m_target->distance = particles->size();
      
    } //end !(particles empty)

} // end GetTargets

/************************************
 * PRIVATE FUNCTIONS
 ************************************/


void TorTargetAcquire::GetImage() //grabs clean image
{
  ColorImage *rawImage;
  ParticleFilterCriteria2 criteria[] = {
                                     {IMAQ_MT_AREA, AREA_MINIMUM, 65535, false, false}};

  rawImage = m_camera.GetImage();                          
    
  BinaryImage *initialFilterImage;
  initialFilterImage = rawImage->ThresholdHSV(60, 150, 90, 255, 20, 255); //keeps green
  //initialFilterImage->Write("/Initial.bmp");
  
  BinaryImage *filledImage = initialFilterImage->ConvexHull(false); //fills in rect
  //filledImage->Write("/ConvexHull.bmp");
  
  image = filledImage->ParticleFilter(criteria, 1); //clean-up/
  //image->Write("/Clean.bmp");
  
  //delete images
  delete rawImage;
  delete filledImage;
  delete initialFilterImage;

  
  return;
}

void TorTargetAcquire::GenerateTargets(vector<ParticleAnalysisReport> *particles) // loops through all particles found  and scores each one
{
       scores = new Scores [particles->size()]; //houses scores for each particle
        
        for (unsigned int i = 0; i < particles->size(); i++)
          {
            ParticleAnalysisReport *report = &(particles->at(i)); //get report
            scores[i].aspectRatioOuter = scoreAspectRatio(report, true);
            scores[i].aspectRatioInner = scoreAspectRatio(report, false);
//            scores[i].xEdge = scoreXEdge(initialFilterImage, report);
//            scores[i].yEdge = scoreYEdge(initialFilterImage, report);
            scores[i].rectangularity = scoreRectangularity(report);
            scores[i].xNorm = report->center_mass_x_normalized;
            scores[i].yNorm = report->center_mass_y_normalized;
            scores[i].yTop = report->center_mass_y_normalized+(report->boundingRect.height/2.0/120.0); // distance to top from center, normalized
            

          }//for loop bracket
            
} 
// End GenerateTargets



/**
 * Compares scores to defined limits and returns true if the particle appears to be a target
 * 
 * @param scores The structure containing the scores to compare
 * @param outer True if the particle should be treated as an outer target, false to treat it as a center target
 * 
 * @return True if the particle meets all limits, false otherwise
 */
bool TorTargetAcquire::scoreCompare(Scores scores, bool outer){
        bool isTarget = true;

        isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
        if(outer){
                isTarget &= scores.aspectRatioOuter > ASPECT_RATIO_LIMIT;
        } else {
                isTarget &= scores.aspectRatioInner > ASPECT_RATIO_LIMIT;
        }
        // commented these out because they were causing high goal from being recognized. dont think we need these checks
        //isTarget &= scores.xEdge > X_EDGE_LIMIT;
        //isTarget &= scores.yEdge > Y_EDGE_LIMIT;

        return isTarget;
}

/**
 * Computes a score (0-100) estimating how rectangular the particle is by comparing the area of the particle
 * to the area of the bounding box surrounding it. A perfect rectangle would cover the entire bounding box.
 * 
 * @param report The Particle Analysis Report for the particle to score
 * @return The rectangularity score (0-100)
 */

double TorTargetAcquire::scoreRectangularity(ParticleAnalysisReport *report){
        if(report->boundingRect.width*report->boundingRect.height !=0){
                return 100*report->particleArea/(report->boundingRect.width*report->boundingRect.height);
        } else {
                return 0;
        }       
}



/**
 * Computes a score (0-100) comparing the aspect ratio to the ideal aspect ratio for the target. This method uses
 * the equivalent rectangle sides to determine aspect ratio as it performs better as the target gets skewed by moving
 * to the left or right. The equivalent rectangle is the rectangle with sides x and y where particle area= x*y
 * and particle perimeter= 2x+2y
 * 
 * @param image The image containing the particle to score, needed to perform additional measurements
 * @param report The Particle Analysis Report for the particle, used for the width, height, and particle number
 * @param outer Indicates whether the particle aspect ratio should be compared to the ratio for the inner target or the outer
 * @return The aspect ratio score (0-100)
 */
double TorTargetAcquire::scoreAspectRatio(ParticleAnalysisReport *report, bool outer){

  double rectLong, rectShort, idealAspectRatio, aspectRatio, rawAspectRatio;
  idealAspectRatio = outer ? (62/29) : (62/20);   //Dimensions of goal opening + 4 inches on all 4 sides for reflective tape

  //imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE, &rectLong);
  //imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE, &rectShort);

  rectLong = report->boundingRect.width;
  rectShort = report->boundingRect.height;
  rawAspectRatio = (rectLong/rectShort)/(scoreRectangularity(report)/100.0);


  //Divide width by height to measure aspect ratio
  if(report->boundingRect.width > report->boundingRect.height){
      //particle is wider than it is tall, divide long by short
      //aspectRatio = 100*(1-fabs((1-((rectLong/rectShort)/idealAspectRatio))));
      aspectRatio = 100*(1-fabs((1-(rawAspectRatio/idealAspectRatio))));
  } else {
      //particle is taller than it is wide, divide short by long
      aspectRatio = 100*(1-fabs((1-(rawAspectRatio/idealAspectRatio))));
  }

  return (max(0, min(aspectRatio, 100)));         //force to be in range 0-100
}

float TorTargetAcquire::GetXTheta(){
  return m_target->xTheta;

}

float TorTargetAcquire::GetYTheta() {
  return m_target->yTheta;
}

float TorTargetAcquire::GetYThetaAdjusted() {
    return m_target->yThetaAdjusted;
}

float TorTargetAcquire::GetDistance() {
  return m_target->distance;
}

TargetIdType TorTargetAcquire::GetType() {
  return m_target->targetType;
}

bool TorTargetAcquire::isTarget() {
  
      return m_target->isValid;
  
}

