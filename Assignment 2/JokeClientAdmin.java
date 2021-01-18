/*--------------------------------------------------------

1. Jordan Johnson / 1/17/2021

2. Java version used, if not the official version for the class:

e.g. build 1.5.0_06-b05

3. Precise command-line compilation examples / instructions:

e.g.:

> javac JokeServer.java


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

import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.Map;

// This class does the main work for all connecting threads 
public class JokeClientAdmin extends Thread {
	
	private JokeServer jokeServer;
	private Socket socket; // Main class for creating a server connection
	
	// Input/Output streams to write data to/from socket 
	private PrintStream printStream; 			// Print output to server 
	private BufferedReader bufferedReader;		
	
	public JokeClientAdmin (Socket socket) {
		
		this.socket = socket;
	}
	
	public JokeClientAdmin (JokeServer jokeServer, Socket socket) {
		
		if(jokeServer != null && socket != null) {
			try {
				this.jokeServer = jokeServer;
				this.socket = socket;
				// Input/Output streams to write data to/from socket
				this.printStream = new PrintStream(socket.getOutputStream());
				this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(jokeServer == null) {
			throw new NullPointerException("Joke Server is null.");
		} else if (socket == null) {
			throw new NullPointerException("Socket is null.");
		}
	}
	
	public String getJoke(int index) {
		if(jokeServer != null) {
			return jokeServer.getJoke(index);
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
	}
	
	public String getProverb(int index) {
		if(jokeServer != null) {
			return jokeServer.getProverb(index);
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
	}
	
	public String getServerState() {
		
		if (jokeServer != null) {
			
			String serverState = jokeServer.getState(); 
			printStream.println("The server is in " + serverState + " mode.");
			printStream.flush();
			
			if(!serverState.isEmpty()) {
				System.out.println("The server is in " + serverState + " mode.");
				return serverState;
			}
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
		
		return "";
	}
	
	public void printAllJokes() {
		if(jokeServer != null) {
			for(int i = 1; i < 4; i++) {
				String joke = jokeServer.getJoke(i);
				printStream.println(joke);
				printStream.flush();
			}
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
	}
	
	public void printAllProverbs() {
		if(jokeServer != null) {
			for(int i = 1; i < 5; i++) {
				String proverb = jokeServer.getProverb(i);
				printStream.println(proverb);
				printStream.flush();
			}
			// printStream.flush();
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
	}	
	
	// Main logic in class 
	public void run(){
		
		String state = getServerState();
		
		if (state.equals("PROVERB")) {
			printAllProverbs();
		} else if (state.equals("JOKE")) {
			printAllJokes();
		}	
	}
	 
	// Convert byte to string 
	static String toText(byte ip[]) { 
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ip.length; ++i) {
			if (i > 0) result.append(".");
			result.append((0xff &ip[i]));
		}
		return result.toString();
	}
}