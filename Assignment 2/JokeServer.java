package main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.ArrayList; 
import java.util.Arrays;
import java.util.HashMap; 

public class JokeServer {
	
	private enum ServerState {
		JOKE, PROVERB
	}
		
	private ServerState state;
	
	Map<Integer, String> jokes = new HashMap<Integer, String>() {{
	    put(1, "How does a cucumber become a pickle?\n \t\t It goes through a jarring experience.");
	    put(2, "What's brown and sticky? \\n \\t\\t A stick.");
	    put(3, "What did one volcano say to the other? \\n \\t\\t I lava you.");
	    put(4, "What do you call two birds in love? \\n \\t\\t Tweethearts.");
	}};
	
	Map<Integer, String> proverbs = new HashMap<Integer, String>() {{
	    put(1, "A bad workman always blames his tools.");
	    put(2, "A drowning man will clutch at a straw.");
	    put(3, "Adversity and loss make a man wise.");
	    put(4, "A stitch in time saves nine.");
	}};
		
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
