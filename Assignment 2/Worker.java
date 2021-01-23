package main;

import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries

public class Worker extends Thread {
	Socket socket; // Main class for creating a server connection
	HttpCookie httpCookie;
	
	String printString;
	
	public Worker (Socket socket) {
		socket = socket;
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
	 
	public void run() {
		 // Get I/O streams in/out from the socket - let's you read data in and out and print 
		 PrintStream out = null;
		 BufferedReader in = null;
		 ObjectOutputStream objectOut; 
		 ObjectInputStream objectIn;
		 
		 try {
			 in = new BufferedReader (new InputStreamReader(socket.getInputStream()));		// Reading data in from socket
			 out = new PrintStream(socket.getOutputStream());								// Print data out from socket 
			 objectOut = new ObjectOutputStream(socket.getOutputStream());					// Send object out
			 objectIn = new ObjectInputStream(socket.getInputStream());						// Read object in 
			 
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
