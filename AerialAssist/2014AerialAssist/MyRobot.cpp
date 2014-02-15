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

    myTorbotDrive = new TorbotDrive(stick, *myJagDrive, *gyro, *wheelEncoderLeft,*ds);

    armPOT = new AnalogChannel(Consts::ARM_POT_CHANNEL);

    myShooter = new TorShooter(stick, tartarus);

    myShooterArmPID = new PIDController(0.05, 0.003, 0.0, armPOT, armJag, 0.05);  //TODO Tune PID    // PID controller for moving the shooter
    myShooterArmPID->SetContinuous(false);
    myShooterArmPID->SetInputRange(Consts::SHOOTER_ARM_MIN, Consts::SHOOTER_ARM_LOADING);
    myShooterArmPID->SetOutputRange(0.75, -0.75); //gravity helps on the way down
    //    // init shooter arm setpoint to its current position so it does not move initially when we enable PID
    myShooterArmPID->SetSetpoint(armPOT->GetAverageValue());    // set point is where it is when started, down

    myShooterArmPID->SetPercentTolerance(3.5);
    myShooterArmPID->Enable();

    //myAutonomous = new TorAutonomous(*myShooter, *myTorbotDrive);
  }

  /**
   * Drive left & right motors for 2 seconds then stop
   */
  void Autonomous()
  { 
    //myAutonomous->runAutonomous();
//    ds->Printf(DriverStationLCD::kUser_Line1, 1, "Encoder: %f", wheelEncoderRight->GetRaw());
//    ds->UpdateLCD();
    
    myShooter->SetJagSpeed(0.0);
    myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED);
    if(!myShooterArmPID->IsEnabled())
      {
        myShooterArmPID->Enable();
      }
    myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_LOADING);
    while(!myShooterArmPID->OnTarget())
          {

          }
        myShooterArmPID->Disable();
    
    myShooter->SetJagSpeed(Consts::SHOOTER_LOAD_SPEED);
    myShooter->SetLoaderBarSpeed(Consts::LOADER_BAR_SPEED);

    myTorbotDrive->DriveStraight(Consts::AUTO_DRIVE_SPEED, Consts::AUTO_DRIVE_DIST); //test drive 180.5 inches
    
    if(!myShooterArmPID->IsEnabled())
      {
        myShooterArmPID->Enable();
      }
    //make sure the pid is enabled
    
    myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_SHOOTING);
    //set the target POT value in the shooting position
    
    while(!myShooterArmPID->OnTarget())
      {

      }
    myShooterArmPID->Disable();
    //do nothing until shooter is in position and then disable the PID
    myShooter->SetLoaderBarSpeed(0.0);
    
    myShooter->MoveLoaderDown(!Consts::LOADER_PISTON_EXTENDED);
    //moves the loader bar up
    
    myShooter->SetJagSpeed(myShooter->ShooterSpeed());
    Wait(2.0);
    //wait for jags to get up to speed

    myShooter->Fire();
    myShooter->SetJagSpeed(0.0);
    //turn off the jags after firing
    
    myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED);
    //move loader back down
    
    if(!myShooterArmPID->IsEnabled())
      {
        myShooterArmPID->Enable();
      }
    myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_LOADING);
    //move shooter arm back to loading position
    
    while(!myShooterArmPID->OnTarget())
      {

      }
    myShooterArmPID->Disable();
    //turn off pid once in shooting position
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


        /***********************************************************
         *                         LOADER OVERRIDE
         ***********************************************************/
        if (stick.GetRawButton(Consts::S_LOADER_DOWN_BUTTON))
          {
            myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED);
          }
        else if (stick.GetRawButton(Consts::S_LOADER_UP_BUTTON))
          {
            myShooter->MoveLoaderDown(!Consts::LOADER_PISTON_EXTENDED);
          }
        
        /************************************************************
         *                          ACTION STATES
         ************************************************************/

        if (tartarus.GetRawButton(Consts::PREP_LOAD))
          {
            myShooter->SetJagSpeed(0.0);
            myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED);
            if(!myShooterArmPID->IsEnabled())
              {
                myShooterArmPID->Enable();
              }
            myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_LOADING);
            

          }
        else if (tartarus.GetRawButton(Consts::PREP_SHOOT))
          {
            
            myShooter->SetJagSpeed(Consts::SHOOTER_LOAD_SPEED);
            if(!myShooterArmPID->IsEnabled())
              {
                myShooterArmPID->Enable();
              }
            myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_SHOOTING);
            myShooter->MoveLoaderDown(!Consts::LOADER_PISTON_EXTENDED);
           

          }


/*****************************************************************
 *                               SHOOTER OVERRIDE
 ****************************************************************/


        if (stick.GetRawButton(Consts::S_SHOOTER_UP_BUTTON))
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
        else if ((stick.GetRawButton(Consts::S_SHOOTER_DOWN_BUTTON)))
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
            if(!myShooter->IsLoaderDown() && !stick.GetRawButton(Consts::CATCH_BUTTON)) {
                myShooter->SetJagSpeed(myShooter->ShooterSpeed());
                ds->Printf(DriverStationLCD::kUser_Line4, 1, "somethingsomething");
            }
          }
        else
          {
            myShooterArmPID->Enable();
          }
        ds->Printf(DriverStationLCD::kUser_Line3, 1,"armPOT: %d \%", armPOT->GetAverageValue());
        ds->UpdateLCD();


        /**********************************
         * MANUAL CAGE CONTROL
         **********************************/

     /*   if (stick.GetRawButton(Consts::SHOOTER_SWEET_SPOT_UP)) //move up
          {
            myShooterArmPID->Disable();
            myShooter->MoveShooter(Consts::CAGE_MOVE_SPEED);
          }
        else if (stick.GetRawButton(Consts::SHOOTER_SWEET_SPOT_DOWN))
          {
            myShooterArmPID->Disable();
            myShooter->MoveShooter(-Consts::CAGE_MOVE_SPEED);
          }
          */
        
        myShooter->Run(); //runs shooter wheels, torshooter code will handle firing as well
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
