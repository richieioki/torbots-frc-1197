package Torbots;


import edu.wpi.first.wpilibj.*;
/**
 * Put all test code here
 * @author torbots
 *
 */
public class TorTest {

	/**
	 * Runs all tests and provides some feedback.
	 */
	private TorElevator elevator;
	private TorbotDrive drive;
	private TorPickup pickup;
	
	public TorTest(TorElevator te, TorbotDrive tbd, TorPickup tp){
		elevator = te;
		drive = tbd;
		pickup = tp;
			
	}
	
	public void runAll() {
		testDrive();
		wait(5.0);
		testPickup();
		wait(5.0);
		testElevator();
	}
	public void wait(double t){
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - startTime) / 1000.0 < t){
            
        }
    }
	public void testDrive() {
		drive.DriveStraight(0.5, 52);
	}
	
	public void testPickup() {
		
	}
	
	public void testElevator() {
		
	}
		
}
