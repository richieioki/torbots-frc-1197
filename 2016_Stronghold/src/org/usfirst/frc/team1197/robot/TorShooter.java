package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;

public class TorShooter
{
  TorCAN cans;
  TorIntake intake;
  CANTalon shooter1;
  CANTalon shooter2;
  CANTalon hood;
  CANTalon elevate;
  CANTalon arm;
  Joystick stick3;
  Joystick stick2;
  AHRS gyro;
  public boolean shooterEnabled;
  private ShooterState m_state;
  float angleToTurn;
  
  private static enum ShooterState
  {
    TURNING,  MANUAL;
    
    private ShooterState() {}
  }
  
  public TorShooter(TorIntake intake, CANTalon shooter1, CANTalon shooter2, CANTalon hood, CANTalon elevate, CANTalon arm, Joystick stick3, Joystick stick2, AHRS gyro, TorCAN can)
  {
    this.intake = intake;
    this.shooter1 = shooter1;
    this.shooter2 = shooter2;
    this.hood = hood;
    this.elevate = elevate;
    this.arm = arm;
    this.stick3 = stick3;
    this.stick2 = stick2;
    this.gyro = gyro;
    this.cans = can;
    this.shooterEnabled = false;
    this.m_state = ShooterState.MANUAL;
  }
  
  public void shoot()
  {
    if (this.stick3.getRawButton(1))
    {
      this.shooter1.set(0.75D);
      this.shooter2.set(0.75D);
    }
    else
    {
      this.shooter1.set(0.0D);
      this.shooter2.set(0.0D);
    }
  }
  
  public void hood()
  {
    if (this.stick3.getY() < -0.05D) {
      this.hood.set(-this.stick3.getY() * 50000.0D + this.hood.getPulseWidthPosition());
    }
  }
  
  public void adjustShooter()
  {
    if (this.stick3.getRawButton(1))
    {
      shoot();
    }
    else if (this.stick3.getRawButton(2))
    {
      this.m_state = ShooterState.TURNING;
      this.gyro.reset();
      this.angleToTurn = 25.0F;
    }
    else if (this.stick3.getRawButton(3))
    {
      this.m_state = ShooterState.TURNING;
      this.gyro.reset();
      this.angleToTurn = 15.0F;
    }
    else if (this.stick3.getRawButton(4))
    {
      this.m_state = ShooterState.TURNING;
      this.gyro.reset();
      this.angleToTurn = 5.0F;
    }
    else if (this.stick3.getY() > 0.05D)
    {
      this.hood.set(-this.stick3.getY() * 50000.0D + this.hood.getPulseWidthPosition());
    }
    else if (this.stick3.getRawButton(5))
    {
      this.m_state = ShooterState.TURNING;
      this.gyro.reset();
      this.angleToTurn = 335.0F;
    }
    else if (this.stick3.getRawButton(6))
    {
      this.m_state = ShooterState.TURNING;
      this.gyro.reset();
      this.angleToTurn = 345.0F;
    }
    else if (this.stick3.getRawButton(7))
    {
      this.m_state = ShooterState.TURNING;
      this.gyro.reset();
      this.angleToTurn = 355.0F;
    }
    else
    {
      this.hood.set(this.hood.getPulseWidthPosition());
    }
  }
  
  public void update()
  {
    if (this.stick2.getRawButton(1))
    {
      this.shooterEnabled = true;
    }
    else
    {
      this.shooterEnabled = false;
      shooterReset();
    }
    if (this.shooterEnabled) {
      adjustShooter();
    }
  }
  
  private void shooterReset()
  {
    this.m_state = ShooterState.MANUAL;
    this.shooter1.set(0.0D);
    this.shooter2.set(0.0D);
  }
}
