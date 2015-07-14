package Torbots;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TorElevator {

	
	static final int MAX_HEIGHT = 1500;
	
	private Joystick tartarus;
	private Joystick stick;

	private TorPickup m_pickup;
	private TorJagDrive m_jagDrive;

	private Jaguar elevatorJag;
	private Solenoid clamp;
	private Solenoid canClamp;

	private DigitalInput bottomSwitch;
	private Encoder elevatorEncoder;

	private boolean isOverridden;
	private boolean isHolding;
	private final int HOLDING = 450;
	private int currentGoal;

	private boolean CAN_CLOSED;
	private boolean CAN_OPEN;
	private boolean CLOSED;
	private boolean OPEN;

	private PIDController pid;

	public TorElevator(Joystick stick, Joystick stick2, TorPickup tpu, 
			Jaguar eJag, Solenoid c, Solenoid cc, DigitalInput bs, 
			TorJagDrive jagDrive, Encoder e){
		tartarus = stick;
		this.stick = stick2;

		m_pickup = tpu;
		m_jagDrive = jagDrive;

		elevatorJag = eJag;
		clamp = c;
		canClamp = cc;

		bottomSwitch = bs;
		elevatorEncoder = e;

		isOverridden = false;
		isHolding = false;
		currentGoal = elevatorEncoder.get();

		CAN_CLOSED = false;
		CAN_OPEN = true;
		CLOSED = false;
		OPEN = true;

		pid = new PIDController(0.002,0.000,0.0,elevatorEncoder, elevatorJag);
		pid.setContinuous(false);
		pid.setInputRange(0.0, 1300.0);
		pid.setOutputRange(-0.9, 0.9);
		pid.setPercentTolerance(1.0);
		pid.setSetpoint(elevatorEncoder.get());
		pid.enable();
	}
	public void wait(double t){
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) / 1000.0 < t){

		}
	}
	public void enablePID(boolean a){
		if(a){
			pid.enable();
		}
		else{
			pid.disable();
		}
	}
	public void clamp(){
		clamp.set(CLOSED);	
	}
	public void canClamp(){
		canClamp.set(CAN_CLOSED);
	}
	public void release(){
		clamp.set(OPEN);
	}
	public void releaseCan(){
		canClamp.set(CAN_OPEN);
	}
	public void setPosition(double pos){
		pid.enable();
		pid.setSetpoint(pos);
		wait(1.0);
	}
	public void setSpeed(double a){
		elevatorJag.set(a);
	}
	public void setP(int x){
		pid.setPID(.002+(.001*(x+1)),0.00,0.00);
	}
	public void run(){
		SmartDashboard.putBoolean("PID", pid.isEnable());
		SmartDashboard.putNumber("Setpoint", pid.getSetpoint());
		if(bottomSwitch.get() && !stick.getRawButton(1)){
//			elevatorJag.set(0.0);
			m_pickup.elevatorRunning(false);
			elevatorEncoder.reset();
//			pid.setSetpoint(0);
		}
		if(elevatorEncoder.get() > MAX_HEIGHT && !stick.getRawButton(2)){
			elevatorJag.set(0.0);
			m_pickup.elevatorRunning(false);
		}
		if(tartarus.getRawButton(3)){
			canClamp.set(!canClamp.get());
			wait(0.1);
		}
		if(isOverridden){
			if(tartarus.getRawButton(2)){
				clamp.set(!clamp.get());
				wait(0.1);
			}
			if(stick.getRawButton(1)){
				pid.disable();
//				pid.enable();
//				pid.setSetpoint(elevatorEncoder.get());
				m_pickup.elevatorRunning(true);
				elevatorJag.set(0.7);
			}
			else if(stick.getRawButton(2)){
				pid.disable();
//				pid.enable();
//				pid.setSetpoint(elevatorEncoder.get());
				elevatorJag.set(-0.7);
				m_pickup.elevatorRunning(true);
			}
			else{
				elevatorJag.set(0.0);
				pid.enable();
				pid.setSetpoint(elevatorEncoder.get());
				m_pickup.elevatorRunning(false);
			}
		}
		else {
			if(m_pickup.isReadyRaise()){
				pid.disable();
				m_jagDrive.setJagSpeed(0.0, 0.0); 
				m_pickup.setRaise(false);
				elevatorJag.set(-0.5);
				while(elevatorEncoder.get()>320){

				}
				elevatorJag.set(0.0);
				clamp.set(OPEN);
				wait(0.5);
				elevatorJag.set(-0.5);
				while(!bottomSwitch.get()){
					if(tartarus.getRawButton(5)){
						break;
					}
				}
				elevatorJag.set(0.0);
				wait(0.25);
				clamp.set(CLOSED);
				wait(0.25);
				
				if(!tartarus.getRawButton(5)){
					pid.enable();
					pid.setSetpoint(500);
				}
				else{
					pid.enable();
					pid.setSetpoint(42);
				}
				isHolding = true;
				wait(0.50);
			}
			
		} 

	}
	public void run2(){
		if(bottomSwitch.get() && !stick.getRawButton(1)){
			elevatorJag.set(0.0);
			m_pickup.elevatorRunning(false);
			elevatorEncoder.reset();
			pid.setSetpoint(0);
		}
		if(tartarus.getRawButton(3)){
			canClamp.set(!canClamp.get());
			wait(0.1);
		}
		if(isOverridden){
			if(tartarus.getRawButton(2)){
				clamp.set(!clamp.get());
				wait(0.1);
			}
			if(stick.getRawButton(1)){
				pid.disable();
				m_pickup.elevatorRunning(true);
				elevatorJag.set(0.7);
			}
			else if(stick.getRawButton(2)){
				pid.disable();
				elevatorJag.set(-0.7);
				m_pickup.elevatorRunning(true);
			}
			else{
				elevatorJag.set(0.0);
				pid.enable();
				pid.setSetpoint(elevatorEncoder.get());
				m_pickup.elevatorRunning(false);
			}
		}
	}
	public void Override(boolean a){
		isOverridden = a;
	}
	public void setGoal(int a){
		currentGoal = a;
	}
	public int getEncoder(){
		return elevatorEncoder.get();
	}
	public void deploy(int x){
		m_pickup.Override(true);
		clamp.set(OPEN);
		wait(1.0);
//		int position = (23*(x-1)*12)-22;
//		if(position < 0){
//			position = 0;
//		}
//		else if(position > MAX_HEIGHT){
//			position = MAX_HEIGHT;
//		}
//		setPosition(position);
		canClamp.set(CAN_OPEN);
		wait(0.5);
	}
	public void test(){
		pid.enable();
		pid.setSetpoint(500);
	}
	public void newAuto(){
		canClamp.set(CAN_OPEN);
	}
	public void newAuto2(){
		canClamp.set(CAN_CLOSED);
	}
	public void newAuto3(){
		clamp.set(OPEN);
	}
}
