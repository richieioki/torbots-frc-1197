package events;

import objects.Robot;

public class StackTotes extends Event {
    //Assumes you are in position.
    private Robot robot;
   
    public StackTotes(Robot robot) {
        super();
        this.robot = robot;
        switch (robot.rr) {
            case ELITE:
                duration = robot.getRandom().nextInt(3) + 5; //since the step is
                //further i will add a little to the base
                break;

            case MID:
                duration = robot.getRandom().nextInt(10) + 6;
                //mid teams sometimes can load about the same time, but can't locate
                //as well and sometimes have more variables
                break;

            case LOW:
                duration = robot.getRandom().nextInt(20) + 1;
                //since some low teams will simply just push the tote onto the platform
                //they will take the least amount of time
                break;

            case TORBOT:
                duration = robot.getRandom().nextInt(3) + 6;
                //I'm hoping that we should be able to unload in a short time span.
                break;
        }
    }
    
    @Override
    public boolean isComplete() {
        if(timer >= duration && duration >= 0) {
            this.pointValue = 2 * robot.grayTotes;
            
            if(robot.isBinAbility() && robot.hasBin) {
                this.pointValue += 4 * robot.grayTotes;
                robot.hasBin = false;
            }
            return true;
        } else {
            return false;
        }        
        
    }
}