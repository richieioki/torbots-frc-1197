/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package robots;

import java.util.Random;
import objects.ball;

public class eliteRobot implements basicRobot {
    
    private Speed m_speed;
    private boolean m_pickup, m_shoot, m_lowGoal, m_pass, m_truss, m_catch;
    private RobotType type;
    
    private basicRobot teammate1, teammate2;
    private ball m_ball;
    
    private Random rng;
    private int autoScore;
    private robotState m_state;
    private int counter = 0;
    private int completionCounter = 0;

    public eliteRobot() {
        type = RobotType.Elite;
    }
    
    public void initRobot(Speed speed, boolean canCatch) {
        //Most elite robots will be able to do most of the tasks
        //the only one that I think elite teams won't all be able to do
        //is catch.
        m_speed = speed;
        m_pickup = true;
        m_shoot = true;
        m_lowGoal = true;
        m_pass = true;
        m_truss = true;
        m_catch = canCatch;
        
        rng = new Random();
        autoScore = 0;
    }

    @Override
    public void pickupBall() {
        if(completionCounter == 0) {
            completionCounter = rng.nextInt(20);
        }
        if(counter == completionCounter) {
            completionCounter = 0;
            counter = 0;
            m_state = robotState.SHOOTING;
        }
    }

    @Override
    public int scoreBall() {
        int score = 0;
        if(completionCounter == 0) {
            completionCounter = rng.nextInt(35);
        }
        if(m_state == robotState.SHOOTING) {
            if(counter == completionCounter) {
                score = 10;
                completionCounter = 0;
                counter = 0;
                m_state = robotState.IDLE;
                m_ball = null;
            }
        } else {
            if(counter == completionCounter) {
                score = 1;
                completionCounter = 0;
                counter = 0;
                m_state = robotState.IDLE;
                m_ball = null;
            }
        }
        return score;
    }

    @Override
    public void passBall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int trussShoot() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int canCatch() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doDefence() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void returnToStation() {
        if(completionCounter == 0) {
            completionCounter = rng.nextInt(35);
        }
        
        if(completionCounter == counter) {
            m_state = robotState.WAITING_FOR_BALL;
            completionCounter = 0;
            counter = 0;
        }
    }

    @Override
    public RobotType getType() {
        return type;
    }

    @Override
    public void passTeammates(basicRobot robot1, basicRobot robot2) {
        teammate1 = robot1;
        teammate2 = robot2;
    }

    @Override
    public robotState checkState() {
        return m_state;
    }

    @Override
    public void runAuto() {
        /**
         * Since it is assumed that elite robots can shoot at a high clip we are going to give them
         * and 80% chance to make a shot.  We are also going to assume that it is greater than 50/50 chance that
         * they are tracking.
         * It is also assumed that they will get the 5 points for mobility
         */
        
        if(rng.nextInt(5) != 2) {
            if(rng.nextInt(4) != 0) {
                autoScore = 25;
            } else {
                autoScore = 20;
            }
            m_state = robotState.GOING_TO_STATION;
        } else {
            autoScore = 5;
            m_state = robotState.GETTING_BALL;
        }
    }

    @Override
    public int getAutoScore() {
        return autoScore;
    }

    @Override
    public int run() {
        int score = 0;
        counter++;
        switch(m_state) {
            case SHOOTING:               
                score = scoreBall();
                break;
            case LOWGOAL:
                score = scoreBall();
                break;
            case GETTING_BALL:
                pickupBall();
                break;
            case GOING_TO_STATION:
                returnToStation();
                break;
            case TRUSS:
                trussShoot();
                break;
            case IDLE:
                
                break;
                
            case DEFENCE:
                
                break;
                               
        }
        
        return score;
    }

    @Override
    public void askToPickup(ball passedBall) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
