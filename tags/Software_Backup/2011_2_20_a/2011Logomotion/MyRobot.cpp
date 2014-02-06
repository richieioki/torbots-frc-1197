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

//defines are for switch case functions during auto. they are the states of it.
#define AUTORaiseArm 1
#define AUTODrive 2
#define AUTOStop 3
#define AUTOEncoder 4
#define AUTOLower 5
#define AUTORelease 6
#define AUTOShutdown 7

//exact angles which each variables are set to
int bottomPegAngle = 55; //# of degrees for the pegs
int middlePegAngle = 87;
int topPegAngle = 120;
int verticalArmAngle = 135;
int maxPotAngle = 360;
int armDeadzone = 2; //most the pot's gonna hop/jump (degrees)
int minArmAngle = 5; //farthest the arm should go (degrees)
int maxArmAngle = 270; //farthest the arm should go (degrees)
int deltaPotVal = 1; //angle arm turns w/ manual pid
float maxArmSpeed = 0.5;

float autoMoveSpeed = 0.5; //speed at which the bot moves during auto
float autoTurnSpeed = 0.75; //speed at which the bot turns during auto
float numLineSensorX = 0.3; //x value that the sensors are at on autonomous number line
float numLineWheelX = 0.5; //x value that the wheels are at on autonomous number line
float wheelCircumference = 8.0*3.1416; //(inches)
float wheelGearRatio = 12.0/26.0;
float encoderDistanceToPegs = -24.0; //(inches)
float encoderDeadZone = 5.0; //(inches)

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
		pid1 = new PIDController(0.05,0.0,0.0,pot,armMotor1,PIDTimeInterval); //change to match with the pot
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
			
			if (pid1->OnTarget())
			{
				armDeploymentPiston->Set(true);
			}
			
			/* autonomous states. 
			 * it is in the order in which we will operate during automous. 
			 * we fetch the bot its tube in the very beginning. */
			switch (state)
			{					
				case AUTORaiseArm: //raise arm to the top peg
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					/*pid1->Enable();
					pid2->Enable();
					pid1->SetSetpoint(verticalArmAngle*1000/maxPotAngle);
					pid2->SetSetpoint(verticalArmAngle*1000/maxPotAngle);*/
					state = AUTODrive;
					break;
					
				case AUTODrive:
					/*Explanation of Drive System
					 * 
					 * BIGGEST RULE: THE RIGHT SENSOR IS WHAT TRACKS THE LINE
					 * OTHER RULE: WE ARE CONSTANTLY MOVING FORWARD
					 * 
					 * If only the right sensor sees the line, we do not turn
					 * If only the left sensor sees the line, turns hard left
					 * If only the middle sensor sees the line, turns a soft left
					 * If both the middle and right see it, turns a soft left
					 * If left and middle see it, turns left
					 * If left and right see it, soft left (just like if only middle)
					 * If right and middle see it, turn soft left
					 * If none of the sensors see it, turn right
					 * 
					 * We represent all of this on a number line.
					 * Mark moves right if right sensor sees line
					 * Left if left sensor sees line
					 * Moves towards middle if middle sees it.
					 * 
					 * ||-----|-----||-----|-----||  (|)  
					 * HL     L     SL    SSL    ST   R
					 * 
					 * HL = Hard Left
					 * L  = "Medium" Left
					 * SL = Soft Left
					 * SSL= Super Soft Left
					 * ST = Straight
					 * R  = Right (Only if no sensors see the line)
					 * 	
					 */
					
					sensorNumLine = 0; 
					
					if (sensorLval && sensorRval && sensorMval) 
					{
						//if all three sees, the bot stops
						state = AUTOStop;
						break;
					}
					if (!sensorLval && !sensorMval && !sensorRval)
					{
						rightWheelSpeed = -0.1;
						leftWheelSpeed = 0.6;
						break;
					}
					if (sensorRval) //if right sees, mark goes right
					{
						sensorNumLine += numLineSensorX;
					}
					if (sensorLval) //if left sees, mark goes left
					{
						sensorNumLine -= numLineSensorX;
					}
					if (sensorMval) //if middle sees, mark goes towards the middle, dividing  by 2.
					{
						sensorNumLine /= 2;
					}
					sensorNumLine -= numLineSensorX;
					
					rightWheelSpeed = numLineWheelX - sensorNumLine; //where the wheels are on the numberline minus the mark on the numberline
					leftWheelSpeed = numLineWheelX + sensorNumLine; //where the wheels are on the numberline plus the mark on the numberline
					
					if (rightWheelSpeed < -1)
					{
						rightWheelSpeed = -1;
					}
					if (rightWheelSpeed > 1)
					{
						rightWheelSpeed = 1;
					}
					if (leftWheelSpeed < -1)
					{
						leftWheelSpeed = -1;
					}
					if (leftWheelSpeed > 1)
					{
						leftWheelSpeed = 1;
					}
					break;
					
				case AUTOStop: //temporarily stops the robot
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					wheelEncoder->Reset();
					state = AUTOEncoder;
					Wait(500);
					break;
					
				case AUTOEncoder: //adjusts to put the tube on the peg.
					if (encoderDistanceData <= encoderDistanceToPegs - (encoderDeadZone / 2))
					{
						leftWheelSpeed = autoMoveSpeed;
						rightWheelSpeed = autoMoveSpeed;
					}
					else if (encoderDistanceData >= encoderDistanceToPegs + (encoderDeadZone / 2))
					{
						leftWheelSpeed = -1*autoMoveSpeed;
						rightWheelSpeed = -1*autoMoveSpeed;
					}
					else
					{
						leftWheelSpeed = 0.0;
						rightWheelSpeed = 0.0;
						state = AUTOLower;
					}
					break;
					
				case AUTOLower:
					pid1->SetSetpoint(topPegAngle*1000/maxPotAngle);
					pid2->SetSetpoint(topPegAngle*1000/maxPotAngle);
					if (pid1->OnTarget())
					{
						state = AUTORelease;
					}
					break;
					
				case AUTORelease: //when the tube meets the desired peg, the arm releases
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					relayPiston->Set(true); //opens claw
					//state = AUTOShutdown;
					break;
					
				case AUTOShutdown: //shutdown of the robot, no movement.
					pid1->Disable();
					pid2->Disable();
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					break;
			}

			if (!armDeployed)
			{
				if (pot->GetValue() < 950)
				{
					//deploys the arm when it is raised to a good position
					armDeploymentPiston->Set(true);
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
		bool stick1button2 = false; //minibot deployment
		bool stick1button6 = false; //minibot undeployment (T2)
		bool stick1button3 = false; //low gear (hold)
		bool stick2trigger = false; //grab
		bool stick2button3 = false; //release
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
			stick2Y = stick2->GetY();
			stick1button2 = stick1->GetRawButton(2);
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
			if (stick1button2)
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
				turnSpeed = turnSpeed / 2;
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
			else if (stick2button3) //claw retracts
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
			
			/* -------------------------------------------------------
			 * ARM:
			 *  - check limit switch
			 *  - manual override (joystick, no pid)
			 *  - bottom peg (button, pid)
			 *  - middle peg (button, pid)
			 *  - top peg (button, pid)
			 *  - manual pid (joystick, pid)
			 * -----------------------------------------------------*/
			if (shoulderswitchval == 0 && armSpeed < 0 && !minAngleOverrideButton)
			{
				/* arm limit switch is pressed
				 * and arm is trying to move down
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
			//ShowActivity("shoulderswitchval: %d", shoulderswitchval);
			ShowActivity("encoder: %f, armAngle: %f, armSpeed: %f, desiredPotVal: %d", shoulderswitchval, wheelEncoder->GetRaw()*wheelCircumference*wheelGearRatio/(360*4), armAngle, armSpeed, desiredPotVal);
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

