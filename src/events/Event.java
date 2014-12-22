package events;

/**
 * Event class that basically holds data for how an event is evaluated
 * 
 */
public class Event {
    protected int duration;
    protected int pointValue;
    protected int timer;
    protected Event nextEvent;
    
    public Event() {
        timer = 0;
        duration = -1;
    }
    
    public void update() {
        timer++;
    }
    
    public boolean isComplete() {
        if(timer >= duration && duration >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public Event getNextEvent() {
        return nextEvent;
    }
    
    public int getPointValue() {
        return pointValue;
    }
}
