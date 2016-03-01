package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;

public class TorOnTarget
{
  private CANTalon pidTalon;
  private int tolerance;
  
  public TorOnTarget(CANTalon talon, int tolerance)
  {
    this.pidTalon = talon;
    this.tolerance = tolerance;
  }
  
  public boolean siegeOnTarget()
  {
    int rawValue = this.pidTalon.getAnalogInRaw();
    if ((rawValue > this.pidTalon.getSetpoint() - this.tolerance) && (rawValue < this.pidTalon.getSetpoint() + this.tolerance)) {
      return true;
    }
    return false;
  }
  
  public boolean onTargetRaw()
  {
    int rawValue = this.pidTalon.getAnalogInRaw();
    if ((rawValue > this.pidTalon.getSetpoint() - this.tolerance) && (rawValue < this.pidTalon.getSetpoint() + this.tolerance)) {
      return true;
    }
    return false;
  }
}
