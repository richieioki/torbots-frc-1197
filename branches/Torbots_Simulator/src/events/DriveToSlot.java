package events;

import objects.Robot;


public class DriveToSlot extends Event {
    
    public DriveToSlot(Robot robot) {
        super();
        
        switch (robot.rr) {
            case ELITE:
                duration = robot.getRandom().nextInt(2) + 4; //I estimate that once teams pickup totes
                //elite teams will be on the platform in about 6 seconds
                break;

            case MID:
                duration = robot.getRandom().nextInt(3) + 5;
                //mid teams sometimes can load about the same time, but can't locate
                //as well and sometimes have more variables
                break;

            case LOW:
                duration = robot.getRandom().nextInt(10) + 5;
                //since most low teams will be pushing might take a little longer
                //however they will not be much slower on base
                break;

            case TORBOT:
                duration = robot.getRandom().nextInt(3) + 4;
                //I am hoping that we can have "elite" speed over the short distance
                break;
        }
    }
    
}
