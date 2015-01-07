package Torbots;

import edu.wpi.first.wpilibj.Jaguar;

public class TorJagDrive {
	
	Jaguar fLeftJag, bLeftJag, fRightJag, bRightJag;
	
	public TorJagDrive(Jaguar leftJag1, Jaguar leftJag2, Jaguar rightJag1, Jaguar rightJag2){
		fLeftJag = leftJag1;
		bLeftJag = leftJag2;
		fRightJag = rightJag1;
	    bRightJag = rightJag2;   
	}
	public TorJagDrive(Jaguar leftJag, Jaguar rightJag){
		fLeftJag = leftJag;
		fRightJag = rightJag;   
	}
	public void setRightJags(double speed){
		try{
		fRightJag.set(speed);
		bRightJag.set(speed);
		}
		catch(NullPointerException e){
			fRightJag.set(speed);
		}
		
	}
	public void setRightJags(double fSpeed, double bSpeed){
		try{
		fRightJag.set(fSpeed);
		bRightJag.set(bSpeed);
		}
		catch(NullPointerException e){
			fRightJag.set(fSpeed);
		}
	}
	public void setLeftJags(double speed){
		try{
		fLeftJag.set(speed);
		bLeftJag.set(speed);
		}
		catch(NullPointerException e){
			fLeftJag.set(speed);

		}
		
	}
	public void setLeftJags(double fSpeed,double bSpeed){
		try{
		fLeftJag.set(fSpeed);
		bLeftJag.set(bSpeed);
		}
		catch(NullPointerException e){
			fLeftJag.set(fSpeed);
		}
		
	}

}
