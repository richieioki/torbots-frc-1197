/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package robotsim;




/**
 *
 * @author richi_000
 */
public class RobotSim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Torbot's robot sim by Richie Ioki");
        Simulator sim = new Simulator();
        //initialize field
            //generate 6 robots and assign teams
        sim.setupField();
        //run simulation
            //run time'd simulation
            //print results
            //print to csv file as well as to a log file to log events.
        sim.runSimulation();
    }
    
}
