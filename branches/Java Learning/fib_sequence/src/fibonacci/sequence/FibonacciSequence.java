package fibonacci.sequence;
public class FibonacciSequence {
private static int[] list;

@SuppressWarnings("empty-statement")
public static void main(String[] args) {
        
    int x,y,z;
    x= 0;
    y= 1;


    z= x+y;
    
    System.out.print(x+y);
    
    
        int counter = 2;
        
        while(true) {
            
            z= x+y;
            
            if (z< 0) {
                System.exit(0);
                
            }
            
            System.out.println(counter + " : " + z);
            
            x=y;
            y=z;
            counter++;
            
        }
    }
            
            
            
            
            
            
            
            
       
            
            
            
            
            
            
            
            
            
         }



 
                   
            
        
        
                
 


