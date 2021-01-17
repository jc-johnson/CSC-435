package main;

import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries

// This class does the main work for all connecting threads 
public class JokeClientAdmin extends Thread {
	
	private JokeServer jokeServer;
	private Socket socket; // Main class for creating a server connection
	
	public JokeClientAdmin (Socket socket) {
		
		this.socket = socket;
	}
	
	public JokeClientAdmin (JokeServer jokeServer, Socket socket) {
		
		this.jokeServer = jokeServer;
		this.socket = socket;
	}
	
	public String getServerState() {
		
		if (jokeServer != null) {
			
			String serverState = jokeServer.getState(); 
			
			if(!serverState.isEmpty()) {
				
				System.out.println("The server is in " + serverState + " mode.");
				return serverState;
			}
		}
		
		return "";
		// throw new exception
	}
	
	public void run(){
		PrintStream toServer;toServer = new PrintStream(socket.getOutputStream());	// Print output to server 
		BufferedReader fromServer;
		toServer.println(name); 
		toServer.flush();
		
		getServerState();
	}
	
	 
	/* 
	public void run() {
		 // Get I/O streams in/out from the socket - let's you read data in and out and print 
		 PrintStream out = null;
		 BufferedReader in = null;
		 try {
			 // in = new BufferedReader (new InputStreamReader(socket.getInputStream()));	// Reading data in from socket
			 // out = new PrintStream(socket.getOutputStream());								// Print data out from socket 
			 try {
				 String name;
				 // name = in.readLine();
				 // System.out.println("Looking up " + name);
				 // printRemoteAddress(name, out);	// Simply print server address to out stream
				 
			 } catch (IOException x) {
				 System.out.println("Server read error");
				 x.printStackTrace();
			 }
			 socket.close(); // close this connection, but not the server
		 } catch (IOException ioe) {
			 System.out.println(ioe);		 
		 }
	 }
	 */
	 
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
