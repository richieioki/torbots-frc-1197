#include "WPILib.h"
#include <Joystick.h>
#include <RobotDrive.h>
#include <Jaguar.h>
#include <Relay.h>
#include <Solenoid.h>
#include <Compressor.h>
#include <Encoder.h>
#include <Vision2009/VisionAPI.h>
#include <Vision2009/BaeUtilities.h>
#include <Vision/AxisCamera.h>
#include <PIDController.h>
#include <AnalogChannel.h>
#include <DigitalInput.h>
#include <SmartDashboard.h>
#include <DriverStation.h>
#include <math.h>
#include <vector>
#include <Gyro.h>
#include <Timer.h>

//defines are for states of the switch case functions during autonomous
//multiple autonomouses are set up, you select one by setting AUTOState below

/*raise arm to bottom peg
 *back up
 *and stop
 */
#define simpleAUTOBegin 1
#define simpleAUTORaiseArm 2
#define simpleAUTODrive 3
#define simpleAUTOStop 4
  float simpleAUTOMoveSpeed = 0.6; //speed at which the bot moves during auto
  float simpleAUTOEncoderDistance = -30.0; //(inches)

/*drive backwards (towards peg) "blindly"
 *use camera to track peg
 *turn around so front of robot is facing peg
 *place tube using general autonomous states
 */
#define colorAUTOBegin 11
#define colorAUTOEncoder 12
#define colorAUTODrive 13
#define colorAUTOTurn 14
#define colorAUTOCheckTurn 15
#define colorAUTOColorRed 21 //color filter options
#define colorAUTOColorYellow 22
#define colorAUTOColorBlue 23
#define colorAUTOColorRedCameraGreen 24
  	int colorAUTOColor = colorAUTOColorRedCameraGreen; //tells colorAUTO what color to filter
  float colorAUTOMoveSpeed = 0.6; //speed at which the bot moves during auto
  //float colorAUTOTurnSpeed = 1.0;
  float colorAUTOEncoderDistance = -32.0;//-174.0; //(inches) how far the robot goes before using the camera
    int colorAUTOCloseParticleWidth = 15; //the width a particle must be on the camera
									    //for the bot to think it's close enough
/*drive forward holding heading with gyro
 *stop when within proximity of peg using sharp sensor
 *place tuve using general autonomous states
 */
#define gyroAUTOBegin 41
#define gyroAUTODrive 42
#define gyroAUTONumOfVoltages 8 //# of voltages to average in, stabilizes proximity voltage
  float gyroAUTOTargetVoltage = 0.06; //stop when proximity sensor sees this voltage
  float gyroAUTOEncoderDistance = 80; //(inches) how far the bot moves forward
  float gyroAUTOMoveSpeed = 1.0; //forward moveSpeed
    
/*general autonomous states:
 *raise arm to top peg
 *approach peg so tube is on peg
 *drop arm slightly and release tube
 *back up, lower arm to ground, and turn around
 *stop
 */
#define AUTORaiseArm 31
#define AUTOCheckArmRaise 32
#define AUTOApproachPeg 33
#define AUTORelease 34
#define AUTOCheckRelease 35
#define AUTOBackUp 36
#define AUTOLowerArm 37
#define AUTOTurn 38
#define AUTOShutdown 39
#define AUTOWait 40
  float AUTOWaitTime = 1.0; //time (seconds) that the AUTOWait state waits for
  	int AUTOWaitNextState = AUTOShutdown; //state to go to after waiting
  float AUTOApproachSpeed = 0.6; //speed at which the bot aproaches the peg
  float AUTOApproachDistance = 36.0; //(inches) how far the bot moves forward to get tube on peg
  float AUTOBackUpSpeed = -0.6; //speed at which the bot backs away from the peg
  float AUTOBackUpDistance = -48.0; //(inches) how far the bot backs up after placing the tube
  float AUTOTurnSpeed = 0.8;
  
	int AUTOState = gyroAUTOBegin; //this controls which autonomous sequence runs, use begin state ie ____AUTOBegin
	int beginState = AUTOState; //which autonomous is running (never changes)
	
//exact angles which each variables are set to
int maxPotAngle = 360;
int armDeadzone = 2; //most the pot's gonna hop/jump (degrees)
int minArmAngle = 28; //farthest the arm should go (degrees)
int maxArmAngle = 270; //farthest the arm should go (degrees)
int bottomPegAngle = 35 + minArmAngle; //# of degrees for the pegs, relative from minArmAngle
int middlePegAngle = 67 + minArmAngle;
int topPegAngle = 125 + minArmAngle;
int deltaPotVal = 1; //angle arm turns w/ manual pid
float pidTolerance = 1.5; //tolerance for armPid's OnTarget()
float maxArmSpeed = 0.65;
float wheelCircumference = 8.0*3.1416; //(inches)
float wheelGearRatio = 12.0/26.0;
int encoderTicks = 360; //tick count on the encoder

