package Torbots;

import edu.wpi.first.wpilibj.Joystick;

public class TorbotDrive {
	
	TorJagDrive myJagDrive;
	Joystick theStick;
	Joystick theStick2;
	
	public TorbotDrive(TorJagDrive torbotJagDrive, Joystick stick1, Joystick stick2){
		
		myJagDrive = torbotJagDrive;
		theStick = stick1;
		theStick2 = stick2;
		
	}
	public void mechanum(boolean squaredInputs){

	        // get negative of the stick controls. forward on stick gives negative value  
	        double stickX = -theStick.getX();
	        double stickY = -theStick.getY();
	        double rotateStick = -theStick2.getX();



	        // adjust joystick by dead zone
	        if (Math.abs(stickX) <= 0.2 && (Math.abs(stickY)) <= 0.2 && (Math.abs(rotateStick)) <= 0.2) {
	            stickX = 0.0;
	            stickY = 0.0;
	            rotateStick = 0.0;
	        }
	       

	        // make sure X and Y don't go beyond the limits of -1 to 1
	        if(Math.abs(stickX) > 1.0){
	        	stickX = Math.abs(stickX)/stickX;
	        }
	        if(Math.abs(stickY) > 1.0){
	        	stickY = Math.abs(stickY)/stickY;
	        }
	        if(Math.abs(rotateStick) > 1.0){
	        	rotateStick = Math.abs(rotateStick)/rotateStick;
	        }
	        if (squaredInputs) {
	            if (stickX >= 0.0) {
	                stickX = (stickX * stickX);
	            } else {
	                stickX = -(stickX * stickX);
	            }

	            if (stickY >= 0.0) {
	                stickY = (stickY * stickY);
	            } else {
	                stickY = -(stickY * stickY);
	            }
	        }
	        double rightFrontSpeed = stickY + rotateStick + stickX;
	        double rightBackSpeed = stickY + rotateStick - stickX;
	        double leftFrontSpeed = stickY - rotateStick - stickX;
	        double leftBackSpeed = stickY - rotateStick + stickX;
	        if(Math.abs(rightFrontSpeed) > .57){
	        	rightFrontSpeed = Math.abs(rightFrontSpeed)/rightFrontSpeed * 0.57;
	        }
	        if(Math.abs(rightBackSpeed) > .57){
	        	rightBackSpeed = Math.abs(rightBackSpeed)/rightBackSpeed * 0.57;
	        }
	        if(Math.abs(leftFrontSpeed) > .57){
	        	leftFrontSpeed = Math.abs(leftFrontSpeed)/leftFrontSpeed * 0.57;
	        }
	        if(Math.abs(leftBackSpeed) > .57){
	        	leftBackSpeed = Math.abs(leftBackSpeed)/leftBackSpeed * 0.57;
	        }
		myJagDrive.setLeftJags(leftFrontSpeed, leftBackSpeed);
		myJagDrive.setRightJags(rightFrontSpeed, rightBackSpeed);
		
		
	}

}
