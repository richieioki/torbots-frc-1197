package torbots;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sun.squawk.io.BufferedWriter;
import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.WaitCommand;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;

/**
 *
 * @author torbots
 */

public class TorbotDrive {
    double wheelCircumference = 8.0*3.1416;
    double wheelGearRatio = 12.0/26.0;
    int encoderTicks = 360;
    private Joystick m_stick;
    private Jaguar m_driveJag;
    private Jaguar m_driveJag2;
    private Jaguar m_driveJag3;
    private Jaguar m_driveJag4;
    private Solenoid m_solenoidshift;
    private Solenoid m_solenoidshift2;
    private Encoder m_rightEncoder;
    private Encoder m_leftEncoder;
    private Gyro m_gyro;
    private DistanceObject[] list;
    
   WaitCommand wait = new WaitCommand(15.0);

    
    public TorbotDrive(Joystick stick, Jaguar driveJag, Jaguar driveJag2, Jaguar driveJag3, Jaguar driveJag4, Solenoid solenoidshift, Solenoid solenoidshift2, Encoder rEncoder, Encoder lEncoder, Gyro gyro) {
        m_stick = stick;
        m_driveJag = driveJag;
        m_driveJag2 = driveJag2;
        m_driveJag3 = driveJag3;
        m_driveJag4 = driveJag4;
        m_solenoidshift = solenoidshift;          
        m_solenoidshift2 = solenoidshift2;   
        m_rightEncoder = rEncoder;
        m_leftEncoder = lEncoder;
        m_gyro = gyro;
        list = new DistanceObject[1000];
        wait = new WaitCommand(15.0);
        

    }
    
    public void runWheels(double time,double speed){
        
        m_driveJag.set(speed);
        m_driveJag2.set(speed);
        m_driveJag3.set(-speed);
        m_driveJag4.set(-speed);
        
        
    }
    private void saveFile(String path, String name, String data) {
        try {
            String url = path + name;
            FileConnection fconn = (FileConnection)Connector.open(url, Connector.READ_WRITE);
            if (!fconn.exists()) {
                fconn.create();
            }
            OutputStream ops = fconn.openOutputStream();
            ops.write(data.getBytes());
            ops.close();
            fconn.close();
        }
        catch (IOException ioe) {
            System.out.println("IOException: "+ioe.getMessage());
        }
        catch (SecurityException se) {
            System.out.println("Security exception:" + se.getMessage());
        }
    } 
    public void driveStraightTest(){
        int index = 0;
        m_driveJag.set(0.9);
        m_driveJag2.set(0.9);
        m_driveJag.set(-0.9);
        m_driveJag.set(-0.9);
        m_rightEncoder.reset();
        m_leftEncoder.reset();
        m_gyro.reset();
        m_rightEncoder.start();
        m_leftEncoder.start();
        while(index < list.length-1){
            if(getDistance(m_rightEncoder)%  5.00 == 0.0){
                DistanceObject data = new DistanceObject(getDistance(m_rightEncoder),getDistance(m_leftEncoder),m_gyro.getAngle());
                list[index] = data;
                index++;
            } 
        }
        m_driveJag.set(0.0);
        m_driveJag2.set(0.0);
        m_driveJag.set(0.0);
        m_driveJag.set(0.0);
        String data = "";
        for(int i = 0; i<list.length -1; i++){
            DistanceObject a = list[i];
            data+=a.getX()+","+a.getY()+"," + a.getAngle() + "\n";
        }
        saveFile("file:/C:/Users/torbots/Desktop", "DistanceData",data);
    }
  public double getDistance(Encoder e){
      return -1*e.getRaw()*wheelCircumference*wheelGearRatio/(encoderTicks*4); 
  }
 
  public void ArcadeDrive (boolean squaredInputs)
  {
       
 
  
  boolean shiftButton = m_stick.getRawButton(2); //Button 2

  double leftMotorSpeed;
  double rightMotorSpeed;

  // get negative of the stick controls. forward on stick gives negative value  
  double stickX = m_stick.getX();
  double stickY = m_stick.getY();
  
  stickX = -stickX;
  stickY = -stickY;
// shiftButton = m_stick.getRawButton(1);
  

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
        m_solenoidshift.set(false);
        m_solenoidshift2.set(true);
      }
    else
      {
        m_solenoidshift.set(true);
        m_solenoidshift2.set(false);
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
