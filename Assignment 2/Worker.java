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

	Socket socket; // Main class for creating a server connection
	HttpCookie httpCookie;
	
	String printString;
	
	public Worker (Socket socket) {
		this.socket = socket;
	}
	
	public Worker(Socket socket, HttpCookie httpCookie) {
		this.socket = socket;
		this.httpCookie = httpCookie;
	}
	
	// Includes the next string that should be printed to output 
	public Worker(Socket socket, String string) {
		this.socket = socket;
		this.printString = string;
	}
	
	public Worker(Socket socket, JokeServer jokeServer, HttpCookie httpCookie) {
		this.socket = socket;
		this.httpCookie = httpCookie;
	}
	
	// The main work to be completed with this socket connection
	public void run() {
		 // Get I/O streams in/out from the socket - let's you read data in and out and print 
		 PrintStream out = null;
		 BufferedReader in = null;
		 ObjectOutputStream objectOut; 
		 ObjectInputStream objectIn;
		 		 
		 try {
			 in = new BufferedReader (new InputStreamReader(socket.getInputStream()));		// Reading data in from socket
			 out = new PrintStream(socket.getOutputStream());								// Print data out from socket 
			 objectOut = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));					// Send object out
			 objectIn = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));						// Read object in 
			 
			 if(!this.printString.isEmpty()) {
				 out.print("Print String: \n" + printString );
			 } else {
				 out.print("Print String is empty");
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
