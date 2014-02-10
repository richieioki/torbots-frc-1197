#pragma once

namespace Consts
{ 
  const float stickDeadZone = 0.2;      //if the joystick's value is less than this, it will be considered 0
  
  //Shooter speed variables
  const float BASE_SHOOTER_FIRE_SPEED = 0.55; //55% is best with full battery against one point goal
  const float SHOOTER_LOAD_SPEED = -0.4;
  
  const float LOADER_BAR_SPEED = 1.0;
  const float CAGE_MOVE_SPEED = 0.30; //positive is up
  
  
  //Autonomous Variables
  const float AUTO_DRIVE_SPEED = 0.8;
  const float AUTO_DRIVE_DIST = 180.5;
  
  //Shooter Booleans
  const bool SHOOTER_PISTON_EXTENDED = false; //TODO: needs to be changed for actual bot
  const bool LOADER_PISTON_EXTENDED = true; //TODO: needs to be changed for actual bot
  
  //Cage POT Values
  const int SHOOTER_ARM_UP = 0; //TODO: get pot value for actual bot
  const int SHOOTER_ARM_DOWN = 0; //TODO: get pot value for actual bot
  const int POT_THRESHOLD = 0; //how close the arm has to be before recognized as "at position"
 
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
  const int S_UNLOAD_OVERRIDE_BUTTON = 6;
  const int S_SHOOTER_UP_BUTTON = 11;
  const int S_SHOOTER_DOWN_BUTTON = 12;
  
  //TARTARUS BUTTONS
  const int TARTARUS_PORT = 2;
  const int SHIFT_BUTTON = 1;
  const int LOAD_OVERRIDE_BUTTON = 6;
  const int UNLOAD_OVERRIDE_BUTTON = 7;
  const int LOADER_DOWN_BUTTON = 2;
  const int LOADER_UP_BUTTON = 3;
  const int SHOOTER_DOWN_BUTTON = 4;
  const int SHOOTER_UP_BUTTON = 5;
  
  
  // WIRING CHANNELS
  //Solenoids
  const int SHIFT_SOLENOID = 3;
  const int LOAD_SOLENOID = 1;
  const int FIRE_SOLENOID = 2;

  //Analog Channels
  // POTs
  //const int SHOOTER_ARM_POT; //TODO: declare value once installed
  
  // JAGs
  const int LEFT1_DRIVE_JAG = 1;
  const int LEFT2_DRIVE_JAG = 2;
  const int RIGHT1_DRIVE_JAG = 3;
  const int RIGHT2_DRIVE_JAG = 4;

  const int TOP1_SHOOTER_JAG = 7;
  const int TOP2_SHOOTER_JAG = 8;
  const int BOTTOM1_SHOOTER_JAG = 5;
  const int BOTTOM2_SHOOTER_JAG = 6;
  
  const int LOADER_BAR_JAG = 9; //pickup
  
  const int CAGE_JAG = 10;
  
//  const int ARM_JAG = 10;
  
  //Gyro Analog
  const int GYRO_CHANNEL = 1;

  //Pot Analog
  const int ARM_POT_CHANNEL = 2;
  
  //Encoders
  const int RIGHT_ENCODER_MSB = 4;
  const int RIGHT_ENCODER_LSB = 5;

  const int LEFT_ENCODER_MSB = 2;
  const int LEFT_ENCODER_LSB = 3;
  
  //Compressor
  const int PRESSURE_SWITCH_CHANNEL = 1;
  const int COMPRESSOR_RELAY_CHANNEL = 1;


  // Digital Sensors
  

}
