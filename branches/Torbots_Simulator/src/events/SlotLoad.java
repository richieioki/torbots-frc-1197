package events;

public class SlotLoad extends Event {
    
    public SlotLoad(int num_boxes) {
        super();
        
        duration = 2 * num_boxes; //Per tote I think it takes 2 seconds to laod a tote
    }
    
}
