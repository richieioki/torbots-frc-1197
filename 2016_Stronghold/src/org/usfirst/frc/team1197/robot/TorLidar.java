package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;

public class TorLidar
{
  SerialPort m_port;
  
  public TorLidar(SerialPort sp)
  {
    this.m_port = sp;
  }
  
  public int getDistance()
  {
    this.m_port.writeString("r\n");
    Timer.delay(0.05D);
    String distance = this.m_port.readString();
    if (distance.trim().length() >= 4)
    {
      distance = distance.substring(0, 3);
    }
    else
    {
      if (distance.length() < 2) {
        return 0;
      }
      distance = distance.trim();
    }
    try
    {
      return new Integer(distance).intValue();
    }
    catch (NumberFormatException nfe)
    {
      DriverStation.reportError("ERROR STRING " + distance + "  LENGITH " + distance.length(), false);
    }
    return 0;
  }
}
