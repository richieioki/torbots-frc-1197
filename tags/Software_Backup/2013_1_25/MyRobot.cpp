#include "WPILib.h"
#include "Vision/AxisCamera.h"
#include "Vision/BinaryImage.h"
#include "Vision/HSLImage.h"
#include "Vision/RGBImage.h"
#include "Math.h"
#include "Gyro.h"
#include "DrivePidOutput.h"
#include "Consts.h"
#include "TorbotDrive.h"
#include "TorJagDrive.h"
/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 * 
 */
#define AREA_MINIMUM 500
#define CAMERA_FOV 23.5

#define RECTANGULARITY_LIMIT 60
#define ASPECT_RATIO_LIMIT 75
#define X_EDGE_LIMIT 40
#define Y_EDGE_LIMIT 60
#define GEAR_RATIO 12.0/26.0

//Edge profile constants used for hollowness score calculation
#define XMAXSIZE 24
#define XMINSIZE 24
#define YMAXSIZE 24
#define YMINSIZE 48
const double xMax[XMAXSIZE] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
const double xMin[XMINSIZE] = {.4, .6, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, .1, 0.6, 0};
const double yMax[YMAXSIZE] = {1, 1, 1, 1, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, .5, 1, 1, 1, 1};
const double yMin[YMINSIZE] = {.4, .6, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
                                                                .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05, .05,
                                                                .05, .05, .6, 0};
const float pidTolerance = 0.05;
const float encoderToDist = 23.15 / 360;


AxisCamera &camera = AxisCamera::GetInstance();


class RobotDemo : public SimpleRobot
{
	//RobotDrive myRobot; // robot drive system
	Joystick stick; // only joystick
	Compressor *compressor;
	DriverStationLCD *ds;
	Jaguar *leftDriveJag;
	Jaguar *leftDriveJag2;
	Jaguar *rightDriveJag;
	Jaguar *rightDriveJag2;
	Gyro *gyro;
	Solenoid *shifter;  //Super Shifter
	PIDController *leftPID1;
	PIDController *leftPID2;
	PIDController *rightPID1;
	PIDController *rightPID2;
	PIDController *drivePID;
	PIDController *turnPID;
	
