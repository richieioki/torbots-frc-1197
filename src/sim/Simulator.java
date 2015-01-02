package sim;

import events.Cycle;
import events.defenseEvent;
import events.endGameEvent;
import events.gatherEvent;
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
        Cycle redCycle = new Cycle(); //red strategy
        
        Robot red1 = new Robot(redCycle); //When you create a robot it will randomly generate it stats.
        Robot red2 = new Robot(redCycle);
        Robot red3 = new Robot(redCycle);
        
        Alliance red = new Alliance("R", red1, red2, red3);
        
        Cycle blueCycle = new Cycle();
        
        Robot blue1 = new Robot(blueCycle);
        Robot blue2 = new Robot(blueCycle);
        Robot blue3 = new Robot(blueCycle);
        
        Alliance blue = new Alliance("B", blue1, blue2, blue3);
        
        thisMatch.setCycle(redCycle, blueCycle);
        
        runSimulation(red,  blue, matchNumber, redCycle, blueCycle);
        
        results.add(thisMatch);
    }

    /**
     * Poorly programmed, will change later to make more compact
     * @param red
     * @param blue
     * @param matchNumber 
     */
    private void runSimulation(Alliance red, Alliance blue, int matchNumber, Cycle redCycle, Cycle blueCycle) {
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
        thisMatch.auto[0] = (red.getOne().evalAuto());
        thisMatch.auto[1] = (red.getTwo().evalAuto());
        thisMatch.auto[2] = (red.getThree().evalAuto());
        thisMatch.auto[3] = blue.getOne().evalAuto();
        thisMatch.auto[4] = (blue.getTwo().evalAuto());
        thisMatch.auto[5] = (blue.getThree().evalAuto());

//        System.out.println("Concluded Auto Scoring");
        
        //setup for teleop by making sure each robot has initial task.
        //first set all defence robots to playing defence.
        //else set them to idle
        if(blue.getOne().getIsOnlyDefence()) {
            blue.getOne().event = new defenseEvent();
            isBlueD = true;
        } else {
            blue.getOne().event = new idleEvent();
        }
        if(blue.getTwo().getIsOnlyDefence()) {
            blue.getTwo().event = new defenseEvent();
            isBlueD = true;
        } else {
            blue.getTwo().event = new idleEvent();
        }
        if(blue.getThree().getIsOnlyDefence()) {
            blue.getThree().event = new defenseEvent();
            isBlueD = true;
        } else {
            blue.getThree().event = new idleEvent();
        }
        
        if(red.getOne().getIsOnlyDefence()) {
            red.getOne().event = new defenseEvent();
            isRedD = true;
        } else {
            red.getOne().event = new idleEvent();
        }
        if(red.getTwo().getIsOnlyDefence()) {
            red.getTwo().event = new defenseEvent();
            isRedD = true;
        } else {
            red.getTwo().event = new idleEvent();
        }
        if(red.getThree().getIsOnlyDefence()) {
            red.getThree().event = new defenseEvent();
            isRedD = true;
        } else {
            red.getThree().event = new idleEvent();
        }
        
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
            check(red, blue, isBlueD, isRedD, i, redCycle, blueCycle);
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
    private void check(Alliance red, Alliance blue, boolean isBlueD, boolean isRedD, int time, Cycle redCycle, Cycle blueCycle) {
        //Check for completed tasks
        //Tasks that are completed, but have a next task that is idle
        //must have scored points so be sure to add to data's robot score.
        if (red.getOne().event.isComplete()) {
            thisMatch.tele[0] = thisMatch.tele[0] + red.getOne().event.getPointValue(); //added completed events point value to total            
            red.getOne().event = red.getOne().event.getNextEvent(); //set next event be equal to current event.
        } else if (red.getOne().event instanceof idleEvent) { //means that the robot isn't playing defence
          
            if(red.getOne().getEndGame() > 0  && time >= 100) { //Checks to see if we are end game
                red.getOne().event = new endGameEvent(red.getOne(), false);
            } else {
                red.getOne().event = new gatherEvent(red.getOne(), isBlueD); //if not start a new cycle
            }
        }

        if (red.getTwo().event.isComplete()) {
            thisMatch.tele[1] = thisMatch.tele[1] + red.getTwo().event.getPointValue(); //added completed events point value to total           
            red.getTwo().event = red.getTwo().event.getNextEvent(); //set next event be equal to current event.
        } else if (red.getTwo().event instanceof idleEvent) { 
            if(red.getTwo().getEndGame() > 0  && time >= 100) {
                red.getTwo().event = new endGameEvent(red.getTwo(), false);
            } else {
                red.getTwo().event = new gatherEvent(red.getTwo(), isBlueD);
            }
        }

        if (red.getThree().event.isComplete()) {
            thisMatch.tele[2] = thisMatch.tele[2] + red.getThree().event.getPointValue(); //added completed events point value to total
            red.getThree().event = red.getThree().event.getNextEvent(); //set next event be equal to current event.
        } else if (red.getThree().event instanceof idleEvent) {
            
            if(red.getThree().getEndGame() > 0  && time >= 100) {
                red.getThree().event = new endGameEvent(red.getThree(), false);
            } else {
                red.getThree().event = new gatherEvent(red.getThree(), isBlueD);
            }
        }

        if (blue.getOne().event.isComplete()) {
            thisMatch.tele[3] = thisMatch.tele[3] + blue.getOne().event.getPointValue(); //added completed events point value to total
            blue.getOne().event = blue.getOne().event.getNextEvent(); //set next event be equal to current event.
        } else if (blue.getOne().event instanceof idleEvent) {
            
            if(blue.getOne().getEndGame() > 0  && time >= 100) {
                blue.getOne().event = new endGameEvent(blue.getOne(), false);
            } else {
                blue.getOne().event = new gatherEvent(blue.getOne(), isRedD);
            }
        }

        if (blue.getTwo().event.isComplete()) {
            thisMatch.tele[4] = thisMatch.tele[4] + blue.getTwo().event.getPointValue(); //added completed events point value to total
            blue.getTwo().event = blue.getTwo().event.getNextEvent(); //set next event be equal to current event.
        } else if (blue.getTwo().event instanceof idleEvent) {
            
            if(blue.getTwo().getEndGame() > 0  && time >= 100) {
                blue.getTwo().event = new endGameEvent(blue.getTwo(), false);
            } else {
                blue.getTwo().event = new gatherEvent(blue.getTwo(), isRedD);
            }
        }
        
        if (blue.getThree().event.isComplete()) {
            thisMatch.tele[5] = thisMatch.tele[5] + blue.getThree().event.getPointValue(); //added completed events point value to total
            blue.getThree().event = blue.getThree().event.getNextEvent(); //set next event be equal to current event.
        } else if (blue.getThree().event instanceof idleEvent) {
            
            if(blue.getThree().getEndGame() > 0  && time >= 100) {
                blue.getThree().event = new endGameEvent(blue.getThree(), false);
            } else {
                blue.getThree().event = new gatherEvent(blue.getThree(), isRedD);
            }
        }
    }
}
