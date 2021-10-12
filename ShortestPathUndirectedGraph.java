import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class ShortestPathUndirectedGraph {
    public static int pis_in_network = 0;

    // Driver Program
    public static void main(String args[]) throws Exception
    {
        //File
        File textFile = new File("userNetworkInput.txt");
        
        //No of nodes is first line in file
        Scanner scan = new Scanner(textFile);
        int nodes = scan.nextInt();
        pis_in_network = nodes;
        

        // No of vertices is nodes + 1
        int v = nodes + 1; //8

 
        // Adjacency list for storing which vertices are connected
        ArrayList<ArrayList<Integer>> adj =
                            new ArrayList<ArrayList<Integer>>(v);
        for (int i = 0; i < v; i++) {
            adj.add(new ArrayList<Integer>());
        }
 
        // Creating graph given in the above diagram.
        // add_edge function takes adjacency list, source
        // and destination vertex as argument and forms
        // an edge between them.
        while(scan.hasNextInt()){
            //System.out.println("Next int is " + scan.nextInt() + scan.nextInt());
            addEdge(adj, scan.nextInt(), scan.nextInt());
        }
       

        int source, dest;
        String piPathsFile = "";
    
        //amount of config files to expect
        int amount_of_config_files = 0;     

        //find each possible path to any given PI
        System.out.println("...Finding shortest route paths...");
        for(int i = 0; i < v-1; i++ ) {
            piPathsFile = "piPathsFile" + (i+1);
            amount_of_config_files++;
            FileWriter path_writer = new FileWriter(piPathsFile);
            for(int j = 0; j < v; j++){
                source = i+1;
                dest = j+1;
                //System.out.println(" ");
                //System.out.println("Source is " + source + " Destination is " + dest);
                printShortestDistance(adj, source, dest, v, path_writer);
            }
            path_writer.close();
        }
       
        
        createScriptsForEachPi(amount_of_config_files);
    }

    public static void createScriptsForEachPi(int amount_of_config_files) throws Exception{
        
       
        //First create the scripts that will be executed within each PI
        //This script will read from each piPathsFile and contain commands to create routes in routing table
        System.out.println("...Creating Routing Config Files...");
        for(int i = 0; i < amount_of_config_files; i++){
            runProcess("touch sshingScriptForPi" + (i+1) + ".sh");
            runProcess("chmod 777 ./sshingScriptForPi" + (i+1) + ".sh");
            runProcess("touch setRoutingInPi" + (i+1) + ".sh");
            runProcess("chmod 777 ./setRoutingInPi" + (i+1) + ".sh");
            runProcess("touch removeRoutingInPi" + (i+1) + ".sh");
            runProcess("chmod 777 ./removeRoutingInPi" + (i+1) + ".sh");
        }

        //write to each setRouting script files
        System.out.println("...Entering Data into Routing Config Files...");
        for(int i = 0; i < amount_of_config_files; i++){
            FileWriter sshingScript_writer = new FileWriter("setRoutingInPi" + (i+1) + ".sh");
            FileWriter sshingScript_writer_del = new FileWriter("removeRoutingInPi" + (i+1) + ".sh");  
            sshingScript_writer.write("#!/bin/bash");
            sshingScript_writer_del.write("#!/bin/bash");
            File textFile = new File("piPathsFile" + (i+1));
            Scanner scan = new Scanner(textFile);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                String[] elements = line.split(" ");
                if(elements.length == 2){
                    //destination is element[1] source is element[0]
                    sshingScript_writer.write("\necho Evanescence1 | sudo -S ip route add " + "10.0.0." + elements[1] + " via " + "10.0.0." + elements[0]);
                    sshingScript_writer_del.write("\necho Evanescence1 | sudo -S ip route delete " + "10.0.0." + elements[1]);
                }
                if(elements.length == 3){
                    //destination is element[2] source is element[0] gateway is element[1]
                    sshingScript_writer.write("\necho Evanescence1 | sudo -S ip route add " + "10.0.0." + elements[2] + " via " + "10.0.0." + elements[1]);
                    sshingScript_writer_del.write("\necho Evanescence1 | sudo -S ip route delete " + "10.0.0." + elements[2]);
                }
            }
            scan.close();
            sshingScript_writer.close();
            sshingScript_writer_del.close();
        }

        sshIntoPiAndRunScript("setRoutingInPi");
       
    }

    public static void sshIntoPiAndRunScript(String scriptToRun) throws Exception{

        //File
        File textFile = new File("userNetworkInput.txt");
        
        //No of nodes is first line in file
        Scanner scan = new Scanner(textFile);
        int nodes = scan.nextInt();
        pis_in_network = nodes;

        System.out.println("...Setting Routing Tables Inside Each PI...");
            //write to each script file the sshing commands
            for(int i = 0; i < pis_in_network; i++){
                FileWriter sshingScript_writer = new FileWriter("sshingScriptForPi" + (i+1) + ".sh");  
                sshingScript_writer.write("#!/bin/bash");
                sshingScript_writer.write("\ncd ~");
                sshingScript_writer.write("\nssh epharra" + (i+1) +"@10.0.0." + (i+1)+ " 'bash -s' < Projects/NetworkingResearch/" +scriptToRun + (i+1) + ".sh");
                System.out.println("Will run the script " + scriptToRun);
                sshingScript_writer.close();
            }

         //run each sshing script
         for(int i = 0; i < pis_in_network; i++){
            runProcess("./sshingScriptForPi" + (i+1) + ".sh");
        }
    }

    
    //method that takes terminal commands and runs them on local device
    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        //print any errors
        try{
            printLinesFromTerminalCommands( "stdout:", pro.getInputStream());
            printLinesFromTerminalCommands(" stderr:", pro.getErrorStream());
        } catch(Exception e){
            e.printStackTrace();
        }
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
    }
        

   private static void printLinesFromTerminalCommands(String name, InputStream ins) throws Exception {
    String line = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
        System.out.println(name + " " + line);
    }
   }
 
    // function to form edge between two vertices
    // source and dest
    private static void addEdge(ArrayList<ArrayList<Integer>> adj, int i, int j)
    {
        adj.get(i).add(j);
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
 
        if (BFS(adj, s, dest, v, pred, dist) == false) {
            //System.out.println("Given source and destination" +
                                         //"are not connected");
            return;
        }
 
        // LinkedList to store path
        LinkedList<Integer> path = new LinkedList<Integer>();
        int crawl = dest;
        path.add(crawl);
        while (pred[crawl] != -1) {
            path.add(pred[crawl]);
            crawl = pred[crawl];
        }
 
        // Print distance
        //System.out.println("Shortest path length is: " + dist[dest]);
 
        // Print path
        //Write to file
        
        
        //System.out.println("Path is ::");
       
        for (int i = path.size() - 1; i >= 0; i--) {
            path_writer.write(path.get(i) + " ");
            //System.out.print(path.get(i) + " ");
        }
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
}
