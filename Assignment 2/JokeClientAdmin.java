package main;

import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries

public class JokeClientAdmin extends Thread {
	Socket sock; // Main class for creating a server connection
	JokeClientAdmin (Socket s) {sock = s;}
	 
	public void run() {
		 // Get I/O streams in/out from the socket - let's you read data in and out and print 
		 PrintStream out = null;
		 BufferedReader in = null;
		 try {
			 in = new BufferedReader (new InputStreamReader(sock.getInputStream()));	// Reading data in from socket
			 out = new PrintStream(sock.getOutputStream());								// Print data out from socket 
			 try {
				 String name;
				 name = in.readLine();
				 System.out.println("Looking up " + name);
				 printRemoteAddress(name, out);	// Simply print server address to out stream 
			 } catch (IOException x) {
				 System.out.println("Server read error");
				 x.printStackTrace();
			 }
			 sock.close(); // close this connection, but not the server
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
