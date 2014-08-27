/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package maze3;
import java.util.Scanner;
public class PathFinder {

    private int[][] maze;
    
    public void buildMaze() {
        //User enters at 0,0
        //User exists when 4,4 is reached
        maze = new int[5][5];
        
        for (int[] maze1 : maze) {
            for (int y = 0; y < maze.length; y++) {
                maze1[y] = 1;
            }
        }
        
        //creating walls
        maze[1][0] = -1;
        maze[1][1] = -1;
        maze[1][3] = -1;
        maze[1][4] = -1;
        maze[2][1] = -1;
        maze[2][3] = -1;
        maze[4][0] = -1;
        maze[4][1] = -1;
        maze[4][2] = -1;
        maze[4][3] = -1;
        
    }
    public boolean movement(int x, int y){
        if(x < 0 || y < 0 || x >= 5 || y >= 5 || maze[x][y] == -1)
            return false;
        else
            return true;
    }
    
    @SuppressWarnings("null")
    public void findPath() {
        int x = 0,y = 0;
        Scanner mov = new Scanner(System.in);
        String z = null;
        @SuppressWarnings("UnusedAssignment")
        String t = null;
        while(x != 4 || y != 4){

            System.out.println("n = north s = south w = west e = east: ");
            System.out.println("                                       ");
            System.out.println("Goal = 4,4");
            System.out.println("                                       ");
            System.out.println(" S  | 0,1 | 0,2 | 0,3 | 0,4");
            System.out.println("---------------------------");
            System.out.println("[]  | []  | 1,2 | []  | [] ");
            System.out.println("---------------------------");
            System.out.println("2,0 | []  | 2,2 | []  | 2,4"); 
            System.out.println("---------------------------");      
            System.out.println("3,0 | 3,1 | 3,2 | 3,3 | 3,4");
            System.out.println("---------------------------");
            System.out.println("[]  | []  | []  | []  | 4,4");
            if(mov.hasNextLine()){
                
                z = mov.nextLine();
                
            }
            switch(z){
                case "n":
                    if(movement(x-1,y)){
                        System.out.println("Valid move");
                        x--;
                        break;
                    }else{
                        System.out.println("Not a valid move");
                        break;
                    }
                case "s":
                    if(movement(x+1,y)){
                        System.out.println("Valid move");
                        x++;
                        break;
                    }else{
                        System.out.println("Not a valid move");
                        break;
                    }
                case "e":
                    if(movement(x,y+1)){
                        System.out.println("Valid move");
                        y++;
                        break;
                    }else{
                        System.out.println("Not a valid move");
                        break;
                    }
                case "w":
                    if(movement(x,y-1)){
                        System.out.println("Valid move");
                        y--;
                        break;
                    }else{
                        System.out.println("Not a valid move");
                        break;
                    }
                    
                 
                }
                     t = "(" + x + "," + y + ")";      
                     System.out.println(t);
                   
            
        }
    }
    
}
