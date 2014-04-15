package robotsim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import objects.ball;
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

    boolean readyForCycleB = false, readyForCycleR = false;
    boolean redDefence, blueDefence;

    ball redBall, blueBall;
    private int redBallsOnField = 0;
    private int blueBallsOnField = 0;

    /**
     * Setup a random assortment of robots and assign them with randomized
     * skills. Randomized skills will be based on the type of robot
     */
    public void setupField() {
        setupLogging();
        rng = new Random();
        stats = new statsGenerator();
        //random number 0-10 if its 5 or less then mid if its even and higher then its noob else elite.
        for (int i = 0; i < robots.length; i++) {
            robotReference[i] = rng.nextInt(11);
            if (robotReference[i] <= 4) {
                robots[i] = new midRobot();
                robots[i] = stats.generateMidStats(robots[i]);
                log.println("Mid Tier Robot Initialized");
            } else if (robotReference[i] == 5 || robotReference[i] == 6 || robotReference[i] == 8 || robotReference[i] == 10 || robotReference[i] == 9) {
                robots[i] = new lowRobot();
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

        blueBall = new ball("blue");
        redBall = new ball("red");

        int redScore = 0, blueScore = 0;

        System.out.println("Running Auto mode");
        log.println("Starting Auto Mode!");
        log.println("------------------------");
        
        
        //run autnomous
        for (int j = 0; j < robots.length; j++) {
            robots[j].runAuto();
            if (j < 3) {
                redScore += robots[j].getAutoScore();
                log.println("Robot " + j + " Scored " + robots[j].getAutoScore() + " for the red alliance");

            } else {
                blueScore += robots[j].getAutoScore();
                log.println("Robot " + j + " Scored " + robots[j].getAutoScore() + " for the blue alliance");
            }
            if (robots[j].getState() == robotState.GETTING_BALL) {
                log.println("Robot " + j + " Missed");
            }
        }

        //Log Auto Data
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
        readyForCycleR = false;
        readyForCycleB = false;

        /**
         * Evaluate state of field first. Tally up balls still on field after
         * auto
         */
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].checkState() == robotState.GETTING_BALL) {
                if (i < 3) {
                    redBallsOnField++;
                } else {
                    blueBallsOnField++;
                }
            }
        }

        for (int i = 0; i < TELE_COUNT; i++) {
            determineStrategy();

            //Checking if a cycle has been completed
            if (redBallsOnField == 0) {
                readyForCycleR = true;
                redBall.reset();
                System.out.println("Ready for Red Cycle");
            }
            if (blueBallsOnField == 0) {
                readyForCycleB = true;
                blueBall.reset();
                System.out.println("Ready for Blue Cycle");
            }
            //System.out.println("B " + blueBallsOnField + "  R " + redBallsOnField);
            log.println("Counter = " + i);

            for (int j = 0; j < robots.length; j++) {
                if (robots[j].hasBall()) {
                    System.out.println("Robot " + j + " Has a ball");
                }
                System.out.println("Robot number : " + j + "  State = " + robots[j].getState().name());
                log.println("Robot number : " + j + "  State = " + robots[j].getState().name());

                //Checking if the robot has scored!
                int score = robots[j].run();
                if (score > 0) {
                    System.out.println("Robot " + j + " scored " + score + " ponts");
                    log.println("Robot " + j + " scored " + score + " ponts");
                    if (j < 3) {
                        redScore += score;
                        redBallsOnField--;
                    } else {
                        blueScore += score;
                        blueBallsOnField--;
                    }

                }
            }
        }
        log.println("Match has ended");
        log.println("Red score : " + redScore + " Blue score : " + blueScore);
        System.out.println("Match has ended\nPrinting out data");
        //Print out match data
        for (int i = 0; i < robots.length; i++) {
            if (i < 3) {
                csvLogger.println("1," + i + "," + robots[i].getType() + ",Red," + robots[i].getAutoScore() + "," + robots[i].getTeleScore() + ","); //need to still add passes
            } else {
                csvLogger.println("1," + i + "," + robots[i].getType() + ",Blue," + robots[i].getAutoScore() + "," + robots[i].getTeleScore() + ","); //need to still add passes
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

        csvLogger.println("Match Number, Robot Number, Robot Type, Team, AutoPoints, Tele Points, Passes");
    }

    private void closeLogs() {
        log.flush();
        log.close();
        csvLogger.flush();
        csvLogger.close();

        System.out.println();
        System.out.println("Loggers closed");
    }

    //For testing purposes to see what logs record at key points.
    private void flushLogs() {
        log.flush();
        csvLogger.flush();

        System.out.println("Loggers flushed");
    }

    private void determineStrategy() {
        //Sending robots back to the loading station
        if(readyForCycleR) {
            for(int i = 0; i < 3; i ++) {
                if(robots[i].canPickup() && robots[i].getState() != robotState.WAITING_FOR_BALL) {
                    robots[i].returnToStation();
                } 
            }
        } else if(readyForCycleB) {
            for(int i = 3; i < 6; i++) {
                if(robots[i].canPickup() && robots[i].getState() != robotState.WAITING_FOR_BALL) {
                    robots[i].returnToStation();
                } 
            }
        }
        
        //Check if a robot at human station, give robot the ball
        
        
        //determine if robot with ball should pass or truss
        
        
        //determine if robot with ball should just shoot
    }
}
