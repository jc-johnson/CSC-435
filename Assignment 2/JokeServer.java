package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JokeServer {
	
	private enum ServerState {
		JOKE, PROVERB
	}
	
	private ServerState state = ServerState.JOKE;
	
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

		ServerSocket serversocket;
		try {
			serversocket = new ServerSocket(port, queuelength);
			
			// Get server connection and pass socket to Worker to start
			System.out.println
				("Jordan Johnson's Joke server starting up, listening at port 4545.\n");
			while (true) {
				socket = serversocket.accept(); // Accepts client connection
				new JokeClientAdmin(socket).start(); // Create worker class to handle new connection   
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
