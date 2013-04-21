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
#include "TorTargetAcquire.h"
#include "TorAuto.h"
#include "TorShooter.h"
#include "TorFeeder.h"
#include "TorClimber.h"
#include "TorPicker.h"
#include "SpeedCounter.h"
#include "TorTest.h"

//AxisCamera &camera = AxisCamera::GetInstance();
DriverStationLCD *ds;


/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */
class RobotDemo : public SimpleRobot
{
  //  RobotDrive myRobot; // robot drive system
  Joystick stick1;         // joystick 1
  Joystick stick2;        // joystick 2

  Timer *timer;

  // Drive train
  Jaguar *leftDriveJag;
  Jaguar *leftDriveJag2;
  Jaguar *rightDriveJag;
  Jaguar *rightDriveJag2;
  TorJagDrive *myJagDrive;
  TorbotDrive *myTorbotDrive;
  Encoder *wheelEncoderLeft;
  Encoder *wheelEncoderRight;


  // Shooter
  TorShooter *myShooter;
  TorTargetAcquire* myTorTarget;
  Jaguar *myShooterArmJag;
  AnalogChannel *myShooterArmPOT;
  //  Jaguar *shooterJagFront;
  //  Jaguar *shooterjagBack;


  // Feeder
  TorFeeder *myFeeder;

  // Climber
  TorClimber *myClimber;
  //DigitalInput *reedSwitch;

  // Picker
//  TorPicker *myPicker;

  // Navigation
  Gyro *gyro;

  // Autonomous
//  TorAuto *myAuto;

//  AnalogChannel *irSensor;

  //PIDS
  PIDController *myShooterArmPID;
  PIDController *turnPID;
  DrivePIDOutput* rotatingPIDOut;

  //compressor
  Compressor *compressor;

  DriverStation *driverStation;
  //more to come!


public:
  RobotDemo(void):
    //                myRobot(8,7,10,9),    // these must be initialized in the same order
    stick1(Consts::STICK1_PORT),                               // as they are declared above.
    stick2(Consts::STICK2_PORT)
  {
    ds = DriverStationLCD::GetInstance();
      driverStation = DriverStation::GetInstance;
      
    timer = new Timer();
    timer->Reset();

    //compressor
    compressor = new Compressor(Consts::PRESSURE_SWITCH_MOD,Consts::PRESSURE_SWITCH_CHANNEL,
        Consts::COMPRESSOR_RELAY_MOD,Consts::COMPRESSOR_RELAY_CHANNEL); //change ports for actual bot
    compressor->Start();

    //analog module 1
    gyro = new Gyro(Consts::GYRO_MOD, Consts::GYRO_CHANNEL);
    gyro->Reset();


    // Wheel Encoders
    wheelEncoderLeft = new Encoder(Consts::LEFT_ENCODER_MOD,Consts::LEFT_ENCODER_MSB,Consts::LEFT_ENCODER_MOD,Consts::LEFT_ENCODER_LSB); //a channel, b channel
    wheelEncoderRight = new Encoder(Consts::RIGHT_ENCODER_MOD,Consts::RIGHT_ENCODER_MSB,Consts::RIGHT_ENCODER_MOD,Consts::RIGHT_ENCODER_LSB); //a channel, b channel

    // drive Jags
    leftDriveJag = new Jaguar(Consts::LEFT1_DRIVE_JAG_MOD, Consts::LEFT1_DRIVE_JAG);    // madera used channel 4
    leftDriveJag2 = new Jaguar(Consts::LEFT2_DRIVE_JAG_MOD, Consts::LEFT2_DRIVE_JAG);
    rightDriveJag = new Jaguar(Consts::RIGHT1_DRIVE_JAG_MOD, Consts::RIGHT1_DRIVE_JAG);
    rightDriveJag2 = new Jaguar(Consts::RIGHT2_DRIVE_JAG_MOD, Consts::RIGHT2_DRIVE_JAG);

    //Jagdrive object gets the 4 jags used for this robot
//    myJagDrive = new TorJagDrive(*leftDriveJag, *rightDriveJag);      // live bot 2 jags
    myJagDrive = new TorJagDrive(*leftDriveJag, *leftDriveJag2, *rightDriveJag, *rightDriveJag2);       // protobot 4 jags
       // Torbot Drive object uses stick, jagdrive, gyro and wheelencoder
    myTorbotDrive = new TorbotDrive(stick1, *myJagDrive, *gyro, *wheelEncoderRight);

    //    // feeder object
    myFeeder = new TorFeeder(*ds);
    //
    //     shooter object
    myShooterArmJag = new Jaguar(Consts::SHOOTER_ARM_JAG_MOD, Consts::SHOOTER_ARM_JAG);
    myShooterArmPOT = new AnalogChannel(Consts::SHOOTER_ARM_POT);
    myShooter = new TorShooter(stick1, stick2, *myTorbotDrive, *myFeeder, *myShooterArmPOT, *ds);          // shooter gets stick and feeder objects passed in

    //    // picker object
    //    myPicker = new TorPicker(stick1, *myTorbotDrive, *myFeeder);
    //
    //    // climber object
    myClimber = new TorClimber(stick1, *myTorbotDrive, *myShooter, *ds);
    //
    //
    //PIDs
    myShooterArmPID = new PIDController(0.0225,0.0007,0.0, myShooterArmPOT, myShooterArmJag, 0.05);     // PID controller for moving the shooter
    myShooterArmPID->SetContinuous(false);
    //myShooterArmPID->SetInputRange(0,1000);
    myShooterArmPID->SetOutputRange(-1.0,1.0); //gravity helps on the way down
    //    // init shooter arm setpoint to its current position so it does not move initially when we enable PID
    myShooterArmPID->SetSetpoint(myShooterArmPOT->GetAverageValue());                 // set point is where it is when started, down

    myShooterArmPID->Enable();

    rotatingPIDOut = new DrivePIDOutput(*myJagDrive, DriveMode::Rotating); // PID controller for turning
    turnPID = new PIDController(0.03, 0.003, 0.0, gyro, rotatingPIDOut, 0.05);
    //    turnPID->SetOutputRange(-1.0,1.0);
    turnPID->SetOutputRange(-0.75f,0.75f);

//    SmartDashboard::init();
  }


