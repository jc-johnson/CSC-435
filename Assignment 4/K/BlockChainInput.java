/* BlockChainInput.java

To compile and run:

javac -cp "gson-2.8.2.jar" BlockChainInput.java
java -cp ".;gson-2.8.2.jar" BlockChainInput

RunBlockInput.bat:
java -cp ".;gson-2.8.2.jar" m %1

Example for process two:

> RunBlockInput 2

Author: Jordan Johnson, with ample help from the below web sources.

You are free to use this code in your assignment, but you MUST add
your own comments. Leave in the web source references.


This utility program shows one method of reading data into a linked list of unverified blocks from an input data file.
The specific data file / Process ID is determined by argment passed to Java at runtime.
The list is shuffled. Blocks are also written into a priority queue with TimeStamp priority which
demonstrates how the priority queue works.

The shuffled list is marshaled (written) to disk in JSON format.

----------------------------

Requires three data files:

BlockInput0.txt:

John Smith 1996.03.07 123-45-6789 Chickenpox BedRest aspirin
Joe  Blow  1996.03.07 123-45-6888 Smallpox BedRest Whiskey
Julie Wilson 1996.03.07 123-45-6999 Insomnia Exercise HotPeppers
Wayne Blaine 1942.07.07 123-45-6777 Measles WaitToGetBetter CodLiverOil

BlockInput1.txt:

Rita Vita  1992.01.31 999-456-789 ObessivePersonality TryToRelax Ibuprofen
Wei  Xu  1996.03.22 123-456-333 Shingles WaitForRelief Zovirax
Sally McCutty 1970.01.01 123-456-999 Migraine IcePack Almotriptan
Bruce Lee 1940.11.27 456-789-123 SoreStomach LessCombat Vicodine

BlockInput2.txt:

Helen Keller 1880.06.27 666-45-6789 Arthritis WarmCloths Aspirin
Abraham Lincoln 1809.02.12 444-45-6888 GreviousWound Surgery Whiskey
John Kennedy 1917.05.29  333-45-6999 AddisonsDisease DrugTherapy Steroids
Joe DiMaggio 1914.11.25  111-22-3333 SoreKnees RestFromSports Aspirin


------------

The web sources:

Reading lines and tokens from a file:
http://www.fredosaurus.com/notes-java/data/strings/96string_examples/example_stringToArray.html
Good explanation of linked lists:
https://beginnersbook.com/2013/12/linkedlist-in-java-with-example/
Priority queue:
https://www.javacodegeeks.com/2013/07/java-priority-queue-priorityqueue-example.html

-----------------------------------------------------------------------*/



