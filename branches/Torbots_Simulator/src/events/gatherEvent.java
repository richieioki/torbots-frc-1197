package events;

import objects.Robot;
import objects.robotRank;

public class gatherEvent extends Event {

    /**
     * Event to gather game piece.
     * Time depends on stats of robot
     * Will have wider range to account for possible travel time
     * @param one
     * @param blueD 
     */
    public gatherEvent(Robot robot, boolean defense) {
        super();
        
        int base = 5;
        if(defense) {
            base = 8;
        }
        
        switch(robot.rr) {
            case ELITE: //elite robots have a better chance of getting to ball
                //I am going to estimate that elite teams find and collect
                //objects in about 5-15 seconds
                this.duration = robot.getRandom().nextInt(10) + base;
                break;
            case MID:
                //MID level robots have the possiblity to be about as good as
                //an elite robot, but the max value is much higher.
                this.duration = robot.getRandom().nextInt(25) + base;
                break;
            case LOW:
                //low level robots take a while to collect objects, even taking
                //most of a match sometimes to get one ball
                this.duration = robot.getRandom().nextInt(55) + base * 2; 
                //low level teams deal with defense even worse than elite/mid teams
                break;
            case TORBOT:
                this.duration = robot.getRandom().nextInt(20) + base;
                break;
        }
        
        this.pointValue = 0; //no points are awarded for gathering the piece
        this.nextEvent = new moveToTargetEvent(robot, defense);
    }
    
}
