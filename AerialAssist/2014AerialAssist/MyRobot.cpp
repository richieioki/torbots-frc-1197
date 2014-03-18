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

    wheelEncoderLeft = new Encoder(1,Consts::LEFT_ENCODER_MSB,1, Consts::LEFT_ENCODER_LSB); //a channel, b channel
    wheelEncoderRight = new Encoder(1,Consts::RIGHT_ENCODER_MSB,1, Consts::RIGHT_ENCODER_LSB); //a channel, b channel
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


    /*************************************************************
     *                   AUTO STAGE 1: LOADING
     *************************************************************/ 

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

    ds->Printf(DriverStationLCD::kUser_Line5, 1, "Encoder Left: %d", wheelEncoderLeft->GetRaw());
    ds->UpdateLCD();
    myTorbotDrive->DriveStraight(Consts::AUTO_DRIVE_SPEED, Consts::AUTO_DRIVE_DIST); //test drive 180.5 inches

    /************************************************************
     *                  AUTO STAGE 2: SHOOTING
     ************************************************************/

    if(!myShooterArmPID->IsEnabled()) //make sure the pid is enabled
      {
        myShooterArmPID->Enable();
      }

    myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_SHOOTING); //set the target POT value in the shooting position

    while(!myShooterArmPID->OnTarget())
      {

      }
    myShooterArmPID->Disable(); //do nothing until shooter is in position and then disable the PID


    myShooter->SetLoaderBarSpeed(0.0);

    myShooter->MoveLoaderDown(!Consts::LOADER_PISTON_EXTENDED); //moves the loader bar up

    myShooter->SetJagSpeed(myShooter->ShooterSpeed());
    Wait(Consts::AUTO_JAG_WAIT_TIME); //wait for jags to get up to speed

    myShooter->Fire();
    myShooter->SetJagSpeed(0.0); //turn off the jags after firing

    /*****************************************************************
     *                  AUTO STAGE 3: AMERICA
     *****************************************************************/
    
    ds->Clear();
    ds->Printf(DriverStationLCD::kUser_Line1, 1, "AMERICA");
    ds->UpdateLCD();
    

    /*****************************************************************
     *                  AUTO STAGE 4: LOADING POSITION
     *****************************************************************/

    myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED); //move loader back down

    if(!myShooterArmPID->IsEnabled())
      {
        myShooterArmPID->Enable();
      }
    myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_LOADING); //move shooter arm back to loading position

    while(!myShooterArmPID->OnTarget())
      {

      }
    myShooterArmPID->Disable(); //turn off pid once in loading position

  }

  void OperatorControl()
  {

    float shooterSpeed;

    while (IsOperatorControl() && IsEnabled())
      {

        /*********************************************************
                                  DS INFO
         *********************************************************/
        shooterSpeed = myShooter->ShooterSpeed();
        ds->Clear();
        ds->Printf(DriverStationLCD::kUser_Line5, 1, "LE: %d", wheelEncoderLeft->GetRaw());
        ds->Printf(DriverStationLCD::kUser_Line4, 1, "RE: %d", wheelEncoderRight->GetRaw());
        ds->Printf(DriverStationLCD::kUser_Line3, 1,"armPOT: %d \%", armPOT->GetAverageValue());
        ds->Printf(DriverStationLCD::kUser_Line6, 1, "Gyro: %f", gyro->GetAngle());        
        ds->Printf(DriverStationLCD::kUser_Line2, 1, "Shooter Speed: %f.0 \%", shooterSpeed * 100);
        ds->UpdateLCD();




        /*********************************************************
                                  DRIVING
         *********************************************************/

        //        ds->Printf(DriverStationLCD::kUser_Line1, 1, "Test");
        //        ds->UpdateLCD();
        if(stick.GetRawButton(Consts::REVERSE_DRIVE))
          {
            myTorbotDrive->ReverseArcadeDrive(true);
          }

        else {
            myTorbotDrive->ArcadeDrive(true);
        }
        /*********************************************************
                                  SHIFTING
         *********************************************************/
        myTorbotDrive->shiftGear(tartarus.GetRawButton(Consts::SHIFT_BUTTON));


        /***********************************************************
         *                         LOADER OVERRIDE
         ***********************************************************/
//        if (stick.GetRawButton(Consts::S_LOADER_DOWN_BUTTON))
//          {
//            myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED);
//          }
//        else if (stick.GetRawButton(Consts::S_LOADER_UP_BUTTON))
//          {
//            myShooter->MoveLoaderDown(!Consts::LOADER_PISTON_EXTENDED);
//            myShooter->LoaderDownOverride(true);
//          }
       
            myShooter->LoaderDownOverride(stick.GetRawButton(Consts::S_LOADER_UP_BUTTON));
          

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
         *                      SHOOTER OVERRIDE
         ****************************************************************/


        if (stick.GetRawButton(Consts::S_SHOOTER_UP_BUTTON)) //put cage in firing position
          {
            if(!myShooterArmPID->IsEnabled())
              {
                myShooterArmPID->Enable();
              }
            myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_SHOOTING); 
          }
        else if ((stick.GetRawButton(Consts::S_SHOOTER_DOWN_BUTTON))) //put cage in loading position
          {

            if(!myShooterArmPID->IsEnabled())
              {
                myShooterArmPID->Enable();
              }
            myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_LOADING);
          }


        if (myShooterArmPID->OnTarget())
          {
            myShooterArmPID->Disable();
            if(!myShooter->IsLoaderDown() && !stick.GetRawButton(Consts::CATCH_BUTTON)) 
              {
                myShooter->SetJagSpeed(myShooter->ShooterSpeed());
              }
          }
        else
          {
            myShooterArmPID->Enable();
          }

        myShooter->Run(); //runs shooter wheels, torshooter code will handle firing as well
      }

  }

  /**
   * Runs during test mode
   */
  void Test() 
  {
    gyro->Reset();
    myShooterArmPID->Disable();
    ds->Clear();
    ds->Printf(DriverStationLCD::kUser_Line1, 1, "Gyro: %f", gyro->GetAngle());
    ds->Printf(DriverStationLCD::kUser_Line2, 1, "POT: %d", armPOT->GetAverageValue());
    ds->Printf(DriverStationLCD::kUser_Line3, 1, "LE: %d", wheelEncoderLeft->GetRaw());
    ds->Printf(DriverStationLCD::kUser_Line4, 1, "RE: %d", wheelEncoderRight->GetRaw());
    ds->UpdateLCD();
    myTorbotDrive->DriveStraight(0.5f, 26.75f); //drive length of robot
    Wait(1.5);
    myTorbotDrive->DriveStraight(0.5f, -26.75f);
    Wait(1.5);
    myTorbotDrive->DriveToTheta(gyro->GetAngle() + 90.0, 0.5f, 0.0f);
    Wait(1.5);
    myTorbotDrive->DriveToTheta(gyro->GetAngle() - 180.0, 0.5f, 0.0f);
    Wait(1.5);
    myShooterArmPID->Enable();
    myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_SHOOTING);
    myShooter->MoveLoaderDown(!Consts::LOADER_PISTON_EXTENDED);
    while(!myShooterArmPID->OnTarget())
      {
        
      }
    myShooterArmPID->Disable();
    myShooter->SetJagSpeed(myShooter->ShooterSpeed());
    Wait(2.0);
    myShooter->Fire();
    Wait(0.5);
    myShooter->SetJagSpeed(0.0);
    myShooterArmPID->SetSetpoint(Consts::SHOOTER_ARM_LOADING);
    myShooter->MoveLoaderDown(Consts::LOADER_PISTON_EXTENDED);
    myShooterArmPID->Enable();
    while(!myShooterArmPID->OnTarget())
      {
            
      }
    myShooterArmPID->Disable();
    
  }

};

