package org.usfirst.frc.team1197.robot;

public class TorJoystickProfiles {
	
	private double trackWidth = 0.5525; //meters, in inches 21.75
	private double halfTrackWidth = trackWidth / 2;
	private double steeringDeadBand = 0.1;
	private double centerRadius = 0.0;
	
	private double k = 0.3;
	private double minTurnRadius = 0.5;
	private double maxTurnRadius = 10.0;
	private double maxThrottle = (5.0/6.0) * (minTurnRadius / (minTurnRadius + halfTrackWidth));
	private double steeringConstant = (maxTurnRadius - minTurnRadius) / 
			Math.tan((Math.PI / 2) * (1 - steeringDeadBand));
	
	private double b = (steeringDeadBand * minTurnRadius - maxTurnRadius) / (steeringDeadBand - 1);
	private double m = (maxTurnRadius - b) / steeringDeadBand;
	
	private double E = (Math.log(minTurnRadius / maxTurnRadius)) / (steeringDeadBand - 1);
	private double D = maxTurnRadius * Math.exp(E * steeringDeadBand);
	private double r = 0.0;
	private double previous_r = 0.0;
	private double negSteeringInertia = 0.0;
	
	public TorJoystickProfiles(){
		
	}
	
	public double findRadius(double carSteeringAxis){
		if(carSteeringAxis == 0.0){
			return 0.0;
		}
		else{
			return steeringConstant * (Math.tan((Math.PI / 2) * (1 - carSteeringAxis))) + 
					(minTurnRadius * (Math.abs(carSteeringAxis) / carSteeringAxis));
		}
	}
	public double findRadiusLinear(double carSteeringAxis){
		if(carSteeringAxis == 0.0){
			return 0.0;
		}
		else{
			return m * carSteeringAxis + (b * (Math.abs(carSteeringAxis) / carSteeringAxis));
		}
	}
	public double findRadiusExponential(double x){
		r = (Math.abs(x) / x) * (D * Math.exp(-E * Math.abs(x)));
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
			return ((k * k) / (1 - (2 * k))) * (Math.pow(((k - 1) * (k - 1) / (k * k)), throttleAxis) - 1);
		}
		else{
			return -((k * k) / (1 - (2 * k))) * (Math.pow(((k - 1) * (k - 1) / (k * k)), -throttleAxis) - 1);
		}
	}
}
