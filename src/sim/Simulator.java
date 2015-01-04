package sim;

import events.idleEvent;
import game.gameConstants;
import java.util.ArrayList;
import objects.Robot;

public class Simulator {
    
    public ArrayList<Data> results;
    private Data thisMatch;
    private int numRuns;
    
    public Simulator(int in) {
        results = new ArrayList<Data>();
        numRuns = in;
    }
    
    void printResults() {
//        System.out.println("The match score was red " + thisMatch.getRedTotal() + " blue " + thisMatch.getBlueTotal());
    }

    void run() {
        for(int i = 0; i < numRuns; i++) {
            setupSim(i);
            printResults();
        }
    }

    /**
     * Sets up a single match
     * @param matchNumber 
     */
    private void setupSim(int matchNumber) {
        
        Robot red1 = new Robot(); //When you create a robot it will randomly generate it stats.
        Robot red2 = new Robot();
        Robot red3 = new Robot();
        
        Alliance red = new Alliance("R", red1, red2, red3);
        
        Robot blue1 = new Robot();
        Robot blue2 = new Robot();
        Robot blue3 = new Robot();
        
        Alliance blue = new Alliance("B", blue1, blue2, blue3);
        
        thisMatch.cycles[0] = red1.cycle;
        thisMatch.cycles[1] = red2.cycle;
        thisMatch.cycles[2] = red3.cycle;
        thisMatch.cycles[3] = blue1.cycle;
        thisMatch.cycles[4] = blue2.cycle;
        thisMatch.cycles[5] = blue3.cycle;
        
        runSimulation(red,  blue, matchNumber);
        
        results.add(thisMatch);
    }

    /**
     * Poorly programmed, will change later to make more compact
     * @param red
     * @param blue
     * @param matchNumber 
     */
    private void runSimulation(Alliance red, Alliance blue, int matchNumber) {
        boolean isBlueD = false, isRedD = false;
        thisMatch = new Data(matchNumber);
        
        //record robot types
        thisMatch.ranks[0] = red.getOne().rr;
        thisMatch.ranks[1] = red.getTwo().rr;
        thisMatch.ranks[2] = red.getThree().rr;
        thisMatch.ranks[3] = blue.getOne().rr;
        thisMatch.ranks[4] = blue.getTwo().rr;
        thisMatch.ranks[5] = blue.getThree().rr;
        
        //Autonomous
        int largest = red.getOne().evalAuto();
        int temp = red.getTwo().evalAuto();
        if(largest < temp) {
            largest = temp;
        } 
        temp = red.getThree().evalAuto();
        if(largest < temp) {
            largest = temp;
        }
        
        thisMatch.auto[0] = largest;
        thisMatch.auto[1] = largest;
        thisMatch.auto[2] = largest;
        
        largest = blue.getOne().evalAuto();
        temp = blue.getTwo().evalAuto();
        if(largest < temp) {
            largest = temp;
        } 
        temp = blue.getThree().evalAuto();
        if(largest < temp) {
            largest = temp;
        }
        
        thisMatch.auto[3] = largest;
        thisMatch.auto[4] = largest;
        thisMatch.auto[5] = largest;
        
        //setup for teleop by making sure each robot has initial task.
        //first set all defence robots to playing defence.
        //else set them to idle

            blue.getOne().event = new idleEvent();
            blue.getTwo().event = new idleEvent();
            blue.getThree().event = new idleEvent();
      
            red.getOne().event = new idleEvent();
            red.getTwo().event = new idleEvent();
            red.getThree().event = new idleEvent();
        
        //teleop
        //For initial purposes we will assume that robots will try to shoot if
        //they are capable of it.  And that there are an unlimited number
        //of objects to shoot at in the moment.
        //We will also ignore robots shifting from offense to defense
        
        //Need to program a way to have multiple "strategies"
        for(int i = 0; i < gameConstants.matchDuration; i++) {
            //Check robot status
            updateRobots(red, blue);
            //assign robot tasks if idle
            check(red, blue, isBlueD, isRedD, i);
        }
//        System.out.println("Concluded Teleop scoring");
        //print out data
    }    

    private void updateRobots(Alliance red, Alliance blue) {
        red.getOne().update();
        red.getTwo().update();
        red.getThree().update();
        blue.getOne().update();
        blue.getTwo().update();
        blue.getThree().update();
    }