package com.company;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class BlockChainInput {
    private static String FILENAME;
    String serverName = "localhost";
    static String blockchain = "[First block]"; // Initialize our block chain
    static int numProcesses = 3;
    static int PID = 0;

    // Priority queue for our blocks
    final PriorityBlockingQueue<BlockRecord> priorityQueue = new PriorityBlockingQueue<>(4, BlockTSComparator);

    // List to hold Unverified blocks
    private LinkedList<BlockRecord> blockRecordList = new LinkedList<BlockRecord>();

    // Hold blocks read in from JSON files
    private LinkedList<BlockRecord> jsonBlockRecordList = new LinkedList<BlockRecord>();

    // Store input values - update to read in additional values from files
    private static final int FirstName = 0;
    private static final int LastName = 1;
    private static final int DOB = 2;
    private static final int SSNUM = 3;
    private static final int DIAG = 4;
    private static final int TREAT = 5;
    private static final int RX = 6;
    private static final int TEST = 7;

    public static int PublicKeyServerPortBase = 4710;
    public static int UnverifiedBlockServerPortBase = 4820;
    public static int BlockchainServerPortBase = 4930;

    public static int KeyServerPort = PublicKeyServerPortBase + PID;
    public static int UnverifiedBlockServerPort = UnverifiedBlockServerPortBase + PID;
    public static int BlockchainServerPort = BlockchainServerPortBase + PID;


    List<String> fileOuput = new ArrayList<>();
    String outputFile = "BlockchainLedger.json";

    String publicKeyFileName = "PublicKey.json";

    private final String  publicKeyMapString = "publicKey";
    private final String privateKeyMapString = "privateKey";

    public BlockChainInput (String argv[]) {
        System.out.println("In the constructor...");
    }

    // Function to compare block records and decides which goes first in the block chain.
    // Determined by the time stamp field in each block
    public static Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>()
    {
        @Override
        public int compare(BlockRecord b1, BlockRecord b2)
        {
            String s1 = b1.getTimeStamp();
            String s2 = b2.getTimeStamp();
            if (s1 == s2) {return 0;}
            if (s1 == null) {return -1;}
            if (s2 == null) {return 1;}
            return s1.compareTo(s2);
        }
    };

    // Easily convert our block records to json string using gson
    public String convertBlockToJSON(BlockRecord blockRecord){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(blockRecord);
        return json;
    }

    // Returns generated public key and private keys
    public KeyPair GeneratePublicPrivateKey() throws NoSuchProviderException, NoSuchAlgorithmException, FileNotFoundException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);
        KeyPair pair = keyGen.generateKeyPair();

        return pair;
    }

    // main method
    public void ListExample(String args[]) throws Exception {

        int processNumber;

        int UnverifiedBlockPort;
        int BlockChainPort;

        if (args.length < 1) {
            processNumber = 0;
            PID = 0;
        }
        else if (args[0].equals("0")) {
            processNumber = 0;
            PID = 0;
        }
        else if (args[0].equals("1")) {
            processNumber = 1;
            PID = 1;
        }
        else if (args[0].equals("2")) {
            processNumber = 2;
            PID = 2;
        }
        else {
            processNumber = 0; // Handle invalid arguments
            PID = 0;
        }

        UnverifiedBlockPort = 4710 + processNumber;
        BlockChainPort = 4820 + processNumber;

        System.out.println("Process number: " + processNumber + " Ports: " + UnverifiedBlockPort + " " +
                BlockChainPort + "\n");

        // Read certain file based on the given number of processes
        switch(processNumber){
            case 1: FILENAME = "BlockInput1.txt"; break;
            case 2: FILENAME = "BlockInput2.txt"; break;
            default: FILENAME= "BlockInput0.txt"; break;
        }

        System.out.println("Using input file: " + FILENAME);

        // Read in blocks from file

        readBlocksFromFile(processNumber);

        if (blockRecordList==null) {
            throw new NullPointerException("block record list is null");
        }

        // Start our 3 separate threads

        new Thread(new PublicKeyServer()).start(); 			// Handle incoming public keys
        new Thread(new UnverifiedBlockServer(priorityQueue)).start(); 	// Handle incoming unverified blocks
        new Thread(new BlockchainServer( )).start(); 		// Handle incoming new blockchains
        Thread.sleep(1000);
        System.out.println("Test");

        // Generate public key and multicast
        multicastToPublicKeyServer();
        Thread.sleep(2000);
        // Multicast to unverified blocks
        multicastToUVBServer();
        Thread.sleep(2000); // Wait for public keys to finish before
        // multicastToBlockChainServer();
        new Thread(new UnverifiedBlockConsumer(priorityQueue)).start(); // Start processing unverified UVBs. Send unverified UVBs to started block chain server

        // 	Write the JSON object to a file:
        Thread.sleep(20000);
        System.out.println("Printing blocks to file: BlockchainLedger.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("BlockchainLedger.json")) {
            gson.toJson(fileOuput, writer);
        } catch (IOException e) {e.printStackTrace();}

        /*
        for (int i = 0; i < 4; i++) {
            ProcessBlock block = new ProcessBlock(i);
            KeyPair keyPair = GeneratePublicPrivateKey();
            if (keyPair == null){
                throw new NullPointerException("Key pair is null.");
            }
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            block.setPublicKey(publicKey);

            // Multicast public key to key server
            Socket keyServerSocket = new Socket(serverName, PublicKeyServerPortBase + i );
            PrintStream keyServerPrintStream = new PrintStream(keyServerSocket.getOutputStream());;
            keyServerPrintStream.println("Hello from process" + i);
            keyServerPrintStream.println(publicKey.toString());
            keyServerPrintStream.flush();

            // Read in response from key server
            InputStream inputStream = keyServerSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            while((read = inputStream.read(buffer)) != -1) {
                String output = new String(buffer, 0, read);
                System.out.println(output);
                System.out.flush();
            }
            keyServerSocket.close();
        }

        // Generate public/private keys
        /*
        KeyPair keyPair = GeneratePublicPrivateKey();
        if (keyPair == null){
            throw new NullPointerException("Key pair is null.");
        }
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Write our public key to a file
        WriteKeyToFile(publicKey.toString(), publicKeyFileName);
        // Read a public key from a file
        String publicKeyString = ReadKeyFromFile(publicKeyFileName);

        // Fake data to sign
        byte[] data = "This is test data".getBytes("UTF8");

        Signature sig = Signature.getInstance("SHA1WithDSA");
        sig.initSign(privateKey);
        sig.update(data);
        byte[] signatureBytes = sig.sign();
        System.out.println(Base64.getEncoder().encode(signatureBytes));

        // Verify the data with our public key
        sig.initVerify(publicKey);
        sig.update(data);
        System.out.println(sig.verify(signatureBytes));
        */
    }

    public static void main(String[] args) {
        BlockChainInput blockChainInput = new BlockChainInput(args);
        blockChainInput.run(args);
    }

    public void multicastToPublicKeyServer() throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        Socket socket;
        PrintStream toServer;
        String serverName = "localhost";

        for (int i = 0; i < numProcesses; i++) {
            // Generate public/private keys
            KeyPair keyPair = GeneratePublicPrivateKey();
            if (keyPair == null){
                throw new NullPointerException("Key pair is null.");
            }
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            socket = new Socket(serverName, KeyServerPort);
            toServer = new PrintStream(socket.getOutputStream());
            String data = publicKey.toString() + PID; // pass the process number along with the public key
            toServer.println(data);
            toServer.flush();
            socket.close();
        }
    }

    // Pass uvbs over the socket
    public void multicastToUVBServer() throws IOException {
        Socket socket;
        PrintStream toServer;
        String serverName = "localhost";

        BlockRecord tempRecord;

        String fakeBlockData;
        String timeFormat;
        String timeStampString;
        Date date;
        Random r = new Random();

        try{
            // TODO: For testing
            // Create fake block data and add it to Linked List
            /*
            for (int i=0; i < 4; i++) {
                BlockRecord tempBlock = new BlockRecord();
                fakeBlockData = "(Block#" + Integer.toString(((PID+1)*10)+i) + " from P"+ PID + ")";
                tempBlock.setData(fakeBlockData);

                date = new Date();
                timeFormat = String.format("%1$s %2$tF.%2$tT", "", date); // Create the TimeStamp string.
                timeStampString = timeFormat + "." + i;
                tempBlock.setTimeStamp(timeStampString);
                blockRecordList.add(tempBlock);
            }*/

            // Multicast the blocks read in from JSON to uvb server
            Collections.shuffle(blockRecordList);
            Iterator<BlockRecord> iterator = blockRecordList.iterator();

            // For each block in record list, multicast to UVB server
            while(iterator.hasNext()) {
                tempRecord = iterator.next();
                String blockRecordString = convertBlockToJSON(tempRecord);
                for (int i = 0; i < numProcesses; i++) {
                    socket = new Socket(serverName, UnverifiedBlockServerPort);
                    toServer = new PrintStream(socket.getOutputStream());
                    toServer.println(blockRecordString);
                    toServer.flush();
                    socket.close();
                    Thread.sleep(2000);
                }
            }

            Thread.sleep((r.nextInt(9) * 100)); // Sleep up to a second to randominze when sent.
        }catch (Exception x) {x.printStackTrace ();}

    }

    public void processBlocks(LinkedList<BlockRecord> recordList) {

        // 	Print out time stamps of all blocks
        BlockRecord tempRecord;
        Iterator<BlockRecord> iterator = recordList.iterator();
        while(iterator.hasNext()){
            tempRecord = iterator.next();
            System.out.println(tempRecord.getTimeStamp() + " " + tempRecord.getFname() + " " + tempRecord.getLname());
        }
        System.out.println("");

        // 	Shuffle blocks and to priority queue - demonstrates that blocks will still be ordered according to timestamp
        Collections.shuffle(recordList);
        iterator=recordList.iterator();
        System.out.println("Placing shuffled records in our priority queue...\n");
        while(iterator.hasNext()){
            priorityQueue.add(iterator.next());
        }

        // Get all blocks from priority queue
        while(true) {
            tempRecord = priorityQueue.poll(); 						// For consumer thread you'll want .take() which blocks while waiting.
            if (tempRecord == null) break;
            System.out.println(tempRecord.getTimeStamp() + " " + tempRecord.getFname() + " " + tempRecord.getLname());
        }

        System.out.println("\n\n");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // 	Convert the Java object to a JSON String:
        String json = gson.toJson(recordList);

        System.out.println("\nJSON (shuffled) String list is: " + json);

        // 	Write the JSON object to a file:
        try (FileWriter writer = new FileWriter("myBlockList.json")) {
            gson.toJson(recordList, writer);
        } catch (IOException e) {e.printStackTrace();}
    }

    public void readBlocksFromFile(int processNumber) {
        LinkedList<BlockRecord> recordList = new LinkedList<BlockRecord>();

        // Read in blocks from a given file name
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(FILENAME));
            String[] tokens;
            String InputLineStr;
            String uuidString;

            StringWriter stringWriter = new StringWriter();

            int n = 0;

            // Read in lines from file, parse block values, add the block to our list
            while ((InputLineStr = bufferedReader.readLine()) != null) {
                BlockRecord BlockRecord = new BlockRecord();

                // Sleep to avoid collisions
                try{
                    Thread.sleep(1001);
                }catch(InterruptedException e){}

                Date date = new Date();

                // Format the timestamp for the block record.
                // Blocks are ordered in the chain based on their timestamps.
                // We are formatting in a way to prevent timestamp collisions.
                String TimeStamp = String.format("%1$s %2$tF.%2$tT", "", date);
                String TimeStampString = TimeStamp + "." + processNumber; // Adding the process number prevents timestamp collisions
                System.out.println("Timestamp: " + TimeStampString);
                BlockRecord.setTimeStamp(TimeStampString);

                // Create unique block id
                uuidString = new String(UUID.randomUUID().toString());
                BlockRecord.setBlockID(uuidString);

                // Read in block record values and add to list of block records
                tokens = InputLineStr.split(" +");
                BlockRecord.setFname(tokens[FirstName]);
                BlockRecord.setLname(tokens[LastName]);
                BlockRecord.setSSNum(tokens[SSNUM]);
                BlockRecord.setDOB(tokens[DOB]);
                BlockRecord.setDiag(tokens[DIAG]);
                BlockRecord.setTreat(tokens[TREAT]);
                BlockRecord.setRx(tokens[RX]);
                BlockRecord.setTestString(tokens[TEST]);

                blockRecordList.add(BlockRecord);
                n++;
            }
            System.out.println(n + " records read." + "\n");

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    // Read in our public key for signing
    public String ReadKeyFromFile(String fileName) throws FileNotFoundException {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(fileName));

            // convert JSON file to map
            Map<?, ?> map = gson.fromJson(reader, Map.class);

            // print map entries
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if(entry.getKey().equals(publicKeyMapString)){
                    String publicKeyString = entry.getValue().toString();
                    System.out.println("Public key: " + publicKeyString);
                    return publicKeyString;
                }
            }

            // close reader
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        throw new NullPointerException("Public key not found in file:" + fileName);
    }

    public void readBlocksFromJson(String fileName) {

        // With help from: 	https://www.javainterviewpoint.com/read-json-java-jsonobject-jsonarray/
        // 					https://attacomsian.com/blog/gson-read-json-file
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(fileName));

            // convert JSON array to list of block records
            List<BlockRecord> records = new Gson().fromJson(reader, new TypeToken<List<BlockRecord>>() {}.getType());

            System.out.println("Printing block records read from file...");
            records.forEach(System.out::println);

            for(BlockRecord record : records) {
                System.out.println("Record first name: " + record.getFname());
            }

            reader.close();

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void run(String argv[]) {

        System.out.println("Running now\n");
        try {
            ListExample(argv);
        } catch (Exception x) {
            x.printStackTrace();
        };
    }

    // Verify if a process has signed a piece of data
    public static boolean VerifySignature(byte[] data, PublicKey publicKey, byte[] decode)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signer = Signature.getInstance("SHA1withDSA");
        signer.initVerify(publicKey);
        signer.update(data);
        return (signer.verify(decode));
    }

    public void WriteKeyToFile(String publicKey, String fileName){
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(publicKeyMapString, publicKey);

            Writer writer = new FileWriter(fileName);

            // convert map to JSON File
            new Gson().toJson(map, writer);

            // close the writer
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Making this class Serializable so it can be sent over sockets
    public class BlockRecord implements Serializable {

        // Add fields here to add more fields to read from text
        String BlockID;
        String TimeStamp;
        String VerificationProcessID;
        String PreviousHash; // We'll copy from previous block
        UUID uuid; // Just to show how JSON marshals this binary data.
        String firstName;
        String lastName;
        String SSNum;
        String DOB;
        String RandomSeed; // Our guess. Ultimately our winning guess.
        String WinningHash;
        String Diag;
        String Treat;
        String Rx;
        String test;
        String data;

        /* Examples of accessors for the BlockRecord fields: */
        public String getBlockID() {return BlockID;}
        public void setBlockID(String BID){this.BlockID = BID;}

        public String getTimeStamp() {return TimeStamp;}
        public void setTimeStamp(String TS){this.TimeStamp = TS;}

        public String getVerificationProcessID() {return VerificationProcessID;}
        public void setVerificationProcessID(String VID){this.VerificationProcessID = VID;}

        public String getPreviousHash() {return this.PreviousHash;}
        public void setPreviousHash (String PH){this.PreviousHash = PH;}

        public UUID getUUID() {return uuid;} // Later will show how JSON marshals as a string. Compare to BlockID.
        public void setUUID (UUID ud){this.uuid = ud;}

        public String getLname() {return lastName;}
        public void setLname (String LN){this.lastName = LN;}

        public String getFname() {return firstName;}
        public void setFname (String FN){this.firstName = FN;}

        public String getSSNum() {return SSNum;}
        public void setSSNum (String SS){this.SSNum = SS;}

        public String getDOB() {return DOB;}
        public void setDOB (String RS){this.DOB = RS;}

        public String getDiag() {return Diag;}
        public void setDiag (String D){this.Diag = D;}

        public String getTreat() {return Treat;}
        public void setTreat (String Tr){this.Treat = Tr;}

        public String getRx() {return Rx;}
        public void setRx (String Rx){this.Rx = Rx;}

        public String getRandomSeed() {return RandomSeed;}
        public void setRandomSeed (String RS){this.RandomSeed = RS;}

        public String getWinningHash() {return WinningHash;}
        public void setWinningHash (String WH){this.WinningHash = WH;}

        public String getTestString() {return test;}
        public void setTestString (String test){this.test = test;}

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    // Thread #1
    // Worker thread that handles incoming public keys
    class PublicKeyWorker extends Thread {
        Socket socket;

        // Holds incoming public keys and their associated processID
        Map<Integer, PublicKey> publicKeyMap = new HashMap<>();

        PublicKeyWorker (Socket socket) {this.socket = socket;}
        public void run(){
            try{
                System.out.println("In Public Key Worker");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String data = in.readLine ();
                System.out.println("Got data: " + data);
                socket.close();

            } catch (IOException x){
                x.printStackTrace();
            }
        }
    }

    // Start worker thread #1 to handle incoming public keys
    class PublicKeyServer implements Runnable {

        public void run(){
            int queueLength = 6;
            Socket socket;
            System.out.println("Starting Key Server input thread using " + Integer.toString(PublicKeyServerPortBase));
            try{
                ServerSocket serverSocket = new ServerSocket(PublicKeyServerPortBase, queueLength);
                while (true) {
                    socket = serverSocket.accept();
                    new PublicKeyWorker (socket).start();
                }
            }catch (IOException ioe) {System.out.println(ioe);}
        }
    }

    // From given utility code
    class UnverifiedBlockConsumer implements Runnable {

        PriorityBlockingQueue<BlockRecord> queue; // Passed from BC object.
        int PID;
        UnverifiedBlockConsumer(PriorityBlockingQueue<BlockRecord> queue){
            this.queue = queue;
        }

        public void run(){

            String data;
            BlockRecord tempRecord;
            PrintStream toBlockChainServer; // Send verified blocks to block chain server
            Socket blockChainSocket;    // used to send verified block chains to block chain server
            String verifiedBlock;       // String representing a verified block
            Random random = new Random();

            System.out.println("Starting the Unverified Block Priority Queue Consumer thread.\n");

            // Take block record from incoming queue. Do fake work and send verified blocks to blockchain.
            try {

                while(true) {
                    tempRecord = queue.take();
                    data = tempRecord.getData();

                    // Super simple fake work puzzle
                    int j;
                    for(int i=0; i<100; i++){
                        j = ThreadLocalRandom.current().nextInt(0,10);      // Get random number between 0 and 10 - we can make this puzzle harder by increasing the range
                        Thread.sleep((random.nextInt(9) * 100));                         // Sleep up to a second to randomize how much fake work is being done
                        if (j < 3) break;                                               // winning solution if random number is less than 3
                    }

                    // Block verification - ensure verified block does not contain the beginning substring of the current block's data
                    if(!blockchain.contains(data.substring(1, 9))){
                        verifiedBlock = "[" + data + " verified by P" + PID + " at time "
                                + Integer.toString(ThreadLocalRandom.current().nextInt(100,1000)) + "]\n";
                        String tempBlockChain = verifiedBlock + blockchain; // add the verified block to the chain

                        // Mulitcast verified block chain and send to block chain server to officially add to final block chain
                        for(int i=0; i < numProcesses; i++){ // Send to each process in group, including THIS process:
                            blockChainSocket = new Socket(serverName, BlockchainServerPort);
                            toBlockChainServer = new PrintStream(blockChainSocket.getOutputStream());
                            toBlockChainServer.println(tempBlockChain);
                            toBlockChainServer.flush();
                            blockChainSocket.close();
                            Thread.sleep(2000);  // Sleep to allow all processes to catch up
                        }
                    }
                    // Wait until block chain server gets all verified blocks before processing a new unverified block
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Start worker thread #2 to handle unverified blocks
    class UnverifiedBlockServer implements Runnable {

        // queue to hold unverified blocks
        BlockingQueue<BlockRecord> queue;
        UnverifiedBlockServer(BlockingQueue<BlockRecord> queue){
            this.queue = queue;
        }

        // Decide the order of UVBs
        public Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>()
        {
            @Override
            public int compare(BlockRecord b1, BlockRecord b2)
            {
                String s1 = b1.getTimeStamp();
                String s2 = b2.getTimeStamp();
                if (s1 == s2) {return 0;}
                if (s1 == null) {return -1;}
                if (s2 == null) {return 1;}
                return s1.compareTo(s2);
            }
        };

        // Start UVB server and open port to receive Unverified Blocks
        public void run(){
            int queueLength = 6;
            Socket socket;
            System.out.println("Starting the Unverified Block Server input thread using " +
                    Integer.toString(UnverifiedBlockServerPort));
            try{
                ServerSocket UVBServer = new ServerSocket(UnverifiedBlockServerPort, queueLength);
                while (true) {
                    // Open socket connection to accept new unverified blocks
                    socket = UVBServer.accept();
                    new UnverifiedBlockWorker(socket).start();
                }
            }catch (IOException ioe) {System.out.println(ioe);}
        }

        // Inner class to get access to block record queue
        // Thread #2 - This working thread class receives unverified blocks and puts them on the priority queue.
        class UnverifiedBlockWorker extends Thread {
            Socket socket;

            UnverifiedBlockWorker (Socket socket) {
                this.socket = socket;
            }

            // Read in UVB and put them on queue
            public void run(){
                System.out.println("In Unverified Block Worker");
                String inputString;

                try{
                    Gson gson = new Gson();
                    StringBuffer stringBuffer = new StringBuffer(); // To rebuild json from a string
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Keep reading input to read full json value
                    while ((inputString = input.readLine()) != null) {
                        stringBuffer.append(inputString);
                    }
                    // Create block record from read-in json
                    BlockRecord blockRecord = gson.fromJson(stringBuffer.toString(), BlockRecord.class);
                    System.out.println("Recieved UVB: " + blockRecord.getTimeStamp() + " " + blockRecord.Diag + " " + blockRecord.Treat);
                    queue.put(blockRecord); // Put unverified block on queue to be consumed later
                    socket.close();
                } catch (Exception x){x.printStackTrace();}
            }
        }
    }

    // Thread #3 - Handles incoming alternative blockchains.
    // Compare to our existing blockchain and replace if the new one is more valid than our current.
    class BlockchainWorker extends Thread {
        Socket socket;

        BlockchainWorker (Socket socket) {this.socket = socket;}
        public void run(){
            try{

                System.out.println("In Blockchain Worker");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String blockData = "";
                String blockDataIn;
                while((blockDataIn = in.readLine()) != null){
                    blockData = blockData + "\n" + blockDataIn;
                }
                blockchain = blockData;
                System.out.println("         --NEW BLOCKCHAIN--\n" + blockchain + "\n\n");
                fileOuput.add("         --NEW BLOCKCHAIN--\n" + blockchain + "\n\n"); // Add data to be output to file
                socket.close();
                Thread.sleep(2000);

            } catch (IOException | InterruptedException x){x.printStackTrace();}
        }
    }

    // Thread #3 - Adds valid blocks to the blockchain
    class BlockchainServer implements Runnable {
        public void run(){
            int queueLength = 6;
            Socket socket;
            System.out.println("Starting the Blockchain server input thread using " + BlockchainServerPort);
            try{
                ServerSocket servsock = new ServerSocket(BlockchainServerPort, queueLength);
                while (true) {
                    socket = servsock.accept();
                    new BlockchainWorker (socket).start();
                }
            }catch (IOException ioe) {System.out.println(ioe);}
        }
    }

    // Would normally keep a process block for each process in the multicast group:
    class ProcessBlock{
        int processID;
        PublicKey publicKey;
        int port;
        String IPAddress;

        public ProcessBlock(int processID){
            this.processID = processID;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(PublicKey pubKey) {
            this.publicKey = pubKey;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getIPAddress() {
            return IPAddress;
        }

        public void setIPAddress(String IPAddress) {
            this.IPAddress = IPAddress;
        }

    }
}
