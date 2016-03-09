package org.usfirst.frc.team1197.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TorCamera
  implements PIDSource
{
  static double CENTER = 80.0;
  NetworkTable m_networkTable;
  AHRS ahrs;
  private double targetArea = 0;
  private double targetX = 0;
  private double deltaX = 0;
  private double angleDeltaX = 0;
  private double targetAngle = 0;
  private double ahrsAngle = 0;
  private Joystick stick3;
  private TorLidar lidar;
  public TorCAN torcan;
  private TorSiege siege;
  public static enum CAMERA {IDLE, TURN;}
  public CAMERA m_camera = CAMERA.IDLE; 
  public TorCamera(NetworkTable tb, AHRS ahrs, TorCAN torcan, TorSiege siege, Joystick stick3, TorLidar lidar)
  {
	this.lidar = lidar;
	this.siege = siege;
	this.torcan = torcan;
	this.ahrs = ahrs;
	tb = NetworkTable.getTable("GRIP/myContoursReport");
    this.m_networkTable = tb;
    this.stick3 = stick3;
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
  
  public double GetValue(){
	  
	  double[] defaultValue = new double[0];
	  double[] centerx = m_networkTable.getNumberArray("centerX", defaultValue);
	  double[] areas = m_networkTable.getNumberArray("area", defaultValue);
	  double value = centerx[0] + 20;
	  System.out.println("VALUE: " + value);
	  if(value < CENTER){
		  deltaX = CENTER - value;
		  angleDeltaX = deltaX * 0.209375;
		  ahrs.reset();
		  System.out.println("ANGLE CHECK: " + ahrs.getAngle());
		  return angleDeltaX;
	  }
	  else if(value > CENTER){
		  value = value + 42;
		  deltaX = value - CENTER;
		  angleDeltaX = deltaX * 0.209375;
		  ahrs.reset();
		  System.out.println("ANGLE CHECK: " + ahrs.getAngle());
		  return angleDeltaX;
	  }
	  else{
		  System.out.println("ERROR");
		  return 0;
	  }
//	  deltaX = targetX - CENTER;
//	  angleDeltaX = deltaX * 0.209375;
//	  targetAngle = angleMapper() + angleDeltaX;
//	  
//	  targetArea = -1.0;
//	  targetX = 0.0;
//	  for(int i=0; i<areas.length; i++){
//		  if(areas[i] > targetArea){
//			  targetArea = areas[i];
//			  targetX = centerx[i];
//		  }
//	  }
//	  return targetX;
  }
  
  public double angleMapper(){
	  ahrsAngle = ahrs.getAngle();
	  if(ahrsAngle > 180){
		  ahrsAngle = 360 - ahrsAngle;
	  }
	  return ahrsAngle;
  }
  
  public void AutoShoot() {
//	  double errorAngle = targetAngle - angleMapper();
//	  System.out.println("ErrorAngle: " + errorAngle);
//	  if(targetArea < 0.0){
//		  return;
//	  }
//	  if(errorAngle > 1){ //need adjustment
//		  torcan.SetDrive(-0.5, -0.5);
//	  }
//	  else if(errorAngle < 1){
//		  torcan.SetDrive(0.5, 0.5);
//	  }
//	  else{
//		  torcan.SetDrive(0, 0);
//		  //some method to shoot
//		  return;
//	  }
	  double[] defaultValue = new double[0];
	  double[] centerx = m_networkTable.getNumberArray("centerX", defaultValue);
	  double value = centerx[0];
	  m_camera = CAMERA.TURN;
	  switch(m_camera){
	  case IDLE:
		  break;
	  case TURN: 
		  if(value < CENTER){
			  System.out.println("Delta Angle HELLO: " + angleDeltaX);
			  System.out.println("Angle: " + ahrs.getAngle());
			  while(360 - angleDeltaX < ahrs.getAngle()){
			  	siege.turnToTheta(ahrs.getAngle() - angleDeltaX);
			  }
			  torcan.SetDrive(0, 0);
		  }
		  else if(value > CENTER){
			  System.out.println("Delta Angle HI: " + angleDeltaX);
			  System.out.println("Angle: " + ahrs.getAngle());
			  while(angleDeltaX > ahrs.getAngle()){
				siege.turnToTheta(ahrs.getAngle() + angleDeltaX);
			  }
			  torcan.SetDrive(0, 0);
	      }
	  }
  }
  public void cameraUpdate(){
	  if(stick3.getRawButton(11)){
    	  GetValue();
      }
      if(stick3.getRawButton(12)){
    	  AutoShoot();
      }
  }
  public boolean checkTable(){
	  double[] defaultValue = new double[0];
	  double[] centerx = m_networkTable.getNumberArray("centerX", defaultValue);
	  System.out.println("CENTERX: " + centerx[0]);
	  return true;
  }
  
}