  //*********************************************************************************************
  // ShooterArmManualControl - Joystick2 Y-axis
  //*********************************************************************************************

  void RobotDemo::ShooterArmManualControl()
  {
    float stickY;
    double motionValue = 0.0;
    double setpoint;

    //    if(fabs(stick2.GetY()/2) < .25) {
    //        myShooterArmJag->Set(0.0);
    //    } else {
    //        myShooterArmJag->Set(stick2.GetY()/2);
    //    }

    // don't move arm if driver is shooting (either trigger)
    if (!stick1.GetRawButton(Consts::TRIGGER_BUTTON) && !stick2.GetRawButton(Consts::MANUAL_TRIGGER_BUTTON))
      {
        if (stick2.GetRawButton(6))
          {
            myShooterArmPID->SetOutputRange(-0.5, 0.5);
            myShooter->SetArmPosition(TorShooter::Loading);                                   // loading position
            myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
          }
        else if (stick2.GetRawButton(7))
          {
            myShooterArmPID->SetOutputRange(-0.5, 0.5);
            myShooter->SetArmPosition(TorShooter::Straight);                                   // Straight ahead position
            myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
          }
        else if (stick2.GetRawButton(10))
          {
            myShooterArmPID->SetOutputRange(-0.5, 0.5);
            myShooter->SetArmPosition(TorShooter::Targeting);                                   // Targeting position
            myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
          }
        else if (stick2.GetRawButton(11))
          {
            myShooterArmPID->SetOutputRange(-0.5, 0.5);
            myShooter->SetArmPosition(TorShooter::Climbing);                                   // Climbing position
            myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
          }
        else if (stick2.GetRawButton(9)){                                                       // nudge feeder up and down
            myFeeder->nudge();
        }
        else
          {
            // check joystick 2, Y axis for manual control
            stickY = stick2.GetY();
            if (fabs(stickY) > Consts::stickDeadZone)
              {
                motionValue = (int)(stickY * 30);
                setpoint = myShooter->GetArmPosition() + motionValue;
                // make sure the setpoint doesn't exceed the loading or climbing position
                if (setpoint <= Consts::SHOOTER_ARM_LOADING)
                  setpoint = Consts::SHOOTER_ARM_LOADING;
                if (setpoint >= Consts::SHOOTER_ARM_CLIMBING)
                  setpoint = Consts::SHOOTER_ARM_CLIMBING;
                
                
                myShooterArmPID->SetOutputRange(-1.0,1.0);
                myShooterArmPID->SetSetpoint(setpoint);

                //            ds->Printf(DriverStationLCD::kUser_Line2, 1, "j2Y: %5.3f mv: %5.3f", stickY, motionValue);
                //ds->Printf(DriverStationLCD::kUser_Line3, 1, "Pot: %5.1f Tgt: %5.1f", myShooter->GetArmPosition(), setpoint);
                //            ds->UpdateLCD();

//                Wait(0.05);
              } // stickY value outside of deadzone
          } // stick control

      } // no trigger pressed

    //    ds->Printf(DriverStationLCD::kUser_Line4, 1, "pVal: %d Er:%5.3f", myShooterArmPOT->GetAverageValue(), myShooterArmPID->GetError());
    //    ds->Printf(DriverStationLCD::kUser_Line5,1,"PotSet: %5.1f Out: %5.1f",myShooterArmPID->GetSetpoint(), myShooterArmPID->Get());
    ds->UpdateLCD();

  } // end ShooterArmManualControl



