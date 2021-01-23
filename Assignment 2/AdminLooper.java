package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminLooper implements Runnable {
	public static boolean adminControlSwitch = true;
	
	public void run(){ 
	    System.out.println("In the admin looper thread");
	    
	    int q_len = 6; 
	    int port = 5050;  
	    Socket sock;

	    try{
	    	ServerSocket servsock = new ServerSocket(port, q_len);
	    	while (adminControlSwitch) {
	    		// wait for the next ADMIN client connection:
	    		sock = servsock.accept();
	    		new AdminWorker (sock).start();
	    		servsock.close();
	    	}
	    }catch (IOException ioe) {
	    	System.out.println(ioe);
	    }
	}
}
