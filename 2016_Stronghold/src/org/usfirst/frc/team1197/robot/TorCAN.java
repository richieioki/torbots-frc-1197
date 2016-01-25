package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;


/**
 * Container class for holding a drive train.
 * Updated to take in CAN objects
 * @author Torbot
 *
 */
public class TorCAN {

	int numOfJags;
	CANTalon m_Rtalon1;
	CANTalon m_Rtalon2;
	CANTalon m_Rtalon3;
	CANTalon m_Ltalon1;
	CANTalon m_Ltalon2;
	CANTalon m_Ltalon3;
	
	/**
	 * Right talons first then left
	 * @param t1
	 * @param t2
	 * @param t3
	 * @param t4
	 */
	public TorCAN(CANTalon R1, CANTalon R2, CANTalon L1, CANTalon L2) 
	{
		numOfJags = 4;
		m_Rtalon1 = R1;
		m_Rtalon2 = R2;
		
		m_Ltalon1 = L1;
		m_Ltalon2 = L2;
	}

	public TorCAN(CANTalon R1, CANTalon L1)
	{
		numOfJags = 2;  
		m_Rtalon1 = R1;
		m_Ltalon1 = L1;
	}
	
	/**
	 * Six motor drive input.  Right then left motors
	 * @param R1
	 * @param R2
	 * @param R3
	 * @param L1
	 * @param L2
	 * @param L3
	 */
	public TorCAN(CANTalon R1, CANTalon R2, CANTalon R3, 
			CANTalon L1, CANTalon L2, CANTalon L3) {
		numOfJags = 6;
		
		m_Rtalon1 = R1;
		m_Rtalon2 = R2;
		m_Rtalon3 = R3;
		
		m_Ltalon1 = L1;
		m_Ltalon2 = L2;
		m_Ltalon3 = L3;
	}

	public void SetDrive(double leftSpeed, double rightSpeed)
	{
		SetLeft(leftSpeed);
		SetRight(rightSpeed);
	}

	public void SetLeft(double speed)
	{
		// left motors take positive speed to move forward
		if (numOfJags == 2)
		{
			m_Ltalon1.set(-speed);
		}
		else if(numOfJags == 4)
		{
			m_Ltalon1.set(-speed);
			m_Ltalon2.set(-speed);
		} else {
			m_Ltalon1.set(-speed);
			m_Ltalon2.set(-speed);
			m_Ltalon3.set(-speed);
		}
		//m_left1.Set(-speed);
		//m_left2.Set(-speed);
	}

	public void SetRight(double speed)
	{
		// right motors are inverted. Change sign for driving right side
		if (numOfJags == 2)
		{
			m_Rtalon1.set(speed);
		}
		else if(numOfJags == 4)
		{
			m_Rtalon1.set(speed);
			m_Rtalon2.set(speed);
		} else {
			m_Rtalon1.set(speed);
			m_Rtalon2.set(speed);
			m_Rtalon3.set(speed);
		}
		//m_right1.Set(speed);
		//m_right2.Set(speed);
	}	
}
