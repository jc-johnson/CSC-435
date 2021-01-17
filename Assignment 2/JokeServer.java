package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JokeServer {

	public static void main(String[] args) {
		int queuelength = 6; 
		int port = 4545;
		String ServerName = "localhost";
		Socket socket;

		ServerSocket serversocket;
		try {
			serversocket = new ServerSocket(port, queuelength);
			
			// Get server connection and pass socket to Worker to start
			System.out.println
				("Jordan Johnson's Joke server starting up, listening at port 4545.\n");
			while (true) {
				socket = serversocket.accept(); // accepts client connection
				new JokeClientAdmin(socket).start(); // Spawn worker to handle connection as a thread 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
