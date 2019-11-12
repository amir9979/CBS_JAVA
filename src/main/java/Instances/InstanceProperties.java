package Instances;

import Instances.Maps.MapDimensions;

public class InstanceProperties {

    public final MapDimensions mapSize;
    private final Float obstacles;
    public final int[] numOfAgents; // done - array of num



    public InstanceProperties() {
        this.mapSize = new MapDimensions();
        this.obstacles = (float)-1;
        this.numOfAgents = new int[0];
    }


    public InstanceProperties(MapDimensions.MapOrientation mapOrientation){
        this();
        this.mapSize.setMapOrientation(mapOrientation);
    }



    /***
     * Properties constructor
     * @param mapSize - {@link MapDimensions} indicates the Axis lengths , zero for unknown
     * @param obstacles - For unknown obstacles enter (float)-1
     * @param numOfAgents - An array of different num of agents.
     */
    public InstanceProperties(MapDimensions mapSize, Float obstacles, int[] numOfAgents) {
        this.mapSize = (mapSize == null ? new MapDimensions() :  mapSize);
        this.obstacles = (obstacles == null ? (float)-1 : obstacles);
        this.numOfAgents = (numOfAgents == null ? new int[0] : numOfAgents);
    }


    /***
     * Get Obstacle as a percentage, Like: 15%
     * @return An int. The obstacle percentage in the map
     */
    public int getObstaclePercentage() {

        if (this.obstacles == -1) {
            return -1;
        }
        return Math.round(obstacles * 100); // Returns 15
    }


    /***
     * Get Obstacle as a ratio, Like: 0.15
     * @return A Float. The obstacle rate in the map
     */
    public Float getObstacleRate() {
        return this.obstacles; // returns 0.15
    }






}
