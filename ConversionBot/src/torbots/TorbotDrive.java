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
    private TorbotJagDrive m_TorJagDrive;
    private Solenoid m_solenoidshift;
    private Encoder m_encoder;
    private Gyro m_gyro;
    private DriverStationLCD m_ds;
    
    private boolean reversed;
    
    public TorbotDrive(Joystick stick, TorbotJagDrive torJagDrive, Solenoid solenoidshift, 
            Encoder wheelEncoder, Gyro gyro, DriverStationLCD ds) {
        m_stick = stick;
        m_TorJagDrive = torJagDrive;
        m_solenoidshift = solenoidshift;       
        m_encoder = wheelEncoder;
        m_gyro = gyro;
        reversed = false;
        m_ds = ds;
    }
    
  public void setReverseStatus(boolean reverse){
      reversed = reverse;
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
  shiftButton = m_stick.getRawButton(2);

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
   
  m_solenoidshift.set(shiftButton);
     


  // square the inputs to produce an exponential power curve
  // this allows finer control with joystick movement and full power as you approach joystick limits
  if (squaredInputs)
    {
      if (stickX >= 0.0)
        stickX = (stickX*stickX);
      else
        stickX = -(stickX*stickX);
      if(!reversed) {
        if (stickY >= 0.0)
         stickY = (stickY*stickY);
        else
         stickY = -(stickY*stickY);
    }
      else{
          if (stickY >= 0.0)
               stickY = -(stickY*stickY);
          else
               stickY = (stickY*stickY);
      }
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
  if(!reversed){
    m_TorJagDrive.SetDrive(leftMotorSpeed, rightMotorSpeed);
  }
  else{
     m_TorJagDrive.SetDrive(-leftMotorSpeed, -rightMotorSpeed); 
  }
  
    
    }
 public void DriveToTheta(double theta, double motorSpeed, double distanceInches)
{
  double angleError = 0.0;
  double angleTarget;
  double motorAdjust;
  
  Timer timer;
  double timeCurr = 0.0;

  timer = new Timer();
  timer.reset();
  timer.start();
  angleTarget = theta;  // adjusted target angle; add current ange to desired angle. resetting may not set to 0.0 exactly

  // drive the desired distance
  resetEncoder();

  
  m_ds.clear();
    
  while (Math.abs(getDistance()) < Math.abs(distanceInches))
    {
        
      angleError = m_gyro.getAngle()-angleTarget;       // error off of adjusted target heading
      motorAdjust = angleError/10.0; // percent of error range (5 degrees) 

      //use these lines to use corrections
      m_TorJagDrive.SetLeft(motorSpeed*(1.0-motorAdjust));
      m_TorJagDrive.SetRight(motorSpeed*(1.0+motorAdjust));
      
   
            
      wait(0.05);
      // if this run takes more than 3 seconds, we probably ran into something. Stop and backup.
      if ((timer.get() - timeCurr) >= 10.0)                                                     // should not take more than 3 seconds
        {
          // stop, back up a bit and exit this loop
          m_TorJagDrive.SetDrive(0.0,0.0);                                                         // stop
          m_TorJagDrive.SetDrive(-motorSpeed, -motorSpeed);                                        // backup
          wait(0.25);
          m_TorJagDrive.SetDrive(0.0,0.0);                                                         // stop
                              
          break;                                                                                // exit      
        }// end time-out check
    } // end while

  // Stop motors
  m_TorJagDrive.SetDrive(0.0, 0.0);

   
} // end driveToTheta


public void DriveStraight(double motorSpeed, double distanceInches)
{
  double currentAngle = 0.0;
  
  // get the current heading and use this as the direction to drive
  currentAngle = m_gyro.getAngle();
  DriveToTheta(currentAngle, motorSpeed, distanceInches); // maintain currentAngle
  
}



double wheelCircumference = 4.0*3.1416;
double wheelGearRatio_High = 1.0/11.733;
double encoderTicks = 250.0;

public double getDistance() {
  double encoderDistance;
  encoderDistance = ((double)m_encoder.getRaw()*wheelCircumference*wheelGearRatio_High)/((encoderTicks*4));
  
  return encoderDistance;
}

public void resetEncoder() {
  m_encoder.reset();
}
public void wait(double seconds){
    Timer time = new Timer();
    time.reset();
    time.start();
    while(time.get() < seconds){
        
    }
    time.stop();
    time.reset();
}

}
  