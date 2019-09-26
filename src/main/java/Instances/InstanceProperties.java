package Instances;

import java.util.Arrays;
import java.util.Objects;

public class InstanceProperties {

    public final String SEPARATOR;

    public final int[] boardSize;
    public final int obstacleRate;
    public final int numOfAgents;


    public InstanceProperties(int[] boardSize, int obstacleRate, int numOfAgents, String separator) {
        this.SEPARATOR = separator;
        this.boardSize = boardSize;
        this.obstacleRate = obstacleRate;
        this.numOfAgents = numOfAgents;
    }



//    public String getInstanceName(){
//
//        String result = "Instance" + this.SEPARATOR;
//
//
//        result += boardSize[0] + this.SEPARATOR; // Adds the board size
//        result += obstacleRate + this.SEPARATOR; // Adds the obstacle rate
//        result += numOfAgents + this.SEPARATOR; // Adds the num of agents
//
//
//        // Example: "Instance-16-0-7-"  * Note ( missing the instance index )
//        return result;
//    }




}
