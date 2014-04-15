package robots;

import java.util.Random;
import objects.ball;

/**
 *
 * @author richi_000
 */
public class lowRobot implements basicRobot {
    private Speed m_speed;
    private boolean m_pickup, m_shoot, m_lowGoal, m_pass, m_truss, m_catch;
    private RobotType type;
    
    private basicRobot teammate1, teammate2;
    private ball m_ball = null;
    
    private int autoScore, teleScore;
    private Random rng;
    private robotState m_state;
    private int counter;
    private int completionCounter = 0;
    
    public lowRobot() {
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
        autoScore = 0;
        teleScore = 0;
    }

    @Override
    public void pickupBall(boolean defence) {
        if(completionCounter == 0) {
            completionCounter = rng.nextInt(10);
        }
        if(counter == completionCounter) {
            completionCounter = 0;
            counter = 0;
            m_state = robotState.SHOOTING;
        }
    }

    @Override
    public int scoreBall(boolean defence) {
        int score = 0;
        if(completionCounter == 0) {
            completionCounter = rng.nextInt(10);
        }
        if(m_shoot) {
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
        teleScore+= score;
        return score;
    }

    @Override
    public void passBall(boolean defence) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int trussShoot(boolean defence) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int canCatch(boolean defence) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doDefence() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void returnToStation() {
        if(completionCounter == 0) {
            completionCounter = rng.nextInt(10);
            m_state = robotState.GOING_TO_STATION;
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
         * Noob robots, some will shoot at a low clip
         * Some will be able to score in the low goal fairly well
         * None will be able to detect the hot goal
         * Some will not take the ball, and most will be able to get the mobility bonus
         */
        
        if(m_shoot) {
            if(rng.nextInt(3) == 0) {
                if(rng.nextInt(2) == 1) {
                    autoScore = 25;
                    m_state = robotState.IDLE;
                } else {
                    autoScore = 20;
                    m_state = robotState.IDLE;
                }
            } else {
                autoScore = 5;
                m_state = robotState.GETTING_BALL;
            }
        } else if(m_lowGoal) {
            if(rng.nextInt(3) != 0) {
                autoScore = 16;
                m_state = robotState.IDLE;
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
        if(completionCounter != 0) {
            counter++;
        }
        //System.out.println("***" + m_state);
        switch(m_state) {            
            case SHOOTING:               
                score = scoreBall(false);
                break;
            case LOWGOAL:
                score = scoreBall(false);
                break;
            case GETTING_BALL:
                pickupBall(false);
            case GOING_TO_STATION:
                //returnToStation();
                break;
            case TRUSS:
                trussShoot(false);
                break;
            case IDLE:
                completionCounter = 0;
                counter = 0;
                break;
                
            case DEFENCE:
                
                break;                              
        }
        
        return score;
    }

    @Override
    public void recieveBall(ball passedBall) {
        m_ball = passedBall;
        m_state = robotState.IDLE;
    }

    @Override
    public int getTeleScore() {
        return teleScore;
    }

    @Override
    public robotState getState() {
        return m_state;
    }

    @Override
    public boolean canPickup() {
        return m_pickup;
    }

    @Override
    public void setToIdle() {
        m_state = robotState.IDLE;
        completionCounter = 0;
        counter = 0;
    }

    @Override
    public boolean hasBall() {
        if(m_ball == null) {
            return false;
        } else {
            return true;
        }
    }
}
