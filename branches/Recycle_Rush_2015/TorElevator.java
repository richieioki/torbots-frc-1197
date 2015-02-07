package Torbots;
import edu.wpi.first.wpilibj.*;

public class TorElevator {
	
		private Joystick tartarus;
		private Joystick stick;
		private TorPickup m_pickup;
		private Jaguar elevatorJag;
		private Solenoid clamp;
		private DigitalInput bottomSwitch;
		private DigitalInput topSwitch;
		private DigitalInput oneTote;
		private TorJagDrive m_jagDrive;
		private Encoder elevatorEncoder;
		private PIDController m_PID;
		private boolean isOverridden;
		public TorElevator(Joystick stick, Joystick stick2, TorPickup tpu, 
				Jaguar eJag, Solenoid c, DigitalInput bs, DigitalInput ts, 
				DigitalInput os, TorJagDrive jagDrive, Encoder e){
			tartarus = stick;
			this.stick = stick2;
			m_pickup = tpu;
			elevatorJag = eJag;
			clamp = c;
			bottomSwitch = bs;
			topSwitch = ts;
			oneTote = os;
			m_jagDrive = jagDrive;
			elevatorEncoder = e;
			isOverridden = false;

			m_PID = new PIDController(0.05, 0.003, 0.0, elevatorEncoder, elevatorJag, 0.05);
			m_PID.setContinuous(false);
			m_PID.setAbsoluteTolerance(3.5);
			m_PID.setSetpoint(elevatorEncoder.get());
			m_PID.setInputRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
			m_PID.setOutputRange(-0.9, 0.9);
			m_PID.enable();
		}
		public void run(){
			if(!bottomSwitch.get() && !stick.getRawButton(1)){
				elevatorJag.set(0.0);
			}
			if(topSwitch.get() && !stick.getRawButton(2)){
				elevatorJag.set(0.0);
			}
			if(isOverridden){
				m_PID.disable();
				if(tartarus.getRawButton(3)){
					clamp.set(true);
				}
				if(tartarus.getRawButton(4)){
					clamp.set(false);
				}
				if(stick.getRawButton(1)){
					elevatorJag.set(0.7);
				}
				else if(stick.getRawButton(2)){
					elevatorJag.set(-0.7);
				}
				else{
					elevatorJag.set(0.0);
				}
			}
			else{
				if(m_pickup.isReadyRaise()){
					m_PID.disable();
					m_jagDrive.setJagSpeed(0.0, 0.0);
					m_pickup.setRaise(false);
					clamp.set(false);
					elevatorJag.set(-0.6);
					while(bottomSwitch.get()){
						
					}
					elevatorJag.set(0.0);
					clamp.set(true);
					elevatorJag.set(0.6);
					while(!oneTote.get()){
					}
					elevatorJag.set(0.0);
					m_PID.setSetpoint(elevatorEncoder.get());
					m_PID.enable();
				}
			}
		}
		public void Override(boolean a){
			isOverridden = a;
		}
	
}
