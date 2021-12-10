ADDITIONAL INSTALLATIONS: 
    1. graphviz (https://graphviz.org/download/)
        -Displays a png image of the graph
    2. gnuplot (https://www.youtube.com/watch?v=u5OJdUTakns)
        -Displays line graphs of data collected
    
    

ASSUMPTIONS:
    1. Assumes local device has ssh keys stored in home directory in order to ssh into Pi's without the need for a password
    2. Assumes PI's are on and running 
    3. Assumes user input file has the name "userNetworkInput.txt" and is stored in project folder
    4. Assumes device we are running program on is connected to Pi's adhoc network. 
    IMPORTANT: Local device connecting to ad hoc network will NOT automatically be assigned IP address. You must manually give deivce an IP address within the subwork 10.0.0.X
    5. Assumes Pi's have already been set up as routers and can be used as gateways in routing table
    6. Assumes gnuplot is installed on each PI
    7. Assumes plot script and gnuplot script are already on PI devices
    8. Assumes graphviz is installed on local device and accessible by running Java program
    
    

RUNNING PROGRAM:
    -a file with the name of "userNetworkInput.txt" must be included to read user graph info
    -run ShortestPathUndirectedGraph.java to start program
    -Refresh project folder in order to see the new files that have been created


FILE DESCRIPTIONS: 
    -clearRouting.java: 
        description:
            Program that clears routing table. 
        assumptions:
            Assumes that ShortestPathUndirectedGraph has been run and config files have all been created. This program needs these files to clear routing table
        when to use:
            Run this program only AFTER you have run ShortestPathUndirectedGraph

    -ShortestPathUndirectedGraph.java: 
        description:
            Program that reads from userNetworkInput.txt file to find shortest path between each pi to every other pi. Then it will create routing table entries within each
            pi accordingly by ssh'ing into each Pi and running terminal commands.  
        assumptions:
            Assumes "userNetworkInput.txt" file exists within project folder.
            Assumes local device has ssh keys stored in home directory in order to ssh into Pi's without the need for a password
        when to use:
            Run this program to set up routing tables

     -userNetworkInput.txt: 
        description:
           File that describes network topology as given by user. It describes each node's direct connection to another.
           MUST have the name "userNetworkInput.txt"
        assumptions:
            Assumes line #1 is the number of nodes in network 
            Assumes line #2-n are a pair of integers where: 
                the first integer is the source node
                the second integer is the destination node
        when to use:
            Include this file when running ShortestPathUndirectedGraph.java

CUSTOMIZATIONS: 
    -Program may be customized in the following ways: 

    To provide a different device-username@ip.address, edit line# 293 of ShortestPathUndirectedGraph.java

    To provide a different password to Pi's, edit line# 249 & line# 250

    If ssh keys are stored in a directory other than home, edit line# 291
        

