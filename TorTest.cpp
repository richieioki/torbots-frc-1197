#include "TorTest.h"
#include "Consts.h"
#include "TorJagDrive.h"

TorTest::TorTest(DriverStationLCD *dsIn, TorJagDrive *JagDriveIn) {
  ds = dsIn;
  myJagDrive = JagDriveIn;
  
  //driver station (actual, not the display)
  driverStation = DriverStation::GetInstance();

  timer = new Timer();
  timer->Reset();
  timer->Start();
  
}

void TorTest::testDrive()
{
  Encoder *wheelEncoderLeft = new Encoder(Consts::LEFT_ENCODER_MOD,Consts::LEFT_ENCODER_MSB,Consts::LEFT_ENCODER_MOD,Consts::LEFT_ENCODER_LSB); //a channel, b channel
  Encoder *wheelEncoderRight = new Encoder(Consts::RIGHT_ENCODER_MOD,Consts::RIGHT_ENCODER_MSB,Consts::RIGHT_ENCODER_MOD,Consts::RIGHT_ENCODER_LSB); //a channel, b channel
  //Gyro *gyro = new Gyro(Consts::GYRO_MOD, Consts::GYRO_CHANNEL);
  Solenoid *shiftSolenoid = new Solenoid (Consts::SHIFT_SOLENOID);
  
  ds->Printf(DriverStationLCD::kUser_Line2, 1, "Drive Test");
  ds->UpdateLCD();

  myJagDrive->SetLeft(0.5);                      //Go Forward at 25% Speed for 5 Second
  myJagDrive->SetRight(0.5);

  timeStart = timer->Get();
  timer->Start();
  while ((timer->Get()) <= 20.0)
    {
      ds->Printf(DriverStationLCD::kUser_Line3, 1, "Left Enc : %8d",  wheelEncoderLeft->GetRaw());
      ds->Printf(DriverStationLCD::kUser_Line4, 1, "Right Enc: %8d",  wheelEncoderRight->GetRaw());
      ds->Printf(DriverStationLCD::kUser_Line5, 1, "Time left: %4.2f",  20.0 - timer->Get());
      //ds->Printf(DriverStationLCD::kUser_Line6, 1, "Gyro %f", gyro->GetAngle());
      ds->UpdateLCD();
    }
  myJagDrive->SetLeft(0.0);     
  myJagDrive->SetRight(0.0);
  
  Wait(1.0);
  timer->Stop();
  if (shiftSolenoid->Get())
    shiftSolenoid->Set(false);
  else
    shiftSolenoid->Set(true);
  timer->Reset();
  timer->Start();
  
  Wait(1.0);
  while ((timer->Get()) <= 10.0)
    {
      ds->Printf(DriverStationLCD::kUser_Line3, 1, "Left Enc : %8d",  wheelEncoderLeft->GetRaw());
      ds->Printf(DriverStationLCD::kUser_Line4, 1, "Right Enc: %8d",  wheelEncoderRight->GetRaw());
      ds->Printf(DriverStationLCD::kUser_Line5, 1, "Time left: %4.2f",  10.0 - timer->Get());
      //ds->Printf(DriverStationLCD::kUser_Line6, 1, "Gyro %f", gyro->GetAngle());
      ds->UpdateLCD();
    }
  myJagDrive->SetLeft(0.0);     
  myJagDrive->SetRight(0.0);

}


void TorTest::testCompressor()
{  
  Compressor *compressor;
  compressor = new Compressor(Consts::PRESSURE_SWITCH_MOD,Consts::PRESSURE_SWITCH_CHANNEL,
      Consts::COMPRESSOR_RELAY_MOD,Consts::COMPRESSOR_RELAY_CHANNEL); //change ports for actual bot

  compressor->Start();                                                 //Starts Compressor for 5 seconds
  ds->Printf(DriverStationLCD::kUser_Line2, 1, "Compressor Started");
  ds->UpdateLCD();
  Wait(5.0);               
  
  compressor->Stop(); 
  ds->Printf(DriverStationLCD::kUser_Line2, 1, "Compressor Stopped");   //Stops Compressor
  ds->UpdateLCD();

}

