/*

Elliott: Example of setting a class variable in one method, and reading it in another. 2020-01-20
You can use this same technique to set the class variable in on thread and read it in another.

Hint for setting the server mode in JokeServer.

OUTPUT:

> java SimpleCommunication


HELLO FROM MAIN.

Hello from AMethod.

In AMethod the string is: This is the ORIGINAL value of the class variable.

Now setting a new value to the class variable, containing AAAA...

Hello from BMethod.

In Bmethod the string is now NEW value of class variable set in AMethod... (AAAA).

So we set the value in AMethod, and read the new value in BMethod

We used a class variable, and nothing was static.

GOODBYE FROM MAIN.

*/
package main;

import java.io.*;  // Get the Input Output libraries

public class SimpleCommunication {

	// Our class variable:
    String classVariableString = "This is the ORIGINAL value of the class variable.";
  
    public void AMethod () {    // Method definition
	System.out.println("Hello from AMethod.\n");
	System.out.println("In AMethod the string is: " + classVariableString);
	System.out.println("\nNow setting a new value to the class variable, containing AAAA...\n");
    classVariableString = "NEW value of class variable set in AMethod... (AAAA).\n";
  }
  
  public void BMethod  () {    // Method definition
    System.out.println("Hello from BMethod.\n");
    System.out.println("In Bmethod the string is now " + classVariableString);
    System.out.println("So we set the value in AMethod, and read the new value in BMethod\n");
    System.out.println("We used a class variable, and nothing was static.\n");
  }
  
  public static void main(String a[]) throws IOException {
    
    SimpleCommunication AnInstance = new SimpleCommunication(); // Create the Object
    System.out.println("\nHELLO FROM MAIN.\n");
    AnInstance.AMethod();
    AnInstance.BMethod();
    System.out.println("GOODBYE FROM MAIN.\n");
    
  }

}
