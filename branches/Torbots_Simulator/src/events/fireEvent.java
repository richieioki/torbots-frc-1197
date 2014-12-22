/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package events;

import game.gameConstants;
import objects.Robot;

/**
 *
 * @author gaming
 */
public class fireEvent extends Event{

    fireEvent(Robot robot, boolean defense) {
        super();
        
        int base = 1;
        if(defense) {
            base = 5;
        }
        //The very best teams will basically shoot once they are in position
        //some even shoot on the run, but that is neither here nor there
        //in this simulator we will say the best teams shoot with a 1 second delay
        //we will assign a range to allow for some teams requireing extra setup time
        //we will also assume that defence slows down the average time of shooting
        //drastically. and Thus base will be increased to 5.
        
        switch(robot.rr) {
            case ELITE: 
                this.duration = robot.getRandom().nextInt(3); //we ignore base here
                //because elite teams will position themselves and this cost
                //is included in the previous event
                break;
            case MID:
                //Some mid tier teams will be able to shoot nearly as quickly as elite
                //teams, but I will still include base here.  I also added a wide range
                //of times 0-24, this is to allow for misses.  We will currently ignore
                //the target the robot aims for
                this.duration = robot.getRandom().nextInt(25) + base;
                break;
            case LOW:
                //Some lower level teams that are shooting will probably aim for
                //the easier goal.  With this in mind we can reduce the miss penalty
                //that I have assigned in the MID, but since many will have setup time
                //I will include this in the base by doubling it.  This is also 
                //reflects my belief that against LOW teams defense will really
                //effect them.
                this.duration = robot.getRandom().nextInt(25) + base * 2; 
                break;
            case TORBOT:
                this.duration = robot.getRandom().nextInt(10) + base;
                break;
        }
        
        if(robot.aimLowGoal()) {
            this.pointValue = gameConstants.teleLow;
        } else {
            this.pointValue = gameConstants.teleHigh;
        }
        this.nextEvent = new idleEvent();
    }
    
}
