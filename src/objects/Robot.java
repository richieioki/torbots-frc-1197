package objects;

import events.Event;
import game.gameConstants;
import java.util.Random;


public class Robot {
    
    private Random random;
    public robotRank rr;
    
    private boolean onlyDefence; //if robot is only defence then they just constantly play defence.
    private boolean lowGoal; //if a robot aims for the low goal or high goal
    
    private int autoAbility; //determines what range they can score in auto   
    private int shootAbility;
    private int endGameAbility; //abilities are ranked 0-9 where 9 basically means you score almost every time
    
    public Event event;
    
    public Robot() {
        //generate random stats
        random = new Random();
        //first determine what range our team falls into
        //Elite, Mid Tier, or Low Tier
        //There roughly 20% of teams are elite, about another 40 percent are mid
        //and the last 40 are low tier teams
        
        int tier = random.nextInt(10);
        
        if(tier <= 1) { //if robot rank is either 0-1
            rr = robotRank.ELITE;
            eliteSetup();
        }
        else if(tier <= 5) { //if robot rank is between 2-5 mid
            rr = robotRank.MID;
            midSetup();
        }
        else {
            rr = robotRank.LOW;
            lowSetup();
        }
    }
    
    /**
     * Function to determine how many points were scored in autonomous
     * @return auto points scored
     */
    public int evalAuto() {
        if(autoAbility > 0) {
            if(lowGoal) {
                return autoAbility/5 * gameConstants.autoLowGoal;
            } else {
                return autoAbility/5 * gameConstants.autoHighGOal; //should allow for some teams to take multiple shots.
            }
        } else {
            return 0; 
        }
    }

    private void eliteSetup() {
        onlyDefence = false;
        shootAbility = random.nextInt(5) + 5;
        endGameAbility = random.nextInt(6) + 4;
        autoAbility = random.nextInt(6) + 4;
        lowGoal = Math.random() < 0.1;
        
        System.out.println("Elite robot setup");
    }

    private void midSetup() {
        onlyDefence = false;
        shootAbility = random.nextInt(5) + 3;
        endGameAbility = random.nextInt(6) + 4;
        autoAbility = random.nextInt(6) + 2;
        lowGoal = Math.random() < 0.3;
        
        System.out.println("Mid robot setup");
    }

    private void lowSetup() {
        int d = random.nextInt(2);//coin flip
        
        if(d == 1) { //only defence low level robot
            onlyDefence = true;
            shootAbility = 0;
            endGameAbility = 0;
            autoAbility = 0;
            lowGoal = false;
        } else { //robot can do some things...
            onlyDefence = false;
            shootAbility = random.nextInt(4) + 1;
            endGameAbility = random.nextInt(4);
            autoAbility = random.nextInt(3);
            lowGoal = Math.random() < 0.5;
        }
        System.out.println("low robot setup");
    } 
    
    
}