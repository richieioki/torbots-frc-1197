package robotsim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import robots.basicRobot;
import robots.*;

/**
 *
 * @author richi_000
 */
public class Simulator {
    
    private basicRobot[] robots = new basicRobot[6];
    private int[] robotReference = new int[6];
    private PrintWriter log;
    private PrintWriter csvLogger;
    private Random rng;
    private statsGenerator stats;
    /**
     * Setup a random assortment of robots and assign them with randomized skills.
     * Randomized skills will be based on the type of robot
     */
    public void setupField() {
        setupLogging();
        rng = new Random();
        stats = new statsGenerator();
        //random number 0-10 if its 5 or less then mid if its even and higher then its noob else elite.
        for(int i = 0; i < robots.length; i++) {
            robotReference[i] = rng.nextInt(11);
            if(robotReference[i] <= 4) {
                robots[i] = new midRobot();
                robots[i] = stats.generateMidStats(robots[i]);
                log.println("Mid Tier Robot Initialized");
            } else if(robotReference[i] == 5 || robotReference[i] == 6 || robotReference[i] == 8 || robotReference[i] == 10 || robotReference[i] == 9) {
                robots[i] = new noobRobot();
                robots[i] = stats.generateNoobStats(robots[i]);
                log.println("Noob Robot Initialized");
            } else {
                robots[i] = new eliteRobot();
                robots[i] = stats.generateEliteStats(robots[i]);
                log.println("Elite Robot Initialized");
            }
        }
                
        //Pass teammates to robots
        robots[0].passTeammates(robots[1], robots[2]);
        robots[1].passTeammates(robots[0], robots[1]);
        robots[2].passTeammates(robots[0], robots[1]);
        robots[3].passTeammates(robots[4], robots[5]);
        robots[4].passTeammates(robots[3], robots[5]);
        robots[5].passTeammates(robots[3], robots[4]);
        log.println("Passed robots");
        log.println();
        log.flush();
    }
    
    public void runSimulation() {
        int AUTO_COUNT = 10;
        int TELE_COUNT = 120;
        int redPassCounter = 0;
        int bluePassCounter = 0;
        int redBallsOnField = 0;
        int blueBallsOnField = 0;
        boolean readyForCycleB = false, readyForCycleR = false;
        boolean redDefence, blueDefence;
        
        int redScore = 0, blueScore = 0;
        csvLogger.println("1,,,,,");
        System.out.println("Running Auto mode");
        log.println("Starting Auto Mode!");
        log.println("------------------------");
        //run autnomous
        for(int j = 0; j < robots.length; j++) {
            robots[j].runAuto();
            if(j < 3) {
                redScore += robots[j].getAutoScore();
                log.println("Robot " + j + " Scored " + robots[j].getAutoScore() + " for the red alliance");
            } else {
                blueScore += robots[j].getAutoScore();
                log.println("Robot " + j + " Scored " + robots[j].getAutoScore() + " for the blue alliance");
            }
            
        }
        flushLogs();
        
        System.out.println();
        log.println();
        log.println("--------------------");
        log.println("Red Alliance Auto Score : " + redScore);
        log.println("Blue Alliance Auto Score : " + blueScore);
        log.println();
        log.println("Entering TELE OP");
        log.println("------------------------");
        System.out.println("Running Teleop");
        //run teleop
        //check who has ball and so on.
        
        /**
         * Evaluate state of field first.
         * Tally up balls still on field
         */
        for(int i = 0; i < robots.length; i++) {
            if(robots[i].checkState() == robotState.GETTING_BALL) {
                if(i < 3) {
                    redBallsOnField++;
                } else {
                    blueBallsOnField++;
                }
            }
        }
        
        flushLogs();
        
        for(int i = 0; i < TELE_COUNT; i++) {
            if(redBallsOnField == 0) {
                readyForCycleR = true;
            }
            if(blueBallsOnField == 0) {
                readyForCycleB = true;
            }
            for(int j = 0; j < robots.length; j++) {
                //Checking if the robot has scored!
                if(robots[j].checkState() == robotState.DEFENCE) {
                    //if you are on defence then just play defence
                    //we are simply going to rule out temperory defence
                    //due to complexity
                    if(j < 3) {
                        redDefence = true;
                    } else {
                        blueDefence = true;
                    }
                } else {
                    int score = robots[j].run();
                    if(score > 0) {
                        System.out.println("Robot " + j + " scored " + score + " ponts");
                        log.println("Robot " + j + " scored " + score + " ponts");
                        if(j<3) {
                            redScore += score;
                        } else {
                            blueScore += score;
                        }
                    }
                }
            }
        }
        
        closeLogs();//finalizes log files
    }
    
    private void setupLogging() {
        try {        
            log = new PrintWriter(new FileWriter("robotSimLog.txt"));
            csvLogger = new PrintWriter(new FileWriter("robotSim.csv"));
        } catch (IOException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Couldn't write to file");
            System.exit(0);
        }
        
        log.println("Log file for Torbots Robot Sim");
        log.println();
        System.out.println();
        System.out.println("Log File Created");
        System.out.println("*****************");
        
        csvLogger.println("Match Number, Robot(Type), Team, AutoPoints, Points, Passes");
    }
    
    private void closeLogs() {
        log.flush();
        log.close();
        csvLogger.flush();
        csvLogger.close();
        
        System.out.println();
        System.out.println("Loggers closed");
    }
    
    private void flushLogs() {
        log.flush();
        csvLogger.flush();
        
        System.out.println("Loggers flushed");
    }
    
    private void determineStrategy() {
        
    }
}
