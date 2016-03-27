package org.usfirst.frc.team1197.robot;

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
    if (distance.length() == 0) {
      return -1;
    }
    
    int start = 0;
    char[] dist = distance.toCharArray();
    for (int i = 0; i < distance.length(); i++) {
      if (dist[i] != '0')
      {
        start = i;
        break;
      }
    }
    int end = 0;
    for (int i = start; i < distance.length(); i++) {
      if (dist[i] == '\n')
      {
        end = i - 1;
        break;
      }
    }
    distance = distance.substring(start, end);
    
    Integer intDist = new Integer(distance);
    
    return intDist.intValue();
  }
}

