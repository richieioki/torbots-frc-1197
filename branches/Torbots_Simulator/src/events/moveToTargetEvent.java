package events;

import objects.Robot;

public class moveToTargetEvent extends Event{

    moveToTargetEvent(Robot robot, boolean defense) {
        super();
        
        int base = 4;
        if(defense) {
            base = 6;
        }
        //Since this is moving to the target with your game piece
        //robots can move across very quickly.  We will estimate that an
        //unimpeded top robot can do so from the gather point to some fire
        //position in about 4-5 seconds and the best teams can avoid
        //defense with only a slight penelty
        switch(robot.rr) {
            case ELITE: 
                this.duration = robot.getRandom().nextInt(5) + base;
                break;
            case MID:
                //MID I will estimate that on average mid tier drivers are not
                //as skilled as top tier and will assign a 2 second increase
                this.duration = robot.getRandom().nextInt(5) + base + 2;
                break;
            case LOW:
                //many lower level teams can still move very fast
                //thus we will assign just a wider range.
                this.duration = robot.getRandom().nextInt(15) + base; 
                break;
            case TORBOT:
                this.duration = robot.getRandom().nextInt(5) + base + 2; //we are about mid tier in terms of speed/driving
                //sometimes probably worse
        }
        
        this.pointValue = 0; //no points are awarded for gathering the piece
        this.nextEvent = new fireEvent(robot, defense);
    }
    
}
