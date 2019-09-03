package Instances;

public class InstanceProperties {


    public final int[] boardSize;
    public final int obstacleRate;
    public final int numOfAgents;
    public final int numOfInstances;


    public InstanceProperties(int[] boardSize, int obstacleRate, int numOfAgents, int numOfInstances) {
        this.boardSize = boardSize;
        this.obstacleRate = obstacleRate;
        this.numOfAgents = numOfAgents;
        this.numOfInstances = numOfInstances;
    }



    public String getInstanceName(){

        String result = "Instance_";


        result += boardSize[0] + "_"; // Adds the board size
        result += obstacleRate + "_"; // Adds the obstacle rate
        result += numOfAgents + "_"; // Adds the num of agents


        // Example: "Instance_16_0_7_"  * Note ( missing the instance index )
        return result;
    }
}
