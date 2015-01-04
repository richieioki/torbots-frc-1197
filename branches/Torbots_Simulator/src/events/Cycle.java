package events;

import java.util.Random;
import objects.Robot;
import objects.robotRank;

/**
 * Cycle class created to generate a new "randomized logical cycle"
 * This will allow us to see what order cycles occur in.
 */
public class Cycle {
    
    public CycleType m_type;
    private Random random;
    private Robot robot;
    
    public Cycle(Robot robot) {
        //determine which cycle type we are using
        random = new Random();
        m_type = CycleType.values()[random.nextInt(CycleType.values().length - 2)]; //picks a random enum value
        this.robot = robot;
        if(robot.rr == robotRank.TORBOT) {
            if(robot.getRandom().nextInt(2) == 0) {
                m_type = CycleType.TORBOT1;
            } else {
                m_type = CycleType.TORBOT2;
            }
        }
           
    }
    
    /**
     * This is where you would setup Cycles
     * It would theoretically setup linked lists based on random probability 
     * Or test specific combinations of results.
     * 
     * TO BE CODED UPON RELEASE OF THE GAME
     * 
     * @param robot
     * @return 
     */
    public Event setupCycle(Robot robot) {
        
        Event temp = null;
        
        switch (m_type) {
            //Put robot code for setting up cycles as linked lists basically here.  
            case TYPE1:
                
                break;

            case TYPE2:
                
                break;

            case TYPE3:
                
                break;

            case TYPE4:
                
                break;
                
            case TORBOT1: //Richie torbot strategy
                temp = new AquireBin(robot);
                temp.nextEvent = new DriveToSlot(robot);
                temp.nextEvent.nextEvent = new SlotLoad(robot.getStackAbility());
                temp.nextEvent.nextEvent.nextEvent = new DriveToPlatform(robot);
                temp.nextEvent.nextEvent.nextEvent.nextEvent = new StackTotes(robot);
                temp.nextEvent.nextEvent.nextEvent.nextEvent.nextEvent = new idleEvent();
                break;
                
            case TORBOT2: //Allen strategy
                if(robot.isFinishedCoop()) {
                    //finished coop so run get gray crate loop
                } else {
                    //continue doing coop
                    if(robot.coopTotes == 3) {
                        //assume that other team has provided tote
                        //so stack totes
                        temp = new DriveToStep(robot);
                        temp.nextEvent = new StackTotes(robot);
                        //probably need step to aquire other tote from other team.
                        temp.nextEvent = new idleEvent();
                        robot.setFinishedCoop(true);
                    } else { //since you have not finished keep finding yellow totes
                        temp = new AquireYBox(robot); //going to assume that you can carry them inside the robot  
                        temp.nextEvent = new idleEvent();
                        robot.coopTotes++;
                    }
                    
                }
                
                break;
        }
        
        
        return temp;
    }

    public Event startNewCycle() {
        return setupCycle(robot);
    }
    
}