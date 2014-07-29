/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.*;
import torbots.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class RobotTemplate extends IterativeRobot {
    public static final int JOYSTICK_PORT = 1;
    
    public static final int PRESSURESWITCHCHANNEL = 1;
    public static final int RELAYSWITCHCHANNEL = 1;
    
    public static final int SHIFTSOLENOIDCHANNEL = 1;
    public static final int LOADERSOLENOIDCHANNEL = 2;

    
    public static final int DRIVEJAGCHANNEL1 = 1;
    public static final int DRIVEJAGCHANNEL2 = 2;
    public static final int DRIVEJAGCHANNEL3 = 3;
    public static final int DRIVEJAGCHANNEL4 = 4;
    public static final int LOADERJAGCHANNEL = 5;
    
    public static final int GYROCHANNEL = 8;
    
    public static final int WHEEL_ENCODER_A_CHANNEL = 6;
    public static final int WHEEL_ENCODER_B_CHANNEL = 7;
    
    public static final int ARM_POT_CHANNEL = 2;
    public static final int ARMJAGCHANNEL = 10;
    
    

    private Jaguar driveJag;
    private Jaguar driveJag2;
    private Jaguar driveJag3;
    private Jaguar driveJag4;
    private Solenoid shiftSolenoid;
    
    private Joystick stick;
    
    private TorbotJagDrive myTorbotJagDrive;
    private TorbotDrive myTorbotDrive;
    private TorbotShooter myTorShooter;
    
    private Compressor compressor;
    private Encoder wEncoder;
    private DriverStationLCD ds;
    private Gyro gyro;
    
    private AnalogChannel armPOT;
    private Talon armJag;
    
    private PIDController examplePID;
    
    private Jaguar loadJag;
    private Solenoid loaderBar;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void wait(double seconds){
    Timer time = new Timer();
    time.reset();
    time.start();
    while(time.get()<seconds){
        
    }
    time.stop();
    time.reset();
}
    public void robotInit() {
        ds = DriverStationLCD.getInstance();
        ds.clear();
        
        compressor = new Compressor(PRESSURESWITCHCHANNEL, RELAYSWITCHCHANNEL);
        compressor.start();

        driveJag = new Jaguar(DRIVEJAGCHANNEL1);
        driveJag2 = new Jaguar(DRIVEJAGCHANNEL2);
        driveJag3 = new Jaguar(DRIVEJAGCHANNEL3);
        driveJag4 = new Jaguar(DRIVEJAGCHANNEL4);
        shiftSolenoid = new Solenoid(SHIFTSOLENOIDCHANNEL);
        
        wEncoder = new Encoder(WHEEL_ENCODER_A_CHANNEL,WHEEL_ENCODER_B_CHANNEL);  //	Encoder ((int)aChannel,(int) bChannel, bool reverseDirection=false)
        gyro = new Gyro(GYROCHANNEL);
        gyro.reset();
        
        armPOT = new AnalogChannel(ARM_POT_CHANNEL);
        armJag = new Talon(ARMJAGCHANNEL);
        
        examplePID = new PIDController(0.05, 0.003, 0.0, armPOT, armJag, 0.05);
        examplePID.setContinuous(false);
       examplePID.setInputRange(GYROCHANNEL, GYROCHANNEL);
       examplePID.setOutputRange(JOYSTICK_PORT, JOYSTICK_PORT);
       examplePID.setSetpoint(armPOT.getAverageValue());
       examplePID.setPercentTolerance(3.5);
       examplePID.enable();
        
        loadJag = new Jaguar(LOADERJAGCHANNEL);
        loaderBar = new Solenoid(LOADERSOLENOIDCHANNEL);
        
        stick = new Joystick(JOYSTICK_PORT);
        
        myTorbotJagDrive = new TorbotJagDrive(driveJag,driveJag2,driveJag3,driveJag4);
        myTorbotDrive = new TorbotDrive(stick, myTorbotJagDrive, shiftSolenoid,wEncoder,gyro,ds);
        myTorShooter = new TorbotShooter(stick,loadJag,loaderBar);

    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        if(!examplePID.isEnable()){
            examplePID.enable();
        }
        examplePID.setSetpoint(420.0);
        while(!examplePID.onTarget()){
            
        }
        examplePID.disable();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {

        if (stick.getRawButton(7)) {
            myTorbotDrive.setReverseStatus(true);
        } else {
            myTorbotDrive.setReverseStatus(false);
        }
        myTorShooter.run();
        myTorbotDrive.ArcadeDrive(true);
        
        

    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }
}