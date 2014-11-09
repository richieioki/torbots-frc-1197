/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package maze3;

/**
 *
 * @author Masaki
 */
public class Maze3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PathFinder pf = new PathFinder();
        pf.buildMaze();
        pf.findPath();
        System.out.println("Goal!");
    }
    
}
