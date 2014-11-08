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
import java.util.Timer;

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
    private Solenoid shiftSolenoid2;
    private Joystick stick;
    private TorbotDrive myTorbotDrive;
    private TorShooter myTorShooter;
    private Compressor compressor;
    private TorJagDrive myTorJagDrive;
    
    private Jaguar loadJag;
    private Solenoid loaderBar;
    
    private boolean stickin;

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
        shiftSolenoid2 = new Solenoid(8);
        compressor.start();
//        loadJag = new Jaguar(5);
//        loaderBar = new Solenoid(2);
        stick = new Joystick(1);
        myTorJagDrive = new TorJagDrive(driveJag,driveJag2,driveJag3,driveJag4);
        myTorbotDrive = new TorbotDrive(stick, myTorJagDrive, shiftSolenoid);
        //myTorShooter = new TorShooter(stick,loadJag,loaderBar);
        
        stickin = false;
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

        if(stick.getRawButton(5)) {
            stickin = true;
            System.out.println("Linear");
        } else if(stick.getRawButton(6)) {
            stickin = false;
            System.out.println("Squared");
        }
        
//        if (stick.getRawButton(7)) {

//            myTorbotDrive.ReverseArcadeDrive(true);
//        } else {
        myTorbotDrive.ArcadeDrive(stickin);
        
        
//        }
        
        shiftSolenoid.set(stick.getRawButton(1));
        shiftSolenoid2.set(stick.getRawButton(1));
//        myTorShooter.run();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
}
