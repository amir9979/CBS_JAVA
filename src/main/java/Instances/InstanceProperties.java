package Instances;

public class InstanceProperties {

    public final String SEPARATOR;

    public final int[] boardSize;
    private final Float obstacles;
    public final int[] numOfAgents; // Todo - array of num


    public InstanceProperties(int[] boardSize, Float obstacles, int[] numOfAgents, String separator) {
        this.SEPARATOR = separator;
        this.boardSize = boardSize;
        this.obstacles = obstacles;
        this.numOfAgents = numOfAgents;
    }

    public int getObstaclePercentage(){

        if ( this.obstacles == null){
            return -1;
        }
        return Math.round(obstacles*100); // Returns 15
    }

    public Float getObstacleRate(){
        return this.obstacles; // returns 0.15
    }

    public int[] getNumOfAgents(){
        return this.numOfAgents;
    }
    public int getArrayAgentsSize(){
        return this.numOfAgents.length;
    }




//    public String getInstanceName(){
//
//        String result = "Instance" + this.SEPARATOR;
//
//
//        result += boardSize[0] + this.SEPARATOR; // Adds the board size
//        result += obstacles + this.SEPARATOR; // Adds the obstacle rate
//        result += numOfAgents + this.SEPARATOR; // Adds the num of agents
//
//
//        // Example: "Instance-16-0-7-"  * Note ( missing the instance index )
//        return result;
//    }




}
