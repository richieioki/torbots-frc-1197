/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package robots;

import objects.ball;

public interface basicRobot {
    //list of all the functions that robots can offensively do
    
    public void pickupBall();
    
    public int scoreBall();
        
    public void passBall();
    
    public int trussShoot();
    
    public int canCatch();
    
    public void returnToStation();
    
    //defence function will add time to offensive robots
    public void doDefence();
    
    public RobotType getType();  
    
    public void askToPickup(ball passedBall);
    
    //passing teammates so that each robot can determine what the features of their alliance partners are
    public void passTeammates(basicRobot robot1, basicRobot robot2);
    
    public robotState checkState();
    
    public int run();
    
    public void runAuto();
    
    public int getAutoScore();
}