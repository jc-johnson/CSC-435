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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class Main {

    public static void main(String[] args) {
	// write your code here
    }
}
