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
		}
		
		// else throw new null argument exception
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
		}
		
		return "";
		// throw new exception
	}
	
	public void printAllJokes() {
		if(jokeServer != null) {
			for(int i = 0; i < 4; i++) {
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
			for(int i = 0; i < 4; i++) {
				String proverb = jokeServer.getJoke(i);
				printStream.println(proverb);
				printStream.flush();
			}
		} else {
			throw new NullPointerException("Joke Server is null.");
		}
	}	
	
	public void run(){
		
		getServerState();
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