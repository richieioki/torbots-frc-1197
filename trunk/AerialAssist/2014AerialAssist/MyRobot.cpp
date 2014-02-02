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
  Jaguar *armJag;

  Solenoid *pickupSOL;
  Solenoid *shootingSOL;
  Solenoid *shiftSOL;

  Gyro *gyro;
  Compressor *compressor;

  bool pickupBOOL, shootingBOOL;

public:
  RobotDemo():
    //myRobot(1, 2),	// these must be initialized in the same order
    stick(1),		// as they are declared above.
    tartarus(2)
  {
    ds = DriverStationLCD::GetInstance();
    compressor = new Compressor(Consts::PRESSURE_SWITCH_CHANNEL, Consts::COMPRESSOR_RELAY_CHANNEL);
    compressor->Start();
    leftDriveJag = new Jaguar(Consts::LEFT1_DRIVE_JAG);
    //leftDriveJag1 = new Jaguar(Consts::LEFT2_DRIVE_JAG_MOD, Consts::LEFT2_DRIVE_JAG);
    rightDriveJag = new Jaguar(Consts::RIGHT1_DRIVE_JAG);
    //rightDriveJag1 = new Jaguar(Consts::RIGHT2_DRIVE_JAG_MOD, Consts::RIGHT2_DRIVE_JAG);

    //    wheelEncoderLeft = new Encoder(Consts::LEFT_ENCODER_MOD, Consts::LEFT_ENCODER_MSB, Consts::LEFT_ENCODER_MOD, Consts::LEFT_ENCODER_LSB); //a channel, b channel
    //    wheelEncoderRight = new Encoder(Consts::RIGHT_ENCODER_MOD, Consts::RIGHT_ENCODER_MSB, Consts::RIGHT_ENCODER_MOD, Consts::RIGHT_ENCODER_LSB); //a channel, b channel
    //    gyro = new Gyro(Consts::GYRO_MOD, Consts::GYRO_CHANNEL);
    //    gyro->Reset();
    myJagDrive = new TorJagDrive(*leftDriveJag, *rightDriveJag);
    myTorbotDrive = new TorbotDrive(stick, *myJagDrive); //, *gyro, *wheelEncoderRight);
    myShooter = new TorShooter(stick, tartarus);
    myAutonomous = new TorAutonomous(*myShooter);

    shiftSOL = new Solenoid(Consts::SHIFT_SOLENOID);
  }

  /**
   * Drive left & right motors for 2 seconds then stop
   */
  void Autonomous()
  {
    myAutonomous->runAutonomous();
  }

  /**
   * Runs the motors with arcade steering. 
   */
  void OperatorControl()
  {

    ds->Clear();
    /*
     * Order of while loop
     * 1: check if states should have changed
     * 2: evaluate and execute inputs
     * 3: execute states
     */
    //    myTorbotDrive->resetEncoder();
    //    gyro->Reset();
    bool shiftToggle = false;
    float shooterSpeed;
    while (IsOperatorControl() && IsEnabled())
      {
        ds->Clear();
        if (myShooter->IsLoaded())
          {
            ds->Printf(DriverStationLCD::kUser_Line1, 1, "Loaded");
          }
        else
          {
            ds->Printf(DriverStationLCD::kUser_Line1, 1, "Loading");
          }
        shooterSpeed = myShooter->ShooterSpeed();
        ds->Printf(DriverStationLCD::kUser_Line2, 1, "Shooter Speed: %f.0 \%", shooterSpeed * 100);
        ds->UpdateLCD();
        myTorbotDrive->ArcadeDrive(true);
        if (stick.GetRawButton(Consts::SHIFT_BUTTON))
          {
            shiftToggle = true; //slow gear
          }
        else
          {
            shiftToggle = false; //fast gear
          }
        shiftSOL->Set(shiftToggle);

        if (stick.GetRawButton(Consts::LOAD_BUTTON))
          {
            myShooter->MoveLoaderDown(true);
          }
        else if (stick.GetRawButton(Consts::DRIVE_BUTTON))
          {
            myShooter->MoveLoaderDown(false);
          }


        if (stick.GetRawButton(Consts::SHOOTER_UP_BUTTON))
          {
            myShooter->MoveShooter(0.65);
          }
        else if (stick.GetRawButton(Consts::SHOOTER_DOWN_BUTTON) && myShooter->IsLoaderDown())
          {
            myShooter->MoveShooter(-0.65);
          }
        else
          {
            myShooter->MoveShooter(0.0);
          }

        myShooter->Run(); //runs shooter wheels, torshooter code will handle firing as well



      }

  }

  /**
   * Runs during test mode
   */
  void Test() {

  }
};

START_ROBOT_CLASS(RobotDemo);
