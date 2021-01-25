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
		// Get I/O streams in/out from the socket:
		PrintStream out = null;
		BufferedReader in = null;
		 
		try {
			in = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			// print all jokes 
			if(this.serverMode.equals(ServerState.JOKE.toString())) {
				 
				System.out.println("Printing all jokes...");
				printAllJokes(out);
				 
			// print all proverbs	 
			} else if (this.serverMode.equals(ServerState.PROVERB.toString())) {
				System.out.println("Printing all proverbs...");
				printAllProverbs(out);
			} else {
				throw new IllegalArgumentException("Illegal state value.");
			}
			socket.close();
		} catch (IOException x) {
			System.out.println("Server read error");
			x.printStackTrace ();
		}
	}
		 
	
		 
	public void printAllJokes(PrintStream printStream) {
		for(int i = 1; i <= 4; i++) {
			String joke = jokeSymbols.get(i);
			printStream.println(joke);
			printStream.flush();
		}
		printStream.println("\n JOKE CYCLE COMPLETED \n");
		printStream.flush();		
	}	 	 
			
	public void printAllProverbs(PrintStream printStream) {
		for(int i = 1; i <= 4; i++) {
			String proverb = proverbSymbols.get(i);
			printStream.println(proverb);
			printStream.flush();
		}
		printStream.println("\n PROVERB CYCLE COMPLETED \n"); 
		printStream.flush();	
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
