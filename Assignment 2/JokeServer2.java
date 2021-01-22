package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JokeServer2 {

	public static void main(String[] args) {
		int q_len = 6; 
	    int port = 4545;
	    Socket sock;

	    // create new thread
	    AdminLooper AdminLooper = new AdminLooper(); 
	    Thread thread = new Thread(AdminLooper);
	    thread.start();  
	    
	    ServerSocket servsock;
	    
		try {
			servsock = new ServerSocket(port, q_len);
			System.out.println("Jordan Johnson's Joke server starting up at port 4545.\n");
		    while (true) {
		      // wait for the next client connection:
		      sock = servsock.accept();
		      new Worker (sock).start();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
