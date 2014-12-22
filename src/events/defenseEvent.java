package events;

import game.gameConstants;

/**
 * defense event so that robots only playing defense still have an event.
 * @author gaming
 */
public class defenseEvent extends Event {
    
    public defenseEvent() {
        super();
        this.duration = gameConstants.matchDuration + 1;
        this.pointValue = 0;
    }
    
}
