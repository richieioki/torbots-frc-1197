#include "WPILib.h"
#include "TorAutonomous.h"
#include "TorbotDrive.h"
#include "TorJagDrive.h"
#include "Consts.h"
#include "TorShooter.h"
#include <time.h>

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
  TorbotDrive *myTorbotDrive;
  TorJagDrive *myJagDrive;
  TorShooter *myShooter;
  TorAutonomous *myAutonomous;
  Encoder *wheelEncoderLeft;
  Encoder *wheelEncoderRight;
  Jaguar *leftDriveJag;
  Jaguar *leftDriveJag1;
  Jaguar *rightDriveJag;
  Jaguar *rightDriveJag1;
  //Jaguar *topWheelJag;
  //Jaguar *topWheelJag1;
  //Jaguar *bottomWheelJag;
  //Jaguar *bottomWheelJag1;
  Gyro *gyro;
  Compressor *compressor;
  //Autonomous *autonomous; //Needs to be initialized.

public:
  RobotDemo():
    //myRobot(1, 2),	// these must be initialized in the same order
    stick(1)		// as they are declared above.
  {
    //myRobot.SetExpiration(0.1);
    compressor = new Compressor(Consts::PRESSURE_SWITCH_CHANNEL, Consts::COMPRESSOR_RELAY_CHANNEL); //change ports for actual bot
    compressor->Start();
    leftDriveJag = new Jaguar(Consts::LEFT1_DRIVE_JAG_MOD, Consts::LEFT1_DRIVE_JAG);
    leftDriveJag1 = new Jaguar(Consts::LEFT2_DRIVE_JAG_MOD, Consts::LEFT2_DRIVE_JAG);
    rightDriveJag = new Jaguar(Consts::RIGHT1_DRIVE_JAG_MOD, Consts::RIGHT1_DRIVE_JAG);
    rightDriveJag1 = new Jaguar(Consts::RIGHT2_DRIVE_JAG_MOD, Consts::RIGHT2_DRIVE_JAG);
    //topWheelJag = new Jaguar(1, 5);
    //topWheelJag1 = new Jaguar(1, 6);
    //bottomWheelJag = new Jaguar(1, 7);
    //bottomWheelJag1 = new Jaguar(1, 8);
    wheelEncoderLeft = new Encoder(Consts::LEFT_ENCODER_MOD, Consts::LEFT_ENCODER_MSB, Consts::LEFT_ENCODER_MOD, Consts::LEFT_ENCODER_LSB); //a channel, b channel
    wheelEncoderRight = new Encoder(Consts::RIGHT_ENCODER_MOD, Consts::RIGHT_ENCODER_MSB, Consts::RIGHT_ENCODER_MOD, Consts::RIGHT_ENCODER_LSB); //a channel, b channel
    gyro = new Gyro(Consts::GYRO_MOD, Consts::GYRO_CHANNEL);
    gyro->Reset();
    myJagDrive = new TorJagDrive(*leftDriveJag, *leftDriveJag1, *rightDriveJag, *rightDriveJag1);
    myTorbotDrive = new TorbotDrive(stick, *myJagDrive, *gyro, *wheelEncoderRight);
    myShooter = new TorShooter(stick);
    myAutonomous = new TorAutonomous(*myShooter);

    //intialize varibles
  }

  /**
   * Drive left & right motors for 2 seconds then stop
   */
  void Autonomous()
  {
    time_t start;
    time_t end;
    double timer; //number of seconds taken to travel
    time(&start); //start timer
    myAutonomous->RunJags();
    myTorbotDrive->DriveStraight(0.6, 180.5f); //drive all the way to target
    time(&end);
    timer = difftime(end, start);
    if (timer < 5.0) //if (timer > 5)
      {
        myAutonomous->AutoFire(); //fire
      }
    else
      {
        //take picture
        if (true) //if (goal is hot)
          {
            myAutonomous->AutoFire(); //fire
          }
        else
          {
            Wait(6.0 - timer); //wait 5-timer plus a little extra
            myAutonomous->AutoFire(); //fire
          }
      }


  }

  /**
   * Runs the motors with arcade steering. 
   */
  void OperatorControl()
  {
    /*
     * Order of while loop
     * 1: check if states should have changed
     * 2: evaluate and execute inputs
     * 3: execute states
     */
    myTorbotDrive->resetEncoder();
    gyro->Reset();
    compressor->Start();
    bool shiftToggle = false;
    while (IsOperatorControl() && IsEnabled())
      {
        if (myShooter->GetCageState() == TorShooter::Drive)
          {
            myTorbotDrive->ArcadeDrive(true);
            if (stick.GetRawButton(Consts::DOWN_SHIFT_BUTTON))
              {
                shiftToggle = true; //slow gear
              }
            else if (stick.GetRawButton(Consts::UP_SHIFT_BUTTON))
              {
                shiftToggle = false; //fast gear
              }
            myTorbotDrive->shiftGear(shiftToggle);
          }
        
        if (stick.GetRawButton(Consts::LOAD_BUTTON))
          {
            myShooter->MoveCage(TorShooter::Load);
          }
        else if (stick.GetRawButton(Consts::DRIVE_BUTTON))
          {
            myShooter->MoveCage(TorShooter::Drive);
          }
        else if (stick.GetRawButton(Consts::SHOOT_BUTTON))
          {
            myShooter->MoveCage(TorShooter::Shoot);
          }
        else if (stick.GetRawButton(Consts::PASS_BUTTON))
          {
            myShooter->MoveCage(TorShooter::Pass);
          }

        if (stick.GetRawButton(Consts::RUN_BUTTON))
          {
            myShooter->Run(); //runs shooter wheels, torshooter code will handle firing as well
          }


      }

  }

  /**
   * Runs during test mode
   */
  void Test() {

  }
};

START_ROBOT_CLASS(RobotDemo);
