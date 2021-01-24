package main;

import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;


public class Worker extends Thread {
	
	private NavigableMap<String, String> jokeSymbols = new TreeMap<>() {{
	    put("JA", "How does a cucumber become a pickle? \n \t\t It goes through a jarring experience.");
	    put("JB", "What's brown and sticky? \n \t A stick.");
	    put("JC", "What did one volcano say to the other? \n \t I lava you.");
	    put("JD", "What do you call two birds in love? \n \t Tweethearts.");
	}};

	private NavigableMap<String, String> proverbSymbols = new TreeMap<>() {{
	    put("JA", "How does a cucumber become a pickle? \n \t\t It goes through a jarring experience.");
	    put("JB", "What's brown and sticky? \n \t A stick.");
	    put("JC", "What did one volcano say to the other? \n \t I lava you.");
	    put("JD", "What do you call two birds in love? \n \t Tweethearts.");
	}};
	
	private enum ServerState {
		JOKE, PROVERB
	}

	private Socket socket; // Main class for creating a server connection
	private String serverMode;
	
	String printString;
	
	public Worker (Socket socket) {
		this.socket = socket;
	}
		
	// Includes server mode at time of connection 
	public Worker(Socket socket, String serverMode) {
		this.socket = socket;
		this.serverMode = serverMode;
	}
	
	// Returns the code of the next joke in the series 
	public String getNextJokeCode(String index) {
		
		Map.Entry<String, String> next = jokeSymbols.higherEntry(index);
		
		// Return the first Joke if current string is the last joke 
		if (next == null) {
			next = jokeSymbols.firstEntry();
			return next.getKey();	
		}
		
		return next.getKey();
	}
	
	// returns next proverb code in the proverb sequence 
	public String getNextProverbCode(String index) {
		Map.Entry<String, String> next = proverbSymbols.higherEntry(index);
		
		// Return the first Joke if current string is the last joke 
		if (next == null) {
			next = proverbSymbols.firstEntry();
			return next.getKey();	
		}
						
		return next.getKey();
	}	

	// The main work to be completed with this socket connection
	public void run() {
		/*
		
		// Decide whether to print joke or proverb string
		if (serverState.equals(ServerState.JOKE.toString())) {
			 
			// get next joke according to uuid in cookie 
			String uuid = clientCookie.getName();
			String currentString = clientCookie.getValue();
			
			// get next joke and print to out stream 
			String currentJoke = jokeSymbols.get(currentString);
			String nextJoke = jokeServer.getNextJoke(currentJoke);
			
			// print currentJoke to output
			
			// update cookie object
			clientCookie.setValue(nextJoke);
			
			// pass cookie object to Worker constructor to print out & pass back cookie 
			
			new Worker(socket).start();
			
			// jokeServer.printAllJokes();
			
			
			// jokeServer.toggleServerState(); // toggle state after each connection
			
		} else if (serverState.equals(ServerState.PROVERB.toString())) {
			
			// get next proverb according to uuid in cookie 
			String uuid = cookie.getName();
			String currentString = cookie.getValue();
			
			// get next joke and print to out stream 
			String currentProverb = jokeSymbols.get(currentString);
			String nextProverb = jokeServer.getNextProverb(currentProverb);
			
			// print currentProverb to output
			
			// update cookie object
			cookie.setValue(nextProverb);
			
			// jokeServer.printAllProverbs();
			
			
			new Worker(socket).start();
		}
		*/
		
		 // Get I/O streams in/out from the socket - let's you read data in and out and print 
		 PrintStream out = null;
		 BufferedReader in = null;
		 		 
		 try {
			 in = new BufferedReader (new InputStreamReader(socket.getInputStream()));		// Reading data in from socket
			 out = new PrintStream(socket.getOutputStream());								// Print data out from socket 
			 
			// get next joke and print to out stream 
			String cookieString = in.readLine();	// read in cookie string from client - tells server which joke/proverb to run 
			System.out.println("Cookie string: " + cookieString);
			// Parse cookieString to get current joke/proverb code for that client 
			String[] cookieArray = cookieString.split("&");
			String cookieUUID = cookieArray[0];
			String cookieValue = cookieArray[1];
			
			if(this.serverMode.equals(ServerState.JOKE.toString())) {
				// Print the entire joke string for this joke symbol 
				String currentJoke = jokeSymbols.get(cookieValue);
				// Get the code of next joke in sequence and update cookieString to send back to client
				String nextJokeCode = getNextJokeCode(currentJoke); 
				String responseCookie = cookieUUID + "&" + nextJokeCode;
				out.println(responseCookie);
				out.flush();
			} else if (this.serverMode.equals(ServerState.PROVERB.toString())) {
				// Print the entire proverb string for this proverb symbol 
				String currentProverb = proverbSymbols.get(cookieValue);
				// Get the code of next proverb in sequence and update cookieString to send back to client
				String nextProverbCode = getNextProverbCode(currentProverb); 
				String responseCookie = cookieUUID + "&" + nextProverbCode;
				out.println(responseCookie);
				out.flush();
			} else {
				throw new IllegalArgumentException("Server is in Illegal State.");
			}	 
			 socket.close(); // close this connection, but not the server
		 } catch (IOException ioe) {
			 System.out.println(ioe);		 
		 }
	 }
	 
	// Print given server address 
	 static void printRemoteAddress(String name, PrintStream out) {
		 try {
			 out.println("Looking up " + name + "...");
			 InetAddress machine = InetAddress.getByName(name);
			 out.println("Host name : " + machine.getHostName()); 
			 out.println("Host IP : " + toText (machine.getAddress()));
		 } catch (UnknownHostException ex) {
			 out.println("Failed in attempt to look up " + name);
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
