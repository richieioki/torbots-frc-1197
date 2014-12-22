package events;

import objects.Robot;

public class endGameEvent extends Event {
    
    private Robot m_robot;
    private boolean completed;
    
    public endGameEvent(Robot robot, boolean completed) {
        super();
        
        //takes the last 20 seconds
        duration = 20;
        this.pointValue = 20;
        m_robot = robot;
        if(!completed) { //if we have already completed the mission don't make another 
            nextEvent = new endGameEvent(m_robot, true);
        }
        this.completed = completed;
    }
    
    @Override
    public boolean isComplete() {
        if(completed) {
            return false; //also make sure that if we have completed there is no
            //next event
        }
        return timer >= (20 - (m_robot.getEndGame() * 2));
    }
    
}
