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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpCookie;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class JokeClient {
	
	private enum JokeCodes {
		JA, JB, JC, JD
	}
	
	private enum ProverbCodes {
		PA, PB, PC, PD
	}
	
	private static int serverPort = 4545;
	private static int adminServerPort = 4546;
	private static String serverName = "localhost" ; 
	
	private String lastJokeCode; // Code for last Joke that ran
	private String lastProverbCode;	// Code for last proverb that ran 
	private String lastMode;	// Code for server mode while last message ran - was the last message a joke or a proverb? 
	private String lastCode;	// Code for the last message that was ran
	
	private UUID uuid; // Unique id to store record of last received message
	private String currentCode;
	private HttpCookie httpCookie;
	
	public JokeClient() {
		this.uuid=UUID.randomUUID(); //Generates random UUID
		this.currentCode = JokeCodes.JA.toString();	// Start each client to receive the first joke
		this.httpCookie = new HttpCookie(uuid.toString(), currentCode);
	}
	
	private void connectToServer(String serverName, int serverPort) {
		Socket socket; // the main class we will use to create a server connection
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		
		try {
			socket = new Socket(serverName, serverPort);
			
			// Create input/output streams
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	// Read input from server
			toServer = new PrintStream(socket.getOutputStream());	// Print output to server 
			textFromServer = fromServer.readLine();
			
			if (this.httpCookie == null) {
		    	socket.close();
		    	throw new NullPointerException("Client httpCookie is null.");
		    	
		    }
			
			// Get uuid for this machine and the current string that needs to be run for it 
			String uuid = this.httpCookie.getName().toString();
			String currentString = this.httpCookie.getValue();
			
			// Concatenated String that represents a cookie to send to the server
			// Made up of cookie name and value 
			String cookieString = uuid + "&" + currentString;					
			toServer.println(cookieString); 
			toServer.flush();	// Actually send the string

			// Read all responses from server
			long length = 0;
			while ((textFromServer != null)) {
				if (textFromServer.isEmpty()) {
			        break;
			    }
				textFromServer = fromServer.readLine();
				if (textFromServer.contains("&")) {
					updateData(textFromServer);
				}
				
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
	
	// Return a copy of httpCookie 
	public HttpCookie getCookie() {
		if (this.httpCookie != null) {
			HttpCookie tempCookie = this.httpCookie;
			return tempCookie;
		}
		
		return null;
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
				socket.close();
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
	
	// Updating class data with info from server via cookie
	private void updateData(HttpCookie httpCookie) {
		if (httpCookie == null) {
			throw new NullPointerException("Cookie parameter is null.");
		}
		
		// Update class statement values 
		else if (httpCookie != null) {
			String Key = httpCookie.getName(); 
			
			// Make sure we are getting the right cookie for the right uuid
			if(Key.equals(this.uuid.toString())) {
				
	    		String value = httpCookie.getValue();
	    		if(!value.isEmpty()) {
	    			// Last statement ran was a joke
	    			if(value.charAt(0) == 'J') {
	    				lastMode = "JOKE";
	    				lastJokeCode = value;
	    			}
	    			// Last statement ran was a proverb
	    			else if(value.charAt(0) == 'P') {
	    				lastMode = "PROVERB";
	    				lastProverbCode = value;
	    			}
	    		} else {
	    			System.out.println("Cookie value is empty.");
	    			throw new IllegalArgumentException("Cookie value is empty.");
	    		}
			} else {
				System.out.println("Cookie contains the wrong UUID.");
				throw new IllegalArgumentException("Cookie contains the wrong UUID.");
			}
    	}
	}
	
	private void updateData(String cookieString) {
		if (cookieString == null) {
			throw new NullPointerException("String parameter is null.");
		}
		
		String[] cookieArray = cookieString.split("&");
		String cookieUUID = cookieArray[0];
		String cookieValue = cookieArray[1];
		
		if(cookieUUID.equals(this.httpCookie.getName())) {
			if(cookieValue.isEmpty()) {
				System.out.println("Cookie value is empty.");
    			throw new IllegalArgumentException("Cookie value is empty."); 
			}
			
			this.httpCookie.setValue(cookieValue);
			// Last statement ran was a joke
			if(cookieValue.charAt(0) == 'J') {
				lastMode = "JOKE";
				lastJokeCode = cookieValue;
				this.httpCookie.setValue(cookieValue);
			}
			// Last statement ran was a proverb
			else if(cookieValue.charAt(0) == 'P') {
				lastMode = "PROVERB";
				lastProverbCode = cookieValue;
				this.httpCookie.setValue(cookieValue);
			} 
		else {
			System.out.println("Cookie contains the wrong UUID.");
			throw new IllegalArgumentException("Cookie contains the wrong UUID.");
		}
		}
		
	}
	
	
	// Main job is to try to connect to an admin server 
	public static void main(String args[]) {
			
		String serverName;
		if (args.length < 1) serverName = "localhost";	// Connect to localhost by default
		else serverName = args[0];						// Else connect to the server name the user entered as an argument 
		System.out.println("Jordan Johnson's Joker Client\n");
		System.out.println("Using server: " + serverName + ", Port: " + serverPort);
		
		UUID uuid = UUID.randomUUID();	// Create uuid for client cookie 
		String lastJokeCode; // Code for last Joke that ran
		String lastProverbCode;	// Code for last proverb that ran 
		String lastMode;	// Code for server mode while last message ran - was the last message a joke or a proverb? 
		String lastCode;	// Code for the last message that was ran
		
		JokeClient jokeClient = new JokeClient();
		
		try {
			// Get user name 
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String userName;
			System.out.print("Enter your name please, (quit) to end: ");
			System.out.flush();
			userName = in.readLine();
			System.out.println("Welcome " + userName + "\n");
			
			do {
				if (userName.indexOf("quit") < 0)
					jokeClient.connectToServer(serverName, serverPort);
			// user has entered 'quit'
			} while (userName.indexOf("quit") < 0); 
				 System.out.println ("Cancelled by user request.");
		
		} catch (IOException e) {
			e.printStackTrace();
	    	
	    }
	}		 
	
}
