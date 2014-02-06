#include "WPILib.h"
#include <Joystick.h>
#include <RobotDrive.h>
#include <Jaguar.h>
#include <Relay.h>
#include <Solenoid.h>
#include <Compressor.h>
#include <Encoder.h>
#include <Vision2009/BaeUtilities.h>
#include <Vision/AxisCamera.h> //use 'vision/' instead of just axis. its annoying.
#include <PIDController.h> //PID
#include <AnalogChannel.h>
#include <DigitalInput.h>
#include <SmartDashboard.h>
#include <DriverStation.h>
#include <math.h>

//defines are for switch case functions during auto. they are the states of it.
#define AUTORaiseArm 1
#define AUTODrive 2
#define AUTOStop 3

//exact angles which each variables are set to
int bottomPegAngle = 28; //# of degrees for the pegs
int middlePegAngle = 60;
int topPegAngle = 88;
int maxPotAngle = 360;
int armDeadzone = 2; //most the pot's gonna hop/jump (degrees)
int minArmAngle = 30; //farthest the arm should go (degrees)
int maxArmAngle = 270; //farthest the arm should go (degrees)
int deltaPotVal = 1; //angle arm turns w/ manual pid
float maxArmSpeed = 0.5;

float autoMoveSpeed = 0.6; //speed at which the bot moves during auto
float autoTurnSpeed = 0.75; //speed at which the bot turns during auto
float numLineSensorX = 0.3; //x value that the sensors are at on autonomous number line
float numLineWheelX = 0.5; //x value that the wheels are at on autonomous number line
float wheelCircumference = 8.0*3.1416; //(inches)
float wheelGearRatio = 12.0/26.0;
float autoEncoderDistance = -30.0; //(inches)

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
	Solenoid *relayPiston; //pneumatic piston
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

