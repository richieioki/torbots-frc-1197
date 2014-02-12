#include "WPILib.h"
#include "TorAutonomous.h"
#include "TorbotDrive.h"
#include "TorJagDrive.h"
#include "Consts.h"
#include "TorShooter.h"
#include <time.h>

DriverStationLCD *ds;

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 
class RobotDemo : public SimpleRobot
{
  //RobotDrive myRobot; // robot drive system
  Joystick stick; // only joystick
  Joystick tartarus; //left-handed gaming pad
  TorbotDrive *myTorbotDrive;
  TorJagDrive *myJagDrive;
  TorShooter *myShooter;
  TorAutonomous *myAutonomous;
  Encoder *wheelEncoderLeft;
  Encoder *wheelEncoderRight;

  LiveWindow *lw;

  //Drive Jags
  Jaguar *leftDriveJag;
  Jaguar *leftDriveJag1;
  Jaguar *rightDriveJag;
  Jaguar *rightDriveJag1;

  //Shooter Jags
  Jaguar *topWheelJag;
  Jaguar *topWheelJag1;
  Jaguar *bottomWheelJag;
  Jaguar *bottomWheelJag1;

  //Pickup/Arm Jags
  Jaguar *pickupJag;
  Talon *armJag;

  Solenoid *pickupSOL;
  Solenoid *shootingSOL;
  //Solenoid *shiftSOL;

  Gyro *gyro;
  Compressor *compressor;

  AnalogChannel *armPOT;

  PIDController *myShooterArmPID;

  bool pickupBOOL, shootingBOOL;

public:
  RobotDemo():
    //myRobot(1, 2),	// these must be initialized in the same order
    stick(Consts::STICK_PORT),		// as they are declared above.
    tartarus(Consts::TARTARUS_PORT)
  {
    ds = DriverStationLCD::GetInstance();
    ds->Clear();


    compressor = new Compressor(Consts::PRESSURE_SWITCH_CHANNEL, Consts::COMPRESSOR_RELAY_CHANNEL);
    compressor->Start();
    leftDriveJag = new Jaguar(Consts::LEFT1_DRIVE_JAG);
    leftDriveJag1 = new Jaguar(Consts::LEFT2_DRIVE_JAG);
    rightDriveJag = new Jaguar(Consts::RIGHT1_DRIVE_JAG);
    rightDriveJag1 = new Jaguar(Consts::RIGHT2_DRIVE_JAG);

    armJag = new Talon(Consts::CAGE_JAG);

    wheelEncoderLeft = new Encoder(Consts::LEFT_ENCODER_MSB, Consts::LEFT_ENCODER_LSB); //a channel, b channel
    wheelEncoderRight = new Encoder(Consts::RIGHT_ENCODER_MSB, Consts::RIGHT_ENCODER_LSB); //a channel, b channel
    gyro = new Gyro(Consts::GYRO_CHANNEL);
    gyro->Reset();

    myJagDrive = new TorJagDrive(*leftDriveJag, *leftDriveJag1, *rightDriveJag, *rightDriveJag1);

    myTorbotDrive = new TorbotDrive(stick, *myJagDrive, *gyro, *wheelEncoderRight);

    armPOT = new AnalogChannel(2);

    myShooter = new TorShooter(stick, tartarus, *armJag);



    myShooterArmPID = new PIDController(0.05, 0.003, 0.0, armPOT, armJag, 0.05);  //TODO Tune PID    // PID controller for moving the shooter
    myShooterArmPID->SetContinuous(false);
    myShooterArmPID->SetInputRange(Consts::SHOOTER_ARM_MIN, Consts::SHOOTER_ARM_LOADING);
    myShooterArmPID->SetOutputRange(0.75, -0.75); //gravity helps on the way down
    //    // init shooter arm setpoint to its current position so it does not move initially when we enable PID
    myShooterArmPID->SetSetpoint(armPOT->GetAverageValue());    // set point is where it is when started, down

    myShooterArmPID->SetPercentTolerance(10.0);
    myShooterArmPID->Enable();

    //lw = LiveWindow::GetInstance();

    //lw->AddActuator("Arm", "arm motor", armJag);
    //lw->AddActuator("Arm", "arm pot", armPOT);

    //myAutonomous = new TorAutonomous(*myShooter, *myTorbotDrive);
  }

