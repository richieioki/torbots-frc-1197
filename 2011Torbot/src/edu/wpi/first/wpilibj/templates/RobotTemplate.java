/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

import torbots.TorbotDrive;

public class RobotTemplate extends IterativeRobot {

    private Jaguar driveJag;
    private Jaguar driveJag2;
    private Jaguar driveJag3;
    private Jaguar driveJag4;
    private Solenoid shiftSolenoid1;
    private Solenoid shiftSolenoid2;
//    private Compressor compressor;
    private Joystick stick;
    private TorbotDrive myTorbotDrive;
    private Compressor compressor;
    private Encoder rightEncoder;
    private Encoder leftEncoder;
    private Gyro gyro;
    private DriverStationLCD ds;

    public void robotInit() {
        
        compressor = new Compressor(1, 1);
        driveJag = new Jaguar(7);
        driveJag2 = new Jaguar(8);
        driveJag3 = new Jaguar(9);
        driveJag4 = new Jaguar(10);
        shiftSolenoid1 = new Solenoid(1);
        shiftSolenoid2 = new Solenoid(2);
        rightEncoder = new Encoder(1,1);
        leftEncoder = new Encoder(2,2);
        gyro = new Gyro(1);
        compressor.start();
        stick = new Joystick(1);
        myTorbotDrive = new TorbotDrive(stick, driveJag, driveJag2, driveJag3, driveJag4, shiftSolenoid1,shiftSolenoid2,rightEncoder,leftEncoder,gyro);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        myTorbotDrive.driveStraightTest();
        
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        myTorbotDrive.ArcadeDrive(true);
        
//        shiftSolenoid.set(stick.getRawButton(1));
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }
}
