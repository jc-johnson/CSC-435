package main;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

public class BlockChainInput {
	
	public BlockChainInput (String argv[]) {
	    System.out.println("In the constructor...");
	}	

	private static String FILENAME;
	
	// Priority queue for our blocks 
	private Queue<BlockRecord> priorityQueue = new PriorityQueue<>(4, BlockTSComparator);
	
	/* Token indexes for input: */
	private static final int FirstName = 0;
	private static final int LastName = 1;
	private static final int iDOB = 2;
	private static final int iSSNUM = 3;
	private static final int iDIAG = 4;
	private static final int iTREAT = 5;
	private static final int iRX = 6;

	public static void main(String[] args) {
		BlockChainInput blockChainInput = new BlockChainInput(args);
		blockChainInput.run(args);
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
    
    class BlockRecord {
		
		  /* Examples of block fields. You should pick, and justify, your own set: */
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

		}

}
