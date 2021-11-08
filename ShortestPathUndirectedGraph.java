import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class ShortestPathUndirectedGraph {
    public static int pis_in_network = 0;

    // Driver Program
    public static void main(String args[]) throws Exception
    {
        
        //Store user input file in textFile variable
        File textFile = new File("userNetworkInput.txt");

        /*Logic to parse file, insert into graph function in new.gv file. Then executes command 
        to open png file*/
        writeToGVFile(textFile);
        openPngFile();
        

        /*Logic to read user text file, display a png image of graph, and confirm with 
        user that this is the graph they intended*/
        Scanner sc = new Scanner(System.in);
        char choice;

        System.out.println();
        System.out.println("-------------------------------------------");
        do{
            System.out.println("Please view the image of the graph");
            Thread.sleep(500);
            System.out.println("Proceed with node configuration? Confirm[Y/N]: ");

            choice = sc.next().charAt(0);
            choice = Character.toUpperCase(choice);
        }while(choice != 'Y' && choice != 'N');


        switch(choice){
            case 'N' : System.out.println("Please edit text file with desired topology. Then, restart program");
                System.exit(0);
                break;
            case 'Y' : System.out.println("Proceeding with program");
                break;
        }
      

        
        //Scan textfile 
        Scanner scan = new Scanner(textFile);
        //Number of nodes is the first integer in file
        int nodes = scan.nextInt();
        //Set number of pi's in network equal to nodes
        //pi's in network is a global variable for other classes
        pis_in_network = nodes;
        

        // Number of vertices is nodes + 1
        int v = nodes + 1; //6

 
        // Adjacency list for storing which vertices are connected
        //i.e. stores all of pi 1's connections, pi 2's connections...etc.
        /** Ex.  [ [x,x,x,...], [x,x,x,...], [x,x,x,...], ...] */
        ArrayList<ArrayList<Integer>> adj = new ArrayList<ArrayList<Integer>>(v);
        //add empty array list in each arraylist index
        // Ex. [ [], [], [], ...]
        for (int i = 0; i < v; i++) {
            adj.add(new ArrayList<Integer>());
        }
 
        
        //Scan textFile until there are no more integers to read
        while(scan.hasNextInt()){
            // Creating graph given in the diagram.
            // add_edge function takes adjacency list, source
            // and destination vertex as argument and forms
            // an edge between them.
                        //source         //destination
            addEdge(adj, scan.nextInt(), scan.nextInt());
        }
       
        int source, dest;
        //Will store the name of the output file to print each PI's paths to
        String piPathsFile = "";
    
        //amount of config files to expect (amount of piPathFiles)
        int amount_of_config_files = 0;     

        //find each possible path to any given PI
        System.out.println("...Finding shortest route paths...");
        //Nested for loops collects all of Pi 1's connection to every other pi, then Pi 2's connections ...etc.
        for(int i = 0; i < v-1; i++ ) {
            //The name of piPathsFile is dependent on which pi we are currently configuring
            //piPathsFile1, piPathsFile2, ...etc.
            piPathsFile = "piPathsFile" + (i+1);
            amount_of_config_files++;
            //Will write to the current piPathsFile we are currently configuring
            FileWriter path_writer = new FileWriter(piPathsFile);
            for(int j = 0; j < v; j++){
                //Number of starting PI
                source = i+1; //i+1 because we do not have a PI with the number 0, all PI's are labeled 1-n
                //Number of destination PI
                dest = j+1; //j+1 because we do not have a PI with the number 0, all PI's are labeled 1-n
                //Will find shortest distance between source and destination pi and write to config file
                printShortestDistance(adj, source, dest, v, path_writer);
            }
            path_writer.close(); //close after we are done writing to file
        }

        scan.close();
       
        //Now that we have all paths stored in individual pi's config files, we can create 
        //a script for each PI
        createScriptsForEachPi(amount_of_config_files);


        /**====================================================
         * ====================================================
         * ==========Script to run tests on routes=============
         * ====================================================
         */

        //Ask user if they would like to run test on route 
        char test_route_choice;

        System.out.println();
        System.out.println("-------------------------------------------");
        
        do{
            Thread.sleep(500);
            System.out.println("Would you like to run tests on a route?[Y/N]: ");

            sc.nextLine();
            test_route_choice = sc.next().charAt(0);
            test_route_choice = Character.toUpperCase(test_route_choice);

            if(test_route_choice == 'N'){
                System.out.println("Node configuration complete. Program terminated.");
                System.exit(0);
            }
            else if(test_route_choice == 'Y'){
                getUserInputRouteToTest(sc);
            }
        }while(test_route_choice == 'Y' || test_route_choice != 'Y');
    }

   
    
    //function to ask user which route we will be testing and fetching data from 
    //then make a script to ssh into
    public static void getUserInputRouteToTest(Scanner sc) throws Exception{  
        
        sc.nextLine();
        //get route user wants to test 
        int source_node = 0;
        int dest_node = 0;
        System.out.println("Enter a route to test in the following format: s,d. Where s is the number of the source node and d is the number of the destination node.");
        source_node = sc.nextInt();
        dest_node = sc.nextInt();

        System.out.println("Running test on route " + source_node + "--" + dest_node);

        //runRouteTests(source_node, dest_node);
        runProcess("chmod 777 ./Non-deletables/pingScript1.sh");
        runProcess("./Non-deletables/pingScript1.sh");
        getPingData(source_node, dest_node);
    }

    //This function calls getPingData, getTracerouteData, and getScpData
    public static void runRouteTests(int source_node, int dest_node) throws Exception{
        getPingData(source_node, dest_node);
    }

    
    //function to run ping command, pipe data into output file, search file for times and save
    //to .dat file which gnuplot will read from
    public static void getPingData(int source_node, int dest_node) throws Exception{
        //Create the script to ping and redirect output into a text file 
        // runProcess("touch pingScript.sh");
        // runProcess("chmod 777 ./pingScript.sh");
        
        // //write to ping script
        // FileWriter ping_script_writer = new FileWriter("pingScript.sh");
        // //ping script
        // ping_script_writer.write("#!/bin/bash");
        // ping_script_writer.write("\nping 10.0.0." + dest_node + " -c 10 > pingOutput.txt");
        // ping_script_writer.close();

        // //run ping script from source node 
        // runProcess("touch sshingScriptForPi" + source_node + ".sh");
        // //write to sshingScript
        // FileWriter sshing_script_writer = new FileWriter("sshingScriptForPi" + source_node + ".sh");
        // sshing_script_writer.write("#!/bin/bash\n");
        // sshing_script_writer.write("cd ~\n");
        // sshing_script_writer.write("ssh epharra" + source_node + "@10.0.0." + source_node + " 'bash -s' < Projects/NetworkingResearch/pingScript.sh");
        
        // sshing_script_writer.close();

        // runProcess("./sshingScriptForPi" + source_node + ".sh");


        // Create file that gnuplot will read from
        runProcess("touch pingData.dat");

        //Read from pingOutput.txt file
        File textFile = new File("pingOutput.txt");
        Scanner sc = new Scanner(textFile);
        Scanner sc_for_avg = new Scanner(textFile);

        // Write to pingData.dat, which is the file gnuplot will graph from
        FileWriter ping_writer = new FileWriter("pingData.dat");
        String nextLine = "";
        String avgRttLine = "";
        int currentLineNumber = 0;

        //Scan file and find average rtt 
        while(sc_for_avg.hasNextLine()){
            nextLine = sc_for_avg.nextLine();
            currentLineNumber++;
            if(currentLineNumber == 15){
                avgRttLine = nextLine.substring(39, 45);
            }
        }
        sc_for_avg.close();

        currentLineNumber = 0;
        //Insert data into pingData.dat line by line
        while(sc.hasNextLine()){
            
            nextLine = sc.nextLine();
            currentLineNumber++;
            if(currentLineNumber >=2 && currentLineNumber < 12){
                if(currentLineNumber != 11){
                    ping_writer.write(currentLineNumber-1 + "\t" + nextLine.substring(47,53) + "\t" + avgRttLine + "\n");
                    
                }
                else{
                    ping_writer.write(currentLineNumber-1 + "\t" + nextLine.substring(47,53) + "\t" + avgRttLine);
                }
            }
        }

         sc. close();
         ping_writer.close();

         //run gnuplot script reading from pingData.dat file
         runProcess("gnuplot -p ./Non-deletables/gnuplotPingScript.gp");

    }
 
    //function to create png file, graphviz command to create graph image, then opens new.png file
    public static void openPngFile() throws Exception{
        runProcess("neato -Tpng new.gv -o new.png");
        Thread.sleep(1000);
        runProcess("open new.png");
    }
    //function to create new.gv file, write to file in the necessary "a--b" format
    //and opens resulting png image
    public static void writeToGVFile(File textFile) throws Exception{
        //Scanner object to read from textFile
        Scanner graph_file_scanner = new Scanner(textFile);
        //Create new.gv file
        runProcess("touch new.gv");
        //File writer to new.gv file
        FileWriter graph_writer = new FileWriter("new.gv");

        //Write first line of new.gv 
        graph_writer.write("graph foo {");

        //Scan first int which is the # of nodes. We don't need to do anything with this
        graph_file_scanner.nextInt();

        //Start scanning remainder of text file
        while(graph_file_scanner.hasNextInt()){
            graph_writer.write("\n");
            graph_writer.write("\t" + graph_file_scanner.nextInt() + " -- " + graph_file_scanner.nextInt() + ";");
        }
        graph_writer.write("\n}");
        
        graph_file_scanner.close();
        graph_writer.close();
    }
    // function to form edge between two vertices
    // source and dest
    private static void addEdge(ArrayList<ArrayList<Integer>> adj, int i, int j)
    {
        //Source pi stores edge to destination pi
        adj.get(i).add(j);
        //Destination pi stores edge to source pi
        adj.get(j).add(i);
    }
 
    // function to print the shortest distance and path
    // between source vertex and destination vertex
    private static void printShortestDistance(
                     ArrayList<ArrayList<Integer>> adj,
                             int s, int dest, int v, FileWriter path_writer) throws IOException
    {
        // predecessor[i] array stores predecessor of
        // i and distance array stores distance of i
        // from s
        int pred[] = new int[v];
        int dist[] = new int[v];
  
        //If BFS is false, then source and destination are NOT connected in network
        if (BFS(adj, s, dest, v, pred, dist) == false) {
            return;
        }
 
        // LinkedList to store path
        LinkedList<Integer> path = new LinkedList<Integer>();
        int crawl = dest;
        //Each node we visit to get to destination is added to path
        path.add(crawl);
        //-1 means we have not visited node, while any other integer means we have
        while (pred[crawl] != -1) {
            //Add node to path if we have visited
            path.add(pred[crawl]);
            crawl = pred[crawl];
        }
 
       //Write each PI's path to their respective PI config files
        for (int i = path.size() - 1; i >= 0; i--) {
            path_writer.write(path.get(i) + " ");
        }
        //New line for a new path
        path_writer.write("\n");
    }
 
    // a modified version of BFS that stores predecessor
    // of each vertex in array pred
    // and its distance from source in array dist
    private static boolean BFS(ArrayList<ArrayList<Integer>> adj, int src,
                                  int dest, int v, int pred[], int dist[])
    {
        // a queue to maintain queue of vertices whose
        // adjacency list is to be scanned as per normal
        // BFS algorithm using LinkedList of Integer type
        LinkedList<Integer> queue = new LinkedList<Integer>();
 
        // boolean array visited[] which stores the
        // information whether ith vertex is reached
        // at least once in the Breadth first search
        boolean visited[] = new boolean[v];
 
        // initially all vertices are unvisited
        // so v[i] for all i is false
        // and as no path is yet constructed
        // dist[i] for all i set to infinity
        for (int i = 0; i < v; i++) {
            visited[i] = false;
            dist[i] = Integer.MAX_VALUE;
            pred[i] = -1;
        }
 
        // now source is first to be visited and
        // distance from source to itself should be 0
        visited[src] = true;
        dist[src] = 0;
        queue.add(src);
 
        // bfs Algorithm
        while (!queue.isEmpty()) {
            int u = queue.remove();
            for (int i = 0; i < adj.get(u).size(); i++) {
                if (visited[adj.get(u).get(i)] == false) {
                    visited[adj.get(u).get(i)] = true;
                    dist[adj.get(u).get(i)] = dist[u] + 1;
                    pred[adj.get(u).get(i)] = u;
                    queue.add(adj.get(u).get(i));
 
                    // stopping condition (when we find
                    // our destination)
                    if (adj.get(u).get(i) == dest)
                        return true;
                }
            }
        }
        return false;
    }


    public static void createScriptsForEachPi(int amount_of_config_files) throws Exception{
        
       
        //First create the scripts that will be executed within each PI
        //This script will read from each piPathsFile and contain commands to create routes in routing table
        System.out.println("...Creating Routing Config Files...");

        /**
         * At this point, we have created a config file for EACH Pi. Each config file
         * will require a script that will read configurations and go into each PI 
         * and set up routing table
         */
        
        for(int i = 0; i < amount_of_config_files; i++){
            //create sshing script for pi1, 2, 3, ...
            runProcess("touch sshingScriptForPi" + (i+1) + ".sh");
            //Give permissions
            runProcess("chmod 777 ./sshingScriptForPi" + (i+1) + ".sh");
            //Create script for pi1,2,3,...to set routing tables
            runProcess("touch setRoutingInPi" + (i+1) + ".sh");
            //Give permissions
            runProcess("chmod 777 ./setRoutingInPi" + (i+1) + ".sh");
            //Create script for pi1,2,3...to reset (clear) routing table
            runProcess("touch removeRoutingInPi" + (i+1) + ".sh");
            //Give permissions
            runProcess("chmod 777 ./removeRoutingInPi" + (i+1) + ".sh");
        }

        //write to each setRouting script files
        System.out.println("...Entering Data into Routing Config Files...");

        //Add commands line by line into scripting file
        for(int i = 0; i < amount_of_config_files; i++){
            //Commands to ssh into each pi will use sshingScript_writer and will write to the 
            //file that belongs to the pi we are currently configuring 
            //setRoutingInPi[1], setRoutingInPi[2]...etc
            FileWriter sshingScript_writer = new FileWriter("setRoutingInPi" + (i+1) + ".sh");
            //Commands to ssh into each pi and delete routing entries will use 
            //sshingScript_writer_del and will write to the file
            FileWriter sshingScript_writer_del = new FileWriter("removeRoutingInPi" + (i+1) + ".sh");  

            //First line of scripts
            sshingScript_writer.write("#!/bin/bash");
            sshingScript_writer_del.write("#!/bin/bash");

            //get pi's config file to read from
            File textFile = new File("piPathsFile" + (i+1));
            //scan file
            Scanner scan = new Scanner(textFile);
            //while config file has next line (path)
            while(scan.hasNextLine()){
                //store path
                String line = scan.nextLine();
                //split path, where each integer represents a node
                String[] elements = line.split(" ");
                //If there are 2 nodes in the path then write the following command to script:
                if(elements.length == 2){
                    //destination is element[1] source is element[0]
                    //Note: sudo commands in terminal will ask for password, update line 247 to give pi your password
                                                //pipe password and use sudo -S to wait for password and read it
                                                                                            //Since all pi's have same subnet, we only need to change last integer
                                                                                            //which is given by config file
                    sshingScript_writer.write("\necho Evanescence1 | sudo -S ip route add " + "10.0.0." + elements[1] + " via " + "10.0.0." + elements[0]);
                    sshingScript_writer_del.write("\necho Evanescence1 | sudo -S ip route delete " + "10.0.0." + elements[1]);
                }
                //If there are more than 2 nodes, this means that a gateway is involved. The gateway is always the 
                //next hop
                //i.e. regardless of length of path, we always provide only the next hop to current node, which is always second position in string array
                if(elements.length > 2){
                    sshingScript_writer.write("\necho Evanescence1 | sudo -S ip route add " + "10.0.0." + elements[elements.length-1] + " via " + "10.0.0." + elements[1]);
                    sshingScript_writer_del.write("\necho Evanescence1 | sudo -S ip route delete " + "10.0.0." + elements[elements.length-1]);
                }
            }
            scan.close();
            sshingScript_writer.close();
            sshingScript_writer_del.close();
        }

        //ssh into PI and run "setRoutingInPi" scripts we have just created above
        sshIntoPiAndRunScript("setRoutingInPi");
       
    }

    public static void sshIntoPiAndRunScript(String scriptToRun) throws Exception{

        //File
        //Need to read from user input file to fetch number of nodes in network
        File textFile = new File("userNetworkInput.txt");
        
        //No of nodes is first line in file
        Scanner scan = new Scanner(textFile);
        //First line is number of pi's in network
        int nodes = scan.nextInt();
        pis_in_network = nodes;

        System.out.println("...Setting Routing Tables Inside Each PI...");
            //write to each script file the sshing commands
            //Each pi needs their own script to ssh into them
            for(int i = 0; i < pis_in_network; i++){
                //sshingScriptForPi1, sshingScriptForPi2,...etc
                FileWriter sshingScript_writer = new FileWriter("sshingScriptForPi" + (i+1) + ".sh");  
                //First line of script
                sshingScript_writer.write("#!/bin/bash");
                //cd to home, because this is where I've stored public key to ssh into pi without password requirement
                sshingScript_writer.write("\ncd ~");
                //ssh and pass script to run
                sshingScript_writer.write("\nssh epharra" + (i+1) +"@10.0.0." + (i+1)+ " 'bash -s' < Projects/NetworkingResearch/" +scriptToRun + (i+1) + ".sh");
                System.out.println("Will run the script " + scriptToRun);
                sshingScript_writer.close();
            }

         //Execute script to ssh into each pi individually and set up routing tables
         for(int i = 0; i < pis_in_network; i++){
            runProcess("./sshingScriptForPi" + (i+1) + ".sh");
        }
        scan.close();
    }

    //method that takes terminal commands and runs them on local device
    private static void runProcess(String command) throws Exception {
        //process waits for each command to be executed and stores any outputs we get from terminal
        Process pro = Runtime.getRuntime().exec(command);
        //print any errors
        try{
            printLinesFromTerminalCommands( "stdout:", pro.getInputStream());
            printLinesFromTerminalCommands(" stderr:", pro.getErrorStream());
        } catch(Exception e){
            e.printStackTrace();
        }
        pro.waitFor(); //wait for current command to finish
        //print exitValue() after commmand has run for troubleshooting hints
        System.out.println(command + " exitValue() " + pro.exitValue());
    }
        

    //method that prints output from termanal after we've run a command
                                                                    //needs to know what process
                                                                    //we want to print from, hence
                                                                    //InputStream ins
   private static void printLinesFromTerminalCommands(String name, InputStream ins) throws Exception {
    String line = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
        System.out.println(name + " " + line);
    }
   }

}