void TorTest::testFire()
{
  Jaguar frontShootMotor (Consts::SHOOTER_FRONT_MOD, Consts::SHOOTER_FRONT_JAG);     
  Jaguar rearShootMotor (Consts::SHOOTER_REAR_MOD, Consts::SHOOTER_REAR_JAG);    
  Solenoid shootSolenoid(Consts::SHOOTER_MAG_LOAD_SOLENOID);

  ds->Printf(DriverStationLCD::kUser_Line2, 1, "Testing shooter");
  ds->UpdateLCD();

  ds->Printf(DriverStationLCD::kUser_Line3, 1, "start motors...");
  ds->UpdateLCD();

  frontShootMotor.Set(0.5);                     //Starts Shooter Motor at 50% Speed
  rearShootMotor.Set(0.5);                      //Starts Feeder Motor at 50% Speed
  Wait(2.0);

  ds->Printf(DriverStationLCD::kUser_Line4, 1, "fire mag loader...");
  ds->UpdateLCD();
  shootSolenoid.Set(false);                      //Shoots Solenoid(pushes disk) for 1 second
  Wait(1.0);

  ds->Printf(DriverStationLCD::kUser_Line4, 1, "retract mag loader...");
  ds->UpdateLCD();
  shootSolenoid.Set(true);                     //Retracts Solenoid
  Wait(2.0);                   

  ds->Printf(DriverStationLCD::kUser_Line5, 1, "stop motors");
  ds->UpdateLCD();
  frontShootMotor.Set(0.0);                     //Stops motors
  rearShootMotor.Set(0.0);

}

void TorTest::testGetShootArmPOT()
{
  AnalogChannel m_ShooterArmPOT(Consts::SHOOTER_ARM_POT); // = new AnalogChannel(3);
  int testPOTVal = m_ShooterArmPOT.GetAverageValue();
  
  ds->Printf(DriverStationLCD::kUser_Line6, 1, "ShootPOTVal: %d", testPOTVal);
  
}
void TorTest::testShootArm()
{
  Jaguar *shooterArm = new Jaguar (Consts::SHOOTER_ARM_JAG_MOD, Consts::SHOOTER_ARM_JAG);

  ds->Printf(DriverStationLCD::kUser_Line2, 1, "Testing shooter arm...");
  ds->UpdateLCD();
  
  shooterArm->Set(0.5);
  Wait (5.0);
  
  shooterArm->Set(0.0);
  
}

void TorTest::testGetFeedPOT()
{
  AnalogChannel feederPOT(Consts::FEEDER_POT); // = new AnalogChannel(3); 
  
  int testPOTVal = feederPOT.GetAverageValue();

  ds->Printf(DriverStationLCD::kUser_Line4, 1, "FeedPOT: %d", testPOTVal);

}

void TorTest::runElevator() {
  
  Jaguar elevator(Consts::FEEDER_ELEVATOR_MOD,Consts::FEEDER_ELEVATOR_JAG);
  
  elevator.Set(0.2f);
  
}

void TorTest::testRaiseFeedPOT()   
{
  AnalogChannel feederPOT(Consts::FEEDER_POT); // = new AnalogChannel(3); 
  Jaguar elevator(Consts::FEEDER_ELEVATOR_MOD, Consts::FEEDER_ELEVATOR_JAG);                // = new Jaguar(1, 10);
  int testPOTVal = feederPOT.GetAverageValue();
  int testTargetPOT = testPOTVal-Consts::FEEDER_INCREMENT_VAL;                      //"385" will change to POT Value from "testGetFeedPOT"

  ds->Printf(DriverStationLCD::kUser_Line3, 1, "FeedPOT: %d Tgt: %d", testPOTVal, testTargetPOT);
  ds->UpdateLCD();

  elevator.Set(0.2);
  while (abs(feederPOT.GetAverageValue()) <= abs(testTargetPOT))    //Raises Feeder 1 disc space
    {}
  elevator.Set(0.0);
  
  ds->Printf(DriverStationLCD::kUser_Line5, 1, "Raised Frisbee");
    ds->UpdateLCD();
}

void TorTest::testbreakBeam()
{
  DigitalInput diskSensor(Consts::PICKER_DISK_SENSOR_MOD,Consts::PICKER_DISK_SENSOR);      //= new DigitalInput(9);

  ds->Printf(DriverStationLCD::kUser_Line2, 1, "Testing Break Beam");
  ds->UpdateLCD();
  //timeStart = timer->Get();
  timer->Stop();
  timer->Reset();
  timer->Start();
  while (true)//(timer->Get()) <= 20.0)
    {
      if(diskSensor.Get() == 0)
        {
          ds->Printf(DriverStationLCD::kUser_Line5, 1, "Beam Broken");
        }
      else
        {
          ds->Printf(DriverStationLCD::kUser_Line5, 1, "Beam Intact");
        }
      ds->UpdateLCD();
    }
  timer->Stop();
}

