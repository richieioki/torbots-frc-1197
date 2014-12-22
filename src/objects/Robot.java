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
        //one in 10 is us
        
        int tier = random.nextInt(10);
        
        if(tier == 0) {
            rr = robotRank.TORBOT;
            torbotSetup();
        } else if(tier <= 1) { //if robot rank is either 1-2
            rr = robotRank.ELITE;
            eliteSetup();
        }
        else if(tier <= 5) { //if robot rank is between 3-6 mid
            rr = robotRank.MID;
            midSetup();
        } 
        else {
            rr = robotRank.LOW;
            lowSetup();
        }
        
        event = null;
    }
    
    /**
     * Function to determine how many points were scored in autonomous
     * @return auto points scored
     */
    public int evalAuto() {
        if(autoAbility > 0) {
            if(lowGoal) {
                return autoAbility/4 * gameConstants.autoLowGoal;
            } else {
                return autoAbility/4 * gameConstants.autoHighGOal; //should allow for some teams to take multiple shots.
                //Teams with 10's or the elite level teams will be able to shoot twice.  I know some teams can shoot like 
                //3 times, but we are not counting those teams at this moment.
            }
        } else {
            return 0; 
        }
    }

    private void eliteSetup() {
        onlyDefence = false;
        shootAbility = random.nextInt(5) + 5;
        endGameAbility = random.nextInt(6) + 3;
        autoAbility = random.nextInt(6) + 4;
        lowGoal = Math.random() < 0.1;
        
//        System.out.println("Elite robot setup");
    }

    private void midSetup() {
        onlyDefence = false;
        shootAbility = random.nextInt(5) + 3;
        endGameAbility = random.nextInt(4) + 4;
        autoAbility = random.nextInt(6) + 2;
        lowGoal = Math.random() < 0.3;
        
//        System.out.println("Mid robot setup");
    }

    private void lowSetup() {
        int d = random.nextInt(3);//coin flip
        
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
//        System.out.println("low robot setup");
    } 
    
    private void torbotSetup() {
        onlyDefence = false;
        shootAbility = random.nextInt(2) + 4;
        endGameAbility = random.nextInt(3) + 4;
        autoAbility = random.nextInt(3) + 3;
        lowGoal = false;
    }
    
    public boolean getIsOnlyDefence() {
        return this.onlyDefence;
    }

    public void update() {
        if(this.event != null) {
            event.update();
        }
    }
    
    public Random getRandom() {
        return random;
    }
    
    public boolean aimLowGoal() {
        return this.lowGoal;
    }
    
    public int getEndGame() {
        return this.endGameAbility;
    }
}