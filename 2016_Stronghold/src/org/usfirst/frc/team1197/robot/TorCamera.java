package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TorCamera
  implements PIDSource
{
  static double CENTER = 160.0D;
  NetworkTable m_networkTable;
  
  public TorCamera(NetworkTable tb)
  {
    this.m_networkTable = tb;
  }
  
  public void setPIDSourceType(PIDSourceType pidSource) {}
  
  public PIDSourceType getPIDSourceType()
  {
    return null;
  }
  
  public double pidGet()
  {
    double[] defaultValue = new double[0];
    
    double[] centerx = this.m_networkTable.getNumberArray("centerX", defaultValue);
    try
    {
      return CENTER - centerx[0];
    }
    catch (Exception e) {}
    return 0.0D;
  }
}
