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
//    RobotDrive myRobot; // robot drive system
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
//  
//
//  // Shooter
//  TorShooter *myShooter;
//  TorTargetAcquire* myTorTarget;
  Jaguar *myShooterArmJag;
//  AnalogChannel *myShooterArmPOT;
//  Jaguar *shooterJagRear;
//  Jaguar *shooterJagFront;
//
//
//  // Feeder
//  TorFeeder *myFeeder;
//  Jaguar *elevatorJag;
//
//  // Climber
//  TorClimber *myClimber;
//  //DigitalInput *reedSwitch;
//
//  // Picker
//  TorPicker *myPicker;
//
//  // Navigation
//  Gyro *gyro;
//
//  // Autonomous
//  TorAuto *myAuto;
//
//  AnalogChannel *irSensor;
//
//  //PIDS
//  PIDController *myShooterArmPID;
//  PIDController *turnPID;
//  DrivePIDOutput* rotatingPIDOut;
//
//  //compressor
  Compressor *compressor;
//
  DriverStation *driverStation;
//  //more to come!


public:
  RobotDemo(void):
    //                myRobot(8,7,10,9),    // these must be initialized in the same order
    stick1(Consts::STICK1_PORT),                               // as they are declared above.
    stick2(Consts::STICK2_PORT)
  {
    ds = DriverStationLCD::GetInstance();
//
////    timer = new Timer();
////    timer->Reset();
//
//    //compressor
    compressor = new Compressor(Consts::PRESSURE_SWITCH_MOD,Consts::PRESSURE_SWITCH_CHANNEL,
        Consts::COMPRESSOR_RELAY_MOD,Consts::COMPRESSOR_RELAY_CHANNEL); //change ports for actual bot
    compressor->Start();

//    //analog module 1
//    gyro = new Gyro(Consts::GYRO_MOD, Consts::GYRO_CHANNEL);
//    gyro->Reset();
//
//
//    //digital module
//    	wheelEncoderLeft = new Encoder(2,5,2,6); //a channel, b channel
//    	wheelEncoderRight = new Encoder(1,3,1,4); //a channel, b channel
//    	wheelEncoderRight = new Encoder(2,8,2,9); //a channel, b channel    "8" is changed
//    	wheelEncoderLeft = new Encoder(Consts::LEFT_ENCODER_MOD,Consts::LEFT_ENCODER_MSB,Consts::LEFT_ENCODER_MOD,Consts::LEFT_ENCODER_LSB); //a channel, b channel
//    	wheelEncoderRight = new Encoder(Consts::RIGHT_ENCODER_MOD,Consts::RIGHT_ENCODER_MSB,Consts::RIGHT_ENCODER_MOD,Consts::RIGHT_ENCODER_LSB); //a channel, b channel
//      
//    // drive Jags
    	//leftDriveJag = new Jaguar(1, 8);
    	//leftDriveJag2 = new Jaguar(1, 7);
    	//rightDriveJag = new Jaguar(1, 10);
    	//rightDriveJag2 = new Jaguar(1, 9);
        leftDriveJag = new Jaguar(Consts::LEFT1_DRIVE_JAG_MOD, Consts::LEFT1_DRIVE_JAG);
       // leftDriveJag2 = new Jaguar(Consts::LEFT2_DRIVE_JAG_MOD, Consts::LEFT2_DRIVE_JAG);
        rightDriveJag = new Jaguar(Consts::RIGHT1_DRIVE_JAG_MOD, Consts::RIGHT1_DRIVE_JAG);
       // rightDriveJag2 = new Jaguar(Consts::RIGHT2_DRIVE_JAG_MOD, Consts::RIGHT2_DRIVE_JAG);
//
//    //Jagdrive object gets the 4 jags used for this robot
    myJagDrive = new TorJagDrive(*leftDriveJag, *rightDriveJag);  // live bot
 //   myJagDrive = new TorJagDrive(*leftDriveJag, *leftDriveJag2, *rightDriveJag, *rightDriveJag2);	// proto bot
//    // Torbot Drive object uses stick, jagdrive, gyro and wheelencoder
//    myTorbotDrive = new TorbotDrive(stick1, *myJagDrive, *gyro, *wheelEncoderRight);
//
//////    // feeder object
//////    myFeeder = new TorFeeder();
//    //elevatorJag = new Jaguar(Consts::FEEDER_ELEVATOR_MOD, Consts::FEEDER_ELEVATOR_JAG);
//    //elevatorPot = new AnalogChannel(Consts::Feede)
//////
//////    // shooter object
//        myShooterArmJag = new Jaguar(Consts::SHOOTER_ARM_JAG_MOD, Consts::SHOOTER_ARM_JAG);
//////    myShooterArmJag = new Jaguar(3);
////        shooterJagRear = new Jaguar(Consts::SHOOTER_REAR_MOD, Consts::SHOOTER_REAR_JAG);
////        shooterJagFront = new Jaguar(Consts::SHOOTER_FRONT_MOD, Consts::SHOOTER_FRONT_JAG);
////    myShooterArmPOT = new AnalogChannel(Consts::SHOOTER_ARM_LPOT);
////    myShooter = new TorShooter(stick1, stick2, *myTorbotDrive, *myFeeder, *myShooterArmPOT, *ds);          // shooter gets stick and feeder objects passed in
//////
//////    // picker object
//////    myPicker = new TorPicker(stick1, *myTorbotDrive, *myFeeder);
//////
//////    // climber object
//////    myClimber = new TorClimber(stick1, *myTorbotDrive, *myShooter);
//////
//////
//////    //PIDs
//////    myShooterArmPID = new PIDController(0.02,0.0,0.0, myShooterArmPOT, myShooterArmJag, 0.1);     // PID controller for moving the shooter
//////    myShooterArmPID->SetContinuous(true);
//////
//////    // init shooter arm to look straight ahead
////////    myShooter->SetArmPosition(TorShooter::Straight);                                   // initial position
////////    myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
//////      myShooterArmPID->SetSetpoint(myShooterArmPOT->GetAverageValue());
////////      myShooterArmPID->Enable();
//////
////    rotatingPIDOut = new DrivePIDOutput(*myJagDrive, DriveMode::Rotating); // PID controller for turning
////////    turnPID = new PIDController(0.006, 0.0, 0.0, gyro, rotatingPIDOut, 0.0005);
////    turnPID = new PIDController(0.015, 0.0, 0.0, gyro, rotatingPIDOut, 0.0005);
////    turnPID->SetOutputRange(0.5f,0.5f);
//
//
//    //driver station (actual, not the display)
    driverStation = DriverStation::GetInstance();
//
//    SmartDashboard::init();
  }


  //*********************************************************************************************
  // ShooterArmManualControl - Joystick2 Y-axis
  //*********************************************************************************************

