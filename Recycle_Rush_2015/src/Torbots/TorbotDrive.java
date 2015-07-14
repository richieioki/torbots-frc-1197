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
public class TorbotDrive {

	private Joystick m_stick;

	private Encoder m_encoder;
	private Gyro m_gyro;

	private TorJagDrive m_jagDrive;

	public TorbotDrive(Joystick stick, TorJagDrive jagDrive,Encoder e, Gyro g) {
		m_stick = stick;

		m_jagDrive = jagDrive;

		m_encoder = e;
		m_gyro = g;
	}
	public double getStickY(Joystick stick){
		double x = -stick.getY();
		if(Math.abs(x) > 0.5){
			x = Math.abs(x)/x;
		}
		return x;
	}

	public void wait(double t){
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) / 1000.0 < t){

		}
	}

	public void ArcadeDrive(boolean squaredInputs) {

		double leftMotorSpeed;
		double rightMotorSpeed;

		// get negative of the stick controls. forward on stick gives negative value  
		double stickX = -m_stick.getX();
		double stickY = -m_stick.getY();

		// adjust joystick by dead zone
		if (Math.abs(stickX) <= 0.2 && (Math.abs(stickY)) <= 0.2) {
			stickX = 0.0;
			stickY = 0.0;
		}


		// make sure X and Y don't go beyond the limits of -1 to 1
		if(Math.abs(stickX) > 1.0){
			stickX = Math.abs(stickX)/stickX;
		}
		if(Math.abs(stickY) > 1.0){
			stickY = Math.abs(stickY)/stickY;
		}



		// square the inputs to produce an exponential power curve
		// this allows finer control with joystick movement and full power as you approach joystick limits
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

		if(Math.abs(stickY) < 0.1){
			leftMotorSpeed = stickX;
			rightMotorSpeed = -stickX;
		}
		else if (stickY > 0.0) {
			if (stickX > 0.0) {
				leftMotorSpeed = stickY * (1-stickX);
				rightMotorSpeed = stickY;
			} else {
				leftMotorSpeed = stickY;
				rightMotorSpeed = stickY * ( 1 + stickX );

			}
		} else {
			if (stickX > 0.0) {
				leftMotorSpeed = stickY * (1-stickX);
				rightMotorSpeed = stickY;
			} else {
				leftMotorSpeed = stickY;
				rightMotorSpeed = stickY * (1+stickX);

			}
		}
//		rightMotorSpeed*=0.5;
//		leftMotorSpeed*=0.5;
		m_jagDrive.setJagSpeed(rightMotorSpeed, -leftMotorSpeed);

	}
	public void DriveToTheta(double theta, double motorSpeed, double distanceInches)
	{
		double angleError = 0.0;
		double angleTarget;
		double motorAdjust;
		double timeCurr = 0.0;

		//  m_gyro.Reset();
		angleTarget = m_gyro.getAngle()+theta;         // adjusted target angle; add current ange to desired angle. resetting may not set to 0.0 exactly

		// drive the desired distance
		m_encoder.reset();

		timeCurr = System.currentTimeMillis()/1000;

		// Arbitrary adjustment made from empirical data 9/15/13
		double distanceAdjustment = (distanceInches / 8.0) + (motorSpeed - 0.1) * 12.5;

		while (Math.abs(getDistance()) < (Math.abs(distanceInches) - distanceAdjustment) )
		{
			angleError = m_gyro.getAngle()-angleTarget;       // error off of adjusted target heading
			motorAdjust = angleError/5.0; // percent of error range (5degrees) 

			//use these lines to use corrections
//			          m_jagDrive.setLeft(motorSpeed*(1.0-motorAdjust));
//			          m_jagDrive.setRight(motorSpeed*(1.0+motorAdjust));

			// test drive constant speed, no adjust, to unit test the motor speed
			//          motorSpeed = 1.0;
			m_jagDrive.setLeft(motorSpeed);
			m_jagDrive.setRight(motorSpeed);

			wait(0.05); // wait so we don't update continously. update 20 times per second

			// if this run takes more than 3 seconds, we probably ran into something. Stop and backup.
			if ((System.currentTimeMillis()/1000 - timeCurr) >= 3.0)                                                     // should not take more than 3 seconds
			{
				// stop, back up a bit and exit this loop
				m_jagDrive.setJagSpeed(0.0, 0.0);                                                         // stop
				m_jagDrive.setJagSpeed(-motorSpeed, -motorSpeed);                                        // backup
				wait(0.25);
				m_jagDrive.setJagSpeed(0.0, 0.0);                                                         // stop

				break;                                                                                // exit      
			}// end time-out check
		} // end while

		// Stop motors
		m_jagDrive.setJagSpeed(0.0, 0.0);


	} // end driveToTheta


	public void DriveStraight(double motorSpeed, double distanceInches)
	{
		double currentAngle = 0.0;

		// get the current heading and use this as the direction to drive
		currentAngle = m_gyro.getAngle();
		DriveToTheta(currentAngle, motorSpeed, distanceInches); // maintain currentAngle

	}
	public void TurnToTheta(double motorSpeed, double Theta){
		if(m_gyro.getAngle() > Theta){
			while(m_gyro.getAngle()>Theta){
				m_jagDrive.setJagSpeed(-motorSpeed, -motorSpeed);
			}
		}
		else{
			while(m_gyro.getAngle() < Theta){
				m_jagDrive.setJagSpeed(motorSpeed, motorSpeed);
			}
		}
		m_jagDrive.setJagSpeed(0.0, 0.0);
	}
	public double getDistance() {
		double encoderDistance;

		encoderDistance = ((double)m_encoder.getRaw()*4.0*4.0*3.1416*30.0/24.0)/(360*4);

		return encoderDistance;
	}


}
