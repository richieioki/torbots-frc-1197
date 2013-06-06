#pragma once
#include "TorTargetAcquire.h"

namespace Consts
{ 
  const float DFLT_PROP_GAIN  = 0.01; //get rid of magic numbers
  const float DFLT_SPEED = 0.5f;
  
  const float AREA_MINIMUM = 500;
  const float CAMERA_FOV = 23.5;
  const float CAMERA_V_FOV = 22.5;

  const float RECTANGULARITY_LIMIT = 60;
  const float ASPECT_RATIO_LIMIT = 75;
  const float X_EDGE_LIMIT = 40;
  const float Y_EDGE_LIMIT = 60;
  const float GEAR_RATIO = 12.0/26.0;
  
  
  // SHOOTER ARM POT POSITIONS
  const double SHOOTER_ARM_LOADING =  58;//196.0;
  const double SHOOTER_ARM_STRAIGHT = 256;//420.0;
  const double SHOOTER_ARM_TARGETING = 386;//580.0;
  const double SHOOTER_ARM_CLIMBING = 667;//850.0;
    
  const double CAM_TO_POT = 2.778;      // 360 degrees / 1000 pot pts constant
  const float stickDeadZone = 0.2;      //if the joystick's value is less than this, it will be considered 0
  
  
  //Encoder distance variables
  const float wheelCircumference = 4.0*3.1416; //(inches)
  const float wheelGearRatio_High = 1.0/9.0;
  const float wheelGearRatio_Low = 1.0/21.0;
  const float encoderTicks = 360.0; //tick count on the encoder

  //sensor constants
  const float shooterCountsPerRev = 6; //hall effect sensor counts per revolution of shooter wheel
  const float feederOneDiskPotValue = -385.0f; //Current Feeder Number protobot
  
  //JOYSTICK BUTTONS
  const int STICK1_PORT = 1;
  const int STICK2_PORT = 2;
  const int TRIGGER_BUTTON = 1;
  const int SHIFT_BUTTON = 2;
  const int PICKUP_BUTTON = 3;
  const int CLIMB_BUTTON = 11;
  const int MANUAL_TRIGGER_BUTTON = 1;          // joystick 2 trigger
  
  // WIRING CHANNELS
  //Solenoids
  const int SHIFT_SOLENOID = 1;
  const int PICKUP_ARM_SOLENOID = 2;
  const int PICKUP_SUCTION_SOLENOID = 3;
  const int SHOOTER_MAG_LOAD_SOLENOID = 4;

  //Analog Channels
  // POTs
  const int SHOOTER_ARM_LPOT = 5;
//  const int SHOOTER_ARM_RPOT = 5;
  const int SHOOTER_ARM_POT_MOD = 1;    // MODULE
  const int FEEDER_POT_MOD = 1;     
  const int FEEDER_POT = 3;
  const int FEEDER_INCREMENT_VAL = 341; //pot val to raise one disk one space in feeder
  const int FEEDER_POT_MAX = 1000;
    
  // JAGs
  const int LEFT1_DRIVE_JAG = 4;
//  const int LEFT2_DRIVE_JAG = 2;
  const int RIGHT1_DRIVE_JAG = 3;
//  const int RIGHT2_DRIVE_JAG = 4;

  const int LEFT1_DRIVE_JAG_MOD = 1;
  const int LEFT2_DRIVE_JAG_MOD = 1;
  const int RIGHT1_DRIVE_JAG_MOD = 1;
  const int RIGHT2_DRIVE_JAG_MOD = 1;

  const int SHOOTER_ARM_JAG_MOD = 1;
  const int SHOOTER_ARM_JAG = 5;

  const int CLIMBER_LEFT_MOD = 1;
  const int CLIMBER_LEFT_JAG = 9;
  
  const int CLIMBER_RIGHT_MOD = 1;
  const int CLIMBER_RIGHT_JAG = 10;
  
  const int SHOOTER_REAR_MOD = 1;
  const int SHOOTER_REAR_JAG = 7;                       // loader, rear motor
  const int SHOOTER_FRONT_MOD = 1;
  const int SHOOTER_FRONT_JAG = 6;                     //main, front motor
  
  const int FEEDER_ELEVATOR_MOD = 1;                      //Made by Alex
  const int FEEDER_ELEVATOR_JAG = 8;
  
  //Gyro 
  const int GYRO_MOD = 1;
  const int GYRO_CHANNEL = 1;

  
  //Encoders
  const int RIGHT_ENCODER_MOD = 1;
  const int RIGHT_ENCODER_MSB = 3;
  const int RIGHT_ENCODER_LSB = 4;
//  const int RIGHT_ENCODER_MSB = 8; Proto-Bot
//  const int RIGHT_ENCODER_LSB = 9;

  const int LEFT_ENCODER_MOD = 2;
  const int LEFT_ENCODER_MSB = 5;
  const int LEFT_ENCODER_LSB = 6;
  
  //Compressor
  const int PRESSURE_SWITCH_MOD = 2;  
  const int PRESSURE_SWITCH_CHANNEL = 7;
  const int COMPRESSOR_RELAY_MOD = 2;  
  const int COMPRESSOR_RELAY_CHANNEL = 1;


  // Digital Sensors
  const int LEFT_HAND_SENSOR_MOD = 2;
  const int LEFT_HAND_SENSOR = 2;
  const int RIGHT_HAND_SENSOR_MOD = 2;
  const int RIGHT_HAND_SENSOR = 4;

  const int LEFT_FOOT_SENSOR_MOD = 2;
  const int LEFT_FOOT_SENSOR = 1;
  const int RIGHT_FOOT_SENSOR_MOD = 2;
  const int RIGHT_FOOT_SENSOR = 3;

  const int BOTTOM_FORKLIFT_LIMIT_MOD = 1;
  const int BOTTOM_FORKLIFT_LIMIT = 2;
  const int TOP_FORKLIFT_LIMIT_MOD = 1;
  const int TOP_FORKLIFT_LIMIT = 1;
  const int INIT_FORKLIFT_LIMIT_MOD = 1;
  const int INIT_FORKLIFT_LIMIT = 14;
  
  const int PICKER_DISK_SENSOR_MOD = 1;                  //Made by Alex
  const int PICKER_DISK_SENSOR = 9;
  const int PICKER_DISK_ORIENTATION_SENSOR_MOD = 1;
  const int PICKER_DISK_ORIENTATION_SENSOR = 11;
  
  const int PICKER_CONTACT_SENSOR_MOD = 1;
  const int PICKER_CONTACT_SENSOR = 12;
  const int PICKER_RETRACT_SENSOR_MOD = 1;
  const int PICKER_RETRACT_SENSOR = 13;
  
  const int SHOOTER_FRONT_STOP_MOD = 1;
  const int SHOOTER_FRONT_STOP = 7;
  const int SHOOTER_REAR_STOP_MOD = 1;
  const int SHOOTER_REAR_STOP = 8;
  
  const int FEEDER_READY_SENSOR_MOD = 1;
  const int FEEDER_READY_SENSOR = 6;
  
  const int FEEDER_DISK_SENSOR_MOD = 1;
  const int FEEDER_DISK_SENSOR = 10;
//  const int FEEDER_DISK_ORIENT_MOD = 1;
//  const int FEEDER_DISK_ORIENT = 11;
  
  const int FEEDER_MAG_FULL_MOD = 1;
  const int FEEDER_MAG_FULL = 11;
  
//Edge profile constants used for hollowness score calculation
//#define XMAXSIZE 24
//#define XMINSIZE 24
//#define YMAXSIZE 24
//#define YMINSIZE 48


}