  //*********************************************************************************************
  // TurnToTheta turns the robot a specified number of degrees using PID
  //*********************************************************************************************
  float RobotDemo::TurnToTheta(float destTheta, bool waitFinish)
  {
    double targetAngle = destTheta;
    float motorSpeed = 0.5;                // turn at 0.5 speed, easier to hit our marks if we move less than 100%
    float pValue;
    
    gyro->Reset();
    targetAngle += gyro->GetAngle();      // add the current angle to the target angle to adjust for any gyro error
      ds->Printf(DriverStationLCD::kUser_Line1, 1, "st g:%4.2f tgt:%4.2f", gyro->GetAngle(), targetAngle);
      ds->UpdateLCD();
    //
    
    turnPID->SetSetpoint(targetAngle);
    turnPID->SetContinuous(false);
    //          turnPID->SetInputRange(-(fabs(targetAngle)), fabs(targetAngle));
    turnPID->SetOutputRange(-motorSpeed, motorSpeed);                                   // caps PID output to motorSpeed, both directions
    //  //turnPID->SetAbsoluteTolerance(gyroPIDTolerance);                              // tolerance is only used if you use OnTarget() method
    turnPID->SetPercentTolerance(5.0);
    //


    // set the P based on the size of the destTheta
    if (fabs(destTheta) > 16.0)               // outside of normal targeting angle, these will be larger turns in autonomous
      pValue = 0.03;
    else
      pValue = 0.06;

    turnPID->SetPID(pValue, turnPID->GetI(), turnPID->GetD());

    // shift to low drive gear
    myTorbotDrive->shiftGear(true);

    turnPID->Enable();
    //
    //  // if waitFinish is true, wait until we get to within our tolerance of the setpoint
    timer->Stop();
    timer->Reset();
    timer->Start();
    //while (waitFinish && (fabs(gyro->GetAngle()) < (fabs(destTheta)*(0.95))))  // 5% tolerance TODO
    while (waitFinish && !turnPID->OnTarget())  // 5% tolerance TODO
      {

//        ds->Printf(DriverStationLCD::kUser_Line2, 1, "gyro: %4.2f %4.2f", gyro->GetAngle(), targetAngle);
//        ds->UpdateLCD();
        Wait(0.01);
        if (timer->Get() >= 3.0)        // if we are waiting to finish (waitFinish=true) break out if the turn takes more than 3 seconds
          {                             // possible obstruction blocking the turn from completing.
            break;                      
          }
      }
    timer->Stop();
    timer->Reset();
    // if we're waiting for the turn to finish, disable PID here, otherwise, leave it up to the calling method
    if (waitFinish)
      turnPID->Disable();

    // shift back to high drive gear
    myTorbotDrive->shiftGear(false);

    return targetAngle;
  }


