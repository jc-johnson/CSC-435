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

If you start the Client before the Server, you will get an error until you also start the Server.  

e.g.:

I faked the random number generator. I have a bug that comes up once every
ten runs or so. If the server hangs, just kill it and restart it. You do not
have to restart the clients, they will find the server again when a request
is made.

----------------------------------------------------------*/

package main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.HttpCookie;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.ArrayList; 
import java.util.Arrays;
import java.util.HashMap; 

public class JokeServer {
	
	private enum ServerState {
		JOKE, PROVERB
	}
		
	private ServerState state;
	
	private Map<UUID, String> clientData = new HashMap<UUID, String>();
	
	private Map<Integer, String> jokes = new HashMap<>() {{
	    put(1, "How does a cucumber become a pickle? \n \t\t It goes through a jarring experience.");
	    put(2, "What's brown and sticky? \n \t A stick.");
	    put(3, "What did one volcano say to the other? \n \t I lava you.");
	    put(4, "What do you call two birds in love? \n \t Tweethearts.");
	}};
	
	private static NavigableMap<String, String> jokeSymbols = new TreeMap<>() {{
	    put("JA", "How does a cucumber become a pickle? \n \t\t It goes through a jarring experience.");
	    put("JB", "What's brown and sticky? \n \t A stick.");
	    put("JC", "What did one volcano say to the other? \n \t I lava you.");
	    put("JD", "What do you call two birds in love? \n \t Tweethearts.");
	}};
	
	private Map<Integer, String> proverbs = new HashMap<>() {{
	    put(1, "A bad workman always blames his tools.");
	    put(2, "A drowning man will clutch at a straw.");
	    put(3, "Adversity and loss make a man wise.");
	    put(4, "A stitch in time saves nine.");
	}};
	
	private static NavigableMap<String, String> proverbSymbols = new TreeMap<>() {{
	    put("JA", "How does a cucumber become a pickle? \n \t\t It goes through a jarring experience.");
	    put("JB", "What's brown and sticky? \n \t A stick.");
	    put("JC", "What did one volcano say to the other? \n \t I lava you.");
	    put("JD", "What do you call two birds in love? \n \t Tweethearts.");
	}};
	
		
	public JokeServer() {
		state = ServerState.JOKE;
	}
	
	// Inserts record of the last message a client received 
	public void addClientRecord(UUID uuid, String lastString) {
		clientData.put(uuid, lastString);
	}
	
	// Get the symbol of the last message a client received 
	public void getClientRecord(UUID uuid) {
		clientData.get(uuid);
	}
	
	public String getJoke(int index) {
		return jokes.get(index);
	}
	
	public String getJoke(String index) {
		return jokeSymbols.get(index);
	}
	
	// Get joke code associated with a given joke string
	public String getJokeCode(String jokeString) {
		Set<String> jokeKeys = jokeSymbols.keySet();
		
		for (String key : jokeKeys) {
			if(jokeSymbols.get(key).equals(jokeString)) {
				return key;
			}
		}
		
		throw new IllegalArgumentException("No code found for given joke.");
		
	}
	
	public String getNextJoke(String index) {
		Map.Entry<String, String> next = jokeSymbols.higherEntry(index);
		
		// Return the first Joke if current string is the last joke 
		if (next == null) {
			next = jokeSymbols.firstEntry();
			return next.getValue();	
		}
		
		return next.getValue();
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
	
	public String getPreviousJoke(String index) {
		Map.Entry<String, String> previous = jokeSymbols.higherEntry(index);
		return previous.getValue();
	}
	
	public String getProverb(int index) {
		return proverbs.get(index);
	}
	
	public String getProverb(String index) {
		return proverbSymbols.get(index);
	}
	
	// Get joke code associated with a given joke string
	public String getProverbCode(String jokeString) {
			Set<String> jokeKeys = jokeSymbols.keySet();
			
			for (String key : jokeKeys) {
				if(jokeSymbols.get(key).equals(jokeString)) {
					return key;
				}
			}
			
			throw new IllegalArgumentException("No code found for given joke.");
			
		}
	
	public String getNextProverb(String index) {
		Map.Entry<String, String> next = proverbSymbols.higherEntry(index);
		
		// Return the first Joke if current string is the last joke 
		if (next == null) {
			next = proverbSymbols.firstEntry();
			return next.getValue();	
		}
				
		return next.getValue();
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
	
	public String getPreviousProverb(String index) {
		Map.Entry<String, String> previous = proverbSymbols.higherEntry(index);
		return previous.getValue();
	}
	
	public String getState() {
		return state.name();
	}
	
	public void printAllJokes() {
		for(Map.Entry<Integer, String> entry : jokes.entrySet())
			System.out.println(
					"Joke: " + entry.getValue() + ": \n ");
	}
	
	public void printAllProverbs() {
		for(Map.Entry<Integer, String> entry : proverbs.entrySet())
			System.out.println(
					"Proverb: " + entry.getValue() + ": \n ");
	}
	
	private void toggleServerState() {
		if (state != null) {
			if(state.equals(ServerState.JOKE)) {
				state = ServerState.PROVERB;
			} else {
				state = ServerState.JOKE;
			}
		} else if (state == null) {
			throw new NullPointerException("Server state is null.");
		}
	}

	public static void main(String[] args) {
		int queuelength = 6; 
		int port = 4545;	// Port for client 
		String serverName = "localhost";
		Socket socket;		
		JokeServer jokeServer = new JokeServer();
		
		ServerSocket serversocket;
		
		try {
			serversocket = new ServerSocket(port, queuelength);
			
			// Get server connection and pass socket to Worker to start
			System.out.println
				("Jordan Johnson's Joke server starting up, listening at port 4545.\n");
			while (true) {
				socket = serversocket.accept(); // Accepts client connection
				String serverState =  jokeServer.getState(); 
				System.out.println("Server state: " + serverState + "\n"); // return server state
				
				InputStream inputStream = socket.getInputStream();
				// ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
				ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
				
				// Read in cookie from client
				// List<HttpCookie> cookies = (List<HttpCookie>) objectInputStream.readObject();
				// HttpCookie clientCookie = cookies.get(0);
				
				
				if (clientCookie == null ) {
					objectInputStream.close();
					throw new NullPointerException("Cookie read from Client is null.");
				}
				
				System.out.println("Cookie Name: " + clientCookie.getName() + "\n"); 
				System.out.println("Cookie Value: " + clientCookie.getValue() + "\n"); 
				
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
			
				// jokeServer.toggleServerState(); // toggle state after each connection
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
