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
    private ball m_ball = null;

    private int autoScore, teleScore;

    private Random rng;
    private robotState m_state;
    private int counter = 0;
    private int completionCounter = 0;

    public midRobot() {
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
        teleScore = 0;
        rng = new Random();
    }

    @Override
    public void pickupBall(boolean defence) {
        if (completionCounter == 0) {
            completionCounter = rng.nextInt(10);
        }
        if (counter == completionCounter) {
            completionCounter = 0;
            counter = 0;
            m_state = robotState.SHOOTING;
        }
    }

    @Override
    public int scoreBall(boolean defence) {
        int score = 0;
        if (completionCounter == 0) {
            completionCounter = rng.nextInt(10);
        }
        if (m_shoot) {
            if (counter == completionCounter) {
                score = 10;
                completionCounter = 0;
                counter = 0;
                m_state = robotState.IDLE;
                m_ball = null;
            }
        } else {
            if (counter == completionCounter) {
                score = 1;
                completionCounter = 0;
                counter = 0;
                m_state = robotState.IDLE;
                m_ball = null;
            }
        }
        teleScore += score;
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
    public RobotType getType() {
        return type;
    }

    @Override
    public void returnToStation() {
        if (completionCounter == 0) {
            completionCounter = rng.nextInt(10);
            m_state = robotState.GOING_TO_STATION;
        }

        if (completionCounter == counter) {
            m_state = robotState.WAITING_FOR_BALL;
            completionCounter = 0;
            counter = 0;
        }
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
        if (m_shoot) {
            if (rng.nextInt(2) != 0 && rng.nextInt(2) != 0) {
                autoScore = 25;
                m_state = robotState.IDLE;
            } else if (rng.nextInt(2) != 0) {
                autoScore = 20;
                m_state = robotState.IDLE;
            } else {
                autoScore = 5;
                m_state = robotState.GETTING_BALL;
            }
        } else if (m_lowGoal) {
            if (rng.nextInt(3) != 0) {
                if (rng.nextInt(2) != 0) {
                    autoScore = 16;
                } else {
                    autoScore = 11;
                }
                m_state = robotState.IDLE;
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
        if (completionCounter != 0) {
            counter++;
        }
        switch (m_state) {
            case SHOOTING:
                score = scoreBall(false);
                break;
            case LOWGOAL:
                score = scoreBall(false);
                break;
            case GETTING_BALL:
                pickupBall(false);
                break;
            case GOING_TO_STATION:
                returnToStation();
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
        if (m_ball == null) {
            return false;
        } else {
            return true;
        }
    }

}
