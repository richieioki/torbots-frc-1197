#pragma once

namespace Consts
{ 
  const float GEAR_RATIO = 12.0/26.0;
    
  
  
  const float stickDeadZone = 0.2;      //if the joystick's value is less than this, it will be considered 0
  
  //Shooter speed variables
  const float SHOOTER_FIRE_SPEED = 0.55; //55% is best with full battery against one point goal
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
  const int SHIFT_BUTTON = 3; //shift to slow gear if held down
  const int LOAD_BUTTON = 8; //change state to load
  const int DRIVE_BUTTON = 7; //change state to drive
  const int LOAD_OVERRIDE_BUTTON = 4; //override checking cage to see if loaded
  const int SHOOTER_UP_BUTTON = 11;
  const int SHOOTER_DOWN_BUTTON = 12;
  
  // WIRING CHANNELS
  //Solenoids
  const int SHIFT_SOLENOID = 3;
  const int LOAD_SOLENOID = 1;
  const int FIRE_SOLENOID = 2;

  //Analog Channels
  // POTs
  
  
  // JAGs
  const int LEFT1_DRIVE_JAG = 6;
  const int LEFT2_DRIVE_JAG = 6;
  const int RIGHT1_DRIVE_JAG = 5;
  const int RIGHT2_DRIVE_JAG = 5;

  const int LEFT1_DRIVE_JAG_MOD = 1;
  const int LEFT2_DRIVE_JAG_MOD = 1;
  const int RIGHT1_DRIVE_JAG_MOD = 1;
  const int RIGHT2_DRIVE_JAG_MOD = 1;

  const int TOP1_SHOOTER_JAG = 3;
  const int TOP2_SHOOTER_JAG = 4;
  const int BOTTOM1_SHOOTER_JAG = 1;
  const int BOTTOM2_SHOOTER_JAG = 2;
  
  const int TOP1_SHOOTER_JAG_MOD = 1;
  const int TOP2_SHOOTER_JAG_MOD = 1;
  const int BOTTOM1_SHOOTER_JAG_MOD = 1;
  const int BOTTOM2_SHOOTER_JAG_MOD = 1;
  
  const int LOADER_BAR_JAG = 7; //pickup
  const int LOADER_BAR_JAG_MOD = 1;
  
  const int CAGE_JAG = 8;
  const int CAGE_JAG_MOD = 1;
  
  const int ARM_JAG = 8;
  const int ARM_JAG_MOD = 1;
  
  //Gyro 
  const int GYRO_MOD = 1;
  const int GYRO_CHANNEL = 1;

  
  //Encoders
  const int RIGHT_ENCODER_MOD = 1;
  const int RIGHT_ENCODER_MSB = 3;
  const int RIGHT_ENCODER_LSB = 4;

  const int LEFT_ENCODER_MOD = 2;
  const int LEFT_ENCODER_MSB = 5;
  const int LEFT_ENCODER_LSB = 6;
  
  //Compressor
  const int PRESSURE_SWITCH_MOD = 1;  
  const int PRESSURE_SWITCH_CHANNEL = 1;
  const int COMPRESSOR_RELAY_MOD = 1;  
  const int COMPRESSOR_RELAY_CHANNEL = 1;


  // Digital Sensors
  

}
