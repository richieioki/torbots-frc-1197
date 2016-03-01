package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.PIDOutput;

public class TorCAN
  implements PIDOutput
{
  int numOfJags;
  CANTalon m_Rtalon1;
  CANTalon m_Rtalon2;
  CANTalon m_Rtalon3;
  CANTalon m_Ltalon1;
  CANTalon m_Ltalon2;
  CANTalon m_Ltalon3;
  
  public TorCAN(CANTalon R1, CANTalon R2, CANTalon L1, CANTalon L2)
  {
    this.numOfJags = 4;
    this.m_Rtalon1 = R1;
    this.m_Rtalon2 = R2;
    
    this.m_Ltalon1 = L1;
    this.m_Ltalon2 = L2;
  }
  
  public TorCAN(CANTalon R1, CANTalon L1)
  {
    this.numOfJags = 2;
    this.m_Rtalon1 = R1;
    this.m_Ltalon1 = L1;
  }
  
  public TorCAN(CANTalon R1, CANTalon R2, CANTalon R3, CANTalon L1, CANTalon L2, CANTalon L3)
  {
    this.numOfJags = 6;
    
    this.m_Rtalon1 = R1;
    this.m_Rtalon2 = R2;
    this.m_Rtalon3 = R3;
    
    this.m_Ltalon1 = L1;
    this.m_Ltalon2 = L2;
    this.m_Ltalon3 = L3;
  }
  
  public void SetDrive(double leftSpeed, double rightSpeed)
  {
    SetLeft(leftSpeed);
    SetRight(rightSpeed);
  }
  
  public void SetLeft(double speed)
  {
    if (this.numOfJags == 2)
    {
      this.m_Ltalon1.set(-speed);
    }
    else if (this.numOfJags == 4)
    {
      this.m_Ltalon1.set(-speed);
      this.m_Ltalon2.set(-speed);
    }
    else
    {
      this.m_Ltalon1.set(-speed);
      this.m_Ltalon2.set(-speed);
      this.m_Ltalon3.set(-speed);
    }
  }
  
  public void SetRight(double speed)
  {
    if (this.numOfJags == 2)
    {
      this.m_Rtalon1.set(speed);
    }
    else if (this.numOfJags == 4)
    {
      this.m_Rtalon1.set(speed);
      this.m_Rtalon2.set(speed);
    }
    else
    {
      this.m_Rtalon1.set(speed);
      this.m_Rtalon2.set(speed);
      this.m_Rtalon3.set(speed);
    }
  }
  
  public void pidWrite(double output)
  {
    SetRight(output);
    SetLeft(-output);
  }
}
