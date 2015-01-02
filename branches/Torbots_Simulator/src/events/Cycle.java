package events;

import java.util.Collections;
import java.util.Random;
import objects.Robot;

/**
 * Cycle class created to generate a new "randomized logical cycle"
 * This will allow us to see what order cycles occur in.
 */
public class Cycle {
    
    public CycleType m_type;
    private Random random;
    
    public Cycle() {
        //determine which cycle type we are using
        random = new Random();
        m_type = CycleType.values()[random.nextInt(CycleType.values().length)]; //picks a random enum value
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
        
        
        
        
        return null;
    }
    
}
