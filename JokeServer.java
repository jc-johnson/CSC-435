package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JokeServer {

	public static void main(String[] args) {
		int q_len = 6; // Number of requests to queue
		int port = 4545;
		String ServerName = "localhost";
		Socket sock;

		ServerSocket servsock;
		try {
			servsock = new ServerSocket(port, q_len);
			
			// Get server connection and pass to Worker to start
			System.out.println
				("Jordan Johnson's Joke server 1.8 starting up, listening at port 4545.\n");
			while (true) {
				sock = servsock.accept(); // accepts client connection
				new Worker(sock).start(); // Spawn worker to handle connection as a thread 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
