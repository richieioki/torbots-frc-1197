package helloworld;

import java.util.Scanner;
public class HelloWorld {

   
    public static void main(String[] args) {
        Scanner input = new Scanner (System.in);
        System.out.println("Enter your name please: ");
        String name = input.nextLine(); 
        
        System.out.println("So your name is:");
        System.out.println(name);
        
        String answer = input.nextLine();
        if (answer=="Yes") {
        System.out.println("lol");
        
    }
        else {
            System.out.println("I know your name now!");
        }
    }
    
    
}
