#pragma once

namespace Consts
{ 
  const float stickDeadZone = 0.2;      //if the joystick's value is less than this, it will be considered 0

  //Shooter speed variables
  const float BASE_SHOOTER_FIRE_SPEED = 0.55; //55% is best with full battery against one point goal
  const float MAX_SHOOTER_FIRE_SPEED = 0.90;
  const float SHOOTER_LOAD_SPEED = -0.4;

  const float LOADER_BAR_SPEED = -1.0;
  const float CAGE_MOVE_SPEED = 0.30; //positive is up


  //Autonomous Variables
  const float AUTO_DRIVE_SPEED = 0.7;
  const float AUTO_DRIVE_DIST = 180.5; //180.5
  const float AUTO_JAG_WAIT_TIME = 2.0;

  //Shooter Booleans
  const bool SHOOTER_PISTON_EXTENDED = true; //TODO: needs to be changed for actual bot
  const bool LOADER_PISTON_EXTENDED = true; //TODO: needs to be changed for actual bot

  //Shooter Arm POT Values

  const float SHOOTER_ARM_INIT = 328.0;
  const float SHOOTER_ARM_LOADING = 420.0; //blaze it
  const float SHOOTER_ARM_MIN = 165.0;
  const float SHOOTER_ARM_SHOOTING = 205.0;
  const float SHOOTER_ARM_LONG_SHOT = 300.0;
  
  //Encoder distance variables
  const float wheelCircumference = 4.0*3.1416; //(inches)

  const float wheelGearRatio_High = 1.0/11.733;
  const float wheelGearRatio_Low = 1.0/21.0;

  const float encoderTicks = 250.0; //tick count on the encoder


  //JOYSTICK BUTTONS
  const int STICK_PORT = 1;
  const int RUN_BUTTON = 1; //trigger
  const int PASS_BUTTON = 6;
  const int CATCH_BUTTON = 4;
  const int REVERSE_DRIVE = 2;

  // const int S_SHIFT_BUTTON = 3; //shift to slow gear if held down
  const int S_LOADER_DOWN_BUTTON = 8; //change state to load
  const int S_LOADER_UP_BUTTON = 7; //change state to drive
  const int S_SHOOTER_UP_BUTTON = 11;
  const int S_SHOOTER_DOWN_BUTTON = 12;


  //TARTARUS BUTTONS
  const int TARTARUS_PORT = 2;
  const int SHIFT_BUTTON = 2; //8 on physical Tartarus
  const int PREP_LOAD = 7;    //14 on physical tartarus
  const int PREP_SHOOT = 3;     //9 on physical tartarus
 // const int SHOOTER_DOWN_BUTTON = 8;  //15 on physical tartarus
 // const int SHOOTER_UP_BUTTON = 4;   //10 on physical tartarus
 // const int SHOOTER_SWEET_SPOT = 6; //13 on physical tartarus


  // WIRING CHANNELS
  //Solenoids
  const int SHIFT_SOLENOID = 3;
  const int LOAD_SOLENOID = 1;
  const int FIRE_SOLENOID = 2;

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

  //Gyro Analog
  const int GYRO_CHANNEL = 1;

  //Pot Analog
  const int ARM_POT_CHANNEL = 2;

  //Encoders
  const int RIGHT_ENCODER_MSB = 6; //a channel
  const int RIGHT_ENCODER_LSB = 7; //b channel

  const int LEFT_ENCODER_MSB = 2; //a channel
  const int LEFT_ENCODER_LSB = 3; //b channel

  //Compressor
  const int PRESSURE_SWITCH_CHANNEL = 1;
  const int COMPRESSOR_RELAY_CHANNEL = 1;
}
