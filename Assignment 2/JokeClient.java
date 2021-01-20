/*--------------------------------------------------------

1. Jordan Johnson / 1/17/2021

2. Java version used, if not the official version for the class:

e.g. build build 11.0.4+11

3. Precise command-line compilation examples / instructions:

e.g.:

javac JokeServer.java
javac JokeClient.java



4. Precise examples / instructions to run this program:

e.g.:

In separate shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For exmaple, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

e.g.:

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:

e.g.:

I faked the random number generator. I have a bug that comes up once every
ten runs or so. If the server hangs, just kill it and restart it. You do not
have to restart the clients, they will find the server again when a request
is made.

----------------------------------------------------------*/

package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.HttpCookie;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class JokeClient {
	
	private static int serverPort = 4545;
	private static int adminServerPort = 4546;
	private static String serverName = "localhost" ; 
	
	private static UUID uuid; // Unique id to store record of last received message
	
	private String lastJokeCode; // Code for last Joke that ran
	private String lastProverbCode;	// Code for last proverb that ran 
	private String lastMode;	// Code for server mode while last message ran - was the last message a joke or a proverb? 
	private static String lastCode;	// Code for the last message that was ran
	
	public JokeClient() {
		this.uuid=UUID.randomUUID(); //Generates random UUID    
	}
	
	private static void connectToAdminServer(String serverName) {
		Socket socket; // the main class we will use to create a server connection
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		// For sending and receiving objects via a socket
		ObjectOutputStream objectToServer; 
	    ObjectInputStream objectFromServer;
		
		try {
			/* Open connection to given server port */
			socket = new Socket(serverName, adminServerPort);
			
			// Create I/O streams to read data to/from the socket
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	// Read input from server
			toServer = new PrintStream(socket.getOutputStream());	// Print output to server 
			textFromServer = fromServer.readLine();
			
			objectToServer = new ObjectOutputStream(socket.getOutputStream());
		    objectFromServer = new ObjectInputStream(socket.getInputStream());
		    HttpCookie httpCookie = new HttpCookie(uuid.toString(), lastCode);
		    
		    // Write object to server
		    objectToServer.writeObject(httpCookie);
			toServer.println("Connected to " + serverName + "."); 
			toServer.flush();
			
			// Read all responses from server
			long length = 0;
			while ((textFromServer != null)) {
				if (textFromServer.isEmpty()) {
			        break;
			    }
				textFromServer = fromServer.readLine();
				System.out.println(textFromServer);
		        length += textFromServer.length();
			}
			socket.close();	//Close socket 
			
		} catch (IOException x){
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
	
	private static void connectToAdminServer(String serverName, HttpCookie httpCookie) {
		Socket socket; // the main class we will use to create a server connection
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		// For sending and receiving objects via a socket
		ObjectOutputStream objectToServer; 
	    ObjectInputStream objectFromServer;
		
		try {
			/* Open connection to given server port */
			socket = new Socket(serverName, adminServerPort);
			
			// Create I/O streams to read data to/from the socket
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	// Read input from server
			toServer = new PrintStream(socket.getOutputStream());	// Print output to server 
			textFromServer = fromServer.readLine();
			
			objectToServer = new ObjectOutputStream(socket.getOutputStream());
		    objectFromServer = new ObjectInputStream(socket.getInputStream());
		    
		    // Write object to server
		    objectToServer.writeObject(httpCookie);
			toServer.println("Connected to " + serverName + "."); 
			toServer.flush();
			
			// Read all responses from server
			long length = 0;
			while ((textFromServer != null)) {
				if (textFromServer.isEmpty()) {
			        break;
			    }
				textFromServer = fromServer.readLine();
				System.out.println(textFromServer);
		        length += textFromServer.length();
			}
			socket.close();	//Close socket 
			
		} catch (IOException x){
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
	
	// Gets next phrase from a server
	private static void getNextPhrase(String serverName) {
		// Connect to Joke Server 
	    while(true) {
	    	getRemoteAddress(serverName, serverName);
	    }
	}
	
	// Gets the address of a given server 
	private static void getRemoteAddress (String name, String serverName) {
		Socket socket; // the main class we will use to create a server connection
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		
		try {
			/* Open connection to given server port */
			socket = new Socket(serverName, serverPort);
			
			// Create I/O streams to read data to/from the socket
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	// Read input from server
			toServer = new PrintStream(socket.getOutputStream());	// Print output to server 
			textFromServer = fromServer.readLine();
			
			toServer.println(name); 
			toServer.flush();
			
			// Read all responses from server
			long length = 0;
			while ((textFromServer != null)) {
				if (textFromServer.isEmpty()) {
			        break;
			    }
				textFromServer = fromServer.readLine();
				System.out.println(textFromServer);
		        length += textFromServer.length();
			}
			socket.close();	//Close socket 
			
		} catch (IOException x){
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}			
	
	// Gets the address of a given server 
	private static void getRemoteAddress (String name, String serverName, HttpCookie cookie) {
		Socket socket; // the main class we will use to create a server connection
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		
		try {
			/* Open connection to given server port */
			socket = new Socket(serverName, serverPort);
			
			// Create I/O streams to read data to/from the socket
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	// Read input from server
			toServer = new PrintStream(socket.getOutputStream());	// Print output to server 
			textFromServer = fromServer.readLine();
			
			// Get code of last printed string
			String code = cookie.getValue();
			if(code.isEmpty()) {
				throw new IllegalArgumentException("Empty code in cookie value");
			}
			
			
			toServer.println(name); 
			
			toServer.flush();
			
			// Read all responses from server
			long length = 0;
			while ((textFromServer != null)) {
				if (textFromServer.isEmpty()) {
			        break;
			    }
				textFromServer = fromServer.readLine();
				System.out.println(textFromServer);
		        length += textFromServer.length();
			}
			socket.close();	//Close socket 
			
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
		
	public static void main(String args[]) {
	    // Connect to Joke Server 
	    while(true) {
	    	// getRemoteAddress(serverName, serverName);
	    	
	    	// Create cookie to send to server
	    	HttpCookie httpCookie = new HttpCookie(uuid.toString(), lastCode);
	    	connectToAdminServer(serverName, httpCookie); 
	    	
	    	
	    }
	}		 
	
}
