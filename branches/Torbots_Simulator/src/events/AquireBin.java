package events;

import objects.Robot;

public class AquireBin extends Event {
    
    public AquireBin(Robot robot) {
        super();
        
        //represents the average time to find and load a recycle bin
        switch(robot.rr) {
            case ELITE:
                duration = robot.getRandom().nextInt(5) + 5; //I estimate about 5 seconds to load with a 5 second variance
                break;
                
            case MID:
                duration = robot.getRandom().nextInt(10) + 5; 
                //mid teams sometimes can load about the same time, but can't locate
                //as well and sometimes have more variables
                break;
                
            case LOW:
                //no code here because low teams can't load bins
                System.err.println("LOW TIER TEAM SHOULDN'T BE LOADING BIN");
                break;
                
            case TORBOT:
                duration = robot.getRandom().nextInt(10) + 5; 
                //mid teams sometimes can load about the same time, but can't locate
                //I estimate we are about a low level team in this case.
                break;
        }
    }
    
}
