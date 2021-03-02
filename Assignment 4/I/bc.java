/* 2020-10-04

bc.java for BlockChain

Dr. Clark Elliott for CSC435
Copyright (C) 2020 by Clark Elliott with all rights reserved

java 1.8.0_181

This is some quick sample code giving a simple example framework for coordinating multiple processes in a blockchain group.


INSTRUCTIONS:

Set the numProceses class variable (e.g., 1,2,3), and use a batch file to match it.

AllStart.bat:

REM for three procesess:
start java bc 0
start java bc 1
java bc 2

You might want to start with just one process to see how it works. I've run it with five processes.

Thanks: http://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example

Sample output is at the bottom of this file.

All [three] processes run the same in this consortium, each in its own terminal window. Control-C to stop.

We start three servers listening for incoming connections:

Public Key server -- accept the public keys of all processes (including THIS process)
Unverified Block (UVB) server -- accept the sample simple unverified blocks from all processes (including THIS process)
Updated Blockchain server -- accept updated blockchains from all processes (including THIS process)

UVBs are placed in a priority queue (by timestamp of time created). They are removed by a consumer, which verifies
the blocks, adds them to the blockchain and multicasts the new blockchain.

WORK in this example is fake -- just sleeping for a random amount of time.

Included as a tool for your toolbox is the sending of BlockRecord objects through streaming data over sockets.

The UVB queue must be thread-safe for concurrent access because multiple UVB workers act as concurrent producers (into
the queue) and the UVB consumer is also operating concurrently. Each is in its own thread access the same queue.

In theory, this should work for any number of processes. But, make sure your priority queue is big enough for all
the data.

Sleep statements are used as a simple way to let servers settle before clients connect to them. Also random sleep statements
are used to introduce intresting interactions and simulate UVBs appearing from many sources at different times.

This example is to illustrate process coordination, and does not contain any actual chaining of the blocks.

I've left in many commented out print statements you can use to see further details if you like.

*/

package com.company;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

// Would normally keep a process block for each process in the multicast group:
/* class ProcessBlock{
  int processID;
  PublicKey pubKey;
  int port;
  String IPAddress;
  // etc.
  } */

// Ports will incremented by 1000 for each additional process added to the multicast group:
class Ports{
    public static int KeyServerPortBase = 6050;
    public static int UnverifiedBlockServerPortBase = 6051;
    public static int BlockchainServerPortBase = 6052;

    public static int KeyServerPort;
    public static int UnverifiedBlockServerPort;
    public static int BlockchainServerPort;

    public void setPorts(){
        KeyServerPort = KeyServerPortBase + (bc.PID * 1000);
        UnverifiedBlockServerPort = UnverifiedBlockServerPortBase + (bc.PID * 1000);
        BlockchainServerPort = BlockchainServerPortBase + (bc.PID * 1000);
    }
}

class BlockRecord implements Serializable { // Because here we send this object over socket stream, must be Serializable
    String TimeStamp;
    String Data;
    public String getTimeStamp() {return TimeStamp;}
    public void setTimeStamp(String TS){this.TimeStamp = TS;}
    public String getData() {return Data;}
    public void setData(String DATA){this.Data = DATA;}
}


class PublicKeyWorker extends Thread { // Worker thread to process incoming public keys
    Socket keySock; // Class member, socket, local to Worker.
    PublicKeyWorker (Socket s) {keySock = s;} // Constructor, assign arg s to local sock
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(keySock.getInputStream()));
            String data = in.readLine ();
            System.out.println("Got key: " + data);
            keySock.close();
        } catch (IOException x){x.printStackTrace();}
    }
}

class PublicKeyServer implements Runnable {
    //public ProcessBlock[] PBlock = new ProcessBlock[3]; // Typical would be: One block to store info for each process.