public:
	RobotDemo(void)
	{
		myRobot = new RobotDrive(1, 2, 3, 5);
		stick1 = new Joystick(1);	
		stick2 = new Joystick(2);
		armMotor1 = new Jaguar(4,6);
		armMotor2 = new Jaguar(4,7);
		//armEncoder = new Encoder(4,2,4,3);
		compressor = new Compressor(4,1,4,1);
		armDeploymentPiston = new Solenoid(7,1);
		relayPiston = new Solenoid(7,2);
		shifterPiston = new Solenoid(7,3);
		minibotPiston = new Solenoid(7,4);
		pot = new AnalogChannel(1,1);
		sensorL = new DigitalInput(4,6);
		sensorM = new DigitalInput(4,4);
		sensorR = new DigitalInput(4,5);
		//TODO Tune PID for smooth accelerations
		float PIDTimeInterval = 0.0005;
		pid1 = new PIDController(0.012,0.0,0.0,pot,armMotor1,PIDTimeInterval); //change to match with the pot
		pid2 = new PIDController(pid1->GetP(),pid1->GetI(),pid1->GetD(),pot,armMotor2,PIDTimeInterval); //change to match with the pot
		wheelEncoder = new Encoder(4,9,4,10);
		shoulderswitch = new DigitalInput(4,3);
		dsIO = DriverStation::GetInstance();
		//myRobot.SetExpiration(0.1);
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
		//int lastSensorVal = -2;
		//float armAngle = 0;
		float leftWheelSpeed = 0;
		float rightWheelSpeed = 0;
		float sensorNumLine = 0;
		float encoderDistanceData = 0;
		//float armSpeed = 0;
		bool armDeployed = false;
		int state = 1;
		
		compressor->Start();
		wheelEncoder->Start();
		pid1->SetInputRange(0,1000);
		pid1->SetOutputRange(-0.7,0.7);
		pid1->SetSetpoint(1000-(topPegAngle*1000/maxPotAngle));
		pid1->SetTolerance(5.0);
		pid1->Disable(); //start off with the pid disabled and enable during case AUTORaiseArm
		pid2->SetInputRange(0,1000);
		pid2->SetOutputRange(-0.7,0.7);
		pid2->SetSetpoint(1000-(topPegAngle*1000/maxPotAngle));
		pid2->SetTolerance(5.0);
		pid2->Disable(); //start off with the pid disabled and enable during case AUTORaiseArm
		shifterPiston->Set(false); //Sets default gear to high gear (drive)
		
		while (IsAutonomous()) //Storyship Entorprise
		{
			sensorLval = sensorL->Get();
			sensorMval = sensorM->Get();
			sensorRval = sensorR->Get();
			encoderDistanceData = -1*wheelEncoder->GetRaw()*wheelCircumference*wheelGearRatio/(360*4);
			ShowActivity("raw: %d, encoder: %f", wheelEncoder->GetRaw(), encoderDistanceData);

			SmartDashboard::Log(state, "Autonomous State");
			
			/* autonomous states. 
			 * it is in the order in which we will operate during automous. 
			 * we fetch the bot its tube in the very beginning. */
			switch (state)
			{					
				case AUTORaiseArm:
					/* raise arm to the bottom peg
					 * arm will then be deployed
					 * and lowered to minArmAngle
					 */
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					pid1->Enable();
					pid2->Enable();
					pid1->SetSetpoint(bottomPegAngle*1000/maxPotAngle);
					pid2->SetSetpoint(bottomPegAngle*1000/maxPotAngle);
					wheelEncoder->Reset();
					state = AUTODrive;
					break;
					
				case AUTODrive:
					/* back up 2.5 ft slowly
					 * using encoders
					 * then go to stop state
					 */
					
					if (encoderDistanceData > autoEncoderDistance)
					{
						rightWheelSpeed = -1 * autoMoveSpeed;
						leftWheelSpeed = -1 * autoMoveSpeed;
					}
					else
					{
						rightWheelSpeed = 0.0;
						leftWheelSpeed = 0.0;
						state = AUTOStop;
					}
					break;
					
				case AUTOStop: //ends autonomous
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					wheelEncoder->Reset();
					break;
			}

			if (!armDeployed)
			{
				if (pot->GetValue()*maxPotAngle/1000 >= minArmAngle)
				{
					/* deploys the arm when it is raised to a good position
					 * then lowers the arm to minArmAngle
					 */
					armDeploymentPiston->Set(true);
					relayPiston->Set(true);
					pid1->SetSetpoint(minArmAngle*1000/maxPotAngle);
					pid2->SetSetpoint(minArmAngle*1000/maxPotAngle);
					armDeployed = true;
				}
			}
			
			myRobot->TankDrive(-1*leftWheelSpeed, -1*rightWheelSpeed);		
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
		bool stick1button10 = false; //minibot deployment
		bool stick1button6 = false; //minibot undeployment (T2)
		bool stick1button3 = false; //low gear (hold)
		bool stick2trigger = false; //close claw (hold)
		bool stick2button3 = false; //arm precision (hold)
		bool bottomPegButton = false; //stick 2 button
		bool middlePegButton = false; //stick 2 button
		bool topPegButton = false; //stick 2 button
		bool armOverrideButton1 = false; //stick 1 button 7
		bool armOverrideButton2 = false; //stick 1 button 8
		bool minAngleOverrideButton = false;
		bool cypressSwitchState = false;
		
		compressor->Start();
		wheelEncoder->Start();
		pid1->SetInputRange(0,1000);
		pid1->SetOutputRange(-0.7,0.7);
		pid1->SetTolerance(5.0);
		pid1->Disable();
		pid2->SetInputRange(0,1000);
		pid2->SetOutputRange(-0.7,0.7);
		pid2->SetTolerance(5.0);
		pid2->Disable();
		AxisCamera &camera = AxisCamera::GetInstance();
		camera.WriteResolution(AxisCamera::kResolution_320x240);
		camera.WriteBrightness(0);
		camera.WriteCompression(20);
		armDeploymentPiston->Set(false); //Deploys arm outward
		shifterPiston->Set(false); //Sets gears to high gear by default(drive)
		//myRobot.SetSafetyEnabled(true);
		
		while (IsOperatorControl())
		{
			//dsIO->SetDigitalOut(1,true); How we can turn on and off LEDs.
			stick1Y = stick1->GetY();
			stick1Z = stick1->GetX();//GetRawAxis(4);
			stick1Z = stick1Z * fabs(stick1Z); //sqaured inputs...again (it's already included in ArcadeDrive)
			stick2Y = stick2->GetY();
			stick1button10 = stick1->GetRawButton(10);
			stick1button3 = stick1->GetRawButton(3);
			stick1button6 = stick1->GetRawButton(6);
			stick2trigger = stick2->GetRawButton(1);
			stick2button3 = stick2->GetRawButton(3);
			bottomPegButton = stick2->GetRawButton(9);
			middlePegButton = stick2->GetRawButton(10);
			topPegButton = stick2->GetRawButton(11);
			armOverrideButton1 = stick1->GetRawButton(7);
			armOverrideButton2 = stick1->GetRawButton(8);
			minAngleOverrideButton = stick2->GetRawButton(2);
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
			
			/*--------------------------------
			 * CYPRESS BOARD SWITCH
			 *--------------------------------*/ 
			cypressSwitchState = dsIO->GetDigitalIn(1);		
			if (cypressSwitchState == false)
			{
				dsIO->SetDigitalOut(1, true);
			}
			else
			{
				dsIO->SetDigitalOut(1,false);
			}
			
			/* -------------------------------------------------------
			 * MINIBOT DEPLOYMENT
			 * -----------------------------------------------------*/
			if (stick1button10)
			{
				minibotPiston->Set(true);
			}
			else if (stick1button6) //(T2)
			{
				minibotPiston->Set(false);
			}

			/* -------------------------------------------------------
			 * DRIVE TRAIN GEAR SHIFTING
			 * -----------------------------------------------------*/
			if (stick1button3)
			{
				turnSpeed = turnSpeed/1.5;
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
				relayPiston->Set(false);
				armDeploymentPiston->Set(false); //retracts arm VALVE (makes it possible to reset the arm)
			}
			else //claw retracts
			{
				relayPiston->Set(true);
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
			if (stick2button3) //high precision
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
			 *  - reduce arm speed on button press
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
			else if (armOverrideButton1 || armOverrideButton2)
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
				if (bottomPegButton) //moves arm to the buttom peg
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
					/*if (desiredPotVal*maxPotAngle/1000 >= maxArmAngle && armSpeed > 0)
					{
						//don't let the arm move too far back
						desiredPotVal = (int)(maxArmAngle*1000/maxPotAngle);
					}
					else if (!minAngleOverrideButton && desiredPotVal*maxPotAngle/1000 <= minArmAngle && armSpeed < 0)
					{
						//don't let the arm go down through the ground
						desiredPotVal = (int)(minArmAngle*1000/maxPotAngle);
					}*/
					pid1->Disable();
					pid2->Disable();
					armMotor1->Set(armSpeed); //it moves the arm when joystick is moved
					armMotor2->Set(armSpeed); //it moves the arm when joystick is moved
				}
				pid1->SetSetpoint(desiredPotVal);
				pid2->SetSetpoint(desiredPotVal);
			}
			
			myRobot->ArcadeDrive(moveSpeed, turnSpeed); // Drive like the man who said that the wind was fast, baby!
			
			SmartDashboard::Log(armAngle,"armAngle");
			//ShowActivity("raw: %d, encoder: %f, potval: %d, armangle: %f, armSpeed: %f, shoulderswitch: %d", wheelEncoder->GetRaw(), wheelEncoder->GetRaw()*wheelCircumference*wheelGearRatio/(360*4), pot->GetValue(), shoulderswitch, armAngle, armSpeed);
			//ShowActivity("shoulderswitchval: %d, armSpeed: %f", shoulderswitchval, armSpeed);
			ShowActivity("encoder: %f, armAngle: %f, armSpeed: %f, desiredPotVal: %d", shoulderswitchval, wheelEncoder->GetRaw()*wheelCircumference*wheelGearRatio/(360*4), armAngle, armSpeed, desiredPotVal);
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

