/*--------------------------------------------------------

1. Jordan Johnson / 1/17/2021

2. Java version used, if not the official version for the class:

e.g. build build 11.0.4+11

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
import java.util.Random;

import main.JokeServer;

// This class does the main work for all connecting threads 
public class JokeClientAdmin extends Thread {
	
	private JokeServer jokeServer;
	private Socket socket; // Main class for creating a server connection
	
	// Input/Output streams to write data to/from socket 
	private PrintStream printStream; 			// Print output to server 
	private BufferedReader bufferedReader;		
	
	private static HttpCookie httpCookie;
	
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
	
	// Start on a random joke and print the rest 
	public String getRandomJoke() {
		if(jokeServer != null) {
			int random = new Random().nextInt(5);
			
			for(int i = random; i < 4; i++) {
				String joke = jokeServer.getJoke(i);
				printStream.println(joke);
				printStream.flush();
			}
			printStream.println("\n JOKE CYCLE COMPLETED \n");
			printStream.flush();
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
		return "";
	}	
	
	// Start on a random proverb and print the rest 
	public String getRandomProverb() {
		if(jokeServer != null) {
			int random = new Random().nextInt(5);
			
			for(int i = random; i < 4; i++) {
				String proverb = jokeServer.getProverb(i);
				printStream.println(proverb);
				printStream.flush();
			}
			printStream.println("\n PROVERB CYCLE COMPLETED \n");
			printStream.flush();
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
		return "";
	}
	
	public String getServerState() {
		
		if (jokeServer != null) {
			
			String serverState = jokeServer.getState(); 
			
			if(!serverState.isEmpty()) {
				System.out.println("The server is in " + serverState + " mode.");
				printStream.println("The server is in " + serverState + " mode.");
				printStream.flush();
				return serverState;
			}
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
		
		return "";
	}
	
	public void printAllJokes() {
		if(jokeServer != null) {
			for(int i = 1; i <= 4; i++) {
				String joke = jokeServer.getJoke(i);
				printStream.println(joke);
				printStream.flush();
			}
			printStream.println("\n JOKE CYCLE COMPLETED \n");
			printStream.flush();
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
	}
	
	public void printAllProverbs() {
		if(jokeServer != null) {
			for(int i = 1; i <= 4; i++) {
				String proverb = jokeServer.getProverb(i);
				printStream.println(proverb);
				printStream.flush();
			}
			printStream.println("\n PROVERB CYCLE COMPLETED \n"); 
			printStream.flush();
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
	}	
	
	// Main logic in class 
	public void run(){
		
		// Some instances of JokeClientAdmin are just worker classes and aren't initialized with a server 
		if (jokeServer != null) {
			String state = getServerState();
			
			// send cookie to joke server 
			if(httpCookie != null) {
				String uuid = httpCookie.getName();
				String lastString = httpCookie.getValue();
			}
			
			
			
			
			
		
		// This is just a worker thread 
		} else if (jokeServer == null) {
			
		}
		
		
		/* 
		while (true) {
			
			for (int i = 1; i <= 4; i++) {
				
				
			}
			
			if (state.equals("PROVERB")) {
				for (int i = 0; i < 3; i++) {
					printAllProverbs();
				}
				for (int i = 0; i < 3; i++) {
					printAllJokes();
				}
			} else if (state.equals("JOKE")) {
				for (int i = 0; i < 3; i++) {
					printAllJokes();
				}
				for (int i = 0; i < 3; i++) {
					printAllProverbs();
				}
			}
		}
		*/		
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
	
	// Main job is to listen for connections
	public static void main(String[] args) {
		int queuelength = 6; 
		int port = 4546;
		String ServerName = "JokeClientAdmin";
		Socket socket;		
		
		ServerSocket serverSocket;
		
		// ObjectOutputStream objectToServer; 
	    // ObjectInputStream objectFromServer;
		
	    // Server starts listening for connections
		try {
			serverSocket = new ServerSocket(port, queuelength);
			
			System.out.println
				("Jordan Johnson's Joke Client Admin starting up, listening at port 4546.\n");

			while (true) {
			
				socket = serverSocket.accept(); // Accepts client connection  
				
				// objectToServer = new ObjectOutputStream(socket.getOutputStream());
			    // objectFromServer = new ObjectInputStream(socket.getInputStream());
			    
			    // read in cookie from connection
			    // httpCookie = (HttpCookie) objectFromServer.readObject();
			    
			    System.out.println("Http Cookie name: " + httpCookie.getName());
			    System.out.println("Http Cookie value: " + httpCookie.getValue());
			    			    
				new Worker(socket).start(); 	// Client admin class handles this thread's work using  alternative constructor.
														// Connects to Joke Server and sends cookie 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} // catch (ClassNotFoundException e) {
			// e.printStackTrace();
		// }
	}
}