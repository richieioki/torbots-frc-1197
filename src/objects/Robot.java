package objects;

import events.Cycle;
import events.Event;
import java.util.Random;

public class Robot {

    private Random random;
    public robotRank rr;
    public Cycle cycle;

    //only defence isn't important becasue there is no defence this year
//    private boolean onlyDefence; //if robot is only defence then they just constantly play defence.
//    private boolean lowGoal; //if a robot aims for the low goal or high goal
    private int stackAbility; //how many boxes can you stack
    private boolean binAbility; //can you handle the bin
    private boolean coopAbility; //can you do coop
    private boolean finishedCoop;
//    private int shootAbility;
//    private int endGameAbility; //abilities are ranked 0-9 where 9 basically means you score almost every time

    public int grayTotes;
    public boolean hasBin;
    
    public int coopTotes;
    
    
    public int numTrips;
    public Event event;

    public Robot() {
        //generate random stats
        random = new Random();
        
        hasBin = false;
        grayTotes = 0; numTrips = 0;

        finishedCoop = false;

        //first determine what range our team falls into
        //Elite, Mid Tier, or Low Tier
        //There roughly 20% of teams are elite, about another 40 percent are mid
        //and the last 40 are low tier teams
        //one in 10 is us
        int tier = random.nextInt(10);

        if (tier == 0) {
            rr = robotRank.TORBOT;
            torbotSetup();
        } else if (tier <= 1) { //if robot rank is either 1-2
            rr = robotRank.ELITE;
            eliteSetup();
        } else if (tier <= 5) { //if robot rank is between 3-6 mid
            rr = robotRank.MID;
            midSetup();
        } else {
            rr = robotRank.LOW;
            lowSetup();
        }

        cycle = new Cycle(this); //to determine what strategy this robot is doing.

        event = null;
    }

    /**
     * Function to determine how many points were scored in autonomous
     *
     * @return auto points scored
     */
    public int evalAuto() {
        int score = 4; //we are going to assume that most robots can drive forwards, we are ignorning broken robots

        if (rr == robotRank.ELITE) {
            if (random.nextInt(20) == 5) {
                score += 20;
            }
        } else {

            int rand = random.nextInt(10);
            //some percent will push crates
            if (rand < 4) {
                score += 6;
            } //some percent will push recycle bins
            //and some percent will push nothing, but we already convered that.
            else if (rand < 8) {
                score += 8;
            }
        }
        return score;
    }

    //should eventually replace with weighted values.
    private void eliteSetup() {
        stackAbility = random.nextInt(6) + 1;
        binAbility = true;
        coopAbility = stackAbility >= 4;
    }

    private void midSetup() {
        stackAbility = random.nextInt(5) + 1;
        binAbility = random.nextInt(10) < 3;
        coopAbility = stackAbility >= 4;
    }

    private void lowSetup() {
        stackAbility = random.nextInt(2) + 1;
        binAbility = false;
        coopAbility = true;
    }

    private void torbotSetup() {
        stackAbility = 4;
        binAbility = false;
        coopAbility = stackAbility >= 4;
    }

    public void update() {
        if (this.event != null) {
            event.update();
        }
    }

    public Random getRandom() {
        return random;
    }

    public int getStackAbility() {
        return stackAbility;
    }

    public void setStackAbility(int stackAbility) {
        this.stackAbility = stackAbility;
    }

    public boolean isBinAbility() {
        return binAbility;
    }

    public void setBinAbility(boolean binAbility) {
        this.binAbility = binAbility;
    }

    public boolean isCoopAbility() {
        return coopAbility;
    }

    public void setCoopAbility(boolean coopAbility) {
        this.coopAbility = coopAbility;
    }

    public boolean isFinishedCoop() {
        return finishedCoop;
    }

    public void setFinishedCoop(boolean finishedCoop) {
        this.finishedCoop = finishedCoop;
    }
}
