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
    private Jaguar m_driveJag;
    private Jaguar m_driveJag2;
    private Jaguar m_driveJag3;
    private Jaguar m_driveJag4;
    private Solenoid m_solenoidshift;
    
    public TorbotDrive(Joystick stick, Jaguar driveJag, Jaguar driveJag2, Jaguar driveJag3, Jaguar driveJag4, Solenoid solenoidshift) {
        m_stick = stick;
        m_driveJag = driveJag;
        m_driveJag2 = driveJag2;
        m_driveJag3 = driveJag3;
        m_driveJag4 = driveJag4;
        m_solenoidshift = solenoidshift;             
    }
    
  public void ArcadeDrive (boolean squaredInputs)
  {
       
 
  
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
  if (Math.abs(stickX) <= .2)
    stickX = 0.0;
  if (Math.abs(stickY) <= .2)
    stickY = 0.0;

  // make sure X and Y don't go beyond the limits of -1 to 1
  if (stickX > 1.0)
    stickX = 1.0;
  if (stickX < -1.0)
    stickX = -1.0;

  if (stickY > 1.0)
    stickY = 1.0;
  if (stickY < -1.0)
    stickY = -1.0;


//    shift high/low drive gear
    if (shiftButton)
      {
        m_solenoidshift.set(true);
      }
    else
      {
        m_solenoidshift.set(false);
      }


  // square the inputs to produce an exponential power curve
  // this allows finer control with joystick movement and full power as you approach joystick limits
  if (squaredInputs)
    {
      if (stickX >= 0.0)
        stickX = (stickX*stickX);
      else
        stickX = -(stickX*stickX);

      if (stickY >= 0.0)
        stickY = (stickY*stickY);
      else
        stickY = -(stickY*stickY);
    }

  if (stickY > 0.0)
    {
      if (stickX > 0.0)
        {
          leftMotorSpeed = stickY - stickX;
          rightMotorSpeed = Math.max(stickY, stickX);
        }
      else
        {
          leftMotorSpeed = Math.max(stickY, -stickX);
          rightMotorSpeed = stickY + stickX;
        }
    }
  else
    {
      if (stickX > 0.0)
        {
          leftMotorSpeed = -Math.max(-stickY, stickX);
          rightMotorSpeed = stickY + stickX;
        }
      else
        {
          leftMotorSpeed = stickY - stickX;
          rightMotorSpeed = -Math.max(-stickY, -stickX);
        }
    }
  // set the motor speed
    m_driveJag.set(-leftMotorSpeed);
    m_driveJag2.set(-leftMotorSpeed);
    m_driveJag3.set(rightMotorSpeed);
    m_driveJag4.set(rightMotorSpeed);
    
    }
   public void ReverseArcadeDrive (boolean squaredInputs)
  {
       
 
  
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
  if (Math.abs(stickX) <= .2)
    stickX = 0.0;
  if (Math.abs(stickY) <= .2)
    stickY = 0.0;

  // make sure X and Y don't go beyond the limits of -1 to 1
  if (stickX > 1.0)
    stickX = 1.0;
  if (stickX < -1.0)
    stickX = -1.0;

  if (stickY > 1.0)
    stickY = 1.0;
  if (stickY < -1.0)
    stickY = -1.0;


//    shift high/low drive gear
    if (shiftButton)
      {
        m_solenoidshift.set(true);
      }
    else
      {
        m_solenoidshift.set(false);
      }


  // square the inputs to produce an exponential power curve
  // this allows finer control with joystick movement and full power as you approach joystick limits
  if (squaredInputs)
    {
      if (stickX >= 0.0)
        stickX = (stickX*stickX);
      else
        stickX = -(stickX*stickX);

      if (stickY >= 0.0)
        stickY = -(stickY*stickY);
      else
        stickY = (stickY*stickY);
    }

  if (stickY > 0.0)
    {
      if (stickX > 0.0)
        {
          leftMotorSpeed = stickY - stickX;
          rightMotorSpeed = Math.max(stickY, stickX);
        }
      else
        {
          leftMotorSpeed = Math.max(stickY, -stickX);
          rightMotorSpeed = stickY + stickX;
        }
    }
  else
    {
      if (stickX > 0.0)
        {
          leftMotorSpeed = -Math.max(-stickY, stickX);
          rightMotorSpeed = stickY + stickX;
        }
      else
        {
          leftMotorSpeed = stickY - stickX;
          rightMotorSpeed = -Math.max(-stickY, -stickX);
        }
    }
  // set the motor speed
    m_driveJag.set(-leftMotorSpeed);
    m_driveJag2.set(-leftMotorSpeed);
    m_driveJag3.set(rightMotorSpeed);
    m_driveJag4.set(rightMotorSpeed);
  
    }
  
    
}