void TorTest::testDiskOrientation()               //Gives disk's Orientation
{
  AnalogChannel feederPOT(Consts::FEEDER_POT);     // = new AnalogChannel(3); 
  Jaguar elevator(Consts::FEEDER_ELEVATOR_MOD, Consts::FEEDER_ELEVATOR_JAG);                // = new Jaguar(1, 10);
  DigitalInput diskOrientionSensor(Consts::PICKER_DISK_ORIENTATION_SENSOR_MOD, Consts::PICKER_DISK_ORIENTATION_SENSOR);    // = new DigitalInput(11);
  DigitalInput diskSensor(Consts::PICKER_DISK_SENSOR_MOD, Consts::PICKER_DISK_SENSOR);      //= new DigitalInput(9);
  int testPOTVal = feederPOT.GetAverageValue();
  int testTargetPOT = testPOTVal-Consts::FEEDER_INCREMENT_VAL;

  if(diskSensor.Get()&& diskOrientionSensor.Get() == 0) {                   //If switch is not pressed
      ds->Printf(DriverStationLCD::kUser_Line5, 1, "Disk Right Side up");   //Disk is Right Side Up
      ds->Printf(DriverStationLCD::kUser_Line3, 1, "FeedPOT: %d Tgt: %d", testPOTVal, testTargetPOT);
      ds->UpdateLCD();

      elevator.Set(0.2);       //Moves Disk Up
      while (abs(feederPOT.GetAverageValue()) <= abs(testTargetPOT))
        {}
      elevator.Set(0.0);
  }

  else if (diskSensor.Get()== 0 && diskOrientionSensor.Get() == 1) {       //If switch is pressed
      ds->Printf(DriverStationLCD::kUser_Line5, 1, "Disk Upside Down");    //Disk is Upside Down
      ds->Printf(DriverStationLCD::kUser_Line3, 1, "FeedPOT: %d Tgt: %d", testPOTVal, testTargetPOT);
      ds->UpdateLCD();

      elevator.Set(0.2);       //Moves Disk Up
      while (abs(feederPOT.GetAverageValue()) <= abs(testTargetPOT))
        {}
      elevator.Set(0.0);
  }

}

void TorTest::testTorClimber()
{
  Jaguar liftJag1 (Consts::CLIMBER_LEFT_MOD, Consts::CLIMBER_LEFT_JAG);                      
  Jaguar liftJag2 (Consts::CLIMBER_RIGHT_MOD, Consts::CLIMBER_RIGHT_JAG); 

  DigitalInput leftHandSwitch (Consts::LEFT_HAND_SENSOR_MOD, Consts::LEFT_HAND_SENSOR);                         // left inside              
  DigitalInput rightHandSwitch (Consts::RIGHT_HAND_SENSOR_MOD, Consts::RIGHT_HAND_SENSOR);                         // right inside      
  DigitalInput initPosSwitch (Consts::INIT_FORKLIFT_LIMIT_MOD, Consts::INIT_FORKLIFT_LIMIT);                     // initial position
  DigitalInput leftFootSwitch (Consts::LEFT_FOOT_SENSOR_MOD, Consts::LEFT_FOOT_SENSOR);                     // left outside
  DigitalInput rightFootSwitch (Consts::RIGHT_FOOT_SENSOR_MOD, Consts::RIGHT_FOOT_SENSOR);                    // right outside
  DigitalInput limitSwitchLift (Consts::TOP_FORKLIFT_LIMIT_MOD, Consts::TOP_FORKLIFT_LIMIT);                    //Limit Switch Front (Fully Extended)
  DigitalInput limitSwitchPull (Consts::BOTTOM_FORKLIFT_LIMIT_MOD, Consts::BOTTOM_FORKLIFT_LIMIT);                     //Limit Switch Back (Retracted)

  //intialize sensors here


  
  ds->Printf(DriverStationLCD::kUser_Line6, 1, "Testing Climber");

  liftJag1.Set(0.25);
  liftJag2.Set(-0.25);
  while(initPosSwitch.Get()== 0){                       // move until intitial position limit switch is hit
      Wait(.01);
  }

  liftJag1.Set(0.0);                                                //Stop Motors
  liftJag2.Set(0.0);

  while(leftFootSwitch.Get() && rightFootSwitch.Get() == 0){        //Wait for feet switches to be pressed
      Wait (.01);
  }

  while(leftFootSwitch.Get() && rightFootSwitch.Get() ==1){         //Wait until feet switches are released
      Wait (.01);
  }

  liftJag1.Set(0.25);                                                //Lift climber hands
  liftJag2.Set(-0.25);
  while(limitSwitchLift.Get()== 0){                                    // move until limit switch is hit
      Wait(.01);
  }


  liftJag1.Set(0.0);                               //Stop Motors
  liftJag2.Set(0.0);


  while(leftHandSwitch.Get() && rightHandSwitch.Get() == 0){     //Wait until hand switches are pressed
      Wait(.01);  
  }

  while(leftHandSwitch.Get() && rightHandSwitch.Get() == 1){     //Wait until hand switches are released
      Wait(.01);
  }

  while(leftHandSwitch.Get() && rightHandSwitch.Get()== 0) { //Retract Hands while Back limit Switch is not pressed
      liftJag1.Set(-0.25);                                                
      liftJag2.Set(0.25);
      while(limitSwitchPull.Get()== 0){                                    
          Wait(.01);
      }

      liftJag1.Set(0.0);
      liftJag2.Set(0.0);
  }
}

