package robots;

import objects.ball;

public interface basicRobot {
    //list of all the functions that robots can offensively do
    
    public void pickupBall(boolean defence);
    
    public int scoreBall(boolean defence);
        
    public void passBall(boolean defence);
    
    public int trussShoot(boolean defence);
    
    public int canCatch(boolean defence);
    
    public void returnToStation();
    
    //defence function will add time to offensive robots
    public void doDefence();
    
    public RobotType getType();  
    
    public void recieveBall(ball passedBall);
    
    //passing teammates so that each robot can determine what the features of their alliance partners are
    public void passTeammates(basicRobot robot1, basicRobot robot2);
    
    public robotState checkState();
    
    public int run();
    
    public void runAuto();
    
    public int getAutoScore();
    
    public int getTeleScore();
    
    public robotState getState();
    
    public boolean canPickup();
    
    public void setToIdle();
    
    public boolean hasBall();
}