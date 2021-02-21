package main;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.*;

public class BlockChain {
	
	public void ReadJSON(){
	    System.out.println("\n=========> In ReadJSON <=========\n");
	    
	    Gson gson = new Gson();

	    try (Reader reader = new FileReader("blockRecord.json")) {
	      
	      // Read and convert JSON File to a Java Object:
	      BlockRecord blockRecordIn = gson.fromJson(reader, BlockRecord.class);
	      
	      // Print the blockRecord:
	      System.out.println(blockRecordIn);
	      System.out.println("Name is: " + blockRecordIn.Fname + " " + blockRecordIn.Lname);

	      String INuid = blockRecordIn.uuid.toString();
	      System.out.println("String UUID: " + blockRecordIn.BlockID + " Stored-binaryUUID: " + INuid);

	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }

	public static void main(String[] args) {
		System.out.println("BlockChain program starting. ");

	}

}
