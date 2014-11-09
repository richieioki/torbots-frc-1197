package maze;

public class PathFinder {

    private int[][] maze;
    private String path = "The Path : ";
    
    public void buildMaze() {
        //User enters at 0,0
        //User exists when 4,4 is reached
        maze = new int[5][5];
        
        //remember 1 = open and -1 = wall
        for(int i = 0; i < maze.length;i++) {
            for(int j = 0;j < maze.length;j++) {
                maze[i][j] = 1;
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

    public void findPath() {
        int x = 0,y = 0;
        path = path + " (0,0)";
        int left, right, up , down;
        while(!(x == 4 && y == 4)) {
            //PUT YOUR CODE HERE
            
            //Get the values around you
            left = 0; right = 0; up = 0; down = 0; //reset variables to zero
            
            if((x - 1) > 0) { //check left
                left = maze[x-1][y];
            }
            if((x + 1) < maze.length) { //check right
                right = maze[x+1][y];
            }
            if((y - 1) > 0) { //up
                up = maze[x][y-1];
            }
            if((y + 1) < maze.length) { //down
                down = maze[x][y+1];
            }
            
            //look down and to the right first because those are the direcions most likely to get us to the goal.
            if(right == 1) {
                System.out.println("moving right");
                x = x+1;
            } else if(down == 1) {
                System.out.println("moving down");
                y = y+1;
            } else if(left == 1) {
                System.out.println("moving left");
                x = x-1;
            } else if(up == 1) {
                System.out.println("moving up");
                y = y-1;
            } else {
                System.err.println("Messed up found nothing");
                return;
            }
            
            //store location and print out value
            path = path + " (" + x + ", " + y+")"; 
            System.out.println("Current location is " + x + " , " + y);
        }
    }

    public String getPath() {
        return path;
    }
    
}
