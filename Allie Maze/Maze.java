/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package maze;

/**
 *
 * @author Rebecca
 */
public class Maze {

    public static void main(String[] args) {
        PathFinder pf = new PathFinder();
        pf.buildMaze();
        pf.findPath();
        String path = pf.getPath();
        System.out.println(path);
    }   
}

