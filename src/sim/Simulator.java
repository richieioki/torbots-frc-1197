package sim;

import java.util.ArrayList;
import objects.Robot;

public class Simulator {
    
    private ArrayList<Data> results;
    private Data thisMatch;
    
    public Simulator() {
        results = new ArrayList<Data>();
    }
    
    void printResults() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void run() {
        setupSim();
    }

    private void setupSim() {
        Robot red1 = new Robot(); //When you create a robot it will randomly generate it stats.
        Robot red2 = new Robot();
        Robot red3 = new Robot();
        
        Alliance red = new Alliance("R", red1, red2, red3);
        
        Robot blue1 = new Robot();
        Robot blue2 = new Robot();
        Robot blue3 = new Robot();
        
        Alliance blue = new Alliance("B", blue1, blue2, blue3);
        
        runSimulation(red,  blue);
    }

    private void runSimulation(Alliance red, Alliance blue) {
        //Autonomous
        
        //teleop
        for(int i = 0; i < 120; i++) {
            //Check robot status
            
            //assign robot tasks if idle
            
            //add scores
        }
        
        //print out data
    }
    
}