  /**
   * Drive left & right motors for 2 seconds then stop
   */
  void Autonomous()
  {
    //myAutonomous->runAutonomous();
    myTorbotDrive->DriveStraight(Consts::AUTO_DRIVE_SPEED, Consts::AUTO_DRIVE_DIST); //test drive 180.5 inches
  }

  /**
   * Runs the motors with arcade steering. 
   */
  void OperatorControl()
  {

    //    ds->Clear();
    //    myTorbotDrive->resetEncoder();
    //    gyro->Reset();

    float shooterSpeed;
    while (IsOperatorControl() && IsEnabled())
      {

        /*********************************************************
                                  DS INFO
         *********************************************************/        
        ds->Clear();
        /*if (myShooter->IsLoaded())
          {
            ds->Printf(DriverStationLCD::kUser_Line1, 1, "Loaded");
          }
        else
          {
            ds->Printf(DriverStationLCD::kUser_Line1, 1, "Loading");
          }
         */
        shooterSpeed = myShooter->ShooterSpeed();
        ds->Printf(DriverStationLCD::kUser_Line2, 1, "Shooter Speed: %f.0 \%", shooterSpeed * 100);
        ds->UpdateLCD();



        /*********************************************************
                                  DRIVING
         *********************************************************/
        myTorbotDrive->ArcadeDrive(true);
        ds->Printf(DriverStationLCD::kUser_Line1, 1, "Test");
        ds->UpdateLCD();

        /*********************************************************
                                  SHIFTING
         *********************************************************/
        myTorbotDrive->shiftGear(tartarus.GetRawButton(Consts::SHIFT_BUTTON));


        if (tartarus.GetRawButton(Consts::LOADER_DOWN_BUTTON) || stick.GetRawButton(Consts::S_LOADER_DOWN_BUTTON))
          {
            myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED);
          }
        else if (tartarus.GetRawButton(Consts::LOADER_UP_BUTTON) || stick.GetRawButton(Consts::S_LOADER_UP_BUTTON))
          {
            myShooter->MoveLoaderDown(!Consts::LOADER_PISTON_EXTENDED);
          }


        if (tartarus.GetRawButton(Consts::SHOOTER_UP_BUTTON) || stick.GetRawButton(Consts::S_SHOOTER_UP_BUTTON))
          //put cage in firing position
          {
            //myShooter->MoveShooter(Consts::CAGE_MOVE_SPEED);
            //myShooter->SetCagePos(true);
            if(!myShooterArmPID->IsEnabled())
              {
                myShooterArmPID->Enable();
              }
            myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_SHOOTING); 
          }
        else if ((tartarus.GetRawButton(Consts::SHOOTER_DOWN_BUTTON) || stick.GetRawButton(Consts::S_SHOOTER_DOWN_BUTTON)))
          //put cage in loading position
          {
            //myShooter->MoveShooter(-Consts::CAGE_MOVE_SPEED); 
            //myShooter->SetCagePos(false);
            if(!myShooterArmPID->IsEnabled())
              {
                myShooterArmPID->Enable();
              }
            myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_LOADING);
          }
        
        if (myShooterArmPID->OnTarget())
          {
            myShooterArmPID->Disable();
          }
        ds->Printf(DriverStationLCD::kUser_Line3, 1,"armPOT: %d \%", armPOT->GetAverageValue());
        ds->UpdateLCD();


        /**********************************
         * MANUAL CAGE CONTROL
         **********************************/

        /*if (stick.GetRawButton(2)) //move up
          {
            myShooterArmPID->Disable();
            myShooter->MoveShooter(Consts::CAGE_MOVE_SPEED);
          }
        else if (stick.GetRawButton(5))
          {
            myShooterArmPID->Disable();
            myShooter->MoveShooter(-Consts::CAGE_MOVE_SPEED);
          }*/


        myShooter->Run(); //runs shooter wheels, torshooter code will handle firing as well
        //myShooter->SetCagePos();

        /*if(stick.GetRawButton(10)) {
            myShooter->SetJagSpeed(1.0);
        } else if(stick.GetRawButton(1)) {
            myShooter->SetJagSpeed(-1.00);
        } else {
            myShooter->SetJagSpeed(0.0);
        }*/

      }

  }

  /**
   * Runs during test mode
   */
  void Test() {
    lw->SetEnabled(true);
    while(true)
      {
        lw->Run();
        
      }
  }
};

START_ROBOT_CLASS(RobotDemo);
