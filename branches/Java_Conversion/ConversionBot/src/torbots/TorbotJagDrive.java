/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package torbots;
import edu.wpi.first.wpilibj.*;


/**
 *
 * @author Family
 */
public class TorbotJagDrive {
    
    int numOfJags;
    Jaguar m_leftJag1;
    Jaguar m_leftJag2;
    Jaguar m_rightJag1;
    Jaguar m_rightJag2;
 public TorbotJagDrive(Jaguar leftJag1,Jaguar leftJag2, Jaguar rightJag1, Jaguar rightJag2) 
{
  numOfJags = 4;
  m_leftJag1 = leftJag1;
  m_leftJag2 = leftJag2;
  m_rightJag1 = rightJag1;
  m_rightJag2 = rightJag2;
}

public TorbotJagDrive(Jaguar leftJag, Jaguar rightJag)
{
  numOfJags = 2;  
    m_leftJag1 = leftJag;
  m_leftJag2 = leftJag;
  m_rightJag1 = rightJag;
  m_rightJag2 = rightJag;
}

public void SetDrive(double leftSpeed, double rightSpeed)
{
  SetLeft(leftSpeed);
  SetRight(rightSpeed);
}

public void SetLeft(double speed)
{
  // left motors take positive speed to move forward
  if (numOfJags == 2)
    {
      m_leftJag1.set(-speed);
    }
  else
    {
      m_leftJag1.set(-speed);
      m_leftJag2.set(-speed);
    }
  //m_left1.Set(-speed);
  //m_left2.Set(-speed);
}

public void SetRight(double speed)
{
  // right motors are inverted. Change sign for driving right side
  if (numOfJags == 2)
      {
        m_rightJag1.set(speed);
      }
  else
      {
        m_rightJag1.set(speed);
        m_rightJag2.set(speed);
      }
  //m_right1.Set(speed);
  //m_right2.Set(speed);
}


    
}
