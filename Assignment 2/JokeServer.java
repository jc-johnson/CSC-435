package main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList; 
import java.util.Arrays; 

public class JokeServer {
	
	private enum ServerState {
		JOKE, PROVERB
	}
	
	private List<String> jokes = new ArrayList<>(
		Arrays.asList("", "", "", ""));
	
	private List<String> proverbs = new ArrayList<>(
		Arrays.asList("", "", "", ""));
	
	private ServerState state; 
	
	public JokeServer() {
		state = ServerState.JOKE;
	}
	
	public String getState() {
		return state.name();
	}
	
	private void toggleServerState() {
		if (state != null) {
			if(state.equals(ServerState.JOKE)) {
				state = ServerState.PROVERB;
			} else {
				state = ServerState.JOKE;
			}
		}
		
		// throw new Exception 
	}

	public static void main(String[] args) {
		int queuelength = 6; 
		int port = 4545;
		String ServerName = "localhost";
		Socket socket;		
		JokeServer jokeServer = new JokeServer();
		
		ServerSocket serversocket;
		
		try {
			serversocket = new ServerSocket(port, queuelength);
			
			// Get server connection and pass socket to Worker to start
			System.out.println
				("Jordan Johnson's Joke server starting up, listening at port 4545.\n");
			while (true) {
				socket = serversocket.accept(); // Accepts client connection
				System.out.println(jokeServer.getState()); // return server state
				// new JokeClientAdmin(socket).start(); // Create worker class to handle new connection   
				new JokeClientAdmin(jokeServer, socket).start(); // Client admin class handles this thread's work 
				jokeServer.toggleServerState(); // toggle state after each connection
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
