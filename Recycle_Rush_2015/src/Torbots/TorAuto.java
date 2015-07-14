package Torbots;


/**
 * Class to hold all of our autonomous code
 * @author Reference 2015 assignment sheet
 *
 * Auto strategy as of now pick up recycle bin
 * and then push crate.
 */
//	We need to understand the distance in which we need to drive in order to pickup the recycle 
// bin, bring it in, load it, then drive to the crate, bring it in, load it and then release. 

public class TorAuto {

	private TorbotDrive m_drive;
	private TorPickup m_pickup;
	private TorElevator m_elevator;

	public TorAuto(TorbotDrive drive, TorPickup pickup, TorElevator elevator){
		m_drive = drive;
		m_pickup = pickup;
		m_elevator = elevator;
	}
	public void wait(double t){
		long startTime = System.currentTimeMillis();
		while((System.currentTimeMillis() - startTime) / 1000.0 < t){

		}
	}
	/**
	 * From the point of view from the driver station.
	 * 
	 * Far left
	 */
	public void run(int x){
		
		if(x!=1 && x!=2){
			if(x==0){
				wait(1.0);
				m_elevator.setPosition(900);
				m_elevator.setSpeed(0.10);
				m_drive.DriveStraight(0.2, 20.0);
			}
			else if(x==3){
				wait(1.0);
				m_elevator.setPosition(900);
				m_elevator.setSpeed(0.10);
				m_drive.DriveStraight(0.2, 20.0);
				m_pickup.closeWheels();
				wait(0.5);
				m_pickup.close();
				wait(1.0);
				m_drive.TurnToTheta(0.2, -80.0);
				m_drive.DriveStraight(0.4, 200.0);
			}
			else if(x==4){
				wait(1.0);
				m_elevator.setPosition(900);
				m_elevator.setSpeed(0.10);
				m_drive.TurnToTheta(0.2, -90.0);
				m_drive.DriveStraight(0.4, 210.0);
			}
			else if(x==5){
				wait(1.0);
				m_elevator.setPosition(450);
				m_elevator.setSpeed(0.10);
				m_drive.DriveStraight(-0.4, -150.0);
			}
			else if(x==6){
				m_elevator.setPosition(450);
				m_elevator.setSpeed(0.10);
				
			}
			else if(x==7){
				m_elevator.newAuto();
				m_drive.DriveStraight(0.2, 25);
				wait(0.2);
				m_elevator.newAuto2();
				m_elevator.setPosition(500);
				wait(0.2);
				m_elevator.setSpeed(0.10);
				m_drive.DriveStraight(0.4, 15.0);
				wait(0.2);
				m_pickup.run();
				m_elevator.run();
				m_drive.TurnToTheta(0.5, 45.0);
				m_drive.DriveStraight(0.4, 100.0);
				m_elevator.setPosition(0);
				m_elevator.newAuto();
				m_elevator.newAuto3();
				m_drive.DriveStraight(-0.4, -30.0);
//				m_drive.TurnToTheta(0.3, -45.0);

			}
		}
		else{
			wait(1.0);
			m_elevator.setPosition(900);
			m_elevator.setSpeed(0.10);
			m_drive.DriveStraight(0.2, 20.0);
			m_pickup.closeWheels();
			wait(0.5);
			m_pickup.close();
			wait(1.0);
			m_drive.TurnToTheta(0.2, -80.0);
			m_pickup.shoot();
		}
//		if(x > 0){
////			wait(0.5);
////			m_elevator.canClamp();
//			wait(1.0);
//			m_elevator.setPosition(900);
//			m_elevator.setSpeed(0.10);
//			m_drive.DriveStraight(0.2, 20.0);
//			m_pickup.closeWheels();
//			wait(0.5);
//			m_pickup.close();
//			wait(1.0);
//			m_drive.TurnToTheta(0.2, -90.0);
//			m_drive.DriveStraight(0.4, 210.0);
//			if(x == 3){
//				m_drive.TurnToTheta(0.2, 0.0);
//			}
//			else{
//				m_drive.TurnToTheta(0.2, -180);
//			}
//			m_pickup.shoot();
//			m_pickup.retractWheels();
//			m_elevator.setGoal(m_elevator.getEncoder());
//		}
//		else if(x==0){
//			m_drive.DriveStraight(0.4, 210.0);
//		}
//		else{
//			wait(1.0);
//			m_elevator.setPosition(900);
//			m_elevator.setSpeed(0.10);
//			m_drive.TurnToTheta(0.2, -60.0);
//			m_drive.DriveStraight(0.4, 210.0);
//		}
	}


}