/*
float autoMoveSpeed = 0.6; //speed at which the bot moves during auto
float autoTurnSpeed = 0.75; //speed at which the bot turns during auto
float numLineSensorX = 0.3; //x value that the sensors are at on autonomous number line
float numLineWheelX = 0.5; //x value that the wheels are at on autonomous number line
*/

//Create the axis camera here, but set settings below
AxisCamera &camera = AxisCamera::GetInstance();

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 
class RobotDemo : public SimpleRobot
{
	RobotDrive *myRobot; // robot drive system
	Joystick *stick1; // joystick
	Joystick *stick2; // second joystick
	Jaguar *armMotor1; // arm moves. did you know?
	Jaguar *armMotor2; // arm moves. did you know?
	Solenoid *relayPistonIn; //pneumatic piston
	Solenoid *relayPistonOut; //pneumatic piston??? o.O
	Compressor *compressor; //compressor
	AnalogChannel *pot; //potentiometer
	DigitalInput *sensorL, *sensorM, *sensorR; //the three line sensors
	DigitalInput *shoulderswitch; //limit switch
	PIDController *pid1; //Proportional Integral Derivative
	PIDController *pid2; //Proportional Integral Derivative
	Solenoid *armDeploymentPiston; //self explanatory
	Solenoid *shifterPiston; // valve to control 2 pistons to shift gear on two-speed
	Solenoid *minibotPiston; // valve to actuate minibot deployment mechanism
	Encoder *wheelEncoder;
	DriverStation *dsIO; //Driver Station Cypress Controller
	Gyro *gyro; //gyroscope
	Timer *timer; //used for "waiting" in autonomous
	Relay *light; //flashlight for autonomous
	Jaguar *jagL1; //1st left jaguar
	Jaguar *jagL2; //2nd left jaguar
	Jaguar *jagR1; //1st right jaguar
	Jaguar *jagR2; //2st right jaguar
	Jaguar *fakeTurnJag; //nonexistent jag to use in autonomous turn pid
	PIDController *turnPid; //pid for turning around in autonomous
	AnalogChannel *irSensor; //proximity sensor for autonomous
	Timer *timer2; //timer for minibot deployment

public:
	RobotDemo(void)
	{
		jagL1 = new Jaguar(4,1);					// PWM1
		jagL2 = new Jaguar(4,2);					// PWM2
		jagR1 = new Jaguar(4,3);					// PWM3
		jagR2 = new Jaguar(4,5);					// PWM5
		fakeTurnJag = new Jaguar(4,10);				// PWM10
		myRobot = new RobotDrive(jagL1, jagL2, jagR1, jagR2);		//4x motors for compeititon robot (PWM1&2 left, PWM3&5 right) 
		//myRobot = new RobotDrive (jagL1,jagR1);				//2x motors for test robot (PWM1 left, PWM3 right)
		stick1 = new Joystick(1);					
		stick2 = new Joystick(2);
		armMotor1 = new Jaguar(4,6);				// PWM6
		armMotor2 = new Jaguar(4,7);				// PWM7
		//armEncoder = new Encoder(4,2,4,3);
		compressor = new Compressor(4,1,4,1);		// Pressure DIO 1, Spike Relay 1
		armDeploymentPiston = new Solenoid(7,2);	// CRIO Solenoid 2
		relayPistonIn = new Solenoid(7,1);			// CRIO Solenoid 1
		relayPistonOut = new Solenoid(7,6);			// CRIO Solenoid 6
		shifterPiston = new Solenoid(7,3);			// CRIO Solenoid 3
		minibotPiston = new Solenoid(7,4);			// CRIO Solenoid 4
		pot = new AnalogChannel(1,2);				// Analog In 2
		sensorL = new DigitalInput(4,6);			// DIO 6
		sensorM = new DigitalInput(4,4);			// DIO 4
		sensorR = new DigitalInput(4,5);			// DIO 5
		gyro = new Gyro(1,1);						// Analog In 1. ONLY WORKS IN THIS SPOT
		//TODO Tune PID for smooth accelerations
		float PIDTimeInterval = 0.0005;
		pid1 = new PIDController(0.012,0.0,0.0,pot,armMotor1,PIDTimeInterval); //change to match with the pot
		pid2 = new PIDController(pid1->GetP(),pid1->GetI(),pid1->GetD(),pot,armMotor2,PIDTimeInterval); //change to match with the pot
		wheelEncoder = new Encoder(4,9,4,10);		// Ch A DIO 9, Ch B DIO 10
		shoulderswitch = new DigitalInput(4,3);		// DIO 3
		timer = new Timer(); // Timer for "waiting" in autonomous
		timer2 = new Timer(); //timer for minibot deployment
		light = new Relay(4,2,Relay::kForwardOnly);	// Spike Relay 2
		//turnPid = new PIDController(0.023,0.000003,0.0,gyro,fakeTurnJag,PIDTimeInterval); //change to match with the pot
		turnPid = new PIDController(0.03,0.0,0.0,gyro,fakeTurnJag,PIDTimeInterval);
		irSensor = new AnalogChannel(1,3);			// Analog In 3
		dsIO = DriverStation::GetInstance();
		
		//myRobot.SetExpiration(0.1);
		
		camera.WriteResolution(AxisCamera::kResolution_320x240);
		camera.WriteBrightness(0);
		camera.WriteCompression(20);
	}