    public void run(){
        int q_len = 6;
        Socket keySock;
        System.out.println("Starting Key Server input thread using " + Integer.toString(Ports.KeyServerPort));
        try{
            ServerSocket servsock = new ServerSocket(Ports.KeyServerPort, q_len);
            while (true) {
                keySock = servsock.accept();
                new PublicKeyWorker (keySock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}

class UnverifiedBlockServer implements Runnable {
    BlockingQueue<BlockRecord> queue;
    UnverifiedBlockServer(BlockingQueue<BlockRecord> queue){
        this.queue = queue; // Constructor binds our prioirty queue to the local variable.
    }

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


    /* Inner class to share priority queue. We are going to place the unverified blocks (UVBs) into this queue in the order
       we get them, but they will be retrieved by a consumer process sorted by TimeStamp of when created. */

    class UnverifiedBlockWorker extends Thread { // Receive a UVB and put it into the shared priority queue.
        Socket sock; // Class member, socket, local to Worker.
        UnverifiedBlockWorker (Socket s) {sock = s;} // Constructor, assign arg s to local sock
        BlockRecord BR = new BlockRecord();

        public void run(){
            // System.out.println("In Unverified Block Worker");
            try{
                ObjectInputStream unverifiedIn = new ObjectInputStream(sock.getInputStream());
                BR = (BlockRecord) unverifiedIn.readObject(); // Read in the UVB as an object
                System.out.println("Received UVB: " + BR.getTimeStamp() + " " + BR.getData());
                queue.put(BR); // Note: make sure you have a large enough blocking priority queue to accept all the puts
                sock.close();
            } catch (Exception x){x.printStackTrace();}
        }
    }

    public void run(){ // Start up the Unverified Block Receiving Server
        int q_len = 6; /* Number of requests for OpSys to queue */
        Socket sock;
        System.out.println("Starting the Unverified Block Server input thread using " +
                Integer.toString(Ports.UnverifiedBlockServerPort));
        try{
            ServerSocket UVBServer = new ServerSocket(Ports.UnverifiedBlockServerPort, q_len);
            while (true) {
                sock = UVBServer.accept(); // Got a new unverified block
                // System.out.println("Got connection to UVB Server.");
                new UnverifiedBlockWorker(sock).start(); // So start a thread to process it.
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}


/* We have received unverified blocks into a thread-safe concurrent access queue. For this example, we retrieve them
   in order according to their TimeStamp of when created. It must be concurrent safe because two or more threads modifiy it
   "at once," (mutiple worker threads to add to the queue, and a consumer thread to remove from it).*/

class UnverifiedBlockConsumer implements Runnable {
    PriorityBlockingQueue<BlockRecord> queue; // Passed from BC object.
    int PID;
    UnverifiedBlockConsumer(PriorityBlockingQueue<BlockRecord> queue){
        this.queue = queue; // Constructor binds our prioirty queue to the local variable.
    }

    public void run(){
        String data;
        BlockRecord tempRec;
        PrintStream toBlockChainServer;
        Socket BlockChainSock;
        String newblockchain;
        String fakeVerifiedBlock;
        Random r = new Random();

        System.out.println("Starting the Unverified Block Priority Queue Consumer thread.\n");
        try{
            while(true){ // Consume from the incoming UVB queue. Do the (fake here) work to verify. Mulitcast new blockchain
                tempRec = queue.take(); // Pop the next BlockRecord from the queue. Will blocked-wait on empty queue
                data = tempRec.getData(); // Could capture TimeStamp too if you want to know when created.
                // System.out.println("Consumer got unverified: " + data);

	/* Ordindarily we would do real work here to verify the UVB. Also, we would periodically want
	   to check to see whether some other proc has already verified this UVB. If so, stop the work and start
	   again on the next UVB in the queue. */

                int j; // Here we fake doing some work (That is, here we could cheat, so not ACTUAL work...)

                for(int i=0; i< 100; i++){ // put a limit on the fake work for this example
                    j = ThreadLocalRandom.current().nextInt(0,10);
                    Thread.sleep((r.nextInt(9) * 100)); // Sleep up to a second to randominze how much fake work
                    if (j < 3) break; // <- how hard our fake work is; about 1.5 seconds.
                }

	/* With duplicate blocks that have already been verified by different procs and placed in the blockchain,
	   ordinarily we would keep only the one with the lowest verification timestamp. For the exmple we use a
	   crude filter, which also may let some dups through */
                // if(bc.blockchain.indexOf(data.substring(1, 9)) > 0){System.out.println("Duplicate: " + data);}

                if(bc.blockchain.indexOf(data.substring(1, 9)) < 0){ // Crude, but excludes most duplicates.
                    fakeVerifiedBlock = "[" + data + " verified by P" + bc.PID + " at time "
                            + Integer.toString(ThreadLocalRandom.current().nextInt(100,1000)) + "]\n";
                    // System.out.print("Fake verified block: " + fakeVerifiedBlock);
                    String tempblockchain = fakeVerifiedBlock + bc.blockchain; // add the verified block to the chain

                    for(int i=0; i < bc.numProcesses; i++){ // Send to each process in group, including THIS process:
                        BlockChainSock = new Socket(bc.serverName, Ports.BlockchainServerPortBase + (i * 1000));
                        toBlockChainServer = new PrintStream(BlockChainSock.getOutputStream());
                        toBlockChainServer.println(tempblockchain); toBlockChainServer.flush();
                        BlockChainSock.close();
                    }
                }
                Thread.sleep(1500); // For the example, wait for our blockchain to be updated before processing a new block
            }
        }catch (Exception e) {System.out.println(e);}
    }
}

// Incoming proposed replacement blockchains. Ordinarily, we would compare to existing. Replace only if winner:

class BlockchainWorker extends Thread { // Class definition
    Socket sock; // Class member, socket, local to Worker.
    BlockchainWorker (Socket s) {sock = s;} // Constructor, assign arg s to local sock
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String blockData = "";
            String blockDataIn;
            while((blockDataIn = in.readLine()) != null){
                blockData = blockData + "\n" + blockDataIn; // Add crlf to make pretty output
            }
            bc.blockchain = blockData; // Would normally have to check first for winner blockchain before replacing.
            System.out.println("         --NEW BLOCKCHAIN--\n" + bc.blockchain + "\n\n");
            sock.close();
        } catch (IOException x){x.printStackTrace();}
    }
}

class BlockchainServer implements Runnable {
    public void run(){
        int q_len = 6; /* Number of requests for OpSys to queue */
        Socket sock;
        System.out.println("Starting the Blockchain server input thread using " + Integer.toString(Ports.BlockchainServerPort));
        try{
            ServerSocket servsock = new ServerSocket(Ports.BlockchainServerPort, q_len);
            while (true) {
                sock = servsock.accept();
                new BlockchainWorker (sock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }
}

/* Class bc for BlockChain. Declare the shared, thread-safe UVB queue. Multicast the public
   key. Create sample UVBs and multicast them.. Consume the UVBs as we perform verification on
   them with WORK. Create a new BlockChain when we verify a UVB. Multicast the new blockchain
   to other processes.
*/

public class bc {
    static String serverName = "localhost";
    static String blockchain = "[First block]";
    static int numProcesses = 3; // Set this to match your batch execution file that starts N processes with args 0,1,2,...N
    static int PID = 0; // Our process ID
    LinkedList<BlockRecord> recordList = new LinkedList<BlockRecord>();

    // This queue of UVBs must be concurrent because it is shared by producer threads and the consumer thread
    final PriorityBlockingQueue<BlockRecord> ourPriorityQueue = new PriorityBlockingQueue<>(100, BlockTSComparator);

    public static Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>()
    {
        @Override
        public int compare(BlockRecord b1, BlockRecord b2)
        {
            //System.out.println("In comparator");
            String s1 = b1.getTimeStamp();
            String s2 = b2.getTimeStamp();
            if (s1 == s2) {return 0;}
            if (s1 == null) {return -1;}
            if (s2 == null) {return 1;}
            return s1.compareTo(s2);
        }
    };

    public void KeySend (){ // Multicast our public key to the other processes
        Socket sock;
        PrintStream toServer;
        try{
            for(int i=0; i< numProcesses; i++){// Send our public key to all servers.
                sock = new Socket(serverName, Ports.KeyServerPortBase + (i * 1000));
                toServer = new PrintStream(sock.getOutputStream());
                toServer.println("FakeKeyProcess" + bc.PID); toServer.flush();
                sock.close();
            }
        }catch (Exception x) {x.printStackTrace ();}
    }

    /* Create some simple Unverified Blocks (TimeStamp and some simple data) -- UVBs. Multicast to all the processes.

     We include the sending of an Object over a socket connection as another tool (the BlockRecord object).
     Be sure that the object (in this case the BlockRecord) is declared as Serializable.
    */
    public void UnverifiedSend (){ // Multicast some unverified blocks to the other processes

        Socket UVBsock; // Will be client connection to the Unverified Block Server for each other process.
        BlockRecord tempRec;

        String fakeBlockData;
        String T1;
        String TimeStampString;
        Date date;
        Random r = new Random();

        //  Thread.sleep(1000); // wait for public keys to settle, normally would wait for an ack that it was received.
        try{
            for (int i=0; i < 4; ++i){
                BlockRecord BR = new BlockRecord();
                fakeBlockData = "(Block#" + Integer.toString(((bc.PID+1)*10)+i) + " from P"+ bc.PID + ")";
                BR.setData(fakeBlockData);
                date = new Date();
                T1 = String.format("%1$s %2$tF.%2$tT", "", date); // Create the TimeStamp string.
                TimeStampString = T1 + "." + i; // Use process num extension. No timestamp collisions!
                // System.out.println("Timestamp: " + TimeStampString);
                BR.setTimeStamp(TimeStampString); // Will be able to priority sort by TimeStamp
                recordList.add(BR);
            }
            Collections.shuffle(recordList); // Shuffle the list to later demonstrate how the priority queue sorts them.

            Iterator<BlockRecord> iterator = recordList.iterator();
	    /* // In case you want to see the shuffled version.
      while(iterator.hasNext()){
	tempRec = iterator.next();
	System.out.println(tempRec.getTimeStamp() + " " + tempRec.getData());
      }
      System.out.println("");
	    */

            ObjectOutputStream toServerOOS = null; // Stream for sending Java objects
            for(int i = 0; i < numProcesses; i++){// Send some sample Unverified Blocks (UVBs) to each process
                System.out.println("Sending UVBs to process " + i + "...");
                iterator = recordList.iterator(); // We saved our samples in a list, restart at the beginning each time.
                while(iterator.hasNext()){
                    // Client connection. Triggers Unverified Block Worker in other process's UVB server:
                    UVBsock = new Socket(serverName, Ports.UnverifiedBlockServerPortBase + (i * 1000));
                    toServerOOS = new ObjectOutputStream(UVBsock.getOutputStream());
                    Thread.sleep((r.nextInt(9) * 100)); // Sleep up to a second to randominze when sent.
                    tempRec = iterator.next();
                    // System.out.println("UVB TempRec for P" + i + ": " + tempRec.getTimeStamp() + " " + tempRec.getData());
                    toServerOOS.writeObject(tempRec); // Send the unverified block record object
                    toServerOOS.flush();
                    UVBsock.close();
                }
            }
            Thread.sleep((r.nextInt(9) * 100)); // Sleep up to a second to randominze when sent.
        }catch (Exception x) {x.printStackTrace ();}
    }

    public static void main(String args[]) {
        bc s = new bc();
        s.run(args); // Break out of main to avoid static reference conflicts.
    }

    /* Set up the PID and port number schemes. Start the three servers for incoming public keys, UVBs and
     BlockChains. Multicast the public key and the sample UVBs. Start consuming the UBVs, verifying them
     and adding them to the blockchain. */

    public void run(String args[]) {
        System.out.println("Running now\n");
        // int q_len = 6; /* Number of requests for OpSys to queue. Not interesting. */
        PID = (args.length < 1) ? 0 : Integer.parseInt(args[0]); // Process ID is passed to the JVM
        System.out.println("Clark Elliott's Block Coordination Framework. Use Control-C to stop the process.\n");
        System.out.println("Using processID " + PID + "\n");
        new Ports().setPorts(); // Establish OUR port number scheme, based on PID

        new Thread(new PublicKeyServer()).start(); // New thread to process incoming public keys
        new Thread(new UnverifiedBlockServer(ourPriorityQueue)).start(); // New thread to process incoming unverified blocks
        new Thread(new BlockchainServer()).start(); // New thread to process incomming new blockchains
        try{Thread.sleep(1000);}catch(Exception e){} // Wait for servers to start.
        KeySend();
        try{Thread.sleep(1000);}catch(Exception e){}
        new bc().UnverifiedSend(); // Multicast some new unverified blocks out to all servers as data
        try{Thread.sleep(1000);}catch(Exception e){} // Wait for multicast to fill incoming queue for our example.
        new Thread(new UnverifiedBlockConsumer(ourPriorityQueue)).start(); // Start consuming the queued-up unverified blocks
    }
}


/* Sample output for each of three consoles:

Running now
Clark Elliott's BlockFramework. Use Control-C to stop the process.
Using processID 2

Starting Key Server input thread using 8050
Starting the Blockchain server input thread using 8052
Starting the Unverified Block Server input thread using 8051
Got key: FakeKeyProcess2
Got key: FakeKeyProcess1
Got key: FakeKeyProcess0
Sending UVBs to process 0...
Sending UVBs to process 1...
Sending UVBs to process 2...
Received UVB:  2020-10-04.21:46:42.0 (Block#30 from P2)
Received UVB:  2020-10-04.21:46:42.1 (Block#21 from P1)
Received UVB:  2020-10-04.21:46:42.3 (Block#13 from P0)

[... CUTS ...]

         --NEW BLOCKCHAIN--

[(Block#33 from P2) verified by P0 at time 272]
[(Block#23 from P1) verified by P1 at time 650]
[(Block#13 from P0) verified by P1 at time 529]
[(Block#32 from P2) verified by P1 at time 861]
[(Block#22 from P1) verified by P2 at time 465]
[(Block#12 from P0) verified by P1 at time 794]
[(Block#11 from P0) verified by P2 at time 223]
[(Block#21 from P1) verified by P2 at time 838]
[(Block#31 from P2) verified by P1 at time 550]
[(Block#20 from P1) verified by P2 at time 419]
[(Block#10 from P0) verified by P2 at time 411]
[(Block#30 from P2) verified by P1 at time 164]
[First block]

*/