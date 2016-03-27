package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import java.io.PrintStream;

public class TorIntake
{
	private CANTalon armTalon;
	private CANTalon elevatorTalon;
	private Joystick stick;
	private DigitalInput bottomBreakBeam;
	private DigitalInput shooterBreakBeam;
	private TorSiege siege;
	private IntakeState m_state;
	private boolean override;
	private TorShooter m_shoot;
	private Joystick stick3;

	private static enum IntakeState
	{
<<<<<<< HEAD
		BOTH,  ELEVATOR,  IDLE,  NULL,  PORT,  SHOOTING;

		private IntakeState() {}
	}

	public TorIntake(Joystick stick, CANTalon cantalon, CANTalon cantalon2, DigitalInput breakBeam, DigitalInput breakBeam2, TorSiege siege, TorShooter shoot, Joystick stick3)
=======
		BOTH,  ELEVATOR,  IDLE,  NULL,  PORT, SHOOTING;

		private IntakeState() {}
	}

	public TorIntake(Joystick stick, CANTalon cantalon, CANTalon cantalon2, 
			DigitalInput breakBeam, DigitalInput breakBeam2, TorSiege siege, 
			TorShooter shoot, Joystick stick3)
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
	{
		this.armTalon = cantalon;
		this.elevatorTalon = cantalon2;
		this.bottomBreakBeam = breakBeam;
		this.shooterBreakBeam = breakBeam2;
		this.siege = siege;
		this.stick = stick;
		this.m_state = IntakeState.IDLE;
<<<<<<< HEAD
		this.m_shoot = shoot;
		this.stick3 = stick3;
=======
		m_shoot = shoot;
		this.stick3 = stick3;
	}

	public void intake()
	{
		if (this.stick.getRawButton(4))
		{
			this.m_state = IntakeState.IDLE;
			this.elevatorTalon.set(0.95D);
			this.armTalon.set(0.95D);
		}
		else if (this.stick.getRawButton(5))
		{
			this.m_state = IntakeState.IDLE;
			this.armTalon.set(-0.95D);
			this.elevatorTalon.set(-0.95D);
			//1690 close, 440 far, 1150/1168 mid, 702 left
		}
		else if (stick.getRawButton(6)){
			this.m_state = IntakeState.IDLE;
//			this.armTalon.set(-0.95D);
			this.elevatorTalon.set(-0.95D);
		}
		else if ((this.stick.getRawButton(3)) && (this.m_state == IntakeState.IDLE))
		{
			if(this.shooterBreakBeam.get() != true) { //there is a ball in the robot
				this.m_state = IntakeState.BOTH;
	//			siege.setDegrees(siege.intakeVal);
				this.elevatorTalon.set(-0.95D);
				this.armTalon.set(-0.95D);
			}
		}
		else if (this.m_state == IntakeState.BOTH)
		{
			if (this.bottomBreakBeam.get() == true)
			{
				this.armTalon.set(0.0D);
				this.m_state = IntakeState.ELEVATOR;
			}
		}
		else if (this.m_state == IntakeState.ELEVATOR)
		{
			if (this.shooterBreakBeam.get() == true)
			{
				this.elevatorTalon.set(0.0D);
//				m_shoot.shootFlag = false;
				this.m_state = IntakeState.IDLE;
			}
		}
		else if (this.m_state == IntakeState.PORT)
		{
			this.armTalon.set(0.95D);
			this.elevatorTalon.set(0.0D);
		}
		else if (this.m_state == IntakeState.IDLE)
		{
			this.elevatorTalon.set(0.0D);
			this.armTalon.set(0.0D);
		}
	}

	public void autoLoad() {}

	public void portcullis()
	{
		this.m_state = IntakeState.PORT;
	}

	public void portStop()
	{
		this.m_state = IntakeState.IDLE;
	}

	public void portcullisTele(double val)
	{
		this.armTalon.set(val);
	}

	public void armIntakeStop(boolean bool)
	{
		if (bool == true) {
			this.armTalon.set(0.0D);
		}
	}

	public void printCurrentOutput()
	{
		System.out.println("Current: " + this.armTalon.getOutputCurrent());
	}

	public double intakeOutputCurrent()
	{
		return this.armTalon.getOutputCurrent();
	}

	public void intakeStop(boolean bool)
	{
		if (bool == true) {
			this.elevatorTalon.set(0.0D);
		}
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
	}
