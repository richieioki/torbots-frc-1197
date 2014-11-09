
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
        
        while(!(x==4 && y==4)) {
            
       if (maze[x+1][y] == -1){
          y++;
          
       }
          else if(maze[x+1][y] == 1){
           x++;
               }
      path = path + " (" + x + "," + y + "),";  
   
        
        }}
    public String getPath() {
       return path;
    }
    }
  