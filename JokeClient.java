package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class JokeClient {

	public static void main(String[] args) {
		String serverName;
		if (args.length < 1 ) serverName = "localhost";
		else serverName = args[0];
		
		System.out.println("Jordan Johnson's Inet Client, 1.8.\n");
		System.out.println("Using server: " + serverName + ", Port: 1565");
		// Create new reader with standard input stream
		// This reads in user input 
		BufferedReader in = new BufferedReader(new InputStreamReader (System.in));
		try {
			String name;
			do {
				System.out.print("Enter a hostname or an IP address, (quit) to end:");
				System.out.flush();
				name = in.readLine();	// read input 
				if (name.indexOf("quit") < 0)
						getRemoteAddress(name, serverName);
			} while (name.indexOf("quit") < 0); // 
				System.out.println("Cancelled by user request.");
		} catch (IOException x) {
			x.printStackTrace();
		} 
	}
	
	// Converts byte to string 
		private static String toText (byte ip[]) { 
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < ip.length; ++i) {
				if (i > 0) result.append(".");
					result.append(0xff & ip[i]);
			}
			return result.toString();
		}
		
		// Gets the address of a given server 
		private static void getRemoteAddress (String name, String serverName) {
			
			Socket sock; // the main class we will use to create a server connection
			BufferedReader fromServer;
			PrintStream toServer;
			String textFromServer;
			
			try {
				/* Open connection to given server port */
				sock = new Socket(serverName, 1565);
				
				// Create I/O streams to read data to/from the socket
				fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));	// Read input from server
				toServer = new PrintStream(sock.getOutputStream());	// Print output to server 
				
				toServer.println(name); 
				toServer.flush();
				
				// Read 2-3 lines of response from server and prints the response 
				for (int i = 1; i <= 3; i++) {
					textFromServer = fromServer.readLine();
					if (textFromServer != null) System.out.println(textFromServer);
				}
				sock.close();	//Close socket 
				
			} catch (IOException x){
				System.out.println("Socket error.");
				x.printStackTrace();
			}
		}

}
