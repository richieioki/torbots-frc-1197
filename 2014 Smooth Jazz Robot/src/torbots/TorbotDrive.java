package torbots;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.wpi.first.wpilibj.*;

/**
 *
 * @author torbots
 */
public class TorbotDrive {

    private Joystick m_stick;
    private Joystick m_stick2;
//    private Jaguar m_driveJag;
//    private Jaguar m_driveJag2;
//    private Jaguar m_driveJag3;
//    private Jaguar m_driveJag4;
    private Solenoid m_solenoidshift;
    private TorJagDrive m_jagDrive;

    public TorbotDrive(Joystick stick, TorJagDrive jagDrive, Solenoid solenoidshift) {
        m_stick = stick;
//        m_driveJag = driveJag;
//        m_driveJag2 = driveJag2;
//        m_driveJag3 = driveJag3;
//        m_driveJag4 = driveJag4;
        m_solenoidshift = solenoidshift;
        m_jagDrive = jagDrive;
    }
    public TorbotDrive(Joystick stick1, Joystick stick2, TorJagDrive jagDrive, Solenoid solenoidshift){
        m_stick = stick1;
        m_stick2 = stick2;
        m_solenoidshift = solenoidshift;
        m_jagDrive = jagDrive;
    }

    public void TankDrive(){
         double stickY = getStickY(m_stick);
        double stickY2 = getStickY(m_stick2);
        m_jagDrive.setJagSpeed(stickY, stickY2);
        
    }
    public double getStickY(Joystick stick){
        double x = stick.getY();
        x = -x;
        if(x>1.0){
            x = 1.0;
        }
        if(x<-1.0){
            x = -1.0;
        }
        return x;
    }

    public void ArcadeDrive(boolean squaredInputs) {

        boolean shiftButton = false; //Button 2

        double leftMotorSpeed;
        double rightMotorSpeed;

        // get negative of the stick controls. forward on stick gives negative value  
        double stickX = m_stick.getX();
        double stickY = m_stick.getY();

        stickX = -stickX;
        stickY = -stickY;
        //shiftButton = m_stick.getRawButton(1);


        // adjust joystick by dead zone
        if (Math.abs(stickX) <= 0.2 && (Math.abs(stickY)) <= 0.2) {
            stickX = 0.0;
            stickY = 0.0;
        }
       

        // make sure X and Y don't go beyond the limits of -1 to 1
        if (stickX > 1.0) {
            stickX = 1.0;
        }
        if (stickX < -1.0) {
            stickX = -1.0;
        }

        if (stickY > 1.0) {
            stickY = 1.0;
        }
        if (stickY < -1.0) {
            stickY = -1.0;
        }


//    shift high/low drive gear
        //  if (shiftButton)
        //{
        //  m_solenoidshift.set(true);
        //  }
        //  else
        //  {
        //      m_solenoidshift.set(false);
//      }


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
        if (Math.abs(stickY) < 0.2){
            leftMotorSpeed = stickX;
            rightMotorSpeed = -stickX;
        }
        else if (stickY > 0.0) {
            if (stickX > 0.0) {
                leftMotorSpeed = stickY;
                rightMotorSpeed = stickY * (1-stickX);
            } else {
                leftMotorSpeed = stickY *(1+stickX);
                rightMotorSpeed = stickY ;
            }
        } else {
            if (stickX > 0.0) {
                leftMotorSpeed = stickY;
                rightMotorSpeed = stickY * (1-stickX);
            } else {
                leftMotorSpeed = stickY * (1+stickX); 
                rightMotorSpeed =stickY;
            }
        }
        // set the motor speed
//    m_driveJag.set(-leftMotorSpeed);
//    m_driveJag2.set(-leftMotorSpeed);
//    m_driveJag3.set(rightMotorSpeed);
//    m_driveJag4.set(rightMotorSpeed);
        m_jagDrive.setJagSpeed(rightMotorSpeed, -leftMotorSpeed);

    }

    public void ReverseArcadeDrive(boolean squaredInputs) {



        boolean shiftButton = false; //Button 2

        double leftMotorSpeed;
        double rightMotorSpeed;

        // get negative of the stick controls. forward on stick gives negative value  
        double stickX = m_stick.getX();
        double stickY = m_stick.getY();

        stickX = -stickX;
        stickY = -stickY;
        shiftButton = m_stick.getRawButton(1);


        // adjust joystick by dead zone
        if (Math.abs(stickX) <= .2 && Math.abs(stickY) <= .2) {
            stickX = 0.0;
            stickY = 0.0;
        }

        // make sure X and Y don't go beyond the limits of -1 to 1
        if (stickX > 1.0) {
            stickX = 1.0;
        }
        if (stickX < -1.0) {
            stickX = -1.0;
        }

        if (stickY > 1.0) {
            stickY = 1.0;
        }
        if (stickY < -1.0) {
            stickY = -1.0;
        }


//    shift high/low drive gear
        if (shiftButton) {
            m_solenoidshift.set(true);
        } else {
            m_solenoidshift.set(false);
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
                stickY = -(stickY * stickY);
            } else {
                stickY = (stickY * stickY);
            }
        }

//        if (stickY > 0.0) {
//            if (stickX > 0.0) {
//                leftMotorSpeed = stickY - stickX;
//                rightMotorSpeed = (Math.max(stickY, stickX)) * 20;
//            } else {
//                leftMotorSpeed = (Math.max(stickY, -stickX)) * 20;
//                rightMotorSpeed = stickY + stickX;
//            }
//        } else {
//            if (stickX > 0.0) {
//                leftMotorSpeed = (-Math.max(-stickY, stickX)) * 20;
//                rightMotorSpeed = stickY + stickX;
//            } else {
//                leftMotorSpeed = stickY - stickX;
//                rightMotorSpeed = (-Math.max(-stickY, -stickX)) * 20;
//            }
//        }
        //System.out.println(stickY);
        if(Math.abs(stickY) < 0.2){
            leftMotorSpeed = stickX;
            rightMotorSpeed = -stickX;
        }
        else if (stickY > 0.0) {
            if (stickX > 0.0) {
                leftMotorSpeed = stickY;
                rightMotorSpeed = stickY * ( 1 - stickX );
            } else {
                leftMotorSpeed = stickY * (1+stickX);
                rightMotorSpeed = stickY;
            }
        } else {
            if (stickX > 0.0) {
                leftMotorSpeed = stickY;
                rightMotorSpeed = stickY * (1-stickX);
            } else {
                leftMotorSpeed = stickY * (1+stickX);
                rightMotorSpeed = stickY;
            }
        }
        
        
        // set the motor speed
//    m_driveJag.set(-leftMotorSpeed);
//    m_driveJag2.set(-leftMotorSpeed);
//    m_driveJag3.set(rightMotorSpeed);
//    m_driveJag4.set(rightMotorSpeed);
        m_jagDrive.setJagSpeed(rightMotorSpeed, -leftMotorSpeed);

    }
}