	/**
	 * 
	 *  Disabled Mode
	 * 
	 * things to do while the robot can't move or have any outputs to motors/spikes
	 * 
	 */
	void Disabled (void)
	{
		AUTOState = beginState;
		pid1->SetInputRange(0,1000);
		pid1->SetOutputRange(-1*maxArmSpeed, maxArmSpeed);
		pid1->SetTolerance(pidTolerance);
		pid1->Disable(); //start off with the pid disabled and enable during case AUTORaiseArm
		pid2->SetInputRange(0,1000);
		pid2->SetOutputRange(-1*maxArmSpeed, maxArmSpeed);
		pid2->SetTolerance(pidTolerance);
		pid2->Disable(); //start off with the pid disabled and enable during case AUTORaiseArm
		turnPid->SetInputRange(-360,360);
		turnPid->SetOutputRange(-1*AUTOTurnSpeed, AUTOTurnSpeed);
		turnPid->SetTolerance(1.5);
		turnPid->SetContinuous(false);
		turnPid->Disable();
		gyro->Reset(); //Resets the gyro angle before starting
	}
	
	
	/**
	 *Game starts off with ubertube on the bot.
	 *
	 */
	void Autonomous(void)
	{
		int sensorLval = 0; //sensor values
		int sensorMval = 0;
		int sensorRval = 0;
		int irSensorVoltageIndex = 0;
		//int lastSensorVal = -2;
		float armAngle = 0;
		//the different autonomouses either use TankDrive or ArcadeDrive...
		float leftWheelSpeed = 0;
		float rightWheelSpeed = 0;
		//...not both
		float moveSpeed = 0;
		float turnSpeed = 0;
		float sensorNumLine = 0;
		float encoderDistanceData = 0;
		float gyroAngle = 0;
		float cumulativeGyroAngle = 0;
		float turnAdjustment = 0;
		float avgIrSensorVoltage = 0;
		//sets up array of ir sensor voltages to average to stabilize voltage
		float irSensorVoltages[gyroAUTONumOfVoltages];
		for (int i = 0; i < gyroAUTONumOfVoltages; i++)
		{
			irSensorVoltages[i] = 0;
		}
		//float armSpeed = 0;
		bool armDeployed = false;

		compressor->Start();
		wheelEncoder->Start();
		relayPistonIn->Set(false);
		relayPistonOut->Set(true);
		armDeploymentPiston->Set(false);
		minibotPiston->Set(false);
		shifterPiston->Set(false); //Sets default gear to high gear (drive)
		
		while (IsAutonomous()) //Storyship Entorprise
		{
			armAngle = pot->GetValue()*maxPotAngle/1000;
			sensorLval = sensorL->Get();
			sensorMval = sensorM->Get();
			sensorRval = sensorR->Get();
			encoderDistanceData = -1*wheelEncoder->GetRaw()*wheelCircumference*wheelGearRatio/(encoderTicks*4);
			cumulativeGyroAngle += (gyro->GetAngle()*1.5)-gyroAngle;
			gyroAngle = gyro->GetAngle();
			SmartDashboard::Log(AUTOState, "Autonomous State");
			SmartDashboard::Log(avgIrSensorVoltage, "proximity");
			/* autonomous states. 
			 * it is in the order in which we will operate during automous. 
			 * we fetch the bot its tube in the very beginning. */
			switch (AUTOState)
			{	
				//****************************************************************
				//BEGIN SIMPLE AUTO
				//****************************************************************
				case simpleAUTOBegin:
					AUTOState = simpleAUTORaiseArm;
					break;
				case simpleAUTORaiseArm:
					/* raise arm to the bottom peg
					 * arm will then be deployed
					 * and lowered to minArmAngle
					 */
					moveSpeed = 0.0;
					pid1->Enable();
					pid2->Enable();
					pid1->SetSetpoint(bottomPegAngle*1000/maxPotAngle);
					pid2->SetSetpoint(bottomPegAngle*1000/maxPotAngle);
					wheelEncoder->Reset();
					AUTOState = simpleAUTODrive;
					break;
				case simpleAUTODrive:
					/* back up 2.5 ft slowly
					 * using encoders
					 * then go to stop state
					 */
					if (encoderDistanceData > simpleAUTOEncoderDistance)
					{
						moveSpeed = -1 * simpleAUTOMoveSpeed;
					}
					else
					{
						moveSpeed = 0.0;
						AUTOState = AUTOShutdown;
					}
					break;
				//****************************************************************
				//END SIMPLE AUTO
				//****************************************************************
					

					
				//****************************************************************
				//BEGIN GYRO CORRECTION AUTO
				//****************************************************************
				case gyroAUTOBegin:
					gyro->Reset();
					AUTOState = gyroAUTODrive;
					break;
				case gyroAUTODrive:
					/*
					//get current voltage
					irSensorVoltages[irSensorVoltageIndex] = irSensor->GetVoltage();
					//average the last X number of voltages
					irSensorVoltageIndex = irSensorVoltageIndex > (gyroAUTONumOfVoltages-2) ? 0 : irSensorVoltageIndex+1;
					avgIrSensorVoltage = 0;
					for (int i = 0; i < gyroAUTONumOfVoltages; i++)
					{
						avgIrSensorVoltage += irSensorVoltages[i];
					}
					avgIrSensorVoltage /= gyroAUTONumOfVoltages;
					*/

					if (encoderDistanceData < gyroAUTOEncoderDistance)
					{
						moveSpeed = gyroAUTOMoveSpeed;
					}
					else
					{
						moveSpeed = 0.0;
						AUTOState = AUTORaiseArm;
					}
					
					//use gyro to hold heading
					if (fabs(gyroAngle) < 0.5)
						cumulativeGyroAngle = 0;
					turnSpeed = cumulativeGyroAngle*-0.005;
					SmartDashboard::Log(gyroAngle, "gyro");
					
					/*
					if (avgIrSensorVoltage >= gyroAUTOTargetVoltage)
					{
						moveSpeed = 0;
						turnSpeed = 0;
						AUTOState = AUTORaiseArm;
						break;
					}
					moveSpeed = 1-avgIrSensorVoltage/gyroAUTOTargetVoltage;
					if (moveSpeed > 0)
					{
						moveSpeed = moveSpeed*0.2+0.45;
					}
					else
					{
						moveSpeed = moveSpeed*0.2-0.45;
					}
					*/
					break;
				//****************************************************************
				//END GYRO CORRECTION AUTO
				//****************************************************************
					
					
					
				//****************************************************************
				//BEGIN REFLECTIVE COLOR TRACKING AUTO
				//****************************************************************
				case colorAUTOBegin:
					SmartDashboard::Log("colorAUTOBegin", "State");
					AUTOState = colorAUTOEncoder;
					minibotPiston->Set(true);
					wheelEncoder->Reset();
					break;
				case colorAUTOEncoder:
					SmartDashboard::Log("colorAUTOEncoder", "State");
					if (encoderDistanceData > colorAUTOEncoderDistance)
					{
						moveSpeed = -1*colorAUTOMoveSpeed;
					}
					else
					{
						moveSpeed = 0.0;
						AUTOState = colorAUTODrive;

						light->Set(Relay::kOn);
					}
					break;
				case colorAUTODrive:
					moveSpeed = 0.0;
					turnSpeed = 0.0;
					if (camera.IsFreshImage())
					{
						// get the camera image
						HSLImage *image = camera.GetImage();
						BinaryImage *binaryImage;
						switch (colorAUTOColor)
						{
							case colorAUTOColorRed:
								SmartDashboard::Log("colorAUTOColorRed", "State");
								//filter out green and blue, keep red
								binaryImage = image->ThresholdRGB(121,255,0,116,0,166);
								break;
							case colorAUTOColorBlue:
								SmartDashboard::Log("colorAUTOColorBlue", "State");
								binaryImage = image->ThresholdRGB(0,126,173,255,233,255);
								break;
							case colorAUTOColorRedCameraGreen:
								SmartDashboard::Log("colorAUTOColorRedCameraGreen", "State");
								binaryImage = image->ThresholdRGB(88,255,0,127,0,127);
								break;
						}
	
						//get particles
						vector<ParticleAnalysisReport> *particles;
						particles = binaryImage->GetOrderedParticleAnalysisReports();
						
						delete image;
						delete binaryImage;
						
						if (!particles->empty()) //if a red particle was reflected
						{
							//use largest particle
							if (particles->at(0).boundingRect.width < colorAUTOCloseParticleWidth)// || particles->at(centeredParticle).particleArea < particles->at(centeredParticle).boundingRect.width*(particles->at(centeredParticle).boundingRect.height-2))
							{
								//too far
								//move closer
								moveSpeed = -1*pow(fabs(((float)particles->at(0).boundingRect.width-(float)colorAUTOCloseParticleWidth)/(float)colorAUTOCloseParticleWidth),0.1f)*0.6;
							}
							else if (particles->at(0).boundingRect.width > colorAUTOCloseParticleWidth)
							{
								//too close
								//move away
								moveSpeed = pow((((float)particles->at(0).boundingRect.width-(float)colorAUTOCloseParticleWidth)/(float)colorAUTOCloseParticleWidth),0.1f)*0.6;
							}
							else //if (particles->at(centeredParticle).boundingRect.width == colorAUTOCloseParticleWidth)
							{
								//perfect position
								//time to move on
								moveSpeed = 0.0f;
								turnSpeed = 0.0f;
								turnAdjustment = particles->at(0).center_mass_x_normalized*(float)(57/2);
								minibotPiston->Set(false);
								AUTOState = AUTOWait; //wait...
								AUTOWaitTime = 0.5; //...for 1 second...
								AUTOWaitNextState = colorAUTOTurn; //...before approaching the peg
								timer->Start(); //start timer
								timer->Reset(); //reset timer to 0
								AUTOState = colorAUTOTurn;
								break;
							}
							
							//moveSpeed = -1*pow(fabs(((float)particles->at(centeredParticle).boundingRect.width-(float)colorAUTOCloseParticleWidth)/(float)colorAUTOCloseParticleWidth), 0.333f);
							//moveSpeed *= (((float)particles->at(centeredParticle).boundingRect.width-(float)colorAUTOCloseParticleWidth)/(float)colorAUTOCloseParticleWidth) / fabs(((float)particles->at(centeredParticle).boundingRect.width-(float)colorAUTOCloseParticleWidth)/(float)colorAUTOCloseParticleWidth);
							turnSpeed = 2.5f*(particles->at(0).center_mass_x_normalized);
							if (moveSpeed < -1.0)
								moveSpeed = -1.0;
							
							if (turnSpeed > 1.0)
								turnSpeed = 1.0;
							else if (turnSpeed < -1.0)
								turnSpeed = -1.0;
							ShowActivity("speed: %f, turn: %f, width: %d", -1*moveSpeed, turnSpeed, particles->at(0).boundingRect.width);
						}
						else
						{
							ShowActivity("speed: %f, turn: %f", -1*moveSpeed, turnSpeed);
						}
					}
					break;
				case colorAUTOTurn:
					SmartDashboard::Log("colorAUTOTurn", "State");
					gyro->Reset();
					turnPid->Enable();
					turnPid->SetSetpoint(168+turnAdjustment/3);
					AUTOState = AUTOWait; //wait...
					AUTOWaitTime = 0.05; //...for 1 second...
					AUTOWaitNextState = colorAUTOCheckTurn;//colorAUTOTurn; //...before approaching the peg
					timer->Start(); //start timer
					timer->Reset(); //reset timer to 0
					break;
				case colorAUTOCheckTurn:
					SmartDashboard::Log("colorAUTOCheckTurn", "State");
					if (turnPid->OnTarget())
					{
						AUTOState = AUTOWait; //wait...
						AUTOWaitTime = 1.2; //...for 1 second...
						AUTOWaitNextState = AUTORaiseArm;//colorAUTOTurn; //...before approaching the peg
						timer->Start(); //start timer
						timer->Reset(); //reset timer to 0
					}
					break;
				//****************************************************************
				//END REFLECTIVE RED AUTO
				//****************************************************************
					
					
					
				//****************************************************************
				//BEGIN GENERIC AUTO
				//****************************************************************
				case AUTORaiseArm:
					SmartDashboard::Log("AUTORaiseArm", "State");
					turnPid->Disable();
					pid1->SetTolerance(pidTolerance + 1.0);
					pid2->SetTolerance(pidTolerance + 1.0);
					pid1->Enable();
					pid2->Enable();
					pid1->SetSetpoint(topPegAngle*1000/maxPotAngle);
					pid2->SetSetpoint(topPegAngle*1000/maxPotAngle);
					if (armAngle < minArmAngle)
					{
						armDeployed = false;
					}
					AUTOState = AUTOWait; //wait...
					AUTOWaitTime = 0.05; //...for 0.05 second...
					AUTOWaitNextState = AUTOCheckArmRaise; //...before raising the arm
					timer->Start(); //start timer
					timer->Reset(); //reset timer to 0
					break;
				case AUTOCheckArmRaise:
					SmartDashboard::Log("AUTOCheckArmRaise", "State");
					printf("raise: %f", armAngle);
					if (armAngle >= minArmAngle && !armDeployed)
					{
						pid1->Disable();
						pid2->Disable();
						armDeploymentPiston->Set(true); //deploys arm
						armDeployed = true;
						AUTOState = AUTOWait; //wait...
						AUTOWaitTime = 0.2; //...for 0.2 second...
						AUTOWaitNextState = AUTORaiseArm; //...before raising the arm the rest of the way
						timer->Start(); //start timer
						timer->Reset(); //reset timer to 0
					}
					if (pid1->OnTarget())
					{
						printf("ONTARGET, angle: %f", armAngle);
						pid1->Disable();
						pid2->Disable();
						pid1->SetTolerance(pidTolerance+0.2);
						pid2->SetTolerance(pidTolerance+0.2);
						wheelEncoder->Reset();
						//armDeploymentPiston->Set(true); //deploys arm
						AUTOState = AUTOWait; //wait...
						AUTOWaitTime = 0.2; //...for 0.2 second...
						AUTOWaitNextState = AUTOApproachPeg; //...before approaching the peg
						timer->Start(); //start timer
						timer->Reset(); //reset timer to 0
					}
					break;
				case AUTOApproachPeg:
					SmartDashboard::Log("AUTOApproachPeg", "State");
					if (encoderDistanceData < AUTOApproachDistance)
					{
						printf("aproaching %f\n", encoderDistanceData);
						moveSpeed = AUTOApproachSpeed;
					}
					else
					{
						printf("we're there");
						moveSpeed = 0.0;
						AUTOState = AUTOWait; //wait...
						AUTOWaitTime = 0.2; //...for 0.2 second...
						AUTOWaitNextState = AUTORelease; //...before approaching the peg
						timer->Start(); //start timer
						timer->Reset(); //reset timer to 0
					}
					break;
				case AUTORelease:
					SmartDashboard::Log("AUTORelease", "State");
					pid1->Enable();
					pid2->Enable();
					pid1->SetSetpoint(((0.4*(topPegAngle-middlePegAngle))+middlePegAngle)*1000/maxPotAngle);
					pid2->SetSetpoint(((0.4*(topPegAngle-middlePegAngle))+middlePegAngle)*1000/maxPotAngle);
					AUTOState = AUTOWait; //wait...
					AUTOWaitTime = 0.05; //...for 0.1 second...
					AUTOWaitNextState = AUTOCheckRelease; //...before approaching the peg
					timer->Start(); //start timer
					timer->Reset(); //reset timer to 0
					break;
				case AUTOCheckRelease:
					SmartDashboard::Log("AUTOCheckRelease", "State");
					printf("release: %f", armAngle);
					if (pid1->OnTarget())
					{
						relayPistonIn->Set(true); //open claw
						relayPistonOut->Set(false);
						pid1->Disable();
						pid2->Disable();
						AUTOState = AUTOBackUp;
					}
					break;
				case AUTOBackUp:
					SmartDashboard::Log("AUTOBackUp", "State");
					if (encoderDistanceData > AUTOBackUpDistance/2)
					{
						moveSpeed = AUTOBackUpSpeed;
					}
					else if (encoderDistanceData > AUTOBackUpDistance)
					{
						moveSpeed = AUTOBackUpSpeed;
						//set PID to lower when we're halfway there
						pid1->Enable();
						pid2->Enable();
						pid1->SetSetpoint((minArmAngle+10)*1000/maxPotAngle);
						pid2->SetSetpoint((minArmAngle+10)*1000/maxPotAngle);
					}
					else
					{
						moveSpeed = 0.0;
						AUTOState = AUTOLowerArm;
					}
					break;
				case AUTOLowerArm:
					SmartDashboard::Log("AUTOLowerArm", "State");
					if (pid1->OnTarget())
					{
						printf("ONTARGET, angle: %f", armAngle);
						pid1->Disable();
						pid2->Disable();
						gyro->Reset();
						AUTOState = AUTOShutdown;//AUTOTurn;
					}
					break;
				case AUTOTurn:
					SmartDashboard::Log("AUTOTurn", "State");
					turnPid->Enable();
					turnPid->SetSetpoint(-160);
					if (gyroAngle <= -160)
					{
						turnPid->Disable();
						moveSpeed = 0.0;
						turnSpeed = 0.0;
						AUTOState = AUTOShutdown;
					}
					break;
				case AUTOShutdown:
					SmartDashboard::Log("AUTOShutdown", "State");
					pid1->Disable();
					pid2->Disable();
					moveSpeed = 0.0;
					turnSpeed = 0.0;
					rightWheelSpeed = 0.0;
					leftWheelSpeed = 0.0;
					wheelEncoder->Reset();
					light->Set(Relay::kOff);
					break;
				case AUTOWait:
					SmartDashboard::Log("AUTOWait", "State");
					if (timer->Get() >= AUTOWaitTime)
					{
						timer->Stop();
						wheelEncoder->Reset();
						AUTOState = AUTOWaitNextState;
					}
					break;
				//****************************************************************
				//END GENERIC AUTO
				//****************************************************************
			}

			if (beginState == simpleAUTOBegin && !armDeployed && armAngle >= minArmAngle)
			{
				//deploys the arm when it is raised to a good position
				armDeploymentPiston->Set(true);
				armDeployed = true;
				relayPistonIn->Set(true);
				relayPistonOut->Set(false);
				pid1->SetSetpoint(minArmAngle*1000/maxPotAngle);
				pid2->SetSetpoint(minArmAngle*1000/maxPotAngle);
			}
			
			if (turnPid->IsEnabled())
			{
				jagL1->Set(-1*turnPid->Get());
				//jagL2->Set(turnPid->Get());
				jagR1->Set(-1*turnPid->Get());
				//jagR2->Set(turnPid->Get());
			}
			else if (leftWheelSpeed == 0 && rightWheelSpeed == 0)
			{
				myRobot->ArcadeDrive(-1*moveSpeed, turnSpeed);
			}
			else
			{
				myRobot->TankDrive(-1*leftWheelSpeed, -1*rightWheelSpeed);
			}
			
			if (AUTOState != colorAUTODrive)
			{
				SmartDashboard::Log(gyroAngle, "gyro");
				ShowActivity("AUTOState: %d, encoder: %f, gyro: %f, armAngle: %f", AUTOState, encoderDistanceData, gyroAngle, armAngle);
			}
		}
	}