//  void RobotDemo::ShooterArmManualControl()
//  {
//    float stickY;
//    int motionValue = 0;
//
//    if (!stick1.GetRawButton(Consts::TRIGGER_BUTTON) && !stick2.GetRawButton(Consts::MANUAL_TRIGGER_BUTTON))
//      {
//        stickY = stick2.GetY();
//        if (fabs(stickY) > Consts::stickDeadZone)
//          {
//            motionValue = (int)(stickY * 40);
//            myShooterArmPID->SetSetpoint(myShooter->GetArmPosition() + motionValue);
//
//            ds->Printf(DriverStationLCD::kUser_Line2, 1, "j2Y: %5.3f mv: %d", stickY, motionValue);
//            ds->UpdateLCD();
//
//            Wait(0.1);
//          }
//      }
//  }
//
//
//
//  //*********************************************************************************************
//  // TurnToTheta turns the robot a specified number of degrees using PID
//  //*********************************************************************************************
//  float RobotDemo::TurnToTheta(float destTheta, bool waitFinish)
//  {
//    double targetAngle = destTheta;
//
//    targetAngle += gyro->GetAngle();      // add the current angle to the target angle to adjust for any gyro error
//    //  ds->Printf(DriverStationLCD::kUser_Line1, 1, "st g:%4.2f tgt:%4.2f", gyro->GetAngle(), targetAngle);
//    //  ds->UpdateLCD();
//    //
//    turnPID->SetSetpoint(targetAngle);
//    turnPID->SetContinuous(false);
//    //          turnPID->SetInputRange(-(fabs(targetAngle)), fabs(targetAngle));
////              turnPID->SetOutputRange(-motorSpeed, motorSpeed);
//    //  //turnPID->SetAbsoluteTolerance(gyroPIDTolerance);
//    //  turnPID->SetPercentTolerance(5.0);
//    //
//    turnPID->Enable();
//    //
//    //  // if waitFinish is true, wait until we get to within our tolerance of the setpoint
//    while (waitFinish && (fabs(gyro->GetAngle()) < (fabs(targetAngle)*(0.95))))  // 1% tolerance
//      {
//        ds->Printf(DriverStationLCD::kUser_Line2, 1, "gyro: %4.2f %4.2f", gyro->GetAngle(), targetAngle);
//        ds->UpdateLCD();
//        Wait(0.01);
//      }
//    // if we're waiting for the turn to finish, disable PID here, otherwise, leave it up to the calling method
//    if(destTheta > 0.0f) {
//        ds->Printf(DriverStationLCD::kUser_Line4, 1, "gyro: %4.2f %4.2f", gyro->GetAngle(), targetAngle);
//        ds->UpdateLCD();
//    } else {
//        ds->Printf(DriverStationLCD::kUser_Line3, 1, "gyro: %4.2f %4.2f", gyro->GetAngle(), targetAngle);
//        ds->UpdateLCD(); 
//    }
//      
//    
//    if (waitFinish)
//      turnPID->Disable();
//
//    return targetAngle;
//  }
//
//
//  //*********************************************************************************************
//  // AutoSearchForTarget
//  //*********************************************************************************************
//
//  bool RobotDemo::AutoSearchForTarget()
//  {
//    int state = 0;
//    int vInc = 30;
//    float hInc = 15.0;                                                                          // degrees to search
//    bool found = false;
//
//    while (!found && IsAutonomous() && IsEnabled())
//      {
//        TurnToTheta (135.0, false);                             //If no target is found, turn to 135 degrees to look for target
//           Wait (1.0); 
//           turnPID->Disable();
//           if (myShooter->Target())
//                   found = true;
//           
//        switch (state)
//        {
//        case (0):       // look up a bit
//                myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+vInc);                         // look up
//        if (myShooter->Target())
//          found = true;
//        else
//          {
//            myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-vInc);                     // reset
//            state += 1;
//          }
//        break;
//
//
//        case (1):       // look down a bit
//                 myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-vInc);         // look down
//        if (myShooter->Target())
//          found = true;
//        else
//          {
//            state += 1;
//            myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+vInc);                     // reset
//          }
//        break;
//
//        case (2):       // turn one way
//     myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+hInc);         // look right
//            if (myShooter->Target())
//              found = true;
//            else
//              {
//                state += 1;
//                myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-hInc);                     // reset
//              }
//            break;
//
//        case (3):
//     myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()-hInc);         // look left
//            if (myShooter->Target())
//              found = true;
//            else
//              {
//                state += 1;
//                myShooterArmPID->SetSetpoint(myShooter->GetArmPosition()+hInc);                     // reset
//              }
//            break;
//
//        case (4):
//                            // look up a bit
//                            break;
//
//        } // switch state
//
//      } // While
//
//    return found;
//
//  } // AutoSearchForTarget



  //*********************************************************************************************
  // Autonomous
  //*********************************************************************************************


  void Autonomous(void)
  {
    TorTest myTest(stick1, ds, myJagDrive);
    
//	  myShooterArmJag->Set(0.55f);
//	  Wait(1.0f);
//	  myShooterArmJag->Set(0.0f);
//    while (IsAutonomous() && IsEnabled())
//     {
//        Wait(0.25);
//  myTest.RunTests();
//  }
    
    	// 314, 490, 645, 900
//    while (IsAutonomous() && IsEnabled())
//    {
    
//    	myTest.testGetShootArmPOT();
    	
    	//myTest.gyroTest();
//    }
//   myTest.testClimberMotors();
    
//    myTest.testDrive();
    
    //myTest.testGetFeedPOT();
    
    
    
//    myTest.runElevator();
//   myTest.testTorClimber();
    	
//    myTest.testClimberLimitSwitch();
    	
//    myTest.testRaiseFeedPOT();
    myTest.testSensors();
//    myTest.testTorClimber();
//    myTest.testPOTAccuracy();

    //myTest.testFire();
    
//   myTest.testGetFeedPOT();
//    myTest.testbreakBeam();
//    myTest.testDiskOrientationSensor();
//    myTest.testFeederMagFullSensor();
    
  } //end of auto




  //*********************************************************************************************
  // OperatorControl
  //*********************************************************************************************

  void OperatorControl(void)
  {
    TorTest myTest(stick1, ds, myJagDrive);
    ds->Clear();
    ds->Printf(DriverStationLCD::kUser_Line1, 1, "Operator Mode");
    ds->UpdateLCD();
    myTorbotDrive->resetEncoder();
    //gyro->Reset();
   
    //compressor->Start();
  //  turnPID->Disable();         // in case it was enabled and left on in auto, disable so we can drive away
    
//    myShooter->SetArmPosition(TorShooter::Straight);                                   // straight ahead position
//    myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
//    compressorRelay->Set(Relay::kOn);
        
    //compressor->Start();
   
//    myTest.testFire();
   
    //myTest.testRaiseFeedPOT();
//    myTest.testPicker();
    myTest.testDrive();
    
   while (IsOperatorControl() && IsEnabled())
      {        
    	
//        shooterJagRear->Set(-0.3);
//       shooterJagFront->Set(-0.3);
    	
//        if(fabs(stick2.GetY()/2) < .25) {
//            myShooterArmJag->Set(0.0);
//        } else {
//            myShooterArmJag->Set(stick2.GetY()/2);
        }
       
//       elevatorJag->Set(stick2.GetY()/2);
//        
//
//        
        myTorbotDrive->ArcadeDrive(true);                                               // true for squared inputs, false for linear
        ds->Printf(DriverStationLCD::kUser_Line2, 1, "comp %d", compressor->GetPressureSwitchValue());
        ds->UpdateLCD();
//
//        // Shooter
////        myShooter->Run();                                                               // check joysticks and run shooter mode
////        if (myShooter->GetState() == TorShooter::StartMove)
////          {
////            myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());
////            TurnToTheta(myShooter->GetTurnSetPoint(), true);                            // set robot turn setpoint and wait for it to reach position
////          }
////        else if ((myShooter->GetState() == TorShooter::Init) && (turnPID->IsEnabled())) // if done shooting and we had turned on PID, turn it off and drive away
////          {
////            turnPID->Disable();
////          }
////
////        // Manual Shooter Arm Movement
////        ShooterArmManualControl();                                                      // stick2 Y-axis controls shooter arm motion manually
////
////        // Feeder
////        myFeeder->checkDiskLoader();                                                    // check the feeder load slot for new disks, could be from picker or human
//
//        // Climber
//        // myClimber->Run();
//
//        // Pickup
//        // myPicker->Run();
//        //            if (myPicker->GetState() == TorPicker::StartMove)
//        //            {
//        //                myShooterArmPID->SetArmPosition(TorShooter::Loading);                       // load position
//        //                myShooterArmPID->SetSetpoint(myShooter->GetArmSetPoint());                  // set the PID setpoint to move the arm
//        //            }
//
//        
//        ds->Printf(DriverStationLCD::kUser_Line2, 1, "Left Encoder: %f",  wheelEncoderLeft->Get());
//        ds->Printf(DriverStationLCD::kUser_Line3, 1, "Right Encoder: %f",  wheelEncoderRight->Get());
////        ds->Printf(DriverStationLCD::kUser_Line4, 1, "GYRO: %f",  gyro->GetAngle());
//              ds->UpdateLCD();
//
//
//      } // end isOperatorControl loop

  } // end OperatorControl method





  //*********************************************************************************************
  // Test
  //*********************************************************************************************

