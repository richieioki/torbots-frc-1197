package Torbots;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TorElevator {
	
		private Joystick tartarus;
		private Joystick stick;
		private TorPickup m_pickup;
		private Jaguar elevatorJag;
		private Solenoid clamp;
		private DigitalInput bottomSwitch;
		private DigitalInput topSwitch;
		private TorJagDrive m_jagDrive;
		private Encoder elevatorEncoder;
//		private PIDController m_PID;
		private boolean isOverridden;
		private boolean isHolding;
		private boolean isDeploying;
		private final int HOLDING = -380;
		private int currentGoal;;
		public TorElevator(Joystick stick, Joystick stick2, TorPickup tpu, 
				Jaguar eJag, Solenoid c, DigitalInput bs, DigitalInput ts, 
				TorJagDrive jagDrive, Encoder e){
			tartarus = stick;
			this.stick = stick2;
			m_pickup = tpu;
			elevatorJag = eJag;
			clamp = c;
			bottomSwitch = bs;
			topSwitch = ts;
			m_jagDrive = jagDrive;
			elevatorEncoder = e;
			isOverridden = false;
			isHolding = false;
			currentGoal = elevatorEncoder.get();
		}
		public void wait(double t){
	        long startTime = System.currentTimeMillis();
	        while((System.currentTimeMillis() - startTime) / 1000.0 < t){
	            
	        }
	    }
		public void run(){
			if(bottomSwitch.get() && !stick.getRawButton(1)){
				elevatorJag.set(0.0);
				m_pickup.elevatorRunning(false);
				elevatorEncoder.reset();
			}
			if(topSwitch.get() && !stick.getRawButton(2)){
				elevatorJag.set(0.0);
				m_pickup.elevatorRunning(false);
			}
			if(isHolding&&!(stick.getRawButton(1)||stick.getRawButton(2))&&!bottomSwitch.get()){
				if( Math.abs(elevatorEncoder.get()- HOLDING)>10){
					if(elevatorEncoder.get()>HOLDING){
						elevatorJag.set(0.05);
					}
					else{
						elevatorJag.set(-0.05);
					}
				}
				else{
					elevatorJag.set(0.0);
				}  
			}
			if(Math.abs(elevatorEncoder.get() - currentGoal)>10){
				if(elevatorEncoder.get()>currentGoal){
					elevatorJag.set(0.05);
				}
				else{
					elevatorJag.set(-0.05);
				}
			}
			else{
				elevatorJag.set(0.0);
			}
			if(isOverridden){
				if(tartarus.getRawButton(3)){
					clamp.set(false);
				}
				if(tartarus.getRawButton(4)){
					clamp.set(true);
				}
				if(stick.getRawButton(1)){
					m_pickup.elevatorRunning(true);
					elevatorJag.set(0.4);
					currentGoal = elevatorEncoder.get();
				}
				else if(stick.getRawButton(2)){
					elevatorJag.set(-0.4);
					m_pickup.elevatorRunning(true);
					currentGoal = elevatorEncoder.get();
				}
				else{
					elevatorJag.set(0.0);
					m_pickup.elevatorRunning(false);
				}
			}
			else{
				if(m_pickup.isReadyRaise()){
					m_jagDrive.setJagSpeed(0.0, 0.0); 
					m_pickup.setRaise(false);
					elevatorJag.set(-0.5);
					while(elevatorEncoder.get()<-250){
						
					}
					elevatorJag.set(0.0);
					clamp.set(true);
					elevatorJag.set(-0.5);
					while(!bottomSwitch.get()){
						if(tartarus.getRawButton(5)){
							break;
						}
					}
					elevatorJag.set(0.0);
					wait(0.5);
					clamp.set(false);
					wait(0.5);
					elevatorJag.set(0.5);
					while(elevatorEncoder.get()>-300){
						if(tartarus.getRawButton(5)){
							break;
						}
					}
					elevatorJag.set(0.3);
					while(elevatorEncoder.get()>- 380){
						if(tartarus.getRawButton(5)){
							break;
						}
					}
					wait(0.1);
					elevatorJag.set(0.0);
					isHolding = true;
					currentGoal = elevatorEncoder.get();
				}
			} 
			
		}
		public void Override(boolean a){
			isOverridden = a;
		}
	
}
