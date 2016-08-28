package org.usfirst.frc.team1197.robot;

public class TorJoystickProfiles {
	
	private double k = 0.3;
	private double A = (k * k) / (1 - (2 * k));
	private double B = (k - 1) * (k - 1) / (k * k);	
	
	private double maxTurnRadius = 10.0;
	private double minTurnRadius = 0.5;
	private double steeringDeadBand = 0.1;
	private double C = (Math.log(minTurnRadius / maxTurnRadius)) / (steeringDeadBand - 1);
	private double D = maxTurnRadius * Math.exp(C * steeringDeadBand);
	private double negSteeringInertia = 0.0;
	private double previous_r = 0.0;
	private double r = 0.0;
	
	public TorJoystickProfiles(){
		
	}
	
	public double findRadiusExponential(double x){
		r = (Math.abs(x) / x) * (D * Math.exp(-C * Math.abs(x)));
		negSteeringInertia = (r - previous_r) * 100;
		previous_r = r;
		
		if(x == 0.0){
			return 0.0;
		}
		else{
			return r + negSteeringInertia;
		}
	}
	
	public double findSpeed(double throttleAxis){
		if(throttleAxis >= 0){
			return (A) * (Math.pow(B, throttleAxis) - 1);
		}
		else{
			return -(A) * (Math.pow(B, -throttleAxis) - 1);
		}
	}
	
	public double getMinTurnRadius(){
		return minTurnRadius;
	}
}
