/*

  THANKS:
  https://www.quickprogrammingtips.com/java/how-to-generate-sha256-hash-in-java.html  @author JJ
  https://dzone.com/articles/generate-random-alpha-numeric  by Kunal Bhatia  Â·  Aug. 09, 12 Â· Java Zone

  Clark Elliott mods 2020-05-20, 2019-05-16.

  Here we do some real work as follows:

  Get some data from the user (represents blockdata)

  WORK LOOP:

  Get a random AlphaNumeric string (represents the random seed--our guess)
  Concatenate with the blockdata
  Hash the concatated string
  Get the numeric value of the first 16 bits, if it is less than 20,000 we solved the puzzle
  Otherwise loop and do it again.

  END LOOP

  Notes:
  Hexadecimal characters each represent four bits, so we count from 0 to 15 (2**4), with chars 0-F.
  16 bits will range from 0 to 65535 (2**16).
  This is real work. We have to find a seed such that the leftmost 16 bits of the hash is < 20000.
  We picked 20000, but if you make the number lower the work is harder and takes longer.
  The "faking" component is only by setting the work threshold higher, and using a sleep() method.
  But it is still real work.
  You could make this MUCH harder by, e.g., looking at 256 bits and making the threshold low.

*/

package com.company;

import java.security.MessageDigest;
import java.util.Scanner;
//import javax.xml.bind.DatatypeConverter;  Java 1.9 does not like this. War on XML!
import java.util.Arrays;

public class WorkB {

    public static String ByteArrayToString(byte[] ba){
        StringBuilder hex = new StringBuilder(ba.length * 2);
        for(int i=0; i < ba.length; i++){
            hex.append(String.format("%02X", ba[i]));
        }
        return hex.toString();
    }

    // Return random alphanumeric number
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    static String someText = "one two three";
    static String randString;

    public static void main(String[] args) throws Exception {
        String concatString = "";  // Random seed string concatenated with the existing data
        String stringOut = ""; // Will contain the new SHA256 string converted to HEX and printable.

        Scanner ourInput = new Scanner(System.in);
        System.out.print("Enter some blockdata: ");
        String stringIn = ourInput.nextLine();

        randString = randomAlphaNumeric(8);
        System.out.println("Our example random seed string is: " + randString + "\n");
        System.out.println("Concatenated with the \"data\": " + stringIn + randString + "\n");

        System.out.println("Number will be between 0000 (0) and FFFF (65535)\n");
        int workNumber = 0;     // Number will be between 0000 (0) and FFFF (65535), here's proof:
        workNumber = Integer.parseInt("0000",16); // Lowest hex value
        System.out.println("0x0000 = " + workNumber);

        workNumber = Integer.parseInt("FFFF",16); // Highest hex value
        System.out.println("0xFFFF = " + workNumber + "\n");

        try {

            for(int i=1; i<20; i++){ // Limit how long we try for this example.
                randString = randomAlphaNumeric(8); // Get a new random AlphaNumeric seed string
                concatString = stringIn + randString; // Concatenate with our input string (which represents Blockdata)
                MessageDigest MD = MessageDigest.getInstance("SHA-256");
                byte[] bytesHash = MD.digest(concatString.getBytes("UTF-8")); // Get the hash value

                //stringOut = DatatypeConverter.printHexBinary(bytesHash); // Turn into a string of hex values Java 1.8
                stringOut = ByteArrayToString(bytesHash); // Turn into a string of hex values, java 1.9
                System.out.println("Hash is: " + stringOut);

                workNumber = Integer.parseInt(stringOut.substring(0,4),16); // Between 0000 (0) and FFFF (65535)
                System.out.println("First 16 bits in Hex and Decimal: " + stringOut.substring(0,4) +" and " + workNumber);
                if (!(workNumber < 20000)){  // lower number = more work.
                    System.out.format("%d is not less than 20,000 so we did not solve the puzzle\n\n", workNumber);
                }
                if (workNumber < 20000){
                    System.out.format("%d IS less than 20,000 so puzzle solved!\n", workNumber);
                    System.out.println("The seed (puzzle answer) was: " + randString);
                    break;
                }
                // Here is where you would periodically check to see if the blockchain has been updated
                // ...if so, then abandon this verification effort and start over.
                // Here is where you will sleep if you want to extend the time up to a second or two.
            }
        } catch(Exception ex) {ex.printStackTrace();}


    }
}