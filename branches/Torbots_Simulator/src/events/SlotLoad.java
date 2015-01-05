package events;

import objects.Robot;

public class SlotLoad extends Event {
    private Robot robot;
    private int boxes;
    
    public SlotLoad(int num_boxes, Robot robot) {
        super();
        this.robot = robot;
        duration = 2 * num_boxes; //Per tote I think it takes 2 seconds to laod a tote
        boxes = num_boxes;
    }
    
    public boolean isComplete() {
        if(timer >= duration && duration >= 0) {
            robot.grayTotes = boxes;
            return true;
        } else {
            return false;
        } 
    }
    
}
