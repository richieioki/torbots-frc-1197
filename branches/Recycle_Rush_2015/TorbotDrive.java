package Torbots;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author torbots
 */
public class TorbotDrive {

    private Joystick m_stick;
    private Joystick m_stick2;
    private TorJagDrive m_jagDrive;

    public TorbotDrive(Joystick stick, TorJagDrive jagDrive) {
        m_stick = stick;
        m_jagDrive = jagDrive;
    }
    public TorbotDrive(Joystick stick1, Joystick stick2, TorJagDrive jagDrive){
        m_stick = stick1;
        m_stick2 = stick2;
        m_jagDrive = jagDrive;
    }

    public void TankDrive(){
         double stickY = getStickY(m_stick);
        double stickY2 = getStickY(m_stick2);
        m_jagDrive.setJagSpeed(stickY, stickY2);
        
    }
    public double getStickY(Joystick stick){
        double x = -stick.getY();
        if(Math.abs(x) > 0.5){
            x = Math.abs(x)/x;
        }
        return x;
    }

    public void wait(double t){
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - startTime) / 1000.0 < t){
            
        }
    }

    public void ArcadeDrive(boolean squaredInputs) {

        double leftMotorSpeed;
        double rightMotorSpeed;

        // get negative of the stick controls. forward on stick gives negative value  
        double stickX = -m_stick.getX();
        double stickY = -m_stick.getY();

        //shiftButton = m_stick.getRawButton(1);


        // adjust joystick by dead zone
        if (Math.abs(stickX) <= 0.2 && (Math.abs(stickY)) <= 0.2) {
            stickX = 0.0;
            stickY = 0.0;
        }
       

        // make sure X and Y don't go beyond the limits of -1 to 1
        if(Math.abs(stickX) > 1.0){
        	stickX = Math.abs(stickX)/stickX;
        }
        if(Math.abs(stickY) > 1.0){
        	stickY = Math.abs(stickY)/stickY;
        }



        // square the inputs to produce an exponential power curve
        // this allows finer control with joystick movement and full power as you approach joystick limits
        if (squaredInputs) {
            if (stickX >= 0.0) {
                stickX = (stickX * stickX);
            } else {
                stickX = -(stickX * stickX);
            }

            if (stickY >= 0.0) {
                stickY = (stickY * stickY);
            } else {
                stickY = -(stickY * stickY);
            }
        }

        if(Math.abs(stickY) < 0.1){
            leftMotorSpeed = -stickX;
            rightMotorSpeed = stickX;
        }
        else if (stickY > 0.0) {
            if (stickX > 0.0) {
            	leftMotorSpeed = stickY * (1-stickX);
                rightMotorSpeed = stickY;
            } else {
            	leftMotorSpeed = stickY;
                rightMotorSpeed = stickY * ( 1 + stickX );
                
            }
        } else {
            if (stickX > 0.0) {
            	leftMotorSpeed = stickY * (1-stickX);
                rightMotorSpeed = stickY;
            } else {
            	leftMotorSpeed = stickY;
                rightMotorSpeed = stickY * (1+stickX);
                
            }
        }
        SmartDashboard.putDouble("right", rightMotorSpeed);
        SmartDashboard.putDouble("left", leftMotorSpeed);
        m_jagDrive.setJagSpeed(rightMotorSpeed, -leftMotorSpeed);

    }
}
