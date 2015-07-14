package Torbots;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.wpi.first.wpilibj.*;

/**
 *
 * @author torbots
 */
public class TorJagDrive {

	Jaguar rightJag1;
	Jaguar rightJag2;
	Jaguar leftJag1;
	Jaguar leftJag2;

	public TorJagDrive (Jaguar leftJag1, Jaguar leftJag2, Jaguar rightJag1, Jaguar rightJag2){
		this.rightJag1 = rightJag1;
		this.rightJag2 = rightJag2;
		this.leftJag1 = leftJag1;
		this.leftJag2 = leftJag2;

	}
	public void setJagSpeed(double rightSpeed, double leftSpeed){
		rightJag1.set(rightSpeed);
		rightJag2.set(rightSpeed);
		leftJag1.set(leftSpeed);
		leftJag2.set(leftSpeed);
	}  
	public void setLeft(double motorSpeed){
		leftJag1.set(-motorSpeed);
		leftJag2.set(-motorSpeed);
	}
	public void setRight(double motorSpeed){
		rightJag1.set(motorSpeed);
		rightJag2.set(motorSpeed);
	}

}
