package main;

import java.io.*;  

public class SimpleCommunication2 {
	
	private enum ServerState {
		JOKE, PROVERB
	}
	
	private ServerState state = ServerState.JOKE;
  
    public void AMethod () {    
    	System.out.println("Hello from AMethod.\n");
    	System.out.println("The current server state is: " + state);
    	System.out.println("\nNow setting a new value to the class variable, containing AAAA...\n");
    	state = ServerState.PROVERB;
    }
  
    public void BMethod  () {    
    	System.out.println("Hello from BMethod.\n");
    	System.out.println("In Bmethod the string is now " + state);
    	System.out.println("So we set the value in AMethod, and read the new value in BMethod\n");
    	System.out.println("We used a class variable, and nothing was static.\n");
    }
  
    public static void main(String a[]) throws IOException {
    	SimpleCommunication2 AnInstance = new SimpleCommunication2(); 
    	System.out.println("\nHELLO FROM MAIN.\n");
    	AnInstance.AMethod();
    	AnInstance.BMethod();
    	System.out.println("GOODBYE FROM MAIN.\n");    
    }

}
