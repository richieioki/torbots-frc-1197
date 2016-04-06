package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import java.io.PrintStream;

public class TorIntake
{
	private CANTalon armTalon;
	private CANTalon elevatorTalon;
	private CANTalon verticalTalon;
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
		BOTH,  ELEVATOR,  IDLE,  NULL,  PORT,  SHOOTING;

		private IntakeState() {}
	}

	public TorIntake(Joystick stick, CANTalon cantalon, CANTalon cantalon2, CANTalon cantalon3, 
			DigitalInput breakBeam, DigitalInput breakBeam2, TorSiege siege, 
			TorShooter shoot, Joystick stick3)
	{
		this.armTalon = cantalon;
		this.elevatorTalon = cantalon2;
		verticalTalon = cantalon3;
		this.bottomBreakBeam = breakBeam;
		this.shooterBreakBeam = breakBeam2;
		this.siege = siege;
		this.stick = stick;
		this.m_state = IntakeState.IDLE;
		this.m_shoot = shoot;
		this.stick3 = stick3;
	}

	public void intake()
	{
		if (this.stick.getRawButton(4))
		{
			this.m_state = IntakeState.IDLE;
			this.elevatorTalon.set(1);
			verticalTalon.set(0.95);
			this.armTalon.set(0.75D);
		}
		else if (this.stick.getRawButton(5))
		{
			this.m_state = IntakeState.IDLE;
			this.armTalon.set(-0.75);
			verticalTalon.set(-0.95);
			this.elevatorTalon.set(-1);
		}
		else if (this.stick.getRawButton(6))
		{
			this.m_state = IntakeState.IDLE;
			armTalon.set(0.0);
			this.elevatorTalon.set(-1);
		}
		else if ((this.stick.getRawButton(3)) && (this.m_state == IntakeState.IDLE))
		{
			if (this.shooterBreakBeam.get() != true)
			{
				this.m_state = IntakeState.BOTH;

				this.elevatorTalon.set(-1);
				verticalTalon.set(-0.95);
				this.armTalon.set(-0.75);
			}
		}
		else if (this.m_state == IntakeState.BOTH)
		{
			if (this.bottomBreakBeam.get() == true)
			{
				armTalon.set(0.0);
				this.m_state = IntakeState.ELEVATOR;
			}
		}
		else if (this.m_state == IntakeState.ELEVATOR)
		{
			if (this.shooterBreakBeam.get() == true)
			{
				this.elevatorTalon.set(0.0D);
				verticalTalon.set(0.0);
				this.m_state = IntakeState.IDLE;
			}
		}
		else if (this.m_state == IntakeState.PORT)
		{
			this.armTalon.set(0.75D);
			verticalTalon.set(0.95);
			this.elevatorTalon.set(0.0D);
		}
		else if ((!this.stick3.getRawButton(2)) && (this.m_state == IntakeState.SHOOTING) && (!this.shooterBreakBeam.get()))
		{
			this.m_state = IntakeState.IDLE;
		}
		else if (this.m_state == IntakeState.IDLE)
		{
			this.elevatorTalon.set(0.0D);
			verticalTalon.set(0.0);
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
		this.elevatorTalon.set(-1);
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