  //*********************************************************************************************
  // AutoSearchForTarget
  //*********************************************************************************************

  bool RobotDemo::AutoSearchForTarget()
  {
    int state = 0;
    int vInc = 30;
    float hInc = 15.0;                                                                          // degrees to search
    bool found = false;

    while (!found && IsAutonomous() && IsEnabled())
      {
        TurnToTheta (135.0, false);                             //If no target is found, turn to 135 degrees to look for target
        Wait (1.0); 
        turnPID->Disable();
        if (myShooter->Target())
          found = true;

        switch (state)
        {
        case (0):       // look up a bit
                    //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+vInc);                         // look up
            if (myShooter->Target())
              found = true;
            else
              {
                //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-vInc);                     // reset
                state += 1;
              }
        break;


        case (1):       // look down a bit
                     //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-vInc);         // look down
            if (myShooter->Target())
              found = true;
            else
              {
                state += 1;
                //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+vInc);                     // reset
              }
        break;

        case (2):       // turn one way
         //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+hInc);         // look right
                if (myShooter->Target())
                  found = true;
                else
                  {
                    state += 1;
                    //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-hInc);                     // reset
                  }
        break;

        case (3):
         //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-hInc);         // look left
                if (myShooter->Target())
                  found = true;
                else
                  {
                    state += 1;
                    //myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+hInc);                     // reset
                  }
        break;

        case (4):
                                // look up a bit
                                break;

        } // switch state

      } // While

    return found;

  } // AutoSearchForTarget



  //*********************************************************************************************
  // Autonomous
  //*********************************************************************************************

  void Autonomous(void)
  {

    myShooterArmPID->Disable();
    myShooterArmPID->SetSetpoint(myShooterArmPOT->GetAverageValue());                 // set point is where it is when started, down
    myShooterArmPID->Enable();
    myTorbotDrive->resetEncoder();

    ds->Clear();
    

        int mode = 0;
          
        for (int i = 1; i <= 4; i++)
          {
            mode = !driverStation->GetDigitalIn(i) ? i : mode;
            driverStation->SetDigitalOut(i, !driverStation->GetDigitalIn(i));
          }
        ds->Printf(DriverStationLCD::kUser_Line1, 1, "auto mode: %d", mode);
        ds->UpdateLCD();

        
    switch (mode)
    {
    case (1): // first tick on driver control dial

     // tell robot it has 4 disks, actually 3, but we'll fire one extra time just in case of a misfire
     //    myFeeder->InitalizeAuto(4);

     // move 24 inches forward, raise arm an move another 24 inches to get into firing position
     //    myTorbotDrive->DriveStraight(0.5, 24.0);
            
     // move the arm to targeting position once the robot has cleared the pyramid bar
     //    myShooter->SetArmPosition(TorShooter::Targeting);                                   // target position
     //    myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());                   // move arm to target

     // move the rest of the distance forward while the arm moves up
     //    myTorbotDrive->DriveStraight(0.5, 24.0);

     //    Wait(3.0);  // wait for camera to adjust to lighting
     //
     //    if (myShooter->Target()) // any target has been found, does not look for specific targets. Closest to center of camera view is targeted
     //      {
     //        myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());                   // move arm to target
     //        TurnToTheta(myShooter->GetTurnSetPoint(), true);                                // set robot turn setpoint and wait for it to reach position
     //        Wait (1.0);                                                                 // give a little extra time for arm to settle in at setpoint
     //        myShooter->Start();
     //        while ((myFeeder->getNumDisks() > 0) && IsAutonomous() && IsEnabled())
     //          {
     //            myShooter->Shoot();
     //          }
     //        myShooter->Stop();
     //
     //        turnPID->Disable ();
     //      }

    break;



    case (2): // second tick on driver control dial                                      
    //    // drive out from right side of pyramid, turn left and shoot    
    //    
    //    ds->Printf(DriverStationLCD::kUser_Line2, 1, "Out, turn shoot");
    //    ds->UpdateLCD();
    //
    //    // init 4 disks in feeder; really only 3 but we'll shoot max number of times in case of jam
    //    myFeeder->InitalizeAuto(4);
    //
    //    break;
    //
    //    
    //    
    //    
    //    
    
        
        
    case (3):  // third tick on driver control dial
    //    ds->Printf(DriverStationLCD::kUser_Line2, 1, "Auto: just shoot...");
    //    ds->UpdateLCD();
    //    
    //    // init 4 disks in feeder; really only 3 but we'll shoot max number of times in case of jam
    //    myFeeder->InitalizeAuto(4);
    //
    //    break;
    //  
    //
            
            
    case (4): // fourth tick on driver control dial
    //        // simple test of small and larger turns to aid in tuning PID
    //
    //    12 inches forward


    break;
    
    } // end switch
    


  // disable arm PID before ending
    myShooterArmPID->Disable();

  
  } // end of auto




