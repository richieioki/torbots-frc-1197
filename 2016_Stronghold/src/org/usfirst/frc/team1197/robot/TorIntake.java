package org.usfirst.frc.team1197.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import java.io.PrintStream;

public class TorIntake
{
	private CANTalon armTalon;
	private CANTalon elevatorTalon;
	private CANTalon verticalTalon, verticalTalon1;
	private Joystick stick;
	private Joystick siegeStick;
	private DigitalInput bottomBreakBeam;
	private DigitalInput shooterBreakBeam;
	private TorSiege siege;
	private IntakeState m_state;
	private boolean override;
	private TorShooter m_shoot;
//	private Joystick stick3;

	private static enum IntakeState
	{
		BOTH,  ELEVATOR,  IDLE,  NULL,  PORT,  SHOOTING, ARM;

		private IntakeState() {}
	}

	public TorIntake(Joystick stick, Joystick siegeStick, CANTalon cantalon, CANTalon cantalon2, CANTalon cantalon3, 
			CANTalon cantalon4, DigitalInput breakBeam, DigitalInput breakBeam2, TorSiege siege, 
			TorShooter shoot)
	{
		armTalon = cantalon;
		elevatorTalon = cantalon2;
		verticalTalon = cantalon3;
		bottomBreakBeam = breakBeam;
		shooterBreakBeam = breakBeam2;
		this.siege = siege;
		this.stick = stick;
		this.siegeStick = siegeStick;
		m_state = IntakeState.IDLE;
		m_shoot = shoot;
		verticalTalon1 = cantalon4;
	}

	public void intake()
	{
		if (stick.getRawButton(6)) //11
		{
			m_state = IntakeState.IDLE;
			elevatorTalon.set(1);
			verticalTalon.set(0.95);
			verticalTalon1.set(0.95);
			armTalon.set(1);
		}
		else if (stick.getRawButton(5) || siegeStick.getRawButton(6)) //12
		{
			m_state = IntakeState.IDLE;
			armTalon.set(-1);
			verticalTalon.set(-0.95);
			verticalTalon1.set(-0.95);
			elevatorTalon.set(-1);
		}
		else if ((stick.getRawButton(7)) && (m_state == IntakeState.IDLE)) //1
		{
			if (shooterBreakBeam.get() != true)
			{
				m_state = IntakeState.BOTH;

				elevatorTalon.set(-1);
				verticalTalon.set(-0.95);
				verticalTalon1.set(-0.95);
				armTalon.set(-1);
			}
		}
		else if (m_state == IntakeState.BOTH)
		{
			if (bottomBreakBeam.get() == true)
			{
				armTalon.set(0.0);
				m_state = IntakeState.ELEVATOR;
			}
		}
		else if (m_state == IntakeState.ELEVATOR)
		{
			if (shooterBreakBeam.get() == true)
			{
				elevatorTalon.set(0.0D);
				verticalTalon.set(0);
				verticalTalon1.set(0);
				m_state = IntakeState.IDLE;
			}
		}
		else if (m_state == IntakeState.PORT)
		{
			armTalon.set(0.75D);
			verticalTalon.set(0.95);
			elevatorTalon.set(0.0D);
		}
//		else if ((!stick3.getRawButton(2)) && (m_state == IntakeState.SHOOTING) && (!shooterBreakBeam.get()))
//		{
//			m_state = IntakeState.IDLE;
//		}
		else if (m_state == IntakeState.IDLE)
		{
			elevatorTalon.set(0.0D);
			verticalTalon.set(0);
			verticalTalon1.set(0);
			armTalon.set(0.0D);
		}
		else if (m_state == IntakeState.ARM ){
			armTalon.set(-1);
			elevatorTalon.set(0);
			verticalTalon.set(0);
		}
	}

	public void autoLoad() {}

	public void portcullis()
	{
		m_state = IntakeState.PORT;
	}

	public void portStop()
	{
		m_state = IntakeState.IDLE;
	}

	public void portcullisTele(double val)
	{
		armTalon.set(val);
	}

	public void armIntakeStop(boolean bool)
	{
		if (bool == true) {
			armTalon.set(0.0D);
		}
	}

	public double intakeOutputCurrent()
	{
		return armTalon.getOutputCurrent();
	}

	public void intakeStop(boolean bool)
	{
		if (bool == true) {
			elevatorTalon.set(0.0D);
		}
	}

	public void stopElevator()
	{
		if (m_state == IntakeState.SHOOTING)
		{
			m_state = IntakeState.IDLE;

			elevatorTalon.set(0.0D);
		}
	}
	public void armtalon(){
		m_state = IntakeState.ARM;
	}
	public void stopArmTalon(){
		m_state = IntakeState.IDLE;
	}

	public void fire()
	{
		m_state = IntakeState.SHOOTING;
		elevatorTalon.set(-1);
	}

	public boolean shooterBreakBeam()
	{
		return shooterBreakBeam.get();
	}

	public boolean shooterMotor()
	{
		return elevatorTalon.get() == 0.0D;
	}
}
