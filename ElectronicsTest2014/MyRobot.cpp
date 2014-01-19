#include "WPILib.h"
#include "Vision/RGBImage.h"
#include "Vision/BinaryImage.h"
#include "Math.h"

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 


#define Y_IMAGE_RES 480         //X Image resolution in pixels, should be 120, 240 or 480
//#define VIEW_ANGLE 49           //Axis M1013
//#define VIEW_ANGLE 41.7               //Axis 206 camera
#define VIEW_ANGLE 37.4  //Axis M1011 camera
#define PI 3.141592653

//Score limits used for target identification
#define RECTANGULARITY_LIMIT 40
#define ASPECT_RATIO_LIMIT 55

//Score limits used for hot target determination
#define TAPE_WIDTH_LIMIT 50
#define VERTICAL_SCORE_LIMIT 50
#define LR_SCORE_LIMIT 50

//Minimum area of particles to be considered
#define AREA_MINIMUM 150

//Maximum number of particles to process
#define MAX_PARTICLES 8


 

class RobotDemo : public SimpleRobot
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
	//RobotDrive myRobot; // robot drive system
	Joystick stick; // only joystick
	Compressor *compressor;
	DriverStationLCD *ds;
	Jaguar *jag1;
	Jaguar *jag2;
	Jaguar *jag3;
	Jaguar *jag4;
	Jaguar *jag5;
	Jaguar *jag6;
	Jaguar *jag7;
	Jaguar *jag8;
	Jaguar *jag9;
	Jaguar *jag10;
	