    /**
     * Checks to see if functions are either in the idle state or completed state
     * If events have been completed set robot to idle phase.  Each robot
     * must take one turn off between shifting from completed to next task.
     * If robot is in idle phase a new "cycle" is started.
     * 
     * Needs to be compacted down into loops, too lazy atm
     * Need to involve cycles
     */
    private void check(Alliance red, Alliance blue, boolean isBlueD, boolean isRedD, int time) {
        //Check for completed tasks
        //Tasks that are completed, but have a next task that is idle
        //must have scored points so be sure to add to data's robot score.
        if (red.getOne().event.isComplete()) {
            thisMatch.tele[0] = thisMatch.tele[0] + red.getOne().event.getPointValue(); //added completed events point value to total            
            red.getOne().event = red.getOne().event.getNextEvent(); //set next event be equal to current event.
        } else if (red.getOne().event instanceof idleEvent) { //means that the robot isn't playing defence        
//            if(red.getOne().getEndGame() > 0  && time >= 100) { //Checks to see if we are end game
//                red.getOne().event = new endGameEvent(red.getOne(), false);
//            } else {
//                red.getOne().event = new gatherEvent(red.getOne(), isBlueD); //if not start a new cycle
//            }
            
            red.getOne().event = red.getOne().cycle.startNewCycle();
        }

        if (red.getTwo().event.isComplete()) {
            thisMatch.tele[1] = thisMatch.tele[1] + red.getTwo().event.getPointValue(); //added completed events point value to total           
            red.getTwo().event = red.getTwo().event.getNextEvent(); //set next event be equal to current event.
        } else if (red.getTwo().event instanceof idleEvent) { 
//            if(red.getTwo().getEndGame() > 0  && time >= 100) {
//                red.getTwo().event = new endGameEvent(red.getTwo(), false);
//            } else {
//                red.getTwo().event = new gatherEvent(red.getTwo(), isBlueD);
//            }
            red.getTwo().event = red.getTwo().cycle.startNewCycle();
        }

        if (red.getThree().event.isComplete()) {
            thisMatch.tele[2] = thisMatch.tele[2] + red.getThree().event.getPointValue(); //added completed events point value to total
            red.getThree().event = red.getThree().event.getNextEvent(); //set next event be equal to current event.
        } else if (red.getThree().event instanceof idleEvent) {          
//            if(red.getThree().getEndGame() > 0  && time >= 100) {
//                red.getThree().event = new endGameEvent(red.getThree(), false);
//            } else {
//                red.getThree().event = new gatherEvent(red.getThree(), isBlueD);
//            }
            red.getThree().event = red.getThree().cycle.startNewCycle();
        }

        if (blue.getOne().event.isComplete()) {
            thisMatch.tele[3] = thisMatch.tele[3] + blue.getOne().event.getPointValue(); //added completed events point value to total
            blue.getOne().event = blue.getOne().event.getNextEvent(); //set next event be equal to current event.
        } else if (blue.getOne().event instanceof idleEvent) {            
//            if(blue.getOne().getEndGame() > 0  && time >= 100) {
//                blue.getOne().event = new endGameEvent(blue.getOne(), false);
//            } else {
//                blue.getOne().event = new gatherEvent(blue.getOne(), isRedD);
//            }
            blue.getOne().event = blue.getOne().cycle.startNewCycle();
        }
        if (blue.getTwo().event.isComplete()) {
            thisMatch.tele[4] = thisMatch.tele[4] + blue.getTwo().event.getPointValue(); //added completed events point value to total
            blue.getTwo().event = blue.getTwo().event.getNextEvent(); //set next event be equal to current event.
        } else if (blue.getTwo().event instanceof idleEvent) {          
//            if(blue.getTwo().getEndGame() > 0  && time >= 100) {
//                blue.getTwo().event = new endGameEvent(blue.getTwo(), false);
//            } else {
//                blue.getTwo().event = new gatherEvent(blue.getTwo(), isRedD);
//            }
            blue.getTwo().event = blue.getTwo().cycle.startNewCycle();
        }
        
        if (blue.getThree().event.isComplete()) {
            thisMatch.tele[5] = thisMatch.tele[5] + blue.getThree().event.getPointValue(); //added completed events point value to total
            blue.getThree().event = blue.getThree().event.getNextEvent(); //set next event be equal to current event.
        } else if (blue.getThree().event instanceof idleEvent) {     
//            if(blue.getThree().getEndGame() > 0  && time >= 100) {
//                blue.getThree().event = new endGameEvent(blue.getThree(), false);
//            } else {
//                blue.getThree().event = new gatherEvent(blue.getThree(), isRedD);
//            }
            blue.getThree().event = blue.getThree().cycle.startNewCycle();
        }
    }
}