<<<<<<< HEAD

	public void intake()
	{
		if (this.stick.getRawButton(4))
		{
			this.m_state = IntakeState.IDLE;
			this.elevatorTalon.set(0.95D);
			this.armTalon.set(0.95D);
		}
		else if (this.stick.getRawButton(5))
		{
			this.m_state = IntakeState.IDLE;
			this.armTalon.set(-0.95D);
			this.elevatorTalon.set(-0.95D);
		}
		else if (this.stick.getRawButton(6))
		{
			this.m_state = IntakeState.IDLE;

			this.elevatorTalon.set(-0.95D);
		}
		else if ((this.stick.getRawButton(3)) && (this.m_state == IntakeState.IDLE))
		{
			if (this.shooterBreakBeam.get() != true)
			{
				this.m_state = IntakeState.BOTH;

				this.elevatorTalon.set(-0.95D);
				this.armTalon.set(-0.95D);
			}
		}
		else if (this.m_state == IntakeState.BOTH)
		{
			if (this.bottomBreakBeam.get() == true)
			{
				this.armTalon.set(0.0D);
				this.m_state = IntakeState.ELEVATOR;
			}
		}
		else if (this.m_state == IntakeState.ELEVATOR)
		{
			if (this.shooterBreakBeam.get() == true)
			{
				this.elevatorTalon.set(0.0D);

				this.m_state = IntakeState.IDLE;
			}
		}
		else if (this.m_state == IntakeState.PORT)
		{
			this.armTalon.set(0.95D);
			this.elevatorTalon.set(0.0D);
		}
		else if ((!this.stick3.getRawButton(2)) && (this.m_state == IntakeState.SHOOTING) && (!this.shooterBreakBeam.get()))
		{
			this.m_state = IntakeState.IDLE;
		}
		else if (this.m_state == IntakeState.IDLE)
		{
			this.elevatorTalon.set(0.0D);
			this.armTalon.set(0.0D);
		}
	}

	public void autoLoad() {}

	public void portcullis()
	{
		this.m_state = IntakeState.PORT;
	}

	public void portStop()
	{
		this.m_state = IntakeState.IDLE;
	}

	public void portcullisTele(double val)
	{
		this.armTalon.set(val);
	}

	public void armIntakeStop(boolean bool)
	{
		if (bool == true) {
			this.armTalon.set(0.0D);
		}
	}

	public void printCurrentOutput()
	{
		System.out.println("Current: " + this.armTalon.getOutputCurrent());
	}

	public double intakeOutputCurrent()
	{
		return this.armTalon.getOutputCurrent();
	}

	public void intakeStop(boolean bool)
	{
		if (bool == true) {
			this.elevatorTalon.set(0.0D);
=======
	
	public void runIntake(){
		m_state = IntakeState.SHOOTING;
		elevatorTalon.set(-0.95);
	}
	
	public void stopElevator() {
		if(m_state == IntakeState.SHOOTING) {
			m_state = IntakeState.IDLE;
			m_shoot.shootFlag = false;
			elevatorTalon.set(0);
>>>>>>> branch 'master' of https://github.com/richieioki/torbots-frc-1197.git
		}
	}

	public void stopElevator()
	{
		if (this.m_state == IntakeState.SHOOTING)
		{
			this.m_state = IntakeState.IDLE;

			this.elevatorTalon.set(0.0D);
		}
	}

	public void fire()
	{
		this.m_state = IntakeState.SHOOTING;
		this.elevatorTalon.set(-0.95D);
	}

	public boolean shooterBreakBeam()
	{
		return this.shooterBreakBeam.get();
	}

	public boolean shooterMotor()
	{
		return this.elevatorTalon.get() == 0.0D;
	}
}
