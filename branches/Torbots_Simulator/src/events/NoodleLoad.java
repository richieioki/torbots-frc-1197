package events;

public class NoodleLoad extends Event{

    public NoodleLoad() {
        super();
        //noodle load will take about 3 seconds to complete
        this.duration = 3;
    }
    
}
