/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim;

/**
 *
 * @author gaming
 */
public class RobotSimulator {
    public static void main(String args[]) {
        
        Simulator sim = new Simulator();
        
        sim.run(); //should run one instance for now.
        sim.printResults();
    }
}
