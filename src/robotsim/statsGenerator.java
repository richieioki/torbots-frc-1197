/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package robotsim;

import java.util.Random;
import robots.*;

public class statsGenerator {
    private Random rng;
    
    public statsGenerator() {
        rng = new Random();
    }
    
    public basicRobot generateEliteStats(basicRobot in) {
        eliteRobot m_robot = (eliteRobot)in;
        
        if(rng.nextInt(4) != 0) {
            m_robot.initRobot(Speed.FAST, true);
        } else {
            m_robot.initRobot(Speed.FAST, false);
        }
        
        return m_robot;
    }
    
    public basicRobot generateMidStats(basicRobot in) {
        //determine speed
        Speed speed;
        boolean passing;
        int rngNumber = rng.nextInt(5);
        if(rngNumber < 2) {
            speed = Speed.FAST;
        } else if(rngNumber == 3) {
            speed = Speed.SLOW;
        } else {
            speed = Speed.MEDIUM;
        }
        
        rngNumber = rng.nextInt(2);
        if(rngNumber!= 0) {
            passing = false;
        } else {
            passing = true;
        }
        
        boolean lowGoal;
        rngNumber = rng.nextInt(4);
        if(rngNumber == 0) {
            lowGoal = false;
        } else {
            lowGoal = true;
        }
        
        midRobot temp = (midRobot)in;
        temp.initRobot(speed, lowGoal, passing);
        
        return temp;
    }
    
    public basicRobot generateNoobStats(basicRobot in) {
        lowRobot temp = (lowRobot)in;
        
        Speed noobSpeed;
        
        int rngNumber = rng.nextInt(3);
        
        //Determine speed
        if(rngNumber == 0) {
            noobSpeed = Speed.SLOW;
        } else if(rngNumber == 1) {
            noobSpeed = Speed.MEDIUM;
        } else {
            noobSpeed = Speed.FAST;
        }
        
        //determine pickup abilities
        boolean noobPickup;
        rngNumber = rng.nextInt(2);
        if(rngNumber == 0) {
            noobPickup = false;
        } else {
            noobPickup = true;
        }
        
        //determine shooting
        boolean noobShooting;
        rngNumber = rng.nextInt(3);
        if(rngNumber != 0) {
            noobShooting = true;
        } else {
            noobShooting = false;
        }
        
        //determine can low goal
        boolean noobLowGoal;
        rngNumber = rng.nextInt(3);
        if(rngNumber != 0 && noobPickup) {
            noobLowGoal = true;
        } else {
            noobLowGoal = false;
        }
        
        boolean noobTrussShot;
        rngNumber = rng.nextInt(3);
        if(rngNumber != 0 || noobShooting) {
            noobTrussShot = true;
        } else {
            noobTrussShot = false;
        }
        
        boolean noobPass;
        rngNumber = rng.nextInt(3);
        if(rngNumber != 0 || !noobPickup) {
            noobPass = false;
        } else {
            noobPass = true;
        }
        
        temp.initRobot(noobSpeed, noobPickup, noobShooting, noobLowGoal, noobPass, noobTrussShot);
        return temp;
    }
}