	/**
	 * Runs the motors with arcade steering. 
	 */
	void OperatorControl(void) //teleop!!
	{
		int sensorLval = 0; //the three line sensor values
		int sensorMval = 0;
		int sensorRval = 0;
		int currentPotVal = 0;
		int desiredPotVal = 0;
		int shoulderswitchval = 0;
		float stick1Y = 0; //stick1 forward
		float stick1Z = 0; //stick1 turning
		float stick2Y = 0;
		float armAngle = 0;
		float moveSpeed = 0;
		float turnSpeed = 0;
		float armSpeed = 0;
		bool stick1button2 = false; //low gear (hold)
		bool stick2trigger = false; //close claw (hold)
		bool slowArmButton = false; //arm precision (hold)
		bool bottomPegButton = false; //stick 2 button
		bool middlePegButton = false; //stick 2 button
		bool topPegButton = false; //stick 2 button
		bool armOverrideButton = false; //stick 2 button 8
		bool minAngleOverrideButton = false;
		bool minibotDeployButton1 = false;
		bool minibotDeployButton2 = false;
		bool lastMinibotDeployButton = false;
		bool minibotDeployed = false;

		light->Set(Relay::kOff);
		compressor->Start();
		wheelEncoder->Start();
		pid1->SetInputRange(0,1000);
		pid1->SetOutputRange(-1*maxArmSpeed, maxArmSpeed);
		pid1->SetTolerance(5.0);
		pid1->Disable();
		pid2->SetInputRange(0,1000);
		pid2->SetOutputRange(-1*maxArmSpeed, maxArmSpeed);
		pid2->SetTolerance(5.0);
		pid2->Disable();
		turnPid->Disable();
		armDeploymentPiston->Set(true); //Deploys arm outward
		shifterPiston->Set(false); //Sets gears to high gear by default(drive)
		//myRobot.SetSafetyEnabled(true);
		
		while (IsOperatorControl())
		{
			SmartDashboard::Log(irSensor->GetVoltage(), "proximity");
			stick1Y = stick1->GetY();
			stick1Z = stick1->GetX();//GetRawAxis(4);
			stick1Z = stick1Z * fabs(stick1Z); //sqaured inputs...again (it's already included in ArcadeDrive)
			stick2Y = stick2->GetY();
			stick1button2 = stick1->GetRawButton(2);
			stick2trigger = stick2->GetRawButton(1);
			slowArmButton = stick2->GetRawButton(2);
			bottomPegButton = stick2->GetRawButton(9);
			middlePegButton = stick2->GetRawButton(10);
			topPegButton = stick2->GetRawButton(11);
			armOverrideButton = stick2->GetRawButton(8);
			minAngleOverrideButton = stick2->GetRawButton(3);
			minibotDeployButton1 = stick2->GetRawButton(6);
			minibotDeployButton2 = stick2->GetRawButton(7);
			armSpeed = stick2Y;
			currentPotVal = pot->GetValue();
			desiredPotVal = (int)(currentPotVal+stick2Y*deltaPotVal); //setpoint goes to this
			armAngle = currentPotVal*maxPotAngle/1000;
			sensorLval = sensorL->Get();
			sensorMval = sensorM->Get();
			sensorRval = sensorR->Get();
			shoulderswitchval = shoulderswitch->Get();
			
			
			moveSpeed = stick1Y;
			turnSpeed = stick1Z;
			
			
			/* -------------------------------------------------------
			 * MINIBOT DEPLOYMENT
			 * -----------------------------------------------------*/
			/* two buttons when pressed together act as a toggle switch
			 * for whether or not the minibot is deployed
			 */
			if (minibotDeployButton1 && minibotDeployButton2)
			{
				bottomPegButton = true;
				if (!lastMinibotDeployButton)
				{
					if (minibotDeployed)
					{
						minibotPiston->Set(false);
						minibotDeployed = false;
					}
					else
					{
						minibotPiston->Set(true);
						minibotDeployed = true;
					}
					lastMinibotDeployButton = true;
				}
			}
			else
			{
				lastMinibotDeployButton = false;
			}
			

			/* -------------------------------------------------------
			 * DRIVE TRAIN GEAR SHIFTING
			 * -----------------------------------------------------*/
			if (stick1button2)
			{
				turnSpeed = turnSpeed;
				shifterPiston->Set(true);
			}
			else
			{
				shifterPiston->Set(false);
			}

			/* -------------------------------------------------------
			 * CLAW
			 * -----------------------------------------------------*/
			if (stick2trigger) //claw actuates
			{
				relayPistonIn->Set(false);
				relayPistonOut->Set(true);
				armDeploymentPiston->Set(false); //retracts arm VALVE (makes it possible to reset the arm)
			}
			else //claw opens
			{
				relayPistonIn->Set(true);
				relayPistonOut->Set(false);
				armDeploymentPiston->Set(true);
			}
			
			/* -------------------------------------------------------
			 * ARM SPEED LIMITS
			 * -----------------------------------------------------*/
			if (armSpeed > maxArmSpeed)
			{
				armSpeed = maxArmSpeed;
			}
			if (armSpeed < -1*maxArmSpeed)
			{
				armSpeed = -1*maxArmSpeed;
			}
			if (slowArmButton) //high precision
			{
				if (armSpeed > 0) //go faster if arm goes up
				{
					armSpeed = armSpeed/1.5;
				}
				else //because gravity helps it go down
				{
					armSpeed = armSpeed/3;
				}
			}
			
			/* -------------------------------------------------------
			 * ARM:
			 *  - check limit switch
			 *  - manual override (joystick, no pid)
			 *  - bottom peg (button, pid)
			 *  - middle peg (button, pid)
			 *  - top peg (button, pid)
			 *  - manual pid (joystick, pid)
			 * -----------------------------------------------------*/
		
			if (shoulderswitchval == 0 && armSpeed > 0)
			{
				/* arm limit switch is pressed
				 * and arm is trying to move too far up
				 * stop arm from moving
				 */
				pid1->Disable();
				pid2->Disable();
				armSpeed = 0;
			}
			else if (armAngle < minArmAngle && armSpeed < 0 && !minAngleOverrideButton)
			{
				/* arm limit switch is pressed
				 * and arm is trying to move too far down
				 * stop arm from moving
				 */
				pid1->Disable();
				pid2->Disable();
				armSpeed = 0;
			}
			else if (armOverrideButton)
			{
				/* arm override:
				 * doesn't use pid
				 * only responds to joystick
				 * and arm limit switch
				 */
				pid1->Disable();
				pid2->Disable();
				armMotor1->Set(armSpeed); //it moves the arm when joystick is moved
				armMotor2->Set(armSpeed); //it moves the arm when joystick is moved
			}
			else
			{
				// let pid control the arm
				if (bottomPegButton) //moves arm to the bottom peg
				{
					pid1->Enable();
					pid2->Enable();
					desiredPotVal = (int)(bottomPegAngle*1000/maxPotAngle);
					desiredPotVal = (int)(bottomPegAngle*1000/maxPotAngle);
				}
				else if (middlePegButton) //moves arm to the middle peg
				{
					pid1->Enable();
					pid2->Enable();
					desiredPotVal = (int)(middlePegAngle*1000/maxPotAngle);
					desiredPotVal = (int)(middlePegAngle*1000/maxPotAngle);
				}
				else if (topPegButton) //moves arm to the top peg
				{
					pid1->Enable();
					pid2->Enable();
					desiredPotVal = (int)(topPegAngle*1000/maxPotAngle);
					desiredPotVal = (int)(topPegAngle*1000/maxPotAngle);
				}
				else
				{
					/* use joystick along w/ pid
					 * this lets the joystick to manually control arm
					 * but uses pid settings to reduce acceleration
					 * protecting the arm from the aliens coming and twisting the steel shaft
					 */
					if (desiredPotVal*maxPotAngle/1000 >= maxArmAngle && armSpeed > 0)
					{
						//don't let the arm move too far back
						desiredPotVal = (int)(maxArmAngle*1000/maxPotAngle);
					}
					else if (!minAngleOverrideButton && desiredPotVal*maxPotAngle/1000 <= minArmAngle && armSpeed < 0)
					{
						//don't let the arm go down through the ground
						desiredPotVal = (int)(minArmAngle*1000/maxPotAngle);
					}
					pid1->Disable();
					pid2->Disable();
					armMotor1->Set(armSpeed); //it moves the arm when joystick is moved
					armMotor2->Set(armSpeed); //it moves the arm when joystick is moved
					/*pid1->Enable();
					pid2->Enable();
					desiredPotVal = (int)cypressPot;*/
				}
				pid1->SetSetpoint(desiredPotVal);
				pid2->SetSetpoint(desiredPotVal);
			}		
			
			myRobot->ArcadeDrive(moveSpeed, turnSpeed); // Drive like the man who said that the wind was fast, baby!
			
			SmartDashboard::Log(armAngle,"armAngle");
			//ShowActivity("raw: %d, encoder: %f, potval: %d, armangle: %f, armSpeed: %f, shoulderswitch: %d", wheelEncoder->GetRaw(), wheelEncoder->GetRaw()*wheelCircumference*wheelGearRatio/(360*4), pot->GetValue(), shoulderswitch, armAngle, armSpeed);
			//ShowActivity("shoulderswitchval: %d, armSpeed: %f", shoulderswitchval, armSpeed);
			//ShowActivity("encoder: %f, armAngle: %f, armSpeed: %f, desiredPotVal: %d", wheelEncoder->GetRaw()*wheelCircumference*wheelGearRatio/(360*4), armAngle, armSpeed, desiredPotVal);
			ShowActivity("armAngle: %f, gyro: %f, ir: %f", armAngle, gyro->GetAngle(), irSensor->GetVoltage());
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

