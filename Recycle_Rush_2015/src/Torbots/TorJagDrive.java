package Torbots;

import edu.wpi.first.wpilibj.Jaguar;

public class TorJagDrive {
	
	Jaguar fLeftJag, bLeftJag, fRightJag, bRightJag, lMidJag,rMidJag;
	
	public TorJagDrive(Jaguar leftJag1, Jaguar leftJag2, Jaguar rightJag1, Jaguar rightJag2){
		fLeftJag = leftJag1;
		bLeftJag = leftJag2;
		fRightJag = rightJag1;
	    bRightJag = rightJag2;
	    
	}
	public void setRightJags(double speed){
		fRightJag.set(speed);
		bRightJag.set(speed);
		
	}
	public void setRightJags(double fSpeed, double bSpeed){
		fRightJag.set(fSpeed);
		bRightJag.set(bSpeed);
	}
	public void setLeftJags(double speed){
		fLeftJag.set(speed);
		bLeftJag.set(speed);
		
	}
	public void setLeftJags(double fSpeed,double bSpeed){
		fLeftJag.set(fSpeed);
		bLeftJag.set(bSpeed);
		
	}

}
