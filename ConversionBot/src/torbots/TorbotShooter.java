/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package torbots;

import edu.wpi.first.wpilibj.*;

/**
 *
 * @author torbots
 */
public class TorbotShooter {
    
     private Joystick m_stick;
     private Jaguar loaderJag;
     private Solenoid loadBar;
     private boolean loaderDown;
     
    
    public TorbotShooter(Joystick stick, Jaguar loadJag, Solenoid loaderBar) {
        
        m_stick = stick;
        loaderJag = loadJag;
        loadBar = loaderBar;
        loaderDown = false;
    }
    
    public void run() {
        while(m_stick.getRawButton(1))
            loaderJag.set(0.65);
        if(m_stick.getRawButton(3))
            moveLoaderBar();
            
    }
    public void moveLoaderBar() {
        loaderDown = !loaderDown;
        loadBar.set(loaderDown);
        
    }
    
    
    
}