void TorTest::testPicker()
{
  Solenoid pickerSolenoid    (Consts::PICKUP_ARM_SOLENOID);                //= new Solenoid(Consts::PICKUP_ARM_SOLENOID);
  Solenoid suctionSolenoid  (Consts::PICKUP_SUCTION_SOLENOID);             //= new Solenoid(Consts::PICKUP_SUCTION_SOLENOID);
  Solenoid magLoader(Consts::SHOOTER_MAG_LOAD_SOLENOID);
  DigitalInput diskSensor(Consts::PICKER_DISK_SENSOR_MOD, Consts::PICKER_DISK_SENSOR);                                           //= new DigitalInput(9);

  //if(diskSensor.Get() == 0) {
      pickerSolenoid.Set (true);
      Wait (1.0);
      suctionSolenoid.Set (true);
      Wait (2.0);
      pickerSolenoid.Set(false);
      Wait(2.0);
      suctionSolenoid.Set (false);
      
      Wait(1.0);
      magLoader.Set(true);
      Wait(1.0);
      magLoader.Set(false);
  }
//}

void TorTest::gyroTest() {
  
  Gyro gyro(Consts::GYRO_MOD,Consts::GYRO_CHANNEL);
  ds->Printf(DriverStationLCD::kUser_Line2, 1, "Gyro Test");    //Disk is Upside Down
  while(true) {
      ds->Printf(DriverStationLCD::kUser_Line3, 1, "Gyro Angle : %f", gyro.GetAngle());
      ds->UpdateLCD();
  }
}

void TorTest::bottomTest() {
  ds->Clear();
    ds->Printf(DriverStationLCD::kUser_Line1, 1, "Test Bottom");
    ds->UpdateLCD();  
    
    
  testDrive(); //tests drive and then tests shifter
  testPicker();
  testShootArm();
}


void TorTest::RunTests()
{
  ds->Clear();
  ds->Printf(DriverStationLCD::kUser_Line1, 1, "Unit Test Mode");
  ds->UpdateLCD();

  //gyro->Reset();

  int mode = 0;
  for (int i = 1; i <= 5; i+=2)
    {
      mode = !driverStation->GetDigitalIn(i) ? i : 0;
      if (mode != 0)
        {
          break;//digital ins are on by default
        }
    }

  switch(mode)
  {
  case (1):                                                                     // Drive Tests
        testDrive();
        testPicker();
      break;

  case (3):                                                                     // Shooter tests
      gyroTest();
      break;

  case (5):                                                                     // Break Beam tests
        testPicker();
    break;
//    
//  default:                                 
//    ds->Clear();
//    ds->Printf(DriverStationLCD::kUser_Line2, 1, "MODE : %d", mode);
//    ds->UpdateLCD();
//    break;


  } // end switch

  if (mode == 0)
    {
//      ds->Clear();
//      ds->Printf(DriverStationLCD::kUser_Line2, 1, "MODE : %d", mode);
//      ds->UpdateLCD();

    }
  
  
  
} // end RunTests


