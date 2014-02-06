#include "WPILib.h"
#include <Joystick.h>
#include <RobotDrive.h>
#include <Jaguar.h>
#include <Relay.h>
#include <Solenoid.h>
#include <Compressor.h>
#include <Vision2009/BaeUtilities.h>
#include <Vision/AxisCamera.h> //use 'vision/' instead of just axis. its annoying.
#include <PIDController.h> //PID
#include <AnalogChannel.h>
#include <DigitalInput.h>
#include <SmartDashboard.h>

//defines are for switch case functions during auto. they are the states of it.
#define AUTORaiseArm 1
#define AUTODrive 2
#define AUTOStop 3
#define AUTOEncoder 4
#define AUTORelease 5
#define AUTOShutdown 6

//exact angles which each variables are set to
int bottomPegAngle = 50; //# of degrees for the pegs
int middlePegAngle = 82;
int topPegAngle = 115;
int maxPotAngle = 360;
int armDeadzone = 2; //most the pot's gonna hop/jump (degrees)

float autoMoveSpeed = 0.5; //speed at which the bot moves during auto
float autoTurnSpeed = 0.75; //speed at which the bot turns during auto
float numLineSensorX = 0.2; //x value that the sensors are at on autonomous number line
float numLineWheelX = 0.6; //x value that the wheels are at on autonomous number line
float wheelCircumference = 25.13; //(inches)
float distanceToPegs = 26.0; //(inches)
float encoderDeadZone = 5; //(inches)

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
	PIDController *pid1; //Proportional Integral Derivative
	PIDController *pid2; //Proportional Integral Derivative
	Solenoid *armDeploymentPiston; //self explanatory
	Solenoid *shifterPiston; // valve to control 2 pistons to shift gear on two-speed
	Solenoid *minibotPiston; // valve to actuate minibot deployment mechanism
	Encoder *wheelEncoder;

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
		pid1 = new PIDController(0.01,0.0,0.03,pot,armMotor1); //change to match with the pot
		pid2 = new PIDController(pid1->GetP(),pid1->GetI(),pid1->GetD(),pot,armMotor2); //change to match with the pot
		wheelEncoder = new Encoder (4,7,4,8);
		
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
		pid1->SetInputRange(0,1000);
		pid1->SetOutputRange(-1.0,1.0);
		pid1->SetSetpoint(1000-(topPegAngle*1000/maxPotAngle));
		pid1->SetTolerance(5.0);
		pid1->Disable(); //start off with the pid disabled and enable during case AUTORaiseArm
		pid2->SetInputRange(0,1000);
		pid2->SetOutputRange(-1.0,1.0);
		pid2->SetSetpoint(1000-(topPegAngle*1000/maxPotAngle));
		pid2->SetTolerance(5.0);
		pid2->Disable(); //start off with the pid disabled and enable during case AUTORaiseArm
		shifterPiston->Set(false); //Sets default gear to high gear (drive)
		
		while (IsAutonomous()) //Storyship Entorprise
		{
			sensorLval = sensorL->Get();
			sensorMval = sensorM->Get();
			sensorRval = sensorR->Get();
			encoderDistanceData = wheelEncoder->GetRaw()*wheelCircumference/360;

			SmartDashboard::Log(state, "Atonomous State");
			
			/* autonomous states. 
			 * it is in the order in which we will operate during automous. 
			 * we fetch the bot its tube in the very beginning. */
			switch (state)
			{					
				case AUTORaiseArm: //raise arm to the top peg
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					pid1->Enable();
					pid1->SetSetpoint(topPegAngle*1000/maxPotAngle);
					pid2->Enable();
					pid2->SetSetpoint(topPegAngle*1000/maxPotAngle);
					state = AUTODrive;
					break;
					
				case AUTODrive:
					//autonomous driving system? using the numberline that corresponds to the line sensors.
					sensorNumLine = 0; 
					/* 5 increments/positions on the line. 
					 * the marks on the line indicates which sensors see the line. 
					 */
					if (sensorLval && sensorRval) 
					{
						if (sensorMval) //if all three sees, the bot stops
						{
							state = AUTOStop;
						}
						else //if both left and right sees, turn left
						{
							leftWheelSpeed = 0.0;
							rightWheelSpeed = 0.0;
							myRobot->ArcadeDrive(autoMoveSpeed, -1*autoTurnSpeed);
						}
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
					rightWheelSpeed = numLineWheelX - sensorNumLine; //where the wheels are on the numberline minus the mark on the numberline
					leftWheelSpeed = numLineWheelX + sensorNumLine; //where the wheels are on the numberline plus the mark on the numberline
					
					break;
					
				case AUTOStop: //temporarily stops the robot
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					pid1->Disable();
					pid2->Disable();
					wheelEncoder->Reset();
					//state = AUTOEncoder;			
					break;
					
				case AUTOEncoder: //adjusts to put the tube on the peg.
					if (encoderDistanceData <= distanceToPegs - (encoderDeadZone / 2))
					{
						leftWheelSpeed = autoMoveSpeed;
						rightWheelSpeed = autoMoveSpeed;
					}
					else if (encoderDistanceData >= distanceToPegs + (encoderDeadZone / 2))
					{
						leftWheelSpeed = -1*autoMoveSpeed;
						rightWheelSpeed = -1*autoMoveSpeed;
					}
					else
					{
						leftWheelSpeed = 0.0;
						rightWheelSpeed = 0.0;
						state = AUTORelease;
					}
					break;
					
				case AUTORelease: //when the tube meets the desired peg, the arm releases
					leftWheelSpeed = 0.0;
					rightWheelSpeed = 0.0;
					relayPiston->Set(true); //opens claw
					state = AUTOShutdown;
					break;
					
				case AUTOShutdown: //shutdown of the robot, no movement.
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
		int currentAngle = 0;
		int desiredAngle = 0;
		const int deltaAngle = 15;
		float stick1Y = 0; //stick1 forward
		float stick1Z = 0; //stick1 turning
		float stick2Y = 0;
		float armAngle = 0;
		float moveSpeed = 0;
		float turnSpeed = 0;
		float armSpeed = 0;
		bool stick1button2 = false; //minibot deployment
		bool stick1button6 = false; //minibot undeployment (T2)
		bool stick1button3 = false; //low gear
		bool stick1button4 = false; //high gear
		bool stick2trigger = false; //grab
		bool stick2button3 = false; //release
		bool bottomPegButton = false; //stick 2 button
		bool middlePegButton = false; //stick 2 button
		bool topPegButton = false; //stick 2 button
		compressor->Start();
		pid1->SetInputRange(0,1000);
		pid1->SetOutputRange(-1.0,1.0);
		pid1->SetTolerance(5.0);
		pid1->Disable();
		pid2->SetInputRange(0,1000);
		pid2->SetOutputRange(-1.0,1.0);
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
			stick1Y = stick1->GetY();
			stick1Z = stick1->GetX();//GetRawAxis(4);
			stick2Y = stick2->GetY();
			stick1button2 = stick1->GetRawButton(2);
			stick1button3 = stick1->GetRawButton(3);
			stick1button4 = stick1->GetRawButton(4);
			stick1button6 = stick1->GetRawButton(6);
			stick2trigger = stick2->GetRawButton(1);
			stick2button3 = stick2->GetRawButton(3);
			bottomPegButton = stick2->GetRawButton(9);
			middlePegButton = stick2->GetRawButton(10);
			topPegButton = stick2->GetRawButton(11);
			currentAngle = pot->GetValue();
			desiredAngle = (int)(currentAngle+stick2Y*deltaAngle);
			armAngle = currentAngle*maxPotAngle/1000;
			sensorLval = sensorL->Get();
			sensorMval = sensorM->Get();
			sensorRval = sensorR->Get();
			
			moveSpeed = stick1Y;
			turnSpeed = stick1Z;
			
			//minibot deployment
			if (stick1button2)
			{
				minibotPiston->Set(true);
			}
			else if (stick1button6) //(T2)
			{
				minibotPiston->Set(false);
			}
			
			//shift gears w/ stick1 buttons 3 and 4
			if (stick1button3)
			{
				shifterPiston->Set(true);
			}
			else// if (stick1button4)
			{
				shifterPiston->Set(false);
			}
			
			if (stick2trigger) //claw actuates
			{
				relayPiston->Set(false);
				armDeploymentPiston->Set(true); //retracts arm VALVE (makes it possible to reset the arm)
			}
			else if (stick2button3) //claw retracts
			{
				relayPiston->Set(true);
			}
		
			if (bottomPegButton) //moves arm to the buttom peg
			{
				pid1->Enable();
				pid1->SetSetpoint(bottomPegAngle*1000/maxPotAngle);
				pid2->Enable();
				pid2->SetSetpoint(bottomPegAngle*1000/maxPotAngle);
			}
			else if (middlePegButton) //moves arm to the middle peg
			{
				pid1->Enable();
				pid1->SetSetpoint(middlePegAngle*1000/maxPotAngle);
				pid2->Enable();
				pid2->SetSetpoint(middlePegAngle*1000/maxPotAngle);
			}
			else if (topPegButton) //moves arm to the top peg
			{
				pid1->Enable();
				pid1->SetSetpoint(topPegAngle*1000/maxPotAngle);
				pid2->Enable();
				pid2->SetSetpoint(topPegAngle*1000/maxPotAngle);
			}
			else //use joystick
			{
				pid1->Disable();
				pid2->Disable();
				armSpeed = stick2Y;
				if (armAngle >= maxPotAngle - armDeadzone && armSpeed > 0)
				{
					armSpeed = 0;
				}
				else if (armAngle <= armDeadzone && armSpeed < 0)
				{
					armSpeed = 0;
				}
				armMotor1->Set(armSpeed); //it moves the arm when joystick is moved
				armMotor2->Set(armSpeed); //it moves the arm when joystick is moved 
				/*pid1->Enable();
				pid2->Enable();
				pid1->SetSetpoint(desiredAngle);
				pid2->SetSetpoint(desiredAngle);*/
			}
			
			myRobot->ArcadeDrive(moveSpeed, turnSpeed); // Drive like the man who said that the wind was fast, baby!
			
			SmartDashboard::Log(armAngle,"armAngle");
			ShowActivity("potval: %d, armangle: %f, armSpeed: %f", pot->GetValue(), armAngle, armSpeed);
		}
	}
};

START_ROBOT_CLASS(RobotDemo);

