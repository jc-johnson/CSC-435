package other;

import java.io.*;  // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries

public class MiniWebServer {
	
	static int i = 0;

	public static void main(String a[]) throws IOException {
		int q_len = 6; /* Number of requests for OpSys to queue */
	    int port = 2540;
	    MiniWebServer server = new MiniWebServer();
	    Socket sock;

	    ServerSocket servsock = new ServerSocket(port, q_len);

	    System.out.println("Jordan Johnson's WebResponse running at 2540.");
	    System.out.println("Point Firefox browser to http://localhost:2540/abc.\n");
	    while (true) {
	      // wait for the next client connection:
	      sock = servsock.accept();
	      new Worker(sock).start();
	    }
	}

}
