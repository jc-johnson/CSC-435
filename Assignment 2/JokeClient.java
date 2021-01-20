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
	
	private static String lastJokeCode; // Code for last Joke that ran
	private static String lastProverbCode;	// Code for last proverb that ran 
	private static String lastMode;	// Code for server mode while last message ran - was the last message a joke or a proverb? 
	private static String lastCode;	// Code for the last message that was ran
	
	private HttpCookie httpCookie;
	
	public JokeClient() {
		this.uuid=UUID.randomUUID(); //Generates random UUID
		this.lastCode="";
		this.httpCookie = new HttpCookie(uuid.toString(), lastCode);
	}
	
	//Connect to Admin Server and send a cookie. Get updated cookie back and return. 
	// Updated cookie has last run joke/proverb. 
	private static HttpCookie connectToAdmin(String serverName, int portNumber, HttpCookie httpCookie) {
		Socket socket;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		
		// I/O objects for sending/receiving objects 
		ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;

		try{
			// Open connection to server port
			socket = new Socket(serverName, portNumber);

			// Create filter I/O streams for the socket:
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toServer = new PrintStream(socket.getOutputStream());
			textFromServer = fromServer.readLine();
			
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			
			// Get updated cookie object from server
			HttpCookie inputHttpCookie = (HttpCookie) objectInputStream.readObject();
			if (inputHttpCookie == null) {
				throw new NullPointerException("Cookie received from the server is null.");
			}
			String cookieName = inputHttpCookie.getName();
			String cookieValue = inputHttpCookie.getValue();
			System.out.println("Cookie Name: " + cookieName);
			System.out.println("Cookie Value: " + cookieValue);
			
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
			updateData(inputHttpCookie);
			socket.close();
			} catch (IOException x) {
			System.out.println ("Socket error.");
			x.printStackTrace ();
		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return null;
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
			// Trying to connect to admin server
			socket = new Socket(serverName, adminServerPort);
			
			// Create I/O streams to read data to/from the socket
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	// Read input from server
			toServer = new PrintStream(socket.getOutputStream());	// Print output to server 
			textFromServer = fromServer.readLine();
			
			objectToServer = new ObjectOutputStream(socket.getOutputStream());
		    objectFromServer = new ObjectInputStream(socket.getInputStream());
		    
		    // Write object cookie to server
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
	private static void updateData(HttpCookie httpCookie) {
		if (httpCookie == null) {
			throw new NullPointerException("Cookie parameter is null.");
		}
		
		// Update class statement values 
		else if (httpCookie != null) {
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
    		}
    	}
	}
	// Main job is to try to connect to an admin server 
	public static void main(String args[]) {
			
		String serverName;
		if (args.length < 1) serverName = "localhost";
		else serverName = args[0];
		System.out.println("Jordan Johnson's Joker Client\n");
		System.out.println("Using server: " + serverName + ", Port: 4546");
		
		// Get user input
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String userName;
		System.out.print("Enter your name please, (quit) to end: ");
		System.out.flush();
		
		UUID uuid = UUID.randomUUID();
		String lastJokeCode; // Code for last Joke that ran
		String lastProverbCode;	// Code for last proverb that ran 
		String lastMode;	// Code for server mode while last message ran - was the last message a joke or a proverb? 
		String lastCode;	// Code for the last message that was ran
		
		// Initialize lastCode as empty for first run  
		HttpCookie httpCookie = new HttpCookie(uuid.toString(), "");
		
		try {			
			userName = in.readLine();
			System.out.println("Welcome " + userName + "\n");
			
			do {
				// getRemoteAddress(serverName, serverName);
				// Create cookie to send to admin server
		    	HttpCookie tempCookie = connectToAdmin(serverName, 4546, httpCookie);
		    	// Update class statement values 
		    	if (tempCookie != null) {
		    		String value = tempCookie.getValue();
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
		    		}
		    	}

			    
				if (userName.indexOf("quit") < 0)
					getRemoteAddress(userName, serverName);
			// user has not entered 'quit'
			} while (userName.indexOf("quit") < 0); 
				 System.out.println ("Cancelled by user request.");
		
		} catch (IOException e) {
			e.printStackTrace();
	    	
	    }
	}		 
	
}
