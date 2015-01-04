package events;

import objects.Robot;

/**
 * Assuming you have the yellow crate you can now drive to the step.
 */
public class DriveToStep extends Event {
    
    public DriveToStep(Robot robot) {
        super();
        
        switch (robot.rr) {
            case ELITE:
                duration = robot.getRandom().nextInt(2) + 6; //since the step is
                //further i will add a little to the base
                break;

            case MID:
                duration = robot.getRandom().nextInt(3) + 7;
                //mid teams sometimes can load about the same time, but can't locate
                //as well and sometimes have more variables
                break;

            case LOW:
                duration = robot.getRandom().nextInt(10) + 7;
                //since most low teams will be pushing might take a little longer
                //however they will not be much slower on base
                break;

            case TORBOT:
                duration = robot.getRandom().nextInt(3) + 7;
                //I am hoping that we can have "elite" speed over the short distance
                break;
        }
    }
    
}
