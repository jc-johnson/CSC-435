package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class JokeClient {
	
	private static int serverPort = 4545;

	public static void main (String args[]) {
		
		// Get user name 
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter your name");
	    String userName = scanner.nextLine(); 
	    System.out.println("You entered: " + userName);  		
	    
	    String serverName = "localhost";	    
	    
	    // Connect to Joke Server 
	    while(true) {
	    	getRemoteAddress(userName, serverName);
	    }
	}		 
	
	private static void connectToServer() {
		
	}
	
	// Gets the address of a given server 
	private static void getRemoteAddress (String name, String serverName) {
		
		Socket sock; // the main class we will use to create a server connection
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		
		try {
			/* Open connection to given server port */
			sock = new Socket(serverName, serverPort);
			
			// Create I/O streams to read data to/from the socket
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));	// Read input from server
			toServer = new PrintStream(sock.getOutputStream());	// Print output to server 
			
			toServer.println(name); 
			toServer.flush();
			
			// Read 2-3 lines of response from server and prints the response 
			for (int i = 1; i <= 3; i++) {
				textFromServer = fromServer.readLine();
				if (textFromServer != null) System.out.println(textFromServer);
			}
			sock.close();	//Close socket 
			
		} catch (IOException x){
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}			
	
	// Converts byte to string 
	private static String toText (byte ip[]) { 
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ip.length; ++i) {
			if (i > 0) result.append(".");
				result.append(0xff & ip[i]);
		}
		return result.toString();
	}	
		
	
	
}
