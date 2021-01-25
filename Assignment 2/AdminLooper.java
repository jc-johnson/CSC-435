/*--------------------------------------------------------

1. Jordan Johnson / 1/24/11:

2. Java version used, if not the official version for the class:

build 11.0.4+11

3. Precise command-line compilation examples / instructions:

Note: 
One command to compile all needed classes 
> javac .\Worker.java JokeClient.java JokeClientAdmin.java JokeServer.java AdminLooper.java AdminWorker.java

4. Precise examples / instructions to run this program:

In different shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

5. List of files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java
 e. Worker.java
 f. AdminLooper.java 
 g. AdminWorker.java

5. Notes:


----------------------------------------------------------*/



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminLooper implements Runnable {
	
	public void run(){ 
	    System.out.println("This is the admin looper thread");
	    
	    int q_len = 6; 
	    int port = 5050;  
	    Socket sock;

	    try{
	    	ServerSocket servsock = new ServerSocket(port, q_len);
	    	while (true) {
	    		// wait for the next admin client connection:
	    		sock = servsock.accept();
	    		new AdminWorker (sock).start();
	    		servsock.close();
	    	}
	    }catch (IOException ioe) {
	    	System.out.println(ioe);
	    }
	}
}