public:
	RobotDemo():
		//myRobot(3,4,1,2),	// these must be initialized in the same order
		stick(1)		// as they are declared above.
	{
		//myRobot.SetExpiration(0.1);
		compressor = new Compressor (1,1);
		jag1 = new Jaguar(1);
		jag2 = new Jaguar(2);
		jag3 = new Jaguar(3);
		jag4 = new Jaguar(4);
		jag5 = new Jaguar(5);
		jag6 = new Jaguar(6);
		jag7 = new Jaguar(7);
		jag8 = new Jaguar(8);
		jag9 = new Jaguar(9);
		jag10 = new Jaguar(10);
		ds = DriverStationLCD::GetInstance();
	}

	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous()
	{
		//myRobot.SetSafetyEnabled(false);
		//myRobot.Drive(-0.5, 0.0); 	// drive forwards half speed
		Wait(2.0); 				//    for 2 seconds
		//myRobot.Drive(0.0, 0.0); 	// stop robot
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl()
	{
		//myRobot.SetSafetyEnabled(true);
	        ds->Clear();
	        ds->Printf(DriverStationLCD::kUser_Line1, 1, "Image Test");
	        ds->UpdateLCD();
		compressor->Start();
		
/*****************************************************************************************
 * *****************************Vision Tracking
 ******************************************************************************************/		
		
		Scores *scores;
		TargetReport target;
		int verticalTargets[MAX_PARTICLES];
		int horizontalTargets[MAX_PARTICLES];
		int verticalTargetCount, horizontalTargetCount;
		Threshold threshold (60, 150, 90, 255, 20, 255); //ORIGINAL VALS(105, 137, 230, 255, 133, 183);      //HSV threshold criteria, ranges are in that order ie. Hue is 60-100
		Threshold testThreshold (0, 359, 0, 255, 0, 255);
		ParticleFilterCriteria2 criteria[] = {
		    {IMAQ_MT_AREA, AREA_MINIMUM, 65535, false, false}
		};      	
		
		AxisCamera &camera = AxisCamera::GetInstance();
		camera.WriteResolution(AxisCamera::kResolution_320x240);
		camera.WriteBrightness(50);
		camera.WriteCompression(20);
		ColorImage *image;
		//image = new RGBImage("/testImage.jpg");         // get the sample image from the cRIO flash

		
		
		
		while(IsOperatorControl() && IsEnabled()) {
		    
		    image = camera.GetImage();                            //To get the images from the camera comment the line above and uncomment this one
		    image->Write("/raw.bmp");

		    BinaryImage *thresholdImage = image->ThresholdRGB(threshold);   // get just the green target pixels
		    thresholdImage->Write("/threshold.bmp");        
		    //test image
		    //BinaryImage *rawImage = image->ThresholdRGB(0,255,0,255,0,255);
		    //rawImage->Write("/raw.bmp");
		    BinaryImage *filledImage = thresholdImage->ConvexHull(false); //fill in particles
		    filledImage->Write("/filled.bmp");
		    
		    ds->Printf(DriverStationLCD::kUser_Line2, 2, "Image 1 Printed");
		    ds->UpdateLCD();
		    BinaryImage *filteredImage = thresholdImage->ParticleFilter(criteria, 1);       //Remove small particles
		    filteredImage->Write("/Filtered.bmp");
		    ds->Printf(DriverStationLCD::kUser_Line3, 2, "Image 2 Printed");
		    ds->UpdateLCD();
		    
		    delete filteredImage;
		    delete thresholdImage;
		    delete image;
		    ds->Printf(DriverStationLCD::kUser_Line4, 2, "Image Deleted");
		    ds->UpdateLCD();
		    
		    if (stick.GetY() > 0 && fabs(stick.GetY()) > 0.05)
		      {
		        jag3->Set(-0.3);
		      }
		    else if (stick.GetY() < 0 && fabs(stick.GetY()) > 0.05)
		      {
		        jag3->Set(0.3);
		      }
		    else
		      {
		        jag3->Set(0.0);
		      }
		                           
		}
	}	
				
		
		
		/*
		while (IsOperatorControl())
		{
			//myRobot.ArcadeDrive(stick); // drive with arcade style (use right stick)
			//Wait(0.005);				// wait for a motor update time
		    
		    ColorImage *image;
		    //image = new RGBImage("/testImage.jpg");         // get the sample image from the cRIO flash

		    image = camera.GetImage();                            //To get the images from the camera comment the line above and uncomment this one
		    image->Write("/Raw.bmp");
		    ds->Printf(DriverStationLCD::kUser_Line2, 2, "Image Printed");
		    ds->UpdateLCD();
		    BinaryImage *thresholdImage = image->ThresholdHSV(threshold);   // get just the green target pixels
		    thresholdImage->Write("/threshold.bmp");
		    BinaryImage *filteredImage = thresholdImage->ParticleFilter(criteria, 1);       //Remove small particles
		    filteredImage->Write("/Filtered.bmp");
		     
		    vector<ParticleAnalysisReport> *reports = filteredImage->GetOrderedParticleAnalysisReports(); 
		    
		    verticalTargetCount = horizontalTargetCount = 0;
		    
		    if(reports->size() > 0)
		      {
		        scores = new Scores[reports->size()];
		        for (unsigned int i = 0; i < MAX_PARTICLES && i < reports->size(); i++) {
		            ParticleAnalysisReport *report = &(reports->at(i));
		            //TODO PRINT REPORTS AND CHECK THE PARTICLES
		            
		            scores[i].rectangularity = scoreRectangularity(report);
		            scores[i].aspectRatioVertical = scoreAspectRatio(filteredImage, report, true);
		            scores[i].aspectRatioHorizontal = scoreAspectRatio(filteredImage, report, false);
		            if(scoreCompare(scores[i], false))
		              {
		                //printf("particle: %d  is a Horizontal Target centerX: %d  centerY: %d \n", i, report->center_mass_x, report->center_mass_y);
		                ds->Printf(DriverStationLCD::kUser_Line1, 1, "Horiz(x): %d", report->center_mass_x);
		                ds->Printf(DriverStationLCD::kUser_Line2,1, "Horiz(y): %d", report->center_mass_y);
		                //horizontalTargets[horizontalTargetCount++] = i; //Add particle to target array and increment count
		              } 
		            else if (scoreCompare(scores[i], true)) 
		              {
		                ds->Printf(DriverStationLCD::kUser_Line1, 1, "Vert(x): %d",report->center_mass_x);
		                ds->Printf(DriverStationLCD::kUser_Line2,1, "Vert(y): %d",report->center_mass_y);
		                  //printf("particle: %d  is a Vertical Target centerX: %d  centerY: %d \n", i, report->center_mass_x, report->center_mass_y);
		                  //verticalTargets[verticalTargetCount++] = i;  //Add particle to target array and increment count
		              } 
		            else 
		              {
		                ds->Printf(DriverStationLCD::kUser_Line1, 1, "Not Target");
		                ds->Printf(DriverStationLCD::kUser_Line2,1, "Not Target");
		                  //printf("particle: %d  is not a Target centerX: %d  centerY: %d \n", i, report->center_mass_x, report->center_mass_y);
		              }
		      }
		        
		        delete filteredImage;
		        delete thresholdImage;
		        delete image;

		        //delete allocated reports and Scores objects also
		        delete scores;
		        delete reports;
		    */
		    
/**********************************************************************************************************
 * END VISION TRACKING BUTTON
 **********************************************************************************************************/		    
		    
		    /*
		    
		    if (stick.GetRawButton(1))
		      {
		        jag1->Set(1.0);
		        jag2->Set(1.0);
		        jag3->Set(1.0);
		        jag4->Set(1.0);
		        jag5->Set(1.0);
		        jag6->Set(1.0);
		        jag7->Set(1.0);
		        jag8->Set(1.0);
		        jag9->Set(1.0);
		        jag10->Set(1.0);		        
		      }
		    else if (stick.GetRawButton(3))
                      {
                        jag1->Set(-1.0);
                        jag2->Set(-1.0);
                        jag3->Set(-1.0);
                        jag4->Set(-1.0);
                        jag5->Set(-1.0);
                        jag6->Set(-1.0);
                        jag7->Set(-1.0);
                        jag8->Set(-1.0);
                        jag9->Set(-1.0);
                        jag10->Set(-1.0);                        
                      }
		    else if (stick.GetY() < -0.2)
		      {
		        jag1->Set(0.3);
		        jag2->Set(0.3);
		        jag3->Set(0.3);
		        jag4->Set(0.3);
		        jag5->Set(0.3);
		        jag6->Set(0.3);
		        jag7->Set(0.3);
		        jag8->Set(0.3);
		        jag9->Set(0.3);
		        jag10->Set(0.3);
		      }
		    else if(stick.GetY() > 0.2)
		      {
		        jag1->Set(-0.3);
		        jag2->Set(-0.3);
		        jag3->Set(-0.3);
		        jag4->Set(-0.3);
		        jag5->Set(-0.3);
		        jag6->Set(-0.3);
		        jag7->Set(-0.3);
		        jag8->Set(-0.3);
		        jag9->Set(-0.3);
		        jag10->Set(-0.3);  
		      }
		    else
		      {
		        jag1->Set(0.0);
		        jag2->Set(0.0);
		        jag3->Set(0.0);
		        jag4->Set(0.0);
		        jag5->Set(0.0);
		        jag6->Set(0.0);
		        jag7->Set(0.0);
		        jag8->Set(0.0);
		        jag9->Set(0.0);
		        jag10->Set(0.0);
		      }*/
		    
		//}
	
	
	
	/**
	 * Runs during test mode
	 */
	void Test() {

	}
	
	
/************************************************************************
 * VISION TRACKING RELATED FUNCTIONS
 *************************************************************************/	
	
        /**
         * Computes the estimated distance to a target using the height of the particle in the image. For more information and graphics
         * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
         * 
         * @param image The image to use for measuring the particle estimated rectangle
         * @param report The Particle Analysis Report for the particle
         * @return The estimated distance to the target in feet.
         */
        double computeDistance (BinaryImage *image, ParticleAnalysisReport *report) {
                double rectLong, height;
                int targetHeight;
                
                imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE, &rectLong);
                //using the smaller of the estimated rectangle long side and the bounding rectangle height results in better performance
                //on skewed rectangles
                height = min(report->boundingRect.height, rectLong);
                targetHeight = 32;
                
                return Y_IMAGE_RES * targetHeight / (height * 12 * 2 * tan(VIEW_ANGLE*PI/(180*2)));
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
        double scoreAspectRatio(BinaryImage *image, ParticleAnalysisReport *report, bool vertical){
                double rectLong, rectShort, idealAspectRatio, aspectRatio;
                idealAspectRatio = vertical ? (4.0/32) : (23.5/4);      //Vertical reflector 4" wide x 32" tall, horizontal 23.5" wide x 4" tall
                
                imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE, &rectLong);
                imaqMeasureParticle(image->GetImaqImage(), report->particleIndex, 0, IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE, &rectShort);
                
                //Divide width by height to measure aspect ratio
                if(report->boundingRect.width > report->boundingRect.height){
                        //particle is wider than it is tall, divide long by short
                        aspectRatio = ratioToScore(((rectLong/rectShort)/idealAspectRatio));
                } else {
                        //particle is taller than it is wide, divide short by long
                        aspectRatio = ratioToScore(((rectShort/rectLong)/idealAspectRatio));
                }
                return aspectRatio;             //force to be in range 0-100
        }
        
        /**
         * Compares scores to defined limits and returns true if the particle appears to be a target
         * 
         * @param scores The structure containing the scores to compare
         * @param vertical True if the particle should be treated as a vertical target, false to treat it as a horizontal target
         * 
         * @return True if the particle meets all limits, false otherwise
         */
        bool scoreCompare(Scores scores, bool vertical){
                bool isTarget = true;

                isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
                if(vertical){
                        isTarget &= scores.aspectRatioVertical > ASPECT_RATIO_LIMIT;
                } else {
                        isTarget &= scores.aspectRatioHorizontal > ASPECT_RATIO_LIMIT;
                }

                return isTarget;
        }
        
        /**
         * Computes a score (0-100) estimating how rectangular the particle is by comparing the area of the particle
         * to the area of the bounding box surrounding it. A perfect rectangle would cover the entire bounding box.
         * 
         * @param report The Particle Analysis Report for the particle to score
         * @return The rectangularity score (0-100)
         */
        double scoreRectangularity(ParticleAnalysisReport *report){
                if(report->boundingRect.width*report->boundingRect.height !=0){
                        return 100*report->particleArea/(report->boundingRect.width*report->boundingRect.height);
                } else {
                        return 0;
                }       
        }       
        
        /**
         * Converts a ratio with ideal value of 1 to a score. The resulting function is piecewise
         * linear going from (0,0) to (1,100) to (2,0) and is 0 for all inputs outside the range 0-2
         */
        double ratioToScore(double ratio)
        {
                return (max(0, min(100*(1-fabs(1-ratio)), 100)));
        }
        
        /**
         * Takes in a report on a target and compares the scores to the defined score limits to evaluate
         * if the target is a hot target or not.
         * 
         * Returns True if the target is hot. False if it is not.
         */
        bool hotOrNot(TargetReport target)
        {
                bool isHot = true;
                
                isHot &= target.tapeWidthScore >= TAPE_WIDTH_LIMIT;
                isHot &= target.verticalScore >= VERTICAL_SCORE_LIMIT;
                isHot &= (target.leftScore > LR_SCORE_LIMIT) | (target.rightScore > LR_SCORE_LIMIT);
                
                return isHot;
        }	
/******************************************************************************************************************
 * END VISION TRACKING FUNCTIONS
 ******************************************************************************************************************/
	
	
	
	
	
	
	
	
	
	
	
};

START_ROBOT_CLASS(RobotDemo);

