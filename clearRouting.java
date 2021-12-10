public class clearRouting {
    public static void main(String[] args) throws Exception{
       

        String scriptToRun = "removeRoutingInPi";

        ShortestPathUndirectedGraph.sshIntoPiAndRunRoutingScript(scriptToRun);
    }
}
