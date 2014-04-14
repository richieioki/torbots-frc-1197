/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package robots;

import java.util.Random;
import objects.ball;

/**
 *
 * @author richi_000
 */
public class midRobot implements basicRobot {
    private Speed m_speed;
    private boolean m_pickup, m_shoot, m_lowGoal, m_pass, m_truss, m_catch;
    private RobotType type;
    
    private basicRobot teammate1, teammate2;
    
    private int autoScore;
    
    private Random rng;
    private robotState m_state;
    private int counter;
    
    public midRobot(){
        type = RobotType.Mid;
    }
    
    public void initRobot(Speed speed, boolean canLowGoal, boolean canPass) {
        //Most mid tier teams will be able to shoot
        //get the ball somehow
        //and of course put it over the truss
        //we will have to determine lowgoal score, pass, and speed
        m_speed = speed;
        m_pickup = true;
        m_shoot = true;
        m_lowGoal = canLowGoal;
        m_pass = canPass;
        m_truss = true;
        m_catch = false;
        
        autoScore = 0;
        rng = new Random();
    }

    @Override
    public void pickupBall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int scoreBall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public RobotType getType() {
        return type;
    }
     
    @Override
    public void returnToStation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        if(m_shoot) {
            if(rng.nextInt(2) != 0 && rng.nextInt(2) != 0) {
                autoScore = 25;
                m_state = robotState.GOING_TO_STATION;
            } else if(rng.nextInt(2) != 0) {
                autoScore = 20;
                m_state = robotState.GOING_TO_STATION;
            } else {
                autoScore = 5;
                m_state = robotState.GETTING_BALL;
            }
        } else if(m_lowGoal) {
            if(rng.nextInt(3) != 0) {
                if(rng.nextInt(2) != 0) {
                    autoScore = 16;
                } else {
                    autoScore = 11;
                }
                m_state = robotState.GOING_TO_STATION;
            } else {
                autoScore = 5;
                m_state = robotState.GETTING_BALL;
            }
        } else {
            autoScore = 5; //I am assuming that all mid tier teams will be able to get the 5 mobility points
            m_state = robotState.IDLE;
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
