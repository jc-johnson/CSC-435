package other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Worker extends Thread {    // Class definition
	  Socket socket;                   // Class member, socket, local to ListnWorker.
	  Worker (Socket s) {socket = s;} // Constructor, assign arg s to local sock
	  
	  public void run(){
	    PrintStream out = null;   // Input from the socket
	    BufferedReader in = null; // Output to the socket
	    try {
	      out = new PrintStream(socket.getOutputStream());
	      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	      
	      String userName;
	      int numberOne;
	      int numberTwo;
	      
	      // read server input 
	      while (true) {
            String currentLine = in.readLine();
            if (currentLine.isEmpty())
                break;
            
            // Read the line with GET parameters 
            if(currentLine.contains("GET")) {
          	  	// Parse for user name and numbers 
            	String[] values = currentLine.split("");
          	  	userName = values[0].substring(values[0].lastIndexOf("=")+1);
          	  	
          	  	String numberOneString = values[1].substring(values[1].lastIndexOf("=")+1);
          	  	String numberTwoString = values[2].substring(values[2].lastIndexOf("=")+1);
          	  	
          	  	numberOne = Integer.parseInt(numberOneString);
          	  	numberTwo = Integer.parseInt(numberTwoString);
            }
            
        }
	      
	      

	      System.out.println("Sending the HTML Reponse now: " + Integer.toString(WebResponse.i) + "\n" );
	      String HTMLResponse = "<html> <h1> Hello Browser World! " + Integer.toString(WebResponse.i++) +  "</h1> <p><p> <hr> <p>";
	      
	      out.println("HTTP/1.1 200 OK");
	      out.println("Connection: close"); // Can fool with this.
	      
	      // int Len = HTMLResponse.length();
	      // out.println("Content-Length: " + Integer.toString(Len));
	      
	      out.println("Content-Length: 400"); // Lazy, so set high. Calculate later.
	      out.println("Content-Type: text/html \r\n\r\n");
	      out.println(HTMLResponse);

	      for(int j=0; j<6; j++){ // Echo some of the request headers for fun
	    	  out.println(in.readLine() + "<br>\n"); // Save and calculate length
	      }                                        // ...if you care to.
	      out.println("</html>"); 
		
	      socket.close(); 
	    } catch (IOException x) {
	      System.out.println("Error: Connetion reset. Listening again...");
	    }
	  }
}
