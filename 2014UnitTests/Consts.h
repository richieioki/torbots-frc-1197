#pragma once
namespace Consts
{
  const float GEAR_RATIO = 12.0/26.0;
      
    
    
  const float stickDeadZone = 0.2;      //if the joystick's value is less than this, it will be considered 0
  
  //Shooter speed variables
    const float BASE_SHOOTER_FIRE_SPEED = 0.55; //55% is best with full battery against one point goal
    const float SHOOTER_LOAD_SPEED = -0.4;
    
    const float LOADER_BAR_SPEED = 1.0;
    
    //Shooter Booleans
    const bool SHOOTER_PISTON_EXTENDED = false; //needs to be changed for actual bot
    const bool LOADER_PISTON_EXTENDED = false; //needs to be changed for actual bot
    
    //Encoder distance variables
    const float wheelCircumference = 4.0*3.1416; //(inches)

    const float wheelGearRatio_High = 1.0/9.0;
    const float wheelGearRatio_Low = 1.0/21.0;

    const float encoderTicks = 360.0; //tick count on the encoder

    //JOYSTICK BUTTONS
    const int STICK_PORT = 1;
    const int RUN_BUTTON = 1; //trigger
    const int FIRE_BUTTON = 2; //side thumb button
    
    const int S_SHIFT_BUTTON = 3; //shift to slow gear if held down
    const int S_LOADER_DOWN_BUTTON = 8; //change state to load
    const int S_LOADER_UP_BUTTON = 7; //change state to drive
    const int S_LOAD_OVERRIDE_BUTTON = 4; //override checking cage to see if loaded
    const int S_SHOOTER_UP_BUTTON = 11;
    const int S_SHOOTER_DOWN_BUTTON = 12;
    
    
    //TARTARUS BUTTONS
    const int TARTARUS_PORT = 2;
    const int SHIFT_BUTTON = 8;
    const int LOAD_OVERRIDE_BUTTON = 13;
    const int LOADER_DOWN_BUTTON = 14;
    const int LOADER_UP_BUTTON = 9;
    const int SHOOTER_DOWN_BUTTON = 15;
    const int SHOOTER_UP_BUTTON = 10;
    
    // WIRING CHANNELS
    //Solenoids
    const int SHIFT_SOLENOID = 3;
    const int LOAD_SOLENOID = 1;
    const int FIRE_SOLENOID = 2;

    //Analog Channels
    // POTs
    
    //DRIVE TALONS
     const int LEFT1_DRIVE_TALON = 1; //Indefinite spots on sidecar until electronics listed for robot
     const int LEFT2_DRIVE_TALON = 2;// 4 Talons on gearbox
     const int RIGHT1_DRIVE_TALON = 3;
     const int RIGHT2_DRIVE_TALON = 4;
    
    //INTAKE TALONS
     const int LEFT_INTAKE_TALON = 5;
     const int RIGHT_INTAKE_TALON = 6;
     
    //SHOOTER TALONS
     const int LEFT_SHOOT_TALON = 7;
     const int RIGHT_SHOOT_TALON = 8;
     
    //ARM TALONS
     const int ARM_TALON = 9;
     
    //Gyro 
    const int GYRO_MOD = 1;
    const int GYRO_CHANNEL = 1;

    
    //Encoders
    const int RIGHT_ENCODER_MSB = 3;
    const int RIGHT_ENCODER_LSB = 4;

    const int LEFT_ENCODER_MSB = 5;
    const int LEFT_ENCODER_LSB = 6;
    
    //Compressor 
    const int PRESSURE_SWITCH_CHANNEL = 1; 
    const int COMPRESSOR_RELAY_CHANNEL = 1;


    // Digital Sensors
    

}
