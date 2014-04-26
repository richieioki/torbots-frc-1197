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

    private Jaguar driveJag;
    private Jaguar driveJag2;
    private Jaguar driveJag3;
    private Jaguar driveJag4;
    private Solenoid shiftSolenoid;
    private Joystick stick;
    private TorbotDrive myTorbotDrive;
    private TorShooter myTorShooter;
    private Compressor compressor;
    
    private Jaguar loadJag;
    private Solenoid loaderBar;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        compressor = new Compressor(1, 1);
        driveJag = new Jaguar(1);
        driveJag2 = new Jaguar(2);
        driveJag3 = new Jaguar(3);
        driveJag4 = new Jaguar(4);
        shiftSolenoid = new Solenoid(1);
        compressor.start();
        loadJag = new Jaguar(5);
        loaderBar = new Solenoid(2);
        stick = new Joystick(1);
        myTorbotDrive = new TorbotDrive(stick, driveJag, driveJag2, driveJag3, driveJag4, shiftSolenoid);
        myTorShooter = new TorShooter(stick,loadJag,loaderBar);

    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {

        if (stick.getRawButton(7)) {

            myTorbotDrive.ReverseArcadeDrive(true);
        } else {
            myTorbotDrive.ArcadeDrive(true);
        }

        shiftSolenoid.set(stick.getRawButton(2));
        myTorShooter.run();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    }
}
