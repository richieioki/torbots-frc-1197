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
public class noobRobot implements basicRobot {
    private Speed m_speed;
    private boolean m_pickup, m_shoot, m_lowGoal, m_pass, m_truss, m_catch;
    private RobotType type;
    
    private basicRobot teammate1, teammate2;
    
    private int autoScore;
    private Random rng;
    private robotState m_state;
    private int counter;
    
    public noobRobot() {
        type = RobotType.Mid;
    }
    
    public void initRobot(Speed speed, boolean canPickup, boolean canShoot, boolean canLowGoal, boolean canPass, boolean canTruss) {
        //most noobteams will see the widest variance 
        //I am assuming that they will not try to catch
        m_speed = speed;
        m_pickup = canPickup;
        m_shoot = canShoot;
        m_lowGoal = canLowGoal;
        m_pass = canPass;
        m_truss = canTruss;
        m_catch = false;
        
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
    public void returnToStation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
         * Noob robots, some will shoot at a low clip
         * Some will be able to score in the low goal fairly well
         * None will be able to detect the hot goal
         * Some will not take the ball, and most will be able to get the mobility bonus
         */
        
        if(m_shoot) {
            if(rng.nextInt(3) == 0) {
                if(rng.nextInt(2) == 1) {
                    autoScore = 25;
                    m_state = robotState.GOING_TO_STATION;
                } else {
                    autoScore = 20;
                    m_state = robotState.GOING_TO_STATION;
                }
            } else {
                autoScore = 5;
                m_state = robotState.GETTING_BALL;
            }
        } else if(m_lowGoal) {
            if(rng.nextInt(3) != 0) {
                autoScore = 16;
                m_state = robotState.GOING_TO_STATION;
            } else {
                autoScore = 5;
                m_state = robotState.GETTING_BALL;
            }
        } else {
            //these robots will not even take a ball
            m_state = robotState.IDLE;
            if(rng.nextInt(6) != 0) {
                autoScore = 5;
            } else {
                autoScore = 0; //some small percentage of new robots will not get the mobility points
            }
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