START_ROBOT_CLASS(RobotDemo);

/* 

 *   *   *   *   *   * ||||||||||||||||||||||||||||||||
   *   *   *   *   *   ||||||||||||||||||||||||||||||||
 *   *   *   *   *   * 
   *   *   *   *   *   ||||||||||||||||||||||||||||||||
 *   *   *   *   *   * ||||||||||||||||||||||||||||||||
   *   *   *   *   *   
 *   *   *   *   *   * ||||||||||||||||||||||||||||||||
   *   *   *   *   *   ||||||||||||||||||||||||||||||||
 *   *   *   *   *   * 
 |||||||||||||||||||||||||||||||||||||||||||||||||||||||
 |||||||||||||||||||||||||||||||||||||||||||||||||||||||

 |||||||||||||||||||||||||||||||||||||||||||||||||||||||
 |||||||||||||||||||||||||||||||||||||||||||||||||||||||

 |||||||||||||||||||||||||||||||||||||||||||||||||||||||
 |||||||||||||||||||||||||||||||||||||||||||||||||||||||

 |||||||||||||||||||||||||||||||||||||||||||||||||||||||
 |||||||||||||||||||||||||||||||||||||||||||||||||||||||


O say can you see, by the dawn’s early light,
What so proudly we hail’d at the twilight’s last gleaming,
Whose broad stripes and bright stars through the perilous fight
O’er the ramparts we watch’d were so gallantly streaming?
And the rocket’s red glare, the bombs bursting in air,
Gave proof through the night that our flag was still there,
O say does that star-spangled banner yet wave
O’er the land of the free and the home of the brave?

  ***   **   **  *****  ***   *****   ****   ***
 *   *  * * * *  *      *  *    *    **     *   *
 *****  *  *  *  ***    ***     *    *      *****
 *   *  *     *  *      * *     *    **     *   *
 *   *  *     *  *****  *  *  *****   ****  *   *
 
 */
