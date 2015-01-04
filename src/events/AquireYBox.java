package events;

import objects.Robot;

public class AquireYBox extends Event {
    
    public AquireYBox(Robot robot) {
        super();
        
        //represents the average time to find and load a yellow tote from the floor
        //I left same as grey because it is hard to assume where you are going to be
        //on the field at time of search.
        switch(robot.rr) {
            case ELITE:
                duration = robot.getRandom().nextInt(5) + 8; //I estimate about 5 seconds to load with a 5 second variance
                break;
                
            case MID:
                duration = robot.getRandom().nextInt(5) + 10; 
                //mid teams sometimes can load about the same time, but can't locate
                //as well and sometimes have more variables
                break;
                
            case LOW:
                duration = robot.getRandom().nextInt(20) + 10;
                //some low teams will be able to load quickly/get into pushing position
                //however the randomness will be higher in the time spent doing the task.
                break;
                
            case TORBOT:
                duration = robot.getRandom().nextInt(15) + 8; 
                //I am estimating that we could load at elite-ish level speed
                //but are not as capable at finding the totes
                break;
        }
    }
    
}