	Encoder *wheelEncoder;
	struct Scores {
	                double rectangularity;
	                double aspectRatioInner;
	                double aspectRatioOuter;
	                double xEdge;
	                double yEdge;

	        };
	Scores *scores; //scores vision targets
	

public:
	RobotDemo(void):
		//myRobot(10, 9),	// these must be initialized in the same order
		stick(1)		// as they are declared above.
	{
	        
		//myRobot.SetExpiration(0.1);
		camera.WriteResolution(AxisCamera::kResolution_320x240);
		camera.WriteBrightness(50);
		camera.WriteCompression(20);
		                             
		//TODO get rid of magic numbers
		leftDriveJag = new Jaguar(10);
		leftDriveJag2 = new Jaguar(9);
		rightDriveJag = new Jaguar(8);
		rightDriveJag2 = new Jaguar(7);
		
		TorJagDrive* torjag_drive = new TorJagDrive(*leftDriveJag, *leftDriveJag2, *rightDriveJag, *rightDriveJag2);
		DrivePIDOutput* drivingPIDOut = new DrivePIDOutput(*torjag_drive, DriveMode::Driving);  // PID controller for driving straight
		DrivePIDOutput* rotatingPIDOut = new DrivePIDOutput(*torjag_drive, DriveMode::Rotating); // PID controller for turning
			
		SmartDashboard::init();       
		                
		//compressor = new Compressor(1,6);
		//shifter = new Solenoid(3);
		ds = DriverStationLCD::GetInstance();
		gyro = new Gyro(1,1);
		wheelEncoder = new Encoder(3,4,false, Encoder::k1X);
		
//		const float DFLT_PROP_GAIN  = 0.09f; //get rid of magic numbers

//		leftPID1 = new PIDController(Consts::DFLT_PROP_GAIN,0.0,0.0, gyro, leftDriveJag, pidTolerance);
////		leftPID2 = new PIDController(0.09,0.0,0.0, gyro, leftDriveJag2, pidTolerance);
//		rightPID1 = new PIDController(0.09,0.0,0.0, gyro, rightDriveJag, pidTolerance);
//		rightPID2 = new PIDController(0.09,0.0,0.0, gyro, rightDriveJag2, pidTolerance);

		drivePID = new PIDController(Consts::DFLT_PROP_GAIN,0.0,0.0, gyro, drivingPIDOut, pidTolerance);
//		turnPID = new PIDController(Consts::DFLT_PROP_GAIN,0.0,0.0, gyro, rotatingPIDOut, pidTolerance);
		turnPID = new PIDController(0.04,0.0,0.0, gyro, rotatingPIDOut, 0.0005);

                
//		leftPID1->SetContinuous(false);
////		leftPID2->SetContinuous(false);
//		rightPID1->SetContinuous(false);
//		rightPID2->SetContinuous(false);
//		
//		leftPID1->SetInputRange(-CAMERA_FOV, CAMERA_FOV);
////		leftPID2->SetInputRange(-CAMERA_FOV, CAMERA_FOV);
//		rightPID1->SetInputRange(-CAMERA_FOV, CAMERA_FOV);
//		rightPID2->SetInputRange(-CAMERA_FOV, CAMERA_FOV);
////		
//		leftPID1->SetOutputRange(-0.25,0.25);
////		leftPID2->SetOutputRange(-0.25,0.25);
//		rightPID1->SetOutputRange(-0.25,0.25);
//		rightPID2->SetOutputRange(-0.25,0.25);
////		
//		leftPID1->SetPercentTolerance(5.0);
////		leftPID2->SetPercentTolerance(5.0);
//		rightPID1->SetPercentTolerance(5.0);
//		rightPID2->SetPercentTolerance(5.0);
//		
		wheelEncoder->SetDistancePerPulse((8.0*3.141516)/360);		
		
		gyro->Reset();
	}
	void VisionTrackingMovement(int x)
	        {
	          //based on x value, turn the robot
	  
	  /**
	   * 2011 BOT JAG WIRING
	   * BACKWARDS = TOWER SIDE FRONT = NON TOWER
	   * 
	   * LEFT JAGS > 0 = BACKWARDS MOVEMENT 
	   * LEFT JAGS < 0 = FORWARD MOVEMENT 
	   * 
	   * RIGHT JAGS > 0 = FORWARD MOVEMENT
	   * RIGHT JAGS < 0 = BACKWARD MOVEMENT
	   * 
	   * RIGHT OF THE TARGET IS SMALLER
	   * LEFT OF THE TARGET IS LARGER
	   */
	  
	          if (x < 150)
	            {
	              rightDriveJag->Set(0.25);
	              rightDriveJag2->Set(0.25);
	              leftDriveJag->Set(0.25);
	              leftDriveJag2->Set(0.25);
	            }
	          else if (x > 170)
	            {
	              rightDriveJag->Set(-0.25);
	              leftDriveJag->Set(-0.25);
	              rightDriveJag2->Set(-0.25);
                      leftDriveJag2->Set(-0.25);
	              
	            }
	          else
	            {
	              rightDriveJag->Set(0.0);
	              leftDriveJag->Set(0.0);
	              rightDriveJag2->Set(0.0);
	              leftDriveJag2->Set(0.0);
	            }
	        }
	/**
	 * Compares scores to defined limits and returns true if the particle appears to be a target
	 * 
	 * @param scores The structure containing the scores to compare
	 * @param outer True if the particle should be treated as an outer target, false to treat it as a center target
	 * 
	 * @return True if the particle meets all limits, false otherwise
	 */
	bool scoreCompare(Scores scores, bool outer){
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
	 * Computes a score based on the match between a template profile and the particle profile in the X direction. This method uses the
	 * the column averages and the profile defined at the top of the sample to look for the solid vertical edges with
	 * a hollow center.
	 * 
	 * @param image The image to use, should be the image before the convex hull is performed
	 * @param report The Particle Analysis Report for the particle
	 * 
	 * @return The X Edge Score (0-100)
	 */
	double scoreXEdge(BinaryImage *image, ParticleAnalysisReport *report){
	        double total = 0;
	        LinearAverages *averages = imaqLinearAverages2(image->GetImaqImage(), IMAQ_COLUMN_AVERAGES, report->boundingRect);
	        for(int i=0; i < (averages->columnCount); i++){
	                if(xMin[i*(XMINSIZE-1)/averages->columnCount] < averages->columnAverages[i] 
	                   && averages->columnAverages[i] < xMax[i*(XMAXSIZE-1)/averages->columnCount]){
	                        total++;
	                }
	        }
	        total = 100*total/(averages->columnCount);              //convert to score 0-100
	        imaqDispose(averages);                                                  //let IMAQ dispose of the averages struct
	        return total;
	}
	/**
	 * Computes a score based on the match between a template profile and the particle profile in the Y direction. This method uses the
	 * the row averages and the profile defined at the top of the sample to look for the solid horizontal edges with
	 * a hollow center
	 * 
	 * @param image The image to use, should be the image before the convex hull is performed
	 * @param report The Particle Analysis Report for the particle
	 * 
	 * @return The Y Edge score (0-100)
	 */
	double scoreYEdge(BinaryImage *image, ParticleAnalysisReport *report){
	        double total = 0;
	        LinearAverages *averages = imaqLinearAverages2(image->GetImaqImage(), IMAQ_ROW_AVERAGES, report->boundingRect);
	        for(int i=0; i < (averages->rowCount); i++){
	                if(yMin[i*(YMINSIZE-1)/averages->rowCount] < averages->rowAverages[i] 
	                   && averages->rowAverages[i] < yMax[i*(YMAXSIZE-1)/averages->rowCount]){
	                        total++;
	                }
	        }
	        total = 100*total/(averages->rowCount);         //convert to score 0-100
	        imaqDispose(averages);                                          //let IMAQ dispose of the averages struct
	        return total;
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
	double scoreAspectRatio(BinaryImage *image, ParticleAnalysisReport *report, bool outer){
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
	
	
	
	void driveToTheta(float theta, float motorSpeed, float distanceInches)
	{
	  float angleError = 0.0;
	  float driveError = 5.0;       // expect angle of error no more than 5 degrees either way

	  gyro->Reset();
	  angleError = gyro->GetAngle()+theta;          // add current ange to desired angle. resetting may not set to 0.0 exactly
	  drivePID->SetSetpoint(angleError);

	  //while (average distance traveled < distanceInches)
	  // {

	  drivePID->SetInputRange(-driveError, driveError);
	  drivePID->SetOutputRange(motorSpeed, motorSpeed+0.25);                // minimum speed will be motorSpeed; max adjusted speed is + 0.25
	  drivePID->SetPercentTolerance(5.0);                                   // tolerance is 5% of (driveError*2); not really used yet though
	  drivePID->Enable();
          ds->Printf(DriverStationLCD::kUser_Line1, 1, "gyro: %2.2f AE: %4.2f", gyro->GetAngle(), angleError);
          ds->UpdateLCD();
	  
	  Wait(10.0); // just testing. no code to drive a distance yet


	  //   }
	// Stop motors
	  drivePID->SetOutputRange(0.0,0.0);                                    // stops motors regardless of PID input/Output
	 
	} // end driveToTheta

	
	// to become TurnToTheta method in TorbotDrive class
	void gyroTrackingMovement(double normX) //normalized X vals from camera
	{

	  double targetAngle = normX * CAMERA_FOV; 
	  double motorSpeed = 0.35;
	  double gyroPIDTolerance = 1.0;        //  get within 1 degrees

          //gyro->Reset();
          targetAngle += gyro->GetAngle();      // add the current angle to the target angle to adjust for any gyro error
          
	  //Wait(2.0);
	  ds->Printf(DriverStationLCD::kUser_Line1, 1, "st g:%4.2f tgt:%4.2f", gyro->GetAngle(), targetAngle);
	  ds->UpdateLCD();

	  turnPID->SetSetpoint(targetAngle);	  
	  turnPID->SetContinuous(false);
	  turnPID->SetInputRange(-(fabs(targetAngle)), fabs(targetAngle));
	  turnPID->SetOutputRange(-motorSpeed, motorSpeed);
	  //turnPID->SetAbsoluteTolerance(gyroPIDTolerance);
          turnPID->SetPercentTolerance(5.0);
          
	  turnPID->Enable();
	  
	  while (fabs(gyro->GetAngle()) < fabs(targetAngle))
	    {
	      ds->Printf(DriverStationLCD::kUser_Line2, 1, "gyro: %4.2f", gyro->GetAngle());
	      ds->UpdateLCD();
	      Wait(0.01);
	    }
	  //leftPID1->Reset();
	  //          if (!leftPID1->IsEnabled())
	  //            ds->Printf(DriverStationLCD::kUser_Line3, 1, "disabled");
	  //                    
	  //          else
	  //            ds->Printf(DriverStationLCD::kUser_Line3, 1, "enabled");

	}
	
	
	/**
	 * Drive left & right motors for 2 seconds then stop
	 */
	void Autonomous(void)
	{
		//myRobot.SetSafetyEnabled(false);
		//myRobot.Drive(-0.5, 0.0); 	// drive forwards half speed
//	  ds->Clear();
//	  gyroTrackingMovement(0.5);
//	  ds->Printf(DriverStationLCD::kUser_Line4, 1, "TURN 1");
//	  ds->UpdateLCD();
//	  Wait(2.0); 
//	  gyroTrackingMovement(-0.5);
//	  ds->Printf(DriverStationLCD::kUser_Line5, 1, "TURN 2");
//	  ds->UpdateLCD();

	  leftPID1->Enable();
	  //          leftPID2->Enable();
	  rightPID1->Enable();
	  rightPID2->Enable();
	  ds->Clear();
	  driveToTheta(0.0, 0.25, 120.0); // drive straight, 25% motor speed, 120 inches

	  leftPID1->Disable();
	  rightPID1->Disable();
	  rightPID2->Disable();
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl(void)
	{
	        
		bool trackButton = false;
		bool triggerButton = false;
		bool targetOuter = false;
		int cameraCounter = 0;
		ParticleFilterCriteria2 criteria[] = {
		                   {IMAQ_MT_AREA, AREA_MINIMUM, 65535, false, false}};
		//shifter->Set(false);
		gyro->Reset();
		ds->Clear();
		wheelEncoder->Reset();
		wheelEncoder->Start();				
        
		while (IsOperatorControl())
		{
		    ds->Printf(DriverStationLCD::kUser_Line1, 1, "operator: %d", IsOperatorControl());
		    ds->UpdateLCD();
		    SmartDashboard::PutBoolean("OperatorControl", IsOperatorControl());
		    SmartDashboard::PutData("gyro", gyro);
		    SmartDashboard::PutData("left Jag", leftDriveJag);
		    SmartDashboard::PutData("turnPID", turnPID);
//
//		    //myRobot.ArcadeDrive(stick); // drive with arcade style (use right stick)
		    trackButton = stick.GetRawButton(3);
		    triggerButton = stick.GetRawButton(1);
//
//		
//		    if (fabs(leftPID1->GetError()) > (0.05*47.0))
//		        leftPID1->Disable();
//		        
//		    if (leftPID1->IsEnabled())
//		      ds->Printf(DriverStationLCD::kUser_Line3, 1, "left: %3.3f", leftPID1->GetError());


		    ds->Printf(DriverStationLCD::kUser_Line2, 1, "camCnt: %d", cameraCounter);
		    ds->UpdateLCD();                            
		    if ((trackButton||triggerButton) &&(cameraCounter == 0))
		      {
		        cameraCounter = 1;
		        ds->Printf(DriverStationLCD::kUser_Line2, 1, "IN camCnt: %d", cameraCounter);

		        // TorTargetAcquire : AcquireTargets
		        ColorImage *image;
		        //ColorImage *image;
		        //Threshold threshold(60, 100, 90, 255, 20, 255);
		        BinaryImage *initialFilterImage;
		        image = camera.GetImage();                          
		        initialFilterImage = image->ThresholdHSV(60, 150, 90, 255, 20, 255); //keeps green
		        //initialFilterImage->Write("/Initial.bmp");
		        BinaryImage *filledImage = initialFilterImage->ConvexHull(false); //fills in rect
		        //filledImage->Write("/ConvexHull.bmp");
		        BinaryImage *cleanImage = filledImage->ParticleFilter(criteria, 1); //clean-up
		        //cleanImage->Write("/Clean.bmp");
		        vector <ParticleAnalysisReport> *particles = 
		            cleanImage->GetOrderedParticleAnalysisReports(); //grab reports
		        //delete images
		        delete image;
		        delete filledImage;
		        delete cleanImage;
		        
		        // End AcquireTargets

		        
		        // GenerateTargets
		        if (!particles->empty())

		          {
		            ds->Printf(DriverStationLCD::kUser_Line3, 1, "found part: %d", particles->size());
		            scores = new Scores [particles->size()]; //houses scores for each particle
		                                    
		            // loop through all particles found trying to find the appropriate target
		            // trigger press = target outer/middle goal
		            // tracker press = target inner/high goal
		            for (unsigned int i = 0; i < particles->size(); i++)
		              {
		                ParticleAnalysisReport *report = &(particles->at(i)); //get report
		                scores[i].aspectRatioOuter = scoreAspectRatio(cleanImage, report, true);
		                scores[i].aspectRatioInner = scoreAspectRatio(cleanImage, report, false);
		                scores[i].xEdge = scoreXEdge(initialFilterImage, report);
		                scores[i].yEdge = scoreYEdge(initialFilterImage, report);
		                scores[i].rectangularity = scoreRectangularity(report);

		                
		      // End GenerateTargets
		                
		                
		                if (triggerButton)
		                  {
		                    targetOuter = true;
		                  }
		                else if (trackButton)
		                  {
		                    targetOuter = false;
		                  }



		                if (scoreCompare(scores[i], targetOuter))
		                  {
		                    ds->Clear();
		                    ds->Printf(DriverStationLCD::kUser_Line1, 1, "pnum: %d x: %d", i, report->center_mass_x);
		                    ds->Printf(DriverStationLCD::kUser_Line2,1, "CntrxN: %f", report->center_mass_x_normalized);
		                    //ds->Printf(DriverStationLCD::kUser_Line3, 1, "pnum: %d Tgt: %d", i, targetOuter);			                  
		                    ds->Printf(DriverStationLCD::kUser_Line4, 1, "t: %3.2f f: %3.2f",  report->center_mass_x_normalized*CAMERA_FOV, gyro->GetAngle());
		                    ds->Printf(DriverStationLCD::kUser_Line5,1, "O: %1.3f I: %1.3f", scores[i].aspectRatioOuter, scores[i].aspectRatioInner);
		                    ds->Printf(DriverStationLCD::kUser_Line6,1,"rect: %3.2f", scores[i].rectangularity);

		                    gyroTrackingMovement(report->center_mass_x_normalized);
		                  }
		                 else
		                  {
		                  //ds->Printf(DriverStationLCD::kUser_Line2, 1, "O: %1.3f I: %1.3f", scores[i].aspectRatioOuter, scores[i].aspectRatioInner);
		                     ds->Printf(DriverStationLCD::kUser_Line1,1, "Particle failed score test.");
		                  }
		              }//for loop bracket
		          } //particles empty
		        else //no particles detected
		          {          
		            ds->Clear();
		            ds->Printf(DriverStationLCD::kUser_Line6, 1, "Not detected");
		          }


		      } // if track or trigger
		    else // no button pressed, clear counter
		      {
		        cameraCounter = 0;
		        //ds->Printf(DriverStationLCD::kUser_Line4, 1, "camCnt: %d", cameraCounter);
		      }
		    ds->UpdateLCD();
		} //while IsOperatorControl bracket
	} //teleop bracket
	
	
	
	
	/**
	 * Runs during test mode
	 */
	void Test() {

	  TorJagDrive* testjagdrive = new TorJagDrive(*leftDriveJag, *leftDriveJag2, *rightDriveJag, *rightDriveJag2);
	                  
	 
          SmartDashboard::PutData("turnPID", turnPID);
          SmartDashboard::PutData("gyro", gyro);
          
          gyro->Reset();
          //Wait(2.0);
          
	  ds->Clear();
	  testjagdrive->SetDrive(0.25, 0.25);
	 Wait(5.0);
	 testjagdrive->SetDrive(0.0, 0.0);
	 Wait(0.5);
	 testjagdrive->SetDrive(-0.25, -0.25);
	  
	
	  //torjag_drive->SetRight(0.25);
	  
	  //driveToTheta(gyro->GetAngle(), 0.25, 120);
	  
	  //	  gyroTrackingMovement(1.0);
//	  ds->Printf(DriverStationLCD::kUser_Line4, 1, "wait 5, end g: %f4.2", gyro->GetAngle());
//	  ds->UpdateLCD();
//	  Wait(2.0); 
//	  gyroTrackingMovement(-1.0);
//	  ds->Printf(DriverStationLCD::kUser_Line5, 1, "TURN 2");
	  Wait(5.0);
	  //turnPID->Disable();
	  
          //drivePID->Disable();
	 // Wait(2.0);
         testjagdrive->SetDrive(0.0, 0.0);

         Wait(0.5);
         
         rightDriveJag->Set(-0.25);
         leftDriveJag->Set(0.25);
         rightDriveJag2->Set(-0.25);
         leftDriveJag2->Set(0.25);
                               
          while (true)
          {
            
          
          ds->Printf(DriverStationLCD::kUser_Line5, 1, "gyro: %f4.2", gyro->GetAngle());
	  ds->UpdateLCD();
          }
	}
};


START_ROBOT_CLASS(RobotDemo);

