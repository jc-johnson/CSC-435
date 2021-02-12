package other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worker extends Thread {    // Class definition
	  Socket socket;                   // Class member, socket, local to ListnWorker.
	  Worker (Socket s) {socket = s;} // Constructor, assign arg s to local sock
	  
	  public void run(){
	    PrintStream out = null;   // Input from the socket
	    BufferedReader in = null; // Output to the socket
	    try {
	      out = new PrintStream(socket.getOutputStream());
	      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	      
	      String userName = "";
	      String answerString = "";
	      
	      // read server input 
	      while (true) {
            String currentLine = in.readLine();
            if (currentLine.isEmpty())
                break;
            
            // Read the line with GET parameters 
            if(currentLine.contains("GET")) {
          	  	// Parse for user name and numbers 
            	String[] values = currentLine.split("&");
            	      	
          	  	userName = values[0].substring(values[0].lastIndexOf("=")+1);
          	  	
          	  	String numberOneString = values[1].substring(values[1].lastIndexOf("=")+1);
          	  	String numberTwoString = values[2].substring(values[2].lastIndexOf("=")+1);
          	  	
          	  	Pattern pattern = Pattern.compile("[0-9]\\s");
          	  	Matcher matcher = pattern.matcher(numberTwoString);
          	  	String numberTwoGroup;
          	  	if (matcher.find()) {
        	  		numberTwoGroup = matcher.group().trim();
        	  	} else {
        	  		throw new IllegalArgumentException("Bad argument.");
        	  	}
          	  	          	  	
          	  	int numberOne = Integer.parseInt(numberOneString);
          	  	int numberTwo = Integer.parseInt(numberTwoGroup);
          	  	int answer = numberOne + numberTwo;
          	  	answerString = String.valueOf(answer);
            }
	      }
	      
	      String HTMLResponse = "<html> <h1> Hi " + userName +  "!</h1> <p><p> <p>The sum of the numbers you entered is: " + answerString + "</p><hr> <p>";
	      
	      out.println("HTTP/1.1 200 OK");
	      out.println("Connection: close"); 
	      
	      out.println("Content-Length: 400"); 
	      out.println("Content-Type: text/html \r\n\r\n");
	      out.println(HTMLResponse);

	      out.println("</html>"); 
		
	      socket.close(); 
	    } catch (IOException x) {
	      System.out.println("Error: Connetion reset. Listening again...");
	    }
	  }
}
