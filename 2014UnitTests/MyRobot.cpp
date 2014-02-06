#include "WPILib.h"
#include "Consts.h"
#include "TorTest.h"
#include "TorDrive.h"

/**
 * This is a demo program showing the use of the RobotBase class.
 * The SimpleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 */ 

class RobotDemo : public SimpleRobot
{
  Joystick stick; // only joystick
  TorDrive *myTorDrive; // initializing TorDrive Class
  TorTest *myTest; // initializing TorTest Class

  //Drive Train Talons
  Talon *leftDriveTalon1; //four talons designated to control the speed of the drive motors  
  Talon *leftDriveTalon2; 
  Talon *rightDriveTalon1; 
  Talon *rightDriveTalon2;

  //Intake Talons
  Talon *leftIntakeTalon; //two talons designated to control the speed of the intake wheel's motors 
  Talon *rightIntakeTalon;

  //Shooter Talons
  Talon *leftShootTalon; //two talons designated to control the speed of the shooter wheel's motors 
  Talon *rightShootTalon;

  Encoder *wheelEncoderLeft; // wheel encoders -- not yet designated, just initializing them
  Encoder *wheelEncoderRight;

  Compressor *compressor; // pneumatics compressor initialization

  DriverStation *driverStation; // MUST INITIALIZE DRIVER STATION INORDER TO USE IT!!!
  DriverStationLCD *ds; // enables the use of the driver station lcd (right corner in DS)

public: // This is a public type, 
        //"the public keyword specifies that those members are accessible from any function within the class"
  RobotDemo(void): // Basically it means "nothing" or "no type"
                    // There are 3 basic ways that void is used:
                  // Function argument: int myFunc(void) -- the function takes nothing.
                  // Function return value: void myFunc(int) -- the function returns nothing
    
    stick(Consts::STICK_PORT) // Notice: This is not located in the 
  {
    ds = DriverStationLCD::GetInstance();
    compressor = new Compressor(Consts::COMPRESSOR_RELAY_CHANNEL, Consts::PRESSURE_SWITCH_CHANNEL);
    compressor->Start();

    //DRIVE TALONS
    leftDriveTalon1 = new Talon (Consts::LEFT1_DRIVE_TALON);
    leftDriveTalon2 = new Talon (Consts::LEFT2_DRIVE_TALON);

    rightDriveTalon1 = new Talon (Consts::RIGHT1_DRIVE_TALON);
    rightDriveTalon2 = new Talon (Consts::RIGHT2_DRIVE_TALON);

    //INTAKE TALONS

    leftIntakeTalon = new Talon (Consts::LEFT_INTAKE_TALON);
    rightIntakeTalon = new Talon (Consts::RIGHT_INTAKE_TALON);

    //SHOOT TALONS
    leftShootTalon = new Talon (Consts::LEFT_SHOOT_TALON);
    rightShootTalon = new Talon (Consts::RIGHT_SHOOT_TALON); 


    driverStation = DriverStation::GetInstance();

  }
  void Autonomous()
  {   
    myTest = new TorTest(stick, ds, myTorDrive);
    myTest->testDrive();
  }

  /**
   * Runs the motors with arcade steering. 
   */
  void OperatorControl()
  {
    myTest = new TorTest(stick, ds, myTorDrive);
    ds->Clear();
    ds->Printf(DriverStationLCD::kUser_Line1, 1, "Operator Mode");
    ds->UpdateLCD();
  }

  /**
   * Runs during test mode
   */
};

START_ROBOT_CLASS(RobotDemo);