  //*********************************************************************************************
  // OperatorControl
  //*********************************************************************************************

  void OperatorControl(void)
  {    
    // if enabled, init PID, encoders, gyro and compressor
    myShooterArmPID->Disable();
    myShooterArmPID->SetSetpoint(myShooterArmPOT->GetAverageValue());                 // set point is where it is when started, down
    myShooterArmPID->Enable();

    myTorbotDrive->resetEncoder();
    gyro->Reset();

    compressor->Start();
    turnPID->Disable();         // in case it was enabled and left on in auto, disable so we can drive away

    //myFeeder->InitalizeAuto(0); // testing mode: init 4 disks in feeder so we can target and shoot
    // reset feeder to load position
    myFeeder->resetFeederToLoad();

    ds->Clear();

    // operator loop
    while (IsOperatorControl() && IsEnabled())
      {

        myTorbotDrive->ArcadeDrive(true);                                                // true for squared inputs, false for linear


        //        if(stick1.GetRawButton(7)) {
        //            myClimber->pullDown();
        //        }
        //        

        // Shooter
        myShooter->Run();                                                               // check joysticks and run shooter mode
        if (myShooter->GetState() == TorShooter::StartMove)
          {

            myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
            TurnToTheta(myShooter->GetTurnSetPoint(), true);                            // set robot turn setpoint and wait for it to reach position
          }
        else if ((myShooter->GetState() == TorShooter::Init) && (turnPID->IsEnabled())) // if done shooting and we had turned on PID, turn it off and drive away
          { 
            turnPID->Disable();
          }

        ////        // Manual Shooter Arm Movement
        ShooterArmManualControl();                                                      // stick2 Y-axis controls shooter arm motion manually

        // Feeder
        myFeeder->checkDiskLoader();                                                // check the feeder load slot for new disks, could be from picker or human
        //        ds->Printf(DriverStationLCD::kUser_Line3, 1, "Snsr: %d", myFeeder->getFeederSensor());

        //        // Climber
        //          myClimber->Run();


        if (stick1.GetRawButton(3))
          {
            myFeeder->resetDisks();
          }
        ds->Printf(DriverStationLCD::kUser_Line2, 4, "Disks: %d", myFeeder->getNumDisks());
        ds->Printf(DriverStationLCD::kUser_Line5,4, "gyro: %f",gyro->GetAngle());
        ds->UpdateLCD();


      } // end isOperatorControl loop



    // disable arm PID before ending
    myShooterArmPID->Disable();

  } // end OperatorControl method


    void Disabled()
    {
      ds->Clear();
      ds->Printf(DriverStationLCD::kUser_Line4, 1, "Disabled");
      ds->UpdateLCD();
      
      myShooterArmPID->Disable();
      
    }

  //*********************************************************************************************
  // Test
  //*********************************************************************************************

  void Test() {


  } // end test

};

START_ROBOT_CLASS(RobotDemo);

