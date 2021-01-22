package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class AdminWorker extends Thread {
	
	Socket socket; 
	public AdminWorker (Socket socket) {
		socket = socket;
	} 
	
	public void run(){
		// Get I/O streams in/out from the socket:
		PrintStream out = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			
			// Note that this branch might not execute when expected:
			try {
				String serverName;
				serverName = in.readLine ();
				System.out.println("Looking up " + serverName);
				printRemoteAddress(serverName, out);
			} catch (IOException x) {
				System.out.println("Server read error");
				x.printStackTrace ();
			}
			socket.close(); 
	 } 
		catch (IOException ioe) {System.out.println(ioe);}
	 }

	static void printRemoteAddress (String name, PrintStream out) {
		try {
			out.println("Looking up " + name + "...");
			InetAddress machine = InetAddress.getByName (name);
			out.println("Host name : " + machine.getHostName ()); // To client...
			out.println("Host IP : " + toText (machine.getAddress ()));
		} catch(UnknownHostException ex) {
			out.println ("Failed in atempt to look up " + name);
		}
	}

	static String toText (byte ip[]) { 
		StringBuffer result = new StringBuffer ();
		for (int i = 0; i < ip.length; ++ i) {
			if (i > 0) result.append (".");
			result.append (0xff & ip[i]);
		}
		return result.toString ();
	}
}