//  void Test() {
//    
//    
////    Jaguar *leftDriveJag = new Jaguar(1, Consts::LEFT1_DRIVE_JAG);
////    Jaguar *leftDriveJag2 = new Jaguar(1, Consts::LEFT2_DRIVE_JAG);
////    Jaguar *rightDriveJag = new Jaguar(1, Consts::RIGHT1_DRIVE_JAG);
////    Jaguar *rightDriveJag2 = new Jaguar(1, Consts::RIGHT2_DRIVE_JAG);
////    TorJagDrive *myJagDrive = new TorJagDrive(*leftDriveJag, *leftDriveJag2, *rightDriveJag, *rightDriveJag2);
//
//    TorTest myTest(ds, myJagDrive);
//    
//    ds->Clear();
//    ds->Printf(DriverStationLCD::kUser_Line1, 1, "Test Mode");
//    ds->UpdateLCD();
//
//    leftDriveJag->Set(0.25);
//    leftDriveJag2->Set(0.25);
//    
//    ds->Printf(DriverStationLCD::kUser_Line2, 1, "jag on");
//    ds->UpdateLCD();
//
//    Wait(5.0);
//    leftDriveJag->Set(0.0);
//    leftDriveJag2->Set(0.0);
//    ds->Printf(DriverStationLCD::kUser_Line3, 1, "jag off");
//    ds->UpdateLCD();
//
//    //myTest.testCompressor();
//    //myTest.testDrive();
//    
////    while (IsTest() && IsEnabled())
////      {
////            //              
////        //              ds->Printf(DriverStationLCD::kUser_Line2,1, "Ticks: %d",wheelEncoder->GetRaw());
////        //              ds->Printf(DriverStationLCD::kUser_Line5,1,"Dist: %f",myTorbotDrive->getDistance());
////        //              ds->Printf(DriverStationLCD::kUser_Line6,1,"Pot Val: %f", potval);
////        //              ds->UpdateLCD();
////    
////      } //test while
////
////
// } // end test

};

START_ROBOT_CLASS(RobotDemo);

